/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.favorites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.IModelerCacheListener;
import com.metamatrix.modeler.internal.ui.ModelerCacheEvent;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.search.ModelObjectFinderDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IModelObjectActionContributor;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.actions.ExtendedMenuManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.OrderableViewerSorter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;

/**
 * FavoritesView is a class designed to provide a table summary of user-defined EObjects or objects contained in MetaMatrix
 * models. The objects can be added to this table in various ways and removed or cleared via the actions and toolbar buttons
 * provided.
 */
public class FavoritesView extends ViewPart
    implements ISelectionProvider, ISelectionListener, IModelerCacheListener, StringUtil.Constants {

    private static final int NAME_COLUMN = 0;
    private static final int LOCATION_COLUMN = 1;
    private static final String ADD_TO_FAVORITES = "action.add.tooltip2"; //$NON-NLS-1$

    private ModelObjectPropertySourceProvider propertySourceProvider;
    ILabelProvider nameLabelProvider = ModelUtilities.getModelObjectLabelProvider();
    private ArrayList listenerList = new ArrayList();
    TableViewer tableViewer;
    private boolean controlCreated = false;
    private EObjectModelerCache eObjCache;
    ISelection externalSelection;
    private ModelerActionService actionService;

    /** The ModelResource listener for file system changes on models and projects */
    EventObjectListener modelResourceListener;

    /*
     * Static methods used obtain i18n.property strings for the string contants in this class
     */
    private static String getString( final String stringId ) {
        return UiConstants.Util.getString("FavoritesView." + stringId); //$NON-NLS-1$
    }

    private static String getString( final String stringId,
                                     Object value,
                                     Object value1 ) {
        if (value == null) return getString(stringId);

        if (value1 == null) return UiConstants.Util.getString("FavoritesView." + stringId, value); //$NON-NLS-1$

        return UiConstants.Util.getString("FavoritesView." + stringId, value, value1); //$NON-NLS-1$
    }

    private Action clearAction, openAction, editAction, addAction, removeAction, selectAllAction, deselectAllAction, findAction;

    /**
     * Base constructor
     */
    public FavoritesView() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    public void createPartControl( Composite parent ) {
        controlCreated = true;

        initTableViewer(parent);

        MenuManager popupMenuManager = new ExtendedMenuManager();
        IMenuListener listener = new IMenuListener() {
            public void menuAboutToShow( IMenuManager mng ) {
                fillContextMenu(mng);
            }
        };
        popupMenuManager.addMenuListener(listener);
        popupMenuManager.setRemoveAllWhenShown(true);
        Menu menu = popupMenuManager.createContextMenu(tableViewer.getControl());
        tableViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(popupMenuManager, tableViewer);

        initActions();
        fillToolBar();

        getSite().setSelectionProvider(this);

        // Get the cache and wire it up
        eObjCache = UiPlugin.getDefault().getEObjectCache();
        eObjCache.addCacheListener(this);

        // set initial state
        this.tableViewer.setInput(this);
        setActionsState();
        packTable();
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        ISelection selection = getSite().getWorkbenchWindow().getSelectionService().getSelection();
        if (selection != null) {
            selectionChanged(this, selection);
        }
    }

    /**
     * Helper method which takes care of creating table viewer, and wiring up listeners and providers.
     * 
     * @param parent
     * @since 4.2
     */
    public void initTableViewer( Composite parent ) {
        this.tableViewer = WidgetFactory.createTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        final Table table = this.tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        WidgetFactory.createTableColumn(table, getString("columns.name")); //$NON-NLS-1$
        WidgetFactory.createTableColumn(table, getString("columns.location")); //$NON-NLS-1$}
        this.tableViewer.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
            }

            public Object[] getElements( final Object inputElement ) {
                return UiPlugin.getDefault().getEObjectCache().toArray();
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        });
        this.tableViewer.setLabelProvider(new AbstractTableLabelProvider() {
            @Override
            public Image getColumnImage( final Object element,
                                         final int column ) {
                if (column == NAME_COLUMN) {
                    return nameLabelProvider.getImage(element);
                }
                return null;
            }

            public String getColumnText( final Object element,
                                         final int column ) {
                switch (column) {
                    case NAME_COLUMN: {
                        return nameLabelProvider.getText(element);
                    }

                    case LOCATION_COLUMN: {
                        return ModelerCore.getModelEditor().getFullPathToParent((EObject)element).makeRelative().toString();
                    }
                }
                return EMPTY_STRING;
            }
        });

        this.tableViewer.setSorter(new OrderableViewerSorter());
        this.tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( final DoubleClickEvent event ) {
                // Assume that we have an Open request here.
                if (SelectionUtilities.isSingleSelection(event.getSelection())) {
                    EObject eObj = SelectionUtilities.getSelectedEObject(event.getSelection());
                    if (eObj != null) {
                        ModelEditorManager.open(eObj, true);
                    }
                }
            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
                handleSelectionChanged(event.getSelection());
            }
        });
    }

    private ImageDescriptor getImageDescriptor( String imageId ) {
        return UiPlugin.getDefault().getImageDescriptor(imageId);
    }

    /*
     *  Initialize view actions, set icons and action text.
     */
    private void initActions() {

        editAction = new Action(getString("action.edit")) { //$NON-NLS-1$
            @Override
            public void run() {
                if (SelectionUtilities.isSingleSelection(getSelection())) {
                    EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
                    if (eObj != null) {
                        ModelEditorManager.edit(eObj);
                    }
                }
            }
        };
        editAction.setEnabled(false);

        openAction = new Action(getString("action.open")) { //$NON-NLS-1$
            @Override
            public void run() {
                if (SelectionUtilities.isSingleSelection(getSelection())) {
                    EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
                    if (eObj != null) {
                        ModelEditorManager.open(eObj, true);
                    }
                }
            }
        };
        openAction.setEnabled(false);

        selectAllAction = new Action(getString("action.selectAll")) { //$NON-NLS-1$
            @Override
            public void run() {
                tableViewer.getTable().selectAll();
            }
        };
        selectAllAction.setEnabled(false);

        deselectAllAction = new Action(getString("action.deselectAll")) { //$NON-NLS-1$
            @Override
            public void run() {
                tableViewer.getTable().deselectAll();
            }
        };
        deselectAllAction.setEnabled(false);

        addAction = new Action(getString("action.add")) {//$NON-NLS-1$
            @Override
            public void run() {
                if (externalSelection != null) {
                    Collection addedObjs = SelectionUtilities.getSelectedEObjects(externalSelection);

                    if (!addedObjs.isEmpty()) {
                        UiPlugin.getDefault().getEObjectCache().addAll(addedObjs);
                    }
                }
            }
        };

        addAction.setImageDescriptor(getImageDescriptor(PluginConstants.Images.ADD_ICON));
        addAction.setToolTipText(getString("action.add.tooltip")); //$NON-NLS-1$

        removeAction = new Action(getString("action.remove")) { //$NON-NLS-1$
            @Override
            public void run() {
                UiPlugin.getDefault().getEObjectCache().removeAll(getSelectedEObjects());
            }
        };
        removeAction.setEnabled(false);
        removeAction.setImageDescriptor(getImageDescriptor(PluginConstants.Images.REMOVE_ICON));
        removeAction.setToolTipText(getString("action.remove.tooltip")); //$NON-NLS-1$

        clearAction = new Action(getString("action.clear")) { //$NON-NLS-1$
            @Override
            public void run() {
                UiPlugin.getDefault().getEObjectCache().clear();
            }
        };
        clearAction.setEnabled(false);
        clearAction.setImageDescriptor(getImageDescriptor(PluginConstants.Images.CLEAR_ICON));
        clearAction.setToolTipText(getString("action.clear.tooltip")); //$NON-NLS-1$

        findAction = new Action(getString("action.find")) {//$NON-NLS-1$
            @Override
            public void run() {
                Collection addedObjs = findObjects();

                if (!addedObjs.isEmpty()) {
                    UiPlugin.getDefault().getEObjectCache().addAll(addedObjs);
                }
            }
        };
        findAction.setImageDescriptor(getImageDescriptor(PluginConstants.Images.FIND_METADATA));
        findAction.setToolTipText(getString("action.find.tooltip")); //$NON-NLS-1$
    }

    /*
     * Private method which sets the enabled state of actions defined and contained in this view.
     * This is business logic, in a sense.
     */
    void setActionsState() {
        // Set up values and booleans for actions
        int nRows = tableViewer.getTable().getSelectionCount();
        boolean rowsSelected = nRows > 0;
        boolean allRowsSelected = nRows == tableViewer.getTable().getItems().length;
        boolean tableHasRows = tableViewer.getTable().getItems().length > 0;

        // Actions requiring selection
        removeAction.setEnabled(rowsSelected);
        deselectAllAction.setEnabled(rowsSelected);

        selectAllAction.setEnabled(!allRowsSelected);

        // Actions requiring non-empty table
        clearAction.setEnabled(tableHasRows);

        // Edit Action
        boolean enableEdit = false;
        if (nRows == 1) {
            EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
            if (eObj != null) enableEdit = ModelEditorManager.canEdit(eObj);
            // open requires that only one object is selected
            openAction.setEnabled(true);
        }
        editAction.setEnabled(enableEdit);

        // Add action requires tracking selections external to this view, so they can be "added"
        // to the view.
        setAddActionState(this);
    }

    /*
     * Internal method which determines the enabled state of the add button based on a cached
     * ISelection obtained through the selection listener.
     */
    private void setAddActionState( IWorkbenchPart part ) {
        boolean enable = false;
        if (part != this) {
            if (externalSelection != null) {
                List selectedEObjects = SelectionUtilities.getSelectedEObjects(externalSelection);
                if (!selectedEObjects.isEmpty()) {
                    int size = selectedEObjects.size();
                    String title = part.getTitle();
                    if (part instanceof ModelEditor) {
                        title = ((ModelEditor)part).getCurrentPage().getTitle();
                    }
                    String tString = getString(ADD_TO_FAVORITES, Integer.toString(size), title);
                    enable = true;
                    addAction.setToolTipText(tString);
                }
            } else {
                addAction.setToolTipText(getString("action.add.tooltip")); //$NON-NLS-1$
            }
        }
        addAction.setEnabled(enable);
    }

    /*
     * Populates the View's toolbar
     */
    private void fillToolBar() {
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();

        tbm.add(findAction);
        tbm.add(new Separator());
        tbm.add(addAction);
        tbm.add(removeAction);
        tbm.add(clearAction);

    }

    /*
     * Util method to layout table and resize cells, columns and rows.
     */
    private void packTable() {
        WidgetUtil.pack(tableViewer.getTable());
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        eObjCache.removeCacheListener(this);
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        super.dispose();
    }

    /**
     * This method is called from the view's popup menu listener.
     * 
     * @param manager
     * @since 4.2
     */
    public void fillContextMenu( IMenuManager manager ) {
        manager.add(openAction);
        manager.add(editAction);
        manager.add(new Separator());
        manager.add(selectAllAction);
        manager.add(deselectAllAction);
        manager.add(new Separator());
        manager.add(findAction);
        manager.add(new Separator());
        manager.add(removeAction);
        manager.add(clearAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        contributePermanentActionsToContextMenu(manager);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.2
     */
    @Override
    public void setFocus() {
        if (controlCreated) tableViewer.getControl().setFocus();
    }

    /*
     * handles selection changed within the table, fires a new event and updates the actions state.
     */
    void handleSelectionChanged( ISelection selection ) {
        fireSelectionChanged(new SelectionChangedEvent(this, selection));
        setActionsState();
    }

    /**
     * @param item
     * @since 4.2
     */
    public void addItem( final Object item ) {
        addItems(new Object[] {item});
    }

    /**
     * @param items
     * @since 4.2
     */
    public void addItems( final Object[] items ) {
        if (items.length > 0) {
            tableViewer.add(items);
            setSelection(new StructuredSelection(items));
        }
        setActionsState();
        packTable();
    }

    /**
     * @param items
     * @since 4.2
     */
    public void removeItems( final Object[] items ) {
        if (!tableViewer.getControl().isDisposed()) {
            tableViewer.remove(items);
            setActionsState();
            packTable();
        }
    }

    /**
     * Clears table view. Actaully, the table contents is really defined by the content provider, or EObject cache. If this cache
     * is cleared, the table is cleared, so all that needs to be done is a refresh. Removing single, or multiple rows, requires
     * specific removal, however.
     * 
     * @since 4.2
     */
    public void removeAllItems() {
        this.tableViewer.refresh();
    }

    /**
     * Removes single row/item from table.
     * 
     * @param item
     * @since 4.2
     */
    public void removeItem( final Object item ) {
        removeItems(new Object[] {item});
    }

    /*
     * Private utility method which extracts a list of EObjects from the selected table rows.
     */
    Collection getSelectedEObjects() {
        List selected = SelectionUtilities.getSelectedEObjects(tableViewer.getSelection());
        if (selected == null || selected.isEmpty()) return Collections.EMPTY_LIST;

        return selected;
    }

    /*
     * This method provides the user a dialog
     */
    Collection findObjects() {
        Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

        ModelObjectFinderDialog dialog = new ModelObjectFinderDialog(shell);

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            // get the selected model object

            Object[] selectedEOList = dialog.getResult();
            if (selectedEOList != null && selectedEOList.length > 0) {
                Collection newList = new ArrayList(selectedEOList.length);
                for (int i = 0; i < selectedEOList.length; i++) {
                    newList.add(selectedEOList[i]);
                }
                return newList;
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     * @since 4.2
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     * @since 4.2
     */
    public ISelection getSelection() {
        return tableViewer.getSelection();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     * @since 4.2
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        listenerList.remove(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    public void setSelection( ISelection selection ) {
        tableViewer.setSelection(selection, true);
        setActionsState();
    }

    /*
     * Enough said
     */
    private void fireSelectionChanged( SelectionChangedEvent event ) {
        for (Iterator iter = listenerList.iterator(); iter.hasNext();) {
            ((ISelectionChangedListener)iter.next()).selectionChanged(event);
        }
    }

    /**
     * Overridden from super to provide the PropertySheet for EObject, since EObject does not implement IAdaptable
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @since 4.0
     */
    @Override
    public Object getAdapter( Class key ) {

        if (key.equals(IPropertySheetPage.class)) {
            if (propertySourceProvider == null) {
                propertySourceProvider = ModelUtilities.getPropertySourceProvider(); // new ModelObjectPropertySourceProvider();
            }

            return propertySourceProvider.getPropertySheetPage();
        }
        return super.getAdapter(key);
    }

    // IModeler Cache Listener

    /**
     * @see com.metamatrix.modeler.internal.ui.IModelerCacheListener#cacheChanged(com.metamatrix.modeler.internal.ui.ModelerCacheEvent)
     * @since 4.2
     */
    public void cacheChanged( final ModelerCacheEvent theEvent ) {
        // make sure event handling in right thread
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                if (!tableViewer.getControl().isDisposed()) {
                    if (theEvent.isClear()) {
                        removeAllItems();
                    } else if (theEvent.isAdd()) {
                        addItems(theEvent.toArray());
                    } else if (theEvent.isDelete()) {
                        removeItems(theEvent.toArray());
                    } else if (theEvent.isChange()) {
                        Object[] changedElements = theEvent.toArray();
                        updateForChange(changedElements);
                    }
                    setActionsState();
                }
            }
        });
    }

    void updateForChange( Object[] changedElements ) {
        for (int i = 0; i < changedElements.length; i++) {
            Object obj = changedElements[i];
            tableViewer.refresh(obj, true);
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        if (part != this) {
            externalSelection = selection;
            setAddActionState(part);
        }
    }

    /**
     * Gives the <code>IModelObjectActionContributor</code>s a chance to contribute to the context menu
     * 
     * @param theMenuMgr the context menu being contributed to
     * @param theSelection the current selection
     */
    public void contributePermanentActionsToContextMenu( IMenuManager theMenuMgr ) {
        List contributors = getActionService().getModelObjectActionContributors();
        ISelection selection = getSelection();
        if (selection != null && !selection.isEmpty() || !SelectionUtilities.isSingleSelection(selection)) {
            for (int size = contributors.size(), i = 0; i < size; i++) {
                IModelObjectActionContributor contributor = (IModelObjectActionContributor)contributors.get(i);
                contributor.contributeToContextMenu(theMenuMgr, getSelection());
            }
        }
    }

    /*
     * Utility method for accessing the Modeler Action Service so we can get the contributors and play in the external
     * action world.
     */
    private ModelerActionService getActionService() {
        if (actionService == null) {
            actionService = (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
        }
        return actionService;
    }
}

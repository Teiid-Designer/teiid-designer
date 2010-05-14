/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.util.NoOpDropTargetAdapter;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.IInlineRenameable;
import com.metamatrix.modeler.ui.editors.INavigationSupported;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;
import com.metamatrix.modeler.ui.editors.NavigableEditor;
import com.metamatrix.modeler.ui.editors.NavigationMarker;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.print.IPrintable;
import com.metamatrix.ui.print.Printable;
import com.metamatrix.ui.table.DoubleClickTableViewer;
import com.metamatrix.ui.table.TableColumnSelectionHelper;

/**
 * ModelTableEditor is a tabbed panel of tables containing the content of the model, with the tab names corresponding to the
 * different types of objects in the model.
 */
public class ModelTableEditor extends NavigableEditor
    implements ModelEditorPage, IInlineRenameable, ITablePasteValidator, INavigationSupported, UiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelTableEditor.class);

    static final String UNDO_PREFIX = UiConstants.Util.getString("ModelTableEditor.undoPrefix") + ' '; //$NON-NLS-1$

    private static final String UNDO_INSERT_ROWS = UiConstants.Util.getString("ModelTableEditor.undoInsertRows"); //$NON-NLS-1$

    private static final String TOOLTIP = UiConstants.Util.getString("ModelTableEditor.tooltip"); //$NON-NLS-1$

    /** A label provider for tab names and icons */
    private ILabelProvider labelProvider;

    /** A content provider for collecting up all the objects in the model */
    private ITreeContentProvider contentProvider;

    /** The container widget */
    TabFolder tabFolder;

    /** The SelectionProvider for selection in the tables to be hooked to the ModelEditor */
    ModelObjectSelectionManager selectionMgr;

    /** Key=tab name, Value=TableViewer */
    Map viewers = new HashMap();

    /** Responsible for contributing actions to menus, toolbars, etc. */
    private ModelTableEditorActionContributor actionContributor;

    /** Responsible for handling notification events on all TableViewers */
    private TableNotificationHandler notificationHandler;

    /** This Editor's ModelResource */
    private ModelResource modelResource;

    /** This Editor's Resource */
    private Resource emfResource;

    Map selectionHelperMap = new HashMap();

    Map modelMap = new HashMap(); // key=viewer value=model

    private String title;
    private boolean lastReadOnly;

    private IBaseLabelProvider tableLabelProvider = new ModelTableLabelProvider();

    ModelEditor meParentEditor;

    /**
     * Construct an instance of ModelTableEditor.
     */
    public ModelTableEditor() {
        super();
    }

    /**
     * Create this part's visual control. Responds by creating the CTabFolder, then filling the tabs from the model.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        this.selectionMgr = new ModelObjectSelectionManager();
        this.tabFolder = createContainer(parent);
        createTabs();
        // set the active page (page 0 by default), unless it has already been done

        int tabID = getActiveTab();
        if (tabID == 1 || tabID == 0) {
            setActiveTab(0);

            // programmatically setting selection to tab 0 while constructing doesn't get
            // handled by our handler created in createContainer(Composite) since it hasn't been
            // called yet. So call pageChange method so that tab 0 can construct it's columns
            pageChange(0);
        }
    }

    /**
     * Creates an empty CTabFolder with no style bits set, and hooks a selection listener which calls <code>pageChange()</code>
     * whenever the selected tab changes.
     * 
     * @return a new container
     */
    private TabFolder createContainer( Composite parent ) {
        final TabFolder container = new TabFolder(parent, SWT.TOP);
        container.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                int newPageIndex = container.indexOf((TabItem)e.item);
                pageChange(newPageIndex);
            }
        });
        return container;
    }

    /**
     * Using the EditorInput supplied to this ModelEditorPage, create the tabs in the container for each EClass type found in the
     * model.
     */
    protected void createTabs() {

        try {
            labelProvider = new ModelExplorerLabelProvider();
            contentProvider = ModelUtilities.getModelContentProvider();
            HashMap objectTypeMap = new HashMap();
            Collection types = buildObjectTypeMap(objectTypeMap);

            int tabIndex = 0;
            for (Iterator iter = types.iterator(); iter.hasNext(); tabIndex++) {

                EClass tabClass = (EClass)iter.next();
                ArrayList instanceList = (ArrayList)objectTypeMap.get(tabClass);
                if (canAddTable(instanceList.get(0))) {
                    addTab(tabClass, instanceList, tabIndex);
                } else {
                    --tabIndex;
                }

            }

        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, PREFIX + "loadError"); //$NON-NLS-1$
        }

    }

    public boolean canAddTable( Object instance ) {
        IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(instance);
        IPropertyDescriptor[] properties = propertySource.getPropertyDescriptors();
        return properties.length > 0;
    }

    protected int addTab( EClass tabClass,
                          ArrayList instanceList,
                          int tabIndex ) {
        boolean supportsDescriptions = ModelUtilities.supportsModelDescription(this.modelResource.getResource());
        ModelObjectTableModel model = ModelObjectTableModelFactory.createModelObjectTableModel(supportsDescriptions,
                                                                                               tabClass,
                                                                                               instanceList);

        if (model.canView()) {

            ModelTableEditorViewer tableViewer = new ModelTableEditorViewer(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL
                                                                                       | SWT.FULL_SELECTION | SWT.MULTI);
            tableViewer.addDropSupport(DND.DROP_COPY, new Transfer[] {}, NoOpDropTargetAdapter.getInstance());
            selectionHelperMap.put(tableViewer, new TableColumnSelectionHelper(tableViewer));
            modelMap.put(tableViewer, model);
            tableViewer.getTable().setHeaderVisible(true);
            tableViewer.getTable().setLinesVisible(true);

            TableLayout layout = new TableLayout();
            tableViewer.getTable().setLayout(layout);

            model.setTableViewer(tableViewer);

            tableViewer.setCellModifier(new TableCellModifier());
            tableViewer.setContentProvider(new ModelTableContentProvider(model));
            tableViewer.setLabelProvider(tableLabelProvider);
            tableViewer.setSorter(new ModelObjectTableSorter(tableViewer));

            // create a selection provider to listen to this TableViewer
            ModelObjectTableSelectionManager tableSelectionMgr = new ModelObjectTableSelectionManager(tableViewer, model);

            // broadcast selection change from the table out to the ModelEditor
            tableSelectionMgr.addSelectionChangedListener(this.selectionMgr);

            // hook each table selection provder to the workbench selection listener
            this.selectionMgr.addSelectionChangedListener(tableSelectionMgr);

            if (tabIndex >= 0) {
                tabIndex = addTab(tableViewer.getControl(), tabIndex);
            } else {
                tabIndex = addTab(tableViewer.getControl());
            }

            TabItem item = tabFolder.getItems()[tabIndex];
            item.setData(tabClass);

            String tabName = tabClass.getName();
            tabName = CoreStringUtil.computePluralForm(CoreStringUtil.computeDisplayableForm(tabName));
            setTabText(tabIndex, tabName);
            setTabToolTipText(tabIndex, tabName);
            Object o = instanceList.get(0);
            setTabImage(tabIndex, labelProvider.getImage(o));

            tableSelectionMgr.setName(tabName);
            viewers.put(tabName, tableViewer);
            getNotificationHandler().addTable(tabClass, tableViewer, model);

            // Added this listener to handle tab selection on table viewer. This will fire a selection based on
            // the selected table rows in the applicable tabbed viewer.
            tabFolder.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( SelectionEvent event ) {
                    if (event.getSource() instanceof TabFolder) {
                        // event came from the table. convert selection to EObjects and fire out to listeners
                        if (getCurrentViewer() != null) {
                            StructuredSelection tableSel = (StructuredSelection)getCurrentViewer().getSelection();

                            final Iterator rowIter = tableSel.iterator();
                            final ArrayList objectList = new ArrayList(tableSel.size());
                            for (final Iterator iter = rowIter; iter.hasNext();) {
                                final ModelRowElement mre = (ModelRowElement)iter.next();
                                objectList.add(mre.getModelObject());
                            }

                            selectionMgr.selectionChanged(new SelectionChangedEvent(getCurrentViewer(),
                                                                                    new StructuredSelection(objectList)));
                        }
                    }
                }
            });
        }

        return tabIndex;
    }

    /**
     * Build a HashMap of the content of the current EditorInput, where the keys are the unique EClass types of objects contained
     * in the model, and the values are ArrayLists of EObjects of the key type.
     * 
     * @return an ordered list of keys in the map
     * @throws CoreException
     */
    private Collection buildObjectTypeMap( HashMap objectTypeMap ) throws CoreException {
        Collection modelContent = modelResource.getEObjects();
        for (Iterator iter = modelContent.iterator(); iter.hasNext();) {
            loadObjectTypeMap(iter.next(), objectTypeMap);
        }
        TreeSet orderedSet = new TreeSet(new EClassNameComparator());
        orderedSet.addAll(objectTypeMap.keySet());
        return orderedSet;
    }

    /**
     * Recursively fill the model object HashMap beginning with the specified object.
     */
    private void loadObjectTypeMap( Object o,
                                    HashMap objectTypeMap ) {
        if (o instanceof EObject) {
            EObject eObj = (EObject)o;
            if (ModelObjectUtilities.isPrimaryMetamodelObject(eObj)) {
                ArrayList list = (ArrayList)objectTypeMap.get(eObj.eClass());
                if (list == null) {
                    list = new ArrayList();
                    objectTypeMap.put(eObj.eClass(), list);
                }
                list.add(o);

                // swj: amazingly, if you don't ask the EMF item provider hasChildren
                // before getting children, you don't always get the correct
                // array of children.
                if (contentProvider.hasChildren(eObj)) {
                    Object[] children = contentProvider.getChildren(eObj);
                    for (int i = 0; i < children.length; ++i) {
                        loadObjectTypeMap(children[i], objectTypeMap);
                    }
                }
            }
        }
    }

    /**
     * Creates and adds a new tab containing the given control to this editor. The control may be <code>null</code>, allowing it
     * to be created and set later using <code>setControl</code>.
     * 
     * @param control the control, or <code>null</code>
     * @return the index of the new tab
     * @see #setControl
     */
    protected int addTab( Control control ) {
        createTab(control);
        return getTabCount() - 1;
    }

    /**
     * Creates and adds a new tab containing the given control to this editor. The control may be <code>null</code>, allowing it
     * to be created and set later using <code>setControl</code>.
     * 
     * @param control the control, or <code>null</code>
     * @return the index of the new tab
     * @see #setControl
     */
    protected int addTab( Control control,
                          int index ) {
        return tabFolder.indexOf(createTab(control, index));
    }

    /**
     * Creates a tab item and places control in the new item. The item is a CTabItem with no style bits set.
     * 
     * @param control is the control to be placed in an item
     * @return a new item
     */
    private TabItem createTab( Control control ) {
        TabItem item = new TabItem(tabFolder, SWT.NONE);
        item.setControl(control);
        return item;
    }

    /**
     * Creates a tab item and places control in the new item. The item is a CTabItem with no style bits set.
     * 
     * @param control is the control to be placed in an item
     * @return a new item
     */
    private TabItem createTab( Control control,
                               int index ) {
        TabItem item = new TabItem(tabFolder, SWT.NONE, index);
        item.setControl(control);
        return item;
    }

    /**
     * Returns the control for the given tab index, or <code>null</code> if no control has been set for the tab. The index must be
     * valid.
     * <p>
     * Subclasses should not override this method
     * </p>
     * 
     * @param tabIndex the index of the tab
     * @return the control for the specified tab, or <code>null</code> if none has been set
     */
    protected Control getControl( int tabIndex ) {
        if (tabIndex >= 0 && tabIndex < tabFolder.getItemCount()) {
            return getItem(tabIndex).getControl();
        }
        return null;
    }

    /**
     * Returns the tab item for the given page index (page index is 0-based). The page index must be valid.
     * 
     * @param pageIndex the index of the tab
     * @return the CTabItem item for the given tab index
     */
    private TabItem getItem( int tabIndex ) {
        // May not have been created yet, or may have been disposed.
        if (tabFolder != null && !tabFolder.isDisposed()) {
            if (tabIndex > -1 && tabIndex < tabFolder.getItemCount()) {
                return tabFolder.getItem(tabIndex);
            }
        }
        return null;
    }

    /**
     * Returns the number of tabs in this editor.
     * 
     * @return the number of tabs
     */
    protected int getTabCount() {
        // May not have been created yet, or may have been disposed.
        if (tabFolder != null && !tabFolder.isDisposed()) return tabFolder.getItemCount();
        return 0;
    }

    /**
     * Sets the image for the tab with the given index, or <code>null</code> to clear the image for the tab. The page index must
     * be valid.
     * 
     * @param tabIndex the index of the page
     * @param image the image, or <code>null</code>
     */
    protected void setTabImage( int tabIndex,
                                Image image ) {
        getItem(tabIndex).setImage(image);
    }

    /**
     * Sets the text label for the tab with the given index. The tab index must be valid. The text label must not be null.
     * 
     * @param tabIndex the index of the page
     * @param text the text label
     */
    protected void setTabText( int tabIndex,
                               String text ) {
        getItem(tabIndex).setText(text);
    }

    /**
     * Sets the tooltip text for the tab with the given index. The tab index must be valid. The text must not be null.
     * 
     * @param tabIndex the index of the page
     * @param text the tool tip text
     */
    protected void setTabToolTipText( int tabIndex,
                                      String text ) {
        getItem(tabIndex).setToolTipText(text);
    }

    /**
     * Sets the currently active tab.
     * 
     * @param tabIndex the index of the page to be activated; the index must be valid
     */
    protected void setActiveTab( int tabIndex ) {
        if (tabIndex >= 0 && tabIndex < getTabCount()) {
            tabFolder.setSelection(tabIndex);
        }
    }

    /**
     * Sets focus on the active nested control if there is one.
     * <p>
     * Subclasses may extend or reimplement.
     * </p>
     */
    @Override
    public void setFocus() {
        int index = getActiveTab();
        if (index != -1) {
            setFocus(index);
            if (meParentEditor != null) {
                meParentEditor.setIgnoreInternalFocus(true);
            }
        }
    }

    /**
     * Sets focus to the control for the given tab. This calls <code>setFocus</code> on the control for the tab.
     * 
     * @tabIndex the index of the tab
     */
    private void setFocus( int tabIndex ) {
        if (tabIndex < 0 || tabIndex >= getTabCount()) return;
        Control control = getControl(tabIndex);
        if (control != null) {
            control.setFocus();
            // selectionMgr.setSelection(getS)
        }
    }

    /**
     * <p>
     * Returns the index of the currently active tab, or -1 if there is no active tab.
     * </p>
     * <p>
     * Subclasses should not override this method.
     * </p>
     * 
     * @return the index of the active tab, or -1 if there is no active tab
     */
    protected int getActiveTab() {
        if (tabFolder != null && !tabFolder.isDisposed()) {
            return tabFolder.getSelectionIndex();
        }
        return -1;
    }

    /**
     * Gets a <code>Map</code> of {@link TableViewer}s. There will be one for each tab within this editor page. The map is keyed
     * by the tab name.
     * 
     * @return the map
     */
    public Map getTableViewerMap() {
        return viewers;
    }

    protected void pageChange( int newPageIndex ) {
        // each page's table columns are created only after their associated tab in the tabfolder
        // has been selected. make sure the colums have been built.
        BusyIndicator.showWhile(null, new Runnable() {
            public void run() {
                if (getCurrentModel() != null) {
                    getCurrentModel().buildColumns();
                }
            }
        });
    }

    protected boolean isReadOnly() {
        IFileEditorInput input = (IFileEditorInput)getEditorInput();
        return input.getFile().isReadOnly();
    }

    private TableNotificationHandler getNotificationHandler() {
        if (notificationHandler == null) {
            notificationHandler = new TableNotificationHandler(this);
        }
        return notificationHandler;
    }

    protected Resource getEmfResource() {
        return emfResource;
    }

    /**
     * Allows the TableNotifierHandler to add a Table to this editor if new object types show up
     * 
     * @param eClass
     * @param instanceList
     */
    void addTable( EClass eClass,
                   ArrayList instanceList ) {

        // figure out what the tab index should be for the new EClass type
        String className = eClass.getName();
        TreeSet orderedNames = new TreeSet();
        orderedNames.addAll(viewers.keySet());
        orderedNames.add(className);
        int index = 0;
        for (Iterator iter = orderedNames.iterator(); iter.hasNext(); index++) {
            if (className.equals(iter.next())) {
                break;
            }
        }

        // create the tab at the appropriate index
        index = addTab(eClass, instanceList, index);

        // register the Table with the action contributor
        this.actionContributor.addContextMenu(getControl(index), className);

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.INavigationSupported
     **/
    public IMarker createMarker() {
        // jhTODO: implement
        //        System.out.println("[ModelTableEditor.createMarker] TOP"); //$NON-NLS-1$
        NavigationMarker nmMarker = new NavigationMarker();

        return nmMarker;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void gotoMarker( IMarker marker ) {
        EObject targetEObject = ModelObjectUtilities.getMarkedEObject(marker);

        if (targetEObject != null) {
            selectionMgr.setSelection(new StructuredSelection(targetEObject));
        }
    }

    /**
     * Receives the input information passed from the platform when this ModelEditorPart was created. Forwards to the baseclass.
     * 
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        setSite(site);
        setInput(input);
        try {
            IFileEditorInput fileInput = (IFileEditorInput)input;
            modelResource = ModelUtilities.getModelResource(fileInput.getFile(), true);
            if (modelResource != null) {
                emfResource = modelResource.getEmfResource();
            }
        } catch (CoreException e) {
            throw new PartInitException(Util.getString(PREFIX + "resourceError", input.getName()), e); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        // modTODO: implement
        return false;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {

    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {

    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    @Override
    public Image getTitleImage() {
        return UiPlugin.getDefault().getImage(PluginConstants.Images.TABLE_ICON);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        this.labelProvider.dispose();
        // Memory leak Defect 22290 - requires that we actually tell the actionContributor to dispose() so it can clean up all
        // the references & listeners to menu managers, etc....
        this.actionContributor.dispose();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#preDispose()
     * @since 4.2
     */
    public void preDispose() {
        // Default Implementation
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     */
    public boolean canDisplay( IEditorInput input ) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext( Object input ) {
        if (input instanceof EObject) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input ) {

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext( Object input,
                             boolean forceRefresh ) {

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#initializeEditorPage()
     * @since 5.0.2
     */
    public void initializeEditorPage() {
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public Control getControl() {
        return getControl(getActiveTab());
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return this.selectionMgr;
    }

    /**
     * Return a selectionChangedListener that can receive ISelections from the workbench and send them into the table
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return this.selectionMgr;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getActionBarContributor()
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        if (actionContributor == null) {
            actionContributor = new ModelTableEditorActionContributor(this);
        }
        return actionContributor;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    public void setLabelProvider( ILabelProvider provider ) {

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    public INotifyChangedListener getNotifyChangedListener() {
        return getNotificationHandler();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setParent()
     */
    public void setParent( ModelEditor meParentEditor ) {
        this.meParentEditor = meParentEditor;
    }

    /** CellModifier to set property values onto the objects in the table */
    class TableCellModifier implements ICellModifier {

        Object lastGetValue = null;

        public boolean canModify( Object element,
                                  String property ) {
            if (isReadOnly()) {
                return false;
            } else if (property.equals(UiConstants.LOCATION_KEY)) {
                return false;
            }
            return true;
        }

        public Object getValue( Object element,
                                String property ) {
            ModelRowElement row = (ModelRowElement)element;
            lastGetValue = row.getValueObject(property);
            return lastGetValue;
        }

        public void modify( Object element,
                            String property,
                            Object theValue ) {
            boolean modifyValue = false;
            Object value = theValue;
            // check for EMPTY name property
            if (property.equalsIgnoreCase("name")) { //$NON-NLS-1$
                if (theValue != null && theValue instanceof String && ((String)theValue).length() == 0) {
                    value = lastGetValue;
                }
            }
            if (lastGetValue == null && value != null) {
                modifyValue = true;
            } else if (value == null && lastGetValue != null) {
                modifyValue = true;
            } else if (value != null && lastGetValue != null) {
                if (!lastGetValue.equals(value)) {
                    modifyValue = true;
                }
            }

            if (modifyValue) {
                boolean startedTxn = ModelerCore.startTxn(UNDO_PREFIX + ModelObjectTableModel.getPropertyLabel(property), this);
                boolean succeeded = false;
                try {
                    ModelRowElement row = (ModelRowElement)((TableItem)element).getData();
                    row.setValue(property, value);
                    succeeded = true;
                } finally {
                    if (startedTxn) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }

        }
    }

    /** Comparator to order the tabs in this editor */
    class EClassNameComparator implements Comparator {
        public int compare( Object o1,
                            Object o2 ) {
            // Order alphabetically by name (ascending)
            EClass c1 = (EClass)o1;
            EClass c2 = (EClass)o2;
            return c1.getName().compareTo(c2.getName());
        }
    }

    /**
     * @since 4.0
     */
    class ModelObjectSelectionManager extends SelectionProvider implements ISelectionChangedListener {
        /**
         * @since 4.0
         */
        public void selectionChanged( final SelectionChangedEvent event ) {
            getModelObjectSelectionProvider().setSelection(event.getSelection());
            if (SelectionUtilities.isSingleSelection(event.getSelection())) {
                Object obj = ((IStructuredSelection)event.getSelection()).getFirstElement();
                if (obj instanceof EObject) {
                    Object activeEditor = meParentEditor.getActiveEditor();
                    if (activeEditor != null && activeEditor == ModelTableEditor.this) {
                        // ----------------------------
                        // Defect 22844 - setting ignoreInternalFocus
                        // This cleans up simple selection causing focus to OperationEditorPage way too often
                        // ----------------------------
                        meParentEditor.setIgnoreInternalFocus(true);
                    }
                }
            }
        }

        /**
         * @see com.metamatrix.ui.internal.eventsupport.SelectionProvider#getListenerList()
         * @since 4.2
         */
        @Override
        protected List getListenerList() {
            return super.getListenerList();
        }

        protected void removeTableSelectionListeners() {
            List listeners = getListenerList();
            List removeList = new ArrayList(listeners.size());
            Object nextListener = null;
            Iterator iter = listeners.iterator();

            while (iter.hasNext()) {
                nextListener = iter.next();
                if (nextListener instanceof ModelObjectTableSelectionManager) {
                    removeList.add(nextListener);
                }
            }

            iter = removeList.iterator();
            while (iter.hasNext()) {
                nextListener = iter.next();
                super.removeSelectionChangedListener((ModelObjectTableSelectionManager)nextListener);
            }
        }
    }

    public boolean canInsertRows() {

        boolean bResult = false;
        TableViewer viewer = getCurrentViewer();

        if (viewer != null) {

            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

            if (selection != null && !selection.isEmpty() && SelectionUtilities.isSingleSelection(selection)) {

                ModelRowElement mre = (ModelRowElement)selection.getFirstElement();
                EObject eObj = mre.getModelObject();

                bResult = (!ModelObjectUtilities.isReadOnly(eObj));
            }
        }

        return bResult;
    }

    public void insertRows( int iRowCount ) {

        TableViewer viewer = getCurrentViewer();

        if (viewer != null) {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

            if (!selection.isEmpty()) {

                ModelRowElement mre = (ModelRowElement)selection.getFirstElement();
                EObject eObj = mre.getModelObject();

                boolean started = ModelerCore.startTxn(true, UNDO_INSERT_ROWS, this);
                boolean succeeded = false;

                try {
                    EClass eClass = eObj.eClass();
                    Collection commands = ModelerCore.getModelEditor().getNewSiblingCommands(eObj);

                    if ((commands != null) && !commands.isEmpty()) {
                        // find the right command to use. it will be the one with the same result object eClass
                        Iterator itr = commands.iterator();
                        Command cmd = null;

                        while (itr.hasNext()) {
                            Command tempCmd = (Command)itr.next();
                            Collection result = tempCmd.getResult();

                            if ((result != null) && !result.isEmpty()) {
                                EObject sibling = (EObject)result.iterator().next();

                                if (sibling.eClass().equals(eClass)) {
                                    cmd = tempCmd;
                                    break;
                                }
                            }
                        }

                        if (cmd == null) {
                            Util.log(IStatus.ERROR, Util.getString(PREFIX + "msg.noSiblingCommandFound", //$NON-NLS-1$
                                                                   new Object[] {eClass}));
                        } else if (cmd.canExecute()) {
                            String originalName = ModelerCore.getModelEditor().getName(eObj);

                            if (iRowCount > 1) {
                                Collection clonedChildren = ModelerCore.getModelEditor().cloneMultiple(eObj, iRowCount);
                                if (clonedChildren != null) {
                                    EObject[] newChildArray = new EObject[clonedChildren.size()];
                                    clonedChildren.toArray(newChildArray);

                                    for (int i = newChildArray.length - 1; i >= 0; i--) {
                                        // Get a unique name given the name of the original
                                        renameToUniqueName(newChildArray[i], originalName);
                                    }
                                }
                            } else {
                                EObject newChild = ModelerCore.getModelEditor().clone(eObj);
                                renameToUniqueName(newChild, originalName);
                            }

                            succeeded = true;
                        } else {
                            Util.log(IStatus.ERROR, Util.getString(PREFIX + "msg.newSiblingCommandProblem", //$NON-NLS-1$
                                                                   new Object[] {eClass}));
                        }
                    }
                } catch (ModelerCoreException theException) {
                    Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "NewSiblingMenu.problem", //$NON-NLS-1$
                                                                         new Object[] {eObj}));
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }

            viewer.refresh();
        }
    }

    /**
     * Checks to see if the clipboard has text contents and if the table has a selection. If a table selection exists, the
     * selected column must not be readonly.
     * 
     * @return <code>true</code> if clipboard is not empty, the current viewer has a selection, and the selected column is not
     *         readonly; <code>false</code> otherwise.
     */
    public boolean canPaste() {
        boolean result = false;

        if (!SystemClipboardUtilities.isEmpty()) {
            TableViewer viewer = getCurrentViewer();

            if (viewer != null) {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

                if (!selection.isEmpty() && SelectionUtilities.isSingleSelection(selection)) {
                    TableColumnSelectionHelper helper = getCurrentSelectionHelper();
                    int selectedColumn = helper.getSelectedColumn();

                    if (selectedColumn != -1 && selectedColumn < viewer.getCellEditors().length) {
                        // if there is a CellEditor for this column, then the property is editable
                        result = viewer.getCellEditors()[selectedColumn] != null;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.table.ITablePasteValidator#constructPasteStatusRecord(java.lang.String, int, int)
     */
    public ClipboardPasteStatusRecord constructPasteStatusRecord( String theProposedValue,
                                                                  int theRow,
                                                                  int theColumn ) {
        /* ----- DESIGN NOTE -----
         * theRow and theColumn must be reference to the table not the paste data
         */

        ClipboardPasteStatusRecord result = null;
        Integer statusType = ClipboardPasteStatusRecord.VALID;
        String columnName = null;
        String errorMsg = null; // valid if null

        if (!willPasteDataRowFit(theRow)) {
            statusType = ClipboardPasteStatusRecord.ROW_TRUNCATED;
            errorMsg = getErrorMsg(theProposedValue, theRow, theColumn);
        } else if (!willPasteDataColumnFit(theColumn)) {
            statusType = ClipboardPasteStatusRecord.COLUMN_TRUNCATED;
            errorMsg = getErrorMsg(theProposedValue, theRow, theColumn);
            columnName = ClipboardPasteProblemDialog.TRUNCATED_COLUMN_TITLE;
        } else if (isProtected(theColumn)) {
            statusType = ClipboardPasteStatusRecord.PROTECTED_COLUMN;
            errorMsg = getErrorMsg(theProposedValue, theRow, theColumn);
        } else {
            errorMsg = getErrorMsg(theProposedValue, theRow, theColumn);

            if (errorMsg != null) {
                if (errorMsg.equals(ClipboardPasteStatusRecord.REFERENCE_COLUMN_MSG)) {
                    statusType = ClipboardPasteStatusRecord.PROTECTED_COLUMN;
                } else {
                    statusType = ClipboardPasteStatusRecord.ERROR;
                }
            }
        }

        // if columnName is null and the column fits, get the column name
        // otherwise the column will be truncated
        // if the row is being truncated, the check to see if the column will fit has not been done
        if (columnName == null) {
            columnName = (willPasteDataColumnFit(theColumn)) ? getCurrentTable().getColumn(theColumn).getText() : ClipboardPasteProblemDialog.TRUNCATED_COLUMN_TITLE;
        }

        if (errorMsg == null) {
            // valid value
            result = new ClipboardPasteStatusRecord(columnName, theProposedValue);
        } else {
            result = new ClipboardPasteStatusRecord(statusType, columnName, theProposedValue, errorMsg);
        }

        return result;
    }

    private List constructClipboardPasteStatusRecords( List thePasteData,
                                                       int theSelectedRow,
                                                       int theSelectedColumn ) {
        List result = new ArrayList(thePasteData.size());

        for (int numRows = thePasteData.size(), i = 0; i < numRows; i++) {
            List row = (List)thePasteData.get(i);
            List resultRow = new ArrayList(row.size());

            for (int numColumns = row.size(), j = 0; j < numColumns; j++) {
                String data = (String)row.get(j);
                resultRow.add(constructPasteStatusRecord(data, i + theSelectedRow, j + theSelectedColumn));
            }

            result.add(resultRow);
        }

        return result;
    }

    ModelObjectTableModel getCurrentModel() {
        TableViewer viewer = getCurrentViewer();

        return (viewer == null) ? null : (ModelObjectTableModel)modelMap.get(viewer);
    }

    private TableColumnSelectionHelper getCurrentSelectionHelper() {
        TableViewer viewer = getCurrentViewer();

        return (viewer == null) ? null : (TableColumnSelectionHelper)selectionHelperMap.get(viewer);
    }

    private Table getCurrentTable() {
        TableViewer viewer = getCurrentViewer();

        return (viewer == null) ? null : viewer.getTable();
    }

    public TableViewer getCurrentViewer() {
        if (tabFolder != null && !tabFolder.isDisposed()) {
            TabItem[] item = tabFolder.getSelection();
            if (item == null || item.length == 0 || item[0] == null) {
                return null;
            }
            return (item[0] == null) ? null : (TableViewer)viewers.get(item[0].getText());
        }
        return null;
    }

    /**
     * @param theProposedValue
     * @param theRow the table row where the proposed data will be pasted
     * @param theColumn the table column where the proposed data will be pasted
     * @return
     */
    private String getErrorMsg( String theProposedValue,
                                int theRow,
                                int theColumn ) {
        String result = null; // valid

        if (!willPasteDataRowFit(theRow)) {
            result = ClipboardPasteStatusRecord.ROW_TRUNCATED_MSG;
        } else if (!willPasteDataColumnFit(theColumn)) {
            result = ClipboardPasteStatusRecord.COLUMN_TRUNCATED_MSG;
        } else if (isProtected(theColumn)) {
            result = ClipboardPasteStatusRecord.PROTECTED_COLUMN_MSG;
        } else {
            // see if value validates
            CellEditor editor = getCurrentViewer().getCellEditors()[theColumn];

            if (editor != null) {
                ICellEditorValidator validator = editor.getValidator();

                if (validator != null) {
                    // if non-null, result is the error msg
                    result = validator.isValid(theProposedValue);
                } else {
                    if (editor instanceof ComboBoxCellEditor) {
                        int iMatch = -1;
                        ComboBoxCellEditor cbceEditor = (ComboBoxCellEditor)editor;
                        String sNewVal = theProposedValue.trim();

                        String[] sItems = cbceEditor.getItems();

                        for (int i = 0; i < sItems.length; i++) {
                            if (sNewVal.equals(sItems[i])) {
                                iMatch = i;
                            }
                        }
                        if (iMatch == -1) {
                            ModelRowElement row = (ModelRowElement)getCurrentViewer().getElementAt(theRow);
                            String propertyID = (String)row.getPropertyIdForColumn(theColumn);
                            result = UiConstants.Util.getString("ModelRowElement.invalidValueError", sNewVal, propertyID); //$NON-NLS-1$
                        }
                    }
                }
            }

            // jh Defect 19246: test for 'reference'
            if (result == null) {
                ModelRowElement row = (ModelRowElement)getCurrentViewer().getElementAt(theRow);
                Object propID = row.getPropertyIdForColumn(theColumn);
                if (propID instanceof String) {
                    result = row.getReferenceColumnMessage((String)propID, theProposedValue);
                }
            }

            if (result == null) {
                ModelRowElement row = (ModelRowElement)getCurrentViewer().getElementAt(theRow);
                Object propID = row.getPropertyIdForColumn(theColumn);
                if (propID instanceof String) {
                    result = row.getInvalidValueMessage((String)propID, theProposedValue);
                } else if (propID instanceof ExtensionPropertyDescriptor) {
                    result = row.getInvalidValueMessage((ExtensionPropertyDescriptor)propID, theProposedValue);
                }
            }
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.table.ITablePasteValidator#getSelectedRowAndColumn()
     */
    public int[] getSelectedRowAndColumn() {
        TableViewer viewer = getCurrentViewer();
        TableColumnSelectionHelper helper = (TableColumnSelectionHelper)selectionHelperMap.get(viewer);

        return helper.getSelectedRowAndColumn();
    }

    /**
     * Determines if the input is valid. The input can either be straight from the clipboard utilities parse content method or
     * from this class's construct status records method.
     * 
     * @param thePasteData the data can either be lists of lists containing strings or lists of lists containing
     *        ClipboardPasteStatusRecords
     * @return
     */
    private boolean isPasteDataValid( List thePasteData ) {
        boolean result = true;
        boolean rawData = true;

        if ((thePasteData != null) && !thePasteData.isEmpty()) {
            int[] rowCol = getSelectedRowAndColumn();
            int selectedRow = rowCol[0];
            int selectedColumn = rowCol[1];

            for (int numRows = thePasteData.size(), i = 0; i < numRows; i++) {
                List row = (List)thePasteData.get(i);

                for (int numColumns = row.size(), j = 0; j < numColumns; j++) {
                    // determine if looking at row data or status records
                    if (j == 0) {
                        Object tempColumn = row.get(j);

                        if (tempColumn instanceof ClipboardPasteStatusRecord) {
                            rawData = false;
                        }
                    }

                    String columnData = null;

                    if (rawData) {
                        columnData = (String)row.get(j);
                    } else {
                        columnData = ((ClipboardPasteStatusRecord)row.get(j)).getPasteData();
                    }

                    if (getErrorMsg(columnData, i + selectedRow, j + selectedColumn) != null) {
                        // if rawData (right from clipboard) any error msg should be flagged as an error
                        // if status record then check if error
                        result = (rawData) ? false : !((ClipboardPasteStatusRecord)row.get(j)).isError();

                        if (!result) {
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private boolean isProtected( int theColumn ) {
        boolean isProtected = false;
        ModelObjectTableModel tableModel = getCurrentModel();
        Object propID = tableModel.getPropertyIdAtIndex(theColumn);
        if (propID instanceof String) {
            if (isReadOnly()) {
                isProtected = true;
            } else if (propID.equals(UiConstants.LOCATION_KEY)) {
                return true;
            }
        } else {
            isProtected = isReadOnly();
        }
        return isProtected;
    }

    public void pasteClipboardContents() {
        if (canPaste()) {
            Table table = getCurrentTable();
            ModelObjectTableModel model = getCurrentModel();
            int[] rowCol = getSelectedRowAndColumn();
            int selectedRow = rowCol[0];
            int selectedColumn = rowCol[1];
            String clipboardContents = SystemClipboardUtilities.getContents();
            List pasteData = SystemClipboardUtilities.convertTableData(clipboardContents);

            // make sure data is valid
            if (willPasteDataFit(pasteData) && isPasteDataValid(pasteData)) {
                int numRows = table.getItemCount();
                int numCols = table.getColumnCount();
                int numPasteDataRows = pasteData.size();
                int numPasteDataColumns = ((List)pasteData.get(0)).size();
                getCurrentViewer().cancelEditing();

                boolean requiredStart = ModelerCore.startTxn(true,
                                                             true,
                                                             Util.getString(PREFIX + "tablePasteTransactionDescription.msg"), this); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    for (int i = 0; i < numPasteDataRows; i++) {
                        int modelRow = selectedRow + i;

                        // throw out row data that doesn't fit
                        if (modelRow == numRows) {
                            continue;
                        }

                        List pasteRow = (List)pasteData.get(i);

                        // Capture the ModelRowElement for this row
                        TableItem ti = getCurrentViewer().getTable().getItem(modelRow);
                        ModelRowElement mreRow = (ModelRowElement)ti.getData();

                        for (int j = 0; j < numPasteDataColumns; j++) {
                            int modelColumn = selectedColumn + j;

                            // throw out column data that doesn't fit
                            if (modelColumn == numCols) {
                                continue;
                            }

                            // to update the right row, we must call setValue on the ModelRowElement
                            // (This gets over any shuffling of rows the user may have done by column sorting.)
                            Object propID = model.getPropertyIdAtIndex(modelColumn);
                            if (propID instanceof String) {
                                mreRow.setValue((String)propID, pasteRow.get(j));
                            } else if (propID instanceof ExtensionPropertyDescriptor) {
                                mreRow.setValue((ExtensionPropertyDescriptor)propID, pasteRow.get(j));
                            }
                        }
                    }
                    succeeded = true;
                } finally {
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    } else {
                        Util.log(IStatus.ERROR, Util.getString(PREFIX + "tablePasteStartTransactionProblem.msg")); //$NON-NLS-1$
                    }
                }
            } else { // show dialog
                List statusRecords = constructClipboardPasteStatusRecords(pasteData, selectedRow, selectedColumn);
                Shell shell = UiUtil.getWorkbenchWindowOnlyIfUiThread().getShell();
                ClipboardPasteProblemDialog dialog = new ClipboardPasteProblemDialog(shell, statusRecords, this);
                int returnStatus = dialog.open();

                if (returnStatus == Window.OK) {
                    statusRecords = dialog.getStatusRecords();

                    if ((statusRecords != null) && !statusRecords.isEmpty() && isPasteDataValid(statusRecords)) {
                        getCurrentViewer().cancelEditing();

                        boolean requiredStart = ModelerCore.startTxn(true,
                                                                     true,
                                                                     Util.getString(PREFIX
                                                                                    + "tablePasteTransactionDescription.msg"), this); //$NON-NLS-1$
                        boolean succeeded = false;
                        try {
                            for (int numRows = statusRecords.size(), i = 0; i < numRows; i++) {
                                List pasteRow = (List)statusRecords.get(i);
                                int modelRow = selectedRow + i;

                                for (int numColumns = pasteRow.size(), j = 0; j < numColumns; j++) {
                                    int modelColumn = selectedColumn + j;
                                    ClipboardPasteStatusRecord statusRecord = (ClipboardPasteStatusRecord)pasteRow.get(j);

                                    if (!statusRecord.isColumnTruncated() && !statusRecord.isRowTruncated()
                                        && !statusRecord.isProtectedColumn()) {
                                        model.setValueAt(statusRecord.getPasteData(), modelRow, modelColumn);
                                    }
                                }
                            }
                            succeeded = true;
                        } finally {
                            if (requiredStart) {
                                if (succeeded) {
                                    ModelerCore.commitTxn();
                                } else {
                                    ModelerCore.rollbackTxn();
                                }
                            } else {
                                Util.log(IStatus.ERROR, Util.getString(PREFIX + "tablePasteStartTransactionProblem.msg")); //$NON-NLS-1$
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean willPasteDataColumnFit( int theColumn ) {
        boolean result = false;

        if (theColumn >= 0) {
            Table table = getCurrentTable();
            result = (theColumn < table.getColumnCount());
        }

        return result;
    }

    private boolean willPasteDataFit( List thePasteData ) {
        boolean result = false;

        if ((thePasteData != null) && !thePasteData.isEmpty()) {
            TableViewer viewer = getCurrentViewer();
            int[] rowCol = getSelectedRowAndColumn();
            int selectedRow = rowCol[0];
            int selectedColumn = rowCol[1];

            // make sure something is selected
            if ((selectedColumn != -1) && (selectedRow != -1)) {
                Table table = viewer.getTable();
                int tableColumnCount = table.getColumnCount();
                int pasteDataRows = thePasteData.size();
                int pasteDataColumns = ((List)thePasteData.get(0)).size();

                if (((selectedColumn + pasteDataColumns - 1) <= tableColumnCount)
                    && ((selectedRow + pasteDataRows - 1) < table.getItemCount())) {
                    result = true;
                }
            }
        }

        return result;
    }

    private boolean willPasteDataRowFit( int theRow ) {
        boolean result = false;

        if (theRow >= 0) {
            Table table = getCurrentTable();
            result = (theRow < table.getItemCount());
        }

        return result;
    }

    // jh Defect 19246: Added ability to copy selected rows to System Clipboard
    public void copySelectedToSystemClipboard() {

        Table table = getCurrentTable();
        int[] iSelectedRows = table.getSelectionIndices();
        int numCols = table.getColumnCount();

        String sVal = ""; //$NON-NLS-1$
        String sFullString = ""; //$NON-NLS-1$

        for (int iRow = 0; iRow < iSelectedRows.length; iRow++) {
            String sRowString = ""; //$NON-NLS-1$

            int iThisRow = iSelectedRows[iRow];

            // Capture the ModelRowElement for this row
            TableItem ti = getCurrentViewer().getTable().getItem(iThisRow);
            ModelRowElement mreRow = (ModelRowElement)ti.getData();

            for (int iCol = 0; iCol < numCols; iCol++) {

                // after the first col, prepend delimiter before adding column value string
                if (iCol > 0) {
                    sRowString += SystemClipboardUtilities.COLUMN_DELIMITER;
                }

                sVal = (String)mreRow.getValue(iCol);
                sRowString += sVal;
            }
            sFullString += sRowString + SystemClipboardUtilities.ROW_DELIMITER;
        }

        // apply the result to the system clipboard
        SystemClipboardUtilities.setContents(sFullString);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return TOOLTIP;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    @Override
    public String getTitle() {
        if (title == null) {
            return super.getTitle();
        }
        return title;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     * @since 4.2
     */
    public void setTitleText( String newTitle ) {
        this.title = newTitle;
    }

    @Override
    public Object getAdapter( Class type ) {
        if (type == IPrintable.class) {
            if (getTitleToolTip() != null) {
                // return a Printable created from the table in the currently selected tab
                return new Printable(getControl(tabFolder.getSelectionIndex()));
            }
        }

        return super.getAdapter(type);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     */
    public void updateReadOnlyState( boolean isReadOnly ) {
        // defect 16345 - table editor not working on checkout
        if (lastReadOnly != isReadOnly) {
            lastReadOnly = isReadOnly;
            Iterator itor = modelMap.keySet().iterator();

            // loop through all views:
            while (itor.hasNext()) {
                Object key = itor.next();
                Object val = modelMap.get(key);

                // refresh the editors for each table:
                if (key instanceof TableViewer && val instanceof ModelObjectTableModel) {

                    TableViewer tv = (TableViewer)key;
                    ModelObjectTableModel model = (ModelObjectTableModel)val;
                    model.rebuildColumnEditors(tv.getTable());

                } // endif -- correct object types
            } // endwhile -- views
        } // endif -- readonly state change
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {
            refresh();
        }
    }

    private void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (getControl() != null && !getControl().isDisposed()) {
                    // Remove Tab items.
                    TabItem[] tabs = tabFolder.getItems();
                    for (int i = tabs.length - 1; i >= 0; i--) {
                        tabs[i].dispose();
                    }
                    // Clear HashMap's and specific tables listeners.
                    modelMap.clear();
                    viewers.clear();
                    selectionHelperMap.clear();

                    selectionMgr.removeTableSelectionListeners();

                    // Recreate the tabs from the model

                    createTabs();

                    int tabID = getActiveTab();
                    if (tabID == 1 || tabID == 0) {
                        setActiveTab(0);

                        // programmatically setting selection to tab 0 while constructing doesn't get
                        // handled by our handler created in createContainer(Composite) since it hasn't been
                        // called yet. So call pageChange method so that tab 0 can construct it's columns
                        pageChange(0);
                    }
                }
            }
        });
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openComplete()
     * @since 4.2
     */
    public void openComplete() {
        // Default Implementation
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#canRenameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public IInlineRenameable getInlineRenameable( EObject theObj ) {
        return this;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#renameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public void renameInline( final EObject targetEObject,
                              IInlineRenameable renameable ) {
        if (renameable == this && targetEObject != null) {
            // Set Selection
            // Let's asynch this off
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    selectionMgr.setSelection(new StructuredSelection(targetEObject));
                    DoubleClickTableViewer dctv = (DoubleClickTableViewer)getCurrentViewer();
                    // We need to get the column for the Name property (if it exists)
                    TableColumn[] columns = dctv.getTable().getColumns();
                    for (int i = 0; i < columns.length; i++) {
                        if (columns[i].getText().equalsIgnoreCase("name")) { //$NON-NLS-1$
                            // We found the column
                            dctv.renameInline(i);
                            break;
                        }
                    }

                }
            });

        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#rename(org.eclipse.emf.ecore.resource.Resource, org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
    protected boolean renameToUniqueName( final EObject eObject,
                                          final String newName ) {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = ModelerCore.getModelEditor().getNameFeature(eObject);
        if (nameFeature != null) {
            generateUniqueInternalName(eObject.eContainer() == null ? eObject.eResource().getContents() : eObject.eContainer().eContents(),
                                       eObject,
                                       nameFeature,
                                       newName);
            return true;
        }
        return false;
    }

    private void generateUniqueInternalName( final EList siblings,
                                             final EObject eObject,
                                             final EStructuralFeature nameFeature,
                                             final String name ) {
        String newName = name;
        if (siblings != null) {
            final Set siblingNames = new HashSet();
            for (Iterator it = siblings.iterator(); it.hasNext();) {
                final EObject child = (EObject)it.next();
                if (eObject.getClass().equals(child.getClass())) {
                    siblingNames.add(child.eGet(nameFeature));
                }
            }
            boolean foundUniqueName = false;
            int index = 1;
            while (!foundUniqueName) {
                if (siblingNames.contains(newName)) {
                    newName = name + String.valueOf(index++);
                } else {
                    foundUniqueName = true;
                }
            }
        }
        eObject.eSet(nameFeature, newName);
    }

    /**
     * @return False.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst( IEditorInput input ) {
        return false;
    }
}

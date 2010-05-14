/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.ui.views.navigator.NavigatorDropAdapter;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.actions.CloneProjectAction2;
import com.metamatrix.modeler.internal.ui.actions.DeleteResourceAction;
import com.metamatrix.modeler.internal.ui.actions.PasteInResourceAction;
import com.metamatrix.modeler.internal.ui.actions.PasteSpecialAction;
import com.metamatrix.modeler.internal.ui.actions.PropertyDialogAction;
import com.metamatrix.modeler.internal.ui.actions.RemoveProjectAction;
import com.metamatrix.modeler.internal.ui.actions.SortModelContentsAction;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditorSelectionSynchronizer;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.util.EObjectTransfer;
import com.metamatrix.modeler.internal.ui.views.ModelViewer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelResourceActionManager;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.actions.ModelerGlobalActionsMap;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.modeler.ui.search.IModelObjectMatch;
import com.metamatrix.modeler.ui.search.MetadataMatchInfo;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.ExtendedMenuManager;
import com.metamatrix.ui.actions.GlobalActionsMap;
import com.metamatrix.ui.actions.IActionConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ModelExplorerResourceNavigator is the Navigator View for the MetaBase Modeler.
 * 
 * @since 4.0
 */
public class ModelExplorerResourceNavigator extends ResourceNavigator
    implements IActionConstants, IModelerActionConstants, IGotoMarker, ModelViewer, UiConstants {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelExplorerResourceNavigator.class);

    private static final String CREATE_RENAME_ACTION_ERROR_MESSAGE = getString("createRenameActionErrorMessage"); //$NON-NLS-1$
    public static final String MODELING_LABEL = UiConstants.Util.getString("ModelerSpecialActionManager.specialLabel"); //$NON-NLS-1$

    /**
     * An ID signifying the start of the view menu. Can be used by contributions which want to be added to the beginning of the
     * menu.
     * 
     * @since 5.0.1
     */
    protected static final String MENU_START_ID = "menuStart"; //$NON-NLS-1$

    protected static final String IMPORT_ACTION_ID = "modelExplorer.importAction"; //$NON-NLS-1$
    protected static final String EXPORT_ACTION_ID = "modelExplorer.exportAction"; //$NON-NLS-1$

    private static List<String> definedFilters;
    private static List<String> defaultFilters;

    public static List<String> getDefinedFilters() {
        // method code copied from org.eclipse.ui.views.navigator.FiltersContentProvider.getDefinedFilters()
        // FiltersContentProvider is a package private class
        if (definedFilters == null) {
            readFilters();
        }

        return definedFilters;
    }

    /**
     * @since 4.0
     */
    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private static void readFilters() {
        // method code copied from org.eclipse.ui.views.navigator.FiltersContentProvider.readFilters()
        // FiltersContentProvider is a package private class

        if (definedFilters == null) {
            definedFilters = new ArrayList<String>();
            defaultFilters = new ArrayList<String>();

            // org.eclipse.ui.views.navigator.ResourcePatternFilter.FILTERS_TAG = "resourceFilters"
            IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(PlatformUI.PLUGIN_ID, "resourceFilters"); //$NON-NLS-1$

            if (extension != null) {
                IExtension[] extensions = extension.getExtensions();

                for (int i = 0; i < extensions.length; i++) {
                    IConfigurationElement[] configElements = extensions[i].getConfigurationElements();

                    for (int j = 0; j < configElements.length; j++) {
                        String pattern = configElements[j].getAttribute("pattern"); //$NON-NLS-1$

                        if (pattern != null) {
                            definedFilters.add(pattern);
                        }

                        String selected = configElements[j].getAttribute("selected"); //$NON-NLS-1$

                        if (selected != null && selected.equalsIgnoreCase("true")) { //$NON-NLS-1$
                            defaultFilters.add(pattern);
                        }
                    }
                }
            }
        }
    }

    private ModelObjectPropertySourceProvider propertySourceProvider;
    private ISelectionListener selectionListener;
    private INotifyChangedListener notificationHandler;
    private ModelerGlobalActionsMap actionsMap;
    private ModelExplorerRenameAction renameAction;
    private ModelExplorerMoveAction moveAction;
    private ModelExplorerCopyAction copyAction;
    private RemoveProjectAction removeProjectAction;
    private CloneProjectAction2 cloneProjectAction;
    /** action allowing user to directly refresh tree */
    private IAction refreshAction;
    /** action allowing user to sort model contents alphabetically */
    private IAction sortModelContentsAction;
    private IResourceChangeListener markerListener;
    private EventObjectListener modelResourceListener;
    private PropertyDialogAction propertyAction;
    private MenuManager menuMgr; // context menu
    private IPartListener partListener;

    /**
     * Construct an instance of ModelExplorerResourceNavigator.
     */
    public ModelExplorerResourceNavigator() {
        super();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl( final Composite parent ) {
        super.createPartControl(parent);

        // Create selection helper
        new ModelExplorerSelectionHelper(getTreeViewer());

        /* 
         * BML TODO: Defect 21210 needs to screen out non-model projects.
         * However we can't do this for non-Model Explorer perspectives/products
         */
        if (UiPlugin.getDefault().isProductContextSupported(IModelerProductContexts.Views.ID_MODEL_PROJECT_FILTER)) {
            ModelWorkspaceViewerFilter filter = new ModelWorkspaceViewerFilter(true, true, true);
            // need to pass the pattern filter so that the view filter agrees with the resource filter settings of this navigator
            filter.setResourceFilter(getPatternFilter());
            getTreeViewer().addFilter(filter);
        }

        // register global actions
        final IWorkbenchWindow wdw = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        final ModelerActionService svc = (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
        final IActionBars bars = getViewSite().getActionBars();

        // MUST construct this action before registering default actions in order to cache the default eclipse copy action. if
        // this is done after the registering default actions then there is no way to get the ResourceNavigators default copy
        // action.
        this.copyAction = new ModelExplorerCopyAction(bars, svc);
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.copyAction);

        this.removeProjectAction = new RemoveProjectAction();
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.removeProjectAction);

        this.cloneProjectAction = new CloneProjectAction2();
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.cloneProjectAction);

        svc.registerDefaultGlobalActions(bars);

        actionsMap = new ModelerGlobalActionsMap();
        try {
            bars.setGlobalActionHandler(EclipseGlobalActions.COPY, copyAction);

            // rename action
            renameAction = new ModelExplorerRenameAction(wdw.getShell(), getTreeViewer());
            renameAction.selectionChanged((IStructuredSelection)getTreeViewer().getSelection()); // initial tree selection
            actionsMap.put(EclipseGlobalActions.RENAME, renameAction);
            bars.setGlobalActionHandler(EclipseGlobalActions.RENAME, renameAction);

            final IAction deleteAction = svc.getAction(DeleteResourceAction.class);
            bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);

            final IAction pasteAction = svc.getAction(PasteInResourceAction.class);
            bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);

            // move action
            moveAction = new ModelExplorerMoveAction(wdw.getShell(), getTreeViewer());
            bars.setGlobalActionHandler(ActionFactory.MOVE.getId(), moveAction);

            bars.updateActionBars();
        } catch (final CoreException err) {
            Util.log(err);
            WidgetUtil.showError(CREATE_RENAME_ACTION_ERROR_MESSAGE);
        }

        // used in context menu to replace default action
        propertyAction = new PropertyDialogAction(getTreeViewer().getControl(), getTreeViewer());
        bars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertyAction);

        // register to listen for Change Notifications
        notificationHandler = getNotifyChangedListener();
        if (notificationHandler != null) {
            ModelUtilities.addNotifyChangedListener(notificationHandler);
        }
        // register a part listener to refresh resource icons when ModelEditors open/close
        // use my page, not the active page:
        this.partListener = new IPartListener() {
            public void partActivated( IWorkbenchPart part ) {
            }

            public void partBroughtToTop( IWorkbenchPart part ) {
            }

            public void partClosed( IWorkbenchPart part ) {
            }

            public void partDeactivated( IWorkbenchPart part ) {
            }

            public void partOpened( IWorkbenchPart part ) {
                checkResource(part);
            }

            private void checkResource( final IWorkbenchPart part ) {
                if (part instanceof ModelEditor) {
                    Display.getCurrent().asyncExec(new Runnable() {
                        public void run() {
                            if (!getViewer().getTree().isDisposed()) {
                                getViewer().refresh(((ModelEditor)part).getModelFile());
                            }
                        }
                    });
                }
            }
        };
        getSite().getPage().addPartListener(this.partListener);

        markerListener = new IResourceChangeListener() {
            public void resourceChanged( IResourceChangeEvent event ) {
                final IMarkerDelta[] deltas = event.findMarkerDeltas(null, true);

                if (deltas != null && deltas.length > 0) {
                    Set<IProject> projects = new HashSet<IProject>();
                    for (int i = 0; i < deltas.length; ++i) {
                        projects.add(deltas[i].getResource().getProject());
                    }

                    final Iterator<IProject> itr = projects.iterator();

                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!getTreeViewer().getTree().isDisposed()) {
                                TreeViewer viewer = getTreeViewer();

                                while (itr.hasNext()) {
                                    IProject project = itr.next();
                                    viewer.refresh(project, true);
                                }
                            }
                        }
                    });
                }
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(markerListener);

        addCustomListeners();
    }

    /**
     * Created this protected method so the VdbView can override it. There were TWO resource event processors in Dimension and
     * Siperian kits and we only need to wire ONE of them up.
     * 
     * @since 5.0.2
     */
    protected void addCustomListeners() {
        modelResourceListener = new EventObjectListener() {
            public void processEvent( EventObject obj ) {
                ModelResourceEvent event = (ModelResourceEvent)obj;
                if (event.getType() == ModelResourceEvent.CLOSING) {
                    final IResource file = event.getResource();
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!getTreeViewer().getTree().isDisposed()) {
                                getTreeViewer().collapseToLevel(file, AbstractTreeViewer.ALL_LEVELS);
                            }
                        }
                    });
                } else if (event.getType() == ModelResourceEvent.CLOSED) {
                    final IResource file = event.getResource();
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!getTreeViewer().getTree().isDisposed()) {
                                getTreeViewer().remove(file);
                                getTreeViewer().refresh(file.getParent(), false);
                            }
                        }
                    });
                } else if (event.getType() == ModelResourceEvent.RELOADED) {
                    final IResource file = event.getResource();
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            if (!getTreeViewer().getTree().isDisposed()) {
                                getTreeViewer().refresh(file.getParent(), false);
                            }
                        }
                    });
                }
            }
        };
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    protected IAction getRefreshAction() {
        if (this.refreshAction == null) {
            this.refreshAction = new RefreshAction(getViewSite());
            this.refreshAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.REFRESH_ICON));
            this.refreshAction.setToolTipText(getString("refreshAction.tooltip")); //$NON-NLS-1$
            this.refreshAction.setText(getString("refreshAction.text")); //$NON-NLS-1$
            this.refreshAction.setId("modelExplorerResourceNavigator.refreshAction"); //$NON-NLS-1$
        }

        return this.refreshAction;
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#makeActions()
     * @since 4.3
     */
    @Override
    protected void makeActions() {
        super.makeActions();

        // view menu
        getViewSite().getActionBars().getMenuManager().add(new GroupMarker(MENU_START_ID));
        getViewSite().getActionBars().getMenuManager().add(new ShowImportsAction());

        // Preview Data Action from DQP Ui. If Exists, place in toolbar
        IAction previewAction = getPreviewDataAction();
        if (previewAction != null) {
            getViewSite().getActionBars().getToolBarManager().add(previewAction);
            getViewSite().getActionBars().getToolBarManager().add(new Separator());
        }
        // Selection of the sort button sets the preference, which will trigger a refresh
        getViewSite().getActionBars().getToolBarManager().add(getSortModelContentsAction());

        // add refresh tree action
        getViewSite().getActionBars().getToolBarManager().add(getRefreshAction());
    }

    protected IAction getSortModelContentsAction() {
        if (this.sortModelContentsAction == null) {
            this.sortModelContentsAction = new SortModelContentsAction(getTreeViewer());
        }

        return this.sortModelContentsAction;
    }

    protected IAction getPreviewDataAction() {
        return ModelerSpecialActionManager.getAction(Extensions.PREVIEW_DATA_ACTION_ID);
    }

    /**
     * Disposes of listeners registered in {@link #createPartControl(Composite)}.
     * 
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        // Remove listeners
        // unhook the selection listeners from the seleciton service
        getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(getModelObjectSelectionListener());

        if (this.copyAction != null) {
            getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this.copyAction);
        }

        if (propertySourceProvider != null) {
            propertySourceProvider.dispose();
        }
        if (notificationHandler != null) {
            ModelUtilities.removeNotifyChangedListener(notificationHandler);
        }
        if (markerListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(markerListener);
        }
        if (modelResourceListener != null) {
            try {
                UiPlugin.getDefault().getEventBroker().removeListener(modelResourceListener);
            } catch (EventSourceException e) {
                Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

        if (this.partListener != null) {
            getSite().getPage().removePartListener(this.partListener);
        }

        if (getViewer().getContentProvider() != null) {
            getViewer().getContentProvider().dispose();
        }

        if (getViewer().getLabelProvider() != null) {
            getViewer().getLabelProvider().dispose();
        }

        super.dispose();
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
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#getShowInTarget()
     */
    @Override
    protected IShowInTarget getShowInTarget() {
        return new IShowInTarget() {
            public boolean show( ShowInContext context ) {
                Set<EObject> toSelect = new HashSet<EObject>();
                ISelection sel = context.getSelection();

                if (sel instanceof IStructuredSelection) {
                    for (Object obj : ((IStructuredSelection)sel).toArray()) {

                        // a search results has been selected
                        if (obj instanceof IModelObjectMatch) {
                            EObject eObj = ((IModelObjectMatch)obj).getEObject();

                            if (eObj != null) {
                                toSelect.add(eObj);
                            }
                        } else if (obj instanceof MetadataMatchInfo) {
                            // a resource in the search result has been selected
                            Match[] matches = ((MetadataMatchInfo)obj).getMatches();
                            
                            for (Match match : matches) {
                                if (match instanceof IModelObjectMatch) {
                                    EObject eObj = ((IModelObjectMatch)match).getEObject();

                                    if (eObj != null) {
                                        toSelect.add(eObj);
                                    }
                                }
                            }
                        }
                    }
                }

                // select in tree
                if (!toSelect.isEmpty()) {
                    getViewer().getControl().setRedraw(false);
                    getViewer().setSelection(new StructuredSelection(toSelect.toArray()), true);
                    getViewer().getControl().setRedraw(true);
                    return true;
                }

                // if no EObjects let the superclass decide if they can be selected
                return superShowInTarget(context);
            }
        };
    }
    
    /**
     * Accessor to superclass method that sets viewer selection using the specified context.
     * 
     * @param context the objects to be selected
     * @return <code>true</code> if the context can be shown
     */
    boolean superShowInTarget( ShowInContext context ) {
        return super.getShowInTarget().show(context);
    }

    /**
     * Handles a selection changed event from the viewer. Overridden (copied, actually) from ResourceNavigator, with code to
     * update the status line commented out, since we are adding a separate SelectionChangedListener to take care of that. Still
     * updates the action bars and links to editor.
     * 
     * @param event the selection event
     * @since 4.0
     */
    @Override
    protected void handleSelectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection sel = (IStructuredSelection)event.getSelection();
        // Commenting out line below. Have a separate SelectionChangedListener to handle
        // updating the status line. BWP 05/21/03.
        // updateStatusLine(sel);
        updateActionBars(sel);
        linkToEditor(sel);
    }

    /**
     * Sets the content provider for the viewer. Overridden to use ModelExplorerContentProvider.
     * 
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initContentProvider(org.eclipse.jface.viewers.TreeViewer)
     * @since 4.0
     */
    @Override
    protected void initContentProvider( TreeViewer viewer ) {
        viewer.setContentProvider(new ModelExplorerContentProvider());

        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( final DoubleClickEvent e ) {
                ISelection selection = e.getSelection();
                if (SelectionUtilities.isSingleSelection(selection) && SelectionUtilities.getSelectedEObject(selection) != null) {
                    EObject eObj = SelectionUtilities.getSelectedEObject(selection);
                    ModelEditorManager.open(eObj, true, UiConstants.ObjectEditor.REFRESH_EDITOR_IF_OPEN);
                } else {
                    ModelEditorSelectionSynchronizer.handleDoubleClick(e);
                }
            }
        });

        // hook up our status bar manager for EObjects
        IStatusLineManager slManager = getViewSite().getActionBars().getStatusLineManager();
        viewer.addSelectionChangedListener(new StatusBarUpdater(slManager));

        // hook up a selection listener to the seleciton service
        getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(getModelObjectSelectionListener());
        // hook up this view's selection provider to this site
        getViewSite().setSelectionProvider(getModelObjectSelectionProvider());
    }

    /**
     * Registers the specified listener to be notified when the context menu is being shown.
     * 
     * @param theListener the listener being registered
     * @since 4.2
     */
    public void addContextMenuListener( IMenuListener theListener ) {
        menuMgr.addMenuListener(theListener);
    }

    /**
     * Unregisters the specified listener from the list being notified when the context menu is being shown.
     * 
     * @param theListener the listener being removed
     * @since 4.2
     */
    public void removeContextMenuListener( IMenuListener theListener ) {
        menuMgr.removeMenuListener(theListener);
    }

    /**
     * Creates and registers the context menu. Override so that we can get the context menu identifier we want.
     */
    @Override
    protected void initContextMenu() {
        this.menuMgr = new ExtendedMenuManager(UiConstants.Extensions.Explorer.CONTEXT_MENU);
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager theMenuMgr ) {
                ModelExplorerResourceNavigator.this.fillContextMenu(theMenuMgr);
            }
        });

        TreeViewer viewer = getTreeViewer();
        Menu menu = menuMgr.createContextMenu(viewer.getTree());
        viewer.getTree().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    protected void fillContextMenu( IMenuManager theMenu ) {
        final String ECLIPSE_RENAME_ID = "org.eclipse.ui.RenameResourceAction"; //$NON-NLS-1$
        final String ECLIPSE_DELETE_ID = "org.eclipse.ui.DeleteResourceAction"; //$NON-NLS-1$
        final String ECLIPSE_PASTE_ID = "org.eclipse.ui.PasteAction"; //$NON-NLS-1$
        final String ECLIPSE_MOVE_ID = "org.eclipse.ui.MoveResourceAction"; //$NON-NLS-1$

        ISelection selection = getViewer().getSelection();

        if (isAllExtendedModelObjects(selection)) {
            // Get the context menu from the extended model Objects
            Object[] objs = SelectionUtilities.getSelectedObjects(selection).toArray();
            boolean didOverride = false;
            for (int i = 0; i < objs.length; i++) {
                IExtendedModelObject obj = (IExtendedModelObject)objs[i];

                if (obj.overrideContextMenu()) {
                    didOverride = true;
                    obj.fillContextMenu(theMenu);
                }
            }

            if (didOverride) {
                if (theMenu.find(IModelerActionConstants.ContextMenu.ADDITIONS) == null) {
                    theMenu.add(new Separator(IModelerActionConstants.ContextMenu.ADDITIONS));
                }
                return;
            }
        }

        // if single or multi selection has only EObjects show our action service context menu
        // else show the ResourceNavigators
        if (SelectionUtilities.isAllEObjects(selection)) {
            getActionService().contributeToContextMenu(theMenu, actionsMap, selection);
        } else {
            super.fillContextMenu(theMenu);

            try {
                // need to override the delete in the context menu. the only way i could figure out was by
                // removing and adding. our action makes sure to close the model and close it's editor
                // (if necessary) prior to deleting.

                if (theMenu.find(ECLIPSE_DELETE_ID) != null) {
                    IAction deleteAction = getActionService().getAction(DeleteResourceAction.class);
                    theMenu.insertAfter(ECLIPSE_DELETE_ID, deleteAction);
                    theMenu.remove(ECLIPSE_DELETE_ID);
                }

                // Add PasteSpecialAction after PasteAction
                // (if single selection and model resource)
                if (SelectionUtilities.isSingleSelection(selection)) {
                    Object obj = SelectionUtilities.getSelectedObject(selection);

                    if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                        // Add PasteSpecial Action after Paste
                        if (theMenu.find(ECLIPSE_PASTE_ID) != null) {
                            IAction pasteSpecialAction = getActionService().getAction(PasteSpecialAction.class);
                            theMenu.insertAfter(ECLIPSE_PASTE_ID, pasteSpecialAction);
                        }
                    }
                }

                // need to override the paste in the context menu. the only way i could figure out was by
                // removing and adding. our action makes sure to close the model and close it's editor
                // (if necessary) prior to pasting.

                if (theMenu.find(ECLIPSE_PASTE_ID) != null) {
                    IAction pasteAction = getActionService().getAction(PasteInResourceAction.class);
                    theMenu.insertAfter(ECLIPSE_PASTE_ID, pasteAction);
                    theMenu.remove(ECLIPSE_PASTE_ID);
                }

                // override the rename in the context menu same as delete.

                if (theMenu.find(ECLIPSE_RENAME_ID) != null) {
                    theMenu.insertAfter(ECLIPSE_RENAME_ID, renameAction);
                    theMenu.remove(ECLIPSE_RENAME_ID);
                    renameAction.selectionChanged((IStructuredSelection)getTreeViewer().getSelection());
                }

                // override the move in the context menu same as delete.

                if (theMenu.find(ECLIPSE_MOVE_ID) != null) {
                    theMenu.insertAfter(ECLIPSE_MOVE_ID, moveAction);
                    theMenu.remove(ECLIPSE_MOVE_ID);
                    moveAction.selectionChanged((IStructuredSelection)getTreeViewer().getSelection());
                }

                // override the properties dialog action in the context menu.
                // unfortunately the default action does not use the ID setup in IWorkbenchConstants:-(
                // so can't use the menu.find(id) method
                // so first time the action is found set it's ID
                if (theMenu.find(ActionFactory.PROPERTIES.getId()) == null) {
                    IContributionItem[] items = theMenu.getItems();
                    IContributionItem oldItem = null;

                    // loop backwards since the item we're looking for is always at/near the bottom of the menu
                    for (int i = (items.length - 1); i >= 0; i--) {
                        if (items[i] instanceof ActionContributionItem) {
                            IAction action = ((ActionContributionItem)items[i]).getAction();

                            if (action instanceof org.eclipse.ui.dialogs.PropertyDialogAction) {
                                action.setId(ActionFactory.PROPERTIES.getId());
                                oldItem = items[i];
                                break;
                            }
                        }
                    }

                    // since the contribution id is set the action's id at construction, setting the
                    // action id above does not affect the contribution id. so the find done below will
                    // not work. so have to do a remove and add here.
                    if (oldItem != null) {
                        theMenu.remove(oldItem);
                        theMenu.add(propertyAction);
                    }
                }

                if (theMenu.find(ActionFactory.PROPERTIES.getId()) != null) {
                    theMenu.insertAfter(ActionFactory.PROPERTIES.getId(), propertyAction);
                    theMenu.remove(ActionFactory.PROPERTIES.getId());
                    propertyAction.selectionChanged((IStructuredSelection)getTreeViewer().getSelection());
                }

            } catch (CoreException theException) {
                Util.log(theException);
            }

            // Let's set up/insert our Insert markers

            if (theMenu.find(OpenFileAction.ID) != null) {
                theMenu.insertBefore(OpenFileAction.ID, new GroupMarker(ContextMenu.INSERT_START));
                theMenu.insertAfter(ContextMenu.INSERT_START, new Separator(ContextMenu.INSERT_END));
            }
            // if single selection and model resource add new child menu
            if (SelectionUtilities.isSingleSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);

                if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                    MenuManager newChildMenu = getActionService().getInsertChildMenu(selection);
                    getActionService().contributePermanentActionsToContextMenu(newChildMenu, selection);

                    // insert menu after New submenu.
                    // the new submenu doesn't have an ID so put it before the open action
                    //
                    // Menu item group for insert child and sibling
                    //
                    theMenu.insertAfter(ContextMenu.INSERT_START, newChildMenu);
                }
            }

            // add group for model related actions. this group is added to by actions in the manifest
            // Example actions are close model and rebuild imports.
            theMenu.insertBefore(ActionFactory.IMPORT.getId(), new GroupMarker(ContextMenu.MODEL_START));
            theMenu.insertBefore(ActionFactory.IMPORT.getId(), new Separator(ContextMenu.MODEL_START));
            theMenu.insertAfter(ContextMenu.MODEL_START, new GroupMarker(ContextMenu.MODEL_END));

            // Combine ModelResourceActions & Special ModelObject actions into a Modeling Menu
            MenuManager modelingActionMenu = getModelingActionMenu(selection);
            if (modelingActionMenu != null && modelingActionMenu.getItems().length > 0) {
                theMenu.insertBefore(ContextMenu.INSERT_END, modelingActionMenu);
            }
        }

        // if single selection and ANY resource add refactor menu
        if (SelectionUtilities.isSingleSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);

            if (obj instanceof IResource) {
                MenuManager refactorMenu = getActionService().getRefactorMenu(selection);

                // insert menu at end of the cut/copy/paste group
                if (refactorMenu != null) {
                    // find the location. Default to the end of the whole
                    // context menu.
                    if (theMenu.find(ECLIPSE_RENAME_ID) != null) {
                        theMenu.insertAfter(ECLIPSE_RENAME_ID, refactorMenu);

                    } else {
                        theMenu.insertBefore(ContextMenu.ADDITIONS, refactorMenu);
                    }
                }

                // remove the 'other' rename and move
                if (theMenu.find(ECLIPSE_RENAME_ID) != null) {
                    theMenu.remove(ECLIPSE_RENAME_ID);
                }

                // override the move in the context menu same as delete.
                if (theMenu.find(ECLIPSE_MOVE_ID) != null) {
                    theMenu.remove(ECLIPSE_MOVE_ID);
                }

            }
            // Add Remove project action if selection is IProject and it is closed
            if (obj instanceof IProject) {
                if (!((IProject)obj).isOpen()) {
                    theMenu.insertBefore(ContextMenu.ADDITIONS, removeProjectAction);
                } else {
                    if (DotProjectUtils.isModelerProject((IProject)obj)) {
                        theMenu.insertBefore(ContextMenu.ADDITIONS, cloneProjectAction);
                    }
                }
            }
        }

    }

    /**
     * Indicates if all selected objects are {@link EObject}s. None of the <code>EObject</codes> are remote
     * objects.
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if all selected objects are <code>EObject</code>; <code>false</code> otherwise.
     */
    public static boolean isAllExtendedModelObjects( ISelection theSelection ) {
        boolean result = ((theSelection != null) && !theSelection.isEmpty() && (theSelection instanceof IStructuredSelection));

        if (result) {
            Object[] objs = SelectionUtilities.getSelectedObjects(theSelection).toArray();

            for (int i = 0; i < objs.length; i++) {
                if (result) {
                    result = objs[i] instanceof IExtendedModelObject;
                }
            }
        }

        return result;
    }

    // Convienced method to get the modeling action sub-menu for a given selection.
    private MenuManager getModelingActionMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(MODELING_LABEL, ModelerActionBarIdManager.getModelingMenuId()); 

        MenuManager mosaMenu = ModelerSpecialActionManager.getModeObjectSpecialActionMenu(theSelection);
        if (mosaMenu != null && mosaMenu.getItems().length > 0) {
            Object[] items = mosaMenu.getItems();
            for (int i = 0; i < items.length; i++) {
                menu.add(mosaMenu.getItems()[i]);
            }
            menu.add(new Separator());
        }

        MenuManager mraMenu = ModelResourceActionManager.getModelResourceActionMenu(theSelection);

        if (mraMenu != null && mraMenu.getItems().length > 0) {
            Object[] items = mraMenu.getItems();
            for (int i = 0; i < items.length; i++) {
                menu.add(mraMenu.getItems()[i]);
            }
        }

        return menu;
    }

    /**
     * Gets the <code>ModelerActionService</code> from the plugin.
     * 
     * @return the action service
     */
    private ModelerActionService getActionService() {
        UiPlugin plugin = UiPlugin.getDefault();
        return (ModelerActionService)plugin.getActionService(getSite().getPage());
    }

    /**
     * Sets the label provider for the viewer. Overridden to use ModelExplorerLabelProvider.
     * 
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initLabelProvider(org.eclipse.jface.viewers.TreeViewer)
     * @since 4.0
     */
    @Override
    protected void initLabelProvider( final TreeViewer viewer ) {
        ModelExplorerLabelProvider lp = new ModelExplorerLabelProvider();
        final ILabelDecorator decorator = getPlugin().getWorkbench().getDecoratorManager().getLabelDecorator();
        viewer.setLabelProvider(new DecoratingLabelProvider(lp, decorator));
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initDragAndDrop()
     */
    @Override
    protected void initDragAndDrop() {
        // code copied from superclass. only change is to the drag adapter
        TreeViewer viewer = getTreeViewer();
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] {LocalSelectionTransfer.getInstance(), ResourceTransfer.getInstance(),
            FileTransfer.getInstance(), PluginTransfer.getInstance(), EObjectTransfer.getInstance()};

        // drop support
        viewer.addDragSupport(ops, transfers, new ModelExplorerDragAdapter(getTreeViewer()));

        // drop support
        NavigatorDropAdapter adapter = new ModelExplorerDropAdapter(getTreeViewer());
        adapter.setFeedbackEnabled(false);

        viewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, adapter);
    }

    /**
     * Overrides super to provide a dialog when there are issues restoring the state.
     * 
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#restoreState(org.eclipse.ui.IMemento)
     */
    @Override
    protected void restoreState( IMemento memento ) {
        // defect 19085 - Tell user about any problems restoring state:
        try {
            super.restoreState(memento);
        } catch (final Exception ex) {
            UiConstants.Util.log(ex);
            // run async to allow the GUI to come up first:
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(getViewSite().getShell(),
                                            Util.getString("ModelExplorerResourceNavigator.restoreStateError.title"), //$NON-NLS-1$
                                            Util.getString("ModelExplorerResourceNavigator.restoreStateError.text", ex.getLocalizedMessage())); //$NON-NLS-1$
                }
            });
        } // endtry
    }

    /**
     * @see com.metamatrix.modeler.ui.views.ModelViewer#addModelObjectDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
     */
    public void addModelObjectDoubleClickListener( IDoubleClickListener listener ) {
        if (getTreeViewer() != null) {
            getTreeViewer().addDoubleClickListener(listener);
        }
    }

    /**
     * Obtain the selection listener that will be used to synchronize this model object viewer
     * 
     * @return
     */
    public ISelectionListener getModelObjectSelectionListener() {
        if (selectionListener == null) {

            selectionListener = new ISelectionListener() {
                public void selectionChanged( IWorkbenchPart part,
                                              final ISelection selection ) {
                    if (part != ModelExplorerResourceNavigator.this && isSynchronized()) {
                        if ((selection instanceof IStructuredSelection) && !selection.isEmpty()) {
                            int nObj = ((IStructuredSelection)selection).size();
                            int nEObj = SelectionUtilities.getSelectedEObjects(selection).size();

                            if (nObj == nEObj) {
                                getTreeViewer().setSelection(selection, true);
                            } else {
                                // Defect 23541: Newly created models were not getting selected in the tree
                                if ((nObj == 1) && (((IStructuredSelection)selection).getFirstElement() instanceof IResource)) {
                                    // do an async here to ensure the resource treeitem has been created first
                                    getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
                                        public void run() {
                                            if (!getTreeViewer().getControl().isDisposed()) {
                                                getTreeViewer().setSelection(selection, true);

                                                // do a refresh if the viewer selection is empty in order to force
                                                // the treeitems to be created.
                                                if (getTreeViewer().getSelection().isEmpty()) {
                                                    ITreeContentProvider cp = (ITreeContentProvider)getTreeViewer().getContentProvider();
                                                    Object parent = cp.getParent(((IStructuredSelection)selection).getFirstElement());

                                                    if (parent == null) {
                                                        getTreeViewer().refresh(true);
                                                    } else {
                                                        getTreeViewer().refresh(parent, true);
                                                    }

                                                    // set selection one more time
                                                    getTreeViewer().setSelection(selection, true);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            };
        }
        return selectionListener;
    }

    /**
     * @see com.metamatrix.modeler.ui.views.ModelViewer#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return getTreeViewer();
    }

    /**
     * Selects the object represented by the specified marker.
     * 
     * @param theMarker the marker being processed
     */
    public void gotoMarker( IMarker theMarker ) {
        EObject targetEObject = ModelObjectUtilities.getMarkedEObject(theMarker);

        if (targetEObject != null) {
            getTreeViewer().setSelection(new StructuredSelection(targetEObject), true);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.views.ModelViewer#isSynchronized()
     */
    public boolean isSynchronized() {
        return isLinkingEnabled();
    }

    /**
     * @see com.metamatrix.modeler.ui.views.ModelViewer#removeModelObjectDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
     */
    public void removeModelObjectDoubleClickListener( IDoubleClickListener listener ) {
        getTreeViewer().removeDoubleClickListener(listener);
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#updateActionBars(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected void updateActionBars( IStructuredSelection selection ) {
        super.updateActionBars(selection);
        if (renameAction != null) {
            renameAction.selectionChanged(selection);
        }

        if (moveAction != null) {
            moveAction.selectionChanged(selection);
        }
    }

    private INotifyChangedListener getNotifyChangedListener() {
        if (notificationHandler == null) {
            notificationHandler = createNotifyChangedListener();
        }

        return notificationHandler;
    }

    protected INotifyChangedListener createNotifyChangedListener() {
        return new ModelExplorerNotificationHandler(getTreeViewer(), this);
    }

    private ModelerActionService getModelerActionService() {
        return (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
    }

    /**
     * Provide special handling for F2 (rename) and DEL (delete)
     */
    @Override
    protected void handleKeyPressed( KeyEvent event ) {
        if (event.stateMask != 0) return;

        if (event.keyCode == SWT.F2) {
            // rename action
            /*
             * ModelerActionService
             * fix for defect 12372: if selection is a single IResource, use the refactor rename instead
             */
            ISelection selection = getViewer().getSelection();

            // if single selection see if it is an IResource
            if (SelectionUtilities.isSingleSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);

                // if single selection and ANY resource use refactor rename
                if (obj instanceof IResource) {
                    // rename
                    IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    IActionDelegate delRefactorRename = new RenameRefactorAction();
                    IAction actRefactorRename = new DelegatableAction(delRefactorRename, window);
                    actRefactorRename.setText(""); //$NON-NLS-1$
                    actRefactorRename.setToolTipText(""); //$NON-NLS-1$
                    delRefactorRename.selectionChanged(actRefactorRename, selection);

                    if (actRefactorRename.isEnabled()) {
                        actRefactorRename.run();
                    }
                }
                // if single, but not an IResource, use the normal Rename
                else if (renameAction != null && renameAction.isEnabled()) {
                    renameAction.run();
                }
            }
            // if not single, use the normal Rename
            else if (renameAction != null && renameAction.isEnabled()) {
                renameAction.run();
            }

        } else if (event.character == SWT.DEL) {
            // delete action
            try {
                IAction actDelete = null;
                ISelection selection = getViewer().getSelection();

                if (SelectionUtilities.isAllEObjects(selection)) {
                    actDelete = getModelerActionService().getAction(ActionFactory.DELETE.getId());
                } else {
                    actDelete = getViewSite().getActionBars().getGlobalActionHandler(ActionFactory.DELETE.getId());
                }

                if (actDelete != null && actDelete.isEnabled()) {
                    actDelete.run();
                    event.doit = false;
                }

            } catch (CoreException ce) {
                System.out.println("[ModelExplorerResourceNavigator.handleKeyEvent] CoreException retrieving delete action"); //$NON-NLS-1$
            }
        } else {

            super.handleKeyPressed(event);
        }
    }

    protected class ShowImportsAction extends Action {
        public ShowImportsAction() {
            super(Util.getString(I18N_PREFIX + "showImportsAction"), IAction.AS_CHECK_BOX); //$NON-NLS-1$
            this.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.IMPORT_CONTAINER));
            setChecked(true);
        }

        private ModelExplorerContentProvider getContentProvider() {
            return (ModelExplorerContentProvider)getViewer().getContentProvider();
        }

        private TreeViewer getViewer() {
            return ModelExplorerResourceNavigator.this.getViewer();
        }

        @Override
        public void run() {
            getContentProvider().setShowImportStatements(isChecked());
            getViewer().refresh();
        }
    }

    /**
     * The <code>ModelExplorerCopyAction</code> delegates the copying to either the default Eclipse ResourceNavigator copy action
     * or to the EObject copy action based on workspace selection. Must be constructed before global actions are overwritten.
     * 
     * @since 4.2
     */
    private class ModelExplorerCopyAction extends Action implements ISelectionListener {
        private IAction defaultAction = null; // default eclipse copy action
        private IAction modelerAction = null; // eobject copy action
        private IAction currentAction = null; // current action based on selection

        public ModelExplorerCopyAction( IActionBars theActionBars,
                                        ActionService theService ) {
            // cache the eclipse resource copy action
            this.defaultAction = theActionBars.getGlobalActionHandler(EclipseGlobalActions.COPY);

            if (this.defaultAction == null) {
                this.defaultAction = GlobalActionsMap.UNSUPPORTED_ACTION;
                Util.log(IStatus.ERROR, getString("msg.noDefaultCopyActionFound")); //$NON-NLS-1$
            }

            // cache the EObject copy action
            try {
                this.modelerAction = theService.getAction(EclipseGlobalActions.COPY);
            } catch (CoreException theException) {
                Util.log(theException);
            }

            if (this.modelerAction == null) {
                this.modelerAction = GlobalActionsMap.UNSUPPORTED_ACTION;
            }

            // initialize state
            this.currentAction = defaultAction;
            setEnabled(this.currentAction.isEnabled());
        }

        /**
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
         *      org.eclipse.jface.viewers.ISelection)
         * @since 4.2
         */
        public void selectionChanged( IWorkbenchPart thePart,
                                      ISelection theSelection ) {
            IAction handler = (SelectionUtilities.isAllEObjects(theSelection)) ? this.modelerAction : this.defaultAction;

            // switch handlers if necessary
            if (this.currentAction != handler) {
                this.currentAction = handler;
            }

            setEnabled(this.currentAction.isEnabled());
        }

        /**
         * @see org.eclipse.jface.action.Action#run()
         * @since 4.2
         */
        @Override
        public void run() {
            this.currentAction.runWithEvent(new Event());
        }
    }
}

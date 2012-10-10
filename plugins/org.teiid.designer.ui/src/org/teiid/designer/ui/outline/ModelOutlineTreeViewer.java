/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.outline;

import java.util.EventObject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.teiid.core.designer.event.EventObjectListener;
import org.teiid.core.designer.event.EventSourceException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.actions.ModelerGlobalActionsMap;
import org.teiid.designer.ui.actions.TreeViewerRenameAction;
import org.teiid.designer.ui.common.actions.ExtendedMenuManager;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorPageOutline;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.views.ModelViewer;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.StatusBarUpdater;


/**
 * ModelOutlineTreeViewer
 *
 * @since 8.0
 */
public class ModelOutlineTreeViewer extends ContentOutlinePage
    implements ModelEditorPageOutline, IMenuListener, IModelerActionConstants, ModelViewer {

    ModelEditor modelEditor;
    private ISelectionListener selectionListener;
    private ModelerGlobalActionsMap actionsMap;
    private INotifyChangedListener notificationHandler;
    private ImageDescriptor icon = UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.OUTLINE_ICON);
    private String toolTipText = "ModelOutlineTreeViewer.tooltip"; //$NON-NLS-1$
    private IWorkbenchPart workbenchPart;
    private ILabelProvider labelProvider;
    private TreeViewerRenameAction renameAction;
    private EventObjectListener modelResourceListener;

    public ModelOutlineTreeViewer( ModelEditor editor ) {
        this.modelEditor = editor;
    }

    @Override
    public void init( IPageSite thePageSite ) {
        super.init(thePageSite);
    }

    /* Overridden based on JavaDoc comments in the superclass.
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);

        IEditorInput input = modelEditor.getEditorInput();

        ITreeContentProvider contentProvider = new ModelOutlineContentProvider(input);
        getTreeViewer().setContentProvider(contentProvider);

        ILabelDecorator decorator = UiUtil.getWorkbench().getDecoratorManager().getLabelDecorator();
        labelProvider = new DecoratingLabelProvider(new ModelExplorerLabelProvider(), decorator);
        getTreeViewer().setLabelProvider(labelProvider);
        getTreeViewer().setInput(input);
        getTreeViewer().expandToLevel(2);
        getTreeViewer().reveal(contentProvider.getElements(input)[0]);

        // hook up our status bar manager for EObjects
        IStatusLineManager slManager = getSite().getActionBars().getStatusLineManager();
        addSelectionChangedListener(new StatusBarUpdater(slManager));

        // hook up a selection listener to the seleciton service
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(getModelObjectSelectionListener());
        // hook up this view's selection provider to this site
        getSite().setSelectionProvider(getModelObjectSelectionProvider());

        // Register to listen for Change Notifications
        notificationHandler = new ModelOutlineNotificationHandler(this);
        ModelUtilities.addNotifyChangedListener(notificationHandler);

        // create up the inline tree rename action
        renameAction = new TreeViewerRenameAction();
        renameAction.setTreeViewer(getTreeViewer(), labelProvider);

        final ModelerActionService svc = (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
        svc.addWorkbenchSelectionListener(renameAction);

        // add the rename action to the Global Action Map
        actionsMap = new ModelerGlobalActionsMap();
        actionsMap.put(EclipseGlobalActions.RENAME, this.renameAction);

        // register rename with global actions
        final IActionBars bars = getSite().getActionBars();
        svc.registerDefaultGlobalActions(bars);
        bars.setGlobalActionHandler(EclipseGlobalActions.RENAME, renameAction);
        bars.updateActionBars();

        // construct context menu - this is populated in the menuAboutToShow(IMenuManager) method
        String contextMenuId = ModelOutlinePage.class.getName() + ContextMenu.MENU_ID_SUFFIX;
        MenuManager mgr = new ExtendedMenuManager(null, contextMenuId);
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(this);
        Control control = getControl();
        Menu contextMenu = mgr.createContextMenu(control);
        control.setMenu(contextMenu);
        getSite().registerContextMenu(contextMenuId, mgr, getModelObjectSelectionProvider());

        initDragAndDrop();

        modelResourceListener = new EventObjectListener() {
            @Override
			public void processEvent( EventObject obj ) {
                ModelResource modelResource = ((ModelResourceEvent)obj).getModelResource();
                if (modelEditor.getModelResource().equals(modelResource)) {
                    refresh();
                }
            }
        };
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
                handleRefresh();
            }
        });
    }

    void handleRefresh() {
        if (getTreeViewer() != null && !getTreeViewer().getTree().isDisposed()) {
            getTreeViewer().refresh();
        }
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initDragAndDrop()
     */
    protected void initDragAndDrop() {
        try {
            EditingDomain domain = ((ContainerImpl)ModelerCore.getModelContainer()).getEditingDomain();
            int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
            Transfer[] transfers = new Transfer[] {LocalTransfer.getInstance()};
            getTreeViewer().addDragSupport(dndOperations, transfers, new OutlineDragAdapter(getTreeViewer()));
            getTreeViewer().addDropSupport(dndOperations,
                                           transfers,
                                           new ModelOutlineTreeViewerDropAdapter(domain, getTreeViewer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor for the ModelOutlineNotificationHandler to operate on the TreeViewer.
     * 
     * @return
     */
    TreeViewer getTree() {
        return getTreeViewer();
    }

    /**
     * Accessor for the ModelOutlineNotificationHandler to run the TreeViewerRenameAction when a new object is created in this
     * viewer.
     * 
     * @return
     */
    IWorkbenchPart getWorkbenchPart() {
        return workbenchPart;
    }

    /**
     * @See org.teiid.designer.ui.views.ModelViewer#addModelObjectDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
     */
    @Override
	public void addModelObjectDoubleClickListener( IDoubleClickListener listener ) {
        getTreeViewer().addDoubleClickListener(listener);
    }

    /**
     * Obtain the selection listener that will be used to synchronize this model object viewer
     * 
     * @return
     */
    public ISelectionListener getModelObjectSelectionListener() {
        if (selectionListener == null) {

            selectionListener = new ISelectionListener() {
                @Override
				public void selectionChanged( IWorkbenchPart part,
                                              ISelection selection ) {
                    if (!(part instanceof ContentOutline) && isSynchronized()) {
                        if ((selection instanceof IStructuredSelection) && !selection.isEmpty()
                            && !ModelOutlineTreeViewer.this.getSelection().equals(selection)) {
                            ModelOutlineTreeViewer.this.setSelection(selection);
                        }
                    }
                }
            };
        }
        return selectionListener;
    }

    /**
     * Gets the <code>ModelResource</code> for the editor whose being shown in this outline.
     * 
     * @return the model resource
     */
    ModelResource getModelResource() {
        return this.modelEditor.getModelResource();
    }

    /**
     * @See org.teiid.designer.ui.views.ModelViewer#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return getTreeViewer();
    }

    /**
     * @See org.teiid.designer.ui.views.ModelViewer#isSynchronized()
     */
    public boolean isSynchronized() {
        // the outline view is always synchronized
        return true;
    }

    /**
     * @see org.eclipse.jface.action.IMenuListener#init(org.eclipse.jface.action..IMenuManager)
     */
    @Override
	public void menuAboutToShow( IMenuManager theMenuMgr ) {
        IWorkbenchWindow window = modelEditor.getSite().getWorkbenchWindow();
        ModelerActionService actionService = (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
        ISelection selection = getTreeViewer().getSelection();

        actionService.contributeToContextMenu(theMenuMgr, actionsMap, selection);

        if (workbenchPart == null) {
            workbenchPart = window.getActivePage().getActivePart();
        }
    }

    @Override
	public void removeModelObjectDoubleClickListener( IDoubleClickListener listener ) {
        getTreeViewer().removeDoubleClickListener(listener);
    }

    /**
     * @see org.eclipse.ui.part.IPage#dispose()
     */
    @Override
    public void dispose() {
        // unhook the selection listener from the seleciton service
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(getModelObjectSelectionListener());
        // unhook the notification handler
        ModelUtilities.removeNotifyChangedListener(notificationHandler);
        // unregister from the plugin's EventBroker
        if (this.modelResourceListener != null) {
            try {
                UiPlugin.getDefault().getEventBroker().removeListener(this.modelResourceListener);
            } catch (EventSourceException e) {
                // no need to do anything
            }
        }

        super.dispose();
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPageOutline#getIcon()
     */
    @Override
	public ImageDescriptor getIcon() {
        return icon;
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPageOutline#getToolTipText()
     */
    @Override
	public String getToolTipText() {
        return UiPlugin.getDefault().getPluginUtil().getString(toolTipText);
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPageOutline#isEnabled()
     */
    @Override
	public boolean isEnabled() {
        return true;
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelEditorPageOutline#setVisible(boolean)
     */
    @Override
	public void setVisible( boolean isVisible ) {
    }

    private class OutlineDragAdapter extends ViewerDragAdapter {
        public OutlineDragAdapter( Viewer theViewer ) {
            super(theViewer);
        }

        /**
         * @see org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
         */
        @Override
        public void dragStart( DragSourceEvent theEvent ) {
            // if model is read-only stop the drag
            if (ModelUtilities.isReadOnly(getModelResource())) {
                theEvent.doit = false;
            }

            super.dragStart(theEvent);
        }
    }

}

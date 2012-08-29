/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.actions.ExecuteVDBAction;
import org.teiid.designer.runtime.ui.connection.CreateDataSourceAction;
import org.teiid.designer.runtime.ui.server.DeleteServerAction;
import org.teiid.designer.runtime.ui.server.DisconnectFromServerAction;
import org.teiid.designer.runtime.ui.server.EditServerAction;
import org.teiid.designer.runtime.ui.server.NewServerAction;
import org.teiid.designer.runtime.ui.server.ReconnectToServerAction;
import org.teiid.designer.runtime.ui.server.SetDefaultServerAction;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider.DataSourcesFolder;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;

/**
 * @since 8.0
 */
public class TeiidServerActionProvider extends CommonActionProvider {

    /**
     * Memento info for saving and restoring menu state from session to session
     */
    private static final String MENU_MEMENTO = "menu-settings"; //$NON-NLS-1$
    private static final String SHOW_PREVIEW_VDBS = "show-preview-vdbs"; //$NON-NLS-1$
    private static final String SHOW_PREVIEW_DATA_SOURCES = "show-preview-data-sources"; //$NON-NLS-1$
    private static final String SHOW_TRANSLATORS = "show-translators"; //$NON-NLS-1$
    
    private ICommonActionExtensionSite actionSite;
    
    private CommonViewer viewer;
    
    /**
     * Collapses all tree nodes.
     */
    private IAction collapseAllAction;

    /**
     * Deletes a server.
     */
    private DeleteServerAction deleteServerAction;

    /**
     * Creates a new server.
     */
    private NewServerAction newServerAction;

    /**
     * Edits a server's properties.
     */
    private EditServerAction editServerAction;
    /**
     * Refreshes the server connections.
     */
    private ReconnectToServerAction reconnectAction;
    
    private DisconnectFromServerAction disconnectAction;

    /**
     * Sets the selected Server as the default server for preview and execution
     */
    private SetDefaultServerAction setDefaultServerAction;

    private Action createDataSourceAction;

    private Action deleteDataSourceAction;

    private Action undeployVdbAction;

    private Action executeVdbAction;
    
    private IAction showPreviewVdbsAction;

    private IAction showPreviewDataSourcesAction;
    
    private IAction showTranslatorsAction;
    
    private ExecutionAdmin currentSelectedAdmin;

    private ISelectionProvider selectionProvider;

    /**
     * <code>true</code> if the viewer should show preview VDBs
     */
    private boolean showPreviewVdbs;
    
    /**
     * <code>true</code> if the viewer should show preview data sources
     */
    private boolean showPreviewDataSources;
    
    /**
     * <code>true</code> if the viewer should show translators
     */
    private boolean showTranslators;
    
    
    
    
    /**
     * Create instance
     */
    public TeiidServerActionProvider() {
        super();
    }
    
    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
        this.actionSite = aSite;
        ICommonViewerSite site = aSite.getViewSite();
        if( site instanceof ICommonViewerWorkbenchSite ) {
            StructuredViewer v = aSite.getStructuredViewer();
            if( v instanceof CommonViewer ) {
                viewer = (CommonViewer)v;
                ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)site;
                selectionProvider = wsSite.getSelectionProvider();
                initActions();
                updateViewerFilters();
            }
        }
    }
    
    private String getString( final String stringId ) {
        return DqpUiConstants.UTIL.getString(TeiidView.PREFIX + stringId);
    }
    
    private String getString( final String stringId, final Object param ) {
        return DqpUiConstants.UTIL.getString(TeiidView.PREFIX + stringId, param);
    }
    
    private List<Object> getSelectedObjects() {
        ISelection selection = selectionProvider.getSelection();
        if (!selection.isEmpty()) {
            return SelectionUtilities.getSelectedObjects(selection);
        }

        return null;
    }
    
    /**
     * @return the server manager
     */
    private TeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }
    
    /**
     * @return <code>true</code> if preview is enabled
     */
    private boolean isPreviewEnabled() {
        PreviewManager previewManager = getServerManager().getPreviewManager();
        return ((previewManager != null) && previewManager.isPreviewEnabled());
    }
    
    /**
     * Applies the current viewer filters.
     */
    private void updateViewerFilters() {
        List<ViewerFilter> filters = new ArrayList<ViewerFilter>(3);

        if (!this.showPreviewDataSources) {
            filters.add(TeiidView.PREVIEW_DATA_SOURCE_FILTER);
        }

        if (!this.showPreviewVdbs) {
            filters.add(TeiidView.PREVIEW_VDB_FILTER);
        }

        // set new content filters
        this.viewer.setFilters(filters.toArray(new ViewerFilter[filters.size()]));
    }
    
    /**
     * Handler for when the show preview VDBs menu action is selected
     */
    private void toggleShowPreviewVdbs() {
        this.showPreviewVdbs = !this.showPreviewVdbs;
        updateViewerFilters();
    }
    
    /**
     * Handler for when the show preview data sources menu action is selected
     */
    private void toggleShowPreviewDataSources() {
        this.showPreviewDataSources = !this.showPreviewDataSources;
        updateViewerFilters();
    }
    
    /**
     * Handler for when the show translator menu action is selected
     */
    private void toggleShowTranslators() {
        this.showTranslators = !this.showTranslators;
//        this.treeProvider.setShowTranslators(this.showTranslators);
        refreshViewer();
    }
    
    private void refreshViewer() {
        for (TeiidServer teiidServer : getServerManager().getServers()) {
            if (teiidServer.isConnected()) {
                this.viewer.refresh(teiidServer);
            }
        }
    }
    
    /*
     *  Initialize view actions, set icons and action text.
     */
    private void initActions() {
        
        this.collapseAllAction = new Action() {
            @Override
            public void run() {
                viewer.collapseAll();
            }
        };

        this.collapseAllAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.COLLAPSE_ALL_ICON));
        this.collapseAllAction.setToolTipText(getString("collapseAllAction.tooltip")); //$NON-NLS-1$
        this.collapseAllAction.setEnabled(true);

        this.deleteDataSourceAction = new Action(getString("deleteTeiidDataSourceAction")) { //$NON-NLS-1$
            @Override
            public void run() {
                List<Object> selectedObjs = getSelectedObjects();
                for (Object obj : selectedObjs) {
                    TeiidDataSource tds = (TeiidDataSource)obj;
                    ExecutionAdmin admin = tds.getAdmin();
                    if (admin != null) {
                        try {
                            admin.deleteDataSource(tds.getName());
                        } catch (Exception e) {
                            DqpUiConstants.UTIL.log(IStatus.WARNING,
                                                    e,
                                                    getString("errorDeletingDataSource", tds.getDisplayName())); //$NON-NLS-1$
                        }
                    }
                }

            }
        };

        this.deleteDataSourceAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DELETE_ICON));
        this.deleteDataSourceAction.setToolTipText(getString("deleteDataSourceAction.tooltip")); //$NON-NLS-1$
        this.deleteDataSourceAction.setEnabled(true);

        this.undeployVdbAction = new Action(getString("undeployVdbAction")) { //$NON-NLS-1$
            @Override
            public void run() {
                List<Object> selectedObjs = getSelectedObjects();
                for (Object obj : selectedObjs) {
                    TeiidVdb vdb = (TeiidVdb)obj;

                    ExecutionAdmin admin = vdb.getAdmin();
                    if (admin != null) {
                        try {
                            admin.undeployVdb(vdb.getVdb());
                        } catch (Exception e) {
                            DqpUiConstants.UTIL.log(IStatus.WARNING,
                                                    e,
                                                    getString("errorUndeployingVdb", vdb.getName())); //$NON-NLS-1$
                        }
                    }
                }

            }
        };

        this.undeployVdbAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DELETE_ICON));
        this.undeployVdbAction.setToolTipText(getString("undeployVdbAction.tooltip")); //$NON-NLS-1$
        this.undeployVdbAction.setEnabled(true);

        this.executeVdbAction = new Action(getString("executeVdbAction")) { //$NON-NLS-1$
            @Override
            public void run() {
                List<Object> selectedObjs = getSelectedObjects();
                for (Object obj : selectedObjs) {
                    TeiidVdb vdb = (TeiidVdb)obj;

                    ExecutionAdmin admin = vdb.getAdmin();
                    if (admin != null) {
                        try {
                            // admin.undeployVdb(vdb.getVdb());
                            ExecuteVDBAction.executeVdb(admin.getServer(), vdb.getVdb().getName());
                        } catch (Exception e) {
                            DqpUiConstants.UTIL.log(IStatus.WARNING,
                                                    e,
                                                    getString("DeployVdbAction.problemDeployingVdbToServer", vdb.getName())); //$NON-NLS-1$
                        }
                    }
                }

            }
        };

        this.executeVdbAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EXECUTE_VDB));
        this.executeVdbAction.setToolTipText(getString("undeployVdbAction.tooltip")); //$NON-NLS-1$
        this.executeVdbAction.setEnabled(true);

        // the shell used for dialogs that the actions display
        Shell shell = this.actionSite.getViewSite().getShell();
        // the reconnect action tries to ping a selected server
        this.reconnectAction = new ReconnectToServerAction(viewer);
        viewer.addSelectionChangedListener(this.reconnectAction);
        
        // the disconnect action clears the server's object cache, closes connection and null's admin references.
        this.disconnectAction = new DisconnectFromServerAction(viewer);
        viewer.addSelectionChangedListener(this.disconnectAction);

        // the delete action will delete one or more servers
        this.deleteServerAction = new DeleteServerAction(shell, getServerManager());
        viewer.addSelectionChangedListener(this.deleteServerAction);

        // the edit action is only enabled when one server is selected
        this.editServerAction = new EditServerAction(shell, getServerManager());
        viewer.addSelectionChangedListener(this.editServerAction);

        // the new server action is always enabled
        this.newServerAction = new NewServerAction(shell, getServerManager());

        this.createDataSourceAction = new Action() {

            @Override
            public void run() {
                if (currentSelectedAdmin != null) {
                    CreateDataSourceAction action = new CreateDataSourceAction();
                    action.setAdmin(currentSelectedAdmin);

                    action.setSelection(new StructuredSelection());

                    action.setEnabled(true);
                    action.run();
                }
            }
        };

        this.createDataSourceAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SOURCE_BINDING_ICON));
        this.createDataSourceAction.setText(getString("createDataSourceAction.text")); //$NON-NLS-1$
        this.createDataSourceAction.setToolTipText(getString("createDataSourceAction.tooltip")); //$NON-NLS-1$
        this.createDataSourceAction.setEnabled(true);

        // the edit action is only enabled when one server is selected
        this.setDefaultServerAction = new SetDefaultServerAction(getServerManager());
        viewer.addSelectionChangedListener(this.setDefaultServerAction);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, editServerAction);
        fillLocalToolBar(actionBars.getToolBarManager());
        fillLocalPullDown(actionBars.getMenuManager());
        actionBars.updateActionBars();
    }
    
    private void fillLocalToolBar( IToolBarManager manager ) {
        manager.removeAll();
        
        manager.add(this.newServerAction);
        manager.add(this.reconnectAction);
        manager.add(new Separator());
        manager.add(this.collapseAllAction);
    }
    
    private void fillLocalPullDown( IMenuManager menuMgr ) {
        // add the show preview VDBs action
        this.showPreviewVdbsAction = new Action(getString("showPreviewVdbsMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
            @Override
            public void run() {
                toggleShowPreviewVdbs();
            }
        };

        // restore state and add to menu
        this.showPreviewVdbsAction.setChecked(this.showPreviewVdbs);
        menuMgr.add(this.showPreviewVdbsAction);

        // add the show translators action
        this.showTranslatorsAction = new Action(getString("showTranslatorsMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
            @Override
            public void run() {
                toggleShowTranslators();
            }
        };

        // restore state and add to menu
        this.showTranslatorsAction.setChecked(this.showTranslators);
        menuMgr.add(this.showTranslatorsAction);

        // add the show preview data sources action
        this.showPreviewDataSourcesAction = new Action(
                                                       getString("showPreviewDataSourcesMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
            @Override
            public void run() {
                toggleShowPreviewDataSources();
            }
        };

        // restore state and add to menu
        this.showPreviewDataSourcesAction.setChecked(this.showPreviewDataSources);
        menuMgr.add(this.showPreviewDataSourcesAction);

        final IAction enablePreviewAction = new Action(
                                                       getString("enablePreviewMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#setChecked(boolean)
             */
            @Override
            public void setChecked( boolean checked ) {
                super.setChecked(checked);

                if (checked != isPreviewEnabled()) {
                    DqpPlugin.getInstance().getPreferences().putBoolean(PreferenceConstants.PREVIEW_ENABLED, checked);
                }
            }
        };

        menuMgr.add(enablePreviewAction);

        // before the menu shows set the state of the enable preview action
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow( IMenuManager manager ) {
                enablePreviewAction.setChecked(isPreviewEnabled());
            }
        });
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager manager) {
        List<Object> selectedObjs = getSelectedObjects();

        if (selectedObjs != null && !selectedObjs.isEmpty()) {
            if (selectedObjs.size() == 1) {
                Object selection = selectedObjs.get(0);
                if (selection instanceof TeiidServer) {
                    if (((TeiidServer)selection).isConnected()) {
                        try {
                            currentSelectedAdmin = ((TeiidServer)selection).getAdmin();
                        } catch (Exception e) {
                            // DO NOTHING
                        }
                    }
                    manager.add(this.editServerAction);

                    if (this.setDefaultServerAction.isEnabled()) {
                        manager.add(this.setDefaultServerAction);
                    }
                    if (currentSelectedAdmin != null) {
                        manager.add(this.disconnectAction);
                    }
                    manager.add(this.reconnectAction);
                    manager.add(new Separator());
                    manager.add(this.newServerAction);
                    if (currentSelectedAdmin != null) {
                        manager.add(this.createDataSourceAction);
                    }
                    manager.add(new Separator());
                    manager.add(this.deleteServerAction);

                } else if (selection instanceof TeiidTranslator) {
                    currentSelectedAdmin = ((TeiidTranslator)selection).getAdmin();
                    manager.add(this.newServerAction);
                    if (currentSelectedAdmin != null) {
                        manager.add(this.createDataSourceAction);
                    }
                } else if (selection instanceof TeiidDataSource) {
                    manager.add(this.createDataSourceAction);
                    manager.add(new Separator());
                    manager.add(this.deleteDataSourceAction);
                    manager.add(new Separator());
                    manager.add(this.newServerAction);
                    currentSelectedAdmin = ((TeiidDataSource)selection).getAdmin();
                } else if (selection instanceof TeiidVdb) {
                    currentSelectedAdmin = ((TeiidVdb)selection).getAdmin();
                    this.executeVdbAction.setEnabled(((TeiidVdb)selection).isActive());
                    manager.add(this.executeVdbAction);
                    manager.add(new Separator());
                    manager.add(this.undeployVdbAction);
                    manager.add(new Separator());
                    manager.add(this.newServerAction);
                    manager.add(this.createDataSourceAction);
                } else if (selection instanceof DataSourcesFolder) {
                    currentSelectedAdmin = ((DataSourcesFolder)selection).getAdmin();
                    if (currentSelectedAdmin != null) {
                        manager.add(this.createDataSourceAction);
                    }
                }
            } else {
                boolean allDataSources = true;

                for (Object obj : selectedObjs) {
                    if (!(obj instanceof TeiidDataSource)) {
                        allDataSources = false;
                        break;
                    }
                }
                if (allDataSources) {
                    manager.add(this.deleteDataSourceAction);
                    manager.add(new Separator());
                    manager.add(this.newServerAction);
                    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                    return;
                }

                boolean allVdbs = true;

                for (Object obj : selectedObjs) {
                    if (!(obj instanceof TeiidVdb)) {
                        allVdbs = false;
                        break;
                    }
                }
                if (allVdbs) {
                    manager.add(this.undeployVdbAction);
                    manager.add(new Separator());
                    manager.add(this.newServerAction);
                    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                    return;
                }

                manager.add(this.newServerAction);
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }

        } else {
            manager.add(this.newServerAction);
        }

        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.navigator.CommonActionProvider#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(IMemento memento) {
        IMemento menuMemento = memento.createChild(MENU_MEMENTO);
        menuMemento.putBoolean(SHOW_PREVIEW_DATA_SOURCES, this.showPreviewDataSourcesAction.isChecked());
        menuMemento.putBoolean(SHOW_PREVIEW_VDBS, this.showPreviewVdbsAction.isChecked());
        menuMemento.putBoolean(SHOW_TRANSLATORS, this.showTranslatorsAction.isChecked());
        super.saveState(memento);
    }

    private void restoreLocalPullDown(IMemento viewMemento) {
        // need to check for null since first time view is opened in a new workspace there won't be previous state
        if (viewMemento != null) {
            IMemento menuMemento = viewMemento.getChild(MENU_MEMENTO);
            
            // also need to check for null here if running an existing workspace that didn't have this memento created
            if (menuMemento != null) {
                this.showPreviewDataSources = menuMemento.getBoolean(SHOW_PREVIEW_DATA_SOURCES);
                this.showPreviewVdbs = menuMemento.getBoolean(SHOW_PREVIEW_VDBS);
                this.showTranslators = menuMemento.getBoolean(SHOW_TRANSLATORS);
                
                if (viewer.getContentProvider() instanceof TeiidServerContentProvider) {
                    ((TeiidServerContentProvider) viewer.getContentProvider()).setShowTranslators(this.showTranslators);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.navigator.CommonActionProvider#restoreState(org.eclipse.ui.IMemento)
     */
    @Override
    public void restoreState(IMemento aMemento) {
        restoreLocalPullDown(aMemento);
        super.restoreState(aMemento);
    }
}

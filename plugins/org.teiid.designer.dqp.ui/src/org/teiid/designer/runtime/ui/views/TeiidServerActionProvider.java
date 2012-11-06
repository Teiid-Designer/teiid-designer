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
import org.teiid.designer.runtime.ITeiidDataSource;
import org.teiid.designer.runtime.ITeiidVdb;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.actions.ExecuteVDBAction;
import org.teiid.designer.runtime.ui.connection.CreateDataSourceAction;
import org.teiid.designer.runtime.ui.server.DisconnectFromServerAction;
import org.teiid.designer.runtime.ui.server.EditServerAction;
import org.teiid.designer.runtime.ui.server.NewServerAction;
import org.teiid.designer.runtime.ui.server.RefreshServerAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.server.SetDefaultServerAction;
import org.teiid.designer.runtime.ui.views.content.DataSourcesFolder;
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
     * Add a new jboss / teiid server
     */
    private NewServerAction newServerAction;

    /**
     * Edits a server's properties.
     */
    private EditServerAction editServerAction;
    
    /**
     * Refreshes the server connections.
     */
    private RefreshServerAction refreshAction;
    
    /**
     * Disconnect the server
     */
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
    
    private IAction enablePreviewAction;
    
    private IMenuListener enablePreviewActionListener = new IMenuListener() {

        @Override
        public void menuAboutToShow( IMenuManager manager ) {
            enablePreviewAction.setChecked(isPreviewEnabled());
        }
    };

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
        
        if (!this.showTranslators) {
            filters.add(TeiidView.TRANSLATORS_FILTER);
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
        updateViewerFilters();
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
                    ITeiidDataSource tds = RuntimeAssistant.adapt(obj, ITeiidDataSource.class);
                    TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                    
                    if (teiidServer != null && teiidServer.isConnected()) {
                        try {
                            teiidServer.deleteDataSource(tds.getName());
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
                    ITeiidVdb vdb = RuntimeAssistant.adapt(obj, ITeiidVdb.class);
                    TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                    
                    if (teiidServer != null && teiidServer.isConnected()) {
                        try {
                            teiidServer.undeployVdb(vdb.getName());
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
                    ITeiidVdb vdb = RuntimeAssistant.adapt(obj, ITeiidVdb.class);
                    TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                    
                    if (teiidServer != null && teiidServer.isConnected()) {
                        try {
                            // admin.undeployVdb(vdb.getVdb());
                            ExecuteVDBAction.executeVdb(teiidServer, vdb.getName());
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
        this.refreshAction = new RefreshServerAction(shell.getDisplay());
        viewer.addSelectionChangedListener(this.refreshAction);
        
        // the disconnect action clears the server's object cache, closes connection and null's admin references.
        this.disconnectAction = new DisconnectFromServerAction(shell.getDisplay());
        viewer.addSelectionChangedListener(this.disconnectAction);

        // the edit action is only enabled when one server is selected
        this.editServerAction = new EditServerAction(shell, getServerManager());
        viewer.addSelectionChangedListener(this.editServerAction);

        // the new server action is always enabled
        this.newServerAction = new NewServerAction(shell, getServerManager());
        
        this.createDataSourceAction = new Action() {

            @Override
            public void run() {
                TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                
                if (teiidServer != null && teiidServer.isConnected()) {
                    CreateDataSourceAction action = new CreateDataSourceAction();
                    action.setTeiidServer(teiidServer);
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
        manager.add(this.refreshAction);
        manager.add(new Separator());
        manager.add(this.collapseAllAction);
    }
    
    private void fillLocalPullDown( IMenuManager menuMgr ) {
        menuMgr.removeAll();
        menuMgr.removeMenuListener(enablePreviewActionListener);
        
        // add the show preview VDBs action
        if (showPreviewVdbsAction == null) {
            showPreviewVdbsAction = new Action(getString("showPreviewVdbsMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void run() {
                    toggleShowPreviewVdbs();
                }
            };
        }

        // add the show translators action
        if (showTranslatorsAction == null) {
            showTranslatorsAction = new Action(getString("showTranslatorsMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void run() {
                    toggleShowTranslators();
                }
            };
        }
        
        // add the show preview data sources action
        if (showPreviewDataSourcesAction == null) {
            showPreviewDataSourcesAction = new Action(
                                                       getString("showPreviewDataSourcesMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void run() {
                    toggleShowPreviewDataSources();
                }
            };
        }

        showPreviewVdbsAction.setChecked(this.showPreviewVdbs);
        showTranslatorsAction.setChecked(this.showTranslators);
        showPreviewDataSourcesAction.setChecked(this.showPreviewDataSources);

        if (enablePreviewAction == null) {
            enablePreviewAction = new Action(
                                             getString("enablePreviewMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void setChecked( boolean checked ) {
                    super.setChecked(checked);

                    if (checked != isPreviewEnabled()) {
                        DqpPlugin.getInstance().getPreferences().putBoolean(PreferenceConstants.PREVIEW_ENABLED, checked);
                    }
                }
            };
        }
        
        // before the menu shows set the state of the enable preview action
        menuMgr.addMenuListener(enablePreviewActionListener);

        menuMgr.add(showPreviewVdbsAction);
        menuMgr.add(showTranslatorsAction);
        menuMgr.add(showPreviewDataSourcesAction);
        menuMgr.add(enablePreviewAction);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager manager) {
        List<Object> selectedObjs = getSelectedObjects();

        manager.removeAll();
        
        manager.add(new Separator());
        manager.add(newServerAction);
        manager.add(refreshAction);
        manager.add(new Separator());
        
        if (selectedObjs == null || selectedObjs.isEmpty()) {
            // Other plug-ins can contribute there actions here
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            return;
        }
        
        TeiidServer teiidServer = null;
        
        if (selectedObjs.size() == 1) {
            Object selection = selectedObjs.get(0);
            
            // This will adapt either the TeiidResourceNode or the
            // TeiidServerContainerNode to the TeiidServer
            teiidServer = RuntimeAssistant.adapt(selection, TeiidServer.class);
            
            if (teiidServer != null) {
                boolean teiidServerConnected = teiidServer.isConnected();
                
                if (this.setDefaultServerAction.isEnabled()) {
                    manager.add(this.setDefaultServerAction);
                }
            
                if (teiidServerConnected) {
                    manager.add(this.disconnectAction);
                }
            
                if (teiidServerConnected) {
                    manager.add(this.createDataSourceAction);
                }
                
                manager.add(new Separator());
                manager.add(this.editServerAction);
                manager.add(new Separator());
                
            } else if (RuntimeAssistant.adapt(selection, ITeiidDataSource.class) != null) {
                manager.add(this.deleteDataSourceAction);                
                manager.add(new Separator());
                
            } else if (RuntimeAssistant.adapt(selection, ITeiidVdb.class) != null) {
                ITeiidVdb teiidVdb = RuntimeAssistant.adapt(selection, ITeiidVdb.class);
                this.executeVdbAction.setEnabled(teiidVdb.isActive());
                manager.add(this.executeVdbAction);
                manager.add(new Separator());
                manager.add(this.undeployVdbAction);
                
            } else if (selection instanceof DataSourcesFolder) {
                manager.add(this.createDataSourceAction);
            }
        } else {
            // More than 1 selected object
            
            boolean allDataSources = true;
            
            for (Object obj : selectedObjs) {
                if (RuntimeAssistant.adapt(obj, ITeiidDataSource.class) == null) {
                    allDataSources = false;
                    break;
                }
            }
            if (allDataSources) {
                manager.add(this.deleteDataSourceAction);
            }

            boolean allVdbs = true;
            for (Object obj : selectedObjs) {
                if (RuntimeAssistant.adapt(obj, ITeiidVdb.class) == null) {
                    allVdbs = false;
                    break;
                }
            }
            if (allVdbs) {
                manager.add(this.undeployVdbAction);
            }

            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        }
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
        updateViewerFilters();
        super.restoreState(aMemento);
    }
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.actions.ExecuteVDBAction;
import org.teiid.designer.runtime.ui.connection.CreateDataSourceAction;
import org.teiid.designer.runtime.ui.connection.CreateDynamicVdbAction;
import org.teiid.designer.runtime.ui.connection.CreateVdbDataSourceAction;
import org.teiid.designer.runtime.ui.dialogs.ClearPreviewArtifactsOptionsDialog;
import org.teiid.designer.runtime.ui.server.DisconnectFromServerAction;
import org.teiid.designer.runtime.ui.server.EditServerAction;
import org.teiid.designer.runtime.ui.server.RefreshServerAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.server.SetDefaultServerAction;
import org.teiid.designer.runtime.ui.views.content.DataSourcesFolder;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.UiUtil;

/**
 * @since 8.0
 */
public class TeiidServerActionProvider extends CommonActionProvider {

    /**
     * Prefix for language NLS properties
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(TeiidServerActionProvider.class);
    
    private ICommonActionExtensionSite actionSite;

    private CommonViewer viewer;
    
    /**
     * Collapses all tree nodes.
     */
    private IAction collapseAllAction;

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
     * Sets the selected Server as the default teiid instance for preview and execution
     */
    private SetDefaultServerAction setDefaultServerAction;

    private Action createDataSourceAction;

    private Action deleteDataSourceAction;

    private Action undeployVdbAction;

    private Action executeVdbAction;
    
    private IAction showPreviewVdbsAction;

    private IAction showPreviewDataSourcesAction;
    
    private IAction enablePreviewAction;
    
    private IAction clearPreviewArtifactsAction;
    
    private Action createVdbDataSourceAction;
    
    private Action importAsDynamicVdbAction;

    private final TeiidServerPreviewOptionContributor previewOptionContributor;

    private IMenuListener enablePreviewActionListener = new IMenuListener() {

        @Override
        public void menuAboutToShow( IMenuManager manager ) {
            enablePreviewAction.setChecked(isPreviewEnabled());
        }
    };

    private ISelectionProvider selectionProvider;

    /**
     * Create instance
     */
    public TeiidServerActionProvider() {
        super();
        previewOptionContributor = TeiidServerPreviewOptionContributor.getDefault();
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
            }
        }
    }
    
    private String getString( final String stringId ) {
        return DqpUiConstants.UTIL.getString(PREFIX + stringId);
    }
    
    private String getString( final String stringId, final Object param ) {
        return DqpUiConstants.UTIL.getString(PREFIX + stringId, param);
    }
    
    private List<Object> getSelectedObjects() {
        ISelection selection = selectionProvider.getSelection();
        if (!selection.isEmpty()) {
            return SelectionUtilities.getSelectedObjects(selection);
        }

        return Collections.emptyList();
    }
    
    /**
     * @return <code>true</code> if preview is enabled
     */
    private boolean isPreviewEnabled() {
        return PreviewManager.getInstance().isPreviewEnabled();
    }

    /**
     * Handler for when the show preview options menu actions are selected
     */
    private void toggleShowPreviewOptions() {
        previewOptionContributor.setShowPreviewOptions(
                                                       showPreviewDataSourcesAction.isChecked(),
                                                       showPreviewVdbsAction.isChecked());
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
                    ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
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
                    ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                    
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
                    ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                    
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
        this.refreshAction = new RefreshServerAction();
        viewer.addSelectionChangedListener(this.refreshAction);
        
        // the disconnect action clears the server's object cache, closes connection and null's admin references.
        this.disconnectAction = new DisconnectFromServerAction(shell.getDisplay());
        viewer.addSelectionChangedListener(this.disconnectAction);

        // the edit action is only enabled when one server is selected
        this.editServerAction = new EditServerAction();
        viewer.addSelectionChangedListener(this.editServerAction);
        
        this.clearPreviewArtifactsAction = new Action() {

            @Override
            public void run() {
                ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                
                if (teiidServer != null && teiidServer.isConnected()) {
                    // TODO: Open Clear Preview Artifacts dialog
                	//  Contains "VDBs" and/or "Data Sources" check-boxes
                	// OK or Cancel buttons
                	
            		Shell shell = UiUtil.getWorkbenchWindowOnlyIfUiThread().getShell();
            		ClearPreviewArtifactsOptionsDialog dialog = new ClearPreviewArtifactsOptionsDialog(shell);

            		dialog.open();

            		if (dialog.getReturnCode() == Window.OK) {
            			String workspaceUuid = ModelerCore.workspaceUuid().toString();
            			
            			if( dialog.doClearVdbs() ) {
            				try {
            					Collection<ITeiidVdb> vdbs = new ArrayList<ITeiidVdb>(teiidServer.getVdbs());
            					for( ITeiidVdb vdb : vdbs ) {
            						if( vdb.getName().contains(workspaceUuid) ) {
            							teiidServer.undeployVdb(vdb.getName());
            						}
            					}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								DqpUiConstants.UTIL.log(IStatus.WARNING,
                                        e,
                                        getString("errorClearingPreviewVdbs", teiidServer.getDisplayName())); //$NON-NLS-1$
							}
            			}
            			if( dialog.doClearDataSources() ) {
            				try {
								Collection<ITeiidDataSource> dataSources = new ArrayList<ITeiidDataSource>(teiidServer.getDataSources());
            					for( ITeiidDataSource ds : dataSources ) {
            						if( ds.getName().contains(workspaceUuid) ) {
            							teiidServer.deleteDataSource(ds.getName());
            						}
            					}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								DqpUiConstants.UTIL.log(IStatus.WARNING,
                                        e,
                                        getString("errorClearingPreviewDataSources", teiidServer.getDisplayName())); //$NON-NLS-1$
							}
            			}
            		}
                }
            }
        };
        this.clearPreviewArtifactsAction.setText(getString("clearPreviewArtifacts")); //$NON-NLS-1$
        this.clearPreviewArtifactsAction.setEnabled(true);
        
        this.createDataSourceAction = new Action() {
        	@Override
            public void run() {
                ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                
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
        
        this.createVdbDataSourceAction = new Action() {
        	@Override
            public void run() {
                ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                if (teiidServer != null && teiidServer.isConnected()) {
	                // Assume a VDB is selected
	                List<Object> selectedObjs = getSelectedObjects();
	                if (selectedObjs.size() == 1) {
	                    Object selection = selectedObjs.get(0);
	                    if (RuntimeAssistant.adapt(selection, ITeiidVdb.class) != null) {
	                        // If we have a legitimate teiid vdb then the server must be connected
	                        ITeiidVdb teiidVdb = RuntimeAssistant.adapt(selection, ITeiidVdb.class);
	                        if( teiidVdb != null ) {
			                    CreateVdbDataSourceAction action = new CreateVdbDataSourceAction(teiidVdb.getName());
			                    action.setTeiidServer(teiidServer);
			                    action.setSelection(new StructuredSelection());
			                    action.setEnabled(true);
			                    action.run();
	                        }
		                }
                    }
                }
            }
        };
        
        this.createVdbDataSourceAction.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.CREATE_VDB_DATA_SOURCE_ICON));
        this.createVdbDataSourceAction.setText("Create VDB Data Source"); //getString("createDataSourceAction.text")); //$NON-NLS-1$
        this.createVdbDataSourceAction.setToolTipText(getString("createDataSourceAction.tooltip")); //$NON-NLS-1$
        this.createVdbDataSourceAction.setEnabled(true);
        
        // the edit action is only enabled when one server is selected
        this.setDefaultServerAction = new SetDefaultServerAction();
        viewer.addSelectionChangedListener(this.setDefaultServerAction);
        
        this.importAsDynamicVdbAction = new Action() {
        	@Override
            public void run() {
                ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selectionProvider.getSelection());
                if (teiidServer != null && teiidServer.isConnected()) {
	                // Assume a VDB is selected
	                List<Object> selectedObjs = getSelectedObjects();
	                if (selectedObjs.size() == 1) {
	                    Object selection = selectedObjs.get(0);
	                    ITeiidVdb teiidVdb = RuntimeAssistant.adapt(selection, ITeiidVdb.class);
	                    if (teiidVdb != null) {
	                    	try {
								String vdbName = teiidVdb.getDeployedName();
	                    		System.out.println(" VDB =  " + vdbName + "\n");
	                        	
	                        	String manifest = teiidVdb.getManifest();
	                        	
	                        	System.out.println(" Manifest for VDB " + vdbName + "\n\n" + manifest);

//								Map<String, String> modelDDLArray = new HashMap<String, String>();
//								
//								for( String modelName : teiidVdb.getModelNames() ) {
//									if( modelName != null ) {
//										String ddl = teiidServer.getSchema(vdbName, teiidVdb.getVersion(), modelName);
//										if( ddl != null ) {
//											System.out.println(" DDL for model " + modelName + "\n\n" + ddl);
//											modelDDLArray.put(modelName, ddl);
//										}
//									}
//								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                    	
	                    }
	                }
                }
        	}
		};
		this.importAsDynamicVdbAction.setText("Import As Dynamic VDB"); //$NON-NLS-1
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, editServerAction);
        fillLocalPullDown(actionBars.getMenuManager());
        actionBars.updateActionBars();
    }

    private void fillLocalPullDown( IMenuManager menuMgr ) {
        menuMgr.removeAll();
        menuMgr.removeMenuListener(enablePreviewActionListener);
        
        // add the show preview VDBs action
        if (showPreviewVdbsAction == null) {
            showPreviewVdbsAction = new Action(getString("showPreviewVdbsMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void run() {
                    toggleShowPreviewOptions();
                }
            };
        }
        
        // add the show preview data sources action
        if (showPreviewDataSourcesAction == null) {
            showPreviewDataSourcesAction = new Action(
                                                       getString("showPreviewDataSourcesMenuItem"), SWT.TOGGLE) { //$NON-NLS-1$
                @Override
                public void run() {
                    toggleShowPreviewOptions();
                }
            };
        }

        showPreviewVdbsAction.setChecked(previewOptionContributor.isShowPreviewVdbs());
        showPreviewDataSourcesAction.setChecked(previewOptionContributor.isShowPreviewDataSources());

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
        menuMgr.add(showPreviewDataSourcesAction);
        menuMgr.add(enablePreviewAction);
        menuMgr.add(new Separator());
        menuMgr.add(clearPreviewArtifactsAction);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager manager) {
        List<Object> selectedObjs = getSelectedObjects();

        manager.removeAll();
        
        manager.add(new Separator());
        
        if (selectedObjs == null || selectedObjs.isEmpty()) {
            manager.add(refreshAction);
            manager.add(new Separator());
            // Other plug-ins can contribute there actions here
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            
            return;
        }
        
        ITeiidServer teiidServer = null;
        
        if (selectedObjs.size() == 1) {
            Object selection = selectedObjs.get(0);
            
            if (RuntimeAssistant.adapt(selection, ITeiidDataSource.class) != null) {
                // If we have a legitimate teiid data source then the server must be connected
            	manager.add(this.createDataSourceAction);
            	manager.add(new Separator());
                manager.add(this.deleteDataSourceAction);              
                manager.add(new Separator());
            }

            if (RuntimeAssistant.adapt(selection, ITeiidVdb.class) != null) {
             // If we have a legitimate teiid vdb then the server must be connected
                ITeiidVdb teiidVdb = RuntimeAssistant.adapt(selection, ITeiidVdb.class);
                this.executeVdbAction.setEnabled(teiidVdb.isActive());
                manager.add(this.executeVdbAction);
                manager.add(this.createVdbDataSourceAction);
                manager.add(this.importAsDynamicVdbAction);
                manager.add(new Separator());
                manager.add(this.undeployVdbAction);
                manager.add(new Separator());
            }
            
            // This will adapt either the TeiidResourceNode or the
            // TeiidServerContainerNode to the TeiidServer
            teiidServer = RuntimeAssistant.adapt(selection, ITeiidServer.class);
            
            if (teiidServer != null) {
                boolean teiidServerConnected = teiidServer.isConnected();
                
                if (this.setDefaultServerAction.isEnabled()) {
                    manager.add(this.setDefaultServerAction);
                }
            
                if (teiidServerConnected) {
                    manager.add(this.disconnectAction);
                }

                manager.add(new Separator());
                manager.add(this.editServerAction);
                manager.add(new Separator());

                if (teiidServerConnected && selection instanceof DataSourcesFolder) {
                    manager.add(this.createDataSourceAction);
                }
            }


            manager.add(new Separator());
            manager.add(clearPreviewArtifactsAction);
            
        } else {
            // More than 1 selected object
            
            boolean allDataSources = true;
            
            for (Object obj : selectedObjs) {
                if (RuntimeAssistant.adapt(obj, ITeiidDataSource.class) == null) {
                 // If we have legitimate teiid data sources then the server must be connected
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
                 // If we have legitimate teiid vdbs then the server must be connected
                    allVdbs = false;
                    break;
                }
            }
            if (allVdbs) {
                manager.add(this.undeployVdbAction);
            }
            manager.add(refreshAction);
            manager.add(new Separator());
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        }
    }
}

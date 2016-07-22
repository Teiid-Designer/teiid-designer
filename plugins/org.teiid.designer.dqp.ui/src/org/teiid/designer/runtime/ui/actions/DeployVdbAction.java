/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import static org.teiid.designer.runtime.ui.DqpUiConstants.PLUGIN_ID;
import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.connection.CreateVdbDataSourceAction;
import org.teiid.designer.runtime.ui.vdb.DeployVdbDialog;
import org.teiid.designer.runtime.ui.vdb.VdbAgeChecker;
import org.teiid.designer.runtime.ui.vdb.VdbDeployer;
import org.teiid.designer.runtime.ui.vdb.VdbRequiresSaveChecker;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.VdbVersionUtil;
import org.teiid.designer.vdb.XmiVdb;


/**
 * @since 8.0
 */
public class DeployVdbAction extends Action implements ISelectionListener, Comparable, ISelectionAction {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DeployVdbAction.class);

    private final Collection<IFile> selectedVDBs = new ArrayList<IFile>();
    
    Properties designerProperties;
    
    static String failedModelName = null;

    /**
     * Create a new instance
     */
    public DeployVdbAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DEPLOY_VDB));
    }
    
    /**
     * Create a new instance with given properties
     * 
     * @param properties the properties
     */
    public DeployVdbAction(Properties properties) {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DEPLOY_VDB));
        designerProperties = properties;
    }

    @Override
    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        List objs = SelectionUtilities.getSelectedObjects(selection);

        if (objs.isEmpty()) {
            return false;
        }

        for (Object obj : objs) {
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();

                if ((extension == null) || !extension.equals(ITeiidVdb.VDB_EXTENSION)) {
                    return false;
                }
            } else {
                return false;
            }
        }
    	ITeiidServer teiidServer = getServerManager().getDefaultServer();
        if (teiidServer != null) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
    	// Make sure default teiid instance is connected
    	if(!checkForConnectedServer()) return;

    	ITeiidServer teiidServer = getServerManager().getDefaultServer();
        
        for (IFile nextVDB : this.selectedVDBs) {
            boolean doDeploy = VdbRequiresSaveChecker.insureOpenVdbSaved(nextVDB);

            if( doDeploy ) {
            	doDeploy = VdbAgeChecker.doDeploy(nextVDB, teiidServer.getServerVersion());
            }
            
//            if( doDeploy ) {
//            	IStatus status = VdbVersionUtil.isVdbVersionValid(teiidServer.getServerVersion(), VdbUtil.getVdbVersion(nextVDB));
//            }
            
            if (doDeploy) {
            	boolean deploySuccess = deployVdb(teiidServer, nextVDB);

            	String vdbName = FileUtils.getNameWithoutExtension(nextVDB);
                try {
                    // make sure deployment worked before going on to the next one
                    if (! teiidServer.hasVdb(vdbName)) {
                    	deploySuccess = false;
                        break;
                    }
                } catch (Exception ex) {
                    DqpPlugin.Util.log(ex);
		    		Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
		    		String title = UTIL.getString(I18N_PREFIX + "problemDeployingVdbDataSource.title", vdbName, teiidServer); //$NON-NLS-1$
					String message = UTIL.getString(I18N_PREFIX + "problemDeployingVdbDataSource.msg", vdbName, teiidServer); //$NON-NLS-1$
					ErrorDialog.openError(shell, title, null, new Status(IStatus.ERROR, PLUGIN_ID, message, ex));
                }
                
                if( deploySuccess ) {
                	try {
						CreateVdbDataSourceAction.doCreateDataSource(vdbName, teiidServer, true);
					} catch (Exception ex) {
						Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
		                MessageDialog.openError(shell,
                                UTIL.getString("CreateVdbDataSourceAction.errorCreatingDataSourceForVDB", vdbName), ex.getMessage()); //$NON-NLS-1$
		                DqpUiConstants.UTIL.log(IStatus.ERROR, ex, UTIL.getString("CreateVdbDataSourceAction.errorCreatingDataSourceForVDB",vdbName)); //$NON-NLS-1$
					}
                }
            }
        }
    }
    
    /**
     * Ask the user to select the vdb and deploy it
     */
    public void queryUserAndRun() {
    	// Make sure default teiid instance is connected
    	if(!checkForConnectedServer()) return;
    	
        ITeiidServer teiidServer = getServerManager().getDefaultServer();
        
        DeployVdbDialog dialog = new DeployVdbDialog(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), designerProperties);

		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			IFile vdbFile = dialog.getSelectedVdb();
			boolean doCreateDS = dialog.doCreateVdbDataSource();
			String jndiName = dialog.getVdbDataSourceJndiName();
			if (vdbFile != null) {
		        boolean doDeploy = VdbRequiresSaveChecker.insureOpenVdbSaved(vdbFile);
		        if( doDeploy  ) {
		        	deployVdb(teiidServer, vdbFile, true);
		        }
		        
			    String vdbName = FileUtils.getNameWithoutExtension(vdbFile);
		        try {
		            if( teiidServer.hasVdb(vdbName) && doCreateDS  ) {
		                createVdbDataSource(vdbFile, jndiName, jndiName);
		            }
		        } catch (Exception ex) {
		            DqpPlugin.Util.log(ex);
		    		Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
		    		String title = UTIL.getString(I18N_PREFIX + "problemDeployingVdbDataSource.title", vdbName, teiidServer); //$NON-NLS-1$
					String message = UTIL.getString(I18N_PREFIX + "problemDeployingVdbDataSource.msg", vdbName, teiidServer); //$NON-NLS-1$
					ErrorDialog.openError(shell, title, null, new Status(IStatus.ERROR, PLUGIN_ID, message, ex));
		        }
			}
		}


    }
    
    

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        this.selectedVDBs.clear();
        boolean enable = isApplicable(selection);

        if (isEnabled() != enable) {
            setEnabled(enable);
        }

        if (isEnabled()) {
            List objs = SelectionUtilities.getSelectedObjects(selection);
            this.selectedVDBs.addAll(objs);
        }
    }

    /*
     * Check that the default teiid instance is connected.  Show dialog if it is not.
     * @return 'true' if default teiid instance is connected, 'false' if not.
     */
    private boolean checkForConnectedServer() {
        ITeiidServer teiidServer = getServerManager().getDefaultServer();
        if(teiidServer==null || !teiidServer.isConnected()) {
    		Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
    		String title = UTIL.getString("ActionRequiresServer.title"); //$NON-NLS-1$
    		String msg = UTIL.getString("ActionRequiresServer.msg"); //$NON-NLS-1$
        	MessageDialog.openInformation(shell,title,msg);
        	return false;
        }
        return true;
    }

    private static ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    /**
     * @param teiidServer the teiidServer where the VDB is being deployed to (can be <code>null</code>)
     * @param vdbOrVdbFile the VDB being deployed
     * @return deployment status
     */
    public static boolean deployVdb( ITeiidServer teiidServer,
                                 final Object vdbOrVdbFile ) {
    	return deployVdb(teiidServer, vdbOrVdbFile, shouldAutoCreateDataSource());
    }
    
	/**
	 * Deploy the given vdb to the given Teiid Instance
	 * 
	 * @param teiidServer the Teiid Instance
	 * @param vdbOrVdbFile the VDB
	 * @param doCreateDataSource 'true' to create corresponding datasource, 'false' if not.
	 * @return deploy status
	 */
	public static boolean deployVdb(ITeiidServer teiidServer, final Object vdbOrVdbFile, final boolean doCreateDataSource) {
		Shell shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
		
		try {
			if (!(vdbOrVdbFile instanceof IFile) && !(vdbOrVdbFile instanceof Vdb)) {
				throw new IllegalArgumentException(UTIL.getString(I18N_PREFIX + "selectionIsNotAVdb")); //$NON-NLS-1$
			}

			// make sure there is a Teiid connection
			if (! teiidServer.isConnected()) {
				return false;
			}

			Vdb vdb = ((vdbOrVdbFile instanceof IFile) ? new XmiVdb(
					(IFile) vdbOrVdbFile) : (Vdb) vdbOrVdbFile);

			if(!vdb.isSynchronized()) {
	    		String title = UTIL.getString("VdbNotSyncdDialog.title"); //$NON-NLS-1$
	    		String msg = UTIL.getString("VdbNotSyncdDialog.msg"); //$NON-NLS-1$
	        	if (!MessageDialog.openQuestion(shell,title,msg)) return false;
	     	}

			if( !VdbAgeChecker.doDeploy(vdb.getSourceFile(), teiidServer.getServerVersion()) ) return false;

			final VdbDeployer deployer = new VdbDeployer(shell, vdb, teiidServer, doCreateDataSource);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

			failedModelName = null;

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				/**
				 * {@inheritDoc}
				 * 
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						failedModelName = deployer.deploy(monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			};

			// deploy using progress monitor (UI is blocked)
			dialog.run(true, false, runnable);

			// process results
			VdbDeployer.DeployStatus status = deployer.getStatus();

			if (status.isError()) {
				String message = null;

				if (VdbDeployer.DeployStatus.CREATE_DATA_SOURCE_FAILED == status) {
					message = UTIL.getString(I18N_PREFIX + "createDataSourceFailed", deployer.getVdbName(), failedModelName); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.DEPLOY_VDB_FAILED == status) {
					message = UTIL.getString(I18N_PREFIX + "vdbFailedToDeploy", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.TRANSLATOR_PROBLEM == status) {
					message = UTIL.getString(I18N_PREFIX + "translatorDoesNotExistOnServer", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.SOURCE_CONNECTION_INFO_PROBLEM == status) {
					message = UTIL.getString(I18N_PREFIX + "sourceMissingConnectionInfo", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.EXCEPTION == status) {
					throw deployer.getException(); // let catch block below
													// handle
				} else {
					// unexpected
					message = UTIL.getString(I18N_PREFIX + "unknownDeployError", deployer.getVdbName(), status); //$NON-NLS-1$
				}

				// show user the error
				MessageDialog.openError(shell,UTIL.getString(I18N_PREFIX+ "vdbNotDeployedTitle"), message); //$NON-NLS-1$
				return false;
			} else if (status.isDeployed()) {
				if (teiidServer.wasVdbRemoved(deployer.getVdbName())) {
					StringBuilder message = new StringBuilder(UTIL.getString(
							I18N_PREFIX + "vdbNotActiveMessage", //$NON-NLS-1$
							vdb.getName()));

					for (String error : teiidServer.retrieveVdbValidityErrors(deployer.getVdbName())) {
						message.append(UTIL.getString(I18N_PREFIX + "notActiveErrorMessage", error)); //$NON-NLS-1$
					}

					MessageDialog
							.openWarning(shell, UTIL.getString(I18N_PREFIX + "vdbNotActiveTitle"), message.toString()); //$NON-NLS-1$
					return true;
				}
			} else {
				return false;
			}
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException) e).getCause();
			}

			String vdbName = null;

			if (vdbOrVdbFile instanceof IFile) {
				vdbName = ((IFile) vdbOrVdbFile).getName();
			} else if (vdbOrVdbFile instanceof Vdb) {
				vdbName = ((Vdb) vdbOrVdbFile).getName();
			} else {
				vdbName = UTIL.getString(I18N_PREFIX + "selectionIsNotAVdb"); //$NON-NLS-1$
			}

			String message = UTIL.getString(I18N_PREFIX + "problemDeployingVdbToServer", vdbName, teiidServer); //$NON-NLS-1$
			UTIL.log(e);
			ErrorDialog.openError(shell, message, null, new Status(IStatus.ERROR, PLUGIN_ID, message, e));
			
			return false;
		}
		
		return true;
	}

    /**
     * @return <code>true</code> if data source should be auto-created based on the current preference value
     */
    static boolean shouldAutoCreateDataSource() {
        return DqpPlugin.getInstance()
                        .getPreferences()
                        .getBoolean(PreferenceConstants.AUTO_CREATE_DATA_SOURCE,
                                    PreferenceConstants.AUTO_CREATE_DATA_SOURCE_DEFAULT);
    }
    
    private void createVdbDataSource(Object vdbOrVdbFile, String displayName, String jndiName) throws Exception {
    	Vdb vdb = ((vdbOrVdbFile instanceof IFile) ? new XmiVdb((IFile) vdbOrVdbFile) : (Vdb) vdbOrVdbFile);
    	ITeiidServer teiidServer = getServerManager().getDefaultServer();
	    String vdbName = vdb.getName();
    	teiidServer.createVdbDataSource(vdbName, displayName, jndiName);
    }

}

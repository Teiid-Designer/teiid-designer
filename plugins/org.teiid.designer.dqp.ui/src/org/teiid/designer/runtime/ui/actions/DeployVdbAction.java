/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.actions;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

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
import org.teiid.adminapi.VDB;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.vdb.DeployVdbDialog;
import org.teiid.designer.runtime.ui.vdb.VdbDeployer;
import org.teiid.designer.runtime.ui.vdb.VdbRequiresSaveChecker;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class DeployVdbAction extends Action implements ISelectionListener, Comparable, ISelectionAction {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DeployVdbAction.class);

    private final Collection<IFile> selectedVDBs = new ArrayList<IFile>();
    
    Properties designerProperties;

    public DeployVdbAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.DEPLOY_VDB));
    }
    
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
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
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

                if ((extension == null) || !extension.equals(Vdb.FILE_EXTENSION_NO_DOT)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        Server server = getServerManager().getDefaultServer();

        for (IFile nextVDB : this.selectedVDBs) {
            boolean doDeploy = VdbRequiresSaveChecker.insureOpenVdbSaved(nextVDB);
            if (doDeploy) {
                VDB deployedVdb = deployVdb(server, nextVDB);

                // make sure deployment worked before going on to the next one
                if (deployedVdb == null) {
                    break;
                }
            }
        }
    }
    
    public void queryUserAndRun() {
        Server server = getServerManager().getDefaultServer();
        
        DeployVdbDialog dialog = new DeployVdbDialog(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), designerProperties);

		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			IFile vdb = dialog.getSelectedVdb();
			boolean doCreateDS = dialog.doCreateVdbDataSource();
			String jndiName = dialog.getVdbDataSourceJndiName();
			if (vdb != null) {
		        boolean doDeploy = VdbRequiresSaveChecker.insureOpenVdbSaved(vdb);
		        VDB deployedVDB = null;
		        if( doDeploy  ) {
		        	deployedVDB = deployVdb(server, vdb, true);
		        }
		        
		        if( deployedVDB != null && doCreateDS  ) {
		        	createVdbDataSource(vdb, jndiName, jndiName);
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

    private static ServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    /**
     * @param server the server where the VDB is being deployed to (can be <code>null</code>)
     * @param vdbOrVdbFile the VDB being deployed
     * @return the Teiid deployed VDB or <code>null</code> if deployment was canceled
     */
    public static VDB deployVdb( Server server,
                                 final Object vdbOrVdbFile ) {
    	return deployVdb(server, vdbOrVdbFile, shouldAutoCreateDataSource());

    }
    
	public static VDB deployVdb(Server server, final Object vdbOrVdbFile, final boolean doCreateDataSource) {
		Shell shell = DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
		VDB[] result = new VDB[1];

		try {
			if (!(vdbOrVdbFile instanceof IFile) && !(vdbOrVdbFile instanceof Vdb)) {
				throw new IllegalArgumentException(UTIL.getString(I18N_PREFIX + "selectionIsNotAVdb")); //$NON-NLS-1$
			}

			// make sure there is a Teiid connection
			if (!RuntimeAssistant.ensureServerConnection(shell, UTIL.getString(I18N_PREFIX + "noTeiidInstanceMsg"))) { //$NON-NLS-1$
				return null;
			}

			Vdb vdb = ((vdbOrVdbFile instanceof IFile) ? new Vdb(
					(IFile) vdbOrVdbFile, null) : (Vdb) vdbOrVdbFile);
			final VdbDeployer deployer = new VdbDeployer(shell, vdb, server.getAdmin(), doCreateDataSource);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);

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
						deployer.deploy(monitor);
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
					message = UTIL.getString(I18N_PREFIX + "createDataSourceFailed", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.DEPLOY_VDB_FAILED == status) {
					message = UTIL.getString(I18N_PREFIX + "vdbFailedToDeploy", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.TRANSLATOR_PROBLEM == status) {
					message = UTIL.getString(I18N_PREFIX + "translatorDoesNotExistOnServer", deployer.getVdbName()); //$NON-NLS-1$
				} else if (VdbDeployer.DeployStatus.EXCEPTION == status) {
					throw deployer.getException(); // let catch block below
													// handle
				} else {
					// unexpected
					message = UTIL.getString(I18N_PREFIX + "unknownDeployError", deployer.getVdbName(), status); //$NON-NLS-1$
				}

				// show user the error
				MessageDialog.openError(shell,UTIL.getString(I18N_PREFIX+ "vdbNotDeployedTitle"), message); //$NON-NLS-1$
			} else if (status.isDeployed()) {
				VDB deployedVdb = deployer.getDeployedVdb();
				result[0] = deployedVdb;

				if (deployedVdb.getStatus().equals(VDB.Status.INACTIVE)) {
					StringBuilder message = new StringBuilder(UTIL.getString(
							I18N_PREFIX + "vdbNotActiveMessage", //$NON-NLS-1$
							deployedVdb.getName()));

					for (String error : deployedVdb.getValidityErrors()) {
						message.append(UTIL.getString(I18N_PREFIX + "notActiveErrorMessage", error)); //$NON-NLS-1$
					}

					MessageDialog
							.openWarning(shell, UTIL.getString(I18N_PREFIX + "vdbNotActiveTitle"), message.toString()); //$NON-NLS-1$
				}
			}
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException) e).getCause();
			}

			String vdbName = null;

			if (vdbOrVdbFile instanceof IFile) {
				vdbName = ((IFile) vdbOrVdbFile).getName();
			} else if (vdbOrVdbFile instanceof Vdb) {
				vdbName = ((Vdb) vdbOrVdbFile).getFile().getName();
			} else {
				vdbName = UTIL.getString(I18N_PREFIX + "selectionIsNotAVdb"); //$NON-NLS-1$
			}

			String message = UTIL.getString(I18N_PREFIX + "problemDeployingVdbToServer", vdbName, server); //$NON-NLS-1$
			UTIL.log(e);
			ErrorDialog.openError(shell, message, null, new Status(IStatus.ERROR, PLUGIN_ID, message, e));
		}

		return result[0];
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
    
    private static void createVdbDataSource(Object vdbOrVdbFile, String displayName, String jndiName) {
    	Vdb vdb = ((vdbOrVdbFile instanceof IFile) ? new Vdb((IFile) vdbOrVdbFile, null) : (Vdb) vdbOrVdbFile);
    	Server server = getServerManager().getDefaultServer();
    	server.createVdbDataSource(vdb.getFile().getName(), displayName, jndiName);
    }

}

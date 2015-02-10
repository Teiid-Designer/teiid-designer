/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.vdb;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.datatools.ui.dialogs.TeiidCPWizardDialog;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.actions.DeployVdbAction;
import org.teiid.designer.runtime.ui.actions.OpenScrapbookEditorAction;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * @since 8.0
 */
public class ExecuteVdbWorker implements VdbConstants {
	protected static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExecuteVdbWorker.class);
	
    static String getString( String key ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + key);
    }

    static String getString( final String key, final Object param ) {
    	return DqpUiConstants.UTIL.getString(I18N_PREFIX + key, param);
	}
    
    static String getString( final String key, final Object param, final Object param2 ) {
    	return DqpUiConstants.UTIL.getString(I18N_PREFIX + key, param, param2);
	}
    
	/**
	 * 
	 * @return is vdb executable or not
	 */
	public boolean isExecutableVdb(Object vdb) {
		if (vdb instanceof IFile) {
			String extension = ((IFile) vdb).getFileExtension();
			if (extension != null && extension.equals("vdb")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public void run(final IFile selectedVdb) {
		if (!VdbRequiresSaveChecker.insureOpenVdbSaved(selectedVdb)) {
			return;
		}

		UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {

			@Override
			public void run() {
				internalRun(selectedVdb);
			}
		});

	}

	void internalRun(final IFile selectedVdb) {
		ITeiidServer teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
		boolean deployed = false;
		
		try {
			if (teiidServer != null) {
				IStatus connectStatus = teiidServer.ping();
				if (connectStatus.isOK() )  {
					if(  !VdbAgeChecker.doDeploy(selectedVdb, teiidServer.getServerVersion())) return;
					// Deploy the VDB
                    deployed = DeployVdbAction.deployVdb(teiidServer, selectedVdb);
                    
				    String vdbName = selectedVdb.getFullPath().removeFileExtension().lastSegment();
				    if( vdbName.indexOf('.') > -1 ) {
				    	vdbName = new Path(vdbName).removeFileExtension().toString();
				    }
                    if (teiidServer.isVdbActive(vdbName)) {
                    	if( deployed ) {
                    		executeVdb(DqpPlugin.getInstance().getServerManager().getDefaultServer(), vdbName);
                    	}
                    } else if (teiidServer.isVdbLoading(vdbName)) {
                        StringBuilder message = new StringBuilder(getString("vdbLoadingMessage", selectedVdb.getName())); //$NON-NLS-1$
                        MessageDialog.openWarning(getShell(), getString("vdbLoadingTitle"), //$NON-NLS-1$
                                                  message.toString());
                    } else if( deployed ) {
                        StringBuilder message = new StringBuilder(getString("vdbNotActiveMessage", selectedVdb.getName())); //$NON-NLS-1$
                        if (teiidServer.hasVdb(vdbName)) {
                            for (String error : teiidServer.retrieveVdbValidityErrors(vdbName)) {
                                message.append("\nERROR:\t").append(error); //$NON-NLS-1$
                            }
                        }
                        MessageDialog.openWarning(getShell(), getString("vdbNotActiveTitle"), //$NON-NLS-1$
                                                  message.toString());
                    }
				} else {
					MessageDialog
							.openWarning(
									getShell(),getString("noTeiidServerConnection.title"), //$NON-NLS-1$
									getString("noTeiidServerConnection.message", connectStatus.getMessage())); //$NON-NLS-1$
				}
			} else {
				MessageDialog
						.openWarning(
								getShell(),
								getString("noTeiidInstance.title"), //$NON-NLS-1$
								getString("noTeiidInstance.message")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("vdbNotDeployedError", //$NON-NLS-1$
							selectedVdb.getName()));
		}
	}
	

	public void executeVdb(ITeiidServer teiidServer, String vdbName)
			throws Exception {
		processForDTP(teiidServer, vdbName);
	}

    /**
     * Opens an error dialog if necessary.  Takes care of
     * complex rules necessary for making the error dialog look nice.
     */
    private void openError(String genericTitle, IStatus status) {
        if (status == null) {
            return;
        }

        int codes = IStatus.ERROR | IStatus.WARNING;

        //simple case: one error, not a multistatus
        if (!status.isMultiStatus()) {
            ErrorDialog.openError(getShell(), genericTitle, null, status, codes);
            return;
        }

        //one error, single child of multistatus
        IStatus[] children = status.getChildren();
        if (children.length == 1) {
            ErrorDialog.openError(getShell(), status.getMessage(), null, children[0], codes);
            return;
        }

        //several problems
        ErrorDialog.openError(getShell(), genericTitle, null, status, codes);
    }

	public void processForDTP(ITeiidServer teiidServer, String vdbName)
			throws Exception {

		String driverPath = teiidServer.getAdminDriverPath();
		ITeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(vdbName, teiidServer.getTeiidJdbcInfo());

		String connectionURL = jdbcInfo.getUrl();

		String profileName = getString("profileName", vdbName, teiidServer.getHost()); //$NON-NLS-1$

		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(profileName);
		if (profile == null) {
			// If username or password is not supplied we bring up the New
			// Connection Profile dialog
			if (null == jdbcInfo.getUsername()
					|| jdbcInfo.getUsername().isEmpty()
					|| null == jdbcInfo.getPassword()
					|| jdbcInfo.getPassword().isEmpty()) {
				Properties cpProps = ConnectivityUtil
						.createVDBTeiidProfileProperties(teiidServer.getServerVersion(), driverPath,connectionURL, jdbcInfo.getUsername(),jdbcInfo.getPassword(), vdbName, profileName);
				NewTeiidFilteredCPWizard wiz = new NewTeiidFilteredCPWizard(profileName, null);
				TeiidCPWizardDialog wizardDialog = new TeiidCPWizardDialog(Display.getCurrent().getActiveShell(), wiz);
				wizardDialog.setProperties(cpProps);
				wizardDialog.setBlockOnOpen(true);
				if (wizardDialog.open() != Window.OK) {
					return;
				}
				// if we have all the info we create it w/o user interaction
			} else {
				profile = ConnectivityUtil.createVDBTeiidProfile(teiidServer.getServerVersion(),
				                                                 driverPath,
				                                                 connectionURL,
				                                                 jdbcInfo.getUsername(),
				                                                 jdbcInfo.getPassword(),
				                                                 vdbName,
				                                                 profileName);
			}
		}

		IStatus connectionStatus = Status.OK_STATUS;
		if( profile != null) {
		    connectionStatus = profile.connectWithoutJob();
		}
		try {
			PlatformUI.getWorkbench().showPerspective(DTP_PERSPECTIVE,
					DqpUiPlugin.getDefault().getCurrentWorkbenchWindow());
		} catch (Throwable e) {
			DqpUiConstants.UTIL.log(e);
		}
		
		// Now open the SQL Scrapbook?
		if( connectionStatus.getSeverity() < IStatus.ERROR ) {
			OpenScrapbookEditorAction sbAction = new OpenScrapbookEditorAction();
			sbAction.run(profile, vdbName);
		} else {
		    openError(getString("vdbConnectionError.title"), //$NON-NLS-1$
		                      connectionStatus);
		}
	}
}

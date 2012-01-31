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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.VDB;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.datatools.ui.dialogs.TeiidCPWizardDialog;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.ui.actions.DeployVdbAction;
import org.teiid.designer.runtime.ui.actions.OpenScrapbookEditorAction;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

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

		/*
		 * Server server = DqpPlugin.getInstance().getServerManager()
		 * .getDefaultServer();
		 * 
		 * if (server != null) { try { server. ExecutionAdmin admin =
		 * server.getAdmin(); if (null ==
		 * server.getAdmin().getVdb(selectedVDB.getName()))
		 * 
		 * MessageDialog.openInformation(Display.getCurrent() .getActiveShell(),
		 * "VDB Deployed", "VDB: " + selectedVDB.getName() +
		 * " is Deployed on server: " + server.getUrl()); } catch (Exception e)
		 * { DqpUiConstants.UTIL.log(IStatus.ERROR, e, DqpPlugin.Util
		 * .getString( "DeployVdbAction.problemDeployingVdbToServer",
		 * //$NON-NLS-1$ selectedVDB.getName(), server.getUrl())); } }
		 */

		if (!VdbRequiresSaveChecker.insureOpenVdbSaved(selectedVdb)) {
			return;
		}

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

			@Override
			public void run() {
				internalRun(selectedVdb);
			}
		});

	}

	void internalRun(final IFile selectedVdb) {
		Server server = DqpPlugin.getInstance().getServerManager().getDefaultServer();
		VDB deployedVDB = null;

		try {
			if (server != null) {
				IStatus connectStatus = server.ping();
				if (connectStatus.isOK()) {
					ExecutionAdmin admin = server.getAdmin();
					if (admin != null) {
						deployedVDB = admin.getVdb(selectedVdb.getName());
						if (deployedVDB == null) {
							deployedVDB = DeployVdbAction.deployVdb(server,
									selectedVdb);
						}

						if (deployedVDB != null && deployedVDB.getStatus().equals(VDB.Status.ACTIVE)) {
							executeVdb(DqpPlugin.getInstance().getServerManager().getDefaultServer(),
									selectedVdb.getFullPath().removeFileExtension().lastSegment());
						} else {
							StringBuilder message = new StringBuilder(
									getString("vdbNotActiveMessage", selectedVdb.getName())); //$NON-NLS-1$
							if (null != deployedVDB) {
								for (String error : deployedVDB.getValidityErrors()) {
									message.append("\nERROR:\t").append(error); //$NON-NLS-1$
								}
							}
							MessageDialog
									.openWarning(getShell(),getString("vdbNotActiveTitle"), //$NON-NLS-1$
											message.toString());
						}
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
								DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
								getString("noTeiidInstance.title"), //$NON-NLS-1$
								getString("noTeiidInstance.message")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("vdbNotDeployedError", //$NON-NLS-1$
							selectedVdb.getName()));
		}
	}

	public void executeVdb(Server server, String vdbName)
			throws CoreException {
		processForDTP(server, vdbName);
	}

	public void processForDTP(Server server, String vdbName)
			throws CoreException {

		String driverPath = Admin.class.getProtectionDomain().getCodeSource().getLocation().getFile();

		TeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(vdbName,server.getTeiidJdbcInfo());

		String connectionURL = jdbcInfo.getUrl();

		String profileName = getString("profileName", vdbName, server.getHost()); //$NON-NLS-1$

		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(profileName);
		if (profile == null) {
			// If username or password is not supplied we bring up the New
			// Connection Profile dialog
			if (null == jdbcInfo.getUsername()
					|| jdbcInfo.getUsername().isEmpty()
					|| null == jdbcInfo.getPassword()
					|| jdbcInfo.getPassword().isEmpty()) {
				Properties cpProps = ConnectivityUtil
						.createVDBTeiidProfileProperties(driverPath,connectionURL, jdbcInfo.getUsername(),jdbcInfo.getPassword(), vdbName, profileName);
				NewTeiidFilteredCPWizard wiz = new NewTeiidFilteredCPWizard(profileName, null);
				TeiidCPWizardDialog wizardDialog = new TeiidCPWizardDialog(Display.getCurrent().getActiveShell(), wiz);
				wizardDialog.setProperties(cpProps);
				wizardDialog.setBlockOnOpen(true);
				if (wizardDialog.open() == Window.OK) {
					profile = wiz.getParentProfile();
					try {
						PlatformUI.getWorkbench().showPerspective(DTP_PERSPECTIVE,DqpUiPlugin.getDefault().getCurrentWorkbenchWindow());
					} catch (Throwable e) {
						DqpUiConstants.UTIL.log(e);
					}
				} else {
					return;
				}
				// if we have all the info we create it w/o user interaction
			} else {
				profile = ConnectivityUtil.createVDBTeiidProfile(driverPath,
						connectionURL, jdbcInfo.getUsername(),
						jdbcInfo.getPassword(), vdbName, profileName);
			}
		}
		IStatus connectionStatus = profile.connectWithoutJob();
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
		}
	}
}

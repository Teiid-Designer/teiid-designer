/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;

/**
 * 
 */
public class ConnectivityUtil {

	public static Properties createDriverProps(String jarList,
			String driverURL,
			String username, String password, String vdbName) {
		Properties baseProperties = new Properties();
		baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
				jarList);
		baseProperties.setProperty(
				IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID,
				"org.teiid.jdbc.TeiidDriver"); //$NON-NLS-1$
		baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID,
				driverURL);
		baseProperties.setProperty(
				IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
		baseProperties.setProperty(
				IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password);
		baseProperties
				.setProperty(
				IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID,
				"Teiid.org"); //$NON-NLS-1$
		baseProperties.setProperty(
				IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, "7.0"); //$NON-NLS-1$
		baseProperties.setProperty(
				IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, vdbName);
		baseProperties.setProperty(
				IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, String
						.valueOf(true));
		baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
				jarList);
		return baseProperties;
	}

	public static IConnectionProfile createTransientTeiidProfile(
			String driverPath, String connectionURL,
			String username, String password, String vdbName)
			throws CoreException {
		ProfileManager pm = ProfileManager.getInstance();

		try {
			return pm.createTransientProfile(
					"org.teiid.datatools.connectivity.connectionProfile",
					createDriverProps(
					driverPath, connectionURL, username, password, vdbName));
		} catch (ConnectionProfileException e) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
					"error getting profile", e);
			throw new CoreException(status);
		}

	}

}

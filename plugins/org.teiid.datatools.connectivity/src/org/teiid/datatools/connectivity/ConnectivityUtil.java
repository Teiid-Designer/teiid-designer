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
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.IDriverMgmtConstants;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
import org.eclipse.datatools.connectivity.drivers.PropertySetImpl;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;

/**
 * 
 */
public class ConnectivityUtil {

    public static final String TEIID_PROFILE_PROVIDER_ID = "org.teiid.datatools.connectivity.connectionProfile"; //$NON-NLS-1$
    public static final String DRIVER_DEFINITION_ID_KEY = "org.eclipse.datatools.connectivity.driverDefinitionID"; //$NON-NLS-1$
    public static final String TEIID_DRIVER_DEFINITION_ID = "DriverDefn.org.teiid.datatools.connectivity.driver.serverDriverTemplate.Teiid Server JDBC Driver"; //$NON-NLS-1$

    public static final String TEIID_DRIVER_NAME = "org.teiid.jdbc.TeiidDriver"; //$NON-NLS-1$
    public static final String TEIID_DATABASE_VENDOR_NAME = "Teiid"; //$NON-NLS-1$

    public static final String TEIID_PREVIEW_DRIVER_DEFINITION_ID = "DriverDefn.org.teiid.datatools.connectivity.driver.serverDriverTemplate.Teiid Preview Driver"; //$NON-NLS-1$
    public static final String TEIID_PREVIEW_DRIVER_NAME = "Teiid Preview Driver"; //$NON-NLS-1$
    public static final String TEIID_PREVIEW_DRIVER_DEFN_TYPE = "org.teiid.datatools.connectivity.driver.serverDriverTemplate"; //$NON-NLS-1$
	public static final String TEIID_DATABASE_VERSION = "7.1";

    public static Properties createDriverProps( String jarList,
                                                String driverURL,
                                                String username,
                                                String password,
                                                String vdbName ) {
        Properties baseProperties = new Properties();
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, TEIID_DRIVER_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, driverURL);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID, TEIID_DATABASE_VENDOR_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, TEIID_DATABASE_VERSION); //$NON-NLS-1$
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, vdbName);
        baseProperties.setProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, String.valueOf(true));
        // baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
        // jarList);
        baseProperties.setProperty(DRIVER_DEFINITION_ID_KEY, TEIID_PREVIEW_DRIVER_DEFINITION_ID);

        return baseProperties;
    }

    public static IConnectionProfile createTransientTeiidProfile( String driverPath,
                                                                  String connectionURL,
                                                                  String username,
                                                                  String password,
                                                                  String vdbName ) throws CoreException {
        ProfileManager pm = ProfileManager.getInstance();

        try {
            DriverInstance mDriver = DriverManager.getInstance().getDriverInstanceByID(TEIID_PREVIEW_DRIVER_DEFINITION_ID);
            if (mDriver == null) {
                createTeiidPreviewDriverInstance(driverPath, connectionURL, username, password);
            }

            return pm.createTransientProfile(TEIID_PROFILE_PROVIDER_ID, createDriverProps(driverPath,
                                                                                          connectionURL,
                                                                                          username,
                                                                                          password,
                                                                                          vdbName));
        } catch (ConnectionProfileException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "error getting profile", e); //$NON-NLS-1$
            throw new CoreException(status);
        }

    }

    private static void createTeiidPreviewDriverInstance( String jarList,
                                                          String driverURL,
                                                          String username,
                                                          String password ) {
        IPropertySet pset = new PropertySetImpl(TEIID_PREVIEW_DRIVER_NAME, TEIID_PREVIEW_DRIVER_DEFINITION_ID);
        Properties baseProperties = new Properties();
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, TEIID_DRIVER_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, driverURL);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID, TEIID_DATABASE_VENDOR_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, TEIID_DATABASE_VERSION);
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList);
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_TYPE, TEIID_PREVIEW_DRIVER_DEFN_TYPE);
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_CLASS, TEIID_DRIVER_NAME);

        pset.setBaseProperties(baseProperties);

        DriverManager.getInstance().addDriverInstance(pset);

    }

    public static IConnectionProfile createVDBTeiidProfile( String driverPath,
                                                            String connectionURL,
                                                            String username,
                                                            String password,
                                                            String vdbName ) throws CoreException {
        ProfileManager pm = ProfileManager.getInstance();

        String profileName = vdbName + " - Teiid Connection"; //$NON-NLS-1$

        try {
            DriverInstance mDriver = DriverManager.getInstance().getDriverInstanceByID(TEIID_PREVIEW_DRIVER_DEFINITION_ID);
            if (mDriver == null) {
                createTeiidPreviewDriverInstance(driverPath, connectionURL, username, password);
            }
            IConnectionProfile existingCP = pm.getProfileByName(profileName);
            if (existingCP != null) {
                return existingCP;
            }

            return pm.createProfile(profileName, "", //$NON-NLS-1$
                                    TEIID_PROFILE_PROVIDER_ID,
                                    createDriverProps(driverPath, connectionURL, username, password, vdbName));
        } catch (ConnectionProfileException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "error getting profile", e); //$NON-NLS-1$
            throw new CoreException(status);
        }

    }
}

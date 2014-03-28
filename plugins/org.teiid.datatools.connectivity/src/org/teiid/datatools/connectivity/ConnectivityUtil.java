/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity;

import java.sql.Driver;
import java.util.Properties;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.teiid.datatools.connectivity.security.impl.EquinoxSecureStorageProvider;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * 
 *
 * @since 8.0
 */
public class ConnectivityUtil {

    public static final String TEIID_PROFILE_PROVIDER_ID = "org.teiid.datatools.connectivity.connectionProfile"; //$NON-NLS-1$
    public static final String DRIVER_DEFINITION_ID_KEY = "org.eclipse.datatools.connectivity.driverDefinitionID"; //$NON-NLS-1$
    public static final String DB_CATEGORY_TEIID_ID = "org.eclipse.datatools.connectivity.db.category.teiid"; //$NON-NLS-1$

    public static final String TEIID_DRIVER_NAME = "org.teiid.jdbc.TeiidDriver"; //$NON-NLS-1$
    public static final String TEIID_DATABASE_VENDOR_NAME = "Teiid"; //$NON-NLS-1$

    private static final String TEIID_DRIVER_ID_SKELETON = "DriverDefn.org.teiid.MAJOR.MINOR.driver.serverDriverTemplate.Teiid Server JDBC Driver MAJOR.MINOR Default"; //$NON-NLS-1$

    /* Property keys for the ad-hoc teiid drivers created if no other suitable teiid driver can be found */
    public static final String TEIID_ADHOC_DRIVER_ID_SKELETON = "DriverDefn.org.teiid.MAJOR.MINOR.driver.serverDriverTemplate.Ad-hoc Teiid Server JDBC Driver MAJOR.MINOR Default"; //$NON-NLS-1$
    public static final String TEIID_ADHOC_DRIVER_DEFAULT_NAME = "Ad-hoc Teiid Server JDBC Driver"; //$NON-NLS-1$
    public static final String TEIID_ADHOC_DRIVER_DEFN_TYPE = "org.teiid.datatools.connectivity.driver.serverDriverTemplate"; //$NON-NLS-1$

    /**
     * Base key for the secure storage node used for holding passwords
     */
    public static final String SECURE_STORAGE_BASEKEY = Activator.PLUGIN_ID.replace('.', IPath.SEPARATOR);
    
    /**
     * Secure storage sub-node for the Admin password property
     */
    public static final String ADMIN_PASSWORD = "admin_password"; //$NON-NLS-1$
    
    /**
     * Secure storage sub-node for the JDBC password property
     */
    public static final String JDBC_PASSWORD = "jdbc_password"; //$NON-NLS-1$

    /**
     * Tries to find the most appropriate driver definition id for the given teiid version.
     * <p>
     * Since teiid driver definition's are version specific, it attempts to find the driver
     * definition based upon the major and minor components of the version.
     * <p>
     * Failing that, it iterates through the existing driver definitions and attempts to
     * determine the most appropriate driver based upon the actual version of the
     * driver definition.
     *
     * @param serverVersion
     * @return driver definition id or null
     */
    private static String getTeiidDriverDefinitionId(ITeiidServerVersion serverVersion) {
        String driverDefnId = TEIID_DRIVER_ID_SKELETON
                                                .replaceAll("MAJOR", serverVersion.getMajor()) //$NON-NLS-1$
                                                .replaceAll("MINOR", serverVersion.getMinor()); //$NON-NLS-1$
        DriverInstance driverInstance = DriverManager.getInstance().getDriverInstanceByID(driverDefnId);
        if (driverInstance != null)
            return driverDefnId; // exact match!!

        /* Need to try harder since no teiid version directly matches a driver */
        for (DriverInstance driver : DriverManager.getInstance().getAllDriverInstances()) {
            String driverClass = driver.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
            if (! TEIID_DRIVER_NAME.equals(driverClass))
                continue;

            String version = driver.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID);
            ITeiidServerVersion driverTeiidVersion = new TeiidServerVersion(version);
            if (! driverTeiidVersion.getMajor().equals(serverVersion.getMajor()))
                continue; // Major versions must match for the driver to be usable

            if (! driverTeiidVersion.isLessThan(serverVersion))
                continue; // eg. cannot use an 8.3 driver for an 8.4 teiid

            /*
             * TODO
             * Can end up with the possibility of using an 8.4 driver for an 8.2 teiid
             * when the 8.3 may be more appropriate but cross that bridge if it ever
             * comes up.
             */
            return driver.getId();
        }

        return null;
    }

    public static Properties createDriverProps(ITeiidServerVersion serverVersion, 
                                                String jarList,
                                                String driverURL,
                                                String username,
                                                String vdbName ) {
        Properties baseProperties = new Properties();
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, TEIID_DRIVER_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, driverURL);
        if(null != username) { 
        	baseProperties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
        }
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID, TEIID_DATABASE_VENDOR_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, serverVersion.toString());
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, vdbName);
        baseProperties.setProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID, String.valueOf(true));
        // baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
        // jarList);
        baseProperties.setProperty(DRIVER_DEFINITION_ID_KEY, getTeiidDriverDefinitionId(serverVersion));

        return baseProperties;
    }

    public static IConnectionProfile createTransientTeiidProfile( ITeiidServerVersion serverVersion,
                                                                  String driverPath,
                                                                  String connectionURL,
                                                                  String username,
                                                                  String password,
                                                                  String vdbName ) throws CoreException {
        ProfileManager pm = ProfileManager.getInstance();

        try {
            DriverInstance mDriver = DriverManager.getInstance().getDriverInstanceByID(getTeiidDriverDefinitionId(serverVersion));
            if (mDriver == null) {
                createTeiidPreviewDriverInstance(serverVersion, driverPath, connectionURL, username);
            } else {
                // JBIDE-7493 Eclipse updates can break profiles because the driverPath is plugin version specific.
                String jarList = mDriver.getJarList();
                if(jarList != driverPath) {
                    mDriver.getPropertySet().getBaseProperties().put(IDriverMgmtConstants.PROP_DEFN_JARLIST, driverPath);
                }
                mDriver.getPropertySet().getBaseProperties().put(IJDBCDriverDefinitionConstants.URL_PROP_ID, connectionURL);
            }

            storeJDBCPassword(connectionURL, password);

            return pm.createTransientProfile(TEIID_PROFILE_PROVIDER_ID, createDriverProps(serverVersion,
                                                                                          driverPath,
                                                                                          connectionURL,
                                                                                          username,
                                                                                          vdbName));
        } catch (ConnectionProfileException e) {
            Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "error getting profile", e); //$NON-NLS-1$
            throw new CoreException(status);
        }

    }

    private static void createTeiidPreviewDriverInstance( ITeiidServerVersion serverVersion, String jarList,
                                                          String driverURL, String username ) {
        /* Create an ad-hoc version to avoid clashing ids with any built-in teiid runtime drivers */
        String driverId = TEIID_ADHOC_DRIVER_ID_SKELETON
                                                    .replaceAll("MAJOR", serverVersion.getMajor()) //$NON-NLS-1$
                                                    .replaceAll("MINOR", serverVersion.getMinor()); //$NON-NLS-1$

        // Have we already created an ad-hoc driver for this server version?
        DriverInstance driverInstance = DriverManager.getInstance().getDriverInstanceByID(driverId);
        if (driverInstance != null)
            return;

        IPropertySet pset = new PropertySetImpl(TEIID_ADHOC_DRIVER_DEFAULT_NAME, driverId);
        Properties baseProperties = new Properties();
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID, TEIID_DRIVER_NAME);
        if(null != driverURL) {
        	baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, driverURL);
        }
        if (null != username) {
            baseProperties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
        }
        
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID, TEIID_DATABASE_VENDOR_NAME);
        baseProperties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, serverVersion.toString());
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST, jarList);
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_TYPE, TEIID_ADHOC_DRIVER_DEFN_TYPE);
        baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_CLASS, TEIID_DRIVER_NAME);

        pset.setBaseProperties(baseProperties);

        DriverManager.getInstance().addDriverInstance(pset);

    }
    
//	private static void createTeiidVDBDriverInstance(String jarList,
//			String driverURL, String username, String password, String vdbName,
//			String profileName) {
//		IPropertySet pset = new PropertySetImpl(profileName + " Driver", //$NON-NLS-1$
//				TEIID_DRIVER_DEFINITION_ID_BASE + vdbName);
//		Properties baseProperties = new Properties();
//		baseProperties.setProperty(
//				IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID,
//				TEIID_DRIVER_NAME);
//		baseProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID,
//				driverURL);
//		if (null != username) {
//			baseProperties.setProperty(
//					IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, username);
//		}
//		if (null != password) {
//			baseProperties.setProperty(
//					IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, password);
//		}
//		baseProperties.setProperty(
//				IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID,
//				TEIID_DATABASE_VENDOR_NAME);
//		baseProperties.setProperty(
//				IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID,
//				TEIID_DATABASE_VERSION);
//		baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_JARLIST,
//				jarList);
//		baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_TYPE,
//				TEIID_PREVIEW_DRIVER_DEFN_TYPE);
//		baseProperties.setProperty(IDriverMgmtConstants.PROP_DEFN_CLASS,
//				TEIID_DRIVER_NAME);
//
//		pset.setBaseProperties(baseProperties);
//
//		DriverManager.getInstance().addDriverInstance(pset);
//
//	}
    
    public static void deleteTransientTeiidProfile(IConnectionProfile profile) {
          ProfileManager.getInstance().deleteTransientProfile(profile);
    }

    
    public static Properties createVDBTeiidProfileProperties(ITeiidServerVersion serverVersion, 
            String driverPath,
    		String connectionURL,
    		String username,
    		String password,
    		String vdbName,
    		String profileName ) throws CoreException {

        DriverInstance mDriver = DriverManager.getInstance().getDriverInstanceByID(getTeiidDriverDefinitionId(serverVersion));
        if (mDriver == null) {
            createTeiidPreviewDriverInstance(serverVersion, driverPath, driverPath, username);
        } else {
			// JBIDE-7493 Eclipse updates can break profiles because the driverPath is plugin version specific.
			String jarList = mDriver.getJarList();
			if(jarList != driverPath) {
				mDriver.getPropertySet().getBaseProperties().put(IDriverMgmtConstants.PROP_DEFN_JARLIST, driverPath);
			}
        }
        
        storeJDBCPassword(connectionURL, password);

        return createDriverProps(serverVersion, driverPath, connectionURL, username, vdbName);
    }

    public static IConnectionProfile createVDBTeiidProfile( ITeiidServerVersion serverVersion, 
            String driverPath,
    		String connectionURL,
    		String username,
    		String password,
    		String vdbName, 
    		String profileName) throws CoreException {
    	ProfileManager pm = ProfileManager.getInstance();
    	try {
            DriverInstance mDriver = DriverManager.getInstance().getDriverInstanceByID(getTeiidDriverDefinitionId(serverVersion));
    		if (mDriver == null) {
    			createTeiidPreviewDriverInstance(serverVersion, driverPath, connectionURL, username);
    		} else {
    			// JBIDE-7493 Eclipse updates can break profiles because the driverPath is plugin version specific.
    			String jarList = mDriver.getJarList();
    			if(jarList != driverPath) {
    				mDriver.getPropertySet().getBaseProperties().put(IDriverMgmtConstants.PROP_DEFN_JARLIST, driverPath);
    			}
    		}
    		
    		storeJDBCPassword(connectionURL, password);
    		
    		IConnectionProfile existingCP = pm.getProfileByName(profileName);
    		if (existingCP != null) {
    			return existingCP;
    		}

    		return pm.createProfile(profileName, "", //$NON-NLS-1$
    				TEIID_PROFILE_PROVIDER_ID,
    				createDriverProps(serverVersion, driverPath, connectionURL, username, vdbName));
    	} catch (ConnectionProfileException e) {
    		Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "error getting profile", e); //$NON-NLS-1$
    		throw new CoreException(status);
    	}
    }
    
    /**
     * Store the given password against the url using the built-in secure storage
     * 
     * @param url
     * @param password
     */
    private static void storeJDBCPassword(String url, String password) {
        if (password == null)
            return;
        
        String urlStorageKey = ConnectivityUtil.buildSecureStorageKey(TeiidJDBCConnection.class, url);
        try {
            getSecureStorageProvider().storeInSecureStorage(urlStorageKey, ConnectivityUtil.JDBC_PASSWORD, password);
        } catch (Exception ex) {
            Activator.log(ex);
        }
    }
    
    /**
     * Assemble the secure storage node key based 
     * upon the given class and connection url
     * 
     * @param klazz
     * @param connectURL
     * 
     * @return fully qualified key used in secure storage
     */
    public static String buildSecureStorageKey(Class<?> klazz, String connectURL) {
        String secureKey = new StringBuilder(SECURE_STORAGE_BASEKEY)
        .append(IPath.SEPARATOR)
        .append(klazz.getSimpleName())
        .append(IPath.SEPARATOR)
        .append(connectURL)
        .append(IPath.SEPARATOR).toString();
        
        return secureKey;
    }

    /**
     * Get the Eclipse implementation of the {@link ISecureStorageProvider} interface
     * 
     * @return
     */
    public static ISecureStorageProvider getSecureStorageProvider() {
        return EquinoxSecureStorageProvider.getInstance();
    }

    /**
     * Find a Teiid {@link Driver} for the given server version.
     *
     * The driver class should be provided as a check to ensure the class name
     * is as expected.
     *
     * @param teiidServerVersion
     * @param driverClass
     *
     * @return the Teiid {@link Driver}
     * @throws Exception
     */
    public static Driver getTeiidDriver(ITeiidServerVersion teiidServerVersion, String driverClass) throws Exception {
        Driver driver = TeiidRuntimeRegistry.getInstance().getTeiidDriver(teiidServerVersion);
        if (driver != null && driver.getClass().getName().equals(driverClass))
            return driver;

        String msg = "No Teiid Driver ( "+ driverClass + " ) available for version " + teiidServerVersion;  //$NON-NLS-1$//$NON-NLS-2$
        throw new IllegalStateException(msg);
    }
}

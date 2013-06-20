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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * @since 8.0
 */
public class TeiidJDBCConnection extends JDBCConnection {

    /**
     * Create an instance of this connection
     *
     * @param profile
     * @param factoryClass
     */
    public TeiidJDBCConnection( IConnectionProfile profile, Class factoryClass ) {
        super(profile, factoryClass);
    }

    @Override
    protected Object createConnection( ClassLoader classloader ) throws Throwable {
        Properties props = getConnectionProfile().getBaseProperties();
        Properties connectionProps = new Properties();

        // boolean hasDriver = (getDriverDefinition() != null);
        String driverClass = null;
        if (getDriverDefinition() != null) {
            driverClass = getDriverDefinition().getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
        } else {
            driverClass = props.getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
        }

        String connectURL = props.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
        String uid = props.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);

        String urlStorageKey = ConnectivityUtil.buildSecureStorageKey(getClass(), connectURL);
        String pwd = ConnectivityUtil.getSecureStorageProvider()
                                    .getFromSecureStorage(urlStorageKey, ConnectivityUtil.JDBC_PASSWORD);

        String nameValuePairs = props.getProperty(IJDBCConnectionProfileConstants.CONNECTION_PROPERTIES_PROP_ID);
        String propDelim = ",";//$NON-NLS-1$

        if (uid != null) {
            connectionProps.setProperty("user", uid); //$NON-NLS-1$
        }
        
        if (pwd != null) {
            connectionProps.setProperty("password", pwd); //$NON-NLS-1$
        }

        if (nameValuePairs != null && nameValuePairs.length() > 0) {
            String[] pairs = parseString(nameValuePairs, ","); //$NON-NLS-1$
            String addPairs = ""; //$NON-NLS-1$
            for (int i = 0; i < pairs.length; i++) {
                String[] namevalue = parseString(pairs[i], "="); //$NON-NLS-1$
                connectionProps.setProperty(namevalue[0], namevalue[1]);
                if (i == 0 || i < pairs.length - 1) {
                    addPairs = addPairs + propDelim;
                }
                addPairs = addPairs + pairs[i];
            }
        }

        /*
         * The classloader is provided to allow access to the JAR_LIST of the connection
         * properties. Thus, the driver should be contained in this list.
         */
        Driver jdbcDriver = (Driver) classloader.loadClass(driverClass).newInstance();
        if (jdbcDriver != null)
            return jdbcDriver.connect(connectURL, connectionProps);

        /*
         * Failed to find the driver with the given classloader so try to get a match from
         * the installed client runtime plugins. This could happen if the jar list fails to
         * contain the correct driver jar, for example.
         *
         * Using the database version id, attempt to acquire the driver
         * from the teiid client runtimes. This no longer requires using
         * the default teiid server (which has to be connected).
         */
        String teiidVersion = props.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID);
        if (teiidVersion != null) {
            ITeiidServerVersion teiidServerVersion = new TeiidServerVersion(teiidVersion);
            jdbcDriver = ConnectivityUtil.getTeiidDriver(teiidServerVersion, driverClass);
            if (jdbcDriver != null) {
                return jdbcDriver.connect(connectURL, connectionProps);
            }
        }

        throw new Exception("Cannot find Teiid Driver"); //$NON-NLS-1$
    }
}

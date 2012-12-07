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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 * @since 8.0
 */
public class TeiidJDBCConnection extends JDBCConnection {

    public TeiidJDBCConnection( IConnectionProfile profile,
                                Class factoryClass ) {
        super(profile, factoryClass);
    }

    @Override
    protected Object createConnection( ClassLoader cl ) throws Throwable {
        Properties props = getConnectionProfile().getBaseProperties();
        // TODO: remove these Sys Outs
        // System.out.print("  >>  Properties for Connection Profile: " + getConnectionProfile().getName());
        // for( Object key : props.keySet() ) {
        // System.out.print("\n      Prop : Key = " + key + "  Value = " + props.get(key));
        // }
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
        
        ITeiidServer defaultServer = ModelerCore.getDefaultServer();
        
        Driver jdbcDriver = defaultServer.getTeiidDriver(driverClass);
        
        if (jdbcDriver != null) {
            return jdbcDriver.connect(connectURL, connectionProps);
        }
        
        throw new Exception("Cannot find Teiid Driver"); //$NON-NLS-1$
    }

}

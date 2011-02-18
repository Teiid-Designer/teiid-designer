package org.teiid.datatools.connectivity;

import java.sql.Driver;
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;

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
            driverClass = getDriverDefinition().getProperty(IJDBCConnectionProfileConstants.DRIVER_CLASS_PROP_ID);
        } else {
            driverClass = props.getProperty(IJDBCConnectionProfileConstants.DRIVER_CLASS_PROP_ID);
        }

        String connectURL = props.getProperty(IJDBCConnectionProfileConstants.URL_PROP_ID);
        String uid = props.getProperty(IJDBCConnectionProfileConstants.USERNAME_PROP_ID);
        String pwd = props.getProperty(IJDBCConnectionProfileConstants.PASSWORD_PROP_ID);
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

        Driver jdbcDriver = (Driver)cl.loadClass(driverClass).newInstance();
        return jdbcDriver.connect(connectURL, connectionProps); // return super.createConnection(cl);
    }

}

package org.teiid.designer.datatools.profiles.modeshape;

import java.sql.Driver;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.JDBCConnection;

public class ModeShapeJDBCConnection extends JDBCConnection {

	public ModeShapeJDBCConnection( IConnectionProfile profile,
                                Class factoryClass ) {
        super(profile, factoryClass);
    }

    @Override
    protected Object createConnection( ClassLoader cl ) throws Throwable {
        Properties props = getConnectionProfile().getBaseProperties();
        Properties connectionProps = new Properties();

        String driverClass = null;
        if (getDriverDefinition() != null) {
            driverClass = getDriverDefinition().getProperty(IJDBCConnectionProfileConstants.DRIVER_CLASS_PROP_ID);
        } else {
            driverClass = props.getProperty(IJDBCConnectionProfileConstants.DRIVER_CLASS_PROP_ID);
        }

        String connectURL = props.getProperty(IJDBCConnectionProfileConstants.URL_PROP_ID);
        String uid = props.getProperty(IJDBCConnectionProfileConstants.USERNAME_PROP_ID);
        String pwd = props.getProperty(IJDBCConnectionProfileConstants.PASSWORD_PROP_ID);

        if (uid != null) {
            connectionProps.setProperty("user", uid); //$NON-NLS-1$
        }
        if (pwd != null) {
            connectionProps.setProperty("password", pwd); //$NON-NLS-1$
        }

        Driver jdbcDriver = (Driver)cl.loadClass(driverClass).newInstance();
        return jdbcDriver.connect(connectURL, connectionProps); // return super.createConnection(cl);
    }

}

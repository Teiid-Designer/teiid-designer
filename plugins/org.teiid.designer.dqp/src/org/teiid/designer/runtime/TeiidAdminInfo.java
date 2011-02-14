package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;


/**
 * The <code>TeiidAdminInfo</code> defines the properties needed to make a Teiid Admin connection.
 */
public class TeiidAdminInfo extends TeiidConnectionInfo {

    /**
     * The default Teiid Admin persist password flag. Value is {@value} .
     */
    public static final boolean DEFAULT_PERSIST_PASSWORD = true;

    /**
     * The default Teiid Admin port number. Value is {@value} .
     */
    public static final String DEFAULT_PORT = "31443"; //$NON-NLS-1$

    /**
     * The default Teiid Admin secure protocol flag. Value is {@value} .
     */
    public static final boolean DEFAULT_SECURE = true;

    /**
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param password the connection password (can be <code>null</code> or empty)
     * @param persistPassword <code>true</code> if the password should be persisted
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    public TeiidAdminInfo( String port,
                           String username,
                           String password,
                           boolean persistPassword,
                           boolean secure ) {
        super(port, username, password, persistPassword, secure);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public TeiidAdminInfo clone() {
        TeiidAdminInfo cloned = new TeiidAdminInfo(getPort(), getUsername(), getPassword(), isPasswordBeingPersisted(), isSecure());
        cloned.setHostProvider(getHostProvider());
        return cloned;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#getType()
     */
    @Override
    public String getType() {
        return Util.getString("adminInfoType"); //$NON-NLS-1$
    }

}
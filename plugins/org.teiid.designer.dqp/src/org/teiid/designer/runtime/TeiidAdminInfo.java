package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.Util;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.security.ISecureStorageProvider;


/**
 * The <code>TeiidAdminInfo</code> defines the properties needed to make a Teiid Admin connection.
 *
 * @since 8.0
 */
public class TeiidAdminInfo extends TeiidConnectionInfo {

    /**
     * The default Teiid Admin persist password flag. Value is {@value} .
     */
    public static final boolean DEFAULT_PERSIST_PASSWORD = true;

    /**
     * The default Teiid Admin port number. Value is {@value} .
     */
    public static final String DEFAULT_PORT = "9999"; //$NON-NLS-1$

    /**
     * The default Teiid Admin secure protocol flag. Value is {@value} .
     */
    public static final boolean DEFAULT_SECURE = true;

    /** 
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider used for storing the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    public TeiidAdminInfo( String port,
                           String username,
                           ISecureStorageProvider secureStorageProvider,
                           String password,
                           boolean secure ) {
        super(port, username, secureStorageProvider, password, secure);
    }
    
    @Override
    protected String getPasswordKey() {
        return ConnectivityUtil.ADMIN_PASSWORD;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings( "javadoc" )
    @Override
    public TeiidAdminInfo clone() {
        TeiidAdminInfo cloned = new TeiidAdminInfo(getPort(), getUsername(), getSecureStorageProvider(), getPassword(), isSecure());
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
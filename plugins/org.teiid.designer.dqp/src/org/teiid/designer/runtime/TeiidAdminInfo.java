package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.Util;

import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;


/**
 * The <code>TeiidAdminInfo</code> defines the properties needed to make a Teiid Admin connection.
 *
 * @since 8.0
 */
public class TeiidAdminInfo extends TeiidConnectionInfo implements ITeiidAdminInfo {

    /** 
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider used for storing the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    public TeiidAdminInfo( String host,
    					   String port,
                           String username,
                           ISecureStorageProvider secureStorageProvider,
                           String password,
                           boolean secure ) {
        super(host, port, username, secureStorageProvider, password, secure);
        /*
         * Password must be set last since it relies on getUrl() which is built from
         * the other properties.
         *
         * When restoring from TeiidServerManager, the new TeiidConnectionInfo
         * will have a pass token for newer models and password for older models
         * so diverge at this point to ensure both situations are handled.
         *
         */
        
        if( password != null ) {
        	setPassword(password);
        }
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
    public ITeiidAdminInfo clone() {
        TeiidAdminInfo cloned = new TeiidAdminInfo(getHost(), getPort(), getUsername(), getSecureStorageProvider(), getPassword(), isSecure());
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
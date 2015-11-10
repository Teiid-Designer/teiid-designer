package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;


/**
 * The <code>TeiidJdbcInfo</code> defines the properties needed to make a Teiid JDBC connection.
 *
 * @since 8.0
 */
public class TeiidJdbcInfo extends TeiidConnectionInfo implements ITeiidJdbcInfo {

    private static final String VDB_PLACEHOLDER = "<vdbname>"; //$NON-NLS-1$
    private static final String JDBC_TEIID_PREFIX = "jdbc:teiid:"; //$NON-NLS-1$

    /**
     * The name of the VDB that this connection will connect to (never empty or <code>null</code>)
     */
    private String vdbname;
    
    /**
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider for storing of the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    public TeiidJdbcInfo( String port,
                          String username,
                          ISecureStorageProvider secureStorageProvider,
                          String password,
                          boolean secure ) {
        this(VDB_PLACEHOLDER, port, username, secureStorageProvider, password, secure);
    }

    /**
     * @param vdbname the VDB name (never empty or <code>null</code>)
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider for storing the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    private TeiidJdbcInfo( String vdbname,
                           String port,
                           String username,
                           ISecureStorageProvider secureStorageProvider,
                           String password,
                           boolean secure ) {
        super(port, username, secureStorageProvider, password, secure);
        CoreArgCheck.isNotEmpty(vdbname, "vdbname"); //$NON-NLS-1$

        this.vdbname = vdbname;

        /*
         * Need to set the password again given 'vdbname' forms part of the url
         * which is required in the formation of the passToken and the reference
         * to the password in secure storage
         */
        initPassword(password);
    }

    /**
     * @param vdbname the VDB name (may not be empty or <code>null</code>)
     * @param teiidJdbcInfo the connection properties whose values are being used to construct this object
     * @throws IllegalArgumentException if vdbname is empty or <code>null</code>
     * @see #validate()
     */
    public TeiidJdbcInfo( String vdbname,
                          ITeiidJdbcInfo teiidJdbcInfo ) {
        this(vdbname, teiidJdbcInfo.getPort(), teiidJdbcInfo.getUsername(), teiidJdbcInfo.getSecureStorageProvider(), 
             teiidJdbcInfo.getPassword(), teiidJdbcInfo.isSecure());
        setHostProvider(teiidJdbcInfo.getHostProvider(), true);
    }
    
    @Override
    protected String getPasswordKey() {
        return ConnectivityUtil.JDBC_PASSWORD;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings( "javadoc" )
    @Override
    public ITeiidJdbcInfo clone() {
        TeiidJdbcInfo cloned = new TeiidJdbcInfo(getPort(), getUsername(), getSecureStorageProvider(), getPassword(), isSecure());
        cloned.setHostProvider(getHostProvider(), true);
        return cloned;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object object ) {
        if (super.equals(object) && this.vdbname.equals(((TeiidJdbcInfo) object).vdbname)) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#getType()
     */
    @Override
    public String getType() {
        return Util.getString("jdbcInfoType"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#getUrl()
     */
    @Override
    public String getUrl() {
        // jdbc:teiid:<vdbname>@mm<s>://host:port
        StringBuilder sb = new StringBuilder();
        sb.append(JDBC_TEIID_PREFIX);
        sb.append(this.vdbname);
        sb.append('@');

        return sb.append(super.getUrl()).toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(super.hashCode(), this.vdbname);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#toString()
     */
    @Override
    public String toString() {
        return DqpPlugin.Util.getString("jdbcConnectionInfoProperties", //$NON-NLS-1$
                                        super.toString(),
                                        this.vdbname);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.TeiidConnectionInfo#validateUrl()
     */
    @Override
    protected IStatus validateUrl() {
        try {
            TeiidServerUtils.validPortNumber(getPort());
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("invalidServerUrl", getType(), e.getMessage()), e); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

}

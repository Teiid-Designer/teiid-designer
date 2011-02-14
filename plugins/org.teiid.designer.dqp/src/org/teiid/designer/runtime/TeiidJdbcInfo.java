package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.DqpPlugin.Util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.util.HashCodeUtil;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * The <code>TeiidJdbcInfo</code> defines the properties needed to make a Teiid JDBC connection.
 */
public class TeiidJdbcInfo extends TeiidConnectionInfo {

    private static final String VDB_PLACEHOLDER = "<vdbname>"; //$NON-NLS-1$
    private static final String JDBC_TEIID_PREFIX = "jdbc:teiid:"; //$NON-NLS-1$

    /**
     * The default Teiid Admin persist password flag. Value is {@value} .
     */
    public static final boolean DEFAULT_PERSIST_PASSWORD = false;

    /**
     * The default Teiid JDBC port number. Value is {@value} .
     */
    public static final String DEFAULT_PORT = "31000"; //$NON-NLS-1$

    /**
     * The default Teiid Admin secure protocol flag. Value is {@value} .
     */
    public static final boolean DEFAULT_SECURE = true;

    /**
     * The name of the VDB that this connection will connect to (never empty or <code>null</code>)
     */
    private String vdbname;

    /**
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param password the connection password (can be <code>null</code> or empty)
     * @param persistPassword <code>true</code> if the password should be persisted
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    public TeiidJdbcInfo( String port,
                          String username,
                          String password,
                          boolean persistPassword,
                          boolean secure ) {
        this(VDB_PLACEHOLDER, port, username, password, persistPassword, secure);
    }

    /**
     * @param vdbname the VDB name (never empty or <code>null</code>)
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param password the connection password (can be <code>null</code> or empty)
     * @param persistPassword <code>true</code> if the password should be persisted
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    private TeiidJdbcInfo( String vdbname,
                           String port,
                           String username,
                           String password,
                           boolean persistPassword,
                           boolean secure ) {
        super(port, username, password, persistPassword, secure);
        CoreArgCheck.isNotEmpty(vdbname, "vdbname"); //$NON-NLS-1$
        this.vdbname = vdbname;
    }

    /**
     * @param vdbname the VDB name (may not be empty or <code>null</code>)
     * @param teiidJdbcInfo the connection properties whose values are being used to construct this object
     * @throws IllegalArgumentException if vdbname is empty or <code>null</code>
     * @see #validate()
     */
    public TeiidJdbcInfo( String vdbname,
                          TeiidJdbcInfo teiidJdbcInfo ) {
        this(vdbname, teiidJdbcInfo.getPort(), teiidJdbcInfo.getUsername(), teiidJdbcInfo.getPassword(),
                teiidJdbcInfo.isPasswordBeingPersisted(), teiidJdbcInfo.isSecure());
        setHostProvider(teiidJdbcInfo.getHostProvider());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public TeiidJdbcInfo clone() {
        TeiidJdbcInfo cloned = new TeiidJdbcInfo(getPort(), getUsername(), getPassword(), isPasswordBeingPersisted(), isSecure());
        cloned.setHostProvider(getHostProvider());
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
            ServerUtils.validPortNumber(getPort());
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("invalidServerUrl", getType(), e.getMessage()), e); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

}

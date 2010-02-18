/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.HashCodeUtil;

/**
 *
 */
public final class Server {

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * @param thisObj an object being compared (may be <code>null</code>)
     * @param thatObj the other object being compared (may be <code>null</code>)
     * @return <code>true</code> if both objects are <code>null</code> or both are not <code>null</code> and equal
     */
    private static boolean equivalent( Object thisObj,
                                       Object thatObj ) {
        // true if both objects are null
        if (thisObj == null) {
            return (thatObj == null);
        }

        if (thatObj == null) return false;
        return thisObj.equals(thatObj);
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The password to use when logging on to the server.
     */
    private final String password;

    /**
     * Indicates if the password should be stored locally when the server is persisted.
     */
    private final boolean persistPassword;

    /**
     * The server URL (never <code>null</code>).
     */
    private final String url;

    /**
     * The user name to use when logging on to the server (never <code>null</code>).
     */
    private final String user;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs on new <code>Server</code>.
     * 
     * @param url the server URL (never <code>null</code>)
     * @param user the server user (may be <code>null</code>)
     * @param password the server password (may be <code>null</code>)
     * @throws IllegalArgumentException if the URL or user arguments are <code>null</code>
     */
    public Server( String url,
                   String user,
                   String password,
                   boolean persistPassword ) {
        ArgCheck.isNotNull(url, "url"); //$NON-NLS-1$
        ArgCheck.isNotNull(user, "user"); //$NON-NLS-1$

        this.url = url;
        this.user = user;
        this.password = password;
        this.persistPassword = persistPassword;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if ((obj == null) || (getClass() != obj.getClass())) return false;

        Server otherServer = (Server)obj;
        return equivalent(this.url, otherServer.url) && equivalent(this.user, otherServer.user)
               && equivalent(this.password, otherServer.password) && (this.persistPassword == otherServer.persistPassword);
    }
    
    public ServerAdmin getAdmin() {
        // TODO implement
        return null;
    }

    /**
     * @return the server authentication password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @return the server URL (never <code>null</code>)
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @return the server authentication user (never <code>null</code>)
     */
    public String getUser() {
        return this.user;
    }
    
    public ServerQueryManager getQueryManager() {
        // TODO implement
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(0, this.url, this.user, this.password);
    }

    /**
     * A server has the same identifying properties if their URL and user matches.
     * 
     * @param otherServer the server whose key is being compared (never <code>null</code>)
     * @return <code>true</code> if the servers have the same key
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public boolean hasSameKey( Server otherServer ) {
        ArgCheck.isNotNull(otherServer, "otherServer"); //$NON-NLS-1$
        return (equivalent(this.url, otherServer.url) && equivalent(this.user, otherServer.user));
    }

    /**
     * @return persistPassword <code>true</code> if the password is being persisted
     */
    public boolean isPasswordBeingPersisted() {
        return this.persistPassword;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getUrl() + "::" + getUser(); //$NON-NLS-1$
    }

}

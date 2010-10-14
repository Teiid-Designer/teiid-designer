/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.teiidadmin;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminFactory;


/**
 * 
 */
public class TeiidAdminConnection implements IConnection {

    private IConnectionProfile savedProfile;
    private Admin admin;
    private Throwable connectException;

    /**
     * @param profile
     */
    public TeiidAdminConnection( IConnectionProfile profile ) {
        savedProfile = profile;
        
        if (null == admin) {
            try {
                Properties props = profile.getBaseProperties();
                String username = props.getProperty(ITeiidAdminProfileConstants.USERNAME_PROP_ID);
                String password = props.getProperty(ITeiidAdminProfileConstants.PASSWORD_PROP_ID);
                String url = props.getProperty(ITeiidAdminProfileConstants.URL_PROP_ID);
                admin = AdminFactory.getInstance().createAdmin(username, password.toCharArray(), url);
            } catch (AdminException e) {
                connectException = e;
                admin = null;
            }

        }
    }

    /**
     * @param profile
     * @param uid
     * @param pwd
     */
    public TeiidAdminConnection( IConnectionProfile profile,
                                 String username,
                                 String password ) {
        savedProfile = profile;
        if (null == admin) {
            try {
                Properties props = profile.getBaseProperties();
                String url = props.getProperty(ITeiidAdminProfileConstants.URL_PROP_ID);
                admin = AdminFactory.getInstance().createAdmin(username, password.toCharArray(), url);
            } catch (AdminException e) {
                connectException = e;
                admin = null;
            }

        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#close()
     */
    @Override
    public void close() {
        if (null != admin) {
        	admin.close();
        	admin = null;
        }
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getConnectException()
     */
    @Override
    public Throwable getConnectException() {
        return connectException;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getConnectionProfile()
     */
    @Override
    public IConnectionProfile getConnectionProfile() {
        return savedProfile;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.IConnection#getRawConnection()
     */
    @Override
    public Object getRawConnection() {
        return admin;
    }

}

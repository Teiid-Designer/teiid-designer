/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.ui;

/**
 *
 */
public interface ICredentialsCommon {

    String PASSWORD_PROP_ID = "AuthPassword"; //$NON-NLS-1$
    String USERNAME_PROP_ID = "AuthUserName"; //$NON-NLS-1$
    String SECURITY_TYPE_ID = "SecurityType"; //$NON-NLS-1$ //None, HTTPBasic, WS-Security
    
    enum SecurityType {
        None,
        HTTPBasic,
        WSSecurity
    }
}

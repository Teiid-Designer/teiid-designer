/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ws;

public interface IWSProfileConstants {

    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    String PASSWORD_PROP_ID = "AuthPassword"; //$NON-NLS-1$
    String USERNAME_PROP_ID = "AuthUserName"; //$NON-NLS-1$
    String SECURITY_TYPE_ID = "SecurityType"; //$NON-NLS-1$ //None, HTTPBasic, WS-Security
    String URL_PROP_ID = "EndPoint"; //$NON-NLS-1$
    String WSDL_URI_PROP_ID = "wsdlURI"; //$NON-NLS-1$

    public enum SecurityType {
        None,
        HTTPBasic,
        WSSecurity
    }
}

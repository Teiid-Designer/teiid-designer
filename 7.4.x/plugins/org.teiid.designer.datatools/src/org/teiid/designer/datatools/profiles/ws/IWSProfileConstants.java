package org.teiid.designer.datatools.profiles.ws;

public interface IWSProfileConstants {

    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$
    String PASSWORD_PROP_ID = "AuthPassword"; //$NON-NLS-1$
    String USERNAME_PROP_ID = "AuthUserName"; //$NON-NLS-1$
    String SECURITY_TYPE_ID = "SecurityType"; //$NON-NLS-1$ //None, HTTPBasic, WS-Security
    String URL_PROP_ID = "EndPoint"; //$NON-NLS-1$

    public enum SecurityType {
        None,
        HTTPBasic,
        WSSecurity
    }
}

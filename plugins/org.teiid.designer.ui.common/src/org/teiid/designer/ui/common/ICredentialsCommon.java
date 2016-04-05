/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.common;

/**
 *
 *
 * @since 8.0
 */
public interface ICredentialsCommon {

    String PASSWORD_PROP_ID = "AuthPassword"; //$NON-NLS-1$
    String USERNAME_PROP_ID = "AuthUserName"; //$NON-NLS-1$
    String SECURITY_TYPE_ID = "SecurityType"; //$NON-NLS-1$ //None, HTTPBasic, HTTPDigest
    
    public enum SecurityType {
        None,
        HTTPBasic,
        HTTPDigest;
        
        
        /*
         * Disabled since it is not currently 
         * supported at the moment.
         * 
         * Should be added to the security type combo
         * in CredentialsComposite for selection.
         * 
         * Also, should be properly checked in the validate
         * method of the class PropertyPage.
         */
//        WSSecurity
        
        /**
         * Safely return one of the enum values for the given string.
         * If the given string is not one of the enums then return
         * the enum {@link #None}.
         * 
         * @param securityType
         * @return one of the enums, {@link #None} by default.
         */
        public static SecurityType retrieveValue(String securityType) {
            for (SecurityType type : SecurityType.values()) {
                if (type.name().equals(securityType)) {
                    return type;
                }
            }
            
            return SecurityType.None;
        }
    }
}

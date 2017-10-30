/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.jdg7;

import org.eclipse.osgi.util.NLS;

/**
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {
	public static String RemoteServerList;
	public static String RemoteServerListMissing;
	public static String RemoteServerListToolTip;
	public static String TrustStoreFileName;
	public static String TrustStoreFileNameTooltip;
	public static String TrustStorePassword;
	public static String TrustStorePasswordTooltip;
	public static String KeyStoreFileName;
	public static String KeyStoreFileNameTooltip;
	public static String KeyStorePassword;
	public static String KeyStorePasswordTooltip;
	public static String AuthenticationServerName;
	public static String AuthenticationServerNameTooltip;
	public static String AuthenticationRealm;
	public static String AuthenticationRealmTooltip;
	public static String SaslMechanism;
	public static String SaslMechanismTooltip;
	public static String AuthenticationUserName;
	public static String AuthenticationUserNameTooltip;
	public static String AuthenticationUserPassword;
	public static String AuthenticationUserPasswordTooltip;
	public static String RequiredProperty;
	
    static {
        NLS.initializeMessages("org.teiid.designer.datatools.profiles.jdg7.messages", Messages.class); //$NON-NLS-1$
    }
}

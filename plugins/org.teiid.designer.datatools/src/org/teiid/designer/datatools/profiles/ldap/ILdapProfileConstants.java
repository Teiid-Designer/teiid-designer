/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ldap;

/**
 * 
 *
 * @since 8.0
 */
public interface ILdapProfileConstants {

	/**
	 * SSL Ldap Scheme
	 */
	String LDAPS_SCHEME = "ldaps://"; //$NON-NLS-1$

	/**
	 * Unencrypted Ldap Scheme
	 */
	String LDAP_SCHEME = "ldap://"; //$NON-NLS-1$

	/**
	 * Teiid Category
	 */
    String TEIID_CATEGORY = "org.teiid.designer.import.category"; //$NON-NLS-1$

    /**
     * Ldap User
     */
    String USERNAME_PROP_ID = "LdapAdminUserDN"; //$NON-NLS-1$

    /**
     * Ldap Password
     */
    String PASSWORD_PROP_ID = "LdapAdminUserPassword"; //$NON-NLS-1$

    /**
     * Ldap Url
     */
    String URL_PROP_ID = "LdapUrl"; //$NON-NLS-1$

    /**
     * Ldap Scheme
     */
    String SCHEME_PROP_ID = "LdapScheme"; //$NON-NLS-1$

    /**
     * Ldap Host
     */
    String HOST_PROP_ID = "LdapHost"; //$NON-NLS-1$

    /**
     * Ldap Port
     */
    String PORT_PROP_ID = "LdapPort"; //$NON-NLS-1$

    /**
     * Network Provider
     */
    String NETWORK_PROVIDER = "NetworkProvider"; //$NON-NLS-1$

    /**
     * JNDI Network Provider Label
     */
    String JNDI_NETWORK_PROVIDER = "JNDI (Java Naming and Directory Interface)"; //$NON-NLS-1$

    /**
     * Context Factory
     */
    String CONTEXT_FACTORY = "LdapContextFactory"; //$NON-NLS-1$

    /**
     * Authentication Method
     */
	String AUTHENTICATION_METHOD = "LdapAuthenticationMethod"; //$NON-NLS-1$

    /**
     * No authentication or anonymous
     */
    String AUTHMETHOD_NONE = "none"; //$NON-NLS-1$

    /**
     * Simple authentication
     */
    String AUTHMETHOD_SIMPLE = "simple"; //$NON-NLS-1$

    /**
     * No Connection
     */
    String NO_CONNECTION = "No connection"; //$NON-NLS-1$

    /**
     * Profile class name key constant
     */
    String LDAP_CLASSNAME = "class-name"; //$NON-NLS-1$

    /**
     * Profile connection teiid's ldap connection factory
     */
    String LDAP_CONNECTION_FACTORY = "org.teiid.resource.adapter.ldap.LDAPManagedConnectionFactory"; //$NON-NLS-1$

    /**
     * DNS Timeout Retries
     */
    String COM_SUN_JNDI_DNS_TIMEOUT_RETRIES = "com.sun.jndi.dns.timeout.retries"; //$NON-NLS-1$

    /**
     * Timeout Initial
     */
    String COM_SUN_JNDI_DNS_TIMEOUT_INITIAL = "com.sun.jndi.dns.timeout.initial"; //$NON-NLS-1$

    /**
     * Connect Timeout
     */
    String COM_SUN_JNDI_LDAP_CONNECT_TIMEOUT = "com.sun.jndi.ldap.connect.timeout"; //$NON-NLS-1$

    /**
     * Ldap Version
     */
    String JAVA_NAMING_LDAP_VERSION = "java.naming.ldap.version"; //$NON-NLS-1$

    /**
     * Base DN
     */
    String BASE_DN = "baseDN"; //$NON-NLS-1$
}

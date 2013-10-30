/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.webservices.util;

import java.util.Properties;
import org.teiid.designer.webservice.WebServicePlugin;

/**
 * Encapsulation of the security properties for web archive creation
 */
public class SecurityCredentials {

    private static final String I18N_PREFIX = "WebArchiveBuilder."; //$NON-NLS-1$

    private String securityType;
    private String securityRealm;
    private String securityRole;
    private String securityUsername;
    private String securityPassword;

    /**
     * @param properties
     */
    public SecurityCredentials(Properties properties) {
        securityType = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_SECURITY_TYPE);
        securityRealm = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_SECURITY_REALM);
        securityRole = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_SECURITY_ROLE);
        securityUsername = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_SECURITY_USERNAME);
        securityPassword = properties.getProperty(WebArchiveBuilderConstants.PROPERTY_SECURITY_PASSWORD);
    }

    protected static String getString( final String id ) {
        return WebServicePlugin.Util.getString(I18N_PREFIX + id);
    }

    /**
     * @param type
     *
     * @return whether this security is of the given type
     */
    public boolean hasType(String type) {
        return securityType != null && securityType.equals(type);
    }

    /**
     * @return the securityType
     */
    public String getSecurityType() {
        return this.securityType;
    }

    /**
     * @throws Exception
     */
    private void checkSecurityType() throws Exception {
        if (getSecurityType() == null)
            throw new Exception(getString("WebArchiveCreationFailed_SecurityTypeNotDefined")); //$NON-NLS-1$
    }

    /**
     * @return the securityRealm
     *
     * @throws Exception if the security type has not been defined.
     */
    public String getSecurityRealm() throws Exception {
        checkSecurityType();

        if (this.securityRealm == null)
            throw new Exception(getString("WebArchiveCreationFailed_SecurityRealmNotDefined")); //$NON-NLS-1$

        return this.securityRealm;
    }

    /**
     * @return the securityRole
     *
     * @throws Exception if the security type has not been defined.
     */
    public String getSecurityRole() throws Exception {
        checkSecurityType();

        if (this.securityRole == null)
            throw new Exception(getString("WebArchiveCreationFailed_SecurityRoleNotDefined")); //$NON-NLS-1$

        return this.securityRole;
    }

    /**
     * @return the securityUsername
     *
     * @throws Exception if the security type has not been defined.
     */
    public String getSecurityUsername() throws Exception {
        checkSecurityType();

        if (this.securityUsername == null)
            throw new Exception(getString("WebArchiveCreationFailed_SecurityUsernameNotDefined")); //$NON-NLS-1$

        return this.securityUsername;
    }

    /**
     * @return the securityPassword
     *
     * @throws Exception if the security type has not been defined.
     */
    public String getSecurityPassword() throws Exception {
        checkSecurityType();

        if (this.securityPassword == null)
            throw new Exception(getString("WebArchiveCreationFailed_SecurityPasswordNotDefined")); //$NON-NLS-1$

        return this.securityPassword;
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.net.URL;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;

import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.ui.ICredentialsCommon;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;

/**
 * Common utilities for the Web Service Wizards
 */
public class WSWizardUtils {

    private WSWizardUtils() {
    }

    /**
     * Extract an {@link URL} from the given {@link IConnectionProfile} using
     * the given property key and test its connectivity.
     * <p>
     * If the URL requires authentication then the authentication security type,
     * username and password will all be extracted from the
     * {@link IConnectionProfile}. Thus, the values for the authentication
     * should be stored in the connection profile using the keys from
     * {@link ICredentialsCommon}.
     * 
     * 
     * @param connectionProfile
     * @param propertyKey
     * @return
     */
    public static Exception testURLConnection(IConnectionProfile connectionProfile,
            final String propertyKey) {
        Properties connProperties = connectionProfile.getBaseProperties();
        // InputStream not provided, check XML file
        String xmlFile = connProperties == null ? null : (String) connProperties.get(propertyKey);

        try {
            URL url = URLHelper.buildURL(xmlFile);
            String securityType = connProperties.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
            boolean resolved = false;

            if (securityType == null || SecurityType.None.name().equals(securityType)) {
                resolved = URLHelper.resolveUrl(url);
            } else {
                String userName = connProperties.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
                String password = connProperties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
                resolved = URLHelper.resolveUrl(url, userName, password, true);
            }

            if (!resolved) {
                throw new Exception("Failed to validate connection"); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            return ex;
        }

        return null;
    }

}

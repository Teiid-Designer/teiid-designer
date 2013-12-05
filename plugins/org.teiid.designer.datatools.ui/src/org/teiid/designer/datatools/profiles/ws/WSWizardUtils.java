/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.core.util.URLHelper;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;


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
            
            // Supply content type
            Map<String,String> connPropMap = new HashMap<String,String>();
            connPropMap.put("Accept", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
            
            if (securityType == null || SecurityType.None.name().equals(securityType)) {
                resolved = URLHelper.resolveUrl(url, null, null, connPropMap, true);
            } else {
                String userName = connProperties.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
                String password = connProperties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
                resolved = URLHelper.resolveUrl(url, userName, password, connPropMap, true);
            }

            if (!resolved) {
                throw new Exception(DatatoolsUiConstants.UTIL.getString("WSWizardUtils.connectionFailureMessage")); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            return ex;
        }

        return null;
    }

}

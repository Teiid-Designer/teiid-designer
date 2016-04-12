/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ws;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.core.util.URLHelper;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.ui.common.ICredentialsCommon;


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
    public static Exception testRestURLConnection(IConnectionProfile connectionProfile,
            final String propertyKey) {
        Properties connProperties = connectionProfile.getBaseProperties();
        // InputStream not provided, check XML file
        String xmlFile = connProperties == null ? null : (String) connProperties.get(propertyKey);
		String responseType = IWSProfileConstants.XML;
		if( connProperties != null ) {
			if(  connProperties.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY) != null) {
				responseType = (String)connProperties.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY);
			}
		}

        try {
            URL url = URLHelper.buildURL(xmlFile);
            
			String userName = null;
			String password = null;
			userName = connProperties.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
            password = connProperties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
            URI uri = url.toURI();
            GetMethod httpget = new GetMethod(uri.toString());
            String securityType = null;
            
        	boolean secure = (userName != null && !userName.isEmpty());

			HttpClient client = new HttpClient();
			if (secure){
				securityType = (String) connProperties.get(ICredentialsCommon.SECURITY_TYPE_ID);
				client.getState().setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
						new UsernamePasswordCredentials(userName, password));
				List<String> authPrefs = new ArrayList<String>(1);
				if (securityType.equals(ICredentialsCommon.SecurityType.HTTPBasic.toString())) {
					authPrefs.add(AuthPolicy.BASIC);
				} else if (securityType.equals(ICredentialsCommon.SecurityType.HTTPDigest.toString())) {
					authPrefs.add(AuthPolicy.DIGEST);
				}
				client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
			}

			if (connProperties.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY) != null) {
				httpget.addRequestHeader(IWSProfileConstants.ACCEPT_PROPERTY_KEY,
						(String) connProperties.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY));
			} else {
				if (responseType.equalsIgnoreCase(IWSProfileConstants.JSON)) {
					httpget.addRequestHeader(IWSProfileConstants.ACCEPT_PROPERTY_KEY,
							IWSProfileConstants.CONTENT_TYPE_JSON_VALUE);
				} else {
					httpget.addRequestHeader(IWSProfileConstants.ACCEPT_PROPERTY_KEY,
							IWSProfileConstants.ACCEPT_DEFAULT_VALUE);
				}
			}

			if (connProperties.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY) != null) {
				httpget.addRequestHeader(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY,
						(String) connProperties.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY));
			} else {
				httpget.addRequestHeader(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY,
						IWSProfileConstants.CONTENT_TYPE_DEFAULT_VALUE);
			}

			int code =0;
			if (secure) {
				client.getParams().setAuthenticationPreemptive(true); 
				httpget.setDoAuthentication(true);
					code = client.executeMethod(httpget);
			} else {
				client.getParams().setAuthenticationPreemptive(false); 
				httpget.setDoAuthentication(false);
				code = client. executeMethod(httpget);
			}
            
            if (code!=200) {
                throw new Exception(DatatoolsUiConstants.UTIL.getString("WSWizardUtils.connectionFailureMessage")); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            return ex;
        }

        return null;
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
		String responseType = IWSProfileConstants.XML;
		if( connProperties != null ) {
			if(  connProperties.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY) != null) {
				responseType = (String)connProperties.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY);
			}
		}

        try {
            URL url = URLHelper.buildURL(xmlFile);
            boolean resolved = false;
            
            // Supply content type
            Map<String,String> connPropMap = new HashMap<String,String>();
            
            if( connProperties.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY) != null ) {
            	connPropMap.put(IWSProfileConstants.ACCEPT_PROPERTY_KEY, (String)connProperties.get(IWSProfileConstants.ACCEPT_PROPERTY_KEY));
			} else {
				if( responseType.equalsIgnoreCase(IWSProfileConstants.JSON) ) {
					connPropMap.put(IWSProfileConstants.ACCEPT_PROPERTY_KEY, IWSProfileConstants.CONTENT_TYPE_JSON_VALUE);
				} else {
					connPropMap.put(IWSProfileConstants.ACCEPT_PROPERTY_KEY, IWSProfileConstants.ACCEPT_DEFAULT_VALUE);
				}
				
			}
			
			if( connProperties.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY) != null ) {
				connPropMap.put(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY, (String)connProperties.get(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY));
			} else {
				connPropMap.put(IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY, IWSProfileConstants.CONTENT_TYPE_DEFAULT_VALUE);
			}
			
			for( Object key : connPropMap.keySet() ) {
				String keyStr = (String)key;
				if( IWSProfileConstants.AUTHORIZATION_KEY.equalsIgnoreCase(keyStr) ||
					ICredentialsCommon.PASSWORD_PROP_ID.equalsIgnoreCase(keyStr) ||
            		ICredentialsCommon.SECURITY_TYPE_ID.equalsIgnoreCase(keyStr) ||
            		ICredentialsCommon.USERNAME_PROP_ID.equalsIgnoreCase(keyStr) ||
            		IWSProfileConstants.END_POINT_URI_PROP_ID.equalsIgnoreCase(keyStr) ||
            		IWSProfileConstants.CONTENT_TYPE_PROPERTY_KEY.equalsIgnoreCase(keyStr) ||
            		IWSProfileConstants.ACCEPT_PROPERTY_KEY.equalsIgnoreCase(keyStr) ) {
            		// do nothing;
            	} else {
            		connPropMap.put(keyStr, connProperties.getProperty(keyStr));
            	}
			}
			
			String userName = null;
			String password = null;
			userName = connProperties.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
            password = connProperties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
	        resolved = URLHelper.resolveUrl(url, userName, password, connPropMap, true);
        
            if (!resolved) {
                throw new Exception(DatatoolsUiConstants.UTIL.getString("WSWizardUtils.connectionFailureMessage")); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            return ex;
        }

        return null;
    }
}

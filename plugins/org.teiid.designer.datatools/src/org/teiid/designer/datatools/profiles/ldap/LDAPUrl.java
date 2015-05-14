/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ldap;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;

/**
 * Retrieve the components of an LDAP URL
 */
public class LDAPUrl implements ILdapProfileConstants, StringConstants {

    private String url;

    private String scheme;

    private String host;

    private int port;

    /**
     * @param url the ldap url
     * @throws Exception if url is not valid
     */
    public LDAPUrl(String url) throws Exception {
        CoreArgCheck.isNotNull(url);
        this.url = url;

        String portString = null;

        if (url.startsWith(LDAPS_SCHEME)) {
            scheme = LDAPS_SCHEME;
            int lastColon = url.lastIndexOf(COLON);
            host = url.substring(LDAPS_SCHEME.length(), lastColon);
            portString = url.substring(lastColon + 1);
        } else if (url.startsWith(LDAP_SCHEME)) {
            scheme = LDAP_SCHEME;
            int lastColon = url.lastIndexOf(COLON);
            host = url.substring(LDAP_SCHEME.length(), lastColon);
            portString = url.substring(lastColon + 1);
        }

        CoreArgCheck.isNotNull(scheme);
        CoreArgCheck.isNotNull(host);
        CoreArgCheck.isNotNull(portString);

        port = Integer.valueOf(portString);
    }

    /**
     * @return the scheme
     */
    public String getScheme() {
        return this.scheme;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.port;
    }

}

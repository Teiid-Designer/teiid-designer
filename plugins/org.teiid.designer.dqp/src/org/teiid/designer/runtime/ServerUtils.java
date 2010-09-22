/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * 
 */
public class ServerUtils {

    private static PluginUtil UTIL = DqpPlugin.Util;
    /**
     * 
     */
    public static final String FORMAT_SERVER = "mm[s]://<hostname>:<port>"; //$NON-NLS-1$
    public static final String DEFAULT_SERVER = "mm://localhost:31443"; //$NON-NLS-1$
    public static final String DEFAULT_SECURE_SERVER = "mms://localhost:31443"; //$NON-NLS-1$

    public static final String MM_PREFIX = "mm://"; //$NON-NLS-1$
    public static final String MMS_PREFIX = "mms://"; //$NON-NLS-1$
    public static final String COLON = ":"; //$NON-NLS-1$
    public static final String COMMA = ","; //$NON-NLS-1$

    public static ServerNameValidator hostNameValidator;

    public ServerUtils getInstance() {
        return this;
    }

    public static void validateServerUrl( String url ) throws IllegalArgumentException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsEmptyOrNullURL")); //$NON-NLS-1$
        }

        if (url.length() < 7) {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsIncompleteUrl", url)); //$NON-NLS-1$
        }

        parseServerURL(url);
    }

    public static void parseServerURL( String serverURL ) throws IllegalArgumentException {
        String leftOver = null;

        if (serverURL.startsWith(MMS_PREFIX)) {
            leftOver = serverURL.substring(6, serverURL.length());
        } else if (serverURL.startsWith(MM_PREFIX)) {
            leftOver = serverURL.substring(5, serverURL.length());
        } else {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsInvalidProtocol")); //$NON-NLS-1$
        }

        StringTokenizer st;
        StringTokenizer st2;

        st = new StringTokenizer(leftOver, COMMA);
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsIncompleteUrl", serverURL)); //$NON-NLS-1$
        }
        while (st.hasMoreTokens()) {
            st2 = new StringTokenizer(st.nextToken(), COLON);
            try {
                String host = st2.nextToken().trim();
                String port = st2.nextToken().trim();
                if (host.equals("")) { //$NON-NLS-1$
                    throw new IllegalArgumentException(UTIL.getString("serverUtilsHostNameCannotBeNull")); //$NON-NLS-1$
                }
                if (!isValidHostName(host)) {
                    throw new IllegalArgumentException(UTIL.getString("serverUtilsHostNameContainsInvalidCharacters")); //$NON-NLS-1$
                }
                int portNumber;
                try {
                    portNumber = Integer.parseInt(port);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(UTIL.getString("serverUtilsPortMustBeNumeric", port)); //$NON-NLS-1$
                }
                if (portNumber < 0 || portNumber > 0xFFFF) {
                    throw new IllegalArgumentException(UTIL.getString("serverUtilsPortOutOfRange", portNumber)); //$NON-NLS-1$
                }
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException(UTIL.getString("serverUtilsIncompleteUrl", serverURL)); //$NON-NLS-1$
            } catch (NullPointerException ne) {
                throw new IllegalArgumentException(UTIL.getString("serverUtilsIncompleteUrl", serverURL)); //$NON-NLS-1$
            }
        }
    }

    public static boolean isValidHostName( String host ) {
        if (hostNameValidator == null) {
            hostNameValidator = new ServerNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                        StringNameValidator.DEFAULT_MAXIMUM_LENGTH, new char[] {';', '@', '#',
                                                            '$', '%', '^', '&', '*', '(', ')', '[', ']', '{', '}', '|', '!', '<',
                                                            '>', '?', '\''});
        }
        return hostNameValidator.isValidName(host);
    }

}

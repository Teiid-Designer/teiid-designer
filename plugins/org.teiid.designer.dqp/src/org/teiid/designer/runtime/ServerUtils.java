/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import org.teiid.net.TeiidURL;

import com.metamatrix.core.PluginUtil;
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


    public ServerUtils getInstance() {
        return this;
    }

    public static void validateServerUrl( String url ) throws IllegalArgumentException {
        new TeiidURL(url);            
    }
    
    /**
     * @param port the port number being validated
     * @return <code>true</code> if the port number is valid
     * @throws IllegalArgumentException if port number is not numeric or out of range
     */
    public static void validPortNumber(String port) {
        int portNumber;

        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsPortMustBeNumeric", port)); //$NON-NLS-1$
        }

        if (portNumber < 0 || portNumber > 0xFFFF) {
            throw new IllegalArgumentException(UTIL.getString("serverUtilsPortOutOfRange", Integer.toString(portNumber))); //$NON-NLS-1$
        }
    }

}

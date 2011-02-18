/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.common.protocol.mmrofile;

import java.net.URL;
import com.metamatrix.common.protocol.mmfile.MMFileURLConnection;

/**
 * Metamatrix's own implementation of the "file:" URL handler. The purpose this handler is to behave the same way as the "mmfile"
 * but, ignore any saves to the permanent stores. Strings are not externalized because of the fact that we have huge dependencies
 * with our plug-in stuff to eclipse.
 * 
 * @since 5.0
 */
public class MMROFileURLConnection extends MMFileURLConnection {

    public static String PROTOCOL = "mmrofile"; //$NON-NLS-1$

    /**
     * @param u - URL to open the connection to
     */
    public MMROFileURLConnection( final URL u ) {
        super(u, true);
    }
}

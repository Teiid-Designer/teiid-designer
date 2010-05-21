/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.protocol.mmfile;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Metamatrix's own implementation of the "file:" URL handler. The reason for a different handler is to support the "output stream"
 * as the sun supplied one does not handle writing to it.
 * 
 * @since 4.4
 */
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection( final URL u ) {
        return new MMFileURLConnection(u);
    }
}

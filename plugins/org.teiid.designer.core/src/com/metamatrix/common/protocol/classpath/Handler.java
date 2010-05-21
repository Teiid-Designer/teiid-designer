/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.protocol.classpath;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * URL Stream Handler class for resources in a given classpath
 * 
 * @since 4.4
 */
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection( final URL u ) {
        return new ClasspathURLConnection(u);
    }
}

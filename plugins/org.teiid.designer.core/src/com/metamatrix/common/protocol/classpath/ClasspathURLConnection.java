/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.common.protocol.classpath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Enumeration;
import com.metamatrix.common.protocol.MMURLConnection;

/**
 * Connection object to a resource in a Classpath. can not use the PluginUtill logging beacuse their dependencies
 * 
 * @since 4.4
 */
public class ClasspathURLConnection extends MMURLConnection {

    public static String PROTOCOL = "classpath"; //$NON-NLS-1$

    /**
     * ctor
     * 
     * @param u - URL to open the connection to
     */
    public ClasspathURLConnection( final URL u ) {
        super(u);
    }

    /**
     * Connect to the supplied URL
     * 
     * @see java.net.URLConnection#connect()
     */
    @Override
    public void connect() throws IOException {
        connected = true;

        final String action = getAction();
        if (action.equals(READ) || action.equals(LIST)) {
            // Check to make sure the resource exists.
            final InputStream in = getResourceAsStream(url);
            if (in == null) throw new FileNotFoundException(url.toString());
            in.close();

            doOutput = false;
            doInput = true;
        } else if (action.equals(WRITE)) {
            doOutput = true;
            doInput = false;
            final String msg = "classpath protocol does not support write. Failed to write to:" + url; //$NON-NLS-1$
            throw new UnknownServiceException(msg);
        } else if (action.equals(DELETE)) {
            final String msg = "classpath protocol does not support delete. Failed to delete:" + url; //$NON-NLS-1$
            throw new UnknownServiceException(msg);
        }
    }

    /**
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) connect();

        // Check if this is request to get list from directory
        if (getAction().equals(LIST)) {

            final ArrayList foundResouces = new ArrayList();
            final Enumeration e = Thread.currentThread().getContextClassLoader().getResources(url.getPath());
            while (e.hasMoreElements()) {
                final URL u = (URL)e.nextElement();
                foundResouces.add(u.toString());
            }
            // Build input stream from the object
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(foundResouces.toArray(new String[foundResouces.size()]));
            oos.close();
            final byte[] content = out.toByteArray();
            out.close();
            return new ByteArrayInputStream(content);
        }

        // This must be read then, see if we can find the resource and
        // return the stream to it.
        InputStream in = null;
        in = getResourceAsStream(url);
        if (in != null) return in;

        final String msg = "Resource not found for reading:" + url; //$NON-NLS-1$
        throw new IOException(msg);
    }

    /**
     * By using the different look up mechanisms look for the resource
     * 
     * @param path
     * @return the input stream if the resource found, null otherwise
     */
    private InputStream getResourceAsStream( final URL pathUrl ) {
        InputStream in;

        String path = pathUrl.getPath();
        if (path.startsWith("/")) path = path.substring(1); //$NON-NLS-1$

        // First look in the thread's context class loader for the resource
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        // then look for the resource in the this classe's class loader
        if (in == null) in = this.getClass().getClassLoader().getResourceAsStream(path);

        // then look in the system class loader
        if (in == null) in = ClassLoader.getSystemResourceAsStream(path);
        return in;
    }

}

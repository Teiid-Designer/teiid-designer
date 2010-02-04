/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.stream.StreamSource;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * StyleFromResource
 */
public class StyleFromResource implements Style {

    private final URL url;
    private final String name;
    private final String description;

    /**
     * Construct an instance of StyleFromResource.
     */
    public StyleFromResource( final URL url,
                              final String name ) {
        this(url, name, null);
    }

    /**
     * Construct an instance of StyleFromResource.
     */
    public StyleFromResource( final URL url,
                              final String name,
                              final String desc ) {
        super();
        ArgCheck.isNotNull(name);
        ArgCheck.isNotZeroLength(name);
        ArgCheck.isNotNull(url);
        this.url = url;
        this.name = name;
        this.description = desc != null ? desc : ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ddl.Style#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ddl.Style#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     */
    public URL getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see Style#getInputStream()
     */
    public InputStream getInputStream() throws IOException, MetaMatrixCoreException {
        // first, try to load them out of a jar
        final URL xsltURL = getUrl();
        InputStream stylesheetStream = xsltURL.openStream();

        if (stylesheetStream == null) {
            // If not found, then create an input stream to an existing file on the file system
            stylesheetStream = new FileInputStream(xsltURL.toExternalForm());
        }

        if (stylesheetStream.available() == 0) {
            final Object[] params = new Object[] {xsltURL};
            final String msg = CoreXsltPlugin.Util.getString("StyleFromResource.empty_xslt", params); //$NON-NLS-1$
            throw new MetaMatrixCoreException(msg);
        }

        return stylesheetStream;
    }

    /* (non-Javadoc)
     * @see Style#getStreamSource()
     */
    public StreamSource getStreamSource() throws IOException, MetaMatrixCoreException {
        final InputStream stream = getInputStream();
        return new StreamSource(stream);
    }

}

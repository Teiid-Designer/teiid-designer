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
import org.teiid.core.TeiidException;
import com.metamatrix.core.util.CoreArgCheck;

/**
 * StyleFromUrlStream is a style that loads the XSLT via the {@link java.net.URL#openStream()}.
 */
public class StyleFromUrlStream implements Style {

    private final String name;
    private final String description;
    private final String xsltUrl;

    /**
     * Construct an instance of StyleFromUrlStream.
     */
    public StyleFromUrlStream( final String name,
                               final String xsltUri ) {
        this(name, xsltUri, null);
    }

    /**
     * Construct an instance of StyleFromUrlStream.
     */
    public StyleFromUrlStream( final String name,
                               final String xsltUri,
                               final String desc ) {
        super();
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotZeroLength(name);
        CoreArgCheck.isNotNull(xsltUri);
        CoreArgCheck.isNotZeroLength(xsltUri);
        CoreArgCheck.isNotNull(xsltUri);
        this.name = name;
        this.xsltUrl = xsltUri;
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
    public String getXsltUrl() {
        return xsltUrl;
    }

    /* (non-Javadoc)
     * @see Style#getInputStream()
     */
    public InputStream getInputStream() throws IOException, TeiidException {
        // first, try to load them out of a jar
        final String xsltURL = getXsltUrl();
        final URL url = new URL(this.xsltUrl);
        InputStream stylesheetStream = url.openStream();

        if (stylesheetStream == null) {
            // If not found, then create an input stream to an existing file on the file system
            stylesheetStream = new FileInputStream(xsltURL);
        }

        if (stylesheetStream.available() == 0) {
            final Object[] params = new Object[] {xsltURL};
            final String msg = CoreXsltPlugin.Util.getString("StyleFromUrlStream.empty_xslt", params); //$NON-NLS-1$
            throw new TeiidException(msg);
        }

        return stylesheetStream;
    }

    /* (non-Javadoc)
     * @see Style#getStreamSource()
     */
    public StreamSource getStreamSource() throws IOException, TeiidException {
        final InputStream stream = getInputStream();
        return new StreamSource(stream);
    }

}

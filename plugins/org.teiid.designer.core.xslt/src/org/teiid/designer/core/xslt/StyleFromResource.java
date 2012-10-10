/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.xslt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.stream.StreamSource;
import org.teiid.core.TeiidException;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 * StyleFromResource
 *
 * @since 8.0
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
        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotZeroLength(name);
        CoreArgCheck.isNotNull(url);
        this.url = url;
        this.name = name;
        this.description = desc != null ? desc : ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ddl.Style#getName()
     */
    @Override
	public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ddl.Style#getDescription()
     */
    @Override
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
    @Override
	public InputStream getInputStream() throws IOException, TeiidException {
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
            throw new TeiidException(msg);
        }

        return stylesheetStream;
    }

    /* (non-Javadoc)
     * @see Style#getStreamSource()
     */
    @Override
	public StreamSource getStreamSource() throws IOException, TeiidException {
        final InputStream stream = getInputStream();
        return new StreamSource(stream);
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.xslt;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import org.teiid.core.designer.TeiidDesignerException;

/**
 * Style
 *
 * @since 8.0
 */
public interface Style {
    
    public String getName();
    
    public String getDescription();

    public InputStream getInputStream() throws IOException, TeiidDesignerException;

    public StreamSource getStreamSource() throws IOException, TeiidDesignerException;
}

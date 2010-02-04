/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import com.metamatrix.core.MetaMatrixCoreException;

/**
 * Style
 */
public interface Style {
    
    public String getName();
    
    public String getDescription();

    public InputStream getInputStream() throws IOException, MetaMatrixCoreException;

    public StreamSource getStreamSource() throws IOException, MetaMatrixCoreException;
}

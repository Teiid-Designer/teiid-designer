/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.core.xslt;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;

import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.util.ArgCheck;

public class XsltTransform {

	private final Style style;

	/**
	 * Create an instance by specifying the TemplatesManager from which the
	 * Templates (and thus the Transformer instances) are to be obtained,
	 * and the URI of the XSLT stylesheet.
	 * @param manager the manager of the Templates; may not be null;
	 * @param xsltURI the URI of the stylesheet to be used in the transformation;
	 * may not be null or zero length
	 */
	public XsltTransform( final Style style) {
		ArgCheck.isNotNull(style);
		this.style = style;
	}
    
    protected static Templates getTemplates( final Style style ) throws IOException, MetaMatrixCoreException,
                                                                        TransformerConfigurationException {
        ArgCheck.isNotNull(style);
        // Create a source for the stylesheet ...
        final StreamSource source = style.getStreamSource();
        
        // Load the templates ...
        final Templates templates = CoreXsltPlugin.createFactory().newTemplates(source);
        if ( templates == null ) {
            throw new TransformerConfigurationException(CoreXsltPlugin.Util.getString("XsltTransform.TransformerFactory_created_a_null_Templates_object")); //$NON-NLS-1$
        }
        return templates;
    }

    /**
     * Transform the supplied source document (or fragment of the source document, if the URI of
     * a fragment root is specified) using the XSLT.
     * @param sourceDoc the source document; may not be null
     * @param baseUriOfRootNode the relative URI of the node in the source document that is to be considered
     * as the root of the fragment to be transformed; may be null if the whole document is to be transformed.
     * @param output the stream to which the transformed 
     */
    protected Transformer createTransformer() throws MetaMatrixCoreException, TransformerConfigurationException, IOException  {

        // Obtain the Templates object (precompiled stylesheet)
        final Templates templates = getTemplates(this.style);   // may throw exception

        // Create the Transformer object
        final Transformer transformer = templates.newTransformer();
        
        return transformer;
    }

    /**
     * Transform the supplied source document using the XSLT.
     * @param sourceDoc the source document; may not be null
     * @param output the OutputStream to which the transformed content is to be written
     */
    public void transform(final Document sourceDoc, final OutputStream output) throws IOException, 
                                                            MetaMatrixCoreException,
                                                            TransformerException,
                                                            TransformerConfigurationException {
    	ArgCheck.isNotNull(sourceDoc);
        ArgCheck.isNotNull(output);
        final Source source = CoreXsltPlugin.createSource(sourceDoc);
        final StreamResult result = new StreamResult(output);
        
        // Transform the document in 'transformSource'
        final Transformer transformer = this.createTransformer();
        try {
            // Feed the resultant I/O stream into the XSLT processor
            transformer.transform(source, result);
        } catch ( Throwable e ) {
            final String msg = CoreXsltPlugin.Util.getString("XsltTransform.Error_applying_the_XSLT_transform"); //$NON-NLS-1$
            throw new MetaMatrixCoreException(e,msg);
        }
    }

}

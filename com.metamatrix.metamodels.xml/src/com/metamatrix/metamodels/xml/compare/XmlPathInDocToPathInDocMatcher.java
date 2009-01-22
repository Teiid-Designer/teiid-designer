/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.compare;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;

/** 
 * XmlPathInDocToPathInDocMatcher
 */
public class XmlPathInDocToPathInDocMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of XmlPathInDocToPathInDocMatcher.
     * 
     */
    public XmlPathInDocToPathInDocMatcher() {
        super();
    }
    
    /** 
     * @see com.metamatrix.metamodels.xml.compare.AbstractXmlEntityMatcher#getInputKey(com.metamatrix.metamodels.xml.XmlDocumentEntity)
     * @since 4.2
     */
    @Override
    protected String getInputKey(final EObject entity) {
        if(entity instanceof XmlDocumentEntity) {
            if(!(entity instanceof XmlDocument)) {
                return ((XmlDocumentEntity)entity).getPathInDocument();
            }
        }
        return null;
    }

    /** 
     * @see com.metamatrix.metamodels.xml.compare.AbstractXmlEntityMatcher#getOutputKey(com.metamatrix.metamodels.xml.XmlDocumentEntity)
     * @since 4.2
     */
    @Override
    protected String getOutputKey(final EObject entity) {
        if(entity instanceof XmlDocumentEntity) {
            if(!(entity instanceof XmlDocument)) {
                return ((XmlDocumentEntity)entity).getPathInDocument();
            }
        }
        return null;
    }

}

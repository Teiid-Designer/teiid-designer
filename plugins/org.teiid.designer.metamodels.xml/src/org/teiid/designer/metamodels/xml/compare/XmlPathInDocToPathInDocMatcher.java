/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.compare;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.compare.AbstractEObjectNameMatcher;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlDocumentEntity;


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
     * @see org.teiid.designer.metamodels.xml.compare.AbstractXmlEntityMatcher#getInputKey(org.teiid.designer.metamodels.xml.XmlDocumentEntity)
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
     * @see org.teiid.designer.metamodels.xml.compare.AbstractXmlEntityMatcher#getOutputKey(org.teiid.designer.metamodels.xml.XmlDocumentEntity)
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

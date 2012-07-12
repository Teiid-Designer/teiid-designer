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
 * @since 4.2
 */
public class XmlXPathToXPathIgnoreCaseMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of XmlXPathToXPathIgnoreCaseMatcher.
     * 
     */
    public XmlXPathToXPathIgnoreCaseMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof XmlDocumentEntity) {
            if(!(entity instanceof XmlDocument)) {
	            final String name = ((XmlDocumentEntity)entity).getXPath();
	            if(name != null) {
	                return name.toUpperCase();
	            }
            }
        }
        return null;
    }

    @Override
    protected String getOutputKey( final EObject entity ) {
        if(entity instanceof XmlDocumentEntity) {
            if(!(entity instanceof XmlDocument)) {
	            final String name = ((XmlDocumentEntity)entity).getXPath();
	            if(name != null) {
	                return name.toUpperCase();
	            }
            }
        }
        return null;
    }

}

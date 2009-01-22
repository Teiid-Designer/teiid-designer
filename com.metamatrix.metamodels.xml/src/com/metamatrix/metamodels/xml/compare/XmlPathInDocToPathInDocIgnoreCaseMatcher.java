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
 * XmlPathInDocToPathInDocIgnoreCaseMatcher
 */
public class XmlPathInDocToPathInDocIgnoreCaseMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of XmlPathInDocToPathInDocIgnoreCaseMatcher.
     * 
     */
    public XmlPathInDocToPathInDocIgnoreCaseMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof XmlDocumentEntity) {
            if(!(entity instanceof XmlDocument)) {
	            final String name = ((XmlDocumentEntity)entity).getPathInDocument();
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
	            final String name = ((XmlDocumentEntity)entity).getPathInDocument();
	            if(name != null) {
	                return name.toUpperCase();
	            }
            }
        }
        return null;
    }

}

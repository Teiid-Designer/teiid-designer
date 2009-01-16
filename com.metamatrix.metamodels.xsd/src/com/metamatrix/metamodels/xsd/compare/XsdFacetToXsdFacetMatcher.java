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

package com.metamatrix.metamodels.xsd.compare;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.xsd.XSDFacet;

import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;


/** 
 * @since 4.2
 */
public class XsdFacetToXsdFacetMatcher extends AbstractEObjectNameMatcher {

    /** 
     * 
     * @since 4.2
     */
    public XsdFacetToXsdFacetMatcher() {
        super();
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher#getInputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getInputKey(EObject entity) {
        if(entity instanceof XSDFacet) {
            XSDFacet facet = (XSDFacet)entity;
            StringBuffer val = new StringBuffer(facet.getFacetName() );
            if(facet.getLexicalValue() != null) {
                val.append(facet.getLexicalValue() );
            }
            return val.toString();
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher#getOutputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getOutputKey(EObject entity) {
        if(entity instanceof XSDFacet) {
            XSDFacet facet = (XSDFacet)entity;
            StringBuffer val = new StringBuffer(facet.getFacetName() );
            if(facet.getLexicalValue() != null) {
                val.append(facet.getLexicalValue() );
            }
            return val.toString();
        }
        return null;
    }

}

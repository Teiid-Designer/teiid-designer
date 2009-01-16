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

package com.metamatrix.modeler.xsd.util;

import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.emf.ecore.EObject;


/** 
 * @since 5.0.2
 */
public class ModelerXsdUtils {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Indicates if the specified object is an <code>XSDSimpleTypeDefinition</code> enumerated type.
     * @param theObject the object being checked
     * @return <code>true</code> if an enumerated type; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public static boolean isEnumeratedType(EObject theObject) {
        boolean result = false;
        
        if (theObject instanceof XSDSimpleTypeDefinition) {
            result = !((XSDSimpleTypeDefinition)theObject).getEnumerationFacets().isEmpty();
        }
        
        return result;
    }
    
    /**
     * Indicates if the specified object is an <code>XSDEnumerationFacet</code> which is a value of an enumerated type.
     * @param theObject the object being checked
     * @return <code>true</code> if an enumerated type value; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public static boolean isEnumeratedTypeValue(EObject theObject) {
        return (theObject instanceof XSDEnumerationFacet);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Don't allow construction */
    private ModelerXsdUtils() {}

}

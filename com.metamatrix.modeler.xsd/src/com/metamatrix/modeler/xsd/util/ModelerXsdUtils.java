/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

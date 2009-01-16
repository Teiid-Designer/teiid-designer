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

package com.metamatrix.metamodels.transformation.util;

import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.ArgCheck;

/**
 * RelationalUtil
 */
public class TransformationUtil {
    
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String NAME_FEATURE_NAME = "name"; //$NON-NLS-1$

    /**
     * Prevent allocation
     */
    private TransformationUtil() {
        super();
    }
    
    /**
     * Get the "name" of the specified {@link org.eclipse.emf.ecore.EObject} instance, if
     * the associated {@link EClass} has a "name" feature.
     * @param eObject the EObject for which the name is to be found
     * @return the name value, if there is one, or an emtpy string if 
     * there is no name feature
     */
    public static String getName(final EObject eObject) {
        ArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = getNameFeature(eObject);
        if ( nameFeature == null ) {
            return EMPTY_STRING;
        }
        final Object value = eObject.eGet(nameFeature);
        return value != null ?
               value.toString() :
               EMPTY_STRING;
    }
    
    /**
     * This method currently looks for a feature with a name that case-insensitively matches "name".
     * @see com.metamatrix.modeler.core.ModelEditor#getNameFeature(org.eclipse.emf.ecore.EObject)
     */
    public static EStructuralFeature getNameFeature(final EObject eObject) {
        ArgCheck.isNotNull(eObject);
        final EClass eClass = eObject.eClass();
        for (Iterator iter = eClass.getEAllStructuralFeatures().iterator(); iter.hasNext();) {
            final EStructuralFeature feature = (EStructuralFeature)iter.next();
            if (NAME_FEATURE_NAME.equalsIgnoreCase(feature.getName())) {
                return feature;
            }
        }
        return null;
    }
    

}

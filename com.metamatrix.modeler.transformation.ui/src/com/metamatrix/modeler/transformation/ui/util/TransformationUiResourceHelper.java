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

package com.metamatrix.modeler.transformation.ui.util;

import org.eclipse.core.resources.IResource;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;


/** This is a utility class that UI components can use to determine if an object resides within a specific Transformation resource
 * @since 5.0
 */
public class TransformationUiResourceHelper {

    /**
     * 
     * @param object - Valid input objects are <code>IResource</code>, <code>ModelResource</code> or <code>EObject</code>
     * @return true if the underlying resource is VIRTUAL and is NOT of type XML Service View, else false
     * @since 5.0
     */
    public static boolean isSqlTransformationResource(Object object) {
        
        boolean result = false;
        
        if( object instanceof IResource ) {
            IResource iResource = (IResource)object;
            if( ModelIdentifier.isVirtualModelType(iResource) && !ModelIdentifier.isXmlServiceViewModel(iResource) ) {
                result = true;
            }
        } else if( object instanceof ModelResource ) {
            ModelResource mr = (ModelResource)object;
            if( ModelIdentifier.isVirtualModelType(mr) && !ModelIdentifier.isXmlServiceViewModel(mr) ) {
                result = true;
            }
        } else if( object instanceof EObject ) {
            EObject eObj = (EObject)object;
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObj);
            if( mr != null ) {
                if( ModelIdentifier.isVirtualModelType(mr) && !ModelIdentifier.isXmlServiceViewModel(mr) ) {
                    result = true;
                }
            }
        }
        
        return result;
    }
    
    /**
     *  
     * @param object - Valid input objects are <code>IResource</code>, <code>ModelResource</code> or <code>EObject</code>
     * @return true if the underlying resource is VIRTUAL and IS of type XML Service View, else false
     * @since 5.0
     */
    public static boolean isXQueryTransformationResource(Object object) {
        
        boolean result = false;
        
        if( object instanceof IResource ) {
            IResource iResource = (IResource)object;
            if( ModelIdentifier.isVirtualModelType(iResource) && ModelIdentifier.isXmlServiceViewModel(iResource) ) {
                result = true;
            }
        } else if( object instanceof ModelResource ) {
            ModelResource mr = (ModelResource)object;
            if( ModelIdentifier.isVirtualModelType(mr) && ModelIdentifier.isXmlServiceViewModel(mr) ) {
                result = true;
            }
        } else if( object instanceof EObject ) {
            EObject eObj = (EObject)object;
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObj);
            if( mr != null ) {
                if( ModelIdentifier.isVirtualModelType(mr) && ModelIdentifier.isXmlServiceViewModel(mr) ) {
                    result = true;
                }
            }
        }
        
        return result;
    }

}

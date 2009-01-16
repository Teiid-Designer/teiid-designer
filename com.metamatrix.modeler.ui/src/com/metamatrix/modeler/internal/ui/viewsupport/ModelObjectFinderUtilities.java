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

package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.ui.UiConstants;


/** 
 * @since 4.2
 */
public abstract class ModelObjectFinderUtilities implements UiConstants {
    private static final char SLASH = '/';

    public static EObject findModelObject(final ModelResource modelResource, final String path, final String name) {
        EObject result = null;
        try {
            result = ModelerCore.getModelEditor().findObjectByPath(modelResource, new Path(path + SLASH + name));
        } catch( ModelWorkspaceException mwe ) {
            UiConstants.Util.log(IStatus.ERROR, mwe, null);
        }
        
        return result;
    }
    
    public static EObject findModelObject(final ModelResource modelResource, final String fullPath) {
        EObject result = null;
        try {
            result = ModelerCore.getModelEditor().findObjectByPath(modelResource, new Path(fullPath));
        } catch( ModelWorkspaceException mwe ) {
            UiConstants.Util.log(IStatus.ERROR, mwe, null);
        }
        
        return result;
    }
    
    public static EObject findModelObject(final Resource resource, final String path, final String name) {     
        return ModelerCore.getModelEditor().findObjectByPath(resource, new Path(path + SLASH + name));
    }
    
    public static EObject findModelObject(final Resource resource, final String fullPath) {     
        return ModelerCore.getModelEditor().findObjectByPath(resource, new Path(fullPath));
    }

}

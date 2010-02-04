/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

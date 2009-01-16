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

package com.metamatrix.modeler.core.util;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;


/** 
 * @since 5.0
 */
public class ImportUtilities {
    

    /**
     * Method to add an import for an external resource to a resource. If an import already exists for the external resource, the 
     * method returns FALSE. If an import is missing, one is created and TRUE is returned.
     * @param resource
     * @param externalResource
     * @return wasAdded TRUE if import was created, FALSE if not.
     * @since 5.0
     */
    public static boolean addMissingImport(final Resource resource, final Resource externalResource) {
        // Note:  Some model builders utilize transient resources, which would result in bogus imports after the merge processor
        // is completed. SO, for now, we'll check for isRelative() and NOT add missing imports for these cases.  The MergeProcessorImpl
        // will STILL do it's own rebuildImports().
        if (externalResource == null || resource.getURI().isRelative() || externalResource.getURI().isRelative()) {
            return false;
        }
        
        boolean wasAdded = false;
        if( resource instanceof EmfResource ) {
            ModelImport existingImport = ModelerCore.getModelEditor().findModelImport((EmfResource)resource, externalResource);
            if( existingImport == null ) {
                try {
                    wasAdded = ModelerCore.getModelEditor().addModelImport((EmfResource)resource, externalResource);
                    if( wasAdded ) {
                        addMissingDependentImports(resource, externalResource);
                    }
                }  catch (ModelerCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }
        
        return wasAdded;
    }
    
    private static void addMissingDependentImports(final Resource resource, Resource externalResource) {
        ModelResource depResource = null;
        if( externalResource instanceof XSDResourceImpl && ((XSDResourceImpl)externalResource).getSchema() != null ) {
            //      Get all the content  
            Iterator iter = ((XSDResourceImpl)externalResource).getSchema().getContents().iterator();
            while( iter.hasNext()) {
                
                // For each of the EObjects, get the import Aspect
                EObject eobject = (EObject) iter.next();
                
                // Skip annotations
                if (eobject instanceof XSDAnnotation) {
                    continue;
                }
                
                // Break if named component found since all imports, includes, and redefines must appear before these
                if (eobject instanceof XSDNamedComponent) {
                    break;
                }
                
                ImportsAspect importsAspect = AspectManager.getModelImportsAspect(eobject);
                
                if (importsAspect != null) {
                    
                    // If imports Aspect is not null, get the import path                    
                    IPath importPath = importsAspect.getModelPath(eobject);

                    if( importPath != null ) {
                        depResource = ModelerCore.getModelWorkspace().findModelResource(importPath);
                    }
                }
            }
        }
        if( depResource != null ) {
            try {
                addMissingImport(resource, depResource.getEmfResource());
            }  catch (ModelerCoreException e) {
                ModelerCore.Util.log(e);
            }
        }
    }
    
    /**
     *  
     * @param resource
     * @param externalResource
     * @return
     * @since 5.0
     */
    public static boolean removeExistingImport(final Resource resource, final Resource externalResource) {
        // Note:  Some model builders utilize transient resources, which would result in bogus imports after the merge processor
        // is completed. SO, for now, we'll check for isRelative() and NOT add missing imports for these cases.  The MergeProcessorImpl
        // will STILL do it's own rebuildImports().
        if (externalResource == null || resource.getURI().isRelative() || externalResource.getURI().isRelative()) {
            return false;
        }
        
        boolean wasRemoved = false;
        if( resource instanceof EmfResource ) {
            ModelImport existingImport = ModelerCore.getModelEditor().findModelImport((EmfResource)resource, externalResource);
            if( existingImport != null ) {
                try {
                    ModelAnnotation modelWithImports = ((EmfResource)resource).getModelAnnotation();
                    if (modelWithImports != null) {
                        ModelerCore.getModelEditor().removeValue(modelWithImports, existingImport, modelWithImports.getModelImports() );
                        wasRemoved = true;
                    }
                    
                    if( wasRemoved ) {
                        addMissingDependentImports(resource, externalResource);
                    }
                }  catch (ModelerCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }
        
        return wasRemoved;
    }
    
    /**
     * Method to add missing imports to an EmfResource given a target object. The target object can be either an EObject, Resource,
     * ModelResource or a list of any of them .  In all cases, the method will recursively walk the contents of the object looking
     * for external referenced resources. If any are detected, the method looks for existing imports for the resources and adds them
     * if they are missing.
     * NOTE:  This method makes no checks for read-only state of resource. 
     * @param target
     * @since 5.0
     */
    public static boolean addMissingImports(final Resource notifyingResource, final Object target) {
        Collection externalResources = ExternalResourceImportsHelper.findExternalResourceReferences(notifyingResource, target);
        boolean result = false;
        
        if( ! externalResources.isEmpty() ) {
            Resource targetResource = null;
            
            if( target instanceof EObject ) {
                targetResource = ((EObject)target).eResource();
            } else if( target instanceof Resource ) {
                targetResource = (Resource)target;
            } else if( target instanceof ModelResource ) {
                try {
                    targetResource = ((ModelResource)target).getEmfResource();
                } catch (ModelWorkspaceException theException) {
                    ModelerCore.Util.log(IStatus.ERROR, theException, theException.getLocalizedMessage());
                }
            } 
            
            boolean importWasAdded = false;
            // Now add any missing imports on each of the 
            if( targetResource != null && targetResource instanceof EmfResource ) {
                for( Iterator iter = externalResources.iterator(); iter.hasNext(); ) {
                    importWasAdded = addMissingImport(targetResource, (Resource)iter.next());
                    if( importWasAdded) {
                        result = true;
                    }
                }
            }
        }
        
        return result;
    }
}

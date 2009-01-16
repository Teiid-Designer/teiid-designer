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

package com.metamatrix.modeler.core.refactor;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * OrganizeImportCommandHelperXsd 
 * @since 4.3
 */
public class OrganizeImportCommandHelperXsd extends OrganizeImportCommandHelper{
        
    protected OrganizeImportCommandHelperXsd() {        
        super();
    }
    
    /**
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus execute( final IProgressMonitor monitor) {
        return process(monitor);
    }
    
    /**
     *  
     * @return Iterator
     * @since 4.3
     */
    private Iterator getResourceSchemaContents() {
        
        if( ((XSDResourceImpl)this.getResource()).getSchema() != null ) {
            return ((XSDResourceImpl)this.getResource()).getSchema().getContents().iterator();
        }
        
        return Collections.EMPTY_LIST.iterator();
    }
    
    /**
     *  
     * @param monitor
     * @return IStatus
     * @since 4.3
     */
    private IStatus process(IProgressMonitor monitor) {

        //      Get all the content            
        for (Iterator iter = getResourceSchemaContents(); iter.hasNext();) {
            
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
//              MyDefect : Refactored for defect 17255
                IPath importPath = this.getHelper().findPath(eobject, importsAspect);
                
//              MyDefect : 16368 updated and added new code.
                if(importPath != null) {
                    ModelResource oldResource = ModelerCore.getModelWorkspace().findModelResource(importPath);
                
                    if(oldResource != null) {
                        try {                                                                                       
                            oldResource.unload();
                            oldResource.close();                            
                            // create a new model resource for the resource at the new path
                            IFile newIFile = createAndSaveResource(monitor, importPath);
                            if (newIFile != null) {
                                importsAspect.setModelLocation(eobject, URI.createFileURI(newIFile.getLocation().toFile().getAbsolutePath()));
                                //importsAspect.setModelPath(eobject, importPath);                                 
                            }
                            
                            this.getResource().setModified(true);                                
                        } catch (ModelWorkspaceException e) {
                            final Object[] params = new Object[]{this.getResource().getURI(),e.getLocalizedMessage()};
                            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Error_while_organizing_imports",params); //$NON-NLS-1$
                            return new Status(IStatus.ERROR, PID, ERROR_ORGANIZING_IMPORTS, msg, e);
                        }
                    }
                }
            }
        }
        
        // Return the result with sucess
        return new Status(IStatus.OK,PLUGINID,EXECUTE_WITH_NO_PROBLEMS,ModelerCore.Util.getString("OrganizeImportCommand.complete"),null); //$NON-NLS-1$        
    }
 
    /**
     *  
     * @param monitor
     * @param importPath
     * @throws ModelWorkspaceException
     * @since 4.3
     */
    private IFile createAndSaveResource(IProgressMonitor monitor, IPath importPath) throws ModelWorkspaceException {
        // Create a new model resource for the resource at the new path
        IFile tmpFile = (IFile) WorkspaceResourceFinderUtil.findIResourceByPath(importPath);
        if (tmpFile != null) {
            ModelResource newResource = ModelerCore.create(tmpFile);
            newResource.save(monitor, true);
        }
        return tmpFile;
    }        
}

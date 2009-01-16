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

package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbGenerationContextParameters;

/** 
 * @since 4.2
 */
public class VdbGenerationContextImpl implements InternalVdbGenerationContext {

    private final VdbGenerationModelObjectHelper modelObjectHelper;
    private final VdbGenerationModelHelper modelHelper;
    private final Resource[] models;
    private final List problems;
    private final Map artifactsByPath;
    private final Map modelNameByResource;
    private final Map workspacePathByResource;
    private final Map modelVisibilityByResource;
    private final Set newArtifactsByCaseInsensitivePath;
    private final Set existingPathsInVdb;
    private final Set existingUpperCasePathsInVdb;
    private final String tempFolderAbsolutePath;
    private String displayMessage;
    
    private VdbContextImpl vdbContext;
    private Map dataMap;
    private ResourceSet resourceSet;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /** 
     * @since 5.0
     */
    public VdbGenerationContextImpl( final VdbGenerationContextParameters parameters ) {
        ArgCheck.isNotNull(parameters);
        
        this.modelObjectHelper = new VdbGenerationModelObjectHelper(parameters.getProblemsByObjectId());
        this.models = parameters.getModels();
        this.problems = new ArrayList();
        this.artifactsByPath = new HashMap();
        this.modelNameByResource = parameters.getModelNameByResource();
        this.workspacePathByResource = parameters.getWorkspacePathByResource();
        this.modelVisibilityByResource = parameters.getModelVisibilityByResource();
        this.modelHelper = new VdbGenerationModelHelper(this.modelNameByResource,this.workspacePathByResource,this.modelVisibilityByResource);
        this.newArtifactsByCaseInsensitivePath = new HashSet();
        this.tempFolderAbsolutePath = parameters.getTempFolderAbsolutePath();
        
        this.dataMap = new HashMap();
        
        this.existingPathsInVdb = new HashSet();
        this.existingUpperCasePathsInVdb = new HashSet();
        
        final IPath[] paths = parameters.getExistingPathsInVdb();
        if ( paths != null ) {
            for (int i = 0; i < paths.length; i++) {
                final IPath path = paths[i];
                if ( path != null ) {
                    final String thePath = path.makeAbsolute().toString();
                    this.existingPathsInVdb.add(path.toString());
                    this.existingUpperCasePathsInVdb.add(thePath.toUpperCase());
                    this.existingUpperCasePathsInVdb.add(path.toString().toUpperCase());
                }
            }
        }
    }
    
    /** 
     * @since 4.2
     */
    public VdbGenerationContextImpl( final Resource[] models, final IProgressMonitor monitor, final IPath[] existingPathsInVdb,
                                     final Map modelNameByResource, final Map workspacePathByResource, 
                                     final Map modelVisibilityByResource, final Map problemsByObjectId,
                                     final String tempFolderAbsolutePath  ) {
        super();
        ArgCheck.isNotNull(models);
        ArgCheck.isNotNull(modelNameByResource);
        ArgCheck.isNotNull(workspacePathByResource);
        ArgCheck.isNotNull(modelVisibilityByResource);
        ArgCheck.isNotNull(problemsByObjectId);
        this.modelObjectHelper = new VdbGenerationModelObjectHelper(problemsByObjectId);
        this.models = models;
        this.problems = new ArrayList();
        this.artifactsByPath = new HashMap();
        this.modelNameByResource = new HashMap(modelNameByResource);
        this.workspacePathByResource = new HashMap(workspacePathByResource);
        this.modelVisibilityByResource = new HashMap(modelVisibilityByResource);
        this.modelHelper = new VdbGenerationModelHelper(this.modelNameByResource,this.workspacePathByResource,this.modelVisibilityByResource);
        this.newArtifactsByCaseInsensitivePath = new HashSet();
        this.tempFolderAbsolutePath = tempFolderAbsolutePath;
        
        this.existingPathsInVdb = new HashSet();
        this.existingUpperCasePathsInVdb = new HashSet();
        if ( existingPathsInVdb != null ) {
            for (int i = 0; i < existingPathsInVdb.length; i++) {
                final IPath path = existingPathsInVdb[i];
                if ( path != null ) {
                    final String thePath = path.makeAbsolute().toString();
                    this.existingPathsInVdb.add(path.toString());
                    this.existingUpperCasePathsInVdb.add(thePath.toUpperCase());
                    this.existingUpperCasePathsInVdb.add(path.toString().toUpperCase());
                }
            }
        }
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================
    
    public Set getExistingPathsInVdb() {
        return this.existingPathsInVdb;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getProblems()
     * @since 5.0
     */
    public List getProblems() {
        return this.problems;
    }
    
    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getTemporaryDirectory()
     * @since 4.2
     */
    public File getTemporaryDirectory() {
        if ( this.tempFolderAbsolutePath != null ) {
            return new File(this.tempFolderAbsolutePath);
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getGeneratedArtifactsByPath()
     * @since 4.2
     */
    public Map getGeneratedArtifactsByPath() {
        return new HashMap(this.artifactsByPath);   // return copy
    }
    
    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getModels()
     * @since 4.2
     */
    public Resource[] getModels() {
        return models;
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getModels(java.lang.String)
     * @since 4.2
     */
    public Resource[] getModels(final String primaryMetamodelUri) {
        ArgCheck.isNotNull(primaryMetamodelUri);
        
        final List results = new ArrayList();
        // The primary metamodel is non-null and non-trivial ...
        for (int i = 0; i < this.models.length; i++) {
            final Resource model = this.models[i];
            final String metamodelUri = getPrimaryMetamodel(model); // may be null!
            if ( primaryMetamodelUri.equals(metamodelUri) ) {
                results.add(model);
            }
        }
        
        // Convert the result list to an array ...
        return (Resource[])results.toArray(new Resource[results.size()]);
    }
    
    protected String getPrimaryMetamodel( final Resource model ) {
        return this.modelHelper.getPrimaryMetamodelUri(model);
    }
    
    
    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getModelHelper()
     * @since 4.2
     */
    public ModelHelper getModelHelper() {
        return this.modelHelper;
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getObjectHelper()
     * @since 4.2
     */
    public ModelObjectHelper getObjectHelper() {
        return this.modelObjectHelper;
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addErrorMessage(java.lang.String, int, java.lang.Throwable)
     * @since 4.2
     */
    public void addErrorMessage(final String message, final int code, final Throwable t) {
        if ( message != null ) {
            final IStatus status = new Status(IStatus.ERROR,VdbEditPlugin.PLUGIN_ID,code,message,t);
            this.problems.add(status);
        }
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addWarningMessage(java.lang.String, int)
     * @since 4.2
     */
    public void addWarningMessage(final String message, final int code) {
        if ( message != null ) {
            final IStatus status = new Status(IStatus.WARNING,VdbEditPlugin.PLUGIN_ID,code,message,null);
            this.problems.add(status);
        }
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addInfoMessage(java.lang.String, int)
     * @since 4.2
     */
    public void addInfoMessage(final String message, final int code) {
        if ( message != null ) {
            final IStatus status = new Status(IStatus.INFO,VdbEditPlugin.PLUGIN_ID,code,message,null);
            this.problems.add(status);
        }
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#setProgressMessage(java.lang.String)
     * @since 4.2
     */
    public void setProgressMessage(final String displayableMessage) {
        this.displayMessage = displayableMessage;
        // RMH 12/15/04 - Can't do this on this thread ...
        //if ( displayableMessage != null && this.monitor != null ) {
        //   this.monitor.setTaskName(displayableMessage);
        //}
    }
    
    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#getProgressMessage()
     * @since 5.0
     */
    public String getProgressMessage() {
        return this.displayMessage;
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addGeneratedArtifact(java.lang.String, java.lang.String)
     * @since 4.2
     */
    public boolean addGeneratedArtifact(final String pathInVdb, final String content) {
        ArgCheck.isNotZeroLength(pathInVdb);
        return doAddGeneratedArtifact(pathInVdb,content);
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addGeneratedArtifact(java.lang.String, org.jdom.Document)
     * @since 4.2
     */
    public boolean addGeneratedArtifact(final String pathInVdb, final Document xmlContent) {
        ArgCheck.isNotZeroLength(pathInVdb);
        return doAddGeneratedArtifact(pathInVdb,xmlContent);
    }

    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addGeneratedArtifact(java.lang.String, java.io.InputStream)
     * @since 4.2
     */
    public boolean addGeneratedArtifact(final String pathInVdb, final InputStream content) {
        ArgCheck.isNotZeroLength(pathInVdb);
        return doAddGeneratedArtifact(pathInVdb,content);
    }
    
    /** 
     * @see com.metamatrix.vdb.edit.VdbGenerationContext#addGeneratedArtifact(java.lang.String, java.io.File)
     * @since 4.2
     */
    public boolean addGeneratedArtifact(final String pathInVdb, final File content) {
        ArgCheck.isNotZeroLength(pathInVdb);
        return doAddGeneratedArtifact(pathInVdb,content);
    }
    
    protected boolean doAddGeneratedArtifact(final String pathInVdb, final Object content) {
        synchronized(this.artifactsByPath) {
            // Make sure it's not a pre-existing artifact ...
            final IPath path = new Path(pathInVdb).makeAbsolute();
            final String thePath = path.toString();
            if ( this.existingUpperCasePathsInVdb.contains(thePath.toUpperCase()) ) {
                return false;
            }
            
            // Make sure it's not a generated artifact artifact ...
            if ( !this.newArtifactsByCaseInsensitivePath.contains(thePath.toUpperCase()) ) {
                // Not there, so add it ...
                this.artifactsByPath.put(thePath,content);
                this.newArtifactsByCaseInsensitivePath.add(thePath.toUpperCase());
                return true;
            }
        }
        return false;
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    public void dispose() {
        try {
            this.problems.clear();
            this.artifactsByPath.clear();
            this.modelNameByResource.clear();
            this.workspacePathByResource.clear();
            this.modelVisibilityByResource.clear();
            this.newArtifactsByCaseInsensitivePath.clear();
            this.existingPathsInVdb.clear();
            this.existingUpperCasePathsInVdb.clear();
            this.dataMap.clear();
        } finally {
//            this.modelObjectHelper = null;
//            this.modelHelper = null;
//            this.models = null;
//            this.tempFolderAbsolutePath = null;
            this.displayMessage = null;
            this.vdbContext = null;
            this.resourceSet = null;
        }
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#getData(java.lang.String)
     * @since 5.0
     */
    public Object getData(final String theKey) {
        return getDataMap().get(theKey);
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#getResourceSet()
     * @since 5.0
     */
    public ResourceSet getResourceSet() {
        return this.resourceSet;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#getVdbContext()
     * @since 5.0
     */
    public VdbContextImpl getVdbContext() {
        return this.vdbContext;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#getDataMap()
     * @since 5.0
     */
    public Map getDataMap() {
        return this.dataMap;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#setResourceSet(org.eclipse.emf.ecore.resource.ResourceSet)
     * @since 5.0
     */
    public void setResourceSet(final ResourceSet theResourceSet) {
        ArgCheck.isNotNull(theResourceSet);
        this.resourceSet = theResourceSet;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.InternalVdbGenerationContext#setVdbContext(com.metamatrix.vdb.internal.edit.VdbContextImpl)
     * @since 5.0
     */
    public void setVdbContext(final VdbContextImpl theVdbContext) {
        ArgCheck.isNotNull(theVdbContext);
        this.vdbContext = theVdbContext;
    }

}

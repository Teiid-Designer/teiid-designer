/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;


/** 
 * @since 5.0
 */
public class VdbGenerationContextParameters {

    private Resource[] models;
    private Map modelNameByResource;
    private Map workspacePathByResource;
    private Map modelVisibilityByResource;
    private Map problemsByObjectId;
    private IPath[] existingPathsInVdb;
    private String tempFolderAbsolutePath;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /** 
     * @since 5.0
     */
    public VdbGenerationContextParameters() {   
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================
    
    public IPath[] getExistingPathsInVdb() {
        return this.existingPathsInVdb;
    }
  
    public void setExistingPathsInVdb(final IPath[] theExistingPathsInVdb) {
        ArgCheck.isNotNull(theExistingPathsInVdb);
        this.existingPathsInVdb = theExistingPathsInVdb;
    }
 
    public Map getModelNameByResource() {
        return this.modelNameByResource;
    }
   
    public void setModelNameByResource(final Map theModelNameByResource) {
        ArgCheck.isNotNull(theModelNameByResource);
        this.modelNameByResource = new HashMap(theModelNameByResource);
    }

    public Resource[] getModels() {
        return this.models;
    }

    public void setModels(final Resource[] theModels) {
        ArgCheck.isNotNull(theModels);
        this.models = theModels;
    }

    public Map getModelVisibilityByResource() {
        return this.modelVisibilityByResource;
    }

    public void setModelVisibilityByResource(final Map theModelVisibilityByResource) {
        ArgCheck.isNotNull(theModelVisibilityByResource);
        this.modelVisibilityByResource = new HashMap(theModelVisibilityByResource);
    }

    public String getTempFolderAbsolutePath() {
        return this.tempFolderAbsolutePath;
    }

    public void setTempFolderAbsolutePath(final String theTempFolderAbsolutePath) {
        this.tempFolderAbsolutePath = theTempFolderAbsolutePath;
    }

    public Map getWorkspacePathByResource() {
        return this.workspacePathByResource;
    }

    public void setWorkspacePathByResource(final Map theWorkspacePathByResource) {
        ArgCheck.isNotNull(theWorkspacePathByResource);
        this.workspacePathByResource = new HashMap(theWorkspacePathByResource);
    }

    public Map getProblemsByObjectId() {
        return this.problemsByObjectId;
    }
 
    public void setProblemsByObjectId(final Map theProblemsByObjectId) {
        ArgCheck.isNotNull(theProblemsByObjectId);
        this.problemsByObjectId = theProblemsByObjectId;
    }

}

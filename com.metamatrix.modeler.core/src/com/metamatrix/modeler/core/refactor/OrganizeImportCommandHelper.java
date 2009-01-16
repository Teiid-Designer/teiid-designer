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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;

/**
 * OrganizeImportCommand
 */
public abstract class OrganizeImportCommandHelper{
    
    protected static final String SDTI_URI = DatatypeConstants.BUILTIN_DATATYPES_URI; 
    protected static final String PLUGINID = ModelerCore.PLUGIN_ID;

    public static final int CAN_EXECUTE                         = 1000;
    public static final int ERROR_MISSING_RESOURCE              = 1001;
    public static final int ERROR_ORGANIZING_IMPORTS            = 1002;
    public static final int UNKNOWN_ERROR_ORGANIZING_IMPORTS    = 1003;
    public static final int EXECUTE_WITH_NO_PROBLEMS            = 1004;
    public static final int EXECUTE_WITH_WARNINGS               = 1005;
    public static final int EXECUTE_WITH_ERRORS                 = 1006;
    public static final int EXECUTE_WITH_WARNINGS_AND_ERRORS    = 1007;
    public static final int EXECUTE_WITH_NO_WARNINGS_AND_ERRORS = 1008;
    public static final int UNKNOWN_ERROR_BUILDING_IMPORT       = 1009;
    public static final int ERROR_GETTING_RESOURCE              = 1010;
    
    protected static final String PID = ModelerCore.PLUGIN_ID;

    protected OrganizeImportHandler handler;
    protected List modelImports;    
    
    private Resource resource;
    private CoreFactory factory;
    private final Object factoryLock = new Object();
    private OrganizeImportCommandFinderHelper finderHelper;
    
    /**
     * Exclude diagram entities from the EObjects that are visited by default
     * DiagramEntities Seldom have external references except in case of CustonDiagrams
     */   
    protected boolean includeDiagramReferences = false;
    
    protected OrganizeImportCommandHelper() {        
        modelImports = new ArrayList();
        finderHelper = new OrganizeImportCommandFinderHelper();
    }
    
    /** 
     * @return Returns the includeDiagramReferences.
     * @since 4.2
     */
    protected boolean isIncludeDiagramReferences() {
        return this.includeDiagramReferences;
    }

    /** 
     * @param includeDiagramReferences The includeDiagramReferences to set.
     * @since 4.2
     */
    protected void setIncludeDiagramReferences(boolean includeDiagramReferences) {
        this.includeDiagramReferences = includeDiagramReferences;
    }
    
    /**
     *  
     * @return OrganizeImportCommandFinderHelper
     * @since 4.3
     */
    protected OrganizeImportCommandFinderHelper getHelper() {
        return finderHelper;
    }
    
    /**
     *  
     * @param resource
     * @since 4.3
     */
    protected void setResource(final Resource resource) {
        this.resource = resource;
        finderHelper.setResource(resource);
    }
    
    /**
     *  
     * @return Resource
     * @since 4.3
     */
    protected Resource getResource() {
        return resource;
    }
    
    /**
     *  
     * @param handler
     * @since 4.3
     */
    protected void setHandler( final OrganizeImportHandler handler ) {
        this.handler = handler;                
    }
    
    /**
     *  
     * @return OrganizeImportHandler
     * @since 4.3
     */
    protected OrganizeImportHandler getHandler() {
        return this.handler;                
    }
    
    /**
     *  
     * @param factory
     * @since 4.3
     */
    protected void setFactory(final CoreFactory factory) {
        this.factory = factory;
    }
    
    /**
     *  
     * @return CoreFactory
     * @since 4.3
     */
    protected CoreFactory getFactory() {
        if ( this.factory == null ) {
            synchronized(this.factoryLock) {
                if ( this.factory == null ) {
                    this.factory = CoreFactory.eINSTANCE;
                }
            }
        }
        return this.factory;
    }               
                
    /**
     * @return List
     */
    protected List getModelImports() {
        return modelImports;
    }    
    
    /**
     *  
     * @param paths
     * @since 4.3
     */
    protected void setRefactoredPaths(Map paths) {
        finderHelper.setRefactoredPaths(paths);
    }
    
    /**
     *  
     * @param monitor
     * @return IStatus
     * @since 4.3
     */
    abstract protected IStatus execute( IProgressMonitor monitor);
}

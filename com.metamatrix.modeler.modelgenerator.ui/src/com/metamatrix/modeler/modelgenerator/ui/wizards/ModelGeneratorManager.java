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

package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.ModelerModelGeneratorUiPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2RelationalGenerator;

/**
 * Virtual Model Generator Manager - Business Object for interacting with GUI
 */
public class ModelGeneratorManager implements IModelGeneratorManager, ModelGeneratorUiConstants {
    
    private static final String GENERATOR_WARNING_TITLE = "Generator Warnings"; //$NON-NLS-1$
    private static final String GENERATOR_WARNING_MSG = "The generation resulted in warnings.  \n Please check log file."; //$NON-NLS-1$
    private static final String GENERATOR_ERROR_TITLE = "Generator Errors"; //$NON-NLS-1$
    private static final String GENERATOR_ERROR_MSG = "The generation resulted in errors.  \n Please check log file."; //$NON-NLS-1$
    
    //============================================================
    // Instance variables
    //============================================================
    private ModelWorkspaceSelections umlInputSelections;
    private ModelWorkspaceSelections datatypeSelections;
    private Uml2RelationalGenerator uml2RelationalGenerator;
    private GeneratorManagerOptions generatorManagerOptions;
            
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     */
    public ModelGeneratorManager( ) {
        init();
    }
    
    //============================================================
    // IModelGeneratorManager interface methods
    //============================================================
             
    /**
     *  initialize the modelGenerator Manager
     */
    public void init() {
        // Uml2Relational ModelGenerator
        this.uml2RelationalGenerator = Uml2ModelGeneratorPlugin.createUml2RelationalGenerator();
        // UML Input Selections and Datatype Model Selections
        this.umlInputSelections = this.uml2RelationalGenerator.getModelWorkspaceUmlInputSelections();
        this.datatypeSelections = this.uml2RelationalGenerator.getModelWorkspaceDatatypeSelections();
        // GeneratorManagerOptions
        this.generatorManagerOptions = new GeneratorManagerOptions(this.uml2RelationalGenerator);
        //clearUmlInputSelections();
    }
    
    /**
     *  get the current Source Model Selections
     * @return the Model Workspace Selections
     */
    public ModelWorkspaceSelections getUmlInputSelections() {
        return this.umlInputSelections;
    }
    
    /**
     *  get the current Datatype Model Selections
     * @return the Model Workspace Selections
     */
    public ModelWorkspaceSelections getDatatypeSelections() {
        return this.datatypeSelections;
    }
    
    
    /**
     *  get the current Source Model Selections
     * @return the Model Workspace Selections
     */
    public boolean hasUmlModelSelections() {
        IStatus status = this.uml2RelationalGenerator.validateInputUmlSelection();
        if( status.getSeverity() == IStatus.OK )
            return true;
        
        return false;
    }
    
    /**
     *  determine if the Virtual Relational Model can be generated using current selections
     * @return 'true' if the model can be generated, 'false' if not.
     */
//    public boolean canGenerateModel( ) {
//        return ( hasValidSourceSelections() && hasValidOptions() );
//    }
    
    /**
     *  determine if the Virtual Relational Model can be generated using current selections
     * @return 'true' if the model can be generated, 'false' if not.
     */
    public GeneratorManagerOptions getGeneratorManagerOptions( ) {
        return this.generatorManagerOptions;
    }
    
    /**
     *  generate the Virtual Relational Model using current selections
     */
    public void generateOutputAndMerge(ModelResource relationalModel, IProgressMonitor monitor) {
        this.generatorManagerOptions.setRelationalOutputModel(relationalModel);
        // Populate the target Model, using the Source Selections
        try {
            final ModelGenerator generator = this.uml2RelationalGenerator.getModelGenerator();
            IStatus status = generator.execute(monitor);
            if(status!=null) {
                // Log status
                if(status.isMultiStatus()) {
                    IStatus[] statuses = status.getChildren();
                    for(int i=0; i<statuses.length; i++) {
                        Util.log(statuses[i]);
                    }
                } else {
                    Util.log(status);
                }
                if(status.getSeverity()==IStatus.ERROR) {
                    Shell shell = ModelerModelGeneratorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                    MessageDialog.openError( shell, GENERATOR_ERROR_TITLE , GENERATOR_ERROR_MSG ); 
                } else if(status.getSeverity()==IStatus.WARNING) {
                    Shell shell = ModelerModelGeneratorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                    MessageDialog.openInformation( shell, GENERATOR_WARNING_TITLE , GENERATOR_WARNING_MSG ); 
                }
            }
        } catch (CoreException e) {
            Util.log(e);
        }
    }
        
    /**
     *  generate the Difference Report for Refresh
     */
    public List getDifferenceReports(ModelResource relationalModel, IProgressMonitor monitor) {
        List diffReports = null;
        this.generatorManagerOptions.setRelationalOutputModel(relationalModel);

        // Populate the target Model, using the Source Selections
        if(relationalModel!=null) {
            try {
                final ModelGenerator generator = this.uml2RelationalGenerator.getModelGenerator();
                generator.generateOutput(monitor);
                generator.computeDifferenceReport(monitor);
                
                diffReports = generator.getDifferenceReports();  // may not be all of them
            } catch (CoreException e) {
                Util.log(e);
            }
        }
        if(diffReports==null) {
            diffReports = Collections.EMPTY_LIST;
        }
        return diffReports;
    }

    /**
     *  Perform merge operation using current generator state
     */
    public IStatus performMerge(IProgressMonitor monitor) {
        IStatus result = null;
        // Do merge operation
        try {
            final ModelGenerator generator = this.uml2RelationalGenerator.getModelGenerator();
            result = generator.mergeOutputIntoOriginal(monitor);
        } catch (CoreException e) {
            Util.log(e);
        }
        return result;
    }

    /**
     *  clear any Source Model Selections
     */
    public void clearUmlInputSelections() {
        ModelWorkspaceSelections inputSelections = getUmlInputSelections();
        List selectedModelResources = new ArrayList();
        try {
            selectedModelResources = inputSelections.getSelectedOrPartiallySelectedModelResources();
        } catch (ModelWorkspaceException e) {
            Util.log(e);
        }
        Iterator iter = selectedModelResources.iterator();
        while(iter.hasNext()) {
            ModelResource modelResource = (ModelResource)iter.next();
            try {
                inputSelections.setSelected(modelResource,false);
            } catch (ModelWorkspaceException e) {
                Util.log(e);
            }
        }
    }

    /**
     *  determine if the current selections are valid
     * @return 'true' if there are selections, 'false' if not.
     */
    public boolean hasValidSourceSelections() {
        boolean selectionsValid = false;
        if(this.umlInputSelections!=null) {
            // Get the selection Paths
            List selectedPaths = this.umlInputSelections.getSelectedPaths();
            // If has Selected Paths, selections valid
            if(selectedPaths.size()>0) {
                selectionsValid = true;
            // If no Selected paths, check partially selected
            } else {
                List partiallySelectedPaths = this.umlInputSelections.getPartiallySelectedPaths();
                // If has partially selected paths, selections valid
                if(partiallySelectedPaths.size()>0) {
                    selectionsValid = true;
                }
            }
        }
        return selectionsValid;
    }
       
    public ModelResource getRelationalOutputModel() {
        return this.generatorManagerOptions.getRelationalOutputModel();   
    }
    
    public ModelResource getRelationshipsModel() {
        return this.generatorManagerOptions.getRelationshipsModel();   
    }
    
    public void save(IProgressMonitor monitor) {
        //------------------------------------------------------
        // Save the relational and relationships output models
        //------------------------------------------------------
        ModelResource relationalModel = getRelationalOutputModel();
        ModelResource relationshipsModel = getRelationshipsModel();
        try {
            // Save Relational and Relationships Model
            if(relationalModel!=null) {
                relationalModel.save(monitor,true);
            }
            // Save Relationships Model
            if(relationshipsModel!=null) {
                relationshipsModel.save(monitor,true);
            }
        } catch (ModelWorkspaceException e) {
            String message = Util.getString("ModelGeneratorManager.modelSaveError"); //$NON-NLS-1$
            Util.log(IStatus.ERROR, e, message); 
        }
    }
    

    //============================================================
    // private methods
    //============================================================
    

}

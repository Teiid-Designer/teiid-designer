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

package com.metamatrix.modeler.modelgenerator.uml2;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;

/**
 * Uml2RelationalGeneratorImpl
 */
public interface Uml2RelationalGenerator {

    // =========================================================================
    //                              Options
    // =========================================================================

    /**
     * Return the options for this generator.
     * @return the options; never null
     */
    public Uml2RelationalOptions getOptions();

    // =========================================================================
    //                          Inputs and Outputs
    // =========================================================================

    /**
     * Return the {@link ModelWorkspaceSelections} object that can be used to choose the
     * existing UML2 models that should be used by the generator as inputs.
     * @return the ModelWorkspaceSelections object; never null
     */
    public ModelWorkspaceSelections getModelWorkspaceUmlInputSelections();
    
    /**
     * Return the {@link ModelWorkspaceSelections} object that can be used to choose the
     * existing datatype models that should be used by the generator.
     * @return the ModelWorkspaceSelections object; never null
     */
    public ModelWorkspaceSelections getModelWorkspaceDatatypeSelections();
    
    /**
     * Return the ModelResource for the relational model into which all generated
     * {@link com.metamatrix.metamodels.relational.RelationalEntity relational entity} instances
     * should be placed.
     * @return the reference to the relational model; may be null if not set
     */
    public ModelResource getRelationalOutputModel();
    
    /**
     * Set the ModelResource for the relational model into which all generated
     * {@link com.metamatrix.metamodels.relational.RelationalEntity relational entity} instances
     * should be placed.
     * @param relationalOutputModel the reference to the relational model; may be null if not set
     */
    public void setRelationalOutputModel( final ModelResource relationalOutputModel );
    
    /**
     * Return the ModelResource for the relationship model into which all relationships created
     * by this generator should be placed.
     * @return the reference to the relationship model; may be null if not set
     */
    public ModelResource getRelationshipModel();

    /**
     * Set the ModelResource for the relationship model into which all relationships created
     * by this generator should be placed.
     * @param resource the relationship model; may be null
     */
    public void setRelationshipModel(ModelResource resource);

    /**
     * Set the Custom Properties Map for generated relational columns.  This is a mapping of 
     * UML Property (map key) to a corresponding relational PropertyDescriptor (map value) for UML
     * @param customPropsMap the UML to relational property mappings
     */
    public void setColumnCustomPropsMap(Map columnCustomPropsMap);

    /**
     * Set the Custom Properties Map for generated relational tables.  This is a mapping of 
     * UML Property (map key) to a corresponding relational PropertyDescriptor (map value) for UML
     * @param customPropsMap the UML to relational property mappings
     */
    public void setTableCustomPropsMap(Map tableCustomPropsMap);
    
    // =========================================================================
    //                           Validation
    // =========================================================================
    
    /**
     * Validate whether the {@link #getModelGenerator() model generator} can be executed.
     * This is equivalent to calling:
     * <ol>
     *  <li>{@link #validateInputUmlSelection()}</li>
     *  <li>{@link #validateRelationalOutput()}</li>
     *  <li>{@link #validateDatatypeSelection()}</li>
     *  <li>{@link #getOptions()}.{@link Uml2RelationalOptions#validate() validate()}</li>
     * </ol>
     * @return the status of whether all of the required inputs have been supplied; never null
     */
    public IStatus validate();
    
    /**
     * Validate whether the {@link #getModelWorkspaceUmlInputSelections() UML inputs} have been specified.
     * @return the status of whether all of the UML inputs have been supplied; never null
     */
    public IStatus validateInputUmlSelection();
    
    /**
     * Validate whether the {@link #getModelWorkspaceDatatypeSelections() datatype inputs} have been specified.
     * @return the status of whether all of the datatype inputs have been supplied; never null
     */
    public IStatus validateDatatypeSelection();
    
    /**
     * Validate whether the {@link #getRelationalOutputModel() relational output model} has been specified.
     * @return the status of whether all of the UML inputs have been supplied; never null
     */
    public IStatus validateRelationalOutput();
    


    // =========================================================================
    //                           Generation / Execution
    // =========================================================================
    
    /**
     * Return the ModelGenerator that can be used to perform the UML2 to Relational conversion/generation.
     * The model generator can be executed with one step (see {@link ModelGenerator#execute(IProgressMonitor)})
     * or can be used to {@link ModelGenerator#generateOutputAndDifferenceReport(IProgressMonitor) compute differences},
     * obtain a {@link ModelGenerator#getDifferenceReport() difference report}, and 
     * {@link ModelGenerator#mergeOutputIntoOriginal(IProgressMonitor) merge results}.
     * @throws CoreException if there is a problem creating the generator
     */
    public ModelGenerator getModelGenerator() throws CoreException;


}

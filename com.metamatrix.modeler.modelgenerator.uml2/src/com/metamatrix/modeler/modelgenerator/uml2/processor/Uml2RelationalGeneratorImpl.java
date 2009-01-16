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

package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLPackage;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.util.RelationshipTypeManager;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.compare.ModelProducer;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.generator.BasicModelGenerator;
import com.metamatrix.modeler.compare.generator.CompositeModelGenerator;
import com.metamatrix.modeler.compare.selector.EmfResourceSelector;
import com.metamatrix.modeler.compare.selector.ModelResourceSelector;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.compare.util.CompareUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelFolder;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceFilter;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelectionFilter;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceView;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.modelgenerator.processor.BuiltInDatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.MultiDatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.NullDatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.RelationTracker;
import com.metamatrix.modeler.modelgenerator.processor.TransientRelationTrackerImpl;
import com.metamatrix.modeler.modelgenerator.processor.XsdDatatypeFinder;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2RelationalGenerator;

/**
 * Uml2RelationalGeneratorImpl
 */
public class Uml2RelationalGeneratorImpl implements Uml2RelationalGenerator {

    public static final int ERROR_UNABLE_TO_ACCESS_UML_MODELS                       = 70100;
    public static final int ERROR_NO_UML_MODELS_SELECTED                            = 70101;
    public static final int ERROR_NO_RELATIONAL_MODEL_SPECIFIED                     = 70102;
    public static final int ERROR_UNABLE_TO_ACCESS_DATATYPE_MODELS                  = 70103;
    public static final int ERROR_UNABLE_TO_OPEN_DATATYPE_MODELS                    = 70104;
    public static final int ERROR_RELATIONAL_MODEL_HAS_NO_PRIMARY_METAMODEL         = 70105;
    public static final int ERROR_RELATIONSHIP_MODEL_HAS_NO_PRIMARY_METAMODEL       = 70106;
    public static final int ERROR_NO_RELATIONAL_AND_RELATIONSHIP_MODEL_SAME         = 70107;
    public static final int ERROR_RELATIONAL_MODEL_HAS_INVALID_PRIMARY_METAMODEL    = 70108;
    public static final int ERROR_RELATIONSHIP_MODEL_HAS_INVALID_PRIMARY_METAMODEL  = 70108;

    private final ModelWorkspaceView workspaceInputView;
    private final ModelWorkspaceSelections workspaceInputSelections;
    private final ModelWorkspaceView workspaceDatatypesView;
    private final ModelWorkspaceSelections workspaceDatatypesSelections;
    private ModelResource relationalOutputModel;
    private ModelResource relationshipModel;
    private final Uml2RelationalOptions options;
    private ModelGenerator generator;
    private RelationalFragmentGenerator fragmentGenerator;
//    private Map customPropsMap;

    /**
     * Construct an instance of Uml2RelationalGeneratorImpl.
     * 
     */
    public Uml2RelationalGeneratorImpl() {
        super();
        this.options = new Uml2RelationalOptions();
        this.workspaceInputView = new ModelWorkspaceView() {
            /**
             * Override the method to ignore EAnnotations as children ...
             * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceView#doGetChildren(java.lang.Object)
             */
            @Override
            protected Object doGetChildren(Object parent) throws ModelWorkspaceException {
                if ( parent instanceof EObject ) {
                    final List copy = new ArrayList(((EObject)parent).eContents());
                    final Iterator iter = copy.iterator();
                    while (iter.hasNext()) {
                        final EObject child = (EObject)iter.next();
                        if ( child instanceof Profile ) {
                            iter.remove();
                        }
                    }
                    return copy;
                }
                return super.doGetChildren(parent);
            }

        };
        this.workspaceInputSelections = new ModelWorkspaceSelections();
        this.workspaceDatatypesView = new ModelWorkspaceView();
        this.workspaceDatatypesSelections = new ModelWorkspaceSelections();
        doConfigureInputs(this.workspaceInputView,this.workspaceInputSelections);
        doConfigureDatatypes(this.workspaceDatatypesView,this.workspaceDatatypesSelections);
        this.fragmentGenerator = new RelationalFragmentGeneratorImpl();
    }
    
    // =========================================================================
    //                              Options
    // =========================================================================

    /**
     * Return the options for this generator.
     * @return the options; never null
     */
    public Uml2RelationalOptions getOptions() {
        return options;
    }

    // =========================================================================
    //                          Inputs and Outputs
    // =========================================================================

    /**
     * Return the {@link ModelWorkspaceSelections} object that can be used to choose the
     * existing UML2 models that should be used by the generator as inputs.
     * @return the ModelWorkspaceSelections object; never null
     */
    public ModelWorkspaceSelections getModelWorkspaceUmlInputSelections() {
        return this.workspaceInputSelections;
    }
    
    /**
     * Return the {@link ModelWorkspaceSelections} object that can be used to choose the
     * existing datatype models that should be used by the generator.
     * @return the ModelWorkspaceSelections object; never null
     */
    public ModelWorkspaceSelections getModelWorkspaceDatatypeSelections() {
        return this.workspaceDatatypesSelections;
    }
    
    /**
     * Return the ModelResource for the relational model into which all generated
     * {@link com.metamatrix.metamodels.relational.RelationalEntity relational entity} instances
     * should be placed.
     * @return the reference to the relational model; may be null if not set
     */
    public ModelResource getRelationalOutputModel() {
        return this.relationalOutputModel;
    }
    
    /**
     * Set the ModelResource for the relational model into which all generated
     * {@link com.metamatrix.metamodels.relational.RelationalEntity relational entity} instances
     * should be placed.
     * @param relationalOutputModel the reference to the relational model; may be null if not set
     */
    public void setRelationalOutputModel( final ModelResource relationalOutputModel ) {
        this.relationalOutputModel = relationalOutputModel;
        clearModelGenerator();
    }
    
    /**
     * Return the ModelResource for the relationship model into which all relationships created
     * by this generator should be placed.
     * @return the reference to the relationship model; may be null if not set
     */
    public ModelResource getRelationshipModel() {
        return relationshipModel;
    }

    /**
     * Set the ModelResource for the relationship model into which all relationships created
     * by this generator should be placed.
     * @param resource the relationship model; may be null
     */
    public void setRelationshipModel(ModelResource resource) {
        relationshipModel = resource;
        clearModelGenerator();
    }

    /**
     * Set the Custom Properties Map for generated relational columns.  This is a mapping of 
     * UML Property (map key) to a corresponding relational PropertyDescriptor (map value)
     * @param customPropsMap the UML to relational property mappings
     */
    public void setColumnCustomPropsMap(Map customPropsMap) {
        getOptions().setColumnCustomPropsMap(customPropsMap);
    }

    /**
     * Set the Custom Properties Map for generated relational tabless.  This is a mapping of 
     * UML Property (map key) to a corresponding relational PropertyDescriptor (map value)
     * @param customPropsMap the UML to relational property mappings
     */
    public void setTableCustomPropsMap(Map customPropsMap) {
        getOptions().setTableCustomPropsMap(customPropsMap);
    }

    /**
     * @return
     */
    public RelationalFragmentGenerator getFragmentGenerator() {
        return fragmentGenerator;
    }

    /**
     * @param generator
     */
    public void setFragmentGenerator(RelationalFragmentGenerator generator) {
        fragmentGenerator = generator;
        clearModelGenerator();
    }
    
    protected void clearModelGenerator() {
        if ( this.generator != null ) {
            synchronized(this) {
                if ( this.generator != null ) {
                    // Clear the generator ...
                    this.generator = null;
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.uml2.Uml2RelationalGenerator#getModelGenerator()
     */
    public ModelGenerator getModelGenerator() throws CoreException {
        if ( this.generator == null ) {
            synchronized(this) {
                if ( this.generator == null ) {
                    // Create the generator ...
                    this.generator = doCreateModelGenerator();
                }
            }
        }
        return this.generator;
    }
    
    public IStatus validate() {
        IStatus status = validateInputUmlSelection();
        if ( !status.isOK() ) {
            return status;
        }
        status = validateRelationalOutput();
        if ( !status.isOK() ) {
            return status;
        }
        status = validateDatatypeSelection();
        if ( !status.isOK() ) {
            return status;
        }
        status = validateRelationshipModel();
        if ( !status.isOK() ) {
            return status;
        }
        status = this.options.validate();
        return status;
    }
    
    public IStatus validateInputUmlSelection() {
        try {
            doGetInputModelSelectors();
        } catch ( CoreException e ) {
            return e.getStatus();
        }
        final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.UML_inputs_are_valid"); //$NON-NLS-1$
        final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
        return new Status(IStatus.OK,pluginId,0,msg,null);
    }
    
    public IStatus validateDatatypeSelection() {
        try {
            doGetDatatypeModelSelectors();
        } catch ( CoreException e ) {
            return e.getStatus();
        }
        final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Datatype_inputs_are_valid"); //$NON-NLS-1$
        final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
        return new Status(IStatus.OK,pluginId,0,msg,null);
    }
    
    public IStatus validateRelationalOutput() {
        // Create a selector for the output model ...
        final ModelResource relationalResource = this.getRelationalOutputModel();
        final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
        if ( relationalResource == null ) {
            final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.A_relational_model_must_be_specified_as_the_output_model"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_NO_RELATIONAL_MODEL_SPECIFIED,msg,null);
            return status;
        }
        // Make sure the primary metamodel is correct ...
        try {
            final MetamodelDescriptor primaryMetamodel = relationalResource.getPrimaryMetamodelDescriptor();
            if ( primaryMetamodel == null ) {
                final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relational_model_has_no_primary_metamodel"); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_RELATIONAL_MODEL_HAS_NO_PRIMARY_METAMODEL,msg,null);
                return status;
            }
            final String metamodelUri = primaryMetamodel.getNamespaceURI();
            if ( metamodelUri != null && !RelationalPackage.eNS_URI.equals(primaryMetamodel.getNamespaceURI()) ) {
                final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relational_model_is_not_relational"); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_RELATIONAL_MODEL_HAS_INVALID_PRIMARY_METAMODEL,msg,null);
                return status;
            }
        } catch (ModelWorkspaceException e) {
            return new Status(IStatus.ERROR,pluginId,0,e.getLocalizedMessage(),e);
        }
        final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relational_output_model_is_valid"); //$NON-NLS-1$
        return new Status(IStatus.OK,pluginId,0,msg,null);
    }
    
    public IStatus validateRelationshipModel() {
        // Create a selector for the output model ...
        final ModelResource relationshipResource = this.getRelationshipModel();
        final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
        if ( relationshipResource != null ) {
            final ModelResource relationalResource = this.getRelationalOutputModel();
            if ( relationalResource != null ) {
                // Make sure they are not the same ...
                final IPath relationshipsPath = relationshipResource.getPath();
                final IPath relationalPath = relationalResource.getPath();
                if ( relationshipsPath.equals(relationalPath) ) {
                    final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relationship_and_relational_models_must_be_different"); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_NO_RELATIONAL_AND_RELATIONSHIP_MODEL_SAME,msg,null);
                    return status;
                }
            }
            
            // Make sure the primary metamodel is correct ...
            try {
                final MetamodelDescriptor primaryMetamodel = relationshipResource.getPrimaryMetamodelDescriptor();
                if ( primaryMetamodel == null ) {
                    final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relationship_model_has_no_primary_metamodel"); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_RELATIONSHIP_MODEL_HAS_NO_PRIMARY_METAMODEL,msg,null);
                    return status;
                }
                final String metamodelUri = primaryMetamodel.getNamespaceURI();
                if ( metamodelUri != null && !RelationshipPackage.eNS_URI.equals(primaryMetamodel.getNamespaceURI()) ) {
                    final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relationship_model_is_not_relationship"); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_RELATIONSHIP_MODEL_HAS_INVALID_PRIMARY_METAMODEL,msg,null);
                    return status;
                }
            } catch (ModelWorkspaceException e) {
                return new Status(IStatus.ERROR,pluginId,0,e.getLocalizedMessage(),e);
            }
        }
        final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Relationship_model_is_valid"); //$NON-NLS-1$
        return new Status(IStatus.OK,pluginId,0,msg,null);
    }
    
    protected List doGetInputModelSelectors() throws CoreException {
        // Create the input selectors ...
        final ModelWorkspaceSelections uml2InputSelections = this.getModelWorkspaceUmlInputSelections();
        final List inputModelSelectors = new LinkedList();
        try {
            final List inputModelResources = uml2InputSelections.getSelectedOrPartiallySelectedModelResources();
            final Iterator iter = inputModelResources.iterator();
            while (iter.hasNext()) {
                final ModelResource modelResource = (ModelResource)iter.next();
                final ModelSelector selector = new ModelResourceSelector(modelResource);
                inputModelSelectors.add(selector);
            }
        } catch (ModelWorkspaceException e) {
            final Object[] params = new Object[]{e.getLocalizedMessage()};
            final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Unable_to_obtain_selected_resources",params); //$NON-NLS-1$
            final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
            final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_UNABLE_TO_ACCESS_UML_MODELS,msg,e);
            throw new CoreException(status);
        }
        if ( inputModelSelectors.isEmpty() ) {
            final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.At_least_one_UML_model_must_be_selected"); //$NON-NLS-1$
            final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
            final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_NO_UML_MODELS_SELECTED,msg,null);
            throw new CoreException(status);
        }
        return inputModelSelectors;
    }
    
    protected List doGetDatatypeModelSelectors() throws CoreException {
        final ModelWorkspaceSelections datatypeSelections = this.getModelWorkspaceDatatypeSelections();
        final List datatypeSelectors = new LinkedList();
        try {
            final List datatypeResources = datatypeSelections.getSelectedOrPartiallySelectedModelResources();
            final Iterator iter = datatypeResources.iterator();
            while (iter.hasNext()) {
                final ModelResource resource = (ModelResource)iter.next();
                final ModelSelector selector = new ModelResourceSelector(resource);
                datatypeSelectors.add(selector);
            }
            final List datatypeFiles = datatypeSelections.getSelectedOrPartiallySelectedNonModelResources();
            final Iterator iter2 = datatypeFiles.iterator();
            while (iter2.hasNext()) {
                final IResource resource = (IResource)iter2.next();
                if ( resource instanceof IFile ) {
                    // Find the EMF Resource for this file ...
                    final Resource emfResource = doGetEmfResource((IFile)resource);
                    final ModelSelector selector = new EmfResourceSelector(emfResource);
                    datatypeSelectors.add(selector);
                }
            }
        } catch (ModelWorkspaceException e) {
            final Object[] params = new Object[]{e.getLocalizedMessage()};
            final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Unable_to_obtain_selected_datatype_resources",params); //$NON-NLS-1$
            final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
            final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_UNABLE_TO_ACCESS_DATATYPE_MODELS,msg,e);
            throw new CoreException(status);
        } catch (CoreException e) {
            final Object[] params = new Object[]{e.getLocalizedMessage()};
            final String msg = Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalGeneratorImpl.Unable_to_open_selected_datatype_resources",params); //$NON-NLS-1$
            final String pluginId = Uml2ModelGeneratorPlugin.PLUGIN_ID;
            final IStatus status = new Status(IStatus.ERROR,pluginId,ERROR_UNABLE_TO_OPEN_DATATYPE_MODELS,msg,e);
            throw new CoreException(status);
        }
        return datatypeSelectors;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.processor.ModelProcessor#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected ModelGenerator doCreateModelGenerator() throws CoreException {
        // Create the input selectors ...
        final ModelWorkspaceSelections uml2InputSelections = this.getModelWorkspaceUmlInputSelections();
        final List inputModelSelectors = doGetInputModelSelectors();

        // Create a selector for the output model ...
        final IStatus relationalStatus = validateRelationalOutput();
        if ( !relationalStatus.isOK() ) {
            throw new CoreException(relationalStatus);
        }
        final ModelResource relationalResource = this.getRelationalOutputModel();
        final ModelSelector relationalOutputModelSelector = new ModelResourceSelector(relationalResource);
        relationalOutputModelSelector.setLabel(relationalResource.getPath().toString());
        final URI relationalUri = relationalOutputModelSelector.getUri();
        // Create a temporary model selector with the same URI as the actual relational model
        // (the temporary will be placed in a separate "temporary" resource set, so same URI can be used)
        final ModelSelector tempRelationalOutputModelSelector = new TransientModelSelector(relationalUri);

        // Check the options ...
        final Uml2RelationalOptions options = this.getOptions();
        final IStatus optionStatus = options.validate();
        if ( optionStatus.getSeverity() == IStatus.ERROR ) {
            throw new CoreException(optionStatus);
        }

        // Set up the relation tracker ...
        final ModelResource relationModel = this.getRelationshipModel();
        RelationTracker relationTracker = new TransientRelationTrackerImpl();
        ModelSelector transientRelationshipModelSelector = null;
        ModelSelector relationModelSelector = null;
        if ( relationModel != null ) {
            relationModelSelector = new ModelResourceSelector(relationModel);
            final String label = relationModel.getPath().toString();
            relationModelSelector.setLabel(label);
            final URI relationshipUri = relationModelSelector.getUri();
            transientRelationshipModelSelector = new TransientModelSelector(relationshipUri);
            final RelationshipFactory factory = RelationshipFactory.eINSTANCE;
            relationTracker = new Uml2RelationalRelationTrackerImpl(transientRelationshipModelSelector,factory);
        }
        final RelationTracker finalRelationTracker = relationTracker;
        final ModelSelector finalRelationModelSelector = relationModelSelector;

        // Set up the datatype finder ...
        DatatypeFinder datatypeFinder = BuiltInDatatypeFinder.INSTANCE;
        final List datatypeSelectors = doGetDatatypeModelSelectors();
        if ( datatypeSelectors.size() == 0 ) {
            datatypeFinder = BuiltInDatatypeFinder.INSTANCE;
        } else if ( datatypeSelectors.size() == 1 ) {
            final ModelSelector modelSelector = (ModelSelector)datatypeSelectors.get(0); 
            datatypeFinder = new XsdDatatypeFinder(modelSelector,BuiltInDatatypeFinder.INSTANCE);
        } else {
            final List finders = new LinkedList();
            finders.add(BuiltInDatatypeFinder.INSTANCE);
            final Iterator iter = datatypeSelectors.iterator();
            while (iter.hasNext()) {
                final ModelSelector selector = (ModelSelector)iter.next();
                final DatatypeFinder finder = new XsdDatatypeFinder(selector,new NullDatatypeFinder());
                finders.add(finder);
            }
            datatypeFinder = new MultiDatatypeFinder(finders);
        }
        final String desc = null;

        // =========================================================================
        //                      Create the Relational model generator ...
        // =========================================================================
        // Create the producer ...
        final Uml2RelationalProcessor producer = new Uml2RelationalProcessor(
                                                        uml2InputSelections,inputModelSelectors,
                                                        tempRelationalOutputModelSelector,options,
                                                        finalRelationTracker,datatypeFinder,desc,
                                                        fragmentGenerator);
        // Create the model generator ...
        // (Note the class is overridden to gain access to the post-processing step for the DifferenceReport)
        final List standardFactories = ModelerComparePlugin.createEObjectMatcherFactories();
        final ModelGenerator relationalGen = new BasicModelGenerator(relationalOutputModelSelector,producer,standardFactories) {
            /**
             * Override this method to gain access to the DifferenceReport before it is returned
             * by the {@link ModelGenerator}.
             * @see com.metamatrix.modeler.compare.generator.BasicModelGenerator#doPostProcessDifferenceReports()
             */
            @Override
            protected void doPostProcessDifferenceReports() {
                // Delegate back out to the Uml2RelationalGenerator implementation ...
                final DifferenceReport report = (DifferenceReport)this.getAllDifferenceReports().get(0);
                Uml2RelationalGeneratorImpl.this.doPostProcessRelational(report,finalRelationTracker);
            }
        };

        ModelGenerator theGenerator = null;
        if ( transientRelationshipModelSelector != null ) {
            // =========================================================================
            //                      Create the Relationship model generator ...
            // =========================================================================
            final ModelSelector generatedRelationSelector = transientRelationshipModelSelector;
            final ModelProducer relationProducer = new ModelProducer() {
                public void execute(IProgressMonitor monitor, List problems) throws Exception {
                    // Make sure the metamodel URI and model type are set ...
                    if ( finalRelationModelSelector != null ) {
                        final ModelAnnotation annotation = finalRelationModelSelector.getModelAnnotation();
                        annotation.setModelType( ModelType.LOGICAL_LITERAL );
                        annotation.setPrimaryMetamodelUri( RelationshipPackage.eNS_URI );
                    }

                    // do nothing else, since it is produced while the generator is creating the relational objects
                }
                public ModelSelector getOutputSelector() {
                    return generatedRelationSelector;
                }
            };
            final ModelGenerator relationshipGen = new BasicModelGenerator(relationModelSelector,relationProducer,standardFactories) {
                /**
                 * Override this method to gain access to the DifferenceReport before it is returned
                 * by the {@link ModelGenerator}.
                 * @see com.metamatrix.modeler.compare.generator.BasicModelGenerator#doPostProcessDifferenceReports()
                 */
                @Override
                protected void doPostProcessDifferenceReports() {
                    // Delegate back out to the Uml2RelationalGenerator implementation ...
                    final DifferenceReport report = (DifferenceReport)this.getAllDifferenceReports().get(0);
                    Uml2RelationalGeneratorImpl.this.doPostProcessRelationship(report,finalRelationTracker);
                }
                /**
                 * @see com.metamatrix.modeler.compare.generator.AbstractModelGenerator#doPostMerge()
                 */
                @Override
                protected void doPostMerge() {
                    super.doPostMerge();
                    Uml2RelationalGeneratorImpl.this.doPostRelationshipMerge();
                }

            };

            // =========================================================================
            //                      Create the Composite model generator ...
            // =========================================================================
            final List generators = new ArrayList(2);
            generators.add(relationalGen);  // Add this one first!!
            generators.add(relationshipGen);
            theGenerator = new CompositeModelGenerator(generators);

        } else {
            // Not generating relationships, so don't bother making a composite ...
            theGenerator = relationalGen;
        }
        return theGenerator;
    }
    
    // =========================================================================
    //          Methods that can be overridden to customize the behavior
    // =========================================================================
    
    /**
     * Method that is invoked by the specialized Relational {@link ModelGenerator} created in 
     * {@link #doCreateModelGenerator()}.  This method can be overridden if the
     * difference report is to be modified or changed before the ModelGenerator
     * exposes it via the {@link ModelGenerator#getDifferenceReports()} method.
     * <p>
     * This implementation attempts to correct for the case when an existing model is
     * being updated with a subset of the {@link #getModelWorkspaceUmlInputSelections() UML2 model}.
     * </p><p>
     * For example, consider a UML2 model with a Package P1 that contains two Classes, C1 and C2.
     * If a relational model were generated originally with all of P1 selected, then the relational
     * model would have at least two BaseTables, T1 and T2, that correspond to C1 and C2.  (There may
     * be additional intersect tables, but that is irrelevant to this discussion.)
     * </p><p>
     * If the UML2 model is changed by adding Class C3, and the Relational model is "updated" (via copy/paste)
     * by selecting only C3, then the generator will only generate a new Table T3.  When the difference
     * is computed between the generated Relational objects and the existing Relational model,
     * Tables T1 and T2 will be marked for deletion (since they were not selected and therefore not involved
     * in the second generation).
     * </p><p>
     * This implementation attempts to correct for the ability to 
     * {@link #getModelWorkspaceUmlInputSelections() partially select the UML2 input model} by
     * post processing the difference report to remove from the report all "deletes" of Relational objects
     * that correspond to something that was not fully selected in the UML2 input model.
     * </p>
     * @param report the difference report; may not be null
     * @param relationTracker the {@link RelationshipTracker} that was used to generate the output model
     * (this has only the relationships between the relational objects generated by this instance and
     * the selected UML2 objects); may not be null
     */
    protected DifferenceReport doPostProcessRelational( final DifferenceReport report, 
                                                        final RelationTracker relationTracker ) {
        ArgCheck.isNotNull(report);
        ArgCheck.isNotNull(relationTracker);
        CompareUtil.skipDeletesOfStandardContainers(report);
        final Mapping root = report.getMapping();
        doPostProcessRelational(root,relationTracker);
        return report;
    }
    
    protected void doPostProcessRelational( final Mapping mapping, final RelationTracker relationTracker ) {
        // See if this mapping has any deletions ...
        final List nestedDeletes = new LinkedList();
        final List nestedAdds = new LinkedList();
        final List nestedOthers = new LinkedList();
        final List nestedMappings = mapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            final DifferenceDescriptor descriptor = CompareUtil.getDifferenceDescriptor(nestedMapping);
            if ( descriptor != null ) {
                if ( descriptor.isDeletion() ) {
                    nestedDeletes.add(nestedMapping);
                } else if ( descriptor.isAddition() ) {
                    nestedAdds.add(nestedMapping);
                } else {
                    nestedOthers.add(nestedMapping);
                }
            } else {
                nestedOthers.add(nestedMapping);
            }
        }

        // If there are any deletes below this mapping, find out whether this mapping has an output 
        // the was generated from a UML2 object that was fully selected or partially selected ...
        if ( nestedDeletes.size() != 0 ) {
            boolean fullySelected = true;
            if ( mapping.getNestedIn() != null ) {
                // This mapping is not the root mapping, and so should have at least one output ...
                // Look at the outputs (generated objects) ...
                final Iterator outputIter = mapping.getOutputs().iterator();
                while (fullySelected && outputIter.hasNext()) {
                    final EObject output = (EObject)outputIter.next();
                    fullySelected = isFullySelected(output,relationTracker);    
                    // stop as soon as we find a non-fully-selected
                }
            } else {
                // This mapping is the root mapping, so check whether the resource is selected ...
                final ModelResource obj = this.getRelationalOutputModel();
                fullySelected = isFullySelected(obj);
            }

            // If not fully selected, then get rid of the deletes below this mapping ...
            if ( !fullySelected ) {
                mapping.getNested().removeAll(nestedDeletes);
            }
        }
        
        // Call this method recursively on all the 'other' nested mappings (changes, changes below) ...
        final Iterator nestedOtherIter = nestedOthers.iterator();
        while (nestedOtherIter.hasNext()) {
            final Mapping nestedMapping = (Mapping)nestedOtherIter.next();
            doPostProcessRelational(nestedMapping,relationTracker);
        }
    }
    
    /**
     * Method that is invoked by the specialized Relationship {@link ModelGenerator} created in 
     * {@link #doCreateModelGenerator()}.  This method can be overridden if the
     * difference report is to be modified or changed before the ModelGenerator
     * exposes it via the {@link ModelGenerator#getDifferenceReports()} method.
     * <p>
     * This implementation attempts to correct for the case when an existing model is
     * being updated with a subset of the {@link #getModelWorkspaceUmlInputSelections() UML2 model}.
     * </p>
     * @param report the difference report; may not be null
     * @param relationTracker the {@link RelationshipTracker} that was used to generate the output model
     * (this has only the relationships between the relational objects generated by this instance and
     * the selected UML2 objects); may not be null
     */
    protected DifferenceReport doPostProcessRelationship( final DifferenceReport report, 
                                                          final RelationTracker relationTracker ) {
        ArgCheck.isNotNull(report);
        ArgCheck.isNotNull(relationTracker);
        CompareUtil.skipDeletesOfStandardContainers(report);
        final Mapping root = report.getMapping();
        doPostProcessRelationship(root,relationTracker);
        
        return report;
    }
    
    protected void doPostProcessRelationship( final Mapping mapping, final RelationTracker relationTracker ) {
        // See if this mapping has any deletions ...
        final List nestedDeletes = new LinkedList();
        final List nestedAdds = new LinkedList();
        final List nestedOthers = new LinkedList();
        final List nestedMappings = mapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            final DifferenceDescriptor descriptor = CompareUtil.getDifferenceDescriptor(nestedMapping);
            if ( descriptor != null ) {
                if ( descriptor.isDeletion() ) {
                    nestedDeletes.add(nestedMapping);
                } else if ( descriptor.isAddition() ) {
                    nestedAdds.add(nestedMapping);
                } else {
                    nestedOthers.add(nestedMapping);
                }
            } else {
                nestedOthers.add(nestedMapping);
            }
        }

        // If there are any deletes below this mapping, find out whether this mapping has an output 
        // the was generated from a UML2 object that was fully selected or partially selected ...
        if ( nestedDeletes.size() != 0 ) {
            // Iterate through the deletes and skip deleting any Relationships that has a UML source
            // that is not selected ...
            final Iterator nestedDeleteIter = nestedDeletes.iterator();
            while (nestedDeleteIter.hasNext()) {
                final Mapping nestedDelete = (Mapping)nestedDeleteIter.next();
                boolean selected = false;
                // Get the inputs of the mapping (these are the deletes) ...
                final EObject deletedObject = nestedDelete.getInputs().get(0);
                if ( deletedObject instanceof Relationship ) {
                    final Relationship relationship = (Relationship)deletedObject;
                    // Get the UML source ...
                    final List sources = relationship.getSources();
                    final Iterator sourceIter = sources.iterator();
                    while (!selected && sourceIter.hasNext()) {
                        final EObject source = (EObject)sourceIter.next();
                        if ( source instanceof Element ) {
                            final Element umlSource = (Element)source;
                            final int selectionMode = this.getModelWorkspaceUmlInputSelections().getSelectionMode(umlSource);
                            if ( selectionMode == ModelWorkspaceSelections.SELECTED ) {
                                selected = true;
                            }
                        }
                    }
                    // Check the targets (in case the relationship objects are changed)
                    final List targets = relationship.getSources();
                    final Iterator targetIter = targets.iterator();
                    while (!selected && targetIter.hasNext()) {
                        final EObject source = (EObject)targetIter.next();
                        if ( source instanceof Element ) {
                            final Element umlSource = (Element)source;
                            final int selectionMode = this.getModelWorkspaceUmlInputSelections().getSelectionMode(umlSource);
                            if ( selectionMode == ModelWorkspaceSelections.SELECTED ) {
                                selected = true;
                            }
                        }
                    }
                }
                // If the UML object referenced by the relationship is selected, then keep the delete
                // (because new ones will be added).  However, if the UML object referenced by the
                // relationship is NOT selected, then don't delete it ...
                if ( !selected ) {
                    nestedDelete.setNestedIn(null); // remove from it's parent ...
                }
            } 
        }
        
        // Call this method recursively on all the 'other' nested mappings (changes, changes below) ...
        final Iterator nestedOtherIter = nestedOthers.iterator();
        while (nestedOtherIter.hasNext()) {
            final Mapping nestedMapping = (Mapping)nestedOtherIter.next();
            doPostProcessRelationship(nestedMapping,relationTracker);
        }
    }
    
    protected void doPostRelationshipMerge() {
        // Workaround for defect 12917 (caused by defect 12793): 
        // Find the one RelationshipType that is in the relationships model and
        // make sure it has a supertype.
        final ModelResource relationshipModel = this.getRelationshipModel();
        if ( relationshipModel != null && relationshipModel.exists() ) {
            try {
                // Find the first generated type ...
                final List roots = relationshipModel.getEObjects();
                final Iterator iter = roots.iterator();
                while (iter.hasNext()) {
                    final EObject rootObj = (EObject)iter.next();
                    if ( rootObj instanceof RelationshipType ) {
                        final RelationshipType type = (RelationshipType)rootObj;
                        if ( type.getSuperType() == null ) {
                            final RelationshipTypeManager typeMgr = RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager();
                            final String manTypeName = RelationshipTypeManager.Names.MANIFESTATION;
                            final RelationshipType manifestationType = typeMgr.getBuiltInRelationshipType(manTypeName);
                            if ( manifestationType != null ) {
                                type.setSuperType(manifestationType);
                            } else {
                                type.setSuperType(typeMgr.getAnyRelationshipType());
                            }
                            break;
                        }
                    }
                }
            } catch (ModelWorkspaceException e) {
                Uml2ModelGeneratorPlugin.Util.log(e);
            }
        }
        
    }
    


    /**
     * Determine whether the supplied input of the supplied Mapping corresponds to a UML2 object
     * that is {@link #getModelWorkspaceUmlInputSelections() fully selected}.
     * @param inputObject
     * @param mapping
     * @return
     */
    protected boolean isFullySelected( final EObject outputObject, final RelationTracker relationTracker ) {
        // Use the relation tracker to find the corresponding UML2 object ...
        final EObject umlObj = relationTracker.getGeneratedFrom(outputObject);
        if ( umlObj != null ) {
            final int selectionMode = this.getModelWorkspaceUmlInputSelections().getSelectionMode(umlObj);
            if ( selectionMode == ModelWorkspaceSelections.SELECTED ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether the supplied input of the supplied Mapping corresponds to a UML2 object
     * that is {@link #getModelWorkspaceUmlInputSelections() fully selected}.
     * @param inputObject
     * @param mapping
     * @return
     */
    protected boolean isFullySelected( final ModelResource outputObject ) {
        final int selectionMode = this.getModelWorkspaceUmlInputSelections().getSelectionMode(outputObject);
        if ( selectionMode == ModelWorkspaceSelections.SELECTED ) {
            return true;
        }
        return false;
    }

    /**
     * @param resource
     * @return
     */
    private Resource doGetEmfResource(IFile resource) throws CoreException {
        final IPath path = resource.getLocation();
        final URI uri = URI.createFileURI(path.toString());
        return ModelerCore.getModelContainer().getResource(uri,true);
    }

    /**
     * This method configures the supplied {@link ModelWorkspaceView view} and 
     * {@link ModelWorkspaceSelections selections} objects used for the input models
     * with the appropriate filters.
     * @param view the ModelWorkspaceView of this generator; never null
     * @param selections the ModelWorkspaceSelections of this generator; never null
     */
    protected void doConfigureInputs( final ModelWorkspaceView view, final ModelWorkspaceSelections selections ) {

        // Create a filter that only allows UML2 models in the view ...
        final String uml2MetamodelUri = UMLPackage.eNS_URI;
        final ModelWorkspaceFilter viewFilter = new ModelWorkspaceFilter() {
            public boolean select(final Object parent, final Object node) {
                if (node instanceof ModelResource) {
                    try {
                        final MetamodelDescriptor mmdesc = ((ModelResource)node).getPrimaryMetamodelDescriptor();
                        if ( mmdesc != null ) {
                            final String uri = mmdesc.getNamespaceURI();
                            return uml2MetamodelUri.equals(uri);
                        }
                    } catch (final Throwable err) {
                        Uml2ModelGeneratorPlugin.Util.log(err);
                    }
                    return false;
                } else if (node instanceof ModelWorkspaceItem) {
                    return true;
                } else if (node instanceof EObject) {
                    return true;    //all objects are visible
                }
                return false;
            }
        };
        view.getModelWorkspaceFilters().add(viewFilter);
        selections.setModelWorkspaceView(view);
        
        // Create a filter for what objects in the view can be selected ...
        final ModelWorkspaceSelectionFilter selectionFilter = new ModelWorkspaceSelectionFilter() {
            public boolean isSelectable(final Object node) {
                if ( node == null ) {
                    return false;
                }
                if (node instanceof ModelWorkspaceItem) {
                    // Don't care what type of model it might be (e.g., relational, uml2, ...)
                    if( node instanceof ModelFolder )
                        return false;
                    
                    return true;
                }
                if ( node instanceof EObject ) {
                    return doCheckUml2Selectability((EObject)node);
                }
                return false;
            }
        };
        selections.getModelWorkspaceSelectionFilters().add(selectionFilter);
    }
    
    /**
     * This method configures the supplied {@link ModelWorkspaceView view} and 
     * {@link ModelWorkspaceSelections selections} objects used for the datatype models
     * with the appropriate filters.
     * @param view the ModelWorkspaceView of this generator; never null
     * @param selections the ModelWorkspaceSelections of this generator; never null
     */
    protected void doConfigureDatatypes( final ModelWorkspaceView view, final ModelWorkspaceSelections selections ) {

        // Create a filter that only allows UML2 models in the view ...
        view.setRestrictedToModelWorkspaceItemsOnly(false);
        final ModelWorkspaceFilter viewFilter = new ModelWorkspaceFilter() {
            public boolean select(final Object parent, final Object node) {
                if (node instanceof IFile) {
                    final boolean xsdFile = ModelUtil.isXsdFile((IFile)node);
                    return xsdFile;
                } else if (node instanceof ModelResource) {
                    final boolean isXsd = ((ModelResource)node).isXsd();
                    return isXsd;
                }
                if ( node == null ) {
                    return false;
                }
                if(parent instanceof ModelResource) {
                    final boolean isXsd = ((ModelResource)parent).isXsd();
                    if(isXsd) return false;
                }
                return true;    // Everything else is visible ...
            }
        };
        view.getModelWorkspaceFilters().add(viewFilter);
        selections.setModelWorkspaceView(view);
        
        // Create a filter for what objects in the view can be selected ...
        final ModelWorkspaceSelectionFilter selectionFilter = new ModelWorkspaceSelectionFilter() {
            public boolean isSelectable(final Object node) {
                if ( node instanceof EObject ) {
                    // Objects within models cannot be selected
                    return false;
                }
                return true;    // everything else (e.g., files) in the view is selectable ...
            }
        };
        selections.getModelWorkspaceSelectionFilters().add(selectionFilter);
    }
    
    /**
     * Check whether the supplied UML2 object can be selected.
     * @param uml2Object the UML2 object for which selectability is to be determined; never null
     * @return true if the user can select or deselect the supplied UML2 object, or false
     * if the user cannot select the object (i.e., because it is something in an object
     * for which the user can select/deselect).
     */
    protected boolean doCheckUml2Selectability( final EObject uml2Object ) {
        if ( !(uml2Object instanceof Element) ) {
            return false;
        }
        final EClass metaclass = uml2Object.eClass();
        final int metaclassId = metaclass.getClassifierID();
        switch (metaclassId) {
            case UMLPackage.PACKAGE:
                return true;
            case UMLPackage.CLASS:
                final EObject classOwner = uml2Object.eContainer();
                if ( classOwner == null ) {
                    return true;    // a root-level object in the model
                }
                if ( classOwner instanceof Package ) {
                    return true;    // under a package
                }
                // Otherwise, it is nested and cannot be selected/deselected on its own
                return false;
            case UMLPackage.MODEL:
                return true;
        }
        return false;
    }

}

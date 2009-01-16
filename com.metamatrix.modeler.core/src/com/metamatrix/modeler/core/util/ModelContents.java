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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.MappingRoot;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.ISafeReturningOperation;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.custom.impl.XsdModelAnnotationImpl;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramFactory;
import com.metamatrix.metamodels.diagram.DiagramPackage;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.XsdObjectExtension;
import com.metamatrix.modeler.internal.core.workspace.ModelBufferImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * The ModelContents provides access to a few of the large and distinct categories of the objects within a model, such as
 * diagrams, transformation, and annotations. It is used by constructing an instance and supplying the
 * {@link org.eclipse.emf.ecore.resource.Resource EMF Resource} for the model.
 */
public class ModelContents {

    protected static final int DEFAULT_DIAGRAM_SIZE = 8;
    protected static final int DEFAULT_TRANSFORMATION_SIZE = 3;

    // private boolean registeredResource = true;

    private final Resource resource;
    private final List transientDiagrams;
    private final boolean isXsd;

    private DiagramFactory diagramFactory;
    private TransformationFactory transformationFactory;
    private CoreFactory coreFactory;

    private DiagramContainer persistentDiagramContainer;
    private TransformationContainer persistentTransformationsContainer;
    private AnnotationContainer persistentAnnotationsContainer;
    private MappingClassSetContainer persistentMappingClassSetContainer;
    private final Object persistentDiagramLock = new Object();
    private final Object persistentTransformationLock = new Object();
    private final Object persistentAnnotationLock = new Object();
    private final Object modelAnnotationLock = new Object();
    private final Object persistentMappingClassContainerLock = new Object();

    // cached here since it is not stored in the resource contents like in XMI models
    private ModelAnnotation xsdModelAnnotation;

    /**
     * Obtain the ModelContents object that exists below a {@link ModelResource}. This may cause the resource to be
     * {@link Openable#open(IProgressMonitor) opened} if it is not already.
     * 
     * @param modelResource the model resource; may not be null
     * @return the ModelContents object
     * @throws ModelWorkspaceException if there is an error getting the contents from the resource.
     */
    public static ModelContents getModelContents( final ModelResource modelResource ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(modelResource);
        ArgCheck.isInstanceOf(ModelResourceImpl.class, modelResource);
        // Get the ModelContents wrapper/utility from the resource ...
        final ModelBufferImpl buffer = (ModelBufferImpl)((ModelResourceImpl)modelResource).getBuffer();
        return buffer.getModelContents();
    }

    /**
     * Construct an instance of ModelContents.
     * 
     * @param resource the {@link Resource EMF resource} to which this object is to provide access; may not be null
     */
    public ModelContents( final Resource resource ) {
        super();
        Assertion.isNotNull(resource);
        this.resource = resource;
        this.transientDiagrams = new LinkedList();
        this.diagramFactory = null;
        this.transformationFactory = null;
        this.coreFactory = null;
        this.isXsd = ModelUtil.isXsdFile(resource);
    }

    /**
     * Provide a protected constructor for subclasses that may not have a Resource.
     */
    protected ModelContents() {
        super();
        this.resource = null;
        this.transientDiagrams = new LinkedList();
        this.diagramFactory = null;
        this.transformationFactory = null;
        this.coreFactory = null;
        this.isXsd = resource != null ? ModelUtil.isXsdFile(resource) : false;
    }

    protected DiagramFactory getDiagramFactory() {
        if (this.diagramFactory == null) {
            this.diagramFactory = DiagramPackage.eINSTANCE.getDiagramFactory();
        }
        return this.diagramFactory;
    }

    protected TransformationFactory getTransformationFactory() {
        if (this.transformationFactory == null) {
            this.transformationFactory = TransformationPackage.eINSTANCE.getTransformationFactory();
        }
        return this.transformationFactory;
    }

    protected CoreFactory getCoreFactory() {
        if (this.coreFactory == null) {
            this.coreFactory = CorePackage.eINSTANCE.getCoreFactory();
        }
        return this.coreFactory;
    }

    /**
     * Returns resource held by this model contents. May be null;
     * 
     * @return
     * @since 4.3
     */
    public Resource getResource() {
        return this.resource;
    }

    // -------------------------------------------------------------------------
    // Diagram-related methods
    // -------------------------------------------------------------------------

    /**
     * Create a new diagram and add it to this resource, and specify whether the diagram is to be persisted.
     * 
     * @param target the "target" for the diagram; null if the diagram's target is the "model" itself
     * @param persistent true if the diagram is to be persisted in this resource, or false otherwise
     * @return the new diagram
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createDiagram() method NOTE: methods in
     *             ModelContents should not be concerned with transactions.
     */
    @Deprecated
    public Diagram createNewDiagram( final EObject target,
                                     final boolean persistent ) {
        if (isXsd) {
            return null;
        }

        Diagram diagram = null;
        // Start a txn and mark it as not significant (this operation can not be undone)
        boolean startedTxn = ModelerCore.startTxn(false, null, this);
        boolean succeeded = false;
        try {
            // Create the new diagram ...
            diagram = getDiagramFactory().createDiagram();
            if (target != null) {
                // Use the non-null target if one was passed in
                diagram.setTarget(target);
            } else {
                // Use the ModelAnnotation as the target. The convention is that diagrams that have a target
                // of the "model" itself (which isn't an EObject) have a target of the ModelAnnotation.
                // The reason for this is that transient diagrams don't have a resource, so getting the
                // resource that contains the diagram for model-level diagrams (those that appear right under
                // the model) requires having a target that is persistent. The ModelAnnotation is always
                // persistent.
                diagram.setTarget(this.getModelAnnotation());
            }

            // And add to the resource
            doSetPersistent(diagram, persistent);
            succeeded = true;
        } finally {
            // if we started the txn... commit it
            if (startedTxn) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return diagram;
    }

    /**
     * Remove the specified diagram from this resource. This method works for persistent or transient diagrams; persistent
     * diagrams can always just be removed from the {@link DiagramContainer}.
     * 
     * @param diagram the diagram; may not be null
     * @return true if the diagram was deleted from this resource, or false if it was not
     */
    public boolean delete( final Diagram diagram ) {
        if (diagram == null) {
            return false;
        }
        // Try transient first
        if (!this.transientDiagrams.remove(diagram)) {
            // Not removed from transient, so try persistent ...
            return ModelResourceContainerFactory.deleteDiagram(diagram);
        }
        return true;
    }

    /**
     * Return whether the supplied diagram is considered transient.
     * 
     * @param diagram the diagram
     * @return true if the diagram is transient and will not be written out to the {@link Resource}, or false otherwise.
     */
    public boolean isPersistent( final Diagram diagram ) {
        return diagram.eContainer() != null;
    }

    /**
     * Define whether the supplied diagram is considered persistent. This method has no effect if the diagram's persistence
     * already matches <code>persistent</code>.
     * 
     * @param diagram the diagram; may not be null
     * @param persistent true if the diagram is to be persisted in this resource, or false otherwise
     * @deprecated - use methods defined in com.metamatrix.modeler.core.internal.workspace.ModelDiagrams
     */
    @Deprecated
    public void setPersistent( final Diagram diagram,
                               boolean persistent ) {
        // If the diagram already matches the desired state ...
        if (persistent == isPersistent(diagram)) {
            return;
        }
        // It doesn't match, so set the persistence
        doSetPersistent(diagram, persistent);
    }

    /**
     * Utility method to actually make the diagram match the desired persistent state. This method does <i>not</i> check first
     * whether the diagram is persistent; it simply performs the desired request.
     * 
     * @deprecated - use methods defined in com.metamatrix.modeler.core.internal.workspace.ModelDiagrams
     */
    @Deprecated
    protected void doSetPersistent( final Diagram diagram,
                                    boolean persistent ) {
        // And add to the resource
        if (persistent) {
            // Either to the actual persistent DiagramContainer. Note that this
            // is done by setting the container on the diagram rather than adding
            // the diagram to the container's getDiagram() list - this is because
            // doing it this way ensures that the resource is notified as
            // having unsaved changes
            final DiagramContainer container = this.getDiagramContainer(true);
            try {
                ModelerCore.getModelEditor().addValue(container, diagram, container.getDiagram());
                // diagram.setDiagramContainer(container);
            } catch (ModelerCoreException err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                diagram.setDiagramContainer(container);
            }
        } else {
            // Or to the transient list
            this.transientDiagrams.add(diagram);
        }
    }

    public void addTransientDiagram( final Diagram diagram ) {
        this.transientDiagrams.add(diagram);
    }

    public void removeTransientDiagram( final Diagram diagram ) {
        this.transientDiagrams.remove(diagram);
    }

    /**
     * Get the diagram objects associated with this resource. Diagrams are created using the
     * {@link #createNewDiagram(EObject, boolean)} method. com.metamatrix.modeler.core.ModelEditor ModelEditor}.
     * <p>
     * Return a {@link List} rather than a <code>Diagram[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these diagrams into the existing children of the
     * target.
     * </p>
     * 
     * @return the {@link Diagram} instances that are in the model; never null, but possibly empty. This is a copy of the actual
     *         container, so modifications to the result will not be reflected in the model.
     */
    public List getDiagrams() {
        if (isXsd) {
            return Collections.EMPTY_LIST;
        }

        final List results = createDiagramList();

        // Add the persistent diagrams ...
        final List persistentDiags = this.getPersistentDiagrams(false);
        results.addAll(persistentDiags);

        // Add the transient diagrams ...
        results.addAll(this.transientDiagrams);

        // These don't need to be ordered
        return results;
    }

    /**
     * Get the diagram objects associated with the supplied target model object. Diagrams are created using the
     * {@link #createNewDiagram(EObject, boolean)} method.
     * <p>
     * Return a {@link List} rather than a <code>Diagram[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these diagrams into the existing children of the
     * target.
     * </p>
     * 
     * @param target the target object
     * @return the {@link Diagram} instances that are associated with the target object; never null, but possibly empty
     */
    public List getDiagrams( final EObject target ) {
        if (isXsd) {
            return Collections.EMPTY_LIST;
        }

        final List results = createDiagramList();

        // Replace the target if null
        final EObject actualTarget = (target == null ? this.getModelAnnotation() : target);

        // Add the persistent diagrams ...
        final List persistentDiags = this.getPersistentDiagrams(false);
        if (persistentDiags.size() != 0) {
            addDiagramsForTarget(persistentDiags, actualTarget, results);
        }

        // Add the transient diagrams ...
        if (this.transientDiagrams.size() != 0) {
            addDiagramsForTarget(this.transientDiagrams, actualTarget, results);
        }

        // These don't need to be ordered
        return results;
    }

    /**
     * Utility method to create a new {@link List} instance that is used to return the list of Diagram instances.
     * 
     * @return a new ArrayList with an initial available size of {@link #DEFAULT_DIAGRAM_SIZE}.
     */
    protected List createDiagramList() {
        return new ArrayList(DEFAULT_DIAGRAM_SIZE);
    }

    /**
     * Utility method to return the DiagramContainer for persistent diagrams in this resource
     * 
     * @return the DiagramContainer for this resource
     */
    public DiagramContainer getDiagramContainer( final boolean createIfNeeded ) {
        if (isXsd) {
            return null;
        }

        if (this.persistentDiagramContainer == null) {
            synchronized (this.persistentDiagramLock) {
                if (this.persistentDiagramContainer == null) {
                    // Let's see if Diagram Container exists
                    this.persistentDiagramContainer = ModelResourceContainerFactory.getDiagramContainer(getAllRootEObjects());
                    // if it does not, then we can create one if resource != null
                    if (persistentDiagramContainer == null && resource != null) {
                        this.persistentDiagramContainer = ModelResourceContainerFactory.getDiagramContainer(resource,
                                                                                                            createIfNeeded);
                    }
                }
            }
        }
        return this.persistentDiagramContainer;
    }

    /**
     * Utility method to obtain the persistent diagrams for this resource
     * 
     * @return the List of diagrams that are persistent in this resource
     */
    protected List getPersistentDiagrams( final boolean createIfNeeded ) {
        if (isXsd) {
            return Collections.EMPTY_LIST;
        }

        final DiagramContainer container = getDiagramContainer(createIfNeeded);
        if (container == null) {
            return Collections.EMPTY_LIST;
        }
        return container.getDiagram();
    }

    /**
     * Utility method to add to the supplied results list all of the diagram objects that have the supplied target.
     * 
     * @param diagrams the list of {@link Diagram} instances to query; may not be null
     * @param target the target
     * @param results the list into which are placed all {@link Diagram} instances that have the supplied target; may not be null
     */
    protected void addDiagramsForTarget( final List diagrams,
                                         final EObject target,
                                         final List results ) {
        Assertion.isNotNull(diagrams);
        Assertion.isNotNull(results);
        final Iterator diagIter = diagrams.iterator();
        while (diagIter.hasNext()) {
            final Diagram diagram = (Diagram)diagIter.next();
            if (diagram != null && isDiagramForTarget(diagram, target)) {
                results.add(diagram);
            }
        }
    }

    /**
     * Utility method to determine whether the supplied diagram has a target that matches the supplied object.
     * 
     * @param diagram the {@link Diagram}; may not be null
     * @param target the target
     * @return true if the diagram has the supplied target, or false otherwise
     */
    protected boolean isDiagramForTarget( final Diagram diagram,
                                          final EObject target ) {
        Assertion.isNotNull(diagram);
        final Object actualTarget = diagram.getTarget();
        if (actualTarget == null) {
            return target == null; // only equal if target is null, too
        }
        return diagram.getTarget().equals(target);
    }

    // -------------------------------------------------------------------------
    // Transformation-related methods
    // -------------------------------------------------------------------------

    /**
     * Utility method to create a new {@link List} instance that is used to return the list of {@link TransformationMappingRoot}
     * instances.
     * 
     * @return a new ArrayList with an initial available size of {@link #DEFAULT_TRANSFORMATION_SIZE}.
     */
    protected List createTransformationList() {
        return new ArrayList(DEFAULT_TRANSFORMATION_SIZE);
    }

    /**
     * Creates a new SQL transformation and add it to this resource.
     * 
     * @param target the "target" for the transformation
     * @return the new transformation
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createSqlTransformationMappingRoot()
     */
    @Deprecated
    public SqlTransformationMappingRoot createSqlTransformation( final EObject target ) {
        ArgCheck.isNotNull(target);
        final SqlTransformationMappingRoot t = getTransformationFactory().createSqlTransformationMappingRoot();
        t.getOutputs().add(target);
        // Defect 18433 - BML 8/31/05 - Changed to not call addNewTransformation()
        // This was not correctly adding the transformation using the ModelEditor.addValue() call
        // Utilities were added to perform this work and insure proper transaction boundaries
        // See com.metamatrix.modeler.core.util.ModelResourceContainerFactory
        return (SqlTransformationMappingRoot)addNewTransformation(target, t);
    }

    /**
     * Creates a new SQL transformation and add it to this resource.
     * 
     * @param target the "target" for the transformation
     * @return the new transformation
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createFragmentMappingRoot()
     */
    @Deprecated
    public FragmentMappingRoot createFragmentMapping( final EObject target ) {
        ArgCheck.isNotNull(target);
        final FragmentMappingRoot t = getTransformationFactory().createFragmentMappingRoot();
        // Defect 18433 - BML 8/31/05 - Changed to not call addNewTransformation()
        // This was not correctly adding the transformation using the ModelEditor.addValue() call
        // Utilities were added to perform this work and insure proper transaction boundaries
        // See com.metamatrix.modeler.core.util.ModelResourceContainerFactory
        return (FragmentMappingRoot)addNewTransformation(target, t);
    }

    /**
     * Creates a new tree-mapping transformation and add it to this resource.
     * 
     * @param target the "target" for the transformation
     * @return the new transformation
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createTreeMappingRoot()
     */
    @Deprecated
    public TreeMappingRoot createTreeMapping( final EObject target ) {
        ArgCheck.isNotNull(target);
        final TreeMappingRoot t = getTransformationFactory().createTreeMappingRoot();
        // Defect 18433 - BML 8/31/05 - Changed to not call addNewTransformation()
        // This was not correctly adding the transformation using the ModelEditor.addValue() call
        // Utilities were added to perform this work and insure proper transaction boundaries
        // See com.metamatrix.modeler.core.util.ModelResourceContainerFactory
        return (TreeMappingRoot)addNewTransformation(target, t);
    }

    /**
     * Add the new transformation and add it to this resource.
     * 
     * @param target the "target" for the transformation
     * @return the new transformation
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory methods for constructing mapping root
     *             objects.
     */
    @Deprecated
    public TransformationMappingRoot addNewTransformation( final EObject target,
                                                           final TransformationMappingRoot newMappingRoot ) {
        ArgCheck.isNotNull(target);

        // Set the target of the transformation
        newMappingRoot.setTarget(target);

        // NOTE: This WILL NOT create a Command that will end up in the UNDO Event's CompoundCommand.
        // the ModelEditor.addValue() method insures this will happen.
        getTransformationContainer(true).getTransformationMappings().add(newMappingRoot);

        // Mark the resource as being modified, since the relationship is NOT bi-directional
        setModified(true);
        return newMappingRoot;
    }

    /**
     * Get the transformation objects associated with the supplied model object that is the output of the transformations.
     * Transformations are created using the {@link #createNewTransformation(EObject)} method.
     * <p>
     * Return a {@link List} rather than a <code>TransformationMappingRoot[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these transformations into the existing children
     * of the target.
     * </p>
     * 
     * @param output the object that is the output of the transformation
     * @return the {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot} instances that are associated with
     *         the output object; never null, but possibly empty
     */
    public List getTransformationsForOutput( final EObject output ) {
        final List results = createTransformationList();

        // Add the persistent diagrams ...
        final List transformations = new ArrayList(this.getTransformations());
        if (transformations.size() != 0) {
            addTransformationsForOutput(transformations, output, results);
        }

        // These don't need to be ordered
        return results;
    }

    /**
     * Get the transformation objects associated with the supplied model object that is the input source of the transformations.
     * Transformations are created using the {@link #createNewTransformation(EObject)} method.
     * <p>
     * Return a {@link List} rather than a <code>TransformationMappingRoot[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these transformations into the existing children
     * of the target.
     * </p>
     * 
     * @param input the object that is the input of the transformation
     * @return the {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot} instances that are associated with
     *         the input object; never null, but possibly empty
     */
    public List getTransformationsForInput( final EObject input ) {
        final List results = createTransformationList();

        // Add the persistent diagrams ...
        final List transformations = this.getTransformations();
        if (transformations.size() != 0) {
            addTransformationsForInput(transformations, input, results);
        }

        // These don't need to be ordered
        return results;
    }

    /**
     * Get the transformation objects associated with the supplied model object that is the target of the transformations.
     * Transformations are created using the {@link #createNewTransformation(EObject)} method.
     * <p>
     * Return a {@link List} rather than a <code>TransformationMappingRoot[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these transformations into the existing children
     * of the target.
     * </p>
     * 
     * @param output the object that is the output of the transformation
     * @return the {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot} instances that are associated with
     *         the output object; never null, but possibly empty
     */
    public List getTransformations( final EObject target ) {
        final List results = createTransformationList();

        // Add the persistent diagrams ...
        final List transformations = this.getTransformations();
        if (transformations.size() != 0) {
            addTransformationsForTarget(transformations, target, results);
        }

        // These don't need to be ordered
        return results;
    }

    /**
     * Get the transformation objects associated with the supplied model object. Transformations are created using the
     * {@link #createNewTransformation(EObject)} method.
     * <p>
     * Return a {@link List} rather than a <code>Diagram[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these transformations into the existing children
     * of the target.
     * </p>
     * 
     * @return the {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot} instances that are in the model;
     *         never null, but possibly empty. This list is modifiable; changes to it will be reflected in the model.
     */
    public List getTransformations() {
        final TransformationContainer container = this.getTransformationContainer(false);
        if (container == null) {
            return new ArrayList();
        }
        return container.getTransformationMappings();
    }

    /**
     * Remove the specified transformation from this resource.
     * 
     * @param transformation the transformation; may not be null
     * @return true if the transformation was deleted from this resource, or false if it was not
     */
    public boolean delete( final TransformationMappingRoot transformation ) {
        if (ModelResourceContainerFactory.deleteTransformation(transformation)) {
            // Mark the resource as being modified, since the relationship is NOT bi-directional
            setModified(true);
            return true;
        }
        return false;
    }

    protected void setModified( final boolean modified ) {
        this.resource.setModified(modified);
    }

    protected URI getUri() {
        return this.resource.getURI();
    }

    /**
     * Utility method to return the TransformationContainer for transformation in this resource
     * 
     * @return the TransformationContainer for this resource
     */
    public TransformationContainer getTransformationContainer( final boolean createIfNeeded ) {
        if (this.persistentTransformationsContainer == null) {
            synchronized (this.persistentTransformationLock) {
                if (this.persistentTransformationsContainer == null) {
                    // Let's see if Transformations Container exists
                    this.persistentTransformationsContainer = ModelResourceContainerFactory.getTransformationContainer(getAllRootEObjects());
                    // if it does not, then we can create one if resource != null
                    if (persistentTransformationsContainer == null && resource != null) {
                        this.persistentTransformationsContainer = ModelResourceContainerFactory.getTransformationContainer(resource,
                                                                                                                           createIfNeeded);
                    }
                }
            }
        }
        return this.persistentTransformationsContainer;
    }

    /**
     * Utility method to add to the supplied results list all of the transformation objects that have the supplied output.
     * 
     * @param transformations the list of {@link TransformationMappingRoot} instances to query; may not be null
     * @param output the output
     * @param results the list into which are placed all {@link TransformationMappingRoot} instances that have the supplied
     *        output; may not be null
     */
    protected void addTransformationsForOutput( final List transformations,
                                                final EObject output,
                                                final List results ) {
        Assertion.isNotNull(transformations);
        Assertion.isNotNull(results);
        final Iterator diagIter = transformations.iterator();
        while (diagIter.hasNext()) {
            final MappingRoot mappingRoot = (MappingRoot)diagIter.next();
            if (mappingRoot != null && isOutputOfTransformation(mappingRoot, output)) {
                results.add(mappingRoot);
            }
        }
    }

    /**
     * Utility method to add to the supplied results list all of the transformation objects that have the supplied input.
     * 
     * @param transformations the list of {@link TransformationMappingRoot} instances to query; may not be null
     * @param input the input
     * @param results the list into which are placed all {@link TransformationMappingRoot} instances that have the supplied
     *        output; may not be null
     */
    protected void addTransformationsForInput( final List transformations,
                                               final EObject input,
                                               final List results ) {
        Assertion.isNotNull(transformations);
        Assertion.isNotNull(results);
        final Iterator diagIter = transformations.iterator();
        while (diagIter.hasNext()) {
            final MappingRoot mappingRoot = (MappingRoot)diagIter.next();
            if (mappingRoot != null && isInputOfTransformation(mappingRoot, input)) {
                results.add(mappingRoot);
            }
        }
    }

    /**
     * Utility method to add to the supplied results list all of the transformation objects that have the supplied target.
     * 
     * @param transformations the list of {@link TransformationMappingRoot} instances to query; may not be null
     * @param output the output
     * @param results the list into which are placed all {@link TransformationMappingRoot} instances that have the supplied
     *        output; may not be null
     */
    protected void addTransformationsForTarget( final List transformations,
                                                final EObject target,
                                                final List results ) {
        Assertion.isNotNull(transformations);
        Assertion.isNotNull(results);

        // Make copy of transformations
        // Needed to prevent concurrent modifications
        List copyOfTransforms = new ArrayList(transformations);

        final Iterator transformIter = copyOfTransforms.iterator();
        while (transformIter.hasNext()) {
            final MappingRoot mappingRoot = (MappingRoot)transformIter.next();
            if (mappingRoot != null && mappingRoot instanceof TransformationMappingRoot) {
                final TransformationMappingRoot tmr = (TransformationMappingRoot)mappingRoot;
                // If the target of the mapping root is null
                if (tmr.getTarget() == null) {
                    if (target == null) {
                        // And the target is null, then it's a match
                        results.add(tmr);
                    }
                    // Else this is not a match, so go on to the next one ...
                    break;
                }
                // the root's target is non-null, so compare to the target ...
                if (tmr.getTarget().equals(target)) {
                    // Match, so add to the results
                    results.add(mappingRoot);
                }
            }
        }
    }

    /**
     * Utility method to determine whether the supplied transformation has an output that matches the supplied object.
     * 
     * @param mappingRoot the {@link MappingRoot}; may not be null
     * @param output the output
     * @return true if the transformation has the supplied output, or false otherwise
     */
    protected boolean isOutputOfTransformation( final MappingRoot mappingRoot,
                                                final EObject output ) {
        Assertion.isNotNull(mappingRoot);
        return mappingRoot.getOutputs().contains(output);
    }

    /**
     * Utility method to determine whether the supplied transformation has an input that matches the supplied object.
     * 
     * @param mappingRoot the {@link MappingRoot}; may not be null
     * @param output the output
     * @return true if the transformation has the supplied input, or false otherwise
     */
    protected boolean isInputOfTransformation( final MappingRoot mappingRoot,
                                               final EObject input ) {
        Assertion.isNotNull(mappingRoot);
        return mappingRoot.getInputs().contains(input);
    }

    // -------------------------------------------------------------------------
    // Annotation-related methods
    // -------------------------------------------------------------------------

    /**
     * Get the annotation associated with the supplied model object. Annotations are created using the
     * {@link #createNewAnnotation(EObject)} method.
     * <p>
     * 
     * @param annotatedObject the object that is annotated
     * @return the {@link Annotation} instance, or null if there is no Annotation for the supplied object
     */
    public ModelAnnotation getModelAnnotation() {
        if (this.isXsd) {
            if (this.xsdModelAnnotation != null) {
                return this.xsdModelAnnotation;
            }
        } else {
            // check for model annotation as a root eObject for the resource
            // Make a copy of the root objects (to prevent concurrent mod exception)
            final List rootObjs = new ArrayList(getAllRootEObjects());

            // Look under the roots for the ModelAnnotation object
            for (final Iterator i = rootObjs.iterator(); i.hasNext();) {
                final EObject rootObj = (EObject)i.next();
                if (rootObj instanceof ModelAnnotation) {
                    return (ModelAnnotation)rootObj;
                }
            }
        }

        // Create a new model annotation
        return (ModelAnnotation)TransactionUtil.executeNonUndoable(new ISafeReturningOperation() {

            public Object execute() {
                return createModelAnnotation();
            }
        }, this);
    }

    ModelAnnotation createModelAnnotation() {
        synchronized (this.modelAnnotationLock) {
            ModelAnnotation newModelAnnotation;
            if (this.isXsd) {
                // Defect 12555: New up a XsdModelAnnotationImpl and populate it
                newModelAnnotation = new XsdModelAnnotationImpl(this.resource);
                newModelAnnotation.setPrimaryMetamodelUri(XSDPackage.eNS_URI);
                newModelAnnotation.setModelType(ModelType.TYPE_LITERAL);
                newModelAnnotation.setMaxSetSize(0);
                newModelAnnotation.setSupportsDistinct(false);
                newModelAnnotation.setSupportsJoin(false);
                newModelAnnotation.setSupportsOrderBy(false);
                newModelAnnotation.setSupportsOuterJoin(false);
                newModelAnnotation.setSupportsWhereAll(false);
                newModelAnnotation.setVisible(false);

                // create a UUID and store it in the ID maps in case an ID-related lookup of this annotation is done later
                if (this.resource instanceof XResource) {
                    this.xsdModelAnnotation = newModelAnnotation;
                    ((XResource)this.resource).setUuid(newModelAnnotation, IDGenerator.getInstance().create().toString());
                }

                if (this.resource instanceof XSDResourceImpl) {
                    try {
                        final XSDResourceImpl xsdResource = (XSDResourceImpl)this.resource;
                        if (xsdResource.getSchema() != null) {
                            XPackage extPackage = XsdObjectExtension.getExtensionPackage(xsdResource);
                            if (extPackage != null) {
                                newModelAnnotation.setExtensionPackage(extPackage);
                            }
                        }
                    } catch (ModelerCoreException err) {
                        ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    }
                }
            } else {
                // If there is no model annotation, then create one ...
                newModelAnnotation = getCoreFactory().createModelAnnotation();
                getAllRootEObjects().add(0, newModelAnnotation);
                // Mark the resource as having changed
                setModified(true);
            }
            return newModelAnnotation;
        }
    }

    /**
     * Create a new annotation and add it to this resource.
     * 
     * @param annotatedObject the object for which an annotation is to be created; should be null only if the annotation is for
     *        the {@link ModelAnnotation model itself}.
     * @return the new annotation
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createNewAnnotation(target,
     *             annotationContainer)
     */
    @Deprecated
    public Annotation createNewAnnotation( final EObject annotatedObject ) {
        return ModelResourceContainerFactory.createNewAnnotation(annotatedObject, getAnnotationContainer(true));
    }

    /**
     * Get the annotation associated with the supplied model object. Annotations are created using the
     * {@link #createNewAnnotation(EObject)} method.
     * <p>
     * 
     * @param annotatedObject the object that is annotated
     * @return the {@link Annotation} instance, or null if there is no Annotation for the supplied object
     */
    public Annotation getAnnotation( final EObject annotatedObject ) {
        if (annotatedObject == null) {
            return null;
        }
        final AnnotationContainer container = getAnnotationContainer(false);
        if (container == null) {
            return null;
        }
        final Annotation existing = container.findAnnotation(annotatedObject);
        if (existing != null) {
            return existing;
        }
        //
        // final Iterator iter = new ArrayList(getAnnotations()).iterator();
        // while (iter.hasNext()) {
        // final Annotation annotation = (Annotation)iter.next();
        // if ( annotation.getAnnotatedObject() == annotatedObject ) {
        // return annotation;
        // }
        // }
        return null;
    }

    /**
     * Get the transformation objects associated with the supplied model object. Transformations are created using the
     * {@link #createNewTransformation(EObject)} method.
     * <p>
     * Return a {@link List} rather than a <code>Diagram[]</code>, since the
     * {@link org.eclipse.jface.viewers.ITreeContentProvider} will have to merge these transformations into the existing children
     * of the target.
     * </p>
     * 
     * @return the {@link com.metamatrix.metamodels.transformation.TransformationMappingRoot} instances that are in the model;
     *         never null, but possibly empty. This list is modifiable; changes to it will be reflected in the model.
     */
    public List getAnnotations() {
        final AnnotationContainer container = getAnnotationContainer(false);
        if (container == null) {
            return Collections.EMPTY_LIST;
        }
        return container.getAnnotations();
    }

    /**
     * Remove the specified annotation from this resource.
     * 
     * @param annotation the annotation; may not be null
     * @return true if the annotation was deleted from this resource, or false if it was not
     */
    public boolean delete( final Annotation annotation ) {
        return ModelResourceContainerFactory.deleteAnnotation(annotation);
    }

    /**
     * Utility method to return the TransformationContainer for transformation in this resource
     * 
     * @return the TransformationContainer for this resource
     */
    public AnnotationContainer getAnnotationContainer( final boolean createIfNeeded ) {
        if (isXsd) {
            return null;
        }

        if (this.persistentAnnotationsContainer == null) {
            synchronized (this.persistentAnnotationLock) {
                if (this.persistentAnnotationsContainer == null) {
                    // Let's see if Annotations Container exists
                    this.persistentAnnotationsContainer = ModelResourceContainerFactory.getAnnotationContainer(getAllRootEObjects());
                    // if it does not, then we can create one if resource != null
                    if (persistentAnnotationsContainer == null && resource != null) {
                        this.persistentAnnotationsContainer = ModelResourceContainerFactory.getAnnotationContainer(resource,
                                                                                                                   createIfNeeded);
                    }
                }
            }
        }
        return this.persistentAnnotationsContainer;
    }

    // -------------------------------------------------------------------------
    // MappingClassSet-related methods
    // -------------------------------------------------------------------------

    /**
     * Create a new {@link MappingClassSet} and add it to this resource.
     * 
     * @param target the "target" for the mapping class sets; may not be null
     * @return the new MappingClassSet object
     * @deprecated - use com.metamatrix.modeler.core.util.ModelResourceContainerFactory.getMappingClassSet() method
     */
    @Deprecated
    public MappingClassSet createNewMappingClassSet( final EObject target ) {
        ArgCheck.isNotNull(target);
        // Create the new mapping class set ...
        final MappingClassSet mcSet = getTransformationFactory().createMappingClassSet();
        mcSet.setTarget(target);

        // And add to the resource.
        // Defect 18433 - BML 8/31/05 - The following call was adding confusion to the transaction
        // boundaries. A new utility class was created to correctly handle creation of this object
        // See com.metamatrix.modeler.core.util.ModelResourceContainerFactory
        getMappingClassSetContainer(true).getMappingClassSets().add(mcSet);
        return mcSet;
    }

    /**
     * Get the {@link MappingClassSet} objects associated with the supplied target model object. MappingClassSets are created
     * using the {@link #createNewMappingClassSet(EObject)} method.
     * 
     * @param target the target object; may be null, meaning find all {@link MappingClassSet} instances that have no target
     * @return the {@link MappingClassSet} instances that are associated with the target object; never null, but possibly empty
     */
    public List getMappingClassSets( final EObject target ) {
        final List result = new ArrayList();
        final Iterator iter = new ArrayList(getMappingClassSets()).iterator();
        while (iter.hasNext()) {
            final MappingClassSet mcSet = (MappingClassSet)iter.next();
            if (mcSet.getTarget() != null && mcSet.getTarget().equals(target)) {
                result.add(mcSet);
            }
        }
        return result;
    }

    /**
     * Get all the {@link MappingClassSet} objects known by this resource. MappingClassSets are created using the
     * {@link #createNewMappingClassSet(EObject)} method.
     * 
     * @return the {@link MappingClassSet} instances for this resource; never null, but possibly empty
     */
    public List getMappingClassSets() {
        final MappingClassSetContainer container = getMappingClassSetContainer(false);
        if (container == null) {
            return Collections.EMPTY_LIST;
        }
        return container.getMappingClassSets();
    }

    /**
     * Remove the specified {@link MappingClassSet} from this resource.
     * 
     * @param mappingClassSet the {@link MappingClassSet} to be deleted; may not be null
     * @return true if the {@link MappingClassSet} was deleted from this resource, or false if it was not
     */
    public boolean delete( final MappingClassSet mappingClassSet ) {
        return ModelResourceContainerFactory.deleteMappingClassSet(mappingClassSet);
    }

    /**
     * Utility method to return the TransformationContainer for transformation in this resource
     * 
     * @return the TransformationContainer for this resource
     */
    public MappingClassSetContainer getMappingClassSetContainer( final boolean createIfNeeded ) {
        if (this.persistentMappingClassSetContainer == null) {
            synchronized (this.persistentMappingClassContainerLock) {
                if (this.persistentMappingClassSetContainer == null) {
                    // Let's see if Annotations Container exists
                    this.persistentMappingClassSetContainer = ModelResourceContainerFactory.getMappingClassSetContainer(getAllRootEObjects());
                    // if it does not, then we can create one if resource != null
                    if (persistentMappingClassSetContainer == null && resource != null) {
                        this.persistentMappingClassSetContainer = ModelResourceContainerFactory.getMappingClassSetContainer(resource,
                                                                                                                            createIfNeeded);
                    }
                }
            }
        }
        return this.persistentMappingClassSetContainer;
    }

    // -------------------------------------------------------------------------
    // Other helper methods
    // -------------------------------------------------------------------------

    /**
     * Return all of the root objects.
     * 
     * @return the list of all root objects in the resource. This is is modifiable.
     */
    public List getAllRootEObjects() {
        return this.resource.getContents();
    }

    /**
     * Return only those root objects that are primary objects. Non-primary objects (i.e., secondary objects) include:
     * <ul>
     * <li>{@link DiagramContainer}</li>
     * <li>{@link TransformationContainer}</li>
     * <li>{@link AnnotationContainer}</li>
     * <li>{@link ModelAnnotation}</li>
     * <li>{@link MappingClassSetContainer}</li>
     * </ul>
     */
    public List getEObjects() {
        // Create a copy of the complete list of roots ...
        final List allRoots = getAllRootEObjects();
        final LinkedList roots = new LinkedList();

        // Add only the primary objects objects ...
        final Iterator iter = allRoots.iterator();
        while (iter.hasNext()) {
            final Object rootObj = iter.next();
            // See if the object is a "secondary" object ...
            if (rootObj instanceof DiagramContainer || rootObj instanceof TransformationContainer
                || rootObj instanceof AnnotationContainer || rootObj instanceof ModelAnnotation
                || rootObj instanceof MappingClassSetContainer) {
                continue;
            }
            // It's not secondary, so add it ...
            roots.add(rootObj);
        }
        return roots;
    }

    // /**
    // * @return Returns the registeredResource.
    // * @since 4.3
    // */
    // public boolean isRegisteredResource() {
    // return this.registeredResource;
    // }
    //
    //
    // /**
    // * @param registeredResource The registeredResource to set.
    // * @since 4.3
    // */
    // public void setRegisteredResource(boolean registeredResource) {
    // this.registeredResource = registeredResource;
    // }

}

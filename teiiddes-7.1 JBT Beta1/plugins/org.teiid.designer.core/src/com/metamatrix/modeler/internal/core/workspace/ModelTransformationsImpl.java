/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelTransformations;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.Openable;

/**
 * ModelDiagramsImpl
 */
public class ModelTransformationsImpl extends NonOpenableModelWorkspaceItemImpl implements ModelTransformations {
    private static final String CREATE_TRANSFORMATION_ERROR = "ModelTransformationsImpl.createTransformationMessage"; //$NON-NLS-1$

    protected static final String DEFAULT_NAME = ModelerCore.Util.getString("ModelTransformationsImpl.defaultName"); //$NON-NLS-1$

    /**
     * Constructor needed for test cases.
     */
    ModelTransformationsImpl() {
        super(TRANSFORMATIONS, null, DEFAULT_NAME);
    }

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelTransformationsImpl( final ModelWorkspaceItem parent ) {
        super(TRANSFORMATIONS, parent, DEFAULT_NAME);
    }

    public ModelResource getModelResource() {
        return (ModelResource)this.getParent();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getResource()
     */
    public IResource getResource() {
        return this.getModelResource().getResource();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    public IResource getUnderlyingResource() {
        return getResource();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getPath()
     */
    public IPath getPath() {
        return this.getModelResource().getPath();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    @Override
    public boolean hasChildren() {
        return false;
    }

    protected ModelBufferImpl getModelBuffer() throws ModelWorkspaceException {
        final ModelResourceImpl modelResourceImpl = (ModelResourceImpl)this.getModelResource();
        return (ModelBufferImpl)modelResourceImpl.getBuffer(); // loads if req'd
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    public List getTransformations( final EObject target ) throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getTransformations(target);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    public List getTransformations() throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getTransformations();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    public SqlTransformationMappingRoot createNewSqlTransformation( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    public FragmentMappingRoot createNewFragmentMapping( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewFragmentMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    public TreeMappingRoot createNewTreeMapping( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewTreeMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    public TransformationMappingRoot addNewTransformation( final EObject target,
                                                           final TransformationMappingRoot newMappingRoot )
        throws ModelWorkspaceException {

        TransformationContainer tc = ModelResourceContainerFactory.getTransformationContainer(getModelResource().getEmfResource(),
                                                                                              true);
        newMappingRoot.setTarget(target);

        // Now that we have the root, have to add the value correctly
        try {
            ModelerCore.getModelEditor().addValue(tc, newMappingRoot, tc.getTransformationMappings());
        } catch (ModelerCoreException err) {
            ModelerCore.Util.log(IStatus.ERROR, err, ModelerCore.Util.getString(CREATE_TRANSFORMATION_ERROR, target));
        }

        return newMappingRoot;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelDiagrams#delete(com.metamatrix.metamodels.diagram.Diagram)
     */
    public boolean delete( final TransformationMappingRoot transformation ) {
        return ModelResourceContainerFactory.deleteTransformation(transformation);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    @Override
    public Openable getOpenable() {
        return getOpenableParent();
    }

}

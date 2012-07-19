/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.metamodels.transformation.FragmentMappingRoot;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;


/**
 * ModelDiagramsImpl
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getResource()
     */
    @Override
	public IResource getResource() {
        return this.getModelResource().getResource();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
	public IResource getUnderlyingResource() {
        return getResource();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     */
    @Override
	public IPath getPath() {
        return this.getModelResource().getPath();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#hasChildren()
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
     * @see org.teiid.designer.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getTransformations( final EObject target ) throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getTransformations(target);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getTransformations() throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getTransformations();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public SqlTransformationMappingRoot createNewSqlTransformation( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public FragmentMappingRoot createNewFragmentMapping( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewFragmentMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public TreeMappingRoot createNewTreeMapping( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewTreeMappingRoot(target, getModelResource().getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelResource#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
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
     * @see org.teiid.designer.core.workspace.ModelDiagrams#delete(org.teiid.designer.metamodels.diagram.Diagram)
     */
    @Override
	public boolean delete( final TransformationMappingRoot transformation ) {
        return ModelResourceContainerFactory.deleteTransformation(transformation);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    @Override
    public Openable getOpenable() {
        return getOpenableParent();
    }

}

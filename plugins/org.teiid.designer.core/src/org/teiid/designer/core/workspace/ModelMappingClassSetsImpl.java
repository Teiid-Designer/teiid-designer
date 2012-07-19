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
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.metamodels.transformation.MappingClassSet;


/**
 * ModelMappingClassSetsImpl
 *
 * @since 8.0
 */
public class ModelMappingClassSetsImpl extends NonOpenableModelWorkspaceItemImpl implements ModelMappingClassSets {

    protected static final String DEFAULT_NAME = ModelerCore.Util.getString("ModelMappingClassSetsImpl.defaultName"); //$NON-NLS-1$

    /**
     * Constructor needed for test cases.
     */
    ModelMappingClassSetsImpl() {
        super(MAPPING_CLASS_SETS, null, DEFAULT_NAME);
    }

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelMappingClassSetsImpl( final ModelWorkspaceItem parent ) {
        super(MAPPING_CLASS_SETS, parent, DEFAULT_NAME);
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
     * @see org.teiid.designer.core.workspace.ModelMappingClassSets#createNewMappingClassSet(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public MappingClassSet createNewMappingClassSet( EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewMappingClassSet(target, this.getModelResource().getEmfResource());
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelMappingClassSets#getMappingClassSets(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getMappingClassSets( EObject target ) throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getMappingClassSets(target);
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelMappingClassSets#getMappingClassSets()
     */
    @Override
	public List getMappingClassSets() throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getMappingClassSets();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelMappingClassSets#delete(org.teiid.designer.metamodels.transformation.MappingClassSet)
     */
    @Override
	public boolean delete( MappingClassSet mappingClassSet ) {
        return ModelResourceContainerFactory.deleteMappingClassSet(mappingClassSet);
    }

}

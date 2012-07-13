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
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.metamodels.diagram.Diagram;


/**
 * ModelDiagramsImpl
 */
public class ModelDiagramsImpl extends NonOpenableModelWorkspaceItemImpl implements ModelDiagrams {
    protected static final String DEFAULT_NAME = ModelerCore.Util.getString("ModelDiagramsImpl.defaultName"); //$NON-NLS-1$

    /**
     * Constructor needed for test cases.
     */
    ModelDiagramsImpl() {
        super(DIAGRAMS, null, DEFAULT_NAME);
    }

    /**
     * Construct an instance of ModelProjectImpl.
     * 
     * @since 4.0
     */
    public ModelDiagramsImpl( final ModelWorkspaceItem parent ) {
        super(DIAGRAMS, parent, DEFAULT_NAME);
    }

    public ModelResource getModelResource() {
        return (ModelResource)this.getParent();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceItem#getResource()
     */
    @Override
	public IResource getResource() {
        return this.getModelResource().getResource();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
	public IResource getUnderlyingResource() {
        return getResource();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     */
    @Override
	public IPath getPath() {
        return this.getModelResource().getPath();
    }

    protected ModelBufferImpl getModelBuffer() throws ModelWorkspaceException {
        final ModelResourceImpl modelResourceImpl = (ModelResourceImpl)this.getModelResource();
        return (ModelBufferImpl)modelResourceImpl.getBuffer(); // loads if req'd
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getDiagrams( final EObject target ) throws ModelWorkspaceException {
        final ModelBufferImpl buffer = getModelBuffer();
        final ModelContents contents = buffer.getModelContents();
        if (contents == null) {
            final Object[] params = new Object[] {this.getParent().getPath()};
            final String msg = ModelerCore.Util.getString("ModelDiagramsImpl.No_ModelContents_found_for_resource", params); //$NON-NLS-1$
            CoreArgCheck.isTrue(false, msg);
        }
        return contents.getDiagrams(target);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelResource#getDiagrams(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getDiagrams() throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getDiagrams();
    }

    /**
     * This method provides a robust method to completely create a diagram and add it correctly to a ModelResource. This is
     * accomplished by specifically calling a ModelResourceContainerFactory method which requires a Resource. This call will
     * assert this requirement. If user desires to create an diagram with fewer restrictions... see
     * org.teiid.designer.core.util.ModelResourceContainerFactory.createNewDiagram(...) methods.
     * 
     * @see org.teiid.designer.core.workspace.ModelDiagrams#createNewDiagram(org.eclipse.emf.ecore.EObject, boolean)
     * @since 4.3
     */
    @Override
	public Diagram createNewDiagram( final EObject target,
                                     final boolean persistent ) throws ModelWorkspaceException {
        EObject finalTarget = target;
        if (finalTarget == null) finalTarget = getModelBuffer().getModelContents().getModelAnnotation();

        return ModelResourceContainerFactory.createNewDiagram(finalTarget, getModelResource().getEmfResource(), persistent);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelDiagrams#delete(org.teiid.designer.metamodels.diagram.Diagram)
     */
    @Override
	public boolean delete( final Diagram diagram ) {
        return ModelResourceContainerFactory.deleteDiagram(diagram);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelDiagrams#isPersistent(org.teiid.designer.metamodels.diagram.Diagram)
     */
    @Override
	public boolean isPersistent( final Diagram diagram ) {
        return ModelResourceContainerFactory.isPersistent(diagram);
    }

    /**
     * This method assumes a diagram is changing persistence and it already exists in a model resource or ModelContents
     * 
     * @see org.teiid.designer.core.workspace.ModelDiagrams#setPersistent(org.teiid.designer.metamodels.diagram.Diagram, boolean)
     * @since 4.3
     */
    @Override
	public void setPersistent( final Diagram diagram,
                               boolean persistent ) throws ModelWorkspaceException {
        if (persistent == isPersistent(diagram)) {
            return;
        }

        ModelResourceContainerFactory.setDiagramPersistence(diagram, getModelResource().getEmfResource(), persistent);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    @Override
    public Openable getOpenable() {
        return getOpenableParent();
    }

}

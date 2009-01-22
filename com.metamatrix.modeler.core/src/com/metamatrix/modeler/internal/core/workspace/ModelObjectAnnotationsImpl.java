/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelObjectAnnotations;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.Openable;

/**
 * ModelObjectAnnotationsImpl
 */
public class ModelObjectAnnotationsImpl extends NonOpenableModelWorkspaceItemImpl implements ModelObjectAnnotations {

    protected static final String DEFAULT_NAME = ModelerCore.Util.getString("ModelObjectAnnotationsImpl.defaultName"); //$NON-NLS-1$

    /**
     * Constructor needed for test cases.
     */
    ModelObjectAnnotationsImpl() {
        super(ANNOTATIONS, null, DEFAULT_NAME);
    }

    /**
     * Construct an instance of ModelObjectAnnotationsImpl.
     * 
     * @since 4.0
     */
    public ModelObjectAnnotationsImpl( final ModelWorkspaceItem parent ) {
        super(ANNOTATIONS, parent, DEFAULT_NAME);
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
     * @see com.metamatrix.modeler.core.workspace.ModelObjectAnnotations#getAnnotation(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Annotation getAnnotation( final EObject target ) throws ModelWorkspaceException {
        return getModelBuffer().getModelContents().getAnnotation(target);
    }

    /**
     * This method provides a robust method to completely create a new annotation and add it correctly to a ModelResource. This is
     * accomplished by specifically calling a ModelResourceContainerFactory method which requires a Resource. This call will
     * assert this requirement. If user desires to create an annotation with fewer restrictions... see
     * com.metamatrix.modeler.core.util.ModelResourceContainerFactory.createNewAnnotation(...) methods.
     * 
     * @see com.metamatrix.modeler.core.workspace.ModelObjectAnnotations#createNewAnnotation(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Annotation createNewAnnotation( final EObject target ) throws ModelWorkspaceException {
        return ModelResourceContainerFactory.createNewAnnotation(target, getModelResource().getEmfResource());
    }

    /**
     * This method provides a robust method to completely delete an annotation and remove it correctly from a ModelResource. This
     * is accomplished by specifically calling a ModelResourceContainerFactory method which requires an Resource.
     * 
     * @see com.metamatrix.modeler.core.workspace.ModelObjectAnnotations#delete(com.metamatrix.metamodels.core.Annotation)
     * @since 4.3
     */
    public boolean delete( final Annotation annotation ) {
        return ModelResourceContainerFactory.deleteAnnotation(annotation);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    @Override
    public Openable getOpenable() {
        return getOpenableParent();
    }

}

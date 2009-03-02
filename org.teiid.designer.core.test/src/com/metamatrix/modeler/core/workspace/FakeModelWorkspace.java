/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * FakeModelWorkspace
 */
public class FakeModelWorkspace extends FakeModelWorkspaceItem implements ModelWorkspace {

    /**
     * Construct an instance of FakeModelWorkspace.
     */
    public FakeModelWorkspace() {
        super(ModelWorkspaceItem.MODEL_WORKSPACE, Path.ROOT.toString());
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#createModelProject(java.lang.String,
     *      org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
     */
    public ModelProject createModelProject( String name,
                                            IPath path,
                                            IProgressMonitor monitor ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelProject(java.lang.String)
     */
    public ModelProject findModelProject( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelProject(org.eclipse.core.resources.IResource)
     */
    public ModelProject findModelProject( IResource resource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getModelProject(org.eclipse.core.resources.IResource)
     */
    public ModelProject getModelProject( IResource resource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getWorkspace()
     */
    public IWorkspace getWorkspace() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getModelProjects()
     */
    public ModelProject[] getModelProjects() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getNonModelingResources()
     */
    public Object[] getNonModelingResources() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getEmfResources()
     * @since 4.2
     */
    public Resource[] getEmfResources() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getModelResources()
     * @since 4.2
     */
    public ModelResource[] getModelResources() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelResource(org.eclipse.core.resources.IResource)
     */
    public ModelResource findModelResource( IResource resource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.resource.Resource)
     */
    public ModelResource findModelResource( Resource resource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelResource(org.eclipse.core.runtime.IPath)
     */
    public ModelResource findModelResource( IPath pathInWorkspace ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.EObject)
     */
    public ModelResource findModelResource( EObject eObject ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#addNotificationListener(com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener)
     */
    public void addNotificationListener( ModelWorkspaceNotificationListener listener ) {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#removeNotificationListener(com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener)
     */
    public void removeNotificationListener( ModelWorkspaceNotificationListener listener ) {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#getParent(org.eclipse.core.resources.IResource)
     */
    public ModelWorkspaceItem getParent( IResource resource ) {
        return null;
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#close()
     */
    public void close() {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#isOpen()
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void open( IProgressMonitor progress ) {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    public void save( IProgressMonitor progress,
                      boolean force ) {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#addModelResourceReloadVetoListener(com.metamatrix.modeler.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    public void addModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspace#removeModelResourceReloadVetoListener(com.metamatrix.modeler.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    public void removeModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

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
     * @see org.teiid.designer.core.workspace.ModelWorkspace#createModelProject(java.lang.String,
     *      org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public ModelProject createModelProject( String name,
                                            IPath path,
                                            IProgressMonitor monitor ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelProject(java.lang.String)
     */
    @Override
	public ModelProject findModelProject( String name ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelProject(org.eclipse.core.resources.IResource)
     */
    @Override
	public ModelProject findModelProject( IResource resource ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getModelProject(org.eclipse.core.resources.IResource)
     */
    @Override
	public ModelProject getModelProject( IResource resource ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getWorkspace()
     */
    @Override
	public IWorkspace getWorkspace() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getModelProjects()
     */
    @Override
	public ModelProject[] getModelProjects() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getEmfResources()
     * @since 4.2
     */
    @Override
	public Resource[] getEmfResources() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getModelResources()
     * @since 4.2
     */
    @Override
	public ModelResource[] getModelResources() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.core.resources.IResource)
     */
    @Override
	public ModelResource findModelResource( IResource resource ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
	public ModelResource findModelResource( Resource resource ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.core.runtime.IPath)
     */
    @Override
	public ModelResource findModelResource( IPath pathInWorkspace ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#findModelResource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public ModelResource findModelResource( EObject eObject ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#addNotificationListener(org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener)
     */
    @Override
	public void addNotificationListener( ModelWorkspaceNotificationListener listener ) {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#removeNotificationListener(org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener)
     */
    @Override
	public void removeNotificationListener( ModelWorkspaceNotificationListener listener ) {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#getParent(org.eclipse.core.resources.IResource)
     */
    @Override
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
     * @see org.teiid.designer.core.workspace.Openable#close()
     */
    @Override
	public void close() {
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#hasUnsavedChanges()
     */
    @Override
	public boolean hasUnsavedChanges() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#isOpen()
     */
    @Override
	public boolean isOpen() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void open( IProgressMonitor progress ) {
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    @Override
	public void save( IProgressMonitor progress,
                      boolean force ) {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#addModelResourceReloadVetoListener(org.teiid.designer.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    @Override
	public void addModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspace#removeModelResourceReloadVetoListener(org.teiid.designer.core.workspace.ModelResourceReloadVetoListener)
     * @since 4.2
     */
    @Override
	public void removeModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener ) {
    }

}

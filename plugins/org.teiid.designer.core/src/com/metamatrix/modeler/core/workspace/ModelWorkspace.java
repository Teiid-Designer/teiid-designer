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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A ModelWorkspace represents the {@link ModelWorkspaceItems items} in a
 * Modeler workspace.
 * <p>
 * Model workspace items need to be opened before they can be navigated or manipulated.
 * The children are of type <code>ModelProject</code>.
 * The children are listed in no particular order.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ModelWorkspace extends ModelWorkspaceItem, Openable {

    /**
     * Creates the Model project with the given name. 
     * 
     * @return the Model project with the given name
     * @throws CoreException If the project could not be created.
     */
    ModelProject createModelProject(String name, IPath path, IProgressMonitor monitor)
    throws CoreException;

    /**
     * Returns the Model project with the given name. 
     * @return the Model project with the given name, or null if there is no model project with such a name
     */
    ModelProject findModelProject(String name);

    /**
     * Returns the Model project for the given resource. 
     * @return the Model project for the given resource, or null if there is no such model project
     */
    ModelProject findModelProject(IResource resource);

    /**
     * Returns the Model project associated with the given resource. 
     * 
     * @return the Model project associated with the given resource.
     */
    ModelProject getModelProject(IResource resource);
    
    /**
     * Return the {@link IWorkspace Eclipse workspace} that this object
     * represents and upon which this item was created.
     * @return the Eclipse workspace
     */
    IWorkspace getWorkspace();
    
    /**
     * Return the {@link ModelProject projects} contained by this workspace, or an empty array if there
     * are none.
     * <p>
     * This method returns the same result as {@link #getChildren()}.
     * </p>
     * @return the {@link ModelProject} instances contained in this
     * workspace item, or an empty array if there are none.
     * @exception JavaModelException if this request fails.
     */
    ModelProject[] getModelProjects() throws ModelWorkspaceException;

    /**
     * Returns an array of non-model resources (that is, non-Modeling projects) in
     * the workspace.
     * <p>
     * Non-Modeling projects include all projects that are closed (even if they have the
     * Modeling nature).
     * </p>
     * 
     * @return an array of non-modeling projects contained in the workspace.
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Object[] getNonModelingResources() throws ModelWorkspaceException;
    
    /**
     * Obtains all the {@link ModelResource}s found in open model projects in the workspace. 
     * @return the model resources (never <code>null</code>)
     * @throws CoreException if problem obtaining the resources
     * @since 4.2
     */
    public ModelResource[] getModelResources() throws CoreException;
    /**
     * Obtains all the model {@link Resource}s found in open model projects in the workspace. 
     * @return the EMF resources (never <code>null</code>)
     * @throws CoreException if problem obtaining the resources
     * @since 4.2
     */
    public Resource[] getEmfResources() throws CoreException;
    
    /**
     * Return the {@link ModelResource} that contains the opened {@link IResource}.
     * @param resource the IResource; may not be null
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    ModelResource findModelResource( final IResource resource );    
    
    /**
     * Return the {@link ModelResource} that contains the opened {@link Resource EMF resource}.
     * @param resource the EMF resource; may not be null
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    ModelResource findModelResource( final Resource resource );
    
    /**
     * Return the {@link ModelResource} that is given by the supplied {@link IPath}.
     * @param pathInWorkspace the workspace-relative path to the model; may not be null
     * @return the ModelResource; null only if the model with the supplied path
     * does not exist
     */
    ModelResource findModelResource(final IPath pathInWorkspace);

    /**
     * Return the {@link ModelResource} that contains the {@link EObject model object}.
     * @param eObject the model object; may not be null
     * @return the ModelResource; null only if the model object's {@link Resource EMF resource}
     * is not known to the {@link ModelWorkspace}.
     */
    ModelResource findModelResource( final EObject eObject );
    
    /**
     * Add a listener of {@link ModelWorkspaceNotification notifications}.
     * @param listener the listener to be added; may not be null
     */
    void addNotificationListener( ModelWorkspaceNotificationListener listener );
    
    /**
     * Remove a listener of {@link ModelWorkspaceNotification notifications}.
     * @param listener the listener to be removed; may not be null
     */
    void removeNotificationListener( ModelWorkspaceNotificationListener listener );
    
    
    /**
     * Add a listener of notifications when the underlying file has been modified
     * and may need to be reloaded.  Each listener will be given the opportunity to 
     * veto the reloading of the model from the underlying file.
     * @param listener the listener to be added; may not be null
     */
    void addModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener );
    
    /**
     * Remove a listener of notifications when the underlying file has been modified
     * and may need to be reloaded.  Each listener will be given the opportunity to 
     * veto the reloading of the model from the underlying file.
     * @param listener the listener to be removed; may not be null
     */
    void removeModelResourceReloadVetoListener( ModelResourceReloadVetoListener listener );
    
    /**
     * Find the ModelworkspaceItem parent for the given resource
     * @param resource
     * @return the ModelworkspaceItem parent for the given resource
     */
    public ModelWorkspaceItem getParent(final IResource resource) ;

}

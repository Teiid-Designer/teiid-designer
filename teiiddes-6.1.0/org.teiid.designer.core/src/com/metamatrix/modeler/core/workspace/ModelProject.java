/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * A ModelProject encapsulates an {@link org.eclipse.core.resources.IProject Eclipse project}
 * that has the {@link ModelerCore#NATURE_ID com.metamatrix.modeler.core.modelnature}
 * nature.
 * <p>
 * Model project items need to be opened before they can be navigated or manipulated.
 * The children are of type {@link ModelFolder} and {@link ModelResource}.
 * The children are listed in no particular order.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ModelProject extends ModelWorkspaceItem, Openable {

    /**
     * Return the {@link IProject Eclipse project} that this object
     * represents and upon which this item was created.
     * @return the Eclipse project
     */
    IProject getProject();
    
    /**
     * Return the {@link ModelWorkspaceItem model workspace item} for the specified resource and
     * contained by this project.  The ModelProject does not have to directly contain the corresponding
     * ModelWorkspaceItem representing the supplied resource.
     * @return the {@link ModelWorkspaceItem} instances contained by this project item that represents
     * the suppplied resource; may be null if the supplied resource doesn't represent a model or a folder
     * or doesn't represent a ModelWorkspaceItem
     * @throws ModelWorkspaceException
     */
    ModelWorkspaceItem findModelWorkspaceItem( IResource resource ) throws ModelWorkspaceException;
    
//    /**
//     * Return the {@link ModelPackageFragmentRoot fragment roots} contained by this
//     * project.
//     * <p>
//     * This method returns the same result as {@link #getChildren()}.
//     * </p>
//     * @return the {@link ModelPackageFragmentRoot} instances contained in this
//     * project item; never null
//     */
//    ModelWorkspaceItem[] getModelPackageFragmentRoots() throws ModelWorkspaceException;

    /**
     * Returns an array of non-modeling resources directly contained in this project.
     * It does not transitively answer non-Java resources contained in folders;
     * these would have to be explicitly iterated over.
     * <p>
     * Non-model resources includes other files and folders located in the
     * project not accounted for by any of it model package fragment
     * roots.
     * </p>
     * 
     * @return an array of non-Modeling resources directly contained in this project
     * @exception ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Object[] getNonModelingResources() throws ModelWorkspaceException;
}

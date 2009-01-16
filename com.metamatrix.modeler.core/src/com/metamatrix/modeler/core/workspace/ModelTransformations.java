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

package com.metamatrix.modeler.core.workspace;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;

/**
 * A ModelTransformations represents the set of transformations available in the model.
 * One {@link ModelResource} has a single ModelTransformations object.
 */
public interface ModelTransformations extends ModelWorkspaceItem {

    /**
     * Create a new SQL transformation and add it to this resource. 
     * @param target the "target" for the transformation; may not be null
     * @return the new SqlTransformationMappingRoot object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    SqlTransformationMappingRoot createNewSqlTransformation( EObject target ) throws ModelWorkspaceException;

    /**
     * Create a new fragment transformation and add it to this resource. 
     * @param target the "target" for the transformation; may not be null
     * @return the new FragmentMappingRoot object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    FragmentMappingRoot createNewFragmentMapping( EObject target ) throws ModelWorkspaceException;

    /**
     * Create a new tree mapping transformation and add it to this resource. 
     * @param target the "target" for the transformation; may not be null
     * @return the new TreeMappingRoot object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    TreeMappingRoot createNewTreeMapping( EObject target ) throws ModelWorkspaceException;

    /**
     * Create a new transformation and add it to this resource. 
     * @param target the "target" for the transformation; may not be null
     * @return the new TransformationMappingRoot object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    TransformationMappingRoot addNewTransformation( EObject target, TransformationMappingRoot newMapping) throws ModelWorkspaceException;

    /**
     * Get the transformation objects associated with the supplied target model object.
     * TransformationMappingRoots are created using the {@link #createNewTransformation(EObject)} method.
     * @param target the target object; may be null, meaning find all transformations that have no target
     * @return the {@link TransformationMappingRoot} instances that are associated with the target object;
     * never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getTransformations( EObject target ) throws ModelWorkspaceException;

    /**
     * Get all the transformation objects known by this resource.
     * TransformationMappingRoots are created using the {@link #createNewTransformation(EObject)} method.
     * @return the {@link TransformationMappingRoot} instances for this resource; never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getTransformations() throws ModelWorkspaceException;

    /**
     * Remove the specified transformation from this resource.  This method works for persistent or transient
     * transformations; persistent transformations can always just be removed from the 
     * @param transformation the transformation; may not be null
     * @return true if the transformation was deleted from this resource, or false if it was not
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean delete( TransformationMappingRoot transformation ) throws ModelWorkspaceException;
}

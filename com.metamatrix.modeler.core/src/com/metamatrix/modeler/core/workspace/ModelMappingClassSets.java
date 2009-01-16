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

import com.metamatrix.metamodels.transformation.MappingClassSet;

/**
 * A ModelMappingClassSets objects represents the container of {@link MappingClassSet} objects
 * available in the model.
 * One {@link ModelResource} has a single ModelMappingClassSets object.
 */
public interface ModelMappingClassSets extends ModelWorkspaceItem {

    /**
     * Create a new {@link MappingClassSet} and add it to this resource. 
     * @param target the "target" for the mapping class sets; may not be null
     * @return the new MappingClassSet object
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    MappingClassSet createNewMappingClassSet( EObject target ) throws ModelWorkspaceException;

    /**
     * Get the {@link MappingClassSet} objects associated with the supplied target model object.
     * MappingClassSets are created using the {@link #createNewMappingClassSet(EObject)} method.
     * @param target the target object; may be null, meaning find all {@link MappingClassSet} instances
     * that have no target
     * @return the {@link MappingClassSet} instances that are associated with the target object;
     * never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getMappingClassSets( EObject target ) throws ModelWorkspaceException;

    /**
     * Get all the {@link MappingClassSet} objects known by this resource.
     * MappingClassSets are created using the {@link #createNewMappingClassSet(EObject)} method.
     * @return the {@link MappingClassSet} instances for this resource; never null, but possibly empty
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getMappingClassSets() throws ModelWorkspaceException;

    /**
     * Remove the specified {@link MappingClassSet} from this resource. 
     * @param mappingClassSet the {@link MappingClassSet} to be deleted; may not be null
     * @return true if the {@link MappingClassSet} was deleted from this resource, or false if it was not
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean delete( MappingClassSet mappingClassSet ) throws ModelWorkspaceException;

}

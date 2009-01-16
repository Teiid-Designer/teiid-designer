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

package com.metamatrix.modeler.core.compare;

import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/**
 * This interface defines an object that can match up objects from two separate lists.
 * Implementations of this class must be stateless and should be thread-safe.
 */
public interface EObjectMatcher {
    
    /**
     * Determine the mappings between the supplied input and output objects that
     * are the roots in the input and output models, respectively.
     * This method should create new {@link Mapping mappings} for input and output
     * references that are considered to be matches.
     * <p>
     * Multiple EObjectMatcher instances may be called to add mappings for the same
     * list of inputs and outputs.  Therfore, if this implementation finds a match,
     * the inputs and outputs that are matched should be removed from the lists.
     * </p>
     * @param inputs the list of {@link org.eclipse.emf.ecore.EObject} instances that
     * are to be the inputs of the computed mappings; may not be null but may be empty
     * @param outputs the list of {@link org.eclipse.emf.ecore.EObject} instances that
     * are to be the outputs of the computed mappings; may not be null but may be empty
     * @param mapping the parent mapping to which computed mappings should be added;
     * may not be null
     * @param factory the {@link MappingFactory} that should be used to create the
     * nested {@link Mapping} objects.
     */
    public void addMappingsForRoots(final List inputs, final List outputs, 
                                    final Mapping mapping, final MappingFactory factory);

    /**
     * Determine the mappings between the supplied input and output objects that
     * are the supplied feature values for the parent input object and output object.
     * This method should create new {@link Mapping mappings} for input and output
     * references that are considered to be matches.
     * <p>
     * Multiple EObjectMatcher instances may be called to add mappings for the same
     * list of inputs and outputs.  Therfore, if this implementation finds a match,
     * the inputs and outputs that are matched should be removed from the lists.
     * </p>
     * @param reference the metaclass reference for which that the inputs and outputs are values;
     * may not be null
     * @param inputs the list of {@link org.eclipse.emf.ecore.EObject} instances that
     * are to be the inputs of the computed mappings; may not be null but may be empty
     * @param outputs the list of {@link org.eclipse.emf.ecore.EObject} instances that
     * are to be the outputs of the computed mappings; may not be null but may be empty
     * @param mapping the parent mapping to which computed mappings should be added;
     * may not be null
     * @param factory the {@link MappingFactory} that should be used to create the
     * nested {@link Mapping} objects.
     */
    public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Mapping mapping, final MappingFactory factory );
}

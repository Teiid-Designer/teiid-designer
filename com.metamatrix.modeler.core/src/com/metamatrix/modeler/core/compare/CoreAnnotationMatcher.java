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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

import com.metamatrix.metamodels.core.Annotation;

/**
 * CoreAnnotationMatcher
 */
public class CoreAnnotationMatcher extends AbstractEObjectMatcher implements TwoPhaseEObjectMatcher {

    /**
     * Construct an instance of CoreAnnotationMatcher.
     * 
     */
    public CoreAnnotationMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs,
                                    final Map inputsToOutputs, 
                                    final Mapping mapping, final MappingFactory factory) {
        // do nothing for roots ...
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Map inputsToOutputs, 
                             final Mapping mapping, final MappingFactory factory) {
        final Map inputAnnotationsByAnnotatedObject = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final Annotation ann = (Annotation)iter.next();
            final EObject annotatedObject = ann.getAnnotatedObject();
            if ( annotatedObject != null ) {
                final Object outputAnnotatedObject = inputsToOutputs.get(annotatedObject);
                inputAnnotationsByAnnotatedObject.put(outputAnnotatedObject,ann);
            }
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Annotation annotation = (Annotation)outputIter.next();
            final EObject annotatedObject = annotation.getAnnotatedObject();
            if ( annotatedObject != null ) {
                final Annotation inputAnn = (Annotation)inputAnnotationsByAnnotatedObject.get(annotatedObject);
                if ( inputAnn != null ) {
                    inputs.remove(inputAnn);
                    outputIter.remove();
                    addMapping(inputAnn,annotation,mapping,factory);
                    inputsToOutputs.put(inputAnn,annotation);
                }
            }
        }
        
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(List inputs, List outputs, Mapping mapping, MappingFactory factory) {
        // do nothing for the first phase ...
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(EReference reference, List inputs, List outputs, Mapping mapping, MappingFactory factory) {
        // do nothing for the first phase ...
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;


/** 
 * TransformationMappingRootTargetMatcher
 */
public class TransformationMappingRootTargetMatcher extends AbstractEObjectMatcher implements
                                                                                      TwoPhaseEObjectMatcher {

    /**
     * Construct an instance of CoreAnnotationMatcher.
     * 
     */
    public TransformationMappingRootTargetMatcher() {
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
        final Map inputRootsByTargetObject = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final TransformationMappingRoot root = (TransformationMappingRoot)iter.next();
            final EObject targetObject = root.getTarget();
            if ( targetObject != null ) {
                final Object outputTargetObject = inputsToOutputs.get(targetObject);
                inputRootsByTargetObject.put(outputTargetObject,root);
            }
        }

        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final TransformationMappingRoot outputRoot = (TransformationMappingRoot)outputIter.next();
            final EObject outputTraget = outputRoot.getTarget();
            if ( outputTraget != null ) {
                final TransformationMappingRoot inputRoot = (TransformationMappingRoot)inputRootsByTargetObject.get(outputTraget);
                if ( inputRoot != null ) {
                    inputs.remove(inputRoot);
                    outputIter.remove();
                    addMapping(inputRoot,outputRoot,mapping,factory);
                    inputsToOutputs.put(inputRoot,outputRoot);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs, final Mapping mapping, final MappingFactory factory) {
        // do nothing for the first phase ...
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(final EReference reference, final List inputs, final List outputs, final Mapping mapping, final MappingFactory factory) {
        // do nothing for the first phase ...
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.compare;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.teiid.designer.core.compare.AbstractEObjectMatcher;
import org.teiid.designer.metamodels.transformation.MappingClassSetContainer;
import org.teiid.designer.metamodels.transformation.TransformationContainer;


/**
 * CoreModelImportMatcher
 */
public class TransformationRootObjectMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of CoreAnnotationMatcher.
     * 
     */
    public TransformationRootObjectMatcher() {
        super();
    }
    
    /**
     * @see org.teiid.designer.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    @Override
	public void addMappingsForRoots(final List inputs, final List outputs,
                                    final Mapping mapping, final MappingFactory factory) {
        TransformationContainer inputTransformationContainer = null;
        MappingClassSetContainer inputMCSetContainer = null;

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if ( obj instanceof TransformationContainer ) {
                inputTransformationContainer = (TransformationContainer)obj;
            } else if ( obj instanceof MappingClassSetContainer ) {
                inputMCSetContainer = (MappingClassSetContainer)obj;
            }
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Object obj = outputIter.next();
            if ( obj instanceof TransformationContainer ) {
                if ( inputTransformationContainer != null ) {
                    outputIter.remove();
                    inputs.remove(inputTransformationContainer);
                    addMapping(inputTransformationContainer,(EObject)obj,mapping,factory);
                }
            } else if ( obj instanceof MappingClassSetContainer ) {
                if ( inputMCSetContainer != null ) {
                    outputIter.remove();
                    inputs.remove(inputMCSetContainer);
                    addMapping(inputMCSetContainer,(EObject)obj,mapping,factory);
                }
            }
        }
    }
    
    /**
     * @see org.teiid.designer.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    @Override
	public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Mapping mapping, final MappingFactory factory) {
        // only processes roots ...
    }
    
}

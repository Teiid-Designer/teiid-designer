/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare.diagram;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;


/** 
 * @since 4.2
 */
public class DiagramTargetMatcher extends AbstractEObjectMatcher implements
                                                                TwoPhaseEObjectMatcher {

    /** 
     * 
     * @since 4.2
     */
    public DiagramTargetMatcher() {
        super();
    }

    /**     /** 
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Map inputsToOutputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        // do nothing for roots ...        
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(final EReference reference,
                            final List inputs,
                            final List outputs,
                            final Map inputsToOutputs,
                            final Mapping mapping,
                            final MappingFactory factory) {
        final Map inputEntityByTargetObject = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final Diagram diagram = (Diagram)iter.next();
            final EObject targetObject = diagram.getTarget();
            if ( targetObject != null ) {
                final Object outputTargetObject = inputsToOutputs.get(targetObject);
                inputEntityByTargetObject.put(outputTargetObject,diagram);
            }
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final Diagram outputDiagram = (Diagram)outputIter.next();
            final EObject outputTraget = outputDiagram.getTarget();
            if ( outputTraget != null ) {
                final Diagram inputDiagram = (Diagram)inputEntityByTargetObject.get(outputTraget);
                if ( inputDiagram != null ) {
                    inputs.remove(inputDiagram);
                    outputIter.remove();
                    addMapping(inputDiagram,outputDiagram,mapping,factory);
                    inputsToOutputs.put(inputDiagram,outputDiagram);
                }
            }
        }        
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        // do nothing for the first phase ...        
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappings(final EReference reference,
                            final List inputs,
                            final List outputs,
                            final Mapping mapping,
                            final MappingFactory factory) {
        // do nothing for the first phase ...        
    }

}

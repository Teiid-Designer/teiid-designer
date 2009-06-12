/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/**
 * FakeEObjectMatcherFactory
 */
public class FakeEObjectMatcherFactory implements EObjectMatcherFactory {

    /**
     * Construct an instance of FakeEObjectMatcherFactory.
     *
     */
    public FakeEObjectMatcherFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        return Collections.singletonList(new FakeMappableObjectMatcher());
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(EReference reference) {
        return Collections.singletonList(new FakeMappableObjectMatcher());
    }

    protected class FakeMappableObjectMatcher extends AbstractEObjectMatcher {

        /**
         * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
         */
        public void addMappingsForRoots(List inputs, List outputs, Mapping mapping, MappingFactory factory) {
            addMappings(null,inputs,outputs,mapping,factory);
        }

        /**
         * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
         */
        public void addMappings(EReference reference, List inputs, List outputs, Mapping mapping, MappingFactory factory) {
            // Return if nothing to match ...
            if ( inputs.isEmpty() || outputs.isEmpty() ) {
                return;
            }

            // Go through the outputs and find objects that match an input ...
            final Iterator outputIter = outputs.iterator();
            while (outputIter.hasNext()) {
                final Object outputObject = outputIter.next();
                if ( outputObject instanceof FakeMappableObject ) {
                    final FakeMappableObject fmoOutput = (FakeMappableObject) outputObject;
                    final String oname = fmoOutput.getName();
                    final int otype = fmoOutput.getType();

                    // See if there are any inputs that match ...
                    final Iterator inputIter = inputs.iterator();
                    while (inputIter.hasNext()) {
                        final Object inputObject = inputIter.next();
                        if ( inputObject instanceof FakeMappableObject ) {
                            final FakeMappableObject fmoInput = (FakeMappableObject) inputObject;
                            final String iname = fmoInput.getName();
                            final int itype = fmoInput.getType();

                            if ( iname.equals(oname) && itype == otype ) {

                                // Found a match !!
                                final Mapping nested = factory.createMapping();
                                nested.getOutputs().add(fmoOutput);
                                nested.getInputs().add(fmoInput);
                                mapping.getNested().add(nested);

                                inputIter.remove();
                                outputIter.remove();

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}

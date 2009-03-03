/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * UmlUnNamedAssociationMatcher
 */
public class UmlUnNamedGeneralizationMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UmlUnNamedAssociationMatcher.
     */
    public UmlUnNamedGeneralizationMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List,
     *      org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots( final List inputs,
                                     final List outputs,
                                     final Mapping mapping,
                                     final MappingFactory factory ) {
        // Delegate ...
        addMappings(null, inputs, outputs, mapping, factory);
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List,
     *      java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference,
                             final List inputs,
                             final List outputs,
                             final Mapping mapping,
                             final MappingFactory factory ) {
        //
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if (obj instanceof Generalization) {
                final Generalization gen = (Generalization)obj;
                final String key = computeKey(gen);
                if (key != null) {
                    inputByName.put(key, obj);
                }
            }
        }

        if (inputByName.isEmpty()) {
            return;
        }

        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            if (output instanceof Generalization) {
                final Generalization outputGen = (Generalization)output;
                final String key = computeKey(outputGen);
                if (key != null) {
                    final Generalization inputGen = (Generalization)inputByName.get(key);
                    if (inputGen != null) {
                        final EClass inputMetaclass = inputGen.eClass();
                        final EClass outputMetaclass = outputGen.eClass();
                        if (inputMetaclass.equals(outputMetaclass)) {
                            inputs.remove(inputGen);
                            outputIter.remove();
                            addMapping(inputGen, outputGen, mapping, factory);
                        }
                    }
                }
            }
        }

    }

    protected String computeKey( final Generalization gen ) {
        final Classifier specific = gen.getSpecific();
        StringBuffer sb = new StringBuffer();
        if (specific != null) sb.append(specific.getName());
        if (sb.length() > 0) sb.append("->"); //$NON-NLS-1$
        sb.append(gen.getGeneral().getName());
        if (sb.length() != 0) return sb.toString();

        return null;
    }

}

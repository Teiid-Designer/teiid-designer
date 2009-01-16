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
import org.eclipse.uml2.uml.NamedElement;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * UmlUnNamedClassifierMatcher
 */
public class UmlUnNamedClassifierMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UmlUnNamedClassifierMatcher.
     * 
     */
    public UmlUnNamedClassifierMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs, 
                                    final Mapping mapping, final MappingFactory factory) {
        // Delegate ...
        addMappings(null,inputs,outputs,mapping,factory);
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(final EReference reference, final List inputs, final List outputs, 
                            final Mapping mapping, final MappingFactory factory) {
        //
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if ( obj instanceof Classifier ) {
                final Classifier classifier = (Classifier)obj;
                final String key = computeKey(classifier);
                if ( key != null ) {
                    inputByName.put(key,obj);
                }
            }
        }
        
        if ( inputByName.isEmpty() ) {
            return;
        }
        
        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            if ( output instanceof Classifier ) {
                final Classifier outputClassifier = (Classifier)output;
                final String key = computeKey(outputClassifier);
                if ( key != null ) {
                    final Classifier inputClassifier = (Classifier) inputByName.get(key);
                    if ( inputClassifier != null ) {
                        final EClass inputMetaclass = inputClassifier.eClass();
                        final EClass outputMetaclass = outputClassifier.eClass();
                        if ( inputMetaclass.equals(outputMetaclass) ) {
                            inputs.remove(inputClassifier);
                            outputIter.remove();
                            addMapping(inputClassifier,outputClassifier,mapping,factory);
                        }
                    }
                }
            }
        }

    }
    
    protected String computeKey( final Classifier classifier ) {
        StringBuffer sb = new StringBuffer();
        sb.append(classifier.getQualifiedName());
        final List ownedMembers = classifier.getOwnedMembers();
        sb.append(ownedMembers.size());
        final Iterator iter = ownedMembers.iterator();
        while (iter.hasNext()) {
            final EObject ownedMember = (EObject)iter.next();
            if ( ownedMember instanceof NamedElement ) {
                sb.append(((NamedElement)classifier).getName());
            }
        }
        return sb.toString();
    }

}

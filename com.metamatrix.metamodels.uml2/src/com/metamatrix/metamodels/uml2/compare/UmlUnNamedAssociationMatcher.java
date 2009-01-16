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
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Property;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * UmlUnNamedAssociationMatcher
 */
public class UmlUnNamedAssociationMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UmlUnNamedAssociationMatcher.
     * 
     */
    public UmlUnNamedAssociationMatcher() {
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
            if ( obj instanceof Association ) {
                final Association association = (Association)obj;
                final String key = computeKey(association);
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
            if ( output instanceof Association ) {
                final Association outputAssociation = (Association)output;
                final String key = computeKey(outputAssociation);
                if ( key != null ) {
                    final Association inputAssociation = (Association) inputByName.get(key);
                    if ( inputAssociation != null ) {
                        final EClass inputMetaclass = inputAssociation.eClass();
                        final EClass outputMetaclass = outputAssociation.eClass();
                        if ( inputMetaclass.equals(outputMetaclass) ) {
                            inputs.remove(inputAssociation);
                            outputIter.remove();
                            addMapping(inputAssociation,outputAssociation,mapping,factory);
                        }
                    }
                }
            }
        }

    }
    
    protected String computeKey( final Association association ) {
        final List memberEnds = association.getMemberEnds();
        if ( !memberEnds.isEmpty() ) {
            StringBuffer sb = new StringBuffer();
            final Iterator endIter = memberEnds.iterator();
            while (endIter.hasNext()) {
                final Property end = (Property)endIter.next();
                final String endName = end.getName();
                if ( endName != null ) {
                    sb.append(endName);
                }
                sb.append("8c84mbdgb"); //$NON-NLS-1$
            }
            if ( sb.length() != 0 ) {
                return sb.toString();
            }
        }
        return null;
    }

}

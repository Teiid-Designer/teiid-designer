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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/** 
 * AbstractEObjectNameMatcher
 */
public abstract class AbstractEObjectNameMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of AbstractEObjectMatcher.
     * 
     */
    public AbstractEObjectNameMatcher() {
        super();
    }    

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.2
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        addMappings(null,inputs,outputs,mapping,factory);        
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
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            final String key = getInputKey(obj);
            if ( key != null ) {
                inputByName.put(key,obj);
            }
        }

        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            final String key = getOutputKey(output);
            if ( key != null ) {
                final EObject inputEntity = (EObject) inputByName.get(key);
                if ( inputEntity != null ) {
                    final EClass inputMetaclass = inputEntity.eClass();
                    final EClass outputMetaclass = output.eClass();
                    if ( inputMetaclass.equals(outputMetaclass) ) {
                        inputs.remove(inputEntity);
                        outputIter.remove();
                        addMapping(inputEntity,output,mapping,factory);
                    }
                }
            }
        }        
    }

    protected abstract String getInputKey(final EObject entity );

    protected abstract String getOutputKey(final EObject entity ); 

}

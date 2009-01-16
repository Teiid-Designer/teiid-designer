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

package com.metamatrix.rose.internal.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.rose.internal.IRoseConstants;

/**
 * Matches UML objects using the Rose name.
 * 
 * @since 4.1
 */
public class UmlRoseNameMatcher extends AbstractEObjectMatcher implements
                                                              IRoseConstants.IMetamodelExtensionProperties,
                                                              UmlHandler.IConstants {

    //============================================================================================================================
    // Implemented Methods

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List,
     *      java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.1
     */
    public void addMappings(final EReference reference,
                            final List inputs,
                            final List outputs,
                            final Mapping mapping,
                            final MappingFactory factory) {
        //
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if (obj instanceof NamedElement) {
                final String key = getStereotype(obj);
                if (key != null) {
                    inputByName.put(key, obj);
                }
            }
        }

        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            if (output instanceof NamedElement) {
                final String key = getStereotype(output);
                if (key != null) {
                    final NamedElement inputEntity = (NamedElement)inputByName.get(key);
                    if (inputEntity != null) {
                        final EClass inputMetaclass = inputEntity.eClass();
                        final EClass outputMetaclass = output.eClass();
                        if (inputMetaclass.equals(outputMetaclass)) {
                            inputs.remove(inputEntity);
                            outputIter.remove();
                            addMapping(inputEntity, output, mapping, factory);
                        }
                    }
                }
            }
        }

    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List,
     *      org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     * @since 4.1
     */
    public void addMappingsForRoots(final List inputs,
                                    final List outputs,
                                    final Mapping mapping,
                                    final MappingFactory factory) {
        // Delegate ...
        addMappings(null, inputs, outputs, mapping, factory);
    }

    //============================================================================================================================
    // Utility Methods

    /**
     * @since 4.1
     */
    private String getStereotype(final Object object) {
        final NamedElement existingElem = (NamedElement)object;
        final Stereotype stereotype = existingElem.getAppliedStereotype(QUALIFIED_ROSE_STEREOTYPE);
        if (stereotype != null) {
            final String existingElemName = (String)existingElem.getValue(stereotype, NAME_IN_SOURCE);
            return existingElemName;
        }
        return null;

    }

}

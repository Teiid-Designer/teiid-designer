/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.rose.internal.IRoseConstants;

/**
 * UmlRoseStereotypeMatcher
 * 
 * @since 4.1
 */
public class UmlRoseIdMatcher extends AbstractEObjectMatcher implements
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
        final Map inputByQuid = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if (obj instanceof Element) {
                final Element entity = (Element)obj;
                final String quid = getQuid(entity);
                if (quid != null) {
                    inputByQuid.put(quid, obj);
                }
            }
        }

        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            if (output instanceof Element) {
                final Element outputEntity = (Element)output;
                final String quid = getQuid(outputEntity);
                if (quid != null) {
                    final Element inputEntity = (Element)inputByQuid.get(quid);
                    if (inputEntity != null) {
                        final EClass inputMetaclass = inputEntity.eClass();
                        final EClass outputMetaclass = outputEntity.eClass();
                        if (inputMetaclass.equals(outputMetaclass)) {
                            inputs.remove(inputEntity);
                            outputIter.remove();
                            addMapping(inputEntity, outputEntity, mapping, factory);
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
    private String getQuid(final Element element) {
        final Stereotype stereotype = element.getAppliedStereotype(QUALIFIED_ROSE_STEREOTYPE);
        if (stereotype != null) {
            final String existingElemQuid = (String)element.getValue(stereotype, QUID);
            return existingElemQuid;
        }
        return null;

    }

}

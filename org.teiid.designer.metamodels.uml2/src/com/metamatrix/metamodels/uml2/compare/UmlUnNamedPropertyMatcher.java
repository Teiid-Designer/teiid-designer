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
import org.eclipse.uml2.uml.Property;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * UmlUnNamedAssociationMatcher
 */
public class UmlUnNamedPropertyMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UmlUnNamedAssociationMatcher.
     * 
     */
    public UmlUnNamedPropertyMatcher() {
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
            if ( obj instanceof Property ) {
                final Property property = (Property)obj;
                final String key = computeKey(property);
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
            if ( output instanceof Property ) {
                final Property outputProperty = (Property)output;
                final String key = computeKey(outputProperty);
                if ( key != null ) {
                    final Property inputProperty = (Property) inputByName.get(key);
                    if ( inputProperty != null ) {
                        final EClass inputMetaclass = inputProperty.eClass();
                        final EClass outputMetaclass = outputProperty.eClass();
                        if ( inputMetaclass.equals(outputMetaclass) ) {
                            inputs.remove(inputProperty);
                            outputIter.remove();
                            addMapping(inputProperty,outputProperty,mapping,factory);
                        }
                    }
                }
            }
        }

    }
    
    protected String computeKey( final Property property ) {
        StringBuffer sb = new StringBuffer();
        sb.append(property.getQualifiedName());
        if ( property.getType() != null ) {
            sb.append(property.getType().getName());
        }
        sb.append(property.getLower());
        sb.append(property.getUpper());
        sb.append(property.getVisibility().getName());
        return sb.toString();
    }

}

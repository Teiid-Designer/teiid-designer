/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/**
 * AbstractEObjectMatcher
 */
public abstract class AbstractEObjectMatcher implements EObjectMatcher {

    /**
     * Construct an instance of AbstractEObjectMatcher.
     * 
     */
    public AbstractEObjectMatcher() {
        super();
    }
    
    protected void addMapping( final EObject input, final EObject output, 
                               final Mapping parentMapping, final MappingFactory factory ) {
        final Mapping nested = factory.createMapping();
        nested.getOutputs().add(output);
        nested.getInputs().add(input);
        parentMapping.getNested().add(nested);
    }

}

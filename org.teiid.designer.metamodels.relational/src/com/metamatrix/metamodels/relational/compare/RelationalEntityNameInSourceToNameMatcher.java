/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.compare;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;

/**
 * RelationalEntityNameToNameMatcher
 */
public class RelationalEntityNameInSourceToNameMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of RelationalEntityNameInSourceToNameInSourceMatcher.
     * 
     */
    public RelationalEntityNameInSourceToNameMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof RelationalEntity) {
            return ((RelationalEntity)entity).getNameInSource();
        }
        return null;
    }

    @Override
    protected String getOutputKey( final EObject entity ) {
        if(entity instanceof RelationalEntity) {
            return ((RelationalEntity)entity).getName();
        }
        return null;
    }
}

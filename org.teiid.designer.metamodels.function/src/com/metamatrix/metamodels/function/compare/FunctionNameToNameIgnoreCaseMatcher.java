/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.compare;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.function.Function;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;


/** 
 * FunctionNameToNameIgnoreCaseMatcher
 */
public class FunctionNameToNameIgnoreCaseMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of RelationalEntityNameToNameIgnoreCaseMatcher.
     * 
     */
    public FunctionNameToNameIgnoreCaseMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof Function) {
            final String name = ((Function)entity).getName();
            if(name != null) {
                return name.toUpperCase();
            }
        }
        return null;
    }

    @Override
    protected String getOutputKey( final EObject entity ) {
        if(entity instanceof Function) {
            final String name = ((Function)entity).getName();
            if(name != null) {
                return name.toUpperCase();
            }
        }
        return null;
    }

}

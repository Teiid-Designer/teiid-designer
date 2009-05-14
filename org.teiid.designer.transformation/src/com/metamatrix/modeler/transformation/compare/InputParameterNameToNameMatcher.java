/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.compare;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;


/** 
 * @since 4.2
 */
public class InputParameterNameToNameMatcher extends AbstractEObjectNameMatcher {

    public InputParameterNameToNameMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof InputParameter) {
            return ((InputParameter)entity).getName();
        }
        return null;
    }

    @Override
    protected String getOutputKey( final EObject entity ) {
        if(entity instanceof InputParameter) {
            return ((InputParameter)entity).getName();
        }
        return null;
    }

}

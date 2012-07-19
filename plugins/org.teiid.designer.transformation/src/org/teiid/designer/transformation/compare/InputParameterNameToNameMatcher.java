/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.compare;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.compare.AbstractEObjectNameMatcher;
import org.teiid.designer.metamodels.transformation.InputParameter;



/** 
 * @since 8.0
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

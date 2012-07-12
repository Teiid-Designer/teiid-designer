/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.compare;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.compare.AbstractEObjectNameMatcher;
import org.teiid.designer.metamodels.function.FunctionParameter;



/** 
 * FunctionParameterNameToNameMatcher
 */
public class FunctionParameterNameToNameMatcher extends AbstractEObjectNameMatcher {

    /**
     * Construct an instance of UuidEObjectMatcher.
     * 
     */
    public FunctionParameterNameToNameMatcher() {
        super();
    }

    /** 
     * @see org.teiid.designer.core.compare.AbstractEObjectNameMatcher#getInputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getInputKey(final EObject entity) {
        if(entity instanceof FunctionParameter) {
            return ((FunctionParameter)entity).getName();
        }
        return null;
    }
    /** 
     * @see org.teiid.designer.core.compare.AbstractEObjectNameMatcher#getOutputKey(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    protected String getOutputKey(final EObject entity) {
        if(entity instanceof FunctionParameter) {
            return ((FunctionParameter)entity).getName();
        }
        return null;
    }

}

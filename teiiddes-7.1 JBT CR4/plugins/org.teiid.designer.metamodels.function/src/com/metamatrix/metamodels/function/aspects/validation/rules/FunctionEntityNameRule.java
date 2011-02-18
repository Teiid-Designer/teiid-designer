/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.aspects.validation.rules;

import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * ScalarFunctionNameRule
 */
public class FunctionEntityNameRule extends StringNameRule {

    /**
     * Construct an instance of ScalarFunctionNameRule.
     * 
     */
    public FunctionEntityNameRule() {
        super(FunctionPackage.FUNCTION__NAME);
    }
    
    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateUniqueness()
     */
    @Override
    protected boolean validateUniqueness() {
        return false;
    }

    


}

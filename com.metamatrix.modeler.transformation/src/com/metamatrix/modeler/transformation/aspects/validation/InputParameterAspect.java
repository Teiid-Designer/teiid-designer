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

package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * InputParameterAspect
 */
public class InputParameterAspect extends TransformationAspect {
	
	public static final ValidationRule NAME_RULE = new StringNameRule(TransformationPackage.INPUT_PARAMETER__NAME);
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(TransformationPackage.INPUT_PARAMETER__NAME);

	public InputParameterAspect(final MetamodelEntity entity) {
		super(entity);
	}

	/**
	 * Get validation rules for InputParameterAspect
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
		addRule(BINDING_RULE);
		return super.getValidationRules();
	}
	
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(InputParameter.class, eObject);
        if(!context.shouldIgnore(eObject)) {
            InputParameter inputParameter = (InputParameter) eObject;
	        EObject inputSet = inputParameter.getInputSet();
	        if(inputSet != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(inputSet);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(inputSet, context);
	                if(!shouldValidate) {
	    	            context.addObjectToIgnore(eObject, true);
	                }
	                return shouldValidate;
	            }
	        }
	        return true;
        }
        return false;
    }	
}

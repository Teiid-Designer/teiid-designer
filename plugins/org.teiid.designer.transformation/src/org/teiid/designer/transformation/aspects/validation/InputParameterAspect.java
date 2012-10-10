/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.ValidationAspect;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.core.validation.rules.StringLengthRule;
import org.teiid.designer.core.validation.rules.StringNameRule;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.TransformationPackage;


/**
 * InputParameterAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject);
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

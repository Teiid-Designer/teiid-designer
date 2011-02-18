/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.transformation.TransformationMapping;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * TransformationMappingAspect
 */
public class TransformationMappingAspect extends TransformationAspect {

    public TransformationMappingAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get validation rules for TransformationMapping
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(MAPPING_RULE);
		return super.getValidationRules();
	}
	
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        CoreArgCheck.isInstanceOf(TransformationMapping.class, eObject);

        if(!context.shouldIgnore(eObject)) {
            TransformationMapping mapping = (TransformationMapping) eObject;
	        EObject mappingRoot = mapping.getMappingRoot();
	        if(mappingRoot != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(mappingRoot);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(mappingRoot, context);
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

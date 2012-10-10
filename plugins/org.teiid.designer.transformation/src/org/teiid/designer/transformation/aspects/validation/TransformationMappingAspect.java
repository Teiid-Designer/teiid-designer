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
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.metamodels.transformation.TransformationMapping;


/**
 * TransformationMappingAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
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

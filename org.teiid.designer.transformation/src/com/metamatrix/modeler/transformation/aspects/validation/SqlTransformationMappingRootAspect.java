/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * SqlTransformationMappingRootAspect
 */
public class SqlTransformationMappingRootAspect extends TransformationAspect {

    public SqlTransformationMappingRootAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get validation rules for SqlTransformationMappingRoot
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(MAPPINGROOT_RULE);
		return super.getValidationRules();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		SqlTransformationAspect transformationAspect = (SqlTransformationAspect) AspectManager.getSqlAspect(eObject);
		EObject transformedObject = (EObject) transformationAspect.getTransformedObject(eObject);
		// update the map		
		context.addTargetTransform(transformedObject, eObject);
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        
        if(!context.shouldIgnore(eObject)) {
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot) eObject;
            EObject targetObj = mappingRoot.getTarget();
            if(targetObj != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(targetObj);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(targetObj, context);
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

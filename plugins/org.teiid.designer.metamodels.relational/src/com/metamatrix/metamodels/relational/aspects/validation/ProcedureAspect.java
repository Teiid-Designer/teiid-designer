/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * ProcedureAspect
 */
public class ProcedureAspect extends RelationalEntityAspect {

    /**
     * Construct an instance of ProcedureAspect.
     * @param entity
     */
    public ProcedureAspect(MetamodelEntity entity){
        super(entity);
    }
    
	/**
	 * Get all the validation rules for Procedure.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(PROC_PARAM_RULE);
		return super.getValidationRules();		
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		Map transformMap = context.getTargetTransformMap();
		SqlProcedureAspect procAspect = (SqlProcedureAspect) AspectManager.getSqlAspect(eObject);
		if(procAspect.isVirtual(eObject)) {
			if(transformMap != null) {
				if(transformMap.containsKey(eObject)) {
					return;
				}
			}
			context.addTargetTransform(eObject, null);
		}
	}	
}

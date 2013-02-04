/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationRuleSet;


/**
 * ProcedureAspect
 *
 * @since 8.0
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
        addRule(PROC_FUNCTION_RULE);
        addRule(SOURCE_PROC_UNIQUENESS_RULE);
		return super.getValidationRules();		
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.ValidationAspect#updateContext(org.teiid.designer.core.validation.ValidationContext)
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

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

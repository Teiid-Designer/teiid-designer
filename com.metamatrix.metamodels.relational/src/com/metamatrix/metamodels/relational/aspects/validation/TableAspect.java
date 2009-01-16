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
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * TableAspect
 */
public abstract class TableAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public TableAspect(MetamodelEntity entity){
        super(entity);
    }
    
	/**
	 * Get all the validation rules for Table.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(TABLE_UPDATABILITY_RULE);
        addRule(TABLE_UNIQUE_KEYS_RULE);
        addRule(TABLE_MATERIALIZED_RULE);
		return super.getValidationRules();		
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		Map transformMap = context.getTargetTransformMap();
		SqlTableAspect tableAspect = (SqlTableAspect) AspectManager.getSqlAspect(eObject);
		if(tableAspect.isVirtual(eObject)) {
			if(transformMap != null) {
				if(transformMap.containsKey(eObject)) {
					return;
				}
			}
			context.addTargetTransform(eObject, null);
		}
	}
}

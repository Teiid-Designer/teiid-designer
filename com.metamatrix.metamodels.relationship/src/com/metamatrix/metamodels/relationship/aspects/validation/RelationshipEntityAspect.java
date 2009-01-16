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

package com.metamatrix.metamodels.relationship.aspects.validation;

import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.aspects.validation.rules.RelationshipEntityValidationRule;
import com.metamatrix.metamodels.relationship.aspects.validation.rules.RelationshipStringNameRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;

/**
 * RelationshipEntityAspect.java
 */
public abstract class RelationshipEntityAspect extends AbstractValidationAspect	{

	public static final ValidationRule NAME_RULE = new RelationshipStringNameRule(RelationshipPackage.RELATIONSHIP_ENTITY__NAME);
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(RelationshipPackage.RELATIONSHIP_ENTITY__NAME);
	public static final ValidationRule RELATIONSHIP_ENTITY_RULE = new RelationshipEntityValidationRule();

	protected RelationshipEntityAspect(final MetamodelEntity entity) {
		super(entity);
	}

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
		addRule(RELATIONSHIP_ENTITY_RULE);
		return super.getValidationRules();		
	}
}

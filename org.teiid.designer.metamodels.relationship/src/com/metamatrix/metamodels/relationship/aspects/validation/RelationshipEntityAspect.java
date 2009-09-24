/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

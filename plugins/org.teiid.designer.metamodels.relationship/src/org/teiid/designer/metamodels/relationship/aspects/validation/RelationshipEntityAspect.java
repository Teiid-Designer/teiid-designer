/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.core.validation.rules.StringLengthRule;
import org.teiid.designer.metamodels.relationship.RelationshipPackage;
import org.teiid.designer.metamodels.relationship.aspects.validation.rules.RelationshipEntityValidationRule;
import org.teiid.designer.metamodels.relationship.aspects.validation.rules.RelationshipStringNameRule;


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

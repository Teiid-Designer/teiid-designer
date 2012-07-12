/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xsd.aspects.validation;

import org.eclipse.xsd.XSDPackage;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.metamodels.xsd.aspects.validation.rules.BaseTypeRule;
import org.teiid.designer.metamodels.xsd.aspects.validation.rules.ItemTypeRule;


/**
 * SimpleDatatypeAspect
 */
public class XsdSimpleTypeDefinitionAspect extends AbstractValidationAspect {
    
    public static final ValidationRule BASE_TYPE_RULE = new BaseTypeRule(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__BASE_TYPE_DEFINITION);
    public static final ValidationRule ITEM_TYPE_RULE = new ItemTypeRule(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__ITEM_TYPE_DEFINITION);

    /**
     * Construct an instance of SimpleDatatypeAspect.
     * @param entity
     */
    public XsdSimpleTypeDefinitionAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for SimpleDatatype.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(BASE_TYPE_RULE);
        addRule(ITEM_TYPE_RULE);
		return super.getValidationRules();
	}
}

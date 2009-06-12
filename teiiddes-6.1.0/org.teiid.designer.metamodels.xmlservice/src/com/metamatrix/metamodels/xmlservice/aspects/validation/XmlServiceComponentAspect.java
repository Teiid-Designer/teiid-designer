/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.validation;

import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.metamodels.xmlservice.aspects.validation.rules.GlobalSchemaReferenceRule;
import com.metamatrix.metamodels.xmlservice.aspects.validation.rules.MissingSchemaReferenceRule;
import com.metamatrix.metamodels.xmlservice.aspects.validation.rules.XmlServiceComponentStringNameRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;


/** 
 * XmlServiceComponentAspect
 */
public abstract class XmlServiceComponentAspect extends AbstractValidationAspect {

    public static final XmlServiceComponentStringNameRule NAME_RULE = new XmlServiceComponentStringNameRule(XmlServicePackage.XML_SERVICE_COMPONENT__NAME);
    public static final MissingSchemaReferenceRule MISSING_REF_RULE = new MissingSchemaReferenceRule();
    public static final GlobalSchemaReferenceRule GLOBAL_REF_RULE = new GlobalSchemaReferenceRule();

	/** 
     * @param entity
     * @since 4.2
     */
    public XmlServiceComponentAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		return super.getValidationRules();		
	}    
}

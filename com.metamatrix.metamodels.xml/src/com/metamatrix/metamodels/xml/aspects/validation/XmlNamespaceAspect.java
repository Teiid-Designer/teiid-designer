/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * XmlNamespaceAspect
 */
public class XmlNamespaceAspect extends AbstractXmlAspect {

    /**
     * Construct an instance of XmlNamespaceAspect.
     * @param entity
     */
    public XmlNamespaceAspect(MetamodelEntity entity){
        super(entity);
    }

	/**
	 * Get all the validation rules for XmlNamespace.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAMESPACE_RULE);
		return super.getValidationRules();
	}

}

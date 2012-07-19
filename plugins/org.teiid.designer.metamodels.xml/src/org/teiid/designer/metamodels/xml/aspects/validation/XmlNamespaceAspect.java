/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;


/**
 * XmlNamespaceAspect
 *
 * @since 8.0
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

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
 * XmlDocumentNodeAspect
 * @since 4.2
 */
public class XmlDocumentNodeAspect extends AbstractXmlNodeAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    public XmlDocumentNodeAspect(MetamodelEntity entity) {
        super(entity);
    }
    
    /**
     * Get all the validation rules for XmlDocumentNode.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(DOCUMENT_NODE_NAME_RULE);
        addRule(DOCUMENT_NODE_LENGTH_RULE);
        addRule(DOCUMENT_NODE_TYPE_RULE);
        return super.getValidationRules();
    }
}

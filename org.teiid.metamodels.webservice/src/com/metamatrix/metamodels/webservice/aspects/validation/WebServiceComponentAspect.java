/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.validation;

import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.GlobalSchemaReferenceRule;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.MessageContentRule;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.OutputDocumentValidationRule;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.SampleFileUrlRule;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.TargetNamespaceRule;
import com.metamatrix.metamodels.webservice.aspects.validation.rules.WebServiceComponentNameRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;


/** 
 * WebServiceComponentAspect
 */
public abstract class WebServiceComponentAspect extends AbstractValidationAspect {

    public static final WebServiceComponentNameRule NAME_RULE = new WebServiceComponentNameRule(WebServicePackage.WEB_SERVICE_COMPONENT__NAME);
    public static final StringLengthRule LENGTH_RULE = new StringLengthRule(WebServicePackage.WEB_SERVICE_COMPONENT__NAME);
    public static final OutputDocumentValidationRule OUTPUT_DOC_RULE = new OutputDocumentValidationRule(WebServicePackage.OUTPUT__XML_DOCUMENT);
    public static final MessageContentRule MESSAGE_CONTENT_RULE = new MessageContentRule();
    public static final SampleFileUrlRule FILE_URL_RULE = new SampleFileUrlRule(WebServicePackage.SAMPLE_FILE__URL);
    public static final GlobalSchemaReferenceRule GLOBAL_REF_RULE = new GlobalSchemaReferenceRule();
    public static final TargetNamespaceRule TARGET_NAMESPACE_RULE = new TargetNamespaceRule();

	/** 
     * @param entity
     * @since 4.2
     */
    public WebServiceComponentAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);		
		return super.getValidationRules();		
	}    
}

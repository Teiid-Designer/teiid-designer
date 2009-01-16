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

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

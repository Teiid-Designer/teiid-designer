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

package com.metamatrix.metamodels.xsd.aspects.validation;

import org.eclipse.xsd.XSDPackage;

import com.metamatrix.metamodels.xsd.aspects.validation.rules.BaseTypeRule;
import com.metamatrix.metamodels.xsd.aspects.validation.rules.ItemTypeRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

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

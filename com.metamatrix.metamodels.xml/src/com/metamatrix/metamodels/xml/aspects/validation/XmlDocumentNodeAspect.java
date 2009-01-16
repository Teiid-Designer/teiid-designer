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

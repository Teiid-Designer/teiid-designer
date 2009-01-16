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

package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;


/** 
 * @since 4.2
 */
public class AnnotationAspect extends CoreEntityAspect {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public AnnotationAspect(MetamodelEntity theEntity) {
        super(theEntity);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect#getValidationRules()
     * @since 4.2
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(ANNOTATION_EXTENSION_ATTRIBUTE_DEFAULT_VALUE_RULE);
        return super.getValidationRules();
    }
}

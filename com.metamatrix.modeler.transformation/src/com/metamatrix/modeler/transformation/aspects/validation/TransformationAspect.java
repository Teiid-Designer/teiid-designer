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

package com.metamatrix.modeler.transformation.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.transformation.aspects.validation.rules.InputParameterValidationRule;
import com.metamatrix.modeler.transformation.aspects.validation.rules.SqlTransformationMappingRootValidationRule;
import com.metamatrix.modeler.transformation.aspects.validation.rules.TransformationMappingValidationRule;
import com.metamatrix.modeler.transformation.aspects.validation.rules.XmlDocumentValidationRule;

/**
 * TransformationAspect
 */
public abstract class TransformationAspect extends AbstractValidationAspect {
	
    public static final ValidationRule MAPPING_RULE = new TransformationMappingValidationRule();
    public static final ValidationRule MAPPINGROOT_RULE = new SqlTransformationMappingRootValidationRule();
    public static final ValidationRule DOCUMENT_RULE = new XmlDocumentValidationRule();
	public static final ValidationRule BINDING_RULE = new InputParameterValidationRule();

    protected TransformationAspect(final MetamodelEntity entity) {
        super(entity);
    }
}

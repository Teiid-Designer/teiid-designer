/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.transformation.aspects.validation.rules.InputParameterValidationRule;
import org.teiid.designer.transformation.aspects.validation.rules.SqlTransformationMappingRootValidationRule;
import org.teiid.designer.transformation.aspects.validation.rules.TransformationMappingValidationRule;
import org.teiid.designer.transformation.aspects.validation.rules.XmlDocumentValidationRule;


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

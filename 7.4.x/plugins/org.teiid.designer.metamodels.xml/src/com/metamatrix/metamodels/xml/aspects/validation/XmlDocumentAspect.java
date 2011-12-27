/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.aspects.validation;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * XmlDocumentAspect
 */
public class XmlDocumentAspect extends AbstractXmlAspect {

    protected XmlDocumentAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#updateContext(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	@Override
    public void updateContext(final EObject eObject, final ValidationContext context) {
		Map transformMap = context.getTargetTransformMap();
		if(transformMap != null) {
			if(transformMap.containsKey(eObject)) {
				return;
			}
		}
		context.addTargetTransform(eObject, null);
	}
    
    /**
     * Get all the validation rules for XmlDocument.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(DOCUMENT_NAME_RULE);
        addRule(DOCUMENT_LENGTH_RULE);
        return super.getValidationRules();
    }
}

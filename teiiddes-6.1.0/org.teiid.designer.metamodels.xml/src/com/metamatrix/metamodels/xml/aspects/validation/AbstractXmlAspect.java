/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.aspects.validation;

import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.aspects.validation.rules.XmlDocumentNodeDatatypeRule;
import com.metamatrix.metamodels.xml.aspects.validation.rules.XmlNamespaceRule;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * AbstractXmlAspect
 */
public abstract class AbstractXmlAspect extends AbstractValidationAspect {

	public static final ValidationRule NAMESPACE_RULE = new XmlNamespaceRule();
    public static final ValidationRule DOCUMENT_NODE_NAME_RULE = new StringNameRule(XmlDocumentPackage.XML_DOCUMENT_NODE__NAME);
    public static final ValidationRule DOCUMENT_NAME_RULE = new StringNameRule(XmlDocumentPackage.XML_DOCUMENT__NAME);
    public static final ValidationRule DOCUMENT_NODE_LENGTH_RULE = new StringLengthRule(XmlDocumentPackage.XML_DOCUMENT_NODE__NAME);
    public static final ValidationRule DOCUMENT_LENGTH_RULE = new StringLengthRule(XmlDocumentPackage.XML_DOCUMENT__NAME);
    public static final ValidationRule DOCUMENT_NODE_TYPE_RULE = new XmlDocumentNodeDatatypeRule();

	protected AbstractXmlAspect(final MetamodelEntity entity) {
		super(entity);
	}

}

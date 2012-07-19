/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.rules.StringLengthRule;
import org.teiid.designer.core.validation.rules.StringNameRule;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.metamodels.xml.aspects.validation.rules.XmlDocumentNodeDatatypeRule;
import org.teiid.designer.metamodels.xml.aspects.validation.rules.XmlNamespaceRule;


/**
 * AbstractXmlAspect
 *
 * @since 8.0
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.extension;

import org.teiid.designer.core.extension.AbstractMetaclassNameProvider;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;

/**
 * Provides extendable metaclass names for the XML Document metamodel.
 * 
 * @since 8.0
 */
public class XmlExtendableClassnameProvider extends AbstractMetaclassNameProvider {

    /**
     * Constructs a provider.
     */
    public XmlExtendableClassnameProvider() {
        super(XmlDocumentPackage.eNS_URI);

        final String document = "org.teiid.designer.metamodels.xml.impl.XmlDocumentImpl"; //$NON-NLS-1$
        final String element = "org.teiid.designer.metamodels.xml.impl.XmlElementImpl"; //$NON-NLS-1$
        final String attribute = "org.teiid.designer.metamodels.xml.impl.XmlAttributeImpl"; //$NON-NLS-1$
        final String root = "org.teiid.designer.metamodels.xml.impl.XmlRootImpl"; //$NON-NLS-1$
        final String comment = "org.teiid.designer.metamodels.xml.impl.XmlCommentImpl"; //$NON-NLS-1$
        final String namespace = "org.teiid.designer.metamodels.xml.impl.XmlNamespaceImpl"; //$NON-NLS-1$
        final String sequence = "org.teiid.designer.metamodels.xml.impl.XmlSequenceImpl"; //$NON-NLS-1$
        final String all = "org.teiid.designer.metamodels.xml.impl.XmlAllImpl"; //$NON-NLS-1$
        final String choice = "org.teiid.designer.metamodels.xml.impl.XmlChoiceImpl"; //$NON-NLS-1$
        final String processingInstruction = "org.teiid.designer.metamodels.xml.impl.ProcessingInstructionImpl"; //$NON-NLS-1$

        addMetaclass(document, NO_PARENTS);
        addMetaclass(root, document);
        addMetaclass(comment, document, root);

        addMetaclass(element, root);
        addMetaclass(attribute, root);
        addMetaclass(namespace, root);
        addMetaclass(processingInstruction, root);
        addMetaclass(all, root);
        addMetaclass(choice, root);
        addMetaclass(sequence, root);
    }

}

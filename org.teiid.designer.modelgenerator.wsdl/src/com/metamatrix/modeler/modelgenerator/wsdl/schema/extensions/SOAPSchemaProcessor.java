/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.ElementImpl;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TextColumn;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TypeDefinition;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;
import com.metamatrix.modeler.schema.tools.processing.internal.ElementContentTraversalContext;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;

public class SOAPSchemaProcessor extends SchemaProcessorImpl {

    public SOAPSchemaProcessor( Logger log,
                                String separator ) {
        super(log, separator);
    }

    @Override
    public void processElementText( SchemaObject element ) {
        XSDSimpleTypeDefinition textType = element.getTextType();
        Column col;

        if (element instanceof SOAPArrayDefinition) {
            if (textType != null) { // scalar array type
                col = new SoapArrayColumn(false, textType, element);
                element.addAttribute(col);
            }
        } else if (textType != null) {
            col = new TextColumn(false, textType);
            element.addAttribute(col);
        }

    }

    @Override
    public void processType( XSDTypeDefinition type,
                             ElementContentTraversalContext traverseCtx2,
                             XSDSchema schema ) throws SchemaProcessingException {
        String namespacePrefix = getNameSpacePrefix(type.getTargetNamespace());

        SchemaObject typeDecl;
        boolean isSOAPArray = SOAPArrayDefinition.isSOAPArray(type);
        if (isSOAPArray) {
            XSDTypeDefinition arrayType = SOAPArrayDefinition.getSOAPArrayType(type);
            typeDecl = new SOAPArrayDefinition(type, arrayType, namespacePrefix, schema);
        } else {
            typeDecl = new TypeDefinition(type, namespacePrefix, schema);
        }

        String fileName = SchemaUtil.shortenFileName(schema.getSchemaLocation());
        typeDecl.setFileName(fileName);
        addElement(typeDecl);
        processAttributes(typeDecl, traverseCtx);
        processElementText(typeDecl);
        processElementContents(typeDecl, traverseCtx);
    }

    @Override
    public void processElement( XSDElementDeclaration elem,
                                ElementContentTraversalContext traverseCtx,
                                XSDSchema schema ) throws SchemaProcessingException {
        String name = elem.getName();
        String namespace = elem.getTargetNamespace();
        String fileName = SchemaUtil.shortenFileName(schema.getSchemaLocation());
        XSDElementDeclaration refElem = resolveElementRef(elem, traverseCtx);

        if (name == null) {
            if (refElem != elem) {
                processElement(refElem, traverseCtx, schema);
                return;
            }
            // How can the ref be null, or equal to the referencer if the name is null?
            log.log(IStatus.WARNING, ModelGeneratorWsdlPlugin.Util.getString("SOAPSchemaProcessor.processElementWarning")); //$NON-NLS-1$
            return;
        }

        if (refElem != null && refElem != elem) {
            // This case represents an anonymous use of a known type.
        }

        XSDTypeDefinition type = resolveElementType(elem, traverseCtx);

        // Elements can have the same name and the same type and be different elements
        // (e.g. <xsd:element name="foo" type="bar"/> appearing in multiple places)
        // We consider these the same table
        SchemaObject element;
        QName qname = SchemaUtil.getQName(namespace, name);
        Map tablesForName = traverseCtx.getElementsByNameThenType(qname);
        element = (SchemaObject)tablesForName.get(type);

        boolean isSoapType = false;
        if (element == null) {
            XSDTypeDefinition soapType = SOAPArrayDefinition.getSOAPArrayType(type);
            if (null != soapType) {
                isSoapType = true;
                element = new SOAPElement(elem, getNameSpacePrefix(elem.getTargetNamespace()), soapType, schema);
                type = soapType;
            } else {
                element = new ElementImpl(elem, getNameSpacePrefix(elem.getTargetNamespace()), type, schema);
            }
            element.setFileName(fileName);
            // It's important to put the table in the map before we recurse down
            // the element's contents, to prevent infinite recursion of circular
            // references
            tablesForName.put(type, element);
            addElement(element);

            processAttributes(element, traverseCtx);
            processElementText(element);
            processElementContents(element, traverseCtx);
        }

        int minOccurs = traverseCtx.calculateMinOccurs(1);
        int maxOccurs = traverseCtx.calculateMaxOccurs(1);

        if (isSoapType) {
            // If the element is a SOAP Array type we have to set the maxOccurs to unlimited.
            element.addParent(traverseCtx.getParentTable(), minOccurs, -1);
        } else {
            element.addParent(traverseCtx.getParentTable(), minOccurs, maxOccurs);
        }
    }
}

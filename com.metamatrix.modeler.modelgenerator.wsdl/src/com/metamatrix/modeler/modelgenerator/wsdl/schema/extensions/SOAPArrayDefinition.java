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

package com.metamatrix.modeler.modelgenerator.wsdl.schema.extensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.ComplexSchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.ISchemaModelCopyTraversalContext;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObjectKey;
import com.metamatrix.modeler.schema.tools.model.schema.impl.BaseSchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.RootElementImpl;
import com.metamatrix.modeler.schema.tools.model.schema.impl.TypeDefinitionKey;

/**
 * A SOAP Array is declared in a Schema as a restriction to type soapenc:Array. The wsdl:arrayType declares what the type or the
 * array contents is. This will always be a type and not an element. <xsd:complexType name="ArrayOfPairs"> <xsd:complexContent>
 * <xsd:restriction base="soapenc:Array"> <xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="pair[]" /> </xsd:restriction>
 * </xsd:complexContent> </xsd:complexType> This causes us problems when we process the schema because we never before had to
 * establish a parent relation to a type declaration, in schema types don't have parents, only elements do. Because of this, an
 * extra step has to happen for SOAPArrayDefinitions. They have to find and maintain a link to the type that it contains, so when
 * it is modeled the contents of the contained type are modeled correctly.
 */

public class SOAPArrayDefinition extends BaseSchemaObject implements ComplexSchemaObject {

    XSDTypeDefinition arrayType;

    public SOAPArrayDefinition( XSDTypeDefinition type,
                                XSDTypeDefinition arrayType,
                                String namespacePrefix,
                                XSDSchema schema ) {
        super(namespacePrefix, type, schema);
        this.arrayType = arrayType;
        this.doesNotHaveUniqueName = false;
        this.schema = schema;
    }

    public SchemaObject copy( ISchemaModelCopyTraversalContext ctx ) {
        return new SOAPArrayDefinition(type, arrayType, getNamespacePrefix(), schema);
    }

    public String getSimpleName() {
        String elemName = type.getName();
        String uniqueName;
        if (doesNotHaveUniqueName) {
            String typeName = type.getAliasName();
            uniqueName = elemName + '(' + typeName + ')';
        } else {
            uniqueName = elemName;
        }
        return uniqueName;
    }

    public XSDTypeDefinition getType() {
        return arrayType;
    }

    public String getElementTypeNamespace() {
        return type.getTargetNamespace();
    }

    public String getName() {
        return "/*"; //$NON-NLS-1$
    }

    public String getNamespace() {
        return type.getTargetNamespace();
    }

    public RootElement getRootRepresentation() {
        return new RootElementImpl(getKey(), getSimpleName(), getNamespace(), true);
    }

    public String getCatalog() {
        return null;
    }

    public int getMinOccurs() {
        return 0;
    }

    public int getMaxOccurs() {
        return -1;
    }

    public SchemaObjectKey getKey() {
        return new TypeDefinitionKey(type);
    }

    @Override
    public XSDSchema getSchema() {
        return schema;
    }

    @Override
    public boolean hasComplexTypeDefinition() {
        return arrayType instanceof XSDComplexTypeDefinition;
    }

    @Override
    public boolean hasSimpleTypeDefinition() {
        return arrayType instanceof XSDSimpleTypeDefinition;
    }

    @Override
    public List getAttributeList() {
        List result;
        if (arrayType instanceof XSDComplexTypeDefinition) {
            result = ((XSDComplexTypeDefinition)arrayType).getAttributeUses();
        } else {
            result = new ArrayList();
        }
        return result;
    }

    @Override
    public XSDComplexTypeContent getContent() {
        XSDComplexTypeContent result = null;
        if (arrayType instanceof XSDComplexTypeDefinition) {
            result = ((XSDComplexTypeDefinition)arrayType).getContent();
        }
        return result;
    }

    @Override
    public String toString() {
        return getName() + "(" + getNamespace() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String getRelativeXpath() {
        return ""; //$NON-NLS-1$
    }

    public static boolean isSOAPArray( XSDTypeDefinition type ) {
        boolean isSoapArray = false;
        if (type instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
            isSoapArray = complexType.getBaseTypeDefinition().getURI().equals("http://schemas.xmlsoap.org/soap/encoding/#Array"); //$NON-NLS-1$
        }
        return isSoapArray;
    }

    public static XSDTypeDefinition getSOAPArrayType( XSDTypeDefinition type ) {
        XSDTypeDefinition soapArrayElement = null;
        if (type instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
            if (complexType.isSetDerivationMethod()) {
                soapArrayElement = getSOAPEncodingArrayItemType(complexType);
            }
        }
        return soapArrayElement;
    }

    // Determine if a soap array is defined within the TypeDefinition.
    // If so, it returns the XSDTypeDefinition, otherwise null.
    static protected XSDTypeDefinition getSOAPEncodingArrayItemType( XSDComplexTypeDefinition xsdComplexTypeDefinition ) {
        if (xsdComplexTypeDefinition.getBaseTypeDefinition().getURI().equals("http://schemas.xmlsoap.org/soap/encoding/#Array")) { //$NON-NLS-1$

            // there's only one item here
            for (Iterator attributeContents = xsdComplexTypeDefinition.getAttributeContents().iterator(); attributeContents.hasNext();) {
                XSDAttributeUse xsdAttributeUse = (XSDAttributeUse)attributeContents.next();
                Element attributeElement = xsdAttributeUse.getElement();
                if (attributeElement != null && attributeElement.hasAttributeNS("http://schemas.xmlsoap.org/wsdl/", "arrayType")) { //$NON-NLS-1$ //$NON-NLS-2$
                    String arrayType = attributeElement.getAttributeNS("http://schemas.xmlsoap.org/wsdl/", "arrayType"); //$NON-NLS-1$//$NON-NLS-2$
                    int index = arrayType.indexOf("["); //$NON-NLS-1$
                    if (index != -1) {
                        String arrayTypeURI = XSDConstants.lookupQName(attributeElement, arrayType.substring(0, index));
                        XSDTypeDefinition itemTypeDefinition = xsdAttributeUse.resolveTypeDefinitionURI(arrayTypeURI);
                        return itemTypeDefinition;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List getAllModelColumns() {
        ArrayList columns = new ArrayList();
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
            DataType datatype = getDataType();
            Column column = (Column)iter.next();
            com.metamatrix.modeler.schema.tools.model.jdbc.Column col = column.getColumnImplementation();
            col.setDataType(datatype);
            col.setMultipleValues(4); // XMLHTTPExtensionManager.COMPLEX_SOAP_ARRAY_MULTIPLE_VALUES
            columns.add(col);
        }
        return columns;
    }

    private DataType getDataType() {

        return new SoapDataTypeImpl(arrayType.getName(), arrayType.getTargetNamespace(), getSimpleName(), getNamespace());
    }
}

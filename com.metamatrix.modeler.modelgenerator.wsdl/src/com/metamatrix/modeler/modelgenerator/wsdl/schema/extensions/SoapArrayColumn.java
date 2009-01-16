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

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.ColumnImpl;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.BaseColumn;

public class SoapArrayColumn extends BaseColumn {

    SchemaObject soapElement;

    public SoapArrayColumn( boolean pk,
                            XSDSimpleTypeDefinition type,
                            SchemaObject element ) {
        super(pk, type);
        this.soapElement = element;
    }

    public String getXpath() {
        return getType().getName() + "/text()"; //$NON-NLS-1$
    }

    public String getSimpleName() {
        return getType().getName();
    }

    public Column copy() {
        return new SoapArrayColumn(pk, getType(), soapElement);
    }

    public void printDebug() {
        // TODO Auto-generated method stub

    }

    @Override
    protected DataType getDataType() {
        String arrayName;
        String arrayNamespace;
        if (soapElement instanceof SOAPArrayDefinition) {
            SOAPArrayDefinition arrayDef = (SOAPArrayDefinition)soapElement;
            arrayName = arrayDef.getType().getName();
            arrayNamespace = arrayDef.getType().getTargetNamespace();
        } else if (soapElement instanceof SOAPElement) {
            SOAPElement elem = (SOAPElement)soapElement;
            arrayName = elem.getName();
            arrayNamespace = elem.getNamespace();
        } else {
            throw new RuntimeException(ModelGeneratorWsdlPlugin.Util.getString("SoapArrayColumn.invalidDataType")); //$NON-NLS-1$
        }
        return new SoapDataTypeImpl(type.getName(), type.getTargetNamespace(), arrayName, arrayNamespace);
    }

    public com.metamatrix.modeler.schema.tools.model.jdbc.Column getColumnImplementation() {
        ColumnImpl newColumn = new ColumnImpl();
        newColumn.setDataAttributeName(getSimpleName());
        newColumn.setDataType(getDataType());
        newColumn.setIsAttributeOfParent(false);
        newColumn.setIsInputParameter(false);
        newColumn.setName(getSimpleName());
        newColumn.setOutputXPath(getXpath());
        newColumn.setMultipleValues(3);
        return newColumn;
    }
}

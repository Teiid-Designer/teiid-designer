/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.ColumnImpl;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;

public class IdColumn extends BaseColumn {
    public IdColumn( XSDSimpleTypeDefinition type ) {
        super(true, type);
    }

    public String getXpath() {
        String xpath = "@com.metamatrix.xml.xpathpart"; //$NON-NLS-1$
        return xpath;
    }

    public String getSimpleName() {
        String name = "mmid"; //$NON-NLS-1$
        return name;
    }

    @Override
    public Column mergeIntoParent( Relationship tableRelationship,
                                   int iOccurence ) {
        return null;
    }

    public Column copy() {
        IdColumn copy = new IdColumn(getType());
        return copy;
    }

    public void printDebug() {
        System.out.println("\t \t IDColumn"); //$NON-NLS-1$
    }

    public com.metamatrix.modeler.schema.tools.model.jdbc.Column getColumnImplementation() {
        ColumnImpl newColumn = new ColumnImpl();
        newColumn.setDataAttributeName(getSimpleName());
        newColumn.setDataType(getDataType());
        newColumn.setIsAttributeOfParent(false);
        newColumn.setIsInputParameter(false);
        newColumn.setName(getSimpleName());
        newColumn.setOutputXPath(getXpath());
        return newColumn;
    }
}

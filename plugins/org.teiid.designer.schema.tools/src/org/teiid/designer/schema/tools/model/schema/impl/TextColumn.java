/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.designer.schema.tools.model.jdbc.internal.ColumnImpl;
import org.teiid.designer.schema.tools.model.schema.Column;

/**
 * @since 8.0
 */
public class TextColumn extends BaseColumn {
    public TextColumn( boolean pk,
                       XSDSimpleTypeDefinition type ) {
        super(pk, type);
    }

    @Override
	public String getXpath() {
        String xpath = "text()"; //$NON-NLS-1$
        return xpath;
    }

    @Override
	public String getSimpleName() {
        String name = "text"; //$NON-NLS-1$
        return name;
    }

    @Override
	public Column copy() {
        Column copy = new TextColumn(isPrimaryKey(), getType());
        return copy;
    }

    @Override
	public void printDebug() {
        System.out.println("\t \t TextColumn"); //$NON-NLS-1$
    }

    @Override
	public org.teiid.designer.schema.tools.model.jdbc.Column getColumnImplementation() {
        ColumnImpl newColumn = new ColumnImpl();
        newColumn.setDataAttributeName(getSimpleName());
        newColumn.setDataType(getDataType());
        newColumn.setIsAttributeOfParent(true);
        newColumn.setIsInputParameter(false);
        newColumn.setName(getSimpleName());
        newColumn.setOutputXPath(getXpath());
        return newColumn;
    }
}

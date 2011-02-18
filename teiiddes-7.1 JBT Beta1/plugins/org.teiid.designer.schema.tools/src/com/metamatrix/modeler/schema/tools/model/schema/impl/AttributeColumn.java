/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDAttributeDeclaration;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.ColumnImpl;
import com.metamatrix.modeler.schema.tools.model.schema.Column;

public class AttributeColumn extends BaseColumn {
    private XSDAttributeDeclaration attr;
    private String prefix;

    public AttributeColumn( XSDAttributeDeclaration attr,
                            String prefix,
                            boolean pk ) {
        super(pk, attr.getTypeDefinition());
        this.attr = attr;
        this.prefix = prefix;
    }

    public String getXpath() {
        String xpath;
        String name = attr.getName();
        if (prefix != null && !prefix.equals("")) { //$NON-NLS-1$
            xpath = "@" + prefix + ':' + name; //$NON-NLS-1$
        } else {
            xpath = "@" + name; //$NON-NLS-1$
        }
        return xpath;
    }

    public String getSimpleName() {
        String name = attr.getName();
        return name;
    }

    public Column copy() {
        return new AttributeColumn(attr, prefix, super.isPrimaryKey());
    }

    public void printDebug() {
        StringBuffer buff = new StringBuffer("\t \t"); //$NON-NLS-1$
        buff.append("Attribute Column:"); //$NON-NLS-1$
        buff.append("SimpleName = " + getSimpleName()); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("Xpath = " + getXpath()); //$NON-NLS-1$
        buff.append(" "); //$NON-NLS-1$
        buff.append("prefix = " + prefix); //$NON-NLS-1$
        System.out.println(buff.toString());
    }

    public com.metamatrix.modeler.schema.tools.model.jdbc.Column getColumnImplementation() {
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

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

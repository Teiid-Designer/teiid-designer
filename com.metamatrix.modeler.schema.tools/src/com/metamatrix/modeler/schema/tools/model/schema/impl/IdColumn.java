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

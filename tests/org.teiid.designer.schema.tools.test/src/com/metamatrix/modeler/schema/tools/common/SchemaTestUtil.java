/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.common;

import java.util.Iterator;
import java.util.List;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;

public class SchemaTestUtil {

    public XSDSchema importSchema( String path ) {
        XSDParser parser = new XSDParser(null);
        parser.parse(path);
        XSDSchema schema = parser.getSchema();
        schema.setSchemaLocation(path);
        return schema;
    }

    public XSDSchema[] importSchemas( List paths ) {
        XSDSchema[] result = new XSDSchema[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            String path = (String)paths.get(i);
            result[i] = importSchema(path);
        }
        return result;

    }

    public static void printTables( SchemaModel clone ) {
        List tables = clone.getTables();
        System.out.println("Number of Tables = " + tables.size()); //$NON-NLS-1$
        for (Iterator iter = tables.iterator(); iter.hasNext();) {
            Table table = (Table)iter.next();
            StringBuffer buff = new StringBuffer();
            buff.append("Table: Name=" + table.getName()); //$NON-NLS-1$
            buff.append(" "); //$NON-NLS-1$
            buff.append("Catalog=" + table.getCatalog()); //$NON-NLS-1$
            buff.append(" "); //$NON-NLS-1$
            buff.append("NamespaceDeclaration=" + table.getNamespaceDeclaration()); //$NON-NLS-1$
            buff.append(" "); //$NON-NLS-1$
            buff.append("InputXPath=" + table.getInputXPath()); //$NON-NLS-1$
            buff.append(" "); //$NON-NLS-1$
            buff.append("OutputXPath=" + table.getOutputXPath()); //$NON-NLS-1$
            System.out.println(buff.toString());

            Column[] columns = table.getColumns();
            System.out.println("\t Number of Attributes = " + columns.length); //$NON-NLS-1$
            for (int i = 0; i < columns.length; i++) {
                Column column = columns[i];
                StringBuffer baff = new StringBuffer();
                baff.append("\t Attribute: "); //$NON-NLS-1$
                baff.append("Name = " + column.getName()); //$NON-NLS-1$
                baff.append(" "); //$NON-NLS-1$
                baff.append("InputXPath = " + column.getInputXPath()); //$NON-NLS-1$
                baff.append(" "); //$NON-NLS-1$
                baff.append("OutputXPath = " + column.getOutputXPath()); //$NON-NLS-1$
                baff.append(" "); //$NON-NLS-1$
                DataType data = column.getDataType();
                baff.append("DataType Name = " + data.getTypeName()); //$NON-NLS-1$
                baff.append(" "); //$NON-NLS-1$
                baff.append("DataType Namespace = " + data.getTypeNamespace()); //$NON-NLS-1$
                System.out.println(baff.toString());
            }

            Table[] helloChildren = table.getChildTables();
            System.out.println("\t Number of Children = " + helloChildren.length); //$NON-NLS-1$

            for (int i = 0; i < helloChildren.length; i++) {
                Table child = helloChildren[i];
                StringBuffer baff = new StringBuffer();
                baff.append("\t Child Table: "); //$NON-NLS-1$
                baff.append("Name = " + child.getName()); //$NON-NLS-1$
                baff.append(" "); //$NON-NLS-1$
                baff.append("Relation to Parent = " + child.getRelationToParent()); //$NON-NLS-1$
                System.out.println(baff.toString());
            }

            Table[] parents = table.getParentTables();
            System.out.println("\t Number of Parents = " + parents.length); //$NON-NLS-1$
            for (int i = 0; i < parents.length; i++) {
                Table parent = parents[i];
                StringBuffer baff = new StringBuffer();
                baff.append("\t Parent Table: "); //$NON-NLS-1$
                baff.append(parent.getName());
                System.out.println(baff.toString());
            }
        }
    }
}

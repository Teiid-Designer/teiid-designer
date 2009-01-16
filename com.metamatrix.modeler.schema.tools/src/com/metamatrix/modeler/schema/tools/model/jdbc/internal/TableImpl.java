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

package com.metamatrix.modeler.schema.tools.model.jdbc.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Namespace;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;
import com.metamatrix.modeler.schema.tools.model.jdbc.Table;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.model.schema.impl.SchemaModelImpl;

public class TableImpl extends DatabaseElementImpl implements Table {

    private String m_catalog;

    private String m_schema;

    private ArrayList m_namespaces;

    private ArrayList m_columns;

    protected SchemaObject element;

    public SchemaModel schemaModel;

    // huge ugly hack - we need to flag one table as special for the WSDL Importer see defect 23639
    private boolean isBase;

    private final String[] wellKnownNamespaces = {"http://schemas.xmlsoap.org/soap/envelope/", //$NON-NLS-1$
        "http://www.metamatrix.com/dummy", //$NON-NLS-1$
        "http://schemas.xmlsoap.org/soap/encoding/", //$NON-NLS-1$
        "http://www.w3.org/1999/XMLSchema-instance", //$NON-NLS-1$
        "http://www.w3.org/1999/XMLSchema", //$NON-NLS-1$
        "http://www.w3.org/2001/XMLSchema", //$NON-NLS-1$
        "http://soap-authentication.org/2001/10/", //$NON-NLS-1$
        "http://schemas.xmlsoap.org/wsdl/", //$NON-NLS-1$
        "http://schemas.xmlsoap.org/wsdl/soap/", //$NON-NLS-1$
    };

    public TableImpl() {
        super();
        m_catalog = null;
        m_namespaces = new ArrayList();
        m_columns = new ArrayList();
    }

    public TableImpl( String name,
                      String catalog,
                      String inputXPath,
                      String outputXPath ) {
        super(name, inputXPath, outputXPath);
        setCatalog(catalog);
        m_namespaces = new ArrayList();
        m_columns = new ArrayList();
    }

    public void setElement( SchemaObject element ) {
        this.element = element;
    }

    public void setSchemaModel( SchemaModel schemaModel ) {
        this.schemaModel = schemaModel;
    }

    public String getNamespaceDeclaration() {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < m_namespaces.size(); i++) {
            Namespace ns = (Namespace)m_namespaces.get(i);
            if (!testForWellKnownNS(ns)) {
                String pre = (ns.getPrefix() == null || ns.getPrefix().trim().equals("")) ? "xmlns" : "xmlns:" + ns.getPrefix(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                buff.append(pre);
                buff.append("='"); //$NON-NLS-1$
                buff.append(ns.getURI());
                buff.append("' "); //$NON-NLS-1$
            }
        }
        return buff.toString().trim();
    }

    private boolean testForWellKnownNS( Namespace ns ) {
        boolean isWellKnown = false;
        String nsUri = ns.getURI();
        for (int i = 0; i < wellKnownNamespaces.length; i++) {
            if (wellKnownNamespaces[i].equals(nsUri)) {
                isWellKnown = true;
                break;
            }
        }
        return isWellKnown;
    }

    public void addNamespace( Namespace ns ) {
        m_namespaces.add(ns);
    }

    public String getCatalog() {
        return m_catalog;
    }

    public void setCatalog( String catalog ) {
        m_catalog = catalog;
    }

    public void addColumn( Column column ) {
        m_columns.add(column);
    }

    public Column[] getColumns() {
        List columns = null;
        if (element != null && !isBase()) {
            columns = element.getAllModelColumns();
        } else {
            columns = new ArrayList();
        }
        columns.addAll(m_columns);
        Column[] column = new Column[columns.size()];
        columns.toArray(column);
        return column;
    }

    public Table[] getChildTables() {
        List children = new ArrayList();
        if (null != element) {
            for (Iterator iter = element.getChildren().iterator(); iter.hasNext();) {
                Relationship relationship = (Relationship)iter.next();
                SchemaObject child = relationship.getChild();
                Table table = schemaModel.findTable(child.getSimpleName());
                children.add(table);
            }
        }
        Table[] tables = new Table[children.size()];
        children.toArray(tables);
        return tables;
    }

    public Table[] getParentTables() {
        List parents = new ArrayList();
        for (Iterator iter = element.getParents().iterator(); iter.hasNext();) {
            Relationship relationship = (Relationship)iter.next();
            SchemaObject parent = relationship.getParent();
            Table table = schemaModel.findTable(parent.getSimpleName());
            if (null != table) {
                parents.add(table);
            }
        }
        Table[] tables = new Table[parents.size()];
        parents.toArray(tables);
        return tables;
    }

    public int getRelationToParent() {
        String key = element.getSimpleName() + ':' + element.getNamespace();
        return ((SchemaModelImpl)schemaModel).getRelationToParent(key);
    }

    // For each of my parent relations, compare the name of the parent in the relation
    // to the name of the parent parameter to find the correct relation.
    public Relationship getRelationObjectToParent( Table parent ) {
        Relationship result = null;
        SchemaObject parentElement = parent.getElement();
        List parents = element.getParents();
        for (Iterator iter = parents.iterator(); iter.hasNext();) {
            Relationship parentRelation = (Relationship)iter.next();
            if (parentRelation.getParent().getSimpleName().equals(parentElement.getSimpleName())) {
                result = parentRelation;
                break;
            }
        }
        return result;
    }

    public String getSchema() {
        return m_schema;
    }

    public void setSchema( String schema ) {
        m_schema = schema;
    }

    public SchemaObject getElement() {
        return element;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setBase( boolean isBase ) {
        this.isBase = isBase;
    }

    public int getMaxOccurs() {
        return element.getMaxOccurs();
    }

    @Override
    public String toString() {
        return getName();
    }
}

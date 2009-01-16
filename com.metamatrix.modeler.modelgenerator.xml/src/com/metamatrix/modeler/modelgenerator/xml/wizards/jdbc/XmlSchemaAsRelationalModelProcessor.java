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

package com.metamatrix.modeler.modelgenerator.xml.wizards.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.metadata.JdbcCatalog;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;
import com.metamatrix.modeler.modelgenerator.xml.model.DatabaseMetaDataImpl;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.BaseXMLRelationalExtensionManager;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.XMLHTTPExtensionManager;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;
import com.metamatrix.modeler.modelgenerator.xml.wizards.StateManager;
import com.metamatrix.modeler.modelgenerator.xml.wizards.XsdAsRelationalImportWizard;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;

public class XmlSchemaAsRelationalModelProcessor extends RelationalModelProcessorImpl {

    BaseXMLRelationalExtensionManager extensions;

    public XmlSchemaAsRelationalModelProcessor() {
        super();
        extensions = XsdAsRelationalImportWizard.extManager;
    }

    public XmlSchemaAsRelationalModelProcessor( RelationalFactory factory ) {
        super(factory);
        extensions = XsdAsRelationalImportWizard.extManager;
    }

    public XmlSchemaAsRelationalModelProcessor( RelationalFactory factory,
                                                RelationalTypeMapping mapping ) {
        super(factory, mapping);
        extensions = XsdAsRelationalImportWizard.extManager;
    }

    @Override
    protected void setNameAndNameInSource( final RelationalEntity entity,
                                           final String name,
                                           final JdbcNode node,
                                           final Context context ) {
        // This is the only callout that we get from the base model processor,
        // so we use it for things other than name and name in source
        super.setNameAndNameInSource(entity, name, node, context);
        processNewEntity(entity, name, node, context);
    }

    protected void processNewEntity( final RelationalEntity entity,
                                     final String name,
                                     final JdbcNode node,
                                     final Context context ) {
        if (entity instanceof Catalog) {
            Catalog catalog = (Catalog)entity;
            processNewCatalog(name, node, context, catalog);
        } else if (entity instanceof Table) {
            Table table = (Table)entity;
            processNewTable(name, node, context, table);
        }
    }

    protected void processNewTable( final String name,
                                    final JdbcNode node,
                                    final Context context,
                                    Table table ) {
        JdbcTable tableNode = (JdbcTable)node;
        // Let's make sure that the model type is physical, and that the
        // resource and
        // the entity are appropriately related to each other, so that we can
        // set the extension attributes.
        // This looks awfully hacky to me.
        ModelAnnotation modelAnnotation = context.getModelContents().getModelAnnotation();
        ModelType oldtype = modelAnnotation.getModelType();
        Catalog oldCatalog = table.getCatalog();

        modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
        context.getResource().getContents().add(table);

        try {
            setTableNamespacePrefixes(table, tableNode, name, context);
            addResponseField(table, tableNode, name, context);
            processRequestAttributes(table, tableNode, name, context);
        } finally {
            context.getResource().getContents().remove(table);
            if (oldCatalog != null) {
                table.setCatalog(oldCatalog);
            }
            modelAnnotation.setModelType(oldtype);
        }
    }

    protected void processNewCatalog( final String name,
                                      final JdbcNode node,
                                      final Context context,
                                      Catalog catalog ) {
        setCatalogNamespacePrefixes(catalog, node, name, context);
    }

    @Override
    protected String computeNameInSource( final RelationalEntity object,
                                          final String name,
                                          final JdbcNode node,
                                          final Context context,
                                          final boolean forced ) {
        String retval;
        if (object instanceof com.metamatrix.metamodels.relational.Table) {
            retval = getTableNameInSource(object, name, node, context);
        } else if (object instanceof com.metamatrix.metamodels.relational.Column) {
            retval = getColumnNameInSource(object, name, node, context);
        } else if (object instanceof com.metamatrix.metamodels.relational.PrimaryKey) {
            retval = getPrimaryKeyNameInSource(object, name, node, context);
        } else if (object instanceof com.metamatrix.metamodels.relational.ForeignKey) {
            retval = getForeignKeyNameInSource(object, name, node, context);
        } else {
            retval = super.computeNameInSource(object, name, node, context, forced);
        }
        return retval;
    }

    protected String getTableNameInSource( final RelationalEntity object,
                                           final String name,
                                           final JdbcNode node,
                                           final Context context ) {
        DatabaseMetaDataImpl databaseMetaData = getMetaDataImpl(context);
        QName qname = getTableQName(node);
        String nameInSource = databaseMetaData.getTableNameInSource(name, qname.getNamespace());
        return nameInSource;
    }

    protected String getColumnNameInSource( final RelationalEntity object,
                                            final String name,
                                            final JdbcNode node,
                                            final Context context ) {
        DatabaseMetaDataImpl databaseMetaData = getMetaDataImpl(context);
        QName qname = getTableQName(node);
        String nameInSource = databaseMetaData.getColumnNameInSource(name, qname.getNamespace(), qname.getLName());
        return nameInSource;
    }

    protected String getPrimaryKeyNameInSource( final RelationalEntity object,
                                                final String name,
                                                final JdbcNode node,
                                                final Context context ) {
        DatabaseMetaDataImpl databaseMetaData = getMetaDataImpl(context);
        QName qname = getTableQName(node);
        String nameInSource = databaseMetaData.getPrimaryKeyNameInSource(name, qname.getNamespace());
        return nameInSource;
    }

    protected String getForeignKeyNameInSource( final RelationalEntity object,
                                                final String name,
                                                final JdbcNode node,
                                                final Context context ) {
        DatabaseMetaDataImpl databaseMetaData = getMetaDataImpl(context);
        QName qname = getTableQName(node);
        String nameInSource = databaseMetaData.getForeignKeyNameInSource(name, qname.getNamespace());
        return nameInSource;
    }

    private QName getTableQName( final JdbcNode node ) {
        JdbcTable jdbcTable = (JdbcTable)node;
        String tableName = jdbcTable.getName();
        String tableNamespace = null;
        JdbcNode catalogNode = node;
        while (catalogNode != null && !(catalogNode instanceof JdbcCatalog)) {
            catalogNode = catalogNode.getParent();
        }
        if (catalogNode instanceof JdbcCatalog) {
            JdbcCatalog jdbcCatalog = (JdbcCatalog)catalogNode;
            tableNamespace = jdbcCatalog.getName();
        }
        QName qname = SchemaUtil.getQName(tableNamespace, tableName);
        return qname;
    }

    private void setCatalogNamespacePrefixes( Catalog entity,
                                              JdbcNode node,
                                              String name,
                                              Context context ) {
        // Let's make sure that the model type is physical, and that the
        // resource and
        // the entity are appropriately related to each other, so that we can
        // set the extension attributes.
        // This looks awfully hacky to me.
        ModelAnnotation modelAnnotation = context.getModelContents().getModelAnnotation();
        ModelType oldtype = modelAnnotation.getModelType();

        modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
        context.getResource().getContents().add(entity);

        try {
            setNamespacePrefixesAttribute(entity, name, context);
        } finally {
            context.getResource().getContents().remove(entity);
            modelAnnotation.setModelType(oldtype);
        }
    }

    private void addResponseField( Table entity,
                                   JdbcTable node,
                                   String name,
                                   Context context ) {
        QName qname = getTableQName(node);
        DatabaseMetaDataImpl metaDataImpl = getMetaDataImpl(context);
        Boolean requestTriState = metaDataImpl.isRequestOrResponseTable(qname);

        if (requestTriState == null) {
            return;
        }

        boolean request = requestTriState.booleanValue();

        List problems = new ArrayList();

        Column column = getFactory().createColumn();
        column.setOwner(entity);
        String colName = request ? XsdAsRelationalImportWizard.RESPONSE_ID_OUT_COL_NAME : XsdAsRelationalImportWizard.RESPONSE_ID_IN_COL_NAME;

        int arbitrarySize = 1000; // arbitrary number
        setColumnInfo(column,
                      node,
                      context,
                      problems,
                      colName,
                      Types.VARCHAR,
                      String.class.getName(),
                      arbitrarySize,
                      0,
                      0,
                      DatabaseMetaData.columnNullable,
                      "", //$NON-NLS-1$
                      0);
        column.setSelectable(request);
        if (extensions instanceof XMLHTTPExtensionManager) {
            ((XMLHTTPExtensionManager)extensions).setColumnRoleAttribute(column,
                                                                         (request ? XMLRequestResponseExtensionManagerImpl.RESPONSE_OUT_ROLE : XMLRequestResponseExtensionManagerImpl.RESPONSE_IN_ROLE));
            ((XMLHTTPExtensionManager)extensions).setColumnInputParamAttribute(column, (request ? Boolean.FALSE : Boolean.TRUE));
        }

        if (!request) {
            AccessPattern accessPattern = getFactory().createAccessPattern();
            accessPattern.setName(colName);
            accessPattern.setTable(entity);
            accessPattern.getColumns().add(column);
        }

        // TODO: figure out a way to get the problem back to the caller
    }

    private void processRequestAttributes( Table entity,
                                           JdbcTable node,
                                           String name,
                                           Context context ) {
        QName qname = getTableQName(node);
        DatabaseMetaDataImpl metaDataImpl = getMetaDataImpl(context);
        Boolean requestTriState = metaDataImpl.isRequestOrResponseTable(qname);

        if (requestTriState == null) {
            return;
        }

        boolean request = requestTriState.booleanValue();
        if (request) {
            setXpathRootForInputAttribute(entity);
        }
    }

    private void setXpathRootForInputAttribute( final RelationalEntity entity ) {
        String xrfi_attribute_value = null;
        xrfi_attribute_value = "/"; // else set it to / since a blank value is invalid. //$NON-NLS-1$
        ((XMLHTTPExtensionManager)extensions).setXPathRootForInputAttribute(entity, xrfi_attribute_value);
    }

    private void setTableNamespacePrefixes( Table entity,
                                            JdbcTable node,
                                            String name,
                                            Context context ) {
        QName qname = getTableQName(node);
        // TODO: remove this hack
        if (qname.getNamespace() != null && qname.getNamespace().equals(StateManager.globalNamespace)) {
            qname.setNamespace(null);
        }

        setNamespacePrefixesAttribute(entity, qname.getNamespace(), context);
    }

    private static String quoteString( String str ) {
        if (str.indexOf('\'') == -1) {
            return '\'' + str + '\'';
        } else if (str.indexOf('\"') == -1) {
            return '\"' + str + '\"';
        } else {
            // The namespace contains both ' and ".
            StringBuffer retval = new StringBuffer();
            retval.append('\'');
            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (c == '\'') {
                    retval.append("&apos;"); //$NON-NLS-1$
                } else {
                    retval.append(c);
                }
            }
            retval.append('\'');
            return retval.toString();
        }
    }

    private void setNamespacePrefixesAttribute( final RelationalEntity entity,
                                                final String name,
                                                final Context context ) {
        DatabaseMetaDataImpl metaDataImpl = getMetaDataImpl(context);
        if (metaDataImpl != null) {
            Map prefixes = metaDataImpl.getNamespacePrefixes(name);
            StringBuffer namespacePrefixes = new StringBuffer();
            for (Iterator iter = prefixes.keySet().iterator(); iter.hasNext();) {
                Object oNamespace = iter.next();
                if (oNamespace == null) continue;
                String namespace = (String)oNamespace;
                Object oPrefix = prefixes.get(oNamespace);
                String prefix = (String)oPrefix;
                if (namespacePrefixes.length() != 0) {
                    namespacePrefixes.append(' ');
                }
                namespacePrefixes.append("xmlns"); //$NON-NLS-1$
                if (!(prefix == null || prefix.equals(""))) { //$NON-NLS-1$
                    namespacePrefixes.append(':');
                }
                if (prefix != null) namespacePrefixes.append(prefix);
                namespacePrefixes.append('=');
                String quotedNamespace = quoteString(namespace);
                namespacePrefixes.append(quotedNamespace);
            }

            extensions.setNamespacePrefixesAttribute(entity, namespacePrefixes.toString());
        }
    }

    private DatabaseMetaDataImpl getMetaDataImpl( final Context context ) {
        JdbcDatabase db = context.getJdbcDatabase();
        DatabaseMetaData metaData;
        DatabaseMetaDataImpl metaDataImpl;
        try {
            metaData = db.getDatabaseMetaData();
        } catch (JdbcException e) {
            context.getWarnings().add(e);
            metaData = null;
        }

        if (metaData instanceof DatabaseMetaDataImpl) {
            metaDataImpl = (DatabaseMetaDataImpl)metaData;
        } else {
            metaDataImpl = null;
        }
        return metaDataImpl;
    }
}

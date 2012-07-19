/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.metadata.JdbcCatalog;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcTable;
import org.teiid.designer.jdbc.relational.impl.Context;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;
import org.teiid.designer.modelgenerator.xml.model.DatabaseMetaDataImpl;
import org.teiid.designer.modelgenerator.xml.modelextension.BaseXMLRelationalExtensionManager;
import org.teiid.designer.modelgenerator.xml.modelextension.XMLHTTPExtensionManager;
import org.teiid.designer.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;
import org.teiid.designer.modelgenerator.xml.wizards.StateManager;
import org.teiid.designer.modelgenerator.xml.wizards.XsdAsRelationalImportWizard;
import org.teiid.designer.schema.tools.model.schema.QName;
import org.teiid.designer.schema.tools.processing.SchemaUtil;


/**
 * @since 8.0
 */
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
                                           final Context context, 
                                           List problems ) {
        // This is the only callout that we get from the base model processor,
        // so we use it for things other than name and name in source
        super.setNameAndNameInSource(entity, name, node, context, problems);
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
                                          final boolean forced, 
                                          List problems ) {
        String retval;
        if (object instanceof org.teiid.designer.metamodels.relational.Table) {
            retval = getTableNameInSource(object, name, node, context);
        } else if (object instanceof org.teiid.designer.metamodels.relational.Column) {
            retval = getColumnNameInSource(object, name, node, context);
        } else if (object instanceof org.teiid.designer.metamodels.relational.PrimaryKey) {
            retval = getPrimaryKeyNameInSource(object, name, node, context);
        } else if (object instanceof org.teiid.designer.metamodels.relational.ForeignKey) {
            retval = getForeignKeyNameInSource(object, name, node, context);
        } else {
            retval = super.computeNameInSource(object, name, node, context, forced, problems);
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

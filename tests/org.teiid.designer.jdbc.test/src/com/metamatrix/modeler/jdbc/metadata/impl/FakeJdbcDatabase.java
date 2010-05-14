/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import junit.framework.Assert;
import com.metamatrix.modeler.jdbc.FakeConnection;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.metadata.Capabilities;
import com.metamatrix.modeler.jdbc.metadata.DatabaseInfo;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * FakeJdbcDatabase
 */
public class FakeJdbcDatabase extends JdbcDatabaseImpl {

    /**
     * Utility method to construct a simple tree useful for testing
     * 
     * @param dbNode
     * @param includeCatalog
     * @param includeSchema
     */
    public static void initialize( final FakeJdbcDatabase dbNode,
                                   final boolean includeCatalog,
                                   final boolean includeSchema ) {
        try {
            final FakeCapabilities cap = (FakeCapabilities)dbNode.getCapabilities();
            cap.setSupportsCatalogs(includeCatalog);
            cap.setSupportsSchemas(includeSchema);

            final JdbcNodeCache cache = dbNode.getJdbcNodeCache();
            JdbcNodeImpl catOrSchemaOrDbNode = dbNode;
            if (includeCatalog) {
                JdbcCatalogImpl catalog = new JdbcCatalogImpl(catOrSchemaOrDbNode, "PartsSupplier"); //$NON-NLS-1$
                cache.put(catalog);
                catOrSchemaOrDbNode.addChild(catalog);
                catOrSchemaOrDbNode = catalog;
                catalog.getChildren(); // computes children
            }
            if (includeSchema) {
                JdbcSchemaImpl schema = new JdbcSchemaImpl(catOrSchemaOrDbNode, "PartsSupplier"); //$NON-NLS-1$
                cache.put(schema);
                catOrSchemaOrDbNode.addChild(schema);
                catOrSchemaOrDbNode = schema;
                schema.getChildren(); // computes children
            }

            if (!includeSchema && !includeCatalog) {
                // Then have to create these manually (otherwise they are created by the schema & catalog ...
                // Create the table types node for "Tables" ...
                final JdbcTableTypeImpl tablesNode = new JdbcTableTypeImpl(catOrSchemaOrDbNode, "Table"); //$NON-NLS-1$
                catOrSchemaOrDbNode.addChild(tablesNode);
                cache.put(tablesNode);

                // Create the table types node for "Views" ...
                final JdbcTableTypeImpl viewsNode = new JdbcTableTypeImpl(catOrSchemaOrDbNode, "View"); //$NON-NLS-1$
                catOrSchemaOrDbNode.addChild(viewsNode);
                cache.put(viewsNode);
            }

            // Find the tables and views object ...
            final JdbcTableTypeImpl tablesNode = (JdbcTableTypeImpl)cache.get(catOrSchemaOrDbNode.getPath().append("Table")); //$NON-NLS-1$
            final JdbcTableTypeImpl viewsNode = (JdbcTableTypeImpl)cache.get(catOrSchemaOrDbNode.getPath().append("View")); //$NON-NLS-1$
            Assert.assertNotNull(tablesNode);
            Assert.assertNotNull(viewsNode);

            // Create the procedure types node ...
            final JdbcProcedureTypeImpl procsNode = new JdbcProcedureTypeImpl(catOrSchemaOrDbNode, "StoredProcedure"); //$NON-NLS-1$
            catOrSchemaOrDbNode.addChild(procsNode);
            cache.put(procsNode);

            // Create the table objects ...
            final JdbcNode partsTable = new JdbcTableImpl(tablesNode, "PARTS"); //$NON-NLS-1$
            final JdbcNode supplierTable = new JdbcTableImpl(tablesNode, "SUPPLIER"); //$NON-NLS-1$
            final JdbcNode supplierPartsTable = new JdbcTableImpl(tablesNode, "SUPPLIER_PARTS"); //$NON-NLS-1$
            tablesNode.addChild(partsTable);
            tablesNode.addChild(supplierTable);
            tablesNode.addChild(supplierPartsTable);
            cache.put(partsTable);
            cache.put(supplierTable);
            cache.put(supplierPartsTable);

            // Create the view objects
            final JdbcNode customerView = new JdbcTableImpl(viewsNode, "CUSTOMERS_VIEW"); //$NON-NLS-1$
            final JdbcNode ordersView = new JdbcTableImpl(viewsNode, "ORDERS_VIEW"); //$NON-NLS-1$
            viewsNode.addChild(customerView);
            viewsNode.addChild(ordersView);
            cache.put(customerView);
            cache.put(ordersView);

            // Create the procedure objects
            final JdbcNode proc1 = new JdbcProcedureImpl(procsNode, "CreateCustomerProcedure"); //$NON-NLS-1$
            final JdbcNode proc2 = new JdbcProcedureImpl(procsNode, "ChangeCustomerProcedure"); //$NON-NLS-1$
            final JdbcNode proc3 = new JdbcProcedureImpl(procsNode, "DeleteCustomerProcedure"); //$NON-NLS-1$
            procsNode.addChild(proc1);
            procsNode.addChild(proc2);
            procsNode.addChild(proc3);
            cache.put(proc1);
            cache.put(proc2);
            cache.put(proc3);
        } catch (JdbcException e) {
            throw new RuntimeException(e);
        }
    }

    private final static Connection connection = new FakeConnection();
    private final FakeCapabilities capabilities = new FakeCapabilities();
    private final Includes includes = new IncludesImpl(this);
    private final DatabaseMetaData metadata = new FakeDatabaseMetaData();
    private final DatabaseInfo dbInfo = new DatabaseInfoImpl();

    /**
     * Construct an instance of FakeJdbcDatabase.
     * 
     * @param name
     * @param parent
     */
    public FakeJdbcDatabase( String name ) {
        super(connection, name);
    }

    /**
     * Construct an instance of FakeJdbcDatabase.
     * 
     * @param name
     * @param parent
     */
    public FakeJdbcDatabase( String name,
                             JdbcNodeSelections selections ) {
        super(connection, name, selections);
    }

    /**
     * In general, the
     * 
     * @param node
     * @throws JdbcException
     */
    public void addChildNode( final JdbcNode node ) throws JdbcException {
        super.addChild(node);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getConnection()
     */
    @Override
    public Connection getConnection() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getDatabaseMetaData()
     */
    @Override
    public DatabaseMetaData getDatabaseMetaData() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getCapabilities()
     */
    @Override
    public Capabilities getCapabilities() {
        return capabilities;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getDatabaseInfo()
     */
    @Override
    public DatabaseInfo getDatabaseInfo() {
        return dbInfo;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabase#getIncludes()
     */
    @Override
    public Includes getIncludes() {
        return includes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcDatabaseImpl#computeChildren()
     */
    @Override
    protected JdbcNode[] computeChildren() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.impl.JdbcDatabaseImpl#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

}

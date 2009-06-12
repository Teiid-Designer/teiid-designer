/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import junit.framework.Assert;
import com.metamatrix.core.util.Stopwatch;

/**
 * The StandardJdbcDatabaseTest can be used to run a JDBC data source through a standard set of test cases, including:
 * <ul>
 * <li>Navigating to the immediate children of the supplied {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JdbcDatabase}
 * and printing the access statistics to do so</li>
 * <li>For each catalog or schema supplied, find that node, navigate (to infinite depth) below it, and printing the access
 * statistics to do so</li>
 * <li></li>
 * </ul>
 */
public class StandardJdbcDatabaseTest extends JdbcDatabaseTest {

    private PathPrintingVisitor printingVisitor;
    private String[] schemasOrCatalogNames;
    private boolean loadTables = true;

    /**
     * Construct an instance of StandardJdbcDatabaseTest.
     * 
     * @param schemaOrCatalogName the schema or catalog name for which metadata is to be loaded
     * @param runPasswordVarianceTest true if the tests should try other passwords and expect failures, or false if the password
     *        tests should not be run.
     */
    public StandardJdbcDatabaseTest( final String schemaOrCatalogName,
                                     final boolean runPasswordVarianceTest ) {
        this(new String[] {schemaOrCatalogName}, runPasswordVarianceTest);
    }

    /**
     * Construct an instance of StandardJdbcDatabaseTest.
     * 
     * @param schemaOrCatalogNames the schema or catalog names for which metadata is to be loaded
     * @param runPasswordVarianceTest true if the tests should try other passwords and expect failures, or
     */
    public StandardJdbcDatabaseTest( final String[] schemaOrCatalogNames,
                                     final boolean runPasswordVarianceTest ) {
        super();
        this.printingVisitor = new PathPrintingVisitor(System.out, "   "); //$NON-NLS-1$
        this.schemasOrCatalogNames = schemaOrCatalogNames != null ? schemaOrCatalogNames : new String[] {};
        super.setRunPasswordTests(runPasswordVarianceTest);
    }

    protected PathPrintingVisitor getPathPrintingVisitor() {
        return this.printingVisitor;
    }

    public void helpRunVisitor( final JdbcNodeVisitor visitor,
                                final int depth,
                                final JdbcNode node ) throws Exception {
        final String depthStr = (depth == JdbcNode.DEPTH_ZERO ? "ZERO" : (depth == JdbcNode.DEPTH_ONE ? "ONE" : "INFINITE")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("\nVisiting " + node.getPath() + " (depth = " + depthStr + "):"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        try {
            node.accept(visitor, depth);
        } finally {
            if (visitor instanceof PerformanceVisitor) {
                PerformanceVisitor pv = (PerformanceVisitor)visitor;
                pv.stop();
                System.out.println("  Total time visit  = " + pv.getTotalTimeInMillis() + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("  Avg time to visit = " + pv.getAverageTimeInMillis() + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("  Number of nodes   = " + pv.getNumberOfNodes()); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.JdbcDatabaseTest#runTest(com.metamatrix.modeler.jdbc.metadata.JdbcDatabase)
     */
    @Override
    protected void runTest( final JdbcDatabase dbNode ) throws Exception {
        System.out.println("DatabaseNode = " + dbNode); //$NON-NLS-1$
        final PerformanceVisitor visitor = new PerformanceVisitor();
        helpRunVisitor(visitor, JdbcNode.DEPTH_ONE, dbNode);

        // get the database info and the capabilities
        final Capabilities caps = dbNode.getCapabilities();
        Assert.assertNotNull(caps);
        final DatabaseInfo info = dbNode.getDatabaseInfo();
        Assert.assertNotNull(info);

        // For each of the named schemas or catalogs ...
        for (int j = 0; j != schemasOrCatalogNames.length; ++j) {
            // Find the "PartsSupplier" schema node ...
            final JdbcNode schemaNode = dbNode.findChild(schemasOrCatalogNames[j]);
            Assert.assertNotNull(schemaNode);

            // Do this a few times; only the first time should take a little time
            System.out.println("Performing performance analysis for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
            for (int i = 0; i != 2; ++i) {
                // Navigate to the children of the schema node (and below)
                final PerformanceVisitor schemaVisitor = new PerformanceVisitor();
                helpRunVisitor(schemaVisitor, JdbcNode.DEPTH_INFINITE, schemaNode);
            }

            // Navigate to the children of the schema node and below
            // (that is, all table types and objects under those type nodes)
            helpRunVisitor(printingVisitor, JdbcNode.DEPTH_INFINITE, schemaNode);

            if (isLoadTables()) {
                // First time: (no printing but timing, no indexes, no foreign keys)
                // Navigate to the children of the schema node and below
                // (that is, all table types and objects under those type nodes)
                dbNode.getIncludes().setIncludeForeignKeys(false);
                dbNode.getIncludes().setIncludeIndexes(false);
                System.out.println("Loading table columns and PKs for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                final Stopwatch sw = new Stopwatch();
                final TableLoadingVisitor tableLoader = new TableLoadingVisitor(sw, null, null);
                helpRunVisitor(tableLoader, JdbcNode.DEPTH_INFINITE, schemaNode);
                System.out.println("Statistics for loading table columns and PKs for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                sw.printStatistics(System.out);

                // Refresh the schema
                schemaNode.refresh();

                // Second time: (no printing but timing)
                // Navigate to the children of the schema node and below
                // (that is, all table types and objects under those type nodes)
                dbNode.getIncludes().setIncludeForeignKeys(true);
                dbNode.getIncludes().setIncludeIndexes(true);
                System.out.println("Loading table indexes and FKs for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                final Stopwatch sw2 = new Stopwatch();
                final TableLoadingVisitor tableLoader2 = new TableLoadingVisitor(sw2, null, null);
                helpRunVisitor(tableLoader2, JdbcNode.DEPTH_INFINITE, schemaNode);
                System.out.println("Statistics for loading table indexes and FKs for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                sw2.printStatistics(System.out);

                // Third time: (printing and timing)
                // Navigate to the children of the schema node and below
                // (that is, all table types and objects under those type nodes)
                final Stopwatch sw3 = new Stopwatch();
                final TableLoadingVisitor tableVisitor = new TableLoadingVisitor(sw3, System.out, "   "); //$NON-NLS-1$
                helpRunVisitor(tableVisitor, JdbcNode.DEPTH_INFINITE, schemaNode);
                System.out.println("Statistics for re-accessing (and printing) table information for " + schemaNode.getPath() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                sw3.printStatistics(System.out);
            }
        }
    }

    /**
     * @return
     */
    public boolean isLoadTables() {
        return loadTables;
    }

    /**
     * @param b
     */
    public void setLoadTables( boolean b ) {
        loadTables = b;
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import junit.framework.Assert;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcTestManager;

/**
 * The JdbcDatabaseTest is an abstract class used by the
 * {@link com.metamatrix.modeler.jdbc.metadata.TestJdbcDatabaseMetadataOracle} test case, and is intended to be subclassed to
 * provide specific test behavior
 */
public abstract class JdbcDatabaseTest {

    // private String sourceName;
    // private String password;
    private JdbcTestManager jdbcTestManager;
    private Connection conn;
    private JdbcSource source;
    private boolean runPasswordTests = false;

    /**
     * Construct an instance of JdbcDatabaseTest.
     */
    public JdbcDatabaseTest() {
        super();
    }

    public Connection getConnection() {
        return this.conn;
    }

    public JdbcSource getJdbcSource() {
        return this.source;
    }

    public void run( final JdbcTestManager manager,
                     final String sourceName,
                     final String password ) throws Exception {
        // this.sourceName = sourceName;
        // this.password = password;
        this.jdbcTestManager = manager;

        // Find the source and driver for the desired source name
        this.source = jdbcTestManager.getJdbcSource(sourceName);
        Assert.assertNotNull(source);
        final JdbcDriver driver = jdbcTestManager.getJdbcManager().findBestDriver(source);

        if (isRunPasswordTests()) {
            performPasswordTests(jdbcTestManager, source, driver, password);
        }

        try {
            // Create the connection to the database ...
            this.conn = jdbcTestManager.getJdbcManager().createConnection(source, driver, password);
            Assert.assertNotNull(this.conn);

        } catch (JdbcException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            // Create the database node ...
            final JdbcDatabase dbNode = JdbcPlugin.getJdbcDatabase(source, conn);
            Assert.assertNotNull(dbNode);

            // Run the test ...
            runTest(dbNode);

        } finally {
            // Clean up the connection
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                } finally {
                    this.conn = null;
                }
            }
        }
    }

    /**
     * Subclasses should override this method to provide specific test cases for the supplied {@link JdbcDatabase} node.
     * 
     * @param dbNode the database node for the JDBC datasource that is being tested.
     */
    protected abstract void runTest( final JdbcDatabase dbNode ) throws Exception;

    protected void performPasswordTests( final JdbcTestManager manager,
                                         final JdbcSource source,
                                         final JdbcDriver driver,
                                         final String password ) {
        for (int i = 0; i <= 2; ++i) {
            String pw = password;
            String pwMsg = null;
            switch (i) {
                case 0:
                    // Try a null password
                    pw = null;
                    pwMsg = "null password"; //$NON-NLS-1$
                    break;
                case 1:
                    // Try a zero-length password
                    pw = ""; //$NON-NLS-1$
                    pwMsg = "zero-length password"; //$NON-NLS-1$
                    break;
                case 2:
                    // Try an alternative password
                    pw = password + "TestWrongPassword"; //$NON-NLS-1$
                    pwMsg = "password \"" + pw + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                    break;
            }

            try {
                // Create the connection to the database ...
                this.conn = jdbcTestManager.getJdbcManager().createConnection(source, driver, pw);
                // Clean up the connection
                if (this.conn != null) {
                    try {
                        this.conn.close();
                    } catch (SQLException e) {
                    } finally {
                        this.conn = null;
                    }
                }
                Assert.fail("Should not have been able to connect with " + pwMsg + " to " + source); //$NON-NLS-1$//$NON-NLS-2$
            } catch (JdbcException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                // This is expected
            }
        }
    }

    /**
     * @return
     */
    public boolean isRunPasswordTests() {
        return runPasswordTests;
    }

    /**
     * @param b
     */
    public void setRunPasswordTests( boolean b ) {
        runPasswordTests = b;
    }

}

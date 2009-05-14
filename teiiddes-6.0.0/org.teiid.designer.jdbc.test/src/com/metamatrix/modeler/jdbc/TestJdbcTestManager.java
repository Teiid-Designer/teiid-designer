/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * TestJdbcTestManager
 */
public class TestJdbcTestManager extends TestCase {

    //    private static final String URI = UnitTestUtil.Data.getTestDataPath() + "/jdbcModel.xmi"; //$NON-NLS-1$
    private static final String URI = SmartTestSuite.getTestScratchPath() + "/testJdbcUnitTestUtil.xmi"; //$NON-NLS-1$
    private static final String EMPTY_URI = SmartTestSuite.getTestDataPath() + "/emptyJdbcModel.xmi"; //$NON-NLS-1$

    private JdbcTestManager standardUtil;
    private JdbcTestManager scratchUtil;
    private JdbcTestManager emptyFileUtil;

    /**
     * Constructor for TestJdbcTestManager.
     * 
     * @param name
     */
    public TestJdbcTestManager( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.standardUtil = new JdbcTestManager();
        this.scratchUtil = new JdbcTestManager(URI);
        this.emptyFileUtil = new JdbcTestManager(EMPTY_URI);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.standardUtil.shutdown();
        this.standardUtil = null;
        this.scratchUtil = null;
        this.emptyFileUtil = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestJdbcTestManager"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcTestManager.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testStartupAndTearDown() {
        // do nothing; this just tests the startUp and tearDown methods
    }

    public void testGetJdbcManager() {
        final JdbcManager manager = standardUtil.getJdbcManager();
        assertNotNull(manager);

        final JdbcManager manager2 = scratchUtil.getJdbcManager();
        assertNotNull(manager2);
    }

    public void testGetJdbcManager2() {
        // This just does it again after one teardown
        final JdbcManager manager = standardUtil.getJdbcManager();
        assertNotNull(manager);

        final JdbcManager manager2 = scratchUtil.getJdbcManager();
        assertNotNull(manager2);
    }

    public void testGetJdbcDriversFromManagerAndSaving() throws Exception {
        // This just does it again after one teardown
        final JdbcManager manager = scratchUtil.getJdbcManager();
        assertNotNull(manager);
        final List drivers = manager.getJdbcDrivers();
        assertNotNull(drivers);
        manager.saveChanges(null);
    }

    public void testGetJdbcManager3() {
        // This just does it again after previous method
        final JdbcManager manager = scratchUtil.getJdbcManager();
        assertNotNull(manager);
    }

    public void testGetJdbcManagerForEmptyFile() {
        final JdbcManager manager = emptyFileUtil.getJdbcManager();
        assertNotNull(manager);
    }

    public void testGetJdbcSourcesToCreateNewSource() throws Exception {
        final List sources = scratchUtil.getJdbcManager().getJdbcSources();
        final JdbcSource source = scratchUtil.getJdbcManager().getFactory().createJdbcSource();
        source.setName("SourceName"); //$NON-NLS-1$
        source.setDriverClass("driver.class"); //$NON-NLS-1$
        source.setDriverName("driver name"); //$NON-NLS-1$
        source.setUrl("jdbc:oracle"); //$NON-NLS-1$
        sources.add(source);
        scratchUtil.getJdbcManager().saveChanges(null);
    }
}

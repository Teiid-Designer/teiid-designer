/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.jdbctest;

//import java.net.URL;
import java.util.Properties;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.util.SmartTestDesignerSuite;

/**
 * TestJdbcMetadataClient
 */
public class TestJdbcMetadataClient extends TestCase {

    public static final String MM_ORALCE_DRIVER_CLASS_NAME = "com.metamatrix.jdbc.oracle.OracleDriver"; //$NON-NLS-1$
    public static final String MM_SQLSERVER_DRIVER_CLASS_NAME = "com.metamatrix.jdbc.sqlserver.SQLServerDriver"; //$NON-NLS-1$
    public static final String MM_SYBASE_DRIVER_CLASS_NAME = "com.metamatrix.jdbc.sybase.SybaseDriver"; //$NON-NLS-1$
    public static final String MM_INFORMIX_DRIVER_CLASS_NAME = "com.metamatrix.jdbc.informix.InformixDriver"; //$NON-NLS-1$
    public static final String MM_DB2_DRIVER_CLASS_NAME = "com.metamatrix.jdbc.db2.DB2Driver"; //$NON-NLS-1$
    public static final String ORALCE_DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver"; //$NON-NLS-1$

    public static final String PATH = "file:/" + SmartTestDesignerSuite.getTestDataPath() + "/drivers/"; //$NON-NLS-1$ //$NON-NLS-2$

    private JdbcMetadataClient client;

    // private ClassLoader driverClassLoaderOracle;
    // private ClassLoader driverClassLoaderOracleOracle;
    // private ClassLoader driverClassLoaderSqlServer;
    // private ClassLoader driverClassLoaderSybase;
    // private ClassLoader driverClassLoaderInformix;
    // private ClassLoader driverClassLoaderDb2;
    // private ClassLoader driverClassLoaderAll;

    /**
     * Constructor for TestJdbcMetadataClient.
     * 
     * @param name
     */
    public TestJdbcMetadataClient( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        client = new JdbcMetadataClient();

        JdbcMetadataClient.DEBUG = true;

        //        final URL oracleOracleUrl = new URL(PATH+"oracle9iR2-9.2.0.3_classes12.zip"); //$NON-NLS-1$
        //        final URL baseUrl       = new URL(PATH+"MJbase.jar"); //$NON-NLS-1$
        //        final URL utilUrl       = new URL(PATH+"MJutil.jar"); //$NON-NLS-1$
        //        final URL passkeyUrl    = new URL(PATH+"MJpasskey.jar"); //$NON-NLS-1$
        //        final URL db2Url        = new URL(PATH+"MJdb2.jar"); //$NON-NLS-1$
        //        final URL oracleUrl     = new URL(PATH+"MJoracle.jar"); //$NON-NLS-1$
        //        final URL sqlserverUrl  = new URL(PATH+"MJsqlserver.jar"); //$NON-NLS-1$
        //        final URL sybaseUrl     = new URL(PATH+"MJsybase.jar"); //$NON-NLS-1$
        //        final URL informixUrl   = new URL(PATH+"MJinformix.jar"); //$NON-NLS-1$

        // final URL[] urlsOracleOracle= new URL[]{oracleOracleUrl};
        // final URL[] urlsOracle = new URL[]{baseUrl,utilUrl,oracleUrl,passkeyUrl};
        // final URL[] urlsDb2 = new URL[]{baseUrl,utilUrl,db2Url,passkeyUrl};
        // final URL[] urlsSqlServer = new URL[]{baseUrl,utilUrl,sqlserverUrl,passkeyUrl};
        // final URL[] urlsSybase = new URL[]{baseUrl,utilUrl,sybaseUrl,passkeyUrl};
        // final URL[] urlsInformix = new URL[]{baseUrl,utilUrl,informixUrl,passkeyUrl};
        // final URL[] urlsAll = new URL[]{baseUrl,utilUrl,oracleUrl,db2Url,sqlserverUrl,sybaseUrl,informixUrl,passkeyUrl};

        // this.driverClassLoaderOracleOracle =
        // new URLClassLoader(urlsOracleOracle);
        // this.driverClassLoaderOracle =
        // new URLClassLoader(urlsOracle);
        // this.driverClassLoaderSqlServer =
        // new URLClassLoader(urlsSqlServer);
        // this.driverClassLoaderSybase =
        // new URLClassLoader(urlsSybase);
        // this.driverClassLoaderInformix =
        // new URLClassLoader(urlsInformix);
        // this.driverClassLoaderDb2 =
        // new URLClassLoader(urlsDb2);
        // this.driverClassLoaderAll =
        // new URLClassLoader(urlsAll);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestJdbcMetadataClient"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcMetadataClient.class);
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

    public void helpTest( final String driver,
                          final String url,
                          final String username,
                          final String password,
                          final Properties additionalProps,
                          final ClassLoader loader ) throws Exception {
        JdbcMetadataClient.execute(driver, url, username, password, additionalProps, loader, null, false);
    }

    public void helpTest( final String[] args,
                          final ClassLoader loader ) {
        JdbcMetadataClient.main(args);
    }

    public void testSetup() {
        assertNotNull(client);
    }

    public void testMainWithNoArgs() {
        final ClassLoader loader = null;
        final String[] args = new String[] {};
        helpTest(args, loader);
    }

}

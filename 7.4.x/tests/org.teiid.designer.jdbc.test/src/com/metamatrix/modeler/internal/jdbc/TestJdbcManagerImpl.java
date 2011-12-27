/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc;

import java.io.File;
import java.io.PrintStream;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.ProfileManager;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcSourceProperty;
import com.metamatrix.modeler.jdbc.impl.JdbcFactoryImpl;

/**
 * TestJdbcManagerImpl
 */
public class TestJdbcManagerImpl extends TestCase {

    private static final int VALID_CLASS_NAME = 10000;

    private static final String[] CLASSES2 = new String[] {"com.metamatrix.jdbc.oracle.OracleDriver", //$NON-NLS-1$
        "com.metamatrix.jdbc.oracle.OracleConnection", //$NON-NLS-1$
        "com.metamatrix.jdbcx.oracle.OracleDataSource", //$NON-NLS-1$
        "com.metamatrix.jdbcx.oracle.OracleDataSourceFactory", //$NON-NLS-1$
    };

    private static final String[] CLASSES3 = new String[] {"sun.jdbc.odbc.JdbcOdbcDriver" //$NON-NLS-1$
    };

    private static final String[] SOURCE_PROPERTY_NAMES = new String[] {
        "databaseName", "serverName", "portNumber", "user", "password", "includeSynonyms"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    private static final String[] SOURCE_PROPERTY_VALUES = new String[] {
        "db08", "slntdb08", "1521", "apollo_rep_test", "mm", "false"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    public static final String ORACLE_DB_URL = "jdbc:oracle:thin:@slntdb08:1521:db08"; //$NON-NLS-1$
    public static final String ORACLE_DB_USERNAME = "apollo_rep_test"; //$NON-NLS-1$
    public static final String ORACLE_DB_PASSWORD = "mm"; //$NON-NLS-1$

    private String[] JAR_FILES2;
    private String[] JAR_FILES_THAT_DONT_EXIST;

    private JdbcManagerImpl mgr;
    private JdbcSource sampleSource;
    private JdbcDriver sampleDriver;

    private JdbcSource matchSource;

    private JdbcDriver nameMatchDriver;
    private JdbcDriver availableClassMatchDriver;
    private JdbcDriver preferredClassMatchDriver;
    private JdbcDriver noMatchDriver;
    private JdbcDriver exactMatchDriver;

    private JdbcDriver metamatrixOracleDriver;
    private JdbcDriver excelDriver;

    private JdbcSource excelSource;

    private final JdbcFactory factory = new JdbcFactoryImpl();

    /**
     * This class overrides the {@link JdbcManagerImpl#getJdbcDrivers()} method (which requires a Resource to be used), and adds
     * the drivers in this test to the returned list. This is used for the 'find*' method test cases.
     */
    protected class JdbcDriverManagerForTesting extends JdbcManagerImpl {
        private final List drivers;

        public JdbcDriverManagerForTesting() {
            super("Testing Manager"); //$NON-NLS-1$
            this.drivers = new ArrayList();
        }

        @Override
        public List getJdbcDrivers() {
            return this.drivers;
        }
        
        @Override
        public void shutdown() {
        	
        }
        
        @Override
        public void start() {
        	setProfileManager(ProfileManager.getInstance());
        }
    }

    /**
     * Constructor for TestJdbcManagerImpl.
     * 
     * @param name
     */
    public TestJdbcManagerImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String jar_path = SmartTestDesignerSuite.getProjectPath("com.metamatrix.datadirect");//$NON-NLS-1$

        JAR_FILES2 = new String[] {(new File(jar_path + "/MJbase.jar")).toURI().toURL().toString(), //$NON-NLS-1$
            (new File(jar_path + "/MJutil.jar")).toURI().toURL().toString(), //$NON-NLS-1$
            (new File(jar_path + "/MJoracle.jar")).toURI().toURL().toString() //$NON-NLS-1$
        };
        JAR_FILES_THAT_DONT_EXIST = new String[] {
            SmartTestDesignerSuite.getTestDataFile("/drivers").toURI().toURL().toString() + "MJbase.ZIP", //$NON-NLS-1$ //$NON-NLS-2$
            SmartTestDesignerSuite.getTestDataFile("/drivers").toURI().toURL().toString() + "MJutil.ZIP", //$NON-NLS-1$ //$NON-NLS-2$
            SmartTestDesignerSuite.getTestDataFile("/drivers").toURI().toURL().toString() + "MJoracle.ZIP" //$NON-NLS-1$ //$NON-NLS-2$
        };

        mgr = new JdbcDriverManagerForTesting();
        // mgr.start();
        sampleSource = factory.createJdbcSource();
        sampleDriver = factory.createJdbcDriver();

        // Create the source for the find method tests
        final String matchDriverName = "My Favorite Driver"; //$NON-NLS-1$
        final String matchDriverClass = CLASSES2[0];
        matchSource = factory.createJdbcSource();
        matchSource.setName("My Database"); //$NON-NLS-1$
        matchSource.setDriverName(matchDriverName);
        matchSource.setDriverClass(matchDriverClass);

        // Create a driver that the matchSource will only match by name
        nameMatchDriver = factory.createJdbcDriver();
        nameMatchDriver.setName(matchDriverName);
        nameMatchDriver.setPreferredDriverClassName(CLASSES3[0]);
        nameMatchDriver.getAvailableDriverClassNames().add(CLASSES3[0]);

        // Create a driver that the matchSource will only match by available driver
        availableClassMatchDriver = factory.createJdbcDriver();
        availableClassMatchDriver.setName(matchDriverName + " (no match)"); //$NON-NLS-1$
        availableClassMatchDriver.setPreferredDriverClassName(CLASSES2[2]);
        availableClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[0]);
        availableClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[1]);
        availableClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[2]);
        availableClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[3]);

        // Create a driver that the matchSource will only match by available driver
        preferredClassMatchDriver = factory.createJdbcDriver();
        preferredClassMatchDriver.setName(matchDriverName + " (no match)"); //$NON-NLS-1$
        preferredClassMatchDriver.setPreferredDriverClassName(matchDriverClass);
        preferredClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[0]);
        preferredClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[1]);
        preferredClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[2]);
        preferredClassMatchDriver.getAvailableDriverClassNames().add(CLASSES2[3]);

        // Create a driver that the matchSource will not match
        noMatchDriver = factory.createJdbcDriver();
        noMatchDriver.setName(matchDriverName + " (no match)"); //$NON-NLS-1$
        noMatchDriver.setPreferredDriverClassName(CLASSES3[0]);
        noMatchDriver.getAvailableDriverClassNames().add(CLASSES3[0]);

        // Create a driver that the matchSource will only match by available driver
        exactMatchDriver = factory.createJdbcDriver();
        exactMatchDriver.setName(matchDriverName);
        exactMatchDriver.setPreferredDriverClassName(matchDriverClass);
        exactMatchDriver.getAvailableDriverClassNames().add(CLASSES2[0]);
        exactMatchDriver.getAvailableDriverClassNames().add(CLASSES2[1]);
        exactMatchDriver.getAvailableDriverClassNames().add(CLASSES2[2]);
        exactMatchDriver.getAvailableDriverClassNames().add(CLASSES2[3]);
        exactMatchDriver.getJarFileUris().add(JAR_FILES2[0]);
        exactMatchDriver.getJarFileUris().add(JAR_FILES2[1]);
        exactMatchDriver.getJarFileUris().add(JAR_FILES2[2]);

        mgr.getJdbcDrivers().add(nameMatchDriver);
        mgr.getJdbcDrivers().add(availableClassMatchDriver);
        mgr.getJdbcDrivers().add(preferredClassMatchDriver);
        mgr.getJdbcDrivers().add(noMatchDriver);
        mgr.getJdbcDrivers().add(exactMatchDriver);

        metamatrixOracleDriver = factory.createJdbcDriver();
        metamatrixOracleDriver.setName("MetaMatrix JDBC for Oracle"); //$NON-NLS-1$
        metamatrixOracleDriver.getAvailableDriverClassNames().add(CLASSES2[0]);
        metamatrixOracleDriver.getAvailableDriverClassNames().add(CLASSES2[2]);
        metamatrixOracleDriver.setPreferredDriverClassName(CLASSES2[0]);
        metamatrixOracleDriver.getJarFileUris().add(JAR_FILES2[0]);
        metamatrixOracleDriver.getJarFileUris().add(JAR_FILES2[1]);
        metamatrixOracleDriver.getJarFileUris().add(JAR_FILES2[2]);

        excelDriver = factory.createJdbcDriver();
        excelDriver.setName("Microsoft Excel"); //$NON-NLS-1$
        excelDriver.getAvailableDriverClassNames().add(CLASSES3[0]);
        excelDriver.setPreferredDriverClassName(CLASSES3[0]);
        // excelDriver.getJarFileUris().add(JAR_FILES1[0]);
        mgr.getJdbcDrivers().add(excelDriver);
        excelSource = factory.createJdbcSource();
        excelSource.setName("Book1 (Excel)"); //$NON-NLS-1$
        excelSource.setDriverClass(CLASSES3[0]);
        excelSource.setDriverName("Microsoft Excel"); //$NON-NLS-1$
        excelSource.setUrl("jdbc:odbc:Driver={MicroSoft Excel Driver (*.xls)};dBQ=testdata/Book1.xls"); //$NON-NLS-1$
        
        mgr.start();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mgr.shutdown();
        mgr = null;
        sampleDriver = null;
        sampleSource = null;

        matchSource = null;
        nameMatchDriver = null;
        availableClassMatchDriver = null;
        preferredClassMatchDriver = null;
        noMatchDriver = null;
        exactMatchDriver = null;

        metamatrixOracleDriver = null;

        excelSource = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestDesignerSuite("com.metamatrix.modeler.jdbc", "TestJdbcManagerImpl"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestJdbcManagerImpl.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                JdbcPlugin.DEBUG = true;
            }

            @Override
            public void tearDown() {
            }
        };
    }

    public JdbcSourceProperty helpCreateProperty( final JdbcSource source,
                                                  final String name,
                                                  final String value ) {
        final JdbcSourceProperty prop = factory.createJdbcSourceProperty();
        prop.setName(name);
        prop.setValue(value);
        source.getProperties().add(prop);
        return prop;
    }

    public JdbcSource helpCreateValidSource() {
        final JdbcSource result = factory.createJdbcSource();
        result.setName("This is a Valid Name With Trailing Spaces   "); //$NON-NLS-1$
        result.setDriverClass(CLASSES2[0]);
        // Set the URL to something valid
        result.setUrl("jdbc:oracle:thin:@slntds01:1521:ds01a"); //$NON-NLS-1$
        result.setDriverName(exactMatchDriver.getName());
        // Create the properties ...
        for (int i = 0; i < SOURCE_PROPERTY_NAMES.length; i++) {
            helpCreateProperty(result, SOURCE_PROPERTY_NAMES[i], SOURCE_PROPERTY_VALUES[i]);
        }
        helpIsValidJdbcSource(result, JdbcManagerImpl.VALID_SOURCE, IStatus.OK);
        return result;
    }

    public JdbcDriver helpCreateValidDriver() {
        final JdbcDriver result = factory.createJdbcDriver();
        result.setName("This is a Valid_ Name"); //$NON-NLS-1$
        result.getAvailableDriverClassNames().add(CLASSES2[0]);
        result.getAvailableDriverClassNames().add(CLASSES2[2]);
        result.setPreferredDriverClassName(CLASSES2[0]);
        result.getJarFileUris().add(JAR_FILES2[0]);
        result.getJarFileUris().add(JAR_FILES2[1]);
        result.getJarFileUris().add(JAR_FILES2[2]);
        helpIsValidJdbcDriver(result, JdbcManagerImpl.VALID_DRIVER, IStatus.OK);
        return result;
    }

    public void helpCheckClassNameForError( final int expectedCode,
                                            final int expectedSeverity,
                                            final String className ) {
        final IStatus status = mgr.checkClassNameForError(className);
        if (expectedCode != VALID_CLASS_NAME) {
            assertEquals(expectedCode, status.getCode());
            assertEquals(expectedSeverity, status.getSeverity());
        }
    }

    public void helpIsValidJdbcSource( final JdbcSource source,
                                       final int expectedCode,
                                       final int expectedSeverity ) {
        final IStatus status = mgr.isValid(source);
        assertNotNull(status);
        assertEquals(expectedCode, status.getCode());
        assertEquals(expectedSeverity, status.getSeverity());
    }

    public void helpIsValidJdbcDriver( final JdbcDriver driver,
                                       final int expectedCode,
                                       final int expectedSeverity ) {
        final IStatus status = mgr.isValid(driver);
        assertNotNull(status);
        assertEquals(expectedCode, status.getCode());
        assertEquals(expectedSeverity, status.getSeverity());
    }

    public void helpTestClassLoader( final ClassLoader loader,
                                     final String[] classNames,
                                     final boolean shouldSucceed ) {
        if (!shouldSucceed) {
            fail("Should not have been able to create class loader"); //$NON-NLS-1$
        }

        for (int i = 0; i < classNames.length; i++) {
            final String className = classNames[i];
            assertNotNull(className);
            try {
                final Class c = Class.forName(className, true, loader);
                assertNotNull(c);
            } catch (ClassNotFoundException e1) {
                fail("Unable to find class " + className + "\nin class loader over " + //$NON-NLS-1$ //$NON-NLS-2$
                     CoreStringUtil.toString(((URLClassLoader)loader).getURLs()));
            }
        }
    }

    public void printJdbcDriver( final JdbcDriver driver,
                                 final PrintStream stream ) {
        stream.println("JdbcDriver: "); //$NON-NLS-1$
        stream.println("  name = " + driver.getName()); //$NON-NLS-1$
        stream.println("  JAR files:"); //$NON-NLS-1$
        final Iterator iter = driver.getJarFileUris().iterator();
        while (iter.hasNext()) {
            stream.println("    - " + iter.next()); //$NON-NLS-1$
        }
        stream.println("  available driver classes:"); //$NON-NLS-1$
        final Iterator iter2 = driver.getAvailableDriverClassNames().iterator();
        while (iter2.hasNext()) {
            stream.println("    - " + iter2.next()); //$NON-NLS-1$
        }
        stream.println("  preferred driver class:"); //$NON-NLS-1$
        stream.println("    - " + driver.getPreferredDriverClassName()); //$NON-NLS-1$
    }

    public void helpTestConnection( final JdbcSource source,
                                    final String passwd,
                                    final boolean shouldConnect ) throws Exception {
        Connection conn = null;
        try {
            conn = mgr.createConnection(source, passwd);
            if (!shouldConnect) {
                if (conn == null) {
                    fail("Should not have been able to connect; got null Connection and no exception!"); //$NON-NLS-1$
                }
                fail("Should not have been able to connect"); //$NON-NLS-1$
            }
            assertNotNull(conn); // if no exception and should have connected, then should not be null
        } catch (Exception e) {
            if (shouldConnect) {
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void testConstructorWithZeroLengthNameArg() {
        try {
            new JdbcManagerImpl(""); //$NON-NLS-1$
            fail("Failed to catch zero-length name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testConstructorWithNullNameArg() {
        try {
            new JdbcManagerImpl(null);
            fail("Failed to catch null name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testConstructorWithNullArgs() {
        try {
            new JdbcManagerImpl(null);
            fail("Failed to catch null parameters"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testGetFactory() {
        final JdbcFactory factory = mgr.getFactory();
        assertNotNull(factory);
    }

    public void testCheckClassNameForErrorWithValidClass1() {
        helpCheckClassNameForError(VALID_CLASS_NAME, IStatus.ERROR, String.class.getName());
    }

    public void testCheckClassNameForErrorWithValidClass2() {
        helpCheckClassNameForError(VALID_CLASS_NAME, IStatus.ERROR, Map.Entry.class.getName());
    }

    public void testCheckClassNameForErrorWithValidClass3() {
        helpCheckClassNameForError(VALID_CLASS_NAME, IStatus.ERROR, "java.x.y.this_is_valid_class"); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsFirstChar() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_AT_START_OF_CLASS_NAME,
                                   IStatus.ERROR,
                                   " " + String.class.getName()); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsNonFirstChar1() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR, "java. x.y.z"); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsNonFirstChar2() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR, "java.*x.y.z"); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsNonFirstChar3() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR, "java.%x.y.z"); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsNonFirstChar4() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR, "java.#x.y.z"); //$NON-NLS-1$
    }

    public void testCheckClassNameForErrorWithSpaceAsNonFirstChar5() {
        helpCheckClassNameForError(JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR, "java.x.y.z "); //$NON-NLS-1$
    }

    public void testIsValidJdbcSourceWithNullName() {
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithEmptyName() {
        sampleSource.setName(""); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithNameStartingWithNonLetterOrNumber() {
        sampleSource.setName("*Not Valid Name since begins with a non-letter or number"); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.NAME_MUST_BEGIN_WITH_LETTER_OR_NUMBER, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithMissingDriverClass() {
        sampleSource.setName("This is a Valid Name"); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.MISSING_DRIVER_CLASS, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithInvalidDriverClass1() {
        sampleSource.setName("This is a Valid Name"); //$NON-NLS-1$
        sampleSource.setDriverClass(null);
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.MISSING_DRIVER_CLASS, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithInvalidDriverClass2() {
        sampleSource.setName("This is a Valid Name"); //$NON-NLS-1$
        sampleSource.setDriverClass(""); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.MISSING_DRIVER_CLASS, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithInvalidDriverClass3() {
        sampleSource.setName("This is a Valid Name"); //$NON-NLS-1$
        sampleSource.setDriverClass("*not. valid class file"); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.ILLEGAL_CHAR_AT_START_OF_CLASS_NAME, IStatus.ERROR);
    }

    public void testIsValidJdbcSourceWithInvalidDriverClass4() {
        sampleSource.setName("This is a Valid Name"); //$NON-NLS-1$
        sampleSource.setDriverClass("not.valid class file"); //$NON-NLS-1$
        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR);
    }

    // NOT sure how we test this. Have to create a connection profile using DTP's ProfileManager
    // would require a bit of set-up. Maybe using Mockito/Powermock would help
    // TODO: fix this
//    public void testIsValidJdbcSource1() {
//        sampleSource.setName("This is a Valid_ Name"); //$NON-NLS-1$
//        sampleSource.setDriverClass(String.class.getName());
//        helpIsValidJdbcSource(sampleSource, JdbcManagerImpl.UNABLE_TO_FIND_DRIVER, IStatus.WARNING);
//    }

    public void testIsValidJdbcDriverWithNullName() {
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithEmptyName() {
        sampleDriver.setName(""); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameStartingWithNonLetterOrNumber() {
        sampleDriver.setName("*Not Valid Name since begins with a non-letter or number"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_MUST_BEGIN_WITH_LETTER_OR_NUMBER, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameNoAvailableClasses1() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NO_AVAILABLE_DRIVER_CLASS_NAMES, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameNoAvailableClasses2() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(""); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(""); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameOneAvailableClassAndOneNullClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add(null);
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameOneAvailableClassAndOneZeroLengthClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add(""); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameOneAvailableClassAndOneClassWithAllSpaces() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("   "); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndDefaultPreferredClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.PREFERRED_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndNullPreferredClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName(null);
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.PREFERRED_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndZeroLengthPreferredClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName(""); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.PREFERRED_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndAllSpacePreferredClass() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("   "); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.PREFERRED_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassWithInvalidStarting() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("*invalid.java.class. name"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.ILLEGAL_CHAR_AT_START_OF_CLASS_NAME, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassWithInvalidChar() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("invalid.java.class. name"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.ILLEGAL_CHAR_IN_CLASS_NAME, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassNotInAvailable() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("valid.class.name.not.in.available"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.PREFERRED_NOT_IN_AVAILABLE, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassAndNoJars() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.NO_JARS_SPECIFIED, IStatus.WARNING);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassAndJarsWithNullUrl() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.getJarFileUris().add(null);
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.JAR_FILE_URI_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassAndJarsWithZeroLengthUrl() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.getJarFileUris().add(""); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.JAR_FILE_URI_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassAndNonExistantJarUrl() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.getJarFileUris().add(JAR_FILES_THAT_DONT_EXIST[0]);
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.JAR_FILE_DOESNT_EXIST, IStatus.ERROR);
    }

    public void testIsValidJdbcDriverWithNameAndTwoAvailableClassAndPreferredClassAndMalformedJarUrl() {
        sampleDriver.setName("This is a Valid_ Name"); //$NON-NLS-1$
        sampleDriver.getAvailableDriverClassNames().add(String.class.getName());
        sampleDriver.getAvailableDriverClassNames().add("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.setPreferredDriverClassName("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        sampleDriver.getJarFileUris().add("x++++xxx" + JAR_FILES_THAT_DONT_EXIST[0]); //$NON-NLS-1$
        helpIsValidJdbcDriver(sampleDriver, JdbcManagerImpl.JAR_FILE_DOESNT_EXIST, IStatus.ERROR);
    }

    public void testWizardUseCase() {
        final JdbcDriver driver = mgr.getFactory().createJdbcDriver();
        helpIsValidJdbcDriver(driver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);

        // Don't set the name, but add an available class ...
        driver.getAvailableDriverClassNames().add(CLASSES2[0]);
        helpIsValidJdbcDriver(driver, JdbcManagerImpl.NAME_NOT_SPECIFIED, IStatus.ERROR);

        // Set the name ...
        driver.setName("This is a valid name"); //$NON-NLS-1$
        helpIsValidJdbcDriver(driver, JdbcManagerImpl.PREFERRED_NOT_SPECIFIED, IStatus.ERROR);
    }

    public void testExcelConnectingWithBadUrl() throws Exception {
        excelSource.setUrl("jdbc:odbc:Driver={MicroSoft Excel Driver (*.xls)}");//$NON-NLS-1$
        helpTestConnection(excelSource, null, false);
    }
}

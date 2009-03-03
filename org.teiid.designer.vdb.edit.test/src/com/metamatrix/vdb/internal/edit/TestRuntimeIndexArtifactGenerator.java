/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom.Document;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.vdb.edit.VdbArtifactGenerator;
import com.metamatrix.vdb.edit.VdbGenerationContext;

/**
 * @since 5.0
 */
public class TestRuntimeIndexArtifactGenerator extends TestCase {

    private static final String NEW_VDB_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "newVdb.vdb"; //$NON-NLS-1$
    private static final File VDB_WORKING_FOLDER = new File(SmartTestSuite.getTestDataPath() + File.separator
                                                            + "vdbWorkingFolder"); //$NON-NLS-1$

    private static final File[] TEST_MODELS = new File[] {
        SmartTestSuite.getTestDataFile("projects/Books Project/Books_Oracle.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/Books_SQLServer.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/Books.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/BookDatatypes.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/BooksWebService_VDB.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartsSupplier_SQLServer.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartSupplier_Oracle.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartsSupplier_VDB.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/BQT/BQT_SQLServer_Output.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/BQT/TestBQT.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Northwind/Northwind.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/System/SystemPhysical.xmi")}; //$NON-NLS-1$

    /**
     * Constructor for TestRuntimeIndexArtifactGenerator.
     * 
     * @param name
     */
    public TestRuntimeIndexArtifactGenerator( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestRuntimeIndexArtifactGenerator"); //$NON-NLS-1$
        //        suite.addTest(new TestRuntimeIndexArtifactGenerator("testDecodeSchemaDirectivePathsForJxdmXsd")); //$NON-NLS-1$
        suite.addTestSuite(TestRuntimeIndexArtifactGenerator.class);

        return new TestSetup(suite) { // junit.extensions package

            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public static void oneTimeSetUp() {
        if (!VDB_WORKING_FOLDER.exists()) {
            VDB_WORKING_FOLDER.mkdir();
        }
    }

    public static void oneTimeTearDown() {
        FileUtils.removeDirectoryAndChildren(VDB_WORKING_FOLDER);
        System.gc();
        Thread.yield();
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        File f = new File(NEW_VDB_FILE_PATH);
        if (f.exists()) {
            f.delete();
        }
        FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
    }

    public File helpFindTestModelByName( final String name ) {
        for (int i = 0; i != TEST_MODELS.length; ++i) {
            File f = TEST_MODELS[i];
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public void testCreate() {
        new RuntimeIndexArtifactGenerator();
    }

    public void testExecuteWithNullVdbGenerationContext() {
        try {
            VdbArtifactGenerator generator = new RuntimeIndexArtifactGenerator();
            generator.execute(null);
            fail("Expected failure but got success"); //$NON-NLS-1$
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testExecuteWithNoOpVdbGenerationContext() {
        try {
            VdbArtifactGenerator generator = new RuntimeIndexArtifactGenerator();
            generator.execute(new NoOpVdbGenerationContext());
        } catch (IllegalArgumentException expected) {
        }
    }

    class NoOpVdbGenerationContext implements VdbGenerationContext {
        public Resource[] getModels() {
            return null;
        }

        public Resource[] getModels( String primaryMetamodelUri ) {
            return null;
        }

        public ModelHelper getModelHelper() {
            return null;
        }

        public ModelObjectHelper getObjectHelper() {
            return null;
        }

        public void addErrorMessage( String message,
                                     int code,
                                     Throwable t ) {
        }

        public void addWarningMessage( String message,
                                       int code ) {
        }

        public void addInfoMessage( String message,
                                    int code ) {
        }

        public void setProgressMessage( String displayableMessage ) {
        }

        public String getProgressMessage() {
            return null;
        }

        public boolean addGeneratedArtifact( String pathInVdb,
                                             String content ) {
            return false;
        }

        public boolean addGeneratedArtifact( String pathInVdb,
                                             Document xmlContent ) {
            return false;
        }

        public boolean addGeneratedArtifact( String pathInVdb,
                                             InputStream content ) {
            return false;
        }

        public boolean addGeneratedArtifact( String pathInVdb,
                                             File content ) {
            return false;
        }

        public Map getGeneratedArtifactsByPath() {
            return null;
        }

        public File getTemporaryDirectory() {
            return null;
        }

        public List getProblems() {
            return null;
        }
    }

}

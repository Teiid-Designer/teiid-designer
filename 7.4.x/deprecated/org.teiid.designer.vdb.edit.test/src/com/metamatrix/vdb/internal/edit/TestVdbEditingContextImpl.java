/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.ObjectConverterUtil;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.internal.core.index.RuntimeIndexSelector;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * TestVdbEditingContextImpl2
 */
public class TestVdbEditingContextImpl extends TestCase {

    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final String PATH_SEPARATOR = File.separator;
    static final String TEST_VDB_PATH1 = SmartTestSuite.getGlobalTestDataPath() + PATH_SEPARATOR + "global" + //$NON-NLS-1$
                                         PATH_SEPARATOR + "Books" + //$NON-NLS-1$
                                         PATH_SEPARATOR + "Books.vdb"; //$NON-NLS-1$
    private static final String MATERIALIZED_VDB_PATH = SmartTestSuite.getGlobalTestDataPath() + PATH_SEPARATOR + "global" + //$NON-NLS-1$
                                                        PATH_SEPARATOR + "matview" + //$NON-NLS-1$
                                                        PATH_SEPARATOR + "MetViewTestVDB.vdb"; //$NON-NLS-1$
    private static final String WSDL_VDB_PATH = SmartTestSuite.getGlobalTestDataPath() + PATH_SEPARATOR + "global" + //$NON-NLS-1$
                                                PATH_SEPARATOR + "wsdl" + //$NON-NLS-1$
                                                PATH_SEPARATOR + "BooksWsdl.vdb"; //$NON-NLS-1$

    private static final File USER_FILE1 = SmartTestSuite.getTestDataFile("userFile1.txt"); //$NON-NLS-1$
    private static final File USER_FILE2 = SmartTestSuite.getTestDataFile("userFile2.txt"); //$NON-NLS-1$

    /**
     * Constructor for PdeTestVdbEditingContextImpl.
     * 
     * @param name
     */
    public TestVdbEditingContextImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        TestSuite suite = new TestSuite("TestVdbEditingContextImpl"); //$NON-NLS-1$
        //suite.addTest(new TestVdbEditingContextImpl("testCreateAndOpenSyncronizationInMetadataCache")); //$NON-NLS-1$
        //suite.addTest(new TestVdbEditingContextImpl("testCreateAndOpenSyncronization")); //$NON-NLS-1$
        //suite.addTest(new TestVdbEditingContextImpl("testOpenSyncronization")); //$NON-NLS-1$

        suite.addTestSuite(TestVdbEditingContextImpl.class);
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

    public void helpEnsureOk( final IStatus status ) throws Throwable {
        if (!status.isOK()) {
            if (status.getException() != null) {
                throw status.getException();
            }
            fail(status.getMessage());
        }
    }

    public void helpCreateAndOpenEditorRunnables( final int numThreads,
                                                  final String vdbFilePath ) {
        final List threads = new ArrayList(numThreads);
        for (int i = 0; i < numThreads; i++) {
            System.out.println("CreateAndOpenEditor " + (i + 1)); //$NON-NLS-1$
            Thread runnable = helpCreateAndOpenEditor(vdbFilePath);
            runnable.start();
            threads.add(runnable);
        }
        while (!threads.isEmpty()) {
            for (final Iterator iter = threads.iterator(); iter.hasNext();) {
                final Thread runnable = (Thread)iter.next();
                if (!runnable.isAlive()) {
                    iter.remove();
                    System.out.println("Thread is dead"); //$NON-NLS-1$
                }
            }
        }
    }

    public Thread helpCreateAndOpenEditor( final String vdbFilePath ) {
        return new Thread(new Runnable() {
            public void run() {
                VdbEditingContext editor = null;
                try {
                    try {
                        System.out.println("  helpCreateAndOpenEditor.run()"); //$NON-NLS-1$
                        editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));
                        editor.open();
                    } finally {
                        if (editor != null) {
                            editor.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void helpCreateAndOpenEditorRunnables2( final int numThreads,
                                                   final String vdbFilePath ) throws Exception {

        final File vdbFile = new File(vdbFilePath);
        final byte[] vdbContents = ObjectConverterUtil.convertFileToByteArray(vdbFile);

        final List threads = new ArrayList(numThreads);
        for (int i = 0; i < numThreads; i++) {
            System.out.println("CreateAndOpenEditor " + (i + 1)); //$NON-NLS-1$
            Thread runnable = helpCreateAndOpenEditor(vdbFile.getName(), vdbContents);
            runnable.start();
            threads.add(runnable);
        }
        while (!threads.isEmpty()) {
            for (final Iterator iter = threads.iterator(); iter.hasNext();) {
                final Thread runnable = (Thread)iter.next();
                if (!runnable.isAlive()) {
                    iter.remove();
                    System.out.println("Thread is dead"); //$NON-NLS-1$
                }
            }
        }
    }

    public Thread helpCreateAndOpenEditor( final String vdbFileName,
                                           final byte[] vdbContents ) {
        // The logic below duplicates the server's logic in MetadataCache.loadModelsUsingVDBContext(...)
        return new Thread(new Runnable() {
            public void run() {
                VdbEditingContextImpl editor = null;
                TempDirectory tempDirectory = helpGetTempDirectory();
                File vdbFile = null;
                tempDirectory.create();

                vdbFile = new File(tempDirectory.getPath(), vdbFileName);
                try {
                    FileUtils.write(vdbContents, vdbFile);
                    editor = new VdbEditingContextImpl(new Path(vdbFile.getAbsolutePath()));
                    editor.open();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public TempDirectory helpGetTempDirectory() {
        final String absolutePath = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        TempDirectory tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
        while (new File(tempDirectory.getPath()).exists()) {
            try {
                Thread.sleep(10);
            } catch (final InterruptedException ignored) {
            }
            tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
        }
        tempDirectory.create();
        return tempDirectory;
    }

    public void helpOpenEditorRunnables( final int numThreads,
                                         final VdbEditingContext editor ) throws Exception {
        final List threads = new ArrayList(numThreads);
        for (int i = 0; i < numThreads; i++) {
            System.out.println("OpenEditor " + (i + 1)); //$NON-NLS-1$
            Thread runnable = helpOpenEditor(editor);
            runnable.start();
            threads.add(runnable);
        }
        while (!threads.isEmpty()) {
            for (final Iterator iter = threads.iterator(); iter.hasNext();) {
                final Thread runnable = (Thread)iter.next();
                if (!runnable.isAlive()) {
                    iter.remove();
                    System.out.println("Thread is dead"); //$NON-NLS-1$
                }
            }
        }
    }

    public Thread helpOpenEditor( final VdbEditingContext editor ) throws Exception {
        return new Thread(new Runnable() {
            public void run() {
                System.out.println("  helpOpenEditor.run()"); //$NON-NLS-1$
                try {
                    editor.open();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    public void testGetModels() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();
        final VirtualDatabase vdb = editor.getVirtualDatabase();
        assertNotNull(vdb);
        assertEquals(6, vdb.getModels().size());

        List models = vdb.getModels();
        for (int i = 0, n = models.size(); i < n; i++) {
            ModelReference mRef = (ModelReference)models.get(i);
            assertNotNull(mRef);
            assertNotNull(mRef.getModelLocation());
            assertNotNull(mRef.getSeverity());
            assertNotNull(mRef.getModelType());
            System.out.println(mRef);

            // If the model is an XML schema file ...
            if (mRef.getModelLocation().endsWith(".xsd")) { //$NON-NLS-1$
                assertEquals(ModelType.UNKNOWN_LITERAL, mRef.getModelType());
                assertNull(mRef.getPrimaryMetamodelUri());
            } else {
                assertNotNull(mRef.getName());
                assertNotNull(mRef.getPrimaryMetamodelUri());
            }
        }
    }

    public void testGetResourcePaths() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        String[] paths = editor.getResourcePaths();
        assertNotNull(paths);
        assertEquals(23, paths.length);
    }

    public void testGetResourceByPath() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        String[] paths = editor.getResourcePaths();
        assertNotNull(paths);
        assertEquals(23, paths.length);

        assertNotNull(editor.getResource(paths[0]));
        assertNotNull(editor.getResource(paths[1]));
        assertNotNull(editor.getResource(paths[2]));
        assertNotNull(editor.getResource(paths[3]));
        assertNotNull(editor.getResource(paths[4]));
        assertNotNull(editor.getResource(paths[5]));
        assertNotNull(editor.getResource(paths[6]));
        assertNotNull(editor.getResource(paths[7]));
        assertNotNull(editor.getResource(paths[8]));
        assertNotNull(editor.getResource(paths[9]));
        assertNotNull(editor.getResource(paths[10]));
        assertNotNull(editor.getResource(paths[11]));
        assertNotNull(editor.getResource(paths[12]));
        assertNotNull(editor.getResource(paths[13]));
        assertNotNull(editor.getResource(paths[14]));
        assertNotNull(editor.getResource(paths[15]));
        assertNotNull(editor.getResource(paths[16]));
        assertNotNull(editor.getResource(paths[17]));
        assertNotNull(editor.getResource(paths[18]));
        assertNotNull(editor.getResource(paths[19]));
        assertNotNull(editor.getResource(paths[20]));
        assertNotNull(editor.getResource(paths[21]));
        assertNotNull(editor.getResource(paths[22]));
    }

    public void testModelVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        assertTrue(editor.isVisible(File.separator + "PaulsProj170722" + File.separator + "Books.xsd")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testManifestModelVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        assertTrue(!editor.isVisible(VdbEditingContext.MANIFEST_MODEL_NAME));
    }

    public void testIndexVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        assertTrue(!editor.isVisible("/runtime-inf" + File.separator + IndexConstants.INDEX_NAME.COLUMNS_INDEX)); //$NON-NLS-1$
    }

    public void testDDLVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(MATERIALIZED_VDB_PATH));

        editor.open();

        assertTrue(!editor.isVisible("/MaterializationModel_IBM_DB2_7_x_DDL.ddl")); //$NON-NLS-1$
    }

    public void testMaterializationModelVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(MATERIALIZED_VDB_PATH));

        editor.open();

        assertTrue(!editor.isVisible("/MaterializationModelForMetViewTestVDB.xmi")); //$NON-NLS-1$
    }

    public void testWsdlVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(WSDL_VDB_PATH));

        editor.open();

        assertTrue(editor.isVisible("/" + VdbEditingContext.GENERATED_WSDL_FILENAME)); //$NON-NLS-1$
    }

    public void testWsdlRuntimeVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(WSDL_VDB_PATH));
        final RuntimeIndexSelector selector = new RuntimeIndexSelector(WSDL_VDB_PATH);

        editor.open();

        String[] paths = selector.getFilePaths();
        for (int i = 0; i < paths.length; i++) {
            String filePath = paths[i];
            if (filePath != null && filePath.indexOf(VdbEditingContext.GENERATED_WSDL_FILENAME) > 0) {
                editor.isVisible(filePath);
            }
        }
    }

    public void testMaterializationModelRuntimeVisibility() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(MATERIALIZED_VDB_PATH));
        final RuntimeIndexSelector selector = new RuntimeIndexSelector(MATERIALIZED_VDB_PATH);

        editor.open();

        String[] paths = selector.getFilePaths();
        for (int i = 0; i < paths.length; i++) {
            String filePath = paths[i];
            if (filePath != null && filePath.indexOf("MaterializationModelForMetViewTestVDB.xmi") > 0) { //$NON-NLS-1$
                editor.isVisible(filePath);
            }
        }
    }

    public void testCreateAndOpenSyncronization() throws Exception {
        helpCreateAndOpenEditorRunnables(20, WSDL_VDB_PATH);
    }

    public void testCreateAndOpenSyncronizationInMetadataCache() throws Exception {
        helpCreateAndOpenEditorRunnables2(100, MATERIALIZED_VDB_PATH);
    }

    public void testOpenSyncronization() throws Exception {
        VdbEditingContext editor = null;
        try {
            editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));
            helpOpenEditorRunnables(20, editor);
        } finally {
            if (editor != null) {
                editor.close();
            }
        }
    }

    /**
     * Make sure listener gets notified of modification.
     */
    public void testChangeListener1() throws Exception {
        VdbEditingContext context = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        // setup listener
        FakeListener l = new FakeListener();
        context.open();
        context.addChangeListener(l);

        // generate event
        context.setModified();
        assertTrue("ChangeListener was not notified of modification", l.notified); //$NON-NLS-1$

        context.close();
    }

    /**
     * Make sure listener gets notified of closing.
     */
    public void testChangeListener2() throws Exception {
        VdbEditingContext context = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));
        context.open();

        // setup listener
        FakeListener l = new FakeListener();
        context.addChangeListener(l);

        // generate event
        context.close();
        assertTrue("ChangeListener was not notified of context close", l.notified); //$NON-NLS-1$
    }

    /**
     * Make sure removed listener does not get notified.
     */
    public void testChangeListener3() throws Exception {
        VdbEditingContext context = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));
        context.open();

        // setup listener
        FakeListener l = new FakeListener();
        context.addChangeListener(l);
        context.removeChangeListener(l);

        // generate event
        context.close();
        assertTrue("ChangeListener was notified and shouldn't have been", !l.notified); //$NON-NLS-1$
    }

    public void testAddUserFile() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        final VirtualDatabase vdb = editor.getVirtualDatabase();
        assertNotNull(vdb);
        assertEquals(6, vdb.getModels().size());

        List models = vdb.getModels();
        for (int i = 0, n = models.size(); i < n; i++) {
            ModelReference mRef = (ModelReference)models.get(i);
            assertNotNull(mRef);
            assertNotNull(mRef.getModelLocation());
            assertNotNull(mRef.getSeverity());
            assertNotNull(mRef.getModelType());
            System.out.println(mRef);

            // If the model is an XML schema file ...
            if (mRef.getModelLocation().endsWith(".xsd")) { //$NON-NLS-1$
                assertEquals(ModelType.UNKNOWN_LITERAL, mRef.getModelType());
                assertNull(mRef.getPrimaryMetamodelUri());
            } else {
                assertNotNull(mRef.getName());
                assertNotNull(mRef.getPrimaryMetamodelUri());
            }
        }
        editor.addUserFile(USER_FILE1);

        Collection userFileNames = editor.getUserFileNames();

        assertEquals(1, userFileNames.size());
        assertEquals("userFile1.txt", (String)userFileNames.iterator().next()); //$NON-NLS-1$
    }

    public void testAddRemoveUserFile() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        final VirtualDatabase vdb = editor.getVirtualDatabase();
        assertNotNull(vdb);
        assertEquals(6, vdb.getModels().size());

        List models = vdb.getModels();
        for (int i = 0, n = models.size(); i < n; i++) {
            ModelReference mRef = (ModelReference)models.get(i);
            assertNotNull(mRef);
            assertNotNull(mRef.getModelLocation());
            assertNotNull(mRef.getSeverity());
            assertNotNull(mRef.getModelType());
            System.out.println(mRef);

            // If the model is an XML schema file ...
            if (mRef.getModelLocation().endsWith(".xsd")) { //$NON-NLS-1$
                assertEquals(ModelType.UNKNOWN_LITERAL, mRef.getModelType());
                assertNull(mRef.getPrimaryMetamodelUri());
            } else {
                assertNotNull(mRef.getName());
                assertNotNull(mRef.getPrimaryMetamodelUri());
            }
        }
        editor.addUserFile(USER_FILE1);
        Collection userFileNames = editor.getUserFileNames();
        assertEquals(1, userFileNames.size());

        editor.removeUserFileWithName(USER_FILE1.getName());
        userFileNames = editor.getUserFileNames();
        assertEquals(0, userFileNames.size());
    }

    public void testAdd2UserFiles() throws Exception {
        final VdbEditingContext editor = new VdbEditingContextImpl(new Path(TEST_VDB_PATH1));

        editor.open();

        final VirtualDatabase vdb = editor.getVirtualDatabase();
        assertNotNull(vdb);
        assertEquals(6, vdb.getModels().size());

        List models = vdb.getModels();
        for (int i = 0, n = models.size(); i < n; i++) {
            ModelReference mRef = (ModelReference)models.get(i);
            assertNotNull(mRef);
            assertNotNull(mRef.getModelLocation());
            assertNotNull(mRef.getSeverity());
            assertNotNull(mRef.getModelType());
            System.out.println(mRef);

            // If the model is an XML schema file ...
            if (mRef.getModelLocation().endsWith(".xsd")) { //$NON-NLS-1$
                assertEquals(ModelType.UNKNOWN_LITERAL, mRef.getModelType());
                assertNull(mRef.getPrimaryMetamodelUri());
            } else {
                assertNotNull(mRef.getName());
                assertNotNull(mRef.getPrimaryMetamodelUri());
            }
        }
        editor.addUserFile(USER_FILE1);
        Collection userFileNames = editor.getUserFileNames();
        assertEquals(1, userFileNames.size());

        editor.addUserFile(USER_FILE2);
        userFileNames = editor.getUserFileNames();
        assertEquals(2, userFileNames.size());

        editor.removeUserFileWithName(USER_FILE1.getName());
        userFileNames = editor.getUserFileNames();
        assertEquals(1, userFileNames.size());
    }

    class FakeListener implements IChangeListener {
        public boolean notified = false;

        public void stateChanged( IChangeNotifier theSource ) {
            notified = true;
        }
    }

}

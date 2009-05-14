/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * TestVdbEditingContextImpl
 */
public class TestVdbEditingContextImplOpenClose extends TestCase {

    protected static final String VDB_ARCHIVE_PATH = "/VdbWithNoWarnings.vdb"; //$NON-NLS-1$
    protected static final String VDB_EMPTY_ARCHIVE_PATH = "/VdbNoModels.vdb"; //$NON-NLS-1$
    protected static final String VDB_ZERO_SIZE_ARCHIVE_PATH = "/VdbEmptyNoManifestZeroSize.vdb"; //$NON-NLS-1$

    private RegisteredVdbInputResourceFinder resourceFinder;
    private VdbEditingContext vdbEditor;
    private VdbEditingContext vdbEditorOfEmpty;
    private VdbEditingContext vdbEditorOfZeroSize;
    private VdbEditingContext vdbEditorOfReadOnly;

    /**
     * Constructor for TestVdbEditingContextImpl.
     * 
     * @param name
     */
    public TestVdbEditingContextImplOpenClose( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        resourceFinder = new RegisteredVdbInputResourceFinder();

        // make resources read only
        final IPath patha = new Path(getTestDataPath() + VDB_ARCHIVE_PATH);
        patha.toFile().setReadOnly();
        final IPath pathb = new Path(getTestDataPath() + VDB_EMPTY_ARCHIVE_PATH);
        pathb.toFile().setReadOnly();
        final IPath pathc = new Path(getTestDataPath() + VDB_ZERO_SIZE_ARCHIVE_PATH);
        pathc.toFile().setReadOnly();

        // Copy the original jars to the scratch ...
        helpCopyFileToScratch(VDB_ARCHIVE_PATH);
        helpCopyFileToScratch(VDB_EMPTY_ARCHIVE_PATH);
        helpCopyFileToScratch(VDB_ZERO_SIZE_ARCHIVE_PATH);

        final IPath path1 = new Path(getTestScratchPath() + VDB_ARCHIVE_PATH);
        this.vdbEditor = new VdbEditingContextImpl(path1, resourceFinder);

        final IPath path2 = new Path(getTestScratchPath() + VDB_EMPTY_ARCHIVE_PATH);
        this.vdbEditorOfEmpty = new VdbEditingContextImpl(path2, resourceFinder);

        final IPath path3 = new Path(getTestScratchPath() + VDB_ZERO_SIZE_ARCHIVE_PATH);
        this.vdbEditorOfZeroSize = new VdbEditingContextImpl(path3, resourceFinder);

        final IPath path4 = new Path(getTestDataPath() + VDB_EMPTY_ARCHIVE_PATH);
        this.vdbEditorOfReadOnly = new VdbEditingContextImpl(path4, resourceFinder);

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (this.vdbEditor != null) {
            try {
                this.vdbEditor.close();
            } finally {
                this.vdbEditor = null;
            }
        }
        if (this.vdbEditorOfEmpty != null) {
            try {
                this.vdbEditorOfEmpty.close();
            } finally {
                this.vdbEditorOfEmpty = null;
            }
        }
        if (this.vdbEditorOfZeroSize != null) {
            try {
                this.vdbEditorOfZeroSize.close();
            } finally {
                this.vdbEditorOfZeroSize = null;
            }
        }
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        VdbEditPlugin plugin = new VdbEditPlugin();
        ((PluginUtilImpl)VdbEditPlugin.Util).initializePlatformLogger(plugin);
        TestSuite suite = new SmartTestSuite("org.teiid.designer.vdb.edit", "TestVdbEditingContextImplOpenClose"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestVdbEditingContextImplOpenClose.class);
        //suite.addTest(new TestVdbEditingContextImplOpenClose("testOpenReadOnlyFile")); //$NON-NLS-1$
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() throws Exception {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public static String getTestDataPath() {
        return SmartTestSuite.getTestDataPath();
    }

    public static String getTestScratchPath() {
        return SmartTestSuite.getTestScratchPath();
    }

    public void helpCopyFileToScratch( final String name ) throws Exception {
        helpTestFileExists(getTestDataPath() + name, false);
        final IPath orig = new Path(getTestDataPath() + name);
        final IPath copy = new Path(getTestScratchPath() + name);
        final File origFile = orig.toFile();
        final File copyFile = copy.toFile();
        if (copyFile.exists()) {
            copyFile.delete();
        }
        FileUtils.copy(origFile.getAbsolutePath(), copyFile.getAbsolutePath());
        helpTestFileExists(getTestScratchPath() + name, true);
    }

    public void helpTestFileExists( final String path,
                                    final boolean writable ) {
        final IPath ipath = new Path(path);
        final File file = ipath.toFile();
        assertEquals(true, file.canRead());
        assertEquals(writable, file.canWrite());
        assertEquals(true, file.exists());
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    public void testSetupData() {
        Assert.assertNotNull(this.vdbEditor);
        Assert.assertNotNull(this.vdbEditorOfEmpty);
        Assert.assertNotNull(this.vdbEditorOfZeroSize);
        Assert.assertNotNull(this.vdbEditorOfReadOnly);
        helpTestFileExists(getTestScratchPath() + VDB_ARCHIVE_PATH, true);
        helpTestFileExists(getTestScratchPath() + VDB_EMPTY_ARCHIVE_PATH, true);
        helpTestFileExists(getTestScratchPath() + VDB_ZERO_SIZE_ARCHIVE_PATH, true);
        helpTestFileExists(getTestDataPath() + VDB_EMPTY_ARCHIVE_PATH, false);
    }

    public void testIsOpen() {
        assertEquals(false, this.vdbEditor.isOpen());
    }

    public void testOpen() throws Exception {
        assertEquals(false, this.vdbEditor.isOpen());
        this.vdbEditor.open();
        assertEquals(false, this.vdbEditor.isSaveRequired());
        Assert.assertNotNull(this.vdbEditor.getVirtualDatabase());
        assertEquals(false, this.vdbEditor.isSaveRequired());
        assertEquals(true, this.vdbEditor.isOpen());
    }

    public void testVetoClose() throws Exception {
        this.vdbEditor.open();

        // setup listener
        VetoableChangeListener rejector = new VetoableChangeListener() {
            public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {
                if (VdbEditingContext.CLOSING.equals(evt.getPropertyName())) {
                    throw new PropertyVetoException("I don't like this change", evt); //$NON-NLS-1$
                } // endif
            }
        };
        vdbEditor.addVetoableChangeListener(rejector);

        this.vdbEditor.close();
        assertEquals(true, this.vdbEditor.isOpen());
        this.vdbEditor.getVirtualDatabase();

        // now, remove listener and try to close:
        vdbEditor.removeVetoableChangeListener(rejector);

        // try again:
        this.vdbEditor.close();

        assertEquals(false, this.vdbEditor.isOpen());
        assertEquals(false, this.vdbEditor.isSaveRequired());
        try {
            this.vdbEditor.getVirtualDatabase();
            fail("Should have thrown an exception"); //$NON-NLS-1$
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testOpenAndCloseRepeatedly() throws Exception {
        for (int i = 0; i != 9; ++i) {
            assertEquals(false, this.vdbEditor.isOpen());
            this.vdbEditor.open();
            assertEquals(false, this.vdbEditor.isSaveRequired());
            Assert.assertNotNull(this.vdbEditor.getVirtualDatabase());
            assertEquals(false, this.vdbEditor.isSaveRequired());
            assertEquals(true, this.vdbEditor.isOpen());
            this.vdbEditor.close();
            assertEquals(false, this.vdbEditor.isOpen());
            assertEquals(false, this.vdbEditor.isSaveRequired());
            try {
                this.vdbEditor.getVirtualDatabase();
                fail("Should have thrown an exception"); //$NON-NLS-1$
            } catch (IllegalStateException e) {
                // expected
            }
            // Reconstruct the editor every other time
            if (i % 3 == 0) {
                this.vdbEditor = null;
                final IPath path = new Path(getTestScratchPath() + VDB_ARCHIVE_PATH);
                this.vdbEditor = new VdbEditingContextImpl(path);
                Assert.assertNotNull(this.vdbEditor);
            }
        }
    }

    public void testOpenReadOnlyFile() throws Exception {
        assertEquals(false, this.vdbEditorOfReadOnly.isOpen());
        this.vdbEditorOfReadOnly.open();
        assertEquals(false, this.vdbEditorOfReadOnly.isSaveRequired());
        Assert.assertNotNull(this.vdbEditorOfReadOnly.getVirtualDatabase());
        assertEquals(false, this.vdbEditorOfReadOnly.isSaveRequired());
        assertEquals(true, this.vdbEditorOfReadOnly.isOpen());
        // Try saving ... this should fail ...
        // Save ...
        try {
            this.vdbEditorOfEmpty.save(null);
            fail("Expected save to throw an exception"); //$NON-NLS-1$
        } catch (Throwable e) {
            // exception expected since we are not running in plugin env
            // so should not be able to save
        }
        this.vdbEditorOfReadOnly.close();
    }

    public void testOpenZeroSizeFile() throws Exception {
        assertEquals(false, this.vdbEditorOfZeroSize.isOpen());
        this.vdbEditorOfZeroSize.open();
        assertEquals(false, this.vdbEditorOfZeroSize.isSaveRequired());
        Assert.assertNotNull(this.vdbEditorOfZeroSize.getVirtualDatabase());
        assertEquals(true, this.vdbEditorOfZeroSize.isSaveRequired());
        assertEquals(true, this.vdbEditorOfZeroSize.isOpen());
        this.vdbEditorOfZeroSize.close();
    }

    public void testOpenEmptyFile() throws Exception {
        assertEquals(false, this.vdbEditorOfEmpty.isOpen());
        this.vdbEditorOfEmpty.open();
        assertEquals(false, this.vdbEditorOfEmpty.isSaveRequired());
        Assert.assertNotNull(this.vdbEditorOfEmpty.getVirtualDatabase());
        assertEquals(false, this.vdbEditorOfEmpty.isSaveRequired());
        assertEquals(true, this.vdbEditorOfEmpty.isOpen());
        this.vdbEditorOfEmpty.close();
    }

    public void testOpenEmptyAndSave() throws Exception {
        for (int i = 0; i != 4; ++i) {
            assertEquals(false, this.vdbEditorOfEmpty.isOpen());
            this.vdbEditorOfEmpty.open();
            assertEquals(false, this.vdbEditorOfEmpty.isSaveRequired());
            Assert.assertNotNull(this.vdbEditorOfEmpty.getVirtualDatabase());
            assertEquals(false, this.vdbEditorOfEmpty.isSaveRequired());
            assertEquals(true, this.vdbEditorOfEmpty.isOpen());
            // Try saving ... this should fail ...
            // Save ...
            try {
                this.vdbEditorOfEmpty.save(null);
                fail("Expected save to throw an exception"); //$NON-NLS-1$
            } catch (Throwable e) {
                // exception expected since we are not running in plugin env
                // so should not be able to save
            }
            // Close ...
            this.vdbEditorOfEmpty.close();
        }
    }

    public void testOpenEmptyAndChange() throws Exception {
        final VdbEditingContext editor = this.vdbEditorOfEmpty;
        for (int i = 0; i != 4; ++i) {
            assertEquals(false, editor.isOpen());
            editor.open();
            assertEquals(false, editor.isSaveRequired());
            final VirtualDatabase vdb = editor.getVirtualDatabase();
            Assert.assertNotNull(vdb);
            // final boolean saveRequired = ( i == 0 );
            assertEquals(false, editor.isSaveRequired());
            assertEquals(true, editor.isOpen());
            // Close ...
            editor.close();
        }
    }

    public void testGetTempDirectory() {
        final VdbEditingContextImpl editor = (VdbEditingContextImpl)this.vdbEditor;
        assertNotNull(editor);
        TempDirectory tempDir = editor.getTempDirectory();
        assertNotNull(tempDir);
    }

    public void testGetTempDirectoryFile() {
        final VdbEditingContextImpl editor = (VdbEditingContextImpl)this.vdbEditor;
        assertNotNull(editor);
        TempDirectory tempDir = editor.getTempDirectory();
        String tempDirPath = tempDir.getPath();

        String fileName = tempDirPath + File.separator + "file"; //$NON-NLS-1$
        File f = editor.getTempDirectoryFile(fileName);
        assertEquals(fileName, f.getAbsolutePath());

        fileName = "file"; //$NON-NLS-1$
        f = editor.getTempDirectoryFile(fileName);
        assertEquals(tempDirPath + File.separator + fileName, f.getAbsolutePath());
    }

}

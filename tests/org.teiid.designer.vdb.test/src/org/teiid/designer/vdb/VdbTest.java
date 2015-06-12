/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.teiid.designer.vdb.Vdb.Event.DESCRIPTION;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hamcrest.core.IsSame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.ChecksumUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.VdbSchemaEntry;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import org.teiid.designer.vdb.file.ValidationVersionCallback;
import org.teiid.designer.vdb.file.VdbFileProcessor;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.VdbElement;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public class VdbTest implements VdbConstants {

    private static final String BOOKS_VDB_PROJECT = "books.vdb.project";

    private static final String RUNTIME_INF = "runtime-inf";

    private static final String TEST_2120  = "Test_TEIIDDES_2120";

    private static final String TEST_PROJECT = BOOKS_VDB_PROJECT + File.separator + TEST_2120 + File.separator;

    private Vdb vdb;
    private EclipseMock eclipseMock;
    private ModelWorkspaceMock modelWorkspaceMock;

    private File booksVdbFile = SmartTestDesignerSuite.getTestDataFile(getClass(), "books.vdb");

    private File books77VdbFile = SmartTestDesignerSuite.getTestDataFile(getClass(), "books-7.7.x.vdb");

    private File books84VdbFile = SmartTestDesignerSuite.getTestDataFile(getClass(), "books-8.4.x.vdb");

    private File BOOK_DATATYPES_XSD = SmartTestDesignerSuite.getTestDataFile(getClass(), TEST_PROJECT + "BookDatatypes.xsd");
    private File BOOKS_XMI = SmartTestDesignerSuite.getTestDataFile(getClass(), TEST_PROJECT + "Books.xmi");
    private File BOOKS_XSD = SmartTestDesignerSuite.getTestDataFile(getClass(), TEST_PROJECT + "Books.xsd");
    private File BOOKSXML_XMI = SmartTestDesignerSuite.getTestDataFile(getClass(), TEST_PROJECT + "BooksXML.xmi");

    @Mock
    private IFile vdbFile;
    
    @Before
    public void before() throws Exception {
        initMocks(this);

        final IPath vdbPathAbsolute = mock(IPath.class);
        when(vdbFile.getLocation()).thenReturn(vdbPathAbsolute);
        
        /*
         * Vdb uses this tempDir for creating temp archive then uses
        * File.renameTo which seems to require that both files are in
        * the same directory.
        */
        File tempDir = VdbPlugin.singleton().getStateLocation().toFile();
        final File vdbFileAbsolute = File.createTempFile(VdbTest.class.getSimpleName(), ".vdb", tempDir);
        vdbFileAbsolute.deleteOnExit();
        when(vdbPathAbsolute.toFile()).thenReturn(vdbFileAbsolute);
        
        final IPath vdbName = mock(IPath.class);
        when(vdbName.toString()).thenReturn(vdbFileAbsolute.getName());
        when(vdbName.lastSegment()).thenReturn(FileUtils.getFilenameWithoutExtension(vdbFileAbsolute.getName()));
        
        final IPath vdbPath = mock(IPath.class);
        when(vdbPath.getFileExtension()).thenReturn("vdb");
        when(vdbPath.removeFileExtension()).thenReturn(vdbName);
        when(vdbPath.makeRelativeTo(isA(IPath.class))).thenReturn(vdbPath);

        when(vdbFile.getFullPath()).thenReturn(vdbPath);
        when(vdbFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);

        eclipseMock = new EclipseMock();
        when(eclipseMock.workspaceRoot().findMember(vdbPath)).thenReturn(vdbFile);

        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);

        vdb = new XmiVdb(vdbFile);
    }
    
    @After
    public void after() {
        modelWorkspaceMock.dispose();
        eclipseMock.dispose();
    }

    /**
     * @return eclipseMock
     */
    EclipseMock getEclipseMock() {
        return eclipseMock;
    }

    /**
     * @return vdb
     */
    Vdb getVdb() {
        return vdb;
    }

    @Test
    public void shouldBeModifiedWhenDescriptionChanges() throws Exception {
        vdb.setDescription("new description");
        assertThat(vdb.isModified(), is(true));
    }

    @Test
    public void shouldBeModifiedWhenEntryIsAdded() throws Exception {
        MockFileBuilder fileBuilder = new MockFileBuilder("Test", "txt");
        vdb.addEntry(fileBuilder.getPath());
        assertThat(vdb.isModified(), is(true));
    }

    @Test
    public void shouldBeSynchronizedAfterAddingEntry() throws Exception {
        MockFileBuilder fileBuilder = new MockFileBuilder("Test", "txt");
        vdb.addEntry(fileBuilder.getPath());
        assertThat(vdb.isSynchronized(), is(true));
    }

    @Test
    public void shouldBeUnmodifiedAfterDescriptionChangesFromNullToEmpty() throws Exception {
        vdb.setDescription(" ");
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldBeUnmodifiedAfterSave() throws Exception {
//        when(vdb.getFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);
//        when(vdb.getFile().createMarker(IMarker.PROBLEM)).thenReturn(mock(IMarker.class));
        
        vdb.setDescription("new description");
        vdb.save();
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldExposeFile() throws Exception {
        assertThat(vdb.getSourceFile(), is(vdbFile));
    }

    @Test
    public void shouldExposeNameAsFileName() throws Exception {
        assertThat(vdb.getSourceFile().getFullPath(), is(vdbFile.getFullPath()));
    }

    @Test
    public void shouldInitiallyBeSynchronized() throws Exception {
        assertThat(vdb.isSynchronized(), is(true));
    }

    @Test
    public void shouldInitiallyBeUnmodified() throws Exception {
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldNeverReturnNullForEntries() throws Exception {
        assertThat(vdb.getEntries(), notNullValue());
        assertThat(vdb.getModelEntries(), notNullValue());
    }

    @Test
    public void shouldNotifyAfterChangingDescription() throws Exception {
        // set an initial description
        final String oldDescription = "oldDescription";
        vdb.setDescription(oldDescription);

        // hookup listener
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change description
        final String newDescription = "newDescription";
        vdb.setDescription("newDescription");

        // tests
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
        assertThat((String)arg.getValue().getOldValue(), is(oldDescription));
        assertThat((String)arg.getValue().getNewValue(), is(newDescription));
    }

    @Test
    public void shouldNotNotifyAfterRemovingListener() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
        vdb.removeChangeListener(listener);
        vdb.setDescription("test1");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotNotifyAfterSettingDescriptionToSameValue() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
        vdb.setDescription("test");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotNotifyIfAlreadySynchronized() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.synchronize();
        verify(listener, never()).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotRequireMonitorToAddEntry() throws Exception {
        MockFileBuilder fileBuilder = new MockFileBuilder("Test", "txt");
        vdb.addEntry(fileBuilder.getPath());
    }

    @Test
    public void shouldNotRequireMonitorToSynchronize() throws Exception {
        vdb.synchronize();
    }

    @Test
    public void shouldReflectAddedAndRemovedEntries() throws Exception {
        MockFileBuilder fileBuilder = new MockFileBuilder("Test", "txt");
        final VdbEntry entry = vdb.addEntry(fileBuilder.getPath());
        assertThat(vdb.getEntries().size(), is(1));
        vdb.removeEntry(entry);
        assertThat(vdb.getEntries().isEmpty(), is(true));
    }

    @Test
    public void shouldReflectChangedDescription() throws Exception {
        vdb.setDescription("test");
        assertThat(vdb.getDescription(), is("test"));
    }

    @Test
    public void shouldReturnExistingEntryWhenAddingDuplicateEntry() throws Exception {
        final IPath path = new Path("/my/full/path");
        final VdbEntry thisEntry = vdb.addEntry(path);
        final VdbEntry thatEntry = vdb.addEntry(path);
        assertThat(thatEntry, IsSame.sameInstance(thisEntry));
    }
    
    @Test
    public void testAddingAndRemovingImportVdbEntry() throws Exception {
    	String entryName = "testImportVdbEntry";
    	
    	vdb.addImport(entryName);
    	
    	Collection<VdbImportVdbEntry> entries = vdb.getImports();
    	assertEquals(1, entries.size());
    	
    	VdbImportVdbEntry entry = entries.iterator().next();
		assertEquals(entryName, entry.getName());
    	
    	vdb.removeImport(entry);
    	assertEquals(0, vdb.getImports().size());
    }

    @Test
    public void testAddingTypesOfVdbEntry() throws Exception {
        MockFileBuilder modelFile = new MockFileBuilder("modelFile", ModelFileUtil.EXTENSION_XMI);
        MockFileBuilder schemaFile = new MockFileBuilder("schemaFile", ModelFileUtil.EXTENSION_XSD);
        MockFileBuilder udfFile = new MockFileBuilder("udfFunction", VdbHelper.JAR_EXT);
        MockFileBuilder userFile = new MockFileBuilder("otherFile", "txt");

        modelFile.addToModelWorkspace(modelWorkspaceMock);
        when(modelFile.getResourceFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);
        when(modelFile.getResourceFile().createMarker(IMarker.PROBLEM)).thenReturn(mock(IMarker.class));

        schemaFile.addToModelWorkspace(modelWorkspaceMock);
        when(schemaFile.getResourceFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);
        when(schemaFile.getResourceFile().createMarker(IMarker.PROBLEM)).thenReturn(mock(IMarker.class));

        udfFile.addToModelWorkspace(modelWorkspaceMock);

        VdbEntry vdbEntry = vdb.addEntry(modelFile.getPath());
        assertTrue(vdbEntry instanceof VdbModelEntry);
        assertEquals(modelFile.getPath(), vdbEntry.getName());
        VdbModelEntry vdbModelEntry = (VdbModelEntry) vdbEntry;
        assertNotNull(vdbModelEntry.getIndexFile());

        assertEquals(1, vdb.getModelEntries().size());
        assertEquals(0, vdb.getSchemaEntries().size());
        /* Model entries are not included in the entries collection */
        assertEquals(0, vdb.getEntries().size());

        vdbEntry = vdb.addEntry(schemaFile.getPath());
        assertTrue(vdbEntry instanceof VdbSchemaEntry);
        assertEquals(schemaFile.getPath(), vdbEntry.getName());
        VdbSchemaEntry vdbSchemaEntry = (VdbSchemaEntry) vdbEntry;
        assertNotNull(vdbSchemaEntry.getIndexFile());

        /* Schemas are added to their own collection as no longer models */
        assertEquals(1, vdb.getModelEntries().size());
        assertEquals(1, vdb.getSchemaEntries().size());
        /* Schemas are included in the entries collection */
        assertEquals(1, vdb.getEntries().size());
        
        vdbEntry = vdb.addEntry(udfFile.getPath());
        assertTrue(vdbEntry instanceof VdbFileEntry);
        /* UDF Jars are stored in the lib directory of the vdb */
        String udfJarName = "/lib/" + udfFile.getName();
        assertEquals(udfJarName, vdbEntry.getName().toString());

        assertEquals(1, vdb.getModelEntries().size());
        assertEquals(1, vdb.getSchemaEntries().size());
        assertEquals(1, vdb.getUdfJarEntries().size());
        assertEquals(2, vdb.getEntries().size());
        assertTrue(vdb.getUdfJarNames().contains(udfJarName));

        vdbEntry = vdb.addEntry(userFile.getPath());
        assertTrue(vdbEntry instanceof VdbFileEntry);
        /* UDF Jars are stored in the other files directory of the vdb */
        String userFileName = "/otherFiles/" + userFile.getName();
        assertEquals(userFileName, vdbEntry.getName().toString());

        assertEquals(1, vdb.getModelEntries().size());
        assertEquals(1, vdb.getSchemaEntries().size());
        assertEquals(1, vdb.getUdfJarEntries().size());
        assertEquals(1, vdb.getUserFileEntries().size());
        assertEquals(3, vdb.getEntries().size());
    }

    @Test
    public void testOpeningExistingVdb() throws Exception {
        List<MockFileBuilder> builders = new ArrayList<MockFileBuilder>();
        MockFileBuilder booksDatatypesXSD = new MockFileBuilder(BOOK_DATATYPES_XSD);
        builders.add(booksDatatypesXSD);
        MockFileBuilder booksXMI = new MockFileBuilder(BOOKS_XMI);
        builders.add(booksXMI);
        MockFileBuilder booksXSD = new MockFileBuilder(BOOKS_XSD);
        builders.add(booksXSD);
        MockFileBuilder booksXMLXMI = new MockFileBuilder(BOOKSXML_XMI);
        builders.add(booksXMLXMI);

        /*
         * Need to ensure that the paths provided by the vdb point to the same file in the workspace
         * so need to mock the workspace finder and point the vdb paths to the testdata files.
         */
        for (MockFileBuilder builder : builders) {
            IPath path = new Path(File.separator + TEST_2120 + File.separator + builder.getName());
            when(getEclipseMock().workspaceRoot().findMember(path)).thenReturn(builder.getResourceFile());
        }

        MockFileBuilder booksVdbBuilder = new MockFileBuilder(booksVdbFile);
        Vdb booksVdb = new XmiVdb(booksVdbBuilder.getResourceFile());
        
        assertEquals(booksVdbFile.getCanonicalPath(), booksVdb.getName().toString());
        assertEquals(2, booksVdb.getModelEntries().size());
        assertEquals(2, booksVdb.getSchemaEntries().size());
        assertEquals(0, booksVdb.getUdfJarEntries().size());
        assertEquals(0, booksVdb.getUserFileEntries().size());

        for (VdbEntry modelEntry : booksVdb.getModelEntries()) {
            assertEquals(Synchronization.Synchronized, modelEntry.getSynchronization());
            assertTrue(((VdbModelEntry)modelEntry).getIndexFile().exists());

            /* Get the expected index file from the test data directory */
            File expIdxFile = SmartTestDesignerSuite.getTestDataFile(getClass(),
                                                   BOOKS_VDB_PROJECT + File.separator +
                                                   RUNTIME_INF + File.separator +
                                                   ((VdbModelEntry)modelEntry).getIndexName());
            assertTrue(expIdxFile.exists());

            /* Compare the checksums of the created index file and the expected index file */
            Checksum idxChksum = ChecksumUtil.computeChecksum(new FileInputStream(((VdbModelEntry)modelEntry).getIndexFile()));
            Checksum expChksum = ChecksumUtil.computeChecksum(new FileInputStream(expIdxFile));
            assertEquals(expChksum.getValue(), idxChksum.getValue());
        }

        for (VdbSchemaEntry schemaEntry : booksVdb.getSchemaEntries()) {
            assertEquals(Synchronization.Synchronized, schemaEntry.getSynchronization());
            assertTrue(schemaEntry.getIndexFile().exists());

            /* Get the expected index file from the test data directory */
            File expIdxFile = SmartTestDesignerSuite.getTestDataFile(getClass(),
                                                   BOOKS_VDB_PROJECT + File.separator +
                                                   RUNTIME_INF + File.separator +
                                                   schemaEntry.getIndexName());
            assertTrue(expIdxFile.exists());

            /* Compare the checksums of the created index file and the expected index file */
            Checksum idxChksum = ChecksumUtil.computeChecksum(new FileInputStream(schemaEntry.getIndexFile()));
            Checksum expChksum = ChecksumUtil.computeChecksum(new FileInputStream(expIdxFile));
            assertEquals(expChksum.getValue(), idxChksum.getValue());
        }
    }

    @Test
    public void testManifestOnVdbSave() throws Exception {
        /* Copy the test data file as we don't want to overwrite it */
        File tempDir = VdbPlugin.singleton().getStateLocation().toFile();
        File booksVdbCopy = FileUtils.copy(booksVdbFile, tempDir, true);
        assertTrue(booksVdbCopy.exists());

        /* Use the copy to test saving and checking out the manifest of the vdb */
        MockFileBuilder booksVdbBuilder = new MockFileBuilder(booksVdbCopy);
        booksVdbBuilder.addToModelWorkspace(modelWorkspaceMock);
        when(booksVdbBuilder.getResourceFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);
        when(booksVdbBuilder.getResourceFile().createMarker(IMarker.PROBLEM)).thenReturn(mock(IMarker.class));

        Vdb booksVdb = new XmiVdb(booksVdbBuilder.getResourceFile());
        booksVdb.save();

        JAXBContext jaxbContext = JAXBContext.newInstance(new Class<?>[] { VdbElement.class });
        ZipFile archive = new ZipFile(booksVdbCopy);
        Enumeration<? extends ZipEntry> iter = archive.entries();
        while(iter.hasMoreElements()) {
            ZipEntry zipEntry = iter.nextElement();
            InputStream entryStream = archive.getInputStream(zipEntry);
            if (! zipEntry.getName().equals(MANIFEST))
                continue;

            // Initialize using manifest
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(VdbUtil.getManifestSchema());
            VdbElement manifest = (VdbElement) unmarshaller.unmarshal(entryStream);

            assertEquals(2, manifest.getModels().size());
            for (ModelElement element : manifest.getModels()) {
                IPath path = Path.fromPortableString(element.getPath());
                assertTrue(ModelUtil.isModelFile(path));
                assertFalse(ModelUtil.isXsdFile(path));

                /* Ensure the models are still under the project folder */
                assertEquals(TEST_2120, path.segment(path.segmentCount() - 2));
            }

            assertEquals(2, manifest.getEntries().size());
            for (EntryElement element : manifest.getEntries()) {
                IPath path = Path.fromPortableString(element.getPath());
                assertTrue(ModelUtil.isXsdFile(path));

                /* Ensure the xsd are still under the project folder */
                assertEquals(TEST_2120, path.segment(path.segmentCount() - 2));
                for (PropertyElement prop : element.getProperties()) {
                    assertTrue(EntryElement.CHECKSUM.equals(prop.getName()) ||
                               EntryElement.INDEX_NAME.equals(prop.getName()));
                }
            }
        }

        archive.close();
    }

    @Test
    public void testVdbVersionCallback() throws Exception {
        MockFileBuilder booksVdbBuilder = new MockFileBuilder(booksVdbFile);
        when(booksVdbBuilder.getResourceFile().exists()).thenReturn(true);
        ValidationVersionCallback callback = new ValidationVersionCallback(booksVdbBuilder.getResourceFile());
        VdbFileProcessor processor = new VdbFileProcessor(callback);
        processor.process();
        /* Original books vdb pre-dates validation version */
        assertFalse(callback.hasException());
        assertNull(callback.getValidationVersion());

        booksVdbBuilder = new MockFileBuilder(books77VdbFile);
        when(booksVdbBuilder.getResourceFile().exists()).thenReturn(true);
        callback = new ValidationVersionCallback(booksVdbBuilder.getResourceFile());
        processor = new VdbFileProcessor(callback);
        processor.process();
        assertFalse(callback.hasException());
        assertEquals(Version.TEIID_7_7.get(), callback.getValidationVersion());

        booksVdbBuilder = new MockFileBuilder(books84VdbFile);
        when(booksVdbBuilder.getResourceFile().exists()).thenReturn(true);
        callback = new ValidationVersionCallback(booksVdbBuilder.getResourceFile());
        processor = new VdbFileProcessor(callback);
        processor.process();
        assertFalse(callback.hasException());
        assertEquals(Version.TEIID_8_4.get(), callback.getValidationVersion());
    }
}

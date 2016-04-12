/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.exception.EmptyArgumentException;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;

public class DdlImporterTest {

    private static final String EMPTY_XMI_CONTENTS = "<?xml version=\"1.0\" encoding=\"ASCII\"?>" + StringConstants.NEW_LINE
                                                     + "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" "
                                                     + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                                                     + "xmlns:diagram=\"http://www.metamatrix.com/metamodels/Diagram\" "
                                                     + "xmlns:mmcore=\"http://www.metamatrix.com/metamodels/Core\" "
                                                     + "xmlns:relational=\"http://www.metamatrix.com/metamodels/Relational\">"
                                                     + StringConstants.NEW_LINE
                                                     + "<mmcore:ModelAnnotation xmi:uuid=\"mmuuid:0863dd9d-c34b-4291-9099-0b84910fa4e5\" "
                                                     + "modelType=\"VIRTUAL\" "
                                                     + "primaryMetamodelUri=\"http://www.metamatrix.com/metamodels/Relational\"/>"
                                                     + StringConstants.NEW_LINE + "</xmi:XMI>";

    private static final String TEIID_DIALECT = "TEIID"; //$NON-NLS-1$
    private static final File TEST_DDL_FILE = SmartTestDesignerSuite.getTestDataFile(DdlImporterTest.class, "createTables.ddl");

    private EclipseMock eclipseMock;

    private DdlImporter importer;

    private DdlImporter createImporter(IProject[] projects) {
        return new DdlImporter(projects);
    }

    @Before
    public void setUp() {
        eclipseMock = new EclipseMock();
        importer = createImporter(null);
    }

    @After
    public void tearDown() throws Exception {
        eclipseMock.dispose();
        eclipseMock = null;
    }

    private MockFileBuilder createEmptyXmiFile() throws Exception, IOException {
        final MockFileBuilder modelBuilder = new MockFileBuilder("blah", StringConstants.XMI);
        FileWriter writer = null;

        try {
            writer = new FileWriter(modelBuilder.getRealFile());
            writer.write(EMPTY_XMI_CONTENTS);
        } finally {
            if (writer != null)
                writer.close();
        }

        assertTrue(ModelUtil.isModelFile(modelBuilder.getPath()));
        return modelBuilder;
    }

    private ModelResource importDdl(final String ddl) throws Exception {
        final NullProgressMonitor monitor = new NullProgressMonitor();
        final MockFileBuilder modelBuilder = createEmptyXmiFile();

        final IPath path = new Path(File.separator + modelBuilder.getProject().getName() + File.separator
                                    + modelBuilder.getName());
        when(eclipseMock.workspaceRoot().findMember(path)).thenReturn(modelBuilder.getResourceFile());
        when(eclipseMock.workspace().validateName(isA(String.class), anyInt())).thenReturn(Status.OK_STATUS);

        final ModelWorkspaceManager workspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();
        final ModelResource modelResource = (ModelResource)workspaceManager.findModelWorkspaceItem(modelBuilder.getResourceFile(),
                                                                                                   true);
        assertNotNull(modelResource);

        // create annotation
        final ModelAnnotation annotation = modelResource.getModelAnnotation();
        assertNotNull(annotation);

        modelResource.save(monitor, false);

        // import
        final DdlImporter importer = new DdlImporter(new IProject[] {modelBuilder.getProject()});
        importer.setSpecifiedParser(TEIID_DIALECT);
        importer.setModelFolder(modelBuilder.getProject());
        importer.setModelName(modelBuilder.getName());
        importer.setModelType(ModelType.VIRTUAL_LITERAL);
        importer.importDdl(ddl, monitor, 1, new Properties());

        assertFalse(importer.noDdlImported());
        assertNull(importer.getParseErrorMessage());

        importer.save(monitor, 1);
        assertTrue(importer.getImportStatus().isOK());

        return modelResource;
    }

    @Test
    public void shouldAcceptNewModel() {

        when(eclipseMock.workspace().validateName(anyString(), eq(IResource.FILE))).thenReturn(Status.OK_STATUS);

        final IFile model = mock(IFile.class);
        when(eclipseMock.workspaceRoot().getFile((IPath)anyObject())).thenReturn(model);

        importer.setModelName("model");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfDdlFileDoesNotExist() {
        importer.setDdlFileName("doesNotExist");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfDdlFileNameIsEmpty() {
        importer.setDdlFileName(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfDdlFileNameIsNull() {
        importer.setDdlFileName(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfModelFolderInNonModelProject() {
        final IProject project = mock(IProject.class);
        when(eclipseMock.workspaceRoot().findMember("project")).thenReturn(project);

        final DdlImporter importer = createImporter(new IProject[] {project});
        importer.setModelFolder("project/folder");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfModelFolderIsFile() {
        final String folder = "project/file";
        final IPath path = Path.fromPortableString(folder).makeAbsolute();
        final IProject project = mock(IProject.class);
        final IWorkspaceRoot root = eclipseMock.workspaceRoot();
        final String projectName = path.segment(0);
        when(root.findMember(projectName)).thenReturn(project);
        when(project.getName()).thenReturn(projectName);
        when(eclipseMock.workspace().validatePath(path.toString(), IResource.PROJECT | IResource.FOLDER)).thenReturn(Status.OK_STATUS);
        final IFile file = mock(IFile.class);
        when(root.findMember(path)).thenReturn(file);

        final DdlImporter importer = createImporter(new IProject[] {project});
        importer.setModelFolder(folder);
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameHasNoSegments() {
        importer.setModelFolder("/");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameIsEmpty() {
        importer.setModelFolder(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameIsNull() {
        importer.setModelFolder((String)null);
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelNameIsEmpty() {
        importer.setModelName(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelNameIsNull() {
        importer.setModelName(null);
    }

    @Test
    public void shouldProvideDdlFile() {
        importer.setDdlFileName(TEST_DDL_FILE.getAbsolutePath());
        assertThat(importer.ddlFileName(), is(TEST_DDL_FILE.getAbsolutePath()));
    }

    @Test
    public void shouldProvideModel() {
        when(eclipseMock.workspace().validateName(anyString(), eq(IResource.FILE))).thenReturn(Status.OK_STATUS);
        final IFile modelFile = mock(IFile.class);
        final IPath modelPath = mock(IPath.class);
        when(modelPath.removeFileExtension()).thenReturn(modelPath);
        when(modelFile.getFullPath()).thenReturn(modelPath);
        when(eclipseMock.workspaceRoot().getFile((IPath)anyObject())).thenReturn(modelFile);
        final IFolder folder = mock(IFolder.class);
        final IPath folderPath = mock(IPath.class);
        when(folder.getFullPath()).thenReturn(folderPath);
        when(folderPath.append(anyString())).thenReturn(folderPath);

        importer.setModelFolder(folder);
        importer.setModelName("model");
        assertThat(importer.modelFile(), notNullValue());
    }

    @Test
    public void shouldProvideModelFolder() {
        when(eclipseMock.workspace().validatePath(anyString(), eq(IResource.PROJECT | IResource.FOLDER))).thenReturn(Status.OK_STATUS);
        final IFolder folder = mock(IFolder.class);
        when(eclipseMock.workspaceRoot().getFolder((IPath)anyObject())).thenReturn(folder);

        importer.setModelFolder("project/folder");
        assertThat(importer.modelFolder(), notNullValue());
        final IContainer container = mock(IContainer.class);
        importer.setModelFolder(container);
        assertThat(importer.modelFolder(), is(container));
    }

    /**
     * Verify fix for TEIIDDES-2558.
     */
    @Test
    public void shouldImportTableCardinalityGreaterThanMaxInt() throws Exception {
        final File ddlFile = SmartTestDesignerSuite.getTestDataFile(DdlImporterTest.class, "fixed-postgres-vdb.xml");
        final String ddl = new String(Files.readAllBytes(ddlFile.toPath()));
        final ModelResource model = importDdl(ddl);

        for (final Object obj : model.getAllRootEObjects()) {
            if (obj instanceof BaseTable) {
                final BaseTable table = (BaseTable)obj;
                final String tableName = table.getName();
                final int cardinality = table.getCardinality();
                final String actual = convertIntToFloatString(cardinality);

                String expected = "";

                // take a few values from the DDL file
                if ("lineitem".equals(tableName)) {
                    expected = "6000000000"; // > Integer.MAX_VALUE
                } else if ("orders".equals(tableName)) {
                    expected = "1500000000";
                } else if ("region".equals(tableName)) {
                    expected = "5";
                } else {
                    continue;
                }

                assertThat(actual, is(expected));
            }
        }
    }

    /**
     * Verify fix for TEIIDDES-1810.
     */
    @Test
    public void shouldImportColumnStatisticValuesGreaterThanMaxInt() throws Exception {
        final File ddlFile = SmartTestDesignerSuite.getTestDataFile(DdlImporterTest.class, "largeColumnStatisticValues.xml");
        final String ddl = new String(Files.readAllBytes(ddlFile.toPath()));
        final ModelResource model = importDdl(ddl);

        for (final Object obj : model.getAllRootEObjects()) {
            if (obj instanceof BaseTable) {
                final BaseTable table = (BaseTable)obj;
                final String tableName = table.getName();
                assertThat(tableName, is("largeColumnStatisticValues"));
                
                final List<Column> columns = table.getColumns();
                assertThat(columns.size(), is(2));
                
                final Column col1 = columns.get(0);
                int distinctValueCount = -1;
                int nullValueCount = -1;
                
                if ("col_a".equals(col1.getName())) {
                    distinctValueCount = col1.getDistinctValueCount();
                    nullValueCount = columns.get(1).getNullValueCount();
                } else if ("col_b".equals(col1.getName())) {
                    nullValueCount = col1.getNullValueCount();
                    distinctValueCount = columns.get(1).getDistinctValueCount();
                } else {
                    fail("Unexpected column name of " + col1.getName());
                }
                
                final String distinctValueCountAsString = convertIntToFloatString(distinctValueCount);
                assertThat(distinctValueCountAsString, is("3500000000"));
                
                final String nullValueCountAsString = convertIntToFloatString(nullValueCount);
                assertThat(nullValueCountAsString, is("6000000000"));
            }
        }
    }
    
    private String convertIntToFloatString(final int value) {
        // copied from FloatAsIntPropertyEditorFactory
        if (value >= -1) {
            return Integer.toString(value);
        }
        
        final float floatValue = Float.intBitsToFloat(value & 0x7fffffff);
        return String.format("%.0f", floatValue);
    }

}
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.transformation.ddl.TeiidModelToDdlGenerator;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestModelToDdlGenerator implements StringConstants {

    private static final String EMPTY_XMI_CONTENTS = EMPTY_STRING +
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" + NEW_LINE +
            "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:diagram=\"http://www.metamatrix.com/metamodels/Diagram\" " +
                "xmlns:mmcore=\"http://www.metamatrix.com/metamodels/Core\" " +
                "xmlns:relational=\"http://www.metamatrix.com/metamodels/Relational\">" + NEW_LINE +
                    "<mmcore:ModelAnnotation xmi:uuid=\"mmuuid:0863dd9d-c34b-4291-9099-0b84910fa4e5\" " +
                        "modelType=\"VIRTUAL\" " +
                        "primaryMetamodelUri=\"http://www.metamatrix.com/metamodels/Relational\"/>" + NEW_LINE +
            "</xmi:XMI>";

    private String resourceName = "testModel";

    private TeiidModelToDdlGenerator generator;

    private EclipseMock eclipseMock;

    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void setup() throws Exception {
        generator = new TeiidModelToDdlGenerator();

        eclipseMock = new EclipseMock();
        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
    }

    private String removeWhitespace(String value) {
        value = value.replaceAll("\\s+", SPACE);
        value = value.replaceAll("\\s\\)", CLOSE_BRACKET);
        value = value.replaceAll("\\(\\s", OPEN_BRACKET);
        return value.trim();
    }

    private MockFileBuilder createEmptyXmiFile() throws Exception, IOException {
        //
        // Set up the mock resource using a temp file.
        // In order for it to qualify as a model file, need to copy into it
        // some preliminary xml so that's its header is identifiable.
        //
        MockFileBuilder modelBuilder = new MockFileBuilder(resourceName, XMI);
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

    private ModelResource createModelResource(String ddl) throws Exception {
        NullProgressMonitor monitor = new NullProgressMonitor();

        MockFileBuilder modelBuilder = createEmptyXmiFile();

        IPath path = new Path(File.separator + modelBuilder.getProject().getName() + File.separator + modelBuilder.getName());
        when(eclipseMock.workspaceRoot().findMember(path)).thenReturn(modelBuilder.getResourceFile());
        when(eclipseMock.workspace().validateName(isA(String.class), anyInt())).thenReturn(Status.OK_STATUS);

        ModelWorkspaceManager workspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();
        ModelResource modelResource = (ModelResource) workspaceManager.findModelWorkspaceItem(modelBuilder.getResourceFile(), true);
        assertNotNull(modelResource);

        //
        // Apply the model annotation, necessary to allow the ddl importer to setModelName() correctly
        //
        ModelAnnotation annotation = modelResource.getModelAnnotation();
        assertNotNull(annotation);

        //
        // Save the model resource just in case
        //
        modelResource.save(monitor, false);

        //
        // Import the ddl using the ddl importer
        //
        DdlImporter importer = new DdlImporter(new IProject[] { modelBuilder.getProject() });
        importer.setModelFolder(modelBuilder.getProject());
        importer.setModelName(modelBuilder.getName());
        importer.setModelType(ModelType.VIRTUAL_LITERAL);
        importer.importDdl(ddl, monitor, 1);

        assertFalse(importer.noDdlImported());
        assertNull(importer.getParseErrorMessage());

        importer.save(monitor, 1);
        assertTrue(importer.getImportStatus().isOK());

        ModelResource mResource = modelWorkspaceMock.getModelEditor().findModelResource(modelBuilder.getResourceFile());
        assertNotNull(mResource);

        return mResource;
    }

    private String roundTrip(String ddl) throws Exception, ModelWorkspaceException {
        ModelResource modelResource = createModelResource(ddl);
        String generatedDdl = generator.generate(modelResource);
        generatedDdl = removeWhitespace(generatedDdl);
        return generatedDdl;
    }

    @Test
    public void testSimpleColumns() throws Exception {
        String ddl = "CREATE VIEW StockPrices (" + NEW_LINE +
                            "symbol string," + NEW_LINE +
                            "price bigdecimal" + NEW_LINE +
                            ") AS SELECT * FROM Stock;";

        // TODO
        // Should these column option clauses be included if all the values are defaults???
        String expectedDdl = "CREATE VIEW StockPrices (" +
                                             "symbol string(10)" + COMMA + SPACE +
                                             "price bigdecimal" +
                                             ") AS SELECT * FROM Stock;"; 

        String generatedDdl = roundTrip(ddl);
        assertEquals(expectedDdl, generatedDdl);
    }

    @Test
    public void testColumnProperties() throws Exception {
        String ddl = "CREATE VIEW StockPrices (" + NEW_LINE +
                            "symbol string NOT NULL AUTO_INCREMENT PRIMARY KEY, " + NEW_LINE +
                            "price bigdecimal DEFAULT 10, " + NEW_LINE +
                            "company string NOT NULL UNIQUE, " + NEW_LINE +
                            "companyID string NOT NULL INDEX" + NEW_LINE +
                            ") AS SELECT * FROM Stock;";

        

        String expectedDdl = "CREATE VIEW StockPrices (" +
                                             "symbol string(10) NOT NULL AUTO_INCREMENT PRIMARY KEY" + COMMA + SPACE +
                                             "price bigdecimal DEFAULT 10" + COMMA + SPACE +
                                             "company string(10) NOT NULL UNIQUE" + COMMA + SPACE +
                                             "companyID string(10) NOT NULL INDEX" +
                                             ") AS SELECT * FROM Stock;"; 

        String generatedDdl = roundTrip(ddl);
        assertEquals(expectedDdl, generatedDdl);
    }
}

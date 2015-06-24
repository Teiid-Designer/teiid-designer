/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbSourceInfo;
import org.teiid.designer.vdb.VdbTestUtils;
import org.teiid.designer.vdb.dynamic.DynamicVdb;
import org.w3c.dom.Document;

@SuppressWarnings( "javadoc" )
public class TestDyamicVdbExport implements VdbConstants {

    private EclipseMock eclipseMock;

    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void before() throws Exception {
        eclipseMock = new EclipseMock();
        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
    }

    @After
    public void after() {
        modelWorkspaceMock.dispose();
        eclipseMock.dispose();
    }

    @Test
    public void testWriteDynamicVdb() throws Exception {
        DynamicVdb vdb = VdbTestUtils.mockPortfolioDynamicVdb(modelWorkspaceMock);

        StringWriter destination = new StringWriter();
        vdb.write(destination);
        
        Document vdbDoc = VdbTestUtils.readDocument(destination.toString());
        assertNotNull(vdbDoc);

        Document controlDoc = VdbTestUtils.readDocument(VdbTestUtils.PORTFOLIO_VDB_FILE);
        assertNotNull(controlDoc);

        VdbTestUtils.compareDocuments(controlDoc, vdbDoc);
    }

    @Test
    public void convertXmiVdbToDynamicVdb() throws Exception {
        Vdb booksVdb = VdbTestUtils.mockBooksVdb(modelWorkspaceMock);

        File destFile = File.createTempFile(booksVdb.getName(), ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        MockFileBuilder destination = new MockFileBuilder(destFile);

        DynamicVdb dynVdb = booksVdb.convert(DynamicVdb.class, destination.getResourceFile());
        assertNotNull(dynVdb);

        assertEquals(booksVdb.getName(), dynVdb.getName());
        assertEquals(booksVdb.getDescription(), dynVdb.getDescription());

        for (Map.Entry<Object, Object> entry : booksVdb.getProperties().entrySet()) {
            assertEquals(entry.getValue(), dynVdb.getProperties().getProperty(entry.getKey().toString()));
        }

        assertEquals(destination.getResourceFile(), dynVdb.getSourceFile());
        assertEquals(booksVdb.getVersion(), dynVdb.getVersion());

        assertEquals(booksVdb.getConnectionType(), dynVdb.getConnectionType());
        assertEquals(booksVdb.isPreview(), dynVdb.isPreview());
        assertEquals(booksVdb.getQueryTimeout(), dynVdb.getQueryTimeout());

        assertEquals(booksVdb.getAllowedLanguages().size(), dynVdb.getAllowedLanguages().size());
        List<String> dynLanguageValues = Arrays.asList(dynVdb.getAllowedLanguages().getAllowedLanguageValues());
        for (String language : booksVdb.getAllowedLanguages().getAllowedLanguageValues()) {
            assertTrue(dynLanguageValues.contains(language));
        }

        assertEquals(booksVdb.getSecurityDomain(), dynVdb.getSecurityDomain());
        assertEquals(booksVdb.getGssPattern(), dynVdb.getGssPattern());
        assertEquals(booksVdb.getPasswordPattern(), dynVdb.getPasswordPattern());
        assertEquals(booksVdb.getAuthenticationType(), dynVdb.getAuthenticationType());
        assertEquals(booksVdb.getValidationDateTime(), dynVdb.getValidationDateTime());
        assertEquals(booksVdb.getValidationVersion(), dynVdb.getValidationVersion());
        assertEquals(booksVdb.isAutoGenerateRESTWar(), dynVdb.isAutoGenerateRESTWar());

        assertEquals(booksVdb.getImports().size(), dynVdb.getImports().size());
        for (VdbImportVdbEntry entry : booksVdb.getImports()) {
            assertTrue(dynVdb.getImports().contains(entry));
        }

        assertEquals(booksVdb.getTranslators().size(), dynVdb.getTranslators().size());
        for (TranslatorOverride translator : booksVdb.getTranslators()) {
            assertTrue(dynVdb.getTranslators().contains(translator));
        }

        assertEquals(booksVdb.getDataRoles().size(), dynVdb.getDataRoles().size());
        for (DataRole role : booksVdb.getDataRoles()) {
            assertTrue(dynVdb.getDataRoles().contains(role));
        }

        assertEquals(booksVdb.getModelEntries().size(), dynVdb.getDynamicModels().size());
        for (VdbModelEntry entry : booksVdb.getModelEntries()) {
            VdbSourceInfo sourceInfo = entry.getSourceInfo();
            DynamicModel dynModel = null;

            Collection<DynamicModel> dynamicModels = dynVdb.getDynamicModels();
            for (DynamicModel model : dynamicModels) {
                if (model.getName().equals(entry.getName())) {
                    dynModel = model;
                    break;
                }
            }
            assertNotNull(dynModel);

            assertEquals(entry.getDescription(), dynModel.getDescription());

            for (Map.Entry<Object, Object> prop : entry.getProperties().entrySet()) {
                assertEquals(prop.getValue(), dynModel.getProperties().getProperty(prop.getKey().toString()));
            }

            assertEquals(entry.getType(), dynModel.getModelType().toString());
            assertEquals(sourceInfo.isMultiSource(), dynModel.isMultiSource());
            assertEquals(sourceInfo.isAddColumn(), dynModel.doAddColumn());
            assertEquals(sourceInfo.getColumnAlias(), dynModel.getColumnAlias());

            assertEquals(sourceInfo.getSources().size(), dynModel.getSources().length);
            List<VdbSource> dynSources = Arrays.asList(dynModel.getSources());
            for (VdbSource source : sourceInfo.getSources()) {
                assertTrue(dynSources.contains(source));
            }

            //
            // TODO
            // Separate unit tests for testing the generator. Assume its done its job correctly.
            //

            if (dynModel.getName().equals(VdbTestUtils.BOOKS_MODEL)) {
                Metadata metadata = dynModel.getMetadata();
                assertEquals(Metadata.Type.DDL, metadata.getType());
                assertEquals(VdbTestUtils.BOOKS_MODEL_DDL, metadata.getSchemaText());
            }
        }
    }
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbSourceInfo;
import org.teiid.designer.vdb.VdbTestUtils;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;


@SuppressWarnings( "javadoc" )
public class TestDynamicVdbImport implements VdbConstants {

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
    public void convertDynamicVdbToXmiVdb() throws Exception {
        DynamicVdb dynVdb = VdbTestUtils.mockPortfolioDynamicVdb(modelWorkspaceMock);
        assertNotNull(dynVdb);

        IFile dynVdbSrcFile = dynVdb.getSourceFile();
        IProject parent = dynVdbSrcFile.getProject();
        assertNotNull(parent);

        File destFile = File.createTempFile(dynVdb.getName(), ITeiidVdb.VDB_DOT_EXTENSION);
        MockFileBuilder destination = new MockFileBuilder(destFile);

        XmiVdb xmiVdb = dynVdb.convert(XmiVdb.class, destination.getResourceFile());

        assertEquals(dynVdb.getName(), xmiVdb.getName());
        assertEquals(dynVdb.getDescription(), xmiVdb.getDescription());

        for (Map.Entry<Object, Object> entry : dynVdb.getProperties().entrySet()) {
            assertEquals(entry.getValue(), xmiVdb.getProperties().getProperty(entry.getKey().toString()));
        }

        assertEquals(destination.getResourceFile(), xmiVdb.getSourceFile());
        assertEquals(dynVdb.getVersion(), xmiVdb.getVersion());

        assertEquals(dynVdb.getConnectionType(), xmiVdb.getConnectionType());
        assertEquals(dynVdb.isPreview(), xmiVdb.isPreview());
        assertEquals(dynVdb.getQueryTimeout(), xmiVdb.getQueryTimeout());

        assertEquals(dynVdb.getAllowedLanguages().size(), xmiVdb.getAllowedLanguages().size());
        List<String> dynLanguageValues = Arrays.asList(xmiVdb.getAllowedLanguages().getAllowedLanguageValues());
        for (String language : dynVdb.getAllowedLanguages().getAllowedLanguageValues()) {
            assertTrue(dynLanguageValues.contains(language));
        }

        assertEquals(dynVdb.getSecurityDomain(), xmiVdb.getSecurityDomain());
        assertEquals(dynVdb.getGssPattern(), xmiVdb.getGssPattern());
        assertEquals(dynVdb.getPasswordPattern(), xmiVdb.getPasswordPattern());
        assertEquals(dynVdb.getAuthenticationType(), xmiVdb.getAuthenticationType());
        assertEquals(dynVdb.getValidationDateTime(), xmiVdb.getValidationDateTime());
        assertEquals(dynVdb.isAutoGenerateRESTWar(), xmiVdb.isAutoGenerateRESTWar());

        assertEquals(dynVdb.getImports().size(), xmiVdb.getImports().size());
        for (VdbImportVdbEntry entry : dynVdb.getImports()) {
            assertTrue(xmiVdb.getImports().contains(entry));
        }

        assertEquals(dynVdb.getTranslators().size(), xmiVdb.getTranslators().size());
        for (TranslatorOverride translator : dynVdb.getTranslators()) {
            assertTrue(xmiVdb.getTranslators().contains(translator));
        }

        assertEquals(dynVdb.getDataRoles().size(), xmiVdb.getDataRoles().size());
        for (DataRole role : dynVdb.getDataRoles()) {
            assertTrue(xmiVdb.getDataRoles().contains(role));
        }

        assertEquals(dynVdb.getDynamicModels().size(), xmiVdb.getModelEntries().size());
        for (DynamicModel dynModel : dynVdb.getDynamicModels()) {

            VdbModelEntry modelEntry = null;
            Collection<VdbModelEntry> entries = xmiVdb.getModelEntries();
            for (VdbModelEntry entry : entries) {
                if (dynModel.getName().equals(entry.getName())) {
                    modelEntry = entry;
                    break;
                }
            }
            assertNotNull(modelEntry);

            assertEquals(dynModel.getDescription(), modelEntry.getDescription());

            for (Map.Entry<Object, Object> prop : dynModel.getProperties().entrySet()) {
                assertEquals(prop.getValue(), modelEntry.getProperties().getProperty(prop.getKey().toString()));
            }

            VdbSourceInfo sourceInfo = modelEntry.getSourceInfo();

            assertEquals(dynModel.getModelType().toString(), modelEntry.getType());            
            assertEquals(dynModel.isMultiSource(), sourceInfo.isMultiSource());
            assertEquals(dynModel.doAddColumn(), sourceInfo.isAddColumn());
            assertEquals(dynModel.getColumnAlias(), sourceInfo.getColumnAlias());

            assertEquals(dynModel.getSources().length, sourceInfo.getSources().size());
            List<VdbSource> entrySources = new ArrayList<VdbSource>(sourceInfo.getSources());
            for (VdbSource source : dynModel.getSources()) {
                assertTrue(entrySources.contains(source));
            }
        }
    }
}

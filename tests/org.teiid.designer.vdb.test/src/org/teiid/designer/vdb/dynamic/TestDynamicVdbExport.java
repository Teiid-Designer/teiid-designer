/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.dynamic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.util.TestUtilities;
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
import org.w3c.dom.Document;

@SuppressWarnings( "javadoc" )
public class TestDynamicVdbExport implements VdbConstants {

    private EclipseMock eclipseMock;

    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void before() throws Exception {
        eclipseMock = new EclipseMock();
        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
        TestUtilities.setDefaultTeiidVersion();
    }

    @After
    public void after() throws Exception {
        // Disposes the eclipse mock as well
        modelWorkspaceMock.dispose();
        modelWorkspaceMock = null;
        eclipseMock = null;
        TestUtilities.unregisterTeiidServerManager();
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
    public void convertXmiVdbToDynamicVdb_BOOKS() throws Exception {
        Vdb booksVdb = VdbTestUtils.mockBooksVdb(modelWorkspaceMock);

        File destFile = File.createTempFile(booksVdb.getName(), ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        MockFileBuilder destination = new MockFileBuilder(destFile);

        DynamicVdb dynVdb = booksVdb.convert(DynamicVdb.class, destination.getResourceFile(), new Properties());
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

        // Should contain all relational model entries. Any model entries that are
        // non-relational should be excluded and a warning placed in the status
        for (VdbModelEntry entry : booksVdb.getModelEntries()) {
            String entryName = entry.getName();

            // Check that the entry model has not been warned about
            // so not exported
            boolean nonRelational = false;
            IStatus status = dynVdb.getStatus();
            if (! status.isOK()) {
                if (status instanceof MultiStatus) {
                    IStatus[] children = status.getChildren();
                    for (IStatus child : children) {
                        String notIncMsg = " is not a relational model and was not included in the generated dynamic VDB";
                        if (! child.isOK() && child.getMessage().contains(entryName + notIncMsg)) {
                            // Model should NOT be included
                            nonRelational = true;
                        }
                    }
                }
            }

            if (nonRelational)
                continue;

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
    
    @Test
    public void convertXmiVdbToDynamicVdb_CUSTOMERS() throws Exception {
    	Vdb mockVdb = VdbTestUtils.mockCustomersVdb(modelWorkspaceMock);

        File destFile = File.createTempFile(mockVdb.getName(), ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        MockFileBuilder destination = new MockFileBuilder(destFile);
        
        DynamicVdb dynVdb = mockVdb.convert(DynamicVdb.class, destination.getResourceFile(), new Properties());
        assertNotNull(dynVdb);

        assertEquals(mockVdb.getName(), dynVdb.getName());
        assertEquals(mockVdb.getDescription(), dynVdb.getDescription());

        for (Map.Entry<Object, Object> entry : mockVdb.getProperties().entrySet()) {
            assertEquals(entry.getValue(), dynVdb.getProperties().getProperty(entry.getKey().toString()));
            
            if( ((String)entry.getKey()).equals("allowed-languages") ) {
            	assertEquals("cobol, java", dynVdb.getProperties().getProperty(entry.getKey().toString()));
            }
        }

        assertEquals(destination.getResourceFile(), dynVdb.getSourceFile());
        assertEquals(mockVdb.getVersion(), dynVdb.getVersion());

        assertEquals(mockVdb.getConnectionType(), dynVdb.getConnectionType());
        assertEquals(mockVdb.isPreview(), dynVdb.isPreview());
        assertEquals(mockVdb.getQueryTimeout(), dynVdb.getQueryTimeout());

        assertEquals(mockVdb.getAllowedLanguages().size(), dynVdb.getAllowedLanguages().size());
        List<String> dynLanguageValues = Arrays.asList(dynVdb.getAllowedLanguages().getAllowedLanguageValues());
        for (String language : mockVdb.getAllowedLanguages().getAllowedLanguageValues()) {
            assertTrue(dynLanguageValues.contains(language));
        }

        assertEquals(mockVdb.getSecurityDomain(), dynVdb.getSecurityDomain());
        assertEquals(mockVdb.getGssPattern(), dynVdb.getGssPattern());
        assertEquals(mockVdb.getPasswordPattern(), dynVdb.getPasswordPattern());
        assertEquals(mockVdb.getAuthenticationType(), dynVdb.getAuthenticationType());
        assertEquals(mockVdb.isAutoGenerateRESTWar(), dynVdb.isAutoGenerateRESTWar());

        assertEquals(mockVdb.getImports().size(), dynVdb.getImports().size());
        for (VdbImportVdbEntry entry : mockVdb.getImports()) {
            assertTrue(dynVdb.getImports().contains(entry));
        }
        
        assertEquals(mockVdb.getTranslators().size(), dynVdb.getTranslators().size());
        for (TranslatorOverride translator : mockVdb.getTranslators()) {
            assertTrue(dynVdb.getTranslators().contains(translator));
        }

        assertEquals(mockVdb.getDataRoles().size(), dynVdb.getDataRoles().size());
        
        for (DataRole role : mockVdb.getDataRoles()) {

            DataRole dynDataRole = null;
            Collection<DataRole> dataRoles = dynVdb.getDataRoles();
            for (DataRole dataRole : dataRoles) {
                if (dataRole.getName().equals(role.getName())) {
                	dynDataRole = dataRole;
                }
            }
            assertNotNull(dynDataRole);
            
            if (dynDataRole.getName().equals(role.getName())) {
            	assertEquals(role.getRoleNames().size(), dynDataRole.getRoleNames().size());
            	assertEquals(role.getPermissions().size(), dynDataRole.getPermissions().size());
                Collection<Permission> permissions = role.getPermissions();
                for (Permission perm : permissions) {
	                Permission dynPermission = dynDataRole.getPermission(perm.getName());
	                assertNotNull(dynPermission);
	                
	            	assertEquals(perm.getCondition(), dynPermission.getCondition());
	            	assertEquals(perm.isConstraint(), dynPermission.isConstraint());
	            	assertEquals(perm.getMask(), dynPermission.getMask());
	            	assertEquals(perm.getOrder(), dynPermission.getOrder());
                }
            }
            
            if( role.getName().equals(VdbTestUtils.CUSTOMER_SUPPORT_DATA_ROLE) ) {
            	// Check permissions;
            	assertEquals(role.getPermissions().size(), dynDataRole.getPermissions().size());
            	assertEquals(VdbTestUtils.CUSTOMER_SUPPORT_DATA_ROLE_DESCRIPTION, dynDataRole.getDescription());
            	
            	Permission permission = role.getPermission("CustomerAccounts.CUSTOMER");
            	assertNotNull(permission);
            	Permission dynPermission = dynDataRole.getPermission("CustomerAccounts.CUSTOMER");
            	assertNotNull(dynPermission);
            	assertEquals("CUSTID > 10000", dynPermission.getCondition());
            	assertEquals(false, dynPermission.isConstraint());
            	/*
        <permission>
            <resource-name>CustomerAccounts.CUSTOMER.CUSTID</resource-name>
            <condition constraint="false">CUSTID = 12345</condition>
            <mask order="2">CUSTID = 0</mask>
        </permission>
            	 */
            	
            	permission = role.getPermission("CustomerAccounts.CUSTOMER.CUSTID");
            	assertNotNull(permission);
            	dynPermission = dynDataRole.getPermission("CustomerAccounts.CUSTOMER.CUSTID");
            	assertNotNull(dynPermission);
            	assertEquals("CUSTID = 12345", dynPermission.getCondition());
            	assertEquals(false, dynPermission.isConstraint());
            	assertEquals("CUSTID = 0", dynPermission.getMask());
            	assertEquals(2, dynPermission.getOrder());
            }
        }

        assertEquals(mockVdb.getModelEntries().size(), dynVdb.getDynamicModels().size());
        for (VdbModelEntry entry : mockVdb.getModelEntries()) {
            VdbSourceInfo sourceInfo = entry.getSourceInfo();
            DynamicModel dynModel = null;

            Collection<DynamicModel> dynamicModels = dynVdb.getDynamicModels();
            for (DynamicModel model : dynamicModels) {
                if (model.getName().equals(entry.getName()))
                    dynModel = model;
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

            if (dynModel.getName().equals(VdbTestUtils.CUSTOMER_ACCOUNTS_MODEL)) {
                Metadata metadata = dynModel.getMetadata();
                assertEquals(Metadata.Type.DDL, metadata.getType());
            }
        }
    }
}

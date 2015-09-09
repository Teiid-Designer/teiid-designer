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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.comments.CommentSets;
import org.teiid.designer.comments.Commentable;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbSourceInfo;
import org.teiid.designer.vdb.VdbTestUtils;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.MaskElement;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PermissionElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.SourceElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings( "javadoc" )
public class TestDynamicVdbImport implements VdbConstants, Commentable {

    private enum Comments {
        VDB_TITLE(NEW_LINE + "    The Portfolio Example Vdb" + NEW_LINE),
        VDB_TITLE_2(NEW_LINE + "    Another comment for testing purposes" + NEW_LINE),
        VDB_DESCRIPTION(NEW_LINE + "        The defaults for attributes included for test comparison purposes" + NEW_LINE
                        + "    "),
        USE_CONNECTOR_PROPERTY(NEW_LINE + "      Setting to use connector supplied metadata. Can be \"true\" or \"cached\"."
                               + NEW_LINE + "      \"true\" will obtain metadata once for every launch of Teiid." + NEW_LINE
                               + "      \"cached\" will save a file containing the metadata into" + NEW_LINE
                               + "      the deploy/<vdb name>/<vdb version/META-INF directory" + NEW_LINE + "    "),
        MODEL_MARKETDATA(NEW_LINE + "      Each model represents a access to one or more sources." + NEW_LINE
                         + "      The name of the model will be used as a top level schema name" + NEW_LINE
                         + "      for all of the metadata imported from the connector." + NEW_LINE + NEW_LINE
                         + "      NOTE: Multiple models, with different import settings, can be bound to" + NEW_LINE
                         + "      the same connector binding and will be treated as the same source at" + NEW_LINE
                         + "      runtime." + NEW_LINE + "    "),
        MARKETDATA_TEXT_CONNECTOR(NEW_LINE + "            Each source represents a translator and data source. There are"
                                  + NEW_LINE + "            pre-defined translators, or you can create one." + NEW_LINE
                                  + "        "),
        ACCOUNTS_USEFULLSCHEMA_PROPERTY(NEW_LINE + "          JDBC Import settings" + NEW_LINE + NEW_LINE
                                        + "          importer.useFullSchemaName directs the importer to drop the source"
                                        + NEW_LINE
                                        + "          schema from the Teiid object name, so that the Teiid fully qualified name"
                                        + NEW_LINE + "          will be in the form of <model name>.<table name>" + NEW_LINE
                                        + "        "),
        ACCOUNT_H2_CONNECTOR(NEW_LINE + "            This connector is defined to reference the H2 localDS" + NEW_LINE
                             + "          "),
        IMPORTER_HEADER_ROW_NUMBER_PROPERTY(" importer header row number property "),
        IMPORT_EXCEL_FILE_NAME_PROPERTY(" import excel file name property "),
        METADATA_ELEMENT(" The DDL for accessing an excel spreadsheet "),

        DATA_ROLE(" Example data role "),
        DATA_ROLE_DESCRIPTION(" Useful to provide comments "),
        SUPERVISOR_MAPPED_ROLE(
                               NEW_LINE
                               + "            This role must defined in the JAAS security domain, the sample UserRolesLoginModules based roles file provided"
                               + NEW_LINE
                               + "            in this sample directory. copy these \"teiid-security-roles.properties\" and \"teiid-security-users.proeprties\""
                               + NEW_LINE
                               + "            into \"<jboss-install>/modules/org/jboss/teiid/conf\" directory and replace the old ones."
                               + NEW_LINE + "        "),
        DEPT_SUPER_MAPPED_ROLE(" Secondary mapped role name "),
        PERMISSION_ON_ACCOUNTS_TABLE(" Permission on Accounts table "),
        RESOURCE_NAME_REFERENCED_BY_PERMISSION(" Resource name referenced by permission "),
        DENY_CREATE(" Deny create "),
        ALLOW_READ(" Allow read "),
        ALLOW_UPDATE(" Allow update "),
        PERMISSION_USING_A_CONDITION(" Permission using a condition "),
        PERMISSION_MASK(" Permission mask ");

        String text;

        Comments(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private EclipseMock eclipseMock;

    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void before() throws Exception {
        eclipseMock = new EclipseMock();
        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
    }

    @After
    public void after() throws Exception {
        // Disposes the eclipse mock as well
        modelWorkspaceMock.dispose();
        modelWorkspaceMock = null;
        eclipseMock = null;
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

        XmiVdb xmiVdb = dynVdb.convert(XmiVdb.class, destination.getResourceFile(), new Properties());

        assertEquals(dynVdb.getName(), xmiVdb.getName());
        assertEquals(dynVdb.getDescription(), xmiVdb.getDescription());

        for (Map.Entry<Object, Object> entry : dynVdb.getProperties().entrySet()) {
            System.out.println("VDB Property:  " + entry.getValue() + " == "
                               + xmiVdb.getProperties().getProperty(entry.getKey().toString()));
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

    private void checkComment(Comments expected, CommentSets comments, String commentKey, int index) {
        List<String> commentsList = comments.getCommentSet(commentKey);
        assertTrue(commentsList.size() > 0);
        assertTrue(commentsList.size() > index);
        assertEquals(expected.toString(), commentsList.get(index));
    }

    @Test
    public void testCommentReader() throws Exception {
        MockFileBuilder portfolioXmlFile = VdbTestUtils.mockPortfolioVdbXmlFile();
        File portfolioFile = portfolioXmlFile.getRealFile();
        InputStream fileStream = new FileInputStream(portfolioFile);

        JAXBContext jaxbContext = JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(VdbUtil.getManifestSchema());
        VdbElement manifest = (VdbElement)unmarshaller.unmarshal(fileStream);

        CommentReader reader = new CommentReader(manifest);
        reader.read(portfolioFile);

        CommentSets vdbComments = manifest.getComments();
        assertNotNull(vdbComments);
        assertEquals(2, vdbComments.size());
        checkComment(Comments.VDB_TITLE, vdbComments, EMPTY_STRING, 0);
        checkComment(Comments.VDB_TITLE_2, vdbComments, EMPTY_STRING, 1);
        checkComment(Comments.VDB_DESCRIPTION, vdbComments, DESCRIPTION, 0);

        assertEquals(1, manifest.getProperties().size());
        PropertyElement propertyElement = manifest.getProperties().get(0);
        checkComment(Comments.USE_CONNECTOR_PROPERTY, propertyElement.getComments(), EMPTY_STRING, 0);

        ModelElement modelElement = manifest.getModels().get(0);
        assertEquals("MarketData", modelElement.getName());
        checkComment(Comments.MODEL_MARKETDATA, modelElement.getComments(), EMPTY_STRING, 0);

        SourceElement sourceElement = modelElement.getSources().get(0);
        assertEquals("text-connector", sourceElement.getName());
        checkComment(Comments.MARKETDATA_TEXT_CONNECTOR, sourceElement.getComments(), EMPTY_STRING, 0);

        modelElement = manifest.getModels().get(1);
        assertEquals("Accounts", modelElement.getName());
        propertyElement = modelElement.getProperties().get(0);
        assertNotNull(propertyElement);
        checkComment(Comments.ACCOUNTS_USEFULLSCHEMA_PROPERTY, propertyElement.getComments(), EMPTY_STRING, 0);

        sourceElement = modelElement.getSources().get(0);
        assertEquals("h2-connector", sourceElement.getName());
        checkComment(Comments.ACCOUNT_H2_CONNECTOR, sourceElement.getComments(), EMPTY_STRING, 0);

        modelElement = manifest.getModels().get(2);
        assertEquals("PersonalValuations", modelElement.getName());
        propertyElement = modelElement.getProperties().get(0);
        assertNotNull(propertyElement);
        checkComment(Comments.IMPORTER_HEADER_ROW_NUMBER_PROPERTY, propertyElement.getComments(), EMPTY_STRING, 0);

        propertyElement = modelElement.getProperties().get(1);
        assertNotNull(propertyElement);
        checkComment(Comments.IMPORT_EXCEL_FILE_NAME_PROPERTY, propertyElement.getComments(), EMPTY_STRING, 0);

        MetadataElement metadataElement = modelElement.getMetadata().get(0);
        assertNotNull(metadataElement);
        checkComment(Comments.METADATA_ELEMENT, metadataElement.getComments(), EMPTY_STRING, 0);

        DataRoleElement dataRoleElement = manifest.getDataPolicies().get(0);
        assertNotNull(dataRoleElement);
        checkComment(Comments.DATA_ROLE, dataRoleElement.getComments(), EMPTY_STRING, 0);
        checkComment(Comments.DATA_ROLE_DESCRIPTION, dataRoleElement.getComments(), DESCRIPTION, 0);
        checkComment(Comments.SUPERVISOR_MAPPED_ROLE, dataRoleElement.getComments(), MAPPED_ROLE_NAME + HYPHEN + "supervisor", 0);
        checkComment(Comments.DEPT_SUPER_MAPPED_ROLE, dataRoleElement.getComments(), MAPPED_ROLE_NAME + HYPHEN
                                                                                     + "dept-supervisor", 0);

        List<PermissionElement> permissionElements = dataRoleElement.getPermissions();
        assertNotNull(permissionElements);
        assertTrue(!permissionElements.isEmpty());
        PermissionElement permissionElement = permissionElements.get(0);
        assertEquals("Accounts", permissionElement.getResourceName());
        checkComment(Comments.PERMISSION_ON_ACCOUNTS_TABLE, permissionElement.getComments(), EMPTY_STRING, 0);
        checkComment(Comments.RESOURCE_NAME_REFERENCED_BY_PERMISSION, permissionElement.getComments(), RESOURCE_NAME, 0);
        checkComment(Comments.DENY_CREATE, permissionElement.getComments(), ALLOW_CREATE, 0);
        checkComment(Comments.ALLOW_READ, permissionElement.getComments(), ALLOW_READ, 0);
        checkComment(Comments.ALLOW_UPDATE, permissionElement.getComments(), ALLOW_UPDATE, 0);

        permissionElement = permissionElements.get(1);
        assertEquals("Accounts.Customer", permissionElement.getResourceName());
        ConditionElement conditionElement = permissionElement.getCondition();
        checkComment(Comments.PERMISSION_USING_A_CONDITION, conditionElement.getComments(), EMPTY_STRING, 0);

        permissionElement = permissionElements.get(2);
        assertEquals("Accounts.Customer.SSN", permissionElement.getResourceName());
        MaskElement maskElement = permissionElement.getMask();
        checkComment(Comments.PERMISSION_MASK, maskElement.getComments(), EMPTY_STRING, 0);
    }

    /**
     * @param dynamicModels
     * @param modelName
     * @return model with given name
     */
    private DynamicModel findModel(Collection<DynamicModel> models, String modelName) {
        assertNotNull(models);
        for (DynamicModel model : models) {
            if (model.getName().equals(modelName))
                return model;
        }

        Assert.fail("No dynamic model named " + modelName);
        return null;
    }

    @Test
    public void testCommentsInDynamicVdb() throws Exception {
        MockFileBuilder portfolioXmlFile = VdbTestUtils.mockPortfolioVdbXmlFile();
        DynamicVdb vdb = new DynamicVdb(portfolioXmlFile.getResourceFile());

        CommentSets vdbComments = vdb.getComments();
        assertNotNull(vdbComments);
        assertEquals(2, vdbComments.size());
        checkComment(Comments.VDB_TITLE, vdbComments, EMPTY_STRING, 0);
        checkComment(Comments.VDB_TITLE_2, vdbComments, EMPTY_STRING, 1);
        checkComment(Comments.VDB_DESCRIPTION, vdbComments, DESCRIPTION, 0);

        CommentSets propertyComments = vdb.getPropertyComments("UseConnectorMetadata");
        assertNotNull(propertyComments);

        checkComment(Comments.USE_CONNECTOR_PROPERTY, propertyComments, EMPTY_STRING, 0);

        DynamicModel model = findModel(vdb.getDynamicModels(), "MarketData");
        checkComment(Comments.MODEL_MARKETDATA, model.getComments(), EMPTY_STRING, 0);

        VdbSource source = model.getSources()[0];
        assertEquals("text-connector", source.getName());
        checkComment(Comments.MARKETDATA_TEXT_CONNECTOR, source.getComments(), EMPTY_STRING, 0);

        model = findModel(vdb.getDynamicModels(), "Accounts");
        propertyComments = model.getPropertyComments("importer.useFullSchemaName");
        assertNotNull(propertyComments);
        checkComment(Comments.ACCOUNTS_USEFULLSCHEMA_PROPERTY, propertyComments, EMPTY_STRING, 0);

        source = model.getSources()[0];
        assertEquals("h2-connector", source.getName());
        checkComment(Comments.ACCOUNT_H2_CONNECTOR, source.getComments(), EMPTY_STRING, 0);

        model = findModel(vdb.getDynamicModels(), "PersonalValuations");
        propertyComments = model.getPropertyComments("importer.headerRowNumber");
        assertNotNull(propertyComments);
        checkComment(Comments.IMPORTER_HEADER_ROW_NUMBER_PROPERTY, propertyComments, EMPTY_STRING, 0);

        propertyComments = model.getPropertyComments("importer.ExcelFileName");
        assertNotNull(propertyComments);
        checkComment(Comments.IMPORT_EXCEL_FILE_NAME_PROPERTY, propertyComments, EMPTY_STRING, 0);

        Metadata metadata = model.getMetadata();
        assertNotNull(metadata);
        checkComment(Comments.METADATA_ELEMENT, metadata.getComments(), EMPTY_STRING, 0);

        Iterator<DataRole> dataRoleIter = vdb.getDataRoles().iterator();
        DataRole dataRole = dataRoleIter.next();
        assertNotNull(dataRole);
        checkComment(Comments.DATA_ROLE, dataRole.getComments(), EMPTY_STRING, 0);
        checkComment(Comments.DATA_ROLE_DESCRIPTION, dataRole.getComments(), DESCRIPTION, 0);
        checkComment(Comments.SUPERVISOR_MAPPED_ROLE,
                     dataRole.getComments(),
                     MAPPED_ROLE_NAME + HYPHEN + "supervisor",
                     0);
        checkComment(Comments.DEPT_SUPER_MAPPED_ROLE, dataRole.getComments(), MAPPED_ROLE_NAME + HYPHEN + "dept-supervisor", 0);

        Collection<Permission> permissions = dataRole.getPermissions();
        assertNotNull(permissions);
        Iterator<Permission> permIter = permissions.iterator();
        assertTrue(permIter.hasNext());
        Permission permission = permIter.next();
        assertEquals("Accounts", permission.getName());
        checkComment(Comments.PERMISSION_ON_ACCOUNTS_TABLE, permission.getComments(), EMPTY_STRING, 0);
        checkComment(Comments.RESOURCE_NAME_REFERENCED_BY_PERMISSION, permission.getComments(), RESOURCE_NAME, 0);
        checkComment(Comments.DENY_CREATE, permission.getComments(), ALLOW_CREATE, 0);
        checkComment(Comments.ALLOW_READ, permission.getComments(), ALLOW_READ, 0);
        checkComment(Comments.ALLOW_UPDATE, permission.getComments(), ALLOW_UPDATE, 0);

        permission = permIter.next();
        assertEquals("Accounts.Customer", permission.getName());

        CommentSets conditionComments = permission.getConditionComments();
        assertNotNull(conditionComments);
        checkComment(Comments.PERMISSION_USING_A_CONDITION, conditionComments, EMPTY_STRING, 0);

        permission = permIter.next();
        assertEquals("Accounts.Customer.SSN", permission.getName());
        CommentSets maskComments = permission.getMaskComments();
        assertNotNull(maskComments);
        checkComment(Comments.PERMISSION_MASK, maskComments, EMPTY_STRING, 0);
    }

    private void checkComment(Comments expected, Node commentNode) {
        assertTrue(commentNode.getNodeType() == Node.COMMENT_NODE);
        Comment comment = (Comment) commentNode;
        assertEquals(expected.toString(), comment.getTextContent());
    }

    private Node child(Node parent, String nodeType, String nodeName) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && nodeType.equals(child.getNodeName())) {
                if (nodeName == null)
                    return child;

                Element element = (Element) child;    
                if (nodeName.equals(element.getAttribute(NAME_ATTR)))
                    return child;
            }
        }

        Assert.fail();
        return null;
    }

    private Node nextSibling(Node node, String nodeType) {
        Node sibling = node.getNextSibling();
        while(sibling != null) {
            if (sibling instanceof Element && nodeType.equals(sibling.getNodeName()))
                return sibling;
            
            sibling = sibling.getNextSibling();
        }

        Assert.fail();
        return null;
    }

    private Node prevComment(Node node) {
        Node sibling = node.getPreviousSibling();
        while (sibling != null) {
            if(sibling.getNodeType() == Node.COMMENT_NODE)
                return sibling;

            sibling = sibling.getPreviousSibling();
        }

        Assert.fail();
        return null;
    }
   
    @Test
    public void testWriteDynamicVdbWithComments() throws Exception {
        MockFileBuilder portfolioXmlFile = VdbTestUtils.mockPortfolioVdbXmlFile();
        DynamicVdb vdb = new DynamicVdb(portfolioXmlFile.getResourceFile());

        StringWriter destination = new StringWriter();
        vdb.write(destination);

        Document vdbDoc = VdbTestUtils.readDocument(destination.toString(), false);
        assertNotNull(vdbDoc);

        System.out.println(VdbTestUtils.printDocument(vdbDoc));

        Element vdbElement = vdbDoc.getDocumentElement();
        Node commentNode = prevComment(vdbElement);
        checkComment(Comments.VDB_TITLE_2, commentNode);

        commentNode = prevComment(commentNode);
        checkComment(Comments.VDB_TITLE, commentNode);        

        Node elementNode = child(vdbElement, DESCRIPTION, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.VDB_DESCRIPTION, commentNode);

        elementNode = nextSibling(elementNode, PROPERTY);
        commentNode = prevComment(elementNode);
        checkComment(Comments.USE_CONNECTOR_PROPERTY, commentNode);

        elementNode = child(vdbElement, MODEL, "MarketData");
        Node modelNode = elementNode;
        commentNode = prevComment(modelNode);
        checkComment(Comments.MODEL_MARKETDATA, commentNode);

        Node sourceNode = child(modelNode, SOURCE, null);
        commentNode = prevComment(sourceNode);
        checkComment(Comments.MARKETDATA_TEXT_CONNECTOR, commentNode);

        modelNode = child(vdbElement, MODEL, "Accounts");
        elementNode = child(modelNode, PROPERTY, "importer.useFullSchemaName");
        commentNode = prevComment(elementNode);
        checkComment(Comments.ACCOUNTS_USEFULLSCHEMA_PROPERTY, commentNode);

        sourceNode = child(modelNode, SOURCE, null);
        commentNode = prevComment(sourceNode);
        checkComment(Comments.ACCOUNT_H2_CONNECTOR, commentNode);

        modelNode = child(vdbElement, MODEL, "PersonalValuations");
        elementNode = child(modelNode, PROPERTY, "importer.headerRowNumber");
        commentNode = prevComment(elementNode);
        checkComment(Comments.IMPORTER_HEADER_ROW_NUMBER_PROPERTY, commentNode);

        elementNode = child(modelNode, PROPERTY, "importer.ExcelFileName");
        commentNode = prevComment(elementNode);
        checkComment(Comments.IMPORT_EXCEL_FILE_NAME_PROPERTY, commentNode);

        elementNode = child(modelNode, METADATA, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.METADATA_ELEMENT, commentNode);

        elementNode = child(vdbElement, DATA_ROLE, null);
        Node dataRoleNode = elementNode;
        commentNode = prevComment(elementNode);
        checkComment(Comments.DATA_ROLE, commentNode);

        elementNode = child(dataRoleNode, DESCRIPTION, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.DATA_ROLE_DESCRIPTION, commentNode);

        elementNode = child(dataRoleNode, MAPPED_ROLE_NAME, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.SUPERVISOR_MAPPED_ROLE, commentNode);

        elementNode = nextSibling(elementNode, MAPPED_ROLE_NAME);
        commentNode = prevComment(elementNode);
        checkComment(Comments.DEPT_SUPER_MAPPED_ROLE, commentNode);

        elementNode = child(dataRoleNode, PERMISSION, null);
        Node permNode = elementNode;
        commentNode = prevComment(elementNode);
        checkComment(Comments.PERMISSION_ON_ACCOUNTS_TABLE, commentNode);

        elementNode = child(permNode, RESOURCE_NAME, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.RESOURCE_NAME_REFERENCED_BY_PERMISSION, commentNode);

        elementNode = nextSibling(elementNode, ALLOW_CREATE);
        commentNode = prevComment(elementNode);
        checkComment(Comments.DENY_CREATE, commentNode);

        elementNode = nextSibling(elementNode, ALLOW_READ);
        commentNode = prevComment(elementNode);
        checkComment(Comments.ALLOW_READ, commentNode);

        elementNode = nextSibling(elementNode, ALLOW_UPDATE);
        commentNode = prevComment(elementNode);
        checkComment(Comments.ALLOW_UPDATE, commentNode);

        permNode = nextSibling(permNode, PERMISSION);
        elementNode = child(permNode, CONDITION, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.PERMISSION_USING_A_CONDITION, commentNode);

        permNode = nextSibling(permNode, PERMISSION);
        elementNode = child(permNode, MASK, null);
        commentNode = prevComment(elementNode);
        checkComment(Comments.PERMISSION_MASK, commentNode);
    }
}

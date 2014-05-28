/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.storage.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jboss.ide.eclipse.as.storage.StorageUnitStream;
import org.junit.After;
import org.junit.Test;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.DefaultStorageProvider;
import org.teiid.designer.runtime.IServersProvider;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.TestServersProvider;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class TestDqpStorageUnit {

    private IServersProvider serversProvider = new TestServersProvider();
    private static final String TEST_DATA_DIR = SmartTestDesignerSuite.getTestDataPath(TestDqpStorageUnit.class);
    private static final String SERVER_TEST_REGISTRY_PATH = TEST_DATA_DIR + File.separator + "serverRegistry.xml";

    @SuppressWarnings( "unused" )
    private String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
        String lineSeparator = System.getProperty("line.separator");

        try {
            if (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
            }
            while (scanner.hasNextLine()) {
                fileContents.append(lineSeparator + scanner.nextLine());
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    @After
    public void afterEach() throws Exception {
        // Stop teiid server manager overwriting the test data
        ModelerCore.setTeiidServerManager(null);
    }

    private Document buildDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(file);
        doc.normalizeDocument();
        return doc;
    }

    /**
     * Tests whether a server manager collection of servers matches
     * the original file when written out to an export stream using the
     * {@link DqpStorageUnit}.
     *
     * @throws Exception
     */
    @Test
    public void testToExportStreams() throws Exception {
        ITeiidServerManager serverManager = new TeiidServerManager(TEST_DATA_DIR, serversProvider,
                                                                                                                       new DefaultStorageProvider());
        serverManager.restoreState();
        assertThat(serverManager.getServers().size(), is(2));
        ModelerCore.setTeiidServerManager(serverManager);

        DqpStorageUnit unit = new DqpStorageUnit();
        Set<StorageUnitStream> streams = unit.toExportStreams();
        assertEquals(1, streams.size());

        StorageUnitStream unitStream = streams.iterator().next();
        assertEquals(DqpStorageUnit.TEIID_INSTANCES_FILE_ID, unitStream.getId());

        InputStream stream = unitStream.getStream();
        assertNotNull(stream);

        /*
         * Write the stream to a test file
         */
        File testFile = File.createTempFile(TestDqpStorageUnit.class.getSimpleName(), ".test");
        testFile.deleteOnExit();

        FileUtils.writeStreamToFile(stream, testFile);

        Document testDoc = buildDocument(testFile);
        assertNotNull(testDoc);

        Element serversElem = testDoc.getDocumentElement();
        assertNotNull(serversElem);
        assertEquals(TeiidServerManager.SERVERS_TAG, serversElem.getNodeName());

        NodeList serversList = serversElem.getElementsByTagName(TeiidServerManager.SERVER_TAG);
        assertNotNull(serversList);
        assertEquals(2, serversList.getLength());

        for (int i = 0; i < serversList.getLength(); ++i) {
            Node serverNode = serversList.item(i);
            assertTrue(serverNode.hasAttributes());
            NamedNodeMap serverAttrs = serverNode.getAttributes();
            Node parentId = serverAttrs.getNamedItem(TeiidServerManager.PARENT_SERVER_ID);
            assertNotNull(parentId);

            if ("server1".equals(parentId.getTextContent())) {
                /*
                    <server host="localhost" customLabel="My Custom Label" default="true" version="8.1.2" parentServerId="server1">
                        <admin password="YWRtaW4=" port="31443" secure="true" user="admin"/>
                        <jdbc jdbcport="31000" jdbcsecure="false" jdbcuser="teiid"/>
                      </server>
                 */
                Node hostAttr = serverAttrs.getNamedItem(TeiidServerManager.HOST_ATTR);
                assertNotNull(hostAttr);
                assertEquals("localhost", hostAttr.getTextContent());
                Node labelAttr = serverAttrs.getNamedItem(TeiidServerManager.CUSTOM_LABEL_ATTR);
                assertNotNull(labelAttr);
                assertEquals("My Custom Label", labelAttr.getTextContent());
                Node defaultAttr = serverAttrs.getNamedItem(TeiidServerManager.DEFAULT_ATTR);
                assertNotNull(defaultAttr);
                assertEquals("true", defaultAttr.getTextContent());
                Node versionAttr = serverAttrs.getNamedItem(TeiidServerManager.SERVER_VERSION);
                assertNotNull(versionAttr);
                assertEquals("8.1.2", versionAttr.getTextContent());
              
            } else if ("server2".equals(parentId.getTextContent())) {
                /*
                    <server host="myserver.com" version="8.2.1" parentServerId="server2">
                        <admin port="31444" secure="false" user="admin2"/>
                        <jdbc jdbcpassword="dGVpaWQ=" jdbcport="31001" jdbcsecure="true" jdbcuser="teiid2"/>
                    </server>
              */
                Node hostAttr = serverAttrs.getNamedItem(TeiidServerManager.HOST_ATTR);
                assertNotNull(hostAttr);
                assertEquals("myserver.com", hostAttr.getTextContent());
                Node labelAttr = serverAttrs.getNamedItem(TeiidServerManager.CUSTOM_LABEL_ATTR);
                assertNull(labelAttr);
                Node defaultAttr = serverAttrs.getNamedItem(TeiidServerManager.DEFAULT_ATTR);
                assertNull(defaultAttr);
                Node versionAttr = serverAttrs.getNamedItem(TeiidServerManager.SERVER_VERSION);
                assertNotNull(versionAttr);
                assertEquals("8.2.1", versionAttr.getTextContent());

            } else {
                fail();
            }
        }
    }

    @Test
    public void testImportStream() throws Exception {
        ITeiidServerManager serverManager = new TeiidServerManager(TEST_DATA_DIR, serversProvider,
                                                                                                                       new DefaultStorageProvider());
        serverManager.restoreState();
        
        List<ITeiidServer> oldServers = new ArrayList<ITeiidServer>();
        oldServers.addAll(serverManager.getServers());
        for (ITeiidServer server : oldServers) {
            serverManager.removeServer(server);
        }
        assertThat(serverManager.getServers().size(), is(0));
        ModelerCore.setTeiidServerManager(serverManager);

        InputStream inStream = new FileInputStream(new File(SERVER_TEST_REGISTRY_PATH));
        StorageUnitStream stream = new StorageUnitStream(DqpStorageUnit.TEIID_INSTANCES_FILE_ID, inStream);
        
        DqpStorageUnit unit = new DqpStorageUnit();
        unit.importStream(Collections.singleton(stream));
        assertEquals(2, serverManager.getServers().size());

    }
}

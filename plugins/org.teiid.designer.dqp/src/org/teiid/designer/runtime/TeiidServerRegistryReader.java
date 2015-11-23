package org.teiid.designer.runtime;

import java.io.File;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.wst.server.core.IServer;
import org.teiid.core.designer.util.Base64;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.core.util.KeyInValueHashMap;
import org.teiid.designer.core.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TeiidServerRegistryReader implements TeiidServerRegistryConstants {
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    Document doc;
    Element root;
    
    ITeiidServer defaultServer;
    
    TeiidServerManager teiidServerManager;
    // secure storage provider needs to get passed in because getSecureStorageProvider() does a state check and when restoreServers() is called
    // The state will not be STARTED
    ISecureStorageProvider secureStorageProvider;
    
    boolean doDebug;
    
    /**
     * The provider used for accessing the collection of available {@link IServer}s
     * rather than relying on {@link parentServersProvider} directly, which makes unit testing difficult
     */
    private IServersProvider parentServersProvider;
    
    private final KeyInValueHashMap<String, ITeiidServer> teiidServers;
    
    
    
    private class TeiidServerKeyValueAdapter implements KeyFromValueAdapter<String, ITeiidServer> {

        @Override
        public String getKey(ITeiidServer value) {
            return value.getId();
        }
    }
    
    
	public TeiidServerRegistryReader(
			TeiidServerManager teiidServerManager,
			IServersProvider parentServersProvider,
			ISecureStorageProvider secureStorageProvider,
			boolean doDebug) {
		this.teiidServerManager = teiidServerManager;
		this.parentServersProvider = parentServersProvider;
		this.secureStorageProvider = secureStorageProvider;
		this.doDebug = doDebug;
		
		this.teiidServers = new KeyInValueHashMap<String, ITeiidServer>(new TeiidServerKeyValueAdapter());
	}
	
	public Collection<ITeiidServer> restoreServers() throws Exception {
		factory = DocumentBuilderFactory.newInstance();
        docBuilder = factory.newDocumentBuilder();
        doc = docBuilder.parse(new File(teiidServerManager.getStateFileName()));
        Node root = doc.getDocumentElement();
        NodeList servers = root.getChildNodes();

        for (int size = servers.getLength(), i = 0; i < size; ++i) {
            Node serverNode = servers.item(i);

            ITeiidServer teiidServer = loadServer(serverNode);
            if( teiidServer != null ) {
            	teiidServers.add(teiidServer);
            }
        }
        
        return teiidServers.values();
	}

	private ITeiidServer loadServer(Node serverNode) throws Exception {
        ITeiidAdminInfo teiidAdminInfo = null;
        ITeiidJdbcInfo teiidJdbcInfo = null;
        String jdbcOverridePort = null;
		// server attributes (host, custom label, default teiid instance)
        NamedNodeMap serverAttributeMap = serverNode.getAttributes();

        if (serverAttributeMap == null) {
            return null;
        }

        String host = null;
        String parentServerId = null;
        String customLabel = null;
        boolean previewServer = false;

        if( doDebug ) System.out.println(    "  <<< LOADING SERVER INFO");
        
        // version attribute
        ITeiidServerVersion teiidServerVersion = Version.TEIID_DEFAULT.get();
        Node versionNode = serverAttributeMap.getNamedItem(SERVER_VERSION);
        if (versionNode != null) teiidServerVersion = new TeiidServerVersion(versionNode.getNodeValue());
        attributeRead(SERVER_VERSION, teiidServerVersion.toString());

        // host attribute
        Node hostNode = serverAttributeMap.getNamedItem(HOST_ATTR);

        if (hostNode != null) {
            host = hostNode.getNodeValue();
            attributeRead(HOST_ATTR, host);
        }


        Node parentServerNode = serverAttributeMap.getNamedItem(PARENT_SERVER_ID);

        if (parentServerNode != null) {
            parentServerId = parentServerNode.getNodeValue();
            attributeRead(PARENT_SERVER_ID, parentServerId);
        }

        // custom label attribute
        Node customLabelNode = serverAttributeMap.getNamedItem(CUSTOM_LABEL_ATTR);

        if (customLabelNode != null) {
            customLabel = customLabelNode.getNodeValue();
            attributeRead(CUSTOM_LABEL_ATTR, customLabel);
        }
        // default teiid instance attribute
        Node defaultServerNode = serverAttributeMap.getNamedItem(DEFAULT_ATTR);

        if (defaultServerNode != null) {
            previewServer = Boolean.parseBoolean(defaultServerNode.getNodeValue());
            attributeRead(DEFAULT_ATTR, defaultServerNode.getNodeValue());
        }

        // Check for newer XML structure where server contains child nodes (admin & jdbc elements)
        NodeList connectionNodes = serverNode.getChildNodes();

        if (connectionNodes.getLength() > 0) {
            for (int connSize = connectionNodes.getLength(), j = 0; j < connSize; ++j) {
                Node connNode = connectionNodes.item(j);
                if (connNode.getNodeType() != Node.TEXT_NODE) {
                    if (connNode.getNodeName().equalsIgnoreCase(ADMIN_TAG)) {
                    	if( doDebug ) System.out.println("     <<<   ADMIN INFO");
                        NamedNodeMap attributeMap = connNode.getAttributes();
                        if (attributeMap == null) continue;

                        // if host is null than an older registry xml file is being used
                        if (host == null) {
                            Node adminHostNode = attributeMap.getNamedItem(HOST_ATTR);
                            assert (adminHostNode != null);
                            host = adminHostNode.getNodeValue();
                            attributeRead(HOST_ATTR, host);
                        }

                        // port must be non-null/not empty to be valid server
                        Node adminPortNode = attributeMap.getNamedItem(PORT_ATTR); // should always have one
                        assert (adminPortNode != null);
                        String adminPort = adminPortNode.getNodeValue();
                        attributeRead(PORT_ATTR, adminPort);

                        // username must be non-null/not empty to be valid server
                        Node userNode = attributeMap.getNamedItem(USER_ATTR); // should always have one
                        assert (userNode != null);
                        String adminUsername = userNode.getNodeValue();
                        attributeRead(USER_ATTR, adminUsername);
                        
                        Node passwordNode = attributeMap.getNamedItem(PASSWORD_ATTR);
                        String adminPassword = null;
                        if( passwordNode != null ) {
                        	adminPassword =  new String( Base64.decode(passwordNode.getNodeValue()), "UTF-8"); //$NON-NLS-1$
                        	attributeRead(PASSWORD_ATTR, adminPassword);
                        }
                        
                        Node adminSecureNode = attributeMap.getNamedItem(SECURE_ATTR);
                        String adminSecureStr = ((adminSecureNode == null) ? Boolean.FALSE.toString() : adminSecureNode.getNodeValue());
                        attributeRead(SECURE_ATTR, adminSecureStr);

                        teiidAdminInfo = new TeiidAdminInfo(host, adminPort, adminUsername, secureStorageProvider,
                                                            adminPassword, Boolean.parseBoolean(adminSecureStr));
                    } else if (connNode.getNodeName().equalsIgnoreCase(JDBC_TAG)) {
                    	if( doDebug ) System.out.println("     <<<   JDBC INFO");
                        NamedNodeMap attributeMap = connNode.getAttributes();
                        if (attributeMap == null) continue;

                        // if host is null than an older registry xml file is being used
                        if (host == null) {
                            Node jdbcHostNode = attributeMap.getNamedItem(JDBC_HOST_ATTR);
                            assert (jdbcHostNode != null);
                            host = jdbcHostNode.getNodeValue();
                            attributeRead(JDBC_HOST_ATTR, host);
                        }

                        // port must be non-null/not empty to be valid server
                        Node jdbcPortNode = attributeMap.getNamedItem(JDBC_PORT_ATTR);
                        assert (jdbcPortNode != null);
                        String jdbcPort = jdbcPortNode.getNodeValue();
                        
                        // port override must be non-null/not empty to be valid server
                        Node jdbcPortOverrideNode = attributeMap.getNamedItem(JDBC_PORT_OVERRIDE_ATTR);
                        if( jdbcPortOverrideNode != null ) {
                        	jdbcOverridePort = jdbcPortOverrideNode.getNodeValue();
                        	attributeRead(JDBC_PORT_OVERRIDE_ATTR, jdbcOverridePort);
                        }
                        

                        // username must be non-null/not empty to be valid server
                        Node jdbcUserNode = attributeMap.getNamedItem(JDBC_USER_ATTR);
                        assert (jdbcUserNode != null);
                        String jdbcUsername = jdbcUserNode.getNodeValue();
                        attributeRead(JDBC_USER_ATTR, jdbcUsername);

                        Node jdbcPasswordNode = attributeMap.getNamedItem(JDBC_PASSWORD_ATTR);
                        String jdbcPassword = null;
                        if( jdbcPasswordNode != null ) {
                        	jdbcPassword =  new String( Base64.decode(jdbcPasswordNode.getNodeValue()), "UTF-8"); //$NON-NLS-1$
                        	attributeRead(JDBC_PASSWORD_ATTR, jdbcPassword);
                        }
                        
                        Node jdbcSecureNode = attributeMap.getNamedItem(JDBC_SECURE_ATTR);
                        String jdbcSecureStr = ((jdbcSecureNode == null) ? Boolean.FALSE.toString() : jdbcSecureNode.getNodeValue());
                        attributeRead(JDBC_SECURE_ATTR, jdbcSecureStr);
                        teiidJdbcInfo = new TeiidJdbcInfo(host, jdbcPort, jdbcUsername, secureStorageProvider, jdbcPassword,
                                                          Boolean.parseBoolean(jdbcSecureStr));
                    }
                }
            }
        }

        // add server to registry
        IServer parentServer = null;
        try {
            parentServer = findParentServer(host, parentServerId, teiidAdminInfo);
        } catch (OrphanedTeiidServerException ex) {
            // Cannot add the Teiid Instance since it has no parent
            return null;
        }

        TeiidServerFactory teiidServerFactory = new TeiidServerFactory();
        ITeiidServer teiidServer = teiidServerFactory.createTeiidServer(teiidServerVersion,
        																host,
                                                                        teiidAdminInfo,
                                                                        teiidJdbcInfo,
                                                                        teiidServerManager,
                                                                        parentServer);
        teiidServer.setCustomLabel(customLabel);

       if( !StringUtilities.isEmpty(jdbcOverridePort) ) {
    	   teiidServerManager.getJdbcPortManager().setPort(teiidServer, Integer.parseInt(jdbcOverridePort), true);
       }

        if (previewServer) {
        	defaultServer = teiidServer;
        }
        
        return teiidServer;
	}

	
    private IServer findParentServer(String host, String parentServerId, ITeiidAdminInfo teiidAdminInfo) throws OrphanedTeiidServerException {
        IServer[] servers = parentServersProvider.getServers();
        for (IServer server : servers) {
            if (! host.equals(server.getHost()))
                continue;

            if (parentServerId != null && ! server.getId().equals(parentServerId)) {
                // Double checks against the parent server id only if a parent server id was
                // save. In the case of the old registry format, this was not possible so host
                // comparison is sufficient
                continue;
            }

            return server;
        }

        throw new OrphanedTeiidServerException(teiidAdminInfo);
    }
    
    private void attributeRead(String key, String value) {
    	if( doDebug) System.out.println("             Read Server Attribute = " + key + "  value = " + value);
    }

	public ITeiidServer getDefaultServer() {
		return defaultServer;
	}

}

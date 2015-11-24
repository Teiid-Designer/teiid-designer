package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;

import java.io.FileOutputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.Base64;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TeiidServerRegistryWriter implements TeiidServerRegistryConstants {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    Document doc;
    Element root;
    
    TeiidServerManager teiidServerManager;
    
    // Default server needs to get passed in because getDefaultServer() does a state check and when storeServers() is called
    // The state will not be STARTED
    ITeiidServer defaultServer;
    
    boolean doDebug;
    
	public TeiidServerRegistryWriter(
									TeiidServerManager teiidServerManager,
									ITeiidServer defaultServer,
									boolean doDebug) {
		super();
		
		this.teiidServerManager = teiidServerManager;
		this.defaultServer = defaultServer;
		this.doDebug = doDebug;
	}
	
    public void storeServers(Collection<ITeiidServer> teiidServers ) throws TransformerFactoryConfigurationError {

        try {
            docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.newDocument();

            // create root element
            root = doc.createElement(SERVERS_TAG);
            doc.appendChild(root);

            Collection<ITeiidServer> servers = teiidServers;
            for (ITeiidServer teiidServer : servers) {
                storeServer(teiidServer);
            }

            DOMSource source = new DOMSource(doc);
            StreamResult resultXML = new StreamResult(new FileOutputStream(teiidServerManager.getStateFileName()));
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$ 
            transformer.transform(source, resultXML);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                                        Util.getString("errorSavingServerRegistry", teiidServerManager.getStateFileName())); //$NON-NLS-1$
            Util.log(status);
        }
    }
    
    private void storeServer(ITeiidServer teiidServer) throws Exception {
        Element serverElement = doc.createElement(SERVER_TAG);
        root.appendChild(serverElement);
        
        if( doDebug ) System.out.println(    "  >>> SAVING SERVER INFO");
        
        { // Server Version
            setAttribute(serverElement, SERVER_VERSION, teiidServer.getServerVersion().toString());
        }
        { // Host
            setAttribute(serverElement, HOST_ATTR, teiidServer.getHost());
        }
            
        { // Parent Server Id
            setAttribute(serverElement, PARENT_SERVER_ID, teiidServer.getParent().getId());
        }
            
        { // CUSTOM LABEL
            if (!StringUtilities.isEmpty(teiidServer.getCustomLabel())) {
                setAttribute(serverElement, CUSTOM_LABEL_ATTR, teiidServer.getCustomLabel());
            }
        }
            
        storeTeiidAdminInfo(teiidServer, serverElement);
            
        storeTeiidJdbcInfo(teiidServer, serverElement);
            
        if ((defaultServer != null) && (defaultServer.equals(teiidServer))) {
            serverElement.setAttribute(DEFAULT_ATTR, Boolean.toString(true));
        }
    }
    
    private void storeTeiidAdminInfo(ITeiidServer teiidServer, Element serverElement) throws Exception {
        Element adminElement = doc.createElement(ADMIN_TAG);
        serverElement.appendChild(adminElement);
        
        if( doDebug ) System.out.println(    "          ADMIN INFO");
        
        setAttribute(adminElement, PORT_ATTR, teiidServer.getTeiidAdminInfo().getPort());
        setAttribute(adminElement, USER_ATTR, teiidServer.getTeiidAdminInfo().getUsername());


        /* The token of the password is saved to file while the password is saved in the eclipse secure storage
         * Saving the token ensures that its possible to find the password again.
         */
        String passToken = teiidServer.getTeiidAdminInfo().getPassToken();
        if (passToken != null) {
            setAttribute(adminElement, PASSWORD_ATTR, Base64.encodeBytes(passToken.getBytes("UTF-8"))); //$NON-NLS-1$
        }

        setAttribute(adminElement, SECURE_ATTR, Boolean.toString(teiidServer.getTeiidAdminInfo().isSecure()));
    }
    
    private void storeTeiidJdbcInfo(ITeiidServer teiidServer, Element serverElement) throws Exception {
        Element jdbcElement = doc.createElement(JDBC_TAG);
        serverElement.appendChild(jdbcElement);
        
        if( doDebug ) System.out.println(    "          JDBC INFO");
        
        // Check if actual port is cached, else set to default
        String actualPort = teiidServerManager.getJdbcPortManager().getPort(teiidServer, false);
        if( actualPort == null ) {
        	actualPort = ITeiidJdbcInfo.DEFAULT_PORT;
        }
        setAttribute(jdbcElement, JDBC_PORT_ATTR, actualPort);

        // check for port override
        String overridePort = teiidServerManager.getJdbcPortManager().getPort(teiidServer, true);
        if( overridePort != null ) {
        	setAttribute(jdbcElement, JDBC_PORT_OVERRIDE_ATTR, overridePort);
        }
        setAttribute(jdbcElement, JDBC_USER_ATTR, teiidServer.getTeiidJdbcInfo().getUsername());

            
        /* The token of the password is saved to file while the password is saved in the eclipse secure storage
         * Saving the token ensures that its possible to find the password again.
         */
        String passToken = teiidServer.getTeiidJdbcInfo().getPassToken();
        if (passToken != null)
            setAttribute(jdbcElement, JDBC_PASSWORD_ATTR, Base64.encodeBytes(passToken.getBytes("UTF-8"))); //$NON-NLS-1$

        setAttribute(jdbcElement, JDBC_SECURE_ATTR, Boolean.toString(teiidServer.getTeiidJdbcInfo().isSecure()));
    }
    
    private void setAttribute(Element element, String key, String value) {
    	element.setAttribute(key, value);
    	if( doDebug) System.out.println("             Set  Server Attribute = " + key + "  value = " + value);
    }

}

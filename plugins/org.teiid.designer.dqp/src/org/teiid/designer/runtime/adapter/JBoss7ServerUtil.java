/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.jboss.dmr.ModelNode;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.core.server.v7.management.AS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerUtil;
import org.jboss.ide.eclipse.as.management.core.ModelDescriptionConstants;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * @since 8.0
 */
public abstract class JBoss7ServerUtil extends JBossServerUtil {

    private static final String OP = ModelDescriptionConstants.OP;
    
    private static final String NAME = ModelDescriptionConstants.NAME;
    
    private static final String READ_ATTRIBUTE_OPERATION = ModelDescriptionConstants.READ_ATTRIBUTE_OPERATION;

    private static final String READ_CHILDREN_NAMES_OPERATION = ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION;

    private static final String SUBSYSTEM = ModelDescriptionConstants.SUBSYSTEM;

    private static final String CHILD_TYPE = ModelDescriptionConstants.CHILD_TYPE;

    private static final String SOCKET_BINDING_GROUP = ModelDescriptionConstants.SOCKET_BINDING_GROUP;

    private static final String SOCKET_BINDING = ModelDescriptionConstants.SOCKET_BINDING;

    private static final String OP_ADDR = ModelDescriptionConstants.OP_ADDR;

    private static final String PORT = ModelDescriptionConstants.PORT;
    
    /**
     * @param server
     * @param jboss7Server
     * @param request
     * @return
     * @throws Exception
     */
    private static ModelNode executeRequest(IServer parentServer, JBoss7Server jboss7Server, ModelNode request) throws Exception {
        String requestString = request.toJSONString(true);
        
        String resultString = JBoss7ManagerUtil.getService(parentServer).execute(new AS7ManagementDetails(parentServer), requestString);
        return ModelNode.fromJSONString(resultString);
    }
    
    /**
     * Determine whether the jboss 7 server is contactable by attempting
     * to talk to its management port
     * 
     * @param parentServer 
     * @param jboss7Server
     * 
     * @return true is server can be connected
     * @throws Exception
     */
    public static boolean isJBossServerConnected(IServer parentServer, JBoss7Server jboss7Server) throws Exception {
        if (! serverStarted(jboss7Server.getServer()))
            return false;
        
        String host = jboss7Server.getHost();
        int port = jboss7Server.getManagementPort();

        return isHostConnected(host, port);
    }
    
    /**
     * Determine whether the given server has teiid support
     * 
     * @param parentServer
     * @param jboss7Server
     * 
     * @return true is server has teiid support, false otherwise
     * @throws Exception
     */
    public static boolean isTeiidServer(IServer parentServer, JBoss7Server jboss7Server) throws Exception {
        if (! serverStarted(parentServer))
            return false;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        request.get(CHILD_TYPE).set(SUBSYSTEM);

        ModelNode result = executeRequest(parentServer, jboss7Server, request);

        List<ModelNode> subsystems = result.asList();
        for (ModelNode subsystem : subsystems) {
            if (subsystem.asString().equals("teiid")) { //$NON-NLS-1$
                return true;
            }
        }
        
        return false;
    }

    /**
     * Find Teiid's JDBC port, which should be stored in the jboss tree at
     * /socket-binding-group=standard-sockets/socket-binding=teiid-jdbc
     * 
     * @param parentServer
     * @param jboss7Server
     * @return the port number as a string
     * @throws Exception
     */
    public static String getJdbcPort(IServer parentServer, JBoss7Server jboss7Server) throws Exception {
        if (! serverStarted(parentServer))
            return ITeiidJdbcInfo.DEFAULT_PORT;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);   
        
        ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, "standard-sockets"); //$NON-NLS-1$
        address.add(SOCKET_BINDING, "teiid-jdbc"); //$NON-NLS-1$
        request.get(OP_ADDR).set(address);
        request.get(NAME).set(PORT);
        
        ModelNode result = executeRequest(parentServer, jboss7Server, request);
        return result.asString();
    }

    /**
     * @param parentServer
     * @param jboss7Server 
     * 
     * @return runtime version of the Teiid Instance
     * @throws Exception
     */
    public static ITeiidServerVersion getTeiidRuntimeVersion(IServer parentServer, JBoss7Server jboss7Server) throws Exception {
        if (! serverStarted(parentServer))
            return TeiidServerVersion.DEFAULT_TEIID_8_SERVER;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);   
        
        ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, "teiid"); //$NON-NLS-1$
        
        request.get(OP_ADDR).set(address);
        request.get(NAME).set("runtime-version"); //$NON-NLS-1$
        
        ModelNode result = executeRequest(parentServer, jboss7Server, request);
        return new TeiidServerVersion(result.asString());
    }    
}

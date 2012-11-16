/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.dmr.ModelNode;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.core.server.v7.management.AS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerUtil;
import org.jboss.ide.eclipse.as.management.core.ModelDescriptionConstants;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * @since 8.0
 */
public abstract class JBoss7ServerUtil extends ModelDescriptionConstants {

    /**
     * @param server
     * @param request
     * @return
     */
    private static ModelNode executeRequest(JBoss7Server jboss7Server, ModelNode request) throws Exception {
        String requestString = request.toJSONString(true);
        IServer server = jboss7Server.getServer();
        
        String resultString = JBoss7ManagerUtil.getService(server).execute(new AS7ManagementDetails(server), requestString);
        return ModelNode.fromJSONString(resultString);
    }
    
    /**
     * Test the given server for whether its been started
     * 
     * @param jboss7Server
     * 
     * @return true is server is in the started state
     */
    private static boolean serverStarted(JBoss7Server jboss7Server) {
        if (jboss7Server == null)
            return false;
        
        if (jboss7Server.getServer().getServerState() != IServer.STATE_STARTED)
            return false;
        
        return true;
    }
    
    /**
     * Determine whether the jboss 7 server is contactable by attempting
     * to talk to its management port
     * 
     * @param jboss7Server
     * 
     * @return true is server can be connected
     */
    public static boolean isJBossServerConnected(JBoss7Server jboss7Server) {
        if (! serverStarted(jboss7Server))
            return false;
        
        // Request that finds the name of the server
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);
        request.get(NAME).set(NAME);
        
        try {
            executeRequest(jboss7Server, request);
            return true;
        } catch (Exception ex) {
            // No need to log the exception
            return false;
        }
    }
    
    /**
     * Determine whether the given server has teiid support
     * 
     * @param jboss7Server
     * @return true is server has teiid support, false otherwise
     */
    public static boolean isTeiidServer(JBoss7Server jboss7Server) {
        if (! serverStarted(jboss7Server))
            return false;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        request.get(CHILD_TYPE).set(SUBSYSTEM);
        
        try {
            ModelNode result = executeRequest(jboss7Server, request);

            List<ModelNode> subsystems = result.asList();
            for (ModelNode subsystem : subsystems) {
                if (subsystem.asString().equals("teiid")) { //$NON-NLS-1$
                    return true;
                }
            }
        } catch (Exception ex) {
            // Failed to connect to the server
            DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("jbossServerConnectionFailureMessage", jboss7Server)); //$NON-NLS-1$
        }
        
        return false;
    }

    /**
     * Find Teiid's JDBC port, which should be stored in the jboss tree at
     * /socket-binding-group=standard-sockets/socket-binding=teiid-jdbc
     * 
     * @param jboss7Server
     * @return the port number as a string
     */
    public static String getJdbcPort(JBoss7Server jboss7Server) {
        if (! serverStarted(jboss7Server))
            return ITeiidJdbcInfo.DEFAULT_PORT;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);   
        
        ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, "standard-sockets"); //$NON-NLS-1$
        address.add(SOCKET_BINDING, "teiid-jdbc"); //$NON-NLS-1$
        request.get(OP_ADDR).set(address);
        request.get(NAME).set(PORT);
        
        try {
            ModelNode result = executeRequest(jboss7Server, request);
            return result.asString();
        } catch (Exception ex) {
            DqpPlugin.Util.log(IStatus.ERROR, ex, "Failed to get teiid jdbc port, defaulting to " + ITeiidJdbcInfo.DEFAULT_PORT); //$NON-NLS-1$
            return ITeiidJdbcInfo.DEFAULT_PORT;
        }
    }

    /**
     * @param jboss7Server 
     * 
     * @return runtime version of the teiid server
     */
    public static ITeiidServerVersion getTeiidRuntimeVersion(JBoss7Server jboss7Server) {
        if (! serverStarted(jboss7Server))
            return TeiidServerVersion.DEFAULT_TEIID_8_SERVER;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);   
        
        ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, "teiid"); //$NON-NLS-1$
        
        request.get(OP_ADDR).set(address);
        request.get(NAME).set("runtime-version"); //$NON-NLS-1$
        
        try {
            ModelNode result = executeRequest(jboss7Server, request);
            return new TeiidServerVersion(result.asString());
        } catch (Exception ex) {
            DqpPlugin.Util.log(IStatus.ERROR, ex, "Failed to get teiid jdbc port, defaulting to " + ITeiidJdbcInfo.DEFAULT_PORT); //$NON-NLS-1$
            return new TeiidServerVersion(ITeiidServerVersion.DEFAULT_TEIID_8_SERVER_ID);
        }
    }    
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.net.ConnectException;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.dmr.ModelNode;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.core.server.v7.management.AS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerUtil;
import org.jboss.ide.eclipse.as.management.core.ModelDescriptionConstants;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidJdbcInfo;

/**
 * @since 8.0
 */
public abstract class TeiidServerAdapterUtil extends ModelDescriptionConstants {

    /**
     * @param server
     * @param request
     * @return
     */
    private static ModelNode executeRequest(IServer server, ModelNode request) throws Exception {
        String requestString = request.toJSONString(true);
        
        String resultString = JBoss7ManagerUtil.getService(server).execute(new AS7ManagementDetails(server), requestString);
        return ModelNode.fromJSONString(resultString);
    }
    
    /**
     * Is the given {@link IServer} a jboss server
     * 
     * @param server
     * 
     * @return true if a jboss server, false otherwise
     */
    public static boolean isJBossServer(IServer server) {
        JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
        return jb7 != null;
    }
    
    /**
     * Determine whether the jboss server is contactable by attempting
     * to talk to its management port
     * 
     * @param server
     * 
     * @return true is server can be connected
     */
    public static boolean isJBossServerConnected(IServer server) {
        if (server.getServerState() != IServer.STATE_STARTED)
            return false;
        
        // Request that finds the name of the server
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);
        request.get(NAME).set(NAME);
        
        try {
            executeRequest(server, request);
            return true;
        } catch (Exception ex) {
            // No need to log the exception
            return false;
        }
    }
    
    /**
     * Determine whether the given server has teiid support
     * 
     * @param server
     * @return true is server has teiid support, false otherwise
     * 
     * @throws Exception
     */
    public static boolean isTeiidServer(IServer server) throws Exception {
        if (server.getServerState() != IServer.STATE_STARTED)
            return false;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        request.get(CHILD_TYPE).set(SUBSYSTEM);
        
        try {
            ModelNode result = executeRequest(server, request);

            List<ModelNode> subsystems = result.asList();
            for (ModelNode subsystem : subsystems) {
                if (subsystem.asString().equals("teiid")) { //$NON-NLS-1$
                    return true;
                }
            }
        } catch (ConnectException ex) {
            // Failed to connect to the server
            DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("jbossServerConnectionFailureMessage", server)); //$NON-NLS-1$
        }
        
        return false;
    }
    
    /**
     * Queries the jboss server's JDBC drivers for the given driver class. If installed,
     * the jboss name of the driver is returned.
     * 
     * @param server
     * @param requestDriverClass class name of driver
     * @return the driver name of the driver class being requested or null if
     *                  no driver was found
     * 
     * @throws Exception
     */
    public static String getJDBCDriver(IServer server, String requestDriverClass) throws Exception {
        CoreArgCheck.isNotNull(server);
        CoreArgCheck.isNotNull(requestDriverClass);
        
        if (server.getServerState() != IServer.STATE_STARTED)
            return null;
        
        ModelNode request = new ModelNode();
        request.get(OP).set("installed-drivers-list");  //$NON-NLS-1$
                
        ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, "datasources"); //$NON-NLS-1$
        request.get(OP_ADDR).set(address);
        
        try {
            ModelNode operationResult = executeRequest(server, request);

            List<ModelNode> driverList = operationResult.asList();
            for (ModelNode driver : driverList) {
                String driverClassName = driver.get("driver-class-name").asString(); //$NON-NLS-1$
                String driverName = driver.get("driver-name").asString(); //$NON-NLS-1$
                
                if (requestDriverClass.equalsIgnoreCase(driverClassName))
                    return driverName;
            }
            
        } catch (Exception ex) {
            // Failed to get mapping
            DqpPlugin.Util.log(IStatus.ERROR, ex, "Failed to get installed driver mappings "); //$NON-NLS-1$
        }
        
        return null;
    }

    /**
     * Find Teiid's JDBC port, which should be stored in the jboss tree at
     * /socket-binding-group=standard-sockets/socket-binding=teiid-jdbc
     * 
     * @param server
     * @return the port number as a string
     */
    public static String getJdbcPort(IServer server) {
        if (server.getServerState() != IServer.STATE_STARTED)
            return TeiidJdbcInfo.DEFAULT_PORT;
        
        ModelNode request = new ModelNode();
        request.get(OP).set(READ_ATTRIBUTE_OPERATION);   
        
        ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, "standard-sockets"); //$NON-NLS-1$
        address.add(SOCKET_BINDING, "teiid-jdbc"); //$NON-NLS-1$
        request.get(OP_ADDR).set(address);
        request.get(NAME).set(PORT);
        
        try {
            ModelNode result = executeRequest(server, request);
            return result.asString();
        } catch (Exception ex) {
            DqpPlugin.Util.log(IStatus.ERROR, ex, "Failed to get teiid jdbc port, defaulting to " + TeiidJdbcInfo.DEFAULT_PORT); //$NON-NLS-1$
            return TeiidJdbcInfo.DEFAULT_PORT;
        }
    }    
}

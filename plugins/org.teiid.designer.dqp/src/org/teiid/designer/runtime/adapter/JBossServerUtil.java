/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.teiid.designer.runtime.DebugConstants;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 *
 */
public class JBossServerUtil {

    /**
     * Determine whether the jboss server is contactable by attempting
     * to create a socket to its web port
     * 
     * @param parentServer
     * @param jbossServer
     * 
     * @return true if server can be connected
     * @throws Exception
     */
    public static boolean isJBossServerConnected(IServer parentServer, JBossServer jbossServer) throws Exception {
        if (!serverStarted(parentServer)) 
            return false;

        return isHostConnected(jbossServer.getHost(), jbossServer.getJBossWebPort());
    }

    /**
     * @param jbossServer
     * @return
     * @throws Exception
     */
    protected static boolean isHostConnected(String host, int port) throws Exception {
        Socket socket = null;
        Reader in  = null;
        InetSocketAddress endPoint = new InetSocketAddress(host, port);

        if (endPoint.isUnresolved()) {
            DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("jbossServerConnectionFailureMessage", endPoint)); //$NON-NLS-1$
            return false;
        }

        try {
            socket = new Socket();
            socket.connect(endPoint, 1024);
            return true;
        } catch (Exception ex) {
            if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.JBOSS_CONNECTION)) {
                /*
                 * Only need to log this with debug tracing turned on.
                 */
                DqpPlugin.Util.log(ex);
            }
            return false;
        } finally {
            try {
                if (in != null)
                    in.close();

                if (socket != null && socket.isConnected()) {
                    socket.close();
                    socket = null;
                }
            } catch (Exception ex2) {
                /*
                 * Unlikely event that socket did not close correctly.
                 */
                DqpPlugin.Util.log(ex2);
            }
        }
    }

    /**
     * Determine whether the given server has teiid support
     * 
     * @param parentServer
     * @param jbossServer
     * 
     * @return true is server has teiid support, false otherwise
     * @throws Exception
     */
    public static boolean isTeiidServer(IServer parentServer, JBossServer jbossServer) throws Exception {
        if (!serverStarted(parentServer)) 
            return false;

        //TODO can we try harder to determine this??
       
        return true;
    }

    /**
     * Test the given server for whether its been started
     * 
     * @param server
     * 
     * @return true is server is in the started state
     */
    protected static boolean serverStarted(IServer server) {
        if (server == null) 
            return false;

        if (server.getServerState() != IServer.STATE_STARTED) 
            return false;

        return true;
    }

    /**
     * @param parentServer
     * @param jbossServer
     *
     * @return since the jboss server is the older version 5, it cannot be queried
     *                 and can only be a maximum of version 7
     */
    public static ITeiidServerVersion getTeiidRuntimeVersion(IServer parentServer, JBossServer jbossServer) {
        return TeiidServerVersion.DEFAULT_TEIID_7_SERVER;
    }
}

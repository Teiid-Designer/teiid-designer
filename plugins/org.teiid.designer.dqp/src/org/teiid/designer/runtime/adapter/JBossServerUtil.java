/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;
import org.teiid.designer.runtime.DebugConstants;
import org.teiid.designer.runtime.DqpPlugin;

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

            /*
             * This may not seem necessary since a socket connection
             * should be enough. However, TEIIDDES-1971 has shown
             * that without actually using the socket, 'Connection reset
             * by peer' messages with be reported in the server log.
             */
            InputStream socketReader = socket.getInputStream();

            final char[] buffer = new char[100];
            in = new InputStreamReader(socketReader);
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz == -1) {
                if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.JBOSS_CONNECTION)) {
                    /*
                     * Only need to log this with debug tracing turned on.
                     */
                    String message = DqpPlugin.Util.getString("jbossServerConnectionStreamEmpty", host, port); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, message);
                    DqpPlugin.Util.log(status);
                }
                return false;
            }

            StringBuffer output = new StringBuffer();
            for (int i = 0; i < buffer.length; ++i) {
                if (Character.isLetterOrDigit(buffer[i])) {
                    output.append(buffer[i]);
                }
            }

            if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.JBOSS_CONNECTION)) {
                /*
                 * Only need to log this with debug tracing turned on.
                 */
                String message = DqpPlugin.Util.getString("jbossServerHeartBeat", host, port, output); //$NON-NLS-1$
                IStatus status = new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, message);
                DqpPlugin.Util.log(status);
            }

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
}

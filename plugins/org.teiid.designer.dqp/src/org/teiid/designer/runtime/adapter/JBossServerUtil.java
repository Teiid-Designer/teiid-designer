/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.io.IOException;
import java.net.Socket;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.JBossServer;

/**
 *
 */
public class JBossServerUtil {

    /**
     * Determine whether the jboss server is contactable by attempting
     * to create a socket to its web port
     * 
     * @param jbossServer
     * 
     * @return true if server can be connected
     */
    public static boolean isJBossServerConnected(JBossServer jbossServer) {
        if (!serverStarted(jbossServer)) 
            return false;

        Socket socket = null;
        try {
            socket = new Socket(jbossServer.getHost(), jbossServer.getJBossWebPort());
            return true;
        } catch (Exception ex) {
            return false;
            
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Determine whether the given server has teiid support
     * 
     * @param jbossServer
     * 
     * @return true is server has teiid support, false otherwise
     */
    public static boolean isTeiidServer(JBossServer jbossServer) {
        if (!serverStarted(jbossServer)) 
            return false;

        //TODO can we try harder to determine this??
       
        return true;
    }

    /**
     * Test the given server for whether its been started
     * 
     * @param jbossServer
     * 
     * @return true is server is in the started state
     */
    protected static boolean serverStarted(JBossServer jbossServer) {
        if (jbossServer == null) 
            return false;

        if (jbossServer.getServer().getServerState() != IServer.STATE_STARTED) 
            return false;

        return true;
    }
}

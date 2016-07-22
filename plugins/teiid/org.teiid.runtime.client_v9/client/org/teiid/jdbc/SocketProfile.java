/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.jdbc;

import java.sql.SQLException;
import java.util.Properties;
import org.teiid.net.TeiidURL;
import org.teiid.net.socket.OioOjbectChannelFactory;
import org.teiid.net.socket.SocketServerConnection;
import org.teiid.net.socket.SocketServerConnectionFactory;
import org.teiid.runtime.client.TeiidClientException;


/**
 * <p> The java.sql.DriverManager class uses this class to connect to Teiid Server.
 * The TeiidDriver class has a static initializer, which
 * is used to instantiate and register itself with java.sql.DriverManager. The
 * DriverManager's <code>getConnection</code> method calls <code>connect</code>
 * method on available registered drivers. </p>
 */

final class SocketProfile implements ConnectionProfile {

    @Override
    public ConnectionImpl connect(String url, Properties info) throws SQLException {
        int loginTimeoutSeconds = 0;
        SocketServerConnection serverConn;
        try {
            String timeout = info.getProperty(TeiidURL.CONNECTION.LOGIN_TIMEOUT);
            if (timeout != null) {
                loginTimeoutSeconds = Integer.parseInt(timeout);
            }
            
            if (loginTimeoutSeconds > 0) {
                OioOjbectChannelFactory.TIMEOUTS.set(System.currentTimeMillis() + loginTimeoutSeconds * 1000);
            }
			serverConn = SocketServerConnectionFactory.getInstance().getConnection(info);
		} catch (TeiidClientException e) {
			throw new SQLException(e);
		} finally {
            if (loginTimeoutSeconds > 0) {
                OioOjbectChannelFactory.TIMEOUTS.set(null);
            }
		}

        // construct a MMConnection object.
        ConnectionImpl connection = new ConnectionImpl(serverConn, info, url);
        return connection;
    }

}

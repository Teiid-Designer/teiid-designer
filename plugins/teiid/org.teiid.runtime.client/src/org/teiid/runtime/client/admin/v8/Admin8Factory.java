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

package org.teiid.runtime.client.admin.v8;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.Session;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.admin.AbstractAdminImpl;
import org.teiid.runtime.client.admin.ExecutionAdmin;



/**
 * Singleton factory for class for creating Admin connections to the Teiid
 * 
 * Note that this class was broken up into a number of components to allow clearer management of :
 * 
 * 	- ConnectionManager.java
 *  	- Data Sources (JDBC)
 *  		- Keeps cache of currently deployed data source configurations
 *  		- refreshes on changes to deployed data sources
 *  		- CLASS = DataSourceCache.java
 *  
 *  	- Resource Adapters
 *  		- Keeps cache of currently deployed resource adapter configurations
 *  		- refreshes on changes to deployed data sources
 *  		- CLASS = resourceAdapterCache.java
 *  
 *  	- Teiid Translators
 *  		- caches the configured Teiid translators and their property templates
 *  		- CLASS = TranslatorCache.java 
 *  
 *  - Admin connection utilities (cli calls and anything dealing with ModelControllerClient
 *  	- AdminConnectionManager.java
 * 
 */
@SuppressWarnings("nls")
public class Admin8Factory {
	private static final Logger LOGGER = Logger.getLogger(Admin8Factory.class.getName());

    private static Admin8Factory instance;

	/**
	 * @return singleton instance
	 */
	public static Admin8Factory getInstance() {
	    if (instance == null)
	        instance = new Admin8Factory();

	    return instance;
	}

	private Admin8Factory() {}

    /**
     * Creates a ServerAdmin with the specified connection properties.
     * @param teiidVersion
     * @param host 
     * @param port 
     * @param userName
     * @param password
     * @return new Admin connection
     * @throws AdminException
     */
    public Admin createAdmin(ITeiidServerVersion teiidVersion, String host, int port, String userName, char[] password) throws AdminException {
        if(host == null) {
            host = "localhost"; //$NON-NLS-1$
        }

        if(port < 0) {
        	if( teiidVersion.isGreaterThan(Version.TEIID_8_12_4)) {
        		port = 9990;
        	} else {
        		port = 9999;
        	}
        }

        try {
            CallbackHandler cbh = new AuthenticationCallbackHandler(userName, password);
            ModelControllerClient newClient = ModelControllerClient.Factory.create(host, port, cbh);

            List<String> nodeTypes = Util.getNodeTypes(newClient, new DefaultOperationRequestAddress());
            if (!nodeTypes.isEmpty()) {
                boolean domainMode = nodeTypes.contains("server-group"); //$NON-NLS-1$
                LOGGER.info("Connected to " //$NON-NLS-1$
                        + (domainMode ? "domain controller at " : "standalone controller at ") //$NON-NLS-1$ //$NON-NLS-2$
                        + host + ":" + port); //$NON-NLS-1$
                return new AdminImpl(teiidVersion, newClient);
            }
            LOGGER.info(Messages.gs(Messages.TEIID.TEIID70051, host, port)); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception e) {
        	 throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70000, host, e.getLocalizedMessage()));
        }
        return null;
    }

    private class AuthenticationCallbackHandler implements CallbackHandler {
        private boolean realmShown = false;
        private String userName = null;
        private char[] password = null;

        public AuthenticationCallbackHandler(String user, char[] password) {
        	this.userName = user;
        	this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
            // Special case for anonymous authentication to avoid prompting user for their name.
            if (callbacks.length == 1 && callbacks[0] instanceof NameCallback) {
                ((NameCallback)callbacks[0]).setName("anonymous CLI user"); //$NON-NLS-1$
                return;
            }

            for (Callback current : callbacks) {
                if (current instanceof RealmCallback) {
                    RealmCallback rcb = (RealmCallback) current;
                    String defaultText = rcb.getDefaultText();
                    rcb.setText(defaultText); // For now just use the realm suggested.
                    if (this.realmShown == false) {
                        this.realmShown = true;
                    }
                } else if (current instanceof RealmChoiceCallback) {
                    throw new UnsupportedCallbackException(current, "Realm choice not currently supported."); //$NON-NLS-1$
                } else if (current instanceof NameCallback) {
                    NameCallback ncb = (NameCallback) current;
                    ncb.setName(this.userName);
                } else if (current instanceof PasswordCallback) {
                    PasswordCallback pcb = (PasswordCallback) current;
                    pcb.setPassword(this.password);
                } else {
                    throw new UnsupportedCallbackException(current);
                }
            }
        }

    }

    /**
     * Implemetation of Admin interface
     */
	@SuppressWarnings( "unused" )
    public class AdminImpl extends AbstractAdminImpl {
	    private static final long CACHE_TIME = 5*1000;
    	private static final String CLASS_NAME = "class-name";
		private static final String JAVA_CONTEXT = "java:/";
		private final ITeiidServerVersion teiidVersion;
		private ModelControllerClient connection;
    	private boolean domainMode = false;
    	private String profileName = "ha";

        /**
         * @param teiidVersion
         * @param connection
         */
        public AdminImpl(ITeiidServerVersion teiidVersion, ModelControllerClient connection) {
            this.teiidVersion = teiidVersion;
            this.connection = connection;
            List<String> nodeTypes = Util.getNodeTypes(connection, new DefaultOperationRequestAddress());
            if (!nodeTypes.isEmpty()) {
                this.domainMode = nodeTypes.contains("server-group"); //$NON-NLS-1$
            }
        }

        /**
         * @return the teiidVersion
         */
        public ITeiidServerVersion getTeiidVersion() {
            return this.teiidVersion;
        }
        
        public ModelControllerClient getConnection() {
        	return this.connection;
        }

		/**
		 * @param name
		 */
		public void setProfileName(String name) {
			this.profileName = name;
		}

		@Override
		public void close() {
			if (this.connection != null) {
		        try {
		        	this.connection.close();
		        } catch (Throwable t) {
		        	//ignore
		        }
		        this.connection = null;
				this.domainMode = false;
			}
		}

		private Collection<String> executeList(final ModelNode request)	throws AdminException {
			try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	            	return Util.getList(outcome);
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
	        return Collections.emptyList();
		}

		@Override
		public Collection<? extends Session> getSessions() throws AdminException {
			return ExecutionAdmin.getAdminConnectionManager().getSessions();
		}

        /**
         * Flushes the caches of the admin connection
         */
        public void flush() {

        }
    }
}

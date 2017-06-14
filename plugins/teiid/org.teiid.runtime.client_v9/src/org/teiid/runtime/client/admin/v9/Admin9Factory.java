/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.admin.v9;

import static org.jboss.as.controller.client.helpers.ClientConstants.DEPLOYMENT_REMOVE_OPERATION;
import static org.jboss.as.controller.client.helpers.ClientConstants.DEPLOYMENT_UNDEPLOY_OPERATION;
import static org.jboss.as.controller.client.helpers.ClientConstants.RESULT;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestAddress;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.CacheStatistics;
import org.teiid.adminapi.DomainAware;
import org.teiid.adminapi.EngineStatistics;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Request;
import org.teiid.adminapi.Session;
import org.teiid.adminapi.Transaction;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.WorkerPoolStatistics;
import org.teiid.adminapi.impl.AdminObjectImpl;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.jboss.MetadataMapper;
import org.teiid.adminapi.jboss.VDBMetadataMapper;
import org.teiid.adminapi.jboss.VDBMetadataMapper.RequestMetadataMapper;
//import org.teiid.adminapi.jboss.VDBMetadataMapper.RequestMetadataMapper;
import org.teiid.adminapi.jboss.VDBMetadataMapper.SessionMetadataMapper;
import org.teiid.adminapi.jboss.VDBMetadataMapper.TransactionMetadataMapper;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;


/**
 * Singleton factory for class for creating Admin connections to the Teiid
 */
@SuppressWarnings("nls")
public class Admin9Factory {
	private static final Logger LOGGER = Logger.getLogger(Admin9Factory.class.getName());

    private static Admin9Factory instance;

	/**
	 * @return singleton instance
	 */
	public static Admin9Factory getInstance() {
	    if (instance == null)
	        instance = new Admin9Factory();

	    return instance;
	}

	private Admin9Factory() {}

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
        	port = 9990;
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

//	private class ResultCallback {
//		@SuppressWarnings("unused")
//		void onSuccess(ModelNode outcome, ModelNode result) throws AdminException {
//		    // Nothinq required
//		}
//		void onFailure(String msg) throws AdminProcessingException {
//			throw new AdminProcessingException(msg);
//		}
//	}

    /**
     * Implementation of Admin interface
     */
	@SuppressWarnings( "unused" )
    public class AdminImpl implements Admin{
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
        
        @Override
		public void refresh() {
//        	flush();
		}

		private void requires(String item, Version requiredVersion) throws AdminException {
            if (getTeiidVersion().isLessThan(requiredVersion))
                throw new AdminComponentException(Messages.getString(Messages.Misc.TeiidVersionFailure, item, getTeiidVersion()));
        }

		/**
		 * @param name
		 */
		public void setProfileName(String name) {
			this.profileName = name;
		}

		@Override
		public void clearCache(String cacheType) throws AdminException {
			cliCall("clear-cache",
					new String[] { "subsystem", "teiid" },
					new String[] { "cache-type", cacheType},
					new ResultCallback());
		}
		
		@Deprecated
		@Override
		public void clearCache(String cacheType, String vdbName, int vdbVersion) throws AdminException {
			cliCall("clear-cache",
					new String[] { "subsystem", "teiid" },
					new String[] { "cache-type", cacheType, "vdb-name",vdbName, "vdb-version", String.valueOf(vdbVersion) },
					new ResultCallback());
		}
		
		@Override
		public void clearCache(String cacheType, String vdbName, String vdbVersion) throws AdminException {
			cliCall("clear-cache",
					new String[] { "subsystem", "teiid" },
					new String[] { "cache-type", cacheType, "vdb-name",vdbName, "vdb-version", String.valueOf(vdbVersion) },
					new ResultCallback());
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

		private void addProfileNode(DefaultOperationRequestBuilder builder) throws AdminException {
			if (this.domainMode) {
				String profile = getProfileName();
				if (profile != null) {
					builder.addNode("profile",profile);
				}
			}
		}

		/**
		 * @return profile name
		 * @throws AdminException
		 */
		public String getProfileName() throws AdminException {
			if (!this.domainMode) {
				return null;
			}
			if (this.profileName == null) {
				this.profileName = getChildNodeNames(null, "profile").get(0);
			}
			return this.profileName;
		}

		@Override
		public void createDataSource(String deploymentName,	String templateName, Properties properties)	throws AdminException {
			// NOOP
		}

		// /subsystem=datasources/data-source=DS/connection-properties=foo:add(value=/home/rareddy/testing)
		private void addConnectionProperty(String deploymentName, String key, String value) throws AdminException {
			if (value == null || value.trim().isEmpty()) {
				throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70054, key));
			}
			cliCall("add", new String[] { "subsystem", "datasources",
					"data-source", deploymentName,
					"connection-properties", key },
					new String[] {"value", value }, new ResultCallback());
		}

		private void execute(final ModelNode request) throws AdminException {
			try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	                 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public void deleteDataSource(String deployedName) throws AdminException {
			// NOOP
		}

		private String removeJavaContext(String deployedName) {
			if (deployedName.startsWith(JAVA_CONTEXT)) {
				deployedName = deployedName.substring(6);
			}
			return deployedName;
		}

		private String addJavaContext(String deployedName) {
			if (!deployedName.startsWith(JAVA_CONTEXT)) {
				deployedName = JAVA_CONTEXT+deployedName;
			}
			return deployedName;
		}

//		private boolean deleteDS(String deployedName, String... subsystem) throws AdminException {
//			DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
//	        final ModelNode request;
//
//	        try {
//	        	addProfileNode(builder);
//
//	            builder.addNode("subsystem", subsystem[0]); //$NON-NLS-1$
//	            builder.addNode(subsystem[1], deployedName);
//	            builder.setOperationName("remove");
//	            request = builder.buildRequest();
//	        } catch (OperationFormatException e) {
//	            throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
//	        }
//
//	        try {
//	            ModelNode outcome = this.connection.execute(request);
//	            if (!Util.isSuccess(outcome)) {
//	                return false;
//	            }
//	        } catch (IOException e) {
//	        	 throw new AdminComponentException(e);
//	        }
//	        return true;
//		}

		@Override
		public void undeploy(String deployedName) throws AdminException {
			ModelNode request;
			try {
				request = buildUndeployRequest(deployedName, false);
	        } catch (OperationFormatException e) {
	        	throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
	        }
			execute(request);
		}

		/**
		 * @param deployedName
		 * @param force
		 * @throws AdminException
		 */
		public void undeploy(String deployedName, boolean force) throws AdminException {
			ModelNode request;
			try {
				request = buildUndeployRequest(deployedName, force);
	        } catch (OperationFormatException e) {
	        	throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
	        }
			execute(request);
		}

	    /**
	     * @param name
	     * @param force
	     * @return undeploy request
	     * @throws OperationFormatException
	     */
	    public ModelNode buildUndeployRequest(String name, boolean force) throws OperationFormatException {
	        ModelNode composite = new ModelNode();
	        composite.get("operation").set("composite");
	        composite.get("address").setEmptyList();
	        ModelNode steps = composite.get("steps");

	        DefaultOperationRequestBuilder builder;

	        if(this.domainMode) {
            	final List<String> serverGroups = Util.getServerGroups(this.connection);

                for (String group : serverGroups){
                    ModelNode groupStep = Util.configureDeploymentOperation(DEPLOYMENT_UNDEPLOY_OPERATION, name, group);
                    steps.add(groupStep);
                }

                for (String group : serverGroups) {
                    ModelNode groupStep = Util.configureDeploymentOperation(DEPLOYMENT_REMOVE_OPERATION, name, group);
                    steps.add(groupStep);
                }
	        } else if(Util.isDeployedAndEnabledInStandalone(name, this.connection)||force) {
	            builder = new DefaultOperationRequestBuilder();
	            builder.setOperationName("undeploy");
	            builder.addNode("deployment", name);
	            steps.add(builder.buildRequest());
	        }

	        // remove content
            builder = new DefaultOperationRequestBuilder();
            builder.setOperationName("remove");
            builder.addNode("deployment", name);
            steps.add(builder.buildRequest());

	        return composite;
	    }

		@Override
		public void deploy(String deployName, InputStream vdb)	throws AdminException {
			ModelNode request = buildDeployVDBRequest(deployName, vdb, true);
			execute(request);
		}

		/**
		 * @param deployName
		 * @param vdb
		 * @param persist
		 * @throws AdminException
		 */
		public void deploy(String deployName, InputStream vdb, boolean persist)	throws AdminException {
			ModelNode request = buildDeployVDBRequest(deployName, vdb, persist);
			execute(request);
		}

		private ModelNode buildDeployVDBRequest(String fileName, InputStream vdb, boolean persist) throws AdminException {
            try {
				if (Util.isDeploymentInRepository(fileName, this.connection)){
	                // replace
					DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	                builder = new DefaultOperationRequestBuilder();
	                builder.setOperationName("full-replace-deployment");
	                builder.addProperty("name", fileName);
	                byte[] bytes = ObjectConverterUtil.convertToByteArray(vdb);
	                builder.getModelNode().get("content").get(0).get("bytes").set(bytes);
	                return builder.buildRequest();
				}

				//add
		        ModelNode composite = new ModelNode();
		        composite.get("operation").set("composite");
		        composite.get("address").setEmptyList();
		        ModelNode steps = composite.get("steps");

				DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	            builder.setOperationName("add");
	            builder.addNode("deployment", fileName);


				byte[] bytes = ObjectConverterUtil.convertToByteArray(vdb);
				builder.getModelNode().get("content").get(0).get("bytes").set(bytes);
				ModelNode request = builder.buildRequest();
	            if (!persist) {
	            	request.get("persistent").set(false); // prevents writing this deployment out to standalone.xml
	            }
	            request.get("enabled").set(true);
				steps.add(request);

	            // deploy
	            if (this.domainMode) {
	            	List<String> serverGroups = Util.getServerGroups(this.connection);
	                for (String serverGroup : serverGroups) {
	                    steps.add(Util.configureDeploymentOperation("add", fileName, serverGroup));
	                }
	                for (String serverGroup : serverGroups) {
	                    steps.add(Util.configureDeploymentOperation("deploy", fileName, serverGroup));
	                }
	            } else {
	                builder = new DefaultOperationRequestBuilder();
	                builder.setOperationName("deploy");
	                builder.addNode("deployment", fileName);
					request = builder.buildRequest();
		            if (!persist) {
		            	request.get("persistent").set(false); // prevents writing this deployment out to standalone.xml
		            }
		            request.get("enabled").set(true);
	                steps.add(request);
	            }
	            return composite;
			} catch (OperationFormatException e) {
				throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
			} catch (IOException e) {
				 throw new AdminComponentException(e);
			}
		}

		@Override
		public Collection<? extends CacheStatistics> getCacheStats(String cacheType) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "cache-statistics",	"cache-type", cacheType);//$NON-NLS-1$ //$NON-NLS-2$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	            	if (this.domainMode) {
	            		return getDomainAwareList(outcome, VDBMetadataMapper.CacheStatisticsMetadataMapper.INSTANCE);
	            	}
	            	if (outcome.hasDefined("result")) {
	            		ModelNode result = outcome.get("result");
	            		return Arrays.asList(VDBMetadataMapper.CacheStatisticsMetadataMapper.INSTANCE.unwrap(getTeiidVersion(), result));
	            	}
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
	        return null;
		}

		@Override
		public Collection<? extends EngineStatistics> getEngineStats() throws AdminException {
	        final ModelNode request = buildRequest("teiid", "engine-statistics");//$NON-NLS-1$ //$NON-NLS-2$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	            	if (this.domainMode) {
	            		return getDomainAwareList(outcome, VDBMetadataMapper.EngineStatisticsMetadataMapper.INSTANCE);
	            	}
	            	if (outcome.hasDefined("result")) {
	            		ModelNode result = outcome.get("result");
	            		return Arrays.asList(VDBMetadataMapper.EngineStatisticsMetadataMapper.INSTANCE.unwrap(getTeiidVersion(), result));
	            	}
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
	        return null;
		}

		@Override
		public Collection<String> getCacheTypes() throws AdminException {
	        final ModelNode request = buildRequest("teiid", "cache-types");//$NON-NLS-1$ //$NON-NLS-2$
	        return new HashSet<String>(executeList(request));
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

		public List<String> getChildNodeNames(String subsystem, String childNode) throws AdminException {
	        final ModelNode request = buildRequest(subsystem, "read-children-names", "child-type", childNode);//$NON-NLS-1$
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
		
		/**
		 * /subsystem=datasources:read-children-names(child-type=data-source)
		 * /subsystem=resource-adapters/resource-adapter={rar-file}:read-resource
		 * @see org.teiid.adminapi.Admin#getDataSourceNames()
		 */
		@Override
		public Collection<String> getDataSourceNames() throws AdminException {
			return new ArrayList<String>();
        }
		
		@Override
		public Properties getDataSource(String deployedName) throws AdminException {
			return null;
		}
		

		/**
		 * @return deployments
		 */
		public List<String> getDeployments(){
			return Util.getDeployments(this.connection);
		}

		@Override
		public Set<String> getDataSourceTemplateNames() throws AdminException {
			Set<String> templates = new HashSet<String>();
			return templates;
		}

		@Override
		public Collection<? extends WorkerPoolStatistics> getWorkerPoolStats() throws AdminException {
			final ModelNode request = buildRequest("teiid", "workerpool-statistics");//$NON-NLS-1$
			if (request != null) {
		        try {
		            ModelNode outcome = this.connection.execute(request);
		            if (Util.isSuccess(outcome)) {
		            	if (this.domainMode) {
		            		return getDomainAwareList(outcome, VDBMetadataMapper.WorkerPoolStatisticsMetadataMapper.INSTANCE);
		            	}
		            	if (outcome.hasDefined("result")) {
		            		ModelNode result = outcome.get("result");
		            		return Arrays.asList(VDBMetadataMapper.WorkerPoolStatisticsMetadataMapper.INSTANCE.unwrap(getTeiidVersion(), result));
		            	}
		            }
		        } catch (IOException e) {
		        	 throw new AdminComponentException(e);
		        }
			}
	        return null;
		}


		@Override
		public void cancelRequest(String sessionId, long executionId) throws AdminException {
			final ModelNode request = buildRequest("teiid", "terminate-session", "session", sessionId, "execution-id", String.valueOf(executionId));//$NON-NLS-1$
			if (request == null) {
				return;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public Collection<? extends Request> getRequests() throws AdminException {
			final ModelNode request = buildRequest("teiid", "list-requests");//$NON-NLS-1$
			if (request != null) {
		        try {
		            ModelNode outcome = this.connection.execute(request);
		            if (Util.isSuccess(outcome)) {
		                return getDomainAwareList(outcome, RequestMetadataMapper.INSTANCE);
		            }

		        } catch (IOException e) {
		        	 throw new AdminComponentException(e);
		        }
			}
	        return Collections.emptyList();
		}

		@Override
		public Collection<? extends Request> getRequestsForSession(String sessionId) throws AdminException {
			final ModelNode request = buildRequest("teiid", "list-requests-per-session", "session", sessionId);//$NON-NLS-1$
			if (request != null) {
		        try {
		            ModelNode outcome = this.connection.execute(request);
		            if (Util.isSuccess(outcome)) {
		                return getDomainAwareList(outcome, RequestMetadataMapper.INSTANCE);
		            }
		        } catch (IOException e) {
		        	 throw new AdminComponentException(e);
		        }
			}
	        return Collections.emptyList();
		}

		@Override
		public Collection<? extends Session> getSessions() throws AdminException {
			final ModelNode request = buildRequest("teiid", "list-sessions");//$NON-NLS-1$
			if (request != null) {
		        try {
		            ModelNode outcome = this.connection.execute(request);
		            if (Util.isSuccess(outcome)) {
		                return getDomainAwareList(outcome, SessionMetadataMapper.INSTANCE);
		            }
		        } catch (IOException e) {
		        	 throw new AdminComponentException(e);
		        }
			}
	        return Collections.emptyList();
		}

		/**
		 * pattern on CLI
		 * /subsystem=datasources/data-source=foo:read-resource-description
		 */
		@Override
		public Collection<PropertyDefinition> getTemplatePropertyDefinitions(String templateName) throws AdminException {
			// NOOP
			return null;
		}

        @Deprecated
        @Override
	    public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName) throws AdminException{
			// NOOP
			return null;
		}
		
		@Override
        public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName, TranlatorPropertyType type) throws AdminException {
			// NOOP
			return null;
		}

		@Override
		public Collection<? extends Transaction> getTransactions() throws AdminException {
			final ModelNode request = buildRequest("teiid", "list-transactions");//$NON-NLS-1$
			if (request != null) {
		        try {
		            ModelNode outcome = this.connection.execute(request);
		            if (Util.isSuccess(outcome)) {
		                return getDomainAwareList(outcome, TransactionMetadataMapper.INSTANCE);
		            }
		        } catch (IOException e) {
		        	 throw new AdminComponentException(e);
		        }
			}
	        return Collections.emptyList();
		}

		@Override
		public void terminateSession(String sessionId) throws AdminException {
			final ModelNode request = buildRequest("teiid", "terminate-session", "session", sessionId);//$NON-NLS-1$
			if (request == null) {
				return;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public void terminateTransaction(String transactionId) throws AdminException {
			final ModelNode request = buildRequest("teiid", "terminate-transaction", "xid", transactionId);//$NON-NLS-1$
			if (request == null) {
				return;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public Translator getTranslator(String deployedName) throws AdminException {
			// NOOP
			return null;
		}

		@Override
		public Collection<? extends Translator> getTranslators() throws AdminException {
			// NOOP
			return Collections.emptyList();
		}
		private ModelNode buildRequest(String subsystem, String operationName, String... params) throws AdminException {
			DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	        final ModelNode request;
	        try {
	        	if (subsystem != null) {
		        	addProfileNode(builder);
		            builder.addNode("subsystem", subsystem); //$NON-NLS-1$
	        	}
	            builder.setOperationName(operationName);
	            request = builder.buildRequest();
	            if (params != null && params.length % 2 == 0) {
	            	for (int i = 0; i < params.length; i+=2) {
	            		builder.addProperty(params[i], params[i+1]);
	            	}
	            }
	        } catch (OperationFormatException e) {
	        	throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
	        }
			return request;
		}

		public void cliCall(String operationName, String[] address, String[] params, ResultCallback callback) throws AdminException {			

			DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
	        final ModelNode request;
	        try {
	        	if (address.length % 2 != 0) {
	        		throw new IllegalArgumentException("Failed to build operation"); //$NON-NLS-1$
	        	}
	        	addProfileNode(builder);
	        	for (int i = 0; i < address.length; i+=2) {
		            builder.addNode(address[i], address[i+1]);
	        	}
	            builder.setOperationName(operationName);
	            request = builder.buildRequest();
	            if (params != null && params.length % 2 == 0) {
	            	for (int i = 0; i < params.length; i+=2) {
	            		builder.addProperty(params[i], params[i+1]);
	            	}
	            }
	            ModelNode outcome = this.connection.execute(request);
	            ModelNode result = null;
	            if (Util.isSuccess(outcome)) {
			    	if (outcome.hasDefined("result")) {
			    		result = outcome.get("result");
			    		callback.onSuccess(outcome, result);
			    	}
	            }
	            else {
	            	callback.onFailure(Util.getFailureDescription(outcome));
	            }
	        } catch (OperationFormatException e) {
	        	throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		private <T> List<T> getDomainAwareList(ModelNode operationResult,  MetadataMapper<T> mapper) {
	    	if (this.domainMode) {
	    		List<T> returnList = new ArrayList<T>();

	    		ModelNode serverGroups = operationResult.get("server-groups");
	    		Set<String> serverGroupNames = serverGroups.keys();
	    		for (String serverGroupName:serverGroupNames) {
	    			ModelNode serverGroup = serverGroups.get(serverGroupName);
	    			ModelNode hostGroups = serverGroup.get("host");
	    			Set<String> hostKeys = hostGroups.keys();
	    			for(String hostName:hostKeys) {
	    				ModelNode hostGroup = hostGroups.get(hostName);
	    	  			Set<String> serverNames = hostGroup.keys();
		    			for (String serverName:serverNames) {
		    				ModelNode server = hostGroup.get(serverName);
		    				if (server.get("response", "outcome").asString().equals(Util.SUCCESS)) {
		    					ModelNode result = server.get("response", "result");
		    					if (result.isDefined()) {
		    				        List<ModelNode> nodeList = result.asList();
		    				        for(ModelNode node : nodeList) {
		    				        	T anObj = mapper.unwrap(getTeiidVersion(), node);
		    				        	if (anObj instanceof DomainAware) {
		    				        		((AdminObjectImpl)anObj).setServerGroup(serverGroupName);
		    				        		((AdminObjectImpl)anObj).setServerName(serverName);
		    				        		((AdminObjectImpl)anObj).setHostName(hostName);
		    				        	}
		    				        	returnList.add(anObj);
		    				        }

		    					}
		    				}
		    			}
	    			}
	    		}
	    		return returnList;
	    	}
	    	return getList(operationResult, mapper);
		}

	    private <T> List<T> getList(ModelNode operationResult,  MetadataMapper<T> mapper) {
	        if(!operationResult.hasDefined("result")) {
				return Collections.emptyList();
			}

	        List<ModelNode> nodeList = operationResult.get("result").asList(); //$NON-NLS-1$
	        if(nodeList.isEmpty()) {
				return Collections.emptyList();
			}

	        List<T> list = new ArrayList<T>(nodeList.size());
	        for(ModelNode node : nodeList) {
        		list.add(mapper.unwrap(getTeiidVersion(), node));
	        }
	        return list;
	    }


	    @Deprecated
		@Override
		public VDB getVDB(String vdbName, int vdbVersion) throws AdminException {
			final ModelNode request = buildRequest("teiid", "get-vdb", "vdb-name", vdbName, "vdb-version", String.valueOf(vdbVersion));//$NON-NLS-1$
			if (request == null) {
				return null;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	            	if (this.domainMode) {
		            	List<VDBMetaData> list = getDomainAwareList(outcome, VDBMetadataMapper.INSTANCE);
		            	if (list != null && !list.isEmpty()) {
		            		return list.get(0);
		            	}
	            	}
	            	else {
		            	if (outcome.hasDefined("result")) {
		            		ModelNode result = outcome.get("result");
		            		return VDBMetadataMapper.INSTANCE.unwrap(getTeiidVersion(), result);
		            	}
	            	}
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
			return null;
		}
		@Override
		public VDB getVDB(String vdbName, String vdbVersion) throws AdminException {
			final ModelNode request = buildRequest("teiid", "get-vdb", "vdb-name", vdbName, "vdb-version", String.valueOf(vdbVersion));//$NON-NLS-1$
			if (request == null) {
				return null;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	            	if (this.domainMode) {
		            	List<VDBMetaData> list = getDomainAwareList(outcome, VDBMetadataMapper.INSTANCE);
		            	if (list != null && !list.isEmpty()) {
		            		return list.get(0);
		            	}
	            	}
	            	else {
		            	if (outcome.hasDefined("result")) {
		            		ModelNode result = outcome.get("result");
		            		return VDBMetadataMapper.INSTANCE.unwrap(getTeiidVersion(), result);
		            	}
	            	}
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
			return null;
		}

		@Override
		public List<? extends VDB> getVDBs() throws AdminException {
	        final ModelNode request = buildRequest("teiid", "list-vdbs");//$NON-NLS-1$ //$NON-NLS-2$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (Util.isSuccess(outcome)) {
	                return getDomainAwareList(outcome, VDBMetadataMapper.INSTANCE);
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }

	        return Collections.emptyList();
		}

		@Deprecated
		@Override
		public void addDataRoleMapping(String vdbName, int vdbVersion, String dataRole, String mappedRoleName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "add-data-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole,
	        		"mapped-role", mappedRoleName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Override
		public void addDataRoleMapping(String vdbName, String vdbVersion, String dataRole, String mappedRoleName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "add-data-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole,
	        		"mapped-role", mappedRoleName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Deprecated
		@Override
		public void removeDataRoleMapping(String vdbName, int vdbVersion, String dataRole, String mappedRoleName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "remove-data-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole,
	        		"mapped-role", mappedRoleName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Override
		public void removeDataRoleMapping(String vdbName, String vdbVersion, String dataRole, String mappedRoleName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "remove-data-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole,
	        		"mapped-role", mappedRoleName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Deprecated
		@Override
		public void setAnyAuthenticatedForDataRole(String vdbName, int vdbVersion, String dataRole, boolean anyAuthenticated) throws AdminException {
	        ModelNode request = buildRequest("teiid", "add-anyauthenticated-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole); //$NON-NLS-1$

	        if (!anyAuthenticated) {
	        	request = buildRequest("teiid", "remove-anyauthenticated-role",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion),
		        		"data-role", dataRole); //$NON-NLS-1$
	        }
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Override
		public void setAnyAuthenticatedForDataRole(String vdbName, String vdbVersion, String dataRole, boolean anyAuthenticated) throws AdminException {
	        ModelNode request = buildRequest("teiid", "add-anyauthenticated-role",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"data-role", dataRole); //$NON-NLS-1$

	        if (!anyAuthenticated) {
	        	request = buildRequest("teiid", "remove-anyauthenticated-role",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion),
		        		"data-role", dataRole); //$NON-NLS-1$
	        }
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
        @Deprecated
		@Override
		public void changeVDBConnectionType(String vdbName, int vdbVersion, ConnectionType type) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "change-vdb-connection-type",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"connection-type", type.name());//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public void changeVDBConnectionType(String vdbName, String vdbVersion, ConnectionType type) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "change-vdb-connection-type",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"connection-type", type.name());//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Deprecated
		@Override
		public void updateSource(String vdbName, int vdbVersion, String sourceName, String translatorName,
				String dsName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "update-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"source-name", sourceName,
	        		"translator-name", translatorName,
	        		"ds-name", dsName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}
		
		@Override
		public void updateSource(String vdbName, String vdbVersion, String sourceName, String translatorName,
				String dsName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "update-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"source-name", sourceName,
	        		"translator-name", translatorName,
	        		"ds-name", dsName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}
		
		@Override
		public void addSource(String vdbName, int vdbVersion, String modelName, String sourceName, String translatorName,
				String dsName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "add-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"model-name", modelName,
	        		"source-name", sourceName,
	        		"translator-name", translatorName,
	        		"ds-name", dsName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}
		
		@Deprecated
		@Override
		public void addSource(String vdbName, String vdbVersion, String modelName, String sourceName, String translatorName,
				String dsName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "add-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"model-name", modelName,
	        		"source-name", sourceName,
	        		"translator-name", translatorName,
	        		"ds-name", dsName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}
		
		@Deprecated
		@Override
		public void removeSource(String vdbName, int vdbVersion, String modelName, String sourceName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "remove-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"model-name", modelName,
	        		"source-name", sourceName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}
		
		
		@Override
		public void removeSource(String vdbName, String vdbVersion, String modelName, String sourceName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "remove-source",
	        		"vdb-name", vdbName,
	        		"vdb-version", String.valueOf(vdbVersion),
	        		"model-name", modelName,
	        		"source-name", sourceName);//$NON-NLS-1$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (Exception e) {
	        	 throw new AdminProcessingException(e);
	        }
		}

	    @Override
	    public void markDataSourceAvailable(String jndiName) throws AdminException {
	        final ModelNode request = buildRequest("teiid", "mark-datasource-available","ds-name", jndiName);//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
	    }
	    
	    @Deprecated
		@Override
		public void restartVDB(String vdbName, int vdbVersion, String... models) throws AdminException {
			ModelNode request = null;
			String modelNames = null;

			if (models != null && models.length > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < models.length-1; i++) {
					sb.append(models[i]).append(",");
				}
				sb.append(models[models.length-1]);
				modelNames = sb.toString();
			}

			if (modelNames != null) {
				request = buildRequest("teiid", "restart-vdb",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion),
		        		"model-names", modelNames);//$NON-NLS-1$
			}
			else {
				request = buildRequest("teiid", "restart-vdb",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion));//$NON-NLS-1$
			}

	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
	    
		@Override
		public void restartVDB(String vdbName, String vdbVersion, String... models) throws AdminException {
			ModelNode request = null;
			String modelNames = null;

			if (models != null && models.length > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < models.length-1; i++) {
					sb.append(models[i]).append(",");
				}
				sb.append(models[models.length-1]);
				modelNames = sb.toString();
			}

			if (modelNames != null) {
				request = buildRequest("teiid", "restart-vdb",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion),
		        		"model-names", modelNames);//$NON-NLS-1$
			}
			else {
				request = buildRequest("teiid", "restart-vdb",
		        		"vdb-name", vdbName,
		        		"vdb-version", String.valueOf(vdbVersion));//$NON-NLS-1$
			}

	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Deprecated
		@Override
		public String getSchema(String vdbName, int vdbVersion,
				String modelName, EnumSet<SchemaObjectType> allowedTypes,
				String typeNamePattern) throws AdminException {
			ModelNode request = null;

			ArrayList<String> params = new ArrayList<String>();
			params.add("vdb-name");
			params.add(vdbName);
			params.add("vdb-version");
			params.add(String.valueOf(vdbVersion));
			params.add("model-name");
			params.add(modelName);

			if (allowedTypes != null) {
				params.add("entity-type");
				StringBuilder sb = new StringBuilder();
				for (SchemaObjectType type:allowedTypes) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(type.name());
				}
				params.add(sb.toString());
			}

			if (typeNamePattern != null) {
				params.add("entity-pattern");
				params.add(typeNamePattern);
			}

			request = buildRequest("teiid", "get-schema", params.toArray(new String[params.size()]));//$NON-NLS-1$ //$NON-NLS-2$

	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	            return outcome.get(RESULT).asString();
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}
		
		@Override
		public String getSchema(String vdbName, String vdbVersion,
				String modelName, EnumSet<SchemaObjectType> allowedTypes,
				String typeNamePattern) throws AdminException {
			ModelNode request = null;

			ArrayList<String> params = new ArrayList<String>();
			params.add("vdb-name");
			params.add(vdbName);
			params.add("vdb-version");
			params.add(String.valueOf(vdbVersion));
			params.add("model-name");
			params.add(modelName);

			if (allowedTypes != null) {
				params.add("entity-type");
				StringBuilder sb = new StringBuilder();
				for (SchemaObjectType type:allowedTypes) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(type.name());
				}
				params.add(sb.toString());
			}

			if (typeNamePattern != null) {
				params.add("entity-pattern");
				params.add(typeNamePattern);
			}

			request = buildRequest("teiid", "get-schema", params.toArray(new String[params.size()]));//$NON-NLS-1$ //$NON-NLS-2$

	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	            return outcome.get(RESULT).asString();
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public String getQueryPlan(String sessionId, int executionId)  throws AdminException {
			final ModelNode request = buildRequest("teiid", "get-plan", "session", sessionId, "execution-id", String.valueOf(executionId));//$NON-NLS-1$
			if (request == null) {
				return null;
			}
	        try {
	            ModelNode outcome = this.connection.execute(request);
	            if (!Util.isSuccess(outcome)) {
	            	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
	            }
	            return outcome.get(RESULT).asString();
	        } catch (IOException e) {
	        	 throw new AdminComponentException(e);
	        }
		}

		@Override
		public void restart() {
			try {
				cliCall("reload", new String[] {}, new String[] {}, new ResultCallback());
			} catch (AdminException e) {
				//ignore
			}
		}

		// REMOVED IN TEIID 8.0
        @Override
        public void mergeVDBs(String sourceVDBName, int sourceVDBVersion, String targetVDBName, int targetVDBVersion) {
            throw new UnsupportedOperationException();
        }

        // REMOVED IN TEIID 8.0
        @Override
        public void deployVDB(String fileName, InputStream vdb) {
            throw new UnsupportedOperationException();
        }

        // REMOVED IN TEIID 8.0
        @Override
        public void deleteVDB(String vdbName, int version) {
            throw new UnsupportedOperationException();
        }

        /**
         * Flushes the caches of the admin connection
         */
        public void flush() {
        	// NOOP
        }
    }

	static class Expirable<T> {
        private long expires;
        private T t;

        public T get() {
            if (this.t == null || System.currentTimeMillis() >= this.expires) {
                this.t = null;
            }
            return this.t;
        }

        public void set(T t, long cacheTimeInMillis) {
            this.expires = System.currentTimeMillis()+cacheTimeInMillis;
            this.t = t;
        }
        
    }
}

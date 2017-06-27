/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.DomainAware;
import org.teiid.adminapi.Session;
import org.teiid.adminapi.impl.AdminObjectImpl;
import org.teiid.adminapi.jboss.MetadataMapper;
import org.teiid.adminapi.jboss.VDBMetadataMapper.SessionMetadataMapper;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;


public class AdminConnectionManager {
	private final ModelControllerClient connection;
	private boolean domainMode = false;
	private String profileName = "ha";
	private ITeiidServerVersion teiidServerVersion;
	
	public AdminConnectionManager(ModelControllerClient connection, ITeiidServerVersion teiidServerVersion) {
		super();
		this.connection = connection;
		this.teiidServerVersion = teiidServerVersion;
	}
	
    
    public ModelControllerClient getConnection() {
    	return this.connection;
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
	
	public ModelNode buildRequest(String subsystem, String operationName, String... params) throws AdminException {
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

    /**
     * Execute an operation synchronously.
     *
     * @param operation the operation to execute
     * @return the result of the operation
     * @throws IOException if an I/O error occurs while executing the operation
     */
    public ModelNode execute(ModelNode request) throws IOException {
    	return this.connection.execute(request);
	}


    public <T> List<T> getList(ModelNode operationResult,  MetadataMapper<T> mapper) {
        if(!operationResult.hasDefined("result")) {
			return Collections.emptyList();
		}

        List<ModelNode> nodeList = operationResult.get("result").asList(); //$NON-NLS-1$
        if(nodeList.isEmpty()) {
			return Collections.emptyList();
		}

        List<T> list = new ArrayList<T>(nodeList.size());
        for(ModelNode node : nodeList) {
    		list.add(mapper.unwrap(getTeiidServerVersion(), node));
        }
        return list;
    }
	
	public boolean isDomainMode() {
		return this.domainMode;
	}
	
	public ITeiidServerVersion getTeiidServerVersion() {
		return this.teiidServerVersion;
	}
	
	// 
	public boolean deleteSubsystem(String subsystemName, String... subsystem) throws AdminException {
		DefaultOperationRequestBuilder builder = new DefaultOperationRequestBuilder();
        final ModelNode request;

        try {
        	addProfileNode(builder);

            builder.addNode("subsystem", subsystem[0]); //$NON-NLS-1$
            builder.addNode(subsystem[1], subsystemName);
            builder.setOperationName("remove");
            request = builder.buildRequest();
        } catch (OperationFormatException e) {
            throw new AdminComponentException("Failed to build operation", e); //$NON-NLS-1$
        }

        try {
            ModelNode outcome = this.connection.execute(request);
            if (!Util.isSuccess(outcome)) {
                return false;
            }
        } catch (IOException e) {
        	 throw new AdminComponentException(e);
        }
        return true;
	}
	
	public <T> List<T> getDomainAwareList(ModelNode operationResult,  MetadataMapper<T> mapper) {
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
	    				        	T anObj = mapper.unwrap(this.teiidServerVersion, node);
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
}

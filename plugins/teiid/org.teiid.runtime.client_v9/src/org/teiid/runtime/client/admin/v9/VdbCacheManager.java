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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jboss.as.cli.Util;
import org.jboss.as.cli.operation.OperationFormatException;
import org.jboss.as.cli.operation.impl.DefaultOperationRequestBuilder;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.Admin.SchemaObjectType;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.jboss.VDBMetadataMapper;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.admin.AdminSpec;
import org.teiid.runtime.client.admin.TeiidVdb;

public class VdbCacheManager implements AdminConstants {
    private static String DYNAMIC_VDB_SUFFIX = "-vdb.xml"; //$NON-NLS-1$
    private static int VDB_LOADING_TIMEOUT_SEC = 300;
    
    private Map<String, ITeiidVdb> nameToVdbMap;
	private AdminConnectionManager manager;
	private final ITeiidServer teiidServer;
	private final EventManager eventManager;
	
	private final AdminSpec adminSpec;
	
	private final Admin admin;
	
	public VdbCacheManager(ITeiidServer teiidServer, Admin admin, AdminConnectionManager adminConnectionManager) throws Exception {
		super();
		this.teiidServer = teiidServer;
		this.manager = adminConnectionManager;
		this.adminSpec = AdminSpec.getInstance(teiidServer.getServerVersion());
		this.admin = admin;
		this.eventManager = teiidServer.getEventManager();
	}
	
    public void init() throws Exception {
        this.nameToVdbMap = new HashMap<String, ITeiidVdb>();
    }

    public Collection<ITeiidVdb> getVdbs() {
        return Collections.unmodifiableCollection(nameToVdbMap.values());
    }
    
    public ITeiidVdb getVdb( String name ) {
        return nameToVdbMap.get(name);
    }
    
    public void refresh() throws AdminException {
	    nameToVdbMap = new HashMap<String, ITeiidVdb>();
	    
	    Collection<? extends VDB> vdbs = null;
	    
	    try {
			vdbs = Collections.unmodifiableCollection(getRawVDBs());
		} catch (AdminException e) {
			throw new AdminComponentException(e);
		}
	    

	
	    for (VDB vdb : vdbs) {
	    	nameToVdbMap.put(vdb.getName(), new TeiidVdb(vdb, this.teiidServer));
	    }
    }

	public void undeploy(String deployedName) throws AdminException  {
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
	 * @throws IOException 
	 */
	public void undeploy(String deployedName, boolean force) throws AdminException{
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

        if(manager.isDomainMode()) {
        	final List<String> serverGroups = Util.getServerGroups(manager.getConnection());

            for (String group : serverGroups){
                ModelNode groupStep = Util.configureDeploymentOperation(DEPLOYMENT_UNDEPLOY_OPERATION, name, group);
                steps.add(groupStep);
            }

            for (String group : serverGroups) {
                ModelNode groupStep = Util.configureDeploymentOperation(DEPLOYMENT_REMOVE_OPERATION, name, group);
                steps.add(groupStep);
            }
        } else if(Util.isDeployedAndEnabledInStandalone(name, manager.getConnection())||force) {
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

	public void deploy(String deployName, InputStream vdb)	throws AdminException, IOException {
		ModelNode request = buildDeployVDBRequest(deployName, vdb, true);
		manager.execute(request);
		
		refresh();
	}

	/**
	 * @param deployName
	 * @param vdb
	 * @param persist
	 * @throws AdminException
	 * @throws IOException 
	 */
	public void deploy(String deployName, InputStream vdb, boolean persist)	throws AdminException, IOException {
		ModelNode request = buildDeployVDBRequest(deployName, vdb, persist);
		manager.execute(request);
		
		refresh();
	}

	private ModelNode buildDeployVDBRequest(String fileName, InputStream vdb, boolean persist) throws AdminException {
        try {
			if (Util.isDeploymentInRepository(fileName, manager.getConnection())){
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
            if (manager.isDomainMode()) {
            	List<String> serverGroups = Util.getServerGroups(manager.getConnection());
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
	
	private ModelNode execute(ModelNode request) throws AdminComponentException {
        try {
        	return manager.execute(request);
        } catch (IOException e) {
        	throw new AdminComponentException(e);
        }
	}
	
    public void disconnect() {
    	this.nameToVdbMap = new HashMap<String, ITeiidVdb>();
    }
    

	public VDB getRawVDB(String vdbName, String vdbVersion) throws AdminException {
		final ModelNode request = this.manager.buildRequest("teiid", "get-vdb", "vdb-name", vdbName, "vdb-version", String.valueOf(vdbVersion));//$NON-NLS-1$
		if (request == null) {
			return null;
		}
		
        ModelNode outcome = execute(request);
        if (Util.isSuccess(outcome)) {
        	if (manager.isDomainMode()) {
            	List<VDBMetaData> list = manager.getDomainAwareList(outcome, VDBMetadataMapper.INSTANCE);
            	if (list != null && !list.isEmpty()) {
            		return list.get(0);
            	}
        	}
        	else {
            	if (outcome.hasDefined("result")) {
            		ModelNode result = outcome.get("result");
            		return VDBMetadataMapper.INSTANCE.unwrap(manager.getTeiidServerVersion(), result);
            	}
        	}
        }
		return null;
	}
	
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

		request = manager.buildRequest("teiid", "get-schema", params.toArray(new String[params.size()]));//$NON-NLS-1$ //$NON-NLS-2$

        ModelNode outcome = execute(request);
        if (!Util.isSuccess(outcome)) {
        	 throw new AdminProcessingException(Util.getFailureDescription(outcome));
        }
        return outcome.get(RESULT).asString();
	}
	public List<? extends VDB> getRawVDBs() throws AdminException {
        final ModelNode request = manager.buildRequest("teiid", "list-vdbs");//$NON-NLS-1$ //$NON-NLS-2$

        ModelNode outcome = execute(request);
        if (Util.isSuccess(outcome)) {
            return manager.getDomainAwareList(outcome, VDBMetadataMapper.INSTANCE);
        }

        return Collections.emptyList();
	}
	
    public void deployDynamicVdb( String deploymentName, InputStream inStream ) throws Exception {
        ArgCheck.isNotNull(deploymentName, "deploymentName"); //$NON-NLS-1$
        ArgCheck.isNotNull(inStream, "inStream"); //$NON-NLS-1$

        // Check dynamic VDB deployment name
        if(!deploymentName.endsWith(DYNAMIC_VDB_SUFFIX)) {
            throw new Exception(Messages.getString(Messages.ExecutionAdmin.dynamicVdbInvalidName, deploymentName));
        }
        
        // Get VDB name
        String vdbName = deploymentName.substring(0, deploymentName.indexOf(DYNAMIC_VDB_SUFFIX));

        // For Teiid Version less than 8.7, do explicit undeploy (TEIID-2873)
    	if(isLessThanTeiidEightSeven()) {
    		undeployDynamicVdb(vdbName);
    	}
    	
        // Deploy the VDB
        // TODO: Dont assume vdbVersion
    	doDeploy(deploymentName,vdbName,"1",inStream);
    }
    
    public void deploy( IFile vdbFile, String version) throws Exception {
        String vdbDeploymentName = vdbFile.getFullPath().lastSegment();
        String vdbName = vdbFile.getFullPath().removeFileExtension().lastSegment();
        
        // For Teiid Version less than 8.7, do explicit undeploy (TEIID-2873)
    	if(isLessThanTeiidEightSeven()) {
    		undeploy(vdbName);
    	}
    	
    	String vdbVersion = "1";
    	if( version != null) {
    		vdbVersion = version;
    	}
        
    	doDeploy(vdbDeploymentName, getVdbName(vdbName), vdbVersion, vdbFile.getContents());

    }
    
    private void doDeploy(String deploymentName, String vdbName, String vdbVersion, InputStream inStream) throws Exception {
        adminSpec.deploy(this.admin, deploymentName, inStream);
        // Give a 0.5 sec pause for the VDB to finish loading metadata.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }   
                      
        // Refresh VDBs list
        refresh();

        // TODO should get version from vdbFile
        VDB vdb = getRawVDB(vdbName, vdbVersion);

        // If the VDB is still loading, refresh again and potentially start refresh job
        if( vdb == null ) {
        	throw new Exception(Messages.getString(Messages.ExecutionAdmin.invalidVdb, vdbName));
        }
        
        if(vdb.getStatus().equals(adminSpec.getLoadingVDBStatus()) && vdb.getValidityErrors().isEmpty()) {
            // Give a 0.5 sec pause for the VDB to finish loading metadata.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }   
            // Refresh again to update vdb states
            refresh();
            vdb = getRawVDB(vdbName, vdbVersion);
            // Determine if still loading, if so start refresh job.  User will get dialog that the
            // vdb is still loading - and try again in a few seconds
            if(vdb.getStatus().equals(adminSpec.getLoadingVDBStatus()) && vdb.getValidityErrors().isEmpty()) {
                final Job refreshVDBsJob = new RefreshVDBsJob(vdbName);
                refreshVDBsJob.schedule();
            }
        }

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createDeployVDBEvent(vdb.getName()));
    }
    
    public void undeployDynamicVdb( String vdbName) throws Exception {
        ITeiidVdb vdb = getVdb(vdbName);
        if(vdb!=null) {
        	adminSpec.undeploy(admin, appendDynamicVdbSuffix(vdbName), vdb.getVersion());
        }
        vdb = getVdb(vdbName);

        refresh();

        if (vdb == null) {

        } else {
            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUnDeployVDBEvent(vdb.getName()));
        }
    }

    private boolean isLessThanTeiidEightSeven() {
        ITeiidServerVersion minVersion = teiidServer.getServerVersion().getMinimumVersion();
        return minVersion.isLessThan(Version.TEIID_8_7);
    }

    /**
     * Append the suffix for dynamic VDB to the vdb name if not already appended.
     * 
     * @param vdbName
     * @return
     */
    private String appendDynamicVdbSuffix(String vdbName) {
        if (vdbName.endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX))
            return vdbName;
        
        return vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX;
    }
    
	private String getVdbName(String originalVdbName) throws Exception {
		String vdbName = originalVdbName;
		int firstIndex = vdbName.indexOf('.');
		int lastIndex = vdbName.lastIndexOf('.');
		if (firstIndex != -1) {
			if (firstIndex != lastIndex) {
				throw new Exception(Messages.getString(Messages.ExecutionAdmin.invalidVdbName, originalVdbName));
			}
			vdbName = vdbName.substring(0, firstIndex);
		}
		return vdbName;
	}
    
    /**
     * Executes VDB refresh when a VDB is loading - as a background job.
     */
    class RefreshVDBsJob extends Job {

        String vdbName;
        
        /**
         * RefreshVDBsJob constructor
         * @param vdbName the name of the VDB to monitor
         */
        public RefreshVDBsJob(String vdbName ) {
            super("VDB Refresh"); //$NON-NLS-1$
            this.vdbName = vdbName;
            
            setSystem(false);
            setUser(true);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            monitor.beginTask("VDB Refresh", //$NON-NLS-1$
                              IProgressMonitor.UNKNOWN);

            try {
                waitForVDBLoad(this.vdbName);
            } catch (Exception ex) {
            }

            monitor.done();

            return Status.OK_STATUS;
        }
        
        /*
         * Wait for the VDB to finish loading.  Will check status every 5 secs and return when the VDB is loaded.
         * If not loaded within 30sec, it will timeout
         * @param vdbName the name of the VDB
         */
        private void waitForVDBLoad(String vdbName) throws Exception {
            long waitUntil = System.currentTimeMillis() + VDB_LOADING_TIMEOUT_SEC*1000;
            boolean first = true;
            do {
                // Pauses 5 sec
                if (!first) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    first = false;
                }
                
                // Refreshes from adminApi
                refresh();
                
                // Get the VDB
                ITeiidVdb vdb = getVdb(vdbName);
                
                // Stop waiting if any conditions have been met
                if(vdb!=null) {
                	boolean hasValidityErrors = !vdb.getValidityErrors().isEmpty();
                	if(  !vdb.hasModels() || vdb.hasFailed()  || !vdb.isLoading() 
                	   || vdb.isActive()  || vdb.wasRemoved() || hasValidityErrors ) {
                		return;
                	} 
                } else {
                    return;
                }
            } while (System.currentTimeMillis() < waitUntil);
            
            refresh();
            return;
        }

    }

}

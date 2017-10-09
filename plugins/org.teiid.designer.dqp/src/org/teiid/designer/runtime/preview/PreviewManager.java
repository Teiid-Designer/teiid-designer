/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.extension.CoreModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.CoreModelExtensionConstants;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.importer.Messages;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.transformation.ddl.TeiidModelToDdlGenerator;

import net.jcip.annotations.ThreadSafe;


/**
 * The <code>PreviewManager</code> is responsible for keeping the hidden Preview VDBs synchronized with the workspace. Also, the
 * Preview Manager is responsible for deploying the Preview VDBs to Teiid to enable data preview. The Preview Manager also removes
 * (undeploys) Preview VDBs from Teiid when their associate workspace model is deleted.
 * <p>
 * For each previewable model in the workspace, an associated Preview VDB will be maintained. This PVDB will be hidden from the
 * user but will be contained within the workspace. When a model is changed, the PVDB is automatically synchronized. When a model
 * is deleted, the PVDB is also deleted. Upon preview of a model object, the PVDB of that model, along with any dependent models
 * will be deployed to the designated preview Teiid Instance (if there is one). Deploying of the PVDBs is only done if necessary.
 *
 * @since 8.0
 */
@ThreadSafe
public final class PreviewManager {
    private static final String DYNAMIC_VDB_SUFFIX = "-vdb.xml";  //$NON-NLS-1$
    public static final String IMPORT_SRC_MODEL = "SrcModel";  //$NON-NLS-1$
    
	private static final String TEIID_INFINISPAN_HOTROD_DRIVER = "infinispan-hotrod"; //$NON-NLS-1$
    
    IStatus vdbDeploymentStatus = null;
    
	EObject targetObject;
	IStatus dynamicVdbStatus;
	String vdbName;
	String deploymentName;
	String modelName;
	DependentObjectHelper helper;
	DataSourceHelper dsHelper;
	IStatus jndiNameStatus;

	public PreviewManager(EObject targetObject) {
		super();
		
		this.targetObject = targetObject;
		
		//helper = new SqlDependencyHelper(targetObject);
		
		ModelResource targetMR = getModelResource(targetObject);
		String modelName = targetMR.getItemName();
		
		if( modelName.toUpperCase().endsWith(".XMI") ) {
			int length = modelName.length();
			modelName = modelName.substring(0, length-4);
		}
		
		String uuid = new UUID(java.util.UUID.randomUUID()).exportableForm();
		vdbName = "PREVIEW-" + uuid;
		deploymentName = vdbName+DYNAMIC_VDB_SUFFIX;
		
		this.dsHelper = new DataSourceHelper();
		
		this.dynamicVdbStatus = null;
	}
	
	public IStatus getDynamicVdbStatus() throws ModelWorkspaceException {
		if( dynamicVdbStatus == null ) {
			try {
				generateDynamicVdb();
			} catch (ModelWorkspaceException e) {
				if( e.getModelStatus().getSeverity() == IStatus.ERROR &&
						e.getModelStatus().getCode() == 9991 ) {
					dynamicVdbStatus = e.getModelStatus();
				} else {
					e.printStackTrace();
				}
			}
		}
		return dynamicVdbStatus;
	}
	
	public IStatus getDataSourcesStatus() {
		dsHelper.checkDeployments();
		
		return dsHelper.getStatus();
	}
	
	public String getPreviewVdbName() {
		return 	vdbName;
	}
	
	public String getPreviewVdbDeploymentName() {
		return 	deploymentName;
	}
	
	private void generateDynamicVdb() throws ModelWorkspaceException {
		if( dynamicVdbStatus == null ) {
			dynamicVdbStatus = createDynamicVdb();
		}
	}
	
    /**
     * Deploy a dynamic VDB using the current DataSource and Translator
     * @return the deployment status
     */
    public IStatus deployDynamicVdb() {
 
		try {
			generateDynamicVdb();
		} catch (ModelWorkspaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        // Create Runnable if the profile is valid

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                	String message = null;
                	if(getTimeoutPrefSecs()<1) {
                		message = "No TIMEOUT defined";
                	} else {
                		message = "Timeout deploying Preview VDB was " + getTimeoutPrefSecs(); //NLS.bind(Messages.TeiidImportManager_deployVdbMsg, getTimeoutPrefSecs());
                	}
                    monitor.beginTask(message, 100); 
                    vdbDeploymentStatus = deployDynamicVdb(dynamicVdbStatus.getMessage(), monitor); 
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            vdbDeploymentStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
            DqpPlugin.Util.log(vdbDeploymentStatus);
        } catch (InterruptedException e) {
            vdbDeploymentStatus = new Status(IStatus.ERROR,DqpPlugin.PLUGIN_ID, "Deploy Preview Dynamic VDB interrupted");
            DqpPlugin.Util.log(vdbDeploymentStatus);
        }
        
        return vdbDeploymentStatus;
    }
	
    /**
     * Undeploy the dynamic VDB and datasource
     * @return the deployment status
     */
    public IStatus undeployDynamicVdb() {
        IStatus resultStatus = Status.OK_STATUS;
        
        ITeiidVdb deployedImportVdb;
        try {
            deployedImportVdb = getDefaultServer().getVdb(vdbName);
            if( deployedImportVdb != null ) {
            	getDefaultServer().undeployDynamicVdb(deployedImportVdb.getName());
            }
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerUndeployVdbError, vdbName));
        }
        
        return resultStatus;
    }

	/**
    
    
    /**
     * Determine if a valid server is available for dynamic vdb import.  The server must be
     * running, and it must be version 8.x or higher.
     * @return 'true' if the server is valid
     */
//    private boolean isValidImportServer() {
//        ITeiidServer importServer = getImportServer();
//        // If no server, or not connected - invalid
//        if(importServer==null || !importServer.isConnected()) {
//            return false;
//        }
//        
//        // If this is a Teiid 7 server, we cant do this type of import
//        ITeiidServerVersion version = importServer.getServerVersion();
//        if(version.isSevenServer()) {
//            return false;
//        }
//        
//        return true;
//    }
    
//    public boolean vdbExists(String vdbName) {
//    	try {
//			return getImportServer().getVdb(vdbName) != null;
//		} catch (Exception e) {
//			return false;
//		}
//    }

    /**
     * @param vdbName name to use for the VDB
     * @param sourceName the dataSource to use for the import
     * @param translatorName the name of the translator
     * @param modelPropertyMap the Map of optional model properties
     * @param monitor the progress monitor
     * @return status of the deployment
     */
    public IStatus deployDynamicVdb(String dynamicVdbString, IProgressMonitor monitor) {
    	IStatus resultStatus = Status.OK_STATUS;
    	
    	// Work remaining for progress monitor
    	int workRemaining = 100;

        monitor.worked(10);
        workRemaining -= 10;
        
        // Deploy the Dynamic VDB
        try {
        	ITeiidServer server = DqpPlugin.getInstance().getServerManager().getDefaultServer();
        	server.deployDynamicVdb(deploymentName,new ByteArrayInputStream(dynamicVdbString.getBytes("UTF-8"))); //$NON-NLS-1$
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerDeployVdbError, vdbName));
            return resultStatus;
        }
        monitor.worked(10);
        workRemaining -= 10;

        // Wait until vdb is done loading, up to timeout sec
        int timeoutSec = DqpPlugin.getInstance().getPreferences().getInt(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);

        boolean finishedLoading = false;
        try {
            finishedLoading = waitForVDBLoad(vdbName,timeoutSec,monitor,workRemaining);
        } catch (InterruptedException ie) {
            resultStatus = new Status(IStatus.CANCEL, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbLoadingInterruptedError, vdbName));
            return resultStatus;
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbLoadingError, vdbName));
            return resultStatus;
        }
        
        // If the VDB finished loading, check Active state
        if(finishedLoading) {
            boolean isVDBActive;
            try {
                isVDBActive = getDefaultServer().isVdbActive(vdbName);
            } catch (Exception ex) {
                resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbGetStateError, vdbName));
                return resultStatus;
            }
            // VDB Active = success
            if(isVDBActive) {
                resultStatus = Status.OK_STATUS;
            } else {
                resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbInactiveStateError, vdbName));
            }
        } else {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerVdbLoadingNotCompleteError, timeoutSec));
        }

        return resultStatus;
    }
    

    /**
     * Undeploy the importer vdb (and datasource)
     * @param importerVdbName the vdb name
     * @return status of the operations
     */
    public IStatus undeployVdb(String importerVdbName) {
        IStatus resultStatus = null;
        // If an import VDB with the supplied name exists, undeploy it
        ITeiidVdb deployedImportVdb;
        try {
            deployedImportVdb = getDefaultServer().getVdb(importerVdbName);
            if( deployedImportVdb != null ) {
            	getDefaultServer().undeployDynamicVdb(deployedImportVdb.getName());
            }
        } catch (Exception ex) {
            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerUndeployVdbError, importerVdbName));
        }
        
        return resultStatus;
    }

    /*
     * Get the deploymentName for the supplied VDB
     * @param deployedVdb the vdb
     * @return the vdb deployment name
     */
//    private String getVdbDeploymentName(ITeiidVdb deployedVdb) { 
//        String fullVdbName = deployedVdb.getPropertyValue("deployment-name"); //$NON-NLS-1$
//        return fullVdbName;
//    }
    
	public IStatus createDynamicVdb() throws ModelWorkspaceException {
		if( ModelerCore.getTeiidServerVersion().isGreaterThan(TeiidServerVersion.Version.TEIID_8_13_5) ) {
			return createDynamicVdb(vdbName, "1.0", "Importer VDB", deploymentName);
		} else {
			// pre Wildfly/Teiid 9.0 version
			return createDynamicVdb(vdbName, "1", "Importer VDB", deploymentName);
		}
	}
        

	public IStatus createDynamicVdb(String vdbName, String version, String description, String deploymentName) throws ModelWorkspaceException {
    	
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"); //$NON-NLS-1$
        sb.append("\n<vdb name=\""+ vdbName + "\" version=\"" + version + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        if( StringUtilities.isNotEmpty(description) ) {
        	sb.append("\n\t<description>" + description + "</description>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        sb.append("\n\t<property name=\"UseConnectorMetadata\" value=\"true\" />"); //$NON-NLS-1$
        
        Collection<VdbSourceModelInfo> vdbImports = getVdbSourceModelInfos();
        
        for( VdbSourceModelInfo info : vdbImports ) {
        	sb.append(info.getXml());
        }
        
        Collection<ModelFragmentInfo> modelFragments = getModelFragments();
        
        for( ModelFragmentInfo info : modelFragments ) {

        	sb.append(info.getModelXml());
        	// constructing model XML will identify/store a JNDI name if it exists in the model resource
        	// Save this off to the DataSourceHelper to check that they exist
        	// check fragment info for missing JNDI name
        	if( info.isJndiNameMissing() ) {
        		String message = NLS.bind(org.teiid.designer.runtime.preview.Messages.PreviewManager_jndiNameMissingMessage,  
        				info.getModelResource().getItemName());
        		return new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, message);
        	}
        	if( info.getJndiName() != null ) {
        		dsHelper.addJndiName(info.getJndiName(), info.getModelResource());
        	}
        }
        String transOverrides = getTranslatorOverrides(modelFragments);
        if( StringUtilities.isNotEmpty(transOverrides)) {
        	sb.append(transOverrides);
        }
        sb.append("\n</vdb>"); //$NON-NLS-1$
        return new Status(IStatus.OK, DqpPlugin.PLUGIN_ID, sb.toString());
    }
    
    /*
     * Helper method - waits for the VDB to finish loading
     * @param vdbName the name of the VDB
     * @param timeoutInSecs time to wait before timeout
     * @param monitor the progress monitor
     * @param workRemaining the number of work units remaining
     * @return 'true' if vdb found and is not 'Loading', 'false' otherwise.
     */
    private boolean waitForVDBLoad(String vdbName, int timeoutInSecs, IProgressMonitor monitor, int workRemaining) throws Exception {

    	final int sleepDurationSec = 5;
    	int increments = timeoutInSecs / sleepDurationSec;
    	int workIncrement = Math.round((float)workRemaining / increments);
    	
        long waitUntil = System.currentTimeMillis() + timeoutInSecs*1000;
        // Timeout of zero or less means no timeout...
        if (timeoutInSecs < 1) {
            waitUntil = Long.MAX_VALUE;
        }
        boolean first = true;
        do {
            // Pause 5 sec before subsequent attempts
            if (!first) {
                try {
                    Thread.sleep(sleepDurationSec*1000);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                first = false;
            }
            monitor.worked(workIncrement);
            
            // Check for cancellation request.  If cancelled, throw InterruptedException
            if(monitor.isCanceled()) {
            	monitor.setCanceled(true);
            	throw new InterruptedException("The operation was cancelled"); //$NON-NLS-1$
            }
            
            boolean isActive = getDefaultServer().isVdbActive(vdbName);
            boolean isLoading = getDefaultServer().isVdbLoading(vdbName);
            boolean hasFailed = getDefaultServer().hasVdbFailed(vdbName);
            ITeiidVdb vdb = getDefaultServer().getVdb(vdbName);
            boolean hasValidityErrors = vdb == null ? false : !vdb.getValidityErrors().isEmpty();
            if(!isLoading || hasFailed || hasValidityErrors || isActive) return true;
        } while (System.currentTimeMillis() < waitUntil);
        return false;
    }
    
    private String getTranslatorOverrides(Collection<ModelFragmentInfo> modelFragments) {
        StringBuffer sb = new StringBuffer();
        for( ModelFragmentInfo info : modelFragments ) {
            final ConnectionInfoHelper helper = new ConnectionInfoHelper();
            ModelResource mr = info.getModelResource();
            String translatorType = helper.getTranslatorName(mr);
        	if(!StringUtilities.isEmpty(translatorType)) {
        		String translatorOverrideName = translatorType + "_override";
                Properties translatorProps = helper.getTranslatorOverrideProperties(mr);
                Properties nonNameProperties = new Properties();
                for( Object key : translatorProps.keySet() ) {
                	String keyStr = (String)key;
                	String value = translatorProps.getProperty(keyStr);
                	if( !keyStr.toUpperCase().equals("NAME") ) {
                		nonNameProperties.put(keyStr, value);
                	}
                }
                
                if( !nonNameProperties.isEmpty() ) {
                	/*
						<translator name="PartsSQL2000_sqlserver" type="sqlserver">
							<property name="supportsFullOuterJoins" value="true"/>
							<property name="MaxInCriteriaSize" value="2000"/>
						</translator> 
					*/
                    sb.append("\n\t\t<translator name=\""+ translatorOverrideName + "\" type=\"" + translatorType + "\" >"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    for( Object key : nonNameProperties.keySet() ) {
                    	String keyStr = (String)key;
                    	String value = nonNameProperties.getProperty(keyStr);
                    	sb.append("\n\t\t\t<property name=\""+ keyStr + "\" value=\"" + value + "\" />"); //$NON-NLS-1$
                    }
                    sb.append("\n\t\t</translator>");

                	
                }
        	}
        }
        
        return sb.toString();
    }
    
    /**
     * Get the server display name
     * @return the display name
     * @throws Exception the exception
     */
    public String getDisplayName() throws Exception {
        return getDefaultServer().getParentName();
    }
    
//    /**
//     * Undeploy the specified VDB
//     * @param vdbName the vdb name
//     * @throws Exception the exception
//     */
//    public void undeployDynamicVdb(String vdbName) throws Exception {
//        getImportServer().undeployDynamicVdb(vdbName);
//    }
    
    /**
     * Deploy the specified dynamic vdb
     * @param deploymentName the deployment name
     * @param inStream dynamic vdb inputStream
     * @throws Exception the exception
     */
    public void deployDynamicVdb(String deploymentName, InputStream inStream) throws Exception {
    	getDefaultServer().deployDynamicVdb(deploymentName, inStream);
    }
    
    public IStatus doCreateMissingDataSources() {
    	return dsHelper.createMissingDataSources();
    }
    

    /**
     * Return the version of the current import server - null if not defined or not connected
     * @return the Teiid Instance version
     */
    public ITeiidServerVersion getServerVersion() {
        ITeiidServer importServer = getDefaultServer();
        // If no server, or not connected - invalid
        if(importServer==null || !importServer.isConnected()) {
            return null;
        }
        return importServer.getServerVersion();
    }
    
    private int getTimeoutPrefSecs() {
        return DqpPlugin.getInstance().getPreferences().getInt(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
    }
    
    private Collection<ModelFragmentInfo> getModelFragments() throws ModelWorkspaceException {
    	Collection<ModelFragmentInfo> modelFragments = new ArrayList<ModelFragmentInfo>();
    	
    	// Create the target fragment info
    	ModelResource targetMR = getModelResource(targetObject);
    	
    	// Target MR might be a simple vdb source model
    	if( isVdbSourceModel(targetMR)) return modelFragments;
    	
    	modelFragments.add(new ModelFragmentInfo(targetObject, targetMR));
    	
    	
    	
    	boolean isVirtual = ModelType.VIRTUAL_LITERAL.equals(((EmfResource)targetMR.getEmfResource()).getModelAnnotation().getModelType());
    	
    	if( isVirtual ) {
    		DependentObjectHelper helper = new DependentObjectHelper(targetObject);
	    	Set<EObject> allDependentObjects = helper.getDependentObjects();
	
	    	
	    	for( EObject eObj : allDependentObjects ) {
	    		ModelResource nextMR = getModelResource(eObj);
	    		
	    		if( isVdbSourceModel(nextMR) ) continue;
	    		
	    		boolean newMR = true;
	    		for( ModelFragmentInfo info : modelFragments ) {
	    			if( info.matchesModelResource(nextMR) ) {
	    				info.addObject(eObj);
	    				newMR = false;
	    				break;
	    			}
	    		}
	    		
	    		if( newMR ) {
	    			modelFragments.add(new ModelFragmentInfo(eObj, nextMR));
	    		}
	    	}
    	}
    	
    	return modelFragments;
    }
    
    private Collection<VdbSourceModelInfo> getVdbSourceModelInfos() throws ModelWorkspaceException {
    	Collection<VdbSourceModelInfo> vdbSources = new ArrayList<VdbSourceModelInfo>();
    	
    	// Create the target fragment info
    	ModelResource targetMR = getModelResource(targetObject);
    	if( isVdbSourceModel(targetMR)) {
    		String vdbName = getVdbName(targetMR);
    		int version = 1;
    		String versionStr = getVdbVersion(targetMR);
    		if( StringUtilities.isNotEmpty(versionStr) ) {
    			try {
					version = Integer.parseInt(versionStr);
				} catch (NumberFormatException ex) {
					DqpPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
				}
    		}
    		vdbSources.add(new VdbSourceModelInfo(targetMR, vdbName, version));
    	}

    	
    	boolean isVirtual = ModelType.VIRTUAL_LITERAL.equals(((EmfResource)targetMR.getEmfResource()).getModelAnnotation().getModelType());
    	
    	if( isVirtual ) {
    		DependentObjectHelper helper = new DependentObjectHelper(targetObject);
	    	Set<EObject> allDependentObjects = helper.getDependentObjects();
	
	    	
	    	for( EObject eObj : allDependentObjects ) {
	    		ModelResource nextMR = getModelResource(eObj);
	        	if( isVdbSourceModel(nextMR)) {
	        		String vdbName = getVdbName(nextMR);
		    		boolean newMR = true;
		    		for( VdbSourceModelInfo info : vdbSources ) {
		    			if( info.matchesModelResource(nextMR) ) {
		    				newMR = false;
		    				break;
		    			}
		    		}
		    		
		    		if( newMR ) {
		        		int version = 1;
		        		String versionStr = getVdbVersion(targetMR);
		        		if( StringUtilities.isNotEmpty(versionStr) ) {
		        			try {
		    					version = Integer.parseInt(versionStr);
		    				} catch (NumberFormatException ex) {
		    					DqpPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
		    				}
		        		}
		    			vdbSources.add(new VdbSourceModelInfo(nextMR, vdbName, version));
		    		}
	        	}
	    	}
    	}
    	
    	return vdbSources;
    }
    
    private ModelResource getModelResource(EObject eObj) {
        return ModelerCore.getModelEditor().findModelResource(eObj);
    }
    
    private ITeiidServer getDefaultServer() {
    	return DqpPlugin.getInstance().getServerManager().getDefaultServer();
    }
    
    private boolean isVdbSourceModel(final ModelResource modelResource) {
    	if (modelResource != null ) {
    		try {
        		ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
    			CoreModelExtensionAssistant assistant = 
    					(CoreModelExtensionAssistant)registry.getModelExtensionAssistant(CoreModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
    			
    			if( assistant != null ) {
    				return assistant.isVdbSourceModel(modelResource);
    			}
			} catch (Exception ex) {
				DqpPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
			}
    	}
    	
    	return false;
    }
    
    private String getVdbName(final ModelResource modelResource) {
    	if (modelResource != null ) {
    		try {
        		ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
    			CoreModelExtensionAssistant assistant = 
    					(CoreModelExtensionAssistant)registry.getModelExtensionAssistant(CoreModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
    			
    			if( assistant != null ) {
    				return assistant.getVdbName(modelResource);
    			}
			} catch (Exception ex) {
				DqpPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
			}
    	}
    	
    	return null;
    }
    
    private String getVdbVersion(final ModelResource modelResource) {
    	if (modelResource != null ) {
    		try {
        		ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
    			CoreModelExtensionAssistant assistant = 
    					(CoreModelExtensionAssistant)registry.getModelExtensionAssistant(CoreModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
    			
    			if( assistant != null ) {
    				return assistant.getVdbVersion(modelResource);
    			}
			} catch (Exception ex) {
				DqpPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
			}
    	}
    	
    	return null;
    }
    
    class VdbSourceModelInfo {
    	ModelResource modelResource;
    	String vdbName;
    	int version;
    	
    	public VdbSourceModelInfo(ModelResource mr, String vdbName, int version) {
    		this.modelResource = mr;
    		this.vdbName = vdbName;
    		this.version = version;
    	}
    	
		public ModelResource getModelResource() {
			return modelResource;
		}
		
		public String getVdbName() {
			return vdbName;
		}
		
		public int getVdbVersion() {
			return version;
		}
		
    	private boolean matchesModelResource(ModelResource mr) {
    		return mr == modelResource;
    	}
    	
    	private String getXml() {
    		StringBuffer sb = new StringBuffer();
    		/*
    		<import-vdb name="common" version="1" import-data-policies="false"/>
    		*/
    		sb.append("\n\t<import-vdb name=\""+ vdbName + "\" version=\"" + version + "\" import-data-policies=\"false\"/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		
    		return sb.toString();
    	}
    }

    /*
     * Internal object that holds information around what tables, view, procedures, functions, etc...
     * that belong to model that are part of a dependency chain of objects.
     * 
     * 
     */
    class ModelFragmentInfo {
    	ModelResource modelResource;
    	Set<EObject> eObjects;
    	String jndiProp;
    	boolean jndiNameMissing = false;
    	
    	public ModelFragmentInfo(EObject eObj, ModelResource mr) {
    		eObjects = new HashSet<EObject>();
    		eObjects.add(eObj);
    		modelResource = mr;
    	}
    	
    	private String getModelXml() throws ModelWorkspaceException {
    		StringBuffer sb = new StringBuffer();
    		boolean isVirtual = ModelType.VIRTUAL_LITERAL.equals(((EmfResource)modelResource.getEmfResource()).getModelAnnotation().getModelType());
    		
    		String modelName = modelResource.getItemName();
    		if( modelName.toUpperCase().endsWith(".XMI") ) {
    			int length = modelName.length();
    			modelName = modelName.substring(0, length-4);
    		}
    		String modelType = modelResource.getModelType().getName();
            final ConnectionInfoHelper helper = new ConnectionInfoHelper();
            String translatorName = helper.getTranslatorName(modelResource);
            if( translatorName == null ) translatorName = StringConstants.EMPTY_STRING;
            jndiProp = helper.getJndiProperty(modelResource);
            
            jndiNameMissing = !isVirtual && jndiProp == null;
    		
            sb.append("\n\t<model name=\""+ modelName + "\" type=\"" + modelType + "\" visible=\"true\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if( !isVirtual ) {
            	sb.append("\n\t\t<source name=\""+ modelName+"\" translator-name=\""+translatorName+"\" connection-jndi-name=\""+jndiProp+"\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
            
            // Check to see if statements will be generated
            // invokeHttp(), invoke(), getFiles(), getTextFiles() and saveFile()
            Collection<String> statements = new ArrayList<String>(eObjects.size());
            
            TeiidModelToDdlGenerator generator = new TeiidModelToDdlGenerator(true);
            generator.setIncludeFKs(false);
            generator.setIncludeNIS(true);
            
            generator.setIsVirtual(isVirtual);
            
            for( EObject eObj : eObjects ) {
            	String statement = generator.getStatement(eObj, new ArrayList<Index>());
            	if( !StringUtilities.isEmpty(statement) ) {
            		statements.add(statement);
            		
            	}
            }
            
            if( !statements.isEmpty() ) {
	            sb.append("\n\t\t<metadata type=\"DDL\"><![CDATA["); //$NON-NLS-1$ 
	            
	            for( String statement : statements ) {
	            	sb.append("\n").append(statement);
	            }
	            
	            sb.append("]]></metadata>");
            }
            
            // check translator for hotrod-infinispan and add a metadata type = NATIVE
            if( TEIID_INFINISPAN_HOTROD_DRIVER.equals(translatorName) ) {
            	sb.append("\n\t\t<metadata type=\"NATIVE\">)");
            }
            sb.append("\n\t</model>"); //$NON-NLS-1$
            return sb.toString();
    	}
    	
    	private boolean matchesModelResource(ModelResource mr) {
    		return mr == modelResource;
    	}

		public ModelResource getModelResource() {
			return modelResource;
		}

		public Set<EObject> getEObjects() {
			return eObjects;
		}
    	
    	public void addObject(EObject eObj) {
    		eObjects.add(eObj);
    	}
    	
    	public String getJndiName() {
    		return jndiProp;
    	}
    	
    	public boolean isJndiNameMissing() {
    		return jndiNameMissing;
    	}
    		
    }
    
    class DynamicVdbDeployer {
        /**
         * @param xmlFile to use for the VDB
         * @param monitor the progress monitor
         * @return status of the deployment
         */
        public IStatus deployDynamicVdb(String vdbFileName, String contents, IProgressMonitor monitor) {
        	// Work remaining for progress monitor
        	
            IStatus resultStatus = Status.OK_STATUS;
                    
            // Get Dynamic VDB string

            
            // Deploy the Dynamic VDB
            try {
            	getDefaultServer().deployDynamicVdb(vdbFileName, new ByteArrayInputStream(contents.getBytes("UTF-8"))); //$NON-NLS-1$
            } catch (Exception ex) {
                resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerDeployVdbError, vdbFileName));
                return resultStatus;
            }


            return resultStatus;
        }
    }
}

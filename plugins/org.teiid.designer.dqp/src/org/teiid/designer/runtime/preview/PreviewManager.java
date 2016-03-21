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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.importer.Messages;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
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
    private static final String JNDI_PROPERTY_KEY = "jndi-name";  //$NON-NLS-1$
    
    /**
     * The Teiid Instance being used for importers (may be <code>null</code>).
     */
    private volatile AtomicReference<ITeiidServer> importServer = new AtomicReference<ITeiidServer>();
    
    IStatus vdbDeploymentStatus = null;
    
	EObject targetObject;
	String dynamicVdb;
	String vdbName;
	String deploymentName;
	String modelName;
	DependentObjectHelper helper;

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
		
		String targetName = ModelerCore.getModelEditor().getName(this.targetObject);
		vdbName = "PREVIEW_" + modelName + "_" + targetName;
		deploymentName = vdbName+DYNAMIC_VDB_SUFFIX;
	}
	
	public String getDynamicVdbString() {
		return dynamicVdb;
	}
	
	public String getPreviewVdbName() {
		return 	vdbName;
	}
	
	public String getPreviewVdbDeploymentName() {
		return 	deploymentName;
	}
	
	private void generateDynamicVdb() throws ModelWorkspaceException {
		
		dynamicVdb = createDynamicVdb(); 
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
                    vdbDeploymentStatus = deployDynamicVdb(dynamicVdb, monitor); 
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

        // If an import VDB with the supplied name exists, undeploy it first
//        ITeiidVdb deployedImportVdb;
//        try {
//            deployedImportVdb = getImportServer().getVdb(vdbName);
//            if( deployedImportVdb != null ) {
//                getImportServer().undeployDynamicVdb(deployedImportVdb.getName());
//            }
//        } catch (Exception ex) {
//            resultStatus = new Status(IStatus.ERROR, DqpPlugin.PLUGIN_ID, NLS.bind(Messages.ImportManagerUndeployVdbError, vdbName));
//            return resultStatus;
//        }
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
        
    /*
     * Create a new, blank deployment for the provided vdbName and version
     * @param vdbName name of the VDB
     * @param vdbVersion the VDB version
     * @param translatorName the translator
     * @param datasourceName the dataSource name
     * @param datasourceJndeName the dataSource jndi name
     * @param modelProps the model properties
     * @return the VDB deployment string
     */
    public String createDynamicVdb() throws ModelWorkspaceException {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"); //$NON-NLS-1$
        sb.append("\n<vdb name=\""+ vdbName +"\" version=\"1\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("\n\t<description>Importer VDB</description>"); //$NON-NLS-1$
        sb.append("\n\t<property name=\"UseConnectorMetadata\" value=\"true\" />"); //$NON-NLS-1$
        sb.append("\n\t<property name=\"deployment-name\" value=\""+ deploymentName +"\" />"); //$NON-NLS-1$ //$NON-NLS-2$
        
        Collection<ModelFragmentInfo> modelFragments = getModelFragments();
        
        for( ModelFragmentInfo info : modelFragments ) {
        	sb.append(info.getModelXml());
        }
        
        sb.append("\n</vdb>"); //$NON-NLS-1$
        return sb.toString();
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
            boolean hasValidityErrors = !getDefaultServer().getVdb(vdbName).getValidityErrors().isEmpty();
            if(!isLoading || hasFailed || hasValidityErrors || isActive) return true;
        } while (System.currentTimeMillis() < waitUntil);
        return false;
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
    	modelFragments.add(new ModelFragmentInfo(targetObject, targetMR));
    	
    	
    	
    	boolean isVirtual = ModelType.VIRTUAL_LITERAL.equals(((EmfResource)targetMR.getEmfResource()).getModelAnnotation().getModelType());
    	
    	if( isVirtual ) {
    		DependentObjectHelper helper = new DependentObjectHelper(targetObject);
	    	Set<EObject> allDependentObjects = helper.getDependentObjects();
	
	    	
	    	for( EObject eObj : allDependentObjects ) {
	    		ModelResource nextMR = getModelResource(eObj);
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
    
    private ModelResource getModelResource(EObject eObj) {
        return ModelerCore.getModelEditor().findModelResource(eObj);
    }
    
    private ITeiidServer getDefaultServer() {
    	return DqpPlugin.getInstance().getServerManager().getDefaultServer();
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
            String jndiProp = helper.getJndiProperty(modelResource);
    		
            sb.append("\n\t<model name=\""+ modelName + "\" type=\"" + modelType + "\" visible=\"true\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if( !isVirtual ) {
            	sb.append("\n\t\t<source name=\""+ modelName+"\" translator-name=\""+translatorName+"\" connection-jndi-name=\""+jndiProp+"\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
            
            // Check to see if statements will be generated
            // invokeHttp(), invoke(), getFiles(), getTextFiles() and saveFile()
            Collection<String> statements = new ArrayList<String>(eObjects.size());
            TeiidModelToDdlGenerator generator = new TeiidModelToDdlGenerator(true);
            generator.setIsVirtual(isVirtual);
            
            for( EObject eObj : eObjects ) {
            	String statement = generator.getStatement(eObj);
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

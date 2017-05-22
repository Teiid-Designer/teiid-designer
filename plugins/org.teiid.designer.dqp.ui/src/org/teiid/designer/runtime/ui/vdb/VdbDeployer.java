/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.ui.vdb;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidDataSourceFactory;
import org.teiid.designer.runtime.spi.FailedTeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;

/**
 *
 *
 * @since 8.0
 */
public class VdbDeployer {

    static final String PREFIX = I18nUtil.getPropertyPrefix(VdbDeployer.class);

    private static final String JNDI_PROPERTY_KEY = "jndi-name"; //$NON-NLS-1$


    /**
     * A VDB deployment status.
     */
    public enum DeployStatus {
        /**
         * Indicates Teiid failed to create a DS.
         */
        CREATE_DATA_SOURCE_FAILED,

        /**
         * Indicates the VDB was deployed to Teiid.
         */
        DEPLOYED_VDB,

        /**
         * Indicates the VDB deployment was canceled by the user.
         */
        DEPLOY_VDB_CANCELED,

        /**
         * Indicates Teiid failed to deploy the VDB.
         */
        DEPLOY_VDB_FAILED,

        /**
         * Indicates the an unexpected exception was caught.
         */
        EXCEPTION,

        /**
         * Indicates the user conceled the progress monitor.
         */
        MONITOR_CANCELLED,

        /**
         * Indicates there are missing translator names or translator names that are not on the current Teiid Instance.
         */
        TRANSLATOR_PROBLEM,
        
        /**
         * Indicates one or more sources has missing connection info.
         */
        SOURCE_CONNECTION_INFO_PROBLEM;

        /**
         * @return <code>true</code> if status indicates the VDB was successfully depoloyed
         */
        public boolean isDeployed() {
            return (this == DEPLOYED_VDB);
        }

        /**
         * @return <code>true</code> if status is an error
         */
        public boolean isError() {
            return ((this == CREATE_DATA_SOURCE_FAILED) || (this == DEPLOY_VDB_FAILED) || (this == EXCEPTION) || (this == TRANSLATOR_PROBLEM) || this == SOURCE_CONNECTION_INFO_PROBLEM);
        }
    }

    private final ITeiidServer teiidServer; // the current Teiid Instance
    private final boolean autoCreateDsOnServer; // indicates if data source should be auto-created on server without asking user
    private Exception error; // non-null if error caught while deploying
    private final Shell shell;
    private DeployStatus status; // non-null after deploying
    private final Vdb vdb; // the workspace VDB

    /**
     * @param shell the shell to use for any UI (may not be <code>null</code>)
     * @param vdbBeingDeployed the VDB being deployed (may not be <code>null</code>)
     * @param defaultServer the server (may not be <code>null</code>)
     * @param shouldAutoCreateDataSourceOnServer indicates if data sources that match the default name should be auto-created if
     *        they don't exist on Teiid Instance
     */
    public VdbDeployer( Shell shell,
                        Vdb vdbBeingDeployed,
                        ITeiidServer defaultServer,
                        boolean shouldAutoCreateDataSourceOnServer ) {
        CoreArgCheck.isNotNull(vdbBeingDeployed, "Vdb is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(defaultServer, "Default Teiid Instance is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(shell, "Shell is null"); //$NON-NLS-1$

        this.teiidServer = defaultServer;
        this.shell = shell;
        this.vdb = vdbBeingDeployed;
        this.autoCreateDsOnServer = shouldAutoCreateDataSourceOnServer;
    }

    /**
     * @return the error caught during deploying the VDB or <code>null</code>
     */
    public Exception getException() {
        return this.error;
    }

    /**
     * @return the deploy status (<code>null</code> if called before deploy is called)
     */
    public DeployStatus getStatus() {
        return this.status;
    }

    /**
     * Deploy the selected VDB to the default Teiid Instance.
     * 
     * @param monitor the progress monitor (can be <code>null</code>)
     * @return if fails then returns the model name otherwise null
     */
    public String deploy( IProgressMonitor monitor ) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        String failedModelName = null;
        
        try {
            // since we need to deploy lets first check to make sure all the data sources exist on server
            if (!this.vdb.getModelEntries().isEmpty()) {
                monitor.beginTask(UTIL.getString(PREFIX + "deployMainTask", getVdbName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

                boolean hasJndiProblems = false;
                boolean foundFirstOne = false; // determines if create DS dialog is shown
                boolean createOnServer = false; // user's choice on if they want DSs auto-created on server

                for (VdbEntry modelEntry : this.vdb.getModelEntries()) {
                    // see if user canceled monitor
                    if (monitor.isCanceled()) {
                        this.status = DeployStatus.MONITOR_CANCELLED;
                        return null; // don't do anything else
                    }

                    String modelName = modelEntry.getPath().toFile().getName();
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelTypeTask", modelName)); //$NON-NLS-1$
                    boolean autoCreate = false; // based on DS name and preference value
                    String modelType = ((VdbModelEntry)modelEntry).getType();
                    boolean sourceModel = (modelType.equalsIgnoreCase(ModelType.PHYSICAL_LITERAL.getName()));

                    // only source models have a data source and translator
                    if (!sourceModel) {
                        continue; // go on to next model (only care about source models)
                    }

                    // check translator
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelTranslatorTask", modelName)); //$NON-NLS-1$

                    if (!hasValidTranslator((VdbModelEntry)modelEntry)) {
                        this.status = DeployStatus.TRANSLATOR_PROBLEM;
                        break; // translator problems are fatal (deployment will fail)
                    }
                    
                    IFile modelFile = modelEntry.findFileInWorkspace();

                    // Check that Source Model has Connection Info
                    if (this.vdb.isPreview() && !hasConnectionInfo(modelFile)) {  
                    	this.status = DeployStatus.SOURCE_CONNECTION_INFO_PROBLEM;
                    	break;
                    }
                    
                    // check DS
                    monitor.subTask(UTIL.getString(PREFIX + "checkModelDataSourceTask", modelName)); //$NON-NLS-1$
                    String sourceName = ((VdbModelEntry)modelEntry).getSourceInfo().getSource(0).getName();
                    String jndiName = ((VdbModelEntry)modelEntry).getSourceInfo().getSource(0).getJndiName();

                    // DS with matching jndi not found on server
                    if (!StringUtilities.isEmpty(jndiName) && !dataSourceWithJndiExists(jndiName)) {

                        // auto-create if jndiName not different than sourceName
                        String jndiNameWithoutContext = jndiName;
                    	// incoming jndiName may not have context, so try that also since server can match it
                    	jndiNameWithoutContext = JndiUtil.removeJavaPrefix(jndiName);
                        
                        if (sourceName.equals(jndiNameWithoutContext) && this.autoCreateDsOnServer) {
                            autoCreate = true; // create without asking user
                        }

                        if (!autoCreate && !foundFirstOne) {
                            // if this is the first DS that isn't found on server ask user if DS should be
                            // auto-created on the server (and do this for any others found to not be on
                            // server)
                            foundFirstOne = true;

                            // if user OK's dialog they want DSs auto-created
                            final boolean[] result = new boolean[1];

                            // make sure in UI thread
                            this.shell.getDisplay().syncExec(new Runnable() {
                                /**
                                 * {@inheritDoc}
                                 * 
                                 * @see java.lang.Runnable#run()
                                 */
                                @Override
                                public void run() {
                                    if (MessageDialog.openQuestion(getShell(),
                                                                   UTIL.getString(PREFIX + "createDataSourcesConfirmation.title"), //$NON-NLS-1$
                                                                   UTIL.getString(PREFIX
                                                                                  + "createDataSourcesConfirmation.message", //$NON-NLS-1$
                                                                                  getVdbName()))) {
                                        result[0] = true;
                                    }
                                }
                            });

                            if (result[0]) {
                                createOnServer = true;
                            }
                        }

                        // TODO must also be able to create DS even if model not found in workspace
                        // if model found in workspace create data source on server
                        if ((autoCreate || createOnServer) && (modelFile != null)) {
                            monitor.subTask(UTIL.getString(PREFIX + "createModelDataSourceTask", modelName)); //$NON-NLS-1$

                            TeiidDataSourceFactory factory = new TeiidDataSourceFactory();
                            
                            ITeiidDataSource ds = factory.createDataSource(teiidServer, modelFile, jndiNameWithoutContext, false);
                            
                            if( ds == null ) {
                            	this.status = DeployStatus.CREATE_DATA_SOURCE_FAILED;
                                break; // don't try again to create a DS
                            } else if( ds instanceof FailedTeiidDataSource ) {
                            	this.status = DeployStatus.CREATE_DATA_SOURCE_FAILED;
                            	failedModelName = FileUtils.getNameWithoutExtension(modelFile);
                                break; // don't try again to create a DS
                            }
                        } else if (!hasJndiProblems) {
                            // DS doesn't exist and won't be auto-created, or model not in workspace
                            hasJndiProblems = true;
                        }
                    }
                }

                // ask user if they still want to deploy even though there are JNDI problems
                if (hasJndiProblems) {
                    // make sure in UI thread
                    this.shell.getDisplay().syncExec(new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (MessageDialog.openQuestion(getShell(),
                                                           UTIL.getString(PREFIX + "deployWithErrorsConfirmation.title"), //$NON-NLS-1$
                                                           UTIL.getString(PREFIX + "deployWithErrorsConfirmation.message"))) { //$NON-NLS-1$
                                setStatus(null); // user wants to deploy regardless of errors
                            } else {
                                setStatus(DeployStatus.DEPLOY_VDB_CANCELED);
                            }
                        }
                    });
                }
            }

            if (this.status == null) {
                monitor.subTask(UTIL.getString(PREFIX + "deployVdbTask", getVdbName())); //$NON-NLS-1$

             // VDB name can contain an integer value
             // VDB can also have a version in it's manifest (vdb.xml)
             //
             // EXAMPLE: Customers.2.vdb
             //
                String version = vdb.getVersion(); // Manifest version
                String versionInName = getVdbVersion(getVdbName()); // version in name
                if (versionInName != null) { // If version in name, then use it (i.e. ignore manifest version)
                    version = versionInName;
                }

                teiidServer.deployVdb(vdb.getSourceFile(), version);
                // VDB name may have a version in it, so need to strip off any extension
                this.status = (teiidServer.hasVdb(getVdbName()) ? DeployStatus.DEPLOYED_VDB : DeployStatus.DEPLOY_VDB_FAILED);
            }
        } catch (Exception e) {
            this.status = DeployStatus.EXCEPTION;
            this.error = e;
        } finally {
            monitor.done();
        }
        
        if( failedModelName != null ) {
        	return failedModelName;
        }
        
        return null;
    }
    
    private String getVdbVersion(String originalVdbName) throws Exception {
    	String vdbName = originalVdbName;
    	String vdbVersionStr = null;
    	int firstIndex = vdbName.indexOf('.');
    	int lastIndex = vdbName.lastIndexOf('.');
    	if (firstIndex != -1) {
    	if (firstIndex != lastIndex) {
    	// TODO:
    	throw new Exception(UTIL.getString(PREFIX + "vdbNameContainsTooManyDotsErrorMessage", originalVdbName)); //$NON-NLS-1$"VBD Version contains more than one '.'"); //Messages.getString(Messages.ExecutionAdmin.invalidVdbName, originalVdbName));
    	}
    	vdbVersionStr = vdbName.substring(firstIndex+1);
    	return vdbVersionStr;
    	}
    	return null;
    	}
    
    /*
     * Check the server sources to see if a datasource with the provided JNDI name exists.
     * @param jndiName the jndi name to check
     * @return 'true' if a source with a matching name is found, 'false' if not.
     */
    private boolean dataSourceWithJndiExists(String jndiName) {
    	if(jndiName==null || jndiName.isEmpty()) return false;
    	
    	boolean hasSourceWithJndi = false;
    	
    	Collection<ITeiidDataSource> serverSources = null; 
    	try {
    		serverSources = teiidServer.getDataSources();
		} catch (Exception ex) {
            DqpPlugin.Util.log(ex);
		}
    	
    	if(serverSources!=null && !serverSources.isEmpty()) {
    		for(ITeiidDataSource serverSource : serverSources) {
                String serverJndiName = serverSource.getPropertyValue(JNDI_PROPERTY_KEY);
                if(!CoreStringUtil.isEmpty(serverJndiName)) {
                	// Straight check
                	if(serverJndiName.equalsIgnoreCase(jndiName)) {
                		hasSourceWithJndi = true;
                		break;
                	}
                	// incoming jndiName may not have context, so try that also since server can match it
                	if(!JndiUtil.hasJavaPrefix(jndiName)) {
                		String jndiNameWithContext = JndiUtil.addJavaPrefix(jndiName);
                		if(serverJndiName.equalsIgnoreCase(jndiNameWithContext)) {
                			hasSourceWithJndi = true;
                			break;
                		}
                	}
                }
    		}
    	}
    	
    	return hasSourceWithJndi;
    }
    
    /*
     * Method to test whether the supplied source model has connection info
     * @param modelFile the supplied model file
     * @return 'true' if the modelResource has connection info, 'false' if not.
     */
    private boolean hasConnectionInfo(IFile modelFile) {
    	boolean hasConnInfo = true;
    	// Try to get the ConnectionInfoProvider.  Exception is thrown if the provider cannot be obtained (no connection info)
    	try {
			ModelResource modelResource = ModelUtil.getModelResource(modelFile, true);
			ConnectionInfoProviderFactory manager = new ConnectionInfoProviderFactory();
			manager.getProvider(modelResource);
		} catch (Exception ex) {
			hasConnInfo = false;
		}
    	return hasConnInfo;
    }

    /**
     * @return the name of the VDB being deployed (never <code>null</code>)
     */
    public String getVdbName() {
        return this.vdb.getName();
    }

    /**
     * @return the shell the deployer is using (never <code>null</code>)
     */
    Shell getShell() {
        return this.shell;
    }

    /**
     * @param modelEntry the model entry whose translator entry is being validated
     * @return <code>true</code> if model entry has a valid translator
     * @throws Exception
     */
    private boolean hasValidTranslator( VdbModelEntry modelEntry ) throws Exception {
        // assertion: must be a source model
        String translatorName = modelEntry.getSourceInfo().getSource(0).getTranslatorName();

        // must have a translator
        if (StringUtilities.isEmpty(translatorName)) {
            return false;
        }

        // make sure server has translator with that name
        boolean isValid = (teiidServer.getTranslator(translatorName) != null);
        
        // Check for overridden translator names
        if( !isValid && !this.vdb.getTranslators().isEmpty()) {
        	for( TranslatorOverride override : this.vdb.getTranslators() ) {
        		if( override.getName().equalsIgnoreCase(translatorName) ) {
        			isValid = (teiidServer.getTranslator(override.getType()) != null);
        			break;
        		}
        	}
        }

        
        return isValid;
    }

    /**
     * @param status the new status (can be <code>null</code>)
     */
    void setStatus( DeployStatus status ) {
        this.status = status;
    }

}

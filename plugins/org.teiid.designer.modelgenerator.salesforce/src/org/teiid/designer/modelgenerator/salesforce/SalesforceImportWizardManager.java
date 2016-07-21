/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce;

import static org.teiid.designer.modelgenerator.salesforce.SalesforceConstants.NAMESPACE_PROVIDER;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.compare.DifferenceProcessor;
import org.teiid.designer.compare.DifferenceReport;
import org.teiid.designer.compare.MergeProcessor;
import org.teiid.designer.compare.ModelerComparePlugin;
import org.teiid.designer.compare.util.CompareUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.modelgenerator.salesforce.connection.SalesforceConnection;
import org.teiid.designer.modelgenerator.salesforce.model.DataModel;
import org.teiid.designer.modelgenerator.salesforce.model.impl.DataModelImpl;
import org.teiid.designer.modelgenerator.salesforce.util.ModelBuildingException;
import org.teiid.designer.modelgenerator.salesforce.util.SalesForceConnectionInfoProvider;
import org.teiid.designer.runtime.spi.ITeiidServer;


/**
 * @since 8.0
 */
public class SalesforceImportWizardManager {

    public static final int WORKSPACE_SOURCE = 0;
    public static final int FILESYSTEM_SOURCE = 1;
    public static final int URL_SOURCE = 2;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private DataModel dataModel;
    private IConnectionProfile connectionProfile;
    private String targetModelName;
    private IContainer targetModelLocation;
    private boolean modelAuditFields;
    private boolean credentialsValid;
    private ModelResource updateModel;
    private boolean canFinish = false;
    private DifferenceReport diffReport;
    private DifferenceProcessor diffProcessor;
    private ModelResource tempModel;
    private boolean supressCollectCardinalities;
    private boolean collectColumnDistinctValue;
    private SalesforceConnection connection;
    private boolean setNameAsNameInSource;
    private boolean generateUpdated;
    private boolean generateDeleted;
	private String jbossJndiName;
	private boolean autoCreateDataSource = true;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public SalesforceImportWizardManager() {
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the WSDL Model. If the current WSDL is not valid or has not been specified an exception will be thrown.
     * 
     * @return the WSDL Model
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    /**
     * Get the currently specified location where the target Model is to be generated.
     * 
     * @return the target Model location
     */
    public IContainer getTargetModelLocation() {
        return this.targetModelLocation;
    }

    /**
     * Set the location where the target Model is to be generated.
     * 
     * @param targetModelLocation the target Model location
     */
    public void setTargetModelLocation( IContainer targetModelLocation ) {
        this.targetModelLocation = targetModelLocation;
    }

    /**
     * Get the name of the target relational model to be generated.
     * 
     * @return the target Model Name
     */
    public String getTargetModelName() {
        return this.targetModelName;
    }

    /**
     * Set the name of the target relational Model.
     * 
     * @param targetModelName the target Model Name
     */
    public void setTargetModelName( String targetModelName ) {
        this.targetModelName = targetModelName;
    }

    /**
     * @return connectionProfile
     */
    public IConnectionProfile getConnectionProfile() {
        return connectionProfile;
    }

    /**
     * @param connectionProfile Sets connectionProfile to the specified value.
     */
    public void setConnectionProfile( IConnectionProfile connectionProfile ) {
        this.connectionProfile = connectionProfile;
    }

    public void clear() {
        dataModel = null;
        targetModelName = null;
        targetModelLocation = null;
    }

    public boolean validateCredentials( IProgressMonitor monitor ) throws Throwable {
        monitor.beginTask(Messages.getString("SalesforceImportWizardManager.validating.credentials"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        SalesforceConnection conn = getConnection();
        return null != conn;
    }

    public SalesforceConnection getConnection() throws Throwable {
        if (null == connection) {

            try {
                IStatus status = connectionProfile.connect();
                if (!status.isOK()) {
                	connection = null;
//                	Throwable exception = status.getException();
//                	if( exception != null ) {
//                		throw exception;
//                	}
//                	
//                    throw new Exception( status.getMessage());
                }
                IConnection conn = connectionProfile.createConnection("org.teiid.designer.datatools.salesforce.ConnectionFactory"); //$NON-NLS-1$
                connection = (SalesforceConnection)conn.getRawConnection();
            } catch (Exception e) {
                connection = null;
                throw e;
            }

        }
        return connection;
    }

    public void runFinish( IProgressMonitor monitor ) throws Exception {

        if (diffReport != null) {
            // Check if the model being updated supports the SF namespace. If not, add the SF MED.

            // Find the SF assistant in the registry...
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());

            // If namespace is not yet supported, add the MED
            if (!assistant.supportsMyNamespace(this.updateModel)) {
                // Get the SF definition - should be found in the registry
                ModelExtensionDefinition definition = assistant.getModelExtensionDefinition();

                // add Salesforce MED
                if (this.updateModel != null && definition != null) {
                    assistant.saveModelExtensionDefinition(this.updateModel);
                }
            }

            final EObject[] externalReferences = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
            final MergeProcessor mergeProc = ModelerComparePlugin.createMergeProcessor(diffProcessor, externalReferences, true);
            mergeProc.execute(monitor);
            tempModel = null;
        } else {
            ModelResource modelResource = createModel(monitor, targetModelName);
            if (!monitor.isCanceled()) {
                monitor.subTask(Messages.getString("SalesforceImportWizardManager.binding")); //$NON-NLS-1$
                SalesForceConnectionInfoProvider helper = new SalesForceConnectionInfoProvider();
                helper.setConnectionInfo(modelResource, connectionProfile);
          		String jndiName = getJBossJndiName();
          		if( !StringUtilities.isEmpty(jndiName) ) {
          			helper.setJNDIName(modelResource, jndiName);
          		}
                  
                handleCreateDataSource(modelResource);
                
                try {
                    monitor.subTask(Messages.getString("SalesforceImportWizardManager.saving.model")); //$NON-NLS-1$
                    modelResource.save(monitor, false);
                    monitor.worked(1);
                } catch (ModelWorkspaceException e) {
                    ModelBuildingException mbe = new ModelBuildingException();
                    mbe.initCause(e);
                    throw mbe;
                }

            }
        }
    }

    private ModelResource createModel( IProgressMonitor monitor,
                                       String modelName ) throws ModelBuildingException, Exception {
        monitor.beginTask(Messages.getString("SalesforceImportWizardManager.creating.salesforce.model"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        Path path = new Path(modelName);
        IFile iFile = targetModelLocation.getFile(path);
        ModelResource modelResource = ModelerCore.create(iFile);

        Resource resource;
        try {
            resource = modelResource.getEmfResource();
        } catch (ModelWorkspaceException e) {
            throw new ModelBuildingException(e);
        }

        RelationalModelgenerator builder = new RelationalModelgenerator(this, monitor);
        builder.createRelationalModel(modelResource, resource);
        return modelResource;
    }

    public DataModel createDataModel( IProgressMonitor monitor ) throws Throwable {
        if (null == dataModel) {
            monitor.setTaskName(Messages.getString("SalesforceImportWizardManager.connecting")); //$NON-NLS-1$
            SalesforceConnection conn = getConnection();
            dataModel = new DataModelImpl();
            monitor.setTaskName(Messages.getString("SalesforceImportWizardManager.gathering.metadata")); //$NON-NLS-1$
            dataModel.load(conn, monitor);
        }
        return dataModel;
    }

    public boolean isModelAuditFields() {
        return modelAuditFields;
    }

    public void modelAuditFields( boolean supressAuditFields ) {
        this.modelAuditFields = supressAuditFields;
    }

    public void setCredentialsValid( boolean valid ) {
        credentialsValid = valid;
    }

    public boolean hasValidCredentials() {
        return credentialsValid;
    }

    public DifferenceReport getDifferenceReport( IProgressMonitor monitor ) throws ModelBuildingException, Exception {
        String tempName = Long.toString(System.currentTimeMillis()) + "_temp.xmi"; //$NON-NLS-1$
        tempModel = createModel(monitor, tempName);
        diffProcessor = ModelerComparePlugin.createDifferenceProcessor(updateModel, tempModel);
        diffProcessor.execute(monitor);
        diffReport = diffProcessor.getDifferenceReport();
        CompareUtil.skipDeletesOfStandardContainers(diffReport);
        return diffReport;
    }

    public void setUpdatedModel( ModelResource model ) {
        this.updateModel = model;
    }

    public boolean canFinish() {
        return canFinish;
    }

    public void setCanFinish( boolean canFinish ) {
        this.canFinish = canFinish;
    }

    public void supressCollectCardinalities( boolean selection ) {
        this.supressCollectCardinalities = selection;
    }

    boolean getSupressCollectCardinalities() {
        return this.supressCollectCardinalities;
    }

    public void setCollectColumnDistinctValue( boolean selection ) {
        this.collectColumnDistinctValue = selection;
    }

    public void setNameAsLabel( boolean selection ) {
        this.setNameAsNameInSource = selection;
    }

    public boolean isSetNameAsLabel() {
        return setNameAsNameInSource;
    }

    public void setSetNameAsNameInSource( boolean setNameAsNameInSource ) {
        this.setNameAsNameInSource = setNameAsNameInSource;
    }

    public boolean isCollectColumnDistinctValue() {
        return collectColumnDistinctValue;
    }

    public void setGenerateUpdated( boolean selection ) {
        this.generateUpdated = selection;
    }

    public boolean isGenerateUpdated() {
        return generateUpdated;
    }

    public void setGenerateDeleted( boolean selection ) {
        this.generateDeleted = selection;
    }

    public boolean isGenerateDeleted() {
        return generateDeleted;
    }
    
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public String getJBossJndiName() {
        return this.jbossJndiName;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setJBossJndiNameName(String jndiName) {
		this.jbossJndiName = jndiName;
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public boolean doCreateDataSource() {
        return this.autoCreateDataSource;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setCreateDataSource(boolean value) {
		this.autoCreateDataSource = value;
	}
	
    protected void handleCreateDataSource(ModelResource model) {
    	if( doCreateDataSource() && DataSourceConnectionHelper.isServerConnected() ) {
            ITeiidServer teiidServer = DataSourceConnectionHelper.getServer();
            
    		String dsName = getJBossJndiName();
    		String jndiName = getJBossJndiName();
    		DataSourceConnectionHelper helper = new DataSourceConnectionHelper(model, getConnectionProfile());
    		
        	Properties connProps = helper.getModelConnectionProperties();
        	
        	String dsType = helper.getDataSourceType();
    		try {
				teiidServer.getOrCreateDataSource(dsName, jndiName, dsType, connProps);
			} catch (Exception e) {
	            IStatus status = new org.eclipse.core.runtime.Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
	            Activator.getDefault().getLog().log(status);
			}
    	}
    }
}

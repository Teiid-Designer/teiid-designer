/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.compare.DifferenceProcessor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.MergeProcessor;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.SalesforceConnection;
import com.metamatrix.modeler.modelgenerator.salesforce.connection.impl.Connection;
import com.metamatrix.modeler.modelgenerator.salesforce.model.DataModel;
import com.metamatrix.modeler.modelgenerator.salesforce.model.impl.DataModelImpl;
import com.metamatrix.modeler.modelgenerator.salesforce.util.ModelBuildingException;

public class SalesforceImportWizardManager {

	public static final int WORKSPACE_SOURCE = 0;
	public static final int FILESYSTEM_SOURCE = 1;
	public static final int URL_SOURCE = 2;
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private DataModel dataModel;
    
    private String targetModelName;
    private IContainer targetModelLocation;
	private String username;
	private String password;
	private URL connectionURL;
	private boolean supressAuditFields;
	private boolean credentialsValid;
	private boolean hasCredentialChanges;
	private ModelResource updateModel;
	private boolean canFinish = false;
	private DifferenceReport diffReport;
	private DifferenceProcessor diffProcessor;
	private ModelResource tempModel;
	// /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public SalesforceImportWizardManager() {
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the WSDL Model.  If the current WSDL is not valid or has not been specified
     * an exception will be thrown.
     * @return the WSDL Model
     * @throws Exception 
     * @throws ModelGenerationException 
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    
    /** 
     * Get the currently specified location where the target Model is to be generated.
     * @return the target Model location
     */
    public IContainer getTargetModelLocation() {
        return this.targetModelLocation;
    }

    
    /** 
     * Set the location where the target Model is to be generated.
     * @param targetModelLocation the target Model location
     */
    public void setTargetModelLocation(IContainer targetModelLocation) {
        this.targetModelLocation = targetModelLocation;
    }

    
    /** 
     * Get the name of the target relational model to be generated.
     * @return the target Model Name
     */
    public String getTargetModelName() {
        return this.targetModelName;
    }

    
    /** 
     * Set the name of the target relational Model.
     * @param targetModelName the target Model Name
     */
    public void setTargetModelName(String targetModelName) {
        this.targetModelName = targetModelName;
    }
	
    public URL getConnectionURL() {
		return connectionURL;
	}
	
	public void setConnectionURL(URL connectionURL) {
		this.connectionURL = connectionURL;
		setCredentialChanges(true);
	}

	public void setUsername(String username) {
		this.username = username;
		setCredentialChanges(true);
	}

	public void setPassword(String password) {
		this.password = password;
		setCredentialChanges(true);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void clear() {
		dataModel = null;
		targetModelName = null;
		targetModelLocation = null;
		username = null;
		password = null;
	}

	public boolean validateCredentials(IProgressMonitor monitor) throws Exception {
		monitor.beginTask(Messages.getString("SalesforceImportWizardManager.validating.credentials"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		SalesforceConnection conn = getConnection();
		return null != conn;
	}

	private SalesforceConnection getConnection() throws Exception {
		SalesforceConnection conn = new Connection();
		conn.login(username, password, connectionURL);
		return conn;
	}

	public void runFinish(IProgressMonitor monitor)
			throws Exception {

        if (diffReport != null) {
        	final EObject[] externalReferences = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
        	final MergeProcessor mergeProc 
            = ModelerComparePlugin.createMergeProcessor(diffProcessor, 
                                                        externalReferences, 
                                                        true);
        	mergeProc.execute(monitor);
        	tempModel = null;
		} else {
			ModelResource modelResource = createModel(monitor, targetModelName);

			try {
				modelResource.save(monitor, false);
			} catch (ModelWorkspaceException e) {
				ModelBuildingException mbe = new ModelBuildingException();
				mbe.initCause(e);
				throw mbe;
			}
			BindingGenerator.createConnectorBinding(this);
		}
	}

	private ModelResource createModel(IProgressMonitor monitor, String modelName)
			throws ModelBuildingException, Exception {
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
		
		RelationalModelgenerator builder = new RelationalModelgenerator(targetModelLocation, monitor, dataModel,
				getConnection(), isSupressAuditFields());
		builder.createRelationalModel(resource);
		return modelResource;
	}

	public DataModel createDataModel(IProgressMonitor monitor) throws Exception {
		if(null == dataModel || hasCredentialChanges()) {
        	SalesforceConnection conn = getConnection();
        	dataModel = new DataModelImpl();
        	monitor.setTaskName(Messages.getString("SalesforceImportWizardManager.gathering.metadata")); //$NON-NLS-1$
        	dataModel.load(conn, monitor);
        	hasCredentialChanges = false;
        }
		return dataModel;
	}

	public boolean isSupressAuditFields() {
		return supressAuditFields;
	}

	public void setSupressAuditFields(boolean supressAuditFields) {
		this.supressAuditFields = supressAuditFields;
	}

	public void setCredentialsValid(boolean valid) {
		credentialsValid = valid;
	}

	public boolean hasValidCredentials() {
		return credentialsValid;
	}
	
	public boolean hasCredentialChanges() {
		return hasCredentialChanges;
	}
	
	private void setCredentialChanges(boolean changed) {
		hasCredentialChanges = changed;
	}

	public DifferenceReport getDifferenceReport(IProgressMonitor monitor) throws ModelBuildingException, Exception {
		String tempName = Long.toString(System.currentTimeMillis()) + "_temp.xmi"; //$NON-NLS-1$
		tempModel = createModel(monitor, tempName);
		diffProcessor = ModelerComparePlugin.createDifferenceProcessor(updateModel, tempModel);
		diffProcessor.execute(monitor);
		diffReport = diffProcessor.getDifferenceReport();
		return diffReport;
	}

	public void setUpdatedModel(ModelResource model) {
		this.updateModel = model;
	}

	public boolean canFinish() {
		return canFinish ;
	}
	
	public void setCanFinish(boolean canFinish) {
		this.canFinish = canFinish ;
	}
}

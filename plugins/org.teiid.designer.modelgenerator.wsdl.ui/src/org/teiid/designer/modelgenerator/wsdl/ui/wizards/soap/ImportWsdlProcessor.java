/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.util.NewModelObjectHelperManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.modelgenerator.wsdl.WSSoapConnectionInfoProvider;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.model.Port;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.query.proc.wsdl.IWsdlAttributeInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import org.teiid.designer.transformation.util.SqlMappingRootCache;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class ImportWsdlProcessor {
	public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
	public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
	public static final int DEFAULT_STRING_LENGTH = 4000;

	
	WSDLImportWizardManager importManager;
	ModelResource sourceModel;
	ModelResource viewModel;
	IStatus createStatus;
	Shell shell;
	
	RelationalViewModelFactory relationalFactory;
	FlatFileRelationalModelFactory relationalProcedureFactory;
	
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    
    
	
	
	public ImportWsdlProcessor(WSDLImportWizardManager importManager, Shell shell) {
		super();
		this.importManager = importManager;
		this.shell = shell;
		createStatus = Status.OK_STATUS;
		this.relationalFactory = new RelationalViewModelFactory();
		this.relationalProcedureFactory = new FlatFileRelationalModelFactory();
	}
	
	private void log(IStatus status) {
		ModelGeneratorWsdlUiConstants.UTIL.log(status);
	}
	
	public IStatus execute() {
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {
            	theMonitor.beginTask(("Creating source model" + importManager.getSourceModelName()), 100); //$NON-NLS-1$
                
            	// Make sure models are found or created if they don't exist yet
            	createStatus = initializeModels(theMonitor);
            	
            	if( createStatus.isOK() ) {
            		createStatus = createSourceProcedures(theMonitor);
            	}
            	
            	if( createStatus.isOK()) {
            		createViewProcedures(theMonitor);
            	}
                theMonitor.worked(50);
                
                if( createStatus.isOK() ) {
                	handleCreateDataSource();
                }
                
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(shell).run(false, true, op);
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            log( new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, e.getTargetException().getMessage(), e));
        } catch (final Exception err) {
            Throwable t = err;

            if (err instanceof InvocationTargetException) {
                t = err.getCause();
            }

            WidgetUtil.showError(t);
        }
        
		return createStatus;
	}
	
	
	private IStatus initializeModels(IProgressMonitor theMonitor) {
		// Check if source and view models exist
		
		try {
			if( importManager.sourceModelExists() ) {
				// find and set source model resource
				IPath modelPath = this.importManager.getSourceModelLocation().getFullPath()
					.append(this.importManager.getSourceModelName());
				ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
				IFile modelFile = (IFile)item.getCorrespondingResource();
				this.sourceModel = ModelUtilities.getModelResourceForIFile(modelFile, false);
				addConnectionProfileInfoToSourceModel(theMonitor, true);
			} else {
				// Create Source Model
				createSourceModelInTxn(theMonitor);
			}
			
			if( importManager.viewModelExists() ) {
				// Find and set view model resource
				IPath modelPath = this.importManager.getViewModelLocation().getFullPath().append(this.importManager.getViewModelName());
				ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
				IFile modelFile = (IFile)item.getCorrespondingResource();
				this.viewModel = ModelUtilities.getModelResourceForIFile(modelFile, false);
			} else {
				// Create view model
				createViewModelInTxn(theMonitor);
			}
		} catch (ModelWorkspaceException ex) {
			return new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, ex.getMessage());
		} catch (InvocationTargetException ex) {
			return new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, ex.getMessage());
		}
		
		return Status.OK_STATUS;
	}
	
    private IStatus createSourceModelInTxn(IProgressMonitor theMonitor) {
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Import Teiid Metadata Create Source Model", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
         	sourceModel = relationalFactory.createRelationalModel( this.importManager.getSourceModelLocation(), 
        		this.importManager.getSourceModelName() );
         	theMonitor.worked(10);
        	
        	addConnectionProfileInfoToSourceModel(theMonitor, false);
        	
    		String jndiName = importManager.getJBossJndiName();
    		if( !StringUtilities.isEmpty(jndiName) ) {
    			ConnectionInfoHelper helper = new ConnectionInfoHelper();
    			helper.setJNDIName(sourceModel, jndiName);
    		}
        	
        	theMonitor.subTask("Saving Source Model" + sourceModel.getItemName()); //$NON-NLS-1$
            try {
                ModelUtilities.saveModelResource(sourceModel, theMonitor, false, this);
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
            theMonitor.worked(10);
            if( createStatus.isOK() && sourceModel != null ) {
            	ModelEditorManager.openInEditMode(sourceModel, true, org.teiid.designer.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
            }
        	succeeded = true;
        } catch (Exception e) {
        	String message = NLS.bind(Messages.Error_Creating_0_Model_1_FromWsdl, "view", this.importManager.getSourceModelName()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, message, e);
            log(status);
            MessageDialog.openError(shell, message, e.getMessage());
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        theMonitor.worked(10);
        
        return Status.OK_STATUS;
    }
    
    protected void addConnectionProfileInfoToSourceModel(IProgressMonitor monitor, boolean doSave) throws InvocationTargetException {
    	IConnectionProfile profile = this.importManager.getConnectionProfile();
    	if( monitor == null ) {
    		return;
    	}
    	// Inject the connection profile info into the model
    	if (profile != null) {
    		try {
    			Properties props = profile.getBaseProperties();
    			String defaultServiceMode = this.importManager.getTranslatorDefaultServiceMode();
    			if( defaultServiceMode.equalsIgnoreCase(WSDLImportWizardManager.MESSAGE ) ) {
    				props.put(IWSProfileConstants.SOAP_SERVICE_MODE, WSDLImportWizardManager.MESSAGE);
    			} else {
    				props.put(IWSProfileConstants.SOAP_SERVICE_MODE, WSDLImportWizardManager.PAYLOAD);
    			}
    			//The CP doesn't allow changing the BINDING, so the default will always return SOAP11
    			// If this wizard returns SOAP12, then we need to replace the binding
    			String defaultBinding = this.importManager.getTranslatorDefaultBinding();
    			if( defaultBinding.equalsIgnoreCase(Port.SOAP12) ) {
    				props.put(IWSProfileConstants.SOAP_BINDING, Port.SOAP12);
    			}
    			
    			profile.setBaseProperties(props);
    			IConnectionInfoProvider provider = new WSSoapConnectionInfoProvider();
    			provider.setConnectionInfo(sourceModel, profile);
    		
    		} catch (ModelerCoreException ex) {
    				log(new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, ex.getMessage(), ex));
    		}
    		if( doSave ) {
                try {
                    ModelUtilities.saveModelResource(sourceModel, monitor, false, this);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
    		}
        }
    }

    private IStatus createSourceProcedures(IProgressMonitor monitor) {
    	try {
			relationalProcedureFactory.addMissingProcedure(this.sourceModel, FlatFileRelationalModelFactory.INVOKE);
		} catch (ModelerCoreException ex) {
			IStatus status = new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, ex.getMessage(), ex);
			log(status);
			return status;
		}
    	
    	return Status.OK_STATUS;
    }
    
    private IStatus createViewModelInTxn(IProgressMonitor monitor) {
    	
        boolean requiredStart = ModelerCore.startTxn(true, true, "Create WSDL Import View Model", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	monitor.subTask("Creating View Procedures" + this.importManager.getViewModelName()); //$NON-NLS-1$
        	
        	this.viewModel = relationalFactory.createRelationalViewModel(
        		this.importManager.getViewModelLocation(), 
        		this.importManager.getViewModelName() );
        	
        	monitor.worked(40);
            if( createStatus.isOK() && sourceModel != null ) {
                try {
                    ModelUtilities.saveModelResource(viewModel, monitor, false, this);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }
            monitor.worked(10);

            if( createStatus.isOK() && viewModel != null ) {
            	ModelEditorManager.openInEditMode(viewModel, true, org.teiid.designer.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
            }
        	succeeded = true;
        } catch (Exception e) {
        	String message = NLS.bind(Messages.Error_Creating_0_Model_1_FromWsdl, "view", this.importManager.getViewModelName()); //$NON-NLS-1$
        	IStatus status = new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, message, e);
            log(status);
            MessageDialog.openError(shell, message, e.getMessage());
            return status;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private void createViewProcedures(IProgressMonitor monitor) {
    	// Assume the source and view models are created
    	
    	removeOverriddenProcedures();
    	
    	for( Operation operation : this.importManager.getSelectedOperations()) {
    		ProcedureGenerator generator = this.importManager.getProcedureGenerator(operation);
    		processGenerator(generator);
    	}
    }
    
    /*
     * Deletes all existing procedures tagged for being overridden
     */
    private void removeOverriddenProcedures() {
    	for( Operation operation : this.importManager.getSelectedOperations()) {
    		ProcedureGenerator generator = this.importManager.getProcedureGenerator(operation);
    
    		if( generator.doOverwriteExistingProcedures() ) {
    			Collection<EObject> deleteList = new ArrayList<EObject>();
    			if( generator.wrapperExists() ) {
    				EObject wrapper = ModelUtilities.getExistingEObject(
    					this.importManager.getViewModelLocation().getFullPath().toString(), 
    					this.importManager.getViewModelName(), 
    					generator.getWrapperProcedureName());
    				if( wrapper != null ) {
    					deleteList.add(wrapper);
    				}
    			}
    			EObject request = ModelUtilities.getExistingEObject(
    				this.importManager.getViewModelLocation().getFullPath().toString(), 
    				this.importManager.getViewModelName(), 
    				generator.getRequestProcedureName());
    			if( request != null ) {
    				deleteList.add(request);
    			}
    			EObject response = ModelUtilities.getExistingEObject(
    				this.importManager.getViewModelLocation().getFullPath().toString(), 
    				this.importManager.getViewModelName(), 
    				generator.getResponseProcedureName());
    			if( response != null ) {
    				deleteList.add(response);
    			}
    			if( !deleteList.isEmpty() ) {
    				try {
						ModelerCore.getModelEditor().delete(deleteList);
					} catch (ModelerCoreException ex) {
						ModelGeneratorWsdlUiConstants.UTIL.log(ex);
					}
    			}
    		}
    	}
    }
    
    private void processGenerator(ProcedureGenerator generator) {

		try {
			// Create the Request Procedure
			createViewRequestProcedure(this.viewModel, (RequestInfo)generator.getRequestInfo());
			// Create the Response ProceduregetWrapperProcedureSqlString
			createViewResponseProcedure(this.viewModel, (ResponseInfo)generator.getResponseInfo());
			// Create the wrapper procedure
			createViewWrapperProcedure(this.viewModel, generator);
		} catch (ModelWorkspaceException ex) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		} catch (ModelerCoreException ex) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		}
			
    }
    
    public void createViewRequestProcedure(ModelResource modelResource, RequestInfo info) throws ModelerCoreException {
    	final EObject STRING_DATATYPE = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	// Create a Procedure using the text file name
    	Procedure procedure = factory.createProcedure();
    	procedure.setName(info.getProcedureName());
    	
    	addValue(modelResource, procedure, modelResource.getEmfResource().getContents());
    	
    	NewModelObjectHelperManager.helpCreate(procedure, null);
    	
    	for(ColumnInfo columnInfo : info.getHeaderColumnInfoList()) {
    		ProcedureParameter parameter = factory.createProcedureParameter();
    		parameter.setName(columnInfo.getName());
    		EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if( type != null ) {
    			parameter.setType(type);
    		}
    		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
    			parameter.setLength(DEFAULT_STRING_LENGTH);
    		}
    		parameter.setProcedure(procedure);
    	}
    	
    	for(ColumnInfo columnInfo : info.getBodyColumnInfoList() ) {
    		ProcedureParameter parameter = factory.createProcedureParameter();
    		parameter.setName(columnInfo.getName());
    		EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if( type != null ) {
    			parameter.setType(type);
    		}
    		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
    			parameter.setLength(DEFAULT_STRING_LENGTH);
    		}
    		parameter.setProcedure(procedure);
    		for( IWsdlAttributeInfo attrInfo : columnInfo.getAttributeInfoArray() ) {
        		ProcedureParameter attributeParam = factory.createProcedureParameter();
        		attributeParam.setName(attrInfo.getName());
        		attributeParam.setType(STRING_DATATYPE);
        		attributeParam.setLength(DEFAULT_STRING_LENGTH);
        		attributeParam.setProcedure(procedure);
    		}
    	}
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setName("Result"); //$NON-NLS-1$
    	result.setProcedure(procedure);
    	Column column_1 = factory.createColumn();
    	column_1.setName("xml_out"); //$NON-NLS-1$
    	EObject blobType = datatypeManager.findDatatype("XMLLiteral"); //$NON-NLS-1$
    	if( blobType != null) {
    		column_1.setType(blobType);
    	}
    	addValue(result, column_1, result.getColumns());
    	
    	String sqlString = info.getSqlString(new Properties());
    	
    	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
    	
    	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);

        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
        
        QueryValidator validator = new TransformationValidator(tRoot);
        
        validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
    	
    }
    
    public void createViewResponseProcedure(ModelResource modelResource, ResponseInfo info) throws ModelerCoreException {
    	
    	// Create a Procedure using the text file name
    	Procedure procedure = factory.createProcedure();
    	procedure.setName(info.getProcedureName());
    	
    	addValue(modelResource, procedure, modelResource.getEmfResource().getContents());
		ProcedureParameter parameter = factory.createProcedureParameter();
		
		parameter.setName("xml_in"); //$NON-NLS-1$
		EObject stringType = datatypeManager.findDatatype("XMLLiteral"); //$NON-NLS-1$
		parameter.setType(stringType);
		parameter.setProcedure(procedure);
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setName("Result"); //$NON-NLS-1$
    	result.setProcedure(procedure);
    	
    	for(ColumnInfo columnInfo : info.getBodyColumnInfoList() ) {
        	Column column = factory.createColumn();
        	column.setName(columnInfo.getName());
        	EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
        	if( type != null) {
        		column.setType(type);
        		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
        			column.setLength(DEFAULT_STRING_LENGTH);
        		}
        	}
        	addValue(result, column, result.getColumns());
    	}

    	NewModelObjectHelperManager.helpCreate(procedure, null);
    	String sqlString = info.getSqlString(new Properties());
    	
    	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
    	
    	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);

        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
        
        QueryValidator validator = new TransformationValidator(tRoot);
        
        validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
    	
    }
    
    private void createViewWrapperProcedure(ModelResource modelResource, ProcedureGenerator generator) throws ModelerCoreException {
    	final EObject STRING_DATATYPE = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	// Create a Procedure using the text file name
    	Procedure procedure = factory.createProcedure();
    	procedure.setName(generator.getWrapperProcedureName());
    	
    	addValue(modelResource, procedure, modelResource.getEmfResource().getContents());
    	
    	for(ColumnInfo columnInfo : generator.getRequestInfo().getHeaderColumnInfoList() ) {
    		ProcedureParameter parameter = factory.createProcedureParameter();
    		parameter.setName(columnInfo.getName());
    		EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if( type != null ) {
    			parameter.setType(type);
        		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
        			parameter.setLength(DEFAULT_STRING_LENGTH);
        		}
    		}
    		parameter.setProcedure(procedure);
    	}
    	
    	for(ColumnInfo columnInfo : generator.getRequestInfo().getBodyColumnInfoList() ) {
    		ProcedureParameter parameter = factory.createProcedureParameter();
    		parameter.setName(columnInfo.getName());
    		EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if( type != null ) {
    			parameter.setType(type);
        		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
        			parameter.setLength(DEFAULT_STRING_LENGTH);
        		}
    		}
    		parameter.setProcedure(procedure);
    		
    		for( IWsdlAttributeInfo attrInfo : columnInfo.getAttributeInfoArray() ) {
        		ProcedureParameter attributeParam = factory.createProcedureParameter();
        		attributeParam.setName(attrInfo.getName());
        		attributeParam.setType(STRING_DATATYPE);
        		attributeParam.setLength(DEFAULT_STRING_LENGTH);
        		attributeParam.setProcedure(procedure);
    		}
    	}
    	
    	
    	// Create Result set with same columns and types as Response procedure
    	ProcedureResult result = factory.createProcedureResult();
    	result.setName("Result"); //$NON-NLS-1$
    	result.setProcedure(procedure);
    	
    	for(ColumnInfo columnInfo : generator.getResponseInfo().getBodyColumnInfoList() ) {
        	Column column = factory.createColumn();
        	column.setName(columnInfo.getName());
        	EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
        	if( type != null) {
        		column.setType(type);
        		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
        			column.setLength(DEFAULT_STRING_LENGTH);
        		}
        	}
        	addValue(result, column, result.getColumns());
    	}
    	
    	NewModelObjectHelperManager.helpCreate(procedure, null);
    	
    	String sqlString = generator.getWrapperProcedureSqlString(new Properties());
    	
    	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
    	
    	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);
    	
    	SqlMappingRootCache.setStatus(tRoot, QueryValidator.SELECT_TRNS, null);

        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
        
        QueryValidator validator = new TransformationValidator(tRoot);
        
        validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
        
    }
    
    protected void addValue(final Object owner, final Object value, EList feature) throws ModelerCoreException {
        if( isTransactionable ) {
            ModelerCore.getModelEditor().addValue(owner, value, feature);
        } else {
            feature.add(value);
        }
    }
    
    protected void handleCreateDataSource() {
    	if( importManager.doCreateDataSource() && DataSourceConnectionHelper.isServerConnected() ) {
            ITeiidServer teiidServer = DataSourceConnectionHelper.getServer();
            
    		String dsName = importManager.getJBossJndiName();
    		String jndiName = importManager.getJBossJndiName();
    		DataSourceConnectionHelper helper = new DataSourceConnectionHelper(this.sourceModel, importManager.getConnectionProfile());
    		
        	Properties connProps = helper.getModelConnectionProperties();
        	
        	String dsType = helper.getDataSourceType();
    		try {
				teiidServer.getOrCreateDataSource(dsName, jndiName, dsType, connProps);
			} catch (Exception e) {
				DatatoolsUiConstants.UTIL.log(e);
			}
    	}
    }
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.util.ModelGeneratorWsdlUiUtil;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.proc.wsdl.IWsdlWrapperInfo;
import org.teiid.designer.query.sql.ISQLConstants;


/** This class provides state information for the create and extract procedures that will be generated during
 * WSDL import and model/procedure generation
 * 
 * There will be both Request and Response information managed by this class.
 * 
 * 
 *
 * @since 8.0
 */
public class ProcedureGenerator implements IWsdlWrapperInfo, ISQLConstants, ModelGeneratorWsdlUiConstants {
	
	private static final StringNameValidator nameValidator = new RelationalStringNameValidator(false, true);

	private RequestInfo requestInfo;
	private ResponseInfo responseInfo;
	
	private boolean generateWrapperProcedure;
	
	private String wrapperProcedureName;
	
	private boolean overwriteExistingProcedures;
	
	private Operation operation;
	
	private String soapAction;
	
	private String namespaceURI;
	
	private String bindingType;
	
	private WSDLImportWizardManager importManager;
	
	private boolean wrapperExists = false;
	
	private boolean changed;
	
	private boolean initializing;
	

	public ProcedureGenerator(Operation operation, WSDLImportWizardManager importManager) {
		super();
		this.initializing = true;
		this.operation = operation;
		this.importManager = importManager;
		this.requestInfo = new RequestInfo(operation, this);
		this.responseInfo = new ResponseInfo(operation, this);
		//this.importManager.setViewModelName(operation.getBinding().getPort().getService().getName() + "View"); //$NON-NLS-1$
		this.generateWrapperProcedure = true;
		this.namespaceURI = operation.getBinding().getPort().getNamespaceURI();
		this.bindingType = operation.getBinding().getPort().getBindingType();
		this.soapAction = operation.getSOAPAction();
		this.initializing = false;
	}

	WSDLImportWizardManager getImportManager() {
		return this.importManager;
	}
	
	@Override
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}

	@Override
	public ResponseInfo getResponseInfo() {
		return this.responseInfo;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return this.operation;
	}
	
	void setRequestProcedureName(String name ) {
		this.requestInfo.setProcedureName(name);
	}
	
	void setResponseProcedureName(String name ) {
		this.responseInfo.setProcedureName(name);
	}
	
	String getRequestProcedureName() {
		return this.requestInfo.getProcedureName();
	}
	
    String getResponseProcedureName() {
		return this.responseInfo.getProcedureName();
	}
	
	@Override
	public String getViewModelName() {
		String fullName = this.importManager.getViewModelName();
		if( fullName.toUpperCase().endsWith(".XMI") ) { //$NON-NLS-1$
			// remove XMI
			int endIndex = fullName.length() - 4;
			return fullName.substring(0, endIndex);
		}
		return fullName;
	}

	void setGenerateWrapperProcedure(boolean value) {
		this.generateWrapperProcedure = value;
		setChanged(true);
	}

	public void setWrapperProcedureName(String name ) {
		this.wrapperProcedureName = name;
		setChanged(true);
	}
	
	public String getDefaultWrapperProcedureName() {
		return getOperation().getName();
	}
	
	@Override
	public String getWrapperProcedureName() {
		if( this.wrapperProcedureName == null ) {
			this.wrapperProcedureName = getDefaultWrapperProcedureName();
		}
		return this.wrapperProcedureName;
	}
	
	
	void setOverwriteExistingProcedures(boolean value) {
		if( value == this.overwriteExistingProcedures ) {
			return;
		}
		this.overwriteExistingProcedures = value;
		// Update procedures for this operation
		String validRequestName = getRequestInfo().getDefaultProcedureName();
		String validResponseName = getResponseInfo().getDefaultProcedureName();
		
		if( !overwriteExistingProcedures ) {
			validRequestName = ModelGeneratorWsdlUiUtil.getUniqueName(
				this.importManager.getViewModelLocation().getFullPath().toString(), 
				getViewModelName(), validRequestName,false, false);
			
			validResponseName = ModelGeneratorWsdlUiUtil.getUniqueName(
				this.importManager.getViewModelLocation().getFullPath().toString(), 
				getViewModelName(), validResponseName,false, false);
		}
		getRequestInfo().setProcedureName(validRequestName);
		getResponseInfo().setProcedureName(validResponseName);
		
		String validWrapperName = getWrapperProcedureName();
		if( !overwriteExistingProcedures && validWrapperName.equals(getDefaultWrapperProcedureName()) ) {
			validWrapperName = ModelGeneratorWsdlUiUtil.getUniqueName(
				this.importManager.getViewModelLocation().getFullPath().toString(), 
				getViewModelName(), validWrapperName,false, false);
		}
		setWrapperProcedureName(validWrapperName);
		setChanged(true);
	}
	
	public boolean doOverwriteExistingProcedures() {
		return this.overwriteExistingProcedures;
	}
	
	@Override
	public String getBindingType() {
	    return this.bindingType;
	}
	
	@Override
	public String getSoapAction() {
		return this.soapAction;
	}
	
	@Override
	public String getNamespaceURI() {
		return this.namespaceURI;
	}
	
	void setChanged(boolean value) {
		this.changed = value;
		if( this.changed && !this.initializing) {
			this.importManager.setChanged(true);
		}
	}
	
	public boolean isChanged() {
	    if( this.changed ) {
	    	return true;
	    }
	    
	    if( requestInfo.isChanged() ) {
	    	return true;
	    }
	    
	    if( responseInfo.isChanged() ) {
	    	return true;
	    }
	    
	    return false;
	}
	
	@Override
	public String getWrapperSqlString() {
	    IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this);
	}
	
	@Override
	public String getSourceModelName() {
	    return importManager.getSourceModelName();
	}
	
	IStatus getNameStatus(String name) {
		String result = nameValidator.checkValidName(name);
		if( result != null ) {
			return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.Error_InvalidName_0_Reason_1, name, result));
		}
		
		return Status.OK_STATUS;
	}
	
	boolean wrapperExists() {
		return this.wrapperExists;
	}
	
	IStatus validate() {
		MultiStatus status = new MultiStatus(PLUGIN_ID, 0, null, null);

		
		// Check for existing wrapper procedure
		if( this.importManager.viewModelExists() ) {
			this.wrapperExists = ModelGeneratorWsdlUiUtil.eObjectExists(
				this.importManager.getViewModelLocation().getFullPath().toString(), 
				this.importManager.getViewModelName(), 
				this.wrapperProcedureName);
		} else {
			this.wrapperExists = false;
		}
		
		// Go through objects and look for problems
		if( getWrapperProcedureName() == null) {	
			status.add( 
				new Status(IStatus.ERROR, PLUGIN_ID, 
					NLS.bind(Messages.Error_Operation_0_WrapperProcedureNameCannotBeNullOrEmpty, 
						getOperation().getName())));
		}
		
		IStatus nameStatus = getNameStatus(getWrapperProcedureName());
		if( nameStatus.getSeverity() > IStatus.INFO) {
			status.merge(nameStatus);
		}
		
		IStatus requestStatus = getRequestInfo().validate();
		if( requestStatus.getSeverity() > IStatus.INFO ) {
			status.merge( requestStatus);
		}

		IStatus responseStatus = getResponseInfo().validate();
		if( responseStatus.getSeverity() > IStatus.INFO ) {
			status.merge( responseStatus);
		}
		
		setChanged(false);
		
		return status;
	}
	
	@Override
	public String getWrapperProcedureSqlString(Properties properties) {
	    IQueryService queryService = ModelerCore.getTeiidQueryService();
        IProcedureService procedureService = queryService.getProcedureService();
        return procedureService.getSQLStatement(this, properties);
	}
}

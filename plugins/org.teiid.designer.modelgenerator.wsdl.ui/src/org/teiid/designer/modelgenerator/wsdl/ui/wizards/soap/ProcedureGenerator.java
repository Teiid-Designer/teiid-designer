/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;

/** This class provides state information for the create and extract procedures that will be generated during
 * WSDL import and model/procedure generation
 * 
 * There will be both Request and Response information managed by this class.
 * 
 * 
 */
public class ProcedureGenerator {
	
	private ProcedureInfo requestInfo;
	private ProcedureInfo responseInfo;
	
	private boolean generateWrapperProcedure;
	
	private String wrapperProcedureName;
	
	private boolean overwriteExistingProcedures;
	
	private Operation operation;
	
	private String viewModelName;
	
	private WSDLImportWizardManager importManager;

	public ProcedureGenerator(Operation operation, WSDLImportWizardManager importManager) {
		super();
		this.operation = operation;
		this.requestInfo = new RequestInfo(operation, this);
		this.responseInfo = new ResponseInfo(operation, this);
		this.viewModelName = operation.getBinding().getPort().getService().getName() + "View"; //$NON-NLS-1$
		this.importManager = importManager;
		this.generateWrapperProcedure = true;
	}

	public WSDLImportWizardManager getImportManager() {
		return this.importManager;
	}
	
	public ProcedureInfo getRequestInfo() {
		return this.requestInfo;
	}

	public ProcedureInfo getResponseInfo() {
		return this.responseInfo;
	}

	public Operation getOperation() {
		return this.operation;
	}
	
	public void setRequestProcedureName(String name ) {
		this.requestInfo.setProcedureName(name);
	}
	
	public void setResponseProcedureName(String name ) {
		this.responseInfo.setProcedureName(name);
	}
	
	public String getRequestProcedureName() {
		return this.requestInfo.getProcedureName();
	}
	
	public String getResponseProcedureName() {
		return this.responseInfo.getProcedureName();
	}
	
	public String getViewModelName() {
		return this.viewModelName;
	}

	public void setGenerateWrapperProcedure(boolean value) {
		this.generateWrapperProcedure = value;
	}
	
	public boolean doGenerateWrapperProcedure() {
		return this.generateWrapperProcedure;
	}
	
	public void setWrapperProcedureName(String name ) {
		this.wrapperProcedureName = name;
	}
	
	public String getWrappedProcedureName() {
		if( this.wrapperProcedureName == null ) {
			this.wrapperProcedureName = getOperation().getName() + "Procedure"; //$NON-NLS-1$
		}
		return this.wrapperProcedureName;
	}
	
	
	public void setOverwriteExistingProcedures(boolean value) {
		this.overwriteExistingProcedures = value;
	}
	
	public boolean doOverwriteExistingProcedures() {
		return this.overwriteExistingProcedures;
	}
}

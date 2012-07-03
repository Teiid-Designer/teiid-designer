/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;

/**
 * Class designed to provide validation logic and IStatus values for the WSDL importer wizard.
 */
public class ImportManagerValidator {
	
	public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
	
	WSDLImportWizardManager manager;
	IStatus operationsStatus;
	IStatus modelsStatus;
	Map<ProcedureGenerator, IStatus> proceduresStatusMap;

	public ImportManagerValidator(WSDLImportWizardManager manager) {
		super();
		this.manager = manager;
		this.proceduresStatusMap = new HashMap<ProcedureGenerator, IStatus>(10);
	}

	public void validate() {
		modelsStatus = validateResourceInfo();
		
		operationsStatus = validateProcedures();
	}
	
	public IStatus getOperationsStatus() {
		return this.operationsStatus;
	}
	
	public IStatus getModelsStatus() {
		return this.modelsStatus;
	}
	
	/**
	 * Check for MultiStatus and determine highest severity message
	 * 
	 * @param status
	 * @return
	 */
	public String getPrimaryMessage(IStatus status) {
		if( status instanceof MultiStatus && status.getChildren().length > 0) {
			if( status.getMessage() != null && status.getMessage().trim().length() == 0 ) {
				int maxSeverity = status.getSeverity();
				for( IStatus childStatus : ((MultiStatus)status).getChildren()) {
					if( maxSeverity == childStatus.getSeverity() ) {
						return childStatus.getMessage();
					}
				}
			} else {
				return status.getMessage();
			}
		} else {
			return status.getMessage();
		}
		
		return StringUtilities.EMPTY_STRING;
	}
	
	public IStatus getProcedureStatus(ProcedureGenerator generator) {
		return proceduresStatusMap.get(generator);
	}
	
	public IStatus getWorstProcedureStatus() {
		MultiStatus status = new MultiStatus(ProcedureGenerator.PLUGIN_ID, 0, null, null);
		
		for(ProcedureGenerator generator : this.manager.getProcedureGenerators() ) {
			IStatus theStatus = this.proceduresStatusMap.get(generator);
			if( theStatus != null ) {
				status.merge(theStatus);
			}
		}
		
		return status;
	}
	
	private IStatus validateResourceInfo() {
		MultiStatus status = new MultiStatus(ProcedureGenerator.PLUGIN_ID, 0, null, null);
		
		// Validate Source location & model name
		if (this.manager.getSourceModelLocation() == null ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_SourceModelLocationUndefined));
		}
		
		if (CoreStringUtil.isEmpty(this.manager.getSourceModelName()) ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_SourceModelNameUndefined));
		}
		
		String fileNameMessage = ModelUtilities.validateModelName(this.manager.getSourceModelName(), ".xmi"); //$NON-NLS-1$
		if (fileNameMessage != null) {
			String finalMessage = 
				NLS.bind(Messages.Error_InvalidSourceModelFileName_0_Reason_1, 
					this.manager.getSourceModelName(), fileNameMessage);
			status.add(createStatus(IStatus.ERROR, finalMessage));
		}
		
		// Validate View location & model name
		if (this.manager.getViewModelLocation() == null ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_ViewModelLocationUndefined));
		}
		
		if (CoreStringUtil.isEmpty(this.manager.getViewModelName()) ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_ViewModelNameUndefined));
		}
		
		fileNameMessage = ModelUtilities.validateModelName(this.manager.getViewModelName(), ".xmi"); //$NON-NLS-1$
		if (fileNameMessage != null) {
			String finalMessage = 
				NLS.bind(Messages.Error_InvalidViewModelFileName_0_Reason_1, 
					this.manager.getSourceModelName(), fileNameMessage);
			status.add(createStatus(IStatus.ERROR, finalMessage));
		}
		
		if( status.isOK() ) {
			if( this.manager.doGenerateDefaultProcedures() ) {
				return createStatus(IStatus.OK, Messages.Status_AllOkClickFinishToGenerateProcedures);
			} else {
				return createStatus(IStatus.OK, Messages.Status_AllOkClickNextToDefineProcedures);
			}
		}
		
		this.manager.setChanged(false);
		
		return status;
	}
	
	private IStatus validateProcedures() {
    	if( this.manager.getProcedureGenerators().isEmpty() ) {
			return createStatus(IStatus.WARNING, Messages.Error_NoOperationsSelected);
    	}
    	
		MultiStatus status = new MultiStatus(ProcedureGenerator.PLUGIN_ID, 0, null, null);
		for(ProcedureGenerator generator : this.manager.getProcedureGenerators() ) {
			IStatus theStatus = generator.validate();
			this.proceduresStatusMap.put(generator, theStatus);
			if( theStatus != null ) {
				status.merge(theStatus);
			}
		}
		
		return status;
	}
	
	private IStatus createStatus(int severity, String message) {
		return new Status(severity, ProcedureGenerator.PLUGIN_ID, message);
	}
	
	public static boolean isValidDatatype(String type) {
		if( type == null) {
			return false;
		}
		// Check Datatypes
		EObject dType = null;
		
		try {
			dType = datatypeManager.findDatatype(type);
		} catch (ModelerCoreException ex) {
			ModelGeneratorWsdlUiConstants.UTIL.log(ex);
		}
		
		return dType != null;
		
	}
}

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
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;


/**
 * Class designed to provide validation logic and IStatus values for the WSDL importer wizard.
 *
 * @since 8.0
 */
public class ImportManagerValidator {
	
	public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
	
	String PLUGIN_ID = ModelGeneratorWsdlUiConstants.PLUGIN_ID;
	
	WSDLImportWizardManager manager;
	IStatus connectionProfileStatus;
	IStatus operationsStatus;
	IStatus modelsStatus;
	Map<ProcedureGenerator, IStatus> proceduresStatusMap;

	public ImportManagerValidator(WSDLImportWizardManager manager) {
		super();
		this.manager = manager;
		this.proceduresStatusMap = new HashMap<ProcedureGenerator, IStatus>(10);
	}

	public void validate() {
	    connectionProfileStatus = validateConnectionProfile();

		modelsStatus = validateResourceInfo();
		
		operationsStatus = validateProcedures();
	}

    public IStatus getConnectionProfileStatus() {
        if (this.connectionProfileStatus == null)
            validate();

        return this.connectionProfileStatus;
    }

	public IStatus getOperationsStatus() {
        if (this.operationsStatus == null)
            validate();

		return this.operationsStatus;
	}
	
	public IStatus getModelsStatus() {
        if (this.modelsStatus == null)
            validate();

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
		
		return StringConstants.EMPTY_STRING;
	}
	
	public IStatus getProcedureStatus(ProcedureGenerator generator) {
		return proceduresStatusMap.get(generator);
	}
	
	public IStatus getWorstProcedureStatus() {
		MultiStatus status = new MultiStatus(PLUGIN_ID, 0, null, null);
		
		for(ProcedureGenerator generator : this.manager.getProcedureGenerators() ) {
			IStatus theStatus = this.proceduresStatusMap.get(generator);
			if( theStatus != null ) {
				status.merge(theStatus);
			}
		}
		
		return status;
	}

	private IStatus validateConnectionProfile() {
	    MultiStatus status = new MultiStatus(PLUGIN_ID, 0, null, null);

	    IConnectionProfile connectionProfile = manager.getConnectionProfile();
	    if (connectionProfile == null) {
	        status.add(createStatus(IStatus.ERROR, Messages.Status_ConnectionProfileMissing));
	        return status;
	    }

	    Properties properties = connectionProfile.getBaseProperties();
	    String[] expectedProperties = {
	        IWSProfileConstants.END_POINT_NAME_PROP_ID,
	        IWSProfileConstants.END_POINT_URI_PROP_ID,
	        IWSProfileConstants.SOAP_BINDING,
	        ICredentialsCommon.SECURITY_TYPE_ID
	    };

	    for (String propertyKey : expectedProperties) {
	        if(properties.get(propertyKey) == null) {
	            status.add(createStatus(IStatus.ERROR, NLS.bind(Messages.Status_ConnectionProfilePropertyMissing, propertyKey)));
	            break;
	        }
	    }

	    return status;
	}

	private IStatus validateResourceInfo() {
		MultiStatus status = new MultiStatus(PLUGIN_ID, 0, null, null);

		// Validate Source location & model name
		if (this.manager.getSourceModelLocation() == null ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_SourceModelLocationUndefined));
		}

		IStatus nameStatus = null;
		if (CoreStringUtil.isEmpty(this.manager.getSourceModelName()) ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_SourceModelNameUndefined));
		} else {
		    nameStatus = ModelNameUtil.validate(this.manager.getSourceModelName(), ModelerCore.MODEL_FILE_EXTENSION, ModelNameUtil.IGNORE_CASE );
		    if (nameStatus.getSeverity() == IStatus.ERROR) {
		    	Status newStatus = new Status(nameStatus.getSeverity(), nameStatus.getPlugin(), 
		    			ModelNameUtil.MESSAGES.INVALID_SOURCE_MODEL_NAME + nameStatus.getMessage());
		        status.add(newStatus);
		    }
		}

		// Validate View location & model name
		if (this.manager.getViewModelLocation() == null ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_ViewModelLocationUndefined));
		}
		
		if (CoreStringUtil.isEmpty(this.manager.getViewModelName()) ) {
			status.add(createStatus(IStatus.ERROR, Messages.Status_ViewModelNameUndefined));
		}
		else {
		    nameStatus = ModelNameUtil.validate(this.manager.getViewModelName(), ModelerCore.MODEL_FILE_EXTENSION, ModelNameUtil.IGNORE_CASE );
		    if (nameStatus.getSeverity() == IStatus.ERROR) {
		    	Status newStatus = new Status(nameStatus.getSeverity(), nameStatus.getPlugin(), 
		    			ModelNameUtil.MESSAGES.INVALID_VIEW_MODEL_NAME + nameStatus.getMessage());
		        status.add(newStatus);
		    }
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
    	
		MultiStatus status = new MultiStatus(PLUGIN_ID, 0, null, null);
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
		return new Status(severity, PLUGIN_ID, message);
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.modelgenerator.wsdl.WSDLReader;
import org.teiid.designer.modelgenerator.wsdl.model.Model;
import org.teiid.designer.modelgenerator.wsdl.model.ModelGenerationException;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ImportManagerValidator;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureGenerator;
import org.teiid.designer.query.proc.wsdl.model.IPort;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 * WSDL Import Manager - Business Object for interacting with GUI
 *
 * @since 8.0
 */
/**
 * @author tejones
 *
 *
 * @since 8.0
 */
public class WSDLImportWizardManager implements IChangeNotifier {

    public static final int WORKSPACE_SOURCE = 0;
    public static final int FILESYSTEM_SOURCE = 1;
    public static final int URL_SOURCE = 2;
    
    public static final String PAYLOAD = "PAYLOAD"; //$NON-NLS-1$
    public static final String MESSAGE = "MESSAGE"; //$NON-NLS-1$
    
    public static final String[] SERVICE_MODES = {PAYLOAD, MESSAGE};

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private WSDLReader wsdlReader;
    
    private IProject targetProject;
    
	private String sourceModelName;
	private boolean sourceModelExists;
    private IContainer sourceModelLocation;
	
	private String viewModelName;
	private boolean viewModelExists;
    private IContainer viewModelLocation;
	
	private boolean generateDefaultProcedures;
	
    /**
     * The unique jbossJndiName
     * 
     */
	private String jbossJndiName;
	
	private boolean autoCreateDataSource = true;
	
    private List<Operation> selectedOperations;
    private int uriSource = URL_SOURCE;
    private IConnectionProfile connectionProfile;

    private Map<Operation, ProcedureGenerator> procedureGenerators;
    
    private Model wsdlModel;
    
    private String translatorDefaultBinding = IPort.SOAP11;
    private String translatorDefaultServiceMode = PAYLOAD; 
    
    private Properties designerProperties;
    
    private ImportManagerValidator validator;
    private Collection<IChangeListener> listeners;
    
    private boolean changed;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public WSDLImportWizardManager() {
        this.wsdlReader = new WSDLReader();
        this.selectedOperations = new ArrayList();
        this.procedureGenerators = new HashMap<Operation, ProcedureGenerator>();
        this.listeners = new ArrayList<IChangeListener>(5);
        this.validator = new ImportManagerValidator(this);
        setChanged(true);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Apply authentication credentials to the WSDLReader
     *
     * @param securityTypeValue
     * @param userName
     * @param password
     */
    public void setWSDLCredentials(String securityTypeValue, String userName, String password) {
        if (securityTypeValue == null) {
            return;
        }

        SecurityType securityType = SecurityType.valueOf(securityTypeValue);
        this.wsdlReader.setAuthenticationCredentials(securityType, userName, password);

        // Changed authentication credentials then the model must be out of date
        this.wsdlModel = null;
        setChanged(true);
    }

    /**
     * Set the WSDL File URI String
     * 
     * @param fileUri the specified file uri
     */
    public void setWSDLFileUri( String fileUri ) {
    	if( this.wsdlReader.getWSDLUri() == null ) {
            this.wsdlReader.setWSDLUri(fileUri);
    	} else if( !(this.wsdlReader.getWSDLUri().equals(fileUri)) ) {
            this.wsdlReader.setWSDLUri(fileUri);
            this.wsdlModel = null;
    	}
    	setChanged(true);
    }

    /**
     * Get the WSDL File URI String
     * 
     * @return the WSDL file uri
     */
    public String getWSDLFileUri() {
        return this.wsdlReader.getWSDLUri();
    }
    
    /**
     * Validate the current WSDL file
     * 
     * @return the WSDL validation MultiStatus
     */
    public MultiStatus validateWSDL( IProgressMonitor monitor ) {
        return this.wsdlReader.validateWSDL(monitor);
    }

    /**
     * Get the WSDL Model. If the current WSDL is not valid or has not been specified an exception will be thrown.
     * 
     * @return the WSDL Model
     * @throws ModelGenerationException
     */
    public Model getWSDLModel() throws ModelGenerationException {
    	if( this.wsdlModel == null ) {
    		this.wsdlModel = this.wsdlReader.getModel();
    	}
        return this.wsdlModel;
    }

    /**
     * Get the currently specified location where the target Model is to be generated.
     * 
     * @return the view Model location
     */
    public IContainer getViewModelLocation() {
        return this.viewModelLocation;
    }

    /**
     * Set the location where the target Model is to be generated.
     * 
     * @param viewModelLocation the target Model location
     */
    public void setViewModelLocation( IContainer viewModelLocation ) {
        this.viewModelLocation = viewModelLocation;
        setChanged(true);
    }

    /**
     * Get the name of the target view relational model to be generated.
     * 
     * @return the target View Model Name
     */
    public String getViewModelName() {
        return this.viewModelName;
    }

    /**
     * Set the name of the target view relational Model.
     * 
     * @param targetModelName the target view Model Name
     */
    public void setViewModelName( String targetModelName ) {
        this.viewModelName = targetModelName;
        setChanged(true);
    }

    /**
     * Set the selected operations to process
     * 
     * @param operations the list of operations
     */
    public void setSelectedOperations( List<Operation> operations ) {
        this.selectedOperations = operations;
        synchronizeProcedureGenerators();
        setChanged(true);
    }

    /**
     * Get the currently selected operations
     * 
     * @return the list of operations
     */
    public List<Operation> getSelectedOperations() {
        return this.selectedOperations;
    }

    /**
     * Get the source of the WSDL URI
     * 
     * @return Integer indicating the source of the WSDL URI
     */
    public int getUriSource() {
        return uriSource;
    }

    /**
     * Set the source of the WSDL URI
     * 
     * @param uriSource Integer indicating the source of the WSDL URI
     */
    public void setUriSource( int uriSource ) {
        this.uriSource = uriSource;
        setChanged(true);
    }

	/**ModelGeneratorWsdlUiUtil.modelExists(modelFileContainerPath.toOSString(), this.viewModelFileText.getText())
	 * @return connectionProfile
	 */
	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	/**
	 * @param connectionProfile Sets connectionProfile to the specified value.
	 */
	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
		if( this.connectionProfile != null ) {
            Properties props = this.connectionProfile.getBaseProperties();

            String securityTypeValue = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
            String userName = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
            String password = props.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
            setWSDLCredentials(securityTypeValue, userName, password);

            String fileUri = props.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID);
            if( fileUri != null ) {
                setWSDLFileUri(fileUri);
            }
            
            String binding = props.getProperty(IWSProfileConstants.SOAP_BINDING);
            if (binding != null) {
                setTranslatorDefaultBinding(binding);
            } else {
                setTranslatorDefaultBinding(IPort.SOAP11);
            }
		}
		setChanged(true);
	}
	
	public ProcedureGenerator getProcedureGenerator(Operation operation) {
		return this.procedureGenerators.get(operation);
	}
	
	public Collection<ProcedureGenerator> getProcedureGenerators() {
		return this.procedureGenerators.values();
	}

	private void synchronizeProcedureGenerators() {
		for( Operation operation : getSelectedOperations()) {
			if( !this.procedureGenerators.containsKey(operation) ) {
				this.procedureGenerators.put(operation, new ProcedureGenerator(operation, this));
			}
		}
		Collection<Operation> staleOperations = new ArrayList<Operation>();
		for(ProcedureGenerator generator : this.procedureGenerators.values() ) {
			if( !this.selectedOperations.contains(generator.getOperation() ) ) {
				staleOperations.add(generator.getOperation());
			}
		}
		
		for( Operation operation : staleOperations) {
			this.procedureGenerators.remove(operation);
		}
		
		// Now set source and target model names if generate == FALSE
		
		if( !generateDefaultProcedures && !getSelectedOperations().isEmpty() ) {
			// get one operation and then 
			Operation firstOperation = getSelectedOperations().get(0);
			String serviceName = firstOperation.getBinding().getPort().getService().getName();
			setSourceModelName(serviceName + ".xmi"); //$NON-NLS-1$
			setViewModelName(serviceName + "View.xmi");  //$NON-NLS-1$
			if( this.viewModelLocation != null ) {
			    ModelWorkspaceManager manager = ModelWorkspaceManager.getModelWorkspaceManager();
			    this.sourceModelExists = manager.modelExists(this.sourceModelLocation.getFullPath().toOSString(), this.sourceModelName);
			    this.viewModelExists = manager.modelExists(this.viewModelLocation.getFullPath().toOSString(), this.viewModelName);
			}
		} else {
			this.sourceModelName = null;
			this.sourceModelExists = false;
			this.viewModelName = null;
			this.viewModelExists = false;
		}
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public String getSourceModelName() {
        return this.sourceModelName;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setSourceModelName(String sourceModelName) {
		this.sourceModelName = sourceModelName;
		setChanged(true);
	}
	
	/**
	 * 
	 * @return sourceModelLocation the target location where the source model is going to be created
	 */
	public IContainer getSourceModelLocation() {
		return this.sourceModelLocation;
	}
	
	/**
	 * 
	 * @return location the target location where the view model either exists or is going to be created
	 */
	public void setSourceModelLocation(IContainer location) {
		this.sourceModelLocation = location;
		setChanged(true);
	}
	
	public void setViewModelExists(boolean viewModelExists) {
		
		this.viewModelExists = viewModelExists;
		// Need to update the request and response procedure names if view model exists
		// and the generator has DO OVERWRITE == FALSE
		if( this.viewModelExists) {
    		for( ProcedureGenerator generator : this.procedureGenerators.values() ) {
    			if( !generator.doOverwriteExistingProcedures()  ) {
                    String validRequestName = ModelUtilities.getUniqueName(getViewModelLocation().getFullPath().toString(),
                                                                           getViewModelName(),
                                                                           generator.getRequestInfo().getDefaultProcedureName(),
                                                                           false,
                                                                           false);
                    generator.getRequestInfo().setProcedureName(validRequestName);
                    String validResponseName = ModelUtilities.getUniqueName(getViewModelLocation().getFullPath().toString(),
                                                                            getViewModelName(),
                                                                            generator.getResponseInfo().getDefaultProcedureName(),
                                                                            false,
                                                                            false);
                    generator.getResponseInfo().setProcedureName(validResponseName);
                    String validWrapperName = ModelUtilities.getUniqueName(getViewModelLocation().getFullPath().toString(),
                                                                           getViewModelName(),
                                                                           generator.getDefaultWrapperProcedureName(),
                                                                           false,
                                                                           false);
                    generator.setWrapperProcedureName(validWrapperName);
    			}
    		}
		} else {
			for( ProcedureGenerator generator : this.procedureGenerators.values() ) {
				generator.getRequestInfo().setProcedureName(generator.getRequestInfo().getDefaultProcedureName());
				generator.getResponseInfo().setProcedureName(generator.getResponseInfo().getDefaultProcedureName());
				String validWrapperName = generator.getWrapperProcedureName();
				if( validWrapperName.startsWith(generator.getDefaultWrapperProcedureName()) ) {
					generator.setWrapperProcedureName(generator.getWrapperProcedureName());
				}
				
			}
		}
		setChanged(true);
	}
	
	public boolean viewModelExists() {
		return this.viewModelExists;
	}
	
	public void setSourceModelExists(boolean sourceModelExists) {
		this.sourceModelExists = sourceModelExists;
		setChanged(true);
	}
	
	public boolean sourceModelExists() {
		return this.sourceModelExists;
	}

	public boolean doGenerateDefaultProcedures() {
		return this.generateDefaultProcedures;
	}

	public void setGenerateDefaultProcedures(boolean generateDefaultProcedures) {
		this.generateDefaultProcedures = generateDefaultProcedures;
		setChanged(true);
	}

	/**
	 * @return the translatorDefaultBinding
	 */
	public String getTranslatorDefaultBinding() {
		return this.translatorDefaultBinding;
	}

	/**
	 * @param translatorDefaultBinding the translatorDefaultBinding to set
	 */
	public void setTranslatorDefaultBinding(String translatorDefaultBinding) {
		this.translatorDefaultBinding = translatorDefaultBinding;
		setChanged(true);
	}

	/**
	 * @return the translatorDefaultServiceMode
	 */
	public String getTranslatorDefaultServiceMode() {
		return this.translatorDefaultServiceMode;
	}
	
	public boolean isMessageServiceMode() {
		return this.translatorDefaultServiceMode.equalsIgnoreCase(MESSAGE); 
	}

	/**
	 * @param translatorDefaultServiceMode the translatorDefaultServiceMode to set
	 */
	public void setTranslatorDefaultServiceMode(String translatorDefaultServiceMode) {
		this.translatorDefaultServiceMode = translatorDefaultServiceMode;
		setChanged(true);
	}

    public void setDesignerProperties(Properties props) {
    	this.designerProperties = props;
    }
    
    public Properties getDesignerProperties() {
    	return this.designerProperties;
    }
    
    public void setDesignerProperty(String key, String value) {
    	if( this.designerProperties != null ) {
    		this.designerProperties.put(key, value);
    	}
    }
    
    public void validate() {
    	if( isChanged() ) {
    		this.validator.validate();
    	}
    	setChanged(false);
    }
    
	public void setChanged(boolean value) {
		this.changed = value;
	}
    
    private boolean isChanged() {
    	if( changed ) {
    		return true;
    	}
    	
		for(ProcedureGenerator generator : this.procedureGenerators.values() ) {
			if( generator.isChanged() ) {
				return true;
			}
		}
		
		return false;
    }
    
    public ImportManagerValidator getValidator() {
    	return this.validator;
    }
    
    public void notifyChanged() {
    	validate();
    	for( IChangeListener listener: this.listeners ) {
    		listener.stateChanged(this);
    	}
    }
    
    @Override
    public void addChangeListener(IChangeListener listener) {
    	this.listeners.add(listener);
    }
    
    @Override
    public void removeChangeListener(IChangeListener listener) {
    	this.listeners.remove(listener);
    }
    
	
    public IProject getTargetProject() {
    	return targetProject;
    }
    
    public boolean setTargetProject(IProject project) {
    	// If project is the same project, do nothing:
    	if( this.targetProject != null && this.targetProject == project ) return false;
    	
    	this.targetProject = project;
    	
    	// If new project, need to set the "location" of the view and source models to the project name
    	if( this.targetProject != null ) {
	    	setViewModelLocation(project);
	    	setSourceModelLocation(project);
    	}
    	
    	return true;
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
}

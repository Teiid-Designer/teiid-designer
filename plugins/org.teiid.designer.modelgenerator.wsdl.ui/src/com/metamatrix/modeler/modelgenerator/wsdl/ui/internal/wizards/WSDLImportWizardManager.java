/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureGenerator;

import com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;

/**
 * WSDL Import Manager - Business Object for interacting with GUI
 */
public class WSDLImportWizardManager {

    public static final int WORKSPACE_SOURCE = 0;
    public static final int FILESYSTEM_SOURCE = 1;
    public static final int URL_SOURCE = 2;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private WSDLReader wsdlReader;

    private IContainer targetModelLocation;
    
	private String sourceModelName;
	private IPath sourceModelLocation;
	private boolean sourceModelExists;
	
	private String viewModelName;
	
    private List selectedOperations;
    private int uriSource = URL_SOURCE;
    private IConnectionProfile connectionProfile;

    private Map<Operation, ProcedureGenerator> procedureGenerators;
    
    private Model wsdlModel;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public WSDLImportWizardManager() {
        this.wsdlReader = new WSDLReader();
        this.selectedOperations = new ArrayList();
        this.procedureGenerators = new HashMap<Operation, ProcedureGenerator>();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set the WSDL File URI String
     * 
     * @param fileUri the specified file uri
     */
    public void setWSDLFileUri( String fileUri ) {
        this.wsdlReader.setWSDLUri(fileUri);
        this.wsdlModel = null;
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
     * Get the name of the target view relational model to be generated.
     * 
     * @return the target View Model Name
     */
    public String getTargetViewModelName() {
        return this.viewModelName;
    }

    /**
     * Set the name of the target view relational Model.
     * 
     * @param targetModelName the target view Model Name
     */
    public void setTargetViewModelName( String targetModelName ) {
        this.viewModelName = targetModelName;
    }

    /**
     * Set the selected operations to process
     * 
     * @param operations the list of operations
     */
    public void setSelectedOperations( List<Operation> operations ) {
        this.selectedOperations = operations;
        synchronizeProcedureGenerators();
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
	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}
	
	public ProcedureGenerator getProcedureGenerator(Operation operation) {
		return this.procedureGenerators.get(operation);
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
	}
	
	/**
	 * 
	 * @return sourceModelLocation the target location where the source model is going to be created
	 */
	public IPath getSourceModelLocation() {
		return this.sourceModelLocation;
	}
	
	/**
	 * 
	 * @return location the target location where the view model either exists or is going to be created
	 */
	public void setSourceModelLocation(IPath location) {
		this.sourceModelLocation = location;
	}
	
	public void setSourceModelExists(boolean sourceModelExists) {
		this.sourceModelExists = sourceModelExists;
	}
	
	public boolean sourceModelExists() {
		return this.sourceModelExists;
	}
}

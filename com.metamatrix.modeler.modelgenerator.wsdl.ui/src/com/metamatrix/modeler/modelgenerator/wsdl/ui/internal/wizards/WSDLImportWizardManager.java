/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;

import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.modelgenerator.wsdl.WSDLReader;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;


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
    
    private String targetModelName;
    private IContainer targetModelLocation;
    private List selectedOperations;
    private int uriSource = URL_SOURCE;
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////////////////////////
    public WSDLImportWizardManager(Logger logger) {
        this.wsdlReader = new WSDLReader(logger);
        this.selectedOperations = new ArrayList();
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set the WSDL File URI String
     * @param fileUri the specified file uri
     */
    public void setWSDLFileUri(String fileUri) {
        this.wsdlReader.setFileUri(fileUri);
    }
    
    /**
     * Get the WSDL File URI String
     * @return the WSDL file uri
     */
    public String getWSDLFileUri() {
        return this.wsdlReader.getFileUri();
    }
    
    /**
     * Validate the current WSDL file
     * @return the WSDL validation MultiStatus
     */
    public MultiStatus validateWSDL(IProgressMonitor monitor) {
        return this.wsdlReader.validateWSDL(monitor);
    }
    
    /**
     * Get the WSDL Model.  If the current WSDL is not valid or has not been specified
     * an exception will be thrown.
     * @return the WSDL Model
     * @throws ModelGenerationException 
     */
    public Model getWSDLModel() throws ModelGenerationException {
        return this.wsdlReader.getModel();
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
    
    /** 
     * Set the selected operations to process
     * @param operations the list of operations
     */
    public void setSelectedOperations(List operations) {
        this.selectedOperations = operations;
    }
    
    /** 
     * Get the currently selected operations
     * @return the list of operations
     */
    public List getSelectedOperations() {
        return this.selectedOperations;
    }

	
    /**
     * Get the source of the WSDL URI
     * @return Integer indicating the source of the WSDL URI
     */public int getUriSource() {
		return uriSource;
	}
	
	/**
	 * Set the source of the WSDL URI
	 * @param uriSource Integer indicating the source of the WSDL URI
	 */
	public void setUriSource(int uriSource) {
		this.uriSource = uriSource;
	}
    

}

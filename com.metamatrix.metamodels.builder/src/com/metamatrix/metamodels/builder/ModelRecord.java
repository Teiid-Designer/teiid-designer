/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.builder;

import com.metamatrix.core.util.ArgCheck;


/**
 * Record object for use in creating models for this builder framework 
 */
public class ModelRecord {
	
	private String locationPath;
	private String modelType;
	private String modelSubType;
	private String modelName;
    private String modelNameInSource;
	private String modelDescription;
    private String extensionPackage;
	
	// ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

	/**
	 * Default Constructor
	 */
	public ModelRecord( ) {
	}

	/**
	 * Constructor
	 * @param type the type of model (eg 'Relational', 'Extension', 'Relationship', etc)
	 * @param subType the model subType (eg 'Physical', 'Virtual', etc)
     * @param name the model name
     * @param nameInSource the model nameInSource
	 * @param description the model description
	 * @param extPackage the extension package for this model
	 */
	public ModelRecord(String type, String subType, String name, String nameInSource, String description, String extPackage) {
		super();
        ArgCheck.isNotNull(type);
        ArgCheck.isNotNull(name);
		this.modelType = type;
		this.modelSubType = subType;
		this.modelName = name;
        this.modelNameInSource = nameInSource;
		this.modelDescription = description;
		this.extensionPackage = extPackage;
	}

	// ==================================================================================
    //                        M E T H O D S
    // ==================================================================================

	/**
	 * Get the Model Description
	 * @return the model description
	 */
	public String getModelDescription() {
		return modelDescription;
	}
	
	/**
	 * Set the Model Description
	 * @param modelDescription the model description
	 */
	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}
	
	/**
	 * Get the Model Name
	 * @return the model name
	 */
	public String getModelName() {
		return modelName;
	}
	
    /**
     * Set the Model Name
     * @param modelName the model name
     */
    public void setModelName(String modelName) {
        ArgCheck.isNotNull(modelName);
        this.modelName = modelName;
    }
	
    /**
     * Get the Model Name In Source
     * @return the model NameInSource
     */
    public String getModelNameInSource() {
        return modelNameInSource;
    }
    
    /**
     * Set the Model NameInSource
     * @param modelNameInSource the model name
     */
    public void setModelNameInSource(String modelNameInSource) {
        ArgCheck.isNotNull(modelNameInSource);
        this.modelNameInSource = modelNameInSource;
    }

    /**
	 * Get the Model subType
	 * @return the model subType
	 */
	public String getModelSubType() {
		return modelSubType;
	}
	
	/**
	 * Set the Model subType
	 * @param modelSubType the model subType
	 */
	public void setModelSubType(String modelSubType) {
		this.modelSubType = modelSubType;
	}
	
	/**
	 * Get the Model type
	 * @return the model type
	 */
	public String getModelType() {
		return modelType;
	}
	
	/**
	 * Set the Model type
	 * @param modelType the model type
	 */
	public void setModelType(String modelType) {
        ArgCheck.isNotNull(modelType);
		this.modelType = modelType;
	}
	
	/**
	 * Get the model location path
	 * @return the model's location path
	 */
	public String getLocationPath() {
		return locationPath;
	}
	
	/**
	 * Set the model location path
	 * @param locationPath the model's location path
	 */
	public void setLocationPath(String locationPath) {
		this.locationPath = locationPath;
	}
    
    /**
     * Get the model's Extension package
     * @return the model's extension package
     */
    public String getExtensionPackage() {
        return this.extensionPackage;
    }
    
    /**
     * Set the model's Extension package
     * @param extensionPackage the model's extension package
     */
    public void setExtensionPackage(String extensionPackage) {
        this.extensionPackage = extensionPackage;
    }
}

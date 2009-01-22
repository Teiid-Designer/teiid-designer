/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import com.metamatrix.metamodels.core.ModelType;


/** 
 * @since 5.0
 */
public class ModelSelectorInfo {
    
    private String modelTypeDisplayName;
    private ModelType modelType;
    private String modelURI;
    private String newModelLabel;
    private String title;
    
    /** 
     * 
     * @since 5.0
     */
    public ModelSelectorInfo(String modelTypeDisplayName, ModelType type, String uri, String modelLabel, String title) {
        super();
        this.modelTypeDisplayName = modelTypeDisplayName;
        this.modelType = type;
        this.modelURI = uri;
        this.newModelLabel = modelLabel;
        this.title = title;
    }

    
    /** 
     * @return Returns the modelType.
     * @since 5.0
     */
    public ModelType getModelType() {
        return this.modelType;
    }

    
    /** 
     * @param theModelType The modelType to set.
     * @since 5.0
     */
    public void setModelType(ModelType theModelType) {
        this.modelType = theModelType;
    }

    
    /** 
     * @return Returns the modelURI.
     * @since 5.0
     */
    public String getModelURI() {
        return this.modelURI;
    }

    
    /** 
     * @param theModelURI The modelURI to set.
     * @since 5.0
     */
    public void setModelURI(String theModelURI) {
        this.modelURI = theModelURI;
    }

    
    /** 
     * @return Returns the newModelLabel.
     * @since 5.0
     */
    public String getNewModelLabel() {
        return this.newModelLabel;
    }

    
    /** 
     * @param theNewModelLabel The newModelLabel to set.
     * @since 5.0
     */
    public void setNewModelLabel(String theNewModelLabel) {
        this.newModelLabel = theNewModelLabel;
    }


    
    /** 
     * @return Returns the title.
     * @since 5.0
     */
    public String getTitle() {
        return this.title;
    }


    
    /** 
     * @param theTitle The title to set.
     * @since 5.0
     */
    public void setTitle(String theTitle) {
        this.title = theTitle;
    }


    
    /** 
     * @return Returns the modelName.
     * @since 5.0
     */
    public String getModelTypeDisplayName() {
        return this.modelTypeDisplayName;
    }


    
    /** 
     * @param theModelName The modelName to set.
     * @since 5.0
     */
    public void setModelTypeDisplayName(String theModelTypeDisplayName) {
        this.modelTypeDisplayName = theModelTypeDisplayName;
    }

}

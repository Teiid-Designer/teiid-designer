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

package com.metamatrix.modeler.ui.wizards;

import com.metamatrix.metamodels.core.ModelType;


/** 
 * This class provides a data-structure that the NewModelWizard framekwork can use to prepopulate the NewModelWizardMetaModelPage
 * (i.e. first wizard page). Intitial implementation provides the ability of the wizard to auto-populate all fields when selecting
 * an XML Schema (xsd) file, including an initial name. (saves multiple clicks) (see Defect 22363)
 * @since 5.0
 */
public class NewModelWizardInput {
    private String metamodelClass;
    private ModelType modelType;
    private String builderType;
    private String name;
    
    /** 
     * 
     * @since 5.0
     */
    public NewModelWizardInput() {
        super();
    }
    
    public NewModelWizardInput(String metamodelClass, ModelType modelType, String builderType) {
        super();
        this.metamodelClass = metamodelClass;
        this.modelType = modelType;
        this.builderType = builderType;
    }
    
    /** 
     * @return Returns the builderType.
     * @since 5.0
     */
    public String getBuilderType() {
        return this.builderType;
    }
    
    /** 
     * @param theBuilderType The builderType to set.
     * @since 5.0
     */
    public void setBuilderType(String theBuilderType) {
        this.builderType = theBuilderType;
    }
    
    /** 
     * @return Returns the metamodelClass.
     * @since 5.0
     */
    public String getMetamodelClass() {
        return this.metamodelClass;
    }
    
    /** 
     * @param theMetamodelClass The metamodelClass to set.
     * @since 5.0
     */
    public void setMetamodelClass(String theMetamodelClass) {
        this.metamodelClass = theMetamodelClass;
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
     * @return Returns the name.
     * @since 5.0
     */
    public String getModelName() {
        return this.name;
    }

    
    /** 
     * @param theName The name to set.
     * @since 5.0
     */
    public void setModelName(String theName) {
        this.name = theName;
    }
    
    public boolean isEmpty() {
        if( this.modelType == null && this.metamodelClass == null && this.builderType == null ) {
            return true;
        }
        return false;
    }

}

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

package com.metamatrix.modeler.webservice.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.xsd.XSDElementDeclaration;

import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.workspace.ModelProject;


/** 
 * @since 4.3
 */
public class WebServiceBuildOptions implements Comparable {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ModelProject currentProject;
    private Object model;   // This is either a Model Resource or a ModelName
    private Object theInterface;
    private String operationName;
    private String operationInputMessageName;
    private XSDElementDeclaration operationInputMessageElem;
    private String operationOutputMessageName;
    private XSDElementDeclaration operationOutputMessageElem;
    private XmlDocument operationOutputXmlDoc;
    private IContainer locationContainer;
    
    private boolean useLocationContainer = false;
    
    /**
     * Default constructor
     */
    public WebServiceBuildOptions() {
    }

    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * @return Returns the currentProject.
     * @since 4.3
     */
    public ModelProject getCurrentProject() {
        return this.currentProject;
    }

    
    /** 
     * @param currentProject The currentProject to set.
     * @since 4.3
     */
    public void setCurrentProject(ModelProject currentProject) {
        this.currentProject = currentProject;
    }

    
    /** 
     * @return Returns the interface object.
     * @since 4.3
     */
    public Object getInterface() {
        return this.theInterface;
    }

    
    /** 
     * @param interfaceName The interfaceName to set.
     * @since 4.3
     */
    public void setInterface(Object theInterface) {
        this.theInterface = theInterface;
    }

    
    /** 
     * @return Returns the model.
     * @since 4.3
     */
    public Object getModel() {
        return this.model;
    }

    
    /** 
     * @param model The model to set.
     * @since 4.3
     */
    public void setModel(Object model) {
        this.model = model;
    }

    
    /** 
     * @return Returns the operationInputMessageElem.
     * @since 4.3
     */
    public XSDElementDeclaration getOperationInputMessageElem() {
        return this.operationInputMessageElem;
    }

    
    /** 
     * @param operationInputMessageElem The operationInputMessageElem to set.
     * @since 4.3
     */
    public void setOperationInputMessageElem(XSDElementDeclaration operationInputMessageElem) {
        this.operationInputMessageElem = operationInputMessageElem;
    }

    
    /** 
     * @return Returns the operationInputMessageName.
     * @since 4.3
     */
    public String getOperationInputMessageName() {
        return this.operationInputMessageName;
    }

    
    /** 
     * @param operationInputMessageName The operationInputMessageName to set.
     * @since 4.3
     */
    public void setOperationInputMessageName(String operationInputMessageName) {
        this.operationInputMessageName = operationInputMessageName;
    }

    
    /** 
     * @return Returns the operationName.
     * @since 4.3
     */
    public String getOperationName() {
        return this.operationName;
    }

    
    /** 
     * @param operationName The operationName to set.
     * @since 4.3
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    
    /** 
     * @return Returns the operationOutputMessageElem.
     * @since 4.3
     */
    public XSDElementDeclaration getOperationOutputMessageElem() {
        return this.operationOutputMessageElem;
    }

    
    /** 
     * @param operationOutputMessageElem The operationOutputMessageElem to set.
     * @since 4.3
     */
    public void setOperationOutputMessageElem(XSDElementDeclaration operationOutputMessageElem) {
        this.operationOutputMessageElem = operationOutputMessageElem;
    }

    
    /** 
     * @return Returns the operationOutputMessageName.
     * @since 4.3
     */
    public String getOperationOutputMessageName() {
        return this.operationOutputMessageName;
    }

    
    /** 
     * @param operationOutputMessageName The operationOutputMessageName to set.
     * @since 4.3
     */
    public void setOperationOutputMessageName(String operationOutputMessageName) {
        this.operationOutputMessageName = operationOutputMessageName;
    }

    
    /** 
     * @return Returns the operationOutputXmlDoc.
     * @since 4.3
     */
    public XmlDocument getOperationOutputXmlDoc() {
        return this.operationOutputXmlDoc;
    }

    
    /** 
     * @param operationOutputXmlDoc The operationOutputXmlDoc to set.
     * @since 4.3
     */
    public void setOperationOutputXmlDoc(XmlDocument operationOutputXmlDoc) {
        this.operationOutputXmlDoc = operationOutputXmlDoc;
    }
    
    public int compareTo(Object o) {
        if( o instanceof String) {
            return getOperationName().compareTo((String)o);
        }
        
        if( o instanceof WebServiceBuildOptions ) {
            return getOperationName().compareTo( ((WebServiceBuildOptions)o).getOperationName() );
        }
        return 0;
    }


    
    public IContainer getLocationContainer() {
        return this.locationContainer;
    }


    
    public void setLocationContainer(IContainer theLocationContainer) {
        this.locationContainer = theLocationContainer;
    }


    
    public boolean shouldUseLocationContainer() {
        return this.useLocationContainer;
    }


    
    public void setUseLocationContainer(boolean theUseLocationContainer) {
        this.useLocationContainer = theUseLocationContainer;
    }
    
}

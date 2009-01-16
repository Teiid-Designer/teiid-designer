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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDNamedComponent;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.xml.XmlValueHolder;


/** 
 * @since 4.2
 */
public class InputVariable {

    private String name;
    private String xpath;
    private final XSDNamedComponent schemaComponent;
    private final List xmlDocEntityForCriteria;
    
    /** 
     * 
     * @since 4.2
     */
    public InputVariable( final XSDNamedComponent schemaComponent ) {
        this(schemaComponent,null,null);
    }

    /** 
     * 
     * @since 4.2
     */
    public InputVariable( final XSDNamedComponent schemaComponent, final String name ) {
        this(schemaComponent,name,null);
    }

    /** 
     * 
     * @since 4.2
     */
    public InputVariable( final XSDNamedComponent schemaComponent, final String name, final String xpath ) {
        super();
        ArgCheck.isNotNull(schemaComponent);
        this.schemaComponent = schemaComponent;
        this.name = name;
        this.xpath = xpath;
        this.xmlDocEntityForCriteria = new ArrayList();
    }

    public String getName() {
        return this.name;
    }
    
    public boolean isNameValid() {
        return this.name != null && this.name.trim().length() != 0;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getXpath() {
        return this.xpath;
    }
    
    public void setXpath(final String xpath) {
        this.xpath = xpath;
    }
    
    public XSDNamedComponent getSchemaComponent() {
        return this.schemaComponent;
    }
    
    public List getXmlDocumentEntityForCriteria() {
        return this.xmlDocEntityForCriteria;
    }
    
    public void addXmlDocumentEntityForCriteria( final XmlValueHolder elementOrAttribute ) {
        if ( !this.xmlDocEntityForCriteria.contains(elementOrAttribute) ) {
            this.xmlDocEntityForCriteria.add(elementOrAttribute);
        }
    }
    
    /** 
     * @see java.lang.Object#toString()
     * @since 4.2
     */
    @Override
    public String toString() {
        return this.name + ":" + this.xpath + " = " + this.schemaComponent.getQName();  //$NON-NLS-1$//$NON-NLS-2$
    }
    
}

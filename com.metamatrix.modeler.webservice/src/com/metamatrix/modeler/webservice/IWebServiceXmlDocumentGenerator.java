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

package com.metamatrix.modeler.webservice;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.modeler.compare.selector.ModelSelector;


/** 
 * Interface for a component that generates XML Documents given one or more 
 * {@link com.metamatrix.metamodels.webservice.WebServiceComponent WebServiceComponent} objects.
 * @since 4.2
 */
public interface IWebServiceXmlDocumentGenerator {

    /**
     * Add a {@link WebServiceComponent} object that should be {@link #generate(IProgressMonitor) processed} 
     * to find {@link com.metamatrix.metamodels.webservice.Output Output} objects.  For each
     * Output object, a new {@link com.metamatrix.metamodels.xml.XmlDocument XmlDocument} 
     * will be added to the {@link #getXmlDocumentResource() XML Document model}.
     * @param webServiceComponent the Web Service object being added; may not be null
     * @see #addWebServiceComponents(List)
     * @see #getWebServiceComponents()
     * @since 4.2
     */
    void addWebServiceComponent( WebServiceComponent webServiceComponent );

    /**
     * Add multiple {@link WebServiceComponent} objects that should be 
     * {@link #generate(IProgressMonitor) processed}  to find
     * {@link com.metamatrix.metamodels.webservice.Output Output} objects.  For each
     * Output object, a new {@link com.metamatrix.metamodels.xml.XmlDocument XmlDocument} 
     * will be added to the {@link #getXmlDocumentResource() XML Document model}.
     * @param webServiceComponents the list of WebServiceComponent objects being added; may not be null
     * @see #addWebServiceComponent(WebServiceComponent)
     * @see #getWebServiceComponents()
     * @since 4.2
     */
    void addWebServiceComponents( List webServiceComponents );

    /**
     * Return the list of {@link WebServiceComponent} objects that will be 
     * {@link #generate(IProgressMonitor) processed} to locate Output objects.
     * @return the List of WebServiceComponent objects used in generation; never null
     * @see #addWebServiceComponent(WebServiceComponent)
     * @see #addWebServiceComponents(List)
     * @since 4.2
     */
    List getWebServiceComponents();

    /** 
     * Set the model selector containing the {@link #getWebServiceComponents() web service components}.
     * @param wsModelSelector the model selector
     * @since 4.2
     */
    void setWebServiceModelSelector(ModelSelector wsModelSelector);

    /** 
     * Get the model selector containing the {@link #getWebServiceComponents() web service components}.
     * @return the model selector
     * @since 4.2
     */
    ModelSelector getWebServiceModelSelector();

    /**
     * Set the XML Document model into which will be generated new 
     * {@link com.metamatrix.metamodels.xml.XmlDocument XmlDocument} objects.
     * @param wsModel the EMF resource; may not be null
     * @since 4.2
     */
    void setXmlDocumentResource( Resource wsModel);
    
    /**
     * Get the XML Document model into which will be generated new 
     * {@link com.metamatrix.metamodels.xml.XmlDocument XmlDocument} objects. 
     * @return the EMF resource
     * @since 4.2
     */
    Resource getXmlDocumentResource();
    
    /**
     * Generate the XML documents into the {@link #getXmlDocumentResource() XML document model}. 
     * @param monitor the progress monitor; may be null
     * @return the status with any information about the generation process, 
     * will be {@link IStatus#isOK() OK} if the generation was successful and
     * had no warnings or errors; never null
     * @see #generate(IProgressMonitor, List)
     * @since 4.2
     */
    IStatus generate( IProgressMonitor monitor );
    
    /**
     * Generate the web service objects and place in the {@link #getWebServiceResource() web service model}. 
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus problems} about the generation process should be placed;
     * may not be null
     * @see #generate(IProgressMonitor)
     * @since 4.2
     */
    void generate( IProgressMonitor monitor, List problems );

}

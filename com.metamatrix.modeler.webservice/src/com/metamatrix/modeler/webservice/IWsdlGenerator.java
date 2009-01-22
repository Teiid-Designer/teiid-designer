/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDSchema;

import com.metamatrix.metamodels.wsdl.WsdlPackage;


/** 
 * This interface is used to generate WSDL from one or more 
 * {@link com.metamatrix.metamodels.webservice.WebServicePackage Web Service} models and the
 * supporting {@link XSDSchema XML Schemas}.
 * 
 * @since 4.2
 */
public interface IWsdlGenerator {
    
    /**
     * The initial value for the {@link #getDefaultNamespaceUri() default namespace URI}.
     * @see #getDefaultNamespaceUri()
     */
    static final String INITIAL_DEFAULT_NAMESPACE_URI = WsdlPackage.eNS_URI;
    
    /**
     * Get the name of the WSDL that is to be generated. 
     * @return the name
     * @see #setName(String)
     * @since 4.2
     */
    String getName();
    
    /**
     * Get the target namespace of the WSDL that is to be generated. 
     * @return the target namespace URI
     * @see #setTargetNamespace(String)
     * @since 4.2
     */
    String getTargetNamespace();

    /**
     * Set the name of the WSDL that is to be generated.  This name will be the 
     * value of the <code>name</code> attribute on the <code>&lt;definitions&gt;</code> element. 
     * @param name the WSDL name; null or zero-length may result in an invalid WSDL document
     * @see #getName()
     * @since 4.2
     */
    void setName( String name );
    
    /**
     * Set the target namespace URI of the WSDL that is to be generated.  This value will be the 
     * value of the <code>targetNamespace</code> attribute on the <code>&lt;definitions&gt;</code> element. 
     * @param name the WSDL target namespace URI; null or zero-length may result in an invalid WSDL document
     * @see #getTargetNamespace()
     * @since 4.2
     */
    void setTargetNamespace( String targetNamspace );
    
    String getXmlEncoding();
    
    void setXmlEncoding( String xmlEncoding );
    
    /**
     * Get the URL root for references to files referenced by this WSDL, including XSDs.
     * @return the beginning of the URL location used in references and imports.
     * @see #setUrlRootForReferences(String)
     * @since 4.2
     */
    String getUrlRootForReferences();
    
    /**
     * Set the URL root for references to files referenced by this WSDL, including XSDs.
     * @param url the beginning of the URL location used in references and imports.
     * @see #getUrlRootForReferences()
     * @since 4.2
     */
    void setUrlRootForReferences(String url);
    
    /**
     * Get the URL suffix for references to files referenced by this WSDL, including XSDs.
     * @return the trailing part of the URL location used in references and imports.
     * @see #setUrlSuffixForReferences(String)
     * @since 4.2
     */
    String getUrlSuffixForReferences();
    
    /**
     * Set the URL suffix for references to files referenced by this WSDL, including XSDs.
     * @param suffix the trailing part of the URL location used in references and imports.
     * @see #getUrlSuffixForReferences()
     * @since 4.2
     */
    void setUrlSuffixForReferences(String suffix);
    
    /**
     * Get the URL for the service binding in the WSDL.
     * @return the URL location used in the service binding.
     * @see #setUrlForWsdlService(String)
     * @since 4.2
     */
    String getUrlForWsdlService();
    
    /**
     * Set the URL suffix for references to files referenced by this WSDL, including XSDs.
     * @param serviceUrl the URL location used in the service binding.
     * @see #getUrlForWsdlService()
     * @since 4.2
     */
    void setUrlForWsdlService(String serviceUrl);
    
    /**
     * Get the namespace URI that is to be the default namespace.  This is initially
     * set to {@link #INITIAL_DEFAULT_NAMESPACE_URI}. 
     * @return the URI for the target namespace that will have no namespace prefix.
     * @see #setDefaultNamespaceUri(String)
     * @see #INITIAL_DEFAULT_NAMESPACE_URI
     * @since 4.2
     */
    String getDefaultNamespaceUri();
    
    /**
     * Set the namespace URI that is to be the default namespace.
     * @param namespaceUri the URI for the target namespace that will have no namespace prefix;
     * if null or no match for any of the namespaces, all namespaces will have prefixes
     * @see #getDefaultNamespaceUri()
     * @see #INITIAL_DEFAULT_NAMESPACE_URI
     * @since 4.2
     */
    void setDefaultNamespaceUri( String namespaceUri );
    
    
    /**
     * Add a {@link com.metamatrix.metamodels.webservice.WebServicePackage Web Service} model
     * to be used by the generator.
     * @param resource the EMF resource containing the web service model; may not be null
     * @return true if the model could be added, or false if the supplied model is not
     * a valid web service model
     * @see #getWebServiceModels()
     * @since 4.2
     */
    boolean addWebServiceModel(Resource resource);
    
    /**
     * Add a {@link XSDSchema XML Schema} to be used by the generator.
     * @param xmlSchema the reference to the XML Schema; may not be null
     * @param pathForLocation the path that will be used for the location URI; may be null
     * if this should be just the name of the containing XML Schema document.
     * @return true if the XSD Schema could be added, or false if the supplied XML Schema
     * cannot be added
     * @see #getXSDSchemas()
     * @since 4.2
     */
    boolean addXsdModel(XSDSchema xmlSchema, IPath pathForLocation);
    
    /**
     * Obtain the list of web service models that have been added to this generator. 
     * @return the list of Resource instances; never null
     * @since 4.2
     * @see #addWebServiceModel(Resource)
     */
    List getWebServiceModels();
    
    /**
     * Obtain the list of XML Schemas that have been added to this generator. 
     * @return the list of {@link XSDSchema XML Schema} instances; never null
     * @since 4.2
     * @see #addXsdModel(XSDSchema)
     */
    List getXSDSchemas();
    
    /**
     * Generate the WSDL and store the contents internally. 
     * @param monitor the progress monitor; may be null
     * @return the status with any information about the generation process, 
     * will be {@link IStatus#isOK() OK} if the generation was successful and
     * had no warnings or errors; never null
     * @since 4.2
     */
    IStatus generate( IProgressMonitor monitor );
    
    /**
     * Write the generated WSDL to the supplied output stream. 
     * This method may be called repeatedly.
     * @param stream the output stream to which the WSDL should be written; may not be null
     * @throws IOException if there is an error writing to the supplied stream
     * @since 4.2
     */
    void write( OutputStream stream ) throws IOException;
    
    /**
     * Signal the generator to release any resources that were acquired during it's lifetime.
     * This also clears out all references to models and XSDs.
     * @since 4.2
     */
    void close();

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;


/** 
 * The <code>IWebServiceModelBuilder</code> uses imported WSDL files to add content to a new Web Services model.
 * <p>
 * Usage of the class is straightforward:
 * <ul>
 *   <li>{@link #addWsdlFile(IFile) Add} WSDL file(s)</li>
 *   <li>{@link #resolveNamespace(Object, IPath) Resolve namespaces} to XSD file locations</li>
 *   <li>Specify the {@link #setXmlModel(IPath) location} of XML document model that is to be generated</li>
 *   <li>Specify the {@link #setParentResource(IResource) location} and {@link #setModelPath(IPath) path}
 *       of the web service model that is to be generated</li>
 *   <li>{@link #validate() Validate} the input information</li>
 *   <li>Obtain and execute the {@link #getModelGenerator() model generator}</li>
 * </ul>
 * </p>
 * @since 4.2
 */
public interface IWebServiceModelBuilder {
    
    // =========================================================================
    //                       Validation-related Codes
    // =========================================================================
    
    static final int MULTIPLE_MESSAGES              = 18101;
    static final int UNRESOLVED_NAMESPACE           = 18102;
    static final int NO_WSDL_FILES                  = 18103;
    static final int MISSING_DESCRIPTOR             = 18104;
    static final int MISSING_MODEL_PATH             = 18105;
    static final int PARENT_LOCATION_NONEXISTANT    = 18106;
    static final int MISSING_PARENT_LOCATION        = 18107;
    static final int UNRESOLVED_SCHEMA_IMPORT       = 18108;
    
    
    // =========================================================================
    //                       Methods for the Web Service Model
    // =========================================================================
    
    /**
     * Gets the model's parent resource. 
     * @return
     * @since 4.2
     */
    IResource getParentResource();
    
    /**
     * Gets the path for the web service model. 
     * @return the path
     * @since 4.2
     */
    IPath getModelPath();
    
    /**
     * Sets the model descriptor. 
     * @param theDescriptor the model descriptor
     * @since 4.2
     */
    void setMetamodelDescriptor(MetamodelDescriptor theDescriptor);
    
    /**
     * Sets the model's parent resource. 
     * @param theResource the parent resource
     * @since 4.2
     */
    void setParentResource(IResource theResource);
    
    /**
     * Sets the model file system path. 
     * @param thePath the path
     * @since 4.2
     */
    void setModelPath(IPath thePath);
    
    /**
     * This method is used to get the map of physical files to URL's this map 
     * is used to allow the view to display URLs while under the covers the contents
     * of those URLs have been downloaded to the local file system. 
     * @return Map of String URL --> File object
     *
     */
    Map getUrlMap();
    
    // =========================================================================
    //                      Methods for the XML Document model
    // =========================================================================
    
    /**
     * Gets the XML model where the XML Documents are generated for each web service operation. 
     * @return the model or <code>null</code> if not set
     * @since 4.2
     */
    IPath getXmlModel();
    
    /**
     * Sets the XML model file system path. The XML model is where the XML Documents are generated for each
     * web service operation.
     * @param theXmlModel the XML model or <code>null</code> if removing current model
     * @since 4.2
     */
    void setXmlModel(IPath theXmlModel);
   
    // =========================================================================
    //                Methods for the Input Files (WSDL and XSD)
    // =========================================================================
    
    /**
     * Add an input resource from the file system.  If the file has been previously
     * added, this method returns the existing resource.
     * @param theFile the file outside of the workspace that is to be added; may not be null
     * @return the web service resource object; never null
     * @throws CoreException if a problem adding/loading the resource
     * @since 4.2
     */
    IWebServiceResource addResource( File theFile ) throws CoreException;
    
    /**
     * Add an input resource from the workspace.  If the file has been previously
     * added, this method returns the existing resource.
     * @param theFile the file in the workspace that is to be added; may not be null
     * @return the web service resource object; never null
     * @throws CoreException if a problem adding/loading the resource
     * @since 4.2
     */
    IWebServiceResource addResource( IFile theFile ) throws CoreException;
    
    /**
     * Resolve the supplied resource using the supplied file on the file system.
     * If the resource {@link IWebServiceResource#isResolved() is resolved}, the
     * existing {@link IWebServiceResource#getResolvedResource()} value on the 
     * <code>resource</code> instance is changed.
     * @param resource the resource that is to be resolved to the supplied file; may not be null
     * @param theFile the file on the file system that is to be added; may not be null
     * @since 4.2
     */
    void resolve(IWebServiceResource resource, File theFile);
    
    /**
     * Resolve the supplied resource using the supplied file in the workspace.
     * If the resource {@link IWebServiceResource#isResolved() is resolved}, the
     * existing {@link IWebServiceResource#getResolvedResource()} value on the 
     * <code>resource</code> instance is changed.
     * @param resource the resource that is to be resolved to the supplied file; may not be null
     * @param theFile the file in the workspace that is to be added; may not be null
     * @since 4.2
     */
    void resolve(IWebServiceResource resource, IFile theFile);
    
    /**
     * Unresolve the supplied <code>IWebServiceResource</code> by removing it's associated file system resource.
     * If the supplied <code>IWebServiceResource</code> is not currently resolved this method does nothing.
     * @param resource the resource that is being unresolved; may not be null
     * @since 4.2
     */
    void unresolve(IWebServiceResource resource);
    
    /**
     * Remove the supplied resource. 
     * @param theResource the resource that is to be removed; may not be null
     * @since 4.2
     */
    void remove(IWebServiceResource theResource);
    
    /**
     * Obtain all of the {@link IWebServiceResource} objects that are currently defined. 
     * @return the collection of {@link IWebServiceResource} objects; never null
     * @since 4.2
     */
    Collection<IWebServiceResource> getResources();
    
    /**
     * Obtain the EMF resource into which the supplied resource has been loaded,
     * if any. 
     * @param theResource
     * @return
     * @since 4.2
     */
    Resource getEmfResource( IWebServiceResource theResource);
    
    // =========================================================================
    //                  Placing XSDs into the Workspace
    // =========================================================================
    
    /**
     * Get the {@link IWebServiceXsdResource} instances that identify which
     * XML Schemas need to be copied into the workspace.
     */
    Collection getXsdDestinations();
    
    /**
     * Get the {@link IWebServiceXsdResource} instances that identify which 
     * WSDL's are involved in the transaction of building the web service model.
     */
    Collection getWSDLResources();
    
    /**
     * Set the path to the workspace location where this XSD is to be saved. 
     * @param workspacePathForXsd the IPath with the workspace location;
     * may be null
     * @since 4.2
     */
    void setDestinationPath( final IWebServiceXsdResource xsdResource, final IPath workspacePathForXsd );
    
    // =========================================================================
    //                           Validation
    // =========================================================================
    
    /**
     * Indicates if the model can be built in terms of WSDL namespace validation. Building can only occur if all wsdl namespaces have been resolved.
     * @return <code>true</code>if the model can be built; <code>false</code> otherwise.
     * @since 4.2
     */
    IStatus validateWSDLNamespaces();
    
    /**
     * Indicates if the model can be built with respect to the schemas imported in the wsdl. Building can only occur if all xsd namespaces have been resolved.
     * @return <code>true</code>if the model can be built; <code>false</code> otherwise.
     * @since 4.2
     */
    IStatus validateXSDNamespaces();
    // =========================================================================
    //                           Generation / Execution
    // =========================================================================
    
    /**
     * Return the ModelGenerator that can be used to perform the UML2 to Relational conversion/generation.
     * The model generator can be executed with one step (see {@link ModelGenerator#execute(IProgressMonitor)})
     * or can be used to {@link ModelGenerator#generateOutputAndDifferenceReport(IProgressMonitor) compute differences},
     * obtain a {@link ModelGenerator#getDifferenceReport() difference report}, and 
     * {@link ModelGenerator#mergeOutputIntoOriginal(IProgressMonitor) merge results}.
     * @throws CoreException if there is a problem creating the generator
     */
    ModelGenerator getModelGenerator(boolean isNewModel) throws CoreException;
    
    void setSaveAllBeforeFinish(boolean doSave);
    
    /**
     * Method provides wizards the chance to access any new resources created by this wizard and to fully clean-up any new resources
     * if user cancels the wizard or there is a major problem detected or encountered during the building of the models 
     * 
     * @since 5.0.2
     */
    List getAllNewResources();
    
    void setSelectedOperations(Collection operations);
    
    Collection getSelectedOperations();
}

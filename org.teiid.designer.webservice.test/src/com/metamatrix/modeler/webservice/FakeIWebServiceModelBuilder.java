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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;

/**
 * @since 4.2
 */
public class FakeIWebServiceModelBuilder implements IWebServiceModelBuilder {

    private IPath modelPath;
    private IPath xmlModelPath;

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean addWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean addWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getDependencies(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public Collection getDependencies( IFile theWsdlFile ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getDependencies(java.io.File)
     * @since 4.2
     */
    public Collection getDependencies( File theWsdlFile ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getXmlModel()
     * @since 4.2
     */
    public IPath getXmlModel() {
        return xmlModelPath;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getParentResource()
     * @since 4.2
     */
    public IResource getParentResource() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getModelPath()
     * @since 4.2
     */
    public IPath getModelPath() {
        return modelPath;
    }

    public Map getUrlMap() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getNamespaceResolutionRecords()
     * @since 4.2
     */
    public Collection getNamespaceResolutionRecords() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getWsdlFiles()
     * @since 4.2
     */
    public Collection getWsdlFiles() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#isWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean isWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#isWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean isWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#removeWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean removeWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#removeWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean removeWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#resolveNamespace(java.lang.Object,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public IStatus resolveNamespace( Object theNamespace,
                                     IPath thePath ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#unresolve(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public void unresolve( IWebServiceResource theResource ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setMetamodelDescriptor(com.metamatrix.modeler.core.metamodel.MetamodelDescriptor)
     * @since 4.2
     */
    public void setMetamodelDescriptor( MetamodelDescriptor theDescriptor ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setParentResource(org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    public void setParentResource( IResource theResource ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setModelPath(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setModelPath( IPath thePath ) {
        this.modelPath = thePath;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setXmlModel(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setXmlModel( IPath theXmlModel ) {
        this.xmlModelPath = theXmlModel;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getModelGenerator()
     * @since 4.2
     */
    public ModelGenerator getModelGenerator( boolean isNewModel ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#validate()
     * @since 4.2
     */
    public IStatus validate() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addResource(java.io.File)
     * @since 4.2
     */
    public IWebServiceResource addResource( File theFile ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addResource(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public IWebServiceResource addResource( IFile theFile ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#resolve(com.metamatrix.modeler.webservice.IWebServiceResource,
     *      java.io.File)
     * @since 4.2
     */
    public void resolve( IWebServiceResource resource,
                         File theFile ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#resolve(com.metamatrix.modeler.webservice.IWebServiceResource,
     *      org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public void resolve( IWebServiceResource resource,
                         IFile theFile ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#remove(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public void remove( IWebServiceResource theResource ) {
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getResources()
     * @since 4.2
     */
    public Collection getResources() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getEmfResource(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public Resource getEmfResource( IWebServiceResource theResource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getXsdDestinations()
     * @since 4.2
     */
    public Collection getXsdDestinations() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setDestinationPath(com.metamatrix.modeler.webservice.IWebServiceXsdResource,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setDestinationPath( IWebServiceXsdResource xsdResource,
                                    IPath workspacePathForXsd ) {
    }

    public IStatus validateWSDLNamespaces() {
        return null;
    }

    public IStatus validateXSDNamespaces() {
        return null;
    }

    public void validateWSDLs() {
    }

    public Collection getWSDLResources() {
        return null;
    }

    public void setSaveAllBeforeFinish( boolean theDoSave ) {
    }

    public List getAllNewResources() {
        return Collections.EMPTY_LIST;
    }

    public Collection getSelectedOperations() {
        return Collections.EMPTY_LIST;
    }

    public void setSelectedOperations( Collection theOperations ) {
    }

}

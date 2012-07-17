/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

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
import org.teiid.designer.compare.ModelGenerator;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.webservice.IWebServiceModelBuilder;
import org.teiid.designer.webservice.IWebServiceResource;
import org.teiid.designer.webservice.IWebServiceXsdResource;


/**
 * @since 4.2
 */
public class FakeIWebServiceModelBuilder implements IWebServiceModelBuilder {

    private IPath modelPath;
    private IPath xmlModelPath;

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#addWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean addWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#addWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean addWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getDependencies(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public Collection getDependencies( IFile theWsdlFile ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getDependencies(java.io.File)
     * @since 4.2
     */
    public Collection getDependencies( File theWsdlFile ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getXmlModel()
     * @since 4.2
     */
    @Override
	public IPath getXmlModel() {
        return xmlModelPath;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getParentResource()
     * @since 4.2
     */
    @Override
	public IResource getParentResource() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getModelPath()
     * @since 4.2
     */
    @Override
	public IPath getModelPath() {
        return modelPath;
    }

    @Override
	public Map getUrlMap() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getNamespaceResolutionRecords()
     * @since 4.2
     */
    public Collection getNamespaceResolutionRecords() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getWsdlFiles()
     * @since 4.2
     */
    public Collection getWsdlFiles() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#isWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean isWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#isWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean isWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#removeWsdlFile(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public boolean removeWsdlFile( IFile theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#removeWsdlFile(java.io.File)
     * @since 4.2
     */
    public boolean removeWsdlFile( File theFile ) {
        return false;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#resolveNamespace(java.lang.Object,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public IStatus resolveNamespace( Object theNamespace,
                                     IPath thePath ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#unresolve(org.teiid.designer.webservice.IWebServiceResource)
     * @since 4.2
     */
    @Override
	public void unresolve( IWebServiceResource theResource ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#setMetamodelDescriptor(org.teiid.designer.core.metamodel.MetamodelDescriptor)
     * @since 4.2
     */
    @Override
	public void setMetamodelDescriptor( MetamodelDescriptor theDescriptor ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#setParentResource(org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    @Override
	public void setParentResource( IResource theResource ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#setModelPath(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    @Override
	public void setModelPath( IPath thePath ) {
        this.modelPath = thePath;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#setXmlModel(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    @Override
	public void setXmlModel( IPath theXmlModel ) {
        this.xmlModelPath = theXmlModel;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getModelGenerator()
     * @since 4.2
     */
    @Override
	public ModelGenerator getModelGenerator( boolean isNewModel ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#validate()
     * @since 4.2
     */
    public IStatus validate() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#addResource(java.io.File)
     * @since 4.2
     */
    @Override
	public IWebServiceResource addResource( File theFile ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#addResource(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    @Override
	public IWebServiceResource addResource( IFile theFile ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#resolve(org.teiid.designer.webservice.IWebServiceResource,
     *      java.io.File)
     * @since 4.2
     */
    @Override
	public void resolve( IWebServiceResource resource,
                         File theFile ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#resolve(org.teiid.designer.webservice.IWebServiceResource,
     *      org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    @Override
	public void resolve( IWebServiceResource resource,
                         IFile theFile ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#remove(org.teiid.designer.webservice.IWebServiceResource)
     * @since 4.2
     */
    @Override
	public void remove( IWebServiceResource theResource ) {
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getResources()
     * @since 4.2
     */
    @Override
	public Collection getResources() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getEmfResource(org.teiid.designer.webservice.IWebServiceResource)
     * @since 4.2
     */
    @Override
	public Resource getEmfResource( IWebServiceResource theResource ) {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#getXsdDestinations()
     * @since 4.2
     */
    @Override
	public Collection getXsdDestinations() {
        return null;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceModelBuilder#setDestinationPath(org.teiid.designer.webservice.IWebServiceXsdResource,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    @Override
	public void setDestinationPath( IWebServiceXsdResource xsdResource,
                                    IPath workspacePathForXsd ) {
    }

    @Override
	public IStatus validateWSDLNamespaces() {
        return null;
    }

    @Override
	public IStatus validateXSDNamespaces() {
        return null;
    }

    public void validateWSDLs() {
    }

    @Override
	public Collection getWSDLResources() {
        return null;
    }

    @Override
	public void setSaveAllBeforeFinish( boolean theDoSave ) {
    }

    @Override
	public List getAllNewResources() {
        return Collections.EMPTY_LIST;
    }

    @Override
	public Collection getSelectedOperations() {
        return Collections.EMPTY_LIST;
    }

    @Override
	public void setSelectedOperations( Collection theOperations ) {
    }

}

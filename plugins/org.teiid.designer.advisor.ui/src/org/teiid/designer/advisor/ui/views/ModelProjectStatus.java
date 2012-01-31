/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * 
 */
public class ModelProjectStatus extends MultiStatus {
    private IProject currentModelProject;

    private boolean workspaceChanged = false;

    private IStatus modelStatus;
    private IStatus sourceModelsStatus;
    private IStatus viewModelsStatus;
    private IStatus connectionFactoriesStatus;
    private IStatus xmlViewModelsStatus;
    private IStatus xmlSchemaFilesStatus;
    private IStatus webServiceOperationsStatus;
    private IStatus previewWsdlStatus;
    private IStatus vdbsStatus;
    private IStatus testStatus;

    /**
     * @param thePluginId
     * @param theCode
     * @param theNewChildren
     * @param theMessage
     * @param theException
     * @since 4.3
     */
    public ModelProjectStatus( String thePluginId,
                               int theCode,
                               IStatus[] theNewChildren,
                               String theMessage,
                               Throwable theException ) {
        super(thePluginId, theCode, theNewChildren, theMessage, theException);
    }

    /**
     * @param thePluginId
     * @param theCode
     * @param theMessage
     * @param theException
     * @since 4.3
     */
    public ModelProjectStatus( String thePluginId,
                               int theCode,
                               String theMessage,
                               Throwable theException ) {
        super(thePluginId, theCode, theMessage, theException);
    }

    /**
     * @see org.eclipse.core.runtime.MultiStatus#add(org.eclipse.core.runtime.IStatus)
     * @since 4.3
     */
    @Override
    public void add( IStatus theStatus ) {
        super.add(theStatus);
    }

    /**
     * @return Returns the cnnectionFactoriesStatus.
     * @since 4.3
     */
    public IStatus getConnectionFactoriesStatus() {
        return this.connectionFactoriesStatus;
    }

    /**
     * @return currentModelProject
     */
    public IProject getCurrentModelProject() {
        return currentModelProject;
    }

    /**
     * @return Returns the modelStatus.
     * @since 4.3
     */
    public IStatus getModelStatus() {
        return this.modelStatus;
    }

    /**
     * @return Returns the previewWsdlStatus.
     * @since 4.3
     */
    public IStatus getPreviewWsdlStatus() {
        return this.previewWsdlStatus;
    }

    /**
     * @return Returns the sourceModelsStatus.
     * @since 4.3
     */
    public IStatus getSourceModelsStatus() {
        return this.sourceModelsStatus;
    }
    
    /**
     * @return Returns the testStatus.
     * @since 4.3
     */
    public IStatus getTestStatus() {
        return this.testStatus;
    }

    /**
     * @return Returns the vdbsStatus.
     * @since 4.3
     */
    public IStatus getVdbsStatus() {
        return this.vdbsStatus;
    }

    /**
     * @return Returns the viewModelsStatus.
     * @since 4.3
     */
    public IStatus getViewModelsStatus() {
        return this.viewModelsStatus;
    }

    /**
     * @return Returns the webServiceOperationsStatus.
     * @since 4.3
     */
    public IStatus getWebServiceOperationsStatus() {
        return this.webServiceOperationsStatus;
    }

    /**
     * @return Returns the xmlSchemaFilesStatus.
     * @since 4.3
     */
    public IStatus getXmlSchemaFilesStatus() {
        return this.xmlSchemaFilesStatus;
    }

    /**
     * @return Returns the xmlViewModelsStatus.
     * @since 4.3
     */
    public IStatus getXmlViewModelsStatus() {
        return this.xmlViewModelsStatus;
    }

    /**
     * @return workspaceChanged
     */
    public boolean isWorkspaceChanged() {
        return workspaceChanged;
    }

    /**
     * @param theConnectionFactoriesStatus The connectorBindingsStatus to set.
     * @since 4.3
     */
    public void setConnectionFactoriesStatus( IStatus theConnectionFactoriesStatus ) {
        this.connectionFactoriesStatus = theConnectionFactoriesStatus;
    }

    /**
     * @param currentModelProject Sets currentModelProject to the specified value.
     */
    public void setCurrentModelProject( IProject currentModelProject ) {
        this.currentModelProject = currentModelProject;
    }

    /**
     * @param theModelStatus The modelStatus to set.
     * @since 4.3
     */
    public void setModelStatus( IStatus theModelStatus ) {
        this.modelStatus = theModelStatus;
    }

    /**
     * @param thePreviewWsdlStatus The previewWsdlStatus to set.
     * @since 4.3
     */
    public void setPreviewWsdlStatus( IStatus thePreviewWsdlStatus ) {
        this.previewWsdlStatus = thePreviewWsdlStatus;
    }

    /**
     * @param theSourceModelsStatus The sourceModelsStatus to set.
     * @since 4.3
     */
    public void setSourceModelsStatus( IStatus theSourceModelsStatus ) {
        this.sourceModelsStatus = theSourceModelsStatus;
    }
    
    /**
     * @param theVdbsStatus The vdbsStatus to set.
     * @since 4.3
     */
    public void setTestStatus( IStatus theTestStatus ) {
        this.testStatus = theTestStatus;
    }

    /**
     * @param theVdbsStatus The vdbsStatus to set.
     * @since 4.3
     */
    public void setVdbsStatus( IStatus theVdbsStatus ) {
        this.vdbsStatus = theVdbsStatus;
    }

    /**
     * @param theViewModelsStatus The viewModelsStatus to set.
     * @since 4.3
     */
    public void setViewModelsStatus( IStatus theViewModelsStatus ) {
        this.viewModelsStatus = theViewModelsStatus;
    }

    /**
     * @param theWebServiceOperationsStatus The webServiceOperationsStatus to set.
     * @since 4.3
     */
    public void setWebServiceOperationsStatus( IStatus theWebServiceOperationsStatus ) {
        this.webServiceOperationsStatus = theWebServiceOperationsStatus;
    }

    /**
     * @param workspaceChanged Sets workspaceChanged to the specified value.
     */
    public void setWorkspaceChanged( boolean workspaceChanged ) {
        this.workspaceChanged = workspaceChanged;
    }

    /**
     * @param theXmlSchemaFilesStatus The xmlSchemaFilesStatus to set.
     * @since 4.3
     */
    public void setXmlSchemaFilesStatus( IStatus theXmlSchemaFilesStatus ) {
        this.xmlSchemaFilesStatus = theXmlSchemaFilesStatus;
    }

    /**
     * @param theXmlViewModelsStatus The xmlViewModelsStatus to set.
     * @since 4.3
     */
    public void setXmlViewModelsStatus( IStatus theXmlViewModelsStatus ) {
        this.xmlViewModelsStatus = theXmlViewModelsStatus;
    }

}

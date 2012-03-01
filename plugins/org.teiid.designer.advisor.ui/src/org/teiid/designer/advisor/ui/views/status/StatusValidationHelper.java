/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;

/**
 * 
 */
public class StatusValidationHelper implements StatusValidationConstants {
    // private static final String XML_LITERAL_TYPE_URI_STRING =
    // URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI).appendFragment(DatatypeConstants.BuiltInNames.XML_LITERAL).toString();

    private static final String TRANSACTION_DESCRIPTION = "Creating new Data Services Project Status"; //$NON-NLS-1$

    // private ModelProjectStatus currentStatus;

    private IProject currentProject;

    private Map eResourceToPath;

    private int sourceModelCount = 0;
    private int viewModelCount = 0;
    private int xmlViewModelCount = 0;
    private int webServiceModelCount = 0;
    private int xmlServiceModelCount = 0;
    private int schemaFileCount = 0;
    private int relationalViewModelCount = 0;
    private int vdbCount = 0;
    private boolean modelErrorsExist = false;
    private boolean sourceModelErrors = false;
    private boolean viewModelErrors = false;
    private boolean xmlViewModelErrors = false;
    private boolean schemaModelErrors = false;
    private boolean webServiceModelErrors = false;
    private boolean xmlServiceModelErrors = false;
    private boolean vdbErrors = false;

    private IResource[] allResources;

    private boolean validating = false;

    public StatusValidationHelper() {
        super();
    }

    /**
     * @since 4.3
     */
    public StatusValidationHelper( IProject project ) {
        super();
        this.currentProject = project;
    }

    // Needed to wrap in transaction so these don't cause UNDO events.
    public ModelProjectStatus getCurrentStatus() {
        validating = true;
        // System.out.println("WebServiceValidationHelper.getCurrentStatus() ---------- START ------------------");
        ModelProjectStatus status = null;

        resetInitialState();

        this.setCurrentProject(AdvisorUiPlugin.getStatusManager().getCurrentProject());

        // Load all resources
        loadAndSetResources();

        boolean started = ModelerCore.startTxn(false,
                                               false,
                                               TRANSACTION_DESCRIPTION,
                                               new DefaultIgnorableNotificationSource(StatusValidationHelper.this));
        boolean succeeded = false;
        try {
            status = createNewStatus();
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        // System.out.println("WebServiceValidationHelper.getCurrentStatus() ---------- END ------------------\n");
        validating = false;
        return status;
    }

    private ModelProjectStatus createNewStatus() {
        if (this.currentProject == null) {
            return STATUS_MSGS.NO_PROJECT_SELECTED;
        }

        processModelTypes(allResources);

        boolean incomplete = false;
        boolean hasErrors = false;
        boolean noModels = allResources.length == 0;

        IStatus sourcesStatus = null;
        IStatus modelStatus = null;
        IStatus bindingsStatus = null;
        IStatus viewsStatus = null;
        IStatus xmlViewsStatus = null;
        IStatus schemaStatus = null;
        IStatus webServicesStatus = null;
        IStatus completedStatus = null;
        IStatus vdbsStatus = null;
        IStatus testStatus = null;

        // 1) No models have ERRORS
        // NOTE: any missing or busted transformation will be taken care of here.
        if (modelErrorsExist) {
            modelStatus = STATUS_MSGS.MODEL_PROBLEMS_ERROR;
            hasErrors = true;
        } else if (noModels) {
            modelStatus = STATUS_MSGS.NO_MODELS_NO_PROBLEMS;
        } else {
            modelStatus = STATUS_MSGS.MODEL_PROBLEMS_OK;
        }

        // 2) Source models exist
        if (sourceModelCount > 0) {
            // Need to check here if any source models have problems???
            if (sourceModelErrors) {
                sourcesStatus = STATUS_MSGS.SOURCE_MODELS_HAVE_ERRORS;
            } else {
                sourcesStatus = STATUS_MSGS.SOURCE_MODELS_ARE_OK;
            }
        } else {
            sourcesStatus = STATUS_MSGS.NO_SOURCE_MODELS_ARE_DEFINED;
            incomplete = true;
        }

        // 3) Connector bindings exist for all sources and have no errors
        if (!isValidModelProject() || (isValidModelProject() && !connectorBindingErrorsExist())) {
            bindingsStatus = STATUS_MSGS.CONNECTOR_BINDINGS_OK;
        } else {
            if (sourcesStatus == STATUS_MSGS.NO_SOURCE_MODELS_ARE_DEFINED) {
                bindingsStatus = STATUS_MSGS.NO_CONNECTOR_BINDINGS_NO_SOURCES;
                incomplete = true;
            } else {
                bindingsStatus = STATUS_MSGS.CONNECTOR_BINDINGS_ERROR;
                hasErrors = true;
            }

        }

        // 2) View models exist
        if (relationalViewModelCount > 0) {
            // Need to check here if any source models have problems???
            if (viewModelErrors) {
                viewsStatus = STATUS_MSGS.VIEW_MODELS_HAVE_ERRORS;
            } else {
                viewsStatus = STATUS_MSGS.VIEW_MODELS_ARE_OK;
            }
        } else {
            viewsStatus = STATUS_MSGS.NO_VIEW_MODELS_ARE_DEFINED;
            incomplete = true;
        }

        // 4) XML View Models exist
        if (xmlViewModelCount > 0) {
            if (xmlViewModelErrors) {
                xmlViewsStatus = STATUS_MSGS.XML_VIEW_MODELS_HAVE_ERRORS;
            } else {
                xmlViewsStatus = STATUS_MSGS.XML_VIEW_MAPPINGS_OK;
            }
        } else if (xmlViewModelCount == 0) {
            xmlViewsStatus = STATUS_MSGS.NO_XML_VIEW_MODELS_ARE_DEFINED;
            incomplete = true;
        }

        // 5) XML Schema files exist
        if (schemaFileCount > 0) {
            if (schemaModelErrors) {
                schemaStatus = STATUS_MSGS.SCHEMA_MODELS_HAVE_ERRORS;
            } else {
                schemaStatus = STATUS_MSGS.SCHEMA_MODELS_ARE_DEFINED;
            }
        } else {
            schemaStatus = STATUS_MSGS.NO_SCHEMA_MODELS_ARE_DEFINED;
            incomplete = true;
        }
        // 6) Web Service Models exist and they contain at least one valid Operation
        if (webServiceModelCount > 0 || xmlServiceModelCount > 0) {
            if (webServiceModelErrors && xmlServiceModelErrors) {
                webServicesStatus = STATUS_MSGS.WEB_SERVICE_MODELS_HAVE_ERRORS;
            } else {
                webServicesStatus = STATUS_MSGS.WEB_SERVICE_MODELS_ARE_OK;
            }
        } else {
            webServicesStatus = STATUS_MSGS.NO_WEB_SERVICE_MODELS_ARE_DEFINED;
            incomplete = true;
        }

        // 8) VDBs

        if( vdbCount > 0 ) {
        	if( vdbErrors ) {
        		vdbsStatus = STATUS_MSGS.VDBS_HAVE_ERRORS;
        	} else {
        		vdbsStatus = STATUS_MSGS.VDBS_OK;
        	}
        	testStatus = STATUS_MSGS.VDBS_OK;
        } else {
        	vdbsStatus = STATUS_MSGS.NO_VDBS_ARE_DEFINED;
        	testStatus = STATUS_MSGS.NO_VDBS_ARE_DEFINED;
        }
        ModelProjectStatus status = null;
        completedStatus = Status.OK_STATUS;
        if (hasErrors) {
            completedStatus = STATUS_MSGS.COMPLETION_ERRORS_EXIST;
        } else if (incomplete) {
            completedStatus = STATUS_MSGS.COMPLETION_INCOMPLETE;
        } else {
            completedStatus = STATUS_MSGS.COMPLETION_OK;
        }

        // NOTE: MultiStatus object will determine the final Severity based on the most severe status, so we don't need to
        // set it here.
        status = new ModelProjectStatus(AdvisorUiConstants.PLUGIN_ID, IStatus.OK, completedStatus.getMessage(), null);
        if (noModels) {
            // status.set(true);
        }
        status.setCurrentModelProject(currentProject);

        status.setModelStatus(modelStatus);
        status.add(modelStatus);
        status.setSourceModelsStatus(sourcesStatus);
        status.add(sourcesStatus);
        status.setViewModelsStatus(viewsStatus);
        status.add(viewsStatus);
        status.setXmlViewModelsStatus(xmlViewsStatus);
        status.add(xmlViewsStatus);
        status.setWebServiceOperationsStatus(webServicesStatus);
        status.add(webServicesStatus);
        status.setConnectionFactoriesStatus(bindingsStatus);
        status.add(bindingsStatus);
        status.setXmlSchemaFilesStatus(schemaStatus);
        status.add(schemaStatus);
        status.setWebServiceOperationsStatus(webServicesStatus);
        status.add(webServicesStatus);
        status.setVdbsStatus(vdbsStatus);
        status.add(vdbsStatus);
        status.setTestStatus(testStatus);
        status.add(testStatus);

        return status;
    }

    private void resetInitialState() {
        sourceModelCount = 0;
        viewModelCount = 0;
        relationalViewModelCount = 0;
        xmlViewModelCount = 0;
        webServiceModelCount = 0;
        xmlServiceModelCount = 0;
        schemaFileCount = 0;
        vdbCount = 0;
        modelErrorsExist = false;
        sourceModelErrors = false;
        viewModelErrors = false;
        xmlViewModelErrors = false;
        schemaModelErrors = false;
        webServiceModelErrors = false;
        xmlServiceModelErrors = false;
        vdbErrors = false;
        allResources = new IResource[0];
        if (eResourceToPath != null) {
            eResourceToPath.clear();
        } else {
            eResourceToPath = new HashMap();
        }
    }

    private IResource[] getResourcesForCurrentProject() throws CoreException {
        ModelResource[] mrs = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
        List<IResource> resources = new ArrayList<IResource>();

        for (ModelResource mr : mrs) {
            if (mr.getModelProject().getProject().equals(currentProject)) {
                try {
                    resources.add(mr.getCorrespondingResource());
                } catch (ModelWorkspaceException e) {
                	AdvisorUiConstants.UTIL.log(e);
                }
            }
        }
        // Check for VDBs
        
        Collection<IFile> vdbs = WorkspaceResourceFinderUtil.getAllWorkspaceResources(WorkspaceResourceFinderUtil.VDB_RESOURCE_FILTER);
        for( IFile vdb : vdbs ) {
        	if( vdb.getProject().equals(currentProject) ) {
                resources.add(vdb);
        	}
        }
        
        
        if (resources.isEmpty()) {
            return new IResource[0];
        }

        IResource[] resArray = new IResource[resources.size()];
        int i = 0;
        for (IResource res : resources) {
            resArray[i++] = res;
        }
        return resArray;
    }

    private void loadAndSetResources() {
        if (isValidModelProject()) {
            try {
                allResources = getResourcesForCurrentProject();
            } catch (CoreException e) {
            	AdvisorUiConstants.UTIL.log(e);
            }
        }
    }

    // private boolean loadResourceIfNecessaryInTransaction(ModelResource modelResource, Resource emfResource) {
    // boolean loadedResource = false;
    // if( modelResource != null && !modelResource.isLoaded() ) {
    // boolean started = ModelerCore.startTxn(false, false, TRANSACTION_DESCRIPTION,
    // new DefaultIgnorableNotificationSource(WebServiceValidationHelper.this));
    // boolean succeeded = false;
    // try {
    // boolean noErrors = true;
    // try {
    // //System.out.println(" WSVH.loadResourceIfNotLoaded():  Loading res = " + modelResource.getResource().getName());
    // // Call getEmfResource() to actually load the Model Buffer
    // modelResource.getEmfResource();
    // modelResource.open(new NullProgressMonitor());
    // // We know we opened this model and it should not be Modified because of it!!!!
    // // Set to FALSE
    // loadedResource = true;
    // } catch (ModelWorkspaceException theException) {
    // noErrors = false;
    // VdbViewConstants.Util.log(theException);
    // } finally {
    // succeeded = noErrors;
    // }
    // } finally {
    // if ( started ) {
    // if ( succeeded ) {
    // ModelerCore.commitTxn();
    // } else {
    // ModelerCore.rollbackTxn();
    // }
    // }
    // }
    // }
    // return loadedResource;
    // }

    private boolean isValidModelProject() {
        return (currentProject != null && currentProject.isAccessible());
    }

    private void processModelTypes( IResource[] resources ) {

        for (IResource resource : resources) {
            boolean resourceHasErrors = errorMarkersExist(resource);
            if (resourceHasErrors && !modelErrorsExist) {
                modelErrorsExist = true;
            }

            if (ModelIdentifier.isPhysicalModelType(resource)) {
                sourceModelCount++;
                if (resourceHasErrors && !sourceModelErrors) {
                    sourceModelErrors = true;
                }
            } else if (ModelIdentifier.isRelationalViewModel(resource)) {
                // Straight relational virtual (VIEW) model
                relationalViewModelCount++;
                if (resourceHasErrors && !viewModelErrors) {
                	viewModelErrors = true;
                }
            } else if (ModelIdentifier.isWebServicesViewModel(resource)) {
                webServiceModelCount++;
                if (resourceHasErrors && !webServiceModelErrors) {
                    webServiceModelErrors = true;
                }
            } else if (ModelIdentifier.isSchemaModel(resource)) {
                schemaFileCount++;
                if (resourceHasErrors && !schemaModelErrors) {
                    schemaModelErrors = true;
                }
            }
            
            if( ModelUtil.isVdbArchiveFile(resource) && !resource.getName().toUpperCase().startsWith("PREVIEW_") ) { //$NON-NLS-1$
            	vdbCount++;
            	if( resourceHasErrors && !vdbErrors ) {
            		vdbErrors = true;
            	}
            }
        }

    }

    private boolean errorMarkersExist( IResource resource ) {
        if (resource.exists()) {
            IMarker[] markers = null;

            try {
                markers = ((IFile)resource).findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
            } catch (CoreException theException) {
            	AdvisorUiConstants.UTIL.log(theException);
            }

            if (markers != null && markers.length > 0) {
                for (int i = 0; i < markers.length; i++) {
                    int severity = markers[i].getAttribute(IMarker.SEVERITY, -1);
                    if (severity == IMarker.SEVERITY_ERROR) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean connectorBindingErrorsExist() {
        if (sourceModelCount == 0) {
            return true;
        }

        try {
            // TODO:
        } catch (Exception ex) {
        	AdvisorUiConstants.UTIL.log(ex);
        }

        return false;
    }

    /**
     * @return Returns the vdbContext.
     * @since 4.3
     */
    public IProject getCurrentProject() {
        return this.currentProject;
    }

    /**
     * @param theVdbContext The vdbContext to set.
     * @since 4.3
     */
    public void setCurrentProject( IProject project ) {
        this.currentProject = project;
    }

    /**
     * @return Returns the validating.
     * @since 5.0
     */
    public boolean isValidating() {
        return this.validating;
    }

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.ui.viewsupport;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;

public class DesignerProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

    public DesignerProperties(String guideID) {
		super();
		setProperty(IPropertiesContext.KEY_GUIDE_ID, guideID);
	}

    public String getGuideID() {
    	return getProperty(IPropertiesContext.KEY_GUIDE_ID);
    }
    
	@Override
	public synchronized void clear() {
		String guideID = getGuideID();
		super.clear();
		setGuideID(guideID);
	}

	public String getProjectName() {
        return getProperty(IPropertiesContext.KEY_PROJECT_NAME);
    }

    public String getVdbName() {
        return getProperty(IPropertiesContext.KEY_LAST_VDB_NAME);
    }
    
    public String getVdbJndiName() {
        return getProperty(IPropertiesContext.KEY_VDB_DATA_SOURCE_JNDI_NAME);
    }

    public String getSourceModelName() {
        return getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME);
    }

    public String getViewModelName() {
        return getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME);
    }

    public String getConnectionProfileName() {
        return getProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID);
    }

    public String getSourcesFolderName() {
        return getProperty(IPropertiesContext.KEY_SOURCES_FOLDER);
    }

    public String getViewsFolderName() {
        return getProperty(IPropertiesContext.KEY_VIEWS_FOLDER);
    }

    public String getSchemaFolderName() {
        return getProperty(IPropertiesContext.KEY_SCHEMA_FOLDER);
    }

    public String getWebServiceFolderName() {
        return getProperty(IPropertiesContext.KEY_WS_FOLDER);
    }

    public String getPreviewTargetObjectName() {
        return getProperty(IPropertiesContext.KEY_PREVIEW_TARGET_OBJECT);
    }

    public String getPreviewTargetModelName() {
        return getProperty(IPropertiesContext.KEY_PREVIEW_TARGET_MODEL);
    }
    
    public String getLastSourceModelObjectName() {
        return getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_OBJECT_NAME);
    }
    public String getLastViewModelObjectName() {
        return getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_OBJECT_NAME);
    }
    
    public boolean isImportXmlRemote() {
    	String value = getProperty(IPropertiesContext.KEY_IMPORT_XML_TYPE);
    	if( value != null && value.equalsIgnoreCase(IPropertiesContext.IMPORT_XML_REMOTE)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public void setGuideID(String ID) {
    	put(IPropertiesContext.KEY_GUIDE_ID, ID);
    }

    public void setProjectName(String projectName ) {
        put(IPropertiesContext.KEY_PROJECT_NAME, projectName);
    }

    public void setVdbName(String vdbName ) {
        put(IPropertiesContext.KEY_LAST_VDB_NAME, vdbName);
    }
    
    public void setVdbJndiName(String vdbJndiName ) {
        put(IPropertiesContext.KEY_VDB_DATA_SOURCE_JNDI_NAME, vdbJndiName);
    }

    public void setSourceModelName(String sourceModelName ) {
        put(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME, sourceModelName);
    }

    public void setViewModelName(String viewModelName ) {
        put(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME, viewModelName);
    }

    public void setConnectionProfileName(String connProfileName ) {
        put(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID, connProfileName);
    }

    public void setSourcesFolderName(String sourcesFolderName ) {
        put(IPropertiesContext.KEY_SOURCES_FOLDER, sourcesFolderName);
    }

    public void setViewsFolderName(String sourcesFolderName ) {
        put(IPropertiesContext.KEY_VIEWS_FOLDER, sourcesFolderName);
    }

    public void setSchemaFolderName(String schemaFolderName ) {
        put(IPropertiesContext.KEY_SCHEMA_FOLDER, schemaFolderName);
    }

    public void setWebServiceFolderName(String webServiceFolderName ) {
        put(IPropertiesContext.KEY_WS_FOLDER, webServiceFolderName);
    }

    public void setPreviewTargetObjectName(String previewTargetObjectName ) {
        put(IPropertiesContext.KEY_PREVIEW_TARGET_OBJECT, previewTargetObjectName);
    }

    public void setPreviewTargetModelName(String previewTargetModelName ) {
        put(IPropertiesContext.KEY_PREVIEW_TARGET_MODEL, previewTargetModelName);
    }
    
    public void setLastViewModelObjectName(String name ) {
    	put(IPropertiesContext.KEY_LAST_VIEW_MODEL_OBJECT_NAME, name);
	}
    
    public void setLastSourceModelObjectName(String name ) {
    	put(IPropertiesContext.KEY_LAST_SOURCE_MODEL_OBJECT_NAME, name);
	}


    /**
     * Get the Sources Folder, if the properties are defined
     * 
     * @param properties the Designer properties
     * @return the Sources Folder Container, null if not defined
     */
    public IContainer getSourcesFolder() {
        IContainer folder = null;
        // check for project property and if sources folder property exists
        String projectName = getProperty(IPropertiesContext.KEY_PROJECT_NAME);
        if (projectName != null && !projectName.isEmpty()) {
            String folderName = projectName;
            String sourcesFolder = getProperty(IPropertiesContext.KEY_SOURCES_FOLDER);
            if (sourcesFolder != null && !sourcesFolder.isEmpty()) {
                folderName = new Path(projectName).append(sourcesFolder).toString();
            }
            final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
            if (resrc != null) {
                folder = (IContainer)resrc;
            }
        }
        return folder;
    }

    /**
     * Get the Views Folder, if the properties are defined
     * 
     * @param properties the Designer properties
     * @return the Views Folder Container, null if not defined
     */
    public IContainer getViewsFolder() {
        IContainer folder = null;
        // check for project property and if sources folder property exists
        String projectName = getProperty(IPropertiesContext.KEY_PROJECT_NAME);
        if (projectName != null && !projectName.isEmpty()) {
            String folderName = projectName;
            String viewsFolder = getProperty(IPropertiesContext.KEY_VIEWS_FOLDER);
            if (viewsFolder != null && !viewsFolder.isEmpty()) {
                folderName = new Path(projectName).append(viewsFolder).toString();
            }
            final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
            if (resrc != null) {
                folder = (IContainer)resrc;
            }
        }
        return folder;
    }

    /**
     * Get the Project, if the properties are defined and project can be found. Also, the Project must be OPEN - or will return
     * null.
     * 
     * @param properties the Designer properties
     * @return the IProject, null if not defined or found
     */
    public IProject getProject() {
        IProject project = null;
        String projectName = getProperty(IPropertiesContext.KEY_PROJECT_NAME);
        if (projectName != null) {
            IProject[] openProjects = DotProjectUtils.getOpenModelProjects();
            for (IProject openProject : openProjects) {
                if (openProject.getName().equals(projectName)) {
                    project = openProject;
                    break;
                }
            }
        }
        return project;
    }

    /**
     * Get the View Model, if the properties are defined and model can be found
     * 
     * @param properties the Designer properties
     * @return the IFile, null if not defined or found
     */
    public IFile getViewModel() {
        IFile viewModel = null;
        String modelName = getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME);
        if (modelName != null) {
            // Expect ModelName to end with extension
            if (!modelName.endsWith(ModelUtil.DOT_EXTENSION_XMI)) modelName = modelName + ModelUtil.DOT_EXTENSION_XMI;

            final IResource resrc = ModelUtilities.findModelByName(modelName);
            if (resrc != null && ModelUtil.isModelFile(resrc) && ModelIdentifier.isVirtualModelType(resrc)) {
                viewModel = (IFile)resrc;
            }
        }
        return viewModel;
    }

    /**
     * Get the Source Model, if the properties are defined and model can be found
     * 
     * @param properties the Designer properties
     * @return the IFile, null if not defined or found
     */
    public IFile getSourceModel() {
        IFile sourceModel = null;
        String modelName = getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME);
        if (modelName != null) {
            // Expect ModelName to end with extension
            if (!modelName.endsWith(ModelUtil.DOT_EXTENSION_XMI)) modelName = modelName + ModelUtil.DOT_EXTENSION_XMI;

            final IResource resrc = ModelUtilities.findModelByName(modelName);
            if (resrc != null && ModelUtil.isModelFile(resrc) && ModelIdentifier.isPhysicalModelType(resrc)) {
                sourceModel = (IFile)resrc;
            }
        }
        return sourceModel;
    }

    /**
     * Get the VDB, if the properties are defined and vdb can be found
     * 
     * @param properties the Designer properties
     * @return the IResource, null if not defined or found
     */
    public IResource getVDB() {
        IResource vdbResource = null;
        // check for vdb name property
        String vdbName = getProperty(IPropertiesContext.KEY_LAST_VDB_NAME);
        if (vdbName != null) {
            // Try to find VDB in workspace - collect only vdb resources from the workspace
            // Collect only vdb archive resources from the workspace
            final Collection vdbResources = WorkspaceResourceFinderUtil.getAllWorkspaceResources(WorkspaceResourceFinderUtil.VDB_RESOURCE_FILTER);
            for (final Iterator iter = vdbResources.iterator(); iter.hasNext();) {
                final IResource vdb = (IResource)iter.next();
                if (vdb.getFullPath().lastSegment().equalsIgnoreCase(vdbName)) {
                    vdbResource = vdb;
                    break;
                }
            }
        }
        return vdbResource;
    }

    /**
     * Get the Preview Target Model, if the properties are defined and model can be found. The preview model must be located in
     * the defined 'views' folder
     * 
     * @param properties the Designer properties
     * @return the IFile, null if not defined or found
     */
    public IFile getPreviewTargetModel() {
        String targetModelName = getPreviewTargetModelName();
        String viewsFolder = getViewsFolderName();

        IFile targetPreviewModel = null;

        // Get the target Project (must be open)
        IProject project = getProject();
        if (project != null) {
            // Construct path to target model
            IPath targetModelPath = new Path("").makeAbsolute(); //$NON-NLS-1$
            if (viewsFolder != null && targetModelName != null && !viewsFolder.isEmpty() && !targetModelName.isEmpty()) {
                targetModelPath = targetModelPath.append(viewsFolder).append(targetModelName);
                targetPreviewModel = project.getFile(targetModelPath);
            }
        }
        return targetPreviewModel;
    }

    /**
     * Get the Preview Target Object, if the properties are defined and it can be found. The preview object model must be located
     * in the defined 'views' folder
     * 
     * @param properties the Designer properties
     * @return the EObject, null if not defined or found
     */
    public EObject getPreviewTargetObject() {
        EObject targetEObj = null;

        IFile targetModel = getPreviewTargetModel();
        if (targetModel != null) {
            String targetObjName = getPreviewTargetObjectName();
            // Locate the Target Preview Object in the Preview Model
            if (targetObjName != null && !targetObjName.isEmpty()) {
                ModelEditor editor = ModelerCore.getModelEditor();
                ModelResource resource = ModelUtilities.getModelResourceForIFile(targetModel, true);
                if (resource != null) {
                    try {
                        List<EObject> eObjects = resource.getEObjects();
                        for (EObject eObj : eObjects) {
                            // If Target Preview Object is found, update the UI
                            if (targetObjName.equals(editor.getName(eObj))) {
                                targetEObj = eObj;
                                break;
                            }
                        }
                    } catch (ModelWorkspaceException ex) {
                        UiPlugin.getDefault().getPluginUtil().log(ex);
                    }
                }
            }
        }

        return targetEObj;
    }
    
    /**
     * Get the Last Source Model Object, if the properties are defined and it can be found.
     * 
     * @param properties the Designer properties
     * @return the EObject, null if not defined or found
     */
    public EObject getLastSourceModelObject() {
        EObject targetEObj = null;

        IFile sourceModel = getSourceModel();
        if (sourceModel != null) {
            String targetObjName = getLastSourceModelObjectName();
            // Locate the Target Preview Object in the Preview Model
            if (targetObjName != null && !targetObjName.isEmpty()) {
                ModelEditor editor = ModelerCore.getModelEditor();
                ModelResource resource = ModelUtilities.getModelResourceForIFile(sourceModel, true);
                if (resource != null) {
                    try {
                        List<EObject> eObjects = resource.getEObjects();
                        for (EObject eObj : eObjects) {
                            // If Target Preview Object is found, update the UI
                            if (targetObjName.equals(editor.getName(eObj))) {
                                targetEObj = eObj;
                                break;
                            }
                        }
                    } catch (ModelWorkspaceException ex) {
                        UiPlugin.getDefault().getPluginUtil().log(ex);
                    }
                }
            }
        }

        return targetEObj;
    }
    
    /**
     * Get the Last View Model Object, if the properties are defined and it can be found.
     * 
     * @param properties the Designer properties
     * @return the EObject, null if not defined or found
     */
    public EObject getLastViewModelObject() {
        EObject targetEObj = null;

        IFile targetModel = getViewModel();
        if (targetModel != null) {
            String targetObjName = getLastViewModelObjectName();
            // Locate the Target Preview Object in the Preview Model
            if (targetObjName != null && !targetObjName.isEmpty()) {
                ModelEditor editor = ModelerCore.getModelEditor();
                ModelResource resource = ModelUtilities.getModelResourceForIFile(targetModel, true);
                if (resource != null) {
                    try {
                        List<EObject> eObjects = resource.getEObjects();
                        for (EObject eObj : eObjects) {
                            // If Target Preview Object is found, update the UI
                            if (targetObjName.equals(editor.getName(eObj))) {
                                targetEObj = eObj;
                                break;
                            }
                        }
                    } catch (ModelWorkspaceException ex) {
                        UiPlugin.getDefault().getPluginUtil().log(ex);
                    }
                }
            }
        }

        return targetEObj;
    }

    /**
     * Determines if the project name value is in the supplied properties
     * 
     * @param properties the Designer properties
     * @return boolean, true if project name value exists in properties
     */
    public boolean isProjectNameSet( ) {
    	return getProjectName() != null;
    }
    
    /**
     * Determines if the new project is different than project name stored in properties
     * 
     * @param newProject the target IProject
     * @param properties the Designer properties
     * @return boolean, true if project names are different
     */
    public boolean isProjectDifferent( IProject newProject) {
    	if( newProject == null && getProjectName() != null ) {
    		return true;
    	}
    	
    	if( newProject != null && getProjectName() == null ) {
    		return true;
    	}

    	return !(newProject.getName().equals(getProjectName()));
    }
    
    /**
     * Determines if the new source model file is different than source name stored in properties
     * 
     * @param newFile the IFile
     * @param properties the Designer properties
     * @return boolean, true if model names are different
     */
    public boolean isSourceModelDifferent( IFile newFile) {
    	if( newFile == null && getSourceModelName() != null ) {
    		return true;
    	}
    	
    	if( newFile != null && getSourceModelName() == null ) {
    		return true;
    	}

    	return !(newFile.getName().equals(getSourceModelName()));
    }
    
    /**
     * Determines if the new view model file is different than view name stored in properties
     * 
     * @param newFile the IFile
     * @param properties the Designer properties
     * @return boolean, true if model names are different
     */
    public boolean isViewModelDifferent( IFile newFile) {
    	if( newFile == null && getViewModelName() != null ) {
    		return true;
    	}
    	
    	if( newFile != null && getViewModelName() == null ) {
    		return true;
    	}

    	return !(newFile.getName().equals(getViewModelName()));
    }
    
    /**
     * Determines if the new vdb file is different than vdb name stored in properties
     * 
     * @param newVdb the IFile
     * @param properties the Designer properties
     * @return boolean, true if vdb names are different
     */
    public boolean isVdbDifferent( IFile newVdb) {
    	if( newVdb == null && getVdbName() != null ) {
    		return true;
    	}
    	
    	if( newVdb != null && getVdbName() == null ) {
    		return true;
    	}

    	return !(newVdb.getName().equals(getVdbName()));
    }
}

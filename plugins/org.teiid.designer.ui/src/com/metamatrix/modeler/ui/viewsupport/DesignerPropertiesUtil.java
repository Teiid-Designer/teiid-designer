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

/**
 * Contains static helper methods for working with IPropertiesContext properties
 */
public class DesignerPropertiesUtil {

    private static final DesignerPropertiesUtil INSTANCE = new DesignerPropertiesUtil();

    /**
     * Get the DesignerPropertiesUtil instance for this VM.
     * 
     * @return the singleton instance for this VM; never null
     */
    public static DesignerPropertiesUtil getInstance() {
        return INSTANCE;
    }

    public static String getProjectName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_PROJECT_NAME);
    }

    public static String getVdbName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_VDB_NAME);
    }
    
    public static String getVdbJndiName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_VDB_DATA_SOURCE_JNDI_NAME);
    }

    public static String getSourceModelName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME);
    }

    public static String getViewModelName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME);
    }

    public static String getConnectionProfileName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID);
    }

    public static String getSourcesFolderName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_SOURCES_FOLDER);
    }

    public static String getViewsFolderName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_VIEWS_FOLDER);
    }

    public static String getSchemaFolderName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_SCHEMA_FOLDER);
    }

    public static String getWebServiceFolderName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_WS_FOLDER);
    }

    public static String getPreviewTargetObjectName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_PREVIEW_TARGET_OBJECT);
    }

    public static String getPreviewTargetModelName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_PREVIEW_TARGET_MODEL);
    }
    
    public static String getLastSourceModelObjectName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_OBJECT_NAME);
    }
    public static String getLastViewModelObjectName( Properties properties ) {
        return properties.getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_OBJECT_NAME);
    }
    
    public static boolean isImportXmlRemote( Properties properties ) {
    	String value = properties.getProperty(IPropertiesContext.KEY_IMPORT_XML_TYPE);
    	if( value != null && value.equalsIgnoreCase(IPropertiesContext.IMPORT_XML_REMOTE)) {
    		return true;
    	}
    	
    	return false;
    }

    public static void setProjectName( Properties properties,
                                       String projectName ) {
        properties.put(IPropertiesContext.KEY_PROJECT_NAME, projectName);
    }

    public static void setVdbName( Properties properties,
                                   String vdbName ) {
        properties.put(IPropertiesContext.KEY_LAST_VDB_NAME, vdbName);
    }
    
    public static void setVdbJndiName( Properties properties, String vdbJndiName ) {
		properties.put(IPropertiesContext.KEY_VDB_DATA_SOURCE_JNDI_NAME, vdbJndiName);
	}

    public static void setSourceModelName( Properties properties,
                                           String sourceModelName ) {
        properties.put(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME, sourceModelName);
    }

    public static void setViewModelName( Properties properties,
                                         String viewModelName ) {
        properties.put(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME, viewModelName);
    }

    public static void setConnectionProfileName( Properties properties,
                                                 String connProfileName ) {
        properties.put(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID, connProfileName);
    }

    public static void setSourcesFolderName( Properties properties,
                                             String sourcesFolderName ) {
        properties.put(IPropertiesContext.KEY_SOURCES_FOLDER, sourcesFolderName);
    }

    public static void setViewsFolderName( Properties properties,
                                           String sourcesFolderName ) {
        properties.put(IPropertiesContext.KEY_VIEWS_FOLDER, sourcesFolderName);
    }

    public static void setSchemaFolderName( Properties properties,
                                            String schemaFolderName ) {
        properties.put(IPropertiesContext.KEY_SCHEMA_FOLDER, schemaFolderName);
    }

    public static void setWebServiceFolderName( Properties properties,
                                                String webServiceFolderName ) {
        properties.put(IPropertiesContext.KEY_WS_FOLDER, webServiceFolderName);
    }

    public static void setPreviewTargetObjectName( Properties properties,
                                                   String previewTargetObjectName ) {
        properties.put(IPropertiesContext.KEY_PREVIEW_TARGET_OBJECT, previewTargetObjectName);
    }

    public static void setPreviewTargetModelName( Properties properties,
                                                  String previewTargetModelName ) {
        properties.put(IPropertiesContext.KEY_PREVIEW_TARGET_MODEL, previewTargetModelName);
    }
    
    public static void setLastViewModelObjectName( Properties properties,
    											   String name ) {
    	properties.put(IPropertiesContext.KEY_LAST_VIEW_MODEL_OBJECT_NAME, name);
	}
    
    public static void setLastSourceModelObjectName( Properties properties,
			   									   String name ) {
    	properties.put(IPropertiesContext.KEY_LAST_SOURCE_MODEL_OBJECT_NAME, name);
	}


    /**
     * Get the Sources Folder, if the properties are defined
     * 
     * @param properties the Designer properties
     * @return the Sources Folder Container, null if not defined
     */
    public static IContainer getSourcesFolder( Properties properties ) {
        IContainer folder = null;
        // check for project property and if sources folder property exists
        String projectName = properties.getProperty(IPropertiesContext.KEY_PROJECT_NAME);
        if (projectName != null && !projectName.isEmpty()) {
            String folderName = projectName;
            String sourcesFolder = properties.getProperty(IPropertiesContext.KEY_SOURCES_FOLDER);
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
    public static IContainer getViewsFolder( Properties properties ) {
        IContainer folder = null;
        // check for project property and if sources folder property exists
        String projectName = properties.getProperty(IPropertiesContext.KEY_PROJECT_NAME);
        if (projectName != null && !projectName.isEmpty()) {
            String folderName = projectName;
            String viewsFolder = properties.getProperty(IPropertiesContext.KEY_VIEWS_FOLDER);
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
    public static IProject getProject( Properties properties ) {
        IProject project = null;
        String projectName = properties.getProperty(IPropertiesContext.KEY_PROJECT_NAME);
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
    public static IFile getViewModel( Properties properties ) {
        IFile viewModel = null;
        String modelName = properties.getProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME);
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
    public static IFile getSourceModel( Properties properties ) {
        IFile sourceModel = null;
        String modelName = properties.getProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME);
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
    public static IResource getVDB( Properties properties ) {
        IResource vdbResource = null;
        // check for vdb name property
        String vdbName = properties.getProperty(IPropertiesContext.KEY_LAST_VDB_NAME);
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
    public static IFile getPreviewTargetModel( Properties properties ) {
        String targetModelName = DesignerPropertiesUtil.getPreviewTargetModelName(properties);
        String viewsFolder = DesignerPropertiesUtil.getViewsFolderName(properties);

        IFile targetPreviewModel = null;

        // Get the target Project (must be open)
        IProject project = DesignerPropertiesUtil.getProject(properties);
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
    public static EObject getPreviewTargetObject( Properties properties ) {
        EObject targetEObj = null;

        IFile targetModel = getPreviewTargetModel(properties);
        if (targetModel != null) {
            String targetObjName = DesignerPropertiesUtil.getPreviewTargetObjectName(properties);
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
    public static EObject getLastSourceModelObject( Properties properties ) {
        EObject targetEObj = null;

        IFile sourceModel = getSourceModel(properties);
        if (sourceModel != null) {
            String targetObjName = DesignerPropertiesUtil.getLastSourceModelObjectName(properties);
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
    public static EObject getLastViewModelObject( Properties properties ) {
        EObject targetEObj = null;

        IFile targetModel = getViewModel(properties);
        if (targetModel != null) {
            String targetObjName = DesignerPropertiesUtil.getLastViewModelObjectName(properties);
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
    public static boolean isProjectNameSet( Properties properties ) {
    	return DesignerPropertiesUtil.getProjectName(properties) != null;
    }
    
    /**
     * Determines if the new project is different than project name stored in properties
     * 
     * @param newProject the target IProject
     * @param properties the Designer properties
     * @return boolean, true if project names are different
     */
    public static boolean isProjectDifferent( IProject newProject, Properties properties ) {
    	if( newProject == null && DesignerPropertiesUtil.getProjectName(properties) != null ) {
    		return true;
    	}
    	
    	if( newProject != null && DesignerPropertiesUtil.getProjectName(properties) == null ) {
    		return true;
    	}

    	return !(newProject.getName().equals(DesignerPropertiesUtil.getProjectName(properties)));
    }
}

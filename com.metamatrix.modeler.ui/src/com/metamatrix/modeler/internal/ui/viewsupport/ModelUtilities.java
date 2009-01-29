/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.ResourceNameUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.OpenEditorMap;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectAdapterFactoryContentProvider;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ModelUtilities is a collection of static methods that isolate our connection to ModelerCore and generic model/metamodel
 * functionality.
 */
public abstract class ModelUtilities implements UiConstants, InternalUiConstants.Debug.Metrics {

    private static Container workspaceContainer;
    private static AdapterFactoryContentProvider emfContentProvider;
    private static ModelObjectAdapterFactoryContentProvider modelContentProvider;
    private static AdapterFactoryLabelProvider emfLabelProvider;
    private static AdapterFactoryItemDelegator emfItemDelegator;
    private static ContainerNotificationManager notificationMgr;
    private static ModelObjectPropertySourceProvider propertySourceProvider;
    private static ILabelProvider labelProvider;
    private static ModelFileCache modelFileCache = new ModelFileCache();

    public static final String MODEL_FILE_EXTENSION = ResourceNameUtil.XMI_FILE_EXTENSION;
    public static final String VDB_FILE_EXTENSION = ResourceNameUtil.VDB_FILE_EXTENSION;
    public static final String XSD_FILE_EXTENSION = ResourceNameUtil.XSD_FILE_EXTENSION;

    public static final String DOT_MODEL_FILE_EXTENSION = ResourceNameUtil.DOT_XMI_FILE_EXTENSION;
    public static final String DOT_VDB_FILE_EXTENSION = ResourceNameUtil.DOT_VDB_FILE_EXTENSION;
    public static final String DOT_XSD_FILE_EXTENSION = ResourceNameUtil.DOT_XSD_FILE_EXTENSION;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelUtilities.class);

    private static final String FILE_DOES_NOT_EXIST_TITLE = getString("fileDoesNotExist.title"); //$NON-NLS-1$
    private static final String FILE_DOES_NOT_EXIST_MSG_KEY = "fileDoesNotExist.message"; //$NON-NLS-1$
    private static final String CLOSED_PROJECT_TITLE = getString("projectClosed.title"); //$NON-NLS-1$
    private static final String CLOSED_PROJECT_MSG_KEY = "projectClosed.message"; //$NON-NLS-1$
    private static final String MODEL_CHANGES_PENDING_TITLE = getString("modelChangesPending.title"); //$NON-NLS-1$
    private static final String MODEL_CHANGES_PENDING_MSG_KEY = "modelChangesPending.message"; //$NON-NLS-1$
    private static final String MODEL_ERRORS_TITLE = getString("modelHasErrors.title"); //$NON-NLS-1$
    private static final String MODEL_ERRORS_MSG_KEY = "modelHasErrors.message"; //$NON-NLS-1$
    private static final String MODEL_NOT_VALIDATED_TITLE = getString("modelNotValidated.title"); //$NON-NLS-1$
    private static final String MODEL_NOT_VALIDATED_MSG_KEY = "modelNotValidated.message"; //$NON-NLS-1$
    private static final String MODEL_RESOURCE_NOT_FOUND_MSG_KEY = "modelResourceNotFound.message"; //$NON-NLS-1$
    private static final String MODEL_IMPORTED_NOT_FOUND_TITLE = getString("modelHasMissingImports.title"); //$NON-NLS-1$
    private static final String MODEL_IMPORTED_NOT_FOUND_MSG_KEY = "modelHasMissingImports.message"; //$NON-NLS-1$
    private static final String MODEL_IMPORTED_PROBLEM_MSG_KEY = "importedError"; //$NON-NLS-1$

    /**
     * @since 4.2
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.2
     */
    private static String getString( final String id,
                                     String arg ) {
        return Util.getString(I18N_PREFIX + id, arg);
    }

    /**
     * Get the Modeler's Content Provider for navigating the workspace and diving into models
     * 
     * @return
     */
    public static ITreeContentProvider getModelContentProvider() {
        if (modelContentProvider == null) {

            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            modelContentProvider = new ModelObjectAdapterFactoryContentProvider(factory);

        }
        return modelContentProvider;
    }

    /**
     * Get EMF's Content Provider for navigating EObjects inside models
     * 
     * @return
     */
    public static AdapterFactoryContentProvider getEmfAdapterFactoryContentProvider() {
        if (emfContentProvider == null) {

            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            emfContentProvider = new AdapterFactoryContentProvider(factory);

        }
        return emfContentProvider;
    }

    /**
     * Get the EMF ItemDelegator for working with EObject properties
     * 
     * @since 4.0
     */
    public static AdapterFactoryItemDelegator getEMFItemDelegator() {
        if (emfItemDelegator == null) {

            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            emfItemDelegator = new AdapterFactoryItemDelegator(factory);

        }
        return emfItemDelegator;
    }

    public static AdapterFactoryLabelProvider getAdapterFactoryLabelProvider() {
        if (emfLabelProvider == null) {

            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            emfLabelProvider = new AdapterFactoryLabelProvider(factory);

        }
        return emfLabelProvider;
    }

    /**
     * Get the EMF LabelProvider for decorating EObjects
     * 
     * @since 4.0
     */
    public static ILabelProvider getEMFLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new ModelObjectLabelProvider();
        }
        return labelProvider;
    }

    /**
     * Get the ModelObjectLabelProvider LabelProvider for decorating EObjects
     * 
     * @since 4.0
     */
    public static ModelObjectLabelProvider getModelObjectLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new ModelObjectLabelProvider();
        }
        return (ModelObjectLabelProvider)labelProvider;
    }

    /**
     * @since 4.0
     */
    public static IPropertySourceProvider getEmfPropertySourceProvider() {
        if (modelContentProvider == null) {

            AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            modelContentProvider = new ModelObjectAdapterFactoryContentProvider(factory);

        }
        return modelContentProvider;
    }

    /**
     * Temporary implementation to determine if the specified resource is a model file.
     * 
     * @param resource
     * @return
     */
    public static boolean isModelFile( IResource resource ) {
        // return ModelUtil.isModelFile(resource);
        return modelFileCache.isModelFile(resource);
    }

    /**
     * Indicates if the specified resource is related to modeling (models, VDB, WSDL, etc.).
     * 
     * @param theResource the resource being checked
     * @return <code>true</code> if modeling related resource; <code>false</code> otherwise.
     * @throws com.metamatrix.core.util.AssertionError if resource is <code>null</code>
     * @since 5.0.1
     */
    public static boolean isModelingRelatedFile( IResource theResource ) {
        Assertion.isNotNull(theResource);

        return (theResource instanceof IFile)
               && (ModelUtilities.isModelFile(theResource) || ModelUtilities.isVdbFile(theResource) || ModelUtilities.isWsdlFile(theResource));
    }

    /**
     * Determines if the specified resource is a vdb file in a valid Modeler Project
     * 
     * @param resource
     * @return
     */
    public static boolean isVdbFile( IResource resource ) {
        boolean result = false;

        // check if the extension = ".vdb"
        if (resource instanceof IFile) {
            String ext = ((IFile)resource).getFileExtension();
            if (ext != null && ext.equals(VDB_FILE_EXTENSION)) result = true;
        }
        return result;
    }

    /**
     * Determines if the specified resource is a WSDL file.
     * 
     * @param theResource the resource being checked
     * @return <code>true</code> if a WSDL; <code>false</code> otherwise.
     */
    public static boolean isWsdlFile( IResource theResource ) {
        boolean result = false;

        if (theResource instanceof IFile) {
            String ext = ((IFile)theResource).getFileExtension();

            if ((ext != null) && ext.equals(ResourceNameUtil.WSDL_FILE_EXTENSION)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Helper method that determines that the given resource is a IFile and exists in a modeling project.
     */
    public static boolean isModelProjectResource( final IResource resource ) {
        if (resource != null) {
            IProject proj = resource.getProject();
            if (proj != null && ModelerCore.hasModelNature(proj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a ModelResource for a model file.
     * 
     * @param resourace
     * @param forceOpen true if the ModelResource should open in responce to this call, false if it is okay to lazily open the
     *        resource.
     * @return the <code>ModelResource</code>; null only if the resource is not known
     * @throws ModelWorkspaceException
     */
    public static ModelResource getModelResource( Resource resource,
                                                  boolean forceOpen ) {
        if (resource == null) {
            return null;
        }
        return ModelerCore.getModelEditor().findModelResource(resource);
    }

    /**
     * Get a ModelResource for a model file.
     * 
     * @param modelFile
     * @param forceOpen true if the ModelResource should open in responce to this call, false if it is okay to lazily open the
     *        resource.
     * @return the <code>ModelResource</code>; null only if the resource is not known
     * @throws ModelWorkspaceException
     */
    public static ModelResource getModelResource( IFile modelFile,
                                                  boolean forceOpen ) throws ModelWorkspaceException {
        if (modelFile == null) {
            return null;
        }
        return ModelerCore.getModelEditor().findModelResource(modelFile);
    }

    /**
     * Get a ModelResource for a model file.
     * 
     * @param modelFile
     * @param forceOpen true if the ModelResource should open in responce to this call, false if it is okay to lazily open the
     *        resource.
     * @return the <code>ModelResource</code>; null only if the resource is not known
     */
    public static ModelResource getModelResourceForIFile( IFile modelFile,
                                                          boolean forceOpen ) {
        if (modelFile == null) {
            return null;
        }

        ModelResource mr = null;

        try {
            mr = getModelResource(modelFile, forceOpen);
        } catch (ModelWorkspaceException err1) {
            String message = UiConstants.Util.getString("ModelUtilities.errorFindingModelResource", modelFile);//$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err1, message);
        }

        return mr;
    }

    /**
     * Get a <code>ModelResource</code> for a specified <code>EObject</code>
     * 
     * @param an <code>EObject</code> contained within a <code>ModelResource</code>
     * @return the <code>ModelResource</code>; null only if the resource is not known
     */
    public static ModelResource getModelResourceForModelObject( EObject modelObject ) {
        if (modelObject == null) {
            return null;
        }

        if (modelObject instanceof DiagramProxy) {
            ModelResource modelResource = ((DiagramProxy)modelObject).getModelResource();
            if (modelResource != null) {
                return modelResource;
            }

            return ModelerCore.getModelEditor().findModelResource(((DiagramProxy)modelObject).getTarget());
        }
        return ModelerCore.getModelEditor().findModelResource(modelObject);
    }

    /**
     * Get a <code>ModelResource</code> for a specified input object. Object may be a <code>ModelResource</code>, an
     * <code>EObject</code>, an <code>IFile</code> or a <code>Resource</code>
     * 
     * @param an <code>EObject</code> contained within a <code>ModelResource</code>
     * @return the <code>ModelResource</code>; null only if the resource is not known
     * @since 5.0.2
     */
    public static ModelResource getModelResource( Object object ) {
        if (object == null) {
            return null;
        }

        if (object instanceof ModelResource) {
            return (ModelResource)object;
        }

        if (object instanceof IFile) {
            return getModelResourceForIFile((IFile)object, true);
        }

        if (object instanceof EObject) {
            return getModelResourceForModelObject((EObject)object);
        }

        if (object instanceof Resource) {
            ModelResource mr = null;

            mr = getModelResource((Resource)object, true);

            return mr;
        }

        return null;
    }

    /**
     * Obtain the WorkspaceContainer for this application.
     * 
     * @return
     */
    public static Container getWorkspaceContainer() throws CoreException {
        if (workspaceContainer == null) {
            workspaceContainer = ModelerCore.getModelContainer();
        }
        return workspaceContainer;
    }

    /**
     * Indicates if a model exists in the workspace with the specified UUID.
     * 
     * @param theUuid the UUID being checked
     * @return <code>true</code> if a model exists; <code>false</code> otherwise.
     * @throws CoreException if problem getting project resources
     * @throws ModelWorkspaceException if problem getting ModelResource or UUID of a model
     */
    public static boolean isModelInWorkspace( String theUuid ) throws CoreException, ModelWorkspaceException {
        if (theUuid == null || theUuid.length() == 0) {
            return false;
        }
        boolean result = false;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject[] projects = workspace.getRoot().getProjects();

        if ((projects != null) && (projects.length > 0)) {
            PROJECT_LOOP: for (int i = 0; i < projects.length; i++) {
                IResource[] resources = projects[i].members();

                if ((resources != null) && (resources.length > 0)) {
                    for (int j = 0; j < resources.length; j++) {
                        if (isModelFile(resources[j])) {
                            ModelResource modelResource = getModelResource((IFile)resources[j], false);
                            String uuid = modelResource.getUuid();

                            if ((uuid != null) && uuid.equals(theUuid)) {
                                result = true;
                                break PROJECT_LOOP;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Add a NotifyChangedListener NOTE: This the <code>INotifyChangedListener</code> is a non-UI listener that simplifies wiring
     * of components to the EMF notification framework. !!!!! BE SURE !!!!! .... to remove your UI component as a listener also or
     * it will become a memory leak.... heap hog, or whatever you want to call it.
     */
    public static void addNotifyChangedListener( INotifyChangedListener listener ) {
        Container container = null;

        try {
            container = getWorkspaceContainer();
        } catch (CoreException e) {
            e.printStackTrace(System.err);
        }

        if (container != null) {
            if (notificationMgr == null) {
                notificationMgr = new ContainerNotificationManager();
                container.getChangeNotifier().addListener(notificationMgr);
            }

            notificationMgr.addNotifyChangedListener(listener);
        }
    }

    /**
     * Remove a NotifyChangedListener NOTE: This the <code>INotifyChangedListener</code> is a non-UI listener that simplifies
     * wiring of components to the EMF notification framework. !!!!! BE SURE !!!!! .... to remove your UI component as a listener
     * also or it will become a memory leak.... heap hog, or whatever you want to call it.
     */
    public static void removeNotifyChangedListener( INotifyChangedListener listener ) {
        Container container = null;

        try {
            container = getWorkspaceContainer();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        if ((container != null) && (notificationMgr != null)) {
            notificationMgr.removeNotifyChangedListener(listener);
        }
    }

    /**
     * return the Designer's contribution to a property source
     * 
     * @return
     * @since 5.0
     */
    public static ModelObjectPropertySourceProvider getPropertySourceProvider() {
        if (propertySourceProvider == null) {
            propertySourceProvider = new ModelObjectPropertySourceProvider();
        }
        return propertySourceProvider;
    }

    /**
     * Determine if the specified ModelResource supports the Description property.
     * 
     * @param modelResource
     * @return
     */
    public static boolean supportsModelDescription( IResource resource ) {
        boolean result = false;
        if (isModelFile(resource)) {
            result = true;
            if (ModelUtil.isXsdFile(resource)) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Set the description on the specified ModelResource.
     * 
     * @param model
     * @param description
     */
    public static void setModelDescription( ModelResource modelResource,
                                            String description ) {
        if (!isReadOnly(modelResource)) {
            try {

                ModelAnnotation annotation = modelResource.getModelAnnotation();
                if (annotation != null) {
                    annotation.setDescription(description);
                } else {
                    UiConstants.Util.log(IStatus.ERROR,
                                         getString("ModelUtilities.nullModelAnnotation", modelResource.getPath().toString())); //$NON-NLS-1$
                }

            } catch (ModelWorkspaceException ex) {
                String message = getString("ModelUtilities.setModelDescriptionError", modelResource.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, ex, message);
            }
        }
    }

    /**
     * Get the description from the specified ModelResource.
     * 
     * @param model
     * @return the description for this file. will not return null.
     */
    public static String getModelDescription( ModelResource model ) {
        String result = PluginConstants.EMPTY_STRING;
        try {
            result = model.getDescription();
        } catch (ModelWorkspaceException ex) {
            String message = getString("ModelUtilities.getModelDescriptionError", model.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        }

        if (result == null) {
            result = PluginConstants.EMPTY_STRING;
        }
        return result;
    }

    /**
     * Return the virtual model state of the specified model resource.
     * 
     * @param modelResource
     * @return true if model resource is a virtual model.
     */
    public static boolean isVirtual( ModelResource modelResource ) {
        boolean virtualState = false;

        if (modelResource != null) {
            try {
                virtualState = modelResource.getModelType().getValue() == ModelType.VIRTUAL;
            } catch (ModelWorkspaceException e) {
                String message = "[ModelUtilities.isVirtual()] ERROR: exception accessing ModelType"; //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        return virtualState;
    }

    /**
     * Return the physical model state of the specified model resource.
     * 
     * @param modelResource
     * @return true if model resource is a physical model.
     */
    public static boolean isPhysical( ModelResource modelResource ) {
        boolean physicalState = false;

        if (modelResource != null) {
            try {
                physicalState = modelResource.getModelType().getValue() == ModelType.PHYSICAL;
            } catch (ModelWorkspaceException e) {
                String message = "[ModelUtilities.isPhysical()] ERROR: exception accessing ModelType"; //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        return physicalState;
    }

    public static boolean isLogical( ModelResource modelResource ) {
        boolean result = false;
        if (modelResource != null) {
            try {
                result = modelResource.getModelType().getValue() == ModelType.LOGICAL;
            } catch (ModelWorkspaceException e) {
                String message = "[ModelUtilities.isVirtual()] ERROR: exception accessing ModelType"; //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        return result;
    }

    public static boolean isReadOnly( ModelResource modelResource ) {
        // consider it read-only until proven otherwise
        boolean result = true;
        if (modelResource != null) {
            // the modelResource must be open in an editor or else it is read-only
            if (OpenEditorMap.getInstance().isEditorOpen(modelResource)) {
                // then check the read-only status on the file
                result = modelResource.isReadOnly();
            }
        }
        return result;
    }

    /**
     * Indicates if the specified resource is a real filesystem resource. Eclipse saves workbench state when it closes. Each part
     * is responsible for saving it's own state. One example is the <code>ModelExplorerResourceNavigator</code> saves the expanded
     * and selected state of it's tree. If a resource is deleted outside of Eclipse, and Eclipse is restarted it will create
     * <code>IResource</code>s for those deleted resources so that it can restore it's state. The corresponding
     * <code>IResource</code> still indicates that it exists and is NOT a phantom. This method uses <code>java.io.File</code> to
     * check existence.
     * 
     * @param theResource the resource whose existence is being requested
     * @return <code>true</code> if resource exists on the filesystem; <code>false</code> otherwise.
     */
    public static boolean existsOnFilesystem( IResource theResource ) {
        boolean result = false;

        if (theResource != null && theResource.getLocation() != null) {
            result = new File(theResource.getLocation().toOSString()).exists();
        }

        return result;
    }

    public static boolean areModelResourcesSame( EObject eObj1,
                                                 EObject eObj2 ) {
        ModelResource mr1 = getModelResourceForModelObject(eObj1);
        ModelResource mr2 = getModelResourceForModelObject(eObj2);
        if (mr1 != null && mr2 != null && mr1.equals(mr2)) return true;

        return false;
    }

    public static boolean supportsDiagrams( final ModelResource modelResource ) {
        boolean result = false;

        if (modelResource != null) {
            MetamodelDescriptor md = null;

            try {
                md = modelResource.getPrimaryMetamodelDescriptor();
            } catch (ModelWorkspaceException e) {
                String message = "[ModelUtilities.supportsDiagrams()] ERROR: finding supports diagrams.  Resource = " + modelResource; //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
            if (md != null) result = md.supportsDiagrams();
        }
        return result;
    }

    /**
     * Method returns whether or not an object's model resource supports diagrams. This is determined finding a model resource for
     * an arbitrary object, getting it's primary metamodel descriptor and asking the simple question. A model resource may exist
     * for any <code>EObject</code>, <code>ModelResource</code>, <code>IFile</code> or <code>Resource</code>
     * 
     * @param input
     * @return <code>true</code> if model resource supports diagrams; <code>false</code> otherwise.
     */
    public static boolean supportsDiagrams( Object input ) {
        MetamodelDescriptor md = null;
        boolean result = false;

        if (input != null) {
            try {
                if (input instanceof EObject && ((EObject)input).eResource() != null) {
                    md = ModelerCore.getModelEditor().getMetamodelDescriptor((EObject)input);
                } else if (input instanceof ModelResource) {
                    md = (MetamodelDescriptor)input;
                } else if (input instanceof IFile) {
                    ModelResource mr = getModelResource((IFile)input, false);
                    if (mr != null) md = mr.getPrimaryMetamodelDescriptor();
                } else if (input instanceof Resource) {
                    ModelResource mr = getModelResource((Resource)input, false);
                    if (mr != null) md = mr.getPrimaryMetamodelDescriptor();
                }

                if (md != null) {
                    result = md.supportsDiagrams();
                }
            } catch (ModelWorkspaceException e) {
                String message = "[ModelUtilities.supportsDiagrams(Object)] ERROR: finding supports diagrams.  Object = " + input; //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        return result;
    }

    /**
     * Determine if the proposed model name is valid, and return an error message if it is not.
     * 
     * @param proposedName
     * @return null if the name is valid, or an error message if it is not.
     */
    public static String validateModelName( String proposedName,
                                            String fileExtension ) {
        boolean removedValidExtension = false;
        if (proposedName.endsWith(fileExtension)) {
            proposedName = proposedName.substring(0, proposedName.lastIndexOf(fileExtension));
            removedValidExtension = true;
        }

        if (proposedName.indexOf('.') != -1) {
            if (!removedValidExtension) {
                return Util.getString("ModelUtilities.illegalExtensionMessage", fileExtension); //$NON-NLS-1$
            }
        }

        // BML TODO: Add I18n PROPERTY For zeroLengthFileMessage
        if (proposedName.equals(PluginConstants.EMPTY_STRING)) {
            return Util.getString("ModelUtilities.zeroLengthFileMessage"); //$NON-NLS-1$
        }

        final ValidationResultImpl result = new ValidationResultImpl(proposedName);
        CoreValidationRulesUtil.validateStringNameChars(result, proposedName, null);
        if (result.hasProblems()) {
            return result.getProblems()[0].getMessage();
        }

        if (fileExtension != null) {
            String reservedError = null;
            if (fileExtension.equalsIgnoreCase(DOT_MODEL_FILE_EXTENSION) || fileExtension.equalsIgnoreCase(MODEL_FILE_EXTENSION)) {
                reservedError = modelNameReservedValidation(proposedName);

                if (reservedError != null) {
                    return reservedError;
                }
            } else if (fileExtension.equalsIgnoreCase(DOT_VDB_FILE_EXTENSION)
                       || fileExtension.equalsIgnoreCase(VDB_FILE_EXTENSION)) {
                reservedError = vdbNameReservedValidation(proposedName);

                if (reservedError != null) {
                    return reservedError;
                }
            } else if (fileExtension.equalsIgnoreCase(DOT_XSD_FILE_EXTENSION)
                       || fileExtension.equalsIgnoreCase(XSD_FILE_EXTENSION)) {
                reservedError = schemaNameReservedValidation(proposedName);

                if (reservedError != null) {
                    return reservedError;
                }
            }
        }

        return null;
    }

    /**
     * Determine if the proposed model name is reserved name or not, and return an error message if it IS.
     * 
     * @param proposedName
     * @return null if the name is valid, or an error message if it is not.
     */
    public static String modelNameReservedValidation( String proposedName ) {
        boolean invalid = ResourceNameUtil.isReservedModelName(proposedName);
        if (invalid) {
            return Util.getString("ModelUtilities.modelNameIsReservedError", proposedName); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Determine if the proposed schema model name is reserved name or not, and return an error message if it IS.
     * 
     * @param proposedName
     * @return null if the name is valid, or an error message if it is not.
     */
    public static String schemaNameReservedValidation( String proposedName ) {
        boolean invalid = ResourceNameUtil.isReservedSchemaName(proposedName);
        if (invalid) {
            return Util.getString("ModelUtilities.schemaNameIsReservedError", proposedName); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Determine if the proposed schema model name is reserved name or not, and return an error message if it IS.
     * 
     * @param proposedName
     * @return null if the name is valid, or an error message if it is not.
     */
    public static String vdbNameReservedValidation( String proposedName ) {
        boolean invalid = ResourceNameUtil.isReservedVdbName(proposedName);
        if (invalid) {
            return Util.getString("ModelUtilities.vdbNameIsReservedError", proposedName); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Method returns list of IResources defined by ModelResource import list. Only those resources that are open in the current
     * workspace will be returned.
     * 
     * @see allDependenciesOpenInWorkspace(ModelResource targetModelResource) to assess whether or not the returned list is
     *      complete or not.
     * @param targetIFile
     * @return
     * @since 4.2
     */
    public static Collection getResourcesUsedBy( IResource targetIFile ) {
        if (targetIFile == null) {
            return Collections.EMPTY_LIST;
        }

        List modelImports = null;
        Collection result = Collections.EMPTY_LIST;

        ModelResource targetModelResource = null;
        try {
            // Find Model Resource
            targetModelResource = getModelResource((IFile)targetIFile, false);
            if (targetModelResource != null) {
                modelImports = targetModelResource.getModelImports();
            }
        } catch (ModelWorkspaceException err) {
            String message = Util.getString("ModelUtilities.errorFindingImports", targetModelResource); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        if (modelImports != null && !modelImports.isEmpty()) {
            result = new ArrayList(modelImports.size());

            for (Iterator iter = modelImports.iterator(); iter.hasNext();) {
                Object importObject = iter.next();
                String modelPath = null;
                if (importObject instanceof ModelImport) {
                    modelPath = ((ModelImport)importObject).getPath();
                } else if (importObject instanceof ResourceImportRecord) {
                    modelPath = ((ResourceImportRecord)importObject).getImportedPath();
                }

                // If the URI is to the Federate Designer built-in datatypes resource or to one
                // of the Emf XMLSchema resources then continue since there is no
                // ModelReference to add.
                if (modelPath == null || WorkspaceResourceFinderUtil.isGlobalResource(modelPath)) {
                    continue;
                }

                // Verify that the model has an IResource
                IResource resource = WorkspaceResourceFinderUtil.findIResource(modelPath);

                if (resource != null && !resource.equals(targetIFile)) {
                    result.add(resource);
                }
            }
        }

        if (result.isEmpty()) return Collections.EMPTY_LIST;

        return result;
    }

    /**
     * Method returns list of ModelResources defined by ModelResource import list. Only those resources that are open in the
     * current workspace will be returned.
     * 
     * @see allDependenciesOpenInWorkspace(ModelResource targetModelResource) to assess whether or not the returned list is
     *      complete or not.
     * @param targetModelResource
     * @return
     * @since 4.2
     */
    public static Collection getResourcesUsedBy( ModelResource targetModelResource ) {
        if (targetModelResource == null) {
            return Collections.EMPTY_LIST;
        }

        List modelImports = null;
        Collection result = Collections.EMPTY_LIST;
        try {
            modelImports = targetModelResource.getModelImports();
        } catch (ModelWorkspaceException err) {
            String message = Util.getString("ModelUtilities.errorFindingImports", targetModelResource); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        if (modelImports != null && !modelImports.isEmpty()) {
            result = new ArrayList(modelImports.size());

            for (Iterator iter = modelImports.iterator(); iter.hasNext();) {
                Object importObject = iter.next();
                String modelPath = null;
                if (importObject instanceof ModelImport) {
                    modelPath = ((ModelImport)importObject).getPath();
                } else if (importObject instanceof ResourceImportRecord) {
                    modelPath = ((ResourceImportRecord)importObject).getImportedPath();
                }

                // If the URI is to the Federate Designer built-in datatypes resource or to one
                // of the Emf XMLSchema resources then continue since there is no
                // ModelReference to add.
                if (modelPath == null || WorkspaceResourceFinderUtil.isGlobalResource(modelPath)) {
                    continue;
                }

                // Verify that the model has an IResource
                IResource resource = WorkspaceResourceFinderUtil.findIResource(modelPath);
                ModelResource mr = null;

                if (resource != null) {
                    try {
                        mr = getModelResource((IFile)resource, false);
                    } catch (ModelWorkspaceException err1) {
                        String message = Util.getString("ModelUtilities.errorFindingModelResource", resource);//$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, err1, message);
                    }
                }
                if (mr != null && !mr.equals(targetModelResource)) result.add(mr);

            }
        }

        if (result.isEmpty()) return Collections.EMPTY_LIST;

        return result;
    }

    /**
     * Method returns list of ModelResources defined by ModelResource import list. Only those resources that are open in the
     * current workspace will be returned.
     * 
     * @see allDependenciesOpenInWorkspace(ModelResource targetModelResource) to assess whether or not the returned list is
     *      complete or not.
     * @param targetModelResource
     * @return
     * @since 4.2
     */
    public static boolean allDependenciesOpenInWorkspace( ModelResource targetModelResource ) {
        if (targetModelResource == null) {
            return true;
        }
        boolean result = true;

        List modelImports = null;

        try {
            modelImports = targetModelResource.getModelImports();
        } catch (ModelWorkspaceException err) {
            String message = "[ModelUtilities.allDependenciesOpenInWorkspace()] ERROR: finding model inports.  Resource = " + targetModelResource; //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        if (modelImports != null && !modelImports.isEmpty()) {

            for (Iterator iter = modelImports.iterator(); iter.hasNext();) {
                Object importObject = iter.next();
                String modelPath = null;
                if (importObject instanceof ModelImport) {
                    modelPath = ((ModelImport)importObject).getPath();
                } else if (importObject instanceof ResourceImportRecord) {
                    modelPath = ((ResourceImportRecord)importObject).getImportedPath();
                }

                // If the URI is to the Federate Designer built-in datatypes resource or to one
                // of the Emf XMLSchema resources then continue since there is no
                // ModelReference to add.
                if (modelPath == null || WorkspaceResourceFinderUtil.isGlobalResource(modelPath)) {
                    continue;
                }

                // Verify that the model has an IResource
                IResource resource = WorkspaceResourceFinderUtil.findIResource(modelPath);
                ModelResource mr = null;

                if (resource != null) {
                    try {
                        mr = getModelResource((IFile)resource, false);
                    } catch (ModelWorkspaceException err1) {
                        String message = "[ModelUtilities.allDependenciesOpenInWorkspace()] ERROR: finding model resource.  IFile = " + resource; //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, err1, message);
                    }
                    if (mr == null) result = false;
                } else {
                    result = false;
                }

                if (!result) break;
            }
        }

        return result;
    }

    /**
     * Method returns if one model resource is a dependent of another.
     * 
     * @param targetModelResource
     * @param dependentModelResource
     * @return true of dependent model resource, false if not.
     * @since 4.2
     */
    public static boolean isDependent( ModelResource targetModelResource,
                                       ModelResource dependentModelResource ) {
        if (targetModelResource == null) {
            return false;
        }

        if (getResourcesUsedBy(targetModelResource).contains(dependentModelResource)) return true;

        return false;
    }

    /**
     * Method returns list of ModelResources defined by ModelResource import list. Only those resources that are open in the
     * current workspace will be returned.
     * 
     * @see allDependenciesOpenInWorkspace(ModelResource targetModelResource) to assess whether or not the returned list is
     *      complete or not.
     * @param targetModelResource
     * @param dependentModelResource
     * @return true of dependent model resource, false if not.
     * @since 4.2
     */
    public static boolean isDependent( IResource targetResource,
                                       IResource dependentResource ) {
        if (targetResource == null) {
            return false;
        }

        if (getResourcesUsedBy(targetResource).contains(dependentResource)) return true;

        return false;
    }

    /**
     * Checks for the following conditions on all model files: 1) Must exist in the workspace 2) Must have been validated since
     * last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also, if no models are
     * stale, the method returns false.
     * 
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     */
    public static boolean verifyWorkspaceValidationState( Collection iFiles,
                                                          Object source,
                                                          String failString ) {
        boolean result = true;

        Collection dirtyModels = ModelEditorManager.getDirtyResources();
        // iterate through the models, as long as result remains TRUE
        for (Iterator iter = iFiles.iterator(); iter.hasNext() && result;) {
            final IFile file = (IFile)iter.next();
            // each file must exist in the workspace
            if (file == null) {
                // do nothing. Shouldn't get here.
            } else if (!file.exists()) {
                // Cannot find file in the workspace - cannot continue
                final String message = getString(FILE_DOES_NOT_EXIST_MSG_KEY, file.getFullPath().makeRelative().toString())
                                       + failString;
                MessageDialog.openError(null, FILE_DOES_NOT_EXIST_TITLE, message);
                result = false;
            } else {
                if (!file.getProject().isOpen()) {
                    // must be in an open project
                    final String message = getString(CLOSED_PROJECT_MSG_KEY, file.getFullPath().makeRelative().toString())
                                           + failString;
                    MessageDialog.openError(null, CLOSED_PROJECT_TITLE, message);
                    result = false;
                } else if (dirtyModels.contains(file)) {
                    // see if the user wants to save changes first
                    final String message = getString(MODEL_CHANGES_PENDING_MSG_KEY, file.getFullPath().makeRelative().toString())
                                           + failString;
                    MessageDialog.openError(null, MODEL_CHANGES_PENDING_TITLE, message);
                    result = false;
                    // MessageDialog dialog = new MessageDialog(null,
                    // MODEL_CHANGES_PENDING_TITLE,
                    // null, // accept the default window icon
                    // message,
                    // MessageDialog.QUESTION,
                    // new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                    // 0 );
                    //
                    // result = dialog.open() == MessageDialog.OK; //MessageDialog.openQuestion(null, MODEL_CHANGES_PENDING_TITLE,
                    // message);
                } else if (requiresValidation(file)) {
                    // the file must have been validated since last save
                    final String message = getString(MODEL_NOT_VALIDATED_MSG_KEY, file.getFullPath().makeRelative().toString())
                                           + failString;
                    MessageDialog.openError(null, MODEL_NOT_VALIDATED_TITLE, message);
                    result = false;
                } else if (hasErrors(file, source)) {
                    // the file must not have any errors
                    final String message = getString(MODEL_ERRORS_MSG_KEY, file.getFullPath().makeRelative().toString())
                                           + failString;
                    MessageDialog.openError(null, MODEL_ERRORS_TITLE, message);
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Checks for the following conditions on all model files: 1) Must exist in the workspace 2) Must have been validated since
     * last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also, if no models are
     * stale, the method returns false. This method will not check dependencies.
     * 
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     * @see #verifyWorkspaceValidationState(IFile, Object, String, boolean)
     */
    public static boolean verifyWorkspaceValidationState( IFile iFile,
                                                          Object source,
                                                          String failString ) {
        return verifyWorkspaceValidationState(iFile, source, failString, false);
    }

    /**
     * Checks for the following conditions on all model files: 1) Must exist in the workspace 2) Must have been validated since
     * last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also, if no models are
     * stale, the method returns false.
     * 
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     */
    public static boolean verifyWorkspaceValidationState( IFile iFile,
                                                          Object source,
                                                          String failString,
                                                          boolean checkDependencies ) {
        return verifyWorkspaceValidationState(iFile, source, failString, checkDependencies, null, new ArrayList());
    }

    /**
     * Checks for the following conditions on all model files: 1) Must exist in the workspace 2) Must have been validated since
     * last save 3) Must not have any validation errors 4) If dirty, notify user and allow chance to cancel Also, if no models are
     * stale, the method returns false.
     * 
     * @param instigator the name of the file or process causing the check for dependencies. Really only used when
     *        checkDependencies is true.
     * @param theProcessedFiles the list of files that already have been verified
     * @return true if the action may proceed, otherwise false.
     * @since 4.2
     */
    private static boolean verifyWorkspaceValidationState( IFile iFile,
                                                           Object source,
                                                           String failString,
                                                           boolean checkDependencies,
                                                           IFile instigator,
                                                           List theProcessedFiles ) {
        boolean result = true;

        // each file must exist in the workspace
        if (iFile == null) {
            // do nothing. Shouldn't get here.
            result = false;
        } else if (!iFile.exists()) {
            // Cannot find file in the workspace - cannot continue
            showVerifyErrorDialog(FILE_DOES_NOT_EXIST_TITLE, FILE_DOES_NOT_EXIST_MSG_KEY, iFile, failString, instigator);
            result = false;
        } else {
            Collection dirtyModels = ModelEditorManager.getDirtyResources();

            if (!iFile.getProject().isOpen()) {
                // must be in an open project
                showVerifyErrorDialog(CLOSED_PROJECT_TITLE, CLOSED_PROJECT_MSG_KEY, iFile, failString, instigator);
                result = false;
            } else if (dirtyModels.contains(iFile)) {
                // see if the user wants to save changes first
                showVerifyErrorDialog(MODEL_CHANGES_PENDING_TITLE, MODEL_CHANGES_PENDING_MSG_KEY, iFile, failString, instigator);
                result = false;
            } else if (requiresValidation(iFile)) {
                // the file must have been validated since last save
                showVerifyErrorDialog(MODEL_NOT_VALIDATED_TITLE, MODEL_NOT_VALIDATED_MSG_KEY, iFile, failString, instigator);
                result = false;
            } else if (hasErrors(iFile, source)) {
                // the file must not have any errors
                showVerifyErrorDialog(MODEL_ERRORS_TITLE, MODEL_ERRORS_MSG_KEY, iFile, failString, instigator);
                result = false;
            } else if (checkDependencies) { // Everything is good so far. Check dependencies?
                // only want to process files that haven't been processed before or there is a possibility
                // of an infinite loop when models import each other (ref Defect 19713)
                if (!theProcessedFiles.contains(iFile)) {
                    theProcessedFiles.add(iFile);
                    result = verifyDependencyWorkspaceValidationState(iFile, source, failString, instigator, theProcessedFiles);
                }
                // no prompting here... done in above method.
            } // endif -- "switch" block for different checks
        } // endif -- file is null

        return result;
    }

    /**
     * @param dialogTitleText
     * @param messageKey
     * @param iFile the file to check
     * @param failString
     * @param instigator the file that started the check. Used to detected dependencies.
     */
    private static void showVerifyErrorDialog( String dialogTitleText,
                                               String messageKey,
                                               IFile iFile,
                                               String failString,
                                               IFile instigator ) {
        String msgStr = getString(messageKey, iFile.getFullPath().makeRelative().toString());
        String fullMessage;

        if (instigator != null) {
            // there is a greater model that depends upon this one:
            fullMessage = getString(MODEL_IMPORTED_PROBLEM_MSG_KEY, instigator.getFullPath().makeRelative().toString()) + msgStr
                          + failString;
        } else {
            fullMessage = msgStr + failString;
        } // endif
        MessageDialog.openError(null, dialogTitleText, fullMessage);
    }

    /**
     * This method analyzes the dependencies of the specified IFile.
     * 
     * @param iFile
     * @param source
     * @param failString
     * @param theProcessedFiles the list of files that already have been verified
     * @return true if the children are in the workspace and are validated and have no errors.
     * @see #verifyWorkspaceValidationState(IFile, Object, String, boolean)
     */
    private static boolean verifyDependencyWorkspaceValidationState( IFile iFile,
                                                                     Object source,
                                                                     String failString,
                                                                     IFile instigator,
                                                                     List theProcessedFiles ) {
        // if this is the parent of everything, set the instigator for those below:
        if (instigator == null) {
            instigator = iFile;
        } else if (instigator.equals(iFile)) {
            // stop processing -- circular dependency
            return true;
        } // endif

        try {
            // first, check that all dependencies are in the workspace:
            boolean allDepsPresent = allDependenciesOpenInWorkspace(getModelResource(iFile, true));
            if (!allDepsPresent) {
                final String message = getString(MODEL_IMPORTED_NOT_FOUND_MSG_KEY,
                                                 instigator.getFullPath().makeRelative().toString())
                                       + failString;
                MessageDialog.openError(null, MODEL_IMPORTED_NOT_FOUND_TITLE, message);
                return false;
            } // endif -- deps not present in WS
        } catch (ModelWorkspaceException ex) {
            String message = Util.getString("ModelUtilities.errorFindingDependents", iFile); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        } // endtry

        // everything is in an open project in the workspace; check each for validity:
        Collection c = getResourcesUsedBy(iFile);
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            IFile depFile = (IFile)iter.next();
            if (!verifyWorkspaceValidationState(depFile, source, failString, true, instigator, theProcessedFiles)) { // always
                // check deps
                // a dependency below this one had a problem; short-cut out:
                return false;
            } // endif -- dependency verify problem
        } // endwhile -- all dependencies

        // got through everything OK, say so:
        return true;
    }

    /**
     * Method returns a collection of all resources in the workspace that are model files.
     * 
     * @return
     * @since 4.2
     */
    public static Collection getAllWorkspaceResources() {
        // Collect all IResources within all IProjects
        Collection allResources = new ArrayList();

        FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        if (ModelerCore.getModelWorkspace() != null && ModelerCore.getWorkspace().getRoot() != null) {
            IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
            for (int i = 0; i < projects.length; i++) {
                try {
                    projects[i].accept(visitor);
                } catch (CoreException e) {
                    // do nothing
                }
            }
        }

        IFile[] fileResources = visitor.getFileResources();
        for (int i = 0; i < fileResources.length; i++) {
            IFile fileResource = fileResources[i];
            if (fileResource != null) {
                IPath path = fileResource.getFullPath();
                // Do not process file names starting with '.' since these
                // are considered reserved for Eclipse specific files
                if (path.lastSegment().charAt(0) == '.') {
                    continue;
                }
                if (isModelFile(fileResource)) {
                    allResources.add(fileResource);
                }
            }
        }

        return allResources;
    }

    /**
     * Finds and returns an IResource based on a full model name with "xmi" extension
     * 
     * @param modelNameWithExtension
     * @return
     * @since 5.0
     */
    public static IResource findModelByName( final String modelNameWithExtension ) {

        Collection allWorkspaceModels = getAllWorkspaceResources();
        if (!allWorkspaceModels.isEmpty()) {
            IResource theIResource = null;
            for (Iterator iter = allWorkspaceModels.iterator(); iter.hasNext();) {
                theIResource = (IResource)iter.next();
                if (theIResource.getName().equalsIgnoreCase(modelNameWithExtension)) {
                    return theIResource;
                }
            }
        }

        return null;
    }

    /**
     * Convenience method to obtain a list of ModelResource instances which contain dependencies to the input model resource
     * 
     * @param resource
     * @return Collection of ModelResource's
     * @since 4.2
     */
    public static Collection getResourcesThatUse( ModelResource resource ) {
        Collection result = Collections.EMPTY_LIST;

        IResource theResource = resource.getResource();

        // Get dependants
        Collection dependants = WorkspaceResourceFinderUtil.getResourcesThatUse(theResource);
        Iterator it = dependants.iterator();
        IResource nextRes = null;
        ModelResource mo = null;
        try {
            while (it.hasNext()) {
                nextRes = (IResource)it.next();
                mo = ModelUtilities.getModelResource((IFile)nextRes, true);
                if (mo != null) {
                    if (result.isEmpty()) {
                        result = new ArrayList();
                    }
                    result.add(mo);
                }
            }
        } catch (ModelWorkspaceException err) {
            String message = getString(MODEL_RESOURCE_NOT_FOUND_MSG_KEY, nextRes == null ? "" : nextRes.getFullPath().toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        return result;
    }

    public static void getDependentPhysicalModelResources( ModelResource modelResource,
                                                           Collection resources ) throws ModelWorkspaceException {
        getDependentPhysicalModelResources(modelResource, resources, new ArrayList());
    }

    /**
     * @param modelResource the model whose dependent physical sources are being requested
     * @param resources the recursive resultant physical source model resources
     * @param modelsProcessed the list of models already processed
     * @throws ModelWorkspaceException if there is a problem working with the dependent models
     * @since 5.5.3
     */
    private static void getDependentPhysicalModelResources( ModelResource modelResource,
                                                            Collection<ModelResource> resources,
                                                            Collection<ModelResource> modelsProcessed )
        throws ModelWorkspaceException {
        if (!modelsProcessed.contains(modelResource)) {
            modelsProcessed.add(modelResource);
            Collection dependents = getDependentResources(modelResource);

            for (Iterator i = dependents.iterator(); i.hasNext();) {
                ModelResource model = (ModelResource)i.next();

                if (model.getModelType().getValue() == ModelType.PHYSICAL) {
                    if (!resources.contains(model)) {
                        resources.add(model);
                    }
                } else if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                    getDependentPhysicalModelResources(model, resources, modelsProcessed);
                }
            }
        }
    }

    /**
     * Convenience method to obtain a list of ModelResource instances which the input model resource depends upon
     * 
     * @param resource
     * @return Collection of ModelResource's
     * @since 4.2
     */
    public static Collection getDependentResources( ModelResource resource ) throws ModelWorkspaceException {
        Collection result = Collections.EMPTY_LIST;

        IResource theResource = resource.getResource();

        // Get the array of resources that this resource depends upon
        IResource[] dependents = WorkspaceResourceFinderUtil.getDependentResources(theResource);

        ModelResource mo = null;
        for (int i = 0; i != dependents.length; ++i) {
            mo = ModelUtilities.getModelResource((IFile)dependents[i], true);
            if (mo != null) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }
                result.add(mo);
            }
        }

        return result;
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a IFile requires validation. The model may be have been
     * saved with auto-build off.
     * 
     * @param targetModelResource
     * @return true if requires validation, false if not.
     * @since 4.2
     */
    public static boolean requiresValidation( IFile file ) {
        ModelResource mr = null;

        // Find Model Resource
        try {
            mr = ModelUtilities.getModelResource(file, false);
        } catch (ModelWorkspaceException err) {
            String message = getString(MODEL_RESOURCE_NOT_FOUND_MSG_KEY, file.toString());
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        if (mr != null) {
            return requiresValidation(mr);
        }

        // If we ever get here it's an error, so let's
        return false;
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a model resource requires validation. The model may be
     * have been saved with auto-build off.
     * 
     * @param targetModelResource
     * @return true if requires validation, false if not.
     * @since 4.2
     */
    public static boolean requiresValidation( ModelResource targetModelResource ) {
        if (targetModelResource == null) return false;

        // todo: (BML 12/14/04) This check is currently required because xsd files are always taked with a NOT_INDEXED during
        // the build process because they need to be unloaded and reloaded. Sucks, but that's the way it is.
        // There will be another defect defining that and pointing to this place to remove the next two lines!!
        if (ModelUtil.isXsdFile(targetModelResource.getResource())) return false;

        // sz - added the code to fix defect 15948.
        boolean isIndexModified = isIndexFileLastModifiedAfterResourceFile(targetModelResource);
        if ((targetModelResource.getIndexType() == ModelResource.NOT_INDEXED) && isIndexModified) {
            return true;
        }

        return false;
    }

    private static boolean isIndexFileLastModifiedAfterResourceFile( ModelResource targetModelResource ) {

        File rsrcIndexFile = new File(IndexUtil.INDEX_PATH, IndexUtil.getRuntimeIndexFileName(targetModelResource));
        if (!rsrcIndexFile.exists()) {
            return false;
        }

        final IPath path = ((IFile)targetModelResource.getResource()).getLocation();
        long resourceLastModified = path.toFile().lastModified();
        long indexLastModified = rsrcIndexFile.lastModified();

        return (indexLastModified < resourceLastModified);
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a IFile has errors. The method obtains a model resource
     * which has a simple hasErrors() method.
     * 
     * @param file
     * @return true if has errors, false if not.
     * @since 4.2
     */
    public static boolean hasErrors( IFile file,
                                     Object source ) {
        ModelResource mr = null;
        boolean foundError = false;
        // Find Model Resource
        try {
            mr = ModelUtilities.getModelResource(file, false);
        } catch (ModelWorkspaceException err) {
            String message = getString(MODEL_RESOURCE_NOT_FOUND_MSG_KEY, file.toString());
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }

        if (mr != null) {
            // return mr.hasErrors();
            IMarker[] mrkrs = null;
            boolean errorOccurred = false;
            try {
                mrkrs = mr.getResource().findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
            } catch (CoreException ex) {
                Util.log(ex);
                errorOccurred = true;
            }

            if (!errorOccurred) {
                final IMarker[] markers = mrkrs;

                final boolean startedTxn = ModelerCore.startTxn(false, false, null, source);

                for (int ndx = markers.length; --ndx >= 0;) {
                    final Object attr = MarkerUtilities.getMarkerAttribute(markers[ndx], IMarker.SEVERITY);
                    if (attr == null) {
                        continue;
                    }
                    // Asserting attr is an Integer...
                    final int severity = ((Integer)attr).intValue();
                    if (severity == IMarker.SEVERITY_ERROR) {
                        foundError = true;
                        break;
                    }
                }

                if (startedTxn) {
                    ModelerCore.commitTxn();
                }

            }
        }

        return foundError;
    }

    public static void initializeModelContainers( ModelResource modelResource,
                                                  String txnLabel,
                                                  Object source ) {
        boolean started = ModelerCore.startTxn(false, false, txnLabel, source);
        boolean succeeded = false;
        try {
            ModelerCore.getModelEditor().getAllContainers(modelResource.getEmfResource());
            succeeded = true;
        } catch (ModelWorkspaceException err) {
            String message = getString("ModelUtilities.initializeModelContainersError", modelResource.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        } finally {

            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Scan all metamodels present in this model resource to see if one has the specified URI. This does not cause the model to be
     * loaded.
     * 
     * @param modelResource The ModelResource to scan
     * @param uri The URI to look for
     * @return
     * @throws ModelWorkspaceException if there is a problem getting metamodel descriptors
     */
    public static boolean hasMetamodelWithURI( ModelResource modelResource,
                                               String uri ) throws ModelWorkspaceException {
        // arg check:
        if (modelResource == null || uri == null) {
            return false;
        } // endif

        List allMetamodelDescriptors = modelResource.getAllMetamodelDescriptors();
        for (int i = 0; i < allMetamodelDescriptors.size(); i++) {
            MetamodelDescriptor mmdesc = (MetamodelDescriptor)allMetamodelDescriptors.get(i);
            if (uri.equals(mmdesc.getNamespaceURI())) {
                return true;
            } // endif
        } // endfor

        // not found:
        return false;
    }

    /**
     * Determine if the specified ModelResource contains JDBC import information. Does not cause a model to be loaded. Due to
     * defect 19206, this may not be entirely accurate if the model is not open.
     * 
     * @param modelResource
     * @return
     * @throws ModelWorkspaceException
     */
    public static boolean hasJdbcSource( ModelResource modelResource ) throws ModelWorkspaceException {
        if (modelResource != null) {
            if (!modelResource.isLoaded()) {
                // not loaded, just work with header:
                return hasMetamodelWithURI(modelResource, JdbcPackage.eNS_URI);
            } // endif

            // is loaded, go for accuracy:
            try {
                Resource resource = modelResource.getEmfResource();
                for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
                    EObject eObj = (EObject)iter.next();
                    if (eObj instanceof JdbcSource) {
                        return true;
                    }
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
                WidgetUtil.showError(err.getLocalizedMessage());
            }
        } // endif

        return false;
    }

    public static boolean isRelationalModel( ModelResource rsrc ) throws ModelWorkspaceException {
        // defect 19183 - do not open the model while determining "relationalness"
        if (rsrc == null || rsrc.getPrimaryMetamodelDescriptor() == null) {
            return false;
        } // endif

        return RelationalPackage.eNS_URI.equals(rsrc.getPrimaryMetamodelDescriptor().getNamespaceURI());
    }

    /**
     * Method provides common model save capability with forced update imports
     * 
     * @param modelResource the ModelResource to save
     * @param monitor the progess monitor
     * @param force comes from File.setContents() and indicates the should proceed even if the resource is out of sync with the
     *        local file system.
     * @param forceUpdateImports updates file imports before save.
     * @param source the source which initiated this save
     * @throws Exception if there is a problem saving the resource
     * @since 5.0.2
     */
    public static void saveModelResource( final ModelResource modelResource,
                                          final IProgressMonitor monitor,
                                          final boolean force,
                                          final Object source ) throws Exception {
        modelResource.save(monitor, force);
    }

    /**
     * The <code>ContainerNotificationManager</code> manages workspace listeners.
     */
    static class ContainerNotificationManager implements INotifyChangedListener {
        /** Collection of notification listeners. */
        private List listeners = new ArrayList();

        /**
         * Adds the specified listener to the collection receiving workspace container notifications.
         * 
         * @param theListener the listener being added
         */
        public void addNotifyChangedListener( INotifyChangedListener theListener ) {
            if (!listeners.contains(theListener)) {
                listeners.add(theListener);
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged( Notification theNotification ) {
            fireNotification(theNotification);
        }

        /**
         * Notifies all registerd listeners of the specified notification.
         * 
         * @param theNotification the notification being processed
         */
        private void fireNotification( Notification theNotification ) {
            boolean timerOn = Util.isDebugEnabled(NOTIFICATIONS);
            String notificationType = null;
            Object[] info = null;

            if (timerOn) {
                if (NotificationUtilities.isAdded(theNotification)) {
                    notificationType = "ADDED"; //$NON-NLS-1$
                } else if (NotificationUtilities.isRemoved(theNotification)) {
                    notificationType = "REMOVED"; //$NON-NLS-1$
                } else if (NotificationUtilities.isMoved(theNotification)) {
                    notificationType = "MOVED"; //$NON-NLS-1$
                } else if (NotificationUtilities.isChanged(theNotification)) {
                    notificationType = "CHANGED"; //$NON-NLS-1$
                }

                EObject eObj = NotificationUtilities.getEObject(theNotification);

                if (eObj == null) {
                    info = new Object[2];
                } else {
                    info = new Object[3];
                    info[2] = eObj;
                }
            }

            List tempListenerList = new ArrayList(listeners);

            for (int size = tempListenerList.size(), i = 0; i < size; i++) {
                String timerId = "Notification Handler:"; //$NON-NLS-1$
                String handlerNumber = null;
                INotifyChangedListener l = (INotifyChangedListener)tempListenerList.get(i);

                // We need to be sure that the listener is still around.
                // listener.notifyChanaged() may result in calling removeListener() method and
                // decrease the size of the listener list.

                if (listeners.contains(l)) {
                    if (timerOn) {
                        timerId += l.getClass().getName();
                        handlerNumber = new StringBuffer().append("Handler=").append(i).toString(); //$NON-NLS-1$

                        info[0] = notificationType;
                        info[1] = handlerNumber;

                        Util.start(timerId, NOTIFICATIONS, info);
                    }

                    l.notifyChanged(theNotification);

                    if (timerOn) {
                        Util.stop(timerId, NOTIFICATIONS, info);
                    }
                }
            }
        }

        /**
         * Removes the specified listener from the collection receiving workspace container notifications.
         * 
         * @param theListener the listener being removed
         */
        public void removeNotifyChangedListener( INotifyChangedListener theListener ) {
            listeners.remove(theListener);
        }

    }

    private static class FileResourceCollectorVisitor implements IResourceVisitor {
        private List resources;

        public FileResourceCollectorVisitor() {
            this.resources = new ArrayList();
        }

        public boolean visit( IResource resource ) {
            if (resource.exists() && resource instanceof IFile) {
                resources.add(resource);
            }
            return true;
        }

        public IFile[] getFileResources() {
            return (IFile[])resources.toArray(new IFile[resources.size()]);
        }
    }
}

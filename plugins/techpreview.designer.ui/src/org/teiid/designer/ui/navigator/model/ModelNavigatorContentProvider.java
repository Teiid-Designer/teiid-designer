/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.SHOW_IMPORTS_IN_MODEL_EXPLORER;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ExtendedModelObjectContentProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectContentProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * ModelExplorerContentProvider
 */
public class ModelNavigatorContentProvider extends WorkbenchContentProvider implements UiConstants {

    private static final Object[] NO_CHILDREN = new Object[0];
    private IResourceDeltaVisitor deltaVisitor;
    private ExtendedModelObjectContentProvider extendedContentProvider;
    private Map importContainerMap; // key = annotation, value = ImportContainer
    private boolean keepProcessing = true;
    private EventObjectListener modelListener;
    private ModelObjectContentProvider modelProvider = ModelObjectContentProvider.getInstance();
    private IResourceChangeListener resourceListener;

    /**
     * Construct an instance of ModelExplorerContentProvider.
     */
    public ModelNavigatorContentProvider() {
        this.importContainerMap = new HashMap();
        this.extendedContentProvider = new ExtendedModelObjectContentProvider();

        this.deltaVisitor = new IResourceDeltaVisitor() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
             */
            @Override
            public boolean visit( IResourceDelta theDelta ) {
                return handleDeltaVisit(theDelta);
            }
        };

        this.modelListener = new EventObjectListener() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
             */
            @Override
            public void processEvent( EventObject theEvent ) {
                handleModelEvents(theEvent);
            }
        };
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this.modelListener);
        } catch (EventSourceException theException) {
            Util.log(theException);
        }

        this.resourceListener = new IResourceChangeListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
             */
            @Override
            public void resourceChanged( IResourceChangeEvent theEvent ) {
                handleResourceEvent(theEvent);
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resourceListener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.WorkbenchContentProvider#dispose()
     */
    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.resourceListener);

        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, this.modelListener);
        } catch (EventSourceException theException) {
            Util.log(theException);
        }

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren( final Object parentElement ) {
        Object[] children = NO_CHILDREN;
        try {

            if (parentElement instanceof EObject) {
                return modelProvider.getChildren(parentElement);
            }

            if (parentElement instanceof IProject) {
                if (((IProject)parentElement).isOpen()) {
                    return ((IProject)parentElement).members();
                }

                return NO_CHILDREN;
            }

            if (parentElement instanceof IFile) {
                IFile resource = (IFile)parentElement;

                if (ModelUtilities.isModelFile(resource)) {
                    try {
                        ModelResource modelResource = ModelUtil.getModelResource(resource, true);

                        if (modelResource != null) {
                            children = modelProvider.getChildren(modelResource);

                            if (isShowingImports() && !ModelUtil.isXsdFile(resource)) {
                                Object[] temp = children != null ? children : new Object[] {};

                                try {
                                    ModelAnnotation annotation = modelResource.getModelAnnotation();

                                    if (annotation == null) {
                                        children = temp;
                                    } else {
                                        children = new Object[temp.length + 1];
                                        children[0] = getImportContainer(annotation);

                                        for (int i = 0; i < temp.length; ++i) {
                                            children[i + 1] = temp[i];
                                        }
                                    }
                                } catch (ModelWorkspaceException theException) {
                                    if (!modelResource.hasErrors()) {
                                        // No errors, so we should log this exception ...
                                        UiConstants.Util.log(IStatus.ERROR, theException, theException.getClass().getName());
                                    }
                                    children = temp;
                                } catch (Exception theException) {
                                    UiConstants.Util.log(IStatus.ERROR, theException, theException.getClass().getName());
                                    children = temp;
                                }
                            }

                            return children;
                        }

                        return NO_CHILDREN;
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                        return NO_CHILDREN;
                    }
                }
            }

            if (parentElement instanceof ImportContainer) {
                // parentElement will only be ModelAnnotation when showImportStatements == true
                return ((ImportContainer)parentElement).getModelAnnotation().getModelImports().toArray();
            }

            if (parentElement instanceof IContainer) {
                return ((IContainer)parentElement).members();
            }

            if (parentElement instanceof IExtendedModelObject) {
                return this.extendedContentProvider.getChildren(parentElement);
            }

            return children;
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return children;
    }

    /**
     * @return
     */
    private boolean isShowingImports() {
        return UiPlugin.getDefault().getPreferenceStore().getBoolean(SHOW_IMPORTS_IN_MODEL_EXPLORER);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

    /**
     * Obtains the cached <code>ImportContainer</code> for the specified import or creates one if necessary.
     * 
     * @param theImport the import whose container is being requested
     * @return the container
     * @since 4.2
     */
    private ImportContainer getImportContainer( ModelAnnotation theAnnotation ) {
        ImportContainer result = (ImportContainer)this.importContainerMap.get(theAnnotation);

        if (result == null) {
            result = new ImportContainer(theAnnotation, theAnnotation.eResource());
            this.importContainerMap.put(theAnnotation, result);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent( Object element ) {
        Object parent = null;

        if (element instanceof ModelImport) {
            parent = ((ModelImport)element).eContainer();

            // ModelImport parent's can either be a ModelAnnotation or a VirtualDatabase.
            // If ModelAnnotation lookup the ImportContainer.
            if (parent instanceof ModelAnnotation) {
                parent = getImportContainer((ModelAnnotation)parent);
            }
        } else if (element instanceof ImportContainer) {
            parent = getParent(((ImportContainer)element).getModelAnnotation());
        } else if (element instanceof EObject) {
            parent = modelProvider.getParent(element);
        } else if (extendedContentProvider.getParent(element) != null) {
            parent = extendedContentProvider.getParent(element);
        } else {
            parent = super.getParent(element);
        }

        return parent;
    }

    /**
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
     * @since 4.2
     */
    public boolean handleDeltaVisit( IResourceDelta theDelta ) {
        // only handle delete/remove events and project open/close events
        if (this.keepProcessing && ResourceChangeUtilities.isRemoved(theDelta)) {
            boolean process = false;
            IResource resource = theDelta.getResource();

            if (resource == null) {
                // safety net. should never happen.
                process = true;
            } else if (ModelerCore.hasModelNature(resource.getProject())) {
                // deletes of resources in model projects.
                // could also check to see if deleted resource was a model
                process = true;
            }

            if (process) {
                validateImportCache(null);
                this.keepProcessing = false; // only validate cache one time
            }
        }

        // check children recursively
        return this.keepProcessing;
    }

    /**
     * Handler for when a model is closing.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void handleModelEvents( EventObject theEvent ) {
        if (((ModelResourceEvent)theEvent).getType() == ModelResourceEvent.CLOSING) {
            validateImportCache(((ModelResourceEvent)theEvent).getModelResource().getResource());
        }
    }

    /**
     * Handler for <code>IResourceChangeEvent</code>s.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void handleResourceEvent( IResourceChangeEvent theEvent ) {
        this.keepProcessing = true;

        if (ResourceChangeUtilities.isPreClose(theEvent) || ResourceChangeUtilities.isPreDelete(theEvent)) {
            // project is being closed or deleted
            validateImportCache(theEvent.getResource());
            this.keepProcessing = false; // only validate cache one time
        } else {
            // not a project
            IResourceDelta delta = theEvent.getDelta();

            if (delta != null) {
                try {
                    delta.accept(this.deltaVisitor);
                } catch (CoreException theException) {
                    Util.log(theException);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren( Object element ) {
        if (element instanceof IResource) {
            IResource resource = (IResource)element;
            if (ModelUtilities.isModelFile(resource)) {
                // For model file, return true when showModelContent is true
                return true; // showModelContent;
            }
        } else if (element instanceof EObject) {
            return modelProvider.hasChildren(element);
        } else if (element instanceof IExtendedModelObject) {
            return extendedContentProvider.hasChildren(element);
        }
        return super.hasChildren(element);
    }

    /**
     * Processes each cache entry deleting those whose eResource is null or if they are in a project or model being closed or
     * deleted.
     * 
     * @param theProject the project being closed or deleted; or <code>null</code>
     * @since 4.2
     */
    private void validateImportCache( IResource theResource ) {
        Iterator itr = this.importContainerMap.keySet().iterator();

        while (itr.hasNext()) {
            boolean remove = false;
            ModelAnnotation annotation = (ModelAnnotation)itr.next();

            if (annotation.eResource() == null) {
                remove = true;
            } else if (theResource != null) {
                // see if the cache entry is from the project being closed or deleted
                ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(annotation);

                if (modelResource == null) {
                    remove = true;
                } else {
                    IResource model = modelResource.getResource();

                    if (model == null) {
                        // shouldn't happen
                        remove = true;
                    } else if (theResource instanceof IFile) {
                        // model is closing
                        remove = model.equals(theResource);
                    } else if (theResource instanceof IProject) {
                        // project is closing
                        remove = model.getProject().equals(theResource);
                    }
                }
            }

            if (remove) {
                itr.remove();
            }
        }
    }

}

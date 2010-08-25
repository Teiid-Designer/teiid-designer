/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.refactor.ModelCopyCommand;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelResourceReloadVetoListener;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.internal.ui.outline.ModelOutlinePage;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.MarkerUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.IEditorActionExporter;
import com.metamatrix.modeler.ui.editors.IInitializationCompleteListener;
import com.metamatrix.modeler.ui.editors.IInitializationCompleteNotifier;
import com.metamatrix.modeler.ui.editors.IInlineRenameable;
import com.metamatrix.modeler.ui.editors.INavigationSupported;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.editors.NavigationMarker;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;
import com.metamatrix.ui.internal.dialog.CheckedListSelectionDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.viewsupport.ListContentProvider;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.print.IPrintable;

/**
 * ModelEditor is a MultiPageEditorPart shell that ModelEditorPage extensions can provide ModelEditorPage pages.
 */
public class ModelEditor extends MultiPageModelEditor
    implements IEditorActionExporter, INavigationLocationProvider, INotifyChangedListener, ITextEditorExtension2,
    UiConstants.ExtensionPoints.ModelEditorPage, IResourceChangeListener, INavigationSupported, IInitializationCompleteListener,
    IInlineRenameable, UiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(ModelEditor.class);

    /** Test Harness override of isDirty */
    public static boolean NEVER_DIRTY = false;

    /** Initial modification stamp value. */
    public static long INITIAL_STAMP = -1;

    private ModelOutlinePage contentOutlinePage;
    private ModelObjectPropertySourceProvider propertySourceProvider;
    private ModelEditorSelectionProvider selectionProvider;
    private ModelEditorSelectionSynchronizer selectionSynchronizer;
    private ModelExplorerLabelProvider labelProvider = new ModelExplorerLabelProvider();
    ModelResource modelResource;
    // private IResource modelProject;
    private ModelEditorActionContributor actionBarHandler;

    private ArrayList completionEditors = new ArrayList(1);
    private int nEditorsCompleted = 0;

    /** cache for the current state of the ModelResource */
    boolean resourceDirty = false;

    /** flag to determine if this ModelEditor has been initialized */
    private boolean initialized = false;

    /** flag to ignore any ModelResource changes due to closing */
    boolean isClosing = false;

    /** The context menu to be disposed. */
    private Menu contextMenu;

    /** The veto listener for reloads */
    private ModelResourceReloadVetoListener vetoListener;

    /** The ModelResource listener for file system changes on models and projects */
    EventObjectListener modelResourceListener;

    private int iCurrentPage;

    /**
     * Creates a multi-page editor example.
     */
    public ModelEditor() {
        super();
    }

    /**
     * Determine if thie ModelEditor has been initialized and therefore knows which ModelResource it is working on. This is
     * important because a ModelEditor that is showing in a tab from a previous Eclipse run may not have been initialized if it's
     * tab is never activated. Accessing this editor's data programatically may not be possible if this method returns false.
     */
    public boolean hasInitialized() {
        return initialized;
    }

    /**
     * Creates the pages of the multi-page editor by loading any extensions of the ModelEditorPage extension-point. Note that the
     * ModelEditorPages will not have their createPartControl methods called until the superclass calls back to
     * initializeEditor(), so no attempt to hook up listeners should be made until then.
     */
    @Override
    protected void createPages() {

        // Get this editor's IEditorActionBarContributor which was created when the
        // plugin was activated
        actionBarHandler = (ModelEditorActionContributor)getEditorSite().getActionBarContributor();

        // give the model file to the contributor so that it can update status bar with readonly/writable state
        actionBarHandler.setEditorInput((IFileEditorInput)getEditorInput());

        // get the ModelEditorPage extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);
        // get the all extensions to the ModelEditorPage extension point

        IExtension[] extensions = extensionPoint.getExtensions();
        Object[] editorList = new Object[10];
        String[] nameList = new String[10];

        // make executable extensions for every CLASSNAME
        for (int i = extensions.length - 1; i >= 0; --i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; ++j) {

                // catch any Exception that occurred initializing a ModelEditorPage so that
                // it can be removed and other pages function normally

                try {

                    Object extension = elements[j].createExecutableExtension(CLASSNAME);
                    String orderString = elements[j].getAttribute(ORDER);
                    if (orderString != null) {
                        int order = extensions.length;
                        try {
                            order = new Integer(orderString).intValue();
                        } catch (Exception e) {
                            // leave order as pre-computed;
                        }
                        boolean tryAgain = true;
                        int index = order;
                        while (tryAgain) {
                            if (editorList[index] == null) {
                                editorList[index] = extension;
                                nameList[index] = elements[j].getAttribute(TABNAME);
                                tryAgain = false;
                            } else {
                                ++index;
                            }
                        }
                    }

                } catch (Exception e) {
                    // catch any Exception that occurred initializing a ModelEditorPage so that
                    // it can be removed and other pages function normally

                    String message = UiConstants.Util.getString("ModelEditor.pageInitializationErrorMessage", elements[j].getAttribute(CLASSNAME)); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, e, message);

                    String title = UiConstants.Util.getString("ModelEditor.pageInitializationErrorTitle"); //$NON-NLS-1$
                    MessageDialog.openError(getSite().getShell(), title, message);

                }
            }
        }

        // walk through the ordered list of editors and insert all that can edit the input
        for (int i = 0; i < editorList.length; ++i) {

            int index = -1;
            Object extension = editorList[i];
            try {
                if (extension instanceof ModelEditorPage) {
                    ModelEditorPage page = (ModelEditorPage)extension;

                    if (page.canDisplay(getEditorInput())) {
                        // set label provider in case this is necessary for the tab
                        page.setLabelProvider(this.labelProvider);

                        if (page instanceof IInitializationCompleteNotifier) {
                            ((IInitializationCompleteNotifier)page).addListener(this);
                            completionEditors.add(page);
                        }
                        // add it to this multi-page editor
                        index = addPage(page, getEditorInput());
                        // Store the name on the page for use later.
                        // Some page editors don't supply a title other than
                        // in the plugin. This way the page can always get back to this
                        // title value when necessary (i.e. via refreshEditorTabs)
                        page.setTitleText(nameList[i]);
                        setPageText(index, nameList[i]);
                        setPageImage(index, page.getTitleImage());
                        setPageToolTipText(index, page.getTitleToolTip());
                    } else {
                        // We need to get rid of the remaining pages, that can't be displayed
                        page.preDispose();
                    }

                }
            } catch (Exception e) {
                // catch any Exception that occurred initializing a ModelEditorPage so that
                // it can be removed and other pages function normally

                String message = UiConstants.Util.getString("ModelEditor.pageInitializationErrorMessage", extension.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);

                String title = UiConstants.Util.getString("ModelEditor.pageInitializationErrorTitle"); //$NON-NLS-1$
                MessageDialog.openError(getSite().getShell(), title, message);

                if (index != -1) {
                    // attempt to remove the page, but this may not work since we don't know
                    // what state things were in when the exception was thrown.
                    try {
                        removePage(index);
                    } catch (Exception ex) {
                        // swallowed intentionally, just need to move on to the next page
                    }
                }

            }
        }

        // create a synchronizer to handle selections and double-click between for this editor
        selectionSynchronizer = new ModelEditorSelectionSynchronizer(this, ((IFileEditorInput)getEditorInput()).getFile());
        // add the Synchronizer as a listener to the ModelEditorSelectionProvider so that
        // selection in the editor can be synchronized with the ModelViewers
        this.getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionSynchronizer);

        // hook up our status bar manager for EObject selection in the editor
        IStatusLineManager slManager = getEditorSite().getActionBars().getStatusLineManager();
        selectionProvider.addSelectionChangedListener(new StatusBarUpdater(slManager));

        // Register to listen for Change Notifications
        ModelUtilities.addNotifyChangedListener(this);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        try {
            if (modelResource.getEmfResource().isModified()) {
                resourceDirty = true;
                firePropertyChange(PROP_DIRTY);
            }
        } catch (ModelWorkspaceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        modelResourceListener = new ModelEditorEventObjectListener(this);
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * Callback from the super class when a specified editor is lazily loaded.
     */
    @Override
    protected void initializeEditor( IEditorPart editor ) {
        if (editor instanceof EventObjectListener) {
            try {
                UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, (EventObjectListener)editor);
            } catch (EventSourceException e) {
                Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

        ModelEditorPage page = (ModelEditorPage)editor;

        // see if the extension has an action bar contributor. create it's context menu
        AbstractModelEditorPageActionBarContributor contributor = page.getActionBarContributor();
        if (contributor != null) {
            actionBarHandler.addContributor(editor, contributor);
            contributor.createContextMenu();
            contributor.getActionService();
        }

        // set the editor's ILabelProvider for model objects
        page.setLabelProvider(labelProvider);

        // give the editor a reference to this class (necessary for navigation)
        if (page instanceof INavigationSupported) {

            ((INavigationSupported)page).setParent(this);
        }

        getContentOutline().addOutlineContribution(page);

        IEditorInput input = getEditorInput();
        if (input instanceof IFileEditorInput) {
            page.initializeEditorPage();
        }
    }

    public ModelResource getModelResource() {
        return this.modelResource;
    }

    public IFile getModelFile() {
        return ((IFileEditorInput)this.getEditorInput()).getFile();
    }

    /**
     * Saves the multi-page editor's document.
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // anticipate ugly error message saving to read-only file
        if (ModelUtil.isIResourceReadOnly(this.modelResource.getResource())) {
            String message = UiConstants.Util.getString("ModelEditor.resourceReadonlySaveError", modelResource.getResource().getName()); //$NON-NLS-1$
            String title = UiConstants.Util.getString("ModelEditor.resourceSaveErrorTitle"); //$NON-NLS-1$
            MessageDialog.openError(getSite().getShell(), title, message);

            // if (monitor != null)
            // monitor.setCanceled(true);

            return;
        }

        super.preSave(false);

        WorkspaceJob job = new WorkspaceJob(UiConstants.Util.getString("ModelEditor.saveModelJob", //$NON-NLS-1$
                                                                       this.modelResource.getResource().getName())) {
            @Override
            public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException {
                try {
                    ModelUtilities.saveModelResource(modelResource, monitor, true, this);
                    resourceDirty = false;

                    // property changes must be handled in UI thread
                    UiUtil.runInSwtThread(new Runnable() {
                        @SuppressWarnings( "synthetic-access" )
                        @Override
                        public void run() {
                            ModelEditor.this.firePropertyChange(PROP_DIRTY);
                        }
                    }, true);

                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getLocalizedMessage(), e));
                }
            }

        };

        job.setRule(this.modelResource.getResource());
        job.schedule();
    }

    /**
     * Method declared on IEditorPart.
     */
    @Override
    public boolean isSaveAsAllowed() {
        IWorkbenchPart activePart = getSite().getWorkbenchWindow().getPartService().getActivePart();
        return this == activePart;
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the text for page 0's tab, and updates this multi-page
     * editor's input to correspond to the nested editor's.
     */
    @Override
    public void doSaveAs() {
        final SaveAsDialog dialog = new SaveAsDialog(getSite().getWorkbenchWindow().getShell(), this.modelResource);
        int result = dialog.open();

        if (result == Window.OK) {

            ModelDependencyCheck check = new ModelDependencyCheck(dialog.getCommand());
            UiBusyIndicator.showWhile(null, check);
            result = check.result;

            if (result == Window.OK) {
                // execute the copy command
                WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

                    @Override
                    protected void execute( IProgressMonitor progressMonitor ) throws CoreException {
                        String undoLabel = UiConstants.Util.getString("ModelEditor.saveAs="); //$NON-NLS-1$
                        ModelerCore.startTxn(true, false, undoLabel, ModelEditor.this);
                        try {
                            IStatus status = dialog.getCommand().execute(progressMonitor);
                            if (status != null && !status.isOK()) {
                                // swjTODO: display the problems dialog
                            } else {
                                modelResource.close();
                                ModelEditorManager.close((IFile)modelResource.getResource(), false);
                                ModelEditorManager.activate(dialog.getCommand().getNewIFile(), true);
                            }
                        } finally {
                            ModelerCore.commitTxn();
                        }
                    }
                };

                try {
                    new ProgressMonitorDialog(getSite().getShell()).run(false, false, operation);
                } catch (InvocationTargetException e) {
                    UiConstants.Util.log(e.getTargetException());
                } catch (InterruptedException e) {
                }

            }
        }
    }

    /**
     * Method declared on IEditorPart
     */
    @Override
    public void gotoMarker( IMarker marker ) {
        String sMarkerType = marker.getAttribute(Navigation.MARKER_TYPE, Navigation.UNKNOWN);

        if (sMarkerType.equals(Navigation.NAVIGATION)) {

            // look for a DELEGATE, and pass this call through if there is one:
            Object oDelegate = MarkerUtilities.getMarkerAttribute(marker, Navigation.DELEGATE);
            Object oDelegatesMarker = MarkerUtilities.getMarkerAttribute(marker, Navigation.DELEGATES_MARKER);

            if (oDelegate != null && oDelegate instanceof IEditorPart && oDelegatesMarker != null
                && oDelegatesMarker instanceof IMarker) {
                IEditorPart iepDelegate = (IEditorPart)oDelegate;
                IDE.gotoMarker(iepDelegate, (IMarker)oDelegatesMarker);

            } else {

                // if no delegate, this is a routine tab index change:
                int iPage = marker.getAttribute(Navigation.TAB_INDEX, 0);
                setActivePage(iPage);
            }

        } else {

            // if NOT a navigation marker, handle this way:
            EObject targetEObject = ModelObjectUtilities.getMarkedEObject(marker);

            // find the ModelPage for this object and delegate to its 'gotoMarker'
            if (targetEObject != null) {
                if (ModelEditorManager.canEdit(targetEObject)) {
                    if (getActiveObjectEditor() == null) {
                        ModelEditorManager.edit(targetEObject);

                    } else if (!getActiveObjectEditor().isEditingObject(targetEObject)) {
                        ModelEditorManager.edit(targetEObject);
                    }
                } else if (getActiveObjectEditor() != null) {
                    closeObjectEditor();
                }

                // Now let's go ahead and check for active page and call goToMarker() so selection occurs.
                ModelEditorPage page = null; // getModelPageFor(targetEObject);
                Collection allPages = getModelPagesFor(targetEObject);
                boolean foundPage = false;
                for (Iterator iter = allPages.iterator(); iter.hasNext();) {
                    page = (ModelEditorPage)iter.next();

                    if (page != null) {
                        if (!super.hasInitialized(page)) {
                            super.initializePage(page);
                        }

                        IDE.gotoMarker(page, marker);
                        foundPage = true;
                    }
                }
                if (!foundPage) {
                    // no editor page can handle the marker, see if the model explorer is open and pass the marker to it.
                    final IViewPart part = UiUtil.getWorkbenchPage().findView(UiConstants.Extensions.Explorer.VIEW);

                    if (part != null) {
                        ((ModelExplorerResourceNavigator)part).gotoMarker(marker);
                    }
                }
            }
        }

    }

    /**
     * The <code>ModelEditor</code> implementation of this <code>IEditorPart</code> method sets its site to the given site, its
     * input to the given input, and the site's selection provider to a <code>ModelEditorSelectionProvider</code>. Subclasses may
     * extend this method.
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        super.init(site, input);

        // check that the input is an instance of IFileEditorInput.
        // also need to make sure that the file exists on the filesystem. this is because when Eclipse shuts
        // down it saves a restore state. if a resource was deleted outside of Eclipse that was part of the
        // restore state Eclipse still creates an IResource for it which we don't want to happen.
        if (!(input instanceof IFileEditorInput) || !ModelUtilities.existsOnFilesystem(((IFileEditorInput)input).getFile())) {
            String message = UiConstants.Util.getString("ModelEditor.invalidInputError", input.getName()); //$NON-NLS-1$
            throw new PartInitException(message);
        }

        final IFile file = ((IFileEditorInput)input).getFile();

        // If the resource has a model file extension but with the wrong case then throw exception (defect 17709)
        if (ModelUtil.isModelFile(file)) {
            // continue

            // If the IFile extension does not match the set of well-known model file extensions with
            // a case-sensitive check but does match when the check is case-insensitive then error (defect 17709)
        } else if (!ModelFileUtil.isModelFileExtension(file.getFileExtension(), true)
                   && ModelFileUtil.isModelFileExtension(file.getFileExtension(), false)) {
            final String actualFileName = file.getName();
            final String actualExtension = file.getFileExtension();
            final int endIndex = actualFileName.length() - actualExtension.length();
            final String expectedFileName = actualFileName.substring(0, endIndex) + actualExtension.toLowerCase();

            final Object[] params = new Object[] {actualFileName, expectedFileName};
            final String msg = UiConstants.Util.getString("ModelEditor.file_extension_not_correct_case_please_rename_file", params); //$NON-NLS-1$
            throw new PartInitException(msg);
        }

        // get the ModelResource.
        try {
            modelResource = ModelUtil.getModelResource(((IFileEditorInput)input).getFile(), false);
            if (modelResource == null) {
                String message = UiConstants.Util.getString("ModelEditor.modelResourceError", input.getName()); //$NON-NLS-1$
                throw new PartInitException(message);
            }

            // Register this editor's veto listener with the workspace ...
            this.vetoListener = new ModelResourceReloadVetoListener() {

                public boolean canReload( final ModelResource theModelResource ) {
                    return handleCanReload(theModelResource);
                }
            };
            modelResource.getModelWorkspace().addModelResourceReloadVetoListener(this.vetoListener);

            // MetamodelDescriptor descriptor = modelResource.getPrimaryMetamodelDescriptor();
            // String descriptorUri = null;
            // if (descriptor != null) {
            // descriptorUri = descriptor.getNamespaceURI();
            // }

            // add the ModelResource to the OpenEditorMap
            OpenEditorMap.getInstance().addModelEditor(this, modelResource);
        } catch (ModelWorkspaceException e) {
            String message = UiConstants.Util.getString("ModelEditor.modelResourceError", input.getName()); //$NON-NLS-1$
            throw new PartInitException(message, e);
        }

        // initialize this IEditorPart
        setSite(site);
        setInput(input);
        setPartName(((IFileEditorInput)getEditorInput()).getFile().getName());

        setTitleImage(ModelIdentifier.getModelImage(modelResource));

        // connect the ModelEditorSelectionProvider as the selection provider for the workbench
        selectionProvider = new ModelEditorSelectionProvider(this);
        super.getEditorSite().setSelectionProvider(selectionProvider);

        // check the readonly status of the editor resource each time the modeler window is activated
        getEditorSite().getWorkbenchWindow().getWorkbench().addWindowListener(new IWindowListener() {

            public void windowActivated( IWorkbenchWindow theWindow ) {
                getActionBarContributor().setReadOnlyState();
            }

            public void windowOpened( IWorkbenchWindow theWindow ) {
            }

            public void windowDeactivated( IWorkbenchWindow theWindow ) {
            }

            public void windowClosed( IWorkbenchWindow theWindow ) {
            }
        });

        this.initialized = true;
    }

    protected boolean handleCanReload( final ModelResource theModelResource ) {
        boolean canReload = true;
        if (this.modelResource != null && this.modelResource.equals(theModelResource)) {
            // The supplied resource is the "same" as this resource, so ask user whether to reload ...
            final ReloadRequestRunnable runnable = new ReloadRequestRunnable();
            Display.getDefault().syncExec(runnable);
            canReload = runnable.canReload();
        }
        return canReload;
    }

    /**
     * Obtains the ModelEditorActionBarContributor
     */
    public ModelEditorActionContributor getActionBarContributor() {
        return (ModelEditorActionContributor)getEditorSite().getActionBarContributor();
    }

    public IEditorPart getCurrentPage() {
        return getActiveEditor();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    public void contributeExportedActions( IMenuManager theMenuMgr ) {
        // called by action service contributeToContextMenu
        ModelEditorPage page = (ModelEditorPage)getCurrentPage();
        AbstractModelEditorPageActionBarContributor contributor = page.getActionBarContributor();

        if (contributor != null) {
            contributor.contributeExportedActions(theMenuMgr);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // called by action service contributeToContextMenu
        ModelEditorPage page = (ModelEditorPage)getCurrentPage();
        AbstractModelEditorPageActionBarContributor contributor = page.getActionBarContributor();

        if (contributor != null) {
            return contributor.getAdditionalModelingActions(selection);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
     */
    public ISelection getSelection() {
        return selectionProvider.getSelection();
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();
        ModelEditorActionContributor abc = getActionBarContributor();
        if (abc != null) {
            abc.setEditorInput((IFileEditorInput)getEditorInput());
            abc.setReadOnlyState();
            // let's notify the model editors that read-only changed.
            ModelEditorPage page = (ModelEditorPage)super.getActiveEditor();
            if (page != null) {
                page.updateReadOnlyState(abc.getReadOnlyState());
            }
            updateReadOnlyState();
        } // endif
    }

    public void setSelection( ISelection selection ) {
        selectionProvider.setSelection(selection);
    }

    /**
     * Overridden to intercept and provide IContentOutlinePage adapter
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class key ) {

        // get the EMF Editor's OutlinePage does the Outline page too
        if (key.equals(IContentOutlinePage.class)) {
            return getContentOutline();
        } else if (key.equals(IPropertySheetPage.class)) {
            if (propertySourceProvider == null) {
                propertySourceProvider = ModelUtilities.getPropertySourceProvider();
            }
            return propertySourceProvider.getPropertySheetPage();
        } else if (key.equals(IFindReplaceTarget.class)) {
            if (this.editorContainer.hasFocus()) {
                if (this.editorContainer.getActiveEditor() instanceof IAdaptable) {
                    return ((IAdaptable)this.editorContainer.getActiveEditor()).getAdapter(key);
                }

                return null;
            }

            return (getCurrentPage() == null) ? null : getCurrentPage().getAdapter(key);
        } else if (key.equals(IPrintable.class)) {
            /*
             * Get the current page, and call its 'getAdapter( IPrintable.class )'
             */
            if (getCurrentPage() != null) {

                return getCurrentPage().getAdapter(IPrintable.class);
            }

        } else if (key.equals(IUndoManager.class)) {
            if (this.editorContainer.hasFocus()) {
                ModelObjectEditorPage objectEditor = this.editorContainer.getActiveEditor();

                if (objectEditor instanceof IAdaptable) {
                    Object undoMgr = ((IAdaptable)objectEditor).getAdapter(key);

                    if (undoMgr != null) {
                        return undoMgr;
                    }
                }
            }
        }

        return super.getAdapter(key);
    }

    public IAdaptable getExternalAdaptable() {
        if (editorContainer != null) {
            return editorContainer;
        }
        return null;
    }

    public ModelObjectEditorPanel getEditorContainer() {
        return editorContainer;
    }

    @Override
    public void dispose() {

        //
        // NOTE: DO NOT dispose of the ModelEditorActionContributor !!!!!
        // The Eclipse framework disposes of it when the last ModelEditor is closed. The framework
        // also constructs a new contributor when the first ModelEditor is opened.
        //

        if (propertySourceProvider != null) {
            propertySourceProvider.dispose();
        }
        if (contentOutlinePage != null) {
            contentOutlinePage.dispose();
        }
        if (selectionSynchronizer != null) {
            selectionSynchronizer.dispose();
            this.getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionSynchronizer);
        }
        if (contextMenu != null) {
            contextMenu.dispose();
        }

        // Un-Register this for notifications
        ModelUtilities.removeNotifyChangedListener(this);

        // Un-Register this for Resource change events
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        // Un-Register this for Reload vetos ...
        modelResource.getModelWorkspace().removeModelResourceReloadVetoListener(this.vetoListener);

        // Need to walk through editors and remove ModelResourceEvent listeners
        Iterator iter = getAllEditors().iterator();
        Object nextEditor = null;
        while (iter.hasNext()) {
            nextEditor = iter.next();
            if (nextEditor instanceof EventObjectListener) {
                try {
                    UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class,
                                                                          (EventObjectListener)nextEditor);
                } catch (EventSourceException e) {
                    Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }

        // Let's remove the model editor from event broker
        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }

        // Dispose created O/S-related resources
        this.labelProvider.dispose();

        if (this.modelResource != null && this.modelResource.isOpen()) {
            try {
                OpenEditorMap.getInstance().removeModelEditor(this.modelResource);
                if (isDirty()) {
                    ModelResourceEvent event = new ModelResourceEvent(this.modelResource, ModelResourceEvent.CLOSING, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                    this.modelResource.close();
                    event = new ModelResourceEvent(this.modelResource, ModelResourceEvent.CLOSED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);

                    ModelerUndoManager.getInstance().clearAllEdits();

                    // defect 16805 - notify listeners that this model will be reloaded
                    // since the changes have been discarded (even though, yes,
                    // technically it hasn't been reloaded yet)
                    event = new ModelResourceEvent(this.modelResource, ModelResourceEvent.RELOADED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            } catch (ModelWorkspaceException e) {
                String message = UiConstants.Util.getString("ModelEditor.modelResourceCloseError", this.modelResource); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        super.dispose(); // this disposes of the nested pages
    }

    public boolean canOpenModelObject( Object modelObject ) {
        boolean result = false;
        if (modelObject != null) {
            for (int i = 0; i < super.getPageCount(); ++i) {
                ModelEditorPage page = (ModelEditorPage)getEditor(i);
                if (page.canOpenContext(modelObject)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public ModelEditorPage getModelPageFor( Object modelObject ) {
        ModelEditorPage result = null;
        if (modelObject != null) {
            for (int i = 0; i < super.getPageCount(); ++i) {
                ModelEditorPage page = (ModelEditorPage)getEditor(i);
                if (page.canOpenContext(modelObject)) {
                    result = page;
                    break;
                }
            }
        }
        return result;
    }

    public Collection getModelPagesFor( Object modelObject ) {
        Collection pages = new ArrayList();

        if (modelObject != null) {
            for (int i = 0; i < super.getPageCount(); ++i) {
                ModelEditorPage page = (ModelEditorPage)getEditor(i);
                if (page.canOpenContext(modelObject)) {
                    pages.add(page);
                }
            }
        }

        if (pages.isEmpty()) return Collections.EMPTY_LIST;

        return pages;
    }

    /**
     * The editor responds by calling openContext for each page that responds positively to canOpenContext. The currently active
     * page (if it can open the new context) will stay the active page, else the first page that can open the new context will
     * become the active page.
     * 
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     * @param modelObject the new editor context
     * @return true if the object was opened in a ModelEditorPage, false if all pages ignored the object.
     */
    public boolean openModelObject( Object modelObject ) {

        // calls the real openModelObject, defaulting forceRefesh to false
        return openModelObject(modelObject, false);
    }

    /**
     * The editor responds by calling openContext for each page that responds positively to canOpenContext. The currently active
     * page (if it can open the new context) will stay the active page, else the first page that can open the new context will
     * become the active page.
     * 
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     * @param modelObject the new editor context
     * @param forceRefresh whether or not to refresh the context after open
     * @return true if the object was opened in a ModelEditorPage, false if all pages ignored the object.
     */
    public boolean openModelObject( Object modelObject,
                                    boolean forceRefresh ) {
        // System.out.println("ModelEditor.openModelObject()"); //$NON-NLS-1$

        boolean result = false;
        ModelEditorPage focusPage = null;
        int pageIndex = -1;

        if (modelObject != null) {
            for (int i = 0; i < super.getPageCount(); ++i) {
                ModelEditorPage page = (ModelEditorPage)getEditor(i);

                if (page.canOpenContext(modelObject)) {
                    if (!hasInitialized(page)) {
                        initializePage(page);
                    }
                    page.openContext(modelObject, forceRefresh);

                    // keep current active page the same if possible
                    if ((focusPage == null) || (getActiveEditor() == page)) {
                        focusPage = page;
                        pageIndex = i;
                        result = true;
                    }
                }
            }

            if (focusPage != null) {
                if (getActiveEditor() != focusPage) {
                    if (!hasInitialized(focusPage)) {
                        super.initializePage(focusPage, super.getItem(pageIndex));
                    }

                    super.setActivePage(pageIndex);
                }

                pageChange(pageIndex);

                if (getActiveEditor() != focusPage) {
                    setFocus();
                    // ----------------------------
                    // Defect 22844 - the individual page.setFocus() was not being called. This is required
                    // to help with this defect.
                    // ----------------------------
                    focusPage.setFocus();
                }

                // Tell this editor's SelectionProvider about the new page
                if (selectionProvider != null) {
                    selectionProvider.setSourcePage(focusPage);
                }

                setPageImage(pageIndex, focusPage.getTitleImage());
                setPageText(pageIndex, focusPage.getTitle());
                setPageToolTipText(pageIndex, focusPage.getTitleToolTip());
            }
        }

        return result;
    }

    public void refreshEditorTabs() {
        for (int i = 0; i < super.getPageCount(); ++i) {
            ModelEditorPage page = (ModelEditorPage)getEditor(i);

            if (page != null) {
                setPageImage(i, page.getTitleImage());
                setPageText(i, page.getTitle());
                setPageToolTipText(i, page.getTitleToolTip());
            }
        }

    }

    private ModelOutlinePage getContentOutline() {
        if (contentOutlinePage == null) {
            contentOutlinePage = new ModelOutlinePage(this);
        }
        return contentOutlinePage;
    }

    /**
     * Overridden to allow the action bar contributers to be activated and deactivated, and the ModelOutlinePage to enable/disable
     * any outline page contributions.
     */
    @Override
    protected void pageChange( int newPageIndex ) {
        // XXX: Workaround for 1GCN531: SWT:WIN2000 - CTabFolder child's visibility is false on notification
        Control control = getControl(newPageIndex);
        if (control != null) {
            control.setVisible(true);
        }
        // XXX: End workaround

        // this is our only chance to keep track of what page we are on; createMarker() will use this:
        iCurrentPage = newPageIndex;

        IEditorPart activeEditor = getEditor(newPageIndex);

        if (activeEditor != null) {
            // handle tabs right here in ModelEditor
            IWorkbenchPage workbenchPage = UiUtil.getWorkbenchPage();
            if (workbenchPage != null) {
                workbenchPage.getNavigationHistory().markLocation(this);
            } // endif

            // notify the ModelOutlinePage that the active editor page has changed
            getContentOutline().setActiveEditorPage((ModelEditorPage)activeEditor);

            if (selectionProvider != null) {
                // Tell this editor's SelectionProvider who the current page is
                selectionProvider.setSourcePage((ModelEditorPage)activeEditor);
            }
            // ----------------------------
            // Defect 22844 - the individual activeEditor.setFocus() was not being called on pageChange().
            // This is required to help with this defect.
            // ----------------------------
            activeEditor.setFocus();
        } else {
            // System.out.println("[ModelEditor.pageChange] activeEditor was NULL! " ); //$NON-NLS-1$
        }

        // let the contributor know about the new page
        IEditorActionBarContributor contributor = getEditorSite().getActionBarContributor();

        if ((contributor != null) && (contributor instanceof ModelEditorActionContributor) && activeEditor != null) {
            ((ModelEditorActionContributor)contributor).setActivePage(activeEditor);
        }

    }

    public void displayModelEditorPage( ModelEditorPage mepPageEditor ) {
        for (int i = 0; i < super.getPageCount(); ++i) {
            ModelEditorPage page = (ModelEditorPage)getEditor(i);
            if (page == mepPageEditor) {
                this.setActivePage(i);
                break;
            }
        }
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( final Notification notification ) {

        if (this.modelResource.isOpen()) {

            // pass the notification on to all pages that have a NotifyChangeListener
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    for (int i = 0; i < getPageCount(); ++i) {
                        ModelEditorPage page = (ModelEditorPage)getEditor(i);
                        if (page != null && hasInitialized(page)) {
                            INotifyChangedListener listener = page.getNotifyChangedListener();
                            if (listener != null) {
                                listener.notifyChanged(notification);
                            }
                        }
                    }
                }
            });

            if (!resourceDirty && NotificationUtilities.isChanged(notification)) {

                try {

                    if (this.modelResource.isOpen()) {
                        if (this.modelResource.getEmfResource().isModified()) {
                            resourceDirty = true;
                            Display.getDefault().asyncExec(new Runnable() {

                                public void run() {
                                    dirtyProperty();
                                }
                            });
                        }
                    }

                } catch (ModelWorkspaceException mwe) {
                    UiConstants.Util.log(mwe);
                }
            } else if (notification.getNotifier() instanceof Resource) {
                try {
                    if (this.modelResource.isOpen()) {
                        final boolean rsrcDirty = this.modelResource.getEmfResource().isModified();
                        if (rsrcDirty != this.resourceDirty) {
                            this.resourceDirty = rsrcDirty;
                            Display.getDefault().asyncExec(new Runnable() {

                                public void run() {
                                    dirtyProperty();
                                }
                            });
                        }
                    }

                } catch (ModelWorkspaceException mwe) {
                    UiConstants.Util.log(mwe);
                }
            }

            // Need to ask the activeObjectEditor if it's resource is still valid
            if (getActiveObjectEditor() != null && !getActiveObjectEditor().isResourceValid()) Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    closeObjectEditor();
                }
            });
        }
    }

    void dirtyProperty() {
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isResourceDirty() {
        return this.resourceDirty;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        if (NEVER_DIRTY || isClosing) {
            return false;
        }
        return super.isDirty();
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged( IResourceChangeEvent event ) {
        int type = event.getType();
        if (type == IResourceChangeEvent.POST_CHANGE) {
            try {
                IResourceDelta delta = event.getDelta();
                if (delta != null) {
                    delta.accept(new IResourceDeltaVisitor() {

                        public boolean visit( IResourceDelta delta ) {
                            if (delta.getResource().equals(getModelFile()) && ((delta.getKind() & IResourceDelta.REMOVED) != 0)) {
                                Display.getDefault().asyncExec(new Runnable() {

                                    public void run() {
                                        if (Display.getDefault().isDisposed()) {
                                            return;
                                        }
                                        if (UiPlugin.getDefault().getCurrentWorkbenchWindow() != null
                                            && UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage() != null) {
                                            UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage().closeEditor(ModelEditor.this,
                                                                                                                          false);
                                        }
                                    }
                                });
                                return false;
                            }
                            return true;
                        }
                    });

                }
            } catch (CoreException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

    // ===========================================
    // ITextEditorExtension2 interface methods
    // ===========================================

    public boolean isEditorInputModifiable() {
        IFindReplaceTarget target = (IFindReplaceTarget)getAdapter(IFindReplaceTarget.class);
        return (target == null ? false : target.isEditable());
    }

    public boolean validateEditorInputState() {
        return true;
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.INavigationLocationProvider#createEmptyNavigationLocation()
     * @since 4.0
     */
    public INavigationLocation createEmptyNavigationLocation() {
        // System.out.println("[ModelEditor.createEmptyNavigationLocation] TOP"); //$NON-NLS-1$
        return null;
        // Defect 22290 reflects memory (leaks) issues within designer. (See createNavigationLocation() comments below)
        // return neNavigableEditor.createEmptyNavigationLocation();
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.INavigationLocationProvider#createNavigationLocation()
     * @since 4.0
     */
    public INavigationLocation createNavigationLocation() {
        // if no editor, return null (and no history entry will be created)
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.INavigationSupported
     */
    public IMarker createMarker() {
        // System.out.println("[ModelEditor.createMarker] TOP"); //$NON-NLS-1$

        NavigationMarker nmMarker = new NavigationMarker();

        // if activeEditor instanceof INavigationLocationProvider, use it as a DELEGATE
        IEditorPart iepActiveEditor = getActiveEditor();

        nmMarker.setAttribute(Navigation.MARKER_TYPE, Navigation.NAVIGATION);

        if (iepActiveEditor != null) {

            if (iepActiveEditor instanceof INavigationLocationProvider && iepActiveEditor instanceof INavigationSupported) {
                nmMarker.setAttribute(Navigation.DELEGATE, iepActiveEditor);
                nmMarker.setAttribute(Navigation.DELEGATES_MARKER, ((INavigationSupported)iepActiveEditor).createMarker());

            } else {
                nmMarker.setAttribute(Navigation.TAB_INDEX, iCurrentPage);
            }
        }

        return nmMarker;
    }

    public void openComplete() {
        // pass the notification on to all pages that have a NotifyChangeListener
        Display display = getSite().getWorkbenchWindow().getShell().getDisplay();
        UiBusyIndicator.showWhile(display, new Runnable() {

            public void run() {
                for (int i = 0; i < getPageCount(); ++i) {
                    ModelEditorPage page = (ModelEditorPage)getEditor(i);
                    if (page != null && hasInitialized(page)) {
                        page.openComplete();
                    }
                }
            }
        });
    }

    /**
     * When the final completionEditor responds, we do a check if the resource for this editor is dirty and automatically save.
     * This insures that any model created during import or otherwise that contains children that can be displayed in a diagram
     * will cause diagrams and diagram entities to be created in the diagram editor. This is a specific case, but there may be
     * other editors that could potentially make the model dirty while opening the editor to edit the resource.
     * 
     * @see com.metamatrix.modeler.ui.editors.IInitializationCompleteListener#processInitializationComplete()
     * @since 4.3
     */
    public void processInitializationComplete() {
        // System.out.println(" ModelEditor.processInitComplete() called!!!!! isDirty = " + isDirty() + " isResourceDirty() = " +
        // isResourceDirty() + " isClosing = " + isClosing);
        if (nEditorsCompleted < completionEditors.size()) {
            nEditorsCompleted++;
            if (nEditorsCompleted == completionEditors.size()) {
                nEditorsCompleted = 0;
                // Initialization complete. Now fork off cleaning up the listeners and SAVE if dirty
                Display.getCurrent().asyncExec(new Runnable() {

                    public void run() {
                        final List allEditors = getAllEditors();
                        for (Iterator iter = allEditors.iterator(); iter.hasNext();) {
                            Object nextEditor = iter.next();
                            if (nextEditor instanceof IInitializationCompleteNotifier) {
                                ((IInitializationCompleteNotifier)nextEditor).removeListener(ModelEditor.this);
                            }
                        }
                        if ((resourceDirty || isDirty()) && !isClosing) {
                            // All editors have completed initialization
                            // Default behavior is we save the editor
                            IProgressMonitor monitor = new NullProgressMonitor();
                            doSave(monitor);
                        }
                    }
                });
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.INavigationSupported#setParent()
     */
    public void setParent( ModelEditor meParentEditor ) {
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#canRenameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public IInlineRenameable getInlineRenameable( EObject theObj ) {
        // there may be more than one renameable editor, however only ONE can be in focus
        // Search the editors and return a single focused renameable editor or NULL
        final List allEditors = getAllEditors();
        for (Iterator iter = allEditors.iterator(); iter.hasNext();) {
            Object nextEditor = iter.next();
            if (nextEditor instanceof IInlineRenameable && nextEditor instanceof EditorPart) {
                boolean isThisActivePage = getActiveEditor() == nextEditor;
                if (isThisActivePage) {
                    return (IInlineRenameable)nextEditor;
                }
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IInlineRenameable#renameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0
     */
    public void renameInline( EObject theObj,
                              IInlineRenameable renameable ) {
        IWorkbenchWindow window = getSite().getWorkbenchWindow();
        // find the renamable editor, set focus then call rename on it.
        boolean isThisActivePage = window.getActivePage().getActivePart() == renameable;
        if (renameable != null) {
            if (!isThisActivePage) {
                ((IWorkbenchPart)renameable).setFocus();
            }
            renameable.renameInline(theObj, renameable);
        }
    }

    /**
     * A runnable for checking model dependencies before Save As. Since this search can take several seconds the logic was
     * implemented as a Runnable that can be passed to a progress dialog or busy indicator.
     * 
     * @since 4.2
     */
    public class ModelDependencyCheck implements Runnable {

        public int result;
        public ModelCopyCommand copyCommand;

        public ModelDependencyCheck( ModelCopyCommand copyCommand ) {
            this.copyCommand = copyCommand;
        }

        public void run() {
            result = Window.OK;

            // determine if there are any models dependent upon this one
            // search the workspace for any models that import anything beneath the path that is moving
            ModelWorkspaceSearch search = new ModelWorkspaceSearch();
            Collection colDependentResources = search.getModelsImportingResources(modelResource.getResource().getFullPath().toString());

            if (!colDependentResources.isEmpty()) {
                // build a collection of files from the search result
                Collection fileList = new ArrayList(colDependentResources.size());
                for (Iterator iter = colDependentResources.iterator(); iter.hasNext();) {
                    String pathString = ((ResourceImportRecord)iter.next()).getPath();
                    IPath path = new Path(pathString);
                    fileList.add(ModelerCore.getWorkspace().getRoot().getFile(path));
                }

                // display the files to the user and allow them to select which, if any, should be redirected to the copy
                ModelLabelProvider labelProvider = new ModelLabelProvider();
                labelProvider.setFullpath(true);
                CheckedListSelectionDialog depDialog = new CheckedListSelectionDialog(
                                                                                      getSite().getWorkbenchWindow().getShell(),
                                                                                      fileList,
                                                                                      new ListContentProvider(),
                                                                                      labelProvider,
                                                                                      UiConstants.Util.getString("ModelEditor.redirectReferencesMessage") //$NON-NLS-1$
                );

                depDialog.setTitle(UiConstants.Util.getString("ModelEditor.redirectReferencesTitle")); //$NON-NLS-1$
                depDialog.setSelectionStatusValidator(new ISelectionStatusValidator() {

                    private Collection dirtyFiles = ModelEditorManager.getDirtyResources();

                    public IStatus validate( Object[] selection ) {
                        for (int i = 0; i < selection.length; ++i) {
                            if (((IFile)selection[i]).isReadOnly()) {
                                final String name = ((IFile)selection[i]).getName();
                                return new StatusInfo(
                                                      UiConstants.PLUGIN_ID,
                                                      IStatus.ERROR,
                                                      UiConstants.Util.getString("ModelEditor.redirectReferencesReadOnlyError", name)); //$NON-NLS-1$
                            } else if (dirtyFiles.contains(selection[i])) {
                                final String name = ((IFile)selection[i]).getName();
                                return new StatusInfo(
                                                      UiConstants.PLUGIN_ID,
                                                      IStatus.ERROR,
                                                      UiConstants.Util.getString("ModelEditor.redirectReferencesDirtyFileError", name)); //$NON-NLS-1$
                            }
                        }
                        return null;
                    }
                });
                result = depDialog.open();

                if (result == Window.OK) {
                    Object[] models = depDialog.getResult();
                    if (models != null && models.length > 0) {
                        ArrayList modelList = new ArrayList(models.length);
                        for (int i = 0; i < models.length; ++i) {
                            IFile file = (IFile)models[i];
                            if (file.exists()) {
                                try {
                                    modelList.add(ModelUtil.getModelResource(file, true));
                                } catch (ModelWorkspaceException e) {
                                    String message = UiConstants.Util.getString("ModelEditor.modelResourceError", file.getName()); //$NON-NLS-1$
                                    UiConstants.Util.log(IStatus.ERROR, e, message);
                                }
                            }
                        }
                        copyCommand.setModelsToRedirect(modelList);
                    }
                }
            }
        }
    }

    protected class ReloadRequestRunnable implements Runnable {

        private boolean reload;

        /**
         * @see java.lang.Runnable#run()
         * @since 4.2
         */
        public void run() {
            this.reload = MessageDialog.openQuestion(getSite().getShell(), Util.getString(PREFIX
                                                                                          + "dialog.fileSystemChange.title"), //$NON-NLS-1$
                                                     Util.getString(PREFIX + "dialog.fileSystemChange.msg", //$NON-NLS-1$
                                                                    new Object[] {ModelEditor.this.modelResource.getItemName()}));
        }

        public boolean canReload() {
            return this.reload;
        }
    }

}

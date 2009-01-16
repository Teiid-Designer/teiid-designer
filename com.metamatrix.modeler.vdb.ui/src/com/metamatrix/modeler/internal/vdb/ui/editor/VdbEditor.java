/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.actions.RedoAction;
import com.metamatrix.modeler.internal.ui.actions.UndoAction;
import com.metamatrix.modeler.internal.vdb.ui.properties.ModelReferencePropertySourceProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.vdb.edit.ClosePreventionVetoableChangeListener;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.0
 */
public final class VdbEditor extends MultiPageEditorPart
    implements IResourceChangeListener, VdbUiConstants, VdbUiConstants.ExtensionPoints, UiConstants.ProductInfo.Capabilities,
    IRevertable, IGotoMarker, ITextEditorExtension2 {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditor.class);

    interface Constants {
        String INVALID_INPUT_MESSAGE = getString("invalidInputMessage"); //$NON-NLS-1$
    }

    /**
     * @since 4.0
     */
    static Image getStatusImage( final Severity severity ) {

        switch (severity.getValue()) {
            case Severity.ERROR: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            }
            case Severity.WARNING: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
            }
            case Severity.INFO: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
            }
            default: {
                return null;
            }
        }
    }

    /**
     * @since 4.0
     */
    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditingContext context;
    private IResource modelProject;
    ArrayList editors;
    private ModelReferencePropertySourceProvider propertySourceProvider;
    boolean saveCanceled = false;
    VdbEditorOverviewPage overviewPage;
    VdbEditorProblemPage problemPage;
    private VdbEditorIndexPage indexPage;
    private VdbEditorUserFilesPage userFilesPage;
    private VdbEditorWsdlPage wsdlPage;

    boolean vdbWasJustSaved = false;
    private long currentTimeStamp = 0;
    private VetoableChangeListener closeVetoer = new ClosePreventionVetoableChangeListener();

    private UndoAction undoAction;
    private RedoAction redoAction;

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     * @since 4.0
     */
    @Override
    protected void createPages() {
        createPagesInTransaction();
    }

    private void createPagesInTransaction() {
        boolean started = ModelerCore.startTxn(false, false, "Open VDB Editor", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            final IEditorInput input = getEditorInput();
            overviewPage = new VdbEditorOverviewPage(this);
            addPage(overviewPage, input);
            problemPage = new VdbEditorProblemPage(this);
            addPage(problemPage, input);
            indexPage = new VdbEditorIndexPage(this);
            addPage(indexPage, input);
            userFilesPage = new VdbEditorUserFilesPage(this);
            addPage(userFilesPage, input);
            wsdlPage = new VdbEditorWsdlPage(this);
            int index = addPage(wsdlPage, input);

            // add additional pages contributed by extensions
            createAdditionalPages(index + 1, input);

            this.modelProject = ((IFileEditorInput)input).getFile().getProject();
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
            succeeded = true;
        } catch (final PartInitException err) {
            WidgetUtil.showError(err.getMessage());
            Util.log(err);
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                    // ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Add any editor pages contributed by extensions.
     * 
     * @since 4.3
     */
    protected void createAdditionalPages( int theStartingIndex,
                                          IEditorInput theInput ) {
        // get the VDB Editor Page extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, VdbEditorPage.ID);

        // get the all extensions to this extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        // if no extensions no work to do
        if (extensions.length == 0) {
            return;
        }

        // make executable extensions for every CLASS_NAME
        for (int i = 0; i < extensions.length; ++i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            for (int j = 0; j < elements.length; ++j) {
                try {
                    Object extension = elements[j].createExecutableExtension(VdbEditorPage.CLASS_NAME);

                    if (extension instanceof IVdbEditorPage) {
                        IVdbEditorPage page = (IVdbEditorPage)extension;
                        page.setVdbEditingContext(this.context);

                        // add the page
                        int pageNum = addPage(page, theInput);

                        // get additional extension info
                        String displayName = elements[j].getAttribute(VdbEditorPage.DISPLAY_NAME);
                        // String order = elements[j].getAttribute(VdbEditorPage.ORDER);

                        // call interface methods
                        page.setDisplayName(displayName);
                        setPageText(pageNum, displayName);
                        page.updateReadOnlyState(getReadonlyState());

                        ISelectionListener handler = page.getSelectionListener();

                        if (handler != null) {
                            getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(handler);
                        }
                    } else {
                        Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "pageExtensionIncorrectClass", //$NON-NLS-1$
                                                               extension.getClass().getName()));
                    }
                } catch (Exception theException) {
                    // problem initializing the VDB Editor Page
                    String msg = Util.getString(I18N_PREFIX + "editorPageInitializationError", //$NON-NLS-1$
                                                elements[j].getAttribute(VdbEditorPage.CLASS_NAME));
                    Util.log(IStatus.ERROR, theException, msg);
                }
            }
        }
    }

    /**
     * Obtains the readonly state of the VDB editor.
     * 
     * @return <code>true</code> if readonly; <code>false</code> otherwise.
     * @since 4.3
     */
    private boolean getReadonlyState() {
        boolean result = true;

        IEditorInput input = getEditorInput();

        if ((input != null) && (input instanceof IFileEditorInput)) {
            result = ((IFileEditorInput)input).getFile().isReadOnly();
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();

        IEditorPart page = getActiveEditor();

        // inform IVdbEditorPages of readonly state
        if (page instanceof IVdbEditorPage) {
            ((IVdbEditorPage)page).updateReadOnlyState(getReadonlyState());
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        boolean started = ModelerCore.startTxn(false, false, "Save VDB Context", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            saveAll(monitor);
            succeeded = true;
        } catch (Exception ex) {
            VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                    // ModelerCore.rollbackTxn();
                }
            }
        }
    }

    private void saveAll( final IProgressMonitor monitor ) {

        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {
                // Update each of the editor pages
                for (final Iterator iter = editors.iterator(); iter.hasNext();) {
                    ((IEditorPart)iter.next()).doSave(monitor);
                }

                // Save the VDB archive file. if the monitor was canceled during the context save operation the
                // actual file was still saved but indexing and validation may not have occurred.
                // handle monitor canceled at the end of this method.
                final IStatus status = context.save(monitor);

                // Show errors if necessary
                if (!status.isOK()) {
                    WidgetUtil.show(status);
                    Util.log(status);
                }

                // Refresh the file since it was modified by the context
                try {
                    final IFileEditorInput input = (IFileEditorInput)getEditorInput();
                    final IFile file = input.getFile();

                    // pass in null for monitor because we don't want an InterruptedException thrown
                    // if the monitor has been cancelled. we still want to refresh since the file has changed.
                    file.refreshLocal(IResource.DEPTH_ZERO, null);
                } catch (final CoreException err) {
                    WidgetUtil.showError(err.getMessage());
                    Util.log(err);
                }

                // Notify framework that save successful. this will update the editor tab to show it is no longer dirty.
                if (!monitor.isCanceled()) {
                    saveCanceled = false;
                    update();
                    vdbWasJustSaved = true;
                } else {
                    // metadata VDB context currently does not handle if the monitor is canceled.
                    // so until this gets fixed it has been completely saved. add an update to indicate the VDB was successfully
                    // saved.
                    // see defect 14549
                    update();

                    // canceled. throw this to let downstream operations (like auto-build) know that
                    // the monitor has been canceled.
                    saveCanceled = true;
                    // temporary code until metadata VDB context handles the monitor being canceled. this will let the isDirty()
                    // method work correctly.
                    // see defect 14549
                    saveCanceled = false;
                    // this exception actually manifests itself as an InterruptedException and is caught
                    // in the catch block of the operation.run(monitor) below.
                    throw new OperationCanceledException(Util.getString(I18N_PREFIX + "saveCanceled", //$NON-NLS-1$
                                                                        new Object[] {getEditorInput().getName()}));
                }
            }
        };

        try {
            operation.run(monitor);
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                throw new OperationCanceledException(ex.getLocalizedMessage());
            } else if (ex instanceof InvocationTargetException) {
                handleSaveException(((InvocationTargetException)ex).getTargetException(), monitor);
            } else {
                handleSaveException(ex, monitor);
            }
        }
    }

    private void handleSaveException( Throwable e,
                                      IProgressMonitor monitor ) {
        String message = Util.getString("VdbEditor.saveError", getEditorInput().getName()); //$NON-NLS-1$
        Util.log(IStatus.ERROR, e, message);

        String title = getString("saveErrorTitle"); //$NON-NLS-1$
        MessageDialog.openError(getSite().getShell(), title, message);

        if (monitor != null) monitor.setCanceled(true);

    }

    /**
     * <p>
     * Does nothing.
     * </p>
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.0
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * <p>
     * Does nothing.
     * </p>
     * 
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * Sets the tab for this editor
     * 
     * @param tabId
     * @since 5.0
     */
    public void setTab( final String tabId ) {
        for (int i = 0; i < editors.size(); ++i) {
            if (((IEditorPart)editors.get(i)).getTitle().equals(tabId)) {
                super.setActivePage(i);
                return;
            }
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.0
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @since 4.2
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IFindReplaceTarget.class)) {
            return getActiveEditor().getAdapter(adapter);
        }

        if (adapter.equals(IPropertySheetPage.class)) {
            if (propertySourceProvider == null) {
                propertySourceProvider = new ModelReferencePropertySourceProvider(this.context);
            }
            return propertySourceProvider.getPropertySheetPage();
        }

        if (adapter.equals(IUndoManager.class)) {
            return getActiveEditor().getAdapter(adapter);
        }

        return super.getAdapter(adapter);
    }

    // ============================================================================================================================
    // Overridden Methods

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#addPage(org.eclipse.ui.IEditorPart, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public int addPage( final IEditorPart editor,
                        final IEditorInput input ) throws PartInitException {
        final int ndx = super.addPage(editor, input);
        setPageText(ndx, editor.getTitle());
        this.editors.add(editor);
        return ndx;
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        // loop through pages and check for IVdbEditorPage
        for (int size = this.editors.size(), i = 0; i < size; ++i) {
            if (this.editors.get(i) instanceof IVdbEditorPage) {
                IVdbEditorPage page = (IVdbEditorPage)this.editors.get(i);

                // unregister selection listener
                ISelectionListener handler = page.getSelectionListener();

                if (handler != null) {
                    getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(handler);
                }

                // alert page of dispose
                page.preDispose();
            }
        }

        try {
            context.removeVetoableChangeListener(closeVetoer);
            this.context.close();
        } catch (final IOException err) {
            Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        // unregister undo/redo actions
        IActionBars bars = getEditorSite().getActionBars();
        IMenuManager editMenu = bars.getMenuManager().findMenuUsingPath(ModelerActionBarIdManager.getEditMenuId());
        editMenu.removeMenuListener(undoAction);
        editMenu.removeMenuListener(redoAction);

        super.dispose();
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        // Let's make sure the vdb's project is a model project

        // get the ModelResource.
        IProject project = ((IFileEditorInput)input).getFile().getProject();

        try {
            if (project != null && project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                String message = Util.getString("VdbEditor.modelProjectError", input.getName()); //$NON-NLS-1$
                throw new PartInitException(message);
            }
        } catch (CoreException ex) {
            // Util.log(ex);
            if (ex instanceof PartInitException) throw (PartInitException)ex;

            throw new PartInitException(ex.getLocalizedMessage(), ex);
        }

        super.init(site, input);
        this.editors = new ArrayList();
        final IFile file = ((IFileEditorInput)input).getFile();

        setCurrentTimeStamp(file.getModificationStamp());

        // If the resource has a VDB file extension but with the wrong case then throw exception (defect 17709)
        final String fileExtension = file.getFileExtension();
        if (!fileExtension.equals(ModelUtil.EXTENSION_VDB) && fileExtension.equalsIgnoreCase(ModelUtil.EXTENSION_VDB)) {
            final String fileName = file.getName();
            final int endIndex = fileName.length() - fileExtension.length();
            final String expectedFileName = fileName.substring(0, endIndex) + fileExtension.toLowerCase();

            final Object[] params = new Object[] {fileName, expectedFileName};
            final String msg = VdbUiConstants.Util.getString("VdbEditor.file_extension_not_correct_case_please_rename_file", params); //$NON-NLS-1$
            throw new PartInitException(msg);
        }

        setPartName(file.getName());
        try {
            this.context = VdbEditPlugin.createVdbEditingContext(file.getRawLocation());
            openContextInTransaction();
        } catch (final Exception err) {
            Util.log(err);
            throw new PartInitException(err.getLocalizedMessage(), err);
        }

        site.setSelectionProvider(new SelectionProvider());

        this.context.addChangeListener(new IChangeListener() {
            public void stateChanged( IChangeNotifier theSource ) {
                handleContextChanged();
            }
        });

        // wire up undo/redo actions using the global Model Editor actions
        this.undoAction = new UndoAction();
        this.redoAction = new RedoAction();

        IActionBars bars = site.getActionBars();
        bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), this.undoAction);
        bars.setGlobalActionHandler(ActionFactory.REDO.getId(), this.redoAction);

        IMenuManager editMenu = bars.getMenuManager().findMenuUsingPath(ModelerActionBarIdManager.getEditMenuId());
        editMenu.addMenuListener(undoAction);
        editMenu.addMenuListener(redoAction);
    }

    private void openContextInTransaction() {
        boolean started = ModelerCore.startTxn(false, false, "Open VDB Context", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            this.context.open();
            succeeded = true;
        } catch (Exception e) {
            VdbUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                    // ModelerCore.rollbackTxn();
                }
            }
        }
    }

    void handleContextChanged() {
        if (this.context.isSaveRequired()) {
            update();
        }
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
     * @since 4.2
     */
    @Override
    public boolean isDirty() {
        return this.saveCanceled || super.isDirty();
    }

    // ============================================================================================================================
    // Property Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public VdbEditingContext getContext() {
        return this.context;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    VirtualDatabase getVirtualDatabase() {
        return this.context.getVirtualDatabase();
    }

    // ============================================================================================================================
    // MVC Controller Methods

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.0
     */
    public void resourceChanged( final IResourceChangeEvent event ) {
        switch (event.getType()) {
            // Close editor if project closed
            case IResourceChangeEvent.PRE_CLOSE: {
                final IResource project = event.getResource();
                if (project != null && project.equals(this.modelProject)) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            UiUtil.getWorkbenchPage().closeEditor(VdbEditor.this, true);
                        }
                    });
                }
                break;
            }
            case IResourceChangeEvent.POST_CHANGE: {
                final IResourceDelta delta = event.getDelta();
                final IFile file = ((IFileEditorInput)getEditorInput()).getFile();
                if (!vdbWasJustSaved) {
                    if (fileChanged(file, delta)) {
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                postChange(file);
                            }
                        });
                    }
                } else {
                    setCurrentTimeStamp(((IFileEditorInput)getEditorInput()).getFile().getModificationStamp());
                    problemPage.refreshViewer();
                    // Fixes Thread access error
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            overviewPage.update();
                        }
                    });

                }

                break;
            }
        }
        vdbWasJustSaved = false;
    }

    void postChange( IFile file ) {

        if (!file.isAccessible()) {
            UiUtil.getWorkbenchPage().closeEditor(VdbEditor.this, false);
        } else {
            // set focus back on the active editor to give it an opportunity to refresh state
            IEditorPart part = getActiveEditor();

            if (part != null) {
                part.setFocus();
            }
        }

        // fix for Defect 17061
        if (fileIsVdb(file)) {
            doRevertToSaved();
            problemPage.refreshViewer();
            overviewPage.update();
        }
    }

    /**
     * Re-read file from disk
     */
    public void doRevertToSaved() {
        if (!vdbTimeStampDifferent()) return;

        // try to refresh contents:
        boolean started = ModelerCore.startTxn(false, false, "Refresh VDB Context", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            InternalVdbEditingContext internalContext = ((InternalVdbEditingContext)context);
            internalContext.close(false, false, false);
            this.context.open();
            // notify anyone who cares that we just did a refresh:
            internalContext.fireStateChanged();
            succeeded = true;
        } catch (Exception ex) {
            VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                    // ModelerCore.rollbackTxn();
                }
            }
        }

        for (Iterator iter = this.editors.iterator(); iter.hasNext();) {
            Object element = iter.next(); // these are all actually IEditorParts, too
            if (element instanceof IRevertable) {
                IRevertable r = (IRevertable)element;
                r.doRevertToSaved();
            } // endif -- instance
        } // endfor

        overviewPage.update();

        update(); // make sure the editor is marked not dirty.
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private boolean fileChanged( final IFile file,
                                 final IResourceDelta delta ) {
        // related to defect 17599 - we weren't using the IResourceDelta correctly
        IResourceDelta rscDelta = delta.findMember(file.getFullPath());
        return rscDelta != null && rscDelta.getKind() == IResourceDelta.CHANGED;
    }

    /**
     * @since 4.0
     */
    void setModified() {
        getContext().setModified();
        update();
    }

    boolean fileIsVdb( final IFile file ) {
        IFile vdbFile = ((IFileEditorInput)getEditorInput()).getFile();
        if (vdbFile.equals(file)) return true;

        return false;
    }

    private boolean vdbTimeStampDifferent() {
        long newModStamp = ((IFileEditorInput)getEditorInput()).getFile().getModificationStamp();
        long currentTimeStamp = getCurrentTimeStamp();
        long diff = newModStamp - currentTimeStamp;
        if (Math.abs(diff) > 0) {
            setCurrentTimeStamp(newModStamp);
            return true;
        }

        return false;
    }

    /**
     * @since 4.0
     */
    void update() {
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * @return Returns the currentTimeStamp.
     * @since 4.3
     */
    public long getCurrentTimeStamp() {
        return this.currentTimeStamp;
    }

    /**
     * @param currentTimeStamp The currentTimeStamp to set.
     * @since 4.3
     */
    public void setCurrentTimeStamp( long currentTimeStamp ) {
        this.currentTimeStamp = currentTimeStamp;
    }

    /**
     * Public method used to determine if the context is open or not. This helps prevent a possible IllegalStateExceptions from
     * listeners who are told the VDB resource is changed (Defect 22305)
     */
    public boolean isVdbContextOpen() {
        return context != null && context.isOpen();
    }

    /**
     * Public method used outside the VDB Editor (i.e. SynchronizeVdbAction). It insures that the context being used is the same
     * one as the open editor and that the sync process is identical, including the save (Defect 22305)
     * 
     * @param autoSave
     */
    public void synchronizeVdb( boolean autoSave ) {
        if (overviewPage != null) {
            overviewPage.synchronizeVdb(autoSave);
        }
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#isEditorInputModifiable()
     * @since 5.5.3
     */
    public boolean isEditorInputModifiable() {
        return !getReadonlyState();
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#validateEditorInputState()
     * @since 5.5.3
     */
    public boolean validateEditorInputState() {
        return false;
    }
}

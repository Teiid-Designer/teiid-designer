/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

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
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.actions.RedoAction;
import com.metamatrix.modeler.internal.ui.actions.UndoAction;
import com.metamatrix.modeler.internal.vdb.ui.properties.ModelEntryPropertySourceProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.0
 */
public final class VdbEditor extends MultiPageEditorPart
    implements IResourceChangeListener, VdbUiConstants, VdbUiConstants.ExtensionPoints, IRevertable, IGotoMarker,
    ITextEditorExtension2 {

    static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditor.class);

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

    Vdb vdb;

    private IResource modelProject;
    ArrayList editors;
    private ModelEntryPropertySourceProvider propertySourceProvider;
    boolean saveCanceled = false;
    VdbEditorOverviewPage overviewPage;
    VdbEditorProblemPage problemPage;
    private VdbEditorUserFilesPage userFilesPage;
    private VdbEditorWsdlPage wsdlPage;
    boolean vdbWasJustSaved = false;

    private long currentTimeStamp = 0;
    private UndoAction undoAction;

    private RedoAction redoAction;

    /**
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
     * Add any editor pages contributed by extensions.
     * 
     * @since 4.3
     */
    protected void createAdditionalPages( final int theStartingIndex,
                                          final IEditorInput theInput ) {
        // get the VDB Editor Page extension point from the plugin class
        final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, VdbEditorPage.ID);

        // get the all extensions to this extension point
        final IExtension[] extensions = extensionPoint.getExtensions();

        // if no extensions no work to do
        if (extensions.length == 0) return;

        // make executable extensions for every CLASS_NAME
        for (int i = 0; i < extensions.length; ++i) {
            final IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            for (int j = 0; j < elements.length; ++j)
                try {
                    final Object extension = elements[j].createExecutableExtension(VdbEditorPage.CLASS_NAME);

                    if (extension instanceof IVdbEditorPage) {
                        final IVdbEditorPage page = (IVdbEditorPage)extension;
                        page.setVdb(this.vdb);

                        // add the page
                        final int pageNum = addPage(page, theInput);

                        // get additional extension info
                        final String displayName = elements[j].getAttribute(VdbEditorPage.DISPLAY_NAME);
                        // String order = elements[j].getAttribute(VdbEditorPage.ORDER);

                        // call interface methods
                        page.setDisplayName(displayName);
                        setPageText(pageNum, displayName);
                        page.updateReadOnlyState(getReadonlyState());

                        final ISelectionListener handler = page.getSelectionListener();

                        if (handler != null) getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(handler);
                    } else Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "pageExtensionIncorrectClass", //$NON-NLS-1$
                                                                  extension.getClass().getName()));
                } catch (final Exception theException) {
                    // problem initializing the VDB Editor Page
                    final String msg = Util.getString(I18N_PREFIX + "editorPageInitializationError", //$NON-NLS-1$
                                                      elements[j].getAttribute(VdbEditorPage.CLASS_NAME));
                    Util.log(IStatus.ERROR, theException, msg);
                }
        }
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     * @since 4.0
     */
    @Override
    protected void createPages() {
        createPagesInTransaction();
    }

    private void createPagesInTransaction() {
        final boolean started = ModelerCore.startTxn(false, false, "Open VDB Editor", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            final IEditorInput input = getEditorInput();
            overviewPage = new VdbEditorOverviewPage(this);
            addPage(overviewPage, input);
            problemPage = new VdbEditorProblemPage(this);
            addPage(problemPage, input);
            userFilesPage = new VdbEditorUserFilesPage(this);
            addPage(userFilesPage, input);
            wsdlPage = new VdbEditorWsdlPage(this);
            final int index = addPage(wsdlPage, input);

            // add additional pages contributed by extensions
            createAdditionalPages(index + 1, input);

            this.modelProject = ((IFileEditorInput)input).getFile().getProject();
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
            succeeded = true;
        } catch (final PartInitException err) {
            WidgetUtil.showError(err.getMessage());
            Util.log(err);
        } catch (final Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        } finally {
            if (started) if (succeeded) ModelerCore.commitTxn();
            else {
                // We don't want to roll this back. Not really changing anything in any model
                // ModelerCore.rollbackTxn();
            }
        }
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        // loop through pages and check for IVdbEditorPage
        for (int size = this.editors.size(), i = 0; i < size; ++i)
            if (this.editors.get(i) instanceof IVdbEditorPage) {
                final IVdbEditorPage page = (IVdbEditorPage)this.editors.get(i);

                // unregister selection listener
                final ISelectionListener handler = page.getSelectionListener();

                if (handler != null) getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(handler);

                // alert page of dispose
                page.preDispose();
            }

        try {
            this.vdb.close();
        } catch (final Exception err) {
            Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);

        // unregister undo/redo actions
        final IActionBars bars = getEditorSite().getActionBars();
        final IMenuManager editMenu = bars.getMenuManager().findMenuUsingPath(ModelerActionBarIdManager.getEditMenuId());
        editMenu.removeMenuListener(undoAction);
        editMenu.removeMenuListener(redoAction);

        super.dispose();
    }

    /**
     * Re-read file from disk
     */
    // TODO: No undo/redo capability
    public void doRevertToSaved() {
        if (!vdbTimeStampDifferent()) return;

        // try to refresh contents:
        try {
            vdb.close();
        } catch (final Exception error) {
            Util.log(error);
            WidgetUtil.showError(error.getLocalizedMessage());
        }

        for (final Iterator iter = this.editors.iterator(); iter.hasNext();) {
            final Object element = iter.next(); // these are all actually IEditorParts, too
            if (element instanceof IRevertable) {
                final IRevertable r = (IRevertable)element;
                r.doRevertToSaved();
            } // endif -- instance
        } // endfor

        overviewPage.update();

        update(); // make sure the editor is marked not dirty.
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        final boolean started = ModelerCore.startTxn(false, false, "Save VDB Context", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            saveAll(monitor);
            succeeded = true;
        } catch (final Exception ex) {
            VdbUiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
        } finally {
            if (started) if (succeeded) ModelerCore.commitTxn();
            else {
                // We don't want to roll this back. Not really changing anything in any model
                // ModelerCore.rollbackTxn();
            }
        }
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.0
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * @since 4.0
     */
    private boolean fileChanged( final IFile file,
                                 final IResourceDelta delta ) {
        // related to defect 17599 - we weren't using the IResourceDelta correctly
        final IResourceDelta rscDelta = delta.findMember(file.getFullPath());
        return rscDelta != null && rscDelta.getKind() == IResourceDelta.CHANGED;
    }

    boolean fileIsVdb( final IFile file ) {
        final IFile vdbFile = ((IFileEditorInput)getEditorInput()).getFile();
        if (vdbFile.equals(file)) return true;

        return false;
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @since 4.2
     */
    @Override
    public Object getAdapter( final Class adapter ) {
        if (adapter.equals(IFindReplaceTarget.class)) return getActiveEditor().getAdapter(adapter);

        if (adapter.equals(IPropertySheetPage.class)) {
            if (propertySourceProvider == null) propertySourceProvider = new ModelEntryPropertySourceProvider();
            return propertySourceProvider.getPropertySheetPage();
        }

        if (adapter.equals(IUndoManager.class)) return getActiveEditor().getAdapter(adapter);

        return super.getAdapter(adapter);
    }

    /**
     * @return Returns the currentTimeStamp.
     * @since 4.3
     */
    public long getCurrentTimeStamp() {
        return this.currentTimeStamp;
    }

    /**
     * Obtains the read-only state of the VDB editor.
     * 
     * @return <code>true</code> if read-only; <code>false</code> otherwise.
     * @since 4.3
     */
    private boolean getReadonlyState() {
        boolean result = true;

        final IEditorInput input = getEditorInput();

        if ((input != null) && (input instanceof IFileEditorInput)) result = ((IFileEditorInput)input).getFile().isReadOnly();

        return result;
    }

    /**
     * @since 4.0
     */
    public Vdb getVdb() {
        return this.vdb;
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    void handleContextChanged() {
        if (this.vdb.isModified()) update();
    }

    private void handleSaveException( final Throwable e,
                                      final IProgressMonitor monitor ) {
        final String message = Util.getString("VdbEditor.saveError", getEditorInput().getName()); //$NON-NLS-1$
        Util.log(IStatus.ERROR, e, message);

        final String title = getString("saveErrorTitle"); //$NON-NLS-1$
        MessageDialog.openError(getSite().getShell(), title, message);

        if (monitor != null) monitor.setCanceled(true);

    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        // Let's make sure the vdb's project is a model project

        // get the ModelResource.
        final IProject project = ((IFileEditorInput)input).getFile().getProject();

        try {
            if (project != null && project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                final String message = Util.getString("VdbEditor.modelProjectError", input.getName()); //$NON-NLS-1$
                throw new PartInitException(message);
            }
        } catch (final CoreException ex) {
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
            vdb = new Vdb(file.getFullPath());
        } catch (final Exception err) {
            Util.log(err);
            throw new PartInitException(err.getLocalizedMessage(), err);
        }

        site.setSelectionProvider(new SelectionProvider());

        this.vdb.addChangeListener(new IChangeListener() {
            public void stateChanged( final IChangeNotifier theSource ) {
                handleContextChanged();
            }
        });

        // wire up undo/redo actions using the global Model Editor actions
        this.undoAction = new UndoAction();
        this.redoAction = new RedoAction();

        final IActionBars bars = site.getActionBars();
        bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), this.undoAction);
        bars.setGlobalActionHandler(ActionFactory.REDO.getId(), this.redoAction);

        final IMenuManager editMenu = bars.getMenuManager().findMenuUsingPath(ModelerActionBarIdManager.getEditMenuId());
        editMenu.addMenuListener(undoAction);
        editMenu.addMenuListener(redoAction);
    }

    /**
     * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
     * @since 4.2
     */
    @Override
    public boolean isDirty() {
        return this.saveCanceled || super.isDirty();
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#isEditorInputModifiable()
     * @since 5.5.3
     */
    public boolean isEditorInputModifiable() {
        return !getReadonlyState();
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.0
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    void postChange( final IFile file ) {

        if (!file.isAccessible()) UiUtil.getWorkbenchPage().closeEditor(VdbEditor.this, false);
        else {
            // set focus back on the active editor to give it an opportunity to refresh state
            final IEditorPart part = getActiveEditor();

            if (part != null) part.setFocus();
        }

        // fix for Defect 17061
        if (fileIsVdb(file)) {
            doRevertToSaved();
            problemPage.refreshViewer();
            overviewPage.update();
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.0
     */
    public void resourceChanged( final IResourceChangeEvent event ) {
        switch (event.getType()) {
            // Close editor if project closed
            case IResourceChangeEvent.PRE_CLOSE: {
                final IResource project = event.getResource();
                if (project != null && project.equals(this.modelProject)) Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        UiUtil.getWorkbenchPage().closeEditor(VdbEditor.this, true);
                    }
                });
                break;
            }
            case IResourceChangeEvent.POST_CHANGE: {
                final IResourceDelta delta = event.getDelta();
                final IFile file = ((IFileEditorInput)getEditorInput()).getFile();
                if (!vdbWasJustSaved) {
                    if (fileChanged(file, delta)) Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            postChange(file);
                        }
                    });
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

    private void saveAll( final IProgressMonitor monitor ) {

        final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor monitor ) {
                // Update each of the editor pages
                for (final Iterator iter = editors.iterator(); iter.hasNext();)
                    ((IEditorPart)iter.next()).doSave(monitor);

                // Save the VDB archive file. if the monitor was canceled during the context save operation the
                // actual file was still saved but indexing and validation may not have occurred.
                // handle monitor canceled at the end of this method.
                final IStatus status = vdb.save(monitor);

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
        } catch (final Exception ex) {
            if (ex instanceof InterruptedException) throw new OperationCanceledException(ex.getLocalizedMessage());
            else if (ex instanceof InvocationTargetException) handleSaveException(((InvocationTargetException)ex).getTargetException(),
                                                                                  monitor);
            else handleSaveException(ex, monitor);
        }
    }

    /**
     * @param currentTimeStamp The currentTimeStamp to set.
     * @since 4.3
     */
    public void setCurrentTimeStamp( final long currentTimeStamp ) {
        this.currentTimeStamp = currentTimeStamp;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();

        final IEditorPart page = getActiveEditor();

        // inform IVdbEditorPages of readonly state
        if (page instanceof IVdbEditorPage) ((IVdbEditorPage)page).updateReadOnlyState(getReadonlyState());
    }

    /**
     * Sets the tab for this editor
     * 
     * @param tabId
     * @since 5.0
     */
    public void setTab( final String tabId ) {
        for (int i = 0; i < editors.size(); ++i)
            if (((IEditorPart)editors.get(i)).getTitle().equals(tabId)) {
                super.setActivePage(i);
                return;
            }
    }

    /**
     * Public method used outside the VDB Editor (i.e. SynchronizeVdbAction). It insures that the context being used is the same one
     * as the open editor and that the sync process is identical, including the save (Defect 22305)
     * 
     * @param autoSave
     */
    public void synchronizeVdb( final boolean autoSave ) {
        if (overviewPage != null) overviewPage.synchronizeVdb(autoSave);
    }

    /**
     * @since 4.0
     */
    void update() {
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#validateEditorInputState()
     * @since 5.5.3
     */
    public boolean validateEditorInputState() {
        return false;
    }

    private boolean vdbTimeStampDifferent() {
        final long newModStamp = ((IFileEditorInput)getEditorInput()).getFile().getModificationStamp();
        final long currentTimeStamp = getCurrentTimeStamp();
        final long diff = newModStamp - currentTimeStamp;
        if (Math.abs(diff) > 0) {
            setCurrentTimeStamp(newModStamp);
            return true;
        }

        return false;
    }

    interface Constants {
        String INVALID_INPUT_MESSAGE = getString("invalidInputMessage"); //$NON-NLS-1$
    }
}

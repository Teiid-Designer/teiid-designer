/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.registry.RegistryEvent;
import org.teiid.designer.extension.registry.RegistryListener;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;
import org.teiid.designer.extension.ui.actions.RegistryDeploymentValidator;
import org.teiid.designer.extension.ui.actions.ShowModelExtensionRegistryViewAction;

import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.ui.forms.MessageFormDialog;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends SharedHeaderFormEditor implements IPersistableEditor,
        IResourceChangeListener, PropertyChangeListener, RegistryListener {

    /**
     * The memento key for the index of the selected editor.
     */
    private static final String SELECTED_PAGE = "SELECTED_PAGE"; //$NON-NLS-1$

    private boolean dirty = false;
    private boolean readOnly = false;

    private IMemento memento;

    private final FileDocumentProvider documentProvider = new FileDocumentProvider();
    private long modificationStamp = 0;
    private Listener refreshListener;

    private ModelExtensionDefinition originalMed;
    private ModelExtensionDefinition medBeingEdited;
    private MedSelectionSynchronizer selectionSynchronizer;

    private ScrolledForm scrolledForm;
    private final Map<MedEditorPage, Integer> medEditorPages = new HashMap<MedEditorPage, Integer>(3);

    private IAction showRegistryViewAction;

    private IAction updateRegisteryAction;

    private MedOutlinePage contentOutlinePage;

    /**
     * Allow inner classes access to the <code>MedEditorPage</code>s.
     * 
     * @return the <code>MedEditorPage</code>s (never <code>null</code>)
     */
    Map<MedEditorPage, Integer> accessMedEditorPages() {
        return this.medEditorPages;
    }

    /**
     * Allow inner classes access to the outer class.
     * 
     * @return the outer class instance
     */
    ModelExtensionDefinitionEditor accessThis() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages() {
        int pageNum = 0;

        try {
            // Page 1: overview editor
            MedEditorPage page = new OverviewEditorPage(this);
            addPage(pageNum, page);
            this.medEditorPages.put(page, pageNum);

            // Page 2: properties editor
            ++pageNum;
            page = new PropertiesEditorPage(this);
            addPage(pageNum, page);
            this.medEditorPages.put(page, pageNum);

            // Page 3: readonly text editor
            ++pageNum;
            page = new SourceEditorPage(this);
            addPage(pageNum, page);
            this.medEditorPages.put(page, pageNum);

            // initialize header text to first page
            this.scrolledForm.setText(getPageText(0));

            // handle page changes
            addPageChangedListener(new IPageChangedListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
                 */
                @Override
                public void pageChanged( PageChangedEvent event ) {
                    handlePageChanged();
                }
            });

            this.refreshListener = new Listener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                 */
                @Override
                public void handleEvent( Event event ) {
                    refreshMed();
                }
            };

            // hook activation listener
            getContainer().addListener(SWT.Activate, refreshListener);

            // restore state
            int selectedPageNum = 0;

            if (this.memento != null) {
                int value = this.memento.getInteger(SELECTED_PAGE);

                if (value != -1) {
                    selectedPageNum = value;
                }
            }

            setActivePage(selectedPageNum);
        } catch (Exception e) {
            // this will open a "Could not open editor" page with exception details
            throw new RuntimeException(Messages.errorOpeningMedEditor, e);
        }
    }

    private void contributeToMenu( IMenuManager menuMgr ) {
        menuMgr.add(this.updateRegisteryAction);
        menuMgr.add(this.showRegistryViewAction);
        menuMgr.update(true);
    }

    private void contributeToToolBar( IToolBarManager toolBarMgr ) {
        toolBarMgr.add(this.updateRegisteryAction);
        toolBarMgr.add(this.showRegistryViewAction);
        toolBarMgr.update(true);
    }

    private void createActions() {
        this.updateRegisteryAction = new Action(Messages.updateMedInRegistryActionText, SWT.FLAT) {
            @Override
            public void run() {
                IEditorInput editorInput = getEditorInput();
                IFile medFile = null;
                if (editorInput instanceof IFileEditorInput) {
                    medFile = ((IFileEditorInput)editorInput).getFile();
                }

                // If editor is not saved, inform user to save first.
                if (isDirty()) {
                    MessageDialog.openWarning(getShell(), Messages.registerMedActionEditorDirtyTitle,
                                              Messages.registerMedActionEditorDirtyMsg);
                    return;
                }

                // If the file has any error markers, user is informed to fix them first
                if (RegistryDeploymentValidator.checkProblemMarkers(medFile)) {
                    return;
                }

                // -------------------------------------------------
                // Do some validation checks before registering.
                // -------------------------------------------------
                ModelExtensionRegistry registry = (Platform.isRunning() ? getRegistry() : null);
                boolean wasAdded = RegistryDeploymentValidator.doDeployment(registry, medFile);
                if (wasAdded) {
                    MessageDialog.openInformation(getShell(), Messages.registerMedActionSuccessTitle,
                                                  Messages.registerMedActionSuccessMsg);
                }
            }
        };
        this.updateRegisteryAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
        this.updateRegisteryAction.setToolTipText(Messages.updateMedInRegistryActionToolTip);

        this.showRegistryViewAction = new ShowModelExtensionRegistryViewAction();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#createHeaderContents(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createHeaderContents( IManagedForm headerForm ) {
        this.scrolledForm = headerForm.getForm();
        this.scrolledForm.setImage(Activator.getDefault().getImage(MED_EDITOR));

        Form form = this.scrolledForm.getForm();
        getToolkit().decorateFormHeading(form);

        createActions();
        contributeToToolBar(form.getToolBarManager());
        contributeToMenu(form.getMenuManager());
    }

    private void createMed() throws Exception {
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(ExtensionPlugin.getInstance().getMedSchema());
        this.originalMed = parser.parse(getFile().getContents(), ExtensionPlugin.getInstance()
                                                                                .createDefaultModelObjectExtensionAssistant());

        // process parsing errors
        Collection<String> fatals = parser.getFatalErrors();

        if ((fatals != null) && !fatals.isEmpty()) {
            throw new RuntimeException(fatals.iterator().next());
        }

        // unhook listening to current MED being edited
        if (this.medBeingEdited != null) {
            this.medBeingEdited.removeListener(this);
        }

        // copy over to MED being edited
        this.medBeingEdited = new ModelExtensionDefinition(ExtensionPlugin.getInstance()
                                                                          .createDefaultModelObjectExtensionAssistant());
        this.medBeingEdited.setDescription(this.originalMed.getDescription());
        this.medBeingEdited.setMetamodelUri(this.originalMed.getMetamodelUri());
        this.medBeingEdited.setNamespacePrefix(this.originalMed.getNamespacePrefix());
        this.medBeingEdited.setNamespaceUri(this.originalMed.getNamespaceUri());
        this.medBeingEdited.setVersion(this.originalMed.getVersion());

        // clone properties but use a different namespace provider
        for (String metaclassName : this.originalMed.getExtendedMetaclasses()) {
            this.medBeingEdited.addMetaclass(metaclassName);

            for (ModelExtensionPropertyDefinition propDefn : this.originalMed.getPropertyDefinitions(metaclassName)) {
                ModelExtensionPropertyDefinition copy = (ModelExtensionPropertyDefinition)propDefn.clone();
                copy.setNamespacePrefixProvider(this.medBeingEdited);
                this.medBeingEdited.addPropertyDefinition(metaclassName, copy);
            }
        }

        // hook selection synchronizer
        if (this.selectionSynchronizer == null) {
            this.selectionSynchronizer = new MedSelectionSynchronizer(this);
        } else {
            this.selectionSynchronizer.setMed(this.medBeingEdited);
        }

        // register to receive property change events
        this.medBeingEdited.addListener(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        super.init(site, input);
        assert (input instanceof IFileEditorInput) : "MED Editor input is not a file"; //$NON-NLS-1$

        try {
            createMed();
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        } catch (Exception e) {
            throw new PartInitException(Messages.errorOpeningMedEditor, e);
        }
    }

    private void internalSave( IProgressMonitor progressMonitor ) {
        IEditorInput input = getEditorInput();

        try {
            ModelExtensionDefinitionWriter writer = new ModelExtensionDefinitionWriter();
            String medAsString = writer.writeAsText(this.medBeingEdited);
            IDocument document = this.documentProvider.getDocument(input);
            document.set(medAsString);

            this.documentProvider.aboutToChange(input);
            this.documentProvider.saveDocument(progressMonitor, input, document, true);
            this.modificationStamp = this.documentProvider.getModificationStamp(input);

            // create new original MED that that will then be copied over to the MED being edited
            createMed();
        } catch (Exception e) {
            IStatus status = null;

            if (!(e instanceof CoreException)) {
                status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, e.getLocalizedMessage());
            } else {
                status = ((CoreException)e).getStatus();
            }

            if ((status == null) || (status.getSeverity() != IStatus.CANCEL)) {
                ErrorDialog.openError(getShell(), Messages.errorDialogTitle, Messages.medEditorSaveError, status);
            }
        } finally {
            this.documentProvider.changed(input);

            // update dirty flag
            refreshDirtyState();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#dispose()
     */
    @Override
    public void dispose() {
        getRegistry().removeListener(this); // unregister to receive registry events
        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor progressMonitor ) {
        internalSave(progressMonitor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        IProgressMonitor progressMonitor = getProgressMonitor();
        SaveAsDialog dialog = new SaveAsDialog(getShell());
        dialog.setOriginalFile(getFile());
        dialog.create();

        // dialog was canceled
        if (dialog.open() == Window.CANCEL) {
            if (progressMonitor != null) {
                progressMonitor.setCanceled(true);
            }

            return;
        }

        // dialog OK'd
        IPath filePath = dialog.getResult();

        // make sure that file has the right extension
        if (!ExtensionConstants.MED_EXTENSION.equals(filePath.getFileExtension())) {
            filePath = filePath.addFileExtension(ExtensionConstants.MED_EXTENSION);
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IFile file = workspace.getRoot().getFile(filePath);

        try {
            // create set new editor input file
            InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
            file.create(emptyStream, true, progressMonitor);
            setInput(new FileEditorInput(file));

            // save MED in new file
            internalSave(progressMonitor);
        } catch (Exception e) {
            IStatus status = null;

            if (!(e instanceof CoreException)) {
                status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, e.getLocalizedMessage());
            } else {
                status = ((CoreException)e).getStatus();
            }

            ErrorDialog.openError(getShell(), Messages.errorDialogTitle, Messages.medEditorSaveError, status);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IContentOutlinePage.class)) {
            if (contentOutlinePage == null) {
                contentOutlinePage = new MedOutlinePage(this);
            }
            return contentOutlinePage;
        }

        return super.getAdapter(adapter);
    }

    /**
     * @return the *.mxd resource (never <code>null</code>)
     */
    protected IFile getFile() {
        return ((IFileEditorInput)getEditorInput()).getFile();
    }

    /**
     * @return the model extension definition being edited (never <code>null</code>)
     */
    ModelExtensionDefinition getMed() {
        return this.medBeingEdited;
    }

    /**
     * @return the form editor's message manager (never <code>null</code>)
     */
    IMessageManager getMessageManager() {
        return this.scrolledForm.getMessageManager();
    }

    private IProgressMonitor getProgressMonitor() {
        IStatusLineManager statusLineMgr = getEditorSite().getActionBars().getStatusLineManager();
        return ((statusLineMgr == null) ? null : statusLineMgr.getProgressMonitor());
    }

    /**
     * @return the registry (never <code>null</code>)
     */
    ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

    /**
     * @return the editor's shell (never <code>null</code>)
     */
    Shell getShell() {
        return getEditorSite().getShell();
    }

    public MedSelectionSynchronizer getSelectionSynchronizer() {
        return this.selectionSynchronizer;
    }

    void handlePageChanged() {
        FormPage page = (FormPage)getSelectedPage();
        this.scrolledForm.setText(page.getTitle());
        page.setFocus();
    }

    private void hookRefreshListener() {
        getContainer().addListener(SWT.Activate, this.refreshListener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * @return <code>true</code> if the file is readonly
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * @return <code>true</code> if editor is synchronized with file system
     */
    boolean isSynchronized() {
        long currentModifiedStamp = this.documentProvider.getModificationStamp(getEditorInput());
        return (this.modificationStamp == currentModifiedStamp);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.registry.RegistryListener#process(org.teiid.designer.extension.registry.RegistryEvent)
     */
    @Override
    public void process( RegistryEvent e ) {
        // tell each page to refesh their messages
        getShell().getDisplay().syncExec(new Runnable() {

            /**
             * {@inheritDoc}
             * 
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                if (!getShell().isDisposed()) {
                    for (MedEditorPage medEditorPage : accessMedEditorPages().keySet()) {
                        medEditorPage.updateAllMessages();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public final void propertyChange( PropertyChangeEvent e ) {
        refreshDirtyState();

        // pass event on to medEditorPages
        for (MedEditorPage page : this.medEditorPages.keySet()) {
            page.handlePropertyChanged(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged( final IResourceChangeEvent event ) {
        int type = event.getType();

        if (type == IResourceChangeEvent.POST_CHANGE) {
            IResourceDelta delta = event.getDelta();

            if (delta == null) {
                return;
            }

            try {
                delta.accept(new IResourceDeltaVisitor() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
                     */
                    @Override
                    public boolean visit( IResourceDelta delta ) {
                        if (delta.getResource().equals(getFile())) {
                            // MXD file has been deleted so close editor
                            if ((delta.getKind() & IResourceDelta.REMOVED) != 0) {
                                if (!getShell().isDisposed()) {
                                    getShell().getDisplay().asyncExec(new Runnable() {

                                        /**
                                         * {@inheritDoc}
                                         * 
                                         * @see java.lang.Runnable#run()
                                         */
                                        @Override
                                        public void run() {
                                            getEditorSite().getPage().closeEditor(accessThis(), false);
                                        }
                                    });
                                }
                            } else if (ResourceChangeUtilities.isContentChanged(delta)) {
                                if (!getShell().isDisposed()) {
                                    getShell().getDisplay().syncExec(new Runnable() {

                                        /**
                                         * {@inheritDoc}
                                         * 
                                         * @see java.lang.Runnable#run()
                                         */
                                        @Override
                                        public void run() {
                                            refreshMed();
                                        }
                                    });
                                }
                            }

                            return false; // stop visiting
                        }

                        return true; // keep visiting
                    }
                });
            } catch (CoreException e) {
                UTIL.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

    /**
     * Refreshes the editor's dirty state by comparing the MED being edited with the original MED.
     */
    protected void refreshDirtyState() {
        boolean newValue = !this.originalMed.equals(this.medBeingEdited);

        if (isDirty() != newValue) {
            this.dirty = newValue;
            getHeaderForm().dirtyStateChanged();
        }
    }

    void refreshMed() {
        if (!isSynchronized()) {
            unhookRefreshListener();

            if (MessageFormDialog.openQuestion(getShell(), Messages.medChangedOnFileSystemDialogTitle,
                                               Activator.getDefault().getImage(MED_EDITOR),
                                               NLS.bind(Messages.medChangedOnFileSystemDialogMsg, getFile().getName()))) {
                try {
                    getFile().refreshLocal(IResource.DEPTH_ONE, null);
                    this.modificationStamp = this.documentProvider.getModificationStamp(getEditorInput());

                    createMed();

                    for (MedEditorPage page : this.medEditorPages.keySet()) {
                        page.handleMedReloaded();
                    }
                } catch (Exception e) {
                    UTIL.log(e);
                    MessageFormDialog.openError(getShell(), Messages.medEditorRefreshErrorTitle,
                                                Activator.getDefault().getImage(MED_EDITOR), Messages.medEditorRefreshErrorMsg);
                }
            }

            hookRefreshListener();
        }
    }

    /**
     * Checks the *.mxd file permissions and notifies the editor's pages if the permissions have changed.
     */
    private void refreshReadOnlyState() {
        ResourceAttributes attributes = getFile().getResourceAttributes();
        boolean newValue = ((attributes == null) ? true : attributes.isReadOnly());

        if (isReadOnly() != newValue) {
            this.readOnly = newValue;

            for (MedEditorPage page : this.medEditorPages.keySet()) {
                page.setResourceReadOnly(this.readOnly);
                page.getManagedForm().refresh();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IPersistableEditor#restoreState(org.eclipse.ui.IMemento)
     */
    @Override
    public void restoreState( IMemento memento ) {
        this.memento = memento;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState( IMemento memento ) {
        int selectedPageNum = getActivePage();
        memento.putInteger(SELECTED_PAGE, selectedPageNum);
    }

    /**
     * @param page the page whose tab needs to be activated by the editor (cannot be <code>null</code>)
     */
    public void selectPage(MedEditorPage page) {
        if (getActivePageInstance() != page) {
            setActivePage(this.medEditorPages.get(page));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();
        refreshMed();
        refreshReadOnlyState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput( IEditorInput input ) {
        if (getEditorInput() == null) {
            getRegistry().addListener(this); // register to receive registry events
        } else {
            // unhook previous document provider if necessary
            this.documentProvider.disconnect(getEditorInput());
        }

        if (input instanceof IFileEditorInput) {
            super.setInput(input);

            try {
                // hook new document provider
                this.documentProvider.connect(input);
                this.modificationStamp = this.documentProvider.getModificationStamp(input);
                IAnnotationModel model = this.documentProvider.getAnnotationModel(input);
                model.connect(this.documentProvider.getDocument(input));

                // set editor tab text
                setPartName(getEditorInput().getName());
            } catch (Exception e) {
                throw new RuntimeException(Messages.errorOpeningMedEditor, e);
            }
        } else {
            throw new RuntimeException(Messages.medEditorInputNotAFile);
        }
    }

    private void unhookRefreshListener() {
        getContainer().removeListener(SWT.Activate, this.refreshListener);
    }

}

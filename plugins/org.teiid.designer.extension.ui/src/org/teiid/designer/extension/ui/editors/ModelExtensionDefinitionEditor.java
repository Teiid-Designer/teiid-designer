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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerUtilities;
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
import org.teiid.designer.extension.ui.actions.UpdateRegistryModelExtensionDefinitionAction;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends SharedHeaderFormEditor implements IResourceChangeListener,
        PropertyChangeListener, RegistryListener {

    private boolean dirty = false;
    private boolean readOnly = false;

    private final FileDocumentProvider documentProvider = new FileDocumentProvider();

    private ModelExtensionDefinition originalMed;
    private ModelExtensionDefinition medBeingEdited;

    private MedEditorPage overviewPage;

    private MedEditorPage propertiesPage;
    private ScrolledForm scrolledForm;
    private final Collection<MedEditorPage> pages = new ArrayList<MedEditorPage>(2);

    private IAction showRegistryViewAction;

    private IAction updateRegisteryAction;

    /**
     * Allow inner classes access to the <code>MedEditorPage</code>s.
     * 
     * @return the <code>MedEditorPage</code>s (never <code>null</code>)
     */
    Collection<MedEditorPage> accessMedEditorPages() {
        return this.pages;
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
        // NOTE: pages are added in reverse order
        try {
            // last page is a readonly text editor
            TextEditor sourceEditor = new TextEditor() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isEditable()
                 */
                @Override
                public boolean isEditable() {
                    return false;
                }
            };

            // add text editor
            addPage(0, sourceEditor, getEditorInput());

            // add properties editor
            this.propertiesPage = new PropertiesEditorPage(this);
            addPage(0, this.propertiesPage);
            this.pages.add(this.propertiesPage);

            // add overview editor
            this.overviewPage = new OverviewEditorPage(this);
            addPage(0, this.overviewPage);
            this.pages.add(this.overviewPage);

            // set text editor title and initialize header text to first page
            setPageText((getPageCount() - 1), Messages.medEditorSourcePageTitle);
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

            this.overviewPage.setFocus();
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
                    MessageDialog.openWarning(getShell(),
                                              Messages.registerMedActionEditorDirtyTitle,
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
                InputStream fileContents = null;
                try {
                    fileContents = medFile.getContents();
                } catch (CoreException e) {
                    UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medFileGetContentsErrorMsg, medFile.getName()));
                }

                boolean wasAdded = true;
                boolean isDeployable = false;
                if (fileContents != null) {
                    isDeployable = RegistryDeploymentValidator.checkMedDeployable(registry, fileContents);
                    // If the URI is not registered, go ahead with registration
                    if (isDeployable) {
                        // Add the Extension Definition to the registry
                        try {
                            UpdateRegistryModelExtensionDefinitionAction.addExtensionToRegistry(medFile);
                        } catch (Exception e) {
                            wasAdded = false;
                            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medRegistryAddErrorMsg, medFile.getName()));
                            MessageDialog.openInformation(getShell(),
                                                          Messages.registerMedActionFailedTitle,
                                                          Messages.registerMedActionFailedMsg);
                        }
                        if (wasAdded) {
                            MessageDialog.openInformation(getShell(),
                                                          Messages.registerMedActionSuccessTitle,
                                                          Messages.registerMedActionSuccessMsg);
                        }
                    }
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

    private void createMarker( int severity,
                               String message ) {
        Map attributes = new HashMap();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);

        try {
            MarkerUtilities.createMarker(getFile(), attributes, UiConstants.ExtensionIds.PROBLEM_MARKER);
        } catch (CoreException e) {
            UTIL.log(e);
        }
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

        // create markers
        refreshMarkers(parser.getErrors(), parser.getWarnings(), parser.getInfos());

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
            // TODO implement IContentOutlinePage
            return null;
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

    void handlePageChanged() {
        FormPage page = (FormPage)getSelectedPage();
        this.scrolledForm.setText(page.getTitle());
        page.setFocus();
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
                    for (MedEditorPage medEditorPage : accessMedEditorPages()) {
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

        // pass event on to pages
        this.overviewPage.handlePropertyChanged(e);
        this.propertiesPage.handlePropertyChanged(e);
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
                                if (!accessThis().getShell().isDisposed()) {
                                    accessThis().getShell().getDisplay().asyncExec(new Runnable() {

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

    /**
     * @param errors the parsing error messages (never <code>null</code>)
     * @param warnings the parsing warning messages (never <code>null</code>)
     * @param infos the parsing info messages (never <code>null</code>)
     * @throws Exception if there is a problem writing the markers to the resource
     */
    private void refreshMarkers( Collection<String> errors,
                                 Collection<String> warnings,
                                 Collection<String> infos ) throws Exception {
        IFile file = ((FileEditorInput)getEditorInput()).getFile();
        file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);

        if (errors != null) {
            for (String message : errors) {
                createMarker(IMarker.SEVERITY_ERROR, message);
            }
        }

        if (warnings != null) {
            for (String message : warnings) {
                createMarker(IMarker.SEVERITY_WARNING, message);
            }
        }

        if (infos != null) {
            for (String message : infos) {
                createMarker(IMarker.SEVERITY_INFO, message);
            }
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

            this.overviewPage.setResourceReadOnly(this.readOnly);
            this.overviewPage.getManagedForm().refresh();

            this.propertiesPage.setResourceReadOnly(this.readOnly);
            this.overviewPage.getManagedForm().refresh();
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

}

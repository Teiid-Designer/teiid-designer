/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.Messages.errorOpeningMedEditor;
import static org.teiid.designer.extension.ui.Messages.medEditorSourcePageTitle;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionText;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionToolTip;
import static org.teiid.designer.extension.ui.UiConstants.PLUGIN_ID;
import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.actions.RegistryDeploymentValidator;
import org.teiid.designer.extension.ui.actions.ShowModelExtensionRegistryViewAction;
import org.teiid.designer.extension.ui.actions.UpdateRegistryModelExtensionDefinitionAction;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends SharedHeaderFormEditor implements IResourceChangeListener,
        PropertyChangeListener {

    private boolean dirty = false;
    private boolean readOnly = false;

    private ModelExtensionDefinition originalMed;
    private ModelExtensionDefinition medBeingEdited;

    private MedEditorPage overviewPage;

    private MedEditorPage propertiesPage;
    private ScrolledForm scrolledForm;

    private IAction showRegistryViewAction;

    private IAction updateRegisteryAction;

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

            // add overview editor
            this.overviewPage = new OverviewEditorPage(this);
            addPage(0, this.overviewPage);

            // set text editor title and initialize header text to first page
            setPageText((getPageCount() - 1), medEditorSourcePageTitle);
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
        } catch (PartInitException e) {
            // TODO implement exception handling
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
        this.updateRegisteryAction = new Action(updateMedInRegistryActionText, SWT.FLAT) {
            @Override
            public void run() {
                IEditorInput editorInput = getEditorInput();
                IFile medFile = null;
                if (editorInput instanceof IFileEditorInput) {
                    medFile = ((IFileEditorInput)editorInput).getFile();
                }

                // -------------------------------------------------
                // Do some validation checks before registering.
                // -------------------------------------------------
                ModelExtensionRegistry registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
                InputStream fileContents = null;
                try {
                    fileContents = medFile.getContents();
                } catch (CoreException e) {
                    UTIL.log(NLS.bind(Messages.medFileGetContentsErrorMsg, medFile.getName()));
                }

                boolean wasAdded = false;
                boolean isDeployable = false;
                if (fileContents != null) {
                    isDeployable = RegistryDeploymentValidator.checkMedDeployable(registry, fileContents);
                    // If the URI is not registered, go ahead with registration
                    if (isDeployable) {
                        // Add the Extension Definition to the registry
                        try {
                            UpdateRegistryModelExtensionDefinitionAction.addExtensionToRegistry(medFile);
                            wasAdded = true;
                        } catch (Exception e) {
                            UTIL.log(NLS.bind(Messages.medRegistryAddErrorMsg, medFile.getName()));
                        }
                    }
                }

                // Notify user if the med was deployable, but the registration failed.
                if (isDeployable && !wasAdded) {
                    MessageDialog.openInformation(getShell(),
                                                  Messages.registerMedActionFailedTitle,
                                                  Messages.registerMedActionFailedMsg);
                    return;
                }
            }
        };
        this.updateRegisteryAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
        this.updateRegisteryAction.setToolTipText(updateMedInRegistryActionToolTip);

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

    private void createMed( IFile medFile ) throws Exception {
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(ExtensionPlugin.getInstance().getMedSchema());
        this.originalMed = parser.parse(medFile.getContents(), new ModelExtensionAssistantAdapter());

        // process parsing errors
        refreshMarkers(parser.getErrors(), parser.getWarnings(), parser.getInfos());

        // copy over data to MED that will be changed by editor
        updateEditorModelObject();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {

        final ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
        final ModelExtensionDefinition medToWrite = this.medBeingEdited;
        final IFile mxdFile = getFile();

        WorkspaceJob job = new WorkspaceJob(NLS.bind(Messages.medEditorSaveJobTitle, mxdFile.getName())) {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException {
                try {
                    InputStream inputStream = medWriter.write(medToWrite);
                    mxdFile.setContents(inputStream, IResource.KEEP_HISTORY, monitor);

                    // copy over data to MED that will be changed by editor
                    updateEditorModelObject();

                    // update editor dirty should be handled in UI thread
                    UiUtil.runInSwtThread(new Runnable() {

                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            refreshDirtyState();
                        }
                    }, true);

                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getLocalizedMessage(), e));
                }
            }

        };

        job.setRule(mxdFile);
        job.schedule();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // TODO implement doSaveAs
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

    protected IFile getFile() {
        return ((IFileEditorInput)getEditorInput()).getFile();
    }

    ModelExtensionDefinition getMed() {
        return this.medBeingEdited;
    }

    IMessageManager getMessageManager() {
        return this.scrolledForm.getMessageManager();
    }

    Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    void handlePageChanged() {
        FormPage page = (FormPage)getSelectedPage();
        this.scrolledForm.setText(page.getTitle());
        page.setFocus();
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
        setPartName(getEditorInput().getName());

        try {
            if (input instanceof IFileEditorInput) {
                createMed(((IFileEditorInput)input).getFile());
                ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
            }
        } catch (Exception e) {
            throw new PartInitException(errorOpeningMedEditor, e);
        }
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

            if (delta == null)
                return;

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
                                    getEditorSite().getPage().closeEditor(accessThis(), false);
                                }
                            }

                            return false; // stop visiting
                        }

                        return true; // keep visiting
                    }
                });
            } catch (CoreException e) {
                UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

    void refreshDirtyState() {
        boolean newValue = !this.originalMed.equals(this.medBeingEdited);

        if (isDirty() != newValue) {
            this.dirty = newValue;
            getHeaderForm().dirtyStateChanged();
        }
    }

    private void refreshMarkers( Collection<String> errors,
                                 Collection<String> warnings,
                                 Collection<String> infos ) throws Exception {
        IFile file = ((FileEditorInput)getEditorInput()).getFile();
        file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);

        for (String msg : errors) {
            final IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.MESSAGE, msg);
            // TODO marker.setAttribute(IMarker.LOCATION, null);
        }

        for (String msg : warnings) {
            final IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            marker.setAttribute(IMarker.MESSAGE, msg);
            // TODO marker.setAttribute(IMarker.LOCATION, null);
        }

        for (String msg : infos) {
            final IMarker marker = file.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
            marker.setAttribute(IMarker.MESSAGE, msg);
            // TODO marker.setAttribute(IMarker.LOCATION, null);
        }
    }

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

    void updateEditorModelObject() {
        if (this.medBeingEdited != null) {
            this.medBeingEdited.removeListener(this);
            this.originalMed = this.medBeingEdited;
        }

        this.medBeingEdited = new ModelExtensionDefinition(new ModelExtensionAssistantAdapter());
        this.medBeingEdited.setDescription(this.originalMed.getDescription());
        this.medBeingEdited.setMetamodelUri(this.originalMed.getMetamodelUri());
        this.medBeingEdited.setNamespacePrefix(this.originalMed.getNamespacePrefix());
        this.medBeingEdited.setNamespaceUri(this.originalMed.getNamespaceUri());
        this.medBeingEdited.setVersion(this.originalMed.getVersion());

        // properties
        for (String metaclassName : this.originalMed.getExtendedMetaclasses()) {
            this.medBeingEdited.addMetaclass(metaclassName);

            for (ModelExtensionPropertyDefinition propDefn : this.originalMed.getPropertyDefinitions(metaclassName)) {
                this.medBeingEdited.addPropertyDefinition(metaclassName, propDefn);
            }
        }

        // register to receive property change events
        this.medBeingEdited.addListener(this);
    }

}

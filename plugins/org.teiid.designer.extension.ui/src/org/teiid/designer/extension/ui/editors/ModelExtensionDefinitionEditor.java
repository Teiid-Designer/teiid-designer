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
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
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
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.actions.ShowModelExtensionRegistryViewAction;
import org.teiid.designer.extension.ui.actions.UpdateRegistryModelExtensionDefinitionAction;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends SharedHeaderFormEditor {

    private ModelExtensionDefinition med;
    private MedEditorPage overviewPage;

    private MedEditorPage propertiesPage;
    private ScrolledForm scrolledForm;

    private IAction showRegistryViewAction;
    private IAction updateRegisteryAction;

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
            this.propertiesPage = new PropertiesEditorPage(this, this.med);
            addPage(0, this.propertiesPage);

            // add overview editor
            this.overviewPage = new OverviewEditorPage(this, this.med);
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
        this.updateRegisteryAction = new UpdateRegistryModelExtensionDefinitionAction();
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
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser();
        this.med = parser.parse(medFile.getContents(), new ModelExtensionAssistantAdapter());
        this.med.setResourcePath(medFile.getLocation().toPortableString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // TODO implement addPages
        commitPages(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // TODO implement addPages
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IContentOutlinePage.class)) {
            // TODO implement getAdapter
            return null;
        }

        return super.getAdapter(adapter);
    }

    IMessageManager getMessageManager() {
        return this.scrolledForm.getMessageManager();
    }

    void handlePageChanged() {
        this.scrolledForm.setText(((FormPage)getSelectedPage()).getTitle());
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
            }
        } catch (Exception e) {
            throw new PartInitException(errorOpeningMedEditor, e);
        }
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

}

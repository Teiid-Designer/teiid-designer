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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends FormEditor {

    private ModelExtensionDefinition med;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages() {
        //        assert (this.med != null) : "MED is null"; //$NON-NLS-1$

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

            addPage(0, sourceEditor, getEditorInput());
            addPage(0, new PropertiesEditorPage(this, this.med));
            addPage(0, new OverviewEditorPage(this, this.med));

            setPageText((getPageCount() - 1), medEditorSourcePageTitle);
        } catch (PartInitException e) {
            // TODO implement exception handling
        }
    }

    private ModelExtensionDefinition createMed( IFile medFile ) {
        // TODO implement createMed
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // TODO implement addPages
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * 
 */
public final class ModelExtensionDefinitionEditor extends MultiPageEditorPart {

    private final IEditorPart textEditor;
    private final TextEditor textEditor2;

    public ModelExtensionDefinitionEditor() {
        this.textEditor =  new TextEditor();
        this.textEditor2 =  new TextEditor();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     */
    @Override
    protected void createPages() {
        try {
            IEditorInput editorInput = getEditorInput();

            int index = addPage(this.textEditor, editorInput);
            setPageText(index, "Design");

            index = addPage(this.textEditor2, editorInput);
            setPageText(index, "Source");
        } catch (PartInitException e) {
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // TODO send to parser
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
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

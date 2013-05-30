/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.edit.IDialogStatusListener;
import org.teiid.designer.relational.ui.edit.RelationalEditorPanel;

/**
 *
 */
public abstract class EditRelationalObjectDialogModel {

    protected final RelationalReference relationalObject;
    protected final IFile modelFile;

    /**
     * Create new dialog model
     *
     * @param relationalObject
     * @param modelFile
     */
    public EditRelationalObjectDialogModel(RelationalReference relationalObject, IFile modelFile) {
        this.relationalObject = relationalObject;
        this.modelFile = modelFile;
    }

    /**
     * @return the relationalObject
     */
    public RelationalReference getRelationalObject() {
        return this.relationalObject;
    }

    /**
     * @return the modelFile
     */
    public IFile getModelFile() {
        return this.modelFile;
    }


    /**
     * @return the initial edit dialog message
     */
    public abstract String getDialogTitle();

    /**
     * @param statusListener the status listener for the dialog
     * @param parent the parent UI panel
     *
     * @return the editor panel
     */
    public abstract RelationalEditorPanel getEditorPanel(IDialogStatusListener statusListener, Composite parent);

    /**
     * @return the initial edit dialog message
     */
    public abstract String getHelpText();

}

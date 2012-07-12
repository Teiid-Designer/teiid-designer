/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.actions.AbstractAction;
import org.teiid.designer.ui.preferences.TableEditorPreferencesDialog;


/**
 * InsertRowsAction
 */
public class EditTableEditorPreferencesAction extends AbstractAction {
    public EditTableEditorPreferencesAction() {
        super(UiPlugin.getDefault());
        setEnabled(true);
    }

    @Override
    protected void doRun() {
        Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        TableEditorPreferencesDialog dialog = new TableEditorPreferencesDialog(shell);
        dialog.validateDialog();
        dialog.open();
    }
}

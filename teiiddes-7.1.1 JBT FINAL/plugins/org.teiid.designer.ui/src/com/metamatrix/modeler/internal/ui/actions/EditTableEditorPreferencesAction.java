/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.internal.ui.preferences.TableEditorPreferencesDialog;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

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

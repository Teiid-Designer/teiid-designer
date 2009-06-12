/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;

public class BaseTypeDialogProvider extends TextDialogProvider {
    private static final String DIALOG_BASETYPE_TITLE  = "BaseTypeDialogProvider.dialog.basetype.title"; //$NON-NLS-1$
    private static final String DIALOG_BASETYPE_DESC   = "BaseTypeDialogProvider.dialog.basetype.desc"; //$NON-NLS-1$

    public BaseTypeDialogProvider(String launchButtonText) {
        super(launchButtonText, DIALOG_BASETYPE_TITLE, DIALOG_BASETYPE_DESC);
    }

    @Override
    public void showDialog(Shell shell, Object initialValue) {
        // Do NOT call super, here, because we don't want the text dialog
        // show dialog:
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell);
        dialog.setAllowSimple(true);
        dialog.setInitialSelections(new Object[] {initialValue});
        int status = dialog.open();

        // process dialog
        if (status == Window.OK) {
            value = dialog.getResult()[0];
        } else {
            value = null;
        } // endif

    }
}

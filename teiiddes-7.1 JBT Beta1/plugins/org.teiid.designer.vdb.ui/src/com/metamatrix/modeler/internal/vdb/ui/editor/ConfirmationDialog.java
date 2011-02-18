package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * 
 */
public class ConfirmationDialog extends MessageDialog {

    /**
     * @param dialog
     * @return <code>true</code> if confirmation was received
     */
    public static boolean confirm( final ConfirmationDialog dialog ) {
        return dialog.open() == Window.OK;
    }

    /**
     * @param message
     * @return <code>true</code> if confirmation was received
     */
    public static boolean confirm( final String message ) {
        return confirm(new ConfirmationDialog(message));
    }

    /**
     * @param message
     */
    public ConfirmationDialog( final String message ) {
        super(Display.getCurrent().getActiveShell(), VdbEditor.CONFIRM_DIALOG_TITLE, null, message, MessageDialog.CONFIRM,
              new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, SWT.NONE);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gives this dialog a {@link SWT#SHEET sheet} style
     * </p>
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected final int getShellStyle() {
        return SWT.SHEET;
    }
}

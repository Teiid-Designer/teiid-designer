/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;

public class TextDialogProvider implements DialogProvider {

    private final String buttonText;
    protected Object value;
    private final String dialogTitle;
    private final String dialogDescriptionKey;

    /**
     * @param descriptionKey specify the i18n id instead of the readable string. This allows the dialog to include the current
     *        value as part of the description
     */
    public TextDialogProvider( String launchButtonText,
                               String titleKey,
                               String descriptionKey ) {
        buttonText = launchButtonText;
        if (titleKey != null) {
            dialogTitle = GUIFacetHelper.getString(titleKey);
        } else {
            dialogTitle = null;
        } // endif
        dialogDescriptionKey = descriptionKey;
    }

    public void showDialog( Shell shell,
                            Object initialValue ) {
        value = initialValue;
        String desc = getDescriptionText(initialValue);
        InputDialog idlg = new InputDialog(shell, dialogTitle, desc, (String)initialValue, null) {
            // defect 18132 -- skip over the error field when tab hit.
            @Override
            protected Control createDialogArea( Composite parent ) {
                Control control = super.createDialogArea(parent);
                // use a traverse listener to do this skip:
                getText().addTraverseListener(new TraverseListener() {
                    public void keyTraversed( TraverseEvent e ) {
                        handleKeyTraversed(e);
                    }
                });
                return control;
            }

            void handleKeyTraversed( TraverseEvent e ) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
                    // skip over the error field when tabbing:
                    e.doit = false;
                    getButton(OK).setFocus();
                } // endif
            }
        };
        idlg.setBlockOnOpen(true);
        idlg.open();
        value = idlg.getValue();
    }

    public boolean wasCancelled() {
        return value == null;
    }

    public Object getValue() {
        return value;
    }

    public String getLaunchButtonText() {
        return buttonText;
    }

    protected String getDescriptionText( Object initialValue ) {
        if (initialValue != null) {
            return ModelerXsdUiConstants.Util.getString(dialogDescriptionKey, initialValue);
        } // endif

        return ModelerXsdUiConstants.Util.getString(dialogDescriptionKey);
    }

    protected String getDialogTitleText() {
        return this.dialogTitle;
    }
}

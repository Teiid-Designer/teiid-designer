/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeHierarchyTreeViewer;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class TypeInputDialog extends Dialog {
    //
    // Instance variables:
    //
    private final EObject selected;
    private final String dialogTitle;
    private final String dialogMessage;
    private EObject value;
    private DatatypeHierarchyTreeViewer dhtv;

    //
    // Constructors:
    //
    public TypeInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, EObject selected) {
        super(parentShell);
        setBlockOnOpen(true);
        setShellStyle(getShellStyle() | SWT.RESIZE);

        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.selected = selected;
    }

    //
    // Data methods:
    //
    public EObject getValue() {
        return value;
    }

    //
    // Overrides:
    //
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = SelectionUtilities.getSelectedEObject(dhtv.getSelection());
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (dialogTitle != null)
            shell.setText(dialogTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        // Copied from InputDialog:
        // create message
        if (dialogMessage != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(dialogMessage);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_BEGINNING);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        // Custom stuff:
        dhtv = new DatatypeHierarchyTreeViewer(composite, SWT.SINGLE, selected);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        data.heightHint = convertVerticalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        dhtv.getTree().setLayoutData(data);
        dhtv.expandToLevel(2);
        dhtv.setSelection(new StructuredSelection(selected), true);

        applyDialogFont(composite);
        return composite;
    }
}

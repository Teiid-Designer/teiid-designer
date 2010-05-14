/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.printing;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramPrintPreferencePage;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class DiagramPageSetupDialog extends SelectionStatusDialog implements DiagramUiConstants {

    private static final String TITLE = Util.getString("DiagramPageSetupDialog.title"); //$NON-NLS-1$
    private static final String RESTORE_DEFAULTS_BUTTON_TEXT = Util.getString("DiagramPageSetupDialog.restoreDefaultsButton.text"); //$NON-NLS-1$

    private Button btnRestoreDefaults;
    private Button btnOk;

    DiagramPrintPreferencePage preferencePage;

    /**
     * Construct an instance of DiagramPageSetupDialog.
     * 
     * @param shell
     */
    public DiagramPageSetupDialog( Shell shell ) {
        super(shell);

        init();

    }

    private void init() {

        this.setTitle(TITLE);
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);
    }

    @Override
    protected void computeResult() {
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite composite = (Composite)super.createDialogArea(parent);

        createPreferencePage(composite);

        return composite;
    }

    private void createPreferencePage( Composite parent ) {

        preferencePage = new DiagramPrintPreferencePage();

        preferencePage.createContents(parent);

        Composite pnlButtons = new Composite(parent, SWT.NONE);
        GridData gdButtons = new GridData(GridData.FILL_HORIZONTAL);
        pnlButtons.setLayoutData(gdButtons);

        GridLayout layButtons = new GridLayout();
        layButtons.numColumns = 3;
        pnlButtons.setLayout(layButtons);

        btnRestoreDefaults = WidgetFactory.createButton(pnlButtons, RESTORE_DEFAULTS_BUTTON_TEXT);

        btnRestoreDefaults.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                preferencePage.performDefaults();
            }
        });
    }

    /**
     * Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        // we'll create our own Ok and Cancel so we can use OK to
        // update the Preferences
        btnOk = createButton(parent, 10991, IDialogConstants.OK_LABEL, true);

        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                okButtonPressed();
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);

    }

    void okButtonPressed() {
        preferencePage.performOk();

        okPressed();
    }

    @Override
    public Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }
}

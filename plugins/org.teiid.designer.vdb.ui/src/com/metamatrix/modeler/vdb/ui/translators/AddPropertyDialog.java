/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.vdb.TranslatorPropertyDefinition;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;

class AddPropertyDialog extends MessageDialog {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AddPropertyDialog.class);

    private Button btnOk;
    private final List<String> existingNames;
    private String name;
    private String value;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingPropertyNames the existing property names (can be <code>null</code>)
     */
    public AddPropertyDialog( Shell parentShell,
                              List<String> existingPropertyNames ) {
        super(parentShell, Util.getString(PREFIX + "title"), null, //$NON-NLS-1$
                Util.getString(PREFIX + "message"), MessageDialog.INFORMATION, //$NON-NLS-1$
                new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

        this.existingNames = (existingPropertyNames == null) ? new ArrayList<String>(0) : existingPropertyNames;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        Button btn = super.createButton(parent, id, label, defaultButton);

        if (id == IDialogConstants.OK_ID) {
            // disable OK button initially
            this.btnOk = btn;
            btn.setEnabled(false);
        }

        return btn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(2, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblName = new Label(pnl, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblName.setText(Util.getString(PREFIX + "lblName.text")); //$NON-NLS-1$

        Text txtName = new Text(pnl, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtName.setToolTipText(Util.getString(PREFIX + "txtName.toolTip")); //$NON-NLS-1$
        txtName.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleNameChanged(((Text)e.widget).getText());
            }
        });

        Label lblValue = new Label(pnl, SWT.NONE);
        lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblValue.setText(Util.getString(PREFIX + "lblValue.text")); //$NON-NLS-1$

        Text txtValue = new Text(pnl, SWT.BORDER);
        txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtValue.setToolTipText(Util.getString(PREFIX + "txtValue.toolTip")); //$NON-NLS-1$
        txtValue.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleValueChanged(((Text)e.widget).getText());
            }
        });

        return pnl;
    }

    public TranslatorPropertyDefinition getDefinition() {
        assert (getReturnCode() == Window.OK);

        return new TranslatorPropertyDefinition(this.name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    public String getValue() {
        return this.value;
    }

    void handleNameChanged( String newName ) {
        this.name = newName;
        updateState();
    }

    void handleValueChanged( String newValue ) {
        this.value = newValue.trim();
        updateState();
    }

    private void updateState() {
        // check to see if new name is valid
        String msg = validateName();

        // empty message means field is valid
        if (StringUtilities.isEmpty(msg)) {
            // if name is valid check value
            msg = validateValue();
        }

        // update UI controls
        if (StringUtilities.isEmpty(msg)) {
            if (!this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(true);
            }

            if (this.imageLabel.getImage() != null) {
                this.imageLabel.setImage(null);
            }

            this.imageLabel.setImage(getInfoImage());
            msg = Util.getString(PREFIX + "message"); //$NON-NLS-1$
        } else {
            // value is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            this.imageLabel.setImage(getErrorImage());
        }

        this.messageLabel.setText(msg);
        this.messageLabel.pack();
    }

    private String validateName() {
        String errorMsg = TranslatorPropertyDefinition.validateName(this.name);

        if (errorMsg == null) {
            // make sure property ID doesn't already exist
            for (String existingName : this.existingNames) {
                if (existingName.equals(this.name)) {
                    errorMsg = Util.getString(PREFIX + "customPropertyAlreadyExists", this.name); //$NON-NLS-1$
                    break;
                }
            }
        }

        return errorMsg;
    }

    private String validateValue() {
        return TranslatorPropertyDefinition.validateValue(this.value);
    }
}
/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.teiid.designer.vdb.TranslatorOverride;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * Use for adding a translator override when no translator types are available.
 */
public class AddTranslatorOverrideDialog extends MessageDialog {

    static final String PREFIX = I18nUtil.getPropertyPrefix(AddTranslatorOverrideDialog.class);

    private Button btnOk;
    private final List<String> existingNames;
    private String name;
    private String type;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingOverrideNames the names of the existing translator overrides (never <code>null</code> but can be empty)
     */
    public AddTranslatorOverrideDialog( Shell parentShell,
                                        List<String> existingOverrideNames ) {
        super(parentShell, Util.getString(PREFIX + "title"), null, //$NON-NLS-1$
                Util.getString(PREFIX + "message"), MessageDialog.INFORMATION, //$NON-NLS-1$
                new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
        this.existingNames = existingOverrideNames;
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

        Label lblType = new Label(pnl, SWT.NONE);
        lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblType.setText(Util.getString(PREFIX + "lblType.text")); //$NON-NLS-1$

        Text txtType = new Text(pnl, SWT.BORDER);
        txtType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtType.setToolTipText(Util.getString(PREFIX + "txtType.toolTip")); //$NON-NLS-1$
        txtType.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleTypeChanged(((Text)e.widget).getText());
            }
        });

        return pnl;
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the name of the new translator override (never <code>null</code> or empty when OK button has been pressed)
     */
    public String getName() {
        assert (getReturnCode() == Window.OK);
        return this.name;
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

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the translator type being overridden (never <code>null</code> or empty when OK button has been pressed)
     */
    public String getType() {
        assert (getReturnCode() == Window.OK);
        return this.type;
    }

    void handleNameChanged( String newName ) {
        this.name = newName;
        updateState();
    }

    void handleTypeChanged( String newType ) {
        this.type = newType;
        updateState();
    }

    private void updateState() {
        // check to see if new name is valid
        String msg = validateName();

        // empty message means field is valid
        if (StringUtilities.isEmpty(msg)) {
            // if name is valid check type
            msg = validateType();
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
        GridDataFactory
		.fillDefaults()
		.align(SWT.FILL, SWT.BEGINNING)
		.grab(true, false)
		.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),
				SWT.DEFAULT).applyTo(messageLabel);
//        this.messageLabel.pack();
    }

    private String validateName() {
        String errorMsg = TranslatorOverride.validateName(this.name);

        if ((errorMsg == null) && this.existingNames.contains(this.name)) {
            errorMsg = Util.getString(PREFIX + "translatorNameAlreadyExists"); //$NON-NLS-1$
        }

        return errorMsg;
    }

    private String validateType() {
        return TranslatorOverride.validateType(this.type);
    }

}

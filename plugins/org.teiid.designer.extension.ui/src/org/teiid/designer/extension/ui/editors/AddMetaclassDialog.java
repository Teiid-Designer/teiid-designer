/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

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
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * Use for adding a new metaclass to a model extension definition.
 */
class AddMetaclassDialog extends MessageDialog {

    private Button btnOk;
    private final List<String> existingMetaclasses;
    private String metaclassName;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingMetaclasses the names of the metaclasses tha currently exist in the model extension definition (never
     *            <code>null</code> but can be empty)
     */
    public AddMetaclassDialog( Shell parentShell,
                               List<String> existingMetaclasses ) {
        super(parentShell, Messages.addMetaclassDialogTitle, null, Messages.addMetaclassDialogMessage, MessageDialog.INFORMATION,
                new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
        this.existingMetaclasses = existingMetaclasses;
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

        Label lblMetaclassName = new Label(pnl, SWT.NONE);
        lblMetaclassName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblMetaclassName.setText(Messages.metaclassLabel);

        Text txtMetaclassName = new Text(pnl, SWT.BORDER);
        txtMetaclassName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtMetaclassName.setToolTipText(Messages.metaclassToolTip);
        txtMetaclassName.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handleMetaclassNameChanged(((Text)e.widget).getText());
            }
        });

        return pnl;
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the metaclass name being added to the model extension definition (never <code>null</code> or empty when OK button has
     *         been pressed)
     */
    public String getMetaclassName() {
        assert (getReturnCode() == Window.OK);
        return this.metaclassName;
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

    void handleMetaclassNameChanged( String newMetaclassName ) {
        this.metaclassName = newMetaclassName;
        updateState();
    }

    private void updateState() {
        // check to see if new metaclassName is valid
        String msg = validateMetaclassName();

        // update UI controls
        if (StringUtilities.isEmpty(msg)) {
            if (!this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(true);
            }

            if (this.imageLabel.getImage() != null) {
                this.imageLabel.setImage(null);
            }

            this.imageLabel.setImage(getInfoImage());
            msg = Messages.addMetaclassDialogMessage;
        } else {
            // value is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            this.imageLabel.setImage(getErrorImage());
        }

        this.messageLabel.setText(msg);
        GridDataFactory.fillDefaults()
                       .align(SWT.FILL, SWT.BEGINNING)
                       .grab(true, false)
                       .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                       .applyTo(messageLabel);
    }

    private String validateMetaclassName() {
        String errorMsg = ModelExtensionDefinitionValidator.validateMetaclassName(this.metaclassName);

        if (CoreStringUtil.isEmpty(errorMsg) && this.existingMetaclasses.contains(this.metaclassName)) {
            errorMsg = Messages.metaclassExistsInMedMsg;
        }

        return errorMsg;
    }

}

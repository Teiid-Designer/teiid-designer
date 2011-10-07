/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.Form.TEXT_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * The <code>EditMetaclassDialog</code> is used to create or edit a metaclass name.
 */
final class EditMetaclassDialog extends FormDialog {

    private ScrolledForm scrolledForm;
    private Button btnOk;
    private final List<String> existingMetaclasses;
    private String metaclassName;

    /**
     * The metaclass name being edited or <code>null</code> when creating a metaclass name.
     */
    private String metaclassNameBeingEdited;

    private final ErrorMessage metaclassError;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingMetaclasses the names of the metaclasses tha currently exist in the model extension definition (never
     *            <code>null</code> but can be empty)
     */
    public EditMetaclassDialog( Shell parentShell,
                                List<String> existingMetaclasses ) {
        super(parentShell);
        this.existingMetaclasses = new ArrayList<String>(existingMetaclasses);
        this.metaclassError = new ErrorMessage();
    }

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingMetaclasses the names of the metaclasses tha currently exist in the model extension definition (never
     *            <code>null</code> but can be empty)
     */
    public EditMetaclassDialog( Shell parentShell,
                                List<String> existingMetaclasses,
                                String metaclassNameBeingEdited ) {
        this(parentShell, existingMetaclasses);

        CoreArgCheck.isNotNull(metaclassNameBeingEdited, "metaclassNameBeingEdited is null"); //$NON-NLS-1$
        this.metaclassNameBeingEdited = metaclassNameBeingEdited;

        // remove the metaclass being edited
        this.existingMetaclasses.remove(this.metaclassNameBeingEdited);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);

        if (isEditMode()) {
            newShell.setText(Messages.editMetaclassDialogTitle);
        }
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
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        this.scrolledForm = managedForm.getForm();
        this.scrolledForm.setText(Messages.addMetaclassDialogTitle);
        this.scrolledForm.setImage(Activator.getDefault().getImage(MED_EDITOR));
        this.scrolledForm.setMessage(Messages.metaclassDialogMessage, IMessageProvider.INFORMATION);

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = scrolledForm.getBody();
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        body.setLayout(layout);

        Label lblMetaclassName = toolkit.createLabel(body, Messages.metaclassLabel, SWT.NONE);
        lblMetaclassName.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));

        Text txtMetaclassName = toolkit.createText(body, null, TEXT_STYLE);
        this.metaclassError.widget = txtMetaclassName;
        txtMetaclassName.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
        txtMetaclassName.setToolTipText(Messages.metaclassToolTip);

        if (isEditMode()) {
            txtMetaclassName.setText(this.metaclassNameBeingEdited);
        }

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
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the metaclass name being added to the model extension definition (never <code>null</code> or empty when OK button has
     *         been pressed)
     */
    public String getMetaclassName() {
        return this.metaclassName;
    }

    void handleMetaclassNameChanged( String newMetaclassName ) {
        if (!CoreStringUtil.isEmpty(this.metaclassName)) {
            this.existingMetaclasses.remove(this.metaclassName);
        }

        this.metaclassName = newMetaclassName;
        this.metaclassError.message = ModelExtensionDefinitionValidator.validateMetaclassName(this.metaclassName);
        this.existingMetaclasses.add(this.metaclassName);
        updateState();
    }

    private boolean isEditMode() {
        return (!CoreStringUtil.isEmpty(this.metaclassNameBeingEdited));
    }

    private void updateState() {
        // check to see if new metaclassName is valid
        String errorMsg = this.metaclassError.message;
        int imageType = IMessageProvider.NONE;

        // update UI controls
        if (StringUtilities.isEmpty(errorMsg)) {
            boolean enable = true;

            if (isEditMode()) {
                enable = !CoreStringUtil.equals(this.metaclassName, this.metaclassNameBeingEdited);
            }

            if (this.btnOk.getEnabled() != enable) {
                this.btnOk.setEnabled(enable);
            }

            errorMsg = Messages.metaclassDialogMessage;
        } else {
            // value is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            imageType = IMessageProvider.ERROR;
        }

        this.scrolledForm.setMessage(errorMsg, imageType);
    }

    private String validateMetaclassName() {
        String errorMsg = ModelExtensionDefinitionValidator.validateMetaclassName(this.metaclassName);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = ModelExtensionDefinitionValidator.validateMetaclassNames(this.existingMetaclasses, false);
        }

        return errorMsg;
    }

}

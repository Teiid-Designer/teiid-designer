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
import java.util.Arrays;
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
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * The <code>EditAllowedValueDialog</code> is used to create or edit property definition allowed values.
 */
final class EditAllowedValueDialog extends FormDialog {

    private ScrolledForm scrolledForm;
    private Button btnOk;

    private final String runtimeType;
    private String[] existingValues;
    private String allowedValue;

    /**
     * The allowed allowedValue being edited or <code>null</code> when creating a new allowedValue.
     */
    private String valueBeingEdited;

    private final ErrorMessage valueError;

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param runtimeType the Teiid runtime type (can be <code>null</code>)
     * @param existingValues the existing allowed values (can be <code>null</code>)
     */
    public EditAllowedValueDialog( Shell parentShell,
                                   Type runtimeType,
                                   String[] existingValues ) {
        super(parentShell);
        this.runtimeType = ((runtimeType == null) ? null : runtimeType.toString());
        this.allowedValue = CoreStringUtil.Constants.EMPTY_STRING;
        this.valueError = new ErrorMessage();

        if (ArrayUtil.isNullOrEmpty(existingValues)) {
            this.existingValues = CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
        } else {
            this.existingValues = new String[existingValues.length];
            int i = 0;

            for (String value : existingValues) {
                this.existingValues[i++] = value;
            }
        }
    }

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param runtimeType the Teiid runtime type (can be <code>null</code>)
     * @param existingValues the allowed values that currently exist in the property definition (can be <code>null</code>)
     * @param valueBeingEdited the allowed allowedValue being edited (cannot be <code>null</code>)
     */
    public EditAllowedValueDialog( Shell parentShell,
                                   Type runtimeType,
                                   String[] existingValues,
                                   String valueBeingEdited ) {
        this(parentShell, runtimeType, existingValues);

        CoreArgCheck.isNotNull(valueBeingEdited, "valueBeingEdited is null"); //$NON-NLS-1$
        this.valueBeingEdited = valueBeingEdited;
        this.allowedValue = this.valueBeingEdited;

        // remove the value being edited
        List<String> temp = new ArrayList<String>(Arrays.asList(this.existingValues));

        if (temp.remove(this.valueBeingEdited)) {
            this.existingValues = temp.toArray(new String[temp.size()]);
        }
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
            newShell.setText(Messages.editAllowedValueDialogTitle);
        } else {
            newShell.setText(Messages.addAllowedValueDialogTitle);
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
        this.scrolledForm.setText(Messages.allowedValueDialogMessageTitle);
        this.scrolledForm.setImage(Activator.getDefault().getImage(MED_EDITOR));
        this.scrolledForm.setMessage(Messages.allowedValueDialogMessage, IMessageProvider.NONE);

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        Composite body = scrolledForm.getBody();
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        body.setLayout(layout);

        Label lblValue = toolkit.createLabel(body, Messages.allowedValueLabel, SWT.NONE);
        lblValue.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));

        Text txtValue = toolkit.createText(body, null, TEXT_STYLE);
        this.valueError.setControl(txtValue);
        txtValue.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
        txtValue.setToolTipText(Messages.allowedValueToolTip);

        if (isEditMode()) {
            txtValue.setText(this.valueBeingEdited);
        }

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
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the allowed allowedValue being added to/update in the property definition definition (never <code>null</code> or
     *         empty when OK button has been pressed)
     */
    public String getAllowedValue() {
        return this.allowedValue;
    }

    void handleValueChanged( String newValue ) {
        this.allowedValue = newValue;
        this.valueError.setMessage(ModelExtensionDefinitionValidator.validatePropertyAllowedValue(this.runtimeType,
                                                                                                  this.allowedValue));

        if (CoreStringUtil.isEmpty(this.valueError.getMessage())) {
            String[] temp = new String[this.existingValues.length + 1];
            System.arraycopy(this.existingValues, 0, temp, 0, this.existingValues.length);
            temp[this.existingValues.length] = newValue;
            this.valueError.setMessage(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(this.runtimeType, temp));
        }

        updateState();
    }

    private boolean isEditMode() {
        return (!CoreStringUtil.isEmpty(this.valueBeingEdited));
    }

    private void updateState() {
        // check to see if new metaclassName is valid
        String errorMsg = this.valueError.getMessage();
        int imageType = IMessageProvider.NONE;

        // update UI controls
        if (StringUtilities.isEmpty(errorMsg)) {
            boolean enable = true;

            if (isEditMode()) {
                enable = !CoreStringUtil.equals(this.allowedValue, this.valueBeingEdited);
            }

            if (this.btnOk.getEnabled() != enable) {
                this.btnOk.setEnabled(enable);
            }

            errorMsg = Messages.allowedValueDialogMessage;
        } else {
            // allowedValue is not valid
            if (this.btnOk.isEnabled()) {
                this.btnOk.setEnabled(false);
            }

            imageType = IMessageProvider.ERROR;
        }

        this.scrolledForm.setMessage(errorMsg, imageType);
    }

}

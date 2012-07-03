/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards.wsdl;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.util.UriValidator;
import com.metamatrix.modeler.ui.IHelpContextIds;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.CredentialsComposite;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * The <code>WsdlUrlDialog</code> class is the dialog that obtains user input for entering a WSDL Url.
 * 
 * @since 5.0.1
 */
public class WsdlUrlDialog extends Dialog implements IHelpContextIds, UiConstants {

    /**
     * Properties key prefix.
     */
    static final String PREFIX = I18nUtil.getPropertyPrefix(WsdlUrlDialog.class);

    interface StatusCodes extends UriValidator.StatusCodes {
        /**
         * Indicates the current value is unchanged from the original value.
         */
        int NO_CHANGE = 1000;

        /**
         * Indicates the current value is empty and will clear the URI.
         */
        int CLEAR_VALUE = 1010;

        /**
         * Indicates the original value has been changed to a non-empty value.
         */
        int CHANGED_VALUE = 1020;
    }

    /**
     * Utility method to obtain Properties values.
     */
    static String getString( String theKey ) {
        return Util.getStringOrKey(PREFIX + theKey);
    }

    private Button btnOk;

    /**
     * Indicates if the newValue is valid.
     */
    private IStatus currentStatus;

    /**
     * UI composite for security, username and password
     */
    private CredentialsComposite credentialsComposite;

    private MessageLabel lblMsg;

    /**
     * The current value display on the dialog.
     */
    private String newValue;

    /**
     * The initial value displayed on the dialog.
     */
    String oldValue;

    /**
     * URL object created from url string entered.
     */
    private URL urlObject;

    /**
     * The object used to validate the URI.
     */
    private NamespaceUriValidator validator;

    /**
     * Constructs a <code>WsdlUrlDialog</code>.
     * 
     * @param theParent the parent
     */
    public WsdlUrlDialog( Shell theParent ) {
        super(theParent, getString("title")); //$NON-NLS-1$

        this.validator = new NamespaceUriValidator();

        setReturnCode(Window.CANCEL);
        setSizeRelativeToScreen(55, 35);
        setCenterOnDisplay(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.3
     */
    @Override
    protected Button createButton( Composite theParent,
                                   int theId,
                                   String theLabel,
                                   boolean theDefaultButton ) {
        Button btn = super.createButton(theParent, theId, theLabel, theDefaultButton);

        // need to set the initial enabled state of the OK button
        if (theId == IDialogConstants.OK_ID) {
            this.btnOk = btn;
            updateButtonStatus();
        }

        this.btnOk.addSelectionListener(new SelectionListener() {
            public void widgetSelected( SelectionEvent theEvent ) {
            }

            public void widgetDefaultSelected( SelectionEvent theEvent ) {
                okPressed();
            }
        });

        return btn;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.3
     */
    @Override
    protected void okPressed() {

        this.currentStatus = new Status(IStatus.INFO, UiConstants.PLUGIN_ID, IStatus.OK,
                                        Util.getString("WsdlUrlDialog.0"), null); //$NON-NLS-1$
        updateMessage();

        boolean resolved = true;
        try {
            if (SecurityType.None.equals(getSecurityOption())) {
                resolved = URLHelper.resolveUrl(this.urlObject);
            } else {
                resolved = URLHelper.resolveUrl(this.urlObject, getUserName(), getPassword(), true);
            }
        } catch (Exception e) {
            resolved = false;
            currentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, IStatus.OK,
                                       Util.getString("WsdlUrlDialog.2"), null); //$NON-NLS-1$
        }

        updateMessage();

        if (resolved) {
            super.okPressed();
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        final int COLUMNS = 2;

        //
        // create main panel
        //
        Composite panel = new Composite(theParent, SWT.NONE);
        GridLayout layout = new GridLayout(COLUMNS, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        //
        // create URI test field label
        //
        Label urlLabel = new Label(panel, SWT.NONE);
        urlLabel.setText(getString("label.text")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().grab(false, false).applyTo(urlLabel);

        //
        // create URI entry text field
        //
        Text txf = new Text(panel, SWT.BORDER);
        txf.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txf.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleModifyText(theEvent);
            }
        });
        
        //
        // Spacer label to occupy the cell
        //
        Label spacerLabel = new Label(panel, SWT.NONE);
        spacerLabel.setVisible(false);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(spacerLabel);

        //
        // Username / password composite
        //
        credentialsComposite = new CredentialsComposite(panel, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(credentialsComposite);

        //
        // create status label
        //
        this.lblMsg = new MessageLabel(panel);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = COLUMNS;
        this.lblMsg.setLayoutData(gd);

        // must set this after the label is created as the ModifyListener sets the label text
        txf.setText((this.oldValue == null) ? "" : this.oldValue); //$NON-NLS-1$

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(panel, NAMESPACE_URI_RENAME_DIALOG);

        return panel;
    }

    /**
     * Obtains the current URI value. If the URI is invalid it returns the initial URI.
     * 
     * @return the URI
     * @since 4.3
     */
    public String getUrl() {
        String result = this.oldValue;
        int code = getReturnCode();

        // return the new value only if valid and the OK button was clicked
        if (code == Window.OK) {
            if ((this.currentStatus != null) && (this.currentStatus.getSeverity() != IStatus.ERROR)) {
                result = this.newValue;
            }
        }

        return result;
    }
    
    public SecurityType getSecurityOption() {
        return credentialsComposite.getSecurityOption();
    }
    
    public String getUserName() {
        return credentialsComposite.getUserName();
    }
    
    public String getPassword() {
        return credentialsComposite.getPassword();
    }

    /**
     * Obtains the current URI object.
     * 
     * @return the URI object
     * @since 5.1
     */
    public URL getUrlObject() {
        return urlObject;
    }

    /**
     * Obtains the current URI object.
     * 
     * @return the URI object
     * @since 5.1
     */
    public void setUrlObject( final URL urlObject ) {
        this.urlObject = urlObject;
    }

    /**
     * Obtains the validator appropriate for validating URI potential values.
     * 
     * @return the validator
     * @since 4.3
     */
    public ICellEditorValidator getValidator() {
        return this.validator;
    }

    /**
     * Handler for when the URI value is modified.
     * 
     * @param theEvent the event being processed
     * @since 4.3
     */
    void handleModifyText( ModifyEvent theEvent ) {
        this.newValue = ((Text)theEvent.widget).getText();
        this.currentStatus = new Status(IStatus.OK, UiConstants.PLUGIN_ID, IStatus.OK, CoreStringUtil.Constants.EMPTY_STRING, null);

        URL url = null;
        try {
            url = URLHelper.buildURL(newValue);
            setUrlObject(url);
        } catch (MalformedURLException e) {
            this.currentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, IStatus.OK,
                                            Util.getString("WsdlUrlDialog.1"), e); //$NON-NLS-1$
        }

        updateMessage();
        updateButtonStatus();
    }

    /**
     * Updates the URI validation status message.
     * 
     * @param theEvent the event being processed.
     * @since 4.3
     */
    private void updateMessage() {
        this.lblMsg.setErrorStatus(this.currentStatus);
        this.lblMsg.pack(true);
        this.lblMsg.update();
    }

    /**
     * Updates the OK button status based on the URI validation status.
     * 
     * @since 4.3
     */
    private void updateButtonStatus() {
        if (this.btnOk != null) {
            this.btnOk.setEnabled((this.currentStatus != null) && (this.currentStatus.getSeverity() != IStatus.ERROR));
        }
    }

    /**
     * The <code>NamespaceUriValidator</code> is the URI validator for the dialog.
     * 
     * @since 4.3
     */
    final class NamespaceUriValidator implements ICellEditorValidator {
        /**
         * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
         * @since 4.3
         */
        public String isValid( Object theValue ) {
            String result = null;

            if (theValue instanceof String) {
                IStatus status = validate((String)theValue);

                if (status.getSeverity() == IStatus.ERROR) {
                    result = status.getMessage();
                }
            } else if (theValue != null) {
                // not valid type
                result = Util.getString(PREFIX + "invalidTypeForUri", theValue.getClass()); //$NON-NLS-1$
            }

            return result;
        }

        /**
         * Validates the proposed URL value.
         * 
         * @param theNewValue the value being validated
         * @return the validation status
         * @since 4.3
         */
        public IStatus validate( String theNewValue ) {
            IStatus result = null;

            try {
                result = UriValidator.validate(theNewValue);

                if (result.getSeverity() == IStatus.ERROR) {
                    result = new Status(result.getSeverity(), result.getPlugin(), result.getCode(), result.getMessage()
                                                                                                    + getString("helpText"), //$NON-NLS-1$
                                        null); // no exception
                } else {
                    // if valid notify user if value hasn't changed or if new value is empty or changed
                    if ((theNewValue == oldValue)
                        || ((theNewValue != null) && (oldValue != null) && theNewValue.equals(oldValue))) {
                        result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, StatusCodes.NO_CHANGE,
                                            getString("valueUnchanged"), //$NON-NLS-1$
                                            null); // no exception
                    } else if (!CoreStringUtil.isEmpty(oldValue) && CoreStringUtil.isEmpty(theNewValue)) {
                        result = new Status(IStatus.WARNING, UiConstants.PLUGIN_ID, StatusCodes.CLEAR_VALUE,
                                            getString("valueCleared"), //$NON-NLS-1$
                                            null); // no exception
                    } else if (!CoreStringUtil.isEmpty(oldValue) && !CoreStringUtil.isEmpty(theNewValue) && !oldValue.equals(theNewValue)) {
                        result = new Status(IStatus.WARNING, UiConstants.PLUGIN_ID, StatusCodes.CHANGED_VALUE,
                                            Util.getString(PREFIX + "valueChanged", oldValue), //$NON-NLS-1$
                                            null); // no exception
                    }
                }
            } catch (RuntimeException theException) {
                result = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
                                    com.metamatrix.metamodels.core.util.UriValidator.StatusCodes.INVALID_URI,
                                    theException.getLocalizedMessage(), null); // no exception needed
            }

            return result;
        }
    }
}

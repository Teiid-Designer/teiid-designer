/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.ui.IHelpContextIds;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.MessageLabel;

/**
 * The <code>XsdUrlDialog</code> class is the dialog that obtains user input for entering a XSD Url.
 * 
 * @since 5.5
 */
public class XsdUrlDialog extends Dialog implements IHelpContextIds, ModelerXsdUiConstants {

    /**
     * Properties key prefix.
     */
    private static final String I18N_PREFIX = "XsdUrlDialog"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    /**
     * Utility method to obtain Properties values.
     */
    private static String getString( String id ) {
        return ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    private Button btnOk;

    /**
     * Indicates if the newValue is valid.
     */
    private IStatus currentStatus;

    private MessageLabel lblMsg;

    /**
     * The current value display on the dialog.
     */
    private String newValue;

    /**
     * The initial value displayed on the dialog.
     */
    private String oldValue;

    /**
     * URL object created from url string entered.
     */
    private URL urlObject;
    private String userName;
    private String password;
    private boolean verifyHostname = true;

    private Text pwdText;
    private Text userText;
    private Button verifyHostnameCheckbox;

    /**
     * Constructs a <code>XsdUrlDialog</code>.
     * 
     * @param theParent the parent
     */
    public XsdUrlDialog( Shell theParent ) {
        super(theParent, getString("title")); //$NON-NLS-1$

        setReturnCode(Window.CANCEL);
        setSizeRelativeToScreen(55, 36);
        setCenterOnDisplay(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 5.5
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
     * @since 5.5
     */
    @Override
    protected void okPressed() {

        this.currentStatus = new Status(IStatus.INFO, ModelerXsdUiConstants.PLUGIN_ID, IStatus.OK, getString("0"), null); //$NON-NLS-1$
        updateMessage();

        boolean resolved = true;
        try {
            this.userName = this.userText.getText();
            this.password = this.pwdText.getText();
            resolved = URLHelper.resolveUrl(this.urlObject, this.getUserName(), this.getPassword(), verifyHostname);
        } catch (Exception e) {
            resolved = false;
            ModelerXsdUiConstants.Util.log(e);
            currentStatus = new Status(IStatus.ERROR, ModelerXsdUiConstants.PLUGIN_ID, IStatus.OK, getString("2"), null); //$NON-NLS-1$
        }

        updateMessage();

        if (resolved) {
            super.okPressed();
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        //
        // create main panel
        //
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        pnlMain.setLayout(layout);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

        pnlMain.setLayoutData(new GridData(GridData.FILL_BOTH));

        createXsdUrlPanel(pnlMain);

        // add optional user/password
        createUserPasswordPanel(pnlMain);

        return pnlMain;
    }

    private void createXsdUrlPanel( Composite theParent ) {
        Composite panel = new Composite(theParent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));

        //
        // create URI test field label
        //
        WidgetFactory.createLabel(panel, getString("label.text"), GridData.HORIZONTAL_ALIGN_END); //$NON-NLS-1$

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
        // create status label
        //
        this.lblMsg = new MessageLabel(panel);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        this.lblMsg.setLayoutData(gd);

        // must set this after the label is created as the ModifyListener sets the label text
        txf.setText((this.oldValue == null) ? "" : this.oldValue); //$NON-NLS-1$

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(panel, NAMESPACE_URI_RENAME_DIALOG);
    }

    private void createUserPasswordPanel( Composite theParent ) {
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        pnl.setLayout(layout);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        pnl.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group optionsGroup = new Group(pnl, SWT.NONE);
        layout = new GridLayout(2, false);
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        optionsGroup.setText(getString("Optional"));//$NON-NLS-1$

        WidgetFactory.createLabel(optionsGroup, getString("label.user"));//$NON-NLS-1$
        this.userText = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        this.userText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                userModified();
            }
        });
        WidgetFactory.createLabel(optionsGroup, getString("label.password"));//$NON-NLS-1$
        this.pwdText = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        this.pwdText.setEchoChar('*');
        this.pwdText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                passwordModified();
            }
        });

        verifyHostnameCheckbox = new Button(optionsGroup, SWT.CHECK);
        verifyHostnameCheckbox.setFont(optionsGroup.getFont());
        verifyHostnameCheckbox.setText(getString("verifyHostname.text")); //$NON-NLS-1$
        verifyHostnameCheckbox.setSelection(true);
        verifyHostnameCheckbox.addSelectionListener(new SelectionListener() {

            public void widgetSelected( SelectionEvent e ) {
                verifyHostnameChanged();
            }

            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
    }

    /**
     * Obtains the current URI value. If the URI is invalid it returns the initial URI.
     * 
     * @return the URI
     * @since 5.5
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

    /**
     * Obtains the current URI object.
     * 
     * @return the URI object
     * @since 5.5
     */
    public URL getUrlObject() {
        return urlObject;
    }

    public String getUserName() {
        if (this.userName == null) {
            return null;
        }
        return this.userName.trim();
    }

    public String getPassword() {
        if (this.password == null) {
            return null;
        }
        return this.password.trim();
    }

    public boolean verifyHostname() {
        return this.verifyHostname;
    }

    /**
     * Obtains the current URI object.
     * 
     * @return the URI object
     * @since 5.5
     */
    public void setUrlObject( final URL urlObject ) {
        this.urlObject = urlObject;
    }

    void passwordModified() {
        password = this.pwdText.getText();
    }

    void userModified() {
        userName = this.userText.getText();
    }

    void verifyHostnameChanged() {
        verifyHostname = verifyHostnameCheckbox.getSelection();
    }

    void handleModifyText( ModifyEvent theEvent ) {
        this.newValue = ((Text)theEvent.widget).getText();
        this.currentStatus = new Status(IStatus.OK, ModelerXsdUiConstants.PLUGIN_ID, IStatus.OK,
                                        CoreStringUtil.Constants.EMPTY_STRING, null);

        URL url = null;
        try {
            url = URLHelper.buildURL(newValue);
            setUrlObject(url);
        } catch (MalformedURLException e) {
            this.currentStatus = new Status(IStatus.ERROR, ModelerXsdUiConstants.PLUGIN_ID, IStatus.OK, getString("1"), e); //$NON-NLS-1$
        }

        updateMessage();
        updateButtonStatus();
    }

    /**
     * Updates the URI validation status message.
     * 
     * @param theEvent the event being processed.
     * @since 5.5
     */
    private void updateMessage() {
        this.lblMsg.setErrorStatus(this.currentStatus);
        this.lblMsg.pack(true);
        this.lblMsg.update();
    }

    /**
     * Updates the OK button status based on the URI validation status.
     * 
     * @since 5.5
     */
    private void updateButtonStatus() {
        if (this.btnOk != null) {
            this.btnOk.setEnabled((this.currentStatus != null) && (this.currentStatus.getSeverity() != IStatus.ERROR));
        }
    }

}

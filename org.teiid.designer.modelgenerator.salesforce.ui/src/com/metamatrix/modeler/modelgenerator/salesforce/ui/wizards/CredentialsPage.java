/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class CredentialsPage extends AbstractWizardPage
    implements Listener, ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CredentialsPage.class);

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    SalesforceImportWizardManager importManager;

    private Text textFieldUsername;

    private Text textFieldPassword;

    private Button validateButton;

    private Button overrideURL;

    boolean validCredentials = false;

    private Text textFieldURL;

    public CredentialsPage( SalesforceImportWizardManager importManager ) {
        super(CredentialsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
    }

    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnl, CREDENTIAL_SELECTION_PAGE);
        setControl(pnl);
        // credentials group
        Group credentialsGroup = new Group(pnl, SWT.NONE);
        credentialsGroup.setText(getString("credentialsOptionsGroup.text")); //$NON-NLS-1$

        GridData gdCredentialsGroup = new GridData(GridData.FILL_HORIZONTAL);
        credentialsGroup.setLayoutData(gdCredentialsGroup);

        credentialsGroup.setLayout(new GridLayout(2, false));
        // --------------------------------------------
        // Composite for Username
        // --------------------------------------------

        CLabel userLabel = new CLabel(credentialsGroup, SWT.NONE);
        userLabel.setText(getString("usernameLabel.text")); //$NON-NLS-1$
        final GridData gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        userLabel.setLayoutData(gridData);

        // URL textfield

        textFieldUsername = WidgetFactory.createTextField(credentialsGroup, GridData.FILL_HORIZONTAL);
        String text = getString("usernameTextField.tooltip"); //$NON-NLS-1$
        textFieldUsername.setToolTipText(text);
        textFieldUsername.setText(EMPTY_STR);
        textFieldUsername.addListener(SWT.Modify, this);

        // --------------------------------------------
        // Composite for Username
        // --------------------------------------------

        CLabel passwordLabel = new CLabel(credentialsGroup, SWT.NONE);
        passwordLabel.setText(getString("passwordLabel.text")); //$NON-NLS-1$
        final GridData gridData2 = new GridData(SWT.NONE);
        gridData2.horizontalSpan = 1;
        passwordLabel.setLayoutData(gridData2);

        // URL textfield
        textFieldPassword = WidgetFactory.createTextField(credentialsGroup, GridData.FILL_HORIZONTAL);
        text = getString("usernameTextField.tooltip"); //$NON-NLS-1$
        textFieldPassword.setToolTipText(text);
        textFieldPassword.setText(EMPTY_STR);
        textFieldPassword.setEchoChar('*');
        textFieldPassword.addListener(SWT.Modify, this);

        overrideURL = WidgetFactory.createCheckBox(credentialsGroup);
        overrideURL.setText(getString("overrideURLCheckbox.text")); //$NON-NLS-1$
        overrideURL.setToolTipText(getString("overrideURLCheckbox.tipText")); //$NON-NLS-1$
        overrideURL.addListener(SWT.Selection, this);

        textFieldURL = WidgetFactory.createTextField(credentialsGroup, GridData.FILL_HORIZONTAL);
        String urlText = getString("URLTextField.tooltip"); //$NON-NLS-1$
        textFieldURL.setToolTipText(urlText);
        textFieldURL.setText("https://test.salesforce.com/services/Soap/u/10.0"); //$NON-NLS-1$
        textFieldURL.addListener(SWT.Selection, this);
        textFieldURL.setEnabled(false);

        Composite buttonComposite = WidgetFactory.createPanel(pnl, SWT.NONE, GridData.FILL_VERTICAL);
        GridLayout layout = new GridLayout(1, false);
        buttonComposite.setLayout(layout);
        validateButton = WidgetFactory.createButton(buttonComposite,
                                                    getString("validateCredentialsButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        validateButton.setToolTipText(getString("validateCredentialsButton.tipText")); //$NON-NLS-1$
        validateButton.addListener(SWT.Selection, this);
        validateButton.setEnabled(false);
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    public void testComplete() {
        if (validCredentials) {
            setPageComplete(true);
        } else {
            setPageComplete(false);
            if (importManager.getUsername() != null && importManager.getPassword() != null) {
                if (!validateButton.isEnabled()) {
                    validateButton.setEnabled(true);
                }
            }
        }
    }

    public void handleEvent( Event event ) {
        if (event.widget == this.textFieldUsername) {
            importManager.setUsername(this.textFieldUsername.getText());
            validCredentials = false;
            testComplete();
        } else if (event.widget == this.textFieldPassword) {
            importManager.setPassword(this.textFieldPassword.getText());
            validCredentials = false;
            testComplete();
        } else if (event.widget == this.validateButton) {

            if (overrideURL.getSelection()) {
                try {
                    URL connectionURL = new URL(textFieldURL.getText());
                    importManager.setConnectionURL(connectionURL);
                } catch (MalformedURLException e) {
                    Shell shell = this.getShell();
                    Status status = new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getLocalizedMessage(), e);
                    ErrorDialog.openError(shell, getString("dialog.urlValidationError.title"), e.getLocalizedMessage(), status); //$NON-NLS-1$  
                    return;
                }
            }
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        validCredentials = importManager.validateCredentials(monitor);
                        monitor.done();
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    }
                }
            };
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                Shell shell = this.getShell();
                Status status = new Status(IStatus.ERROR, PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
                ErrorDialog.openError(shell,
                                      getString("dialog.credentialValidationError.title"), cause.getLocalizedMessage(), status); //$NON-NLS-1$  
                return;
            } catch (InterruptedException e) {
                Shell shell = this.getShell();
                Status status = new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getLocalizedMessage(), e);
                ErrorDialog.openError(shell, getString("dialog.credentialValidationError.title"), e.getLocalizedMessage(), status); //$NON-NLS-1$  
                return;
            }
            if (validCredentials) {
                testComplete();
            }
        } else if (event.widget == this.overrideURL) {
            boolean enable = this.overrideURL.getSelection();
            textFieldURL.setEnabled(enable);
            if (!enable) {
                importManager.setConnectionURL(null);
            }
            validCredentials = false;
            testComplete();
        } else if (event.widget == this.textFieldURL) {
            validCredentials = false;
            testComplete();
        }
    }

    @Override
    public void setErrorMessage( String message ) {
        setErrorMessage(message);
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.designer.datatools.salesforce.ISalesForceProfileConstants;
import org.teiid.designer.datatools.ui.jobs.PingJobWithoutPopup;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class CredentialsPage extends AbstractWizardPage
    implements Listener, ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CredentialsPage.class);

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    SalesforceImportWizardManager importManager;

    private Combo profileCombo;

    private Text textFieldUsername;

    private Text textFieldPassword;

    private Button validateButton;

    private Text textFieldURL;

    private ProfileManager profileManager;

    public CredentialsPage( SalesforceImportWizardManager importManager ) {
        super(CredentialsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
        profileManager = ProfileManager.getInstance();
    }

    @Override
    public void createControl( Composite theParent ) {
        GridData gridData;
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

        CLabel profileLabel = new CLabel(credentialsGroup, SWT.NONE);
        profileLabel.setText(getString("profileLabel.text")); //$NON-NLS-1$
        gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        profileLabel.setLayoutData(gridData);

        profileCombo = new Combo(credentialsGroup, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        profileCombo.setLayoutData(gridData);
        profileCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                setValues();
            }
        });

        // --------------------------------------------
        // Composite for Username
        // --------------------------------------------

        CLabel userLabel = new CLabel(credentialsGroup, SWT.NONE);
        userLabel.setText(getString("usernameLabel.text")); //$NON-NLS-1$
        gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        userLabel.setLayoutData(gridData);

        // URL textfield

        textFieldUsername = WidgetFactory.createTextField(credentialsGroup, GridData.FILL_HORIZONTAL);
        String text = getString("usernameTextField.tooltip"); //$NON-NLS-1$
        textFieldUsername.setToolTipText(text);
        textFieldUsername.setText(EMPTY_STR);
        textFieldUsername.setEnabled(false);

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
        textFieldPassword.setEnabled(false);

        CLabel urlLabel = new CLabel(credentialsGroup, SWT.NONE);
        urlLabel.setText(getString("overrideURLCheckbox.text")); //$NON-NLS-1$
        urlLabel.setToolTipText(getString("overrideURLCheckbox.tipText")); //$NON-NLS-1$

        textFieldURL = WidgetFactory.createTextField(credentialsGroup, GridData.FILL_HORIZONTAL);
        String urlText = getString("URLTextField.tooltip"); //$NON-NLS-1$
        textFieldURL.setToolTipText(urlText);
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
        setValues();
    }

    /**
     * 
     */
    protected void setValues() {
        if (null == profileCombo.getItems() || 0 == profileCombo.getItems().length) {
            IConnectionProfile[] sfProfiles = profileManager.getProfilesByCategory("org.teiid.designer.import.category"); //$NON-NLS-1$
            if (sfProfiles.length == 0) {
                setErrorMessage(getString("define.profile")); //$NON-NLS-1$
                textFieldUsername.setText(EMPTY_STR);
                textFieldPassword.setText(EMPTY_STR);
                textFieldURL.setText(EMPTY_STR);
                validateButton.setEnabled(false);
            } else {
                List<String> profileNames = new ArrayList();
                for (int i = 0; i < sfProfiles.length; i++) {
                    IConnectionProfile profile = sfProfiles[i];
                    profileNames.add(profile.getName());
                }
                profileCombo.setItems(profileNames.toArray(new String[profileNames.size()]));
                setErrorMessage(null);
                setMessage(getString("select.profile")); //$NON-NLS-1$
                return;
            }
        }

        String profileName = profileCombo.getText();
        IConnectionProfile profile = findMatchingProfile(profileName);
        if (null == profile) {
            // this should really never happen
            setMessage(null);
            setErrorMessage(getString("no.profile.match", new Object[] {profileName})); //$NON-NLS-1$
            validateButton.setEnabled(false);
            return;
        }
        Properties props = profile.getBaseProperties();
        textFieldUsername.setText(props.getProperty(ISalesForceProfileConstants.USERNAME_PROP_ID));
        textFieldPassword.setText(props.getProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID));
        if (null == props.getProperty(ISalesForceProfileConstants.URL_PROP_ID)) {
            textFieldURL.setText(EMPTY_STR);
        } else {
            textFieldURL.setText(props.getProperty(ISalesForceProfileConstants.URL_PROP_ID));
        }
        setErrorMessage(null);
        setMessage(getString("validate.profile")); //$NON-NLS-1$
        validateButton.setEnabled(true);
    }

    private IConnectionProfile findMatchingProfile( String name ) {
        IConnectionProfile result = null;
        IConnectionProfile[] sfProfiles = profileManager.getProfilesByCategory("org.teiid.designer.import.category"); //$NON-NLS-1$
        for (int i = 0; i < sfProfiles.length; i++) {
            IConnectionProfile profile = sfProfiles[i];
            if (profile.getName().equals(name)) {
                result = profile;
            }
        }
        return result;
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

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @param parameters parameters for the message format
     * @return the localized text
     */
    private static String getString( String theKey,
                                     Object[] parameters ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString(), parameters);
    }

    @Override
    public void handleEvent( Event event ) {
        if (event.widget == this.validateButton) {
            String profileName = profileCombo.getText();
            IConnectionProfile profile = findMatchingProfile(profileName);
            if (null == profile) {
                // this should really never happen
                setMessage(null);
                setErrorMessage(getString("no.profile.match", new Object[] {profileName})); //$NON-NLS-1$
                validateButton.setEnabled(false);
                return;
            }

            final PingJobWithoutPopup pingJob = new PingJobWithoutPopup(Display.getCurrent().getActiveShell(), profile);
            pingJob.schedule();

            Runnable op = new Runnable() {
                @Override
                public void run() {
                    try {
                        pingJob.join();
                    } catch (InterruptedException e) {
                    }
                }
            };

            BusyIndicator.showWhile(getShell().getDisplay(), op);

            if (pingJob.getResult().isOK()) {
                importManager.setConnectionProfile(profile);
                setErrorMessage(null);
                setMessage(getString("Click.Next")); //$NON-NLS-1$
                setPageComplete(true);
            } else {
                setErrorMessage(pingJob.getResult().getMessage());
                setPageComplete(false);
            }
        }
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }

}

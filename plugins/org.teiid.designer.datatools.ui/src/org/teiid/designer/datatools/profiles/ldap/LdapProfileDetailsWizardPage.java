/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.ldap;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * 
 */
public class LdapProfileDetailsWizardPage extends ConnectionProfileDetailsPage implements Listener, DatatoolsUiConstants {

    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private CLabel descriptionText;
    private Label usernameLabel;
    private Text usernameText;
    private Label passwordLabel;
    private Text passwordText;
    private Label urlLabel;
    private Text urlText;
    private Label contextFactoryLabel;
    private Text contextFactoryText;

    /**
     * @param wizardPageName
     */
    public LdapProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("LdapProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
        // TODO: image
        /*)
        */

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl( Composite parent ) {
        GridData gd;

        Group group = new Group(parent, SWT.BORDER);
        group.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
        group.setLayout(new FillLayout());

        scrolled = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        scrolled.setLayout(gridLayout);

        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        profileLabel.setLayoutData(gd);

        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        profileText.setLayoutData(gd);
        profileText.setText(((ConnectionProfileWizard)getWizard()).getProfileName());
//        profileText.setEnabled(false);

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(((ConnectionProfileWizard)getWizard()).getProfileDescription());
//        descriptionText.setEnabled(false);

        usernameLabel = new Label(scrolled, SWT.NONE);
        usernameLabel.setText(UTIL.getString("Common.Username.Label")); //$NON-NLS-1$
        usernameLabel.setToolTipText(UTIL.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        usernameLabel.setLayoutData(gd);

        usernameText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        usernameText.setToolTipText(UTIL.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        usernameText.setLayoutData(gd);

        passwordLabel = new Label(scrolled, SWT.NONE);
        passwordLabel.setText(UTIL.getString("Common.Password.Label")); //$NON-NLS-1$
        passwordLabel.setToolTipText(UTIL.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        passwordLabel.setLayoutData(gd);

        passwordText = new Text(scrolled, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordText.setToolTipText(UTIL.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        passwordText.setLayoutData(gd);

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        urlLabel.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        //urlText.setText("ldap://<ldapServer>:<389>"); //$NON-NLS-1$
        urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        urlText.setLayoutData(gd);

        contextFactoryLabel = new Label(scrolled, SWT.NONE);
        contextFactoryLabel.setText(UTIL.getString("Common.Context.Factory.Label")); //$NON-NLS-1$
        contextFactoryLabel.setToolTipText(UTIL.getString("Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        contextFactoryLabel.setLayoutData(gd);

        contextFactoryText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        //contextFactoryText.setText("com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
        contextFactoryText.setToolTipText(UTIL.getString("Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        contextFactoryText.setLayoutData(gd);

        setPingButtonVisible(false);
        setAutoConnectOnFinishDefault(false);
        setPingButtonEnabled(false);
        setPageComplete(false);
        addListeners();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( Composite parent ) {
        super.createControl(parent);
        updateState();
    }

    /**
     * 
     */
    private void addListeners() {
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        urlText.addListener(SWT.Modify, this);
        contextFactoryText.addListener(SWT.Modify, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == usernameText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ILdapProfileConstants.USERNAME_PROP_ID, usernameText.getText());
        }
        if (event.widget == passwordText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ILdapProfileConstants.PASSWORD_PROP_ID, passwordText.getText());
        }
        if (event.widget == urlText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ILdapProfileConstants.URL_PROP_ID, urlText.getText());
        }
        if (event.widget == contextFactoryText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ILdapProfileConstants.CONTEXT_FACTORY, contextFactoryText.getText());
        }
        updateState();
    }

    void updateState() {

        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(ILdapProfileConstants.USERNAME_PROP_ID)
            || properties.get(ILdapProfileConstants.USERNAME_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setErrorMessage(null);
        if (null == properties.get(ILdapProfileConstants.PASSWORD_PROP_ID)
            || properties.get(ILdapProfileConstants.PASSWORD_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setErrorMessage(null);
        if (null == properties.get(ILdapProfileConstants.URL_PROP_ID)
            || properties.get(ILdapProfileConstants.URL_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        if (null == properties.get(ILdapProfileConstants.CONTEXT_FACTORY)
            || properties.get(ILdapProfileConstants.CONTEXT_FACTORY).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Context.Factory.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setPageComplete(true);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return internalComplete(super.canFlipToNextPage());
    }

    /**
     * @param complete
     * @return
     */
    private boolean internalComplete( boolean complete ) {
        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (complete
            && (null == properties.get(ILdapProfileConstants.USERNAME_PROP_ID) || properties.get(ILdapProfileConstants.USERNAME_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(ILdapProfileConstants.PASSWORD_PROP_ID) || properties.get(ILdapProfileConstants.PASSWORD_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(ILdapProfileConstants.URL_PROP_ID) || properties.get(ILdapProfileConstants.URL_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(ILdapProfileConstants.CONTEXT_FACTORY) || properties.get(ILdapProfileConstants.CONTEXT_FACTORY).toString().isEmpty())) {
            complete = false;
        }
        return complete;
    }

    @Override
    public void testConnection() {
        super.testConnection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List getSummaryData() {
        List result = super.getSummaryData();
        result.add(new String[] {UTIL.getString("Common.Username.Label"), usernameText.getText()}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("Common.URL.Label"), urlText.getText()}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("Common.Context.Factory.Label"), contextFactoryText.getText()}); //$NON-NLS-1$
        return result;
    }
}

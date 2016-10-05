/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ws;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.modelgenerator.wsdl.WSDLReader;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.wizards.wsdl.WsdlFileSelectionComposite;

/**
 * Wizard page for entering the wsdl URL 
 */
public class WSSoapProfileDetailsWizardPage extends ConnectionProfileDetailsPage implements DatatoolsUiConstants {

    private IStatus testStatus = Status.OK_STATUS;

    private Composite scrolled;
    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Label urlLabel;
    private Text urlText;

    private Composite parent;

    private WSSoapConnectionProfileWizard wizard;
    private Properties profileProperties;

    /**
     * @param pageName
     */
    public WSSoapProfileDetailsWizardPage(String pageName) {
        super(pageName, UTIL.getString("WSSoapProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/full/obj16/web-service-cp.png")); //$NON-NLS-1$
    }

    @Override
    public void createCustomControl(Composite parent) {

        wizard = (WSSoapConnectionProfileWizard)getWizard();
        profileProperties = wizard.getProfileProperties();

        Group group = WidgetFactory.createSimpleGroup(parent, null);

        scrolled = new Composite(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(scrolled);

        Label title = new Label(scrolled, SWT.NONE);
        title.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
        title.setFont(JFaceResources.getBannerFont());
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.CENTER, SWT.CENTER).applyTo(title);

        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(profileLabel);

        profileText = WidgetFactory.createLabel(scrolled, wizard.getProfileName(), SWT.SINGLE);
        GridDataFactory.swtDefaults().applyTo(profileText);

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(descriptionLabel);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(descriptionText);
        descriptionText.setText(wizard.getProfileDescription());
        descriptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URLorFILE.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URLorFILE.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().applyTo(urlLabel);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        urlText.setEditable(false);
        urlText.setToolTipText(UTIL.getString("Common.URLorFILE.ToolTip")); //$NON-NLS-1$
        urlText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        GridDataFactory.fillDefaults().grab(true, false).applyTo(urlText);
        urlText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String urlStr = urlText.getText();
                if (urlStr != null) {
                    urlStr = urlStr.trim();
                }

                setProperty(IWSProfileConstants.WSDL_URI_PROP_ID, urlStr);
                updateState();
            }
        });

        Label spacerLabel = new Label(scrolled, SWT.NONE);
        spacerLabel.setVisible(false);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(spacerLabel);

        WsdlFileSelectionComposite wsdlFileSelectionComposite = new WsdlFileSelectionComposite(scrolled, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(wsdlFileSelectionComposite);

        WsdlFileSelectionComposite.IFileSelectionCallback fileSelectionCallback = new WsdlFileSelectionComposite.IFileSelectionCallback() {
            @Override
            public void execute(File wsdlFile) {
                try {
                    urlText.setText(wsdlFile.toURI().toURL().toString());
                } catch (MalformedURLException ex) {
                    UTIL.log(ex);
                }
            }

            @Override
            public Display getDisplay() {
                return getShell().getDisplay();
            }
        };

        WsdlFileSelectionComposite.IURLSelectionCallback urlSelectionCallback = new WsdlFileSelectionComposite.IURLSelectionCallback() {

            @Override
            public void execute(URL url, SecurityType securityType, String userName, String password) {
                setProperty(ICredentialsCommon.SECURITY_TYPE_ID, securityType.name());
                setProperty(ICredentialsCommon.USERNAME_PROP_ID, userName);
                setProperty(ICredentialsCommon.PASSWORD_PROP_ID, password);

                urlText.setText(url.toString());
            }

            @Override
            public Display getDisplay() {
                return getShell().getDisplay();
            }
        };
        wsdlFileSelectionComposite.setCallbacks(fileSelectionCallback, fileSelectionCallback, urlSelectionCallback);

        // Add widgets to page
        Group descriptionGroup = new Group(scrolled, SWT.BORDER);
        GridLayoutFactory.fillDefaults().applyTo(descriptionGroup);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(descriptionGroup);

        Label descTitle = new Label(descriptionGroup, SWT.NONE);
        descTitle.setText(UTIL.getString("Common.Description")); //$NON-NLS-1$
        descTitle.setFont(JFaceResources.getBannerFont());
        GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(descTitle);

        Text descriptionText = new Text(descriptionGroup, SWT.WRAP | SWT.READ_ONLY);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(500, SWT.DEFAULT).applyTo(descriptionText);
        descriptionText.setText(UTIL.getString("WSSoapProfileDetailsWizardPage.descriptionMessage")); //$NON-NLS-1$
        descriptionText.setBackground(scrolled.getBackground());
        descriptionText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

        setPingButtonVisible(true);
        setPingButtonEnabled(false);
        setAutoConnectOnFinishDefault(false);
        setCreateAutoConnectControls(false);
        setShowAutoConnect(false);
        setShowAutoConnectOnFinish(false);
        setPageComplete(false);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        profileText.setText(wizard.getProfileName());
        descriptionText.setText(wizard.getProfileDescription());
       // setProperty(ICredentialsCommon.SECURITY_TYPE_ID, SecurityType.None.name());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        this.parent = parent;
        super.createControl(parent);
        updateState();
    }

    private void setProperty(String key,
                             String value) {
        if (value != null) {
            profileProperties.setProperty(key, value);
        }
    }

    private void updateState() {
        setPingButtonVisible(true);
        setPingButtonEnabled(false);

        String wsdlUrl = profileProperties.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID);
        if (null == wsdlUrl || wsdlUrl.isEmpty()) {
            setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
            return;
        }
        setErrorMessage(null);

        boolean urlError = true;
        try {
            new URL(wsdlUrl);
            urlError = false;
        } catch (MalformedURLException e) {
            // Exception not required to be logged
        }

        if (urlError) {
            File file = new File(wsdlUrl);

            if (!file.exists()) {
                setErrorMessage(UTIL.getString("Common.URLorFILE.Invalid.Message")); //$NON-NLS-1$
                return;
            }
        }

        setPingButtonEnabled(true);

        setErrorMessage(null);
        setMessage(UTIL.getString("Click.Next")); //$NON-NLS-1$

        // Reset the test status
        testStatus = Status.OK_STATUS;

        setPageComplete(isPageComplete());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        WSSoapConnectionProfileWizard wizard = (WSSoapConnectionProfileWizard)getWizard();
        Properties properties = wizard.getProfileProperties();
        String wsdlUrl = properties.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID);

        if (null == wsdlUrl || wsdlUrl.toString().isEmpty()) {
            return false;
        }

        if (!testStatus.isOK()) return false;

        final WSDLReader wsdlReader = wizard.getWsdlReader();

        String securityTypeValue = properties.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        String userName = properties.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        String password = properties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);

        if (securityTypeValue != null) {
            SecurityType securityType = SecurityType.valueOf(securityTypeValue);
            wsdlReader.setAuthenticationCredentials(securityType, userName, password);
        }

        wsdlReader.setWSDLUri(wsdlUrl);

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List getSummaryData() {
        List result = super.getSummaryData();

        String securityType = profileProperties.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (securityType == null) {
            securityType = SecurityType.None.name();
        }

        result.add(new String[] {
            UTIL.getString("Common.URL.Label"), profileProperties.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID)}); //$NON-NLS-1$

        if (!SecurityType.None.name().equals(securityType)) {
            result.add(new String[] {UTIL.getString("Common.SecurityType.Label"), securityType}); //$NON-NLS-1$
            result.add(new String[] {
                UTIL.getString("Common.Username.Label"), profileProperties.getProperty(ICredentialsCommon.USERNAME_PROP_ID)}); //$NON-NLS-1$

            // Mask the password
            String password = profileProperties.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
            StringBuffer masked = new StringBuffer(password.length());
            for (int i = 0; i < password.length(); ++i) {
                masked.append("*"); //$NON-NLS-1$
            }

            result.add(new String[] {UTIL.getString("Common.Password.Label"), masked.toString()}); //$NON-NLS-1$
        }

        return result;

    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#testConnection()
     */
    @Override
    protected void testConnection() {
        /* 
         * Want it to retest the connection not simply 
         * print the last error message
         */
        setErrorMessage(null);
        super.testConnection();
    }

    @Override
    protected Runnable createTestConnectionRunnable(final IConnectionProfile profile) {
        final URLPingJob pingJob = new URLPingJob(profile);
        pingJob.schedule();
        return new Runnable() {
            @Override
            public void run() {
                try {
                    pingJob.join();
                    testStatus = pingJob.getResult();

                    if (!testStatus.isOK()) {
                        Throwable exception = testStatus.getException();
                        setErrorMessage(exception.getMessage());
                    } else {

                        setMessage(UTIL.getString("WSSoapProfileDetailsWizardPage.validatingWsdlMessage")); //$NON-NLS-1$
                        UiBusyIndicator.showWhile(parent.getDisplay(), new Runnable() {

                            @Override
                            public void run() {
                                testStatus = wizard.getWsdlReader().validateWSDL(new NullProgressMonitor());
                            }
                        });

                        if (testStatus.isOK()) {
                            setMessage(UTIL.getString("WSSoapProfileDetailsWizardPage.validationSuccessfulWsdlMessage")); //$NON-NLS-1$
                        } else {
                            setErrorMessage(UTIL.getString("WSSoapProfileDetailsWizardPage.validationErrorWsdlMessage")); //$NON-NLS-1$
                        }
                    }

                    setPageComplete(isPageComplete());

                } catch (InterruptedException e) {
                }
            }
        };
    }

    /**
     * Executes a ping operation as a background job.
     */
    public class URLPingJob extends Job {

        private IConnectionProfile icp;

        /**
         * @param profile
         */
        public URLPingJob(IConnectionProfile profile) {
            super(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.job")); //$NON-NLS-1$
            setSystem(false);
            setUser(true);
            icp = profile;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.title"), //$NON-NLS-1$
                              IProgressMonitor.UNKNOWN);

            Exception exception = WSWizardUtils.testURLConnection(icp, IWSProfileConstants.WSDL_URI_PROP_ID);

            monitor.done();

            if (exception == null) return Status.OK_STATUS;
            else return new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
                                   ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure"), exception); //$NON-NLS-1$
        }
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.salesforce.ui;

import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.dialogs.ExceptionHandler;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.teiid.designer.datatools.salesforce.ISalesForceProfileConstants;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.Activator;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;

/**
 * 
 */
public class SalesForceProfileDetailsWizardPage extends ConnectionProfileDetailsPage
    implements Listener, ModelGeneratorSalesforceUiConstants {

    private Composite scrolled;

    private Label profileLabel;
    private Text profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Label usernameLabel;
    private Text usernameText;
    private Label passwordLabel;
    private Text passwordText;
    private Button urlCheckBox;
    private Text urlText;

    private boolean validatedConnection = false;

    /**
     * @param wizardPageName
     */
    public SalesForceProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("SalesForceProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/salesforce_wiz.gif"));
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

        profileText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        profileText.setLayoutData(gd);
        profileText.setText(((ConnectionProfileWizard)getWizard()).getProfileName());
        profileText.setEnabled(false);

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        descriptionLabel.setLayoutData(gd);

        descriptionText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(((ConnectionProfileWizard)getWizard()).getProfileDescription());
        descriptionText.setEnabled(false);

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

        urlCheckBox = new Button(scrolled, SWT.CHECK);
        urlCheckBox.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
        urlCheckBox.setToolTipText(UTIL.getString("Common.URL.CheckBox.ToolTip")); //$NON-NLS-1$
        urlCheckBox.setSelection(false);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        urlCheckBox.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        urlText.setText("https://test.salesforce.com/services/Soap/u/19.0"); //$NON-NLS-1$
        urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        urlText.setLayoutData(gd);
        urlText.setEditable(false);
        urlText.setEnabled(false);

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
        urlCheckBox.addListener(SWT.Selection, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {
        if (event.widget == urlCheckBox) {
            boolean enable = urlCheckBox.getSelection();
            urlText.setEnabled(enable);
            urlText.setEditable(enable);
            setValidatedConnection(false);
            if (!enable) {
                Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
                properties.remove(ISalesForceProfileConstants.URL_PROP_ID);
            }
        }

        if (event.widget == usernameText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ISalesForceProfileConstants.USERNAME_PROP_ID, usernameText.getText());
            setValidatedConnection(false);
        }
        if (event.widget == passwordText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID, passwordText.getText());
            setValidatedConnection(false);
        }
        if (event.widget == urlText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(ISalesForceProfileConstants.URL_PROP_ID, urlText.getText());
            setValidatedConnection(false);
        }
        updateState();
    }

    void updateState() {
        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(ISalesForceProfileConstants.USERNAME_PROP_ID)
            || properties.get(ISalesForceProfileConstants.USERNAME_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setErrorMessage(null);
        if (null == properties.get(ISalesForceProfileConstants.PASSWORD_PROP_ID)
            || properties.get(ISalesForceProfileConstants.PASSWORD_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setErrorMessage(null);
        if (urlCheckBox.getSelection()) {
            if (null == properties.get(ISalesForceProfileConstants.URL_PROP_ID)
                || properties.get(ISalesForceProfileConstants.URL_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
                setPingButtonEnabled(false);
                return;
            }
        } else {
            setErrorMessage(null);
        }
        if (!isValidatedConnection()) {
            setMessage(UTIL.getString("Click.Test.Connection")); //$NON-NLS-1$
            setPingButtonEnabled(true);
        } else {
            setPageComplete(true);
            setMessage(UTIL.getString("Click.Next")); //$NON-NLS-1$
        }

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
            && (null == properties.get(ISalesForceProfileConstants.USERNAME_PROP_ID) || properties.get(ISalesForceProfileConstants.USERNAME_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(ISalesForceProfileConstants.PASSWORD_PROP_ID) || properties.get(ISalesForceProfileConstants.PASSWORD_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && urlCheckBox.getSelection()
            && (null == properties.get(ISalesForceProfileConstants.URL_PROP_ID) || properties.get(ISalesForceProfileConstants.URL_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete && btnPing.isEnabled() && isValidatedConnection()) {
            complete = true;
        }
        return complete;
    }

    @Override
    public void testConnection() {
        super.testConnection();
    }

    @Override
    protected Runnable createTestConnectionRunnable( final IConnectionProfile profile ) {
        final Job pingJob = new SalesforcePingJob(getShell(), profile);
        pingJob.schedule();
        return new Runnable() {
            @Override
            public void run() {
                try {
                    pingJob.join();
                } catch (InterruptedException e) {
                }
            }
        };
    }

    /**
     * @param validatedConnection Sets validatedConnection to the specified value.
     */
    public void setValidatedConnection( boolean validatedConnection ) {
        this.validatedConnection = validatedConnection;
    }

    /**
     * @return validatedConnection
     */
    public boolean isValidatedConnection() {
        return validatedConnection;
    }

    /**
     * Executes a ping operation as a background job.
     */
    public class SalesforcePingJob extends Job {

        private IConnectionProfile icp;
        private Shell shell;

        /**
         * @param exceptions
         * @param name
         */
        public SalesforcePingJob( Shell shell,
                                  IConnectionProfile profile ) {
            super(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.job")); //$NON-NLS-1$
            setSystem(false);
            setUser(true);
            this.shell = shell;
            icp = profile;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            monitor.beginTask(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.title"), //$NON-NLS-1$
                              IProgressMonitor.UNKNOWN);

            IConnection con = createTestConnection(icp);

            monitor.done();

            new SalesforcePingUIJob(shell, getTestConnectionException(con)).schedule();

            if (con != null) con.close();

            return Status.OK_STATUS;
        }

        public IConnection createTestConnection( IConnectionProfile icp ) {
            if (icp == null) return null;
            return icp.createConnection(ConnectionProfileConstants.PING_FACTORY_ID);
        }

        public Throwable getTestConnectionException( IConnection conn ) {
            return conn != null ? conn.getConnectException() : new RuntimeException(
                                                                                    ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure")); //$NON-NLS-1$
        }

        public class SalesforcePingUIJob extends UIJob {

            private Shell shell;
            private Throwable exception;

            /**
             * @param name
             */
            public SalesforcePingUIJob( Shell shell,
                                        Throwable exception ) {
                super(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.uijob")); //$NON-NLS-1$
                setSystem(false);
                this.exception = exception;
                this.shell = shell;
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            public IStatus runInUIThread( IProgressMonitor monitor ) {
                showTestConnectionMessage(shell, exception);
                return Status.OK_STATUS;
            }

            public void showTestConnectionMessage( Shell shell,
                                                   Throwable exception ) {
                if (exception == null) {
                    MessageDialog.openInformation(shell,
                                                  ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.success"), //$NON-NLS-1$
                                                  ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.success")); //$NON-NLS-1$
                    setValidatedConnection(true);
                    updateState();
                } else {
                    ExceptionHandler.showException(shell,
                                                   ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.error"), //$NON-NLS-1$
                                                   ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure"), //$NON-NLS-1$
                                                   exception);
                    setValidatedConnection(false);
                    setErrorMessage(exception.getMessage());
                    updateState();

                }
            }

        }

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
        if (urlCheckBox.getSelection()) {
            result.add(new String[] {UTIL.getString("Common.URL.Label"), urlText.getText()}); //$NON-NLS-1$
        }
        return result;
    }
}

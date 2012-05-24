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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.dialogs.ExceptionHandler;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

import com.metamatrix.modeler.ui.wizards.wsdl.WsdlFileSelectionComposite;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class WSSoapProfileDetailsWizardPage  extends ConnectionProfileDetailsPage
		implements Listener, DatatoolsUiConstants {

	
    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Label urlLabel;
    private Text urlText;

    /**
     * @param wizardPageName
     */
    public WSSoapProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("WSSoapProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/full/obj16/web-service-cp.png")); //$NON-NLS-1$
    }

	@Override
	public void createCustomControl(Composite parent) {
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
        
        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        profileText.setLayoutData(gd);
        profileText.setText(((WSSoapConnectionProfileWizard)getWizard()).getProfileName());

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        descriptionText.setLayoutData(gd);
        String description = ((WSSoapConnectionProfileWizard)getWizard()).getProfileDescription();
        descriptionText.setText(description);
        descriptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URLorFILE.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URLorFILE.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        urlLabel.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        urlText.setEditable(false);
        urlText.setToolTipText(UTIL.getString("Common.URLorFILE.ToolTip")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        urlText.setLayoutData(gd);

        Label spacerLabel = new Label(scrolled, SWT.NONE);
        spacerLabel.setVisible(false);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(spacerLabel);

        WsdlFileSelectionComposite wsdlFileSelectionComposite = new WsdlFileSelectionComposite(
                scrolled, SWT.NONE);
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
                urlText.setText(url.toString());
                setAuthenticationProperties(securityType, userName, password);
            }

            @Override
            public Display getDisplay() {
                return getShell().getDisplay();
            }
        };
        wsdlFileSelectionComposite.setCallbacks(fileSelectionCallback, fileSelectionCallback, urlSelectionCallback);

		// Add widgets to page
		Group descriptionGroup = WidgetFactory.createGroup(scrolled, UTIL.getString("Common.Description"), GridData.FILL_HORIZONTAL, 3); //$NON-NLS-1$

        Text descriptionText = new Text(descriptionGroup,  SWT.WRAP | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 100;
        gd.widthHint = 500;
        descriptionText.setLayoutData(gd);
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
        addListeners();

	}
	
    /* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		super.setVisible(visible);
		
        String description = ((WSSoapConnectionProfileWizard)getWizard()).getProfileDescription();
        descriptionText.setText(description);
        
        profileText.setText(((WSSoapConnectionProfileWizard)getWizard()).getProfileName());
	}
	
    /**
     * 
     */
    private void addListeners() {
        urlText.addListener(SWT.Modify, this);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == urlText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            String urlStr = urlText.getText();
            if (urlStr != null) urlStr = urlStr.trim();
            properties.setProperty(IWSProfileConstants.WSDL_URI_PROP_ID, urlStr);
        }
        updateState();
    }
    
    /**
     * Used in the {@link IURLSelectionCallback} to record the 
     * authentication details if a remote URL was selected.
     * 
     * @param securityType
     * @param userName
     * @param password
     */
    private void setAuthenticationProperties(SecurityType securityType, String userName, String password) {
        Properties properties = ((NewConnectionProfileWizard) getWizard())
                .getProfileProperties();
        
        if (securityType != null) {
            properties.setProperty(IWSProfileConstants.SECURITY_TYPE_ID, securityType.name());
        }
        else {
            properties.setProperty(IWSProfileConstants.SECURITY_TYPE_ID,
                    SecurityType.None.name());
        }
        
        if (userName != null) {
            properties.setProperty(IWSProfileConstants.USERNAME_PROP_ID, userName);
        }
        
        if( password != null) {
            properties.setProperty(IWSProfileConstants.PASSWORD_PROP_ID, password);
        }
        
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
    
    void updateState() {
        setPingButtonVisible(true);
        setPingButtonEnabled(false);

        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(IWSProfileConstants.WSDL_URI_PROP_ID)
                || properties.get(IWSProfileConstants.WSDL_URI_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
                return;
        }
        setErrorMessage(null);
        
        boolean urlError = true;
        try {
        	@SuppressWarnings("unused")
			URL url = new URL(properties.get(IWSProfileConstants.WSDL_URI_PROP_ID).toString());
        	urlError = false;
        } catch(MalformedURLException e) {
        	
        }
        if( urlError ) {
            File file = new File(properties.get(IWSProfileConstants.WSDL_URI_PROP_ID).toString());

            if( !file.exists() && urlError) {
            	setErrorMessage(UTIL.getString("Common.URLorFILE.Invalid.Message")); //$NON-NLS-1$
            	return;
            }
        }

        setPingButtonEnabled(true);
        
        setErrorMessage(null);
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
	private boolean internalComplete(boolean complete) {
		Properties properties = ((NewConnectionProfileWizard) getWizard())
				.getProfileProperties();
		if (complete
				&& (null == properties.get(IWSProfileConstants.WSDL_URI_PROP_ID) || properties
						.get(IWSProfileConstants.WSDL_URI_PROP_ID).toString()
						.isEmpty())) {
			complete = false;
		}
		return complete;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List getSummaryData() {
        List result = super.getSummaryData();
    
        Properties properties = ((NewConnectionProfileWizard) getWizard()).getProfileProperties();
        String securityType = properties.getProperty(IWSProfileConstants.SECURITY_TYPE_ID);
        if (securityType == null) {
            securityType = SecurityType.None.name();
        }
        
        result.add(new String[] { UTIL.getString("Common.URL.Label"), properties.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID) }); //$NON-NLS-1$
        
        if (! SecurityType.None.name().equals(securityType)) {
            result.add(new String[] { UTIL.getString("Common.SecurityType.Label"), securityType }); //$NON-NLS-1$
            result.add(new String[] { UTIL.getString("Common.Username.Label"), properties.getProperty(IWSProfileConstants.USERNAME_PROP_ID) }); //$NON-NLS-1$

            // Mask the password
            String password = properties.getProperty(IWSProfileConstants.PASSWORD_PROP_ID);
            StringBuffer masked = new StringBuffer(password.length());
            for (int i = 0; i < password.length(); ++i) {
                masked.append("*"); //$NON-NLS-1$
            }

            result.add(new String[] {
                    UTIL.getString("Common.Password.Label"), masked.toString() }); //$NON-NLS-1$
        }
        
        return result;
    }
    
    @Override
    public void testConnection() {
        super.testConnection();
    }

    @Override
    protected Runnable createTestConnectionRunnable( final IConnectionProfile profile ) {
        final Job pingJob = new URLPingJob(getShell(), profile);
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
     * Executes a ping operation as a background job.
     */
    public class URLPingJob extends Job {

        private IConnectionProfile icp;
        private Shell shell;

        /**
         * @param exceptions
         * @param name
         */
        public URLPingJob( Shell shell,
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

            Exception exception = testXmlUrlConnection(icp);

            monitor.done();

            new XmlUrlPingUIJob(shell, exception).schedule();

            return Status.OK_STATUS;
        }

        public Exception testXmlUrlConnection( IConnectionProfile icp ) {
        	return WSWizardUtils.testURLConnection(icp, IWSProfileConstants.WSDL_URI_PROP_ID);
        }

        public Throwable getTestConnectionException( IConnection conn ) {
            return conn != null ? conn.getConnectException() : 
            	new RuntimeException(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure")); //$NON-NLS-1$
        }

        public class XmlUrlPingUIJob extends UIJob {

            private Shell shell;
            private Throwable exception;

            /**
             * @param name
             */
            public XmlUrlPingUIJob( Shell shell,
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
                    MessageDialog.openInformation(
                    	shell,                         
                    	ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.success"), //$NON-NLS-1$             
                    	ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.success")); //$NON-NLS-1$
                    //setValidatedConnection(true);
                    updateState();
                } else {
                    ExceptionHandler.showException(
                    	shell,
                        ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.error"), //$NON-NLS-1$
                        ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure"), //$NON-NLS-1$
                         exception);
                    //setValidatedConnection(false);
                    setErrorMessage(exception.getMessage());
                    updateState();

                }
            }

        }

    }
}
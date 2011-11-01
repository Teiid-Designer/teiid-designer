/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.xml;

import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.datatools.enablement.oda.xml.util.XMLSourceFromPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
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

import com.metamatrix.ui.internal.util.WidgetFactory;

public class XmlUrlProfileDetailsWizardPage  extends ConnectionProfileDetailsPage
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
    public XmlUrlProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("XmlUrlProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
    }

	@Override
	public void createCustomControl(Composite parent) {
        GridData gd;

        Group group = new Group(parent, SWT.BORDER);
        group.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
        group.setLayout(new FillLayout());
        group.setFont(JFaceResources.getBannerFont());

        scrolled = new Composite(group, SWT.SCROLL_PAGE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        scrolled.setLayout(gridLayout);

        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$
        
        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 1;
        profileText.setLayoutData(gd);
        profileText.setText(((XmlUrlConnectionProfileWizard)getWizard()).getProfileName());

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        descriptionText.setLayoutData(gd);
        String description = ((XmlUrlConnectionProfileWizard)getWizard()).getProfileDescription();
        descriptionText.setText(description);
        descriptionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        urlLabel.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        urlText.setLayoutData(gd);
        
		// Add widgets to page
		Group descriptionGroup = WidgetFactory.createGroup(scrolled, UTIL.getString("Common.Description"), GridData.FILL_HORIZONTAL, 3); //$NON-NLS-1$

        Text descriptionText = new Text(descriptionGroup,  SWT.WRAP | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.heightHint = 100;
        gd.widthHint = 300;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(UTIL.getString("XmlUrlProfileDetailsWizardPage.descriptionMessage")); //$NON-NLS-1$
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
            properties.setProperty(IXmlProfileConstants.URL_PROP_ID, urlText.getText());
        }

        updateState();
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
        if (null == properties.get(IXmlProfileConstants.URL_PROP_ID)
                || properties.get(IXmlProfileConstants.URL_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
                return;
        }
        setErrorMessage(null);
        try {
        	@SuppressWarnings("unused")
			URL url = new URL(properties.get(IXmlProfileConstants.URL_PROP_ID).toString());
        } catch(MalformedURLException e) {
        	setErrorMessage(UTIL.getString("Common.URL.Invalid.Message") + e.getMessage()); //$NON-NLS-1$
        	return;
        }
        
        // Check to see if URL ends with .xml ??
        String urlString = properties.get(IXmlProfileConstants.URL_PROP_ID).toString();
        
        if( !urlString.toUpperCase().endsWith(DOT_XML_UPPER_CASE) ) {
        	setErrorMessage(UTIL.getString("XmlProfileDetailsWizardPage.NotXmlUrl.Message", urlString)); //$NON-NLS-1$
        	return;
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
				&& (null == properties.get(IXmlProfileConstants.URL_PROP_ID) || 
				properties.get(IXmlProfileConstants.URL_PROP_ID).toString().isEmpty())) {
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
        result.add(new String[] {UTIL.getString("Common.URL.Label"), urlText.getText()}); //$NON-NLS-1$
        return result;
    }
    
    @Override
    public void testConnection() {
        super.testConnection();
    }

    @Override
    protected Runnable createTestConnectionRunnable( final IConnectionProfile profile ) {
        final Job pingJob = new XmlURLPingJob(getShell(), profile);
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
    public class XmlURLPingJob extends Job {

        private IConnectionProfile icp;
        private Shell shell;

        /**
         * @param exceptions
         * @param name
         */
        public XmlURLPingJob( Shell shell,
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
        	Properties connProperties = icp.getBaseProperties();
			//InputStream not provided, check XML file
			String xmlFile = connProperties == null ? null :(String) connProperties.get( IXmlProfileConstants.URL_PROP_ID );
			try {
				InputStream is = new XMLSourceFromPath(xmlFile).openInputStream();
				try
				{
					is.close( );
				}catch ( IOException e ) {
				}
			} catch (OdaException odaEx) {
				return odaEx;
			}
			
			return null;
        }

        public Throwable getTestConnectionException( IConnection conn ) {
            return conn != null ? conn.getConnectException() : new RuntimeException(
                                                                                    ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure")); //$NON-NLS-1$
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
                    MessageDialog.openInformation(shell,
                                                  ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.success"), //$NON-NLS-1$
                                                  ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.success")); //$NON-NLS-1$
                    //setValidatedConnection(true);
                    updateState();
                } else {
                    ExceptionHandler.showException(shell,
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

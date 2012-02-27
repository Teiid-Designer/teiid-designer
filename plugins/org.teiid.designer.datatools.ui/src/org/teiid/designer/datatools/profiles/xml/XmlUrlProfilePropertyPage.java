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
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.enablement.oda.xml.util.XMLSourceFromPath;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

public class XmlUrlProfilePropertyPage extends ProfileDetailsPropertyPage implements
		IContextProvider, DatatoolsUiConstants {

	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;

	private Label urlLabel;
	private Text urlText;


	public XmlUrlProfilePropertyPage() {
		super();
	}

	@Override
	public IContext getContext(Object target) {
		return contextProviderDelegate.getContext(target);
	}

	@Override
	public int getContextChangeMask() {
		return contextProviderDelegate.getContextChangeMask();
	}

	@Override
	public String getSearchExpression(Object target) {
		return contextProviderDelegate.getSearchExpression(target);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		this.setPingButtonVisible(true);
		return result;
	}

	@Override
	protected void createCustomContents(Composite parent) {
		GridData gd;

		Group group = new Group(parent, SWT.BORDER);
		group.setText(UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$
		FillLayout fl = new FillLayout();
		fl.type = SWT.HORIZONTAL;
		group.setLayout(new FillLayout());

		scrolled = new Composite(group, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		scrolled.setLayout(gridLayout);

		urlLabel = new Label(scrolled, SWT.NONE);
		urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
		urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		urlLabel.setLayoutData(gd);

		urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
		urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		urlText.setLayoutData(gd);

		initControls();
		addlisteners();
	}

	/**
* 
*/
	private void addlisteners() {

		urlText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

	}

	protected void validate() {
		String errorMessage = null;
		boolean valid = true;
		if (null == urlText.getText() || urlText.getText().isEmpty()) {
			errorMessage = UTIL.getString("Common.URL.Error.Message"); //$NON-NLS-1$
			valid = false;
		}
		setErrorMessage(errorMessage);
		this.setPingButtonEnabled(valid);
		setValid(valid);

	}

	/**
* 
*/
	private void initControls() {
		IConnectionProfile profile = getConnectionProfile();
		Properties props = profile.getBaseProperties();
		if (null != props.get(IXmlProfileConstants.URL_PROP_ID)) {
			urlText.setText((String) props.get(IXmlProfileConstants.URL_PROP_ID));
		}
		validate();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
	 */
	@Override
	protected Properties collectProperties() {
		Properties result = super.collectProperties();
		if (null == result) {
			result = new Properties();
		}
		result.setProperty(IXmlProfileConstants.URL_PROP_ID, urlText.getText());
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
				InputStream is = new XMLSourceFromPath(xmlFile, null).openInputStream();
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
                    validate();
                } else {
                    ExceptionHandler.showException(shell,
                                                   ConnectivityUIPlugin.getDefault().getResourceString("dialog.title.error"), //$NON-NLS-1$
                                                   ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure"), //$NON-NLS-1$
                                                   exception);
                    //setValidatedConnection(false);
                    setErrorMessage(exception.getMessage());
                    validate();

                }
            }

        }

    }
}

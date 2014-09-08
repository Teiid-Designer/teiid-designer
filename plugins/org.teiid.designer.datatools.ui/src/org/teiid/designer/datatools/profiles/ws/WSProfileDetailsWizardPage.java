/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ws;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.CredentialsComposite;

public class WSProfileDetailsWizardPage extends ConnectionProfileDetailsPage implements DatatoolsUiConstants {

	
    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private Text descriptionText;
    private Label urlPreviewLabel;
    Text urlPreviewText;
    private Label urlLabel;
    private Text urlText;
    private Label responseTypeLabel;
    private CredentialsComposite credentialsComposite;
    private Combo responseTypeCombo; 
    private Map<String, Parameter> parameterMap = new LinkedHashMap<String, Parameter>();

	private TabItem parametersTab;
    private TabItem headerPropertiesTab;
    ParameterPanel parameterPanel;

    /**
     * @param wizardPageName
     */
    public WSProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("WSProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/full/obj16/web-service-cp.png")); //$NON-NLS-1$
    }

	@Override
	public void createCustomControl(Composite parent) {
        GridData gd;

        Group group = WidgetFactory.createSimpleGroup(parent, UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$;

        scrolled = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.verticalSpacing = 10;
        scrolled.setLayout(gridLayout);

        profileLabel = new Label(scrolled, SWT.NONE);
        profileLabel.setText(UTIL.getString("Common.Profile.Label")); //$NON-NLS-1$

        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 1;
        profileText.setLayoutData(gd);
        profileText.setText(((ConnectionProfileWizard)getWizard()).getProfileName());

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createTextBox(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, GridData.FILL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        descriptionText.setLayoutData(gd);
        String description = ((ConnectionProfileWizard)getWizard()).getProfileDescription();
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
        gd.widthHint = 500;
        urlText.setLayoutData(gd);
        
        responseTypeLabel = new Label(scrolled, SWT.NONE);
        responseTypeLabel.setText(UTIL.getString("Common.ResponseType.Label")); //$NON-NLS-1$
        responseTypeLabel.setToolTipText(UTIL.getString("Common.ResponseType.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        responseTypeLabel.setLayoutData(gd);

        responseTypeCombo = WidgetFactory.createCombo(scrolled,
        		SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        responseTypeCombo.setLayoutData(gd);
        responseTypeCombo.setItems(new String[] { IWSProfileConstants.XML,
        		IWSProfileConstants.JSON });
        responseTypeCombo.select(0);
        responseTypeCombo.setText(IWSProfileConstants.XML);
    //    setProperty(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY, IWSProfileConstants.XML);
        responseTypeCombo.setToolTipText(UTIL.getString("Common.ResponseType.ToolTip"));  //$NON-NLS-1$
        GridDataFactory.swtDefaults().grab(false, false).applyTo(responseTypeCombo);
        responseTypeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleResponseTypeChanged(((Combo)e.widget).getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

        responseTypeCombo.setVisibleItemCount(2);
        
        credentialsComposite = new CredentialsComposite(scrolled, SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        credentialsComposite.setLayoutData(gd);
        this.profileProperties = ((NewConnectionProfileWizard) getWizard()).getProfileProperties();
        this.parameterMap = (Map) profileProperties.get(IWSProfileConstants.PARAMETER_MAP);
        
        urlPreviewLabel = new Label(scrolled, SWT.NONE);
        urlPreviewLabel.setText(UTIL.getString("WSProfileDetailsWizardPage.urlPreviewLabel")); //$NON-NLS-1$
        gd = new GridData();
        urlPreviewLabel.setLayoutData(gd);

        urlPreviewText = new Text(scrolled, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 40;
        gd.horizontalSpan = 3;
        urlPreviewText.setLayoutData(gd);
        urlPreviewText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        TabFolder tabFolder = new TabFolder(scrolled, SWT.TOP | SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        
		Composite parameterPanel = WidgetFactory.createPanel(tabFolder);
		this.parametersTab = new TabItem(tabFolder, SWT.FILL);
		this.parametersTab.setControl(parameterPanel);
		this.parametersTab.setText(UTIL.getString("ParametersPanel_groupTitle")); //$NON-NLS-1$
		this.parameterPanel = new ParameterPanel(this, parameterPanel, parameterMap, 6);
		this.urlPreviewText.setText(updateUrlPreview().toString());
		
		Composite headerPropertiesPanel = WidgetFactory.createPanel(tabFolder);
		this.headerPropertiesTab = new TabItem(tabFolder, SWT.FILL);
		this.headerPropertiesTab.setControl(headerPropertiesPanel);
		this.headerPropertiesTab.setText(UTIL.getString("HeaderPropertiesPanel_groupTitle")); //$NON-NLS-1$
        new HeaderPropertiesPanel(headerPropertiesPanel, profileProperties, 6);
        
        setPingButtonVisible(true);
        setPingButtonEnabled(false);
        setAutoConnectOnFinishDefault(false);
        setCreateAutoConnectControls(false);
        setShowAutoConnect(false);
        setShowAutoConnectOnFinish(false);
        setPageComplete(false);
        addListeners();

	}
	
	 void handleResponseTypeChanged(String type) {
		 setProperty(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY, type);
     }
	
	/**
	 * @return the parameterMap
	 */
	public Map<String, Parameter> getParameterMap() {
		return this.parameterMap;
	}

	/**
	 * @param parameterMap the parameterMap to set
	 */
	public void setParameterMap(Map<String, Parameter> parameterMap) {
		this.parameterMap = parameterMap;
	}

	private Properties profileProperties;
    /**
	 * @return the profileProperties
	 */
	public Properties getProfileProperties() {
		return this.profileProperties;
	}

	/**
	 * @param profileProperties the profileProperties to set
	 */
	public void setProfileProperties(Properties profileProperties) {
		this.profileProperties = profileProperties;
	}
	 
	/**
	 * @return
	 */
	StringBuilder updateUrlPreview() {
		StringBuilder previewUrl = new StringBuilder();
		String urlText = this.urlText.getText();
		if (urlText == null || urlText.trim().equals(StringUtilities.EMPTY_STRING)){
			urlText = "{base URL}"; //$NON-NLS-1$
		}
		String parameters = null;
		try {
			parameters = buildParameterString();
		} catch (UnsupportedEncodingException ex) {
		  	setErrorMessage(UTIL.getString("Common.URL.Invalid.Message") + ex.getMessage()); //$NON-NLS-1$
		}
		previewUrl.append(urlText).append(parameters);
		return previewUrl;
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String buildParameterString() throws UnsupportedEncodingException {
		
		StringBuilder parameterString = new StringBuilder();
		if (this.parameterMap==null) return parameterString.toString();
		Map<String, Parameter> parameterMap = this.parameterMap;

		for (String key : parameterMap.keySet()) {
	      Parameter value = parameterMap.get(key);
	      if (value.getType().equals(Parameter.Type.URI)) {
	    	  parameterString.append("/").append(value.getDefaultValue()); //$NON-NLS-1$
	      }
	      if (value.getType().equals(Parameter.Type.Query)) {
	    	  if (parameterString.length()==0 || !parameterString.toString().contains("?")){ //$NON-NLS-1$
	    		  parameterString.append("?");   //$NON-NLS-1$
	    	  }else{
	    		  parameterString.append("&");   //$NON-NLS-1$  
	    	  }
	    	  parameterString.append(key).append("=").append(value.getDefaultValue()); //$NON-NLS-1$
	      }
	    }

		return URLEncoder.encode(parameterString.toString(), Charset.defaultCharset().displayName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		super.setVisible(visible);

        String description = ((ConnectionProfileWizard)getWizard()).getProfileDescription();
        descriptionText.setText(description);
        
        profileText.setText(((ConnectionProfileWizard)getWizard()).getProfileName());
	}

    /**
     * 
     */
    private void addListeners() {
        urlText.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String urlStr = urlText.getText();
                if (urlStr != null) {
                    urlStr = urlStr.trim();
                }
                setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, urlStr);
            }
        });

        credentialsComposite.addSecurityOptionListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setProperty(ICredentialsCommon.SECURITY_TYPE_ID,
                        credentialsComposite.getSecurityOption().name());
            }
        });

        credentialsComposite.addUserNameListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setProperty(ICredentialsCommon.USERNAME_PROP_ID,
                        credentialsComposite.getUserName());
            }
        });

        credentialsComposite.addPasswordListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                setProperty(ICredentialsCommon.PASSWORD_PROP_ID,
                        credentialsComposite.getPassword());
            }
        });
    }

    private void setProperty(String propertyId, String value) {
        if (null == profileProperties) profileProperties = ((NewConnectionProfileWizard) getWizard()).getProfileProperties();
        profileProperties.setProperty(propertyId, value);
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

        this.profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        this.descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());
        this.urlPreviewText.setText(updateUrlPreview().toString());
        
        if( this.profileProperties ==  null ) {
        	this.profileProperties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        }
        
        if( this.profileProperties !=  null && this.parameterMap !=null) {
        	profileProperties.put(IWSProfileConstants.PARAMETER_MAP, this.parameterMap);
        }
        
        if (null != profileProperties) {
        	profileProperties.put(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY,responseTypeCombo.getText());
        }
        
        if (null == profileProperties.get(IWSProfileConstants.END_POINT_URI_PROP_ID)
                || profileProperties.get(IWSProfileConstants.END_POINT_URI_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
                return;
        }
        setErrorMessage(null);
        try {
        	@SuppressWarnings("unused")
			URL url = new URL(profileProperties.get(IWSProfileConstants.END_POINT_URI_PROP_ID).toString());
        } catch(MalformedURLException e) {
        	setErrorMessage(UTIL.getString("Common.URL.Invalid.Message") + e.getMessage()); //$NON-NLS-1$
        	return;
        }
        
        if (null != profileProperties.get(ICredentialsCommon.SECURITY_TYPE_ID) &&
        		!SecurityType.None.name().equals(profileProperties.get(ICredentialsCommon.SECURITY_TYPE_ID))) {
        	if (null == profileProperties.get(ICredentialsCommon.USERNAME_PROP_ID)
                    || profileProperties.get(ICredentialsCommon.USERNAME_PROP_ID).toString().isEmpty()) {
                    setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
                    return;
                }
                setErrorMessage(null);
                if (null == profileProperties.get(ICredentialsCommon.PASSWORD_PROP_ID)
                    || profileProperties.get(ICredentialsCommon.PASSWORD_PROP_ID).toString().isEmpty()) {
                    setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
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
				&& (null == properties.get(IWSProfileConstants.END_POINT_URI_PROP_ID) || properties
						.get(IWSProfileConstants.END_POINT_URI_PROP_ID).toString()
						.isEmpty())) {
			complete = false;
		}
		if (complete
				&& null != properties.get(ICredentialsCommon.SECURITY_TYPE_ID) && (!SecurityType.None.name().equals(
						properties.get(ICredentialsCommon.SECURITY_TYPE_ID)
								.toString()))) {
			if (complete
					&& (null == properties
							.get(ICredentialsCommon.USERNAME_PROP_ID) || properties
							.get(ICredentialsCommon.USERNAME_PROP_ID)
							.toString().isEmpty())) {
				complete = false;
			}
			if (complete
					&& (null == properties
							.get(ICredentialsCommon.PASSWORD_PROP_ID) || properties
							.get(ICredentialsCommon.PASSWORD_PROP_ID)
							.toString().isEmpty())) {
				complete = false;
			}

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
        result.add(new String[] {
                UTIL.getString("Common.Username.Label"), credentialsComposite.getUserName() }); //$NON-NLS-1$
        result.add(new String[] {
                UTIL.getString("Common.Security.Type.Label"), credentialsComposite.getSecurityOption().name() }); //$NON-NLS-1$
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
        	return WSWizardUtils.testURLConnection(icp, IWSProfileConstants.END_POINT_URI_PROP_ID);
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

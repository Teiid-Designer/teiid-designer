/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.print.attribute.standard.Severity;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributor;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.datatools.connectivity.ui.wizards.OptionalPropertiesPane;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.TeiidJDBCConnection;
import org.teiid.datatools.connectivity.TeiidServerJDBCURL;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * @since 8.0
 */
public class TeiidDriverUIContributor implements IDriverUIContributor, Listener {

    protected String VDB_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.VDB_LBL_UI_"); //$NON-NLS-1$

    private static final String SERVER_VERSION_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.SERVER_VERSION_LBL_UI_"); //$NON-NLS-1$

    private static final String HOST_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.HOST_LBL_UI_"); //$NON-NLS-1$

    private static final String PORT_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.PORT_LBL_UI_"); //$NON-NLS-1$

    private static final String CONNECTIONURL_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.CONNECTIONURL_LBL_UI_"); //$NON-NLS-1$

    private static final String USERNAME_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.USERNAME_LBL_UI_"); //$NON-NLS-1$

    private static final String PASSWORD_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.PASSWORD_LBL_UI_"); //$NON-NLS-1$

    private static final String URL_PROPERTIES = Messages.getString("TeiidDriverUIContributor.URL_PROPERTIES_ARG_LBL_UI_"); //$NON-NLS-1$

    private static final String SSL_BTN_UI_ = Messages.getString("TeiidDriverUIContributor.SSL_BTN_UI_"); //$NON-NLS-1$

    private static final String SAVE_PASSWORD_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.SAVE_PASSWORD_LBL_UI_"); //$NON-NLS-1$

    private static final String DATABASE_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.database"); //$NON-NLS-1$

    private static final String HOST_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.host"); //$NON-NLS-1$

    private static final String PORT_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.port"); //$NON-NLS-1$

    private static final String USERNAME_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.username"); //$NON-NLS-1$

    private static final String URL_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.url"); //$NON-NLS-1$

    private static final String SSL_SUMMARY_DATA_TEXT_ = TextProcessor.process(Messages.getString("TeiidDriverUIContributor.summary.protocol")); //$NON-NLS-1$

    private static final String SAVE_PASSWORD_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.persistpassword.label"); //$NON-NLS-1$

    private static final String TRUE_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.true"); //$NON-NLS-1$

    private static final String FALSE_SUMMARY_DATA_TEXT_ = Messages.getString("TeiidDriverUIContributor.summary.false"); //$NON-NLS-1$

    private Label serverVersionLabel;

    private Text serverVersionText;
    
    private Label databaseLabel;

    private Text databaseText;

    private Label hostLabel;

    private Text hostText;

    private Label portLabel;

    private Text portText;

    private Label usernameLabel;

    private Text usernameText;

    private Label passwordLabel;

    private Text passwordText;

    private Label urlPropertiesLabel;

    private Text urlPropertiesText;

    private Button protocolCheck;

    private Button savePasswordButton;

    private Label urlLabel;

    private Text urlText;
    
    private Text validationMessageText;

    private DialogPage parentPage;

    private ScrolledComposite parentComposite;

    private OptionalPropertiesPane optionalPropsComposite;

    private IDriverUIContributorInformation contributorInformation;

    private Properties properties;

    private boolean isReadOnly = false;
    
    @Override
	public Composite getContributedDriverUI( Composite parent,
                                             boolean isReadOnly ) {

        if ((parentComposite == null) || parentComposite.isDisposed() || (this.isReadOnly != isReadOnly)) {

            this.isReadOnly = isReadOnly;
            int additionalStyles = SWT.NONE;
            if (isReadOnly) {
                additionalStyles = SWT.READ_ONLY;
            }

            parentComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
            parentComposite.setExpandHorizontal(true);
            parentComposite.setExpandVertical(true);
            GridLayoutFactory.fillDefaults().applyTo(parentComposite);

            TabFolder tabComposite = new TabFolder(parentComposite, SWT.TOP);

            // add general tab
            TabItem generalTab = new TabItem(tabComposite, SWT.None);
            generalTab.setText(ConnectivityUIPlugin.getDefault().getResourceString("CommonDriverUIContributor.generaltab")); //$NON-NLS-1$

            Composite baseComposite = new Composite(tabComposite, SWT.NULL);
            GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).applyTo(baseComposite);
            generalTab.setControl(baseComposite);
            
            validationMessageText = new Text(baseComposite, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP );
            validationMessageText.setBackground(parent.getBackground());
            GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.BEGINNING).
                grab(true, false).hint(190, 20).applyTo(
                        validationMessageText);
            
            serverVersionLabel = new Label(baseComposite, SWT.NONE);
            serverVersionLabel.setText(SERVER_VERSION_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(serverVersionLabel);

            serverVersionText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(serverVersionText);
            
            databaseLabel = new Label(baseComposite, SWT.NONE);
            databaseLabel.setText(VDB_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(databaseLabel);

            databaseText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(databaseText);

            hostLabel = new Label(baseComposite, SWT.NONE);
            hostLabel.setText(HOST_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(hostLabel);

            hostText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(hostText);

            portLabel = new Label(baseComposite, SWT.NONE);
            portLabel.setText(PORT_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(portLabel);

            portText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(portText);
            
        	usernameLabel = new Label(baseComposite, SWT.NONE);
        	usernameLabel.setText(USERNAME_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(usernameLabel);
            
            usernameText = new Text(baseComposite, SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(usernameText);
            
            passwordLabel = new Label(baseComposite, SWT.NONE);
            passwordLabel.setText(PASSWORD_LBL_UI_);
            GridDataFactory.swtDefaults().span(1, 1).align(SWT.LEFT, SWT.CENTER).applyTo(passwordLabel);

            passwordText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD | additionalStyles);
            GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(passwordText);

            savePasswordButton = new Button(baseComposite, SWT.CHECK);
            savePasswordButton.setText(SAVE_PASSWORD_LBL_UI_);
            GridDataFactory.fillDefaults().span(1, 1).align(SWT.END, SWT.BEGINNING).applyTo(savePasswordButton);

            urlPropertiesLabel = new Label(baseComposite, SWT.NONE);
            urlPropertiesLabel.setText(URL_PROPERTIES);
            GridDataFactory.fillDefaults().span(3, 1).applyTo(urlPropertiesLabel);

            urlPropertiesText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            GridDataFactory.fillDefaults().span(3, 1).grab(true, false).applyTo(urlPropertiesText);

            urlLabel = new Label(baseComposite, SWT.NONE);
            urlLabel.setText(CONNECTIONURL_LBL_UI_);
            GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(urlLabel);

            protocolCheck = new Button(baseComposite, SWT.CHECK);
            protocolCheck.setText(SSL_BTN_UI_);
            protocolCheck.setSelection(false);
            GridDataFactory.fillDefaults().span(2, 1).align(SWT.END, SWT.BEGINNING).applyTo(protocolCheck);

            urlText = new Text(baseComposite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
            GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.BEGINNING).
                grab(true, false).hint(190, 50).applyTo(urlText);

            // add optional properties tab
            TabItem optionalPropsTab = new TabItem(tabComposite, SWT.None);
            optionalPropsTab.setText(ConnectivityUIPlugin.getDefault().getResourceString("CommonDriverUIContributor.optionaltab")); //$NON-NLS-1$
            optionalPropsComposite = new OptionalPropertiesPane(tabComposite, SWT.NULL, isReadOnly);
            optionalPropsTab.setControl(optionalPropsComposite);

            parentComposite.setContent(tabComposite);
            parentComposite.setMinSize(tabComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            initialize();
        }
        return parentComposite;
    }

    public void setConnectionInformation() {
    	// validate version #
    	String newServerText = this.serverVersionText.getText().trim();
    	if( newServerText == null || newServerText.isEmpty() ) {
    		setMessage(IStatus.ERROR, Messages.getString("TeiidDriverUIContributor.serverVersionCannotBeEmpty"));  //$NON-NLS-1$
    	} else {
    		TeiidServerVersionValidator validator = new TeiidServerVersionValidator(newServerText);
    		if( validator.getSeverity() == IStatus.OK ) {
    			setMessage(IStatus.OK, validator.getMessage());
    		} else {
    			setMessage(validator.getSeverity(), validator.getMessage());
    		}
    	}
    	properties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID, this.serverVersionText.getText().trim());
    	
        properties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, this.databaseText.getText().trim());

        properties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, this.usernameText.getText());
        
        String url = this.urlText.getText().trim();
        properties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, url);
        
        /* 
         * Avoid placing the password into properties that are persisted in the clear
         * by securely storing the password against the connection's url and a 1-way hash of the url and password
         */
        try {
            String passToken = ConnectivityUtil.generateHashToken(url, this.passwordText.getText());
            properties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, passToken);
            String urlStorageKey = ConnectivityUtil.buildSecureStorageKey(TeiidJDBCConnection.class, url, passToken);

            ConnectivityUtil.getSecureStorageProvider().storeInSecureStorage(
                                                                             urlStorageKey, 
                                                                             ConnectivityUtil.JDBC_PASSWORD, 
                                                                             this.passwordText.getText());
        } catch (Exception ex) {
            Activator.log(ex);
        }

        properties.setProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID,
                               String.valueOf(savePasswordButton.getSelection()));
        optionalPropsComposite.setConnectionInformation();
        this.contributorInformation.setProperties(properties);
    }
    
    private void setMessage( int severity, String message) {
    	switch(severity) {
    		case IStatus.OK: {
    			this.validationMessageText.setText("Click OK to save profile properties");
                this.validationMessageText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
                this.serverVersionLabel.setForeground(serverVersionLabel.getParent().getForeground());
    		} break;
			case IStatus.WARNING: {
				this.validationMessageText.setText(message);
				this.validationMessageText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
    		} break;
			case IStatus.ERROR: {
				this.validationMessageText.setText(message);
				this.validationMessageText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				this.serverVersionLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			} break;
    	}
    }

    public void updateURL() {
        String url = "jdbc:teiid:" + databaseText.getText().trim(); //$NON-NLS-1$
        url += "@"; //$NON-NLS-1$
        if (protocolCheck.getSelection()) {
            url += "mms://"; //$NON-NLS-1$
        } else {
            url += "mm://"; //$NON-NLS-1$
        }
        if (hostText.getText().trim().length() > 0) {
            url += hostText.getText().trim();
        }
        if (portText.getText().trim().length() > 0) {
            url += ":" + portText.getText().trim(); //$NON-NLS-1$
        }
        if (urlPropertiesText.getText().trim().length() > 0) {
            url += ";" + urlPropertiesText.getText().trim(); //$NON-NLS-1$
        }
        urlText.setText(url);
    }

    private void removeListeners() {
        serverVersionText.removeListener(SWT.Modify, this);
        hostText.removeListener(SWT.Modify, this);
        databaseText.removeListener(SWT.Modify, this);
        hostText.removeListener(SWT.Modify, this);
        portText.removeListener(SWT.Modify, this);
        usernameText.removeListener(SWT.Modify, this);
        passwordText.removeListener(SWT.Modify, this);
        urlPropertiesText.removeListener(SWT.Modify, this);
        protocolCheck.removeListener(SWT.Selection, this);
        savePasswordButton.removeListener(SWT.Selection, this);
    }

    private void addListeners() {
    	serverVersionText.addListener(SWT.Modify, this);
        databaseText.addListener(SWT.Modify, this);
        hostText.addListener(SWT.Modify, this);
        portText.addListener(SWT.Modify, this);
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        urlPropertiesText.addListener(SWT.Modify, this);
        protocolCheck.addListener(SWT.Selection, this);
        savePasswordButton.addListener(SWT.Selection, this);
    }

    private void initialize() {
        updateURL();
        addListeners();
    }

    @Override
	public void handleEvent( Event event ) {
        if (isReadOnly) {
            if (event.widget == savePasswordButton) {
                savePasswordButton.setSelection(!savePasswordButton.getSelection());
            } else if (event.widget == protocolCheck) {
                protocolCheck.setSelection(!protocolCheck.getSelection());
            }
        } else {
            updateURL();
            setConnectionInformation();
        }
    }

    @Override
	public boolean determineContributorCompletion() {
        boolean isComplete = true;
        if (databaseText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(Messages.getString("TeiidDriverUIContributor.VALIDATE_DATABASE_REQ_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (hostText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(Messages.getString("TeiidDriverUIContributor.VALIDATE_HOST_REQ_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (usernameText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(Messages.getString("TeiidDriverUIContributor.VALIDATE_USERID_REQ_MSG_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (portText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(Messages.getString("TeiidDriverUIContributor.VALIDATE_PORT_REQ_MSG_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (!optionalPropsComposite.validateControl(parentPage)) {
            isComplete = false;
        }
        if (isComplete) {
            parentPage.setErrorMessage(null);
        }
        return isComplete;
    }

    @Override
	public void setDialogPage( DialogPage parentPage ) {
        this.parentPage = parentPage;
    }

    @Override
	public void setDriverUIContributorInformation( IDriverUIContributorInformation contributorInformation ) {
        this.contributorInformation = contributorInformation;
        this.properties = contributorInformation.getProperties();
        optionalPropsComposite.setDriverUIContributorInformation(contributorInformation);
        
        if(parentPage instanceof TeiidProfileDetailsWizardPage) {
        	((TeiidProfileDetailsWizardPage)parentPage).setDriver(this.properties.getProperty(ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID));
        }
    }

    @Override
	public void loadProperties() {
        removeListeners();
        
        String version = this.properties.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID);
        
        if (version != null) {
        	TeiidServerVersionValidator validator = new TeiidServerVersionValidator(version);
        	if( validator.containsWildcards()) {
        		version = TeiidServerVersion.deriveUltimateDefaultServerVersion().toString();
        	}
        	serverVersionText.setText(version);
        	
        }
        
		TeiidServerJDBCURL url = new TeiidServerJDBCURL(
				this.properties
						.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID));
        hostText.setText(url.getNode());
        portText.setText(url.getPort());
        urlPropertiesText.setText(url.getProperties());
        databaseText.setText(url.getDatabaseName());

        String username = this.properties.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
        if (username != null) {
            usernameText.setText(username);
        }
        
        if (url.isSecureProtocol()) {
            protocolCheck.setSelection(true);
        } else {
            protocolCheck.setSelection(false);
        }
        String savePassword = this.properties.getProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID);
        if ((savePassword != null) && Boolean.valueOf(savePassword) == Boolean.TRUE) {
            savePasswordButton.setSelection(true);
        }

        // load optional connection properties
        optionalPropsComposite.loadProperties();

        updateURL();
        
        String urlString = urlText.getText().trim();
        /*
         * The pass token not the actual password is provided by the PASSWORD property. This provides a
         * reference to a node key made from a hash of the url and original password.
         */
        String passToken = this.properties.getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
        String urlStorageKey = ConnectivityUtil.buildSecureStorageKey(TeiidJDBCConnection.class, urlString, passToken);
        String password = null;
        
        /* Retrieve the password from the secure storage */
        try {
            password = ConnectivityUtil.getSecureStorageProvider().getFromSecureStorage(
                                                                                               urlStorageKey,
                                                                                               ConnectivityUtil.JDBC_PASSWORD);
        } catch (Exception ex) {
            Activator.log(ex);
        }
        
        if (password != null) {
            passwordText.setText(password);
        }
        
        addListeners();
        setConnectionInformation();
    }

    @Override
	public List getSummaryData() {
        List summaryData = new ArrayList();

        summaryData.add(new String[] {SERVER_VERSION_LBL_UI_, this.serverVersionText.getText().trim()});
        summaryData.add(new String[] {HOST_SUMMARY_DATA_TEXT_, this.hostText.getText().trim()});
        summaryData.add(new String[] {HOST_SUMMARY_DATA_TEXT_, this.hostText.getText().trim()});
        summaryData.add(new String[] {PORT_SUMMARY_DATA_TEXT_, this.portText.getText().trim()});

        summaryData.add(new String[] {USERNAME_SUMMARY_DATA_TEXT_, this.usernameText.getText().trim()});
        summaryData.add(new String[] {SSL_SUMMARY_DATA_TEXT_,
            protocolCheck.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {SAVE_PASSWORD_SUMMARY_DATA_TEXT_,
            savePasswordButton.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {URL_PROPERTIES, this.urlPropertiesText.getText().trim()});
        summaryData.add(new String[] {URL_SUMMARY_DATA_TEXT_, this.urlText.getText().trim()});
        return summaryData;
    }
    
    class TeiidServerVersionValidator {
    	String versionString;
    	int severity = IStatus.OK;
    	String message = Messages.getString("TeiidDriverUIContributor.serverVersionIsValid");  //$NON-NLS-1$
    	
    	public TeiidServerVersionValidator(String versionString) {
    		super();
    		this.versionString = versionString;
    		validate();
    	}
    	
    	private boolean containsWildcards() {
    		TeiidServerVersion version = null;
    		try {
				version = new TeiidServerVersion(versionString);
			} catch (IllegalArgumentException e) {
				return false;
			}
    		
    		return version.hasWildCards();
    	}
    	
    	private void validate() {
    		TeiidServerVersion version = null;
    		try {
				version = new TeiidServerVersion(versionString);
			} catch (IllegalArgumentException e) {
				message = e.getMessage();
				return;
			}
    		// Test major, minor, micro versions
    		
    		try {
				Integer.parseInt(version.getMajor());
			} catch (NumberFormatException e) {
				severity = IStatus.ERROR;
				message = Messages.getString("TeiidDriverUIContributor.majorVersionError.message")  //$NON-NLS-1$
						+ version.getMajor() + Messages.getString("TeiidDriverUIContributor.isNotAnInteger.message"); //$NON-NLS-1$
				return;
			}
    		
    		try {
				Integer.parseInt(version.getMinor());
			} catch (NumberFormatException e) {
				severity = IStatus.ERROR;
				message = Messages.getString("TeiidDriverUIContributor.minorVersionError.message")  //$NON-NLS-1$
						 + version.getMinor() + Messages.getString("TeiidDriverUIContributor.isNotAnInteger.message"); //$NON-NLS-1$
				return;
			}
    		
    		try {
				Integer.parseInt(version.getMicro());
			} catch (NumberFormatException e) {
				severity = IStatus.ERROR;
				message = Messages.getString("TeiidDriverUIContributor.microVersionError.message")  //$NON-NLS-1$
						+ version.getMicro() + Messages.getString("TeiidDriverUIContributor.isNotAnInteger.message"); //$NON-NLS-1$
				return;
			}
    	}
    	
    	public int getSeverity() {
    		return severity;
    	}
    	
    	public String getMessage() {
    		return message;
    	}
    }
}

package org.teiid.designer.datatools.profiles.modeshape;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributor;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

public class ModeShapeDriverUIContributor implements IDriverUIContributor, Listener {

    private static final String HOST_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.HOST_LBL_UI_"); //$NON-NLS-1$

    private static final String PORT_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.PORT_LBL_UI_"); //$NON-NLS-1$

    private static final String CONNECTIONURL_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.CONNECTIONURL_LBL_UI_"); //$NON-NLS-1$

    private static final String USERNAME_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.USERNAME_LBL_UI_"); //$NON-NLS-1$

    private static final String PASSWORD_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.PASSWORD_LBL_UI_"); //$NON-NLS-1$

    private static final String SSL_BTN_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.SSL_BTN_UI_"); //$NON-NLS-1$

    private static final String SAVE_PASSWORD_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.SAVE_PASSWORD_LBL_UI_"); //$NON-NLS-1$

	private static final String PATH_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.PATH_LBL_UI_"); //$NON-NLS-1$

    private static final String HOST_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.host"); //$NON-NLS-1$

    private static final String PORT_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.port"); //$NON-NLS-1$

    private static final String USERNAME_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.username"); //$NON-NLS-1$

    private static final String URL_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.url"); //$NON-NLS-1$

    private static final String SSL_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.protocol"); //$NON-NLS-1$

    private static final String SAVE_PASSWORD_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.persistpassword.label"); //$NON-NLS-1$

    private static final String TRUE_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.true"); //$NON-NLS-1$

    private static final String FALSE_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.false"); //$NON-NLS-1$

	private static final String PATH_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.summary.path"); //$NON-NLS-1$
    
	private boolean isReadOnly = false;
	
	private ScrolledComposite parentComposite;

	private Label hostLabel;

	private Text hostText;

	private Label portLabel;

	private Text portText;

	private Label usernameLabel;

	private Text usernameText;

	private Label passwordLabel;

	private Text passwordText;

	private Button protocolCheck;

	private Button savePasswordButton;

	private Label urlLabel;

	private Text urlText;

	private Label pathLabel;

	private Text pathText;
	
    private Properties properties;

    private DialogPage parentPage;

	private IDriverUIContributorInformation contributorInformation;
    
	@Override
	public Composite getContributedDriverUI(Composite parent, boolean isReadOnly) {
        if ((parentComposite == null) || parentComposite.isDisposed() || (this.isReadOnly  != isReadOnly)) {
            GridData gd;

            this.isReadOnly = isReadOnly;
            int additionalStyles = SWT.NONE;
            if (isReadOnly) {
                additionalStyles = SWT.READ_ONLY;
            }

            parentComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
            parentComposite.setExpandHorizontal(true);
            parentComposite.setExpandVertical(true);
            parentComposite.setLayout(new GridLayout());

            TabFolder tabComposite = new TabFolder(parentComposite, SWT.TOP);

            // add general tab
            TabItem generalTab = new TabItem(tabComposite, SWT.None);
            generalTab.setText(ConnectivityUIPlugin.getDefault().getResourceString("CommonDriverUIContributor.generaltab")); //$NON-NLS-1$

            Composite baseComposite = new Composite(tabComposite, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            baseComposite.setLayout(layout);
            generalTab.setControl(baseComposite);

            hostLabel = new Label(baseComposite, SWT.NONE);
            hostLabel.setText(HOST_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            hostLabel.setLayoutData(gd);

            hostText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.horizontalSpan = 2;
            gd.grabExcessHorizontalSpace = true;
            hostText.setLayoutData(gd);

            portLabel = new Label(baseComposite, SWT.NONE);
            portLabel.setText(PORT_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            portLabel.setLayoutData(gd);

            portText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            portText.setLayoutData(gd);
            
            pathLabel = new Label(baseComposite, SWT.NONE);
            pathLabel.setText(PATH_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            pathLabel.setLayoutData(gd);

            pathText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            pathText.setLayoutData(gd);

            usernameLabel = new Label(baseComposite, SWT.NONE);
            usernameLabel.setText(USERNAME_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            usernameLabel.setLayoutData(gd);

            usernameText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            usernameText.setLayoutData(gd);

            passwordLabel = new Label(baseComposite, SWT.NONE);
            passwordLabel.setText(PASSWORD_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            passwordLabel.setLayoutData(gd);

            passwordText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD | additionalStyles);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            passwordText.setLayoutData(gd);

            protocolCheck = new Button(baseComposite, SWT.CHECK);
            protocolCheck.setText(SSL_BTN_UI_);
            protocolCheck.setSelection(false);
            gd = new GridData();
            gd.horizontalSpan = 3;
            protocolCheck.setLayoutData(gd);

            savePasswordButton = new Button(baseComposite, SWT.CHECK);
            savePasswordButton.setText(SAVE_PASSWORD_LBL_UI_);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.horizontalSpan = 3;
            gd.grabExcessHorizontalSpace = true;
            savePasswordButton.setLayoutData(gd);

            urlLabel = new Label(baseComposite, SWT.NONE);
            urlLabel.setText(CONNECTIONURL_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            urlLabel.setLayoutData(gd);

            urlText = new Text(baseComposite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            gd.widthHint = 190;
            gd.heightHint = 90;
            urlText.setLayoutData(gd);

            parentComposite.setContent(tabComposite);
            parentComposite.setMinSize(tabComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            initialize();
        }
        return parentComposite;
    }

    private void initialize() {
        updateURL();
        addListeners();
    }

    public void updateURL() {
        String url = "jdbc:jcr:"; //$NON-NLS-1$
        if (protocolCheck.getSelection()) {
            url += "https://"; //$NON-NLS-1$
        } else {
            url += "http://"; //$NON-NLS-1$
        }
        if (hostText.getText().trim().length() > 0) {
            url += hostText.getText().trim();
        }
        if (portText.getText().trim().length() > 0) {
            url += ":" + portText.getText().trim(); //$NON-NLS-1$
        }
        if (pathText.getText().trim().length() > 0) {
            url += pathText.getText().trim();
        }
        urlText.setText(url);
    }

    private void removeListeners() {
        hostText.removeListener(SWT.Modify, this);
        portText.removeListener(SWT.Modify, this);
        pathText.removeListener(SWT.Modify, this);
        usernameText.removeListener(SWT.Modify, this);
        passwordText.removeListener(SWT.Modify, this);
        protocolCheck.removeListener(SWT.Selection, this);
        savePasswordButton.removeListener(SWT.Selection, this);
    }

    private void addListeners() {
        hostText.addListener(SWT.Modify, this);
        portText.addListener(SWT.Modify, this);
        pathText.addListener(SWT.Modify, this);
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        protocolCheck.addListener(SWT.Selection, this);
        savePasswordButton.addListener(SWT.Selection, this);
    }

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

    public void setConnectionInformation() {
        // TODO: test to see if this is an issue, might need to stuff something in
    	//properties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, this.databaseText.getText().trim());
        properties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, this.passwordText.getText());

        properties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, this.usernameText.getText());
        properties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, this.urlText.getText().trim());

        properties.setProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID,
                               String.valueOf(savePasswordButton.getSelection()));
        this.contributorInformation.setProperties(properties);
    }

	@Override
    public boolean determineContributorCompletion() {
        boolean isComplete = true;
        if (hostText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_HOST_REQ_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (portText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_PORT_REQ_MSG_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (pathText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_PATH_REQ_UI_")); //$NON-NLS-1$
            isComplete = false;
        }
        else if (usernameText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_USERID_REQ_MSG_UI_")); //$NON-NLS-1$
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
	public void setDriverUIContributorInformation(
			IDriverUIContributorInformation contributorInformation) {
		this.contributorInformation = contributorInformation;
		this.properties = contributorInformation.getProperties();
	}

	@Override
	public void loadProperties() {
        removeListeners();
		ModeShapeJdbcUrl url = new ModeShapeJdbcUrl(
				this.properties
						.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID));
        hostText.setText(url.getHost());
        portText.setText(url.getPort());
        pathText.setText(url.getPath());

        String username = this.properties.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
        if (username != null) {
            usernameText.setText(username);
        }
        String password = this.properties.getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
        if (password != null) {
            passwordText.setText(password);
        }
        if (!(url.getProtocol().equals("https"))) { //$NON-NLS-1$
            protocolCheck.setSelection(false);
        } else {
            protocolCheck.setSelection(true);
        }
        String savePassword = this.properties.getProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID);
        if ((savePassword != null) && Boolean.valueOf(savePassword) == Boolean.TRUE) {
            savePasswordButton.setSelection(true);
        }


        updateURL();
        addListeners();
        setConnectionInformation();
    }
	@SuppressWarnings({ "unchecked" })
	@Override
    public List getSummaryData() {
        List summaryData = new ArrayList();

        //summaryData.add(new String[] {DATABASE_SUMMARY_DATA_TEXT_, this.databaseText.getText().trim()});
        summaryData.add(new String[] {HOST_SUMMARY_DATA_TEXT_, this.hostText.getText().trim()});
        summaryData.add(new String[] {PORT_SUMMARY_DATA_TEXT_, this.portText.getText().trim()});
        summaryData.add(new String[] {PATH_SUMMARY_DATA_TEXT_, this.pathText.getText().trim()});

        summaryData.add(new String[] {USERNAME_SUMMARY_DATA_TEXT_, this.usernameText.getText().trim()});
        summaryData.add(new String[] {SSL_SUMMARY_DATA_TEXT_,
            protocolCheck.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {SAVE_PASSWORD_SUMMARY_DATA_TEXT_,
            savePasswordButton.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {URL_SUMMARY_DATA_TEXT_, this.urlText.getText().trim()});
        return summaryData;
    }
}

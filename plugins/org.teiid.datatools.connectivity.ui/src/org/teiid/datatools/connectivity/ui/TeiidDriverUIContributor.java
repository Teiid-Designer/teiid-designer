package org.teiid.datatools.connectivity.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributor;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.datatools.connectivity.ui.wizards.OptionalPropertiesPane;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.osgi.util.TextProcessor;
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

public class TeiidDriverUIContributor implements IDriverUIContributor, Listener {

    protected String VDB_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.VDB_LBL_UI_"); //$NON-NLS-1$

    private static final String HOST_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.HOST_LBL_UI_"); //$NON-NLS-1$

    private static final String PORT_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.PORT_LBL_UI_"); //$NON-NLS-1$

    private static final String CONNECTIONURL_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.CONNECTIONURL_LBL_UI_"); //$NON-NLS-1$

    private static final String USERNAME_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.USERNAME_LBL_UI_"); //$NON-NLS-1$

    private static final String PASSWORD_LBL_UI_ = Messages.getString("TeiidDriverUIContributor.PASSWORD_LBL_UI_"); //$NON-NLS-1$

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

    private static final String PROTOCOL_SSL_TRUE_TEXT = "mms://"; //$NON-NLS-1$

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

    private Button protocolCheck;

    private Button savePasswordButton;

    private Label urlLabel;

    private Text urlText;

    private DialogPage parentPage;

    private ScrolledComposite parentComposite;

    private OptionalPropertiesPane optionalPropsComposite;

    private IDriverUIContributorInformation contributorInformation;

    private Properties properties;

    private boolean isReadOnly = false;

    public Composite getContributedDriverUI( Composite parent,
                                             boolean isReadOnly ) {

        if ((parentComposite == null) || parentComposite.isDisposed() || (this.isReadOnly != isReadOnly)) {
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

            databaseLabel = new Label(baseComposite, SWT.NONE);
            databaseLabel.setText(VDB_LBL_UI_);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            databaseLabel.setLayoutData(gd);

            databaseText = new Text(baseComposite, SWT.SINGLE | SWT.BORDER | additionalStyles);
            gd = new GridData();
            gd.verticalAlignment = GridData.BEGINNING;
            gd.horizontalAlignment = GridData.FILL;
            gd.horizontalSpan = 2;
            databaseText.setLayoutData(gd);

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
        properties.setProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, this.databaseText.getText().trim());
        properties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, this.passwordText.getText());

        properties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, this.usernameText.getText());
        properties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, this.urlText.getText().trim());

        properties.setProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID,
                               String.valueOf(savePasswordButton.getSelection()));
        optionalPropsComposite.setConnectionInformation();
        this.contributorInformation.setProperties(properties);
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
        urlText.setText(url);
    }

    private void removeListeners() {
        databaseText.removeListener(SWT.Modify, this);
        hostText.removeListener(SWT.Modify, this);
        portText.removeListener(SWT.Modify, this);
        usernameText.removeListener(SWT.Modify, this);
        passwordText.removeListener(SWT.Modify, this);
        protocolCheck.removeListener(SWT.Selection, this);
        savePasswordButton.removeListener(SWT.Selection, this);
    }

    private void addListeners() {
        databaseText.addListener(SWT.Modify, this);
        hostText.addListener(SWT.Modify, this);
        portText.addListener(SWT.Modify, this);
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        protocolCheck.addListener(SWT.Selection, this);
        savePasswordButton.addListener(SWT.Selection, this);
    }

    private void initialize() {
        updateURL();
        addListeners();
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

    public void setDialogPage( DialogPage parentPage ) {
        this.parentPage = parentPage;
    }

    public void setDriverUIContributorInformation( IDriverUIContributorInformation contributorInformation ) {
        this.contributorInformation = contributorInformation;
        this.properties = contributorInformation.getProperties();
        optionalPropsComposite.setDriverUIContributorInformation(contributorInformation);
    }

    public void loadProperties() {
        removeListeners();
        TeiidServerJDBCURL url = new TeiidServerJDBCURL(this.properties.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID));
        hostText.setText(url.getNode());
        portText.setText(url.getPort());
        databaseText.setText(url.getDatabaseName());

        String username = this.properties.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
        if (username != null) {
            usernameText.setText(username);
        }
        String password = this.properties.getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
        if (password != null) {
            passwordText.setText(password);
        }
        if (!(url.getProperties().indexOf(PROTOCOL_SSL_TRUE_TEXT) > -1)) {
            protocolCheck.setSelection(false);
        } else {
            protocolCheck.setSelection(true);
        }
        String savePassword = this.properties.getProperty(IJDBCConnectionProfileConstants.SAVE_PASSWORD_PROP_ID);
        if ((savePassword != null) && Boolean.valueOf(savePassword) == Boolean.TRUE) {
            savePasswordButton.setSelection(true);
        }

        // load optional connection properties
        optionalPropsComposite.loadProperties();

        updateURL();
        addListeners();
        setConnectionInformation();
    }

    public List getSummaryData() {
        List summaryData = new ArrayList();

        summaryData.add(new String[] {DATABASE_SUMMARY_DATA_TEXT_, this.databaseText.getText().trim()});
        summaryData.add(new String[] {HOST_SUMMARY_DATA_TEXT_, this.hostText.getText().trim()});
        summaryData.add(new String[] {PORT_SUMMARY_DATA_TEXT_, this.portText.getText().trim()});

        summaryData.add(new String[] {USERNAME_SUMMARY_DATA_TEXT_, this.usernameText.getText().trim()});
        summaryData.add(new String[] {SSL_SUMMARY_DATA_TEXT_,
            protocolCheck.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {SAVE_PASSWORD_SUMMARY_DATA_TEXT_,
            savePasswordButton.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {URL_SUMMARY_DATA_TEXT_, this.urlText.getText().trim()});
        return summaryData;
    }

    private class TeiidServerJDBCURL {

        private String node = ""; //$NON-NLS-1$

        private String port = ""; //$NON-NLS-1$

        private String vdbName = ""; //$NON-NLS-1$

        private String properties = ""; //$NON-NLS-1$

        /**
         * @param url
         */
        public TeiidServerJDBCURL( String url ) {
            if (url != null) {
                parseURL(url);
            }
        }

        /**
         * @return Returns the databaseName.
         */
        public String getDatabaseName() {
            return vdbName;
        }

        /**
         * @return Returns the node.
         */
        public String getNode() {
            return node;
        }

        private void parseURL( String url ) {
            // jdbc:teiid:<vdb-name>@mm[s]://<host>:<port>;[prop-name=prop-value;]*
            try {
                String remainingURL = url.substring(url.indexOf(':') + 1);
                remainingURL = remainingURL.substring(url.indexOf(':') + 2);
                this.vdbName = remainingURL.substring(0, remainingURL.indexOf('@'));
                remainingURL = remainingURL.substring(remainingURL.indexOf('@') + 1);
                remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 3);
                this.node = remainingURL.substring(0, remainingURL.indexOf(':'));
                remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 1);

                if (remainingURL.indexOf(';') > -1) {
                    // there are connection properties
                    // TODO: how do we want to handle these? As checkboxes or test fields?
                    this.node = remainingURL.substring(0, remainingURL.indexOf(';'));
                    remainingURL = remainingURL.substring(remainingURL.indexOf(';') + 1);
                    this.properties = remainingURL;

                } else {
                    this.port = remainingURL;
                }
            } catch (Exception e) {
            }
        }

        /**
         * @return Returns the port.
         */
        public String getPort() {
            return port;
        }

        /**
         * @return Returns the properties.
         */
        public String getProperties() {
            return properties;
        }
    }
}

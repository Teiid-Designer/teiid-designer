package org.teiid.designer.datatools.profiles.modeshape;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributor;
import org.eclipse.datatools.connectivity.ui.wizards.IDriverUIContributorInformation;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

import com.metamatrix.ui.internal.util.WidgetFactory;

public class ModeShapeDriverUIContributor implements IDriverUIContributor, Listener {

    private static final String HOST_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.HOST_LBL_UI_"); //$NON-NLS-1$

    private static final String PORT_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.PORT_LBL_UI_"); //$NON-NLS-1$

    private static final String CONNECTIONURL_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.CONNECTIONURL_LBL_UI_"); //$NON-NLS-1$

    private static final String USERNAME_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.USERNAME_LBL_UI_"); //$NON-NLS-1$

    private static final String PASSWORD_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.PASSWORD_LBL_UI_"); //$NON-NLS-1$

    private static final String SSL_BTN_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.SSL_BTN_UI_"); //$NON-NLS-1$
    
    private static final String TEIID_BTN_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.TEIID_BTN_UI_"); //$NON-NLS-1$

    private static final String BROWSE_BUTTON_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.BROWSE_BUTTON_LBL_UI_"); //$NON-NLS-1$
    
    private static final String SAVE_PASSWORD_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("Common.SAVE_PASSWORD_LBL_UI_"); //$NON-NLS-1$

	private static final String PATH_LBL_UI_ = DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.REPOS.TXT"); //$NON-NLS-1$

    private static final String HOST_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.host"); //$NON-NLS-1$

    private static final String PORT_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.port"); //$NON-NLS-1$

    private static final String USERNAME_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.username"); //$NON-NLS-1$

    private static final String URL_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.url"); //$NON-NLS-1$

    private static final String SSL_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.protocol"); //$NON-NLS-1$
    
    private static final String TEIID_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.teiid"); //$NON-NLS-1$

    private static final String SAVE_PASSWORD_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.persistpassword.label"); //$NON-NLS-1$

    private static final String TRUE_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.true"); //$NON-NLS-1$

    private static final String FALSE_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.false"); //$NON-NLS-1$

	private static final String PATH_SUMMARY_DATA_TEXT_ = DatatoolsUiPlugin.UTIL.getString("Common.summary.path"); //$NON-NLS-1$
    
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
	
	private Button teiidCheck;

	private Button savePasswordButton;

	private Label urlLabel;

	private Text urlText;

	private Label reposLabel;

	private Combo reposCombo;
	
    private Properties properties;

    private DialogPage parentPage;

	private IDriverUIContributorInformation contributorInformation;

	private Button reposBrowseButton;

	private LabelProvider reposLabelProvider;
    
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
            layout.numColumns = 3;
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
            gd.horizontalSpan = 3;
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
            gd.horizontalSpan = 3;
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
            gd.horizontalSpan = 3;
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
            gd.horizontalSpan = 3;
            passwordText.setLayoutData(gd);

            reposLabel = new Label(baseComposite, SWT.NONE);
            reposLabel.setText(PATH_LBL_UI_);
            gd = new GridData();
            gd.horizontalSpan = 3;
            gd.verticalAlignment = GridData.BEGINNING;
            reposLabel.setLayoutData(gd);

            reposLabelProvider = new LabelProvider() {

                @Override
                public String getText( final Object source ) {
                    return (String)source;
                }
            };
            reposCombo = WidgetFactory.createCombo(baseComposite,
                    SWT.SIMPLE,
                    GridData.FILL_HORIZONTAL,
                    new ArrayList<String>(),
                    reposLabelProvider,
                    true);
            gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            reposCombo.setLayoutData(gd);

            reposBrowseButton = new Button(baseComposite, SWT.BUTTON1);
            reposBrowseButton.setText(BROWSE_BUTTON_LBL_UI_);
            gd = new GridData();
            gd.horizontalAlignment = GridData.CENTER;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.horizontalSpan = 1;
            gd.grabExcessHorizontalSpace = false;
            reposBrowseButton.setLayoutData(gd);
            reposBrowseButton.setEnabled(false);

            protocolCheck = new Button(baseComposite, SWT.CHECK);
            protocolCheck.setText(SSL_BTN_UI_);
            protocolCheck.setSelection(false);
            gd = new GridData();
            gd.horizontalSpan = 3;
            protocolCheck.setLayoutData(gd);
            
            teiidCheck = new Button(baseComposite, SWT.CHECK);
            teiidCheck.setText(TEIID_BTN_UI_);
            teiidCheck.setSelection(true);
            gd = new GridData();
            gd.horizontalSpan = 3;
            teiidCheck.setLayoutData(gd);

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
            url += ":" + portText.getText().trim() + "/modeshape-rest"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (reposCombo.getText().trim().length() > 0) {
            String repos = reposCombo.getText().trim();
            if(repos.indexOf('/') == 0) {
            	url += repos;
            } else {
            	url += "/" + repos; //$NON-NLS-1$
            }
        }
        if (teiidCheck.getSelection()) {
        	url += "?teiidsupport=true"; //$NON-NLS-1$
        } 
        urlText.setText(url);
    }
    
    private String getRestUrl() {
    	StringBuffer url = new StringBuffer();
    	if (protocolCheck.getSelection()) {
            url.append("https://"); //$NON-NLS-1$
        } else {
            url.append("http://"); //$NON-NLS-1$
        }
        if (hostText.getText().trim().length() > 0) {
            url.append(hostText.getText().trim());
        }
        if (portText.getText().trim().length() > 0) {
            url.append(":").append(portText.getText().trim()); //$NON-NLS-1$
        }
        url.append("/modeshape-rest/"); //$NON-NLS-1$
        return url.toString();
    }

    private void removeListeners() {
        hostText.removeListener(SWT.Modify, this);
        portText.removeListener(SWT.Modify, this);
        reposCombo.removeListener(SWT.Modify, this);
        usernameText.removeListener(SWT.Modify, this);
        passwordText.removeListener(SWT.Modify, this);
        reposBrowseButton.removeListener(SWT.Selection, this);
        protocolCheck.removeListener(SWT.Selection, this);
        teiidCheck.removeListener(SWT.Selection, this);
        savePasswordButton.removeListener(SWT.Selection, this);
    }

    private void addListeners() {
        hostText.addListener(SWT.Modify, this);
        portText.addListener(SWT.Modify, this);
        reposCombo.addListener(SWT.Modify, this);
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        reposBrowseButton.addListener(SWT.Selection, this);
        protocolCheck.addListener(SWT.Selection, this);
        teiidCheck.addListener(SWT.Selection, this);
        savePasswordButton.addListener(SWT.Selection, this);
    }

    public void handleEvent( Event event ) {
    	if (event.widget == savePasswordButton) {
    		savePasswordButton.setSelection(savePasswordButton.getSelection());
    	} else if (event.widget == protocolCheck) {
    		protocolCheck.setSelection(protocolCheck.getSelection());
    	} else if (event.widget == teiidCheck) {
    		teiidCheck.setSelection(teiidCheck.getSelection());
    	} else if (event.widget == reposBrowseButton) {
    		browseForRepos();
    	}
    	updateURL();
    	updateBrowseButtonEnablement();
    	setConnectionInformation();
    }

	private void updateBrowseButtonEnablement() {
		if(!hostText.getText().trim().isEmpty() &&
				!portText.getText().trim().isEmpty() &&
				!usernameText.getText().trim().isEmpty() &&
				!passwordText.getText().trim().isEmpty()) {
			reposBrowseButton.setEnabled(true);
		} else {
			reposBrowseButton.setEnabled(false);
		}
	}

	public void setConnectionInformation() {
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
        } else if (usernameText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_USERID_REQ_MSG_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (passwordText.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_PASSWORD_REQ_MSG_UI_")); //$NON-NLS-1$
            isComplete = false;
        } else if (reposCombo.getText().trim().length() < 1) {
            parentPage.setErrorMessage(DatatoolsUiPlugin.UTIL.getString("ModeShapeDriverUIContributor.VALIDATE_PATH_REQ_UI_")); //$NON-NLS-1$
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
        reposCombo.setText(url.getRepos());

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
        if (!(url.getTeiidMetadata())) {
            teiidCheck.setSelection(false);
        } else {
            teiidCheck.setSelection(true);
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
        summaryData.add(new String[] {PATH_SUMMARY_DATA_TEXT_, this.reposCombo.getText().trim()});

        summaryData.add(new String[] {USERNAME_SUMMARY_DATA_TEXT_, this.usernameText.getText().trim()});
        summaryData.add(new String[] {SSL_SUMMARY_DATA_TEXT_,
            protocolCheck.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {TEIID_SUMMARY_DATA_TEXT_,
                teiidCheck.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {SAVE_PASSWORD_SUMMARY_DATA_TEXT_,
            savePasswordButton.getSelection() ? TRUE_SUMMARY_DATA_TEXT_ : FALSE_SUMMARY_DATA_TEXT_});
        summaryData.add(new String[] {URL_SUMMARY_DATA_TEXT_, this.urlText.getText().trim()});
        return summaryData;
    }
	
    private void browseForRepos() {
    	URL url;
		try {
			url = new URL(getRestUrl());
			RestUtils restUtils = new RestUtils();
			List<String> repositories = restUtils.getRepositoryList(url, usernameText.getText(), passwordText.getText());
			reposCombo.setItems(repositories.toArray(new String[0]));
			if(!repositories.isEmpty()) {
				reposCombo.setText(repositories.get(0));
			}
		} catch (Exception e) {
			Shell shell = Display.getCurrent().getActiveShell();
			MessageDialog.openError(shell,
                    DatatoolsUiConstants.UTIL.getString("ModeShapeDriverUIContributor.repos.browse.exception"), e.getMessage()); //$NON-NLS-1$
					IStatus status = new Status(IStatus.ERROR, DatatoolsUiConstants.PLUGIN_ID,
                        DatatoolsUiConstants.UTIL.getString("ModeShapeDriverUIContributor.repos.browse.exception"), e); //$NON-NLS-1$
					DatatoolsUiConstants.UTIL.log(status);
		}
	}

}

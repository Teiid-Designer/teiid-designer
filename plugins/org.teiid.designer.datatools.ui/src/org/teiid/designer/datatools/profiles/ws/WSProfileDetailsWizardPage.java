package org.teiid.designer.datatools.profiles.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants.SecurityType;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

import com.metamatrix.ui.internal.util.WidgetFactory;

public class WSProfileDetailsWizardPage extends ConnectionProfileDetailsPage
		implements Listener, DatatoolsUiConstants {

	
    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private CLabel descriptionText;
    private Label usernameLabel;
    private Text usernameText;
    private Label passwordLabel;
    private Text passwordText;
    private Label urlLabel;
    private Text urlText;
    private Label securityLabel;
    private Combo securityCombo;

    /**
     * @param wizardPageName
     */
    public WSProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("WSProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
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
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        profileLabel.setLayoutData(gd);

        profileText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        profileText.setLayoutData(gd);
        profileText.setText(((ConnectionProfileWizard)getWizard()).getProfileName());

        descriptionLabel = new Label(scrolled, SWT.NONE);
        descriptionLabel.setText(UTIL.getString("Common.Description.Label")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        descriptionLabel.setLayoutData(gd);

        descriptionText = WidgetFactory.createLabel(scrolled, SWT.SINGLE | SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        descriptionText.setLayoutData(gd);
        descriptionText.setText(((ConnectionProfileWizard)getWizard()).getProfileDescription());

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

        securityLabel = new Label(scrolled, SWT.NONE);
        securityLabel.setText(UTIL.getString("Common.Security.Type.Label")); //$NON-NLS-1$
        securityLabel.setToolTipText(UTIL.getString("Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        securityLabel.setLayoutData(gd);

        securityCombo = new Combo(scrolled, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        securityCombo.setToolTipText(UTIL.getString("Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        securityCombo.setLayoutData(gd);
        securityCombo.setItems(new String[]{SecurityType.None.name(), SecurityType.HTTPBasic.name(), SecurityType.WSSecurity.name()});
        securityCombo.setText(SecurityType.None.name());
        
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
        usernameText.setEnabled(false);
        
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
        passwordText.setEnabled(false);
        
        setPingButtonVisible(false);
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
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
        urlText.addListener(SWT.Modify, this);
        securityCombo.addListener(SWT.Modify, this);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == usernameText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IWSProfileConstants.USERNAME_PROP_ID, usernameText.getText());
        }
        if (event.widget == passwordText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IWSProfileConstants.PASSWORD_PROP_ID, passwordText.getText());
        }
        if (event.widget == urlText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IWSProfileConstants.URL_PROP_ID, urlText.getText());
        }
        if (event.widget == securityCombo) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IWSProfileConstants.SECURITY_TYPE_ID, securityCombo.getText());
        	if(securityCombo.getText().equals(SecurityType.None.name())) {
        		usernameText.setEnabled(false);
        		passwordText.setEnabled(false);
        	} else {
        		usernameText.setEnabled(true);
        		passwordText.setEnabled(true);
        	}
        	

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
        setPingButtonVisible(false);
        setPingButtonEnabled(false);

        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(IWSProfileConstants.URL_PROP_ID)
                || properties.get(IWSProfileConstants.URL_PROP_ID).toString().isEmpty()) {
                setErrorMessage(UTIL.getString("Common.URL.Error.Message")); //$NON-NLS-1$
                return;
        }
        setErrorMessage(null);
        try {
        	URL url = new URL(properties.get(IWSProfileConstants.URL_PROP_ID).toString());
        } catch(MalformedURLException e) {
        	setErrorMessage(UTIL.getString("Common.URL.Invalid.Message") + e.getMessage());
        	return;
        }
        
        if (null != properties.get(IWSProfileConstants.SECURITY_TYPE_ID) &&
        		!SecurityType.None.name().equals(properties.get(IWSProfileConstants.SECURITY_TYPE_ID))) {
        	if (null == properties.get(IWSProfileConstants.USERNAME_PROP_ID)
                    || properties.get(IWSProfileConstants.USERNAME_PROP_ID).toString().isEmpty()) {
                    setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
                    return;
                }
                setErrorMessage(null);
                if (null == properties.get(IWSProfileConstants.PASSWORD_PROP_ID)
                    || properties.get(IWSProfileConstants.PASSWORD_PROP_ID).toString().isEmpty()) {
                    setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
                    return;
                }
                
        }
        
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
				&& (null == properties.get(IWSProfileConstants.URL_PROP_ID) || properties
						.get(IWSProfileConstants.URL_PROP_ID).toString()
						.isEmpty())) {
			complete = false;
		}
		if (complete
				&& null != properties.get(IWSProfileConstants.SECURITY_TYPE_ID) && (!SecurityType.None.name().equals(
						properties.get(IWSProfileConstants.SECURITY_TYPE_ID)
								.toString()))) {
			if (complete
					&& (null == properties
							.get(IWSProfileConstants.USERNAME_PROP_ID) || properties
							.get(IWSProfileConstants.USERNAME_PROP_ID)
							.toString().isEmpty())) {
				complete = false;
			}
			if (complete
					&& (null == properties
							.get(IWSProfileConstants.PASSWORD_PROP_ID) || properties
							.get(IWSProfileConstants.PASSWORD_PROP_ID)
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
        result.add(new String[] {UTIL.getString("Common.Username.Label"), usernameText.getText()}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("Common.Security.Type.Label"), securityCombo.getText()}); //$NON-NLS-1$
        return result;
    }

}

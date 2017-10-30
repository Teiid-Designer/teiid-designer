/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jdg7;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.datatools.profiles.jdg.IJDGProfileConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.dialogs.ScrolledConnectionProfileDetailsPage;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.util.JndiNameHelper;

public class JDG7ProfileDetailsWizardPage extends ScrolledConnectionProfileDetailsPage implements IJDGProfileConstants.PropertyKeys, Listener, DatatoolsUiConstants {
    private Composite scrolled;

    private CLabel profileText;
    private CLabel descriptionText;
    private Text jndiText;
    private Text remoteServerListText;
    private Text trustFileStoreNameText;
    private Text trustStorePasswordText;
    private Text keyStoreFileNameText;
    private Text keyStorePasswordText;
    private Text authenticationServerNameText;
    private Text authenticationRealmText;
    private Text salsMechanismText;
    private Text authenticationUserNameText;
    private Text authenticationUserPasswordText;

    private boolean settingProperty = false;
    
    private JndiNameHelper jndiNameHelper;
    
    /**
     * Constructor
     * @param pageName the page name
     */
    public JDG7ProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("JDG7ProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
        this.jndiNameHelper = new JndiNameHelper();
        setShowPing(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl( Composite parent ) {

		final Composite mainPanel = parent; //scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));

        scrolled = new Composite(mainPanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(scrolled);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(scrolled);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Profile.Label"), null);

        profileText = createLabel(scrolled, SWT.NONE, ((JDG7ConnectionProfileWizard)getWizard()).getProfileName(), null);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Description.Label"), null);

        descriptionText = createLabel(scrolled, SWT.NONE, ((JDG7ConnectionProfileWizard)getWizard()).getProfileDescription(), null);

        createLabel(scrolled, SWT.NONE, 
        		UTIL.getString("JBossDsPropertyPage.jndi.Label"), 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"));
        jndiText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"), 1, true);
        jndiText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiText.getText());
			}
		});

        

        createLabel(scrolled, SWT.NONE, 
        		Messages.RemoteServerList, 
        		Messages.RemoteServerListToolTip);
    	remoteServerListText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.RemoteServerListToolTip, 1, true);
    	remoteServerListText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(REMOTE_SERVER_LIST, remoteServerListText.getText());
			}
		});
    	
        createLabel(scrolled, SWT.NONE, 
        		Messages.TrustStoreFileName, 
        		Messages.TrustStoreFileNameTooltip);
        trustFileStoreNameText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.TrustStoreFileNameTooltip, 1, true);
    	trustFileStoreNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(TRUST_FILE_STORE_NAME, trustFileStoreNameText.getText());
			}
		});
    	
        createLabel(scrolled, SWT.NONE, 
        		Messages.TrustStorePassword, 
        		Messages.TrustStorePasswordTooltip);
        trustStorePasswordText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.TrustStorePasswordTooltip, 1, true);
        trustStorePasswordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(TRUST_STORE_PASSWORD, trustStorePasswordText.getText());
			}
		});
	    
        createLabel(scrolled, SWT.NONE, 
        		Messages.KeyStoreFileName, 
        		Messages.KeyStoreFileNameTooltip);
        keyStoreFileNameText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.KeyStoreFileNameTooltip, 1, true);
        keyStoreFileNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(KEY_STORE_FILE_NAME, keyStoreFileNameText.getText());
			}
		});
	    
        createLabel(scrolled, SWT.NONE, 
        		Messages.KeyStorePassword, 
        		Messages.KeyStorePasswordTooltip);
        keyStorePasswordText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.KeyStorePasswordTooltip, 1, true);
        keyStorePasswordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(KEY_STORE_PASSWORD, keyStorePasswordText.getText());
			}
		});
	    
        createLabel(scrolled, SWT.NONE, 
        		Messages.AuthenticationServerName, 
        		Messages.AuthenticationServerNameTooltip);
        authenticationServerNameText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.AuthenticationServerNameTooltip, 1, true);
        authenticationServerNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(AUTHENTICATION_SERVER_NAME, authenticationServerNameText.getText());
			}
		});
	    
        createLabel(scrolled, SWT.NONE, 
        		Messages.AuthenticationRealm, 
        		Messages.AuthenticationRealmTooltip);
        authenticationRealmText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.AuthenticationRealmTooltip, 1, true);
        authenticationRealmText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(AUTHENTICATION_REALM, authenticationRealmText.getText());
			}
		});
        
        createLabel(scrolled, SWT.NONE, 
        		Messages.SaslMechanism, 
        		Messages.SaslMechanismTooltip);
        salsMechanismText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.SaslMechanismTooltip, 1, true);
        salsMechanismText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(SASL_MECHANISM, salsMechanismText.getText());
			}
		});
        createLabel(scrolled, SWT.NONE, 
        		Messages.AuthenticationUserName, 
        		Messages.AuthenticationUserNameTooltip);
        authenticationUserNameText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.AuthenticationUserNameTooltip, 1, true);
        authenticationUserNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(AUTHENTICATION_USER_NAME, authenticationUserNameText.getText());
			}
		});
	    ;
	    createLabel(scrolled, SWT.NONE, 
        		Messages.AuthenticationUserPassword, 
        		Messages.AuthenticationUserPasswordTooltip);
	    authenticationUserPasswordText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.AuthenticationUserPasswordTooltip, 1, true);
	    authenticationUserPasswordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if( settingProperty ) return;
				
				setProperty(AUTHENTICATION_PASSWORD, authenticationUserPasswordText.getText());
			}
		});
	    
	    org.teiid.designer.ui.common.widget.Label label = createLabel(scrolled, SWT.NONE, 
        		Messages.RequiredProperty, 
        		Messages.RequiredProperty);
	    label.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        setPingButtonVisible(false);
        setCreateAutoConnectControls(false);
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
        jndiText.addListener(SWT.Modify, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

//        if (event.widget == jndiText) {
//            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
//            String jndiName = JndiUtil.addJavaPrefix(jndiText.getText());
//            properties.setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiName);
//        }
//
//        updateState();
    }

    void updateState() {

		
        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        profileText.getParent().layout(true);
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());
        descriptionText.getParent().layout(true);

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(IJBossDsProfileConstants.JNDI_PROP_ID)
            || properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("JBossDsPropertyPage.jndi.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            setPageComplete(false);
            return;
        }
        
        // Check JNDI name property
        String msg = jndiNameHelper.checkValidName(properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString());
        if( ! StringUtilities.isEmpty(msg)) {
            setErrorMessage(msg); //$NON-NLS-1$
            setPingButtonEnabled(false);
            setPageComplete(false);
            return;
        }
        
        if( StringUtilities.isEmpty((String)properties.get(REMOTE_SERVER_LIST)) ) {
            setErrorMessage(Messages.RemoteServerListMissing); //$NON-NLS-1$
            setPageComplete(false);
            return;
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
    private boolean internalComplete( boolean complete ) {
        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (complete
            && (null == properties.get(IJDGProfileConstants.JNDI_PROP_ID) || properties.get(IJDGProfileConstants.JNDI_PROP_ID).toString().isEmpty())) {
            complete = false;
            properties.put(IJBossDsProfileConstants.TRANSLATOR_PROP_ID, IJDGProfileConstants.JDG7_TRANSLATOR_TYPE);
        }

        return complete;
    }

    @Override
    public void testConnection() {
        super.testConnection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List<String[]> getSummaryData() {
        @SuppressWarnings("unchecked")
		List<String[]> result = super.getSummaryData();
        result.add(new String[] {UTIL.getString("JBossDsPropertyPage.jndi.Label"), jndiText.getText()}); //$NON-NLS-1$
        return result;
    }

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ) {
			updateState();
		}
	}
    
	private void setProperty(String id, String value) {
		settingProperty = true;
        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();

        if( StringUtilities.isEmpty(value) ) {
        	properties.remove(id);
        } else {
        	properties.setProperty(id, value);
        }
        updateState();
        settingProperty = false;
	}
	
	private Text createTextField(Composite parent, int swtStyle, String tooltip, int hSpan, boolean grabHorizontal) {
    	Text textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
    	textField.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().span(hSpan, 1).grab(grabHorizontal,  false).applyTo(textField);
    	return textField;
	}
	
	
	private org.teiid.designer.ui.common.widget.Label createLabel(Composite parent, int swtStyle, String text, String tooltip) {
		org.teiid.designer.ui.common.widget.Label theLabel = WidgetFactory.createLabel(parent, text);
		theLabel.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).applyTo(theLabel);
    	return theLabel;
	}
	
	private org.teiid.designer.ui.common.widget.Label createLabelRightJustified(Composite parent, int swtStyle, String text, String tooltip) {
		org.teiid.designer.ui.common.widget.Label theLabel = WidgetFactory.createLabel(parent, text);
		theLabel.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).applyTo(theLabel);
    	return theLabel;
	}
    
}

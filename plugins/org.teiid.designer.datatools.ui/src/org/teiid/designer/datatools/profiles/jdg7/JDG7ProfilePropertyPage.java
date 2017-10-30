/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jdg7;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.datatools.profiles.jdg.IJDGProfileConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.util.JndiNameHelper;

public class JDG7ProfilePropertyPage extends ProfileDetailsPropertyPage
		implements Listener, IJDGProfileConstants.PropertyKeys, IContextProvider, DatatoolsUiConstants {

	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
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
   
	public JDG7ProfilePropertyPage() {
		super();
		this.jndiNameHelper = new JndiNameHelper();
		setPingButtonEnabled(false);
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
		final Composite mainPanel = parent; //scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));

        scrolled = new Composite(mainPanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(scrolled);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(scrolled);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Profile.Label"), null);

        profileText = createLabel(scrolled, SWT.NONE, getConnectionProfile().getName(), null);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Description.Label"), null);

        descriptionText = createLabel(scrolled, SWT.NONE, getConnectionProfile().getDescription(), null);

        createLabel(scrolled, SWT.NONE, 
        		UTIL.getString("JBossDsPropertyPage.jndi.Label"), 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"));
        jndiText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"), 1, true);
        jndiText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
	            String jndiName = JndiUtil.addJavaPrefix(jndiText.getText());
	            setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiName);
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
        addListeners();


		initControls();
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

        if (event.widget == jndiText) {
            Properties properties = getConnectionProfile().getBaseProperties();
            properties.setProperty(IJDGProfileConstants.JNDI_PROP_ID, jndiText.getText());
        }

        updateState();
    }

	protected void validate() {
		String errorMessage = null;
		boolean valid = true;

		setErrorMessage(errorMessage);
		this.setPingButtonEnabled(valid);
		setValid(valid);

	}
	
    void updateState() {
		
        profileText.setText(getConnectionProfile().getName());
        profileText.getParent().layout(true);
        descriptionText.setText(getConnectionProfile().getDescription());
        descriptionText.getParent().layout(true);

        Properties properties = getConnectionProfile().getBaseProperties();
        if (null == properties.get(IJBossDsProfileConstants.JNDI_PROP_ID)
            || properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("JBossDsPropertyPage.jndi.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            setValid(false);
            return;
        }
        
        String msg = jndiNameHelper.checkValidName(properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString());
        if( ! StringUtilities.isEmpty(msg)) {
            setErrorMessage(msg); //$NON-NLS-1$
            setPingButtonEnabled(false);
            setValid(false);
        }
        
        if( StringUtilities.isEmpty((String)properties.get(REMOTE_SERVER_LIST)) ) {
            setErrorMessage(Messages.RemoteServerListMissing); //$NON-NLS-1$
            setValid(false);
            return;
        }

        setValid(true);
        setErrorMessage(null);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$

    }

	/**
	* 
	*/
	private void initControls() {
		IConnectionProfile profile = getConnectionProfile();
		Properties props = profile.getBaseProperties();

		if (propExists(props, IJBossDsProfileConstants.JNDI_PROP_ID)) {
			jndiText.setText((String) props.get(IJBossDsProfileConstants.JNDI_PROP_ID));
		}

		
		if (!StringUtilities.isEmpty((String)props.get(REMOTE_SERVER_LIST))) {
			remoteServerListText.setText((String)props.get(REMOTE_SERVER_LIST));
			remoteServerListText.setEnabled(true);
		}
		
		if (!StringUtilities.isEmpty((String)props.get(TRUST_FILE_STORE_NAME))) {
			trustFileStoreNameText.setText((String)props.get(TRUST_FILE_STORE_NAME));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(TRUST_STORE_PASSWORD))) {
			trustStorePasswordText.setText((String)props.get(TRUST_STORE_PASSWORD));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(KEY_STORE_FILE_NAME))) {
			keyStoreFileNameText.setText((String)props.get(KEY_STORE_FILE_NAME));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(KEY_STORE_PASSWORD))) {
			keyStorePasswordText.setText((String)props.get(KEY_STORE_PASSWORD));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(AUTHENTICATION_SERVER_NAME))) {
			authenticationServerNameText.setText((String)props.get(AUTHENTICATION_SERVER_NAME));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(AUTHENTICATION_REALM))) {
			authenticationRealmText.setText((String)props.get(AUTHENTICATION_REALM));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(SASL_MECHANISM))) {
			salsMechanismText.setText((String)props.get(SASL_MECHANISM));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(AUTHENTICATION_USER_NAME))) {
			authenticationUserNameText.setText((String)props.get(AUTHENTICATION_USER_NAME));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(AUTHENTICATION_PASSWORD))) {
			authenticationUserPasswordText.setText((String)props.get(AUTHENTICATION_PASSWORD));
		}

		validate();
	}
	
	private boolean propExists(Properties props, String key) {
		String value = props.getProperty(key);
		
		if (StringUtilities.isEmpty(value)) return false;
		
		return true;
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

		return result;
	}
	
    
	private void setProperty(String id, String value) {
		settingProperty = true;
		
		IConnectionProfile profile = getConnectionProfile();
		Properties properties = profile.getBaseProperties();
		
        if( ! StringUtilities.isEmpty(value) ) {
        	properties.setProperty(id, value);
        } else {
        	properties.remove(id);
        }
        
        profile.setBaseProperties(properties);
        
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

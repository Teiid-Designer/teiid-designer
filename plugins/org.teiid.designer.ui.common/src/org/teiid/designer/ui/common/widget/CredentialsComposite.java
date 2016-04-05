/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.widget;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.UiConstants;


/**
 *
 *
 * @since 8.0
 */
public class CredentialsComposite extends Composite implements UiConstants, Listener,
        ICredentialsCommon {
    
    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CredentialsComposite.class);

    private Label securityLabel;

    protected Combo securityCombo;

    private Label usernameLabel;

    private Text usernameText;

    private Label passwordLabel;

    private Text passwordText;
    
    boolean handlingEvent = false;

    /*
     * Need to stash the inputted values since they may be retrieved after the
     * composite has been disposed.
     */
    protected SecurityType securityType = SecurityType.None;

    private String password;

    private String userName;

    public CredentialsComposite(Composite parent, int style, String wsType) {
        super(parent, style);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(this);

        securityLabel = new Label(this, SWT.NONE);
        securityLabel.setText(Util.getString(PREFIX + "Common.Security.Type.Label")); //$NON-NLS-1$
        securityLabel.setToolTipText(Util.getString(PREFIX + "Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER).applyTo(securityLabel);

        securityCombo = new Combo(this, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        if ("rest".equals(wsType)){ //$NON-NLS-1$
      		 securityCombo.setItems(new String[] { SecurityType.None.name(),
      	                SecurityType.HTTPBasic.name(),  SecurityType.HTTPDigest.name() });
      	        securityCombo.setText(SecurityType.None.name());
   	   	}else{
   	   		 securityCombo.setItems(new String[] { SecurityType.None.name(),
   		                SecurityType.HTTPBasic.name() });
   		        securityCombo.setText(SecurityType.None.name());
   	   	}
        securityCombo.setToolTipText(Util.getString(PREFIX + "Common.Context.Factory.ToolTip")); //$NON-NLS-1$
        securityCombo.addListener(SWT.Modify, this);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(securityCombo);

        usernameLabel = new Label(this, SWT.NONE);
        usernameLabel.setText(Util.getString(PREFIX + "Common.Username.Label")); //$NON-NLS-1$
        usernameLabel.setToolTipText(Util.getString(PREFIX + "Common.Username.ToolTip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER).applyTo(usernameLabel);

        usernameText = new Text(this, SWT.SINGLE | SWT.BORDER);
        usernameText.setToolTipText(Util.getString(PREFIX + "Common.Username.ToolTip")); //$NON-NLS-1$
        usernameText.setEnabled(false);
        usernameText.addListener(SWT.Modify, this);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(usernameText);

        passwordLabel = new Label(this, SWT.NONE);
        passwordLabel.setText(Util.getString(PREFIX + "Common.Password.Label")); //$NON-NLS-1$
        passwordLabel.setToolTipText(Util.getString(PREFIX + "Common.Password.ToolTip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.CENTER, SWT.CENTER).applyTo(passwordLabel);

        passwordText = new Text(this, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordText.setToolTipText(Util.getString(PREFIX + "Common.Password.ToolTip")); //$NON-NLS-1$
        passwordText.setEnabled(false);
        passwordText.addListener(SWT.Modify, this);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
     * Event)
     */
    @Override
    public void handleEvent(Event event) {
    	if( !handlingEvent ) {
    		handlingEvent = true;
	        if (event.widget == securityCombo) {
	            if (securityCombo.getText().equals(SecurityType.None.name())) {
	                usernameText.setText(StringConstants.EMPTY_STRING); 
	                usernameText.setEnabled(false);
	                passwordText.setText(StringConstants.EMPTY_STRING);
	                passwordText.setEnabled(false);
	            } else {
	                usernameText.setEnabled(true);
	                passwordText.setEnabled(true);
	            }
	        }
	        securityType = SecurityType.valueOf(securityCombo.getText());
	        userName = usernameText.getText();
	        password = passwordText.getText();
	        handlingEvent = false;
    	}
    }

    public void addSecurityOptionListener(int eventType, Listener listener) {
        securityCombo.addListener(eventType, listener);
    }

    public void addUserNameListener(int eventType, Listener listener) {
        usernameText.addListener(eventType, listener);
    }

    public void addPasswordListener(int eventType, Listener listener) {
        passwordText.addListener(eventType, listener);
    }

    /**
     * @return
     */
    public SecurityType getSecurityOption() {
        return securityType;
    }
    
    /**
     * @param string
     */
    public void setSecurityOption(String securityValue) {
        securityType = SecurityType.valueOf(securityValue);
        securityCombo.setText(securityValue);
    }

    /**
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param string
     */
    public void setUserName(String userNameValue) {
        userName = userNameValue;
        usernameText.setText(userNameValue);
    }
    
    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param string
     */
    public void setPassword(String passwordValue) {
        password = passwordValue;
        passwordText.setText(passwordValue);
    }
}

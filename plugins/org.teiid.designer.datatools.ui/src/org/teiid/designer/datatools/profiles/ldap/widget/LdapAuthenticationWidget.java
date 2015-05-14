/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ldap.widget;

import static org.teiid.designer.datatools.ui.DatatoolsUiConstants.UTIL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;

/**
 *
 */
public class LdapAuthenticationWidget extends Composite implements Listener, IChangeNotifier {

    private Composite scrolled;

    /** The combo to select the authentication method */
    private Label authenticationMethodLabel;
    private Combo authenticationMethodCombo;

    private Label usernameLabel;
    private Text usernameText;

    private Label passwordLabel;
    private Text passwordText;

    //
    // The connection profile properties
    //
    private Properties connProperties;

    private Set<IChangeListener> listeners = new HashSet<IChangeListener>();

    /**
     * @param parent
     * @param style
     * @param connProperties
     */
    public LdapAuthenticationWidget(Composite parent, int style, Properties connProperties) {
        super(parent, style);
        this.connProperties = connProperties;
        createComposite();
    }

    /**
     * @return title of composite
     */
    public String getTitle() {
        return UTIL.getString("LdapAuthenticationWidget.Label"); //$NON-NLS-1$
    }

    private void setProperty(String key, String value) {
        connProperties.setProperty(key, value);
    }

    /**
     * Gets the authentication method.
     * 
     * @return the authentication method
     */
    private String getAuthenticationMethod() {
        switch (authenticationMethodCombo.getSelectionIndex()) {
            case 1:
                return ILdapProfileConstants.AUTHMETHOD_SIMPLE;
            default:
                return ILdapProfileConstants.AUTHMETHOD_NONE;
        }
    }

    @Override
    public void handleEvent(Event event) {
        setProperty(ILdapProfileConstants.AUTHENTICATION_METHOD, getAuthenticationMethod());

        if (event.widget == usernameText) {
            setProperty(ILdapProfileConstants.USERNAME_PROP_ID, usernameText.getText());
        }
        if (event.widget == passwordText) {
            setProperty(ILdapProfileConstants.PASSWORD_PROP_ID, passwordText.getText());
        }

        boolean authSimple = ILdapProfileConstants.AUTHMETHOD_SIMPLE.equals(getAuthenticationMethod());
        usernameText.setEnabled(authSimple);
        passwordText.setEnabled(authSimple);

        notifyListeners();
    }

    private void addListeners() {
        authenticationMethodCombo.addListener(SWT.Modify, this);
        usernameText.addListener(SWT.Modify, this);
        passwordText.addListener(SWT.Modify, this);
    }

    private void initControls() {
        String authMethod = connProperties.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD);
        int index = authMethod == null ? 0 : 1;
        if (ILdapProfileConstants.AUTHMETHOD_SIMPLE.equals(authMethod))
            index = 1; // Simple Selection

        authenticationMethodCombo.select(index);

        if (null != connProperties.get(ILdapProfileConstants.USERNAME_PROP_ID)) {
            usernameText.setText(connProperties.getProperty(ILdapProfileConstants.USERNAME_PROP_ID));
        }

        if (null != connProperties.get(ILdapProfileConstants.PASSWORD_PROP_ID)) {
            passwordText.setText(connProperties.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID));
        }
    }

    private void createComposite() {
        GridLayoutFactory.fillDefaults().applyTo(this);

        scrolled = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolled);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(scrolled);

        authenticationMethodLabel = new Label(scrolled, SWT.NONE);
        authenticationMethodLabel.setText(UTIL.getString("LdapAuthenticationWidget.Auth.Label")); //$NON-NLS-1$
        authenticationMethodLabel.setToolTipText(UTIL.getString("LdapAuthenticationWidget.Auth.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(authenticationMethodLabel);

        String[] authMethods = new String[] {UTIL.getString("LdapAuthenticationWidget.AnonymousAuthentication"), //$NON-NLS-1$
            UTIL.getString("LdapAuthenticationWidget.SimpleAuthentication")}; //$NON-NLS-1$
        authenticationMethodCombo = new Combo(scrolled, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        authenticationMethodCombo.setItems(authMethods);
        authenticationMethodCombo.select(0);
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(authenticationMethodCombo);

        usernameLabel = new Label(scrolled, SWT.NONE);
        usernameLabel.setText(UTIL.getString("LdapAuthenticationWidget.User.Label")); //$NON-NLS-1$
        usernameLabel.setToolTipText(UTIL.getString("LdapAuthenticationWidget.User.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(usernameLabel);

        usernameText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        usernameText.setToolTipText(UTIL.getString("LdapAuthenticationWidget.User.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(usernameText);

        passwordLabel = new Label(scrolled, SWT.NONE);
        passwordLabel.setText(UTIL.getString("LdapAuthenticationWidget.Password.Label")); //$NON-NLS-1$
        passwordLabel.setToolTipText(UTIL.getString("LdapAuthenticationWidget.Password.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(passwordLabel);

        passwordText = new Text(scrolled, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordText.setToolTipText(UTIL.getString("LdapAuthenticationWidget.Password.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(passwordText);

        addListeners();
        initControls();
    }

    private void notifyListeners() {
        for (IChangeListener listener : listeners) {
            listener.stateChanged(this);
        }
    }

    @Override
    public void addChangeListener(IChangeListener theListener) {
        listeners.add(theListener);
    }

    @Override
    public void removeChangeListener(IChangeListener theListener) {
        listeners.remove(theListener);
    }
}

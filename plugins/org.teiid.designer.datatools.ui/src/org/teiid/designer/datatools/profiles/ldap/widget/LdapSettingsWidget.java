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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;
import org.teiid.designer.datatools.profiles.ldap.LDAPConnectionFactory;
import org.teiid.designer.datatools.profiles.ldap.LDAPUrl;

/**
 *
 */
public class LdapSettingsWidget extends Composite implements Listener, IChangeNotifier, StringConstants {

    private Composite scrolled;

    private Label hostLabel;
    private Text hostText;

    private Label portLabel;
    private Spinner portSpinner;

    /** The combo to select the encryption method */
    private Label encMethodLabel;
    private Combo encMethodCombo;

    private String[] encMethods = new String[] {
        UTIL.getString("LdapSettingsWidget.Encryption.NoEncryption"), //$NON-NLS-1$
        UTIL.getString("LdapSettingsWidget.Encryption.UseSSLEncryption") //$NON-NLS-1$
    };

    private Label urlLabel;
    private Text urlText;

    /** The combo to select the network provider */
    private Label networkProviderLabel;
    private Combo networkProviderCombo;

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
    public LdapSettingsWidget(Composite parent, int style, Properties connProperties) {
        super(parent, style);
        this.connProperties = connProperties;
        createComposite();
    }

    /**
     * @return title of composite
     */
    public String getTitle() {
        return UTIL.getString("LdapSettingsWidget.Label"); //$NON-NLS-1$
    }

    private void setProperty(String key, String value) {
        connProperties.setProperty(key, value);
    }

    private void setUrl() {
        String host = hostText.getText();
        int port = portSpinner.getSelection();
        String encMethod = encMethodCombo.getText();
        String ldapScheme = ILdapProfileConstants.LDAP_SCHEME;

        if (host.isEmpty())
            return;

        if (encMethod.equals(UTIL.getString("LdapSettingsWidget.Encryption.UseSSLEncryption"))) //$NON-NLS-1$
            ldapScheme = ILdapProfileConstants.LDAPS_SCHEME;

        String ldapUrl = ldapScheme + host + COLON + port;
        setProperty(ILdapProfileConstants.HOST_PROP_ID, host);
        setProperty(ILdapProfileConstants.PORT_PROP_ID, Integer.toString(port));
        setProperty(ILdapProfileConstants.SCHEME_PROP_ID, ldapScheme);
        setProperty(ILdapProfileConstants.URL_PROP_ID, ldapUrl);

        urlText.setText(ldapUrl);
    }

    private void addListeners() {
        hostText.addListener(SWT.Modify, this);
        portSpinner.addListener(SWT.Modify, this);
        encMethodCombo.addListener(SWT.Modify, this);
        networkProviderCombo.addListener(SWT.Modify, this);
    }

    private void notifyListeners() {
        for (IChangeListener listener : listeners) {
            listener.stateChanged(this);
        }
    }

    @Override
    public void handleEvent(Event event) {

        if (event.widget == hostText || event.widget == portSpinner || event.widget == encMethodCombo) {
            setUrl();
        }
        if (event.widget == networkProviderCombo) {
            setProperty(ILdapProfileConstants.NETWORK_PROVIDER, ILdapProfileConstants.JNDI_NETWORK_PROVIDER);
            setProperty(ILdapProfileConstants.CONTEXT_FACTORY, LDAPConnectionFactory.getDefaultLdapContextFactory());
        }

        notifyListeners();
    }

    private void initControls() {
        if (null != connProperties.get(ILdapProfileConstants.URL_PROP_ID)) {
            String url = connProperties.getProperty(ILdapProfileConstants.URL_PROP_ID);
            // ldap[s] : // hostname : 389

            try {
                LDAPUrl ldapUrl = new LDAPUrl(url);

                if (ILdapProfileConstants.LDAPS_SCHEME.equals(ldapUrl.getScheme()))
                    encMethodCombo.select(1);
                else if (ILdapProfileConstants.LDAP_SCHEME.equals(ldapUrl.getScheme()))
                    encMethodCombo.select(0);

                hostText.setText(ldapUrl.getHost());
                portSpinner.setSelection(ldapUrl.getPort());

            } catch (Exception ex1) {
                // Failed to process the url
            }

            urlText.setText(url);
        }

        if (null != connProperties.get(ILdapProfileConstants.CONTEXT_FACTORY)) {
            networkProviderCombo.setText(connProperties.getProperty(ILdapProfileConstants.CONTEXT_FACTORY));
        }
    }

    private void createComposite() {
        GridLayoutFactory.fillDefaults().applyTo(this);

        scrolled = new Composite(this, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolled);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(scrolled);

        hostLabel = new Label(scrolled, SWT.NONE);
        hostLabel.setText(UTIL.getString("LdapSettingsWidget.Host.Label")); //$NON-NLS-1$
        hostLabel.setToolTipText(UTIL.getString("LdapSettingsWidget.Host.Tooltip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(hostLabel);

        hostText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        hostText.setToolTipText(UTIL.getString("LdapSettingsWidget.Host.Tooltip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(hostText);

        portLabel = new Label(scrolled, SWT.NONE);
        portLabel.setText(UTIL.getString("LdapSettingsWidget.Port.Label")); //$NON-NLS-1$
        portLabel.setToolTipText(UTIL.getString("LdapSettingsWidget.Port.Tooltip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(portLabel);

        portSpinner = new Spinner(scrolled, SWT.NONE);
        portSpinner.setToolTipText(UTIL.getString("LdapSettingsWidget.Port.Tooltip")); //$NON-NLS-1$
        portSpinner.setMinimum(1);
        portSpinner.setMaximum(65535);
        portSpinner.setSelection(389); // Default LDAP Port
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(portSpinner);

        encMethodLabel = new Label(scrolled, SWT.NONE);
        encMethodLabel.setText(UTIL.getString("LdapSettingsWidget.Encryption.Label")); //$NON-NLS-1$
        encMethodLabel.setToolTipText(UTIL.getString("LdapSettingsWidget.Encryption.Tooltip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(portLabel);

        encMethodCombo = new Combo(scrolled, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        encMethodCombo.setItems(encMethods);
        encMethodCombo.select(0);
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(encMethodCombo);

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(urlLabel);

        urlText = new Text(scrolled, SWT.READ_ONLY | SWT.BORDER);
        urlText.setEnabled(false);
        urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(urlText);

        networkProviderLabel = new Label(scrolled, SWT.NONE);
        networkProviderLabel.setText(UTIL.getString("LdapSettingsWidget.Provider.Label")); //$NON-NLS-1$
        networkProviderLabel.setToolTipText(UTIL.getString("LdapSettingsWidget.Provider.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.BEGINNING, GridData.BEGINNING).applyTo(networkProviderLabel);

        //
        // Only JNDI is currently supported
        //
        String[] networkProviders = new String[] {ILdapProfileConstants.JNDI_NETWORK_PROVIDER};

        networkProviderCombo = new Combo(scrolled, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        networkProviderCombo.setItems(networkProviders);

        //
        // Since only JNDI is supported then disable the
        // combo and set the connection property
        //
        networkProviderCombo.select(0);
        networkProviderCombo.setEnabled(false);
        setProperty(ILdapProfileConstants.NETWORK_PROVIDER, ILdapProfileConstants.JNDI_NETWORK_PROVIDER);
        setProperty(ILdapProfileConstants.CONTEXT_FACTORY, LDAPConnectionFactory.getDefaultLdapContextFactory());

        networkProviderCombo.setToolTipText(UTIL.getString("LdapSettingsWidget.Provider.ToolTip")); //$NON-NLS-1$
        GridDataFactory.swtDefaults().align(GridData.FILL, GridData.BEGINNING).grab(true, false).applyTo(networkProviderCombo);

        addListeners();
        initControls();
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

package org.teiid.designer.datatools.profiles.ldap;

import java.util.Map.Entry;
import java.util.Properties;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.datatools.profiles.ldap.widget.LdapAuthenticationWidget;
import org.teiid.designer.datatools.profiles.ldap.widget.LdapSettingsWidget;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

/**
 * Property page displaying all settings when an
 * ldap connection is to be edited.
 */
public class PropertyPage extends ProfileDetailsPropertyPage
    implements IContextProvider, DatatoolsUiConstants, IChangeListener, ILdapProfileConstants {

    private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
                                                                                          DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
    private TabFolder tabFolder;

    private Properties newProperties = new Properties();

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
        this.setPingButtonEnabled(true);
        this.setPingButtonVisible(true);
        return result;
    }

    @Override
    protected void createCustomContents(Composite parent) {
        Properties oldProperties = getConnectionProfile().getBaseProperties();
        for (Entry<Object, Object> entry : oldProperties.entrySet()) {
            newProperties.put(entry.getKey(), entry.getValue());
        }

        tabFolder = new TabFolder(parent, SWT.TOP);

        TabItem[] tabs = new TabItem[2];

        tabs[0] = new TabItem(tabFolder, SWT.NONE);
        LdapSettingsWidget settingsWidget = new LdapSettingsWidget(tabFolder, SWT.NONE, newProperties);
        settingsWidget.addChangeListener(this);
        tabs[0].setText(settingsWidget.getTitle());
        tabs[0].setControl(settingsWidget);

        tabs[0] = new TabItem(tabFolder, SWT.NONE);
        LdapAuthenticationWidget authWidget = new LdapAuthenticationWidget(tabFolder, SWT.NONE, newProperties);
        authWidget.addChangeListener(this);
        tabs[0].setText(authWidget.getTitle());
        tabs[0].setControl(authWidget);
    }

    protected void validate() {
        String errorMessage = null;
        boolean valid = true;
        if (null == newProperties.get(URL_PROP_ID) || newProperties.getProperty(URL_PROP_ID).isEmpty()) {
            errorMessage = UTIL.getString("Common.URL.Error.Message"); //$NON-NLS-1$
            valid = false;
        }
        if (null == newProperties.get(CONTEXT_FACTORY) || newProperties.getProperty(CONTEXT_FACTORY).isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Context.Factory.Error.Message")); //$NON-NLS-1$
            valid = false;
        }

        boolean authSimple = AUTHMETHOD_SIMPLE.equals(newProperties.getProperty(AUTHENTICATION_METHOD));

        boolean hasPassword = (newProperties.get(PASSWORD_PROP_ID) != null && !newProperties.getProperty(PASSWORD_PROP_ID).isEmpty());
        boolean hasUser = (newProperties.get(USERNAME_PROP_ID) != null && !newProperties.getProperty(USERNAME_PROP_ID).isEmpty());

        if (authSimple && !hasPassword) {
            setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
            valid = false;
        } else if (authSimple && !hasUser) {
            setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
            valid = false;
        }

        setErrorMessage(errorMessage);
        setValid(valid);
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        validate();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
     */
    @Override
    protected Properties collectProperties() {
        return newProperties;
    }

}

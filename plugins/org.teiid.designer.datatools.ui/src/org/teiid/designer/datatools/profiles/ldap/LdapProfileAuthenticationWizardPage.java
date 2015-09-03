/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 *
 * Additional code taken from Apache Directory Studio (http://directory.apache.org/studio)
 * licensed under the http://www.apache.org/licenses/LICENSE-2.0
 */
package org.teiid.designer.datatools.profiles.ldap;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.datatools.profiles.ldap.widget.LdapAuthenticationWidget;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

/**
 * 
 */
public class LdapProfileAuthenticationWizardPage extends ConnectionProfileDetailsPage
	implements IChangeListener, DatatoolsUiConstants {

    private LdapAuthenticationWidget authWidget;

	/**
     * @param pageName
     */
    public LdapProfileAuthenticationWizardPage( String pageName ) {
        super(pageName, UTIL.getString("LdapProfileAuthenticationWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl( Composite parent ) {

    	authWidget = new LdapAuthenticationWidget(parent, SWT.NONE,
    									((LdapConnectionProfileWizard) getWizard()).getProfileProperties());
    	authWidget.addChangeListener(this);

        setPingButtonVisible(false);
        setAutoConnectOnFinishDefault(false);
        setPingButtonEnabled(false);
        setPageComplete(false);
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

    @Override
    public void stateChanged(IChangeNotifier theSource) {
    	updateState();
    }

    private boolean hasProperty(Properties properties, String key) {
		if (properties.get(key) == null)
			return false;

		String value = properties.getProperty(key);
		if (value.isEmpty())
			return false;

		return true;
    }

	private boolean hasUser(Properties properties) {
		return hasProperty(properties, ILdapProfileConstants.USERNAME_PROP_ID);
	}

	private boolean hasPassword(Properties properties) {
		return hasProperty(properties, ILdapProfileConstants.PASSWORD_PROP_ID);
	}

	private boolean hasAuth(Properties properties) {
		return hasProperty(properties, ILdapProfileConstants.AUTHENTICATION_METHOD);
	}

	private boolean isAuthNone(Properties properties) {
		if (! hasAuth(properties))
			return false;

		return ILdapProfileConstants.AUTHMETHOD_NONE.equals(properties.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD));
	}

	private boolean isAuthSimple(Properties properties) {
		if (! hasAuth(properties))
			return false;

		return ILdapProfileConstants.AUTHMETHOD_SIMPLE.equals(properties.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD));
	}

    void updateState() {
    	setErrorMessage(null);

    	Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        boolean authSimple = isAuthSimple(properties);

        boolean hasPassword = hasPassword(properties);
        boolean hasUser = hasUser(properties);

		if (authSimple && ! hasPassword)
			setErrorMessage(UTIL.getString("Common.Password.Error.Message")); //$NON-NLS-1$
		else if (authSimple && ! hasUser)
            setErrorMessage(UTIL.getString("Common.Username.Error.Message")); //$NON-NLS-1$
        else
        	setErrorMessage(null);

		boolean pageComplete = authSimple ? hasUser && hasPassword : true;
        setPingButtonEnabled(pageComplete);
        setPageComplete(pageComplete);
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

        if (! hasAuth(properties)) {
        	// The default of No Authentication remains and user just clicked next
        	// Ensure that a setting for auth-method has been added to properties
        	properties.setProperty(ILdapProfileConstants.AUTHENTICATION_METHOD, ILdapProfileConstants.AUTHMETHOD_NONE);
        }

        if (isAuthNone(properties))
        	return complete;

        if (isAuthSimple(properties)) {
        	//
        	// Simple Authentication requires user/password whilst No Auth does not
        	//
        	if (complete && ! hasUser(properties))
        		complete = false;

        	if (complete && ! hasPassword(properties))
        		complete = false;
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
    public List getSummaryData() {
    	Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();

        List result = super.getSummaryData();
        result.add(new String[] {UTIL.getString("LdapAuthenticationWidget.Auth.Label"), properties.getProperty(ILdapProfileConstants.AUTHENTICATION_METHOD)}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("LdapAuthenticationWidget.User.Label"), properties.getProperty(ILdapProfileConstants.USERNAME_PROP_ID)}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("LdapAuthenticationWidget.Password.Label"), properties.getProperty(ILdapProfileConstants.PASSWORD_PROP_ID)}); //$NON-NLS-1$
        return result;
    }
}

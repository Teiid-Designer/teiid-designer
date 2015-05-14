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
import org.teiid.designer.datatools.profiles.ldap.widget.LdapSettingsWidget;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

/**
 * 
 */
public class LdapProfileDetailsWizardPage extends ConnectionProfileDetailsPage
	implements DatatoolsUiConstants, IChangeListener {

	private LdapSettingsWidget settingsWidget;

    /**
     * @param pageName
     */
    public LdapProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("LdapProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl(Composite parent) {

        settingsWidget = new LdapSettingsWidget(parent, SWT.NONE,
                                                ((LdapConnectionProfileWizard)getWizard()).getProfileProperties());
        settingsWidget.addChangeListener(this);

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

    void updateState() {
        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        setErrorMessage(null);
        if (null == properties.get(ILdapProfileConstants.URL_PROP_ID)
            || properties.get(ILdapProfileConstants.URL_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("LdapProfileDetailsWizardPage.Url.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        if (null == properties.get(ILdapProfileConstants.CONTEXT_FACTORY)
            || properties.get(ILdapProfileConstants.CONTEXT_FACTORY).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("Common.Context.Factory.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setPageComplete(true);
        setPingButtonEnabled(true);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$

    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        updateState();
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
            && (null == properties.get(ILdapProfileConstants.URL_PROP_ID) || properties.get(ILdapProfileConstants.URL_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(ILdapProfileConstants.CONTEXT_FACTORY) || properties.get(ILdapProfileConstants.CONTEXT_FACTORY).toString().isEmpty())) {
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
        result.add(new String[] {UTIL.getString("Common.URL.Label"), properties.getProperty(ILdapProfileConstants.URL_PROP_ID)}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("LdapSettingsWidget.Provider.Label"), ILdapProfileConstants.JNDI_NETWORK_PROVIDER}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("LdapSettingsWidget.CtxFactory.Label"), properties.getProperty(ILdapProfileConstants.CONTEXT_FACTORY)}); //$NON-NLS-1$
        return result;
    }
}

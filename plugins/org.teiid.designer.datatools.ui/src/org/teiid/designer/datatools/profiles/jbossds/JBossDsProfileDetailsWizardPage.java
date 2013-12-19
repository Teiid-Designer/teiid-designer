/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jbossds;

import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * The JBossDs Connection Profile Details WizardPage
 */
public class JBossDsProfileDetailsWizardPage extends ConnectionProfileDetailsPage implements Listener, DatatoolsUiConstants {

    private Composite scrolled;

    private Label profileLabel;
    private CLabel profileText;
    private Label descriptionLabel;
    private CLabel descriptionText;
    private Label jndiLabel;
    private Text jndiText;
    private Label translatorLabel;
    private Text translatorText;

    /**
     * Constructor
     * @param pageName the page name
     */
    public JBossDsProfileDetailsWizardPage( String pageName ) {
        super(pageName, UTIL.getString("JBossDsProfileDetailsWizardPage.Name"), //$NON-NLS-1$
              AbstractUIPlugin.imageDescriptorFromPlugin(DatatoolsUiConstants.PLUGIN_ID, "icons/ldap.gif")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ConnectionProfileDetailsPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl( Composite parent ) {
        GridData gd;

        Group group = WidgetFactory.createSimpleGroup(parent, UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$

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
        // descriptionText.setEnabled(false);

        jndiLabel = new Label(scrolled, SWT.NONE);
        jndiLabel.setText(UTIL.getString("JBossDsPropertyPage.jndi.Label")); //$NON-NLS-1$
        jndiLabel.setToolTipText(UTIL.getString("JBossDsPropertyPage.jndi.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        jndiLabel.setLayoutData(gd);

        jndiText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        jndiText.setToolTipText(UTIL.getString("JBossDsPropertyPage.jndi.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        jndiText.setLayoutData(gd);

        translatorLabel = new Label(scrolled, SWT.NONE);
        translatorLabel.setText(UTIL.getString("JBossDsPropertyPage.translator.Label")); //$NON-NLS-1$
        translatorLabel.setToolTipText(UTIL.getString("JBossDsPropertyPage.translator.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        translatorLabel.setLayoutData(gd);

        translatorText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        translatorText.setToolTipText(UTIL.getString("JBossDsPropertyPage.translator.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        translatorText.setLayoutData(gd);

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
        translatorText.addListener(SWT.Modify, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == jndiText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiText.getText());
        }
        if (event.widget == translatorText) {
            Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
            properties.setProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID, translatorText.getText());
        }
        updateState();
    }

    void updateState() {

        profileText.setText(((NewConnectionProfileWizard)getWizard()).getProfileName());
        descriptionText.setText(((NewConnectionProfileWizard)getWizard()).getProfileDescription());

        Properties properties = ((NewConnectionProfileWizard)getWizard()).getProfileProperties();
        if (null == properties.get(IJBossDsProfileConstants.JNDI_PROP_ID)
            || properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("JBossDsPropertyPage.jndi.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            return;
        }
        setErrorMessage(null);
        if (null == properties.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID)
            || properties.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("JBossDsPropertyPage.translator.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
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
            && (null == properties.get(IJBossDsProfileConstants.JNDI_PROP_ID) || properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString().isEmpty())) {
            complete = false;
        }
        if (complete
            && (null == properties.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID) || properties.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID).toString().isEmpty())) {
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
        List result = super.getSummaryData();
        result.add(new String[] {UTIL.getString("JBossDsPropertyPage.jndi.Label"), jndiText.getText()}); //$NON-NLS-1$
        result.add(new String[] {UTIL.getString("JBossDsPropertyPage.translator.Label"), translatorText.getText()}); //$NON-NLS-1$
        return result;
    }
}

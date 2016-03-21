/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap.ui.wizards.pages.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.core.jobs.FetchBaseDNsRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datatools.profiles.ldap.ILdapProfileConstants;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileWorker;
import org.teiid.designer.datatools.ui.dialogs.IProfileChangedListener;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizard;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapImportWizardManager;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.LdapPageUtils;
import org.teiid.designer.ui.common.UiConstants.ConnectionProfileIds;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;

/**
 * Wizard page for specifying the connection parameters
 */
public class LdapDefinitionPage extends WizardPage
    implements IChangeListener, IProfileChangedListener, ModelGeneratorLdapUiConstants, ModelGeneratorLdapUiConstants.Images,
    ModelGeneratorLdapUiConstants.HelpContexts, ILdapProfileConstants, StringConstants {

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    /* The import manager. */
    private final LdapImportWizardManager importManager;

    private Button newCPButton;
    private Button editCPButton;

    private Combo connectionProfilesCombo;
    private ILabelProvider profileLabelProvider;

    private ConnectionProfileWorker profileWorker;

    /** Source and target text fields */
    private Text ldapURIText;
    private Combo baseDNCombo;
    private Button fetchBaseDnsButton;

    private SourceModelPanel sourceModelPanel;

    private boolean synchronising;

    /**
     * Constructs the page with the provided import manager
     *
     * @param theImportManager
     *            the import manager object
     */
    public LdapDefinitionPage(LdapImportWizardManager theImportManager) {
        super(LdapDefinitionPage.class.getSimpleName(), getString("title"), null); //$NON-NLS-1$
        this.importManager = theImportManager;
        setImageDescriptor(LdapImportWizard.BANNER);
        this.importManager.addChangeListener(this);
    }

    private static String getString(String key, Object... properties) {
        return ModelGeneratorLdapUiConstants.UTIL.getString(LdapDefinitionPage.class.getSimpleName() + "_" + key, properties); //$NON-NLS-1$
    }

    /**
     * @return the synchronising
     */
    private boolean isSynchronising() {
        return this.synchronising;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite theParent) {
        //
        // create main container
        //
        this.profileWorker = new ConnectionProfileWorker(this.getShell(), ConnectionProfileIds.CATEGORY_LDAP_CONNECTION, this);

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(pnlMain);
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, LDAP_SELECTION_PAGE);

        createSourceSelectionComposite(pnlMain);

        // Set the initial page status
        setPageStatus();
    }

    /**
     * Constructs the source LDAP selection component panel.
     *
     * @param theParent
     *            the parent container
     */
    private void createSourceSelectionComposite(Composite theParent) {
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        GridLayoutFactory.fillDefaults().applyTo(pnl);

        // ================================================================================
        Group profileGroup = WidgetFactory.createGroup(pnl, getString("profileLabel_text"), SWT.NONE, 2); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 5).applyTo(profileGroup);
        LdapPageUtils.setBackground(profileGroup, pnl);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(profileGroup);

        profileLabelProvider = new LabelProvider() {

            @Override
            public String getText(final Object source) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.connectionProfilesCombo = WidgetFactory.createCombo(profileGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 profileWorker.getProfiles(),
                                                                 profileLabelProvider,
                                                                 true);
        LdapPageUtils.blueForeground(this.connectionProfilesCombo);
        this.connectionProfilesCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // Need to sync the worker with the current profile
                handleConnectionProfileSelected();
            }
        });

        connectionProfilesCombo.setVisibleItemCount(10);

        newCPButton = WidgetFactory.createButton(profileGroup, getString("new_label")); //$NON-NLS-1$
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                profileWorker.create();
            }
        });

        editCPButton = WidgetFactory.createButton(profileGroup, getString("edit_label")); //$NON-NLS-1$
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                profileWorker.edit();
            }
        });

        // options group
        Group ldapURIGroup = WidgetFactory.createGroup(pnl, getString("ldapLabel_title"), SWT.FILL, 2); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 5).applyTo(ldapURIGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(ldapURIGroup);
        LdapPageUtils.setBackground(ldapURIGroup, pnl);

        Label ldapURILabel = new Label(ldapURIGroup, SWT.NONE);
        ldapURILabel.setText(getString("ldapLabel_text")); //$NON-NLS-1$
        LdapPageUtils.setBackground(ldapURILabel, pnl);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(ldapURILabel);

        // LDAP URI used for information only. Extracted from the connection profile
        ldapURIText = new Text(ldapURIGroup, SWT.BORDER | SWT.SINGLE);
        ldapURIText.setToolTipText(getString("ldapURITextField_tooltip")); //$NON-NLS-1$
        LdapPageUtils.blueForeground(ldapURIText);
        ldapURIText.setEditable(false);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(ldapURIText);

        Label dnLabel = new Label(ldapURIGroup, SWT.NONE);
        dnLabel.setText(getString("dnLabel_text")); //$NON-NLS-1$
        LdapPageUtils.setBackground(dnLabel, pnl);
        GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(dnLabel);

        baseDNCombo = new Combo(ldapURIGroup, SWT.DROP_DOWN);
        baseDNCombo.setToolTipText(getString("dnTextField_tooltip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(baseDNCombo);
        baseDNCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IConnectionProfile profile = importManager.getConnectionProfile();
                Properties properties = profile.getBaseProperties();
                properties.setProperty(ILdapProfileConstants.BASE_DN, baseDNCombo.getText());

                importManager.resetBrowserConnection();
            }
        });

        fetchBaseDnsButton = new Button(ldapURIGroup, SWT.PUSH);
        fetchBaseDnsButton.setEnabled(false);
        fetchBaseDnsButton.setText(getString("fetchBaseDNs" ) ); //$NON-NLS-1$
        GridDataFactory.fillDefaults().applyTo(fetchBaseDnsButton);
		fetchBaseDnsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IBrowserConnection browserConnection = importManager.getBrowserConnection();
				if (browserConnection == null)
					return;

				FetchBaseDNsRunnable runnable = new FetchBaseDNsRunnable(browserConnection);
				IStatus status = RunnableContextRunner.execute(runnable, getContainer(), true);
				if (status.isOK()) {
					if (!runnable.getBaseDNs().isEmpty()) {
						List<String> baseDNs = runnable.getBaseDNs();
						baseDNCombo.setItems(baseDNs.toArray(new String[baseDNs
								.size()]));
						baseDNCombo.select(0);
					} else {
						MessageDialog.openWarning(Display.getDefault()
								.getActiveShell(), getString("fetchBaseDNs"), //$NON-NLS-1$
								getString("noBaseDNReturnedFromServer")); //$NON-NLS-1$
						baseDNCombo.setItems(new String[0]);
					}
				}
			}
		});

        // Defines Location and Name values for source model
        sourceModelPanel = new SourceModelPanel(pnl, this.importManager);
    }

    private void handleConnectionProfileSelected() {
        if (isSynchronising())
            return;

        int selIndex = connectionProfilesCombo.getSelectionIndex();

        if (selIndex >= 0) {
            String name = connectionProfilesCombo.getItem(selIndex);
            if (name != null) {
                IConnectionProfile profile = profileWorker.getProfile(name);
                profileWorker.setSelection(profile);
                setConnectionProfileInternal(profile);
            }
        }

        notifyChanged();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * Override to replace the ImportWizard settings with the section devoted to
     * the LDAP import Wizard.
     *
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
    }

    /**
     * Determines if the supplied string is a valid formatted URI
     *
     * @param str
     *            the supplied uri string
     * @return 'true' if the string is a valid format, 'false' if not.
     */
    public boolean isValidUri(String str) {
        try {
            new org.apache.xerces.util.URI(str);
        } catch (org.apache.xerces.util.URI.MalformedURIException e) {
            return false;
        }
        return true;
    }

    /**
     * Persists dialog size and position.
     */
    private void saveState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null && getContainer() != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                Rectangle r = shell.getBounds();
                settings.put(DIALOG_X, r.x);
                settings.put(DIALOG_Y, r.y);
                settings.put(DIALOG_WIDTH, r.width);
                settings.put(DIALOG_HEIGHT, r.height);
            }
        }
    }

    /**
     * Refresh the ui state from the manager
     */
    private void refreshUiFromManager() {
        synchronising = true;

        try {
            if (this.importManager == null)
                return;

            if (connectionProfilesCombo.getItems() == null || connectionProfilesCombo.getItems().length == 0) {
                if (profileWorker.getProfiles().isEmpty()) {
                    setErrorMessage(getString("no_profiles_configured")); //$NON-NLS-1$
                    ldapURIText.setText(EMPTY_STR);
                    baseDNCombo.setText(EMPTY_STR);
                    return;
                }

                setErrorMessage(null);
                setMessage(getString("select_profile")); //$NON-NLS-1$
                return;
            }

            if (connectionProfilesCombo.getSelectionIndex() < 0)
                return;

            String profileName = connectionProfilesCombo.getText();
            IConnectionProfile profile = profileWorker.getProfile(profileName);
            if (profile == null) {
                // this should really never happen
                setMessage(null);
                setErrorMessage(getString("no_profile_match", profileName)); //$NON-NLS-1$
                return;
            }

            Properties props = profile.getBaseProperties();
            ldapURIText.setText(props.getProperty(ILdapProfileConstants.URL_PROP_ID));

            setErrorMessage(null);
            setMessage(getString("select_profile")); //$NON-NLS-1$
        } finally {
            synchronising = false;
        }
    }

    @Override
    public void profileChanged(IConnectionProfile profile) {
        resetCPComboItems();

        selectConnectionProfile(profile.getName());

        setConnectionProfileInternal(profile);

        notifyChanged();
    }

    /**
     * Performs validation and sets the page status.
     */
    private void setPageStatus() {
        IConnectionProfile connectionProfile = this.importManager.getConnectionProfile();

        if (connectionProfile == null) {
            WizardUtil.setPageComplete(this, getString("no_profile_selected"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        /*
         * Time-consuming check and only relevant to this page.
         *
         * Other pages will call this method through notifyChanged() but it is pointless to keep testing
         * the connection profile since this is the only page it is set from
         */
        if (this.isCurrentPage()) {
            IStatus status = null;
            try {
                status = connectionProfile.connectWithoutJob();
                if (! status.isOK()) {
                    WizardUtil.setPageComplete(this, getString("no_connection_made"), IMessageProvider.ERROR); //$NON-NLS-1$
                    return;
                }
            } finally {
                if (status != null && status.isOK()) {
                    /*
                     * This has the potential to deadlock the UI due to disconnect
                     * creating a job itself that joins to the JobManager. By putting
                     * this inside its own job, at least it locks up the worker thread
                     * rather than the UI thread.
                     */
                    Job job = new Job("Disconnect connection profile") { //$NON-NLS-1$
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            IConnectionProfile connProfile = importManager.getConnectionProfile();
                            if (connProfile != null)
                                connProfile.disconnect();

                            return Status.OK_STATUS;
                        }
                    };
                    job.setSystem(true);
                    job.setUser(false);
                    job.schedule();
                }
            }
        }
        
        if( this.importManager.getSourceModelLocation() == null ) {
            WizardUtil.setPageComplete(this, getString("statusSourceModelLocationUndefined"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        }

        if (this.importManager.getSourceModelName() == null || this.importManager.getSourceModelName().length() == 0) {
            WizardUtil.setPageComplete(this, getString("statusSourceModelNameCannotBeNullOrEmpty"), IMessageProvider.ERROR); //$NON-NLS-1$
            return;
        } else {
        	IStatus status = ModelNameUtil.validate(this.importManager.getSourceModelName(), ModelerCore.MODEL_FILE_EXTENSION, null,
                    ModelNameUtil.IGNORE_CASE );
            if( status.getSeverity() == IStatus.ERROR ) {
            	WizardUtil.setPageComplete(this, ModelNameUtil.MESSAGES.INVALID_MODEL_NAME + status.getMessage(), ERROR);
                return ;
            }
        }

        if (sourceModelPanel.sourceModelExists()) {
            if (!sourceModelPanel.sourceModelHasConnectionProfile()) {
                WizardUtil.setPageComplete(this, getString("statusExistingSourceModelHasNoProfile", importManager.getSourceModelName()), IMessageProvider.ERROR); //$NON-NLS-1$
                return;
            } else if (!sourceModelPanel.sourceModelHasSameConnectionProfile()) {
                WizardUtil.setPageComplete(this, getString("statusExistingSourceModelHasWrongProfile", importManager.getSourceModelName()), IMessageProvider.ERROR); //$NON-NLS-1$
                return;
            }
        } else {
            WizardUtil.setPageComplete(this, getString("statusSourceModelDoesNotExistAndWillBeCreated", importManager.getSourceModelName()), IMessageProvider.INFORMATION); //$NON-NLS-1$
            return;
        }

        WizardUtil.setPageComplete(this);
    }

    void resetCPComboItems() {
        if (connectionProfilesCombo != null) {
            ArrayList profileList = new ArrayList();
            for (IConnectionProfile prof : profileWorker.getProfiles()) {
                profileList.add(prof);
            }

            WidgetUtil.setComboItems(connectionProfilesCombo, profileList, profileLabelProvider, true);
        }
    }

    /**
     * Select the connection profile with the given name
     *
     * @param name
     * @return true if successfully selected
     */
    public boolean selectConnectionProfile(String name) {
        if (name == null) {
            return false;
        }

        int cpIndex = -1;
        int i = 0;
        for (String item : connectionProfilesCombo.getItems()) {
            if (item != null && item.length() > 0) {
                if (item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
                    cpIndex = i;
                    break;
                }
            }
            i++;
        }
        boolean profileChanged = false;
        if (cpIndex > -1) {
            connectionProfilesCombo.select(cpIndex);
            IConnectionProfile profile = profileWorker.getProfile(connectionProfilesCombo.getText());
            this.profileWorker.setSelection(profile);
            IConnectionProfile currentProfile = this.importManager.getConnectionProfile();
            if (profile != currentProfile) {
                profileChanged = true;
                setConnectionProfileInternal(profile);
            }
        }

        return profileChanged;
    }

    private void setConnectionProfileInternal(final IConnectionProfile profile) {
        this.importManager.setConnectionProfile(profile);
        this.fetchBaseDnsButton.setEnabled(importManager.getConnectionProfile() != null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (! visible)
            return;

        if (this.importManager.getConnectionProfile() == null)
            return;

        boolean profileChanged = false;

        if (this.connectionProfilesCombo.getItemCount() > 0) {
            if (this.connectionProfilesCombo.getText() != null
                && this.connectionProfilesCombo.getText().equals(this.importManager.getConnectionProfile().getName())) {
                profileChanged = selectConnectionProfile(this.importManager.getConnectionProfile().getName());
            }
        } else {
            if (this.connectionProfilesCombo.getSelectionIndex() < 0) {
                this.connectionProfilesCombo.select(0);
                profileChanged = true;
            }
        }

        refreshUiFromManager();

        if (profileChanged) {
            handleConnectionProfileSelected();
        }

        if (sourceModelPanel != null) {
            sourceModelPanel.refresh();
        }

        setPageStatus();
    }

    @Override
    public void stateChanged(IChangeNotifier theSource) {
        setPageStatus();
    }

    private void notifyChanged() {
        refreshUiFromManager();
        this.importManager.notifyChanged();
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce.ui.wizards;

import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileWorker;
import org.teiid.designer.datatools.ui.dialogs.IProfileChangedListener;
import org.teiid.designer.datatools.ui.jobs.PingJobWithoutPopup;
import org.teiid.designer.modelgenerator.salesforce.SalesforceImportWizardManager;
import org.teiid.designer.modelgenerator.salesforce.datatools.ISalesForceProfileConstants;
import org.teiid.designer.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * @since 8.0
 */
public class CredentialsPage extends AbstractWizardPage
    implements Listener, IProfileChangedListener, ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CredentialsPage.class);

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    private static final String SF_PROFILE_ID = "org.teiid.designer.datatools.salesforce.connectionProfile"; //$NON-NLS-1$
    
    SalesforceImportWizardManager importManager;

    private Combo connectionProfilesCombo;
    private ILabelProvider profileLabelProvider;
    private IConnectionProfile selectedConnectionProfile;

    private CLabel textFieldUsername;

    private Text textFieldPassword;

    private Button validateButton;
    private Button newCPButton;
    private Button editCPButton;

    private CLabel textFieldURL;

    private ConnectionProfileWorker profileWorker;

    public CredentialsPage( SalesforceImportWizardManager importManager ) {
        super(CredentialsPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
    }

    @Override
    public void createControl( Composite theParent ) {
    	this.profileWorker = new ConnectionProfileWorker(this.getShell(), SF_PROFILE_ID, this);
    	
        GridData gridData;
        final int COLUMNS = 1;
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnl, CREDENTIAL_SELECTION_PAGE);
        setControl(pnl);
        
        // ================================================================================
        Group profileGroup = WidgetFactory.createGroup(pnl, getString("profileLabel.text"), SWT.NONE, 2, 3); //$NON-NLS-1$
        profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        profileLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.connectionProfilesCombo = WidgetFactory.createCombo(profileGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 profileWorker.getProfiles(),
                                                                 profileLabelProvider,
                                                                 true);
        this.connectionProfilesCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Need to sync the worker with the current profile
				int selIndex = connectionProfilesCombo.getSelectionIndex();
				
				String name = connectionProfilesCombo.getItem(selIndex);
				if( name != null ) {
					IConnectionProfile profile = profileWorker.getProfile(name);
					profileWorker.setSelection(profile);
					handleProfileSelection();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        connectionProfilesCombo.setVisibleItemCount(10);
        
        newCPButton = WidgetFactory.createButton(profileGroup, getString("new.label")); //$NON-NLS-1$
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	profileWorker.create();
            }
        });
        
        editCPButton = WidgetFactory.createButton(profileGroup, getString("edit.label")); //$NON-NLS-1$
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	profileWorker.edit();
            }
        });
        
        // ================================================================================
        // properties group
        Group propertiesGroup = WidgetFactory.createGroup(pnl, getString("properties.label"), SWT.NONE, 2, 2); //$NON-NLS-1$

        GridData gdCredentialsGroup = new GridData(GridData.FILL_HORIZONTAL);
        propertiesGroup.setLayoutData(gdCredentialsGroup);
        
        // URL
        CLabel urlLabel = new CLabel(propertiesGroup, SWT.NONE);
        urlLabel.setText(getString("overrideURLCheckbox.text")); //$NON-NLS-1$
        urlLabel.setToolTipText(getString("overrideURLCheckbox.tipText")); //$NON-NLS-1$

        textFieldURL = WidgetFactory.createLabel(propertiesGroup, GridData.FILL_HORIZONTAL);
        String urlText = getString("URLTextField.tooltip"); //$NON-NLS-1$
        textFieldURL.setToolTipText(urlText);
        textFieldURL.addListener(SWT.Selection, this);
        textFieldURL.setEnabled(false);
        
        // username
        CLabel userLabel = new CLabel(propertiesGroup, SWT.NONE);
        userLabel.setText(getString("usernameLabel.text")); //$NON-NLS-1$
        gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        userLabel.setLayoutData(gridData);

        textFieldUsername = WidgetFactory.createLabel(propertiesGroup, GridData.FILL_HORIZONTAL);
        String text = getString("usernameTextField.tooltip"); //$NON-NLS-1$
        textFieldUsername.setToolTipText(text);
        textFieldUsername.setText(EMPTY_STR);
        textFieldUsername.setEnabled(false);

        // Password
        CLabel passwordLabel = new CLabel(propertiesGroup, SWT.NONE);
        passwordLabel.setText(getString("passwordLabel.text")); //$NON-NLS-1$
        final GridData gridData2 = new GridData(SWT.NONE);
        gridData2.horizontalSpan = 1;
        passwordLabel.setLayoutData(gridData2);
        
        textFieldPassword = WidgetFactory.createTextField(propertiesGroup, GridData.FILL_HORIZONTAL);
        text = getString("usernameTextField.tooltip"); //$NON-NLS-1$
        textFieldPassword.setToolTipText(text);
        textFieldPassword.setText(EMPTY_STR);
        this.textFieldPassword.setEchoChar('*');
        this.textFieldPassword.setEnabled(false);

        Composite buttonComposite = WidgetFactory.createPanel(pnl, SWT.NONE, GridData.FILL_VERTICAL);
        GridLayout layout = new GridLayout(1, false);
        buttonComposite.setLayout(layout);
        validateButton = WidgetFactory.createButton(buttonComposite,
                                                    getString("validateCredentialsButton.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        validateButton.setToolTipText(getString("validateCredentialsButton.tipText")); //$NON-NLS-1$
        validateButton.addListener(SWT.Selection, this);
        validateButton.setEnabled(false);
        handleProfileSelection();
    }

    /**
     * 
     */
    protected void handleProfileSelection() {
    	if (null == connectionProfilesCombo.getItems() || 0 == connectionProfilesCombo.getItems().length) {
    		setErrorMessage(getString("no.profile")); //$NON-NLS-1$
    		textFieldUsername.setText(EMPTY_STR);
    		textFieldPassword.setText(EMPTY_STR);
    		textFieldURL.setText(EMPTY_STR);
    		validateButton.setEnabled(false);
    		return;
        }
        
        if( connectionProfilesCombo.getSelectionIndex() < 0 ) {
        	setErrorMessage(null);
            setMessage(getString("select.profile")); //$NON-NLS-1$
        	return;
        }
        
        String selectedItem = connectionProfilesCombo.getItem(connectionProfilesCombo.getSelectionIndex());
        this.selectedConnectionProfile = ProfileManager.getInstance().getProfileByName(selectedItem);
        
        Properties props = selectedConnectionProfile.getBaseProperties();
        textFieldUsername.setText(props.getProperty(ISalesForceProfileConstants.USERNAME_PROP_ID));
        textFieldPassword.setText(props.getProperty(ISalesForceProfileConstants.PASSWORD_PROP_ID));
        if (null == props.getProperty(ISalesForceProfileConstants.URL_PROP_ID)) {
            textFieldURL.setText(UTIL.getString("Common.URL.Default.Label")); //$NON-NLS-1$
        } else {
            textFieldURL.setText(props.getProperty(ISalesForceProfileConstants.URL_PROP_ID));
        }
        setErrorMessage(null);
        setMessage(getString("validate.profile")); //$NON-NLS-1$
        validateButton.setEnabled(true);
        setPageComplete(false);
        importManager.setConnectionProfile(selectedConnectionProfile);
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    @Override
    public void handleEvent( Event event ) {
        if (event.widget == this.validateButton) {
            String selectedItem = connectionProfilesCombo.getItem(connectionProfilesCombo.getSelectionIndex());
            this.selectedConnectionProfile = ProfileManager.getInstance().getProfileByName(selectedItem);

            final PingJobWithoutPopup pingJob = new PingJobWithoutPopup(Display.getCurrent().getActiveShell(), selectedConnectionProfile);
            pingJob.schedule();

            Runnable op = new Runnable() {
                @Override
                public void run() {
                    try {
                        pingJob.join();
                    } catch (InterruptedException e) {
                    }
                }
            };

            UiBusyIndicator.showWhile(getShell().getDisplay(), op);

            if (pingJob.getResult().isOK()) {
                importManager.setConnectionProfile(selectedConnectionProfile);
                setErrorMessage(null);
                setMessage(getString("Click.Next")); //$NON-NLS-1$
                setPageComplete(true);
            } else {
                setErrorMessage(getString("connectionFailedMsg")); //$NON-NLS-1$
                setPageComplete(false);
            }
        }
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }
    
    @Override
	public void profileChanged(IConnectionProfile profile) {
    	resetCPComboItems();
    	
    	selectConnectionProfile(profile.getName());
    }
    
    void resetCPComboItems() {
    	if( connectionProfilesCombo != null ) {
        	ArrayList<IConnectionProfile> profileList = new ArrayList<IConnectionProfile>();
            for( IConnectionProfile prof : profileWorker.getProfiles()) {
            	profileList.add(prof);
            }
            
            WidgetUtil.setComboItems(connectionProfilesCombo, profileList, profileLabelProvider, true);
    	}
    }
    
    void selectConnectionProfile(String name) {
    	if( name == null ) {
    		return;
    	}
    	
    	int cpIndex = -1;
    	int i = 0;
    	for( String item : connectionProfilesCombo.getItems()) {
    		if( item != null && item.length() > 0 ) {
    			if( item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
    				cpIndex = i;
    				break;
    			}
    		}
    		i++;
    	}
    	if( cpIndex > -1 ) {
    		connectionProfilesCombo.select(cpIndex);
    	}
    	handleProfileSelection();
    }
}

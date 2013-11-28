/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.ws.IWSProfileConstants;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileWorker;
import org.teiid.designer.datatools.ui.dialogs.IProfileChangedListener;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.util.ModelGeneratorWsdlUiUtil;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.WsdlOperationsPanel;
import org.teiid.designer.ui.common.UiConstants.ConnectionProfileIds;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 * @since 8.0
 */
public class WsdlDefinitionPage extends WizardPage 
	implements IChangeListener, Listener, IProfileChangedListener, FileUtils.Constants,
	ModelGeneratorWsdlUiConstants, ModelGeneratorWsdlUiConstants.Images, ModelGeneratorWsdlUiConstants.HelpContexts {

	/** <code>IDialogSetting</code>s key for saved dialog height. */
	private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog width. */
	private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog X position. */
	private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog Y position. */
	private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

	/** Source radio buttons and validate button */
	private Button buttonValidateWSDL;

	/** Source and target text fields */
	private Text wsdlURIText;
	private Text endPointNameText;
	private Text endPointURIText;

	private Button newCPButton;
	private Button editCPButton;

	/** The import manager. */
	private WSDLImportWizardManager importManager;

	private MultiStatus wsdlStatus;

	private boolean initializing = false;

	private Combo connectionProfilesCombo;
	private ILabelProvider profileLabelProvider;

	private ConnectionProfileWorker profileWorker;

	boolean synchronizing = false;
	
	WsdlOperationsPanel operationsPanel;
	
	ImportWsdlSoapWizard wizard;

	/**
	 * Constructs the page with the provided import manager
	 * 
	 * @param theImportManager
	 *            the import manager object
	 * @param wizard that this page is displayed on
	 */
	public WsdlDefinitionPage(WSDLImportWizardManager theImportManager, ImportWsdlSoapWizard wizard) {
		super(WsdlDefinitionPage.class.getSimpleName(), Messages.WsdlDefinitionPage_title, null);
		this.importManager = theImportManager;
		this.wizard = wizard;
		setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
		this.importManager.addChangeListener(this);
	}

	/**
	 * widget event handler
	 * 
	 * @param event
	 *            the widget event
	 */
	@Override
	public void handleEvent(Event event) {
		if (!initializing) {
			boolean validate = false;

			if (event.widget == this.buttonValidateWSDL) {
				handleValidateWSDLButtonPressed();
				validate = true;
			}

			// Update the page status
			if (validate) {
				setPageStatus();
			}
		}
	}

	/**
	 * Updates the enabled state of source selection controls
	 */
	private void updateWidgetEnablements() {
		// Workspace control enablement

		updateValidateWSDLButtonEnablement();
	}

	/**
	 * Updates the enabled state of the WSDL validation button.
	 */
	private void updateValidateWSDLButtonEnablement() {
		// if wsdl already has valid status, disable
		if (this.wsdlStatus != null && this.wsdlStatus.isOK()) {
			this.buttonValidateWSDL.setEnabled(false);
		} else {
			// if there is a wsdl selection, enable validation button
			if (this.importManager.getWSDLFileUri() != null) {
				this.buttonValidateWSDL.setEnabled(true);
			} else {
				this.buttonValidateWSDL.setEnabled(false);
			}
		}
//		if( this.operationsPanel != null ) {
//			this.operationsPanel.notifyWsdlChanged();
//		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite theParent) {
		//
		// create main container
		//

		this.profileWorker = new ConnectionProfileWorker(this.getShell(), ConnectionProfileIds.CATEGORY_WS_SOAP, this);

		final int COLUMNS = 1;
		Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
		GridLayout layout = new GridLayout(COLUMNS, false);
		pnlMain.setLayout(layout);
		setControl(pnlMain);

		IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
		helpSystem.setHelp(pnlMain, WSDL_SELECTION_PAGE);

		// Controls for Selection of WSDL
		createSourceSelectionComposite(pnlMain);
		
		createWsdlOperationsPanel(pnlMain);

		// Set the initial page status
		setPageStatus();
	}

	/**
	 * Constructs the source WSDL selection component panel.
	 * 
	 * @param theParent
	 *            the parent container
	 */
	private void createSourceSelectionComposite(Composite theParent) {
		final int COLUMNS = 1;

		Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
		pnl.setLayout(new GridLayout(COLUMNS, false));

		// ================================================================================
		Group profileGroup = WidgetFactory.createGroup(pnl, Messages.WsdlDefinitionPage_profileLabel_text, SWT.NONE, 2, 3);
		profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		profileLabelProvider = new LabelProvider() {

			@Override
			public String getText(final Object source) {
			    if (source == null)
			        return ""; //$NON-NLS-1$

				return ((IConnectionProfile) source).getName();
			}
		};
		this.connectionProfilesCombo = WidgetFactory.createCombo(profileGroup, SWT.READ_ONLY, GridData.FILL_HORIZONTAL,
			profileWorker.getProfiles(), profileLabelProvider, true);
		this.connectionProfilesCombo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		this.connectionProfilesCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Need to sync the worker with the current profile
				handleConnectionProfileSelected();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		connectionProfilesCombo.setVisibleItemCount(10);

		newCPButton = WidgetFactory.createButton(profileGroup, Messages.New);
		newCPButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				profileWorker.create();
			}
		});

		editCPButton = WidgetFactory.createButton(profileGroup, Messages.Edit);
		editCPButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				profileWorker.edit();
			}
		});

		// options group
		Group wsdlURIGroup = WidgetFactory.createGroup(pnl, Messages.WsdlDefinitionPage_wsdlLabel_text, SWT.FILL, 2, 2);
		wsdlURIGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Workspace textfield
		wsdlURIText = new Text(wsdlURIGroup, SWT.BORDER | SWT.SINGLE);
		wsdlURIText.setToolTipText(Messages.WsdlDefinitionPage_workspaceTextField_tooltip);
		wsdlURIText.setForeground(wsdlURIGroup.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		wsdlURIText.setBackground(wsdlURIGroup.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		wsdlURIText.setEditable(false);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(wsdlURIText);
		
		// --------------------------------------------
		// WSDL Validation Button
		// --------------------------------------------
		buttonValidateWSDL = WidgetFactory.createButton(wsdlURIGroup,
			Messages.WsdlDefinitionPage_validateWsdlButton_text, GridData.HORIZONTAL_ALIGN_END, 1);
		buttonValidateWSDL.setToolTipText(Messages.WsdlDefinitionPage_validateWsdlButton_tooltip);

		// --------------------------------------------
		// Add Listener to handle selection events
		// --------------------------------------------
		buttonValidateWSDL.addListener(SWT.Selection, this);
		
		// End point
		Group endPointGroup = WidgetFactory.createGroup(pnl, Messages.WsdlDefinitionPage_endPointLabel_text, SWT.FILL, 2, 4);
		endPointGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label endPointNameLabel = WidgetFactory.createLabel(endPointGroup, Messages.WsdlDefinitionPage_endPointNameLabel_label);
		endPointNameLabel.setToolTipText(Messages.WsdlDefinitionPage_endPointNameTextField_tooltip);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(endPointNameLabel);

		endPointNameText = new Text(endPointGroup, SWT.BORDER | SWT.SINGLE);
		endPointNameText.setToolTipText(Messages.WsdlDefinitionPage_endPointNameTextField_tooltip);
		endPointNameText.setForeground(endPointGroup.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		endPointNameText.setBackground(endPointGroup.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		endPointNameText.setEditable(false);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(endPointNameText);
		
		Label endPointURILabel = WidgetFactory.createLabel(endPointGroup, Messages.WsdlDefinitionPage_endPointURILabel_label);
		GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(endPointURILabel);
        
		endPointURIText = new Text(endPointGroup, SWT.BORDER | SWT.SINGLE);
		endPointURIText.setToolTipText(Messages.WsdlDefinitionPage_endPointURITextField_tooltip);
		endPointURIText.setForeground(endPointGroup.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		endPointURIText.setBackground(endPointGroup.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		endPointURIText.setEditable(false);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(endPointURIText);
        
		updateWidgetEnablements();
	}

	private void handleConnectionProfileSelected() {
		int selIndex = connectionProfilesCombo.getSelectionIndex();

		if (selIndex >= 0) {
			String name = connectionProfilesCombo.getItem(selIndex);
			if (name != null) {
				IConnectionProfile currentProfile = importManager.getConnectionProfile();
				IConnectionProfile profile = profileWorker.getProfile(name);
				boolean profileChanged = true;
				if (currentProfile != null && (currentProfile.getName().equals(profile.getName()))) {
					profileChanged = false;
				}

				profileWorker.setSelection(profile);
				setConnectionProfileInternal(profile);
				if (profileChanged) {
					this.wsdlStatus = null;
				}
				
				this.operationsPanel.notifyWsdlChanged();
			}
		}
		
		notifyChanged();
	}
	
	private void createWsdlOperationsPanel(Composite theParent) {
		this.operationsPanel = new WsdlOperationsPanel(theParent, this, this.importManager);
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
	 * the WSDL import Wizard.
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
	 * Handler for Validate WSDL Button pressed
	 */
	private void handleValidateWSDLButtonPressed() {
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) {
				validateWSDL(monitor);
			}
		};

		try {
			final ProgressMonitorDialog dlg = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			dlg.run(false, false, op);
		} catch (final InterruptedException ignored) {
		} catch (final Exception err) {
			err.printStackTrace(System.err);
		}
		if (!this.wsdlStatus.isOK()) {
			Shell shell = this.getShell();
			ErrorDialog.openError(shell, Messages.WsdlDefinitionPage_dialog_wsdlValidationError_title,
				Messages.WsdlDefinitionPage_dialog_wsdlValidationError_msg, this.wsdlStatus);
			//this.operationsPanel.notifyWsdlChanged();
		} else {
			//this.operationsPanel.notifyWsdlChanged();
		}
		
		updateValidateWSDLButtonEnablement();
		notifyChanged();
	}

	void validateWSDL(IProgressMonitor monitor) {
		this.wsdlStatus = this.importManager.validateWSDL(monitor);
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
		synchronizing = true;

		if (this.importManager != null) {

			if (null == connectionProfilesCombo.getItems() || 0 == connectionProfilesCombo.getItems().length) {
				if (profileWorker.getProfiles().isEmpty()) {
					setErrorMessage(Messages.WsdlDefinitionPage_no_profile_match);
					WidgetUtil.setText(wsdlURIText, null);
					WidgetUtil.setText(endPointNameText, null);
					WidgetUtil.setText(endPointURIText, null);
					buttonValidateWSDL.setEnabled(false);
					return;
				}

				setErrorMessage(null);
				setMessage(Messages.WsdlDefinitionPage_select_profile);
				return;
			}

			if (connectionProfilesCombo.getSelectionIndex() < 0) {
				return;
			}

			String profileName = connectionProfilesCombo.getText();
			IConnectionProfile profile = profileWorker.getProfile(profileName);
			if (null == profile) {
				// this should really never happen
				setMessage(null);
				setErrorMessage(NLS.bind(Messages.WsdlDefinitionPage_no_profile_match, new Object[] { profileName }));
				buttonValidateWSDL.setEnabled(false);
				return;
			}
			Properties props = profile.getBaseProperties();
			WidgetUtil.setText(wsdlURIText, props.getProperty(IWSProfileConstants.WSDL_URI_PROP_ID));
			WidgetUtil.setText(endPointNameText, props.getProperty(IWSProfileConstants.END_POINT_NAME_PROP_ID));
			WidgetUtil.setText(endPointURIText, ConnectionInfoHelper.readEndPointProperty(props));

			updateWidgetEnablements();
			setErrorMessage(null);
			setMessage(Messages.WsdlDefinitionPage_select_profile);
		}

		synchronizing = false;
	}

	@Override
	public void profileChanged(IConnectionProfile profile) {
		resetCPComboItems();

		String profileName = profile != null ? profile.getName() : null;
		selectConnectionProfile(profileName);

		setConnectionProfileInternal(profile);

		notifyChanged();
	}

	/**
	 * Performs validation and sets the page status.
	 */
	public void setPageStatus() {
        // Check connection profile status
        IStatus connProfileStatus = this.importManager.getValidator().getConnectionProfileStatus();

        if (connProfileStatus.getSeverity() != IStatus.OK) {
            WizardUtil.setPageComplete(this,
                                       this.importManager.getValidator().getPrimaryMessage(connProfileStatus),
                                       WizardUtil.getMessageSeverity(connProfileStatus.getSeverity()));
            return;
        }

		// Validate the source WSDL Selection
		boolean sourceValid = validateSourceSelection();
		if (!sourceValid) {
			return;
		}

		if( this.importManager.getSelectedOperations().size() == 0 ) {
			WizardUtil.setPageComplete(this, Messages.NoOperationsSelected, IMessageProvider.ERROR);
		} else if (this.wsdlStatus != null && this.wsdlStatus.getSeverity() > IStatus.WARNING) {
			WizardUtil.setPageComplete(this, Messages.WsdlDefinitionPage_wsdlErrorContinuation_msg, IMessageProvider.WARNING);
		} else if( this.operationsPanel.getStatus().getSeverity() > IStatus.WARNING){
			WizardUtil.setPageComplete(this, this.operationsPanel.getStatus().getMessage(), IMessageProvider.ERROR);
		} else {
			WizardUtil.setPageComplete(this);
		}

		getContainer().updateButtons();
	}

	/**
	 * Sets the initial workspace selection.
	 * 
	 * @param theSelection
	 *            the current workspace selection
	 */
	public void setInitialSelection(ISelection theSelection) {
		this.importManager.setWSDLFileUri(null);
		if (!theSelection.isEmpty() && (theSelection instanceof IStructuredSelection)) {
			Object[] selectedObjects = ((IStructuredSelection) theSelection).toArray();

			// Set the selected container as the target location
			if (selectedObjects.length == 1) {
				final IContainer container = ModelUtil.getContainer(selectedObjects[0]);
				if (container != null) {
					this.importManager.setViewModelLocation(container);
					this.importManager.setSourceModelLocation(container);
				}
			}

			for (int i = 0; i < selectedObjects.length; i++) {
				if (selectedObjects[i] instanceof IFile) {
					if (ModelUtilities.isWsdlFile((IFile) selectedObjects[i])
						|| ModelUtilities.isModelFile((IFile) selectedObjects[i])) {
						// Convert the IFile object to a File object
						File fNew = ((IFile) selectedObjects[i]).getLocation().toFile();
						if (fNew != null) {
							String uriStr = null;
							try {
								uriStr = fNew.toURI().toURL().toExternalForm();
							} catch (MalformedURLException err) {
								// exception will leave uri null
							}
							if (ModelUtilities.isWsdlFile((IFile) selectedObjects[i])) {
								this.importManager.setUriSource(WSDLImportWizardManager.WORKSPACE_SOURCE);
								this.importManager.setWSDLFileUri(uriStr);
								break;
							} else if (ModelUtilities.isModelFile((IFile) selectedObjects[i])) {
								this.importManager.setViewModelName(uriStr.substring(uriStr.lastIndexOf('/') + 1));
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * validate the selected source WSDL. Returns 'true' if the validation is
	 * successful, 'false' if not.
	 * 
	 * @return 'true' if the WSDL selection is valid, 'false' if not.
	 */
	private boolean validateSourceSelection() {
		String msg = Messages.WsdlDefinitionPage_pageComplete_msg;
		String sourceWsdl = this.importManager.getWSDLFileUri();

		// If no WSDL is specified, set message and return
		if (sourceWsdl == null) {
			// No WSDL selected message
			msg = Messages.WsdlDefinitionPage_noWsdlSelected_msg;
			// If URL radio is selected, check URL validity
			WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
			return false;
		}

		// If WSDL is specified, see if it's been validated
		if (this.wsdlStatus != null && this.wsdlStatus.getSeverity() > IStatus.WARNING) {
			msg = this.wsdlStatus.getMessage();
			WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
			return false;
		}

		WizardUtil.setPageComplete(this);

		return true;
	}

	void resetCPComboItems() {
		if (connectionProfilesCombo != null) {
			List<IConnectionProfile> profileList = new ArrayList<IConnectionProfile>();
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
			if( profile != currentProfile ) {
				profileChanged = true;
				setConnectionProfileInternal(profile);
			}
		}

		/*
		 * Want to revalidate the operations even if the connection
		 * profile is the same since its properties may have been edited
		 */
		this.operationsPanel.notifyWsdlChanged();
		return profileChanged;
	}
	
	private void setConnectionProfileInternal(final IConnectionProfile profile) {
        this.importManager.setConnectionProfile(profile);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			boolean profileChanged = false;
			
			if (this.connectionProfilesCombo.getItemCount() > 0 ) {
				if( this.importManager.getConnectionProfile() != null ) {
					if( this.connectionProfilesCombo.getText() != null &&
						this.connectionProfilesCombo.getText().equals(this.importManager.getConnectionProfile().getName()) ) {
						profileChanged = selectConnectionProfile(this.importManager.getConnectionProfile().getName());
					}
				} else {
    				if(this.connectionProfilesCombo.getSelectionIndex() < 0 ) {
    					this.connectionProfilesCombo.select(0);
    					profileChanged = true;
    				}
				}

			} 

			refreshUiFromManager();
			
			if( profileChanged ) {
				handleConnectionProfileSelected();
			}
			
			setPageStatus();
		}
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		//refreshUiFromManager();
		setPageStatus();
	}
	
	private void notifyChanged() {
	    refreshUiFromManager();
		this.importManager.notifyChanged();
	}
}

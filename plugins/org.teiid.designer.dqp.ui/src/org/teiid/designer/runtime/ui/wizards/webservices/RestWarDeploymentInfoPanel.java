/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices;

import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.DqpUiStringUtil;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.DesignerProperties;

/**
 * @since 8.0
 */
public abstract class RestWarDeploymentInfoPanel extends Composite implements
		InternalModelerWarUiConstants {

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private static final String I18N_PREFIX = I18nUtil
			.getPropertyPrefix(RestWarDeploymentInfoPanel.class);
	protected static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
	private static final String SECURITY_OPTIONS_GROUP = getString("securityOptionsGroup"); //$NON-NLS-1$
	public static final String NOSECURITY = getString("noSecurityButton"); //$NON-NLS-1$
	
	// /////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	// /////////////////////////////////////////////////////////////////////////////////////////////
	protected IDialogSettings settings;
	protected RestWarDeploymentInfoDialog dialog;
	protected Text txfWarFileDeploymentLocation;
	protected Text txfContext;
	protected Text txfJNDIName;
	protected Button checkboxIncludeRestJars;
	private Button warBrowseButton;
	private Button restoreDefaultButton;
	protected Text txfSecurityRealm;
	protected Text txfSecurityRole;
	/**
	 * @since 8.2
	 */
	public static final String BASIC = getString("basicButton"); //$NON-NLS-1$
	/**
	 * @since 8.2
	 */
	protected Button noSecurityButton, basicSecurityButton;

	protected IFile theVdb;

	protected String WARFILELOCATION;
	protected String CONTEXTNAME;
	protected String JNDI_NAME;
	/**
	 * @since 8.2
	 */
	protected String SECURITY_TYPE,SECURITY_REALM,SECURITY_ROLE;
	private DesignerProperties designerProperties;

	/**
	 * @param parent
	 * @param dialog
	 * @param theVdb
	 * @param designerProperties
	 * @since 7.4
	 */
	public RestWarDeploymentInfoPanel(Composite parent,
			RestWarDeploymentInfoDialog dialog, IFile theVdb,
			Properties designerProperties) {
		super(parent, SWT.NONE);
		this.dialog = dialog;
		this.theVdb = theVdb;
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.designerProperties = (DesignerProperties) designerProperties;
		init(this);
	}

	/**
	 * @param id
	 * @return
	 * @since 7.4
	 */
	protected static String getString(final String id) {
		return DqpUiStringUtil.getString(I18N_PREFIX + id);
	}

	/**
	 * @since 7.4
	 */
	protected void loadData() {

		try {
			// war file location
			String text = (this.settings.get(WARFILELOCATION) == null ? RestWarDataserviceModel
					.getInstance().getWarFileLocation() : this.settings
					.get(WARFILELOCATION));
			txfWarFileDeploymentLocation.setText(text);

			// context name should be populated as default, not from the
			// settings.
			text = RestWarDataserviceModel.getInstance().getContextName();
			txfContext.setText(text);

			// JNDI Name
			text = RestWarDataserviceModel.getInstance().getJndiName();
			txfJNDIName.setText(text);
			if (designerProperties != null) {
				String vdbJndiName = this.designerProperties.getVdbJndiName();
				if (vdbJndiName != null) {
					txfJNDIName.setText(vdbJndiName);
				}
			}

            // Security type
            text = (this.settings.get(SECURITY_TYPE) == null ? RestWarDataserviceModel.getInstance().getSecurityType() : this.settings.get(SECURITY_TYPE));
            if (text != null && text.equals(BASIC)) {
                this.basicSecurityButton.setSelection(true);
                this.noSecurityButton.setSelection(false);
                basicSecurityButtonSelected();
            } else {
                this.noSecurityButton.setSelection(true);
                this.basicSecurityButton.setSelection(false);
                noSecurityButtonSelected();
            }

            // Security Realm Name
            text = (this.settings.get(SECURITY_REALM) == null ? RestWarDataserviceModel.getInstance().getSecurityRealm() : this.settings.get(SECURITY_REALM));
            if (text != null)
                txfSecurityRealm.setText(text);

            // Security Role Name
            text = (this.settings.get(SECURITY_ROLE) == null ? RestWarDataserviceModel.getInstance().getSecurityRole() : this.settings.get(SECURITY_ROLE));
            if (text != null)
                txfSecurityRole.setText(text);

		} catch (RuntimeException err) {
			DqpUiConstants.UTIL.log(err);
		}

	}

	/**
	 * @param isValid
	 * @since 7.4
	 */
	protected void setDialogMessage(boolean isValid) {

		this.dialog.setMessage(INITIAL_MESSAGE);
		this.dialog.setOkButtonEnable(isValid);
	}

	/**
	 * @param status
	 * @since 7.4
	 */
	protected void setDialogMessage(IStatus status) {
		boolean isError = (status.getSeverity() == IStatus.ERROR);

		/**
		 * Need to convert the error status code from 4 to 3 because error code
		 * in setMessage() is not mapped correctly or IStatus.ERROR !=
		 * IMessageProvider.ERROR
		 */
		int statusCode = (status.getSeverity() == IStatus.ERROR ? IMessageProvider.ERROR
				: status.getSeverity());

		this.dialog.setMessage(INITIAL_MESSAGE);
		if (!status.isOK()) {
			this.dialog.setMessage(status.getMessage(), statusCode);
		}

		this.dialog.setOkButtonEnable(!isError);
	}

	/**
	 * @since 7.4
	 */
	protected abstract void validatePage();

	/**
	 * @param parent
	 * @since 7.4
	 */
	private void init(Composite parent) {

		createDeploymentInfoComposite(parent);
		createRestoreDefaultAndIncludeJars(parent);

		this.settings = WidgetUtil.initializeSettings(this,
				DqpUiPlugin.getDefault());

		addListeners();
	}

	/**
	 * @param parent
	 * @since 7.4
	 */
	private void createDeploymentInfoComposite(final Composite parent) {

		// Create info group panel
		String text = getString("grpPanelText"); //$NON-NLS-1$
		Group pnlContents = WidgetFactory.createGroup(parent, text,
				GridData.FILL_HORIZONTAL, 3, 3);
		// ------------------------------------
		// REST Service WAR components
		// ------------------------------------
		// contextLabel
		CONTEXTNAME = getString("contextLabel"); //$NON-NLS-1$       
		WidgetFactory.createLabel(pnlContents,
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, CONTEXTNAME);

		// context name
		txfContext = WidgetFactory.createTextField(pnlContents,
				GridData.FILL_HORIZONTAL, 2);
		text = getString("contextTooltip"); //$NON-NLS-1$
		txfContext.setToolTipText(text);

		// JNDILabel
		JNDI_NAME = getString("jndiLabel"); //$NON-NLS-1$       
		WidgetFactory.createLabel(pnlContents,
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, JNDI_NAME);

		// jndi name
		txfJNDIName = WidgetFactory.createTextField(pnlContents,
				GridData.FILL_HORIZONTAL, 2);
		text = getString("jndiTooltip"); //$NON-NLS-1$
		txfJNDIName.setToolTipText(text);

		// WAR file save location Label
		this.WARFILELOCATION = getString("warFileSaveLocationLabel"); //$NON-NLS-1$       
		WidgetFactory.createLabel(pnlContents,
				GridData.HORIZONTAL_ALIGN_BEGINNING, 1, this.WARFILELOCATION);

		// WAR file save location textfield
		this.txfWarFileDeploymentLocation = WidgetFactory.createTextField(
				pnlContents, GridData.FILL_HORIZONTAL, 1);
		text = getString("warFileSaveLocationTooltip"); //$NON-NLS-1$
		this.txfWarFileDeploymentLocation.setToolTipText(text);

		// WAR folder browse button
		this.warBrowseButton = WidgetFactory.createButton(pnlContents,
				InternalUiConstants.Widgets.BROWSE_BUTTON);
		this.warBrowseButton.setText(getString("changeButtonText")); //$NON-NLS-1$
		this.warBrowseButton.setToolTipText(text);
		this.warBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent theEvent) {
				handleWarBrowseSourceSelected();
			}
		});

		final Group securityOptionsGroup = WidgetFactory.createGroup(
				pnlContents, SECURITY_OPTIONS_GROUP, GridData.FILL_HORIZONTAL,
				3, 3);
		{
			CLabel label3 = new CLabel(securityOptionsGroup, SWT.WRAP);
			label3.setText("When using HTTPBasic security, a local Teiid connection is required using the PassthroughAuthentication property."); //$NON-NLS-1$
			final GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
			gridData3.horizontalSpan = 3;
			label3.setLayoutData(gridData3);

			this.noSecurityButton = WidgetFactory.createRadioButton(
					securityOptionsGroup, NOSECURITY);
			this.noSecurityButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					noSecurityButtonSelected();
				}
			});
			this.noSecurityButton.setSelection(true);
			this.basicSecurityButton = WidgetFactory.createRadioButton(
					securityOptionsGroup, BASIC);
			this.basicSecurityButton
					.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent event) {
							basicSecurityButtonSelected();
						}
					});
			
			new CLabel(securityOptionsGroup, SWT.NONE);

			// security realm Label
			this.SECURITY_REALM = getString("securityRealmLabel"); //$NON-NLS-1$       
			WidgetFactory.createLabel(securityOptionsGroup,
					GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_REALM);

			// security realm
			this.txfSecurityRealm = WidgetFactory.createTextField(
					securityOptionsGroup, GridData.FILL_HORIZONTAL, 2);
			text = getString("securityRealmTooltip"); //$NON-NLS-1$;
			this.txfSecurityRealm.setToolTipText(text);
			this.txfSecurityRealm.setEnabled(false);

			// security role Label
			this.SECURITY_ROLE = getString("securityRoleLabel"); //$NON-NLS-1$       
			WidgetFactory.createLabel(securityOptionsGroup,
					GridData.HORIZONTAL_ALIGN_BEGINNING, 1, SECURITY_ROLE);

			// security role
			this.txfSecurityRole = WidgetFactory.createTextField(
					securityOptionsGroup, GridData.FILL_HORIZONTAL, 2);
			text = getString("securityRoleTooltip"); //$NON-NLS-1$;
			this.txfSecurityRole.setToolTipText(text);
			this.txfSecurityRole.setEnabled(false);
		}

	}

	/**
	 * @param parent
	 * @since 7.4
	 */
	private void createRestoreDefaultAndIncludeJars(final Composite parent) {

		// Create page
		Composite restoreDefault = WidgetFactory.createPanel(parent, SWT.NONE,
				GridData.FILL_HORIZONTAL, 1);

		GridLayout layout = new GridLayout();
		restoreDefault.setLayout(layout);
		layout.numColumns = 2;

		this.checkboxIncludeRestJars = WidgetFactory.createCheckBox(
				restoreDefault, getString("includeJars.text"), //$NON-NLS-1$
				GridData.FILL_HORIZONTAL, true);
		String text = getString("includeJars.tooltip"); //$NON-NLS-1$
		this.checkboxIncludeRestJars.setToolTipText(text);
		this.checkboxIncludeRestJars
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent theEvent) {
						handleIncludeJarsSelected();
					}
				});

		// Restore default button
		text = getString("restoreDefaultButtonText"); //$NON-NLS-1$ 
		this.restoreDefaultButton = WidgetFactory.createButton(restoreDefault,
				text, GridData.END);
		text = getString("restoreDefaultTooltip"); //$NON-NLS-1$
		this.restoreDefaultButton.setToolTipText(text);
		this.restoreDefaultButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				restoreDefaultButtonPressed();
			}
		});

	}

	/**
	 * @since 7.4
	 */
	private void addListeners() {

		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent theEvent) {
				validatePage();
				setWarFileNameInDialog();
			}
		};

		this.txfWarFileDeploymentLocation.addModifyListener(modifyListener);
		this.txfContext.addModifyListener(modifyListener);
		this.txfJNDIName.addModifyListener(modifyListener);
		this.txfSecurityRealm.addModifyListener(modifyListener);
		this.txfSecurityRole.addModifyListener(modifyListener);
	}

	protected void setWarFileNameInDialog() {
		dialog.setWarFileName(txfContext.getText());
	}

	void restoreDefaultButtonPressed() {
		this.txfWarFileDeploymentLocation.setText(RestWarDataserviceModel
				.getInstance().getWarFilenameDefault());
		this.txfContext.setText(RestWarDataserviceModel.getInstance()
				.getContextNameDefault());
		this.txfJNDIName.setText(RestWarDataserviceModel.getInstance()
				.getJndiNameDefault());
	}
	
	void noSecurityButtonSelected() {
        if (this.noSecurityButton.getSelection()) {
            RestWarDataserviceModel.getInstance().setSecurityTypeDefault(NOSECURITY);
            this.txfSecurityRealm.setText(CoreStringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRealm.setEnabled(false);
            this.txfSecurityRole.setText(CoreStringUtil.Constants.EMPTY_STRING);
            this.txfSecurityRole.setEnabled(false);
        }

        validatePage();
    }

    /**
     * @since 7.1.1
     */
    void basicSecurityButtonSelected() {
        if (this.basicSecurityButton.getSelection()) {
        	RestWarDataserviceModel.getInstance().setSecurityTypeDefault(BASIC);
            this.txfSecurityRealm.setText("teiid-security"); //$NON-NLS-1$
            RestWarDataserviceModel.getInstance().setSecurityRealmDefault("teiid-security"); //$NON-NLS-1$
            this.txfSecurityRealm.setEnabled(true);
            this.txfSecurityRole.setText("MyRole"); //$NON-NLS-1$
            RestWarDataserviceModel.getInstance().setSecurityRoleDefault("MyRole"); //$NON-NLS-1$
            this.txfSecurityRole.setEnabled(true);
        }

        validatePage();
    }


	void handleWarBrowseSourceSelected() {
		DirectoryDialog folderDialog = new DirectoryDialog(getShell());
		folderDialog.setText(getString("warTitle")); //$NON-NLS-1$
		folderDialog.setMessage(getString("warMessage")); //$NON-NLS-1$
		folderDialog.setFilterPath(txfWarFileDeploymentLocation.getText());
		String selectedUnit = folderDialog.open();

		if (selectedUnit != null) {
			this.txfWarFileDeploymentLocation.setText(selectedUnit);
		}
	}

	void handleIncludeJarsSelected() {

		RestWarDataserviceModel.getInstance().setIncludeJars(
				this.checkboxIncludeRestJars.getSelection());
	}
}

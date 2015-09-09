package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.util.ModelGeneratorWsdlUiUtil;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.SelectWsdlOperationsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class OperationsOptionsPage extends AbstractWizardPage implements
		IChangeListener, ModelGeneratorWsdlUiConstants,
		ModelGeneratorWsdlUiConstants.Images {

	/** <code>IDialogSetting</code>s key for saved dialog height. */
	private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog width. */
	private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog X position. */
	private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

	/** <code>IDialogSetting</code>s key for saved dialog Y position. */
	private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

	/** The import manager. */
	WSDLImportWizardManager importManager;

	ImportWsdlSoapWizard wizard;

	Button generateDefaultProceduresButton;
	Button generateCustomProceduresButton;

	/**
	 * Constructs the page with the provided import manager
	 * 
	 * @param theImportManager
	 *            the import manager object
	 */
	public OperationsOptionsPage(WSDLImportWizardManager theImportManager,
			ImportWsdlSoapWizard wizard) {
		super(SelectWsdlOperationsPage.class.getSimpleName(), Messages.OperationsTypeSelection);
		this.importManager = theImportManager;
		this.wizard = wizard;
		setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
		this.importManager.addChangeListener(this);
		setMessage(Messages.OperationsTypeSelectionMessage);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.2
	 */
	@Override
	@SuppressWarnings("unused")
	public void createControl(Composite theParent) {
		final Composite hostPanel = new Composite(theParent, SWT.NONE);
		hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hostPanel.setLayout(new GridLayout(1, false));

		// Create page
		DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
		GridDataFactory.fillDefaults().grab(true, false);

		final Composite pnlMain = scrolledComposite.getPanel();
		pnlMain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlMain.setLayout(new GridLayout(1, false));
		((GridData) pnlMain.getLayoutData()).widthHint = 400;

		GENERATE_PROCEDURE_OPTION: {
			// Includes a radio button and description for
			// 1) Generate Default Procedures and
			// 2) Customize Request & Response
			Group group = WidgetFactory.createGroup(pnlMain,
					Messages.ProcedureGenerationOptions,
					GridData.HORIZONTAL_ALIGN_FILL, 1);
			GridDataFactory.fillDefaults().grab(true,  false).applyTo(group);

			generateCustomProceduresButton = WidgetFactory.createRadioButton(group, Messages.CustomProcedures, true);
			generateCustomProceduresButton.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							handleGenerateOptionChanged();
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});

			Text customProceduresHelpText = new Text(group, SWT.WRAP | SWT.READ_ONLY);
			customProceduresHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
			customProceduresHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			customProceduresHelpText.setText(Messages.OptionDefineCustomProcures);
			GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(customProceduresHelpText);

			generateDefaultProceduresButton = WidgetFactory.createRadioButton(group, Messages.DefaultProcedures, false);
			generateDefaultProceduresButton.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							handleGenerateOptionChanged();
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});

			Text defaultProceduresHelpText = new Text(group, SWT.WRAP | SWT.READ_ONLY);
			defaultProceduresHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
			defaultProceduresHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			defaultProceduresHelpText.setText(Messages.OptionDefineDefaultProcedures);
			GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT)
					.align(SWT.FILL, SWT.FILL).grab(true, true)
					.applyTo(defaultProceduresHelpText);
		}

		scrolledComposite.sizeScrolledPanel();

		setControl(hostPanel);
	}

	private void handleGenerateOptionChanged() {
		// need to set the boolean value on importManager and notify the wizard
		// to update it's pages
		// to remove Page 3 if generate default is checked
		this.importManager.setGenerateDefaultProcedures(this.generateDefaultProceduresButton.getSelection());
		this.wizard.notifyManagerChanged();

		notifyChanged();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 * @since 4.2
	 */
	@Override
	public void dispose() {
		saveState();
	}

	/**
	 * Override to replace the NewModelWizard settings with the section devoted
	 * to the Web Service Model Wizard.
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
	 * @since 4.2
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
	 * Persists dialog size and position.
	 * 
	 * @since 4.2
	 */
	private void saveState() {
		IDialogSettings settings = getDialogSettings();

		if (settings != null) {
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
	 * Sets the wizard page status message.
	 * 
	 * @since 4.2
	 */
	void setPageStatus() {
		// Check importOptionsPanel status
		IStatus modelStatus = this.importManager.getValidator()
				.getModelsStatus();

		if (modelStatus.getSeverity() == IStatus.OK) {
			WizardUtil.setPageComplete(this);
			this.setMessage(modelStatus.getMessage());
		} else {
			WizardUtil.setPageComplete(this, 
					this.importManager.getValidator().getPrimaryMessage(modelStatus), 
					WizardUtil.getMessageSeverity(modelStatus.getSeverity()));
		}

		getContainer().updateButtons();
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible) {
			this.importManager.validate();

			this.generateDefaultProceduresButton.setSelection(this.importManager.doGenerateDefaultProcedures());
			this.generateCustomProceduresButton.setSelection(!this.importManager.doGenerateDefaultProcedures());
			setPageStatus();
			// getControl().pack(true);
		}
	}

	public void updateDesignerProperties() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix
	 * .core.event.IChangeNotifier)
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		setPageStatus();
	}

	public void notifyChanged() {
		this.importManager.notifyChanged();
	}
}

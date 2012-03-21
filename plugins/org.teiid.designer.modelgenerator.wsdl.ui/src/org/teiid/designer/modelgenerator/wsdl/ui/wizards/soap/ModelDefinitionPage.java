/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels.ImportOptionsPanel;

import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.SelectWsdlOperationsPage;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class ModelDefinitionPage extends AbstractWizardPage implements 
	ModelGeneratorWsdlUiConstants, 
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
	
	ImportOptionsPanel importOptionsPanel;
	
	Button generateDefaultProceduresButton;
	Button generateCustomProceduresButton;

	/**
	 * Constructs the page with the provided import manager
	 * 
	 * @param theImportManager
	 *            the import manager object
	 */
	public ModelDefinitionPage(WSDLImportWizardManager theImportManager, ImportWsdlSoapWizard wizard) {
		super(SelectWsdlOperationsPage.class.getSimpleName(), Messages.ModelsDefinition);
		this.importManager = theImportManager;
		this.wizard = wizard;
		setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.2
	 */
	@SuppressWarnings("unused")
	public void createControl(Composite theParent) {
		final int COLUMNS = 1;
		Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
		GridLayout layout = new GridLayout(COLUMNS, false);
		pnlMain.setLayout(layout);
		setControl(pnlMain);
		pnlMain.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				System.out.println("XXXXX"); //$NON-NLS-1$
				
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		MODEL_DEFINITION: {
			// Defines Location and Name values for source and view models
			importOptionsPanel = new ImportOptionsPanel(pnlMain, this.importManager);
		};
		
		GENERATE_PROCEDURE_OPTION : {
			// Includes a radio button and description for 
			//  1) Generate Default Procedures and 
			//  2) Customize Request & Response
			Group group = WidgetFactory.createGroup(pnlMain, Messages.ProcedureGenerationOptions, SWT.NONE | SWT.BORDER, 1);
			group.setLayout(new GridLayout(1, false));
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
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
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			customProceduresHelpText.setLayoutData(gd);
			customProceduresHelpText.setText(Messages.OptionDefineCustomProcures);
			
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
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			defaultProceduresHelpText.setLayoutData(gd);
			defaultProceduresHelpText.setText(Messages.OptionDefineDefaultProcedures);
		}

		restoreState();
	}
	
	private void handleGenerateOptionChanged() {
		// need to set the boolean value on importManager and notify the wizard to update it's pages
		// to remove Page 3 if generate default is checked
		this.importManager.setGenerateDefaultProcedures(this.generateDefaultProceduresButton.getSelection());
		this.importOptionsPanel.validate();
		this.wizard.notifyManagerChanged();
		
		setPageStatus();
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
	 * Restores dialog size and position of the last time wizard ran.
	 * 
	 * @since 4.2
	 */
	private void restoreState() {
		IDialogSettings settings = getDialogSettings();

		if (settings != null) {
			Shell shell = getContainer().getShell();

			if (shell != null) {
				try {
					int x = settings.getInt(DIALOG_X);
					int y = settings.getInt(DIALOG_Y);
					int width = settings.getInt(DIALOG_WIDTH);
					int height = settings.getInt(DIALOG_HEIGHT);
					shell.setBounds(x, y, width, height);
				} catch (NumberFormatException theException) {
					// getInt(String) throws exception if not found.
					// just means no settings exist yet.
				}
			}
		}
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
		IStatus modelStatus = this.importOptionsPanel.getCurrentStatus();
		
		if( modelStatus.getSeverity() == IStatus.OK ) {
			WizardUtil.setPageComplete(this);
			this.setMessage(modelStatus.getMessage());
		} else {
			WizardUtil.setPageComplete(this, modelStatus.getMessage(), modelStatus.getSeverity());
		}


		getContainer().updateButtons();
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible) {
			this.importOptionsPanel.setVisible();
			this.generateDefaultProceduresButton.setSelection(this.importManager.doGenerateDefaultProcedures());
			this.generateCustomProceduresButton.setSelection(! this.importManager.doGenerateDefaultProcedures());
			setPageStatus();
			//getControl().pack(true);
		}
	}

    public void updateDesignerProperties() {
    	
    }
}
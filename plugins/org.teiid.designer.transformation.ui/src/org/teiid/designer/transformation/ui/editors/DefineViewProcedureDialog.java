/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.actions.CreateViewProcedureAction;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.ClosedProjectFilter;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.DesignerProperties;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceDialog;
import org.teiid.designer.ui.viewsupport.SingleProjectFilter;
import org.teiid.designer.ui.wizards.NewModelWizard;
import org.teiid.designer.ui.wizards.NewModelWizardInput;


/**
 * @since 8.0
 */
public class DefineViewProcedureDialog extends TitleAreaDialog implements
		IChangeListener, UiConstants {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(DefineViewProcedureDialog.class);

	private static String getString(String key) {
		return Util.getString(PREFIX + key);
	}

	private EObject viewProcedure;
	private IResource selectedModel;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

	private Button newViewModelButton;
	private Button browseModelsButton;
	private Text selectedViewModelText;

	private Button newViewProcedureButton;
	private Button browseButton;
	private Text selectedViewProcedureText;
	
	private Button applyRestWarPropertiesCB;
	private boolean doApplyRestWarProperties;
	
	private String restMethodValue = METHODS.GET;
	private Combo restMethodsCombo;
	private String restUriValue;
	private Text restUriText;

	DesignerProperties designerProperties;

	/**
	 * @since 5.5.3
	 */
	public DefineViewProcedureDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @since 5.5.3
	 */
	public DefineViewProcedureDialog(Shell parentShell, Properties properties) {
		this(parentShell);
		this.designerProperties = (DesignerProperties)properties;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 * @since 5.5.3
	 */
	@Override
	public boolean close() {

		if (this.labelProvider != null) {
			this.labelProvider.dispose();
		}

		return super.close();
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * @since 5.5.3
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(getString("title")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(OK).setEnabled(false);

		// set the first selection so that initial validation state is set
		// (doing it here since the selection handler uses OK
		// button)

		return buttonBar;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @since 5.5.3
	 */
	@SuppressWarnings("unused")
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite pnlOuter = (Composite) super.createDialogArea(parent);
		Composite panel = new Composite(pnlOuter, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		panel.setLayout(gridLayout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// set title
		setTitle(getString("subTitle")); //$NON-NLS-1$
		setMessage(getString("initialMessage")); //$NON-NLS-1$

		VIEW_MODEL_WIDGETS: {
			Label label = WidgetFactory.createLabel(panel, getString("viewModelName")); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

			// textfield for named type
			this.selectedViewModelText = WidgetFactory.createTextField(panel, GridData.FILL_HORIZONTAL);
			this.selectedViewModelText.setToolTipText(getString("viewModelNameTooltip")); //$NON-NLS-1$
			this.selectedViewModelText.setEditable(false);
			this.selectedViewModelText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedViewModelText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

			// browse type button
			this.newViewModelButton = WidgetFactory.createButton(panel, getString("newViewModelButton")); //$NON-NLS-1$
			this.newViewModelButton.setToolTipText(getString("newViewModelButtonTooltip")); //$NON-NLS-1$
			this.newViewModelButton.setEnabled(true);
			this.newViewModelButton.setLayoutData(new GridData(SWT.CENTER,
					SWT.NONE, false, false));
			this.newViewModelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleNewViewModelPressed();
				}
			});

			this.browseModelsButton = WidgetFactory.createButton(panel, getString("browseModelsButton")); //$NON-NLS-1$
			this.browseModelsButton.setToolTipText(getString("browseModelsButtonTooltip")); //$NON-NLS-1$
			this.browseModelsButton.setEnabled(true);
			this.browseModelsButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseModelsButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceForModelPressed();
				}
			});
		}

		VIEW_PROCEDURE_WIDGETS: {
			Label label = WidgetFactory.createLabel(panel, getString("viewProcedureName")); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

			// textfield for named type
			this.selectedViewProcedureText = WidgetFactory .createTextField(panel, GridData.FILL_HORIZONTAL);
			this.selectedViewProcedureText.setToolTipText(getString("viewProcedureNameTooltip")); //$NON-NLS-1$
			this.selectedViewProcedureText.setEditable(false);
			this.selectedViewProcedureText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedViewProcedureText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));

			// browse type button
			this.newViewProcedureButton = WidgetFactory.createButton(panel, getString("newButton")); //$NON-NLS-1$
			this.newViewProcedureButton.setToolTipText(getString("newButtonTooltip")); //$NON-NLS-1$
			this.newViewProcedureButton.setEnabled(true);
			this.newViewProcedureButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.newViewProcedureButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleNewViewProcedurePressed();
				}
			});

			this.browseButton = WidgetFactory.createButton(panel, getString("browseButton")); //$NON-NLS-1$
			this.browseButton.setToolTipText(getString("browseButtonTooltip")); //$NON-NLS-1$
			this.browseButton.setEnabled(true);
			this.browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,false, false));
			this.browseButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceForObjectPressed();
				}
			});
		}
		
		REST_PROPERTIES : {
			Group restGroup = WidgetFactory.createGroup(panel, getString("restOptions"), SWT.FILL, 1, 2); //$NON-NLS-1$
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan=4;
			restGroup.setLayoutData(gd);
			
			this.applyRestWarPropertiesCB = WidgetFactory.createCheckBox(restGroup, getString("enableRestForThisProcedure")); //$NON-NLS-1$
			this.applyRestWarPropertiesCB.setEnabled(true);
			this.applyRestWarPropertiesCB.setSelection(true);
			this.doApplyRestWarProperties = true;
			gd = new GridData();
			gd.horizontalSpan=2;
			this.applyRestWarPropertiesCB.setLayoutData(gd);
			this.applyRestWarPropertiesCB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean checked = applyRestWarPropertiesCB.getSelection();
					restMethodsCombo.setEnabled(checked);
					restUriText.setEnabled(checked);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			Label label = WidgetFactory.createLabel(restGroup, getString("restMethod")); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			
			restMethodsCombo = new Combo(restGroup, SWT.NONE | SWT.READ_ONLY);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalAlignment = GridData.CENTER;
			gd.horizontalSpan = 1;
			restMethodsCombo.setLayoutData(gd);
			
			List<String> comboItems = new ArrayList<String>();
			for( String str : METHODS_ARRAY ) {
				comboItems.add(str);
			}
			WidgetUtil.setComboItems(restMethodsCombo, comboItems, null, true);
			restMethodsCombo.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( SelectionEvent ev ) {
	            	selectComboItem(restMethodsCombo.getSelectionIndex());
	            }
	        });
			
			label = WidgetFactory.createLabel(restGroup, getString("uri"));  //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

			// textfield for named type
			this.restUriText = WidgetFactory.createTextField(restGroup, GridData.FILL_HORIZONTAL);
			this.restUriText.setToolTipText(getString("viewModelNameTooltip")); //$NON-NLS-1$
			this.restUriText.setEditable(true);
			this.restUriText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					restUriValue = restUriText.getText();
				}
			});
		}


		return panel;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if (this.designerProperties != null) {
			// Check for existing project??
			this.selectedModel = this.designerProperties.getViewModel();
			if (this.selectedModel != null) {
				this.selectedViewModelText.setText(this.selectedModel.getName());
			}
			EObject lastModelObject = this.designerProperties.getLastViewModelObject();
			if (lastModelObject != null && lastModelObject instanceof Procedure) {
				 this.viewProcedure = lastModelObject;
				this.selectedViewProcedureText.setText(this.designerProperties.getLastViewModelObjectName());
			}

			updateState();
		}
		return control;
	}

	public EObject getViewProcedure() {
		return this.viewProcedure;
	}
	
	public boolean doApplyRestWarProperties() {
		return this.doApplyRestWarProperties;
	}
	
	public String getRestMethod() {
		return this.restMethodValue;
	}
	
	public String getRestUri() {
		return this.restUriValue;
	}

	@Override
	protected void okPressed() {
		this.doApplyRestWarProperties = this.applyRestWarPropertiesCB.getSelection();

		super.okPressed();
	}
	
    private void selectComboItem(int selectionIndex) {
    	if( selectionIndex >=0 ) {
    		restMethodsCombo.select(selectionIndex);
    		this.restMethodValue = restMethodsCombo.getItem(selectionIndex);
    	}
    }
	
	/**
	 * @see org.teiid.core.designer.event.IChangeListener#stateChanged(org.teiid.core.designer.event.IChangeNotifier)
	 * @since 5.5.3
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		updateState();
	}

	private void updateState() {
		IStatus status = Status.OK_STATUS;

		if (status.getSeverity() == IStatus.ERROR) {
			getButton(OK).setEnabled(false);
			setErrorMessage(status.getMessage());
		} else {
			getButton(OK).setEnabled(true);
			setErrorMessage(null);
			setMessage(getString("okMsg")); //$NON-NLS-1$
		}
	}

	private void handleNewViewProcedurePressed() {
        try {
			// open editor
			IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
			IDE.openEditor(page, (IFile)this.selectedModel);
		} catch (PartInitException ex) {
			// Do nothing?
		}
		
		CreateViewProcedureAction action = new CreateViewProcedureAction(this.designerProperties);
		action.run();
		this.viewProcedure = action.getNewViewProcedure();
		if( this.viewProcedure != null ) {
			String name = ModelerCore.getModelEditor().getName(this.viewProcedure);
			this.selectedViewProcedureText.setText(name);
			this.designerProperties.setLastViewModelObjectName(name);
		}
	}

	private void handleBrowseWorkspaceForObjectPressed() {
		ModelWorkspaceDialog sdDialog = createViewProcedureSelector();

		// add filters
		sdDialog.addFilter(new ClosedProjectFilter());
		sdDialog.addFilter(new SingleProjectFilter(
				this.designerProperties));

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			Object[] selections = sdDialog.getResult();
			// should be single selection
			viewProcedure = (EObject) selections[0];
			// Set the View Model too
			String name = ModelerCore.getModelEditor().getName(this.viewProcedure);
			this.selectedViewProcedureText.setText(name);
			this.designerProperties.setLastViewModelObjectName(name);
			this.designerProperties.setPreviewTargetObjectName(name);

			try {
				ModelResource mr = ModelUtilities.getModelResource(this.viewProcedure);
				this.selectedModel = mr.getUnderlyingResource();
				this.designerProperties.setViewModelName(this.selectedModel.getName());
				this.selectedViewModelText.setText(this.selectedModel.getName());
				this.designerProperties.setPreviewTargetModelName(this.selectedModel.getName());
			} catch (ModelWorkspaceException e) {
				UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			}

			updateState();
		}
	}

	private void handleNewViewModelPressed() {
		NewModelWizardInput newModelInput = new NewModelWizardInput(
				"Relational", ModelType.VIRTUAL_LITERAL, null); //$NON-NLS-1$

		boolean projectDefined = false;
		
		final IWorkbenchWindow iww = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		boolean successful = false;
		try {

			NewModelWizard wizard = new NewModelWizard(newModelInput,
					this.designerProperties);

			// Create a Project Selection here
            IContainer container = this.designerProperties.getViewsFolder();
            if( container == null ) {
            	projectDefined = true;
            	container = this.designerProperties.getProject();
            }
            if( container != null ) {
            	wizard.init(iww.getWorkbench(), new StructuredSelection(container));
            }

			WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
			int result = dialog.open();
			if (result == Window.OK) {
				successful = true;
			}
		} catch (Exception e) {
			UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
			MessageDialog.openError(iww.getShell(),
					getString("modelCreationError.title"), //$NON-NLS-1$
					getString("modelCreationError.message")); //$NON-NLS-1$
		} finally {
			// Find the view model from the properties
			if (successful) {
        		// Check for view model
        		
        		if( this.selectedModel == null || this.designerProperties.isViewModelDifferent((IFile)this.selectedModel) ) {
	        		this.selectedModel = this.designerProperties.getViewModel();
	        		if( this.selectedModel != null ) {
	        			this.selectedViewModelText.setText(this.selectedModel.getName());
	        		}
        		}
        		
        		if( !projectDefined ) {
        			this.designerProperties.setProjectName(this.selectedModel.getProject().getName());
        		}
			}
		}
	}

	private void handleBrowseWorkspaceForModelPressed() {
		ModelWorkspaceDialog sdDialog = createViewModelSelector();

		// add filters
		sdDialog.addFilter(new ClosedProjectFilter());
		sdDialog.addFilter(new SingleProjectFilter(
				this.designerProperties));

		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			Object[] selections = sdDialog.getResult();
			// should be single selection
			this.selectedModel = (IResource) selections[0];
			this.designerProperties.setViewModelName(this.selectedModel.getName());
			this.selectedViewModelText.setText(this.selectedModel.getName());

			updateState();
		}
	}

	public ModelWorkspaceDialog createViewProcedureSelector() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = getString("selectionDialog.title"); //$NON-NLS-1$
		String message = getString("selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ModelerCore.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				boolean ok = false;
				if (selection != null && selection.length == 1
						&& selection[0] instanceof EObject) {
					EObject eObj = (EObject) selection[0];
					if (eObj instanceof Procedure) {
						ModelResource mr = ModelUtilities.getModelResource(eObj);
						if (mr != null) {
							ok = ModelIdentifier.isVirtualModelType(mr);
						}
					}
				}
				if (!ok) {
					String msg = getString("selectionDialog.invalidSelection"); //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});

		return result;
	}

	public ModelWorkspaceDialog createViewModelSelector() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = getString("viewModel.selectionDialog.title"); //$NON-NLS-1$
		String message = getString("viewModel.selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ModelerCore.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				boolean ok = false;
				if (selection != null && selection.length == 1 && selection[0] instanceof IResource) {
					IResource res = (IResource) selection[0];
					ModelResource mr = ModelUtilities.getModelResource(res);
					if (mr != null) {
						ok = ModelIdentifier.isVirtualModelType(mr);
					}

				}
				if (!ok) {
					String msg = getString("viewModel.selectionDialog.invalidSelection"); //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR,
							msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});

		return result;
	}
}
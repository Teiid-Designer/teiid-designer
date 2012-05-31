/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors;

import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.SingleProjectFilter;
import com.metamatrix.modeler.internal.ui.wizards.NewModelWizard;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.CreateViewTableAction;
import com.metamatrix.modeler.ui.viewsupport.DesignerProperties;
import com.metamatrix.modeler.ui.wizards.NewModelWizardInput;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.Label;

public class DefineViewTableDialog extends TitleAreaDialog implements
		IChangeListener, UiConstants {

	private static final String PREFIX = I18nUtil.getPropertyPrefix(DefineViewTableDialog.class);
	
	private static String getString(String key) {
		return Util.getString(PREFIX + key);
	}

	private EObject viewTable;
	private IResource selectedModel;

	private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

	private Button newViewModelButton;
	private Button browseModelsButton;
	private Text selectedViewModelText;
	
	private Button newViewTableButton;
	private Button browseButton;
	private Text selectedViewTableText;

	DesignerProperties designerProperties;

	/**
	 * @since 5.5.3
	 */
	public DefineViewTableDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @since 5.5.3
	 */
	public DefineViewTableDialog(Shell parentShell, Properties properties) {
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

		VIEW_MODEL_WIDGETS : {
			Label label = WidgetFactory.createLabel(panel,getString("viewModelName")); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	
			// textfield for named type
			this.selectedViewModelText = WidgetFactory.createTextField(panel,
					GridData.FILL_HORIZONTAL/* GridData.HORIZONTAL_ALIGN_FILL */);
			this.selectedViewModelText.setToolTipText(getString("viewModelNameTooltip")); //$NON-NLS-1$
			this.selectedViewModelText.setEditable(false);
			this.selectedViewModelText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedViewModelText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
			// browse type button
			this.newViewModelButton = WidgetFactory.createButton(panel,getString("newViewModelButton")); //$NON-NLS-1$
			this.newViewModelButton.setToolTipText(getString("newViewModelButtonTooltip")); //$NON-NLS-1$
			this.newViewModelButton.setEnabled(true);
			this.newViewModelButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,false, false));
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
		
		VIEW_TABLE_WIDGETS : {
			Label label = WidgetFactory.createLabel(panel,getString("viewTableName")); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	
			// textfield for named type
			this.selectedViewTableText = WidgetFactory.createTextField(panel,
					GridData.FILL_HORIZONTAL/* GridData.HORIZONTAL_ALIGN_FILL */);
			this.selectedViewTableText.setToolTipText(getString("viewTableNameTooltip")); //$NON-NLS-1$
			this.selectedViewTableText.setEditable(false);
			this.selectedViewTableText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			this.selectedViewTableText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	
			// browse type button
			this.newViewTableButton = WidgetFactory.createButton(panel,getString("newButton")); //$NON-NLS-1$
			this.newViewTableButton.setToolTipText(getString("newButtonTooltip")); //$NON-NLS-1$
			this.newViewTableButton.setEnabled(true);
			this.newViewTableButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE,false, false));
			this.newViewTableButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleNewViewTablePressed();
				}
			});
	
			this.browseButton = WidgetFactory.createButton(panel, getString("browseButton")); //$NON-NLS-1$
			this.browseButton.setToolTipText(getString("browseButtonTooltip")); //$NON-NLS-1$
			this.browseButton.setEnabled(true);
			this.browseButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
			this.browseButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent theEvent) {
					handleBrowseWorkspaceForObjectPressed();
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
    		if( this.selectedModel != null ) {
				this.selectedViewModelText.setText(this.selectedModel.getName());
    		}
			EObject lastModelObject = this.designerProperties.getLastViewModelObject();
			if (lastModelObject != null && lastModelObject instanceof Table) {
				this.viewTable = lastModelObject;
    			this.selectedViewTableText.setText(this.designerProperties.getLastViewModelObjectName());
    		}
    		
			updateState();
		}
		return control;
	}

	{

	}

	public EObject getViewTable() {
		return this.viewTable;
	}

	/**
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 * @since 5.5.3
	 */
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
	
	private void handleNewViewTablePressed() {
       try {
			// open editor
			IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
			IDE.openEditor(page, (IFile)this.selectedModel);
		} catch (PartInitException ex) {
			// Do nothing?
		}
	       
		CreateViewTableAction action = new CreateViewTableAction(this.designerProperties);
		action.run();
		this.viewTable = action.getNewViewTable();
		if( this.viewTable != null ) {
			String name = ModelerCore.getModelEditor().getName(this.viewTable);
			this.selectedViewTableText.setText(name);
			this.designerProperties.setLastViewModelObjectName(name);
		}
	}

	private void handleBrowseWorkspaceForObjectPressed() {
		ModelWorkspaceDialog sdDialog = createViewTableSelector();

		// add filters
		((ModelWorkspaceDialog) sdDialog).addFilter(new ClosedProjectFilter());
		((ModelWorkspaceDialog) sdDialog).addFilter(new SingleProjectFilter(this.designerProperties));

		IFile viewModel = designerProperties.getViewModel();

		if (viewModel != null) {
			((ModelWorkspaceDialog) sdDialog).setInitialSelection(viewModel);
		}
		
		sdDialog.open();

		if (sdDialog.getReturnCode() == Window.OK) {
			Object[] selections = sdDialog.getResult();
			// should be single selection
			viewTable = (EObject) selections[0];
			// Set the View Model too
			String name = ModelerCore.getModelEditor().getName(this.viewTable);
			this.selectedViewTableText.setText(name);
			this.designerProperties.setLastViewModelObjectName(name);
			this.designerProperties.setPreviewTargetObjectName(name);
			
			try {
				ModelResource mr = ModelUtilities.getModelResource(this.viewTable);
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
		NewModelWizardInput newModelInput = 
				new NewModelWizardInput("Relational", ModelType.VIRTUAL_LITERAL, null); //$NON-NLS-1$

		boolean projectDefined = false;
		
        final IWorkbenchWindow iww = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        boolean successful = false;
        try {

            NewModelWizard wizard = new NewModelWizard(newModelInput, this.designerProperties);
            
            // Create a Project Selection here
            IContainer container = this.designerProperties.getViewsFolder();
            if( container == null ) {
            	container = this.designerProperties.getProject();
            }
            if( container != null ) {
            	projectDefined = true;
            	wizard.init(iww.getWorkbench(), new StructuredSelection(container));
            }
            
            WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
            int result = dialog.open();
            if (result == Dialog.OK) {
                successful = true;
            }
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            MessageDialog.openError(iww.getShell(), 
            		getString("modelCreationError.title"),  //$NON-NLS-1$
            		getString("modelCreationError.message")); //$NON-NLS-1$
        } finally {
        	// Find the view model from the properties
        	if( successful ) {
        		
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
		((ModelWorkspaceDialog) sdDialog).addFilter(new ClosedProjectFilter());
		((ModelWorkspaceDialog) sdDialog).addFilter(new SingleProjectFilter(this.designerProperties));
		

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

	public ModelWorkspaceDialog createViewTableSelector() {

		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(),
				null, new ModelExplorerLabelProvider(),
				new ModelExplorerContentProvider());

		String title = getString("selectionDialog.title"); //$NON-NLS-1$
		String message = getString("selectionDialog.message"); //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				boolean ok = false;
				if( selection != null && 
					selection.length == 1 &&
					selection[0] instanceof EObject ) {
					EObject eObj = (EObject)selection[0];
					if( eObj instanceof Table )  {
						ModelResource mr = ModelUtilities.getModelResource(eObj);
						if( mr != null ) {
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

		result.setInput(ResourcesPlugin.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				boolean ok = false;
				if( selection != null && 
					selection.length == 1 &&
					selection[0] instanceof IResource ) {
					IResource res = (IResource)selection[0];
					ModelResource mr = ModelUtilities.getModelResource(res);
					if( mr != null ) {
						ok = ModelIdentifier.isVirtualModelType(mr);
					}

				}
				if (!ok) {
					String msg = getString("viewModel.selectionDialog.invalidSelection"); //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});

		return result;
	}
}

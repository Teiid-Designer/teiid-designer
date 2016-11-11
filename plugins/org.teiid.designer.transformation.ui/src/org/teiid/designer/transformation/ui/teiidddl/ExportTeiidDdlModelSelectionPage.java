package org.teiid.designer.transformation.ui.teiidddl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;

/**
 * This is the first page in the Teiid DDL Exporter wizard
 * 
 * It's purpose is to show current model selection in the workspace (if exists) and allow user
 * to change the selected model for export
 * 
 *
 */
public class ExportTeiidDdlModelSelectionPage extends AbstractWizardPage implements UiConstants {	
	private static final String TITLE = Messages.ExportTeiidDdlModelSelectionPage_title;
	private static final String OPTIONS_GROUP = Messages.ExportTeiidDdlModelSelectionPage_ddlExportOptions;
	private static final String USE_NAMES_IN_SOURCE_CHECKBOX = Messages.ExportTeiidDdlModelSelectionPage_nameInSourceOption;
	private static final String USE_NATIVE_TYPE_CHECKBOX = Messages.ExportTeiidDdlModelSelectionPage_nativeTypeOption;

	private final TeiidDdlExporter exporter;
	
	private Text selectedFileText;

	private Button useNamesInSourceCheckBox, useNativeTypeCheckBox;
	
	IStatus currentStatus = Status.OK_STATUS;

	public ExportTeiidDdlModelSelectionPage(TeiidDdlExporter exporter) {
		super(ExportTeiidDdlModelSelectionPage.class.getSimpleName(), TITLE);
		this.exporter = exporter;
	}

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(2, false));
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);

		setMessage(Messages.ExportTeiidDdlModelSelectionPage_initialMessage);
		
		new Label(mainPanel, SWT.NONE);
		new Label(mainPanel, SWT.NONE);
		
		createSelectModelGroup(mainPanel);
		
		createOptionsGroup(mainPanel);

		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			validatePage();
		}
	}
	

	private void createSelectModelGroup(Composite parent) {
		Group viewGroup = WidgetFactory.createGroup(parent, Messages.ExportTeiidDdlModelSelectionPage_modelGroupTitle, SWT.NONE, 2, 3);
		GridData gd_vg = new GridData(GridData.FILL_HORIZONTAL);
		gd_vg.horizontalSpan = 2;
		viewGroup.setLayoutData(gd_vg);

		Label fileLabel = new Label(viewGroup, SWT.NULL);
		fileLabel.setText(Messages.ExportTeiidDdlModelSelectionPage_fileLabel);

		selectedFileText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		selectedFileText.setLayoutData(gridData);
		selectedFileText.setEditable(false);
		if( this.exporter.getModelResource() != null ) {
			selectedFileText.setText(this.exporter.getModelResource().getItemName());
		}

		Button browseButton = new Button(viewGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText("..."); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleModelBrowse();
			}
		});
	}
	
	private void createOptionsGroup(Composite parent) {
		exporter.setNameInSourceUsed(false);
		exporter.setNativeTypeUsed(false);

		Group group = WidgetFactory.createGroup(parent, OPTIONS_GROUP,
				GridData.FILL_HORIZONTAL, 1, 2);
		{
			this.useNamesInSourceCheckBox = WidgetFactory.createCheckBox(group, USE_NAMES_IN_SOURCE_CHECKBOX, 0, 2, exporter.isNameInSourceUsed());
			this.useNamesInSourceCheckBox
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							useNamesInSourceCheckBoxSelected();
						}
					});
			this.useNativeTypeCheckBox = WidgetFactory.createCheckBox(group, USE_NATIVE_TYPE_CHECKBOX, 0, 2, exporter.isNativeTypeUsed());
			this.useNativeTypeCheckBox
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent event) {
							useNativeTypeCheckBoxSelected();
						}
					});
		}
	}
	
	void handleModelBrowse() {
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						Messages.ExportTeiidDdlModelSelectionPage_selectModelDialogTitle,
						Messages.ExportTeiidDdlModelSelectionPage_selectModelDialogMessage,
						false, null, modelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1 && selectedFileText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
				ModelResource mr = ModelUtilities.getModelResource(modelFile);
				exporter.setModelResource(mr);
			}
		}
		
        if( this.exporter.getModelResource() != null ) {
        	this.selectedFileText.setText(this.exporter.getModelResource().getItemName());
        } else {
        	this.selectedFileText.setText(StringConstants.EMPTY_STRING);
        }

		validatePage();
	}
	
	private boolean validatePage() {
		currentStatus = exporter.validate();
		if( currentStatus.isOK()) {
			setErrorMessage(null);
			setMessage(Messages.ExportTeiidDdlModelSelectionPage_configureOptionsAndClickNextMessage);
			setPageComplete(true);
			return true;
		} else if( currentStatus.getSeverity() == IStatus.WARNING ){
			setMessage(currentStatus.getMessage());
			setPageComplete(true);
			return true;
		} else if( currentStatus.getSeverity() == IStatus.ERROR ) {
			setErrorMessage(currentStatus.getMessage());
			setPageComplete(false);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		// TODO Auto-generated method stub
		return currentStatus.getSeverity() < IStatus.ERROR;
	}

	final ViewerFilter modelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				if (projectOpen) {
					// Show open projects
					if (element instanceof IProject) {
						try {
		                	doSelect = ((IProject)element).hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null &&
								(ModelIdentifier.isRelationalViewModel(theModel) || ModelIdentifier.isRelationalSourceModel(theModel))) {
							doSelect = true;
						}
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			}

			return doSelect;
		}
	};
	

	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	private void useNamesInSourceCheckBoxSelected() {
		this.exporter.setNameInSourceUsed(this.useNamesInSourceCheckBox.getSelection());
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @since 4.0
	 */
	private void useNativeTypeCheckBoxSelected() {
		this.exporter.setNativeTypeUsed(this.useNativeTypeCheckBox.getSelection());
	}
}

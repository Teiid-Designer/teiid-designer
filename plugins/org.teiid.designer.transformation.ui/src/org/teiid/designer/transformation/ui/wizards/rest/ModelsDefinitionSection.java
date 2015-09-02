/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.rest;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportInfo;
import org.teiid.designer.transformation.ui.wizards.file.TeiidMetadataImportSourcePage;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

public final class ModelsDefinitionSection implements UiConstants{
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportSourcePage.class);
	

	private static final String GET_TEXT_FILES = "getTextFiles()"; //$NON-NLS-1$
	private static final String INVOKE_HTTP = "invokeHttp()"; //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}
	
	TeiidRestImporterModelDefinitionPage page;
	private TeiidMetadataImportInfo info;
	
	private Text sourceModelContainerText;
	private Text sourceModelFileText;
	private Text sourceHelpText;
	private IPath sourceModelFilePath;
	private Text viewModelContainerText;
	private Text viewModelFileText;
	private Text viewHelpText;
	private IPath viewModelFilePath;
	private Text viewProcedureNameText;
	
	RelationalStringNameValidator validator = new RelationalStringNameValidator(true);
	IConnectionInfoHelper connectionInfoHelper = new ConnectionInfoHelper();
	
	boolean synchronizing = false;
	boolean controlComplete = false;

	public ModelsDefinitionSection(TeiidRestImporterModelDefinitionPage page, TeiidMetadataImportInfo info, Composite parent) {
		super();
		
		this.page = page;
		this.info = info;
		buildUi(parent);
	}
	
	private void buildUi(Composite parent) {
		// SOURCE
		Group sourceGroup = WidgetFactory.createGroup(parent,
				getString("sourceModelDefinitionGroup"), SWT.NONE, 1, 3); //$NON-NLS-1$
		sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		((GridData)sourceGroup.getLayoutData()).widthHint = 400;

		Label locationLabel = new Label(sourceGroup, SWT.NULL);
		locationLabel.setText(getString("location")); //$NON-NLS-1$

		sourceModelContainerText = new Text(sourceGroup, SWT.BORDER
				| SWT.SINGLE);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceModelContainerText.setLayoutData(gridData);
		sourceModelContainerText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		sourceModelContainerText.setEditable(false);

		Button browseButton = new Button(sourceGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelLocationBrowse();
			}
		});

		Label fileLabel = new Label(sourceGroup, SWT.NULL);
		fileLabel.setText(getString("name")); //$NON-NLS-1$

		sourceModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceModelFileText.setLayoutData(gridData);
		sourceModelFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleSourceModelTextChanged();
			}
		});

		browseButton = new Button(sourceGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelBrowse();
			}
		});

		Group helpGroup = WidgetFactory.createGroup(parent,
				getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH, 1); //$NON-NLS-1$
		helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			sourceHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
			sourceHelpText.setBackground(WidgetUtil
					.getReadOnlyBackgroundColor());
			sourceHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			gd.horizontalSpan = 3;
			sourceHelpText.setLayoutData(gd);
		}

		// VIEW
		Group viewGroup = WidgetFactory.createGroup(parent,
				getString("viewModelDefinitionGroup"), SWT.NONE, 1, 3); //$NON-NLS-1$
		viewGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label viewLocationLabel = new Label(viewGroup, SWT.NULL);
		viewLocationLabel.setText(getString("location")); //$NON-NLS-1$

		viewModelContainerText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);

		GridData viewGridData = new GridData(GridData.FILL_HORIZONTAL);
		viewModelContainerText.setLayoutData(viewGridData);
		viewModelContainerText.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		viewModelContainerText.setEditable(false);

		Button viewBrowseButton = new Button(viewGroup, SWT.PUSH);
		gridData = new GridData();
		viewBrowseButton.setLayoutData(gridData);
		viewBrowseButton.setText(getString("browse")); //$NON-NLS-1$
		viewBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleViewModelLocationBrowse();
			}
		});

		Label viewFileLabel = new Label(viewGroup, SWT.NULL);
		viewFileLabel.setText(getString("name")); //$NON-NLS-1$

		viewModelFileText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
		viewGridData = new GridData(GridData.FILL_HORIZONTAL);
		viewModelFileText.setLayoutData(viewGridData);
		viewModelFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleViewModelTextChanged();
			}
		});

		browseButton = new Button(viewGroup, SWT.PUSH);
		viewGridData = new GridData();
		browseButton.setLayoutData(viewGridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleViewModelBrowse();
			}
		});
		
		// View Table Definition
		Label viewProcedureLabel = new Label(viewGroup, SWT.NULL);
		viewProcedureLabel.setText(getString("newViewProcedureName")); //$NON-NLS-1$

		viewProcedureNameText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		viewProcedureNameText.setLayoutData(gridData);
		viewProcedureNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// Check view file name for existing if "location" is already
				// set
				handleViewProcedureTextChanged();
			}
		});

		new Label(viewGroup, SWT.NONE);

		Group viewHelpGroup = WidgetFactory.createGroup(parent,
				getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH, 1); //$NON-NLS-1$
		viewHelpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			viewHelpText = new Text(viewHelpGroup, SWT.WRAP | SWT.READ_ONLY);
			viewHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
			viewHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			gd.horizontalSpan = 3;
			viewHelpText.setLayoutData(gd);
		}
		
		controlComplete = true;
	}
	
	protected TeiidXmlFileInfo getXmlFileInfo() {
		return this.info.getSourceXmlFileInfo();
	}
	
	public void setThisPageComplete(String message, int severity) {
		page.setThisPageComplete(message, severity);
	}
	
	protected boolean validatePage() {
		

		setSourceHelpMessage();
		setViewHelpMessage();
		
		// Check for at least ONE open non-hidden Model Project
		boolean validProj = false;
		for (IProject proj : ModelerCore.getWorkspace().getRoot().getProjects()) {
			try {
				boolean result = proj.isOpen()
						&& !proj.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
						&& proj.hasNature(ModelerCore.NATURE_ID);
				if (result) {
					validProj = true;
					break;
				}
			} catch (CoreException e) {
				UiConstants.Util.log(e);
			}
		}

		if (!validProj) {
			setThisPageComplete(getString("noOpenProjectsMessage"), IMessageProvider.ERROR);//$NON-NLS-1$
			return false;
		}

		// =============== SOURCE MODEL INFO CHECKS ==================
		String container = sourceModelContainerText.getText();
		if (CoreStringUtil.isEmpty(container)) {
			setThisPageComplete(
					getString("sourceFileLocationMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}
		IProject project = getTargetProject();
		if (project == null) {
			setThisPageComplete(
					getString("sourceFileLocationMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		String fileText = sourceModelFileText.getText().trim();

		if (fileText.length() == 0) {
			setThisPageComplete(
					getString("sourceFileNameMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}
		IStatus status = ModelNameUtil.validate(fileText,
				ModelerCore.MODEL_FILE_EXTENSION, null,
				ModelNameUtil.IGNORE_CASE
						| ModelNameUtil.NO_DUPLICATE_MODEL_NAMES);
		if (status.getSeverity() == IStatus.ERROR) {
			setThisPageComplete(
					ModelNameUtil.MESSAGES.INVALID_SOURCE_MODEL_NAME
							+ status.getMessage(), IMessageProvider.ERROR);
			return false;
		}

		// We've got a valid source model
		// If Existing, need to check for the wrong connection profile
		if (info.sourceModelExists() && !sourceModelHasSameConnectionProfile()) {
			setThisPageComplete(
					Util.getString(I18N_PREFIX
							+ "connectionProfileForModelIsDifferent", fileText), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		// =============== VIEW MODEL INFO CHECKS ==================
		String viewContainer = viewModelContainerText.getText();
		if (CoreStringUtil.isEmpty(viewContainer)) {
			setThisPageComplete(
					getString("viewFileLocationMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}
		project = getViewTargetProject();
		if (project == null) {
			setThisPageComplete(
					getString("viewFileLocationMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		String viewFileText = viewModelFileText.getText().trim();

		if (viewFileText.length() == 0) {
			setThisPageComplete(getString("viewFileNameMustBeSpecified"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}
		status = ModelNameUtil.validate(viewFileText,
				ModelerCore.MODEL_FILE_EXTENSION, null,
				ModelNameUtil.IGNORE_CASE
						| ModelNameUtil.NO_DUPLICATE_MODEL_NAMES);
		if (status.getSeverity() == IStatus.ERROR) {
			setThisPageComplete(ModelNameUtil.MESSAGES.INVALID_VIEW_MODEL_NAME
					+ status.getMessage(), IMessageProvider.ERROR);
			return false;
		}

		String viewFileName = getViewFileName();
		String sourceFilename = getSourceFileName();
		if (viewFileName.equalsIgnoreCase(sourceFilename)) {
			setThisPageComplete(
					getString("sourceAndViewFilesCannotHaveSameName"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		// Check if View Name is valid
		String invalidMessage = getXmlFileInfo() == null ? null : validator.checkValidName(getXmlFileInfo().getViewTableName());
		if (invalidMessage != null) {
			setThisPageComplete(getString("viewProcedureNameInvalid"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}
		// Check if view table already exists
		if (viewAlreadyExists()) {
			setThisPageComplete(getString("viewProcedureAlreadyExists"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		if (getXmlFileInfo() == null || getXmlFileInfo().getViewProcedureName() == null || getXmlFileInfo().getViewProcedureName().length() == 0) {
			setThisPageComplete(
					getString("viewProcedureNameNullOrEmpty"), IMessageProvider.ERROR); //$NON-NLS-1$
			return false;
		}

		// We've got a valid view model

		setThisPageComplete(EMPTY_STRING, IMessageProvider.NONE);
		
		return true;
	}
	
	private boolean viewAlreadyExists() {
		if (!info.viewModelExists()) {
			return false;
		}

		IPath modelPath = new Path(viewModelFilePath.toOSString())
				.append(this.viewModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
		}

		IResource viewModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
		ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile) viewModel, false);
		if (smr != null) {
			try {
				if (getXmlFileInfo().getViewProcedureName() == null) {
					return false;
				}
				String existingName = getXmlFileInfo().getViewProcedureName();

				for (Object obj : smr.getAllRootEObjects()) {

					EObject eObj = (EObject) obj;
					if (eObj instanceof Procedure && existingName.equalsIgnoreCase(ModelObjectUtilities.getName(eObj))) {
						return true;
					}
				}
			} catch (ModelWorkspaceException err) {
				Util.log(err);
			}

		}

		return false;
	}
	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleSourceModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(
				ModelerCore.getWorkspace().getRoot(),
				new ModelingResourceFilter(),
				new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			this.info.setSourceModelLocation(folder.getFullPath()
					.makeRelative());
			this.sourceModelFilePath = this.info.getSourceModelLocation();
			this.sourceModelContainerText.setText(this.info
					.getSourceModelLocation().makeRelative().toString());
		} else {
			this.info.setSourceModelLocation(new Path(
					StringConstants.EMPTY_STRING));
			this.sourceModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		if (this.sourceModelFileText.getText() != null
				&& this.sourceModelFileText.getText().length() > -1) {
			this.info.setSourceModelExists(sourceModelExists());
		}

		validatePage();
	}

	void handleSourceModelBrowse() {
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						getString("selectSourceModelTitle"), //$NON-NLS-1$
						getString("selectSourceModelMessage"), //$NON-NLS-1$
						false, null, sourceModelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1
				&& sourceModelFileText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
				IPath folderPath = modelFile.getFullPath()
						.removeLastSegments(1);
				String modelName = modelFile.getFullPath().lastSegment();
				info.setSourceModelExists(true);
				info.setSourceModelLocation(folderPath);
				info.setSourceModelName(modelName);
			}
		}

		if (this.info.getSourceModelName() != null) {
			this.sourceModelFilePath = this.info.getSourceModelLocation();
			this.sourceModelContainerText.setText(this.info
					.getSourceModelLocation().makeRelative().toString());
			this.sourceModelFileText.setText(this.info.getSourceModelName());
		} else {
			this.sourceModelFileText.setText(StringConstants.EMPTY_STRING);
			this.sourceModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		this.info.setSourceModelExists(sourceModelExists());

		validatePage();
	}

	void handleSourceModelTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > -1) {
			newName = this.sourceModelFileText.getText();
			if( newName.trim().isEmpty() ) {
				this.info.setSourceModelName(null);
				this.info.setSourceModelExists(false);
			} else {
				this.info.setSourceModelName(newName);
				this.info.setSourceModelExists(sourceModelExists());
			}

		}

		validatePage();
	}
	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleViewModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(
				ModelerCore.getWorkspace().getRoot(),
				new ModelingResourceFilter(),
				new ModelProjectSelectionStatusValidator());

		if (folder != null && viewModelContainerText != null) {
			this.info.setViewModelLocation(folder.getFullPath().makeRelative());
			this.viewModelFilePath = this.info.getViewModelLocation();
			this.viewModelContainerText.setText(this.info
					.getViewModelLocation().makeRelative().toString());
		} else {
			this.info.setViewModelLocation(new Path(
					StringConstants.EMPTY_STRING));
			this.viewModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		if (this.viewModelFileText.getText() != null
				&& this.viewModelFileText.getText().length() > -1) {
			this.info.setViewModelExists(sourceModelExists());
		}

		validatePage();
	}

	void handleViewModelBrowse() {
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						getString("viewSourceModelTitle"), //$NON-NLS-1$
						getString("viewSourceModelMessage"), //$NON-NLS-1$
						false, null, viewModelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1
				&& viewModelFileText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
				IPath folderPath = modelFile.getFullPath()
						.removeLastSegments(1);
				String modelName = modelFile.getFullPath().lastSegment();
				info.setViewModelExists(true);
				info.setViewModelLocation(folderPath);
				info.setViewModelName(modelName);
			}
		}

		if (this.info.getViewModelName() != null) {
			this.viewModelFilePath = this.info.getViewModelLocation();
			this.viewModelContainerText.setText(this.info
					.getViewModelLocation().makeRelative().toString());
			this.viewModelFileText.setText(this.info.getViewModelName());
		} else {
			this.viewModelFileText.setText(StringConstants.EMPTY_STRING);
			this.viewModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		this.info.setViewModelExists(viewModelExists());

		validatePage();
	}

	void handleViewModelTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.viewModelFileText.getText() != null && this.viewModelFileText.getText().length() > -1) {
			newName = this.viewModelFileText.getText();
			if( newName == null || newName.trim().isEmpty() ) {
				this.info.setViewModelName(null);
				this.info.setViewModelExists(false);
			} else {
				this.info.setViewModelName(newName);
				this.info.setViewModelExists(viewModelExists());
			}

		}

		validatePage();
	}

	void handleViewProcedureTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.viewProcedureNameText.getText() != null
				&& this.viewProcedureNameText.getText().length() > -1) {
			newName = this.viewProcedureNameText.getText();
			this.info.getSourceXmlFileInfo().setViewProcedureName(newName);
		} else {
			this.info.getSourceXmlFileInfo().setViewProcedureName(StringConstants.EMPTY_STRING);
		}

		int caret = this.viewProcedureNameText.getCaretPosition();

		this.viewProcedureNameText.setSelection(caret);
		validatePage();
	}
	
	protected void synchronizeUi() {
		synchronizing = true;
		
		if (this.info.getSourceModelLocation() != null) {
			this.sourceModelFilePath = this.info.getSourceModelLocation();
			this.sourceModelContainerText.setText(this.info
					.getSourceModelLocation().makeRelative().toString());
		} else {
			this.sourceModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		if (this.info.getSourceModelName() != null) {
			this.sourceModelFileText.setText(this.info.getSourceModelName());
		} else {
			this.sourceModelFileText.setText(StringConstants.EMPTY_STRING);
		}

		String sourceFileName = EMPTY_STRING;
		if( this.info.getSourceModelName() != null ) {
			sourceFileName = this.info.getSourceModelName();
		}
		//xmlFileInfo = null;
		for (TeiidXmlFileInfo fileInfo : this.info.getXmlFileInfos()) {
			if (fileInfo.doProcess()) {
				this.info.setSourceXmlFileInfo(fileInfo);
				if (this.info.getSourceModelName() != null) {
					sourceFileName = this.info.getSourceModelName();
				} else {
					sourceFileName = "SourceProcedures"; //$NON-NLS-1$
					this.info.setSourceModelName(sourceFileName);
				}
				break;
			}
		}

		this.sourceModelFileText.setText(sourceFileName);

		if (this.info.getViewModelLocation() != null) {
			this.viewModelFilePath = this.info.getViewModelLocation();
			this.viewModelContainerText.setText(this.info
					.getViewModelLocation().makeRelative().toString());
		} else {
			this.viewModelContainerText.setText(StringConstants.EMPTY_STRING);
		}

		if (this.info.getViewModelName() != null) {
			this.viewModelFileText.setText(this.info.getViewModelName());
		} else {
			this.viewModelFileText.setText(StringConstants.EMPTY_STRING);
		}

		String viewProcedureName = viewProcedureNameText.getText();
		if (getXmlFileInfo() != null) {
			if (getXmlFileInfo().getViewProcedureName() != null) {
				this.viewProcedureNameText.setText(getXmlFileInfo().getViewProcedureName());
			} else {
				this.viewProcedureNameText.setText(StringConstants.EMPTY_STRING);
			}
		} else {
			this.viewProcedureNameText.setText(viewProcedureName);
		}

		String viewFileName = EMPTY_STRING;
		if( this.info.getViewModelName() != null ) {
			viewFileName = this.info.getViewModelName();
		}
		this.info.setSourceXmlFileInfo(null);
		for (TeiidXmlFileInfo fileInfo : this.info.getXmlFileInfos()) {
			if (fileInfo.doProcess()) {
				this.info.setSourceXmlFileInfo(fileInfo);
				if (this.info.getViewModelName() != null) {
					viewFileName = this.info.getViewModelName();
				} else {
					viewFileName = "ViewProcedures"; //$NON-NLS-1$
					this.info.setViewModelName(viewFileName);
				}
				break;
			}
		}

		this.viewModelFileText.setText(viewFileName);

		synchronizing = false;
	}
	

	private boolean sourceModelExists() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString())
				.append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager
				.getModelWorkspaceManager().findModelWorkspaceItem(modelPath,
						IResource.FILE);
		if (item != null) {
			return true;
		}

		return false;
	}

	private boolean viewModelExists() {
		if (this.viewModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(viewModelFilePath.toOSString())
				.append(this.viewModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager
				.getModelWorkspaceManager().findModelWorkspaceItem(modelPath,
						IResource.FILE);
		if (item != null) {
			return true;
		}

		return false;
	}
	
	private String getViewFileName() {
		return this.viewModelFileText.getText().trim();
	}

	private String getSourceFileName() {
		return this.sourceModelFileText.getText().trim();
	}

	public IProject getTargetProject() {
		IProject result = null;
		String containerName = getSourceContainerName();

		if (!CoreStringUtil.isEmpty(containerName)) {
			IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));

			if (resource.exists()) {
				result = resource.getProject();
			}
		}

		return result;
	}

	public IProject getViewTargetProject() {
		IProject result = null;
		String containerName = getViewContainerName();

		if (!CoreStringUtil.isEmpty(containerName)) {
			IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));

			if (resource.exists()) {
				result = resource.getProject();
			}
		}

		return result;
	}

	public String getSourceContainerName() {
		String result = null;

		if (ProductCustomizerMgr.getInstance().getProductCharacteristics()
				.isHiddenProjectCentric()) {
			result = getHiddenProjectPath();
		} else {
			result = sourceModelContainerText.getText().trim();
		}

		return result;
	}

	public String getViewContainerName() {
		String result = null;

		if (ProductCustomizerMgr.getInstance().getProductCharacteristics()
				.isHiddenProjectCentric()) {
			result = getHiddenProjectPath();
		} else {
			result = viewModelContainerText.getText().trim();
		}

		return result;
	}

	private String getHiddenProjectPath() {
		String result = null;
		IProject hiddenProj = ProductCustomizerMgr.getInstance()
				.getProductCharacteristics().getHiddenProject(false);

		if (hiddenProj != null) {
			result = hiddenProj.getFullPath().makeRelative().toString();
		}

		return result;
	}

	private void setViewHelpMessage() {
		if (!controlComplete)
			return;
		
		if (getXmlFileInfo() == null || !getXmlFileInfo().doProcess()
				|| info.getViewModelName() == null
				|| info.getViewModelName().length() == 0) {
			this.viewHelpText.setText(Util.getString(I18N_PREFIX
					+ "viewModelUndefined")); //$NON-NLS-1$
		} else {
			this.viewHelpText
					.setText(Util
							.getString(
									I18N_PREFIX + "viewModelWillBeCreated", info.getViewModelName(), info.getViewModelName())); //$NON-NLS-1$
		}
	}

	private void setSourceHelpMessage() {
		if (!controlComplete)
			return;
		
		String procedureName = GET_TEXT_FILES;
		if (info.isRestUrlFileMode()) {
			procedureName = INVOKE_HTTP;
		}
		if (info.sourceModelExists()) {
			if (sourceHasProcedure()) {
				this.sourceHelpText
						.setText(Util
								.getString(
										I18N_PREFIX
												+ "existingSourceModelHasProcedure", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			} else {
				this.sourceHelpText
						.setText(Util
								.getString(
										I18N_PREFIX
												+ "existingSourceModelHasNoProcedure", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			}
		} else {
			if (getXmlFileInfo() == null || !getXmlFileInfo().doProcess()
					|| info.getSourceModelName() == null
					|| info.getSourceModelName().length() == 0) {
				this.sourceHelpText.setText(Util.getString(I18N_PREFIX
						+ "sourceModelUndefined")); //$NON-NLS-1$
			} else {
				this.sourceHelpText
						.setText(Util
								.getString(
										I18N_PREFIX
												+ "sourceModelWillBeCreated", info.getSourceModelName(), procedureName)); //$NON-NLS-1$
			}
		}
	}

	private boolean sourceHasProcedure() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString())
				.append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		IResource sourceModel = ModelerCore.getWorkspace().getRoot()
				.getFile(modelPath);
		ModelResource smr = ModelUtilities.getModelResourceForIFile(
				(IFile) sourceModel, false);
		if (smr != null) {
			if (info.isRestUrlFileMode()) {
				return FlatFileRelationalModelFactory.procedureExists(smr,
						FlatFileRelationalModelFactory.INVOKE_HTTP);
			}
		}

		return false;
	}

	private boolean sourceModelHasSameConnectionProfile() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString())
				.append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		IResource sourceModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
		ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile) sourceModel, false);
		if (smr != null) {
			IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
			if (profile == null || this.info.getConnectionProfile() == null) {
				return false;
			}

			if (profile.getName().equalsIgnoreCase(
					this.info.getConnectionProfile().getName())) {
				return true;
			}
		}

		return false;
	}
	
	final ViewerFilter sourceModelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject()
						.isOpen();

				if (projectOpen) {
					// Show open projects
					if (element instanceof IProject) {
						try {
							doSelect = ((IProject) element)
									.hasNature(ModelerCore.NATURE_ID);
						} catch (CoreException e) {
							UiConstants.Util.log(e);
						}
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile
							&& ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource(
									(IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null
								&& ModelIdentifier
										.isRelationalSourceModel(theModel)) {
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

	final ViewerFilter viewModelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject()
						.isOpen();

				if (projectOpen) {
					// Show open projects
					if (element instanceof IProject) {
						try {
							doSelect = ((IProject) element)
									.hasNature(ModelerCore.NATURE_ID);
						} catch (CoreException e) {
							UiConstants.Util.log(e);
						}
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile
							&& ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource(
									(IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null
								&& ModelIdentifier
										.isRelationalViewModel(theModel)) {
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

}

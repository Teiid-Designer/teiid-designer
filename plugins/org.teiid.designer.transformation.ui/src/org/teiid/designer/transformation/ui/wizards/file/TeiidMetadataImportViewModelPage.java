/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
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


/**
 * @since 8.0
 */
public class TeiidMetadataImportViewModelPage extends AbstractWizardPage
		implements UiConstants {
	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportViewModelPage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

	private static final String DEFAULT_EXTENSION = ".xmi"; //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private static String getString(final String id, final Object param) {
		return Util.getString(I18N_PREFIX + id, param);
	}

	RelationalStringNameValidator validator = new RelationalStringNameValidator(true, true);
	
	// View Model Variables
	private Text viewModelContainerText;
	private Text viewModelFileText;
	private Text viewHelpText;
	private IPath viewModelFilePath;
	
	private Text viewTableNameText;

	private TeiidMetadataImportInfo info;

	Text selectedFileText;

	private TeiidFileInfo fileInfo;

	boolean creatingControl = false;

	boolean synchronizing = false;

	/**
     * @param info the import data (cannot be <code>null</code>)
	 * @since 4.0
	 */
	public TeiidMetadataImportViewModelPage(TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportViewModelPage.class.getSimpleName(), TITLE);

        CoreArgCheck.isNotNull(info, "info"); //$NON-NLS-1$
        this.info = info;

		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
				Images.IMPORT_TEIID_METADATA));
	}

	@Override
	public void createControl(Composite parent) {
		creatingControl = true;
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(2, false));
		mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);

		setMessage(INITIAL_MESSAGE);

		Label selectedFileLabel = new Label(mainPanel, SWT.NONE);
		selectedFileLabel.setText(getString("selectedXmlFile")); //$NON-NLS-1$
		
        selectedFileText = new Text(mainPanel, SWT.BORDER | SWT.SINGLE);
        selectedFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        selectedFileText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedFileText.setEditable(false);
		
		new Label(mainPanel, SWT.NONE);
		new Label(mainPanel, SWT.NONE);
		
		createViewModelGroup(mainPanel);

		setViewHelpMessage();

		creatingControl = false;

		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			if( info.isFlatFileLocalMode() || info.isFlatFileUrlMode() ) {
				for (TeiidMetadataFileInfo theFileInfo : info.getFileInfos()) {
					if (theFileInfo.doProcess()) {
						this.fileInfo = theFileInfo;
						break;
					}
				}
			} else {
				for (TeiidXmlFileInfo xmlInfo : info.getXmlFileInfos()) {
					if (xmlInfo.doProcess()) {
						this.fileInfo = xmlInfo;
						break;
					}
				}
			}
			synchronizeUI();

			setViewHelpMessage();
			
			validatePage();
		}
	}

	private void createViewModelGroup(Composite parent) {
		Group viewGroup = WidgetFactory.createGroup(parent,getString("viewModelDefinitionGroup"), SWT.NONE, 2, 3); //$NON-NLS-1$
		GridData gd_vg = new GridData(GridData.FILL_HORIZONTAL);
		gd_vg.horizontalSpan = 2;
		viewGroup.setLayoutData(gd_vg);

		Label locationLabel = new Label(viewGroup, SWT.NULL);
		locationLabel.setText(getString("location")); //$NON-NLS-1$

		viewModelContainerText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		viewModelContainerText.setLayoutData(gridData);
		viewModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		viewModelContainerText.setEditable(false);

		Button browseButton = new Button(viewGroup, SWT.PUSH);
		gridData = new GridData();
		// buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		browseButton.setLayoutData(gridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleViewModelLocationBrowse();
			}
		});

		Label fileLabel = new Label(viewGroup, SWT.NULL);
		fileLabel.setText(getString("name")); //$NON-NLS-1$

		viewModelFileText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		viewModelFileText.setLayoutData(gridData);
		viewModelFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// Check view file name for existing if "location" is already
				// set
				handleViewModelTextChanged();
			}
		});

		browseButton = new Button(viewGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText(getString("browse")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleViewModelBrowse();
			}
		});

		new Label(viewGroup, SWT.NONE);

		Group helpGroup = WidgetFactory.createGroup(viewGroup, getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH, 2); //$NON-NLS-1$
		helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			viewHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
			viewHelpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			viewHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			gd.horizontalSpan = 3;
			viewHelpText.setLayoutData(gd);
		}
		
		new Label(viewGroup, SWT.NONE);
		
		// View Table Definition
		{
			Label viewTableLabel = new Label(viewGroup, SWT.NULL);
			viewTableLabel.setText(getString("newViewTableName")); //$NON-NLS-1$

			viewTableNameText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			viewTableNameText.setLayoutData(gridData);
			viewTableNameText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					// Check view file name for existing if "location" is already
					// set
					handleViewTableTextChanged();
				}
			});
		}
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
			// viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.info.setViewModelLocation(folder.getFullPath().makeRelative());
		}
		
        if (folder != null && viewModelContainerText != null) {
            this.info.setViewModelLocation(folder.getFullPath().makeRelative());
            this.viewModelFilePath = this.info.getViewModelLocation();
            this.viewModelContainerText.setText(this.info.getViewModelLocation().makeRelative().toString());
        } else {
        	this.info.setViewModelLocation(new Path(StringUtilities.EMPTY_STRING));
            this.viewModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
    	if( this.viewModelFileText.getText() != null && this.viewModelFileText.getText().length() > -1 ) {
    		this.info.setViewModelExists(viewModelExists());
    		
    	}

		validatePage();
	}

	void handleViewModelBrowse() {
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						getString("selectViewModelTitle"), //$NON-NLS-1$
						getString("selectViewModelMessage"), //$NON-NLS-1$
						false, null, virtualModelFilter,
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
		
        if( this.info.getViewModelName() != null ) {
        	this.viewModelFilePath = this.info.getViewModelLocation();
        	this.viewModelContainerText.setText(this.info.getViewModelLocation().makeRelative().toString());
        	this.viewModelFileText.setText(this.info.getViewModelName());
        } else {
        	this.viewModelFileText.setText(StringUtilities.EMPTY_STRING);
        }

		validatePage();
	}

	void handleViewModelTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.viewModelFileText.getText() != null) {
			if( this.viewModelFileText.getText().length() == 0 ) {
				this.info.setViewModelName(newName);
				this.info.setViewModelExists(false);
			} else {
				newName = this.viewModelFileText.getText();
				this.info.setViewModelName(newName);
				this.info.setViewModelExists(viewModelExists());
			}

		}

		//synchronizeUI();

		validatePage();
	}
	
	void handleViewTableTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.viewTableNameText.getText() != null && this.viewTableNameText.getText().length() > -1) {
			newName = this.viewTableNameText.getText();
			this.fileInfo.setViewTableName(newName);
		} else {
			this.fileInfo.setViewTableName(StringUtilities.EMPTY_STRING);
		}

		synchronizeUI();

		validatePage();
	}

	private boolean validatePage() {
		setViewHelpMessage();

		// =============== Target MODEL INFO CHECKS ==================
		String container = viewModelContainerText.getText();
		if (CoreStringUtil.isEmpty(container)) {
			setThisPageComplete(getString("viewFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
			return false;
		}

		IProject project = getTargetProject();
		if (project == null) {
			setThisPageComplete(getString("viewFileLocationMustBeSpecified"), ERROR); //$NON-NLS-1$
			return false;
		} else if (!project.isOpen()) {
			setThisPageComplete(getString("targetProjectIsClosed"), ERROR); //$NON-NLS-1$
			return false;
		} else {
			try {
				if (project.getNature(ModelerCore.NATURE_ID) == null) {
					setThisPageComplete(getString("targetProjectIsNotDesignerNature"), ERROR); //$NON-NLS-1$
					return false;
				}
			} catch (CoreException ex) {
				setThisPageComplete(getString("targetProjectHasNoNature"), ERROR); //$NON-NLS-1$
				return false;
			}
		}

		String fileText = this.viewModelFileText.getText().trim();
		
        IStatus status = ModelNameUtil.validate(fileText, ModelerCore.MODEL_FILE_EXTENSION, null,
        		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES);
        if( status.getSeverity() == IStatus.ERROR ) {
        	setThisPageComplete(status.getMessage(), ERROR);
			return false;
		}

		String viewFileName = getViewFileName();
		String sourceFilename = getSourceFileName();
		if (viewFileName.equalsIgnoreCase(sourceFilename)) {
			setThisPageComplete(getString("sourceAndViewFilesCannotHaveSameName"), ERROR); //$NON-NLS-1$
			return false;
		}
		
		// Check if View Name is valid
		String invalidMessage = validator.checkValidName(fileInfo.getViewTableName());
		if( invalidMessage != null) {
			setThisPageComplete(getString("viewTableNameInvalid", invalidMessage), ERROR); //$NON-NLS-1$
			return false;
		}
		// Check if view table already exists
		if( viewAlreadyExists() ) {
			setThisPageComplete(getString("viewTableAlreadyExists", fileInfo.getViewTableName()), ERROR); //$NON-NLS-1$
			return false;
		}
		
		if( fileInfo.getViewTableName() == null || fileInfo.getViewTableName().length() == 0) {
			setThisPageComplete(getString("viewTableNameNullOrEmpty"), ERROR); //$NON-NLS-1$
			return false;
		}

		setThisPageComplete(StringUtilities.EMPTY_STRING, NONE);

		return true;
	}

	private void setViewHelpMessage() {
		if (creatingControl)
			return;

		if (info.viewModelExists()) {
			this.viewHelpText.setText(Util.getString(I18N_PREFIX + "existingViewModelMessage", info.getViewModelName())); //$NON-NLS-1$
		} else {
			if (info.getViewModelName() == null || info.getViewModelName().length() == 0) {
				this.viewHelpText.setText(Util.getString(I18N_PREFIX + "viewModelUndefined")); //$NON-NLS-1$
			} else {
				this.viewHelpText.setText(Util.getString(I18N_PREFIX + "newViewModelMessage", info.getViewModelName())); //$NON-NLS-1$
			}
		}
	}

	private IProject getTargetProject() {
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

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}

	private boolean viewModelExists() {
		if (this.viewModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(viewModelFilePath.toOSString()).append(this.viewModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if (item != null) {
			return true;
		}

		return false;
	}

	private void synchronizeUI() {
		synchronizing = true;

		java.io.File dataFile = fileInfo.getDataFile();
		String fileName = dataFile.getName();
		selectedFileText.setText(fileName);

		if (this.info.getViewModelLocation() != null) {
			viewModelContainerText.setText(this.info.getViewModelLocation().makeRelative().toString());
		} else {
			this.viewModelContainerText.setText(StringUtilities.EMPTY_STRING);
		}

		if (this.info.getViewModelName() != null) {
			String viewModelName = this.info.getViewModelName();
			this.viewModelFileText.setText(viewModelName);
		} else {
			this.viewModelFileText.setText(StringUtilities.EMPTY_STRING);
		}

        { // view table name
            final String viewTableName = this.fileInfo.getViewTableName();

            if (viewTableName != null) {
                if (!StringUtilities.equalsIgnoreCase(viewTableName, this.viewTableNameText.getText())) {
                    this.viewTableNameText.setText(viewTableName);
                }
            } else {
                this.viewTableNameText.setText(StringUtilities.EMPTY_STRING);
            }
        }

		synchronizing = false;
	}

	private String getHiddenProjectPath() {
		String result = null;
		IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

		if (hiddenProj != null) {
			result = hiddenProj.getFullPath().makeRelative().toString();
		}

		return result;
	}

	private String getViewContainerName() {
		String result = null;

		if (ProductCustomizerMgr.getInstance().getProductCharacteristics()
				.isHiddenProjectCentric()) {
			result = getHiddenProjectPath();
		} else {
			result = this.viewModelContainerText.getText().trim();
		}

		return result;
	}

	private String getViewFileName() {
		String result = this.viewModelFileText.getText().trim();
		if (!result.endsWith(DEFAULT_EXTENSION)) {
			result += DEFAULT_EXTENSION;
		}
		return result;
	}
	
	private String getSourceFileName() {
		String result = this.info.getSourceModelName().trim();
		if (!result.endsWith(DEFAULT_EXTENSION)) {
			result += DEFAULT_EXTENSION;
		}
		return result;
	}

	final ViewerFilter virtualModelFilter = new ModelWorkspaceViewerFilter(true) {

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
						if (theModel != null && ModelIdentifier.isRelationalViewModel(theModel)) {
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

	private boolean viewAlreadyExists() {
		if( !info.viewModelExists() ) {
			return false;
		}
		
		IPath modelPath = new Path(viewModelFilePath.toOSString()).append(this.viewModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
		}

    	IResource viewModel = ModelerCore.getWorkspace().getRoot().getFile(modelPath);
    	ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)viewModel, false);
    	if( smr != null ) {
    		try {
    			String existingName = fileInfo.getViewTableName();
    			for( Object obj : smr.getAllRootEObjects() ) {

                    EObject eObj = (EObject)obj;
                    if (eObj instanceof BaseTable  && existingName.equalsIgnoreCase(ModelObjectUtilities.getName(eObj)) ) {
                        return true;
                    }
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
            }

    	}
    	
    	return false;
	}
}

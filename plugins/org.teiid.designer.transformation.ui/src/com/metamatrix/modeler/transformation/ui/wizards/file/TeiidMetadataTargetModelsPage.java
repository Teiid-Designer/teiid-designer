/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * Wizard page to allow users to enter, edit and manage the source and view models for this importer
 */
public class TeiidMetadataTargetModelsPage extends AbstractWizardPage
	implements UiConstants {
	// ===========================================================================================================================
	// Constants
	
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataTargetModelsPage.class);
	
	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
	
    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_SOURCE_LOCATION 	= 1;
    private static final int STATUS_NO_VIEW_LOCATION 	= 2;
    private static final int STATUS_NO_SOURCE_FILENAME 	= 3;
    private static final int STATUS_SOURCE_FILE_EXISTS 	= 4;    
    private static final int STATUS_NO_VIEW_FILENAME 	= 5;
    private static final int STATUS_BAD_FILENAME 		= 6;
    private static final int STATUS_CLOSED_PROJECT 		= 7;
    private static final int STATUS_NO_PROJECT_NATURE 	= 8;
    private static final int STATUS_SAME_MODEL_NAMES 	= 9;
    
    private static final String DEFAULT_EXTENSION = ".xmi"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
	
    private Text sourceModelContainerText;
    private Text sourceModelFileText;
    private IPath sourceModelFilePath;
    
    private Text viewModelContainerText;
    private Text viewModelFileText;
    private IPath viewModelFilePath;
    private Button updateViewModelCheckBox;
    
    private int currentStatus = STATUS_OK;
    private String fileNameMessage = null;
    private String panelMessage = null;
    
    private TeiidMetadataImportInfo info;
    
	/**
	 * @since 4.0
	 */
	public TeiidMetadataTargetModelsPage(TeiidMetadataImportInfo info) {
		super(TeiidMetadataTargetModelsPage.class.getSimpleName(), TITLE);
		this.info = info;
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA));
	}
	
	@Override
	public void createControl(Composite parent) {
        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        
        mainPanel.setLayout(new GridLayout());
        mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        setControl(mainPanel);
        // Add widgets to page
        
        createSourceModelGroup(mainPanel);
        
        createViewModelGroup(mainPanel);
        
        setMessage(INITIAL_MESSAGE);
        
        setPageComplete(false);
	}
	
//    private boolean validatePage() {
//    	WizardUtil.setPageComplete(this);
//    	setMessage(INITIAL_MESSAGE);
//        return true;
//    }
    
    private void createSourceModelGroup(Composite parent) {
    	Group sourceGroup = WidgetFactory.createGroup(parent, getString("sourceModelDefinitionGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	sourceGroup.setLayout(new GridLayout(3, false));
    	sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label locationLabel = new Label(sourceGroup, SWT.NULL);
        locationLabel.setText(getString("location")); //$NON-NLS-1$

        sourceModelContainerText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        if( this.info.getSourceModelLocation() != null ) {
        	sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
        }
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        sourceModelContainerText.setLayoutData(gridData);
        sourceModelContainerText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                updateStatusMessage();
            }
        });
        sourceModelContainerText.setEditable(false);

        Button browseButton = new Button(sourceGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleSourceModelLocationBrowse();
            }
        });

        Label fileLabel = new Label(sourceGroup, SWT.NULL);
        fileLabel.setText(getString("name")); //$NON-NLS-1$

        sourceModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        sourceModelFileText.setLayoutData(gridData);
        sourceModelFileText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
            	handleSourceModelTextChanged();
            }
        });
        new Label(sourceGroup, SWT.NONE);
    }
    
    private void createViewModelGroup(Composite parent) {
    	Group viewGroup = WidgetFactory.createGroup(parent, getString("viewModelDefinitionGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	viewGroup.setLayout(new GridLayout(3, false));
    	viewGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
        Label locationLabel = new Label(viewGroup, SWT.NULL);
        locationLabel.setText(getString("location")); //$NON-NLS-1$

        viewModelContainerText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
        if( this.info.getViewModelLocation() != null ) {
        	viewModelContainerText.setText(this.info.getViewModelLocation().makeRelative().toString());
        }
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        viewModelContainerText.setLayoutData(gridData);
        viewModelContainerText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                updateStatusMessage();
            }
        });
        viewModelContainerText.setEditable(false);

        Button browseButton = new Button(viewGroup, SWT.PUSH);
         gridData = new GridData();
        // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleViewModelLocationBrowse();
            }
        });

        Label fileLabel = new Label(viewGroup, SWT.NULL);
        fileLabel.setText(getString("name")); //$NON-NLS-1$

        viewModelFileText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        viewModelFileText.setLayoutData(gridData);
        viewModelFileText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
            	// Check view file name for existing if "location" is already set
            	handleViewModelTextChanged();
            }
        });
        
        browseButton = new Button(viewGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleViewModelBrowse();
            }
        });
        
        this.updateViewModelCheckBox = WidgetFactory.createCheckBox(viewGroup, getString("updateExistingModel"), 0, 3); //$NON-NLS-1$
        this.updateViewModelCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateViewModelCheckBoxSelected();
            }
        });
        
        
        if( this.info.getViewModelName() != null ) {
        	viewModelFileText.setText(this.info.getViewModelName());
        	this.updateViewModelCheckBox.setSelection(true);
        }
    }
    
    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleSourceModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ResourcesPlugin.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && sourceModelContainerText != null) {
            sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
            this.info.setSourceModelLocation(folder.getFullPath().makeRelative());
        }

        updateStatusMessage();
    }
    
    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     */
    void handleViewModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ResourcesPlugin.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && viewModelContainerText != null) {
            viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
            this.info.setViewModelLocation(folder.getFullPath().makeRelative());
        }

        updateStatusMessage();
    }
    
    void handleViewModelBrowse() {
        final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("selectViewModelTitle"), //$NON-NLS-1$
                                                                               getString("selectViewModelMessage"), //$NON-NLS-1$
                                                                               false,
                                                                               null,
                                                                               virtualModelFilter,
                                                                               new ModelResourceSelectionValidator(false),
                                                                               new ModelExplorerLabelProvider(),
                                                                               new ModelExplorerContentProvider() ); 

        if (selections != null && selections.length == 1 && viewModelFileText != null) {
        	if( selections[0] instanceof IFile) {
        		IFile modelFile = (IFile)selections[0];
        		IPath folderPath = modelFile.getFullPath().makeRelative().removeLastSegments(1);
        		String modelName = modelFile.getFullPath().lastSegment();
        		viewModelFileText.setText(modelName);
        		viewModelFilePath = folderPath;
        		viewModelContainerText.setText(folderPath.makeRelative().toString());
        		this.updateViewModelCheckBox.setSelection(true);
        		info.setViewModelExists(true);
        		updateViewModelCheckBoxSelected();
        	}
        }

        updateStatusMessage();
    }
    
    void handleSourceModelTextChanged() {
    	String newName = ""; //$NON-NLS-1$
    	if( this.sourceModelFileText.getText() != null ) {
    		newName = this.sourceModelFileText.getText();
    	}
    	this.info.setSourceModelName(newName);
    	
        updateStatusMessage();
    }
    
    void handleViewModelTextChanged() {
    	if( info.getViewModelLocation() != null && this.viewModelFileText.getText() != null ) {
    		IPath modelPath = info.getViewModelLocation().append(this.viewModelFileText.getText());
    		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
    			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
    		}
    		
    		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
    		if( item != null ) {
    			this.updateViewModelCheckBox.setSelection(true);
    			
    		} else {
    			this.updateViewModelCheckBox.setSelection(false);
    		}
    		if( this.viewModelFileText.getText().length() > 0 ) {
    			this.info.setViewModelName(this.viewModelFileText.getText());
    		}
    	}
    	updateViewModelCheckBoxSelected();
        updateStatusMessage();
    }
    
    void updateViewModelCheckBoxSelected() {
    	final ModelResource model;
        /*
         * jhTODO: when checkbox is true, disable finish, enable next
         */
        if (this.updateViewModelCheckBox.getSelection()) {
            try {
                model = getSelectedViewResource();
                if (model != null && !model.isReadOnly()) {
                    if (model.getModelType().getValue() != ModelType.VIRTUAL) {
                        this.viewModelFileText.setText(model.getItemName());
                        // Return here (skipping call to validatePage) since previous line that sets the model name will end
                        // up calling validatePage anyway.
                        return;
                    }
                }
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
                WidgetUtil.showError(err.getLocalizedMessage());
            }
        }
        updateStatusMessage();
    }
    
    /**
     * Get the selected ModelResource.  This method will check the selectedModel field - if it is
     * not null it will be returned.  Otherwise, the original selection is returned.
     * @return the ModelResource selection
     */
    private ModelResource getSelectedViewResource() throws ModelWorkspaceException {
    	ModelResource model = null;

        final IStructuredSelection selection = UiUtil.getStructuredSelection();
        if(selection.size() == 1) {
        	model = ModelUtil.getModel(selection.getFirstElement());
        }
        
    	return model;
    }

    /**
     * Ensures that controls are set and sends the panel status message.
     * 
     * @return true if the status is okay, false if there is an error.
     */
    void updateStatusMessage() {
        checkStatus();
        switch (currentStatus) {

            case (STATUS_NO_SOURCE_LOCATION):
                updateStatus(Util.getString(I18N_PREFIX + "sourceFileLocationMustBeSpecified")); //$NON-NLS-1$
                break;

            case (STATUS_NO_SOURCE_FILENAME):
                updateStatus(Util.getString(I18N_PREFIX + "sourceFileNameMustBeSpecified")); //$NON-NLS-1$
                break;

            case (STATUS_BAD_FILENAME):
                updateStatus(Util.getString(I18N_PREFIX + "illegalFileName", fileNameMessage)); //$NON-NLS-1$
                break;

            case (STATUS_SOURCE_FILE_EXISTS):
                final String fileName = getSourceFileName();
                final String container = getSourceContainerName();
                sourceModelFilePath = new Path(container).append(fileName);
                updateStatus(Util.getString(I18N_PREFIX + "fileAlreadyExistsMessage", sourceModelFilePath.toOSString())); //$NON-NLS-1$
                break;
            case (STATUS_NO_VIEW_LOCATION):
                updateStatus(Util.getString(I18N_PREFIX + "viewFileLocationMustBeSpecified")); //$NON-NLS-1$
                break;

            case (STATUS_NO_VIEW_FILENAME):
                updateStatus(Util.getString(I18N_PREFIX + "viewFileNameMustBeSpecified")); //$NON-NLS-1$
                break;
                
            case (STATUS_SAME_MODEL_NAMES):
            	updateStatus(Util.getString(I18N_PREFIX + "sourceAndViewFilesCannotHaveSameName")); //$NON-NLS-1$
            	break;
            
            case (STATUS_OK):
            default:
                updateStatus(panelMessage);
                break;
        }
    }

    private boolean checkStatus() {
    	// =============== SOURCE MODEL INFO CHECKS ==================
        String container = sourceModelContainerText.getText();
        if (CoreStringUtil.isEmpty(container)) {
            currentStatus = STATUS_NO_SOURCE_LOCATION;
            return false;
        }
        IProject project = getTargetProject();
        if (project == null) {
            currentStatus = STATUS_NO_SOURCE_LOCATION;
            return false;
        } else if (!project.isOpen()) {
            currentStatus = STATUS_CLOSED_PROJECT;
            return false;
        } else {
            try {
                if (project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                    currentStatus = STATUS_NO_PROJECT_NATURE;
                    return false;
                }
            } catch (CoreException ex) {
                currentStatus = STATUS_NO_PROJECT_NATURE;
                return false;
            }
        }
        
        String fileText = getSourceFileText();
        if (fileText.length() == 0) {
            currentStatus = STATUS_NO_SOURCE_FILENAME;
            return false;
        }
        fileNameMessage = ModelUtilities.validateModelName(fileText, DEFAULT_EXTENSION);
        if (fileNameMessage != null) {
            currentStatus = STATUS_BAD_FILENAME;
            return false;
        }
        String fileName = getSourceFileName();
        sourceModelFilePath = new Path(container).append(fileName);
        if (ResourcesPlugin.getWorkspace().getRoot().exists(sourceModelFilePath)) {
            currentStatus = STATUS_SOURCE_FILE_EXISTS;
            return false;
        }
        
        // =============== SOURCE MODEL INFO CHECKS ==================

        container = viewModelContainerText.getText();
        if (CoreStringUtil.isEmpty(container)) {
            currentStatus = STATUS_NO_VIEW_LOCATION;
            return false;
        }
        project = getTargetProject();
        if (project == null) {
            currentStatus = STATUS_NO_VIEW_LOCATION;
            return false;
        } else if (!project.isOpen()) {
            currentStatus = STATUS_CLOSED_PROJECT;
            return false;
        } else {
            try {
                if (project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null) {
                    currentStatus = STATUS_NO_PROJECT_NATURE;
                    return false;
                }
            } catch (CoreException ex) {
                currentStatus = STATUS_NO_PROJECT_NATURE;
                return false;
            }
        }
        
        fileText = getViewFileText();
        if (fileText.length() == 0) {
            currentStatus = STATUS_NO_VIEW_FILENAME;
            return false;
        }
        fileNameMessage = ModelUtilities.validateModelName(fileText, DEFAULT_EXTENSION);
        if (fileNameMessage != null) {
            currentStatus = STATUS_BAD_FILENAME;
            return false;
        }
        
        String viewFileName = getViewFileName();
        String sourceFilename = getSourceFileName();
        if( viewFileName.equalsIgnoreCase(sourceFilename) ) {
        	currentStatus = STATUS_SAME_MODEL_NAMES;
        	return false;
        }
        
        
        currentStatus = STATUS_OK;
        return true;
    }

    private void updateStatus( String message ) {
        setErrorMessage(message);
        boolean complete = ((message == null));
        setPageComplete(complete);
        if( complete ) {
        	setMessage(getString("pressFinishMessage")); //$NON-NLS-1$
        }
    }
    
    private String getHiddenProjectPath() {
        String result = null;
        IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

        if (hiddenProj != null) {
            result = hiddenProj.getFullPath().makeRelative().toString();
        }

        return result;
    }

    public String getSourceContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = sourceModelContainerText.getText().trim();
        }

        return result;
    }

    public String getSourceFileName() {
        String result = sourceModelFileText.getText().trim();
        if (!result.endsWith(DEFAULT_EXTENSION)) {
            result += DEFAULT_EXTENSION;
        }
        return result;
    }

    public String getSourceFileText() {
        return sourceModelFileText.getText().trim();
    }

    public IPath getSourceFilePath() {
        return this.sourceModelFilePath;
    }
    
    public String getViewContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = this.viewModelContainerText.getText().trim();
        }

        return result;
    }

    public String getViewFileName() {
        String result = this.viewModelFileText.getText().trim();
        if (!result.endsWith(DEFAULT_EXTENSION)) {
            result += DEFAULT_EXTENSION;
        }
        return result;
    }

    public String getViewFileText() {
        return this.viewModelFileText.getText().trim();
    }

    public IPath getViewFilePath() {
        return this.viewModelFilePath;
    }
    
    public IProject getTargetProject() {
        IProject result = null;
        String containerName = getSourceContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource.getProject();
            }
        }

        return result;
    }
    
    final ViewerFilter virtualModelFilter = new ModelWorkspaceViewerFilter(true) {

        @Override
        public boolean select( final Viewer viewer,
                               final Object parent,
                               final Object element ) {
            boolean doSelect = false;
            if (element instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)element).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (element instanceof IProject) {
                        doSelect = true;
                    } else if (element instanceof IContainer) {
                        doSelect = true;
                        // Show webservice model files, and not .xsd files
                    } else if (element instanceof IFile && ModelUtil.isModelFile((IFile)element)) {
                        ModelResource theModel = null;
                        try {
                            theModel = ModelUtil.getModelResource((IFile)element, true);
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
}

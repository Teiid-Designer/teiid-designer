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

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.Procedure;
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
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
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
	
	private static final String GET_TEXT_FILES = "getTextFiles"; //$NON-NLS-1$
	
    private static final int STATUS_OK = 0;
    private static final int STATUS_NO_SOURCE_LOCATION 	= 1;
    private static final int STATUS_NO_VIEW_LOCATION 	= 2;
    private static final int STATUS_NO_SOURCE_FILENAME 	= 3;
    private static final int STATUS_NO_VIEW_FILENAME 	= 4;
    private static final int STATUS_BAD_FILENAME 		= 5;
    private static final int STATUS_CLOSED_PROJECT 		= 6;
    private static final int STATUS_NO_PROJECT_NATURE 	= 7;
    private static final int STATUS_SAME_MODEL_NAMES 	= 8;
    
    private static final String DEFAULT_EXTENSION = ".xmi"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
	
    private Text sourceModelContainerText;
    private Text sourceModelFileText;
    private Text sourceHelpText;
    private IPath sourceModelFilePath;
    
    private Text viewModelContainerText;
    private Text viewModelFileText;
    private Text viewHelpText;
    private IPath viewModelFilePath;
    
    private int currentStatus = STATUS_OK;
    private String fileNameMessage = null;
    private String panelMessage = null;
    
    private TeiidMetadataImportInfo info;
    
    boolean creatingControl = false;
    
    boolean synchronizing = false;
    
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
		creatingControl = true; 
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
        
        setViewHelpMessage();
        setSourceHelpMessage();

        creatingControl = false;
        
        setPageComplete(false);
	}
    
    @Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		if( visible ) {
			synchronizeUI();
		}
	}

	private void createSourceModelGroup(Composite parent) {
    	Group sourceGroup = WidgetFactory.createGroup(parent, getString("sourceModelDefinitionGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	sourceGroup.setLayout(new GridLayout(3, false));
    	sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label locationLabel = new Label(sourceGroup, SWT.NULL);
        locationLabel.setText(getString("location")); //$NON-NLS-1$

        sourceModelContainerText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        sourceModelContainerText.setLayoutData(gridData);
        sourceModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
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
        
        browseButton = new Button(sourceGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(getString("browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleSourceModelBrowse();
            }
        });
        
    	new Label(sourceGroup, SWT.NONE);
    	
        Group helpGroup = WidgetFactory.createGroup(sourceGroup, getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH,2); //$NON-NLS-1$
        helpGroup.setLayout(new GridLayout(1, false));
        helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
        {        	
        	sourceHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
        	sourceHelpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	sourceHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        	gd.heightHint = 25;
        	gd.horizontalSpan=3;
        	sourceHelpText.setLayoutData(gd);
        }
        
    }
    
    private void createViewModelGroup(Composite parent) {
    	Group viewGroup = WidgetFactory.createGroup(parent, getString("viewModelDefinitionGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	viewGroup.setLayout(new GridLayout(3, false));
    	viewGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
        Label locationLabel = new Label(viewGroup, SWT.NULL);
        locationLabel.setText(getString("location")); //$NON-NLS-1$

        viewModelContainerText = new Text(viewGroup, SWT.BORDER | SWT.SINGLE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        viewModelContainerText.setLayoutData(gridData);
        viewModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
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
        
    	new Label(viewGroup, SWT.NONE);
    	
        Group helpGroup = WidgetFactory.createGroup(viewGroup, getString("modelStatus"), SWT.NONE | SWT.BORDER_DASH,2); //$NON-NLS-1$
        helpGroup.setLayout(new GridLayout(1, false));
        helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        {
        	viewHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
        	viewHelpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	viewHelpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        	gd.heightHint = 25;
        	gd.horizontalSpan=3;
        	viewHelpText.setLayoutData(gd);
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
            this.info.setSourceModelLocation(folder.getFullPath().makeRelative());
        }
        
        synchronizeUI();

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
            //viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
            this.info.setViewModelLocation(folder.getFullPath().makeRelative());
        }

        synchronizeUI();
        
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
        		IPath folderPath = modelFile.getFullPath().removeLastSegments(1);
        		String modelName = modelFile.getFullPath().lastSegment();
        		info.setViewModelExists(true);
        		info.setViewModelLocation(folderPath);
        		info.setViewModelName(modelName);
        	}
        }
        
        synchronizeUI();

        updateStatusMessage();
    }
    
    void handleSourceModelBrowse() {
        final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("selectViewModelTitle"), //$NON-NLS-1$
                                                                               getString("selectViewModelMessage"), //$NON-NLS-1$
                                                                               false,
                                                                               null,
                                                                               sourceModelFilter,
                                                                               new ModelResourceSelectionValidator(false),
                                                                               new ModelExplorerLabelProvider(),
                                                                               new ModelExplorerContentProvider() ); 

        if (selections != null && selections.length == 1 && viewModelFileText != null) {
        	if( selections[0] instanceof IFile) {
        		IFile modelFile = (IFile)selections[0];
        		IPath folderPath = modelFile.getFullPath().removeLastSegments(1);
        		String modelName = modelFile.getFullPath().lastSegment();
        		info.setSourceModelExists(true);
        		info.setSourceModelLocation(folderPath);
        		info.setSourceModelName(modelName);
        	}
        }
        
        synchronizeUI();
        
        updateStatusMessage();
    }
    
    void handleSourceModelTextChanged() {
    	if( synchronizing ) return;
    	
    	String newName = ""; //$NON-NLS-1$
    	if( this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > 0 ) {
    		newName = this.sourceModelFileText.getText();
    		this.info.setSourceModelName(newName);
    		this.info.setSourceModelExists(sourceModelExists());
    		
    	}
    	synchronizeUI();
    	updateStatusMessage();
    }
    
    void handleViewModelTextChanged() {
    	if( synchronizing ) return;
    	
    	String newName = ""; //$NON-NLS-1$
    	if( this.viewModelFileText.getText() != null && this.viewModelFileText.getText().length() > 0 ) {
    		newName = this.viewModelFileText.getText();
    		this.info.setViewModelName(newName);
    		this.info.setViewModelExists(viewModelExists());
    		
    	}
    	
    	synchronizeUI();
    	
        updateStatusMessage();
    }

    /**
     * Ensures that controls are set and sends the panel status message.
     * 
     * @return true if the status is okay, false if there is an error.
     */
    void updateStatusMessage() {
        checkStatus();
        
        setViewHelpMessage();
        setSourceHelpMessage();
        
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
    
    private void setViewHelpMessage() {
    	if( creatingControl ) return;
    	
        if( info.viewModelExists() ) {
        	this.viewHelpText.setText(Util.getString(I18N_PREFIX + "existingViewModelMessage", info.getViewModelName())); //$NON-NLS-1$
        } else {
        	if( info.getViewModelName() == null ) {
        		this.viewHelpText.setText(Util.getString(I18N_PREFIX + "viewModelUndefined")); //$NON-NLS-1$
        	} else {
        		this.viewHelpText.setText(Util.getString(I18N_PREFIX + "newViewModelMessage", info.getViewModelName())); //$NON-NLS-1$
        	}
        }
    }
    
    private void setSourceHelpMessage() {
    	if( creatingControl ) return;
    
        if( info.sourceModelExists() ) {
	    	if(  sourceHasProcedure() ) {
	    		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasProcedure", info.getSourceModelName())); //$NON-NLS-1$
	    		currentStatus = STATUS_OK;
	    	} else {
	    		currentStatus = STATUS_OK;
	    		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "existingSourceModelHasNoProcedure", info.getSourceModelName())); //$NON-NLS-1$
	    	}
        } else {
        	if( info.getSourceModelName() == null ) {
        		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelUndefined")); //$NON-NLS-1$
        	} else {
        		this.sourceHelpText.setText(Util.getString(I18N_PREFIX + "sourceModelWillBeCreated", info.getSourceModelName())); //$NON-NLS-1$
        	}
        }
    }

    private void updateStatus( String message ) {
        setErrorMessage(message);
        boolean complete = ((message == null));
        setPageComplete(complete);
        if( complete ) {
        	setMessage(getString("pressNextMessage")); //$NON-NLS-1$
        }
    }
    
    private boolean sourceHasProcedure() {
    	if( this.sourceModelFilePath == null ) {
    		return false;
    	}
    	
    	IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
		}
    	
    	IResource sourceModel = ResourcesPlugin.getWorkspace().getRoot().getFile(modelPath);
    	ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
    	if( smr != null ) {
    		try {
    			for( Object obj : smr.getAllRootEObjects() ) {

                    EObject eObj = (EObject)obj;
                    if (eObj instanceof Procedure  && GET_TEXT_FILES.equalsIgnoreCase(ModelObjectUtilities.getName(eObj)) ) {
                        return true;
                    }
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
            }
    	}
    	
    	return false;
    }
    
    private boolean viewModelExists() {
    	if( this.viewModelFilePath == null ) {
    		return false;
    	}
    	
    	IPath modelPath = new Path(viewModelFilePath.toOSString()).append(this.viewModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
		}
		
		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if( item != null ) {
			return true;
		}
    		
    	return false;
    }
    
    private boolean sourceModelExists() {
    	if( this.sourceModelFilePath == null ) {
    		return false;
    	}
    	
		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if( !modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension(".xmi"); //$NON-NLS-1$
		}
		
		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath, IResource.FILE);
		if( item != null ) {
			return true;
		}
    		
    	return false;
    }
    

    
    private void synchronizeUI() {
    	synchronizing = true;
    	
        if( this.info.getSourceModelLocation() != null ) {
        	this.sourceModelContainerText.setText(this.info.getSourceModelLocation().makeRelative().toString());
        } else {
        	this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
        if( this.info.getViewModelLocation() != null ) {
        	viewModelContainerText.setText(this.info.getViewModelLocation().makeRelative().toString());
        } else {
        	this.viewModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
        if( this.info.getViewModelName() != null ) {
        	this.viewModelFilePath = this.info.getViewModelLocation();
        	this.viewModelFileText.setText(this.info.getViewModelName());
        } else {
        	this.viewModelFileText.setText(StringUtilities.EMPTY_STRING);
        }
        
        if( this.info.getSourceModelName() != null ) {
        	this.sourceModelFilePath = this.info.getSourceModelLocation();
        	this.sourceModelFileText.setText(this.info.getSourceModelName());
        } else {
        	this.sourceModelFileText.setText(StringUtilities.EMPTY_STRING);
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
    
    final ViewerFilter sourceModelFilter = new ModelWorkspaceViewerFilter(true) {

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
                        if (theModel != null && ModelIdentifier.isRelationalSourceModel(theModel)) {
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

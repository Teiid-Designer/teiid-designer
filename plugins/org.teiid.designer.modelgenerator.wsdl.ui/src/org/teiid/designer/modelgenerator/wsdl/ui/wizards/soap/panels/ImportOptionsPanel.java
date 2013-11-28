/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.event.IChangeListener;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.MetamodelSelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

/**
 * @since 8.0
 */
public class ImportOptionsPanel implements IChangeListener, ModelGeneratorWsdlUiConstants {
	// Source Model Definition
	private Text sourceModelFileText;
	private Text sourceModelContainerText;
	private Text sourceModelHelpText;
	
	// View Model Definition
	private Text viewModelFileText;
	private Text viewModelContainerText;
	private Text viewModelHelpText;
	
	private IConnectionInfoHelper connectionInfoHelper;
	
	private final WSDLImportWizardManager importManager;

	private ModelWorkspaceManager modelWorkspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();

	private boolean refreshing = false;

	public ImportOptionsPanel(Composite parent, WSDLImportWizardManager importManager) {
		super();
		this.importManager = importManager;
		this.importManager.addChangeListener(this);
		init(parent);
	}

	@SuppressWarnings("unused")
	private void init(Composite parent) {
		
		this.connectionInfoHelper = new ConnectionInfoHelper();
		
		SOURCE_MODEL_INFO : {
    		Group group = WidgetFactory.createGroup(parent, Messages.SourceModelDefinition, GridData.FILL_HORIZONTAL, 1, 3);
    		Label locationLabel = new Label(group, SWT.NULL);
    		locationLabel.setText(Messages.Location);
    
    		this.sourceModelContainerText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    		this.sourceModelContainerText.setLayoutData(gridData);
    		WidgetUtil.colorizeWidget(this.sourceModelContainerText, WidgetUtil.TEXT_COLOR_BLUE, true);
    		this.sourceModelContainerText.setEditable(false);
    
    		Button browseButton = new Button(group, SWT.PUSH);
    		gridData = new GridData();
    		
    		browseButton.setLayoutData(gridData);
    		browseButton.setText(Messages.BrowseElipsis);
    		browseButton.setToolTipText(NLS.bind(Messages.BrowseToSelectModelsLocation, Messages.Source_lower));
    		browseButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleSourceModelLocationBrowse();
    			}
    		});
    
    		Label fileLabel = new Label(group, SWT.NULL);
    		fileLabel.setText(Messages.Name);
    		fileLabel.setToolTipText(Messages.SourceNameTooltip);
    
    		this.sourceModelFileText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		gridData = new GridData(GridData.FILL_HORIZONTAL);
    		gridData.widthHint = 200;
    		this.sourceModelFileText.setLayoutData(gridData);
    		this.sourceModelFileText.setToolTipText(Messages.SourceNameTooltip);
    		WidgetUtil.colorizeWidget(this.sourceModelFileText, WidgetUtil.TEXT_COLOR_BLUE, false);
    		this.sourceModelFileText.addModifyListener(new ModifyListener() {
    			@Override
				public void modifyText(ModifyEvent e) {
    				// Check view file name for existing if "location" is already
    				// set
    				handleSourceModelTextChanged();
    			}
    		});
    
    		browseButton = new Button(group, SWT.PUSH);
    		gridData = new GridData();
    		browseButton.setLayoutData(gridData);
    		browseButton.setText(Messages.BrowseElipsis);
    		browseButton.setToolTipText(NLS.bind(Messages.BrowseToSelect_0_Model, Messages.Source_lower));
    		browseButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleSourceModelBrowse();
    			}
    		});
    		
    		SOURCE_HELP : {
        		Group helpGroup = WidgetFactory.createGroup(group,
    				Messages.Status, SWT.NONE | SWT.BORDER_DASH, 3);
        		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        		gd.horizontalSpan = 3;
        		helpGroup.setLayoutData(gd);
        
    
    			sourceModelHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
    			WidgetUtil.colorizeWidget(this.sourceModelHelpText, WidgetUtil.TEXT_COLOR_BLUE, true);
    			gd = new GridData(GridData.FILL_BOTH);
    			gd.heightHint = 40;
    			gd.widthHint = 600;
    			sourceModelHelpText.setLayoutData(gd);
    		}
		
		}

		VIEW_MODEL_INFO : {
			Group group = WidgetFactory.createGroup(parent, Messages.ViewModelDefinition, GridData.FILL_HORIZONTAL, 1, 3);
    		Label locationLabel = new Label(group, SWT.NULL);
    		locationLabel.setText(Messages.Location);
    
    		this.viewModelContainerText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    		this.viewModelContainerText.setLayoutData(gridData);
    		WidgetUtil.colorizeWidget(this.viewModelContainerText, WidgetUtil.TEXT_COLOR_BLUE, true);
    		this.viewModelContainerText.setEditable(false);
    
    		Button browseButton = new Button(group, SWT.PUSH);
    		gridData = new GridData();
    		// buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
    		browseButton.setLayoutData(gridData);
    		browseButton.setText(Messages.BrowseElipsis);
    		browseButton.setToolTipText(NLS.bind(Messages.BrowseToSelectModelsLocation, Messages.Source_lower));
    		browseButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleViewModelLocationBrowse();
    			}
    		});
			
			
    		Label fileLabel = new Label(group, SWT.NULL);
    		fileLabel.setText(Messages.Name);
    		fileLabel.setToolTipText(Messages.ViewNameTooltip);
    
    		this.viewModelFileText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		gridData = new GridData(GridData.FILL_HORIZONTAL);
    		this.viewModelFileText.setLayoutData(gridData);
    		this.viewModelFileText.setToolTipText(Messages.ViewNameTooltip);
    		WidgetUtil.colorizeWidget(this.viewModelFileText, WidgetUtil.TEXT_COLOR_BLUE, false);
    		this.viewModelFileText.setForeground(WidgetUtil.getDarkBlueColor());
    		this.viewModelFileText.addModifyListener(new ModifyListener() {
    			@Override
				public void modifyText(ModifyEvent e) {
    				// Check view file name for existing if "location" is already
    				// set
    				handleViewModelTextChanged();
    			}
    		});
    
    		browseButton = new Button(group, SWT.PUSH);
    		gridData = new GridData();
    		browseButton.setLayoutData(gridData);
    		browseButton.setText(Messages.BrowseElipsis);
    		browseButton.setToolTipText(NLS.bind(Messages.BrowseToSelect_0_Model, Messages.View_lower));
    		browseButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleViewModelBrowse();
    			}
    		});
    		
    		VIEW_HELP : {
        		Group helpGroup = WidgetFactory.createGroup(group,
    				Messages.Status, SWT.NONE | SWT.BORDER_DASH, 3); 
        		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        		gd.horizontalSpan = 3;
        		helpGroup.setLayoutData(gd);
        
    
    			viewModelHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
    			WidgetUtil.colorizeWidget(this.viewModelHelpText, WidgetUtil.TEXT_COLOR_BLUE, true);
    			gd = new GridData(GridData.FILL_BOTH);
    			gd.heightHint = 40;
    			gd.widthHint = 600;
    			viewModelHelpText.setLayoutData(gd);
    		}
		}

	}

	public void setVisible() {
		// Set field values from import manager\
		refreshing = true;

		refreshUiFromManager();
		
		refreshing = false;
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleSourceModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
			new ModelingResourceFilter(), new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			// viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.importManager.setSourceModelLocation(folder);
			this.sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
		}

		notifyChanged();
	}
	
	void handleViewModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
			new ModelingResourceFilter(), new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			// viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.importManager.setViewModelLocation(folder);
			this.viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
		}

		notifyChanged();
	}

	void handleSourceModelBrowse() {
		IFile modelFile = MetamodelSelectionUtilities.selectSourceModelInWorkspace();

		if( modelFile != null ) {
			IContainer folder = modelFile.getParent();
			String modelName = modelFile.getFullPath().lastSegment();
			this.importManager.setSourceModelExists(true);
			this.importManager.setSourceModelLocation(folder);
			this.importManager.setSourceModelName(modelName);
			this.sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.sourceModelFileText.setText(modelName);
		}

		notifyChanged();
	}

	void handleViewModelBrowse() {
		IFile modelFile = MetamodelSelectionUtilities.selectViewModelInWorkspace();

		if( modelFile != null ) {
			IContainer folder = modelFile.getParent();
			String modelName = modelFile.getFullPath().lastSegment();
			this.importManager.setViewModelExists(true);
			this.importManager.setViewModelLocation(folder);
			this.importManager.setViewModelName(modelName);
			this.viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.viewModelFileText.setText(modelName);
		}

		notifyChanged();
	}

	void handleViewModelTextChanged() {
		if( refreshing ) return;
		
		String newName = ""; //$NON-NLS-1$
		if (this.viewModelFileText.getText() != null) {
			if (this.viewModelFileText.getText().length() == 0) {
				this.importManager.setViewModelName(newName);
				this.importManager.setViewModelExists(false);
			} else {
				newName = this.viewModelFileText.getText();
				this.importManager.setViewModelName(newName);
				this.importManager.setViewModelExists(viewModelExists());
			}

		}

		notifyChanged();
	}

	void handleSourceModelTextChanged() {
		if( refreshing ) return;
		
		String newName = ""; //$NON-NLS-1$
		if (this.sourceModelFileText.getText() != null) {
			if (this.sourceModelFileText.getText().length() == 0) {
				this.importManager.setSourceModelName(newName);
				this.importManager.setSourceModelExists(false);
			} else {
				newName = this.sourceModelFileText.getText();
				this.importManager.setSourceModelName(newName);
				this.importManager.setSourceModelExists(sourceModelExists());
			}

		}

		notifyChanged();
	}

	private boolean viewModelExists() {
		if (this.importManager.getViewModelLocation() == null) {
			return false;
		}

		return modelWorkspaceManager.modelExists(importManager.getViewModelLocation().getFullPath().toOSString(),
			this.viewModelFileText.getText());
	}

	private boolean sourceModelExists() {
		if (this.importManager.getSourceModelLocation() == null) {
			return false;
		}

		return modelWorkspaceManager.modelExists(importManager.getSourceModelLocation().getFullPath().toOSString(),
			this.sourceModelFileText.getText());
	}

	public void refreshUiFromManager() {
		this.refreshing = true;
		IContainer sourceLocation = this.importManager.getSourceModelLocation();
		if( sourceLocation != null ) {
			this.sourceModelContainerText.setText(sourceLocation.getFullPath().makeRelative().toString());
		}
		if( this.importManager.getSourceModelName() != null ) {
		    if (!this.sourceModelFileText.getText().equals(this.importManager.getSourceModelName())) {
		        this.sourceModelFileText.setText(this.importManager.getSourceModelName());
		    }
		}
		
		IContainer viewLocation = this.importManager.getViewModelLocation();
		if( viewLocation != null ) {
			this.viewModelContainerText.setText(viewLocation.getFullPath().makeRelative().toString());
		}
		if( this.importManager.getViewModelName() != null ) {
            if (!this.viewModelFileText.getText().equals(this.importManager.getViewModelName())) {
                this.viewModelFileText.setText(this.importManager.getViewModelName());
            }
		}
		
//		// source model info is complete so go ahead and set message for user
		setSourceModelHelpMessage();
		
		// source model info is complete so go ahead and set message for user
		setViewModelHelpMessage();
		
		updateDesignerProperties();
		
		this.refreshing = false;
	}

	private void setSourceModelHelpMessage() {
		String message = NLS.bind(Messages.Status_SourceModelDoesNotExistAndWillBeCreated, this.sourceModelFileText.getText());
		if (sourceModelExists()) {
            if (!sourceModelHasConnectionProfile()) {
                message = NLS.bind(Messages.Status_ExistingSourceModelHasNoProfile, this.sourceModelFileText.getText());
            } else if (!sourceModelHasSameConnectionProfile()) {
				message = NLS.bind(Messages.Status_ExistingSourceModelHasWrongProfile, this.sourceModelFileText.getText());
			} else {
    			if (sourceHasProcedure()) {
    				message = NLS.bind(Messages.Status_ExistingSourceModelAlreadyContainsInvoke, this.sourceModelFileText.getText());
    			} else {
    				message = NLS.bind(Messages.Status_ExistingSourceModelMissingInvoke, this.sourceModelFileText.getText());
    			}
			}
		} else if (this.importManager.getSourceModelName() == null || this.importManager.getSourceModelName().length() == 0) {
			message = Messages.Status_SourceModelNameCannotBeNullOrEmpty;
		}
		
		this.sourceModelHelpText.setText(message);
	}
	
	private void setViewModelHelpMessage() {
		String message = NLS.bind(Messages.Status_ViewModelDoesNotExistAndWillBeCreated, this.viewModelFileText.getText());
		if (viewModelExists()) {
				message = NLS.bind(Messages.Statis_ViewModelAlreadyExists, this.viewModelFileText.getText());
		} else {
			if (this.importManager.getViewModelName() == null || this.importManager.getViewModelName().length() == 0) {
				message = Messages.Status_ViewModelNameCannotBeNullOrEmpty;
			}
		}
		
		this.viewModelHelpText.setText(message);
	}
	
	private boolean sourceHasProcedure() {
		if ( !sourceModelExists() ) {
			return false;
		}

		try {
			IResource sourceModel = ModelUtilities.getModelFile(
				this.importManager.getSourceModelLocation().getFullPath().toOSString(), this.sourceModelFileText.getText());

			ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile) sourceModel, false);
			if (smr != null) {
				return FlatFileRelationalModelFactory.procedureExists(smr, FlatFileRelationalModelFactory.INVOKE);
			}
		} catch (ModelWorkspaceException ex) {
			ModelGeneratorWsdlUiConstants.UTIL.log(IStatus.ERROR, ex, NLS.bind(Messages.Error_DeterminingSourceModelHas_0_Procedure, FlatFileRelationalModelFactory.INVOKE));
		}

		return false;
	}
	
    /*
     * Determine if the Source Model has ConnectionProfile set.
     */
    private boolean sourceModelHasConnectionProfile() {
        if (!sourceModelExists()) {
            return false;
        }

        try {
            IResource sourceModel = ModelUtilities.getModelFile(this.importManager.getSourceModelLocation().getFullPath().toOSString(),
                                                                          this.sourceModelFileText.getText());

            ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile)sourceModel, false);
            if (smr != null) {
                IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
                if (profile == null || this.importManager.getConnectionProfile() == null) {
                    return false;
                }
                return true;
            }
        } catch (ModelWorkspaceException ex) {
            ModelGeneratorWsdlUiConstants.UTIL.log(IStatus.ERROR, ex, Messages.Error_DeterminingSourceModelHasProfile);
        }

        return false;
    }

    private boolean sourceModelHasSameConnectionProfile() {
		if ( !sourceModelExists() ) {
			return false;
		}

		try {
			IResource sourceModel = ModelUtilities.getModelFile(
				this.importManager.getSourceModelLocation().getFullPath().toOSString(), this.sourceModelFileText.getText());

			ModelResource smr = ModelUtilities.getModelResourceForIFile((IFile) sourceModel, false);
			if (smr != null) {
				IConnectionProfile profile = connectionInfoHelper.getConnectionProfile(smr);
				if (profile == null || this.importManager.getConnectionProfile() == null) {
					return false;
				}

				if (profile.getName().equalsIgnoreCase(this.importManager.getConnectionProfile().getName())) {
					return true;
				}			}
		} catch (ModelWorkspaceException ex) {
			ModelGeneratorWsdlUiConstants.UTIL.log(IStatus.ERROR, ex, Messages.Error_DeterminingSourceModelHasMatchingProfile);
		}

		return false;
	}
	
    private void updateDesignerProperties() {
        Properties designerProperties = this.importManager.getDesignerProperties();
        if (designerProperties != null) {
            if (this.sourceModelFileText.getText() != null) {
                DesignerPropertiesUtil.setSourceModelName(designerProperties, this.sourceModelFileText.getText());
            }
            if (this.importManager.getSourceModelLocation() != null) {
                DesignerPropertiesUtil.setProjectName(designerProperties,
                                                      this.importManager.getSourceModelLocation().getProject().getName());
            }
            if (this.viewModelFileText.getText() != null) {
                DesignerPropertiesUtil.setViewModelName(designerProperties, this.viewModelFileText.getText());
            }
        }
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
	 */
	@Override
	public void stateChanged(IChangeNotifier theSource) {
		refreshUiFromManager();
	}
    
	public void notifyChanged() {
		this.importManager.notifyChanged();
	}
}

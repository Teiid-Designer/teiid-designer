/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.osgi.util.NLS;
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
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.MetamodelSelectionUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;
import com.metamatrix.modeler.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class ImportOptionsPanel implements ModelGeneratorWsdlUiConstants {
	// Source Model Definition
	private Text sourceModelFileText;
	private Text sourceModelContainerText;
	private Text sourceModelHelpText;
	
	// View Model Definition
	private Text viewModelFileText;
	private Text viewModelContainerText;
	private Text viewModelHelpText;
	
	IConnectionInfoHelper connectionInfoHelper;
	
	final WSDLImportWizardManager importManager;
	
	IStatus currentStatus;

	public ImportOptionsPanel(Composite parent, WSDLImportWizardManager importManager) {
		super();
		this.importManager = importManager;
		init(parent);
	}

	@SuppressWarnings("unused")
	private void init(Composite parent) {
		
		this.connectionInfoHelper = new ConnectionInfoHelper();
		
		SOURCE_MODEL_INFO : {
    		Group group = WidgetFactory.createGroup(parent, Messages.SourceModelDefinition, GridData.FILL_HORIZONTAL, 1);
    
    		group.setLayout(new GridLayout(3, false));
    		
    		Label locationLabel = new Label(group, SWT.NULL);
    		locationLabel.setText(Messages.Location);
    
    		this.sourceModelContainerText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    		this.sourceModelContainerText.setLayoutData(gridData);
    		this.sourceModelContainerText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    		this.sourceModelContainerText.setForeground(WidgetUtil.getDarkBlueColor());
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
    		//this.sourceModelFileText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    		this.sourceModelFileText.setForeground(WidgetUtil.getDarkBlueColor());
    		this.sourceModelFileText.addModifyListener(new ModifyListener() {
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
        		helpGroup.setLayout(new GridLayout(1, false));
        		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        		gd.horizontalSpan = 3;
        		helpGroup.setLayoutData(gd);
        
    
    			sourceModelHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
    			sourceModelHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    			sourceModelHelpText.setForeground(WidgetUtil.getDarkBlueColor());
    			gd = new GridData(GridData.FILL_BOTH);
    			gd.heightHint = 40;
    			gd.widthHint = 600;
    			sourceModelHelpText.setLayoutData(gd);
    		}
		
		};

		VIEW_MODEL_INFO : {
			Group group = WidgetFactory.createGroup(parent, Messages.ViewModelDefinition, GridData.FILL_HORIZONTAL, 1);
			
    		group.setLayout(new GridLayout(3, false));
    		
    		Label locationLabel = new Label(group, SWT.NULL);
    		locationLabel.setText(Messages.Location);
    
    		this.viewModelContainerText = new Text(group, SWT.BORDER | SWT.SINGLE);
    		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    		this.viewModelContainerText.setLayoutData(gridData);
    		this.viewModelContainerText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    		this.viewModelContainerText.setForeground(WidgetUtil.getDarkBlueColor());
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
    		//this.viewModelFileText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    		this.viewModelFileText.setForeground(WidgetUtil.getDarkBlueColor());
    		this.viewModelFileText.addModifyListener(new ModifyListener() {
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
        		helpGroup.setLayout(new GridLayout(1, false));
        		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        		gd.horizontalSpan = 3;
        		helpGroup.setLayoutData(gd);;
        
    
    			viewModelHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
    			viewModelHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
    			viewModelHelpText.setForeground(WidgetUtil.getDarkBlueColor());
    			gd = new GridData(GridData.FILL_BOTH);
    			gd.heightHint = 40;
    			gd.widthHint = 600;
    			viewModelHelpText.setLayoutData(gd);
    		}
		}

	}

	public void setVisible() {
		// Set field values from import manager\

		IContainer sourceLocation = this.importManager.getSourceModelLocation();
		if( sourceLocation != null ) {
			this.sourceModelContainerText.setText(sourceLocation.getFullPath().makeRelative().toString());
		}
		if( this.importManager.getSourceModelName() != null ) {
			this.sourceModelFileText.setText(this.importManager.getSourceModelName());
		}
		
		IContainer viewLocation = this.importManager.getViewModelLocation();
		if( viewLocation != null ) {
			this.viewModelContainerText.setText(viewLocation.getFullPath().makeRelative().toString());
		}
		if( this.importManager.getViewModelName() != null ) {
			this.viewModelFileText.setText(this.importManager.getViewModelName());
		}
		
		validate();
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleSourceModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(ResourcesPlugin.getWorkspace().getRoot(),
			new ModelingResourceFilter(), new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			// viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.importManager.setSourceModelLocation(folder);
			this.sourceModelContainerText.setText(folder.getFullPath().makeRelative().toString());
		}

		validate();
	}
	
	void handleViewModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(ResourcesPlugin.getWorkspace().getRoot(),
			new ModelingResourceFilter(), new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			// viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
			this.importManager.setViewModelLocation(folder);
			this.viewModelContainerText.setText(folder.getFullPath().makeRelative().toString());
		}

		validate();
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

		validate();
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

		validate();
	}

	void handleViewModelTextChanged() {

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

		validate();
	}

	void handleSourceModelTextChanged() {

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

		validate();
	}

	private boolean viewModelExists() {
		if (this.importManager.getViewModelLocation() == null) {
			return false;
		}

		return ModelGeneratorWsdlUiUtil.modelExists(importManager.getViewModelLocation().getFullPath().toOSString(),
			this.viewModelFileText.getText());
	}

	private boolean sourceModelExists() {
		if (this.importManager.getSourceModelLocation() == null) {
			return false;
		}

		return ModelGeneratorWsdlUiUtil.modelExists(importManager.getSourceModelLocation().getFullPath().toOSString(),
			this.sourceModelFileText.getText());
	}

	public void validate() {
		// Check Source Model info
		// 1) Location is NOT NULL
		// 2) Model Name 
		//     - NOT NULL
		//     - VALID NAME
		if (CoreStringUtil.isEmpty(this.sourceModelContainerText.getText()) ) {
			setCurrentStatus(IStatus.ERROR, Messages.Status_SourceModelLocationUndefined);
			return;
		}
		
		if (CoreStringUtil.isEmpty(this.sourceModelFileText.getText()) ) {
			setCurrentStatus(IStatus.ERROR, Messages.Status_SourceModelNameUndefined);
			return;
		}
		
		String fileNameMessage = ModelUtilities.validateModelName(this.sourceModelFileText.getText(), ".xmi"); //$NON-NLS-1$
		if (fileNameMessage != null) {
			setCurrentStatus(IStatus.ERROR, fileNameMessage);
			return;
		}
		
		// source model info is complete so go ahead and set message for user
		setSourceModelHelpMessage();
		
		// Check View Model info
		// 1) Location is NOT NULL
		// 2) Model Name 
		//     - NOT NULL
		//     - VALID NAME
		
		if (CoreStringUtil.isEmpty(this.sourceModelContainerText.getText()) ) {
			setCurrentStatus(IStatus.ERROR, Messages.Status_ViewModelLocationUndefined);
			return;
		}
		
		if (CoreStringUtil.isEmpty(this.sourceModelFileText.getText()) ) {
			setCurrentStatus(IStatus.ERROR, Messages.Status_ViewModelNameUndefined);
			return;
		}
		
		fileNameMessage = ModelUtilities.validateModelName(this.sourceModelFileText.getText(), ".xmi"); //$NON-NLS-1$
		if (fileNameMessage != null) {
			setCurrentStatus(IStatus.ERROR, fileNameMessage);
			return;
		}
		
		// source model info is complete so go ahead and set message for user
		setViewModelHelpMessage();
		if( this.importManager.doGenerateDefaultProcedures() ) {
			setCurrentStatus(IStatus.OK, Messages.Status_AllOkClickFinishToGenerateProcedures);
		} else {
			setCurrentStatus(IStatus.OK, Messages.Status_AllOkClickNextToDefineProcedures);
		}
		
		updateDesignerProperties();
	}
	
	public void setCurrentStatus(int severity, String message) {
		this.currentStatus =  new Status(severity, PLUGIN_ID, message);
	}
	
	public IStatus getCurrentStatus() {
		return this.currentStatus;
	}
	
	private void setSourceModelHelpMessage() {
		String message = NLS.bind(Messages.Status_SourceModelDoesNotExistAndWillBeCreated, this.sourceModelFileText.getText());
		if (sourceModelExists()) {
			if( ! sourceModelHasSameConnectionProfile() ) {
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
			IResource sourceModel = ModelGeneratorWsdlUiUtil.getModelFile(
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
	
	private boolean sourceModelHasSameConnectionProfile() {
		if ( !sourceModelExists() ) {
			return false;
		}

		try {
			IResource sourceModel = ModelGeneratorWsdlUiUtil.getModelFile(
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
    	if( this.currentStatus.isOK() ) {
    		if( this.sourceModelFileText.getText() != null ) {
    			this.importManager.setDesignerProperty(IPropertiesContext.KEY_LAST_SOURCE_MODEL_NAME, this.sourceModelFileText.getText());
    		}
    		if( this.importManager.getSourceModelLocation() != null ) {
    			this.importManager.setDesignerProperty(IPropertiesContext.KEY_PROJECT_NAME, this.importManager.getSourceModelLocation().getProject().getName());
    		}
    		if( this.viewModelFileText.getText() != null ) {
    			this.importManager.setDesignerProperty(IPropertiesContext.KEY_LAST_VIEW_MODEL_NAME, this.viewModelFileText.getText());
    		}
    	}
    }
}

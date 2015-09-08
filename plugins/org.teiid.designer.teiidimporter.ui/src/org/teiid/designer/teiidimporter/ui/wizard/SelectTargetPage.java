package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

/**
 * SelectTargetPage 
 * TeiidImportWizard page - for selection of the target model
 * 
 * @since 8.5
 */
public class SelectTargetPage extends AbstractWizardPage implements UiConstants {

    private static final String UNKNOWN = "Unknown"; //$NON-NLS-1$
        
    private TeiidImportManager importManager;

    private Text targetModelContainerText;
    private Text targetModelFileText;
    private Text targetModelInfoText;
    private Text timeoutText;
    private Button filterRedundantUCsCB;
    private Button createConnProfileCB;
    private StyledTextEditor vdbTextEditor;

    /**
     * SelectedTranslatorAndTargetPage Constructor
     * @param importManager the TeiidImportManager for the wizard
     */
    public SelectTargetPage( TeiidImportManager importManager ) {
        super(SelectTargetPage.class.getSimpleName(), Messages.SelectTargetPage_title); 
        this.importManager = importManager;
    }

    @Override
    public void createControl( Composite theParent ) {
        final Composite basePanel = new Composite(theParent, SWT.NONE);
        basePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        basePanel.setLayout(new GridLayout(1, false));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(basePanel, SWT.H_SCROLL | SWT.V_SCROLL);
    	scrolledComposite.setExpandHorizontal(true);
    	scrolledComposite.setExpandVertical(true);
        GridLayoutFactory.fillDefaults().equalWidth(false).applyTo(scrolledComposite);
        GridDataFactory.fillDefaults().grab(true,  false);

        final Composite pnl = scrolledComposite.getPanel();
        pnl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pnl.setLayout(new GridLayout(1, false));

        setControl(pnl);

        Label serverNameLabel = new Label(pnl,SWT.NONE);
        String serverString;
        try {
            serverString = importManager.getDisplayName();
        } catch (Exception ex) {
            serverString = UNKNOWN;
        }
        serverNameLabel.setText(Messages.SelectTargetPage_defaultServerPrefix + StringConstants.SPACE + serverString);
        serverNameLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        Font bannerFont = JFaceResources.getBannerFont();
        serverNameLabel.setFont(bannerFont);

        // TabFolder - one tab contains Target Model info, other tab contains VDB deployment info
		createTabPanel(pnl);
		
        scrolledComposite.sizeScrolledPanel();
        
        setControl(basePanel);
        
        // Validate the page
        validatePage();
    }

    /**
     * Create the tab panel containing the Target Model Definition tab and VDB Details tab
     * @param parent the parent composite
     */
    private void createTabPanel(Composite parent) {
        final TabFolder tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER | SWT.NO_SCROLL);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(tabFolder);
        
        // The Target Model Definition tab
		Composite modelDefnPanel = createTargetModelDefnPanel(tabFolder);
        TabItem modelDefinitionTab = new TabItem(tabFolder, SWT.NONE);
        modelDefinitionTab.setControl(modelDefnPanel);
        modelDefinitionTab.setText(Messages.SelectTargetPage_TgtModelDefnTab);
		
		// The VDB details tab
        Composite vdbDetailsPanel = createVdbDetailsPanel(tabFolder);
        TabItem vdbTab = new TabItem(tabFolder, SWT.NONE);
        vdbTab.setControl(vdbDetailsPanel);
        vdbTab.setText(Messages.SelectTargetPage_AdvancedTab);
    }
    
    private int getTimeoutPrefSecs() {
        return DqpPlugin.getInstance().getPreferences().getInt(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT);
    }

    private void setTimeoutPrefSecs(int timeoutSecs) {
        DqpPlugin.getInstance().getPreferences().putInt(PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC, timeoutSecs);
    }
    
    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            
            // Set source model location
            if( importManager.getTargetModelLocation() != null ) {
            	this.targetModelContainerText.setText(this.importManager.getTargetModelLocation().makeRelative().toString());
            }
            
            vdbTextEditor.setText(importManager.getDynamicVdbString());
            timeoutText.setText(Integer.toString(getTimeoutPrefSecs()));
            
            validatePage();
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }
    
    /*
     * Create Group for definition of the target relational model
     * @parent the parent Composite
     * @return the target model definition composite
     */
    private Composite createTargetModelDefnPanel(Composite parent) {
        new Label(parent,SWT.NULL);  // For spacing
        
        // -------------------------------------
        // Create the Model Definition Group
        // -------------------------------------
        Group modelDefnPanel = WidgetFactory.createGroup(parent, StringConstants.EMPTY_STRING, SWT.NONE, 1, 3);
        modelDefnPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData)modelDefnPanel.getLayoutData()).widthHint = 400;

        // -----------------------
        // Location controls
        // -----------------------
        // Location Label
        Label locationLabel = new Label(modelDefnPanel, SWT.NULL);
        locationLabel.setText(Messages.SelectTargetPage_Location);

        // Location Text box
        targetModelContainerText = new Text(modelDefnPanel, SWT.BORDER | SWT.SINGLE);
        String targetContainerText = null;
        if( this.importManager.getTargetModelLocation() != null ) {
        	targetContainerText = this.importManager.getTargetModelLocation().toOSString();
        }
        if(targetContainerText!=null) targetModelContainerText.setText(targetContainerText);
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        targetModelContainerText.setLayoutData(gridData);
        targetModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        targetModelContainerText.setEditable(false);

        // Browse button for Location Definition
        Button browseButton = new Button(modelDefnPanel, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(Messages.SelectTargetPage_Browse);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleTargetModelLocationBrowse();
            }
        });

        // -----------------------
        // Model Name controls
        // -----------------------
        // Name label
        Label fileLabel = new Label(modelDefnPanel, SWT.NULL);
        fileLabel.setText(Messages.SelectTargetPage_Name); 

        // Name Text box
        targetModelFileText = new Text(modelDefnPanel, SWT.BORDER | SWT.SINGLE);
        
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        targetModelFileText.setLayoutData(gridData);
        targetModelFileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                handleTargetModelTextChanged();
            }
        });
        
        new Label(modelDefnPanel, SWT.NONE);  // For spacing
        new Label(modelDefnPanel, SWT.NONE);  // For spacing
        
        // Info area - shows model selection status
        Group infoGroup = WidgetFactory.createGroup(modelDefnPanel, Messages.SelectTargetPage_ModelStatus, SWT.NONE | SWT.BORDER_DASH,2);
        infoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // Text box for info message
        targetModelInfoText = new Text(infoGroup, SWT.WRAP | SWT.READ_ONLY);
        setTargetModelInfoText();
        targetModelInfoText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        targetModelInfoText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 35;
        gd.horizontalSpan=3;
        targetModelInfoText.setLayoutData(gd);
        
        // CheckBox for FilterRedundantUniqueConstraints DDL Import property
        filterRedundantUCsCB = WidgetFactory.createCheckBox(modelDefnPanel, Messages.SelectTargetPage_FilterRedundantUCsCB_Label, SWT.NONE, 3);
        filterRedundantUCsCB.setToolTipText(Messages.SelectTargetPage_FilterRedundantUCsCB_ToolTip);
        filterRedundantUCsCB.setSelection(true);
        this.importManager.setDdlImportOptionFilterConstraints(true);
        // Toggling the checkbox toggles the import property
        filterRedundantUCsCB.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                importManager.setDdlImportOptionFilterConstraints(filterRedundantUCsCB.getSelection());
            }
        });
        
        // CheckBox for Connection Profile - defaults to checked
        createConnProfileCB = WidgetFactory.createCheckBox(modelDefnPanel, Messages.SelectTargetPage_CreateConnectionProfileCB_Label, SWT.NONE, 3);
        createConnProfileCB.setToolTipText(Messages.SelectTargetPage_CreateConnectionProfileCB_ToolTip);
        createConnProfileCB.setSelection(true);

        return modelDefnPanel;
    }
    
    /*
     * Create panel containing the import VDB details (content and deployment timeout)
     * @parent the parent Composite
     * @return the vdb details composite
     */
    private Composite createVdbDetailsPanel(Composite parent) {
    	// Overall panel containing VDB Details
        final Composite vdbPanel = new Composite(parent, SWT.NONE);
        vdbPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        vdbPanel.setLayout(new GridLayout(2, false));
        
        // VDB Deployment timeout preference
        Label timeoutLabel = new Label(vdbPanel,SWT.NULL);
        timeoutLabel.setText(Messages.SelectTargetPage_TimeoutLabelText);
        
        timeoutText = new Text(vdbPanel,SWT.BORDER | SWT.SINGLE);
        GridDataFactory.fillDefaults().minSize(40,SWT.DEFAULT).applyTo(timeoutText);
        timeoutText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                validatePage();
            }
        });
        timeoutLabel.setToolTipText(Messages.SelectTargetPage_TimeoutTooltip);
        timeoutText.setToolTipText(Messages.SelectTargetPage_TimeoutTooltip);
                
        // Dynamic VDB content
        Group vdbContentGroup = WidgetFactory.createGroup(vdbPanel, Messages.SelectTargetPage_dynamic_vdb_text, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        vdbContentGroup.setLayoutData(gd);
        vdbContentGroup.setToolTipText(Messages.SelectTargetPage_dynamic_vdb_tooltip);
        
        Composite innerPanel = new Composite(vdbContentGroup, SWT.NONE);
        innerPanel.setLayout(new GridLayout());
        GridData pgd = new GridData(GridData.FILL_BOTH);
        pgd.minimumWidth = 400;
        pgd.minimumHeight = 400;
        pgd.grabExcessVerticalSpace = true;
        pgd.grabExcessHorizontalSpace = true;
        innerPanel.setLayoutData(pgd);
        
        vdbTextEditor = new StyledTextEditor(innerPanel, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        GridData gdt = new GridData(GridData.FILL_BOTH);
        gdt.widthHint = 400;
        gdt.heightHint = 400;
        vdbTextEditor.setLayoutData(gdt);
        vdbTextEditor.setEditable(false);
        vdbTextEditor.setAllowFind(false);
        vdbTextEditor.getTextWidget().setWordWrap(false);
        vdbTextEditor.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        vdbTextEditor.getTextWidget().setToolTipText(Messages.SelectTargetPage_dynamic_vdb_tooltip);
        
        return vdbPanel;
    }
    
    /**
     * Handler for choosing the Target Model Location
     */
    void handleTargetModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());
        if( folder == null ) {
        	return; // do nothing
        }

        if (targetModelContainerText != null) {
            this.importManager.setTargetModelLocation(folder.getFullPath().makeRelative());
            this.targetModelContainerText.setText(this.importManager.getTargetModelLocation().makeRelative().toString());
        } else {
            this.importManager.setTargetModelLocation(new Path(StringConstants.EMPTY_STRING));
            this.targetModelContainerText.setText(StringConstants.EMPTY_STRING);
        }
        
        validatePage();
    }
    
    // Handler for ModelText changes
    void handleTargetModelTextChanged() {
    	String mdlText = this.targetModelFileText.getText();
        if( !CoreStringUtil.isEmpty(mdlText) && !matchingModelOfWrongTypeExists(mdlText) ) {
           this.importManager.setTargetModelName(this.targetModelFileText.getText());
        }

        validatePage();
    }
    
    /**
     * Check if the supplied modelName already exists, but is a different type
     * @param modelName the proposed model Name
     * @return 'true' if model of different type exists, otherwise 'false'
     */
    private boolean matchingModelOfWrongTypeExists(String modelName ) {
    	IPath modelLocation = importManager.getTargetModelLocation();
    	if(modelLocation==null) return false;
    	
    	IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
    	
        IResource containerResource = root.findMember(modelLocation);
        if(containerResource==null || !(containerResource instanceof IContainer) ) {
        	return false;
        }
        IContainer container = (IContainer)containerResource;
        if(!container.exists()) return false;
        
        IPath modelPath = container.getFullPath().append(modelName);
        if (!modelName.endsWith(ModelerCore.MODEL_FILE_EXTENSION)) modelPath = modelPath.addFileExtension(ModelerCore.MODEL_FILE_EXTENSION.substring(1));

        IResource modelResource = root.findMember(modelPath);
        if( modelResource==null || !ModelUtil.isModelFile(modelResource) ) {
        	return false;
        }
        
        // See if model is non-relational
        if (!RelationalPackage.eNS_URI.equals(ModelUtil.getXmiHeader(modelResource).getPrimaryMetamodelURI())) {
        	return true;
        }
        return false;
    }
    
    /*
     * Page Validation
     * @return 'true' if the page is valid, 'false' if not
     */
    private boolean validatePage() {
        
        //---------------------------------------------------
        // Timeout preference value - validation
        //---------------------------------------------------
        boolean timeoutTextValid = validateTimeoutText();
        if(!timeoutTextValid) return false;

        //---------------------------------------------------
        // Target Model Section - validation
        //---------------------------------------------------
        boolean modelSelectionValid = validateTargetModelSelection();
        if(!modelSelectionValid) return false;

        setThisPageComplete(StringConstants.EMPTY_STRING, NONE);
        return true;
    }

    /*
     * This method validates the target model section of this page
     * @return 'true' if the target model section is valid, 'false' if not.
     */
    private boolean validateTargetModelSelection() {
        // Sets info about the current model selection
        setTargetModelInfoText();
        
        // Check for at least ONE open non-hidden Model Project
        Collection<IProject> openModelProjects = DotProjectUtils.getOpenModelProjects();

        // No open projects
        if (openModelProjects.size() == 0) {
            setThisPageComplete(Messages.SelectTargetPage_NoOpenProjMsg, ERROR);
            return false;
        } 

        // Check for a selected container
        String container = targetModelContainerText.getText();
        if (CoreStringUtil.isEmpty(container)) {
            setThisPageComplete(Messages.SelectTargetPage_SrcLocationNotSpecified, ERROR); 
            return false;
        }   
        
        // Check for the target project
        IProject project = getTargetProject();
        if (project == null) {
            setThisPageComplete(Messages.SelectTargetPage_SrcLocationNotSpecified, ERROR);
            return false;
        }
        
        // Validate the target model name
        String fileText = targetModelFileText.getText().trim();
        if(StringUtilities.isEmpty(fileText)) {
            setThisPageComplete(Messages.SelectTargetPage_EnterModelNameMsg, ERROR);
            return false;
        }
        
        IStatus status = ModelNameUtil.validate(fileText, ModelerCore.MODEL_FILE_EXTENSION, null,
                ModelNameUtil.IGNORE_CASE );
        if( status.getSeverity() == IStatus.ERROR ) {
            setThisPageComplete(ModelNameUtil.MESSAGES.INVALID_MODEL_NAME + status.getMessage(), ERROR);
            return false;
        }
        
        // Valid target model - now compare it's connection profile vs the selected profile
        if( importManager.targetModelExists() ) {
            setThisPageComplete(NLS.bind(Messages.SelectTargetPage_ModelExistsWithThisNameMsg, fileText), ERROR);
            return false;
        }

        return true;
    }
    
    /*
     * This method validates the timeout textbox.  If valid, the preference is updated.
     * @return 'true' if the timeout textbox is valid, 'false' if not.
     */
    private boolean validateTimeoutText() {
    	// Make sure the timeout text entered is parsable
    	String timeoutValue = timeoutText.getText();
    	try {
    		// Check for empty value
    		if(StringUtilities.isEmpty(timeoutValue)) {
	            setThisPageComplete(Messages.SelectTargetPage_TimeoutEmptyMsg, ERROR);
	    		return false;
    		}
    		
			int timeoutInt = Integer.parseInt(timeoutValue.trim());
			
	    	// Make sure the timeout is not less than min value
	    	if(timeoutInt < PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_MIN) {
	            setThisPageComplete(NLS.bind(Messages.SelectTargetPage_TimeoutLessThanMinAllowedMsg, timeoutValue, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_MIN), ERROR);
	    		return false;
	    	}
	    	
	    	// Make sure the timeout is not greater than max value
	    	if(timeoutInt > PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_MAX) {
	            setThisPageComplete(NLS.bind(Messages.SelectTargetPage_TimeoutGreaterThanMaxAllowedMsg, timeoutValue, PreferenceConstants.TEIID_IMPORTER_TIMEOUT_SEC_MAX), ERROR);
	    		return false;
	    	}
	    	
	    	// If valid, update the preference
	        setTimeoutPrefSecs(timeoutInt);
	    	
		} catch (NumberFormatException ex) {
            setThisPageComplete(NLS.bind(Messages.SelectTargetPage_TimeoutTextNotParsableMsg, timeoutValue), ERROR);
            return false;
		}
        return true;
    }
        
    private void setThisPageComplete( String message, int severity) {
        WizardUtil.setPageComplete(this, message, severity);
    }
    
    /*
     * Get workspace target Project
     * @return the target project
     */
    private IProject getTargetProject() {
        IProject result = null;
        String containerName = getTargetContainerName();

        if (!CoreStringUtil.isEmpty(containerName)) {
            IWorkspaceRoot root = ModelerCore.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(containerName));

            if (resource.exists()) {
                result = resource.getProject();
            }
        }
        return result;
    }
    
    /*
     * Get the Target Container name
     */
    private String getTargetContainerName() {
        String result = null;

        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            result = getHiddenProjectPath();
        } else {
            result = targetModelContainerText.getText().trim();
        }

        return result;
    }
    
    private String getHiddenProjectPath() {
        String result = null;
        IProject hiddenProj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false);

        if (hiddenProj != null) {
            result = hiddenProj.getFullPath().makeRelative().toString();
        }

        return result;
    }
    
    /*
     * Sets target model info section
     */
    private void setTargetModelInfoText() {
        String targetModelName = this.targetModelFileText.getText();
        if(targetModelName==null || targetModelName.trim().length()==0) {
            this.targetModelInfoText.setText(Messages.SelectTargetPage_SrcModelUndefined);
        } else {
            this.targetModelInfoText.setText(Messages.SelectTargetPage_SrcModelSelected+": "+targetModelName); //$NON-NLS-1$
        }
    }
    
    /**
     * Gets the Create Connection Profile status
     * @return 'true' if the create checkbox is checked, 'false' if not.
     */
    public boolean isCreateConnectionProfile() {
    	boolean isChecked = false;
    	if(this.createConnProfileCB != null) {
    		isChecked = this.createConnProfileCB.getSelection();
    	}
    	return isChecked;
    }
    
    /*
     * Filter for Model Selection
     */
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
                    if (element instanceof IProject ) {
		                try {
		                	doSelect = ((IProject)element).hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	UTIL.log(e);
		                }
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
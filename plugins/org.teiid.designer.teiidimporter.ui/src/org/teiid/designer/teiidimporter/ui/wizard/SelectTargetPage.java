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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.widget.Dialog;
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

    private static final String EMPTY_STR = ""; //$NON-NLS-1$
    private static final String SERVER_PREFIX = "Default Server: "; //$NON-NLS-1$

    private TeiidImportManager importManager;

    private Text targetModelContainerText;
    private Text targetModelFileText;
    private Text targetModelInfoText;
    private Button createConnProfileCB;
    private Button showVdbXMLButton;

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
        final Composite hostPanel = new Composite(theParent, SWT.NONE);
        hostPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hostPanel.setLayout(new GridLayout(1, false));
        
        // Create page            
        DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel, SWT.H_SCROLL | SWT.V_SCROLL);
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
            serverString = "Unknown"; //$NON-NLS-1$
        }
        serverNameLabel.setText(SERVER_PREFIX + serverString);
        serverNameLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        Font bannerFont = JFaceResources.getBannerFont();
        serverNameLabel.setFont(bannerFont);
                
        // Group for Selection of target Source Model
        createTargetModelGroup(pnl);
        
        showVdbXMLButton = new Button(pnl, SWT.PUSH);
        showVdbXMLButton.setText(Messages.ShowVdbXmlAction_text);
        showVdbXMLButton.setToolTipText(Messages.ShowVdbXmlAction_tooltip);
        showVdbXMLButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Launch a dialog to show vdb.xml text contents
				ShowVdbXmlDialog dialog = new ShowVdbXmlDialog(getShell(), importManager.getDynamicVdbString());
				dialog.open();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        
        scrolledComposite.sizeScrolledPanel();
        
        setControl(hostPanel);
        
        // Validate the page
        validatePage();
    }



    
    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            
            // Set source model location
            if( importManager.getTargetModelLocation() != null ) {
            	this.targetModelContainerText.setText(this.importManager.getTargetModelLocation().makeRelative().toString());
            }
            
            validatePage();
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }
    
    /*
     * Create Group for Definition of the target relational model
     * @parent the parent Composite
     */
    private void createTargetModelGroup(Composite parent) {
        new Label(parent,SWT.NULL);  // For spacing
        
        // -------------------------------------
        // Create the Model Definition Group
        // -------------------------------------
        Group sourceGroup = WidgetFactory.createGroup(parent, Messages.SelectTargetPage_TgtModelDefnGroup, SWT.NONE, 1, 3);
        sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData)sourceGroup.getLayoutData()).widthHint = 400;

        // -----------------------
        // Location controls
        // -----------------------
        // Location Label
        Label locationLabel = new Label(sourceGroup, SWT.NULL);
        locationLabel.setText(Messages.SelectTargetPage_Location);

        // Location Text box
        targetModelContainerText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
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
        Button browseButton = new Button(sourceGroup, SWT.PUSH);
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
        Label fileLabel = new Label(sourceGroup, SWT.NULL);
        fileLabel.setText(Messages.SelectTargetPage_Name); 

        // Name Text box
        targetModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
//        String targetModelName = this.importManager.getTargetModelName();
//        if(targetModelName!=null) targetModelFileText.setText(targetModelName);
        
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        targetModelFileText.setLayoutData(gridData);
        targetModelFileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                handleTargetModelTextChanged();
            }
        });
        
        new Label(sourceGroup, SWT.NONE);  // For spacing
        new Label(sourceGroup, SWT.NONE);  // For spacing
        
        // Info area - shows model selection status
        Group infoGroup = WidgetFactory.createGroup(sourceGroup, Messages.SelectTargetPage_ModelStatus, SWT.NONE | SWT.BORDER_DASH,2);
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
        
        // CheckBox for Connection Profile - defaults to checked
        createConnProfileCB = WidgetFactory.createCheckBox(sourceGroup, Messages.SelectTargetPage_CreateConnectionProfileCB_Label, SWT.NONE, 3);
        createConnProfileCB.setToolTipText(Messages.SelectTargetPage_CreateConnectionProfileCB_Label);
        createConnProfileCB.setSelection(true);

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

        if (folder != null && targetModelContainerText != null) {
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
        // Target Model Section - validation
        //---------------------------------------------------
        boolean modelSelectionValid = validateTargetModelSelection();
        if(!modelSelectionValid) return false;

        setThisPageComplete(EMPTY_STR, NONE);
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
        IStatus status = ModelNameUtil.validate(fileText, ModelerCore.MODEL_FILE_EXTENSION, null,
                ModelNameUtil.IGNORE_CASE );
        if( status.getSeverity() == IStatus.ERROR ) {
            setThisPageComplete(ModelNameUtil.MESSAGES.INVALID_MODEL_NAME + status.getMessage(), ERROR);
            return false;
        }
        
        // Valid target model - now compare it's connection profile vs the selected profile
        if( importManager.targetModelExists() ) {
            setThisPageComplete(Messages.bind(Messages.SelectTargetPage_ModelExistsWithThisNameMsg, fileText), ERROR);
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
    
    class ShowVdbXmlDialog extends Dialog {
        
        //============================================================================================================================
        // Variables
        
        private StyledTextEditor textEditor;
        
        private String theXmlText;


        //============================================================================================================================
        // Constructors
            
        /**<p>
         * </p>
         * @param shell the shell
         * @param theXmlText the xml text
         * @since 4.0
         */
        public ShowVdbXmlDialog(final Shell shell, final String theXmlText) {
            super(shell, Messages.ShowVdbXmlDialog_title);
            this.theXmlText = theXmlText;
        }
        
        //============================================================================================================================
        // Overridden Methods

        /**<p>
         * </p>
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         * @since 4.0
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            final Composite dlgPanel = (Composite)super.createDialogArea(parent);
            
            Group descGroup = WidgetFactory.createGroup(dlgPanel, Messages.ShowVdbXmlDialog_dynamic_vdb_text, SWT.NONE);
            descGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            Composite innerPanel = new Composite(descGroup, SWT.NONE);
            innerPanel.setLayout(new GridLayout());
            GridData pgd = new GridData(GridData.FILL_BOTH);
            pgd.minimumWidth = 400;
            pgd.minimumHeight = 400;
            pgd.grabExcessVerticalSpace = true;
            pgd.grabExcessHorizontalSpace = true;
            innerPanel.setLayoutData(pgd);
            
            this.textEditor = new StyledTextEditor(innerPanel, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
            GridData gdt = new GridData(GridData.FILL_BOTH);
            gdt.widthHint = 400;
            gdt.heightHint = 400;
            this.textEditor.setLayoutData(gdt);
            this.textEditor.setEditable(false);
            this.textEditor.setAllowFind(false);
            this.textEditor.getTextWidget().setWordWrap(false);
            
            this.textEditor.setText(theXmlText);
            
            return dlgPanel;
        }

    	@Override
    	protected Control createContents(Composite parent) {
    		// TODO Auto-generated method stub
    		Control superControl =  super.createContents(parent);
    		
    		getButton(IDialogConstants.OK_ID).setEnabled(true);
    		
    		return superControl;
    	}
    }
    
}
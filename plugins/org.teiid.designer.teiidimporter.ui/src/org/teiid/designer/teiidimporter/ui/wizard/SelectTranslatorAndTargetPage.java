/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.ArrayList;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.panels.ImportPropertiesPanel;
import org.teiid.designer.teiidimporter.ui.panels.TranslatorHelper;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;


/**
 * SelectTranslatorAndTargetPage 
 * Page 2 of the TeiidImportWizard - for selection of the translator and target model
 * 
 * @since 8.1
 */
public class SelectTranslatorAndTargetPage extends AbstractWizardPage implements UiConstants {

    private static final String EMPTY_STR = ""; //$NON-NLS-1$
    private static final String SERVER_PREFIX = "Server: "; //$NON-NLS-1$

    private TeiidImportManager importManager;

    private Text dataSourceNameText;
    private Text dataSourceDriverText;
    
    private Collection<String> translatorNames = new ArrayList<String>();
    private Combo translatorNameCombo;

    private Text targetModelContainerText;
    private Text targetModelFileText;
    private Text targetModelInfoText;

    /**
     * SelectedTranslatorAndTargetPage Constructor
     * @param importManager the TeiidImportManager for the wizard
     */
    public SelectTranslatorAndTargetPage( TeiidImportManager importManager ) {
        super(SelectTranslatorAndTargetPage.class.getSimpleName(), Messages.SelectTranslatorPage_title); 
        this.importManager = importManager;
    }

    @Override
    public void createControl( Composite theParent ) {
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(1, false));

        setControl(pnl);

        Label serverNameLabel = new Label(pnl,SWT.NONE);
        String serverString;
        try {
            serverString = importManager.getDisplayName();
        } catch (Exception ex) {
            serverString = "Unknown"; //$NON-NLS-1$
        }
        serverNameLabel.setText(SERVER_PREFIX+serverString);

        // Group for selection of the Connections
        createDataSourceAndTranslatorPanel(pnl);
        
        // Panel for Optional Properties
        new ImportPropertiesPanel(pnl, importManager, 4);
                
        // Group for Selection of target Source Model
        createTargetModelGroup(pnl);
        
        // Validate the page
        validatePage();
    }


    /*
     * Panel for selection of the Connection Type
     * @param parent the parent Composite
     */
    private void createDataSourceAndTranslatorPanel(Composite parent) {
        // -------------------------------------
        // Create the Source Definition Group
        // -------------------------------------
        Group sourceGroup = WidgetFactory.createGroup(parent, Messages.SelectTranslatorPage_SrcDefnGroup, SWT.NONE, 1, 2);
        sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // -------------------------------------
        // DataSource Name
        // -------------------------------------
        Label dsNameLabel = new Label(sourceGroup,SWT.NONE);
        dsNameLabel.setText(Messages.SelectTranslatorPage_dsNameLabel);
        
        dataSourceNameText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        dataSourceNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String dsName = this.importManager.getDataSourceName();
        if(dsName!=null) dataSourceNameText.setText(dsName);
        dataSourceNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        dataSourceNameText.setEditable(false);

        // -----------------------
        // DataSource Driver Name
        // -----------------------
        Label dsDriverLabel = new Label(sourceGroup,SWT.NONE);
        dsDriverLabel.setText(Messages.SelectTranslatorPage_dsTypeLabel);            

        dataSourceDriverText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        dataSourceDriverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String dsDriver = this.importManager.getDataSourceDriverName();
        if(dsDriver!=null) dataSourceDriverText.setText(dsDriver);
        dataSourceDriverText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        dataSourceDriverText.setEditable(false);
        
        // -------------------------------------
        // Combo for Translator selection
        // -------------------------------------

        Label translatorLabel = new Label(sourceGroup,SWT.NONE);
        translatorLabel.setText(Messages.SelectTranslatorPage_translatorLabel);
        
        refreshTranslators();
        
        this.translatorNameCombo = WidgetFactory.createCombo(sourceGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 translatorNames.toArray());
        this.translatorNameCombo.setVisibleItemCount(8);
        this.translatorNameCombo.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleTranslatorChanged();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
                
    }
    
    /*
     * Get the initial Translator selection.  Attempts to choose the best option,
     * based on the name of the driver being used.
     * @return the translator name
     */
    private String getInitialTranslatorSelection() {
        String driverName = importManager.getDataSourceDriverName();
        if(translatorNames.isEmpty() || CoreStringUtil.isEmpty(driverName)) {
            return null;
        }
		ITeiidServerVersion teiidVersion = null;
        try {
			teiidVersion = importManager.getTeiidServerVersion();
		} catch (Exception ex) {
            UTIL.log(ex);
		}
        return TranslatorHelper.getTranslator(driverName, translatorNames, teiidVersion);
    }
    
    /*
     * Refresh the list of currently available translators on the server
     */
    private void refreshTranslators() {
        try {
            translatorNames.clear();
            Collection<ITeiidTranslator> availableTranslators = importManager.getTranslators();
            for(ITeiidTranslator translator: availableTranslators) {
                translatorNames.add(translator.getName());
            }
        } catch (Exception ex) {
            translatorNames.clear();
            UTIL.log(ex);
        }
    }
    
    /*
     * Handler for Translator Name Combo Selection
     */
    private void handleTranslatorChanged( ) { 
        // Need to sync the worker with the current profile
        int selIndex = translatorNameCombo.getSelectionIndex();
        String translatorName = translatorNameCombo.getItem(selIndex);

        importManager.setTranslatorName(translatorName);
        
        // Validate the page
        validatePage();
    }
    
    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            String dsName = importManager.getDataSourceName();
            String dsType = importManager.getDataSourceDriverName();
            this.dataSourceNameText.setText(dsName);
            this.dataSourceDriverText.setText(dsType);
            // Set initial translator selection
            String initialTranslatorSelection = getInitialTranslatorSelection();
            if(initialTranslatorSelection!=null) {
                int indx = this.translatorNameCombo.indexOf(initialTranslatorSelection);
                if(indx!=-1) {
                    this.translatorNameCombo.select(indx);
                    this.importManager.setTranslatorName(initialTranslatorSelection);
                } else {
                    this.importManager.setTranslatorName(null);
                }
            }
            
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
        Group sourceGroup = WidgetFactory.createGroup(parent, Messages.SelectTranslatorPage_TgtModelDefnGroup, SWT.NONE, 1, 3);
        sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // -----------------------
        // Location controls
        // -----------------------
        // Location Label
        Label locationLabel = new Label(sourceGroup, SWT.NULL);
        locationLabel.setText(Messages.SelectTranslatorPage_Location);

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
        browseButton.setText(Messages.SelectTranslatorPage_Browse);
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
        fileLabel.setText(Messages.SelectTranslatorPage_Name); 

        // Name Text box
        targetModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
        String targetModelName = this.importManager.getTargetModelName();
        if(targetModelName!=null) targetModelFileText.setText(targetModelName);
        
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        targetModelFileText.setLayoutData(gridData);
        targetModelFileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent e ) {
                handleTargetModelTextChanged();
            }
        });
        
        // Browse button for Model selection
        browseButton = new Button(sourceGroup, SWT.PUSH);
        gridData = new GridData();
        browseButton.setLayoutData(gridData);
        browseButton.setText(Messages.SelectTranslatorPage_Browse); 
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleTargetModelBrowse();
            }
        });
        
        new Label(sourceGroup, SWT.NONE);  // For spacing
        
        // Info area - shows model selection status
        Group infoGroup = WidgetFactory.createGroup(sourceGroup, Messages.SelectTranslatorPage_ModelStatus, SWT.NONE | SWT.BORDER_DASH,2);
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
        
    }
    
    /**
     * Handler for choosing the Target Model Location
     */
    void handleTargetModelLocationBrowse() {
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && targetModelContainerText != null) {
            this.importManager.setTargetModelLocation(folder.getFullPath().makeRelative());
            this.targetModelContainerText.setText(this.importManager.getTargetModelLocation().makeRelative().toString());
        } else {
            this.importManager.setTargetModelLocation(new Path(StringUtilities.EMPTY_STRING));
            this.targetModelContainerText.setText(StringUtilities.EMPTY_STRING);
        }
        
        validatePage();
    }
    
    /**
     * Handler for choosing the Target Model
     */
    void handleTargetModelBrowse() {
        final Object[] selections = WidgetUtil.
                showWorkspaceObjectSelectionDialog(Messages.SelectTranslatorPage_SelectTargetModelTitle,
                     Messages.SelectTranslatorPage_SelectTargetModelMsg,
                     false,
                     null,
                     sourceModelFilter,
                     new ModelResourceSelectionValidator(false),
                     new ModelExplorerLabelProvider(),
                     new ModelExplorerContentProvider() ); 

        // Update importManager with selections
        if (selections != null && selections.length == 1 && targetModelFileText != null) {
            if( selections[0] instanceof IFile) {
                IFile modelFile = (IFile)selections[0];
                IPath folderPath = modelFile.getFullPath().removeLastSegments(1);
                String modelName = modelFile.getFullPath().lastSegment();
                importManager.setTargetModelLocation(folderPath);
                importManager.setTargetModelName(modelName);
            }
        }
        
        // Update UI widgets
        if( this.importManager.getTargetModelName() != null ) {
            this.targetModelContainerText.setText(this.importManager.getTargetModelLocation().makeRelative().toString());
            this.targetModelFileText.setText(this.importManager.getTargetModelName());
        } else {
            this.targetModelFileText.setText(StringUtilities.EMPTY_STRING);
        }
        
        // Validate the page
        validatePage();
    }
    
    // Handler for ModelText changes
    void handleTargetModelTextChanged() {
        if( !CoreStringUtil.isEmpty(this.targetModelFileText.getText()) ) {
           this.importManager.setTargetModelName(this.targetModelFileText.getText());
        }

        validatePage();
    }
    
    /*
     * Page Validation
     * @return 'true' if the page is valid, 'false' if not
     */
    private boolean validatePage() {
        // Name, Driver and Translator validation
        boolean nameDriverTranslatorValid = validateNameDriverTranslator();
        if(!nameDriverTranslatorValid) return false;
        
        //---------------------------------------------------
        // Target Model Section - validation
        //---------------------------------------------------
        boolean modelSelectionValid = validateTargetModelSelection();
        if(!modelSelectionValid) return false;

        setThisPageComplete(EMPTY_STR, NONE);
        return true;
    }
    
    /*
     * This method validates the Source name and driver, plus the translator selection.
     * @return 'true' if the entries are non-null, 'false' if not.
     */
    private boolean validateNameDriverTranslator() {
        String dsName = this.importManager.getDataSourceName();
        String dsDriver = this.importManager.getDataSourceDriverName();
        String dsTranslator = this.importManager.getTranslatorName();
        
        if(CoreStringUtil.isEmpty(dsName)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoDataSourceNameMsg, ERROR);
            return false;
        }
        
        if(CoreStringUtil.isEmpty(dsDriver)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoDataSourceDriverMsg, ERROR);
            return false;
        }

        if(CoreStringUtil.isEmpty(dsTranslator)) {
            setThisPageComplete(Messages.SelectTranslatorPage_NoTranslatorMsg, ERROR);
            return false;
        }

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
            setThisPageComplete(Messages.SelectTranslatorPage_NoOpenProjMsg, ERROR);
            return false;
        } 

        // Check for a selected container
        String container = targetModelContainerText.getText();
        if (CoreStringUtil.isEmpty(container)) {
            setThisPageComplete(Messages.SelectTranslatorPage_SrcLocationNotSpecified, ERROR); 
            return false;
        }   
        
        // Check for the target project
        IProject project = getTargetProject();
        if (project == null) {
            setThisPageComplete(Messages.SelectTranslatorPage_SrcLocationNotSpecified, ERROR);
            return false;
        }
        
        // Validate the target model name
        String fileText = targetModelFileText.getText().trim();
        IStatus status = ModelNameUtil.validate(fileText, ModelerCore.MODEL_FILE_EXTENSION, null,
                ModelNameUtil.IGNORE_CASE );
        if( status.getSeverity() == IStatus.ERROR ) {
            setThisPageComplete(status.getMessage(), ERROR);
            return false;
        }
        
        // Valid target model - now compare it's connection profile vs the selected profile
        if( importManager.targetModelExists() && !importManager.isTargetModelConnectionProfileCompatible() ) {
            setThisPageComplete(Messages.SelectTranslatorPage_ConnProfileInTargetIncompatible, ERROR);
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
            this.targetModelInfoText.setText(Messages.SelectTranslatorPage_SrcModelUndefined);
        } else {
            this.targetModelInfoText.setText(Messages.SelectTranslatorPage_SrcModelSelected+": "+targetModelName); //$NON-NLS-1$
        }
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

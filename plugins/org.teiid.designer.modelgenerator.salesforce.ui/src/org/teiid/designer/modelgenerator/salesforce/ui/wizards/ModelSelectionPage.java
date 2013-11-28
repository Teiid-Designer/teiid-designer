/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce.ui.wizards;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelProject;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.modelgenerator.salesforce.SalesforceImportWizardManager;
import org.teiid.designer.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import org.teiid.designer.modelgenerator.salesforce.ui.util.XMLExtensionsFilter;
import org.teiid.designer.ui.common.dialog.FolderSelectionDialog;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;


/**
 * @since 8.0
 */
public class ModelSelectionPage extends AbstractWizardPage
    implements Listener, ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(ModelSelectionPage.class);

    protected SalesforceImportWizardManager importManager;

    private Text textFieldTargetModelName;

    private Button buttonSelectTargetModel;

    private Text textFieldTargetModelLocation;

    private boolean usesHiddenProject = false;

    private Button buttonSelectTargetModelLocation;

    private IContainer targetModelLocation;

    private boolean updating = false;

    private Button modelAuditFieldsCheck;

    private Button namesAslabelCheckBox;

    private Button generateUpdatedCheckBox;

    private Button generateDeletedCheckBox;

    public ModelSelectionPage( SalesforceImportWizardManager importManager ) {
        super(ModelSelectionPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
    }

    @Override
    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        GridLayout layout = new GridLayout(COLUMNS, false);
        pnl.setLayout(layout);
        setControl(pnl);

        createTargetModelControls(pnl);
        createImportOptionsControls(pnl);
        setPageStatus();
    }

    private void createTargetModelControls( Composite pnl ) {
        // target model group
        Group optionsGroup = WidgetFactory.createGroup(pnl, null, SWT.FILL, 1, 3);
        optionsGroup.setText(getString("targetModelGroup.text")); //$NON-NLS-1$

        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gData);

        // --------------------------------------------
        // Composite for Model Selection
        // --------------------------------------------
        // Select Model Label
        CLabel theLabel = new CLabel(optionsGroup, SWT.NONE);
        theLabel.setText(getString("targetModelLabel.text")); //$NON-NLS-1$
        final GridData gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        theLabel.setLayoutData(gridData);

        // target model name textfield
        textFieldTargetModelName = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        String text = getString("targetModelTextField.tooltip"); //$NON-NLS-1$
        textFieldTargetModelName.setToolTipText(text);
        if (null != importManager.getTargetModelName()) {
            textFieldTargetModelName.setText(importManager.getTargetModelName());
        }
        this.textFieldTargetModelName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( final ModifyEvent event ) {
                setPageStatus();
            }
        });

        // target model Browse Button
        buttonSelectTargetModel = WidgetFactory.createButton(optionsGroup,
                                                             getString("targetModelBrowseButton.text"), GridData.FILL); //$NON-NLS-1$
        buttonSelectTargetModel.setToolTipText(getString("targetModelBrowseButton.tooltip")); //$NON-NLS-1$

        // --------------------------------------------
        // Composite for Model Location Selection
        // --------------------------------------------
        // Select Target Location Label
        // WidgetFactory.createLabel( optionsGroup,
        // getString("targetModelLocationLabel.text")); //$NON-NLS-1$
        CLabel theLabel2 = new CLabel(optionsGroup, SWT.NONE);
        theLabel2.setText(getString("targetModelLocationLabel.text")); //$NON-NLS-1$
        final GridData gridData2 = new GridData(SWT.NONE);
        gridData2.horizontalSpan = 1;
        theLabel2.setLayoutData(gridData2);

        final IContainer location = this.importManager.getTargetModelLocation();
        final String name = (location == null ? null : location.getFullPath().makeRelative().toString());

        // FileSystem textfield
        textFieldTargetModelLocation = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        text = getString("targetModelLocationTextField.tooltip"); //$NON-NLS-1$
        textFieldTargetModelLocation.setToolTipText(text);

        if (name != null) {
            textFieldTargetModelLocation.setText(name);
        }
        this.textFieldTargetModelLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText( final ModifyEvent event ) {
                setPageStatus();
            }
        });

        // If hidden project is used for the current project, don't show the
        // folder fields
        if (usesHiddenProject) {
            theLabel2.setVisible(false);
            this.textFieldTargetModelLocation.setEditable(false);
            this.textFieldTargetModelLocation.setVisible(false);
            textFieldTargetModelLocation.setVisible(false);
            try {
                ModelProject theProject = ModelerCore.getModelWorkspace().getModelProjects()[0];
                IProject proj = theProject.getProject();
                textFieldTargetModelLocation.setText(proj.getName());
            } catch (ModelWorkspaceException e) {
                // if we end up here we're hosed
                UTIL.log(e);
                WizardUtil.setPageComplete(this, e.getLocalizedMessage(), IMessageProvider.ERROR);
            }
        }

        // Model Location Browse Button
        if (!usesHiddenProject) {
            buttonSelectTargetModelLocation = WidgetFactory.createButton(optionsGroup,
                                                                         getString("targetModelLocationBrowseButton.text"), GridData.FILL); //$NON-NLS-1$
            buttonSelectTargetModelLocation.setToolTipText(getString("targetModelLocationBrowseButton.tooltip")); //$NON-NLS-1$
            buttonSelectTargetModelLocation.addListener(SWT.Selection, this);
        }

        // --------------------------------------------
        // Add Listener to handle selection events
        // --------------------------------------------
        buttonSelectTargetModel.addListener(SWT.Selection, this);
    }

    private void createImportOptionsControls( Composite pnl ) {
    	Group optionsGroup = WidgetFactory.createGroup(pnl, SWT.FILL);
        optionsGroup.setText(getString("importOptionsGroup.text")); //$NON-NLS-1$
        GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
        optionsGroup.setLayoutData(gData);

        final ScrolledComposite c1 = new ScrolledComposite(optionsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL);
        c1.setLayout(new GridLayout());
        c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        c1.setExpandHorizontal(true);
        c1.setExpandVertical(true);
        
        final Composite c2 = WidgetFactory.createPanel(c1);
        gData = new GridData(SWT.FILL, SWT.FILL, true, true);
        c2.setLayoutData(gData);
        c2.setLayout(new GridLayout(2, false));
        c1.setContent(c2);
        
        
        modelAuditFieldsCheck = WidgetFactory.createCheckBox(c2,
                                                             getString("modelAuditFields.text")); //$NON-NLS-1$
        modelAuditFieldsCheck.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.modelAuditFields(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.modelAuditFields(((Button)e.getSource()).getSelection());
            }
        });
        final GridData labelData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        Label label = WidgetFactory.createLabel(c2, getString("modelAuditFieldsWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);
        label = WidgetFactory.createLabel(c2, getString("modelAuditFieldsWarning.text2"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);

        cardinalitiesCheckBox = WidgetFactory.createCheckBox(c2, getString("gatherCardianalitiesLabel.text")); //$NON-NLS-1$
        cardinalitiesCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.supressCollectCardinalities(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.supressCollectCardinalities(((Button)e.getSource()).getSelection());
            }
        });
        
        label = WidgetFactory.createLabel(c2, getString("gatherCardianalitiesWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);
        label = WidgetFactory.createLabel(c2, getString("gatherCardianalitiesWarning.text2"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);

        uniqueValueCheckBox = WidgetFactory.createCheckBox(c2, getString("gatherColumnDistinctValueLabel.text")); //$NON-NLS-1$
        uniqueValueCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setCollectColumnDistinctValue(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setCollectColumnDistinctValue(((Button)e.getSource()).getSelection());
            }
        });
        
        label = WidgetFactory.createLabel(c2, getString("gatherColumnDistinctValueWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);
        label = WidgetFactory.createLabel(c2, getString("gatherColumnDistinctValueWarning.text2"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);

        namesAslabelCheckBox = WidgetFactory.createCheckBox(c2, getString("namesAsNameInSourceLabel.text")); //$NON-NLS-1$
        namesAslabelCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setNameAsLabel(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setNameAsLabel(((Button)e.getSource()).getSelection());
            }
        });
        
        label = WidgetFactory.createLabel(c2, getString("namesAsNameInSourceWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);
        label = WidgetFactory.createLabel(c2, getString("namesAsNameInSourceWarning.text2"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);

        generateUpdatedCheckBox = WidgetFactory.createCheckBox(c2, getString("generateUpdatedLabel.text")); //$NON-NLS-1$
        generateUpdatedCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setGenerateUpdated(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setGenerateUpdated(((Button)e.getSource()).getSelection());
            }
        });
        label = WidgetFactory.createLabel(c2, getString("generateUpdatedWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);

        generateDeletedCheckBox = WidgetFactory.createCheckBox(c2, getString("generateDeletedLabel.text")); //$NON-NLS-1$
        generateDeletedCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setGenerateDeleted(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setGenerateDeleted(((Button)e.getSource()).getSelection());
            }
        });
        
        label = WidgetFactory.createLabel(c2, getString("generateDeletedWarning.text"), SWT.FILL); //$NON-NLS-1$
        label.setLayoutData(labelData);
        Point point = c2.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        point.x += 150;
        point.y += 20;
        c1.setMinSize(point);
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Handler for Workspace Target Relational Model Browse button.
     */
    void handleBrowseWorkspaceForTargetModel() {
        // Open the selection dialog for the target relational model
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.browseTargetModel.title"), //$NON-NLS-1$
                                                                           getString("dialog.browseTargetModel.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           this.importManager.getTargetModelLocation(),
                                                                           new XMLExtensionsFilter(),
                                                                           new ValidationFilter(),
                                                           				   new ModelExplorerLabelProvider());
        // Update the manager and the controls with selection
        if ((resources != null) && (resources.length > 0)) {
            IFile model = (IFile)resources[0];
            IContainer location = model.getParent();

            this.textFieldTargetModelName.setText(model.getName());
            this.textFieldTargetModelLocation.setText((location == null) ? "" //$NON-NLS-1$
            : location.getFullPath().makeRelative().toString());
            // this.updateCheckBox.setSelection(true);
            // updateCheckBoxSelected(); // to get handler activated
        }
    }

    /**
     * Handler for Workspace Target Model Location Browse button.
     */
    void handleBrowseWorkspaceForTargetModelLocation() {
        // create the dialog for target location
        FolderSelectionDialog dlg = new FolderSelectionDialog(Display.getCurrent().getActiveShell(),
                                                              new WorkbenchLabelProvider(), new WorkbenchContentProvider());

        dlg.setInitialSelection(this.importManager.getTargetModelLocation());
        dlg.addFilter(this.targetLocationFilter);
        dlg.setValidator(new ModelProjectSelectionStatusValidator());
        dlg.setAllowMultiple(false);
        dlg.setInput(ModelerCore.getWorkspace().getRoot());

        // display the dialog
        Object[] objs = new Object[1];
        if (dlg.open() == Window.OK) {
            objs = dlg.getResult();
        }

        IContainer location = (objs.length == 0 ? null : (IContainer)objs[0]);

        // Update the controls with the target location selection
        if (location != null) {
            this.textFieldTargetModelLocation.setText(location.getFullPath().makeRelative().toString());
            setPageStatus();
        }
    }

    void setPageStatus() {
        // Validate the target relational model name and location
        boolean targetValid = validateTargetModelNameAndLocation();
        if (!targetValid) {
            return;
        }

        getContainer().updateButtons();
    }

    @Override
    public void handleEvent( Event event ) {
        if (event.widget == this.buttonSelectTargetModel) {
            handleBrowseWorkspaceForTargetModel();
            // Handle workspace browse for target model location
        } else if (event.widget == this.buttonSelectTargetModelLocation) {
            handleBrowseWorkspaceForTargetModelLocation();
        }
    }

    /**
     * validate the selected target relational model name and location. Returns 'true' if the validation is successful, 'false' if
     * not.
     * 
     * @return 'true' if the selection is valid, 'false' if not.
     */
    private boolean validateTargetModelNameAndLocation() {
        importManager.setCanFinish(updating = false);
        try {
            // Validate the target Model Name and location
            targetModelLocation = validateFileAndFolder(this.textFieldTargetModelName,
                                                        this.textFieldTargetModelLocation,
                                                        ModelerCore.MODEL_FILE_EXTENSION);

            // If null location was returned, error was found
            if (targetModelLocation == null) {
                return false;
                // Check if locations project is a model project
            } else if (targetModelLocation.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                setErrorMessage(getString("notModelProjectMessage")); //$NON-NLS-1$
                setPageComplete(false);
                targetModelLocation = null;
                return false;
            }

            // Continue checks on the target model and location
            String targetModelName = this.textFieldTargetModelName.getText();
            targetModelName = FileUtils.toFileNameWithExtension(targetModelName, ModelerCore.MODEL_FILE_EXTENSION);
            final IFile file = targetModelLocation.getFile(new Path(targetModelName));
            ModelResource model = null;
            if (file.exists()) {
                try {
                    model = ModelerCore.getModelEditor().findModelResource(file);
                    if (model.isReadOnly()) {
                        WizardUtil.setPageComplete(this, UTIL.getString(PREFIX + "readOnlyModelMessage", file.getName()), //$NON-NLS-1$
                                                   IMessageProvider.ERROR);
                        return false;
                    }
                    if (!RelationalPackage.eNS_URI.equals(model.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                        WizardUtil.setPageComplete(this, UTIL.getString(PREFIX + "notRelationalModelMessage", file.getName()), //$NON-NLS-1$
                                                   IMessageProvider.ERROR);
                        return false;
                    }
                    if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                        WizardUtil.setPageComplete(this, UTIL.getString(PREFIX + "virtualModelMessage", file.getName()), //$NON-NLS-1$
                                                   IMessageProvider.ERROR);
                        return false;
                    }
                    this.importManager.setUpdatedModel(model);
                    this.updating = true;
                } catch (final ModelWorkspaceException err) {
                    UTIL.log(err);
                    WidgetUtil.showError(err.getLocalizedMessage());
                }
            }
            this.importManager.setTargetModelName(targetModelName);
            this.importManager.setTargetModelLocation(targetModelLocation);
            if (!updating) {
                importManager.setCanFinish(true);
            }
            getContainer().updateButtons();
        } catch (final CoreException err) {
            UTIL.log(err);
            WizardUtil.setPageComplete(this, err.getLocalizedMessage(), IMessageProvider.ERROR);
            return false;
        }
        WizardUtil.setPageComplete(this);
        return true;
    }

    /**
     * validate the file name and location name. if the file is valid and the location is found, return the location container. If
     * not valid, return a null value.
     * 
     * @param fileText the Text entry widget for the file name.
     * @param locationText the Text entry widget for the model location.
     * @return the location container, null if invalid or not found.
     */
    private IContainer validateFileAndFolder( final Text fileText,
                                              final Text folderText,
                                              final String fileExtension ) throws CoreException {
        CoreArgCheck.isNotNull(fileText);
        CoreArgCheck.isNotNull(folderText);
        CoreArgCheck.isNotNull(fileExtension);
        String fileName = fileText.getText();
        if (CoreStringUtil.isEmpty(fileName)) {
            WizardUtil.setPageComplete(this, getString("missingFileMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        } else {
            IStatus status = ModelNameUtil.validate(fileName, ModelerCore.MODEL_FILE_EXTENSION, targetModelLocation,
            		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES);
            if( status.getSeverity() == IStatus.ERROR ) {
                WizardUtil.setPageComplete(this, status.getMessage(), ERROR);
                targetModelLocation = null;
            } else {
                final String folderName = folderText.getText();
                if (CoreStringUtil.isEmpty(folderName)) {
                    WizardUtil.setPageComplete(this, getString("missingFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                } else {
                    final IResource resrc = ModelerCore.getWorkspace().getRoot().findMember(folderName);
                    if (resrc == null || !(resrc instanceof IContainer) || resrc.getProject() == null) {
                        WizardUtil.setPageComplete(this, getString("invalidFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                    } else if (!resrc.getProject().isOpen()) {
                        WizardUtil.setPageComplete(this, getString("closedProjectMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                    } else {
                        final IContainer folder = (IContainer)resrc;
                        boolean exists = false;
                        final IResource[] resrcs;
                        resrcs = folder.members();
                        // Append file extension if necessary
                        fileName = FileUtils.toFileNameWithExtension(fileName, fileExtension);
                        for (int ndx = resrcs.length; --ndx >= 0;) {
                            if (resrcs[ndx].getName().equalsIgnoreCase(fileName)) {
                                exists = true;
                                break;
                            }
                        }
                        if (exists) {
                            WizardUtil.setPageComplete(this);
                        } else {
                            WizardUtil.setPageComplete(this, getString("invalidFileMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                        }
                        return folder;
                    }
                }
            }
        }
        return null;
    }

    /** Filter for selecting target location. */
    private ViewerFilter targetLocationFilter = new ViewerFilter() {
        @Override
        public boolean select( final Viewer viewer,
                               final Object parent,
                               final Object element ) {

            boolean result = false;

            if (element instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)element).getProject().isOpen();
                if (projectOpen) {
                    // Show projects
                    if (element instanceof IProject) {
                        result = true;
                        // Show folders
                    } else if (element instanceof IFolder) {
                        result = true;
                    }
                }
            }
            return result;
        }
    };

    private Button cardinalitiesCheckBox;

    private Button uniqueValueCheckBox;

    /**
     * Sets the initial workspace selection.
     * 
     * @param theSelection the current workspace selection
     */
    public void setInitialSelection( ISelection theSelection ) {
        if (!theSelection.isEmpty() && (theSelection instanceof IStructuredSelection)) {
            Object[] selectedObjects = ((IStructuredSelection)theSelection).toArray();

            // Set the selected container as the target location
            if (selectedObjects.length == 1) {
                final IContainer container = ModelUtil.getContainer(selectedObjects[0]);
                if (container != null) {
                    this.importManager.setTargetModelLocation(container);
                }
            }

            for (int i = 0; i < selectedObjects.length; i++) {
                if (selectedObjects[i] instanceof IFile) {
                    if (SalesforceUIUtil.isModelFile((IFile)selectedObjects[i])) {
                        // Convert the IFile object to a File object
                        File fNew = ((IFile)selectedObjects[i]).getLocation().toFile();
                        if (fNew != null) {
                            String uriStr = null;
                            try {
                                uriStr = fNew.toURI().toURL().toExternalForm();
                            } catch (MalformedURLException err) {
                                // exception will leave uri null
                            }
                            if (null != uriStr) {
                                this.importManager.setTargetModelName(uriStr.substring(uriStr.lastIndexOf('/') + 1));
                            }
                            break;

                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canFlipToNextPage() {
        return super.canFlipToNextPage() && updating;
    }
    
    class ValidationFilter extends ModelResourceSelectionValidator {

        public ValidationFilter() {
            super(ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(RelationalPackage.eNS_URI), false);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator#validate(java.lang.Object[])
         */
        @Override
        public IStatus validate( Object[] selection ) {
            IStatus status = super.validate(selection);

            if (status.getSeverity() != IStatus.ERROR) {
                IFile file = (IFile)selection[0];

                try {
                    ModelResource model = ModelerCore.getModelEditor().findModelResource(file);
    
                    if (model.isReadOnly()) {
                        status = new Status(IStatus.ERROR, PLUGIN_ID, UTIL.getString(PREFIX + "dialog.readOnlyModelMessage")); //$NON-NLS-1$
                    }
    
                    if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                        status = new Status(IStatus.ERROR, PLUGIN_ID, UTIL.getString(PREFIX + "dialog.virtualModelMessage")); //$NON-NLS-1$
                    }
                } catch (Exception e) {
                    status = new Status(IStatus.ERROR, PLUGIN_ID, null, e);
                }
            }

            return status;
        }
    }
}

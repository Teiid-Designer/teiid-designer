/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import java.io.File;
import java.net.MalformedURLException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.salesforce.SalesforceImportWizardManager;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.util.XMLExtensionsFilter;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class ModelSelectionPage extends AbstractWizardPage
    implements Listener, ModelGeneratorSalesforceUiConstants, ModelGeneratorSalesforceUiConstants.Images,
    ModelGeneratorSalesforceUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelSelectionPage.class);

    protected SalesforceImportWizardManager importManager;

    private Text textFieldTargetModelName;

    private Button buttonSelectTargetModel;

    private Text textFieldTargetModelLocation;

    private boolean usesHiddenProject = false;

    private Button buttonSelectTargetModelLocation;

    private IContainer targetModelLocation;

    private boolean updating = false;

    private Button supressAuditFieldsCheck;

    private Button namesAsNameInSouceCheckBox;

    private Button generateUpdatedCheckBox;

    private Button generateDeletedCheckBox;

    public ModelSelectionPage( SalesforceImportWizardManager importManager ) {
        super(ModelSelectionPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.importManager = importManager;
    }

    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnl);

        createTargetModelControls(pnl);
        createImportOptionsControls(pnl);
        setPageStatus();
    }

    private void createTargetModelControls( Composite pnl ) {
        // target model group
        Group optionsGroup = new Group(pnl, SWT.NONE);
        optionsGroup.setText(getString("targetModelGroup.text")); //$NON-NLS-1$

        GridData gData = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gData);

        optionsGroup.setLayout(new GridLayout(3, false));

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
        // target model group
        Group optionsGroup = new Group(pnl, SWT.VERTICAL);
        optionsGroup.setText(getString("importOptionsGroup.text")); //$NON-NLS-1$

        GridData gData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        optionsGroup.setLayoutData(gData);

        optionsGroup.setLayout(new GridLayout(1, false));

        supressAuditFieldsCheck = WidgetFactory.createCheckBox(optionsGroup,
                                                               getString("supressAuditFields.text"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        supressAuditFieldsCheck.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setSupressAuditFields(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setSupressAuditFields(((Button)e.getSource()).getSelection());
            }
        });

        cardinalitiesCheckBox = WidgetFactory.createCheckBox(optionsGroup, getString("gatherCardianalitiesLabel.text")); //$NON-NLS-1$
        cardinalitiesCheckBox.setSelection(true);
        cardinalitiesCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setCollectCardinalities(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setCollectCardinalities(((Button)e.getSource()).getSelection());
            }
        });

        WidgetFactory.createLabel(optionsGroup, getString("gatherCardianalitiesWarning.text")); //$NON-NLS-1$

        uniqueValueCheckBox = WidgetFactory.createCheckBox(optionsGroup, getString("gatherColumnDistinctValueLabel.text")); //$NON-NLS-1$
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
        WidgetFactory.createLabel(optionsGroup, getString("gatherColumnDistinctValueWarning.text")); //$NON-NLS-1$

        namesAsNameInSouceCheckBox = WidgetFactory.createCheckBox(optionsGroup, getString("namesAsNameInSourceLabel.text")); //$NON-NLS-1$
        namesAsNameInSouceCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                importManager.setNameAsNameInSource(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
                importManager.setNameAsNameInSource(((Button)e.getSource()).getSelection());
            }
        });
        WidgetFactory.createLabel(optionsGroup, getString("namesAsNameInSourceWarning.text")); //$NON-NLS-1$

        generateUpdatedCheckBox = WidgetFactory.createCheckBox(optionsGroup, getString("generateUpdatedLabel.text")); //$NON-NLS-1$
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

        generateDeletedCheckBox = WidgetFactory.createCheckBox(optionsGroup, getString("generateDeletedLabel.text")); //$NON-NLS-1$
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
        MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(RelationalPackage.eNS_URI);
        Object[] resources = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.browseTargetModel.title"), //$NON-NLS-1$
                                                                           getString("dialog.browseTargetModel.msg"), //$NON-NLS-1$
                                                                           false,
                                                                           this.importManager.getTargetModelLocation(),
                                                                           new XMLExtensionsFilter(),
                                                                           new ModelResourceSelectionValidator(descriptor, false));
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
        dlg.setInput(ResourcesPlugin.getWorkspace().getRoot());

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
                        WizardUtil.setPageComplete(this, getString("readOnlyModelMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                        return false;
                    }
                    if (!RelationalPackage.eNS_URI.equals(model.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                        WizardUtil.setPageComplete(this, getString("notRelationalModelMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                        return false;
                    }
                    if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                        WizardUtil.setPageComplete(this, getString("virtualModelMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
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
        ArgCheck.isNotNull(fileText);
        ArgCheck.isNotNull(folderText);
        ArgCheck.isNotNull(fileExtension);
        String fileName = fileText.getText();
        if (StringUtil.isEmpty(fileName)) {
            WizardUtil.setPageComplete(this, getString("missingFileMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        } else {
            String problem = ModelUtilities.validateModelName(fileName, ModelerCore.MODEL_FILE_EXTENSION);
            if (problem != null) {
                String msg = getString("invalidFileMessage") + '\n' + problem; //$NON-NLS-1$
                WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
                targetModelLocation = null;
            } else {
                final String folderName = folderText.getText();
                if (StringUtil.isEmpty(folderName)) {
                    WizardUtil.setPageComplete(this, getString("missingFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                } else {
                    final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
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
}

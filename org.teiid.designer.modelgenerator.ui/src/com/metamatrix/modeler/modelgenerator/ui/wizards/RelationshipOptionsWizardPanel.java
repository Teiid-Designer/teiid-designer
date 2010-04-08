/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.jdom.JDOMException;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.ModelWorkspaceTreeProvider;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * RelationshipOptionsWizardPanel. This panel contains the relationship options.
 */
public class RelationshipOptionsWizardPanel extends Composite implements ModelGeneratorUiConstants, CoreStringUtil.Constants {

    private final static String FILE_EXT = ".xmi"; //$NON-NLS-1$

    private final static String BROWSE_SHORTHAND = "..."; //$NON-NLS-1$
    private final static int MODEL_NAME_TEXT_WIDTH = (int)(Display.getCurrent().getBounds().width * .25);
    private final static String SELECT_MODEL_TITLE = Util.getString("RelationshipOptionsWizardPanel.selectModelDialog.title"); //$NON-NLS-1$
    private final static String SELECT_MODEL_MSG = Util.getString("RelationshipOptionsWizardPanel.selectModelDialog.msg"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_NO_NAME = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.errMsg.noName"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_INVALID_NAME = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.errMsg.invalidName"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_ALREADY_EXISTS = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.errMsg.alreadyExists"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_SAME_NAME_AS_RELATIONAL = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.errMsg.sameNameAsRelational"); //$NON-NLS-1$
    private final static String MODEL_CREATE_ERROR_IS_VALID = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.errMsg.isValid"); //$NON-NLS-1$

    /** Title for the panel group */
    private static final String GROUP_TITLE = Util.getString("RelationshipOptionsWizardPanel.title"); //$NON-NLS-1$

    private RelationshipOptionsWizardPage wizardPage;

    private Button chooserBrowseButton;
    private Text modelChooserNameText;
    private ModelResource selectedRelationshipsModel;
    private GeneratorManagerOptions generatorMgrOptions;

    private Button createUmlRelationshipsRadio;
    private Button noCreateUmlRelationshipsRadio;
    private Text relationshipsModelNameField;
    private Button createRelationshipsModelButton;
    IResource targetResource;
    private IPath targetRelationalFilePath;
    private CLabel relationshipModelNameStatusLabel;

    public RelationshipOptionsWizardPanel( Composite parent,
                                           RelationshipOptionsWizardPage page,
                                           GeneratorManagerOptions generatorMgrOptions ) {
        super(parent, SWT.NULL);
        this.wizardPage = page;
        this.generatorMgrOptions = generatorMgrOptions;

        initialize();

        this.wizardPage.validatePage();
    }

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        createRelationshipOptionsPanel(this);
    }

    /**
     * Create the Relationship Options Content Panel
     * 
     * @param parent the parent composite
     * @return the content panel
     */
    private Composite createRelationshipOptionsPanel( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        // Set grid layout
        GridLayout gridLayout = new GridLayout();
        panel.setLayout(gridLayout);

        String createRelationshipsToUMLRadioText = Util.getString("RelationshipOptionsWizardPanel.content.createRelationshipsToUMLRadio.text"); //$NON-NLS-1$
        String createRelationshipsToUMLRadioTip = Util.getString("RelationshipOptionsWizardPanel.content.createRelationshipsToUMLRadio.tip"); //$NON-NLS-1$
        String noCreateRelationshipsToUMLRadioText = Util.getString("RelationshipOptionsWizardPanel.content.noCreateRelationshipsToUMLRadio.text"); //$NON-NLS-1$
        String noCreateRelationshipsToUMLRadioTip = Util.getString("RelationshipOptionsWizardPanel.content.noCreateRelationshipsToUMLRadio.tip"); //$NON-NLS-1$

        // Relationships Group
        final Group relationshipsLocationGrp = WidgetFactory.createGroup(panel, GROUP_TITLE, GridData.HORIZONTAL_ALIGN_FILL);
        {
            // ------------------------------------------
            // Radio Button - Create Relationships
            // ------------------------------------------
            createUmlRelationshipsRadio = WidgetFactory.createRadioButton(relationshipsLocationGrp,
                                                                          createRelationshipsToUMLRadioText,
                                                                          true);
            createUmlRelationshipsRadio.setToolTipText(createRelationshipsToUMLRadioTip);
            createUmlRelationshipsRadio.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    createUmlRelationshipsRadioSelected();
                }
            });

            // -------------------------------------------------
            // Subpanel containing chooser / creation controls
            // -------------------------------------------------
            Composite relationshipsModelPanel = new Composite(relationshipsLocationGrp, SWT.NONE);
            relationshipsModelPanel.setLayout(new GridLayout());

            // Select Model Title label
            String selectRelModelStr = Util.getString("RelationshipOptionsWizardPanel.content.title"); //$NON-NLS-1$
            WidgetFactory.createLabel(relationshipsModelPanel, selectRelModelStr);
            // Workspace Model Chooser
            createModelChooser(relationshipsModelPanel);

            // Subpanel for creation of a new Relationships Model
            createModelCreationComposite(relationshipsModelPanel);

            // ------------------------------------------
            // Radio Button - Dont Create Relationships
            // ------------------------------------------
            noCreateUmlRelationshipsRadio = WidgetFactory.createRadioButton(relationshipsLocationGrp,
                                                                            noCreateRelationshipsToUMLRadioText,
                                                                            false);
            noCreateUmlRelationshipsRadio.setToolTipText(noCreateRelationshipsToUMLRadioTip);

            // init chooser states
            this.chooserBrowseButton.setEnabled(true);
            this.modelChooserNameText.setEnabled(true);
            this.modelChooserNameText.setEditable(false);
        }
        return panel;
    }

    /**
     * Handler for selection/deselection of createRelationshipsRadio
     */
    void createUmlRelationshipsRadioSelected() {
        // If selected, enable Model Chooser, otherwise disable
        if (this.createUmlRelationshipsRadio.getSelection()) {
            setRelationshipModelChoicesEnabledStatus(true);
            // generated relationships must be placed in a selected relationships model
            this.generatorMgrOptions.setRelationshipsModelOption(GeneratorManagerOptions.PUT_RELATIONSHIPS_IN_SELECTED_MODEL);
            this.generatorMgrOptions.setRelationshipsModel(selectedRelationshipsModel);
        } else {
            setRelationshipModelChoicesEnabledStatus(false);
            // Set the generator option for the relationships model
            this.generatorMgrOptions.setRelationshipsModelOption(GeneratorManagerOptions.DO_NOT_GENERATE_RELATIONSHIPS);
        }
        this.wizardPage.validatePage();
    }

    /**
     * set the enabled states for the model chooser and newModel creation controls
     * 
     * @param createRelationships 'true' if createRelationships is selected, 'false' if not.
     */
    private void setRelationshipModelChoicesEnabledStatus( boolean createRelationships ) {
        if (!createRelationships) {
            // Disable all model controls
            this.modelChooserNameText.setEnabled(false);
            this.chooserBrowseButton.setEnabled(false);
            this.relationshipsModelNameField.setEnabled(false);
            this.createRelationshipsModelButton.setEnabled(false);
        } else {
            // Enable model controls
            this.modelChooserNameText.setEnabled(true);
            this.chooserBrowseButton.setEnabled(true);
            this.relationshipsModelNameField.setEnabled(true);
            handleSetRelationshipModelName();
        }
    }

    /**
     * Create the controls for creating a new relationships Model.
     * 
     * @param parent the parent composite
     * @return the created composite
     */
    private Composite createModelCreationComposite( Composite parent ) {
        // Set up Composite
        Composite nameComposite = new Composite(parent, SWT.NONE);
        GridLayout nameCompositeLayout = new GridLayout();
        nameComposite.setLayout(nameCompositeLayout);
        nameCompositeLayout.numColumns = 3;
        nameCompositeLayout.marginWidth = 0;
        GridData nameCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        nameCompositeGridData.horizontalIndent = 20;
        nameComposite.setLayoutData(nameCompositeGridData);

        // Title Label - spans all cols
        String titleText = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.titleLabel.text"); //$NON-NLS-1$
        WidgetFactory.createLabel(nameComposite, GridData.HORIZONTAL_ALIGN_FILL, 3, titleText);

        // Model name entry field - col 2
        this.relationshipsModelNameField = WidgetFactory.createTextField(nameComposite, GridData.HORIZONTAL_ALIGN_FILL);
        GridData modelNameTextGridData = new GridData();
        modelNameTextGridData.widthHint = MODEL_NAME_TEXT_WIDTH;
        this.relationshipsModelNameField.setLayoutData(modelNameTextGridData);
        // listener for typing changes
        this.relationshipsModelNameField.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                handleSetRelationshipModelName();
            }
        });

        // Create Model Button - col 3
        String createButtonText = Util.getString("RelationshipOptionsWizardPanel.modelCreationComposite.createButton.text"); //$NON-NLS-1$
        this.createRelationshipsModelButton = WidgetFactory.createButton(nameComposite, createButtonText);
        createRelationshipsModelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                createRelationshipsModelButtonClicked();
            }
        });

        // Model Name status field - spans all 3 columns
        this.relationshipModelNameStatusLabel = WidgetFactory.createLabel(nameComposite, GridData.HORIZONTAL_ALIGN_FILL, 3);

        // This initializes the status label
        handleSetRelationshipModelName();

        return nameComposite;
    }

    /**
     * handler for Create Relationships Model Button pressed
     */
    void createRelationshipsModelButtonClicked() {
        String nameText = this.relationshipsModelNameField.getText();
        ModelResource newModel = createRelationshipsModel(this.targetResource, nameText);
        // Save Relationships Model
        try {
            if (newModel != null) {
                newModel.save(null, false);
            }
        } catch (ModelWorkspaceException e) {
            Util.log(e);
        }
        // Set model chooser with newly created model and set generator options
        modelChooserNameText.setText(getFileName(nameText));
        this.generatorMgrOptions.setRelationshipsModel(newModel);

        // Reset new ModelName entry field
        this.relationshipsModelNameField.setText(EMPTY_STRING);
        this.wizardPage.validatePage();
    }

    /**
     * handler when the relationships Model name field changes. This updates the status label under the modelName entry field
     */
    void handleSetRelationshipModelName() {
        String nameText = this.relationshipsModelNameField.getText();
        if (isValidRelationshipsModelName(nameText)) {
            this.createRelationshipsModelButton.setEnabled(true);
        } else {
            this.createRelationshipsModelButton.setEnabled(false);
        }
        String message = getRelationshipsModelNameStatus(nameText);
        this.relationshipModelNameStatusLabel.setText(message);
    }

    /**
     * test whether the supplied modelName is valid
     * 
     * @param modelName the model name to test
     * @return 'true' if the name is valid, 'false' if not.
     */
    private boolean isValidRelationshipsModelName( String modelName ) {
        // Check for null or zero-length
        if (modelName == null || modelName.length() == 0) {
            return false;
            // Check for valid model name
        }
        String fileNameMessage = ModelUtilities.validateModelName(modelName, FILE_EXT);
        if (fileNameMessage != null) {
            return false;
        }
        // Check if already exists
        String fileName = getFileName(modelName);
        IPath modelFullPath = null;
        IPath modelRelativePath = null;
        if (this.targetResource != null) {
            modelFullPath = this.targetResource.getFullPath().append(fileName).makeRelative();
            modelRelativePath = this.targetResource.getProjectRelativePath().append(fileName);
        }

        if (this.targetResource != null && this.targetResource.getProject().exists(modelRelativePath)) {
            return false;
        }
        // Check if it is the same path as the relational model being generated
        if (this.targetRelationalFilePath != null && this.targetRelationalFilePath.equals(modelFullPath)) {
            return false;
        }
        // success
        return true;
    }

    /**
     * set the target resource in which to the models are to be created
     * 
     * @param targetRes the target Resource
     */
    public void setTargetResource( IResource targetRes ) {
        this.targetResource = targetRes;
    }

    /**
     * set the target relational file IPath for the relational model being created.
     * 
     * @param targetFilePath the target file IPath
     */
    public void setTargetRelationalFilePath( IPath targetFilePath ) {
        this.targetRelationalFilePath = targetFilePath;
    }

    /**
     * get a string status indicator for the modelName status label, given the modelName
     * 
     * @param modelName the model name to test
     * @return the status of the supplied model name
     */
    private String getRelationshipsModelNameStatus( String modelName ) {
        // Check for null or zero-length
        if (modelName == null || modelName.length() == 0) {
            return MODEL_CREATE_ERROR_NO_NAME;
            // Check for valid model name
        }
        String fileNameMessage = ModelUtilities.validateModelName(modelName, FILE_EXT);
        if (fileNameMessage != null) {
            return MODEL_CREATE_ERROR_INVALID_NAME;
        }
        // Check if already exists
        String fileName = getFileName(modelName);
        IPath modelFullPath = null;
        IPath modelRelativePath = null;
        if (this.targetResource != null) {
            modelFullPath = this.targetResource.getFullPath().append(fileName).makeRelative();
            modelRelativePath = this.targetResource.getProjectRelativePath().append(fileName);
        }

        if (this.targetResource != null && this.targetResource.getProject().exists(modelRelativePath)) {
            return MODEL_CREATE_ERROR_ALREADY_EXISTS;
        }

        if (this.targetRelationalFilePath != null && this.targetRelationalFilePath.equals(modelFullPath)) {
            return MODEL_CREATE_ERROR_SAME_NAME_AS_RELATIONAL;
        }
        // success
        return MODEL_CREATE_ERROR_IS_VALID;
    }

    /**
     * get the full file name, given a modelName string
     * 
     * @param modelName the model name
     * @return the full model name, including extension
     */
    private String getFileName( String modelName ) {
        String result = modelName.trim();
        if (!result.endsWith(FILE_EXT)) {
            result += FILE_EXT;
        }
        return result;
    }

    /**
     * Create a Relationships Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource createRelationshipsModel( IResource targetRes,
                                                   String modelName ) {
        String fileName = getFileName(modelName);
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(fileName);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(ModelIdentifier.RELATIONSHIP_MODEL_URI);
            resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        } catch (ModelWorkspaceException e) {
            e.printStackTrace();
        }
        return resrc;
    }

    /**
     * construct the ModelChooser Controls composite
     * 
     * @param parent the parent composite
     * @return the newly-created Composite
     */
    private Composite createModelChooser( Composite parent ) {
        // Set up Composite
        Composite chooserComposite = new Composite(parent, SWT.NONE);
        GridLayout chooserCompositeLayout = new GridLayout();
        chooserComposite.setLayout(chooserCompositeLayout);
        chooserCompositeLayout.numColumns = 2;
        chooserCompositeLayout.marginWidth = 0;
        GridData chooserCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        chooserCompositeGridData.horizontalIndent = 20;
        chooserComposite.setLayoutData(chooserCompositeGridData);

        // Model Name textbox
        modelChooserNameText = WidgetFactory.createTextField(chooserComposite, GridData.HORIZONTAL_ALIGN_FILL);
        GridData modelNameTextGridData = new GridData();
        modelNameTextGridData.widthHint = MODEL_NAME_TEXT_WIDTH;
        modelChooserNameText.setLayoutData(modelNameTextGridData);
        // ILabelProvider fileImageProvider = new ModelWorkspaceTreeProvider();

        // Browse Button
        chooserBrowseButton = new Button(chooserComposite, SWT.PUSH);
        chooserBrowseButton.setText(BROWSE_SHORTHAND);
        chooserBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                browseButtonClicked();
            }
        });
        return chooserComposite;
    }

    /**
     * handler for model Selection browse button clicked
     */
    void browseButtonClicked() {
        selectedRelationshipsModel = null;

        ModelWorkspaceTreeProvider provider = new ModelWorkspaceTreeProvider();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), provider, provider);
        dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
        dialog.setAllowMultiple(false);
        // Must select a Relationship Model
        MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(ModelIdentifier.RELATIONSHIP_MODEL_URI);
        dialog.setValidator(new ModelResourceSelectionValidator(descriptor, false));
        dialog.addFilter(filter);
        dialog.setTitle(SELECT_MODEL_TITLE);
        dialog.setMessage(SELECT_MODEL_MSG);

        if (dialog.open() == Window.OK) {
            Object[] selection = dialog.getResult();
            if ((selection.length == 1) && (selection[0] instanceof IFile)) {
                IFile sourceFile = (IFile)selection[0];
                selectedRelationshipsModel = null;
                modelChooserNameText.setText(""); //$NON-NLS-1$
                boolean exceptionOccurred = false;
                try {
                    selectedRelationshipsModel = ModelUtilities.getModelResource(sourceFile, true);
                } catch (Exception ex) {
                    Util.log(ex);
                    exceptionOccurred = true;
                }
                if (!exceptionOccurred) {
                    modelChooserNameText.setText(sourceFile.getName());
                    this.generatorMgrOptions.setRelationshipsModel(selectedRelationshipsModel);
                    this.wizardPage.validatePage();
                }
            }
        }
    }

    /** Filter for showing just open projects and their folders and models */
    private ViewerFilter filter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParent,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IResource) {
                // If the project is closed, dont show
                boolean projectOpen = ((IResource)theElement).getProject().isOpen();
                if (projectOpen) {
                    // Show open projects
                    if (theElement instanceof IProject) {
                        result = true;
                        // Show model files, and not .xsd files
                    } else if (theElement instanceof IFile) {
                        result = ModelUtil.isModelFile((IFile)theElement) && !ModelUtil.isXsdFile((IFile)theElement);
                    }
                }
                try {
                    // Look for .projects
                    if (DotProjectUtils.isDotProject(targetResource, false)) {
                        result = false;
                    }
                } catch (CoreException theException) {
                    Util.log(theException);
                } catch (IOException theException) {
                    Util.log(theException);
                } catch (JDOMException theException) {
                    Util.log(theException);
                }
            }

            return result;
        }
    };

    public void activateRelationshipModel() {
        ModelResource relationshipsModel = generatorMgrOptions.getRelationshipsModel();
        if (relationshipsModel != null) {
            ModelEditorManager.activate(relationshipsModel, true);
        } // endif
    }

}// end RelationshipOptionsWizardPanel

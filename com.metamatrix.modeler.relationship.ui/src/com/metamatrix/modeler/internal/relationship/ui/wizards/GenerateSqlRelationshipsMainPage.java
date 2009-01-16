/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.relationship.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.progress.IProgressConstants;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.relationship.ui.textimport.RelationshipTextImportModelSelectorDialog;
import com.metamatrix.modeler.internal.relationship.ui.textimport.RelationshipsLocationSelectionValidator;
import com.metamatrix.modeler.internal.relationship.ui.util.SqlDependencyRelationshipHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceDialog;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.editor.AbstractRelationshipTypeFilter;
import com.metamatrix.modeler.relationship.ui.properties.RelationshipPropertyEditorFactory;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.ClosedProjectFilter;
import com.metamatrix.ui.internal.widget.IListPanelController;

/**
 * Main Page for the Dependency Relationships Generator
 */
public class GenerateSqlRelationshipsMainPage extends WizardDataTransferPage implements UiConstants, IListPanelController {

    // widgets
    private Text modelFolderNameField;
    private Button modelFolderBrowseButton;
    private Text txtRelationshipType;
    private Button relTypeBrowseButton;
    protected Button autoGenerateNamesCheckbox;
    protected Button includeTableToTableCheckBox;
    protected Button includeColumnToColumnCheckbox;
    private ListViewer listViewer;

    private Group columnRadioGroup;
    private Button radioGenerateAllRels;
    private Button radioGenerateTargetDescendentRels;
    private Combo nLevelsCombo;

    // Metadata related variables
    private IProject targetProject;
    private ModelResource targetResource;
    private Object targetLocation;
    private Object selection;
    private Collection relationshipDataRows = Collections.EMPTY_LIST;
    RelationshipObjectProcessor relationshipObjectProcessor = new RelationshipObjectProcessor();
    private SqlDependencyRelationshipHelper dependencyHelper;

    // A boolean to indicate if the user has typed anything
    private boolean initializing = false;

    private static final String BROWSE_SHORTHAND = getString("browse_1"); //$NON-NLS-1$
    private static final String CHANGE_SHORTHAND = getString("change"); //$NON-NLS-1$
    private static final String DEFAULT_REL_TYPE = "Transformation"; //$NON-NLS-1$
    private static final String DELIM = "/"; //$NON-NLS-1$

    // dialog store id constants
    private static final String I18N_PREFIX = "GenerateSqlRelationshipsMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$
    private static final String FINISH_MESSAGE = getString("finishMessage"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String SELECT_TYPE_TEXT = getString("selectType"); //$NON-NLS-1$
    private static final String COLUMN_OPTIONS_TEXT = getString("columnOptionsGroup.text"); //$NON-NLS-1$
    private static final String GEN_ALL_COL_RELS_TEXT = getString("generateAllColRels.text"); //$NON-NLS-1$
    private static final String GEN_DECENDENT_COL_RELS_TEXT = getString("generateDecendentColRels.text"); //$NON-NLS-1$
    private static final String NLEVELS_LABEL_TEXT = getString("nLevelsLabel.text"); //$NON-NLS-1$
    private static final String OPTIONS_GROUP_TEXT = getString("optionsGroup.text"); //$NON-NLS-1$

    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    /**
     * Creates an instance of this class
     * 
     * @param title the page title
     * @param selection the initial selection (target groups)
     */
    protected GenerateSqlRelationshipsMainPage( String title,
                                                IStructuredSelection selection ) {
        super(title);
        setTitle(title);
    }

    /**
     * Creates an instance of this class
     * 
     * @param selection the initial selection (target groups)
     */
    public GenerateSqlRelationshipsMainPage( IStructuredSelection selection ) {
        this(PAGE_TITLE, selection);
    }

    /**
     * Creates an instance of this class
     */
    public GenerateSqlRelationshipsMainPage() {
        this(null);
    }

    /**
     * The <code>WizardResourceImportPage</code> implementation of this <code>WizardDataTransferPage</code> method returns
     * <code>true</code>. Subclasses may override this method.
     */
    @Override
    protected boolean allowNewContainerName() {
        return true;
    }

    /**
     * Handle all events and enablements for widgets in this dialog
     * 
     * @param event Event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            // Location to place the relationships has changed, display dialog and reset text field
            if (event.widget == modelFolderBrowseButton) {
                handleModelFolderBrowseButtonPressed();
            }

            // Type of relationship to generated has changed, display dialog and reset text field
            if (event.widget == relTypeBrowseButton) {
                handleBrowseTypeButtonPressed();
            }

            // Reset helper options and reload viewer
            if (event.widget == modelFolderBrowseButton || event.widget == relTypeBrowseButton
                || event.widget == autoGenerateNamesCheckbox || event.widget == includeTableToTableCheckBox
                || event.widget == includeColumnToColumnCheckbox || event.widget == this.radioGenerateAllRels
                || event.widget == this.nLevelsCombo) {

                // Disable column options if not generating column relationships
                if (event.widget == includeColumnToColumnCheckbox) {
                    updateColumnControlsEnablement();
                }

                // Get all of the options and reset on the helper
                updateHelperFromControls();

                // Reload the text area with relationship rows
                loadViewer();
                validate = true;
            }

            if (validate) setCompletionStatus();

            updateWidgetEnablements();
        }
    }

    /**
     * Update dependency helper using the page control settings
     */
    private void updateHelperFromControls() {
        // Get all of the options and reset on the helper
        String relLocation = this.modelFolderNameField.getText();
        String relType = this.txtRelationshipType.getText();
        boolean autoGen = this.autoGenerateNamesCheckbox.getSelection();
        boolean genTableRelsCheckbox = this.includeTableToTableCheckBox.getSelection();
        boolean genColRelsCheckbox = this.includeColumnToColumnCheckbox.getSelection();
        boolean genAllColRels = this.radioGenerateAllRels.getSelection();
        int nLevels = this.nLevelsCombo.getSelectionIndex() + 1;

        this.dependencyHelper.setOptions(relLocation,
                                         relType,
                                         genTableRelsCheckbox,
                                         genColRelsCheckbox,
                                         genAllColRels,
                                         autoGen,
                                         nLevels);
    }

    /**
     * Update the column controls enablement based on current selections
     */
    private void updateColumnControlsEnablement() {
        boolean genColsCB = includeColumnToColumnCheckbox.getSelection();
        if (genColsCB) {
            this.columnRadioGroup.setEnabled(true);
            this.radioGenerateAllRels.setEnabled(true);
            this.radioGenerateTargetDescendentRels.setEnabled(true);
        } else {
            this.columnRadioGroup.setEnabled(false);
            this.radioGenerateAllRels.setEnabled(false);
            this.radioGenerateTargetDescendentRels.setEnabled(false);
        }
    }

    /**
     * Creates a new button with the given id.
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates a standard push button, registers for selection
     * events including button presses and registers default buttons with its shell. The button id is stored as the buttons client
     * data. Note that the parent's layout is assumed to be a GridLayout and the number of columns in this layout is incremented.
     * Subclasses may override.
     * </p>
     * 
     * @param parent the parent composite
     * @param id the id of the button (see <code>IDialogConstants.*_ID</code> constants for standard dialog button ids)
     * @param label the label from the button
     * @param defaultButton <code>true</code> if the button is to be the default button, and <code>false</code> otherwise
     */
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        // increment the number of columns in the button bar
        ((GridLayout)parent.getLayout()).numColumns++;

        Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
        button.setLayoutData(buttonData);

        button.setData(new Integer(id));
        button.setText(label);

        if (defaultButton) {
            Shell shell = parent.getShell();
            if (shell != null) {
                shell.setDefaultButton(button);
            }
            button.setFocus();
        }
        return button;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite parent ) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        // Creates the targetModel and relationshipType selection controls
        createDestinationGroup(composite);

        // Creates the relationship generation options
        createOptionsGroup(composite);

        // Creates the list viewer for showing the relationships
        createWorkspaceListGroup(composite);

        restoreWidgetValues();

        // Initialize the relationship helper and page controls
        initDependencyHelper();
        initControlValues();

        setControl(composite);
        setCompletionStatus();
    }

    /**
     * Set the initial control values from the helper object. Adds listeners as last step.
     */
    private void initControlValues() {
        // ------------------------------
        // Set location string
        // ------------------------------
        String locStr = this.dependencyHelper.getLocationString();
        this.modelFolderNameField.setText(locStr);
        // ------------------------------
        // Set relationship type
        // ------------------------------
        String relType = this.dependencyHelper.getRelationshipType();
        this.txtRelationshipType.setText(relType);
        // ------------------------------
        // Set autogen names checkbox
        // ------------------------------
        boolean autoGenNames = this.dependencyHelper.getAutoGenNames();
        this.autoGenerateNamesCheckbox.setSelection(autoGenNames);
        this.autoGenerateNamesCheckbox.addListener(SWT.Selection, this);
        // ------------------------------
        // Set create table rels checkbox
        // ------------------------------
        boolean createTableRels = this.dependencyHelper.getCreateTableRelationshipsOption();
        this.includeTableToTableCheckBox.setSelection(createTableRels);
        this.includeTableToTableCheckBox.addListener(SWT.Selection, this);

        // ------------------------------
        // Set create col rels checkbox
        // ------------------------------
        boolean createColRels = this.dependencyHelper.getCreateColumnRelationshipsOption();
        this.includeColumnToColumnCheckbox.setSelection(createColRels);
        this.includeColumnToColumnCheckbox.addListener(SWT.Selection, this);

        // ------------------------------
        // Set create all cols radio
        // ------------------------------
        boolean createAllColRels = this.dependencyHelper.getCreateAllColumnRelationshipsOption();
        this.radioGenerateAllRels.setSelection(createAllColRels);
        this.radioGenerateAllRels.addListener(SWT.Selection, this);

        // ------------------------------------------------------------
        // Set column controls enablement based on initial selections
        // ------------------------------------------------------------
        updateColumnControlsEnablement();

        // ------------------------------
        // Set MaxLevels combo
        // ------------------------------
        int maxLevels = this.dependencyHelper.getMaxLevels();
        int nLevels = this.dependencyHelper.getNLevels();

        // Init combo items
        String[] items = new String[maxLevels];
        for (int i = 0; i < maxLevels; i++) {
            if (i == maxLevels - 1) {
                items[i] = "All"; //$NON-NLS-1$
            } else {
                items[i] = Integer.toString(i + 1);
            }
        }
        nLevelsCombo.setItems(items);

        // Set initial selection index
        if (nLevels == SqlDependencyRelationshipHelper.ALL_LEVELS) {
            nLevelsCombo.select(maxLevels - 1);
        } else {
            nLevelsCombo.select(nLevels - 1);
        }

        // Add listener
        nLevelsCombo.addListener(SWT.Selection, this);
    }

    /**
     * Method to create List box control group for displaying the list of relationships.
     * 
     * @param parent
     * @since 4.2
     */
    private void createWorkspaceListGroup( Composite parent ) {
        Label messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setText(getString("modelListMessage")); //$NON-NLS-1$
        messageLabel.setFont(parent.getFont());

        listViewer = new ListViewer(parent);
        GridData data = new GridData(GridData.FILL_BOTH);
        org.eclipse.swt.widgets.List list = listViewer.getList();
        int listMinHeight = list.getItemHeight() * 3;
        Rectangle trim = list.computeTrim(0, 0, 0, listMinHeight);
        data.minimumHeight = trim.height;
        listViewer.getControl().setLayoutData(data);
    }

    /**
     * Creates the target model selection controls and the relationship type selection controls.
     * 
     * @param parent the parent control
     */
    protected void createDestinationGroup( Composite parent ) {
        // container specification group
        Composite containerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        // container label
        Label resourcesLabel = new Label(containerGroup, SWT.NONE);
        resourcesLabel.setText(getString("targetLocation")); //$NON-NLS-1$
        resourcesLabel.setFont(parent.getFont());

        // container name entry field
        modelFolderNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        modelFolderNameField.setLayoutData(data);
        modelFolderNameField.setFont(parent.getFont());
        modelFolderNameField.setEditable(false);

        // container browse button
        modelFolderBrowseButton = new Button(containerGroup, SWT.PUSH);
        modelFolderBrowseButton.setText(BROWSE_SHORTHAND);
        modelFolderBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        modelFolderBrowseButton.addListener(SWT.Selection, this);
        modelFolderBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(modelFolderBrowseButton);

        // Add Relationship Type widgets
        Label typeSelectionLabel = new Label(containerGroup, SWT.NONE);
        typeSelectionLabel.setText(SELECT_TYPE_TEXT);
        typeSelectionLabel.setFont(parent.getFont());
        txtRelationshipType = WidgetFactory.createTextField(containerGroup, GridData.FILL_HORIZONTAL, DEFAULT_REL_TYPE);
        txtRelationshipType.setText(DEFAULT_REL_TYPE);
        txtRelationshipType.setEditable(false);

        // Relationship browse button
        relTypeBrowseButton = new Button(containerGroup, SWT.PUSH);
        relTypeBrowseButton.setText(CHANGE_SHORTHAND);
        relTypeBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        relTypeBrowseButton.addListener(SWT.Selection, this);
        relTypeBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(relTypeBrowseButton);
    }

    /**
     * Creates the Sql Dependency generation option controls.
     * <p>
     * The <code>WizardDataTransferPage</code> implementation of this method does nothing. Subclasses wishing to define such
     * components should reimplement this hook method.
     * </p>
     * 
     * @param optionsGroup the parent control
     */
    @Override
    protected void createOptionsGroupButtons( Group optionsGroup ) {
        optionsGroup.setText(OPTIONS_GROUP_TEXT);

        autoGenerateNamesCheckbox = new Button(optionsGroup, SWT.CHECK);
        autoGenerateNamesCheckbox.setSelection(true);
        autoGenerateNamesCheckbox.setFont(optionsGroup.getFont());
        autoGenerateNamesCheckbox.setText(getString("autoRenameRelationships")); //$NON-NLS-1$

        includeTableToTableCheckBox = new Button(optionsGroup, SWT.CHECK);
        includeTableToTableCheckBox.setSelection(true);
        includeTableToTableCheckBox.setFont(optionsGroup.getFont());
        includeTableToTableCheckBox.setText(getString("createTableRelationships")); //$NON-NLS-1$

        includeColumnToColumnCheckbox = new Button(optionsGroup, SWT.CHECK);
        includeColumnToColumnCheckbox.setSelection(true);
        includeColumnToColumnCheckbox.setFont(optionsGroup.getFont());
        includeColumnToColumnCheckbox.setText(getString("createColumnRelationships")); //$NON-NLS-1$

        columnRadioGroup = new Group(optionsGroup, SWT.NONE);
        columnRadioGroup.setText(COLUMN_OPTIONS_TEXT);

        GridData gdRadioGroup = new GridData(GridData.FILL_HORIZONTAL);
        columnRadioGroup.setLayoutData(gdRadioGroup);

        columnRadioGroup.setLayout(new GridLayout());

        radioGenerateAllRels = WidgetFactory.createRadioButton(columnRadioGroup, GEN_ALL_COL_RELS_TEXT, true);
        radioGenerateTargetDescendentRels = WidgetFactory.createRadioButton(columnRadioGroup, GEN_DECENDENT_COL_RELS_TEXT);

        // Composite for Levels selection
        Composite levelsComp = new Composite(optionsGroup, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        levelsComp.setLayout(layout);
        levelsComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        WidgetFactory.createLabel(levelsComp, NLEVELS_LABEL_TEXT, GridData.HORIZONTAL_ALIGN_BEGINNING);
        nLevelsCombo = WidgetFactory.createCombo(levelsComp, SWT.READ_ONLY, GridData.HORIZONTAL_ALIGN_BEGINNING, new ArrayList());
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleModelFolderBrowseButtonPressed() {

        // ==================================
        // launch Location chooser
        // ==================================

        RelationshipTextImportModelSelectorDialog mwdDialog = new RelationshipTextImportModelSelectorDialog(
                                                                                                            UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
        mwdDialog.setValidator(new RelationshipsLocationSelectionValidator());
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();

        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = mwdDialog.getResult();

            // add the selected location to this Relationship
            if (oSelectedObjects.length > 0) {
                setRelationshipLocation(oSelectedObjects[0]);
            }
        }

    }

    /**
     * Opens a relationship type selection dialog and displays the selection in the relationship type text field.
     */
    private void handleBrowseTypeButtonPressed() {

        // ==================================
        // launch Relationship Type chooser
        // ==================================
        RelationshipType relType = null;
        // generate the dialog
        SelectionDialog sdDialog = RelationshipPropertyEditorFactory.createRelationshipTypeSelector(UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                                                    relType);

        // add filters
        ((ModelWorkspaceDialog)sdDialog).addFilter(new ClosedProjectFilter());
        ((ModelWorkspaceDialog)sdDialog).addFilter(new AbstractRelationshipTypeFilter());

        // present it
        sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = sdDialog.getResult();

            // add the selected RelationshipType to this Relationship
            if (oSelectedObjects.length > 0) {
                RelationshipType rt = (RelationshipType)oSelectedObjects[0];
                txtRelationshipType.setText(rt.getName());
            }
        }

    }

    /**
     * Set the relationship target location
     * 
     * @param oLocation the target location
     */
    private void setRelationshipLocation( Object oLocation ) {
        String locationStr = null;
        if (oLocation instanceof IFile) {
            // Let's get the model resource and work from there...
            try {
                targetProject = ((IFile)oLocation).getProject();
                targetResource = ModelUtilities.getModelResource((IFile)oLocation, false);
                targetLocation = targetResource;
            } catch (ModelWorkspaceException err) {
            }
            locationStr = getModelPathStr(targetProject, targetResource);
            this.modelFolderNameField.setText(locationStr);

        } else if (oLocation instanceof EObject) {
            EObject eObj = (EObject)oLocation;
            targetLocation = eObj;
            targetResource = ModelUtilities.getModelResourceForModelObject(eObj);
            try {
                targetProject = targetResource.getUnderlyingResource().getProject();
            } catch (ModelWorkspaceException e) {
                e.printStackTrace();
            }
            locationStr = getModelPathStr(targetProject, targetResource) + DELIM + ModelObjectUtilities.getRelativePath(eObj);
            this.modelFolderNameField.setText(locationStr);
        } else if (oLocation instanceof ModelResource) {
            targetResource = (ModelResource)oLocation;
            targetLocation = targetResource;
            try {
                targetProject = targetResource.getUnderlyingResource().getProject();
            } catch (ModelWorkspaceException e) {
                e.printStackTrace();
            }
            locationStr = getModelPathStr(targetProject, targetResource);
            this.modelFolderNameField.setText(locationStr);
        }

    }

    /**
     * Get the targetLocation in stringified form
     * 
     * @param targetProj the target project
     * @param modelResrc the Model Resource
     * @return the model path string
     */
    private String getModelPathStr( IProject targetProj,
                                    ModelResource modelResrc ) {
        IPath modelPath = modelResrc.getPath();
        // Make relative to project
        modelPath = modelPath.removeFirstSegments(1);
        String relativePath = modelPath.toString();

        String resultPath = targetProject.getName() + DELIM + relativePath;
        return resultPath;
    }

    /**
     * Initialize the SqlDependency helper, using the current target selection
     */
    private void initDependencyHelper() {
        Collection targets = new ArrayList();
        // the selection should be a virtual table
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection)selection;
            if (sel.size() == 1) {
                Object firstObj = sel.getFirstElement();
                if (firstObj != null && TransformationHelper.isVirtualSqlTable(firstObj)) {
                    targets.add(firstObj);
                } else if (firstObj instanceof Collection) {
                    targets = (Collection)firstObj;
                }
            } else {
                targets = SelectionUtilities.getSelectedEObjects((ISelection)selection);
            }
            // Generate RowObjects from raw RowStrings
            this.dependencyHelper = new SqlDependencyRelationshipHelper(targets);
        }
    }

    /**
     * Load the relationship listViewer with the dependencyHelper relationship rows
     */
    private void loadViewer() {
        // Let's initialize the helper here
        clearListViewer();
        if (this.dependencyHelper != null) {
            relationshipDataRows = this.dependencyHelper.getRelationshipRows();
            if (!relationshipDataRows.isEmpty()) {
                loadListViewer(relationshipDataRows);
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
     * @since 4.2
     */
    @Override
    public void setMessage( String newMessage ) {
        super.setMessage(newMessage);
    }

    /**
     * Set the pages completion status
     * 
     * @return 'true' if page is complete, 'false' if not.
     */
    private boolean setCompletionStatus() {
        if (validateDestination()) {
            if (relationshipDataRows.size() > 0) {
                setMessage(FINISH_MESSAGE, IMessageProvider.NONE);
                setPageComplete(true);
                return true;
            }
            setMessage(getString("noRelationshipsToCreateMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        }

        setPageComplete(false);
        return false;
    }

    /**
     * Validate the selected destination.
     * 
     * @return 'true' if the selection is valid, 'false' if not.
     */
    private boolean validateDestination() {
        if (targetResource == null) {
            setMessage(getString("noValidLocationSelectedMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is
     * returned then the wizard will not close.
     * 
     * @return boolean
     */
    public boolean finish() {
        saveWidgetValues();

        // Generate relationships if exists valid relationships and targets
        if (!relationshipDataRows.isEmpty() && targetResource != null && targetLocation != null) {
            generateWithJob(targetResource, targetLocation, relationshipDataRows);
        }

        return true;
    }

    boolean execute( final ModelResource resource,
                     final Object location,
                     final Collection tableRows,
                     IProgressMonitor monitor ) {
        boolean requiredStart = ModelerCore.startTxn(true, true, getString("undoTitle"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.relationshipObjectProcessor.generateObjsFromRowObjs(resource, location, tableRows);
            succeeded = true;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded && !monitor.isCanceled()) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        ModelEditorManager.activate(targetResource, true);
        // Other resources may be been modified (i.e. relationships added to multiple models
        // Make sure they are opened/activated to show "DIRTY" state.
        List otherModifiedResources = new ArrayList(this.relationshipObjectProcessor.getOtherModifiedResources());
        for (Iterator iter = otherModifiedResources.iterator(); iter.hasNext();) {
            ModelEditorManager.activate((ModelResource)iter.next(), true);
        }

        return succeeded;
    }

    private boolean generateWithJob( final ModelResource resource,
                                     final Object location,
                                     final Collection tableRows ) {
        final String message = getString("progressTitle"); //$NON-NLS-1$ 
        final Job job = new Job(message) {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                try {
                    monitor.beginTask(message, tableRows.size());

                    if (!monitor.isCanceled()) {
                        relationshipObjectProcessor.setProgressMonitor(monitor);
                        execute(resource, location, tableRows, monitor);
                    }

                    monitor.done();

                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    return new Status(IStatus.OK, UiConstants.PLUGIN_ID, IStatus.OK, "Finished creating relationships", null); //$NON-NLS-1$
                } catch (Exception e) {
                    UiConstants.Util.log(e);
                    return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, IStatus.ERROR, getString("createError"), e); //$NON-NLS-1$
                } finally {
                }
            }
        };

        job.setSystem(false);
        job.setUser(true);
        job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
        // start as soon as possible
        job.schedule();
        return true;
    }

    public void clearListViewer() {
        listViewer.getList().removeAll();
    }

    public void loadListViewer( Collection rows ) {
        Iterator iter = rows.iterator();
        while (iter.hasNext()) {
            listViewer.add(iter.next());
        }
    }

    public Object[] addButtonSelected() {
        return null;
    }

    public void downButtonSelected( IStructuredSelection selection ) {
    }

    public Object editButtonSelected( IStructuredSelection selection ) {
        return null;
    }

    public void itemsSelected( IStructuredSelection selection ) {
    }

    public Object[] removeButtonSelected( IStructuredSelection selection ) {
        return null;
    }

    public void upButtonSelected( IStructuredSelection selection ) {
    }

    public void setSelection( Object selection ) {
        this.selection = selection;
    }
}

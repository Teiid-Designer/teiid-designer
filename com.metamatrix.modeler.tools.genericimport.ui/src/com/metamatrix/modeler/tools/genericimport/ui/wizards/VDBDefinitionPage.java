/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui.wizards;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.tools.genericimport.ui.UiConstants;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.IListPanelController;

/**
 * VDBDefinitionPage - page on which the user selects the source VDB and model to use in the generic import process. Also the
 * target location for the generated models.
 */
public class VDBDefinitionPage extends WizardDataTransferPage implements UiConstants, IListPanelController {

    private GenericImportManager importManager;

    // VDB selecton controls
    private Button buttonVDBBrowse;
    private Text textFieldVDBName;

    private Combo availableModelsCombo;

    // target location selection controls
    private Text targetLocationField;
    private Button targetLocationBrowseButton;

    // A boolean to indicate if the user has typed anything
    private boolean initializing = false;

    // dialog store id constants
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VDBDefinitionPage.class);
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$

    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    private final static String BROWSE_SHORTHAND = "Browse..."; //$NON-NLS-1$

    /**
     * Creates an instance of this class
     * 
     * @param selection IStructuredSelection
     */
    public VDBDefinitionPage( GenericImportManager importManager ) {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
        this.importManager = importManager;
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

            if (event.widget == buttonVDBBrowse) {
                handleVDBBrowse();
                validate = true;
            }

            if (event.widget == targetLocationBrowseButton) {
                handleTargetLocationBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == targetLocationField) {
                validate = true;
            }

            if (event.widget == this.availableModelsCombo) {
                handleAvailableModelsComboSelectionChanged();
                validate = true;
            }

            if (validate) {
                setCompletionStatus();
            }

            updateWidgetEnablements();
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
     */
    public void createControl( Composite parent ) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        // Create the VDB selection controls
        createSelectVDBComposite(composite);

        // Create the VDB available models combo controls
        createVDBModelsComboComposite(composite);

        // Create the Model Destination location controls
        createModelDestinationComposite(composite);

        restoreWidgetValues();
        updateWidgetEnablements();

        setPageComplete(false);
        setPageComplete(true);
        setMessage(INITIAL_MESSAGE);
        setControl(composite);
    }

    /**
     * Handler for the VDB Browse button pressed
     */
    private void handleVDBBrowse() {
        // Show the selection dialog
        Object[] vdbs = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("browseVDBDialog.title"), //$NON-NLS-1$
                                                                      getString("browseVDBDialog.msg"), //$NON-NLS-1$
                                                                      true,
                                                                      null,
                                                                      this.vdbFilter,
                                                                      this.vdbValidator);

        // Get the selected VDB, set it on the importManager
        if ((vdbs != null) && (vdbs.length > 0) && vdbs[0] instanceof IFile) {
            IFile theFile = (IFile)vdbs[0];
            String vdbFileName = theFile.getName();
            this.textFieldVDBName.setText(vdbFileName);
            if (StringUtil.endsWithIgnoreCase(vdbFileName, ModelerCore.VDB_FILE_EXTENSION)) {
                this.importManager.setSelectedVDB(theFile);
                if (this.importManager.canExecuteVdb()) {
                    // Populate the available models within the vdb
                    populateAvailableModelsCombo();
                }
            }
        }
    }

    /**
     * Populate the available VDB models combo, using the System.Models query ResultSet
     */
    private void populateAvailableModelsCombo() {
        // repopulation should not fire events
        this.availableModelsCombo.removeListener(SWT.Selection, this);

        this.availableModelsCombo.removeAll();

        // Get the list of models in the VDB and populate the comboBox
        List models = null;
        if (this.importManager.canExecuteVdb()) {
            models = this.importManager.getVirtualRelationalModelsForSelectedVDB();
            Iterator iter = models.iterator();
            while (iter.hasNext()) {
                this.availableModelsCombo.add((String)iter.next());
            }
        }

        // re-enable listener and select first item
        this.availableModelsCombo.addListener(SWT.Selection, this);
        if (models != null && !models.isEmpty()) {
            String selectedModel = this.availableModelsCombo.getItem(0);
            this.importManager.setSelectedVDBModel(selectedModel);
            this.availableModelsCombo.select(0);
        }
    }

    /**
     * Handler for Available Models combo selection.
     */
    private void handleAvailableModelsComboSelectionChanged() {
        int index = this.availableModelsCombo.getSelectionIndex();
        String selectedModel = this.availableModelsCombo.getItem(index);
        this.importManager.setSelectedVDBModel(selectedModel);
    }

    /**
     * Handler for Model Target Location Browse button.
     */
    private void handleTargetLocationBrowseButtonPressed() {
        // create the dialog for target location
        FolderSelectionDialog dlg = new FolderSelectionDialog(Display.getCurrent().getActiveShell(),
                                                              new WorkbenchLabelProvider(), new WorkbenchContentProvider());

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
            this.importManager.setTargetLocation(location);
            this.targetLocationField.setText(location.getFullPath().makeRelative().toString());
        }
    }

    /**
     * Create the composite which contains the entity type controls
     */
    protected void createSelectVDBComposite( Composite parent ) {
        Composite vdbComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        vdbComposite.setLayout(layout);
        vdbComposite.setFont(parent.getFont());
        vdbComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        // VDB label
        Label vdbLabel = new Label(vdbComposite, SWT.NONE);
        vdbLabel.setText(getString("vdbLabel")); //$NON-NLS-1$
        vdbLabel.setFont(parent.getFont());

        // VDB name field
        textFieldVDBName = new Text(vdbComposite, SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        textFieldVDBName.setLayoutData(data);
        textFieldVDBName.setFont(parent.getFont());
        textFieldVDBName.setEditable(false);

        // VDB browse button
        buttonVDBBrowse = new Button(vdbComposite, SWT.PUSH);
        buttonVDBBrowse.setText(BROWSE_SHORTHAND);
        buttonVDBBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        buttonVDBBrowse.addListener(SWT.Selection, this);
        buttonVDBBrowse.setFont(parent.getFont());
        setButtonLayoutData(buttonVDBBrowse);
    }

    /**
     * Create the available VDB Models Combo composite
     * 
     * @param parent the parent composite
     */
    private void createVDBModelsComboComposite( Composite parent ) {
        // combo composite
        Composite comboComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        comboComposite.setLayout(layout);
        comboComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        comboComposite.setFont(parent.getFont());

        WidgetFactory.createLabel(comboComposite, getString("vdbModelLabel")); //$NON-NLS-1$
        this.availableModelsCombo = WidgetFactory.createCombo(comboComposite, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);
        this.availableModelsCombo.addListener(SWT.Selection, this);
    }

    /**
     * Create Model Destination Location Controls
     * 
     * @param parent the parent composite
     */
    private void createModelDestinationComposite( Composite parent ) {
        // container specification group
        Composite containerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        // container label
        Label targetLocLabel = new Label(containerGroup, SWT.NONE);
        targetLocLabel.setText(getString("modelsTargetLocation")); //$NON-NLS-1$
        targetLocLabel.setFont(parent.getFont());

        // container name entry field
        targetLocationField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        targetLocationField.addListener(SWT.Modify, this);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        targetLocationField.setLayoutData(data);
        targetLocationField.setFont(parent.getFont());
        targetLocationField.setEditable(false);

        // container browse button
        targetLocationBrowseButton = new Button(containerGroup, SWT.PUSH);
        targetLocationBrowseButton.setText(BROWSE_SHORTHAND);
        targetLocationBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        targetLocationBrowseButton.addListener(SWT.Selection, this);
        targetLocationBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(targetLocationBrowseButton);

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
     * Set the page completion status. Checks the user selections and updates the displayed message and status.
     * 
     * @return the boolean status
     */
    private boolean setCompletionStatus() {
        // ------------------------------------------
        // Validate the VDB selection
        // ------------------------------------------

        // No VDB selected
        if (this.importManager.getSelectedVDB() == null) {
            WizardUtil.setPageComplete(this, getString("noVDBSelected.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return false;
        }

        // VDB selected, check vdb status
        // If there is an error, display the error message
        IStatus vdbStatus = this.importManager.getVdbExecutionStatus();
        if (vdbStatus != null && vdbStatus.getSeverity() == IStatus.ERROR) {
            String message = vdbStatus.getMessage();
            WizardUtil.setPageComplete(this, message, IMessageProvider.ERROR);
            return false;
        }
        // ------------------------------------------
        // Validate the VDB model selection
        // ------------------------------------------
        // First verify that there is at least one model available in the vdb
        if (this.availableModelsCombo.getItemCount() <= 0) {
            WizardUtil.setPageComplete(this, getString("noVirtualRelationalModels.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return false;
        }

        String selectedModel = this.importManager.getSelectedVDBModel();
        if (selectedModel == null) {
            WizardUtil.setPageComplete(this, getString("noModelSelected.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return false;
        }
        // -----------------------------------------------
        // Validate the model target location selection
        // -----------------------------------------------
        if (this.importManager.getTargetLocation() == null) {
            WizardUtil.setPageComplete(this, getString("noTargetLocationSelected.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
            return false;
        }

        WizardUtil.setPageComplete(this);
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
        return true;
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

    /** Filter for selecting VDBs and their parent containers. */
    private ViewerFilter vdbFilter = new ViewerFilter() {
        @Override
        public boolean select( Viewer theViewer,
                               Object theParentElement,
                               Object theElement ) {
            boolean result = false;

            if (theElement instanceof IContainer) {
                IProject project = ((IContainer)theElement).getProject();

                // check for closed project
                if (project.isOpen()) {
                    try {
                        if (project.getNature(ModelerCore.NATURE_ID) != null) {
                            result = true;
                        }
                    } catch (CoreException theException) {
                        String message = "Error checking the open project "; //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, theException, message);
                    }
                }
            } else if (theElement instanceof IFile) {
                result = ModelUtilities.isVdbFile((IFile)theElement);
            } else if (theElement instanceof File) {
                return (((File)theElement).isDirectory());
            }

            return result;
        }
    };

    /** Validator that makes sure the selection is a vdb */
    private ISelectionStatusValidator vdbValidator = new ISelectionStatusValidator() {
        public IStatus validate( Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length > 0)) {
                for (int i = 0; i < theSelection.length; i++) {
                    if ((!(theSelection[i] instanceof IFile)) || !ModelUtilities.isVdbFile((IFile)theSelection[i])) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }

            if (valid) {
                result = new StatusInfo(PLUGIN_ID);
            } else {
                result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("selectionIsNotVDB.msg")); //$NON-NLS-1$
            }

            return result;
        }
    };

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
}

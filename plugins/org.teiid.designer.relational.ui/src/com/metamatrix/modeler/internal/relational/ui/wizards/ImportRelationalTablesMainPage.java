/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relational.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.progress.IProgressConstants;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.relational.ui.textimport.RelationalModelSelectorDialog;
import com.metamatrix.modeler.internal.relational.ui.textimport.RelationalTableLocationSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.IListPanelController;

/**
 * @since 4.2
 */
public class ImportRelationalTablesMainPage extends WizardDataTransferPage implements IListPanelController, ITextImportMainPage {

    public static final String IMPORT_ID = getString("textImport.comboText"); //$NON-NLS-1$
    public static final String IMPORT_DESC = getString("textImport.descriptionText"); //$NON-NLS-1$
    public static final String IMPORT_DATA = getString("textImport.sampleData"); //$NON-NLS-1$

    // widgets
    protected Combo sourceNameField;
    protected Button sourceBrowseButton;
    private Text modelFolderNameField;
    private Button modelFolderBrowseButton;
    private Button chkDefaultToString;
    private boolean useStringDefaultDatatype = false;
    private EObject defaultDatatype;
    private int defaultLength = 255;
    private ListViewer listViewer;

    private ModelResource targetResource;
    private Object targetLocation;
    private Collection rows = Collections.EMPTY_LIST;
    RelationalObjectProcessor relationalObjectProcessor = new RelationalObjectProcessor();

    // A boolean to indicate if the user has typed anything
    boolean entryChanged = false;
    private boolean initializing = false;

    /**
     * Mode flag to open a zip file for reading.
     */
    public static final int OPEN_READ = 0x1;

    private final static String BROWSE_SHORTHAND = "Browse..."; //$NON-NLS-1$
    private static final String FILE_IMPORT_MASK = "*.csv;*.txt"; //$NON-NLS-1$

    // dialog store id constants
    private static final String I18N_PREFIX = "ImportRelationalTablesMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private final static String STORE_SOURCE_NAMES_ID = getString("storeSourceNamesId");//$NON-NLS-1$
    private static final int LARGE_ROWS = 10000;

    static String getString( final String id ) {
        return UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    /**
     * Creates an instance of this class
     * 
     * @param selection IStructuredSelection
     */
    public ImportRelationalTablesMainPage( IStructuredSelection selection ) {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
    }

    public ImportRelationalTablesMainPage() {
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

            if (event.widget == sourceBrowseButton) {
                handleSourceBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == modelFolderBrowseButton) {
                handleModelFolderBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == sourceNameField || event.widget == modelFolderNameField) {
                validate = true;
            }

            if (validate) setCompletionStatus();

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
     * @since 4.2
     */
    public void createControl( Composite parent ) {

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);

        createDestinationGroup(composite);

        createWorkspaceListGroup(composite);

        createOptionsGroup(composite);

        restoreWidgetValues();
        updateWidgetEnablements();

        setPageComplete(false);
        setMessage(INITIAL_MESSAGE);
        setControl(composite);
    }

    /**
     * Create the group for creating the root directory
     */
    protected void createSourceGroup( Composite parent ) {
        Composite sourceContainerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        sourceContainerGroup.setLayout(layout);
        sourceContainerGroup.setFont(parent.getFont());
        sourceContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label groupLabel = new Label(sourceContainerGroup, SWT.NONE);
        groupLabel.setText(getString("groupLabel")); //$NON-NLS-1$
        groupLabel.setFont(parent.getFont());

        // source name entry field
        sourceNameField = new Combo(sourceContainerGroup, SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        sourceNameField.setLayoutData(data);
        sourceNameField.setFont(parent.getFont());

        sourceNameField.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                updateFromSourceField();
                setCompletionStatus();
            }
        });

        sourceNameField.addKeyListener(new KeyListener() {

            /*
             * @see KeyListener.keyPressed
             */
            public void keyPressed( KeyEvent e ) {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;
            }

            /*
             * @see KeyListener.keyReleased
             */
            public void keyReleased( KeyEvent e ) {
            }
        });

        sourceNameField.addFocusListener(new FocusListener() {

            /*
             * @see FocusListener.focusGained(FocusEvent)
             */
            public void focusGained( FocusEvent e ) {
                // Do nothing when getting focus
            }

            /*
             * @see FocusListener.focusLost(FocusEvent)
             */
            public void focusLost( FocusEvent e ) {
                // Clear the flag to prevent constant update
                if (entryChanged) {
                    entryChanged = false;
                }

            }
        });

        // source browse button
        sourceBrowseButton = new Button(sourceContainerGroup, SWT.PUSH);
        sourceBrowseButton.setText(getString("browse_1")); //$NON-NLS-1$
        sourceBrowseButton.addListener(SWT.Selection, this);
        sourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        sourceBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(sourceBrowseButton);
    }

    /**
     * Method to create List box control group for displaying current zip file project list.
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
        listViewer.getControl().setLayoutData(data);
    }

    /**
     * Creates the import destination specification controls.
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
        modelFolderNameField.addListener(SWT.Modify, this);
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

    }

    @Override
    protected void createOptionsGroup( Composite parent ) {
        // options group
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(getString("options")); //$NON-NLS-1$
        optionsGroup.setFont(parent.getFont());

        WidgetFactory.createLabel(optionsGroup, SWT.NONE, getString("label.defaultToString")); //$NON-NLS-1$

        this.chkDefaultToString = WidgetFactory.createCheckBox(optionsGroup, "", SWT.NONE, (1)); //$NON-NLS-1$
        this.chkDefaultToString.setToolTipText(getString("checkbox.defaultToString")); //$NON-NLS-1$
        this.chkDefaultToString.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleDefaultToStringSelected(theEvent);
            }
        });
    }

    void handleDefaultToStringSelected( SelectionEvent theEvent ) {
        this.useStringDefaultDatatype = ((Button)theEvent.getSource()).getSelection();
        if (useStringDefaultDatatype) {
            try {
                defaultDatatype = ModelerCore.getWorkspaceDatatypeManager().findDatatype("STRING"); //$NON-NLS-1$
            } catch (ModelerCoreException err) {
            }
        }
    }

    /**
     * Open an appropriate source browser so that the user can specify a source to import from
     */
    protected void handleSourceBrowseButtonPressed() {
        String selectedFile = queryFileToImport();
        clearListViewer();
        if (selectedFile != null) {
            if (!selectedFile.equals(sourceNameField.getText())) {
                sourceNameField.setText(selectedFile);
                // Need to call the method to update the project list because source (zip file) may have changed.
                this.rows = this.relationalObjectProcessor.loadLinesFromFile(sourceNameField.getText());
                loadListViewer(this.rows);
            }
        }
    }

    protected void updateFromSourceField() {
        clearListViewer();
        this.rows = this.relationalObjectProcessor.loadLinesFromFile(sourceNameField.getText());
        loadListViewer(this.rows);
    }

    /**
     * Opens a file selection dialog and returns a string representing the selected file, or <code>null</code> if the dialog was
     * canceled.
     */
    protected String queryFileToImport() {
        FileDialog dialog = new FileDialog(sourceNameField.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] {FILE_IMPORT_MASK});

        String currentSourceString = sourceNameField.getText();
        int lastSeparatorIndex = currentSourceString.lastIndexOf(java.io.File.separator);
        if (lastSeparatorIndex != -1) dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));

        return dialog.open();
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleModelFolderBrowseButtonPressed() {

        // ==================================
        // launch Location chooser
        // ==================================

        RelationalModelSelectorDialog mwdDialog = new RelationalModelSelectorDialog(
                                                                                    UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
        mwdDialog.setValidator(new RelationalTableLocationSelectionValidator());
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();

        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] oSelectedObjects = mwdDialog.getResult();

            // add the selected location to this Relationship
            if (oSelectedObjects.length > 0) {
                setObjectLocation(oSelectedObjects[0]);
            }
        }

    }

    private void setObjectLocation( Object oLocation ) {
        if (oLocation instanceof IFile) {
            // Let's get the model resource and work from there...
            this.modelFolderNameField.setText(((IFile)oLocation).getName());
            try {
                targetResource = ModelUtil.getModelResource((IFile)oLocation, false);
                targetLocation = targetResource;
            } catch (ModelWorkspaceException err) {
            }

        } else if (oLocation instanceof EObject) {
            EObject eObj = (EObject)oLocation;
            targetLocation = eObj;
            targetResource = ModelUtilities.getModelResourceForModelObject(eObj);
            String locationStr = targetResource.getItemName() + '/' + ModelObjectUtilities.getRelativePath(eObj);
            this.modelFolderNameField.setText(locationStr);
        } else if (oLocation instanceof ModelResource) {
            targetResource = (ModelResource)oLocation;
            targetLocation = targetResource;
            String locationStr = targetResource.getItemName();
            this.modelFolderNameField.setText(locationStr);
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

    boolean setCompletionStatus() {
        if (validateSource() && validateDestination()) {
            setErrorMessage(null);
            setMessage(INITIAL_MESSAGE);
            setPageComplete(true);
            return true;
        }

        setPageComplete(false);
        return false;
    }

    private boolean validateDestination() {
        if (targetResource == null) {
            setErrorMessage(getString("noValidLocationSelectedMessage")); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    private boolean validateSource() {
        if (sourceNameField == null) {
            setErrorMessage(getString("noValidSourceSelectedMessage")); //$NON-NLS-1$
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

        // Generate RowObjects from raw RowStrings
        Collection tableRows = this.relationalObjectProcessor.createRowObjsFromStrings(this.rows);

        if (!tableRows.isEmpty() && relationalObjectProcessor.confirmLargeImport(this.getShell(), tableRows.size(), LARGE_ROWS)) {
            generateWithJob(tableRows);
        }

        return true;
    }

    boolean execute( final Collection tableRows,
                     IProgressMonitor monitor ) {
        boolean requiredStart = ModelerCore.startTxn(false, false, getString("transactionTitle"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.relationalObjectProcessor.generateObjsFromRowObjs(targetResource,
                                                                   targetLocation,
                                                                   tableRows,
                                                                   useStringDefaultDatatype,
                                                                   defaultDatatype,
                                                                   defaultLength);
            succeeded = true;
        } catch (Exception ex) {
            UiConstants.Util.log(IStatus.ERROR, ex, getString("importError")); //$NON-NLS-1$
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
        if (succeeded) {
            ModelEditorManager.activate(targetResource, true);

            // Other resources may be been modified (i.e. relationships added to multiple models
            // Make sure they are opened/activated to show "DIRTY" state.
            List otherModifiedResources = new ArrayList(this.relationalObjectProcessor.getOtherModifiedResources());
            for (Iterator iter = otherModifiedResources.iterator(); iter.hasNext();) {
                ModelEditorManager.activate((ModelResource)iter.next(), true);
            }
        }

        return succeeded;
    }

    private boolean generateWithJob( final Collection tableRows ) {
        final String message = getString("progressTitle"); //$NON-NLS-1$ 
        final Job job = new Job(message) {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                try {
                    monitor.beginTask(message, tableRows.size());

                    if (!monitor.isCanceled()) {
                        relationalObjectProcessor.setProgressMonitor(monitor);
                        execute(tableRows, monitor);
                    }

                    monitor.done();

                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    return new Status(IStatus.OK, UiConstants.PLUGIN_ID, IStatus.OK, AbstractObjectProcessor.FINISHED, null);
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

    private void clearListViewer() {
        org.eclipse.swt.widgets.List contents = listViewer.getList();
        String[] items = contents.getItems();
        listViewer.remove(items);
    }

    private void loadListViewer( Collection rows ) {
        Iterator iter = rows.iterator();
        while (iter.hasNext()) {
            String rowStr = (String)iter.next();
            listViewer.add(rowStr);
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

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        WidgetUtil.removeMissingResources(settings, STORE_SOURCE_NAMES_ID);
        if (settings != null) {
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null) return; // ie.- no values stored, so stop

            // set filenames history
            for (int i = 0; i < sourceNames.length; i++)
                sourceNameField.add(sourceNames[i]);
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    @Override
    public void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // update source names history
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null) sourceNames = new String[0];

            sourceNames = addToHistory(sourceNames, sourceNameField.getText());
            settings.put(STORE_SOURCE_NAMES_ID, sourceNames);

        }
    }

    /**
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage#getComboText()
     */
    public String getComboText() {
        return IMPORT_ID;
    }

    /**
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage#getDescriptionText()
     */
    public String getDescriptionText() {
        return IMPORT_DESC;
    }

    /**
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage#getSampleDataText()
     */
    public String getSampleDataText() {
        return IMPORT_DATA;
    }

    /**
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage#getType()
     */
    public String getType() {
        return IMPORT_ID;
    }
}

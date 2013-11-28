/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.wizards;

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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.textimport.RelationalModelSelectorDialog;
import org.teiid.designer.tools.textimport.ui.wizards.AbstractObjectProcessor;
import org.teiid.designer.tools.textimport.ui.wizards.ITextImportMainPage;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.ListContentProvider;
import org.teiid.designer.ui.common.widget.IListPanelController;
import org.teiid.designer.ui.editors.ModelEditorManager;


/**
 * 
 *
 * @since 8.0
 */
public class RelationalModelTextImporterMainPage extends WizardDataTransferPage implements IListPanelController, ITextImportMainPage {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalModelTextImporterMainPage.class);
    
    public static final String IMPORT_ID = getString("textImport.comboText"); //$NON-NLS-1$
    public static final String IMPORT_DESC = getString("textImport.descriptionText"); //$NON-NLS-1$
    public static final String IMPORT_DATA = getString("textImport.sampleData"); //$NON-NLS-1$

    // widgets
    protected Combo sourceNameField;
    protected Button sourceBrowseButton;
    private Text modelFolderNameField;
    private Button modelFolderBrowseButton;
    private ListViewer listViewer;

    private ModelResource targetResource;
    @SuppressWarnings( "unused" )
    private Object targetLocation;
    private Collection rows = Collections.EMPTY_LIST;
    RelationalModelXmlTextFileProcessor relationalProcessor = new RelationalModelXmlTextFileProcessor();

    // A boolean to indicate if the user has typed anything
    boolean entryChanged = false;
    private boolean initializing = false;

    /**
     * Mode flag to open a zip file for reading.
     */
    public static final int OPEN_READ = 0x1;

    private final static String BROWSE_SHORTHAND = "Browse..."; //$NON-NLS-1$
    private static final String FILE_IMPORT_MASK = "*.xml"; //$NON-NLS-1$

    // dialog store id constants

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private final static String STORE_SOURCE_NAMES_ID = getString("storeSourceNamesId");//$NON-NLS-1$
    
    static final String OVERWRITE_TITLE = UiConstants.Util.getString("OverwriteObjectsDialog.duplicateObjectsExistTitle"); //$NON-NLS-1$
    static final String OVERWRITE_OPTIONS = UiConstants.Util.getString("OverwriteObjectsDialog.optionsGroup"); //$NON-NLS-1$
    static final String OVERWRITE_REPLACE = UiConstants.Util.getString("OverwriteObjectsDialog.replaceExistingObjectsButton"); //$NON-NLS-1$
    static final String OVERWRITE_CREATE = UiConstants.Util.getString("OverwriteObjectsDialog.createNewObjectsButton"); //$NON-NLS-1$
    static final String OVERWRITE_CANCEL = UiConstants.Util.getString("OverwriteObjectsDialog.cancelImportButton"); //$NON-NLS-1$
    static final String OVERWRITE_DUPLICATE_OBJECTS = UiConstants.Util.getString("OverwriteObjectsDialog.duplicateObjectsGroup"); //$NON-NLS-1$

    static String getString( final String id ) {
        return UiConstants.Util.getString(I18N_PREFIX + id);
    }
    
    static String getString( final String id , final Object param) {
        return UiConstants.Util.getString(I18N_PREFIX + id, param);
    }

    /**
     * Creates an instance of this class
     * 
     * @param selection IStructuredSelection
     */
    public RelationalModelTextImporterMainPage( IStructuredSelection selection ) {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
    }

    public RelationalModelTextImporterMainPage() {
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
    @Override
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
    @Override
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
        sourceNameField.setBackground(sourceContainerGroup.getBackground());

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
            @Override
			public void keyPressed( KeyEvent e ) {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;
            }

            /*
             * @see KeyListener.keyReleased
             */
            @Override
			public void keyReleased( KeyEvent e ) {
            }
        });

        sourceNameField.addFocusListener(new FocusListener() {

            /*
             * @see FocusListener.focusGained(FocusEvent)
             */
            @Override
			public void focusGained( FocusEvent e ) {
                // Do nothing when getting focus
            }

            /*
             * @see FocusListener.focusLost(FocusEvent)
             */
            @Override
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
        modelFolderNameField.setBackground(containerGroup.getBackground());

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
                this.rows = this.relationalProcessor.loadLinesFromFile(sourceNameField.getText());
                loadListViewer(this.rows);
            }
        }
    }

    protected void updateFromSourceField() {
        clearListViewer();
        this.rows = this.relationalProcessor.loadLinesFromFile(sourceNameField.getText());
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
        mwdDialog.setValidator(new RelationalModelLocationSelectionValidator());
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
        if (validateSource() && validateProcessor() && validateDestination()) {
            setErrorMessage(null);
            if( this.relationalProcessor.getStatusInfo().isOK() ) {
                setMessage(INITIAL_MESSAGE);
            } else if( this.relationalProcessor.getStatusInfo().isWarning() ) {
                setMessage(this.relationalProcessor.getStatusInfo().getMessage(), IMessageProvider.WARNING);
            }
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
            setErrorMessage(getString("noValidSourceSelectedMessage", sourceNameField)); //$NON-NLS-1$
            return false;
        }
        return true;
    }
    
    private boolean validateProcessor() {
        if( relationalProcessor.getStatusInfo().isError() ) {
            setErrorMessage(relationalProcessor.getStatusInfo().getMessage());
            return false;
        } else if( relationalProcessor.getStatusInfo().isWarning() ) {
            setErrorMessage(null);
            setMessage(relationalProcessor.getStatusInfo().getMessage());
            return true;
        }
        
        return true;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is
     * returned then the wizard will not close.
     * 
     * @return boolean
     */
    @Override
	public boolean finish() {

        saveWidgetValues();
        
        // Check if model has existing objects that might be overridden, then warn user?
        boolean doGenerate = checkModelForExistingChildren();

        //Process the rows of data
        if( doGenerate ) {
            generateWithJob();
        }

        return true;
    }
    
    private boolean checkModelForExistingChildren() {
        Collection<EObject> existingChildren = new ArrayList<EObject>();
        List existingChildrenNames = new ArrayList<String>();
        Collection<RelationalReference> existingChildrenRefs = new ArrayList<RelationalReference>();
        
        try {
            Collection<EObject> children = targetResource.getEmfResource().getContents();
            
            if( children.isEmpty() || this.relationalProcessor.getRelationalModel() == null) {
                return false;
            }
            
            for( EObject child : children ) {
                String name = ModelerCore.getModelEditor().getName(child);
                
                if( name != null && this.relationalProcessor.getRelationalModel().hasChild(name) && !existingChildrenNames.contains(name)) {
                    existingChildrenRefs.add(this.relationalProcessor.getRelationalModel().getChildWithName(name));
                    existingChildren.add(child);
                    existingChildrenNames.add(name);
                }
            }
            
        } catch (ModelWorkspaceException e) {
        }
        
        if( !existingChildren.isEmpty() ) {
//            final List messgs = existingChildrenNames;
//            
//            final String title = "Duplicate Objects In Model";
//            final String message = "Duplicate named objects exist in selected model.\n\n" +
//                "Choose option to replace existing objects, or add duplicates.";
            
            OverwriteObjectsDialog depDialog = new OverwriteObjectsDialog(
                                  Display.getCurrent().getActiveShell(), 
                                  existingChildrenRefs, this.relationalProcessor);
            int result = depDialog.open();
            
            if (result == Window.OK) {
//                Object[] relRefs = depDialog.getResult();
//                for( int i=0; i< relRefs.length; i++ ) {
//                    RelationalReference ref = (RelationalReference)relRefs[i];
//                    
//                }
                
                return true;
            }
            
            return false; 
        }
        
        return true;
    }

    boolean execute( IProgressMonitor monitor ) {
        boolean requiredStart = ModelerCore.startTxn(false, false, getString("transactionTitle"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.relationalProcessor.buildModel(targetResource, monitor);
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
        }

        return succeeded;
    }

    private boolean generateWithJob() {
        final String message = getString("progressTitle"); //$NON-NLS-1$ 
        final Job job = new Job(message) {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                try {
                    monitor.beginTask(message, relationalProcessor.getRelationalModel().getChildren().size() + 2);

                    if (!monitor.isCanceled()) {
                        relationalProcessor.setProgressMonitor(monitor);
                        execute(monitor);
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

    @Override
	public Object[] addButtonSelected() {
        return null;
    }

    @Override
	public void downButtonSelected( IStructuredSelection selection ) {
    }

    @Override
	public Object editButtonSelected( IStructuredSelection selection ) {
        return null;
    }

    @Override
	public void itemsSelected( IStructuredSelection selection ) {
    }

    @Override
	public Object[] removeButtonSelected( IStructuredSelection selection ) {
        return null;
    }

    @Override
	public void upButtonSelected( IStructuredSelection selection ) {
    }        
    // the root element to populate the viewer with
//  private Object inputElement;

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
     * @see org.teiid.designer.tools.textimport.ui.wizards.ITextImportMainPage#getComboText()
     */
    @Override
	public String getComboText() {
        return IMPORT_ID;
    }

    /**
     * @see org.teiid.designer.tools.textimport.ui.wizards.ITextImportMainPage#getDescriptionText()
     */
    @Override
	public String getDescriptionText() {
        return IMPORT_DESC;
    }

    /**
     * @see org.teiid.designer.tools.textimport.ui.wizards.ITextImportMainPage#getSampleDataText()
     */
    @Override
	public String getSampleDataText() {
        return IMPORT_DATA;
    }

    /**
     * @see org.teiid.designer.tools.textimport.ui.wizards.ITextImportMainPage#getType()
     */
    @Override
	public String getType() {
        return IMPORT_ID;
    }
    
    /**
     * Inner class to display dialog showing objects to be created that have names of objects that aleady exist in the target
     * model. This dialog should not be displayed if there are no duplicate names.
     */
    class OverwriteObjectsDialog extends SelectionDialog {
        //============================================================================================================================
        // Constants
        // sizing constants
        private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;
        private final static int SIZING_SELECTION_WIDGET_WIDTH = 350;
        
        //============================================================================================================================
        // Variables
        
        RelationalModelXmlTextFileProcessor processor;

        // providers for populating this dialog
        private ILabelProvider labelProvider;

        // the visual selection widget group
        CheckboxTableViewer listViewer;

        private CLabel statusLine;
        
        Collection<RelationalReference> duplicateObjs;
        
        private int proccessType = RelationalReference.REPLACE;
        
        //============================================================================================================================
        // Widgets
        private Button replaceOptionButton, createOptionButton, cancelOptionButton; // appendNameOptionButton, 
        
        //============================================================================================================================
        // Constructors
            
        /**
         * 
         * @param parent
         * @param title
         * @since 4.0
         */
        public OverwriteObjectsDialog(final Shell shell, 
                                      Collection<RelationalReference> duplicateObjs, 
                                      RelationalModelXmlTextFileProcessor processor) {
            super(shell);
            this.setTitle(OVERWRITE_TITLE);
            this.labelProvider = new RelationalReferenceLabelProvider();
            this.processor = processor;
            this.duplicateObjs = new ArrayList<RelationalReference>(duplicateObjs);
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
         // page group
            Composite composite = (Composite)super.createDialogArea(parent);
            composite.setLayout(new GridLayout());
            composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
            
            Font font = parent.getFont();
            composite.setFont(font);

            createMessageArea(composite);
            
            Group optionsGroup = WidgetFactory.createGroup(parent, OVERWRITE_OPTIONS, SWT.BORDER | SWT.FILL);   
            optionsGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
            
            { // ========================= OPTIONS ============================
                //WidgetFactory.createLabel(optionsGroup, GridData.FILL_HORIZONTAL, MODEL_OBJECT_NAMES_DESCRIPTION, SWT.WRAP);
                this.replaceOptionButton = WidgetFactory.createRadioButton(optionsGroup, OVERWRITE_REPLACE, true);
                
                this.replaceOptionButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected( final SelectionEvent event ) {
                        processWidgetSelected();
                        
                        checkStatus();
                    }
                });
                this.createOptionButton = WidgetFactory.createRadioButton(optionsGroup, OVERWRITE_CREATE, false);
                this.createOptionButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected( final SelectionEvent event ) {
                        processWidgetSelected();
                        checkStatus();
                    }
                });
//                this.appendNameOptionButton = WidgetFactory.createRadioButton(optionsGroup, "Make all names unique (i.e. ProductsID_1", false);
//                this.appendNameOptionButton.addSelectionListener(new SelectionAdapter() {
//
//                    @Override
//                    public void widgetSelected( final SelectionEvent event ) {
//                        processWidgetSelected();
//                    }
//                });
                this.cancelOptionButton = WidgetFactory.createRadioButton(optionsGroup, OVERWRITE_CANCEL, false);
                this.cancelOptionButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected( final SelectionEvent event ) {
                        processWidgetSelected();
                        checkStatus();
                    }
                });
            }
            
            Group duplicateObjectsGroup = WidgetFactory.createGroup(parent, OVERWRITE_DUPLICATE_OBJECTS, SWT.FILL);
            duplicateObjectsGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
            
            listViewer = CheckboxTableViewer.newCheckList(duplicateObjectsGroup, SWT.BORDER);
            GridData data = new GridData(GridData.FILL_BOTH);
            data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
            data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
            listViewer.getTable().setLayoutData(data);

            listViewer.setLabelProvider(labelProvider);
            listViewer.setContentProvider(new ListContentProvider());
            listViewer.getControl().setFont(font);
            listViewer.setSorter(new ViewerSorter() {});

            addSelectionButtons(duplicateObjectsGroup);

            initializeViewer();

            // initialize page
            checkInitialSelections();

            getViewer().addCheckStateListener(new ICheckStateListener() {
                @Override
				public void checkStateChanged( CheckStateChangedEvent event ) {
                    checkStatus();
                }
            });

            statusLine = new CLabel(duplicateObjectsGroup, SWT.LEFT);
            statusLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            statusLine.setText(""); //$NON-NLS-1$   
            statusLine.setImage(null);
            statusLine.setFont(parent.getFont());

            return composite;
        }

        /**
         * Add the selection and deselection buttons to the dialog.
         * 
         * @param composite org.eclipse.swt.widgets.Composite
         */
        private void addSelectionButtons( Composite composite ) {

            Composite buttonComposite = new Composite(composite, SWT.RIGHT);
            buttonComposite.setFont(composite.getFont());
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            buttonComposite.setLayout(layout);
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
            data.grabExcessHorizontalSpace = true;
            composite.setData(data);

            Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, InternalUiConstants.Widgets.SELECT_ALL_BUTTON, false);

            SelectionListener listener = new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    listViewer.setAllChecked(true);
                    checkStatus();
                }
            };
            selectButton.addSelectionListener(listener);

            Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, InternalUiConstants.Widgets.DESELECT_ALL_BUTTON, false);

            listener = new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    listViewer.setAllChecked(false);
                    checkStatus();
                }
            };
            deselectButton.addSelectionListener(listener);

        }
        
        void processWidgetSelected() {
            if( replaceOptionButton.getSelection() ) {
                this.proccessType = RelationalReference.REPLACE;
            } else if( createOptionButton.getSelection() ) {
                this.proccessType = RelationalReference.CREATE_ANYWAY;
            } 
//            else if( appendNameOptionButton.getSelection() ) {
//                this.proccessType = RelationalReference.CREATE_UNIQUE_NAME;
//            } 
            else {
                this.proccessType = RelationalReference.IGNORE;
            }
        }
        
        /**
         * Visually checks the previously-specified elements in this dialog's list viewer.
         */
        private void checkInitialSelections() {
            int nItems = this.listViewer.getTable().getItemCount();
            for( int i=0; i<nItems; i++ ) {
                listViewer.setChecked(this.listViewer.getElementAt(i), true);
            }
            
            processWidgetSelected();
            
            checkStatus();
        }
        
        void checkStatus() {
            int nItemsChecked = 0;
            int nItems = this.listViewer.getTable().getItemCount();
            for( int i=0; i<nItems; i++ ) {
                RelationalReference element = ((RelationalReference)this.listViewer.getElementAt(i));
                if( this.listViewer.getChecked(element) ) {
                    nItemsChecked++;
                    element.setDoProcessType(this.proccessType);
                } else {
                    element.setDoProcessType(RelationalReference.IGNORE);
                }
                
            }
            if( nItemsChecked == 0 ) {
                setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, getString("noDuplicateObjectsWillBeProcessed"))); //$NON-NLS-1$
            }
        }

        /**
         * Returns the viewer used to show the list.
         * 
         * @return the viewer, or <code>null</code> if not yet created
         */
        protected CheckboxTableViewer getViewer() {
            return listViewer;
        }

        /**
         * Initializes this dialog's viewer after it has been laid out.
         */
        private void initializeViewer() {
            listViewer.setInput(duplicateObjs);
        }

        /**
         * The <code>ListSelectionDialog</code> implementation of this <code>Dialog</code> method builds a list of the selected
         * elements for later retrieval by the client and closes this dialog.
         */
        @Override
        protected void okPressed() {

            // Get the input children.
            Object[] children = ((IStructuredContentProvider)this.listViewer.getContentProvider()).getElements(duplicateObjs);

            // Build a list of selected children.
            if (children != null) {
                ArrayList list = new ArrayList();
                for (int i = 0; i < children.length; ++i) {
                    Object element = children[i];
                    if (listViewer.getChecked(element)) list.add(element);
                }
                setResult(list);
            }

            super.okPressed();
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#create()
         */
        @Override
        public void create() {
            setShellStyle(getShellStyle() | SWT.RESIZE);
            super.create();
        }

        private void setStatus( IStatus status ) {
            if (status != null) {
                String message = status.getMessage();
                if (message != null && message.length() > 0) {
                    statusLine.setText(message);
                    statusLine.setImage(findImage(status));
                    statusLine.setBackground(JFaceColors.getErrorBackground(
                            statusLine.getDisplay()));
                }
            } else {
                statusLine.setText(""); //$NON-NLS-1$   
                statusLine.setImage(null);
            }

            if (status == null || status.isOK()) {
                getButton(Window.OK).setEnabled(true);
            } else {
                getButton(Window.OK).setEnabled(false);
            }
        }
        
        private Image findImage(IStatus status) {
            if (status.isOK()) {
                return null;
            } else if (status.matches(IStatus.ERROR)) {
                return PlatformUI.getWorkbench().getSharedImages().getImage(
                        ISharedImages.IMG_OBJS_ERROR_TSK);
            } else if (status.matches(IStatus.WARNING)) {
                return PlatformUI.getWorkbench().getSharedImages().getImage(
                        ISharedImages.IMG_OBJS_WARN_TSK);
            } else if (status.matches(IStatus.INFO)) {
                return PlatformUI.getWorkbench().getSharedImages().getImage(
                        ISharedImages.IMG_OBJS_INFO_TSK);
            }
            return null;
        }
    }
    
    class RelationalReferenceLabelProvider implements ILabelProvider {

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if( element instanceof RelationalReference ) {
                return ((RelationalReference)element).getName();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void addListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void removeListener( ILabelProviderListener listener ) {
        }
        
    }

}
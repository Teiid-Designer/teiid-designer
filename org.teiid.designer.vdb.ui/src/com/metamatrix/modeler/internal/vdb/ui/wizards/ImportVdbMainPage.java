/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.internal.dialog.FileSystemDialog;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.ListContentProvider;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

public class ImportVdbMainPage extends WizardDataTransferPage implements VdbUiConstants {

    private final static int SIZING_LISTS_HEIGHT = 200;

    // dialog store id constants
    private static final String I18N_PREFIX = "ImportVdbMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$
    private static final String FILE_IMPORT_MASK = "*.vdb;";//$NON-NLS-1$

    private final static String STORE_FILE_SYSTEM_SOURCE_NAMES_ID = getString("storeFileSystemSourceNamesId");//$NON-NLS-1$
    private final static String STORE_WORKSPACE_SOURCE_NAMES_ID = getString("storeWorkspaceSourceNamesId");//$NON-NLS-1$

    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String SOURCE_EMPTY_MESSAGE = getString("sourceEmpty"); //$NON-NLS-1$
    private static final String EMPTY_FOLDER_MESSAGE = getString("emptyFolderMessage"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    // constants used in the validator inner class
    static final IStatus STATUS_OK = new StatusInfo(PLUGIN_ID);
    static final IStatus STATUS_ERROR = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("validationError")); //$NON-NLS-1$

    public static int availableRows( final Composite parent ) {

        final int fontHeight = (parent.getFont().getFontData())[0].getHeight();
        final int displayHeight = parent.getDisplay().getClientArea().height;

        return displayHeight / fontHeight;
    }

    static String getString( final String id ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    private static String getString( final String id,
                                     final Object object ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id, object);
    }

    private final String initialContainerFieldValue = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toString();
    // widgets
    Combo fileSystemSourceNameField;
    private Button fileSystemSourceBrowseButton;
    String fileSystemSourceFile;

    Combo workspaceSourceNameField;
    private Button workspaceSourceBrowseButton;
    String workspaceSourceFile;
    private Button overwriteExistingResourcesCheckbox;

    private Button importFromFileSystemCheckbox;
    private Button importFromWorkspaceCheckbox;
    private Button loadVdbIntoProjectCheckbox;
    Text containerNameField;
    private Button containerBrowseButton;
    private TableViewer projectViewer;

    private Text vdbLocationNameField;
    private Button vdbLocationBrowseButton;

    private IContainer vdbLocation;

    // A boolean to indicate if the user has typed anything
    boolean entryChanged = false;

    private boolean initializing = false;

    /** Filter for selecting VDB files. */
    private final ViewerFilter vdbFilter = new ViewerFilter() {
        @Override
        public boolean select( final Viewer theViewer,
                               final Object theParentElement,
                               final Object theElement ) {
            boolean result = false;

            if (theElement instanceof IContainer) {
                final IProject project = ((IContainer)theElement).getProject();

                // check for closed project
                if (project.isOpen()) try {
                    if (project.getNature(ModelerCore.NATURE_ID) != null) result = true;
                } catch (final CoreException theException) {
                    VdbUiConstants.Util.log(theException);
                }
            } else if (theElement instanceof IFile) result = ModelUtilities.isVdbFile((IFile)theElement);
            else if (theElement instanceof File) return (((File)theElement).isDirectory() || ((File)theElement).getName().endsWith(".vdb")); //$NON-NLS-1$

            return result;
        }
    };

    /** Validator that makes sure the selection containes all WSDL files. */
    private final ISelectionStatusValidator vdbValidator = new ISelectionStatusValidator() {
        public IStatus validate( final Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length == 1)) {
                for (int i = 0; i < theSelection.length; i++)
                    if ((!(theSelection[i] instanceof IFile)) || !ModelUtilities.isVdbFile((IFile)theSelection[i])) {
                        valid = false;
                        break;
                    }
            } else valid = false;

            if (valid) result = new StatusInfo(PLUGIN_ID);
            else result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("noVdbSelected")); //getString("msg.selectionIsNotVdb")); //$NON-NLS-1$

            return result;
        }
    };

    /** Filter for selecting WSDL files and their parent containers. */
    private final ViewerFilter vdbLocationFilter = new ViewerFilter() {
        @Override
        public boolean select( final Viewer theViewer,
                               final Object theParentElement,
                               final Object theElement ) {
            boolean result = false;

            if (theElement instanceof IContainer) {
                final IProject project = ((IContainer)theElement).getProject();

                // check for closed project
                if (project.isOpen()) try {
                    if (project.getNature(ModelerCore.NATURE_ID) != null) result = true;
                } catch (final CoreException theException) {
                    VdbUiConstants.Util.log(theException);
                }
            } else if (theElement instanceof File) return (((File)theElement).isDirectory());

            return result;
        }
    };

    /** Validator that makes sure the selection containes all WSDL files. */
    private final ISelectionStatusValidator vdbLocationValidator = new ISelectionStatusValidator() {
        public IStatus validate( final Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length == 1)) {
                final Object selectedElement = theSelection[0];
                if (selectedElement instanceof IContainer) valid = true;
                else if (selectedElement instanceof File) {
                    final File selectedFile = (File)selectedElement;
                    if (!selectedFile.isDirectory()) valid = false;
                }
            } else valid = false;

            if (valid) result = new StatusInfo(PLUGIN_ID);
            else result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("noProjectOrFolderSelected")); //getString("msg.selectionIsNotVdb")); //$NON-NLS-1$

            return result;
        }
    };

    public ImportVdbMainPage() {
        super(PAGE_TITLE);
        setTitle(PAGE_TITLE);
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
     * Called when the user presses the Cancel button. Return a boolean indicating permission to close the wizard.
     */
    public boolean cancel() {
        return true;
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
    protected Button createButton( final Composite parent,
                                   final int id,
                                   final String label,
                                   final boolean defaultButton ) {
        // increment the number of columns in the button bar
        ((GridLayout)parent.getLayout()).numColumns++;

        final Button button = new Button(parent, SWT.PUSH);
        button.setFont(parent.getFont());

        final GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
        button.setLayoutData(buttonData);

        button.setData(new Integer(id));
        button.setText(label);

        if (defaultButton) {
            final Shell shell = parent.getShell();
            if (shell != null) shell.setDefaultButton(button);
            button.setFocus();
        }
        return button;
    }

    /**
     * Method declared on IDialogPage.
     */
    public void createControl( final Composite parent ) {
        initializing = true;

        initializeDialogUnits(parent);

        final Composite topComposite = new Composite(parent, SWT.NULL);
        topComposite.setLayout(new GridLayout());
        topComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topComposite.setSize(topComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        topComposite.setFont(parent.getFont());

        // Add File System checkbox
        importFromFileSystemCheckbox = new Button(topComposite, SWT.RADIO);
        importFromFileSystemCheckbox.setFont(topComposite.getFont());
        importFromFileSystemCheckbox.setSelection(true);
        importFromFileSystemCheckbox.addListener(SWT.Selection, this);
        importFromFileSystemCheckbox.setText(getString("importFromFileSystem")); //getString("overwriteExisting")); //$NON-NLS-1$

        createFromFileSystemGroup(topComposite);

        new Label(topComposite, SWT.NONE);

        importFromWorkspaceCheckbox = new Button(topComposite, SWT.RADIO);
        importFromWorkspaceCheckbox.setFont(topComposite.getFont());
        importFromWorkspaceCheckbox.addListener(SWT.Selection, this);
        importFromWorkspaceCheckbox.setText(getString("extractFromWorkspace")); //getString("overwriteExisting")); //$NON-NLS-1$

        createFromWorkspaceGroup(topComposite);

        createProjectListGroup(topComposite);

        createDestinationGroup(topComposite);

        restoreWidgetValues();

        updateWidgetEnablements();

        setPageComplete(true);
        setMessage(INITIAL_MESSAGE);
        setControl(topComposite);

        initializing = false;
    }

    /**
     * Creates the import destination specification controls.
     * 
     * @param parent the parent control
     */
    protected void createDestinationGroup( final Composite parent ) {
        // container specification group
        final Composite containerGroup = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        // container label
        final Label resourcesLabel = new Label(containerGroup, SWT.NONE);
        resourcesLabel.setText(getString("folderLabel")); //$NON-NLS-1$
        resourcesLabel.setFont(parent.getFont());

        // container name entry field
        containerNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        containerNameField.addListener(SWT.Modify, this);
        final GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        containerNameField.setLayoutData(data);
        containerNameField.setFont(parent.getFont());

        // container browse button
        containerBrowseButton = new Button(containerGroup, SWT.PUSH);
        containerBrowseButton.setText(getString("browse")); //$NON-NLS-1$
        containerBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        containerBrowseButton.addListener(SWT.Selection, this);
        containerBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(containerBrowseButton);

        initialPopulateContainerField();
    }

    /**
     * Create the group for creating the root directory
     */
    protected void createFromFileSystemGroup( final Composite parent ) {
        final Group fromFileSystemContainerGroup = new Group(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        fromFileSystemContainerGroup.setLayout(layout);
        fromFileSystemContainerGroup.setFont(parent.getFont());
        fromFileSystemContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        final Label groupLabel = new Label(fromFileSystemContainerGroup, SWT.NONE);
        groupLabel.setText(getSourceLabel());
        groupLabel.setFont(parent.getFont());

        // source name entry field
        fileSystemSourceNameField = new Combo(fromFileSystemContainerGroup, SWT.BORDER);
        final GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        fileSystemSourceNameField.setLayoutData(data);
        fileSystemSourceNameField.setFont(parent.getFont());

        fileSystemSourceNameField.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent e ) {
                fileSystemSourceFile = fileSystemSourceNameField.getText();
                setCompletionStatus();
            }
        });

        fileSystemSourceNameField.addKeyListener(new KeyListener() {

            public void keyPressed( final KeyEvent e ) {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;
                setCompletionStatus();
            }

            public void keyReleased( final KeyEvent e ) {
            }
        });

        fileSystemSourceNameField.addFocusListener(new FocusListener() {

            public void focusGained( final FocusEvent e ) {
                // Do nothing when getting focus
            }

            public void focusLost( final FocusEvent e ) {
                // Clear the flag to prevent constant update
                if (entryChanged) {
                    entryChanged = false;
                    setCompletionStatus();
                }

            }
        });

        // source browse button
        fileSystemSourceBrowseButton = new Button(fromFileSystemContainerGroup, SWT.PUSH);
        fileSystemSourceBrowseButton.setText(getString("browse_3")); //$NON-NLS-1$
        fileSystemSourceBrowseButton.addListener(SWT.Selection, this);
        fileSystemSourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        fileSystemSourceBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(fileSystemSourceBrowseButton);

        createLoadVdbGroup(fromFileSystemContainerGroup);
    }

    /**
     * Create the group for creating the root directory
     */
    protected void createFromWorkspaceGroup( final Composite parent ) {
        final Group fromWorkspaceContainerGroup = new Group(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        fromWorkspaceContainerGroup.setLayout(layout);
        fromWorkspaceContainerGroup.setFont(parent.getFont());
        fromWorkspaceContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        //fromWorkspaceContainerGroup.setText("File System Options"); //getString("chooseVdb")); //"Choose VDB"); //$NON-NLS-1$

        final Label groupLabel2 = new Label(fromWorkspaceContainerGroup, SWT.NONE);
        groupLabel2.setText(getSourceLabel());
        groupLabel2.setFont(parent.getFont());

        // source name entry field
        workspaceSourceNameField = new Combo(fromWorkspaceContainerGroup, SWT.BORDER);
        final GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        data2.widthHint = SIZING_TEXT_FIELD_WIDTH;
        workspaceSourceNameField.setLayoutData(data2);
        workspaceSourceNameField.setFont(parent.getFont());

        workspaceSourceNameField.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent e ) {
                workspaceSourceFile = workspaceSourceNameField.getText();
                setCompletionStatus();
            }
        });

        workspaceSourceNameField.addKeyListener(new KeyListener() {

            /*
             * @see KeyListener.keyPressed
             */
            public void keyPressed( final KeyEvent e ) {
                // If there has been a key pressed then mark as dirty
                entryChanged = true;
                setCompletionStatus();
            }

            /*
             * @see KeyListener.keyReleased
             */
            public void keyReleased( final KeyEvent e ) {
            }
        });

        workspaceSourceNameField.addFocusListener(new FocusListener() {

            /*
             * @see FocusListener.focusGained(FocusEvent)
             */
            public void focusGained( final FocusEvent e ) {
                // Do nothing when getting focus
            }

            /*
             * @see FocusListener.focusLost(FocusEvent)
             */
            public void focusLost( final FocusEvent e ) {
                // Clear the flag to prevent constant update
                if (entryChanged) {
                    entryChanged = false;
                    setCompletionStatus();
                }

            }
        });

        // source browse button
        workspaceSourceBrowseButton = new Button(fromWorkspaceContainerGroup, SWT.PUSH);
        workspaceSourceBrowseButton.setText(getString("browse_3")); //$NON-NLS-1$
        workspaceSourceBrowseButton.addListener(SWT.Selection, this);
        workspaceSourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        workspaceSourceBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(fileSystemSourceBrowseButton);

    }

    private void createLoadVdbGroup( final Composite parent ) {
        final Group loadVdbGroup = new Group(parent, SWT.NONE);
        final GridLayout layout1 = new GridLayout();
        layout1.numColumns = 3;
        loadVdbGroup.setLayout(layout1);

        final GridData gd_load_group = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gd_load_group.horizontalSpan = 3;
        loadVdbGroup.setLayoutData(gd_load_group);

        loadVdbGroup.setFont(parent.getFont());
        loadVdbGroup.setText(getString("optionsLabel")); //$NON-NLS-1$
        loadVdbIntoProjectCheckbox = new Button(loadVdbGroup, SWT.CHECK);
        loadVdbIntoProjectCheckbox.setFont(loadVdbGroup.getFont());
        loadVdbIntoProjectCheckbox.setSelection(true);
        loadVdbIntoProjectCheckbox.addListener(SWT.Selection, this);
        loadVdbIntoProjectCheckbox.setText(getString("importVdbFromFileSystem")); //$NON-NLS-1$
        final GridData gd_load_ckbx = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gd_load_ckbx.horizontalSpan = 3;
        loadVdbIntoProjectCheckbox.setLayoutData(gd_load_ckbx);

        // container label
        final Label resourcesLabel = new Label(loadVdbGroup, SWT.NONE);
        resourcesLabel.setText(getString("targetProjectOrFolder")); //$NON-NLS-1$
        resourcesLabel.setFont(loadVdbGroup.getFont());

        vdbLocationNameField = new Text(loadVdbGroup, SWT.SINGLE | SWT.BORDER);
        vdbLocationNameField.addListener(SWT.Modify, this);
        final GridData vdbLocData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        vdbLocData.widthHint = SIZING_TEXT_FIELD_WIDTH;
        vdbLocationNameField.setLayoutData(vdbLocData);
        vdbLocationNameField.setFont(loadVdbGroup.getFont());
        vdbLocationNameField.setEditable(false);

        // source browse button
        vdbLocationBrowseButton = new Button(loadVdbGroup, SWT.PUSH);
        vdbLocationBrowseButton.setText(getString("browse_3")); //$NON-NLS-1$
        vdbLocationBrowseButton.addListener(SWT.Selection, this);
        vdbLocationBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        vdbLocationBrowseButton.setFont(loadVdbGroup.getFont());
        setButtonLayoutData(vdbLocationBrowseButton);
    }

    /**
     * Create the import options specification widgets.
     */
    @Override
    protected void createOptionsGroupButtons( final Group optionsGroup ) {
    }

    /**
     * Method to create List box control group for displaying current zip file project list.
     * 
     * @param parent
     * @since 4.2
     */
    private void createProjectListGroup( final Composite parent ) {
        final Label messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setText(getString("projectListMessage")); //$NON-NLS-1$
        messageLabel.setFont(parent.getFont());

        // Create a table for the list
        final Table table = new Table(parent, SWT.BORDER);
        final GridData data = new GridData(GridData.FILL_BOTH);

        final int availableRows = availableRows(parent);

        // Only give a height hint if the dialog is going to be too small
        if (availableRows > 50) data.heightHint = SIZING_LISTS_HEIGHT;
        else data.heightHint = availableRows * 3;

        table.setLayoutData(data);
        table.setFont(parent.getFont());

        // the list viewer
        projectViewer = new TableViewer(table);
        projectViewer.setContentProvider(new ListContentProvider());
        projectViewer.setLabelProvider(new ProjectReferenceLabelProvider());

        setViewerContents(Collections.EMPTY_LIST);
    }

    /**
     * Answer a boolean indicating whether the specified source currently exists and is valid (ie.- proper format)
     */
    protected boolean ensureSourceIsValid( final Object specifiedFile ) {

        if (specifiedFile == null) return false;

        return true;
    }

    /**
     * Execute the passed import operation. Answer a boolean indicating success.
     */
    protected boolean executeImportOperation( final ImportOperation op ) {
        initializeOperation(op);

        try {
            getContainer().run(true, true, op);
        } catch (final InterruptedException e) {
            return false;
        } catch (final InvocationTargetException e) {
            displayErrorDialog(e.getTargetException());
            return false;
        }

        final IStatus status = op.getStatus();
        if (!status.isOK()) {
            ErrorDialog.openError(getContainer().getShell(), getString("importProblems"), //$NON-NLS-1$
                                  null, // no special message
                                  status);
            return false;
        }

        return true;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false is returned
     * then the wizard will not close.
     * 
     * @return boolean
     */
    public boolean finish() {
        saveWidgetValues();
        return true;
    }

    /**
     * Returns the path of the container resource specified in the container name entry field, or <code>null</code> if no name has
     * been typed in.
     * <p>
     * The container specified by the full path might not exist and would need to be created.
     * </p>
     * 
     * @return the full path of the container resource specified in the container name entry field, or <code>null</code>
     */
    protected IPath getContainerFullPath() {
        // make the path absolute to allow for optional leading slash
        final IPath testPath = getResourcePath();

        return testPath;
    }

    IPath getDefaultFolder() {
        return ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getFileProvider()
     * @since 4.2
     */
    protected ITreeContentProvider getFileProvider() {
        return null;
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardResourceImportPage#getFolderProvider()
     * @since 4.2
     */
    protected ITreeContentProvider getFolderProvider() {
        return null;
    }

    /**
     * Returns the current project location path as entered by the user, or its anticipated initial value.
     * 
     * @return the project location path, its anticipated initial value, or <code>null</code> if no project location path is known
     */
    public IPath getLocationPath( final String projectName ) {

        return new Path(getResourcePath() + "\\" + projectName); //$NON-NLS-1$
    }

    /**
     * Return the path for the resource field.
     * 
     * @return IPath
     */
    protected IPath getResourcePath() {
        return getPathFromText(this.containerNameField);
    }

    /**
     * Answer the string to display as the label for the source specification field
     */
    protected String getSourceLabel() {
        return getString("fromDirectory"); //$NON-NLS-1$
    }

    protected String getSourceName() {
        if (importFromFileSystemCheckbox.getSelection()) return fileSystemSourceFile;

        // Import from workspace
        // Need to get the full pathname
        return workspaceSourceFile;
    }

    /**
     * Returns the container resource specified in the container name entry field, or <code>null</code> if such a container does not
     * exist in the workbench.
     * 
     * @return the container resource specified in the container name entry field, or <code>null</code>
     */
    protected IContainer getSpecifiedContainer() {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IPath path = getContainerFullPath();
        if (workspace.getRoot().exists(path)) return (IContainer)workspace.getRoot().findMember(path);

        return null;
    }

    /**
     * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist or is not of
     * valid format.
     */
    protected File getSpecifiedSourceFile() {
        fileSystemSourceNameField.setFocus();

        return null;
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleContainerBrowseButtonPressed() {
        UiBusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
            public void run() {
                final FileSystemDialog dialog = new FileSystemDialog(containerNameField.getShell());
                dialog.setAllowMultiple(false);
                dialog.setInitialSelection(getDefaultFolder().toFile());
                dialog.setOnlyShowFolders();
                dialog.setValidator(new Validator());
                dialog.setTitle(getString("dialog.targetFolder.title")); //$NON-NLS-1$
                dialog.setMessage(getString("dialog.targetFolder.msg")); //$NON-NLS-1$

                if (dialog.open() == Window.OK) {
                    final Object[] selection = dialog.getResult();

                    // should never be null since OK was pressed, but checking can't hurt.
                    // should always have a selection but checking can't hurt
                    if ((selection != null) && (selection.length > 0)) containerNameField.setText(((File)selection[0]).getAbsolutePath());
                }
            }
        });
    }

    /**
     * Handle all events and enablements for widgets in this dialog
     * 
     * @param event Event
     */
    public void handleEvent( final Event event ) {
        if (!initializing) {
            boolean validate = false;

            if (event.widget == fileSystemSourceBrowseButton) {
                handleFileSystemSourceBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == workspaceSourceBrowseButton) {
                handleWorkspaceSourceBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == containerBrowseButton) {
                handleContainerBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == fileSystemSourceNameField || event.widget == containerNameField) validate = true;

            if (event.widget == importFromFileSystemCheckbox || event.widget == importFromWorkspaceCheckbox) {
                setOptionsWidgetsState();
                validate = true;
            }

            if (event.widget == loadVdbIntoProjectCheckbox) {
                vdbLocationBrowseButton.setEnabled(loadVdbIntoProjectCheckbox.getSelection());
                vdbLocationNameField.setEnabled(loadVdbIntoProjectCheckbox.getSelection());
                validate = true;
            }

            if (event.widget == containerBrowseButton) {
                handleContainerBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == vdbLocationBrowseButton) {
                handleVdbLocationBrowseButtonPressed();
                validate = true;
            }

            if (validate) setCompletionStatus();

            updateWidgetEnablements();
        }
    }

    /**
     * Open an appropriate source browser so that the user can specify a source to import from
     */
    protected void handleFileSystemSourceBrowseButtonPressed() {
        String selectedFile = null;

        if (importFromFileSystemCheckbox.getSelection()) {
            final FileDialog dialog = new FileDialog(fileSystemSourceNameField.getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {FILE_IMPORT_MASK});

            final String currentSourceString = getSourceName();
            if (currentSourceString != null) {
                final int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
                if (lastSeparatorIndex != -1) dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));
            }
            selectedFile = dialog.open();
        }

        if (selectedFile != null) {
            if (!selectedFile.equals(getSourceName())) {
                fileSystemSourceNameField.setText(selectedFile);
                fileSystemSourceFile = selectedFile;
                // Need to call the method to update the project list because source (zip file) may have changed.
            }
        } else {
            fileSystemSourceNameField.setText(null);
            fileSystemSourceFile = null;
        }
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's vdb
     * location name field.
     */
    protected void handleVdbLocationBrowseButtonPressed() {
        final Object[] selectedFiles = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("vdbLocationTitle"), //$NON-NLS-1$
                                                                                     getString("vdbLocationMessage"), //$NON-NLS-1$
                                                                                     true,
                                                                                     null,
                                                                                     new ModelingResourceFilter(
                                                                                                                this.vdbLocationFilter),
                                                                                     this.vdbLocationValidator);

        if ((selectedFiles != null) && (selectedFiles.length == 1)) if (selectedFiles[0] instanceof IContainer) {
            vdbLocation = (IContainer)selectedFiles[0];
            vdbLocationNameField.setText(vdbLocation.getFullPath().toOSString());
        }
        // System.out.println(" Selected VDB Location = " );
    }

    /**
     * Open an appropriate source browser so that the user can specify a source to import from
     */
    protected void handleWorkspaceSourceBrowseButtonPressed() {
        String selectedFile = null;
        IFile selectedIFile = null;
        final Object[] selectedFiles = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("selectVdbInWorkspaceTitle"), //$NON-NLS-1$
                                                                                     getString("selectVdbInWorkspaceMessage"), //$NON-NLS-1$
                                                                                     true,
                                                                                     null,
                                                                                     new ModelingResourceFilter(this.vdbFilter),
                                                                                     this.vdbValidator);

        if ((selectedFiles != null) && (selectedFiles.length == 1)) {
            selectedIFile = (IFile)selectedFiles[0];
            selectedFile = ((IFile)selectedFiles[0]).getFullPath().toOSString();
        }

        if (selectedIFile != null) {
            if (!selectedFile.equals(getSourceName())) {
                workspaceSourceNameField.setText(selectedFile);
                workspaceSourceFile = selectedIFile.getLocation().toOSString();
                // Need to call the method to update the project list because source (zip file) may have changed.
            }
        } else {
            workspaceSourceNameField.setText(null);
            workspaceSourceFile = null;
        }
    }

    /**
     * Initializes the specified operation appropriately.
     */
    protected void initializeOperation( final ImportOperation op ) {
        // op.setCreateContainerStructure(createContainerStructureButton.getSelection());
        op.setOverwriteResources(overwriteExistingResourcesCheckbox.getSelection());
    }

    /**
     * Sets the initial contents of the container name field.
     */
    protected final void initialPopulateContainerField() {
        if (initialContainerFieldValue != null) containerNameField.setText(initialContainerFieldValue);
    }

    private boolean isClosedWorkspaceModelProjectFolder( final String projectName ) {
        // IProject existingProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        final IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; i++)
            if (projects[i].getName().equals(projectName)) if (!projects[i].isOpen()) // System.out.println("ImportMPSMP.isWorkspaceProjectFolder() Selected Closed Project = "
            // + existingProject);
            // Need to query the .project file and see if has a MODELER NATURE
            return true;

        return false;
    }

    private boolean isNonWorkspaceModelProjectFolderOrDecendent( final String projectName ) {
        return false;
    }

    private boolean isOpenWorkspaceModelProjectFolder( final String projectName ) {
        final IProject existingProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        final IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (final IProject project : projects)
            if (project.getName().equals(projectName)) if (project.isOpen()) if (ModelerCore.hasModelNature(existingProject)) return true;

        return false;
    }

    /**
     * Use the dialog store to restore widget values to the values that they held last time this wizard was used to completion
     */
    @Override
    protected void restoreWidgetValues() {
        final IDialogSettings settings = getDialogSettings();
        WidgetUtil.removeMissingResources(settings, STORE_FILE_SYSTEM_SOURCE_NAMES_ID);
        WidgetUtil.removeMissingResources(settings, STORE_WORKSPACE_SOURCE_NAMES_ID);
        if (settings != null) {
            String[] sourceNames = settings.getArray(STORE_FILE_SYSTEM_SOURCE_NAMES_ID);
            if (sourceNames == null) return; // ie.- no values stored, so stop

            // set filenames history
            for (final String sourceName : sourceNames)
                fileSystemSourceNameField.add(sourceName);

            sourceNames = null;
            sourceNames = settings.getArray(STORE_WORKSPACE_SOURCE_NAMES_ID);
            if (sourceNames == null) return; // ie.- no values stored, so stop

            // set filenames history
            for (final String sourceName : sourceNames)
                workspaceSourceNameField.add(sourceName);
        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of this
     * wizard page
     */
    @Override
    protected void saveWidgetValues() {
        final IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // update source names history
            String[] sourceNames = settings.getArray(STORE_FILE_SYSTEM_SOURCE_NAMES_ID);
            if (sourceNames == null) sourceNames = new String[0];

            sourceNames = addToHistory(sourceNames, getSourceName());
            settings.put(STORE_FILE_SYSTEM_SOURCE_NAMES_ID, sourceNames);

            sourceNames = null;
            sourceNames = settings.getArray(STORE_WORKSPACE_SOURCE_NAMES_ID);
            if (sourceNames == null) sourceNames = new String[0];

            sourceNames = addToHistory(sourceNames, getSourceName());
            settings.put(STORE_WORKSPACE_SOURCE_NAMES_ID, sourceNames);
        }
    }

    protected boolean setCompletionStatus() {
        // Need to call the method to update the project list because source (zip file) may have changed.
        // String selectedFileString = fileSystemSourceNameField.getText();
        final String selectedFileString = getSourceName();
        if (selectedFileString != null && selectedFileString.length() > 0) {
            if (validateTargetFolder() && validateLoadVdb()) {
                setErrorMessage(null);
                setMessage(INITIAL_MESSAGE);
                setPageComplete(true);
                return true;
            }
        } else {
            validateTargetFolder();
            setErrorMessage(SOURCE_EMPTY_MESSAGE);
        }
        setPageComplete(false);
        return false;
    }

    /**
     * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the source
     * combo and selects it.
     * 
     * @param path the path to be added
     */
    protected void setFileSystemSourceName( final String path ) {

        if (path.length() > 0) {

            final String[] currentItems = this.fileSystemSourceNameField.getItems();
            int selectionIndex = -1;
            for (int i = 0; i < currentItems.length; i++)
                if (currentItems[i].equals(path)) selectionIndex = i;
            if (selectionIndex < 0) {
                final int oldLength = currentItems.length;
                final String[] newItems = new String[oldLength + 1];
                System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                newItems[oldLength] = path;
                this.fileSystemSourceNameField.setItems(newItems);
                selectionIndex = oldLength;
            }
            this.fileSystemSourceNameField.select(selectionIndex);

        }
    }

    private void setOptionsWidgetsState() {
        loadVdbIntoProjectCheckbox.setEnabled(importFromFileSystemCheckbox.getSelection());
        vdbLocationBrowseButton.setEnabled(importFromFileSystemCheckbox.getSelection());
        vdbLocationNameField.setEnabled(importFromFileSystemCheckbox.getSelection());

        workspaceSourceNameField.setEnabled(importFromWorkspaceCheckbox.getSelection());
        workspaceSourceBrowseButton.setEnabled(importFromWorkspaceCheckbox.getSelection());

        fileSystemSourceNameField.setEnabled(importFromFileSystemCheckbox.getSelection());
        fileSystemSourceBrowseButton.setEnabled(importFromFileSystemCheckbox.getSelection());
    }

    /**
     * Internal method to change the contents of the project list box with a List of strings (assumes project folder names)
     * 
     * @param projectList
     * @since 4.2
     */
    private void setViewerContents( final List<IProject> projectList ) {
        final List<IProject> pList = new ArrayList<IProject>(projectList);
        projectViewer.setInput(pList);
    }

    /*
     * (non-Javadoc) Method declared on IDialogPage. Set the selection up when it becomes visible.
     */
    @Override
    public void setVisible( final boolean visible ) {
        super.setVisible(visible);

        if (visible) this.fileSystemSourceNameField.setFocus();
    }

    /**
     * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the source
     * combo and selects it.
     * 
     * @param path the path to be added
     */
    protected void setWorkspaceSourceName( final String path ) {

        if (path.length() > 0) {

            final String[] currentItems = this.workspaceSourceNameField.getItems();
            int selectionIndex = -1;
            for (int i = 0; i < currentItems.length; i++)
                if (currentItems[i].equals(path)) selectionIndex = i;
            if (selectionIndex < 0) {
                final int oldLength = currentItems.length;
                final String[] newItems = new String[oldLength + 1];
                System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                newItems[oldLength] = path;
                this.workspaceSourceNameField.setItems(newItems);
                selectionIndex = oldLength;
            }
            this.workspaceSourceNameField.select(selectionIndex);

        }
    }

    /**
     * Check if widgets are enabled or disabled by a change in the dialog. Provided here to give access to inner classes.
     * 
     * @param event Event
     */
    @Override
    protected void updateWidgetEnablements() {

        super.updateWidgetEnablements();
        setOptionsWidgetsState();
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateDestinationGroup()
     * @since 4.2
     */
    @Override
    protected boolean validateDestinationGroup() {
        return true;
    }

    private boolean validateLoadVdb() {
        if (importFromFileSystemCheckbox.getSelection() && loadVdbIntoProjectCheckbox.getSelection()) {
            setErrorMessage(getString("noVdbLocationSelected")); //$NON-NLS-1$
            return (vdbLocation != null);
        }

        // if the above conditions aren't met, the we don't care about the state of the vdbLocation
        return true;
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateOptionsGroup()
     * @since 4.2
     */
    @Override
    protected boolean validateOptionsGroup() {
        return true;
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateSourceGroup()
     * @since 4.2
     */
    @Override
    protected boolean validateSourceGroup() {
        return true;
    }

    /* (non-Javadoc)
     * Method declared on WizardDataTransferPage.
     */
    private final boolean validateTargetFolder() {

        final IPath containerPath = getContainerFullPath();
        if (containerPath == null) {
            setMessage(EMPTY_FOLDER_MESSAGE);
            return false;
        }

        final File directory = containerPath.toFile();
        if (!directory.isDirectory()) {
            setMessage(null);
            setErrorMessage(getString("folderDoesNotExist", containerPath)); //$NON-NLS-1$
            return false;
        }
        // We have a valid directory but we need to check for workspace and non-workspace projects.
        if (isOpenWorkspaceModelProjectFolder(directory.getName())) {
            setErrorMessage(getString("badOpenProjectLocation")); //$NON-NLS-1$
            return false;
        }

        if (isClosedWorkspaceModelProjectFolder(directory.getName())) {
            setErrorMessage(getString("badClosedProjectLocation")); //$NON-NLS-1$
            return false;
        }

        if (isNonWorkspaceModelProjectFolderOrDecendent(directory.getAbsolutePath())) {
            setErrorMessage(getString("badNonWorkspaceProjectLocation")); //$NON-NLS-1$
            return false;
        }

        setErrorMessage(null);

        return true;
    }

    class ProjectReferenceLabelProvider implements ILabelProvider {

        public void addListener( final ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public Image getImage( final Object element ) {
            if (element instanceof IProject) return UiPlugin.getDefault().getProjectImage();

            return null;
        }

        public String getText( final Object element ) {
            if (element instanceof IProject) return ((IProject)element).getName();
            return null;
        }

        public boolean isLabelProperty( final Object element,
                                        final String property ) {
            return false;
        }

        public void removeListener( final ILabelProviderListener listener ) {
        }

    }

    /**
     * Validates the selection ensuring there is no .project file in the selected directory or in an ancestor directory.
     * 
     * @since 4.2
     */
    class Validator implements ISelectionStatusValidator {
        private boolean containsProjectFile( final File theFile ) {
            return false;
        }

        /**
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         * @since 4.2
         */
        public IStatus validate( final Object[] theSelection ) {
            IStatus result = STATUS_OK;

            if (theSelection.length > 0) // need to make sure a ".project" does not exist in this and any ancestor directory
            if (containsProjectFile((File)theSelection[0])) result = STATUS_ERROR;

            return result;
        }
    }

}

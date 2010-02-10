/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;
import com.metamatrix.core.util.FileSeparatorUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.dialog.FileSystemDialog;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.ListContentProvider;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * Page 1 of the base resource import-from-file-system Wizard
 */
public class ImportModelerProjectSetMainPage extends WizardDataTransferPage implements UiConstants {

    static final String DOT_PROJECT = ".project"; //$NON-NLS-1$
    protected static final int BUFFER = 2048;

    // dialog store id constants
    private static final String I18N_PREFIX = "ImportModelerProjectSetMainPage"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$
    private static final String FILE_IMPORT_MASK = "*.zip;*";//$NON-NLS-1$

    private final static String STORE_SOURCE_NAMES_ID = getString("storeSourceNamesId");//$NON-NLS-1$
    private final static String STORE_OVERWRITE_EXISTING_RESOURCES_ID = getString("storeOverwriteExistingResourcesId");//$NON-NLS-1$

    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String EMPTY_FOLDER_MESSAGE = getString("emptyFolderMessage"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String NOT_PROJECT_SET_ZIP = getString("notProjectSetZip"); //$NON-NLS-1$
    private static final String ZIP_CONTAINS_NESTED_PROJECT = getString("zipContainsNestedProject"); //$NON-NLS-1$

    // constants used in the validator inner class
    static final IStatus STATUS_OK = new StatusInfo(PLUGIN_ID);
    static final IStatus STATUS_ERROR = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("validationError")); //$NON-NLS-1$

    private ZipFileStructureProvider providerCache;
    // private IWorkbench workbench;
    // initial value stores
    private String initialContainerFieldValue = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toString();

    // widgets
    protected Combo sourceNameField;
    protected Button overwriteExistingResourcesCheckbox;
    // protected Button createContainerStructureButton;
    protected Button sourceBrowseButton;
    Text containerNameField;
    private Button containerBrowseButton;
    private TableViewer projectViewer;

    // A boolean to indicate if the user has typed anything
    boolean entryChanged = false;
    private final static int SIZING_LISTS_HEIGHT = 200;
    private boolean hasProjects = false;
    private boolean initializing = false;

    static String getString( final String id ) {
        return UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id);
    }

    private static String getString( final String id,
                                     Object object ) {
        return UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id, object);
    }

    /**
     * Creates an instance of this class
     */
    protected ImportModelerProjectSetMainPage( String title,
                                               IStructuredSelection selection ) {
        super(title);
        setTitle(title);
    }

    /**
     * Creates an instance of this class
     * 
     * @param aWorkbench IWorkbench
     * @param selection IStructuredSelection
     */
    public ImportModelerProjectSetMainPage( IStructuredSelection selection ) {
        this(PAGE_TITLE, selection);
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
     * Method declared on IDialogPage.
     */
    public void createControl( Composite parent ) {
        initializing = true;

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);

        createProjectListGroup(composite);

        createDestinationGroup(composite);

        createOptionsGroup(composite);

        restoreWidgetValues();
        updateWidgetEnablements();

        setPageComplete(true);
        setMessage(INITIAL_MESSAGE);
        setControl(composite);

        initializing = false;
    }

    /**
     * Method to create List box control group for displaying current zip file project list.
     * 
     * @param parent
     * @since 4.2
     */
    private void createProjectListGroup( Composite parent ) {
        Label messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setText(getString("projectListMessage")); //$NON-NLS-1$
        messageLabel.setFont(parent.getFont());

        // Create a table for the list
        Table table = new Table(parent, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);

        int availableRows = DialogUtil.availableRows(parent);

        // Only give a height hint if the dialog is going to be too small
        if (availableRows > 50) {
            data.heightHint = SIZING_LISTS_HEIGHT;
        } else {
            data.heightHint = availableRows * 3;
        }

        table.setLayoutData(data);
        table.setFont(parent.getFont());

        // the list viewer
        projectViewer = new TableViewer(table);
        projectViewer.setContentProvider(new ListContentProvider());
        projectViewer.setLabelProvider(new ProjectReferenceLabelProvider());

        setViewerContents(Collections.EMPTY_LIST);
    }

    /**
     * Internal method to change the contents of the project list box with a List of strings (assumes project folder names)
     * 
     * @param projectList
     * @since 4.2
     */
    private void setViewerContents( List projectList ) {
        List pList = new ArrayList(projectList);
        hasProjects = pList.size() > 0;
        projectViewer.setInput(pList);
    }

    private void clearViewerContents() {
        projectViewer.setInput(new ArrayList());
    }

    public boolean hasProjects() {
        return hasProjects;
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
        resourcesLabel.setText(getString("folderLabel")); //$NON-NLS-1$
        resourcesLabel.setFont(parent.getFont());

        // container name entry field
        containerNameField = new Text(containerGroup, SWT.SINGLE | SWT.BORDER);
        containerNameField.addListener(SWT.Modify, this);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
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
     * Create the import options specification widgets.
     */
    @Override
    protected void createOptionsGroupButtons( Group optionsGroup ) {

        // overwrite... checkbox
        overwriteExistingResourcesCheckbox = new Button(optionsGroup, SWT.CHECK);
        overwriteExistingResourcesCheckbox.setFont(optionsGroup.getFont());
        overwriteExistingResourcesCheckbox.setText(getString("overwriteExisting")); //$NON-NLS-1$
    }

    /**
     * Create the group for creating the root directory
     */
    protected void createRootDirectoryGroup( Composite parent ) {
        Composite sourceContainerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        sourceContainerGroup.setLayout(layout);
        sourceContainerGroup.setFont(parent.getFont());
        sourceContainerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label groupLabel = new Label(sourceContainerGroup, SWT.NONE);
        groupLabel.setText(getSourceLabel());
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
                // updateFromSourceField();
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
                setCompletionStatus();
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
                    setCompletionStatus();
                }

            }
        });

        // source browse button
        sourceBrowseButton = new Button(sourceContainerGroup, SWT.PUSH);
        sourceBrowseButton.setText(getString("browse_3")); //$NON-NLS-1$
        sourceBrowseButton.addListener(SWT.Selection, this);
        sourceBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        sourceBrowseButton.setFont(parent.getFont());
        setButtonLayoutData(sourceBrowseButton);
    }

    /**
     * Called when the user presses the Cancel button. Return a boolean indicating permission to close the wizard.
     */
    public boolean cancel() {
        clearProviderCache();
        return true;
    }

    /**
     * Clears the cached structure provider after first finalizing it properly.
     */
    protected void clearProviderCache() {
        if (providerCache != null) {
            closeZipFile(providerCache.getZipFile());
            providerCache = null;
        }
    }

    /**
     * Attempts to close the passed zip file, and answers a boolean indicating success.
     */
    protected boolean closeZipFile( ZipFile file ) {
        try {
            file.close();
        } catch (IOException e) {
            //            displayErrorDialog(DataTransferMessages.format("ZipImport.couldNotClose", new Object [] {file.getName()})); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    /**
     * Sets the initial contents of the container name field.
     */
    protected final void initialPopulateContainerField() {
        if (initialContainerFieldValue != null) containerNameField.setText(initialContainerFieldValue);
    }

    /**
     * Create the import source specification widgets
     */
    protected void createSourceGroup( Composite parent ) {
        createRootDirectoryGroup(parent);
    }

    /**
     * Answer a boolean indicating whether the specified source currently exists and is valid (ie.- proper format)
     */
    protected boolean ensureSourceIsValid( ZipFile specifiedFile ) {

        if (specifiedFile == null) return false;

        return closeZipFile(specifiedFile);
    }

    /**
     * Execute the passed import operation. Answer a boolean indicating success.
     */
    protected boolean executeImportOperation( ImportOperation op ) {
        initializeOperation(op);

        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            displayErrorDialog(e.getTargetException());
            return false;
        }

        IStatus status = op.getStatus();
        if (!status.isOK()) {
            ErrorDialog.openError(getContainer().getShell(), getString("importProblems"), //$NON-NLS-1$
                                  null, // no special message
                                  status);
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
        ZipFile sourceZip = getSpecifiedSourceFile();
        if (!ensureSourceIsValid(sourceZip)) return false;

        saveWidgetValues();

        List newProjectStrings = unZipFiles(sourceZip);

        List<IProject> newProjects = addToWorkspace(newProjectStrings, getShell());
        
        if (newProjects != null) {
        	for( IProject nextProj : newProjects ) {
        		createExistingProject(nextProj, 0, 0);
        	}
        }

        return true;
    }

    /**
     * Answer the string to display as the label for the source specification field
     */
    protected String getSourceLabel() {
        return getString("fromDirectory"); //$NON-NLS-1$
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

            if (event.widget == containerBrowseButton) {
                handleContainerBrowseButtonPressed();
                validate = true;
            }

            if (event.widget == sourceNameField || event.widget == containerNameField) {
                validate = true;
            }

            if (validate) setCompletionStatus();

            updateWidgetEnablements();
        }
    }

    protected boolean setCompletionStatus() {
        if (validateSourceAndDestination()) {
            setErrorMessage(null);
            setMessage(INITIAL_MESSAGE);
            setPageComplete(true);
            return true;
        }

        setPageComplete(false);
        return false;
    }

    /**
     * Open an appropriate source browser so that the user can specify a source to import from
     */
    protected void handleSourceBrowseButtonPressed() {
        String selectedFile = queryZipFileToImport();

        if (selectedFile != null) {
            if (!selectedFile.equals(getSourceName())) {
                sourceNameField.setText(selectedFile);
                // Need to call the method to update the project list because source (zip file) may have changed.
            }
        }
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
        IPath testPath = getResourcePath();

        return testPath;
    }

    /**
     * Returns the container resource specified in the container name entry field, or <code>null</code> if such a container does
     * not exist in the workbench.
     * 
     * @return the container resource specified in the container name entry field, or <code>null</code>
     */
    protected IContainer getSpecifiedContainer() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath path = getContainerFullPath();
        if (workspace.getRoot().exists(path)) return (IContainer)workspace.getRoot().findMember(path);

        return null;
    }

    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleContainerBrowseButtonPressed() {
        UiBusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
            public void run() {
                FileSystemDialog dialog = new FileSystemDialog(containerNameField.getShell());
                dialog.setAllowMultiple(false);
                dialog.setInitialSelection(getDefaultFolder().toFile());
                dialog.setOnlyShowFolders();
                dialog.setValidator(new Validator());
                dialog.setTitle(getString("dialog.targetFolder.title")); //$NON-NLS-1$
                dialog.setMessage(getString("dialog.targetFolder.msg")); //$NON-NLS-1$

                if (dialog.open() == Window.OK) {
                    Object[] selection = dialog.getResult();

                    // should never be null since OK was pressed, but checking can't hurt.
                    // should always have a selection but checking can't hurt
                    if ((selection != null) && (selection.length > 0)) {
                        containerNameField.setText(((File)selection[0]).getAbsolutePath());
                    }
                }
            }
        });
    }

    private boolean isOpenWorkspaceModelProjectFolder( String projectName ) {
        IProject existingProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; i++) {
            if (projects[i].getName().equals(projectName)) {
                if (projects[i].isOpen()) {
                    if (ModelerCore.hasModelNature(existingProject)) return true;
                }
            }
        }

        return false;
    }

    private boolean isClosedWorkspaceModleProjectFolder( String projectName ) {
        // IProject existingProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; i++) {
            if (projects[i].getName().equals(projectName)) {
                if (!projects[i].isOpen()) {
                    // System.out.println("ImportMPSMP.isWorkspaceProjectFolder() Selected Closed Project = " + existingProject);
                    // Need to query the .project file and see if has a MODELER NATURE
                    return true;
                }
            }

        }

        return false;
    }

    private boolean isNonWorkspaceModelProjectFolderOrDecendent( String projectName ) {
        return false;
    }

    /*
     * Internal utility to get a String list of project folder names from the current source zip file 
     */
    private List findProjectsInZip( ZipFile zipFile ) {
        List projectFolders = new ArrayList();

        try {
            FileInputStream fis = new FileInputStream(new File(zipFile.getName()));
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                // may need to create folder(s)
                if (entry.getName().indexOf(DOT_PROJECT) != -1) {
                    String fileSepString = FileSeparatorUtil.getFileSeparator(entry.getName());

                    int lastFileSep = entry.getName().lastIndexOf(fileSepString);

                    if (lastFileSep != -1) {
                        String projectFolder = entry.getName().substring(0, lastFileSep);

                        boolean isModelerProject = isModelerProject(zis);

                        if (projectFolder != null) {
                            projectFolders.add(new ProjectReference(projectFolder, isModelerProject));
                        }
                    } // endif
                }

            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectFolders;
    }

    private boolean isModelerProject( ZipInputStream zis ) throws IOException {
        int count;
        byte data[] = new byte[BUFFER];
        // write the files to the disk
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedOutputStream dest = new BufferedOutputStream(bos, BUFFER);
        while ((count = zis.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
        }
        dest.flush();
        dest.close();

        String projectFileContents = bos.toString();
        String natureOpenTag = "<nature>"; //$NON-NLS-1$
        String natureCloseTag = "</nature>"; //$NON-NLS-1$
        return projectFileContents.indexOf(natureOpenTag + ModelerCore.NATURE_ID + natureCloseTag) > 0;
    }

    /*
     * Utility which unzips the files and stores them on the file system.
     */
    private List unZipFiles( ZipFile zipFile ) {
        List projectFolders = new ArrayList();
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(new File(zipFile.getName()));
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            String fullName = null;
            String containerPath = getContainerFullPath().makeAbsolute().toString();
            while ((entry = zis.getNextEntry()) != null) {
                // may need to create folder(s)
                String fileSepString = FileSeparatorUtil.getFileSeparator(entry.getName());

                if (entry.getName().indexOf(DOT_PROJECT) != -1) {
                    String projectFolder = entry.getName().substring(0, entry.getName().lastIndexOf(fileSepString));
                    if (projectFolder != null) {
                        projectFolders.add(projectFolder);
                    }
                }

                if (getContainerFullPath() != null) {
                    fullName = containerPath + fileSepString + entry.getName();
                }

                // may need to create folder(s)
                String path = fullName.substring(0, fullName.lastIndexOf(fileSepString));
                File folder = new File(path);
                if (!folder.exists()) folder.mkdirs();

                if (!entry.isDirectory()) {
                    int count;
                    byte data[] = new byte[BUFFER];
                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(fullName);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (projectFolders.isEmpty()) return Collections.EMPTY_LIST;

        return projectFolders;
    }

    /**
     * Initializes the specified operation appropriately.
     */
    protected void initializeOperation( ImportOperation op ) {
        // op.setCreateContainerStructure(createContainerStructureButton.getSelection());
        op.setOverwriteResources(overwriteExistingResourcesCheckbox.getSelection());
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

            // radio buttons and checkboxes
            overwriteExistingResourcesCheckbox.setSelection(settings.getBoolean(STORE_OVERWRITE_EXISTING_RESOURCES_ID));

        }
    }

    /**
     * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next invocation of
     * this wizard page
     */
    @Override
    protected void saveWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            // update source names history
            String[] sourceNames = settings.getArray(STORE_SOURCE_NAMES_ID);
            if (sourceNames == null) sourceNames = new String[0];

            sourceNames = addToHistory(sourceNames, getSourceName());
            settings.put(STORE_SOURCE_NAMES_ID, sourceNames);

            // radio buttons and checkboxes
            settings.put(STORE_OVERWRITE_EXISTING_RESOURCES_ID, overwriteExistingResourcesCheckbox.getSelection());

            // settings.put(STORE_CREATE_CONTAINER_STRUCTURE_ID, createContainerStructureButton.getSelection());

        }
    }

    /**
     * Sets the source name of the import to be the supplied path. Adds the name of the path to the list of items in the source
     * combo and selects it.
     * 
     * @param path the path to be added
     */
    protected void setSourceName( String path ) {

        if (path.length() > 0) {

            String[] currentItems = this.sourceNameField.getItems();
            int selectionIndex = -1;
            for (int i = 0; i < currentItems.length; i++) {
                if (currentItems[i].equals(path)) selectionIndex = i;
            }
            if (selectionIndex < 0) {
                int oldLength = currentItems.length;
                String[] newItems = new String[oldLength + 1];
                System.arraycopy(currentItems, 0, newItems, 0, oldLength);
                newItems[oldLength] = path;
                this.sourceNameField.setItems(newItems);
                selectionIndex = oldLength;
            }
            this.sourceNameField.select(selectionIndex);

        }
    }

    /**
     * Method declared on IDialogPage. Set the selection up when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        if (visible) this.sourceNameField.setFocus();
    }

    /**
     * Check if widgets are enabled or disabled by a change in the dialog. Provided here to give access to inner classes.
     * 
     * @param event Event
     */
    @Override
    protected void updateWidgetEnablements() {

        super.updateWidgetEnablements();
    }

    private boolean validateSourceAndDestination() {
        if (validateZipFile() && validateTargetFolder()) {
            return true;
        }

        return false;
    }

    /**
     * Answer a boolean indicating whether self's source specification widgets currently all contain valid values.
     */
    private boolean validateZipFile() {
        File sourceFile = new File(getSourceName());
        if (sourceFile.exists()) {
            ZipFile zFile = getSpecifiedSourceFile();
            if (zFile != null) {
                List projects = findProjectsInZip(zFile);
                if (projects.size() == 0) {
                    // NO PROJECTS
                    setErrorMessage(NOT_PROJECT_SET_ZIP);
                    setMessage(null);
                    clearViewerContents();
                    return false;
                }

                setViewerContents(projects);
                closeZipFile(zFile);

                for (int i = 0; i < projects.size(); i++) {
                    if (isSegmentCountInvalid(((ProjectReference)projects.get(i)).getProject())) {
                        setErrorMessage(ZIP_CONTAINS_NESTED_PROJECT);
                        return false;
                    }
                }

                return true;
            }
            return false;
        }

        setMessage(null);
        setErrorMessage(getString("zipFileDoesNotExist", getSourceName())); //$NON-NLS-1$
        return false;
    }

    protected String getSourceName() {
        return sourceNameField.getText();
    }

    /**
     * Check to see if project is under folder/another project. In which case it is invalid.
     */
    private boolean isSegmentCountInvalid( String projectName ) {
        StringTokenizer tokenizer = new StringTokenizer(projectName, "/"); //$NON-NLS-1$

        return tokenizer.countTokens() > 1;
    }

    /**
     * Method declared on WizardDataTransferPage.
     */
    private final boolean validateTargetFolder() {

        IPath containerPath = getContainerFullPath();
        if (containerPath == null) {
            setMessage(EMPTY_FOLDER_MESSAGE);
            return false;
        }

        File directory = containerPath.toFile();
        if (!directory.isDirectory()) {
            setMessage(null);
            setErrorMessage(getString("folderDoesNotExist", containerPath)); //$NON-NLS-1$
            return false;
        }
        // We have a valid directory but we need to check for workspace and non-workspace projects.
        if (isOpenWorkspaceModelProjectFolder(directory.getName())) {
            setErrorMessage("Desired Location is in Currently Open Model Project. Re-select location"); //$NON-NLS-1$
            return false;
        }

        if (isClosedWorkspaceModleProjectFolder(directory.getName())) {
            setErrorMessage("Desired Location is in Currently Closed Model Project. Re-select location"); //$NON-NLS-1$
            return false;
        }

        if (isNonWorkspaceModelProjectFolderOrDecendent(directory.getAbsolutePath())) {
            setErrorMessage("Desired Location is in Non-Workspace Model Project. Re-select location"); //$NON-NLS-1$
            return false;
        }

        setErrorMessage(null);

        return true;
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

    IPath getDefaultFolder() {
        return ResourcesPlugin.getWorkspace().getRoot().getRawLocation();
    }

    /**
     * Opens a file selection dialog and returns a string representing the selected file, or <code>null</code> if the dialog was
     * canceled.
     */
    protected String queryZipFileToImport() {
        FileDialog dialog = new FileDialog(sourceNameField.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] {FILE_IMPORT_MASK});

        String currentSourceString = getSourceName();
        int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
        if (lastSeparatorIndex != -1) dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));

        return dialog.open();
    }

    /**
     * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist or is not
     * of valid format.
     */
    protected ZipFile getSpecifiedSourceFile() {
        try {
            return new ZipFile(getSourceName());
        } catch (ZipException err) {
            String msg = getString("badFormatZipFile", getSourceName()); //$NON-NLS-1$
            setMessage(null);
            setErrorMessage(msg);
        } catch (IOException err) {
            String msg = getString("couldNotReadZipFile", getSourceName()); //$NON-NLS-1$
            setMessage(null);
            setErrorMessage(msg);
        }

        sourceNameField.setFocus();
        return null;
    }

    /**
     * /** This was confiscated from the IProjectSetSerializer.addToWorkspace() method. It checks each input project, confirms
     * with the user to overwrite if it exists.
     * 
     * @param referenceStrings List
     * @param context Object
     * @return projectList IProject[]
     * @since 4.2
     */
    public List<IProject> addToWorkspace( List referenceStrings,
                                      Object context ) {
        final int size = referenceStrings.size();
        final IProject[] allProjects = new IProject[size];
        
        List<IProject> addedProjects = new ArrayList<IProject>(size);

        for (int i = 0; i < size; i++) {
            String projectName = (String)referenceStrings.get(i);
            allProjects[i] = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        }
       
        
        // If user specifies ALWAYS OVERWRITE, we don't want to confirm overwrite for any project
        boolean yesToAll = overwriteExistingResourcesCheckbox.getSelection();
        
        int action = -1;
        
        for (int i = 0; i < size; i++) {
            Shell shell = null;
            IProject project = allProjects[i];
            if (project.exists()) {
                if (context instanceof Shell) shell = (Shell)context;
                else return null;
                
                if( !yesToAll ) {
                	// Check if any projects will be overwritten, and warn the user.
                	action = confirmOverwrite(project, yesToAll, shell);
                    if( action == 2 ) {
                    	yesToAll = true;
                    }
                }
                
                if( yesToAll || action == 0 ) {
                	addedProjects.add(project);
                }
            } else {
            	addedProjects.add(project);
            }
        }

        return addedProjects;
    }

    /**
     * utility method to present a dialog to the user to overwrite existing projects on import.
     * 
     * @param project
     * @param yesToAll
     * @param shell
     * @return
     * @since 4.2
     */
    private int confirmOverwrite( IProject project,
                                  boolean yesToAll,
                                  Shell shell ) {
        if (yesToAll) return 2;
        if (!project.exists()) return 0;
        final MessageDialog dialog = new MessageDialog(shell, getString("projectExists", project.getName()), null, //$NON-NLS-1$,
                                                       getString("overwriteExistingProject"), MessageDialog.QUESTION, //$NON-NLS-1$,
                                                       new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
                                                           IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
        final int[] result = new int[1];
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                result[0] = dialog.open();
            }
        });
        return result[0];
    }

    /**
     * Returns the current project location path as entered by the user, or its anticipated initial value.
     * 
     * @return the project location path, its anticipated initial value, or <code>null</code> if no project location path is known
     */
    public IPath getLocationPath( String projectName ) {
        String fileSep = FileSeparatorUtil.getFileSeparator(getResourcePath().toString());
        return new Path(getResourcePath() + fileSep + projectName);
    }

    /**
     * Creates a new project resource with the selected name.
     * <p>
     * In normal usage, this method is invoked after the user has pressed Finish on the wizard; the enablement of the Finish
     * button implies that all controls on the pages currently contain valid values.
     * </p>
     * 
     * @return the created project resource, or <code>null</code> if the project was not created
     */
    private IProject createExistingProject( IProject inputProject,
                                            final int nProjects,
                                            final int iProject ) {
        final IProject project = inputProject;
        String projectName = project.getName();
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IPath locationPath = getLocationPath(projectName);
        final IProjectDescription description = workspace.newProjectDescription(projectName);
        // If it is under the root use the default location
        if (isPrefixOfRoot(locationPath)) description.setLocation(null);
        else description.setLocation(locationPath);

        // create the new project operation
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            protected void execute( IProgressMonitor monitor ) throws CoreException {
                monitor.beginTask("", 2000); //$NON-NLS-1$
                project.create(description, new SubProgressMonitor(monitor, 1000));
                if (monitor.isCanceled()) throw new OperationCanceledException();
                project.open(new SubProgressMonitor(monitor, 1000));
            }
        };

        // run the new project creation operation
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return null;
        } catch (InvocationTargetException e) {
            // ie.- one of the steps resulted in a core exception
            Throwable t = e.getTargetException();
            if (t instanceof CoreException) {
                if (((CoreException)t).getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                    MessageDialog.openError(getShell(), getString("createExitingProjectErrorMessage", inputProject.getName()), //$NON-NLS-1$,
                                            null);
                } else {
                    refreshExistingProject(project);
                }
            }
            return null;
        }

        return project;
    }

    /**
     * Return whether or not the specifed location is a prefix of the root.
     */
    private boolean isPrefixOfRoot( IPath locationPath ) {
        return Platform.getLocation().isPrefixOf(locationPath);
    }

    /**
     * utility method for refreshing an existing project in the workspace.
     * 
     * @param existingProject
     * @since 4.2
     */
    private void refreshExistingProject( IProject existingProject ) {
        try {
            existingProject.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException err) {
            ErrorDialog.openError(getShell(), getString("refreshExistingProjectErrorMessage", existingProject.getName()),//$NON-NLS-1$
                                  null,
                                  err.getStatus());
        }
    }

    /**
     * @see org.eclipse.ui.dialogs.WizardDataTransferPage#validateDestinationGroup()
     * @since 4.2
     */
    @Override
    protected boolean validateDestinationGroup() {
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

    /**
     * Validates the selection ensuring there is no .project file in the selected directory or in an ancestor directory.
     * 
     * @since 4.2
     */
    class Validator implements ISelectionStatusValidator {
        /**
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         * @since 4.2
         */
        public IStatus validate( Object[] theSelection ) {
            IStatus result = STATUS_OK;

            if (theSelection.length > 0) {
                // need to make sure a ".project" does not exist in this and any ancestor directory
                if (containsProjectFile((File)theSelection[0])) {
                    result = STATUS_ERROR;
                }
            }

            return result;
        }

        private boolean containsProjectFile( File theFile ) {
            boolean result = false;

            if (theFile.isDirectory()) {
                File[] kids = theFile.listFiles();

                if ((kids != null) && kids.length > 0) {
                    for (int i = 0; i < kids.length; i++) {
                        if (!kids[i].isDirectory() && kids[i].getName().equals(DOT_PROJECT)) {
                            result = true;
                            break;
                        }
                    }
                }

                if (!result) {
                    File parent = theFile.getParentFile();

                    if (parent != null) {
                        return containsProjectFile(parent);
                    }

                }
            }

            return result;
        }
    }

    class ProjectReference {
        private String project;
        private boolean isModelerProject;

        // private Image image;

        public ProjectReference( String project,
                                 boolean isModelerProject ) {
            super();
            this.project = project;
            this.isModelerProject = isModelerProject;
        }

        /**
         * @return Returns the isModelerProject.
         */
        public boolean isModelerProject() {
            return isModelerProject;
        }

        /**
         * @return Returns the project.
         */
        public String getProject() {
            return project;
        }

        public Image getImage() {
            if (isModelerProject) {
                return UiPlugin.getDefault().getProjectImage();
            }

            return UiPlugin.getDefault().getSimpleProjectImage();
        }
    }

    class ProjectReferenceLabelProvider implements ILabelProvider {

        public Image getImage( Object element ) {
            ProjectReference projectReference = (ProjectReference)element;
            return projectReference.getImage();
        }

        public String getText( Object element ) {
            ProjectReference projectReference = (ProjectReference)element;
            return projectReference.getProject();
        }

        public void addListener( ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        public void removeListener( ILabelProviderListener listener ) {
        }

    }
}

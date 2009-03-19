/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
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
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.XMLExtensionsFilter;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * Source WSDL and Target Relational Model Selection page. This page of the WSDL to Relational Importer is used to select the
 * source wsdl file for processing and the target relational model in which the generated entities will be placed.
 */
public class SelectWsdlPage extends WizardPage
    implements Listener, FileUtils.Constants, ModelGeneratorWsdlUiConstants, ModelGeneratorWsdlUiConstants.Images,
    ModelGeneratorWsdlUiConstants.HelpContexts {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SelectWsdlPage.class);

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    /** Source radio buttons and validate button */
    private Button radioSelectWorkspace;
    private Button radioSelectFileSystem;
    private Button radioSelectURL;
    private Button buttonValidateWSDL;

    /** Source and target text fields */
    Text textFieldWorkspace;
    Text textFieldFileSystem;
    Text textFieldURL;
    Text textFieldTargetModelName;
    Text textFieldTargetModelLocation;

    /** selection buttons */
    Button buttonSelectFileSystem;
    Button buttonSelectWorkspace;
    Button buttonSelectTargetModel;
    Button buttonSelectTargetModelLocation;

    /** The import manager. */
    private WSDLImportWizardManager importManager;

    private boolean urlValid = false;
    private boolean urlReadable = false;
    private MultiStatus wsdlStatus;
    private IContainer targetModelLocation;
    private boolean initializing = false;

    /**
     * Constructs the page with the provided import manager
     * 
     * @param theImportManager the import manager object
     */
    public SelectWsdlPage( WSDLImportWizardManager theImportManager ) {
        super(SelectWsdlPage.class.getSimpleName(), getString("title"), null); //$NON-NLS-1$
        this.importManager = theImportManager;
        setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * widget event handler
     * 
     * @param event the widget event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            // Select Workspace radio selected
            if (event.widget == this.radioSelectWorkspace && this.radioSelectWorkspace.getSelection()) {
                this.importManager.setWSDLFileUri(null);
                this.textFieldFileSystem.setText(EMPTY_STR);
                this.textFieldURL.setText(EMPTY_STR);
                updateWidgetEnablements();
                validate = true;
                // Select FileSystem radio selected
            } else if (event.widget == this.radioSelectFileSystem && this.radioSelectFileSystem.getSelection()) {
                this.importManager.setWSDLFileUri(null);
                this.textFieldWorkspace.setText(EMPTY_STR);
                this.textFieldURL.setText(EMPTY_STR);
                updateWidgetEnablements();
                validate = true;
                // Select URL radio selected
            } else if (event.widget == this.radioSelectURL && this.radioSelectURL.getSelection()) {
                this.importManager.setWSDLFileUri(null);
                this.textFieldFileSystem.setText(EMPTY_STR);
                this.textFieldWorkspace.setText(EMPTY_STR);
                String urlText = this.textFieldURL.getText();
                updateCurrentURL(urlText);
                updateWidgetEnablements();
                validate = true;
                // Handle Workspace Browse Button Selection
            } else if (event.widget == this.buttonSelectWorkspace) {
                handleBrowseWorkspaceWsdlFile();
                validate = true;
                // Handle FileSystem Browse Button Selection
            } else if (event.widget == this.buttonSelectFileSystem) {
                handleBrowseFileSystemWsdlFile();
                validate = true;
                // Handle Workspace browse for target model
            } else if (event.widget == this.buttonSelectTargetModel) {
                handleBrowseWorkspaceForTargetModel();
                validate = true;
                // Handle workspace browse for target model location
            } else if (event.widget == this.buttonSelectTargetModelLocation) {
                handleBrowseWorkspaceForTargetModelLocation();
                validate = true;
                // Handle wsdl validate button pressed
            } else if (event.widget == this.buttonValidateWSDL) {
                handleValidateWSDLButtonPressed();
                validate = true;
            }
            // Update the page status
            if (validate) {
                setPageStatus();
            }
        }
    }

    /**
     * Updates the enabled state of source selection controls
     */
    private void updateWidgetEnablements() {
        // Workspace control enablement
        if (this.radioSelectWorkspace.getSelection()) {
            this.textFieldWorkspace.setEnabled(true);
            this.buttonSelectWorkspace.setEnabled(true);
        } else {
            this.textFieldWorkspace.setEnabled(false);
            this.buttonSelectWorkspace.setEnabled(false);
        }

        if (this.radioSelectFileSystem.getSelection()) {
            this.textFieldFileSystem.setEnabled(true);
            this.buttonSelectFileSystem.setEnabled(true);
        } else {
            this.textFieldFileSystem.setEnabled(false);
            this.buttonSelectFileSystem.setEnabled(false);
        }

        if (this.radioSelectURL.getSelection()) {
            this.textFieldURL.setEnabled(true);
        } else {
            this.textFieldURL.setEnabled(false);
        }

        updateValidateWSDLButtonEnablement();
    }

    /**
     * Updates the enabled state of the WSDL validation button.
     */
    private void updateValidateWSDLButtonEnablement() {
        // if wsdl already has valid status, disable
        if (this.wsdlStatus != null && this.wsdlStatus.isOK()) {
            this.buttonValidateWSDL.setEnabled(false);
        } else {
            // if there is a wsdl selection, enable validation button
            if (this.importManager.getWSDLFileUri() != null) {
                this.buttonValidateWSDL.setEnabled(true);
            } else {
                this.buttonValidateWSDL.setEnabled(false);
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite theParent ) {
        //
        // create main container
        //

        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(COLUMNS, false);
        pnlMain.setLayout(layout);
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, WSDL_SELECTION_PAGE);

        // Controls for Selection of WSDL
        createSourceSelectionComposite(pnlMain);

        // Controls for Selection of Relational target model
        createTargetSelectionComposite(pnlMain);

        // Refresh Controls from manager
        refreshUiFromManager();

        // Set the initial page status
        setPageStatus();

        restoreState();
    }

    /**
     * Constructs the source WSDL selection component panel.
     * 
     * @param theParent the parent container
     */
    private void createSourceSelectionComposite( Composite theParent ) {
        final int COLUMNS = 1;
        String text = ""; //$NON-NLS-1$
        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        // options group
        Group optionsGroup = new Group(pnl, SWT.NONE);
        optionsGroup.setText(getString("sourceOptionsGroup.text")); //$NON-NLS-1$

        GridData gdRadioGroup = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gdRadioGroup);

        optionsGroup.setLayout(new GridLayout(3, false));

        // --------------------------------------------
        // Composite for Workspace Selection
        // --------------------------------------------
        // Workspace Radio
        radioSelectWorkspace = WidgetFactory.createRadioButton(optionsGroup, getString("workspaceRadio.text")); //$NON-NLS-1$ 

        // Workspace textfield
        textFieldWorkspace = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        text = getString("workspaceTextField.tooltip"); //$NON-NLS-1$
        textFieldWorkspace.setToolTipText(text);
        textFieldWorkspace.setEditable(false);

        // Workspace Browse Button
        buttonSelectWorkspace = WidgetFactory.createButton(optionsGroup, getString("workspaceBrowseButton.text"), GridData.FILL); //$NON-NLS-1$
        buttonSelectWorkspace.setToolTipText(getString("workspaceBrowseButton.tooltip")); //$NON-NLS-1$
        // --------------------------------------------
        // Composite for FileSystem Selection
        // --------------------------------------------
        // File System Radio
        radioSelectFileSystem = WidgetFactory.createRadioButton(optionsGroup, getString("fileSystemRadio.text")); //$NON-NLS-1$

        // FileSystem textfield
        textFieldFileSystem = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        text = getString("fileSystemTextField.tooltip"); //$NON-NLS-1$
        textFieldFileSystem.setToolTipText(text);
        textFieldFileSystem.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleTextFieldFileSystemChanged();
            }
        });

        // File System Browse Button
        buttonSelectFileSystem = WidgetFactory.createButton(optionsGroup, getString("fileSystemBrowseButton.text"), GridData.FILL); //$NON-NLS-1$
        buttonSelectFileSystem.setToolTipText(getString("fileSystemBrowseButton.tooltip")); //$NON-NLS-1$

        // --------------------------------------------
        // Composite for URL Selection
        // --------------------------------------------
        // URL Radio
        radioSelectURL = WidgetFactory.createRadioButton(optionsGroup, getString("urlRadio.text"), true); //$NON-NLS-1$

        // URL textfield
        textFieldURL = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL, 2);
        text = getString("urlTextField.tooltip"); //$NON-NLS-1$
        textFieldURL.setToolTipText(text);
        textFieldURL.setText(EMPTY_STR);
        textFieldURL.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                handleURLChanged();
            }
        });

        // --------------------------------------------
        // WSDL Validation Button
        // --------------------------------------------
        buttonValidateWSDL = WidgetFactory.createButton(optionsGroup,
                                                        getString("validateWsdlButton.text"), GridData.HORIZONTAL_ALIGN_END, 3); //$NON-NLS-1$
        buttonValidateWSDL.setToolTipText(getString("validateWsdlButton.tooltip")); //$NON-NLS-1$

        // --------------------------------------------
        // Add Listener to handle selection events
        // --------------------------------------------
        buttonSelectWorkspace.addListener(SWT.Selection, this);
        radioSelectWorkspace.addListener(SWT.Selection, this);
        buttonSelectFileSystem.addListener(SWT.Selection, this);
        radioSelectFileSystem.addListener(SWT.Selection, this);
        radioSelectURL.addListener(SWT.Selection, this);
        buttonValidateWSDL.addListener(SWT.Selection, this);

        updateWidgetEnablements();
    }

    /**
     * Constructs the target Relational Model selection component panel.
     * 
     * @param theParent the parent container
     */
    private void createTargetSelectionComposite( Composite theParent ) {

        final int COLUMNS = 1;

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        // options group
        Group optionsGroup = new Group(pnl, SWT.NONE);
        optionsGroup.setText(getString("targetOptionsGroup.text")); //$NON-NLS-1$

        GridData gdRadioGroup = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gdRadioGroup);

        optionsGroup.setLayout(new GridLayout(3, false));

        // --------------------------------------------
        // Composite for Model Selection
        // --------------------------------------------
        // Select Model Label
        //WidgetFactory.createLabel( optionsGroup, getString("targetModelLabel.text")); //$NON-NLS-1$ 
        CLabel theLabel = new CLabel(optionsGroup, SWT.NONE);
        theLabel.setText(getString("targetModelLabel.text")); //$NON-NLS-1$
        final GridData gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        theLabel.setLayoutData(gridData);

        // target model name textfield
        textFieldTargetModelName = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        String text = getString("targetModelTextField.tooltip"); //$NON-NLS-1$
        textFieldTargetModelName.setToolTipText(text);
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
        //WidgetFactory.createLabel( optionsGroup, getString("targetModelLocationLabel.text")); //$NON-NLS-1$
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

        // Model Location Browse Button
        buttonSelectTargetModelLocation = WidgetFactory.createButton(optionsGroup,
                                                                     getString("targetModelLocationBrowseButton.text"), GridData.FILL); //$NON-NLS-1$
        buttonSelectTargetModelLocation.setToolTipText(getString("targetModelLocationBrowseButton.tooltip")); //$NON-NLS-1$
        buttonSelectTargetModelLocation.addListener(SWT.Selection, this);

        // --------------------------------------------
        // Add Listener to handle selection events
        // --------------------------------------------
        buttonSelectTargetModel.addListener(SWT.Selection, this);

    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * Override to replace the ImportWizard settings with the section devoted to the WSDL import Wizard.
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();

        if (settings != null) {
            // get the right section of the NewModelWizard settings
            IDialogSettings temp = settings.getSection(DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(DIALOG_SETTINGS_SECTION);
            } else {
                settings = temp;
            }
        }

        return settings;
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     */
    static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @param parameter the parameter
     * @return the localized text
     */
    private static String getString( final String theKey,
                                     final Object parameter ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString(), parameter);
    }

    /**
     * Handler for workspace WSDL Browse button.
     */
    private void handleBrowseWorkspaceWsdlFile() {
        // Show the Workspace WSDL File Selection dialog
        Object[] wsdlFiles = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.browseWorkspaceWsdl.title"), //$NON-NLS-1$
                                                                           getString("dialog.browseWorkspaceWsdl.msg"), //$NON-NLS-1$
                                                                           true,
                                                                           null,
                                                                           new ModelingResourceFilter(this.wsdlFilter),
                                                                           this.wsdlValidator);
        // Update the import manager and controls with the selection
        if ((wsdlFiles != null) && (wsdlFiles.length > 0)) {

            Object[] aryFiles = new Object[wsdlFiles.length];
            for (int i = 0; i < wsdlFiles.length; i++) {
                IFile ifFile = (IFile)wsdlFiles[i];

                // Convert the IFile object to a File object
                File fNew = ifFile.getLocation().toFile();
                aryFiles[i] = fNew;
            }

            String uriStr = null;
            try {
                uriStr = ((File)aryFiles[0]).toURI().toURL().toExternalForm();
            } catch (MalformedURLException err) {
                // exception will leave uri null
            }
            this.importManager.setWSDLFileUri(uriStr);
            this.textFieldWorkspace.setText(uriStr);
            this.wsdlStatus = null;
            updateValidateWSDLButtonEnablement();
        }
    }

    /**
     * Handler for FileSystem WSDL Browse button.
     */
    private void handleBrowseFileSystemWsdlFile() {
        // File System Dialog
        FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
        dialog.setText(getString("dialog.browseFileSystemWsdl.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(ModelGeneratorWsdlUiUtil.FILE_DIALOG_WSDL_EXTENSIONS);

        // Open the dialog
        if (dialog.open() != null) {
            boolean validFile = true;
            String[] filenames = dialog.getFileNames();

            if ((filenames != null) && (filenames.length > 0)) {
                String directory = dialog.getFilterPath();
                Object[] wsdlFiles = new Object[filenames.length];

                for (int i = 0; i < filenames.length; i++) {
                    String path = new StringBuffer().append(directory).append(File.separatorChar).append(filenames[i]).toString();
                    wsdlFiles[i] = new File(path);

                    // make sure the right type of file was selected. since the user can enter *.* in file name
                    // field of the dialog they can view all files regardless of the filter extensions. this allows
                    // them to actually select invalid file types.

                    if (!ModelGeneratorWsdlUiUtil.isWsdlFile((File)wsdlFiles[i])) {
                        validFile = false;
                        break;
                    }
                }

                // If WSDL file was selected, update the manager and the controls
                if (validFile) {
                    String fileStr = ((File) wsdlFiles[0]).getAbsolutePath();
					this.importManager.setWSDLFileUri(fileStr);
					this.textFieldFileSystem.setText(fileStr);
					this.wsdlStatus = null;
					updateValidateWSDLButtonEnablement();
                } else {
                    // open file chooser again based on if user OK'd dialog
                    if (MessageDialog.openQuestion(getShell(), getString("dialog.browseFileSystemWsdl.wrongFileType.title"), //$NON-NLS-1$
                                                   getString("dialog.browseFileSystemWsdl.wrongFileType.msg"))) { //$NON-NLS-1$
                        handleBrowseFileSystemWsdlFile();
                    }
                }
            }
        }
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
        dlg.addFilter(new ModelingResourceFilter(this.targetLocationFilter));
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

    /**
     * Handler for Validate WSDL Button pressed
     */
    private void handleValidateWSDLButtonPressed() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                validateWSDL(monitor);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            dlg.run(false, false, op);
        } catch (final InterruptedException ignored) {
        } catch (final Exception err) {
            err.printStackTrace(System.err);
        }
        if (!this.wsdlStatus.isOK()) {
            Shell shell = this.getShell();
            ErrorDialog.openError(shell,
                                  getString("dialog.wsdlValidationError.title"), getString("dialog.wsdlValidationError.msg"), this.wsdlStatus); //$NON-NLS-1$  //$NON-NLS-2$
        }
        updateValidateWSDLButtonEnablement();
        setPageStatus();
    }

    void validateWSDL( IProgressMonitor monitor ) {
        this.wsdlStatus = this.importManager.validateWSDL(monitor);
    }

    /**
     * Handler for changes to the File System textbox.
     */
    void handleTextFieldFileSystemChanged() {
        if (this.radioSelectFileSystem.getSelection()) {
            updateCurrentFileSystemSelection(this.textFieldFileSystem.getText());
            this.wsdlStatus = null;
            updateValidateWSDLButtonEnablement();
            setPageStatus();
        }
    }

    private void updateCurrentFileSystemSelection( String text ) {
        text = text.replace('\\', '/');
        File file = new File(text);
        if (file.exists() && file.isFile() && ModelGeneratorWsdlUiUtil.isWsdlFile(file)) {
            this.importManager.setWSDLFileUri(file.getAbsolutePath());
        }
    }

    /**
     * Handler for changes to the URL textbox.
     */
    void handleURLChanged() {
        if (this.radioSelectURL.getSelection()) {
            updateCurrentURL(this.textFieldURL.getText());
            this.wsdlStatus = null;
            updateValidateWSDLButtonEnablement();
            setPageStatus();
        }
    }

    /**
     * Takes a url string and tries to read it, saving state info
     * 
     * @param urlText the supplied url text
     */
    private void updateCurrentURL( String urlText ) {
        this.importManager.setWSDLFileUri(null);
        if (urlText != null && urlText.length() > 0) {
            this.urlValid = isValidUri(urlText);
            if (this.urlValid) {
                this.importManager.setWSDLFileUri(urlText.replaceAll("\\\\", "/")); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * Determines if the supplied string is a valid formatted URI
     * 
     * @param str the supplied uri string
     * @return 'true' if the string is a valid format, 'false' if not.
     */
    public boolean isValidUri( String str ) {
        try {
            new org.apache.xerces.util.URI(str);
        } catch (org.apache.xerces.util.URI.MalformedURIException e) {
            return false;
        }
        return true;
    }

    /**
     * Restores dialog size and position of the last time wizard ran.
     */
    private void restoreState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null && getContainer() != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                try {
                    int x = settings.getInt(DIALOG_X);
                    int y = settings.getInt(DIALOG_Y);
                    int width = settings.getInt(DIALOG_WIDTH);
                    int height = settings.getInt(DIALOG_HEIGHT);
                    shell.setBounds(x, y, width, height);
                } catch (NumberFormatException theException) {
                    // getInt(String) throws exception if not found.
                    // just means no settings exist yet.
                }
            }
        }
    }

    /**
     * Persists dialog size and position.
     */
    private void saveState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null && getContainer() != null) {
            Shell shell = getContainer().getShell();

            if (shell != null) {
                Rectangle r = shell.getBounds();
                settings.put(DIALOG_X, r.x);
                settings.put(DIALOG_Y, r.y);
                settings.put(DIALOG_WIDTH, r.width);
                settings.put(DIALOG_HEIGHT, r.height);
            }
        }
    }

    /**
     * Refresh the ui state from the manager
     */
    private void refreshUiFromManager() {
        if (this.importManager != null) {
            String tgtModelName = this.importManager.getTargetModelName();
            if (this.textFieldTargetModelName != null && tgtModelName != null) {
                this.textFieldTargetModelName.setText(tgtModelName);
            }
            IContainer tgtModelLocation = this.importManager.getTargetModelLocation();
            if (this.textFieldTargetModelLocation != null && tgtModelLocation != null) {
                this.textFieldTargetModelLocation.setText(tgtModelLocation.getFullPath().makeRelative().toString());
            }

            if (importManager.getUriSource() == WSDLImportWizardManager.WORKSPACE_SOURCE) {
                this.radioSelectWorkspace.setSelection(true);
                this.radioSelectFileSystem.setSelection(false);
                this.radioSelectURL.setSelection(false);
                updateWidgetEnablements();
            }

            if (this.textFieldWorkspace != null && this.radioSelectWorkspace != null && this.radioSelectWorkspace.getSelection()) {
                String wsdlFileUri = this.importManager.getWSDLFileUri();
                if (wsdlFileUri != null) {
                    this.textFieldWorkspace.setText(wsdlFileUri);
                }
                if (this.textFieldFileSystem != null) {
                    this.textFieldFileSystem.setText(EMPTY_STR);
                }
                if (this.textFieldURL != null) {
                    this.textFieldURL.setText(EMPTY_STR);
                }
            }
        }
    }

    /**
     * Performs validation and sets the page status.
     */
    void setPageStatus() {
        // Validate the source WSDL Selection
        boolean sourceValid = validateSourceSelection();
        if (!sourceValid) {
            return;
        }

        // Validate the target relational model name and location
        boolean targetValid = validateTargetModelNameAndLocation();
        if (!targetValid) {
            return;
        }

        // Finally, display a warning message if there were WSDL validation errors.
        if (this.wsdlStatus.getSeverity() > IStatus.WARNING) {
            WizardUtil.setPageComplete(this, getString("wsdlErrorContinuation.msg"), IMessageProvider.WARNING); //$NON-NLS-1$
        } else {
            WizardUtil.setPageComplete(this);
        }

        getContainer().updateButtons();
    }

    /**
     * Sets the initial workspace selection.
     * 
     * @param theSelection the current workspace selection
     */
    public void setInitialSelection( ISelection theSelection ) {
        this.importManager.setWSDLFileUri(null);
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
                    if (ModelGeneratorWsdlUiUtil.isWsdlFile((IFile)selectedObjects[i])
                        || ModelGeneratorWsdlUiUtil.isModelFile((IFile)selectedObjects[i])) {
                        // Convert the IFile object to a File object
                        File fNew = ((IFile)selectedObjects[i]).getLocation().toFile();
                        if (fNew != null) {
                            String uriStr = null;
                            try {
                                uriStr = fNew.toURI().toURL().toExternalForm();
                            } catch (MalformedURLException err) {
                                // exception will leave uri null
                            }
                            if (ModelGeneratorWsdlUiUtil.isWsdlFile((IFile)selectedObjects[i])) {
                                this.importManager.setUriSource(WSDLImportWizardManager.WORKSPACE_SOURCE);
                                this.importManager.setWSDLFileUri(uriStr);
                                break;
                            } else if (ModelGeneratorWsdlUiUtil.isModelFile((IFile)selectedObjects[i])) {
                                this.importManager.setTargetModelName(uriStr.substring(uriStr.lastIndexOf('/') + 1));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * validate the selected source WSDL. Returns 'true' if the validation is successful, 'false' if not.
     * 
     * @return 'true' if the WSDL selection is valid, 'false' if not.
     */
    private boolean validateSourceSelection() {
        String msg = getString("pageComplete.msg"); //$NON-NLS-1$
        String sourceWsdl = this.importManager.getWSDLFileUri();

        // If no WSDL is specified, set message and return
        if (sourceWsdl == null) {
            // No WSDL selected message
            msg = getString("noWsdlSelected.msg"); //$NON-NLS-1$
            // If URL radio is selected, check URL validity
            if (this.radioSelectURL.getSelection()) {
                String urlText = this.textFieldURL.getText();
                if (urlText == null || urlText.length() == 0) {
                    msg = getString("noURLString.msg"); //$NON-NLS-1$
                } else if (!this.urlValid) {
                    msg = getString("invalidURLString.msg"); //$NON-NLS-1$
                } else if (!this.urlReadable) {
                    msg = getString("urlValidNotReadable.msg"); //$NON-NLS-1$
                }
            } else {
                if (this.radioSelectWorkspace.getSelection()) {
                    msg = getString("noWsdlSelected.workspace.msg"); //$NON-NLS-1$
                }
            }
            WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
            return false;
        }

        // If WSDL is specified, see if it's been validated
        if (this.wsdlStatus == null) {
            msg = getString("validateWsdl.msg"); //$NON-NLS-1$
            WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
            return false;
        }

        WizardUtil.setPageComplete(this);

        return true;
    }

    /**
     * validate the selected target relational model name and location. Returns 'true' if the validation is successful, 'false' if
     * not.
     * 
     * @return 'true' if the WSDL selection is valid, 'false' if not.
     */
    private boolean validateTargetModelNameAndLocation() {
        // Hardcode the updating flag to false for now.
        // Plan to implement in the future.
        // final boolean updating = this.updateCheckBox.getSelection();
        final boolean updating = false;
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
                } catch (final ModelWorkspaceException err) {
                    UTIL.log(err);
                    WidgetUtil.showError(err.getLocalizedMessage());
                }
            } else if (updating) {
                String msg = getString(getString("noModelToUpdateMessage"), file.getFullPath().makeRelative()); //$NON-NLS-1$
                WizardUtil.setPageComplete(this, msg, IMessageProvider.ERROR);
                return false;
            }
            this.importManager.setTargetModelName(targetModelName);
            this.importManager.setTargetModelLocation(targetModelLocation);
            // this.importManager.setUpdatedModel(model);
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

    /** Filter for selecting WSDL files and their parent containers. */
    private ViewerFilter wsdlFilter = new ViewerFilter() {

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
                        UTIL.log(theException);
                    }
                }
            } else if (theElement instanceof IFile) {
                result = ModelGeneratorWsdlUiUtil.isWsdlFile((IFile)theElement);
            } else if (theElement instanceof File) {
                return (((File)theElement).isDirectory() || ModelGeneratorWsdlUiUtil.isWsdlFile(((File)theElement)));
            }

            return result;
        }
    };

    /** Validator that makes sure the selection containes all WSDL files. */
    private ISelectionStatusValidator wsdlValidator = new ISelectionStatusValidator() {
        public IStatus validate( Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length > 0)) {
                for (int i = 0; i < theSelection.length; i++) {
                    if ((!(theSelection[i] instanceof IFile)) || !ModelGeneratorWsdlUiUtil.isWsdlFile((IFile)theSelection[i])) {
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
                result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, getString("selectionNotWsdl.msg")); //$NON-NLS-1$
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

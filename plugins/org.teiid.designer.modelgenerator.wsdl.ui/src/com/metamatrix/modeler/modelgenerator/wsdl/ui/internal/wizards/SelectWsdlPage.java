/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.util.FileUtils;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
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
    
    private static final String WSDL_URI_PROP_KEY = "wsdlURI"; //$NON-NLS-1$

    private static final String EMPTY_STR = ""; //$NON-NLS-1$

    /** Source radio buttons and validate button */
    private Button buttonValidateWSDL;

    /** Source and target text fields */
    Text wsdlURIText;
    Text textFieldTargetModelLocation;

    /** selection buttons */
    Button buttonSelectTargetModelLocation;

    /** The import manager. */
    private WSDLImportWizardManager importManager;

    private boolean urlValid = false;
    private boolean urlReadable = false;
    private MultiStatus wsdlStatus;
    private IContainer targetModelLocation;
    private boolean initializing = false;

    private ProfileManager profileManager;

    private Combo profileCombo;

    /**
     * Constructs the page with the provided import manager
     * 
     * @param theImportManager the import manager object
     */
    public SelectWsdlPage( WSDLImportWizardManager theImportManager ) {
        super(SelectWsdlPage.class.getSimpleName(), getString("title"), null); //$NON-NLS-1$
        this.importManager = theImportManager;
        setImageDescriptor(ModelGeneratorWsdlUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
        profileManager = ProfileManager.getInstance();
    }

    /**
     * widget event handler
     * 
     * @param event the widget event
     */
    public void handleEvent( Event event ) {
        if (!initializing) {
            boolean validate = false;

            if (event.widget == this.buttonSelectTargetModelLocation) {
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
        Group optionsGroup = new Group(pnl, SWT.FILL);
        optionsGroup.setText(getString("sourceOptionsGroup.text")); //$NON-NLS-1$
        optionsGroup.setLayout(new GridLayout(2, false));
        optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData gridData;
        CLabel profileLabel = new CLabel(optionsGroup, SWT.NONE);
        profileLabel.setText("Connection Profile");//getString("profileLabel.text")); //$NON-NLS-1$
        gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        profileLabel.setLayoutData(gridData);

        profileCombo = new Combo(optionsGroup, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        profileCombo.setLayoutData(gridData);
        profileCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                refreshUiFromManager();
            }
        });

        CLabel wsldLabel = new CLabel(optionsGroup, SWT.NONE);
        wsldLabel.setText(getString("wsdlLabel.text")); //$NON-NLS-1$
        gridData = new GridData(SWT.NONE);
        gridData.horizontalSpan = 1;
        wsldLabel.setLayoutData(gridData);

        // Workspace textfield
        wsdlURIText = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        text = getString("workspaceTextField.tooltip"); //$NON-NLS-1$
        wsdlURIText.setToolTipText(text);
        wsdlURIText.setEditable(false);
        wsdlURIText.setEnabled(false);

        // --------------------------------------------
        // WSDL Validation Button
        // --------------------------------------------
        buttonValidateWSDL = WidgetFactory.createButton(optionsGroup,
                                                        getString("validateWsdlButton.text"), GridData.HORIZONTAL_ALIGN_END, 3); //$NON-NLS-1$
        buttonValidateWSDL.setToolTipText(getString("validateWsdlButton.tooltip")); //$NON-NLS-1$

        // --------------------------------------------
        // Add Listener to handle selection events
        // --------------------------------------------
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
        String text = getString("targetModelLocationTextField.tooltip"); //$NON-NLS-1$
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

    private void updateCurrentFileSystemSelection( String text ) {
        text = text.replace('\\', '/');
        File file = new File(text);
        if (file.exists() && file.isFile() && ModelGeneratorWsdlUiUtil.isWsdlFile(file)) {
            this.importManager.setWSDLFileUri(file.getAbsolutePath());
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
            
        	IContainer tgtModelLocation = this.importManager.getTargetModelLocation();
            if (this.textFieldTargetModelLocation != null && tgtModelLocation != null) {
                this.textFieldTargetModelLocation.setText(tgtModelLocation.getFullPath().makeRelative().toString());
            }

            if (null == profileCombo.getItems() || 0 == profileCombo.getItems().length) {
                IConnectionProfile[] sfProfiles = profileManager.getProfilesByCategory("org.eclipse.datatools.enablement.oda.ws"); //$NON-NLS-1$
                if (sfProfiles.length == 0) {
                    setErrorMessage(getString("no.profile")); //$NON-NLS-1$
                    wsdlURIText.setText(EMPTY_STR);
                    buttonValidateWSDL.setEnabled(false);
                    return;
                } else {
                    List<String> profileNames = new ArrayList();
                    for (int i = 0; i < sfProfiles.length; i++) {
                        IConnectionProfile profile = sfProfiles[i];
                        profileNames.add(profile.getName());
                    }
                    profileCombo.setItems(profileNames.toArray(new String[profileNames.size()]));
                    setErrorMessage(null);
                    setMessage(getString("select.profile")); //$NON-NLS-1$
                    return;
                }
            }

            String profileName = profileCombo.getText();
            IConnectionProfile profile = findMatchingProfile(profileName);
            if (null == profile) {
                // this should really never happen
                setMessage(null);
                setErrorMessage(getString("no.profile.match", new Object[] {profileName})); //$NON-NLS-1$
                buttonValidateWSDL.setEnabled(false);
                return;
            }
            Properties props = profile.getBaseProperties();
            wsdlURIText.setText(props.getProperty(WSDL_URI_PROP_KEY));
            importManager.setWSDLFileUri(props.getProperty(WSDL_URI_PROP_KEY));
            updateWidgetEnablements();
        }
    }

    private IConnectionProfile findMatchingProfile( String name ) {
        IConnectionProfile result = null;
        IConnectionProfile[] sfProfiles = profileManager.getProfilesByCategory("org.eclipse.datatools.enablement.oda.ws"); //$NON-NLS-1$
        for (int i = 0; i < sfProfiles.length; i++) {
            IConnectionProfile profile = sfProfiles[i];
            if (profile.getName().equals(name)) {
                result = profile;
            }
        }
        return result;
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
            targetModelLocation = validateFileAndFolder(this.textFieldTargetModelLocation,
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
    private IContainer validateFileAndFolder( final Text folderText,
                                              final String fileExtension ) throws CoreException {
        CoreArgCheck.isNotNull(folderText);
        CoreArgCheck.isNotNull(fileExtension);
        final String folderName = folderText.getText();
        if (CoreStringUtil.isEmpty(folderName)) {
        	WizardUtil.setPageComplete(this, getString("missingFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        } else {
        	final IResource resrc = ResourcesPlugin.getWorkspace().getRoot().findMember(folderName);
        	if (resrc == null || !(resrc instanceof IContainer) || resrc.getProject() == null) {
        		WizardUtil.setPageComplete(this, getString("invalidFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        	} else if (!resrc.getProject().isOpen()) {
        		WizardUtil.setPageComplete(this, getString("closedProjectMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
        	} else {
        		final IContainer folder = (IContainer)resrc;
        		WizardUtil.setPageComplete(this);
        		return folder;
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

    /** Validator that makes sure the selection contains all WSDL files. */
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

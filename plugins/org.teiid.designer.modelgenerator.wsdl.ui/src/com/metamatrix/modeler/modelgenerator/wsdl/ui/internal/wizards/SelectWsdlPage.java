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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.teiid.core.util.FileUtils;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileWorker;
import org.teiid.designer.datatools.ui.dialogs.IProfileChangedListener;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelProjectSelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelResourceSelectionValidator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.dialog.FolderSelectionDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;

/**
 * Source WSDL and Target Relational Model Selection page. This page of the WSDL to Relational Importer is used to select the
 * source wsdl file for processing and the target relational model in which the generated entities will be placed.
 */
public class SelectWsdlPage extends WizardPage
    implements Listener, IProfileChangedListener, FileUtils.Constants, ModelGeneratorWsdlUiConstants, ModelGeneratorWsdlUiConstants.Images,
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
    private Button buttonValidateWSDL;

    /** Source and target text fields */
    CLabel wsdlURIText;
    Text textFieldTargetModelLocation;

    /** selection buttons */
    Button buttonSelectTargetModelLocation;
    
    private Button newCPButton;
    private Button editCPButton;

    /** The import manager. */
    private WSDLImportWizardManager importManager;

    private MultiStatus wsdlStatus;
    
	private Text sourceModelContainerText;
	private Text sourceModelFileText;
	private Text sourceHelpText;
	private IPath sourceModelFilePath;
    
    private IContainer targetModelLocation;
    private boolean initializing = false;

    private Combo connectionProfilesCombo;
    private ILabelProvider profileLabelProvider;
    
    private ConnectionProfileWorker profileWorker;
    
    boolean synchronizing = false;

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

    	this.profileWorker = new ConnectionProfileWorker(this.getShell(), ConnectionProfileWorker.CATEGORY_WS_ODA, this);
    	
        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(COLUMNS, false);
        pnlMain.setLayout(layout);
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, WSDL_SELECTION_PAGE);

        // Controls for Selection of WSDL
        createSourceSelectionComposite(pnlMain);
        
        createSourceModelGroup(pnlMain);

        // Controls for Selection of Relational target model
        createTargetSelectionComposite(pnlMain);

        // Refresh Controls from manager
        //refreshUiFromManager();

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

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        // ================================================================================
        Group profileGroup = WidgetFactory.createGroup(pnl, getString("profileLabel.text"), SWT.NONE, 2); //$NON-NLS-1$
        profileGroup.setLayout(new GridLayout(3, false));
        profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        profileLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.connectionProfilesCombo = WidgetFactory.createCombo(profileGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 profileWorker.getProfiles(),
                                                                 profileLabelProvider,
                                                                 true);
        this.connectionProfilesCombo.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.connectionProfilesCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Need to sync the worker with the current profile
				handleConnectionProfileSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        connectionProfilesCombo.setVisibleItemCount(10);
        
        newCPButton = WidgetFactory.createButton(profileGroup, getString("new.label")); //$NON-NLS-1$
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                profileWorker.create();
            }
        });
        
        editCPButton = WidgetFactory.createButton(profileGroup, getString("edit.label")); //$NON-NLS-1$
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                profileWorker.edit();
            }
        });
        
        // options group
        Group optionsGroup = WidgetFactory.createGroup(pnl, getString("wsdlLabel.text"), SWT.FILL,  2); //$NON-NLS-1$
        optionsGroup.setLayout(new GridLayout(2, false));
        optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Workspace textfield
        wsdlURIText = WidgetFactory.createLabel(optionsGroup, GridData.FILL_HORIZONTAL);
        wsdlURIText.setToolTipText(getString("workspaceTextField.tooltip")); //$NON-NLS-1$
        wsdlURIText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        // --------------------------------------------
        // WSDL Validation Button
        // --------------------------------------------
        buttonValidateWSDL = WidgetFactory.createButton(optionsGroup,
                                                        getString("validateWsdlButton.text"), GridData.HORIZONTAL_ALIGN_END, 1); //$NON-NLS-1$
        buttonValidateWSDL.setToolTipText(getString("validateWsdlButton.tooltip")); //$NON-NLS-1$

        // --------------------------------------------
        // Add Listener to handle selection events
        // --------------------------------------------
        buttonValidateWSDL.addListener(SWT.Selection, this);
        
        updateWidgetEnablements();
    }
    
    private void handleConnectionProfileSelected() {
		int selIndex = connectionProfilesCombo.getSelectionIndex();
		
		if( selIndex >= 0 ) {
			String name = connectionProfilesCombo.getItem(selIndex);
			if( name != null ) {
				IConnectionProfile profile = profileWorker.getProfile(name);
				profileWorker.setSelection(profile);
				importManager.setConnectionProfile(profile);
				refreshUiFromManager();
			}
		}
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
        Group optionsGroup = WidgetFactory.createGroup(pnl, getString("targetLocationGroup.text"),SWT.NONE); //$NON-NLS-1$

        GridData gdRadioGroup = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gdRadioGroup);

        optionsGroup.setLayout(new GridLayout(2, false));

        // --------------------------------------------
        // Composite for Model Location Selection
        // --------------------------------------------
        // Select Target Location Label

        final IContainer location = this.importManager.getViewModelLocation();
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
    
	private void createSourceModelGroup(Composite parent) {
		Group sourceGroup = WidgetFactory.createGroup(parent,"Source Model Definition", SWT.NONE, 1); //$NON-NLS-1$
		sourceGroup.setLayout(new GridLayout(3, false));
		sourceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label locationLabel = new Label(sourceGroup, SWT.NULL);
		locationLabel.setText("Location"); //$NON-NLS-1$

		sourceModelContainerText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceModelContainerText.setLayoutData(gridData);
		sourceModelContainerText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		sourceModelContainerText.setForeground(WidgetUtil.getDarkBlueColor());
		sourceModelContainerText.setEditable(false);

		Button browseButton = new Button(sourceGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText("Browse..."); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelLocationBrowse();
			}
		});

		Label fileLabel = new Label(sourceGroup, SWT.NULL);
		fileLabel.setText("Name :"); //$NON-NLS-1$

		sourceModelFileText = new Text(sourceGroup, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceModelFileText.setLayoutData(gridData);
		sourceModelFileText.setForeground(WidgetUtil.getDarkBlueColor());
		sourceModelFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleSourceModelTextChanged();
			}
		});

		browseButton = new Button(sourceGroup, SWT.PUSH);
		gridData = new GridData();
		browseButton.setLayoutData(gridData);
		browseButton.setText("Browse..."); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSourceModelBrowse();
			}
		});

		new Label(sourceGroup, SWT.NONE);

		Group helpGroup = WidgetFactory.createGroup(sourceGroup,
				"Model Status", SWT.NONE | SWT.BORDER_DASH, 2); //$NON-NLS-1$
		helpGroup.setLayout(new GridLayout(1, false));
		helpGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			sourceHelpText = new Text(helpGroup, SWT.WRAP | SWT.READ_ONLY);
			sourceHelpText.setBackground(WidgetUtil.getReadOnlyBackgroundColor());
			sourceHelpText.setForeground(WidgetUtil.getDarkBlueColor());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 40;
			gd.horizontalSpan = 3;
			sourceHelpText.setLayoutData(gd);
		}

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

        dlg.setInitialSelection(this.importManager.getViewModelLocation());
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
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	void handleSourceModelLocationBrowse() {
		final IContainer folder = WidgetUtil.showFolderSelectionDialog(
				ResourcesPlugin.getWorkspace().getRoot(),
				new ModelingResourceFilter(),
				new ModelProjectSelectionStatusValidator());

		if (folder != null && sourceModelContainerText != null) {
			this.importManager.setSourceModelLocation(folder);
		}

		refreshUiFromManager();

		setPageStatus();
	}

	void handleSourceModelBrowse() {
		final Object[] selections = WidgetUtil
				.showWorkspaceObjectSelectionDialog(
						getString("selectSourceModelTitle"), //$NON-NLS-1$
						getString("selectSourceModelMessage"), //$NON-NLS-1$
						false, null, sourceModelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1
				&& sourceModelFileText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
				String modelName = modelFile.getFullPath().lastSegment();
				importManager.setSourceModelExists(true);
				importManager.setSourceModelLocation(modelFile.getParent());
				importManager.setSourceModelName(modelName);
			}
		}

		refreshUiFromManager();

		setPageStatus();
	}

	void handleSourceModelTextChanged() {
		if (synchronizing)
			return;

		String newName = ""; //$NON-NLS-1$
		if (this.sourceModelFileText.getText() != null && this.sourceModelFileText.getText().length() > -1) {
			newName = this.sourceModelFileText.getText();
			this.importManager.setSourceModelName(newName);
			this.importManager.setSourceModelExists(sourceModelExists());

		}
		refreshUiFromManager();
		setPageStatus();
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
    	synchronizing = true;
    	
        if (this.importManager != null) {
    		if (this.importManager.getSourceModelLocation() != null) {
    			this.sourceModelContainerText.setText(this.importManager.getSourceModelLocation().getFullPath().makeRelative().toString());
    		} else {
    			this.sourceModelContainerText.setText(StringUtilities.EMPTY_STRING);
    		}

    		if (this.importManager.getSourceModelName() != null) {
    			this.sourceModelFilePath = this.importManager.getSourceModelLocation().getFullPath().makeRelative();
    			this.sourceModelFileText.setText(this.importManager.getSourceModelName());
    		} else {
    			this.sourceModelFileText.setText(StringUtilities.EMPTY_STRING);
    		}
            
        	IContainer tgtModelLocation = this.importManager.getViewModelLocation();
        	if( tgtModelLocation != null ) {
	        	String targetFolder = tgtModelLocation.getFullPath().makeRelative().toString();
	            if (this.textFieldTargetModelLocation != null ) {
	                this.textFieldTargetModelLocation.setText(targetFolder);
	            }
	            
	            this.sourceModelContainerText.setText(targetFolder);
        	}

            if (null == connectionProfilesCombo.getItems() || 0 == connectionProfilesCombo.getItems().length) {
                if (profileWorker.getProfiles().isEmpty()) {
                    setErrorMessage(getString("no.profile")); //$NON-NLS-1$
                    wsdlURIText.setText(EMPTY_STR);
                    buttonValidateWSDL.setEnabled(false);
                    return;
                }
                
                setErrorMessage(null);
                setMessage(getString("select.profile")); //$NON-NLS-1$
                return;
            }

            if( connectionProfilesCombo.getSelectionIndex() < 0 ) {
            	return;
            }
            
            String profileName = connectionProfilesCombo.getText();
            IConnectionProfile profile = profileWorker.getConnectionProfile(); //findMatchingProfile(profileName);
            if (null == profile) {
                // this should really never happen
                setMessage(null);
                setErrorMessage(getString("no.profile.match", new Object[] {profileName})); //$NON-NLS-1$
                buttonValidateWSDL.setEnabled(false);
                return;
            }
            if( importManager.getWSDLFileUri() != null ) {
            	wsdlURIText.setText(importManager.getWSDLFileUri());
            }
            updateWidgetEnablements();
        }
        
        synchronizing = false;
    }
    
    public void profileChanged(IConnectionProfile profile) {
    	resetCPComboItems();
    	
    	selectConnectionProfile(profile.getName());
    	
    	importManager.setConnectionProfile(profile);
    	
    	setPageStatus();
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
                    this.importManager.setViewModelLocation(container);
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
                                this.importManager.setViewModelName(uriStr.substring(uriStr.lastIndexOf('/') + 1));
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
            
            this.importManager.setViewModelLocation(targetModelLocation);
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
                                              final String fileExtension ) {
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
    
    void resetCPComboItems() {
    	if( connectionProfilesCombo != null ) {
        	ArrayList profileList = new ArrayList();
            for( IConnectionProfile prof : profileWorker.getProfiles()) {
            	profileList.add(prof);
            }
            
            WidgetUtil.setComboItems(connectionProfilesCombo, profileList, profileLabelProvider, true);
    	}
    }
    
    void selectConnectionProfile(String name) {
    	if( name == null ) {
    		return;
    	}
    	
    	int cpIndex = -1;
    	int i = 0;
    	for( String item : connectionProfilesCombo.getItems()) {
    		if( item != null && item.length() > 0 ) {
    			if( item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
    				cpIndex = i;
    				break;
    			}
    		}
    		i++;
    	}
    	if( cpIndex > -1 ) {
    		connectionProfilesCombo.select(cpIndex);
    	}
    	
    	refreshUiFromManager();
    }
	
	private boolean sourceModelExists() {
		if (this.sourceModelFilePath == null) {
			return false;
		}

		IPath modelPath = new Path(sourceModelFilePath.toOSString()).append(this.sourceModelFileText.getText());
		if (!modelPath.toString().toUpperCase().endsWith(".XMI")) { //$NON-NLS-1$
			modelPath = modelPath.addFileExtension("xmi"); //$NON-NLS-1$
		}

		ModelWorkspaceItem item = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath,IResource.FILE);
		if (item != null) {
			return true;
		}

		return false;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
	        if( this.connectionProfilesCombo.getItemCount() > 0 ) {
	        	this.connectionProfilesCombo.select(0);
	        	handleConnectionProfileSelected();
	        }
	        
	        refreshUiFromManager();
		}
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
    
	final ViewerFilter sourceModelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject()
						.isOpen();
				if (projectOpen) {
					// Show open projects
					if (element instanceof IProject) {
						doSelect = true;
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null
								&& ModelIdentifier.isRelationalSourceModel(theModel)) {
							doSelect = true;
						}
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			}

			return doSelect;
		}
	};
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.ui.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidationConfiguration;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidator;
import org.jdom.JDOMException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.core.util.FileUtils;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.FileUrl;
import org.teiid.designer.core.util.URLHelper;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.wsdl.io.WsdlHelper;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.common.dialog.FolderSelectionDialog;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;
import org.teiid.designer.ui.wizards.wsdl.WsdlFileSelectionComposite;
import org.teiid.designer.ui.wizards.wsdl.WsdlFileSelectionComposite.FileSelectionButtons;
import org.teiid.designer.ui.wizards.wsdl.WsdlFilter;
import org.teiid.designer.webservice.IWebServiceModelBuilder;
import org.teiid.designer.webservice.IWebServiceResource;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.ui.IInternalUiConstants;
import org.teiid.designer.webservice.ui.util.WebServiceUiUtil;


/**
 * The <code>WsdlSelectionPage</code> allows the user to select WSDL files from the file system which will be used to generate the
 * new model.
 * 
 * @since 8.0
 */
public final class WsdlSelectionPage extends AbstractWizardPage
    implements FileUtils.Constants, IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images,
    Listener {
    
    public static enum EditableNameField {
        EDITABLE, UNEDITABLE;
    }

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(WsdlSelectionPage.class);

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    /** Wsdl File name suffix for saving Url based wsdl locally. */
    private static final String WSDL_SUFFIX = ".wsdl"; //$NON-NLS-1$

    /** Model name. */
    private Text textName;

    /** The model builder. */
    private IWebServiceModelBuilder builder;

    /** Mappings of Temp file to source Urls. */
    private Map urlMap;

    /** Mappings of WSDL files to validation report instances. */
    private Map wsdlValidationMessages = new HashMap();

    /** Action to copy file information of selected WSDLs to the clipboard. */
    private IAction copyAction;

    /** Viewer label provider. */
    private IBaseLabelProvider labelProvider;

    /**
     * Collection of WSDLs in the workspace selection when the page is constructed.
     */
    private Object[] startupWsdls;

    /** Action to remove selected WSDLs. */
    private IAction removeWsdlAction;

    /** Action to view selected WSDL using system editor. */
    private IAction viewWsdlAction;

    /** Enum indicating whether the model name text field is editable */
    private EditableNameField modelNameTextFieldEditable;

    /** selection buttons */
    private Button buttonSelectTargetModelLocation;

    /** Project location of the model */
    private Text textFieldTargetProjectLocation;
    
    /** Project IContainer location */
    private IContainer targetModelLocation;

    /** Filter for selecting WSDL files and their parent containers. */
    private ViewerFilter wsdlFilter = new WsdlFilter();
    
    /** Key=File or IFile, Value=IWebServiceResource. */
    private Map wsdlMap;

    /** The selected WSDL files table viewer. */
    private TableViewer viewer;

    private WsdlFileSelectionComposite fileSelectionComposite;

    /**
     * Constructs a <code>WsdlSelectionPage</code> using the specified builder.
     * 
     * @param theBuilder the model builder
     * @param flag from {@link EditableNameField} indicating whether the model 
     *                  name is editable
     * @since 4.2
     */
    public WsdlSelectionPage( IWebServiceModelBuilder theBuilder,
                              EditableNameField nameFieldEditable ) {
        super(WsdlSelectionPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.builder = theBuilder;
        this.wsdlMap = new HashMap();
        this.modelNameTextFieldEditable = nameFieldEditable;
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * Adds the specified WSDL files to the model builder and the UI table viewer then updates page status.
     * 
     * @param theFiles the WSDL files being added
     * @param theWorkspaceResourceFlag the flag indicating if the resource is from the workspace (i.e., an
     *        {@link org.eclipse.core.resources.IResource}).
     * @since 4.2
     */
    private void addWsdlFiles(final Object[] theFiles,
            final boolean theWorkspaceResourceFlag) {

        boolean requiredStart = ModelerCore.startTxn(false, false,
                "Add WSDL Files", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            addWsdlFilesInternal(theFiles, theWorkspaceResourceFlag);
            succeeded = true;
        }
        finally {
            // If we start txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                }
                else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    private void addWsdlFilesInternal( final Object[] theFiles,
                               final boolean theWorkspaceResourceFlag ) {
        Map map = getWsdlMap();
        List problems = null;
        List newWsdls = new ArrayList(theFiles.length);

        for (int i = 0; i < theFiles.length; i++) {
            // make sure file hasn't been added already by checking the absolute
            // file system paths
            boolean okToAdd = true;
            String newPath = null; // path of WSDL being added
            Iterator itr = map.keySet().iterator();

            // WSDL potentially being added
            if (theWorkspaceResourceFlag) {
                newPath = ((IFile)theFiles[i]).getLocation().toOSString();
            } else if (theFiles[i] instanceof FileUrl) {
                newPath = ((FileUrl)theFiles[i]).getOriginalUrlString();
            } else {
                newPath = ((File)theFiles[i]).getAbsolutePath();
            }

            while (itr.hasNext()) {
                Object wsdl = itr.next();
                String path = null; // path of already added WSDL

                // already added WSDL
                if (wsdl instanceof IFile) {
                    path = ((IFile)wsdl).getLocation().toOSString();
                } else if (wsdl instanceof FileUrl) {
                    path = ((FileUrl)wsdl).getOriginalUrlString();
                } else if (wsdl instanceof File) {
                    path = ((File)wsdl).getAbsolutePath();
                } else {
                    // unexpected
                    CoreArgCheck.isTrue(false, "Unexpected WSDL object type of " + wsdl.getClass()); //$NON-NLS-1$
                }

                // don't add if it has already been added
                if (path!=null && path.equals(newPath)) {
                    okToAdd = false;

                    if (problems == null) {
                        problems = new ArrayList();
                    }

                    problems.add(theFiles[i]);

                    break;
                }
            }

            if (okToAdd) {
                IWebServiceResource resource = null;

                // possibility the addResource() methods throw an exception.
                // catch and keep on adding.
                try {
                    if (theWorkspaceResourceFlag) {
                        resource = builder.addResource((IFile)theFiles[i]);
                    } else {
                        resource = builder.addResource((File)theFiles[i]);
                    }

                    map.put(theFiles[i], resource);
                    newWsdls.add(theFiles[i]);
                    validateWSDL(resource);
                }
                catch (Exception theException) {
                    UTIL.log(theException);

                    if (problems == null) {
                        problems = new ArrayList();
                    }

                    problems.add(theFiles[i]);
                }
            }
        }

        // if wsdls added refresh table and update page status
        if (!newWsdls.isEmpty()) {
            getViewer().refresh();
            getViewer().setSelection(new StructuredSelection(newWsdls), true);
            setPageStatus();
        }

        // show dialog showing the files that weren't added because they were
        // added previously or had problems
        if (problems != null) {
            ListMessageDialog.openInformation(getShell(), getString("dialog.problems.title"), //$NON-NLS-1$
                                              null,
                                              getString("dialog.problems.msg"), //$NON-NLS-1$,
                                              problems,
                                              getLabelProvider());
        }
    }

    /**
     * Init the Xml response document model based on the webservice model.
     */
    private void setBuilderXmlResponseDocModel() {
        IPath modelPath = this.builder.getModelPath();
        String name = modelPath.removeFileExtension().lastSegment() + getString("xmlModelSuffix"); //$NON-NLS-1$
        this.builder.setXmlModel(modelPath.removeLastSegments(1).append(name).addFileExtension(modelPath.getFileExtension()));
    }

    /**
     * Validate the selected WSDL file and save any errors to message map.
     * 
     * @param resource
     */
    private void validateWSDL( IWebServiceResource resource ) {

        /*
         * reset the state of whether or not all of the WSDLs have errors. If
         * all WSDLs have errors then we do not allow the 'next' or finish
         * operations on this page.
         */
        WSDLValidator validator = new WSDLValidator();

        IValidationReport report = null;

        try {
            report = validator.validate(resource.getFile().toURI().toString(),
                                        new FileInputStream(resource.getFile()),
                                        new WSDLValidationConfiguration());
        } catch (FileNotFoundException err) {
            // This should really never happen.
        }

        if (report != null && report.getValidationMessages().length > 0) {
            wsdlValidationMessages.put(resource, report);
        }
    }

    /**
     * Validate the location name. if the location is found, return the location container. 
     * 
     * If not valid, return a null value.
     */
    private IContainer validateTargetFolder() {
        final String folderName = textFieldTargetProjectLocation.getText();
        if (CoreStringUtil.isEmpty(folderName)) {
            WizardUtil.setPageComplete(this, getString("page.selectProject.msg"), IMessageProvider.ERROR); //$NON-NLS-1$
        } else {
            final IResource resrc = ModelerCore.getWorkspace().getRoot().findMember(folderName);

            if (resrc == null || !(resrc instanceof IContainer) || resrc.getProject() == null) {
                WizardUtil.setPageComplete(this, getString("invalidFolderMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
            } else if (!resrc.getProject().isOpen()) {
                WizardUtil.setPageComplete(this, getString("closedProjectMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
            } else {
                try{
                    if (resrc.getProject().getNature(ModelerCore.NATURE_ID) != null) {
                        final IContainer folder = (IContainer)resrc;
                        WizardUtil.setPageComplete(this);
                        return folder;
                    }
                    else {
                        WizardUtil.setPageComplete(this, getString("notModelProjectMessage"), IMessageProvider.ERROR); //$NON-NLS-1$
                    }
                } catch (final CoreException err) {
                    UTIL.log(err);
                    WizardUtil.setPageComplete(this, err.getLocalizedMessage(), IMessageProvider.ERROR);
                }
            }
        }
        return null;
    }

    /**
     * Validate the model name. if the model name is appropriate,
     * return the model name with extension.
     *
     * If not valid, return a null value.
     */
    private String validateModelName() {
        String name = textName.getText();
        
        IStatus status = ModelNameUtil.validate(name, ModelerCore.MODEL_FILE_EXTENSION, targetModelLocation,
        		ModelNameUtil.IGNORE_CASE | ModelNameUtil.NO_DUPLICATE_MODEL_NAMES | ModelNameUtil.NO_EXISTING_MODEL_AT_LOCATION);
        if( status.getSeverity() == IStatus.ERROR ) {
        	WizardUtil.setPageComplete(this, status.getMessage(), IMessageProvider.ERROR);
        	return null;
        }

        WizardUtil.setPageComplete(this);
        return name + ModelerCore.MODEL_FILE_EXTENSION;
    }

    /**
     * @return
     */
    public Map getWsdlValidationMessages() {
        return wsdlValidationMessages;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    public void createControl( Composite theParent ) {
        //
        // create main container
        //

        final int COLUMNS = 1;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, WSDL_SELECTION_PAGE);

        createFileAndLocationPanel(pnlMain);
        
        fileSelectionComposite = new WsdlFileSelectionComposite(pnlMain, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(fileSelectionComposite);
        
        WsdlFileSelectionComposite.IFileSelectionCallback fileSelectionCallback = new WsdlFileSelectionComposite.IFileSelectionCallback() {

            @Override
            public void execute(File wsdlFile) {
                addWsdlFiles(new Object[] { wsdlFile }, false);

                // jh Defect 22412
                validatePage();
            }

            @Override
            public Display getDisplay() {
                return getShell().getDisplay();
            }
        };
        
        WsdlFileSelectionComposite.IURLSelectionCallback urlSelectionCallback = new WsdlFileSelectionComposite.IURLSelectionCallback() {
            @Override
            public void execute(URL url, SecurityType securityType,
                    String userName, String password) {
                addURLWsdlFile(url, securityType, userName, password);
            }

            @Override
            public Display getDisplay() {
                return getShell().getDisplay();
            }

        };
        
        fileSelectionComposite.setCallbacks(fileSelectionCallback, fileSelectionCallback, urlSelectionCallback);
        
        createTablePanel(pnlMain);

    }

    /**
     * @see org.teiid.designer.ui.common.wizard.AbstractWizardPage#canFlipToNextPage()
     * @since 4.2
     */
    @Override
    public boolean canFlipToNextPage() {
        // if no WSDLs identified don't let the next page show
        return (this.viewer.getTable().getItemCount() > 0) && super.canFlipToNextPage();
    }

    /**
     * Constructs the button panel controls.
     * 
     * @param theParent the parent container
     * @since 4.2
     */
    private void createFileAndLocationPanel( Composite theParent ) {
        final int COLUMNS = 2;

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        createTargetSelectionComposite(theParent);

        // Add widgets to page
        WidgetFactory.createLabel(pnl, getString("modelName")); //$NON-NLS-1$,
        this.textName = WidgetFactory.createTextField(pnl, GridData.FILL_HORIZONTAL, 1);
        // If the model name should not be editable, set editable to false and
        // set the value to to the model name.
        if (EditableNameField.UNEDITABLE.equals(modelNameTextFieldEditable)) {
            this.textName.setEditable(false);
            String modelName = builder.getModelPath().removeFileExtension().lastSegment();
            this.textName.setText(modelName);
        }
        this.textName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( final ModifyEvent event ) {
                nameModified();
            }
        });
    }

    /**
     * Constructs the target project selection component panel.
     * 
     * @param theParent the parent container
     */
    private void createTargetSelectionComposite( Composite theParent ) {

        final int COLUMNS = 1;

        Composite pnl = WidgetFactory.createPanel(theParent, SWT.FILL, GridData.FILL_HORIZONTAL);
        pnl.setLayout(new GridLayout(COLUMNS, false));

        // options group
        Group optionsGroup = WidgetFactory.createGroup(pnl, getString("targetLocationGroup.text"), SWT.NONE); //$NON-NLS-1$

        GridData gdRadioGroup = new GridData(GridData.FILL_HORIZONTAL);
        optionsGroup.setLayoutData(gdRadioGroup);

        optionsGroup.setLayout(new GridLayout(2, false));

        // --------------------------------------------
        // Composite for Workspace Folder Location Selection
        // --------------------------------------------
        // Select Target Location Label

        final String location = (this.builder.getParentResource() == null ? null : this.builder.getParentResource().getFullPath().makeRelative().toOSString());

        // FileSystem textfield
        textFieldTargetProjectLocation = WidgetFactory.createTextField(optionsGroup, GridData.FILL_HORIZONTAL);
        String text = getString("targetModelLocationTextField.tooltip"); //$NON-NLS-1$
        textFieldTargetProjectLocation.setToolTipText(text);

        if (location != null) {
            textFieldTargetProjectLocation.setText(location);
        }
        this.textFieldTargetProjectLocation.addModifyListener(new ModifyListener() {
            @Override
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
     * Constructs the table's toolbar actions.
     * 
     * @param theToolBarMgr the toolbar where the actions are installed
     * @since 4.2
     */
    private void createTableActions( IToolBarManager theToolBarMgr ) {
        //
        // copy action
        //

        this.copyAction = new Action(getString("action.copy"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleCopyWsdlInfo();
            }
        };

        this.copyAction.setImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        this.copyAction.setDisabledImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        this.copyAction.setHoverImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        this.copyAction.setToolTipText(getString("action.copy.tip")); //$NON-NLS-1$
        this.copyAction.setEnabled(false);

        theToolBarMgr.add(this.copyAction);

        //
        // remove action
        //

        this.removeWsdlAction = new Action(getString("action.removeWsdlFile"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleRemoveWsdlFile();
            }
        };

        this.removeWsdlAction.setImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.removeWsdlAction.setToolTipText(getString("action.removeWsdlFile.tip")); //$NON-NLS-1$
        this.removeWsdlAction.setEnabled(false);

        theToolBarMgr.add(this.removeWsdlAction);

        //
        // view action
        //

        this.viewWsdlAction = new Action(getString("action.viewWsdlFile"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleViewWsdlFile();
            }
        };

        this.viewWsdlAction.setImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
        this.viewWsdlAction.setToolTipText(getString("action.viewWsdlFile.tip")); //$NON-NLS-1$
        this.viewWsdlAction.setEnabled(false);

        theToolBarMgr.add(this.viewWsdlAction);

        // update toolbar to pick up actions
        theToolBarMgr.update(true);
    }

    /**
     * Creates the context menu of the table.
     * 
     * @since 4.2
     */
    protected void createTableContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.copyAction);
        mgr.add(this.removeWsdlAction);
        mgr.add(this.viewWsdlAction);

        Control table = this.viewer.getControl();
        table.setMenu(mgr.createContextMenu(table));
    }

    /**
     * Constructs the table panel controls.
     * 
     * @param theParent the parent container
     * @since 4.2
     */
    private void createTablePanel( Composite theParent ) {
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, getString("label.table"))); //$NON-NLS-1$

        Composite pnl = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH);
        viewForm.setContent(pnl);

        // table
        this.viewer = WidgetFactory.createTableViewer(pnl, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        this.viewer.setContentProvider(new TableContentProvider());
        this.labelProvider = new LabelProvider() {
            @Override
            public String getText( Object theElement ) {
                return WebServiceUiUtil.getText(theElement);
            }
        };
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTableSelectionChanged();
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleViewWsdlFile();
            }
        });
        this.viewer.addFilter(this.wsdlFilter);

        // create toolbar actions
        createTableActions(WidgetFactory.createViewFormToolBar(viewForm));

        // context menu for table
        createTableContextMenu();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        saveState();
    }

    /**
     * @since 5.0.1
     */
    void nameModified() {
        validatePage();
    }

    /**
     * Override to replace the NewModelWizard settings with the section devoted to the Web Service Model Wizard.
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 4.2
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
     * Gets the viewer label provider.
     * 
     * @return the label provider
     * @since 4.2
     */
    private IBaseLabelProvider getLabelProvider() {
        return this.labelProvider;
    }

    /**
     * Convenience method to give inner classes access to the table viewer.
     * 
     * @return the viewer
     * @since 4.2
     */
    private TableViewer getViewer() {
        return this.viewer;
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     * @since 4.2
     */
    static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Utility to get localized text from properties file.
     * 
     * @param theKey the key whose localized value is being requested
     * @return the localized text
     * @since 4.2
     */
    private static String getString( String theKey,
                                     Object value1 ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString(), value1);
    }

    /**
     * Convenience method to give inner classes access to the WSDL map.
     * 
     * @return the map
     * @since 4.2
     */
    private Map getWsdlMap() {
        return this.wsdlMap;
    }

    /**
     * Returns the map containing local file and original url mappings.
     * 
     * @return the map
     * @since 5.0.1
     */
    protected Map getUrlMap() {
        return this.builder.getUrlMap();
    }

    /**
     * Gets a collection of {@link IFile}s and {@link File}s related to the WSDL file(s) being loaded.
     * 
     * @return the WSDLs
     * @since 4.2
     */
    private Object[] getWsdlResources() {
        return this.wsdlMap.keySet().toArray();
    }

    /**
     * Handler for entering and adding a WSDL Url to the list.
     * @param securityType 
     * 
     * @since 5.1
     */
    private void addURLWsdlFile(final URL url, final SecurityType securityType,
            final String userName, final String password) {
        ArrayList list = new ArrayList();
        urlMap = getUrlMap();

        try {
            String filePath = formatPath(url);
            File wsdlFile;

            if (SecurityType.None.equals(securityType)) {
                wsdlFile = URLHelper.createFileFromUrl(url,
                        CoreStringUtil.createFileName(filePath), WSDL_SUFFIX);
            }
            else {
                wsdlFile = URLHelper.createFileFromUrl(url,
                        CoreStringUtil.createFileName(filePath), WSDL_SUFFIX,
                        userName, password);
            }

            urlMap.put(wsdlFile.getName(), url.toString());
            WsdlHelper.convertImportsToAbsolutePaths(wsdlFile,
                    url.toExternalForm(), list, urlMap, true);
        }
        catch (MalformedURLException theException) {
            UTIL.log(theException);
        }
        catch (IOException theException) {
            UTIL.log(theException);
        }
        catch (JDOMException theException) {
            UTIL.log(theException);
        }

        Object[] wsdlFiles = list.toArray();
        addWsdlFiles(wsdlFiles, false);

        // jh Defect 22412
        validatePage();
    }

    /**
     * If the path begins with a "/", we need to strip off since this will be changed to an underscore and create an invalid model
     * name. Also, we need to remove any periods.
     * 
     * @param newUrl
     * @return filePath - reformatted string used for generating the new file name
     */
    public static String formatPath( URL newUrl ) {
        String filePath = newUrl.getPath();
        /*
         * If the path begins with a "/", we need to strip off since this will
         * be changed to an underscore and create an invalid model name.
         */
        while (filePath.startsWith("/")) { //$NON-NLS-1$
            filePath = filePath.substring(1);
        }
        int dotLocation = filePath.indexOf("."); //$NON-NLS-1$
        if (dotLocation > -1) {
            filePath = filePath.substring(0, dotLocation);
        }
        return filePath;
    }

    /**
     * Handler for when WSDL file(s) file information is copied to clipboard.
     * 
     * @since 4.2
     */
    private void handleCopyWsdlInfo() {
        WebServiceUiUtil.copyToClipboard(this.viewer.getSelection());
    }

    /**
     * Handler for when a WSDL file is removed from the list.
     * 
     * @since 4.2
     */
    private void handleRemoveWsdlFile() {
        Object[] selectedWsdlFiles = ((IStructuredSelection)this.viewer.getSelection()).toArray();

        // remove from builder and wsdlValidationMessages map
        for (int i = 0; i < selectedWsdlFiles.length; i++) {
            IWebServiceResource resource = (IWebServiceResource)this.wsdlMap.get(selectedWsdlFiles[i]);
            this.builder.remove(resource);
            wsdlValidationMessages.remove(resource);

            // remove from map
            Iterator itr = this.wsdlMap.entrySet().iterator();

            while (itr.hasNext()) {
                Map.Entry entry = (Map.Entry)itr.next();

                if (entry.getValue() == resource) {
                    itr.remove();
                    break;
                }
            }
        }

        // remove files from viewer
        this.viewer.remove(selectedWsdlFiles);

        // enable buttons
        handleTableSelectionChanged();

        // update page message
        setPageStatus();
    }

    /**
     * Handler for when table selection changes.
     * 
     * @since 4.2
     */
    private void handleTableSelectionChanged() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enable buttons

        this.copyAction.setEnabled(!selection.isEmpty());
        this.removeWsdlAction.setEnabled(this.copyAction.isEnabled());
        this.viewWsdlAction.setEnabled(selection.size() == 1);
    }

    /**
     * Handler for when a request to view a WSDL file. Code copied from action that Eclipse uses to view a file using system
     * editor.
     * 
     * @since 4.2
     */
    private void handleViewWsdlFile() {
        WebServiceUiUtil.viewFile(getShell(), this.viewer.getSelection());
    }

    /**
     * Restores dialog size and position of the last time wizard ran.
     * 
     * @since 4.2
     */
    private void restoreState() {
        IDialogSettings settings = getDialogSettings();

        if (settings != null) {
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
     * 
     * @since 4.2
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

    private void setPageStatus() {
        String msg = null;
        int wsdlCount = this.wsdlMap.size();

        if (wsdlCount > 0) {
            msg = UTIL.getString(PREFIX + "page.wsdlsIncluded.msg", new Object[] {new Integer(wsdlCount)}); //$NON-NLS-1$
        } else {
            msg = getString("page.noWsdls.msg"); //$NON-NLS-1$
        }

        // Validate the target relational model name and location
        boolean valid = validatePage();
        if (!valid) {
            return;
        }

        setMessage(msg, IStatus.OK);

        // update enabled state of next, finish
        getContainer().updateButtons();
    }

    /**
     * Sets the initial workspace selection. Must be called during construction. Automatically adds any WSDL files contained in
     * the specified selection.
     * 
     * @param theSelection the current workspace selection
     * @since 4.2
     */
    public void setInitialSelection( ISelection theSelection ) {
        if (!theSelection.isEmpty() && (theSelection instanceof IStructuredSelection)) {
            Object[] selectedObjects = ((IStructuredSelection)theSelection).toArray();
            List wsdls = new ArrayList();

            for (int i = 0; i < selectedObjects.length; i++) {
                if ((selectedObjects[i] instanceof IFile) && WebServicePlugin.isWsdlFile((IFile)selectedObjects[i])) {
                    // Convert the IFile object to a File object
                    File fNew = ((IFile)selectedObjects[i]).getLocation().toFile();
                    wsdls.add(fNew);
                }
            }

            if (!wsdls.isEmpty()) {
                this.startupWsdls = wsdls.toArray();
            }
        }
    }

    public void setWsdlValidationMessages( Map wsdlValidationMessages ) {
        this.wsdlValidationMessages = wsdlValidationMessages;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // initialize state
        if (theShowFlag) {

            // only restore state one time. after first time input will not be
            // null.
            if (this.viewer.getInput() == null) {
                restoreState();
            }

            setPageComplete(true); // always complete since no errors possible

            // add workspace WSDL selections if necessary and first time showing
            // page
            if (this.startupWsdls != null) {
                addWsdlFiles(this.startupWsdls, false);
                this.startupWsdls = null; // reset back to null so not added
                // again
            }

            this.viewer.setInput(this); // could be any object since it is not
            // used by the viewer
            setPageStatus();

            
            // set focus so that the help context will be correct
            if (fileSelectionComposite.isVisible(FileSelectionButtons.WORKSPACE)) {
                fileSelectionComposite.setFocus(FileSelectionButtons.WORKSPACE);
            }
        }

        super.setVisible(theShowFlag);
    }

    /**
     * @since 5.0.1
     */
    private boolean validatePage() {
        targetModelLocation = validateTargetFolder();
        if (targetModelLocation == null) {
            return false;
        }

        String modelName = validateModelName();
        if (modelName == null) {
            return false;
        }

        IPath fullModelPath = targetModelLocation.getFullPath().append(modelName);

        // see if model exists or would be a new model
        String temp = null;
        IResource resource = ModelerCore.getWorkspace().getRoot().findMember(fullModelPath);

        if (resource != null) {
            IPath path = resource.getLocation();
            temp = path.toOSString();
        }
        String pathString = getModelPath(temp, modelName);
        boolean exists = new File(pathString).exists();

        if (exists) {
            WizardUtil.setPageComplete(this, getString("page.existingWebServiceModel.msg", fullModelPath), IMessageProvider.ERROR); //$NON-NLS-1$,
        } else {
            WizardUtil.setPageComplete(this);
        }

        // jh Defect 22412: page complete must also consider whether any wsdl
        // has been specified.
        if (isPageComplete()) {
            if (this.viewer.getTable().getItemCount() == 0) {
                WizardUtil.setPageComplete(this, getString("page.noWsdls.msg"), IMessageProvider.ERROR); //$NON-NLS-1$,
            }
        }

        builder.setParentResource(targetModelLocation);
        builder.setModelPath(fullModelPath);
        getContainer().updateButtons();

        if (isPageComplete()) {
            setBuilderXmlResponseDocModel();
        }

        return true;
    }

    private String getModelPath( final String modelLocation,
                                 final String modelName ) {
        return new StringBuffer().append(modelLocation).append(File.separator).append(modelName).append(FILE_EXTENSION_SEPARATOR_CHAR).append(ModelUtil.EXTENSION_XMI).toString();
    }

    /**
     * The content provider for the WSDL table.
     * 
     * @since 4.2
     */
    class TableContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        @Override
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        @Override
        public Object[] getElements( Object theInputElement ) {
            return getWsdlResources();
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        @Override
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }

    }

    @Override
    public void handleEvent( Event event ) {
        // boolean validate = false;

        if (event.widget == this.buttonSelectTargetModelLocation) {
            handleBrowseWorkspaceForTargetModelLocation();
        }
    }

    /**
     * Handler for Workspace Target Model Location Browse button.
     */
    private void handleBrowseWorkspaceForTargetModelLocation() {
        // create the dialog for target location
        FolderSelectionDialog dlg = new FolderSelectionDialog(Display.getCurrent().getActiveShell(),
                                                              new WorkbenchLabelProvider(), new WorkbenchContentProvider());

        dlg.setInitialSelection(this.builder.getModelPath());
        dlg.addFilter(new ModelingResourceFilter(this.targetLocationFilter));
        dlg.setValidator(new ModelProjectSelectionStatusValidator());
        dlg.setAllowMultiple(false);
        dlg.setInput(ModelerCore.getWorkspace().getRoot());

        // display the dialog
        Object[] objs = new Object[1];
        if (dlg.open() == Window.OK) {
            objs = dlg.getResult();
        }

        IContainer location = (objs.length == 0 ? null : (IContainer)objs[0]);

        // Update the controls with the target location selection
        targetModelLocation = location;
        if (location != null) {
            this.textFieldTargetProjectLocation.setText(location.getFullPath().makeRelative().toString());
        }

        validatePage();
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

}

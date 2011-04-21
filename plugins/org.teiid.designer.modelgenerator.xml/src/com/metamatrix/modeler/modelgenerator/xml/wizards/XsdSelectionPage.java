/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.modelgenerator.xml.IUiConstants;
import com.metamatrix.modeler.modelgenerator.xml.Util;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.schema.tools.processing.internal.SchemaProcessorImpl;
import com.metamatrix.modeler.ui.viewsupport.ModelingResourceFilter;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.ListMessageDialog;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public abstract class XsdSelectionPage extends AbstractWizardPage {
    /** Used as a prefix to properties file keys. */
    String prefix;

    PluginUtil util;

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    /** The import wizard. */
    protected XsdAsRelationalImportWizard importWizard;

    /** The wizard state manager */

    StateManager manager;

    /** Action to copy file information of selected XSDs to the clipboard. */
    private IAction copyAction;

    /** Viewer label provider. */
    private IBaseLabelProvider labelProvider;

    /** Collection of XSDs in the workspace selection when the page is constructed. */
    private Object[] startupXsds;

    /** Action to remove selected XSDs. */
    private IAction removeXsdAction;

    /** Filter for selecting XSD files and their parent containers. */
    private ViewerFilter xsdFilter = new ViewerFilter() {
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
                        util.log(theException);
                    }
                }
            } else if (theElement instanceof IFile) {
                result = Util.isXsdFile((IFile)theElement);
            } else if (theElement instanceof File) {
                return (((File)theElement).isDirectory() || Util.isXsdFile(((File)theElement)));
            }

            return result;
        }
    };

    /** Validator that makes sure the selection containes all XSD files. */
    private ISelectionStatusValidator xsdValidator = new ISelectionStatusValidator() {
        public IStatus validate( Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length > 0)) {
                for (int i = 0; i < theSelection.length; i++) {
                    if ((!(theSelection[i] instanceof IFile)) || !Util.isXsdFile((IFile)theSelection[i])) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }

            if (valid) {
                result = new StatusInfo(IUiConstants.PLUGIN_ID);
            } else {
                result = new StatusInfo(IUiConstants.PLUGIN_ID, IStatus.ERROR, getString("msg.selectionIsNotXsd")); //$NON-NLS-1$
            }

            return result;
        }
    };

    /** Control to add file system XSD file to list. */

    private Button btnFileSystemAdd;

    /** Control to add workspace XSD file to list. */
    private Button btnWorkspaceAdd;

    /** The selected XSD files table viewer. */
    private TableViewer viewer;

    public XsdSelectionPage( XsdAsRelationalImportWizard importWizard,
                             String prefix,
                             PluginUtil util ) {
        super(XsdSelectionPage.class.getSimpleName(), util.getString(prefix + "title")); //$NON-NLS-1$
        this.importWizard = importWizard;
        this.manager = importWizard.getStateManager();
        this.prefix = prefix;
        this.util = util;
        setImageDescriptor(XmlImporterUiPlugin.getDefault().getImageDescriptor(IUiConstants.Images.NEW_MODEL_BANNER));
    }

    /**
     * Adds the specified XSD files to the model builder and the UI table viewer then updates page status.
     * 
     * @param theFiles the XSD files being added
     * @param theWorkspaceResourceFlag the flag indicating if the resource is from the workspace (i.e., an
     *        {@link org.eclipse.core.resources.IResource}).
     * @since 4.2
     */
    public void addXsdFiles( final Object[] theFiles,
                             final boolean theWorkspaceResourceFlag ) {
        UiBusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
            public void run() {
                List problems = null;
                List newXsds = new ArrayList(theFiles.length);

                for (int i = 0; i < theFiles.length; i++) {
                    // make sure file hasn't been added already by checking the absolute file system paths
                    boolean okToAdd = true;
                    String newPath = null; // path of XSD being added

                    // XSD potentially being added
                    if (theWorkspaceResourceFlag) {
                        newPath = ((IFile)theFiles[i]).getLocation().toOSString();
                    } else {
                        newPath = ((File)theFiles[i]).getAbsolutePath();
                    }

                    Iterator itr = manager.getSchemaKeySet().iterator();
                    while (itr.hasNext()) {
                        Object xsd = itr.next();
                        String path = null; // path of already added XSD

                        // already added XSD
                        if (xsd instanceof IFile) {
                            path = ((IFile)xsd).getLocation().toOSString();
                        } else if (xsd instanceof File) {
                            path = ((File)xsd).getAbsolutePath();
                        } else {
                            // unexpected
                            CoreArgCheck.isTrue(false, "Unexpected XSD object type of " + xsd.getClass()); //$NON-NLS-1$
                        }

                        // don't add if it has already been added
                        if (path.equals(newPath)) {
                            okToAdd = false;

                            if (problems == null) {
                                problems = new ArrayList();
                            }

                            problems.add(theFiles[i]);

                            break;
                        }
                    }

                    if (okToAdd) {
                        if (theWorkspaceResourceFlag) {
                            addResource((IFile)theFiles[i]);
                        } else {
                            addResource((File)theFiles[i]);
                        }
                        newXsds.add(theFiles[i]);
                    }
                }

                // if xsds added refresh table and update page status
                if (!newXsds.isEmpty()) {
                    getViewer().refresh();
                    getViewer().setSelection(new StructuredSelection(newXsds), true);
                    setPageStatus();
                }

                // show dialog showing the files that weren't added because they were added previously or had problems
                if (problems != null) {
                    ListMessageDialog.openInformation(getShell(), getString("dialog.problems.title"), //$NON-NLS-1$
                                                      null,
                                                      getString("dialog.problems.msg"), //$NON-NLS-1$,
                                                      problems,
                                                      getLabelProvider());
                }
            }
        });
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        //
        // create main container
        //

        final int COLUMNS = 2;
        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, IUiConstants.HelpContexts.XSD_SELECTION_PAGE);

        //
        // column 1: table panel
        //

        createTablePanel(pnlMain);

        //
        // column 2: button panel
        //

        createButtonPanel(pnlMain);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizardPage#canFlipToNextPage()
     * @since 4.2
     */
    @Override
    public boolean canFlipToNextPage() {
        // if no XSDs identified don't let the next page show
        return (this.viewer.getTable().getItemCount() > 0) && super.canFlipToNextPage();
    }

    /**
     * Constructs the button panel controls.
     * 
     * @param theParent the parent container
     * @since 4.2
     */
    private void createButtonPanel( Composite theParent ) {
        Composite pnl = WidgetFactory.createPanel(theParent, GridData.VERTICAL_ALIGN_CENTER);

        // workspace add button
        this.btnWorkspaceAdd = WidgetFactory.createButton(pnl, getString("button.addXsdFile.workspace"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnWorkspaceAdd.setToolTipText(getString("button.addXsdFile.workspace.tip")); //$NON-NLS-1$
        this.btnWorkspaceAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAddWorkspaceXsdFile();
            }
        });

        // file system add button
        this.btnFileSystemAdd = WidgetFactory.createButton(pnl,
                                                           getString("button.addXsdFile.filesystem"), GridData.FILL_HORIZONTAL); //$NON-NLS-1$
        this.btnFileSystemAdd.setToolTipText(getString("button.addXsdFile.filesystem.tip")); //$NON-NLS-1$
        this.btnFileSystemAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleAddFileSystemXsdFile();
            }
        });
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
                handleCopyXsdInfo();
            }
        };

        this.copyAction.setImageDescriptor(Util.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        this.copyAction.setDisabledImageDescriptor(Util.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        this.copyAction.setHoverImageDescriptor(Util.getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        this.copyAction.setToolTipText(getString("action.copy.tip")); //$NON-NLS-1$
        this.copyAction.setEnabled(false);

        theToolBarMgr.add(this.copyAction);

        //
        // remove action
        //

        this.removeXsdAction = new Action(getString("action.removeXsdFile"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleRemoveXsdFile();
            }
        };

        this.removeXsdAction.setImageDescriptor(Util.getSharedImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.removeXsdAction.setToolTipText(getString("action.removeXsdFile.tip")); //$NON-NLS-1$
        this.removeXsdAction.setEnabled(false);

        theToolBarMgr.add(this.removeXsdAction);

        // update toolbar to pick up actions
        theToolBarMgr.update(true);
    }

    /**
     * Creates the context menu of the table.
     * 
     * @since 4.2
     */
    private void createTableContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.copyAction);
        mgr.add(this.removeXsdAction);
        // mgr.add(this.viewXsdAction);

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

        CLabel label = new CLabel(viewForm, SWT.NONE);
        label.setText(getString("label.table")); //$NON-NLS-1$
        final GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 1;
        label.setLayoutData(gridData);
        viewForm.setTopLeft(label);

        Composite pnl = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH);
        viewForm.setContent(pnl);

        // table
        this.viewer = WidgetFactory.createTableViewer(pnl, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        this.viewer.setContentProvider(new TableContentProvider());
        this.labelProvider = new LabelProvider() {
            @Override
            public String getText( Object theElement ) {
                return XsdSelectionPage.getText(theElement);
            }
        };
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTableSelectionChanged();
            }
        });
        // this.viewer.addDoubleClickListener(new IDoubleClickListener() {
        // public void doubleClick(DoubleClickEvent theEvent) {
        // handleViewXsdFile();
        // }
        // });
        this.viewer.addFilter(this.xsdFilter);

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
            IDialogSettings temp = settings.getSection(IUiConstants.DIALOG_SETTINGS_SECTION);

            if (temp == null) {
                settings = settings.addNewSection(IUiConstants.DIALOG_SETTINGS_SECTION);
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
    IBaseLabelProvider getLabelProvider() {
        return this.labelProvider;
    }

    /**
     * Convenience method to give inner classes access to the table viewer.
     * 
     * @return the viewer
     * @since 4.2
     */
    TableViewer getViewer() {
        return this.viewer;
    }

    String getString( String theKey ) {
        return util.getString(prefix + theKey);
    }

    private String getString( String theKey,
                              Object[] params ) {
        return util.getString(prefix + theKey, params);
    }

    /**
     * Handler for browsing to add one or more file system XSD files to the list.
     * 
     * @since 4.2
     */
    void handleAddFileSystemXsdFile() {
        FileDialog dialog = new FileDialog(getShell(), SWT.MULTI);
        dialog.setText(getString("dialog.addXsd.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(Util.FILE_DIALOG_XSD_EXTENSIONS);

        if (dialog.open() != null) {
            boolean validFiles = true;
            String[] filenames = dialog.getFileNames();

            if ((filenames != null) && (filenames.length > 0)) {
                String directory = dialog.getFilterPath();
                Object[] xsdFiles = new Object[filenames.length];

                for (int i = 0; i < filenames.length; i++) {
                    String path = new StringBuffer().append(directory).append(File.separatorChar).append(filenames[i]).toString();
                    xsdFiles[i] = new File(path);

                    // make sure the right type of file was selected. since the user can enter *.* in file name
                    // field of the dialog they can view all files regardless of the filter extensions. this allows
                    // them to actually select invalid file types.

                    if (!Util.isXsdFile((File)xsdFiles[i])) {
                        validFiles = false;
                        break;
                    }
                }

                if (validFiles) {
                    addXsdFiles(xsdFiles, false);
                } else {
                    // open file chooser again based on if user OK'd dialog
                    if (MessageDialog.openQuestion(getShell(), getString("dialog.wrongFileType.title"), //$NON-NLS-1$
                                                   getString("dialog.wrongFileType.msg"))) { //$NON-NLS-1$
                        handleAddFileSystemXsdFile();
                    }
                }
            }
        }
    }

    /**
     * Handler for browsing to add one or more workspace XSD files to the list.
     * 
     * @since 4.2
     */
    void handleAddWorkspaceXsdFile() {
        // setup viewer filter to only allow resources that the Model Explorer shows and then add xsd filter
        Object[] xsdFiles = WidgetUtil.showWorkspaceObjectSelectionDialog(getString("dialog.addXsd.title"), //$NON-NLS-1$
                                                                          getString("dialog.addXsd.msg"), //$NON-NLS-1$
                                                                          true,
                                                                          null,
                                                                          new ModelingResourceFilter(this.xsdFilter),
                                                                          this.xsdValidator,
                                                       				   new ModelExplorerLabelProvider());
        if ((xsdFiles != null) && (xsdFiles.length > 0)) {
            addXsdFiles(xsdFiles, true);
        }
    }

    /**
     * Handler for when XSD file(s) file information is copied to clipboard.
     * 
     * @since 4.2
     */
    void handleCopyXsdInfo() {
        copyToClipboard(this.viewer.getSelection());
    }

    /**
     * Handler for when a XSD file is removed from the list.
     * 
     * @since 4.2
     */
    void handleRemoveXsdFile() {
        Object[] selectedXsdFiles = ((IStructuredSelection)this.viewer.getSelection()).toArray();

        // remove from builder
        for (int i = 0; i < selectedXsdFiles.length; i++) {
            Object resource = selectedXsdFiles[i];
            if (resource instanceof File) {
                removeResource((File)resource);
            } else {
                removeResource((IFile)resource);
            }
        }

        // remove files from viewer
        this.viewer.remove(selectedXsdFiles);

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
    void handleTableSelectionChanged() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enable buttons

        this.copyAction.setEnabled(!selection.isEmpty());
        this.removeXsdAction.setEnabled(this.copyAction.isEnabled());
        // this.viewXsdAction.setEnabled(selection.size() == 1);
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

        if (settings != null) {
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
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    void setPageStatus() {
        // no errors are possible on this page so the page is always complete.
        // just update the message based on if XSD files selected

        String msg = null;
        int xsdCount = manager.getSchemaCount();

        if (xsdCount > 0) {
            msg = util.getString(prefix + "page.xsdsIncluded.msg", new Object[] {new Integer(xsdCount)}); //$NON-NLS-1$
        } else {
            msg = getString("page.noXsds.msg"); //$NON-NLS-1$
        }

        setMessage(msg, IStatus.OK);

        // update enabled state of next, finish
        getContainer().updateButtons();
    }

    /**
     * Sets the initial workspace selection. Must be called during construction. Automatically adds any XSD files contained in the
     * specified selection.
     * 
     * @param theSelection the current workspace selection
     * @since 4.2
     */
    void setInitialSelection( ISelection theSelection ) {
        if (!theSelection.isEmpty() && (theSelection instanceof IStructuredSelection)) {
            Object[] selectedObjects = ((IStructuredSelection)theSelection).toArray();
            List xsds = new ArrayList();

            for (int i = 0; i < selectedObjects.length; i++) {
                if ((selectedObjects[i] instanceof IFile) && Util.isXsdFile((IFile)selectedObjects[i])) {
                    xsds.add(selectedObjects[i]);
                }
            }

            if (!xsds.isEmpty()) {
                this.startupXsds = xsds.toArray();
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // initialize state
        if (theShowFlag) {

            // only restore state one time. after first time input will not be null.
            if (this.viewer.getInput() == null) {
                restoreState();
            }

            setPageComplete(true); // always complete since no errors possible

            // add workspace XSD selections if necessary and first time showing page
            if (this.startupXsds != null) {
                addXsdFiles(this.startupXsds, true);
                this.startupXsds = null; // reset back to null so not added again
            }

            this.viewer.setInput(this); // could be any object since it is not used by the viewer
            setPageStatus();

            // set focus so that the help context will be correct
            this.btnWorkspaceAdd.setFocus();
        }
        super.setVisible(theShowFlag);
    }

    void addResource( IFile theFile ) {
        CoreArgCheck.isNotNull(theFile);

        // Check whether the file exists ...
        if (!theFile.exists()) {
            final Object[] params = new Object[] {theFile};
            final String msg = getString("FileDoesNotExist", params); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        URI uri = URI.createFileURI(theFile.getRawLocation().toString());
        addResourceURI(theFile, uri);
    }

    void addResource( File theFile ) {
        CoreArgCheck.isNotNull(theFile);

        // Check whether the file exists ...
        if (!theFile.exists()) {
            final Object[] params = new Object[] {theFile};
            final String msg = getString("FileDoesNotExist", params); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        URI uri = getURI(theFile);
        addResourceURI(theFile, uri);
    }

    private void addResourceURI( Object key,
                                 URI uri ) {
        // Check that the URI at least points to a schema file, and the schema file
        // does not give any initial errors. It will have to get loaded again later
        // when the set of schema files must be parsed together to resolve
        // cross references

        XSDSchema schema = SchemaProcessorImpl.getSchemaFromURI(uri);

        if (schema != null) {
            // TODO: throw an error here - what?-jd
        }
        manager.addSchema(key, uri);
    }

    private boolean removeResource( File theFile ) {
        CoreArgCheck.isNotNull(theFile);
        return removeResourceURI(theFile);
    }

    private boolean removeResource( IFile theFile ) {
        CoreArgCheck.isNotNull(theFile);
        return removeResourceURI(theFile);
    }

    private boolean removeResourceURI( Object key ) {
        Object oldValue = manager.removeSchema(key);
        return oldValue != null;
    }

    private URI getURI( File theFile ) {
        URI uri;
        try {
            uri = URI.createFileURI(theFile.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    /**
     * Gets a string representation of the specified object.
     * 
     * @param theObject the object whose string representation is being requested
     * @return the localized text
     * @since 4.2
     */
    public static String getText( Object theObject ) {
        String result = ""; //$NON-NLS-1$

        if (theObject != null) {
            if (theObject instanceof IFile) {
                result = ((IFile)theObject).getFullPath().toOSString();
            } else if (theObject instanceof File) {
                result = theObject.toString();
            } else {
                result = theObject.toString();
            }
        }

        return result;
    }

    /**
     * Copies the <code>toString()</code> of each object in the selection, separated by a linefeed, to the clipboard. If selection
     * is <code>null</code> or empty nothing is copied.
     * 
     * @param theSelection the selection being copied to the clipboard
     * @since 4.2
     */
    public static void copyToClipboard( ISelection theSelection ) {
        List objects = new ArrayList(SelectionUtilities.getSelectedObjects(theSelection));

        if (!objects.isEmpty()) {
            for (int size = objects.size(), i = 0; i < size; i++) {
                objects.set(i, getText(objects.get(i)));
            }

            SystemClipboardUtilities.copyToClipboard(new StructuredSelection(objects));
        }
    }

    /**
     * The content provider for the XSD table.
     * 
     * @since 4.2
     */
    class TableContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        public Object[] getElements( Object theInputElement ) {
            return manager.getSchemaKeySet().toArray();
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }
    }
}

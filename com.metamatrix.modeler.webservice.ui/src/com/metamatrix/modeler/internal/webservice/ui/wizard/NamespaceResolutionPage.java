/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import com.metamatrix.core.io.FileUrl;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * The <code>NamespaceResolutionPage</code> allows the user to resolve namespaces to files.
 * 
 * @since 4.2
 */
public final class NamespaceResolutionPage extends AbstractWizardPage
    implements IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images {

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(NamespaceResolutionPage.class);

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the error column in the table. */
    static final int ERROR_COLUMN;

    /** Index of the namespace column in the table. */
    static final int NAMESPACE_COLUMN;

    /** Index of the path column in the table. */
    static final int PATH_COLUMN;

    /** Number of columns in the main panel. */
    private final int MAIN_PANEL_COLUMNS = 2;

    /** Setup constants used in the table. */
    static {
        // set column indexes
        ERROR_COLUMN = 0;
        NAMESPACE_COLUMN = 1;
        PATH_COLUMN = 2;

        // set column headers
        TBL_HDRS = new String[3];
        TBL_HDRS[ERROR_COLUMN] = ""; //$NON-NLS-1$
        TBL_HDRS[NAMESPACE_COLUMN] = getString("tableColumn.namespace"); //$NON-NLS-1$
        TBL_HDRS[PATH_COLUMN] = getString("tableColumn.path"); //$NON-NLS-1$
    }

    /** The model builder. */
    private IWebServiceModelBuilder builder;

    /** Action to copy file information of selected WSDLs to the clipboard. */
    private IAction copyAction;

    /** Action to view selected namespace using system editor. */
    IAction viewAction;

    /** Action to show namespace dependencies dialog. */
    private IAction showDependenciesAction;

    /** Table containing the namespace resolution data. */
    private TableViewer viewer;

    /**
     * Constructs a <code>NamespaceResolutionPage</code>.'
     * 
     * @param theBuilder the model builder
     * @param urlMap Map of temp file locations and original urls
     */
    public NamespaceResolutionPage( IWebServiceModelBuilder theBuilder ) {
        super(NamespaceResolutionPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.builder = theBuilder;
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
        setPageComplete(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        //
        // create main container
        //

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(MAIN_PANEL_COLUMNS, false));
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, NAMESPACE_RESOLUTION_PAGE);

        //
        // column 1: table panel
        //

        createTablePanel(pnlMain);

        //
        // column 2: buttons that react to table row selection
        //

        // createButtonPanel(pnlMain);
    }

    /**
     * Constructs actions that get installed into the table's ViewForm toolbar.
     * 
     * @param theToolBarMgr the toolbar where the actions will be installed
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
        // view action
        //

        this.viewAction = new Action(getString("action.viewNamespace"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleViewNamespace();
            }
        };

        this.viewAction.setImageDescriptor(WebServiceUiUtil.getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
        this.viewAction.setToolTipText(getString("action.viewNamespace.tip")); //$NON-NLS-1$
        this.viewAction.setEnabled(false);

        theToolBarMgr.add(this.viewAction);

        //
        // show dependencies action
        //

        this.showDependenciesAction = new Action(getString("action.showDependencies"), IAction.AS_PUSH_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                handleShowDependencies();
            }
        };

        this.showDependenciesAction.setImageDescriptor(WebServiceUiUtil.getImageDescriptor(Images.SHOW_DEPENDENCIES));
        this.showDependenciesAction.setToolTipText(getString("action.showDependencies.tip")); //$NON-NLS-1$
        this.showDependenciesAction.setEnabled(false);

        theToolBarMgr.add(this.showDependenciesAction);

        // update the toolbar to get actions to show
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
        mgr.add(this.viewAction);
        // mgr.add(this.unresolveAction);
        mgr.add(this.showDependenciesAction);

        Control table = this.viewer.getControl();
        table.setMenu(mgr.createContextMenu(table));
    }

    /**
     * Constructs the table panel and the ViewForm it is contained in.
     * 
     * @param theParent the parent container
     * @since 4.2
     */
    private void createTablePanel( Composite theParent ) {
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, getString("label.tableViewForm"))); //$NON-NLS-1$

        final int COLUMNS = 1;
        Composite pnl = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH, (MAIN_PANEL_COLUMNS - 1), COLUMNS);
        viewForm.setContent(pnl);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI;
        this.viewer = WidgetFactory.createTableViewer(pnl, style);
        this.viewer.setContentProvider(new TableContentProvider());
        this.viewer.setLabelProvider(new TableLabelProvider());
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleRowSelected();
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                if (NamespaceResolutionPage.this.viewAction.isEnabled()) {
                    NamespaceResolutionPage.this.viewAction.run();
                }
            }
        });

        Table tbl = this.viewer.getTable();
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);

        // create columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);
            col.pack();

            if (i == ERROR_COLUMN) {
                col.setResizable(false);
                col.setImage(WebServiceUiUtil.getImage(Images.RESOLUTION_STATUS));
            }
        }

        // construct actions to be installed in toolbar
        createTableActions(WidgetFactory.createViewFormToolBar(viewForm));

        // context menu for table
        createTableContextMenu();
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     * @since 4.2
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    IWebServiceModelBuilder getBuilder() {
        return this.builder;
    }

    /**
     * Determines if the current file was created from a url originally by looking for the value in the mapping of files to urls
     * built in the <code>WebServiceSelectionPage</code>.
     * 
     * @param file
     * @return file - Eithetr a File or a FileUrl instance
     */
    File getFile( File file ) {

        Object url = null;
        // Get the url map from the previous page.
        final Map urlMap = builder.getUrlMap();

        // Check for an occurance of the file path in the url map and get the original url.
        if (file != null && urlMap != null) {
            String key = file.getAbsolutePath();
            url = urlMap.get(key);
        }

        // If the url variable is null, this File was not created from a URL. Just
        // return the File that was passed in.
        if (url == null) {
            return file;
        }

        // This means the File was created from a URL. Create a FileUrl
        // instance and return.
        FileUrl fileUrl = new FileUrl(file.toURI());
        fileUrl.setOriginalUrlString(url.toString());
        return fileUrl;
    }

    /**
     * Handler for when WSDL file(s) file information is copied to clipboard.
     * 
     * @since 4.2
     */
    void handleCopyWsdlInfo() {
        WebServiceUiUtil.copyToClipboard(this.viewer.getSelection());
    }

    /**
     * Handler for when table row selection changes.
     * 
     * @since 4.2
     */
    void handleRowSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        boolean singleSelection = SelectionUtilities.isSingleSelection(selection);

        this.copyAction.setEnabled(!selection.isEmpty());

        // only enable unresolve namespace if one or more resolved namespaces selected
        boolean enable = false;

        if (!selection.isEmpty()) {
            List resources = SelectionUtilities.getSelectedObjects(selection);

            for (int size = resources.size(), i = 0; i < size; i++) {
                IWebServiceResource resource = (IWebServiceResource)resources.get(i);

                if (resource.isResolved()) {
                    enable = true;
                    break;
                }
            }
        }

        // this.unresolveAction.setEnabled(enable);

        // only enable view action if single selection and resolved
        enable = singleSelection;

        if (singleSelection) {
            IWebServiceResource resource = (IWebServiceResource)SelectionUtilities.getSelectedObject(selection);
            enable = resource.isResolved();
        }

        this.viewAction.setEnabled(enable);
    }

    /**
     * Handler for when the show dependencies button is selected. The {@link ShowDependenciesDialog} is shown.
     * 
     * @since 4.2
     */
    void handleShowDependencies() {
        new ShowDependenciesDialog(getShell(), this.builder).open();
    }

    /**
     * Handler for when a request to view a resolved namespace file. Code copied from action that Eclipse uses to view a file
     * using system editor.
     * 
     * @since 4.2
     */
    void handleViewNamespace() {
        WebServiceUiUtil.viewFile(getShell(), this.viewer.getSelection());
    }

    /**
     * Packs the table columns. Should be called when the table data changes.
     * 
     * @since 4.2
     */
    private void packTableColumns() {
        Table tbl = this.viewer.getTable();

        for (int i = 0; i < TBL_HDRS.length; i++) {
            tbl.getColumn(i).pack();
        }
    }

    /**
     * Sets the wizard page status message.
     * 
     * @since 4.2
     */
    private void setPageStatus() {
        // refresh page message
        IStatus status = this.builder.validateWSDLNamespaces();

        if (status.getSeverity() == IStatus.ERROR) {
            setErrorMessage(status.getMessage());
            setPageComplete(false);
        } else {
            int severity = IStatus.OK;
            String msg = null;

            if (status.getSeverity() == IStatus.OK) {
                msg = getString("page.msg"); //$NON-NLS-1$
            } else {
                severity = status.getSeverity();
                msg = status.getMessage();
            }

            setErrorMessage(null); // must clear error message
            setMessage(msg, severity);
            setPageComplete(true);
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // initialize state
        if (theShowFlag) {
            Collection<IWebServiceResource> resources = this.builder.getResources();
            this.viewer.setInput(resources);

            // enable show dependencies dialog if at least one dependency exists
            boolean enable = false;

            if ((resources != null) && !resources.isEmpty()) {
                Iterator<IWebServiceResource> itr = resources.iterator();

                while (itr.hasNext()) {
                    IWebServiceResource resource = itr.next();

                    if (!resource.getReferencedResources().isEmpty()) {
                        enable = true;
                        break;
                    }
                }
            }

            packTableColumns();
            setPageStatus();

            this.showDependenciesAction.setEnabled(enable);

            // set focus so that the help context will be correct
            this.viewer.getControl().setFocus();
        } else {
            // do this to allow the wizard to finish prior to viewing this page
            setPageComplete(true);
        }

        super.setVisible(theShowFlag);
    }

    /** The content provider for the table. */
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
            return getBuilder().getWSDLResources().toArray();
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

    /** The label provider for the table. */
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            Image result = null;

            if (theElement instanceof IWebServiceResource) {
                IWebServiceResource resource = (IWebServiceResource)theElement;
                IStatus status = resource.getStatus();

                if (theIndex == ERROR_COLUMN) {
                    result = WebServiceUiUtil.getStatusImage(status);
                } else if (theIndex == NAMESPACE_COLUMN) {
                } else if (theIndex == PATH_COLUMN) {
                } else {
                    // should not happen
                    Assertion.failed(UTIL.getString(PREFIX + "msg.unknownTableColumn", //$NON-NLS-1$
                                                    new Object[] {Integer.toString(theIndex)}));
                }
            } else {
                // should not happen
                Assertion.failed(UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;

            if (theElement instanceof IWebServiceResource) {
                IWebServiceResource resource = (IWebServiceResource)theElement;

                if (theIndex == ERROR_COLUMN) {
                    result = ""; //$NON-NLS-1$
                } else if (theIndex == NAMESPACE_COLUMN) {
                    result = WebServiceUiUtil.getText(resource.getNamespace());
                } else if (theIndex == PATH_COLUMN) {
                    result = WebServiceUiUtil.getText(getFile(resource.getFile()));
                } else {
                    // should not happen
                    Assertion.failed(UTIL.getString(PREFIX + "msg.unknownTableColumn", //$NON-NLS-1$
                                                    new Object[] {Integer.toString(theIndex)}));
                }
            } else {
                // should not happen
                Assertion.failed(UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

    }
}

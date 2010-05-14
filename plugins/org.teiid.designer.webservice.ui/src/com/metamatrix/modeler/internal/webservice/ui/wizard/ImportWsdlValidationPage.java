/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.wst.wsdl.validation.internal.IValidationMessage;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;
import com.metamatrix.ui.table.TableViewerSorter;

/**
 *
 */
public class ImportWsdlValidationPage extends AbstractWizardPage
    implements FileUtils.Constants, IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ImportWsdlValidationPage.class);

    private static final int SEVERITY_COLUMN = 0;
    private static final int MESSAGE_COLUMN = 1;
    private static final int OBJECT_COLUMN = 2;
    private static final int LINE_NUMBER_COLUMN = 3;

    private static final String MESSAGE_HEADER = getString("messageHeader"); //$NON-NLS-1$
    private static final String OBJECT_HEADER = getString("objectHeader"); //$NON-NLS-1$
    private static final String LINE_NUMBER_HEADER = getString("lineNumber"); //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog height. */
    private static final String DIALOG_HEIGHT = "dialogHeight"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog width. */
    private static final String DIALOG_WIDTH = "dialogWidth"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog X position. */
    private static final String DIALOG_X = "dialogX"; //$NON-NLS-1$

    /** <code>IDialogSetting</code>s key for saved dialog Y position. */
    private static final String DIALOG_Y = "dialogY"; //$NON-NLS-1$

    /** The model builder. */
    protected IWebServiceModelBuilder builder;

    /** Action to view selected WSDL using system editor. */
    private IAction viewWsdlAction;

    /** The wsdl file validation warning/error table view */
    private TableViewer viewer;

    /**
     * The list of WSDL validation messages. These are of type IValidationMessage. This list is filled when the validateWSDLs()
     * method is run.
     */
    List wsdlValidationMessages;

    private boolean wsdlsHaveErrors = true;

    /**
     * Constructs a <code>WsdlValidationPage</code> using the specified builder.
     * 
     * @param theBuilder the model builder
     * @since 4.2
     */
    public ImportWsdlValidationPage( IWebServiceModelBuilder theBuilder ) {
        super(ImportWsdlValidationPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        wsdlValidationMessages = new LinkedList();
        this.builder = theBuilder;
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
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

        createTablePanel(pnlMain);

        nameModified();
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizardPage#canFlipToNextPage()
     * @since 4.2
     */
    @Override
    public boolean canFlipToNextPage() {
        // if no WSDLs identified don't let the next page show
        return !wsdlsHaveErrors && super.canFlipToNextPage();
    }

    /**
     * Constructs the table's toolbar actions.
     * 
     * @param theToolBarMgr the toolbar where the actions are installed
     * @since 4.2
     */
    private void createTableActions( IToolBarManager theToolBarMgr ) {

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
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, getString("WSDL_Validation_Panel"))); //$NON-NLS-1$

        Composite pnl = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH);
        viewForm.setContent(pnl);

        this.viewer = WidgetFactory.createTableViewer(pnl, SWT.FULL_SELECTION);
        final Table table = this.viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        WidgetFactory.createTableColumn(table);
        WidgetFactory.createTableColumn(table, MESSAGE_HEADER);
        WidgetFactory.createTableColumn(table, OBJECT_HEADER);
        WidgetFactory.createTableColumn(table, LINE_NUMBER_HEADER);
        this.viewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public Object[] getElements( final Object inputElement ) {

                return wsdlValidationMessages.toArray();
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        });
        this.viewer.setLabelProvider(new AbstractTableLabelProvider() {

            @Override
            public Image getColumnImage( final Object element,
                                         final int column ) {
                if (column == SEVERITY_COLUMN) {
                    return getStatusImage(((IValidationMessage)element).getSeverity());
                }
                return null;
            }

            public String getColumnText( final Object element,
                                         final int column ) {
                final IValidationMessage message = (IValidationMessage)element;
                switch (column) {
                    case MESSAGE_COLUMN: {
                        final String messageString = message.getMessage();
                        return messageString == null ? CoreStringUtil.Constants.EMPTY_STRING : messageString;
                    }
                    case OBJECT_COLUMN: {
                        // Get the file name from the URI. This is the key to the url map if this wsdl was loaded via url.
                        String target = (String)builder.getUrlMap().get(message.getURI().substring(message.getURI().lastIndexOf('/') + 1));
                        // If the target is null, this wsdl was file based so we get the uri off the message
                        if (target == null) target = message.getURI();
                        return target == null ? CoreStringUtil.Constants.EMPTY_STRING : target;
                    }
                    case LINE_NUMBER_COLUMN: {
                        final String lineNumber = String.valueOf(message.getLine());
                        return lineNumber == null ? CoreStringUtil.Constants.EMPTY_STRING : lineNumber;
                    }
                }
                return CoreStringUtil.Constants.EMPTY_STRING;
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick( final DoubleClickEvent event ) {
            }
        });
        this.viewer.setSorter(new TableViewerSorter(this.viewer, SEVERITY_COLUMN, TableViewerSorter.ASCENDING) {

            @Override
            protected int compareColumn( final TableViewer viewer,
                                         final Object object1,
                                         final Object object2,
                                         final int column ) {
                if (column == SEVERITY_COLUMN) {
                    return (((IValidationMessage)object2).getSeverity() - ((IValidationMessage)object1).getSeverity());
                }
                return super.compareColumn(viewer, object1, object2, column);
            }
        });

        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTableSelectionChanged();
            }
        });

        this.viewer.setInput(this);
        WidgetUtil.pack(table);

        // construct actions to be installed in toolbar
        createTableActions(WidgetFactory.createViewFormToolBar(viewForm));

        // context menu for table
        createTableContextMenu();
    }

    /**
     * @since 4.0
     */
    void nameModified() {
        validatePage();
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
     * @since 4.2
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /**
     * Handler for when table selection changes.
     * 
     * @since 4.2
     */
    void handleTableSelectionChanged() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enable buttons
        this.viewWsdlAction.setEnabled(selection.size() == 1);
    }

    /**
     * Handler for when a request to view a WSDL file. Code copied from action that Eclipse uses to view a file using system
     * editor.
     * 
     * @since 4.2
     */
    void handleViewWsdlFile() {
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

            this.viewer.setInput(this); // could be any object since it is not used by the viewer

            // // set focus so that the help context will be correct
            // if (btnWorkspaceAdd != null) {
            // this.btnWorkspaceAdd.setFocus();
            // }
        }

        super.setVisible(theShowFlag);

        validatePage();

    }

    /**
     * Check to see if there are any validation messages for display.
     * 
     * @return boolean
     */
    public boolean hasValidationMessages() {
        return wsdlValidationMessages.size() > 0;
    }

    /**
     * Setter for wsdl validation messages related to currently selected wsdl files.
     * 
     * @param wsdlValidationMessageMap
     */
    public void setValidationMessages( Map wsdlValidationMessageMap ) {

        Iterator wsdlIter = wsdlValidationMessageMap.values().iterator();
        /*
         * Iterate through the list of validation messages for the currently selected wsdl files.
         * If the message already exists in the validation page's current list of messages, we will
         * not add them again. This logic is necessary to get around a bug in the wst validator
         * that will add new messages for the same wsdl file if it is added, removed and added again.
         */
        while (wsdlIter.hasNext()) {
            IValidationReport report = (IValidationReport)wsdlIter.next();
            IValidationMessage[] messages = report.getValidationMessages();
            for (int i = 0; i < messages.length; i++) {
                if (!wsdlValidationMessages.contains(messages[i])) {
                    boolean alreadyExists = false;
                    for (int j = 0; j < wsdlValidationMessages.size(); j++) {
                        IValidationMessage currentWsdlMessage = (IValidationMessage)wsdlValidationMessages.get(j);
                        if (currentWsdlMessage.getMessage().equals(messages[i].getMessage())
                            && currentWsdlMessage.getLine() == messages[i].getLine()) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        wsdlValidationMessages.add(messages[i]);
                    }
                }
            }
        }
    }

    public void clearValidationMessages() {
        wsdlValidationMessages.clear();
    }

    /**
     * @since 4.0
     */
    private void validatePage() {
        /*
         * Check to see if any WSDLs have any validation messages.
         */
        if (hasValidationMessages()) {
            WizardUtil.setPageComplete(this, getString("One_or_more_selected_WSDL_files_have_errors"), IMessageProvider.ERROR); //$NON-NLS-1$
        }

    }

    Image getStatusImage( final int severity ) {

        switch (severity) {
            case IValidationMessage.SEV_ERROR: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            }
            case IValidationMessage.SEV_WARNING: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
            }

            default: {
                return null;
            }
        }
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.webservice.NonResolvableXSDSchema;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceXsdResource;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 4.2
 */
public class SchemaLocationPage extends AbstractWizardPage
    implements IInternalUiConstants, IInternalUiConstants.HelpContexts, IInternalUiConstants.Images {

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(SchemaLocationPage.class);

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the error column in the table. */
    static final int ERROR_COLUMN;

    /** Index of the namespace column in the table. */
    static final int NAMESPACE_COLUMN;

    /** Index of the source file column in the table. */
    static final int SOURCE_FILE_COLUMN;

    /** Index of the target path column in the table. */
    static final int TARGET_PATH_COLUMN;

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

    static {
        // set column indexes
        ERROR_COLUMN = 0;
        NAMESPACE_COLUMN = 1;
        SOURCE_FILE_COLUMN = 2;
        TARGET_PATH_COLUMN = 3;

        // set column headers
        TBL_HDRS = new String[4];
        TBL_HDRS[ERROR_COLUMN] = ""; //$NON-NLS-1$
        TBL_HDRS[NAMESPACE_COLUMN] = getString("table.column.namespace"); //$NON-NLS-1$
        TBL_HDRS[SOURCE_FILE_COLUMN] = getString("table.column.sourceFile"); //$NON-NLS-1$
        TBL_HDRS[TARGET_PATH_COLUMN] = getString("table.column.targetPath"); //$NON-NLS-1$
    }

    /** The model builder. */
    private IWebServiceModelBuilder builder;

    /** Action to show/hide editor. */
    private IAction showEditorAction;

    private SchemaLocationEditor editor;

    private CLabel lblStatusMsg;

    private TableViewer viewer;

    private ViewForm viewForm;

    public SchemaLocationPage( IWebServiceModelBuilder theBuilder ) {
        super(SchemaLocationPage.class.getSimpleName(), getString("title")); //$NON-NLS-1$
        this.builder = theBuilder;
        setImageDescriptor(WebServiceUiUtil.getImageDescriptor(NEW_MODEL_BANNER));
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    public void createControl( Composite theParent ) {
        //
        // Create main container
        //

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        setControl(pnlMain);

        IWorkbenchHelpSystem helpSystem = UiUtil.getWorkbench().getHelpSystem();
        helpSystem.setHelp(pnlMain, SCHEMA_LOCATION_PAGE);

        //
        // pnlMain contents
        //

        // target models view form
        this.viewForm = WidgetFactory.createViewForm(pnlMain, SWT.BORDER, GridData.FILL_BOTH, 1);
        this.viewForm.setTopLeft(WidgetFactory.createLabel(this.viewForm, getString("label.viewForm"))); //$NON-NLS-1$
        createTablePanelContents(this.viewForm);

        createTableActions(WidgetFactory.createViewFormToolBar(this.viewForm));

        // editor view form
        this.editor = new SchemaLocationEditor(pnlMain, this.builder);
        this.editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.editor.addListener(new Listener() {
            public void handleEvent( Event theEvent ) {
                processEditorEvent(theEvent);
            }
        });
    }

    private void createTableActions( IToolBarManager theToolBarMgr ) {
        this.showEditorAction = new Action("", IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                showEditor(!getEditor().isVisible());
            }
        };

        this.showEditorAction.setImageDescriptor(WebServiceUiUtil.getImageDescriptor(SCHEMA_EDITOR));
        this.showEditorAction.setToolTipText(getString("button.showEditor.tip")); //$NON-NLS-1$
        this.showEditorAction.setChecked(true);
        theToolBarMgr.add(this.showEditorAction);

        theToolBarMgr.update(true);
    }

    private void createTablePanelContents( ViewForm theViewForm ) {
        Composite pnl = WidgetFactory.createPanel(theViewForm, SWT.NONE, GridData.FILL_BOTH);
        theViewForm.setContent(pnl);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION;
        Table tbl = new Table(pnl, style);
        tbl.setLayout(new GridLayout());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = tbl.getItemHeight() * 8;
        gd.widthHint = 450;
        tbl.setLayoutData(gd);
        this.viewer = new TableViewer(tbl);
        this.viewer.setContentProvider(new TableContentProvider());
        this.viewer.setLabelProvider(new TableLabelProvider());
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleRowSelected();
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleDoubleClick();
            }
        });

        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);

        // create columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);
            col.pack();

            if (i == ERROR_COLUMN) {
                col.setResizable(false);
                col.setImage(WebServiceUiUtil.getImage(PROBLEM_INDICATOR));
            }
        }

        // label showing selected unit's status message
        this.lblStatusMsg = WidgetFactory.createLabel(pnl, GridData.HORIZONTAL_ALIGN_FILL);
    }

    /**
     * Convenience method to give inner classes access to the builder.
     * 
     * @return the builder
     * @since 4.2
     */
    IWebServiceModelBuilder getBuilder() {
        return this.builder;
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

    SchemaLocationEditor getEditor() {
        return this.editor;
    }

    void handleDoubleClick() {
        if (!getEditor().isVisible()) {
            showEditor(true);
        }
    }

    void handleRowSelected() {
        // if uncommitted changes exist in editor offer to save prior to setting new editor input
        handleUnsavedEditor();

        // process new selection
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enable/disable editor related controls
        boolean doEdit = !selection.isEmpty();
        this.editor.setEnabled(doEdit);

        // load/clear editor based on selection
        if (doEdit) {
            this.editor.setInput(selection.toList());
        } else {
            this.editor.clear();
        }

        updateStatusLabel();
        updatePageStatus();
    }

    void handleUnsavedEditor() {
        if (this.editor.isDirty() && this.editor.canSave()) {
            String msg = null;
            List editorInput = this.editor.getInput();

            if (editorInput.size() == 1) {
                IWebServiceXsdResource xsd = (IWebServiceXsdResource)editorInput.get(0);
                msg = UTIL.getString(PREFIX + "dialog.dirtyEditor.oneFile.msg", new Object[] {xsd.getTargetNamespace()}); //$NON-NLS-1$
            } else {
                msg = getString("dialog.dirtyEditor.multipleFiles.msg"); //$NON-NLS-1$
            }

            if (MessageDialog.openQuestion(getShell(), getString("dialog.dirtyEditor.title"), msg)) { //$NON-NLS-1$
                this.editor.save();
            }
        }
    }

    private void packTableColumns() {
        Table tbl = this.viewer.getTable();

        for (int i = 0; i < TBL_HDRS.length; i++) {
            tbl.getColumn(i).pack();
        }
    }

    void processEditorEvent( Event theEvent ) {
        if (theEvent.type == SchemaLocationEditor.CLOSED) {
            // editor is already closed but calling this method sets the button state to match
            // and layouts the container
            showEditor(false);
        } else {
            // editor was saved
            // refresh each row that was edited. will always have rows selected.
            // can't use the currently selected rows in the viewer for this as they have already changed.
            List editorInput = this.editor.getInput();

            for (int size = editorInput.size(), i = 0; i < size; i++) {
                this.viewer.refresh(editorInput.get(0), true);
            }

            packTableColumns();

            // update statuses
            updatePageStatus();
            updateStatusLabel();
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // gets the editor and table initialized along with the page status
        // and makes sure they're are no unsaved changes in the editor
        handleRowSelected();

        if (theShowFlag) {
            this.editor.setDialogSettings(getDialogSettings());
            this.viewer.setInput(this);
            packTableColumns();

            // set focus so that the help context will be correct
            this.viewer.getControl().setFocus();
        } else {
            this.editor.clear();
        }

        super.setVisible(theShowFlag);
    }

    void showEditor( boolean theShowFlag ) {
        this.showEditorAction.setChecked(theShowFlag);
        this.editor.setVisible(theShowFlag);
        this.editor.getParent().layout(true);
    }

    private void updatePageStatus() {
        // loop through to determine current status
        boolean enable = true;
        int severity = IStatus.OK;
        String msgId = null;
        Collection xsdResources = null;

        boolean requiredStart = ModelerCore.startTxn(false, false, "Get XSD Destinations", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            xsdResources = this.builder.getXsdDestinations();
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        if ((xsdResources == null) || xsdResources.isEmpty()) {
            msgId = "page.msg.noXsdResources"; //$NON-NLS-1$
            enable = false;
        } else {
            Iterator itr = xsdResources.iterator();

            while (itr.hasNext()) {
                IWebServiceXsdResource xsd = (IWebServiceXsdResource)itr.next();
                IStatus status = xsd.isValid();

                if (status.getSeverity() > severity) {
                    severity = status.getSeverity();

                    if (severity == IStatus.ERROR) {
                        break;
                    }
                }
            }

            switch (severity) {
                case IStatus.ERROR: {
                    msgId = "page.msg.error"; //$NON-NLS-1$
                    break;
                }
                case IStatus.WARNING: {
                    msgId = "page.msg.warning"; //$NON-NLS-1$
                    break;
                }
                case IStatus.INFO: {
                    msgId = "page.msg.info"; //$NON-NLS-1$
                    break;
                }
                default: {
                    msgId = "page.msg.ok"; //$NON-NLS-1$
                    break;
                }
            }
        }

        // enable table
        this.viewer.getControl().setEnabled(enable);

        // set page status
        String msg = getString(msgId);

        if (severity == IStatus.ERROR) {
            setMessage(msg, IStatus.ERROR);
            setPageComplete(false);
        } else {
            setMessage(msg, severity);
            setPageComplete(true);
        }
    }

    private void updateStatusLabel() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        Image image = null;
        String text = ""; //$NON-NLS-1$

        if (selection.size() == 1) {
            IWebServiceXsdResource xsd = (IWebServiceXsdResource)selection.getFirstElement();
            IStatus status = xsd.isValid();

            if (!status.isOK()) {
                image = WebServiceUiUtil.getStatusImage(status);
                text = status.getMessage();
            }
        }

        this.lblStatusMsg.setImage(image);
        this.lblStatusMsg.setText(text);
    }

    /**
     * Determines if the current file location was created from a url originally by looking for the value in the mapping of files
     * to urls built in the <code>WebServiceSelectionPage</code>.
     * 
     * @param schemaLocation
     * @return location (Original Url or the actual file location)
     */
    String getUrlValue( String schemaLocation ) {

        Object location = null;

        Map urlMap = getBuilder().getUrlMap();

        // Remove preceding backslash from schemaLocation
        String substringLocation = schemaLocation.substring(1);

        if (urlMap != null) {
            location = urlMap.get(substringLocation);
        }

        return location == null ? schemaLocation : location.toString();
    }

    /**
     * Formats source location for display
     * 
     * @param schemaLocation
     * @return location
     */
    String formatLocation( String schemaLocation ) {

        String location = schemaLocation;

        // Strip off the file schema if present.
        if (location.indexOf("file:/") > -1) { //$NON-NLS-1$
            location = location.substring(6);
        }

        return location;
    }

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
            Collection xsdResources = getBuilder().getXsdDestinations();
            return (xsdResources == null) ? Collections.EMPTY_LIST.toArray() : xsdResources.toArray();
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

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;

            if (theElement instanceof IWebServiceXsdResource) {
                if (theColumnIndex == ERROR_COLUMN) {
                    IStatus status = ((IWebServiceXsdResource)theElement).isValid();
                    result = WebServiceUiUtil.getStatusImage(status);
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

            if (theElement instanceof IWebServiceXsdResource) {
                IWebServiceXsdResource xsd = (IWebServiceXsdResource)theElement;

                if (theIndex == ERROR_COLUMN) {
                    result = ""; //$NON-NLS-1$
                } else if (theIndex == NAMESPACE_COLUMN) {
                    result = xsd.getTargetNamespace();
                } else if (theIndex == SOURCE_FILE_COLUMN) {
                    // Defect 23072 - handle NonResolvable schemas
                    if (xsd.getSchema() instanceof NonResolvableXSDSchema) {
                        NonResolvableXSDSchema nrSchema = (NonResolvableXSDSchema)xsd.getSchema();
                        String schemaLoc = nrSchema.getSchemaLocation();
                        if (schemaLoc != null) {
                            result = formatLocation(schemaLoc);
                        } else {
                            result = nrSchema.getUri() != null ? nrSchema.getUri().toString() : null;
                        }
                        // If this is a standalone schema file, xsd.getSchema().getSchemaLocation() will return a
                        // non-null value. If this is schema imbedded in a WSDL file, xsd.getOriginalPath() will return
                        // the correct value which we will use to see if there is a corresponding url value in the urlMap.
                    } else {
                        String schemaLoc = xsd.getSchema().getSchemaLocation();
                        result = schemaLoc != null ? formatLocation(schemaLoc) : getUrlValue(xsd.getOriginalPath());
                    }
                } else if (theIndex == TARGET_PATH_COLUMN) {
                    result = xsd.getDestinationPath().makeRelative().toOSString();
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

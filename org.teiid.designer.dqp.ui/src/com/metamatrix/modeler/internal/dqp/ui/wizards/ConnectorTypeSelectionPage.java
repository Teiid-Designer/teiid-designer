/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>ConnectorTypeSelectionPage</code> class is the first page of the connector importer. This mandatory page is where the
 * user first chooses the connector file to process and then chooses which connector binding types they want to import.
 * 
 * @since 5.5.3
 */
public final class ConnectorTypeSelectionPage extends WizardPage {

    /**
     * Table column indexes.
     * 
     * @since 5.5.3
     */
    private interface ColumnIndexes {
        int NAME = 0;
        int STATUS = 1;
    }

    /**
     * Dialog setting properties that keep track of the last used directory where the connector file was imported from.
     * 
     * @since 5.5.3
     */
    private interface DialogSettingsProperties {
        String PAGE_SECTION = ConnectorTypeSelectionPage.class.getSimpleName();
        String LOCATION = "location"; //$NON-NLS-1$
    }

    /**
     * Valid connector file extensions.
     * 
     * @since 5.5.3
     */
    private static final String[] FILE_EXTENSIONS = {"*.caf;*.cdk", "*.caf", "*.cdk"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * Names associated with the valid connector file extensions.
     * 
     * @since 5.5.3
     */
    private static final String[] FILE_EXTENSION_NAMES = {I18n.AllConnectorFileExtensionNames + " (" + FILE_EXTENSIONS[0] + ')', //$NON-NLS-1$
        I18n.CafFileExtensionName + " (" + FILE_EXTENSIONS[1] + ')', //$NON-NLS-1$
        I18n.CdkFileExtensionName + " (" + FILE_EXTENSIONS[2] + ')'}; //$NON-NLS-1$

    /**
     * Table headers used to create the table columns.
     * 
     * @since 5.5.3
     */
    private static final String[] HEADERS;

    static {
        HEADERS = new String[2];
        HEADERS[ColumnIndexes.NAME] = I18n.NameHeader;
        HEADERS[ColumnIndexes.STATUS] = I18n.StatusHeader;
    }

    /**
     * Indicates if the file the user selected to import is valid.
     * 
     * @since 5.5.3
     */
    private boolean connectorFileValid;

    /**
     * The area where the selected row details is shown.
     * 
     * @since 5.5.3
     */
    private DetailsAreaPanel detailsArea;

    /**
     * The business object used by the wizard.
     * 
     * @since 5.5.3
     */
    private final ConnectorImportHelper helper;

    /**
     * Label identifying the connector file being imported.
     * 
     * @since 5.5.3
     */
    private CLabel lblFilePath;

    /**
     * The table containing the available connector types for import.
     * 
     * @since 5.5.3
     */
    private TableViewer viewer;

    /**
     * @param helper the business object
     * @since 5.5.3
     */
    public ConnectorTypeSelectionPage( ConnectorImportHelper helper ) {
        super(ConnectorTypeSelectionPage.class.getSimpleName());
        this.helper = helper;
        setTitle(I18n.ConnectorTypeSelectionPageTitle);
    }

    /**
     * A way for inner classes to get to the business object.
     * 
     * @since 5.5.3
     */
    ConnectorImportHelper accessHelper() {
        return this.helper;
    }

    /**
     * Constructs the controls related to choosing the import file.
     * 
     * @param parent the UI parent of the browse panel
     * @since 5.5.3
     */
    private void constructBrowsePanel( Composite parent ) {
        // construct sub-panel
        Composite pnlBrowse = WidgetFactory.createPanel(parent, SWT.BORDER, GridData.FILL_HORIZONTAL, 1, 3);

        // add label
        WidgetFactory.createLabel(pnlBrowse, I18n.BrowseConnectorFileLabel);

        // add control that displays the selected import file path
        this.lblFilePath = new CLabel(pnlBrowse, SWT.READ_ONLY | SWT.SINGLE);
        this.lblFilePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.lblFilePath.setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));

        // add browse button
        Button btnBrowse = WidgetFactory.createButton(pnlBrowse, I18n.BrowseConnectorFileButton);
        btnBrowse.setToolTipText(I18n.BrowseConnectorFileToolTip);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleBrowse();
            }
        });
        WidgetUtil.setLayoutData(btnBrowse);
    }

    /**
     * Constructs the table containing the connector types available for import.
     * 
     * @param parent the UI parent of the table panel
     * @since 5.5.3
     */
    private void constructTablePanel( Composite parent ) {
        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK;
        this.viewer = WidgetFactory.createTableViewer(parent, style);
        this.viewer.setContentProvider(new TypeContentProvider());
        this.viewer.setLabelProvider(new TypeLabelProvider());

        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleConnectorTypeSelected(event);
            }
        });

        // create columns
        WidgetFactory.createTableColumns(table, HEADERS, SWT.LEFT);

        // populate the table
        this.viewer.setInput(this);
        refresh(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    public void createControl( Composite parent ) {
        Composite mainControl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        setControl(mainControl);

        // panel to choose the import file
        constructBrowsePanel(mainControl);

        // put a splitter between the table and the import details
        SashForm splitter = WidgetFactory.createSplitter(mainControl, SWT.VERTICAL, GridData.FILL_BOTH);

        // construct table
        constructTablePanel(splitter);

        // create details control that displays the selected connector type's import details
        this.detailsArea = new DetailsAreaPanel(splitter);

        // position the splitter
        splitter.setWeights(new int[] {7, 3});
        splitter.layout();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 5.5.3
     */
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings temp = super.getDialogSettings();
        IDialogSettings settings = temp.getSection(DialogSettingsProperties.PAGE_SECTION);

        // add a section in the wizard's dialog setting for this page
        if (settings == null) {
            settings = temp.addNewSection(DialogSettingsProperties.PAGE_SECTION);
        }

        return settings;
    }

    /**
     * Handler for the browse button.
     * 
     * @since 5.5.3
     */
    void handleBrowse() {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setText(I18n.ConnectorFileBrowseDialogTitle);
        dialog.setFilterExtensions(FILE_EXTENSIONS);
        dialog.setFilterNames(FILE_EXTENSION_NAMES);

        // set directory to last used directory
        String directory = getDialogSettings().get(DialogSettingsProperties.LOCATION);

        if (!StringUtil.isEmpty(directory)) {
            dialog.setFilterPath(directory);
        }

        // show dialog
        String connectorFile = dialog.open();

        // process file choice if one was made
        if (connectorFile != null) {
            connectorFileValid = false;

            // remember the filter path for the next time dialog is shown
            IDialogSettings settings = getDialogSettings();
            settings.put(DialogSettingsProperties.LOCATION, dialog.getFilterPath());

            // update business object and set the path in the UI
            IStatus status = this.helper.setConnectorFile(connectorFile);
            this.lblFilePath.setText(connectorFile);

            if (status.isOK()) {
                this.connectorFileValid = true;
                refresh(true);
            } else {
                // not a good file
                setMessage(status.getMessage(), IMessageProvider.ERROR);
                setPageComplete(false);
                refresh(false);
            }
        }
    }

    /**
     * Handler for when a table row is selected.
     * 
     * @since 5.5.3
     */
    void handleConnectorTypeSelected( SelectionEvent event ) {
        // user checked/unchecked connector type - update import status in business object
        TableItem tableItem = (TableItem)event.item;
        if (event.detail == SWT.CHECK) {
            ConnectorBindingType type = (ConnectorBindingType)tableItem.getData();
            this.helper.setImportStatus(type, tableItem.getChecked());
        }

        // Update details panel and page status
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        ConnectorBindingType selectedType = (ConnectorBindingType)selection.getFirstElement();
        // update details panel and page state
        this.detailsArea.setStatus(this.helper.getStatus(selectedType));
        updateState();
    }

    /**
     * Refreshes the table and page messages and button state.
     * 
     * @param updatePageState <code>true</code> if the page state should be updated
     * @since 5.5.3
     */
    private void refresh( boolean updatePageState ) {
        this.viewer.refresh();
        WidgetUtil.pack(this.viewer.getTable(), 10);

        // update checkboxes
        for (TableItem item : this.viewer.getTable().getItems()) {
            ConnectorBindingType type = (ConnectorBindingType)item.getData();
            item.setChecked(this.helper.isSelectedForImport(type));
        }

        // auto-select first row
        Table table = this.viewer.getTable();

        if (this.viewer.getSelection().isEmpty() && (table.getItemCount() != 0)) {
            table.select(0);
            Event event = new Event();
            event.widget = table;
            table.notifyListeners(SWT.Selection, event);
        }

        // update page state
        if (updatePageState) {
            updateState();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     * @since 5.5.3
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        // wizard was started or next or previous button was pressed to show this page so update UI
        if (visible) {
            refresh(true);
        }
    }

    /**
     * Update page messages and button state.
     * 
     * @since 5.5.3
     */
    private void updateState() {
        boolean complete = this.connectorFileValid;

        setErrorMessage(null);
        if (complete) {
            // no connector types to import
            if (this.helper.getSelectedImportFileConnectorTypes().isEmpty()) {
                // no connector bindings to import
                if (this.helper.getAllImportFileConnectors().isEmpty()) {
                    // nothing to import
                    complete = false;
                    setMessage(I18n.NoConnectorTypesToImportNoConnectorsMsg, IMessageProvider.ERROR);
                } else {
                    // there are connector bindings available to import
                    setMessage(I18n.NoConnectorTypesToImportSelectConnectorsMsg);
                }
            } else {
                // there are connector types to import
                int numTypes = this.helper.getSelectedImportFileConnectorTypes().size();

                // there are no connector bindings to import
                if (this.helper.getAllImportFileConnectors().isEmpty()) {
                    // only one connector type and no connector bindings to import
                    if (numTypes == 1) {
                        setMessage(I18n.OneConnectorTypeToImportNoConnectorsMsg);
                    } else {
                        // no connector bindings but multiple connector types to import
                        setMessage(NLS.bind(I18n.ManyConnectorTypesToImportNoConnectorsMsg, numTypes));
                    }
                } else {
                    // there are connector bindings available and one connector type to import
                    if (numTypes == 1) {
                        setMessage(I18n.OneConnectorTypeToImportSelectConnectorsMsg);
                    } else {
                        // there are connector bindings available and many connector types to import
                        setMessage(NLS.bind(I18n.ManyConnectorTypesToImportSelectConnectorsMsg, numTypes));
                    }
                }
            }
        } else {
            setMessage(I18n.ConnectorFileMissingMsg, IMessageProvider.ERROR);
        }

        setPageComplete(complete);
    }

    /**
     * The <code>TypeContentProvider</code> provides the available connector types as content.
     * 
     * @since 5.5.3
     */
    class TypeContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return accessHelper().getAllNonStandardImportFileConnectorTypes().toArray();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // nothing to do
        }
    }

    /**
     * @since 5.5.3
     */
    class TypeLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            if (columnIndex == ColumnIndexes.STATUS) {
                ConnectorBindingType type = (ConnectorBindingType)element;
                IStatus status = accessHelper().getStatus(type);
                return WidgetUtil.getStatusImage(status);
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object element,
                                     int columnIndex ) {
            ConnectorBindingType type = (ConnectorBindingType)element;

            if (columnIndex == ColumnIndexes.NAME) {
                return type.getFullName();
            }

            return StringUtil.Constants.EMPTY_STRING;
        }
    }
}

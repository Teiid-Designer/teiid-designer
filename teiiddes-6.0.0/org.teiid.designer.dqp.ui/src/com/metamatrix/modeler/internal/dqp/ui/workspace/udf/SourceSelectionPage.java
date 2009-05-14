/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.modeler.util.FileUtil.Extensions;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.workspace.udf.UdfModelImporter;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * A wizard page to allow the user to choose the UDF model zip file to import.
 * 
 * @since 6.0.0
 */
public final class SourceSelectionPage extends AbstractWizardPage {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * A prefix for I18n message keys.
     * 
     * @since 6.0.0
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SourceSelectionPage.class);

    /**
     * Message indicating the UDF Model jar in the zip file will be imported.
     * 
     * @since 6.0.0
     */
    static final String IMPORT_MSG = UTIL.getString(PREFIX + "importArchiveMsg"); //$NON-NLS-1$;

    /**
     * Message indicating the existing workspace jar will be overwritten.
     * 
     * @since 6.0.0
     */
    static final String IMPORTING_OVERWRITE_MSG = UTIL.getString(PREFIX + "overwriteExistingArchiveMsg"); //$NON-NLS-1$;

    /**
     * Message indicating the UDF Model jar in the zip file will not be imported.
     * 
     * @since 6.0.0
     */
    static final String NOT_IMPORTING_MSG = UTIL.getString(PREFIX + "notImportingArchiveMsg"); //$NON-NLS-1$;

    // ===========================================================================================================================
    // Interfaces
    // ===========================================================================================================================

    private interface DialogSettingsProperties {
        String PAGE_SECTION = SourceSelectionPage.class.getSimpleName();
        String LOCATIONS = "locations"; //$NON-NLS-1$
    }

    /**
     * Table column indexes.
     * 
     * @since 6.0.0
     */
    private interface ColumnIndexes {
        int NAME = 0;
        int ACTION = 1;
    }

    /**
     * Table headers used to create the table columns.
     * 
     * @since 6.0.0
     */
    private static final String[] HEADERS;

    // ===========================================================================================================================
    // Class Initializer
    // ===========================================================================================================================

    static {
        HEADERS = new String[2];
        // Order of headers is determined by ColumnIndexes above, not by definition order below
        HEADERS[ColumnIndexes.NAME] = UTIL.getString(PREFIX + "nameHeader"); //$NON-NLS-1$
        HEADERS[ColumnIndexes.ACTION] = UTIL.getString(PREFIX + "actionHeader"); //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * MRU of previously imported files.
     * 
     * @since 6.0.0
     */
    private Combo cbxZipFileName;

    /**
     * The business object.
     * 
     * @since 6.0.0
     */
    private final UdfModelImporter importer;

    /**
     * The label above the jar list.
     * 
     * @since 6.0.0
     */
    private Label lblIncludedJars;

    private TableViewer viewer;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param importer the wizard business object
     * @since 6.0.0
     */
    public SourceSelectionPage( UdfModelImporter importer ) {
        super(SourceSelectionPage.class.getSimpleName(), UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
        this.importer = importer;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @return the business object
     * @since 6.0.0
     */
    UdfModelImporter accessImporter() {
        return this.importer;
    }

    /**
     * Constructs the table containing the UDF import jar files available for import.
     * 
     * @param parent the UI parent of the table panel
     * @since 6.0.0
     */
    private void constructTablePanel( Composite parent ) {
        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK;
        this.viewer = WidgetFactory.createTableViewer(parent, style);
        this.viewer.setContentProvider(new UdfJarFileContentProvider());
        this.viewer.setLabelProvider(new UdfJarFileLabelProvider());

        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleJarFileSelected(event);
            }
        });

        // create columns
        WidgetFactory.createTableColumns(table, HEADERS, SWT.LEFT);

        // populate the table
        this.viewer.setInput(this);
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    public void createControl( Composite parent ) {
        Composite mainControl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        setControl(mainControl);

        // pnlBrowse looks like:
        // label textfield button
        Composite pnlBrowse = WidgetFactory.createPanel(mainControl, SWT.NONE, GridData.FILL_HORIZONTAL, 1, 3);

        // add label
        WidgetFactory.createLabel(pnlBrowse, UTIL.getString(PREFIX + "lblImportLocation")); //$NON-NLS-1$

        // add MRU combo box of previously imported files
        this.cbxZipFileName = WidgetFactory.createCombo(pnlBrowse,
                                                        SWT.READ_ONLY,
                                                        GridData.FILL_HORIZONTAL,
                                                        getDialogSettings().getArray(DialogSettingsProperties.LOCATIONS));
        this.cbxZipFileName.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleInputFileModified();
            }
        });

        // add browse button
        Button btnBrowse = WidgetFactory.createButton(pnlBrowse, UTIL.getString(PREFIX + "btnBrowse")); //$NON-NLS-1$
        btnBrowse.setToolTipText(UTIL.getString(PREFIX + "btnBrowse.tip")); //$NON-NLS-1$
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleBrowse();
            }
        });
        WidgetUtil.setLayoutData(btnBrowse);

        // pnlJars looks like:
        // label
        // table
        Composite pnlJars = WidgetFactory.createPanel(mainControl, SWT.NONE, GridData.FILL_BOTH);

        // add label for the included jars
        this.lblIncludedJars = new Label(pnlJars, SWT.NONE);
        this.lblIncludedJars.setText(UTIL.getString(PREFIX + "lblIncludedJars")); //$NON-NLS-1$

        // table showing include jars
        constructTablePanel(pnlJars);

        // populate list
        this.viewer.setInput(this);

        // set initial state
        setPageComplete(false);
        setMessage(UTIL.getString(PREFIX + "initialMsg")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#getDialogSettings()
     * @since 6.0.0
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
     * Handler for when the imported jar file is selected/deselected.
     * 
     * @param event the event being processed
     * @since 6.0.0
     */
    void handleJarFileSelected( SelectionEvent event ) {
        TableItem item = (TableItem)event.item;
        String name = (String)item.getData();

        if (event.detail == SWT.CHECK) {
            this.importer.selectJarFile(name, item.getChecked());
            updateState();
        }
    }

    /**
     * Handler for when the browse button is clicked.
     * 
     * @since 6.0.0
     */
    void handleBrowse() {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
        dialog.setText(UTIL.getString(PREFIX + "browseDialogtitle")); //$NON-NLS-1$
        dialog.setFilterExtensions(UdfModelUtil.FILE_EXTENSIONS);
        dialog.setFilterNames(UdfModelUtil.FILE_EXTENSION_NAMES);

        // set starting directory if necessary
        String currentPath = this.importer.getSourcePath();

        if (currentPath != null) {
            int index = currentPath.lastIndexOf(File.separator);

            if (index != -1) {
                dialog.setFilterPath(currentPath.substring(0, index));
            }
        }

        String path = dialog.open();

        // modify MRU history if new zip file name selected
        if (path != null) {
            // make sure file extension exists
            if (!FileUtil.isZipFileName(path)) {
                path += Extensions.ZIP;
            }

            ArrayList items = new ArrayList(Arrays.asList(this.cbxZipFileName.getItems()));

            if (!items.contains(path)) {
                items.add(path);
                WidgetUtil.setComboItems(this.cbxZipFileName, items, null, false, path);
            } else {
                this.cbxZipFileName.setText(path);
            }
        }

        updateState();
    }

    /**
     * Handler for when the input file name has been changed.
     * 
     * @since 6.0.0
     */
    void handleInputFileModified() {
        this.importer.setSourcePath(this.cbxZipFileName.getText());
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.internal.wizard.AbstractWizardPage#saveSettings()
     * @since 6.0.0
     */
    @Override
    public void saveSettings() {
        IDialogSettings settings = getDialogSettings();
        WidgetUtil.saveSettings(settings, DialogSettingsProperties.LOCATIONS, this.cbxZipFileName);
        WidgetUtil.removeMissingResources(settings, DialogSettingsProperties.LOCATIONS);
    }

    /**
     * Refreshes the UI to reflect the current state of the business object.
     * 
     * @since 6.0.0
     */
    void updateState() {
        this.viewer.refresh();
        WidgetUtil.pack(this.viewer.getTable(), 10);

        // update checkboxes to reflect current state of business object
        int numImported = 0;

        for (TableItem item : this.viewer.getTable().getItems()) {
            boolean checked = this.importer.isJarFileBeingImported((String)item.getData());
            item.setChecked(checked);

            if (checked) {
                ++numImported;
            }
        }

        // set enablement for the table label
        boolean enable = (this.importer.getJarFileNames().size() != 0);

        if (this.lblIncludedJars.getEnabled() != enable) {
            this.lblIncludedJars.setEnabled(enable);
            this.viewer.getTable().setEnabled(enable);
        }

        IStatus status = this.importer.isValidZipFile();
        setPageComplete(status.isOK());

        if (isPageComplete()) {
            setErrorMessage(null);
            if (numImported == 0) {
                setMessage(UTIL.getString(PREFIX + "noArchivesImported")); //$NON-NLS-1$
            } else {
                setMessage(UTIL.getString(PREFIX + "archivesBeingImported", numImported)); //$NON-NLS-1$
            }

            // this.viewer.refresh();
        } else {
            setErrorMessage(status.getMessage());
        }
    }

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    /**
     * The <code>TypeContentProvider</code> provides the available connector types as content.
     * 
     * @since 6.0.0
     */
    class UdfJarFileContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return accessImporter().getJarFileNames().toArray();
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

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    /**
     * @since 6.0.0
     */
    class UdfJarFileLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object element,
                                     int columnIndex ) {
            if (columnIndex == ColumnIndexes.NAME) {
                return element.toString();
            }

            if (columnIndex == ColumnIndexes.ACTION) {
                String jarName = element.toString();

                // jar file is being imported
                if (accessImporter().isJarFileBeingImported(jarName)) {
                    if (accessImporter().existsInWorkspace(jarName)) {
                        return IMPORTING_OVERWRITE_MSG;
                    }

                    return IMPORT_MSG;
                }

                // jar file is not being imported
                return NOT_IMPORTING_MSG;
            }

            return StringUtil.Constants.EMPTY_STRING;
        }
    }

}

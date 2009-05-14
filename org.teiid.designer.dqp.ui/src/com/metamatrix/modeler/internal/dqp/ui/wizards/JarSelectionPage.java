/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.FileSystemContentProvider;
import com.metamatrix.ui.internal.viewsupport.FileSystemLabelProvider;

/**
 * The <code>JarSelectionPage</code> class is the last page of the connector importer. This mandatory page is where the user
 * selects where the required jar should be imported from.
 * 
 * @since 5.5.3
 */
public final class JarSelectionPage extends WizardPage implements DqpUiConstants {

    /**
     * Table column indexes.
     * 
     * @since 5.5.3
     */
    private interface ColumnIndexes {
        int NAME = 0;
        int STATUS = 1;
        int PATH = 2;
    }

    /**
     * Dialog setting properties that keep track of the last used directory where jar files were imported from.
     * 
     * @since 5.5.3
     */
    private interface DialogSettingsProperties {
        String PAGE_SECTION = JarSelectionPage.class.getSimpleName();
        String LOCATION = "location"; //$NON-NLS-1$
    }

    /**
     * Table headers used to create the table columns.
     * 
     * @since 5.5.3
     */
    private static final String[] HEADERS;

    /**
     * Status message indicating the a jar that is not required has been sel selected in the jar selection dialog.
     * 
     * @since 5.5.3
     */
    static final IStatus INVALID_JAR_SELECTION = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK,
                                                            I18n.InvalidJarSelection, null);

    /**
     * Status message indicating more than one jar (table row) has been selected. This is displayed in the details area.
     * 
     * @since 5.5.3
     */
    private static final IStatus MULTIPLE_JARS_SELECTED = new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK,
                                                                     I18n.MultipleJarsSelectedMsg, null);

    /**
     * Status message indicating there is nothing selected in the jar selection dialog.
     * 
     * @since 5.5.3
     */
    static final IStatus NO_JAR_SELECTION = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, I18n.NoJarSelection,
                                                       null);

    /**
     * Status message indicating that all the jars selected in the jar chooser dialog are valid choices.
     * 
     * @since 5.5.3
     */
    static final IStatus VALID_JAR_SELECTION = new Status(IStatus.OK, DqpUiConstants.PLUGIN_ID, IStatus.OK,
                                                          I18n.ValidJarSelection, null);

    static {
        HEADERS = new String[3];
        HEADERS[ColumnIndexes.NAME] = I18n.NameHeader;
        HEADERS[ColumnIndexes.STATUS] = I18n.StatusHeader;
        HEADERS[ColumnIndexes.PATH] = I18n.PathHeader;
    }

    /**
     * The button whose backing action allows the user to browse the file system and find required jars.
     * 
     * @since 5.5.3
     */
    private Button btnBrowse;

    /**
     * The button whose backing action allows the user to reset the selected table row jars import path back to the path
     * indicating the current workspace configuration jar should be used.
     * 
     * @since 5.5.3
     */
    private Button btnUseExistingJar;

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
     * The table containing the required jars that must be imported.
     * 
     * @since 5.5.3
     */
    private TableViewer viewer;

    /**
     * @param helper the business object
     * @since 5.5.3
     */
    public JarSelectionPage( ConnectorImportHelper helper ) {
        super(JarSelectionPage.class.getSimpleName());
        this.helper = helper;
        setTitle(I18n.ArchiveSelectionPageTitle);
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
     * Constructs the table containing the required jars that must be imported.
     * 
     * @param parent the UI parent of the table panel
     * @since 5.5.3
     */
    private void constructTablePanel( Composite parent ) {
        Composite pnl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION;
        this.viewer = WidgetFactory.createTableViewer(pnl, style);
        this.viewer.setContentProvider(new JarContentProvider());
        this.viewer.setLabelProvider(new JarLabelProvider());

        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleJarSelected(event);
            }
        });

        // create columns
        WidgetFactory.createTableColumns(table, HEADERS, SWT.LEFT);
        table.getColumn(ColumnIndexes.STATUS).setResizable(false);

        // populate the table
        this.viewer.setInput(this);

        //
        // construct button panel
        //

        Composite buttonPanel = WidgetFactory.createPanel(pnl, SWT.NONE, GridData.HORIZONTAL_ALIGN_END, 1, 2);

        // add browse to select jars
        this.btnBrowse = WidgetFactory.createButton(buttonPanel, I18n.BrowseJarFile);
        this.btnBrowse.setToolTipText(I18n.BrowseJarFileToolTip);
        this.btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowse();
            }
        });
        WidgetUtil.setLayoutData(this.btnBrowse);

        // add reset to use existing jar in configuration
        this.btnUseExistingJar = WidgetFactory.createButton(buttonPanel, I18n.UseExistingJar);
        this.btnUseExistingJar.setEnabled(false);
        this.btnUseExistingJar.setToolTipText(I18n.UseExistingJarToolTip);
        this.btnUseExistingJar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleUseExistingJar();
            }
        });
        WidgetUtil.setLayoutData(this.btnUseExistingJar);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        Composite mainControl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        setControl(mainControl);

        // put a splitter between the table and the import details
        SashForm splitter = WidgetFactory.createSplitter(mainControl, SWT.VERTICAL, GridData.FILL_BOTH);

        // construct table
        constructTablePanel(splitter);

        // create details control
        this.detailsArea = new DetailsAreaPanel(splitter);

        // position the splitter
        splitter.setWeights(new int[] {7, 3});
        splitter.layout();

        // set initial state
        refresh();
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
     * Handler for when the browse button is clicked. A file chooser is displayed and only jars with names matching those of
     * required jars are allowed to be selected.
     * 
     * @since 5.5.3
     */
    void handleBrowse() {
        JarSelectionDialog dialog = new JarSelectionDialog(getShell(), this.helper.getAllRequiredExtensionJarNames());
        dialog.setInput(dialog); // doesn't matter what the input is as the file system roots will always be used

        // set directory to last used directory
        String directory = getDialogSettings().get(DialogSettingsProperties.LOCATION);

        if (!StringUtil.isEmpty(directory)) {
            dialog.setInitialSelection(new File(directory));
        }

        // show dialog
        if (dialog.open() == Window.OK) {
            File jarFile = null;

            // for each jar selected set it's path on the business object
            for (Object file : dialog.getResult()) {
                jarFile = (File)file;
                this.helper.setRequiredJarPath(jarFile.getName(), jarFile.getParent());
                this.viewer.refresh(jarFile.getName(), true); // update the jar's table row to show new path
            }

            // save location for next time by using the last jar selected
            IDialogSettings settings = getDialogSettings();
            settings.put(DialogSettingsProperties.LOCATION, jarFile.getParent());

            // reselect so that details and button state
            Event event = new Event();
            event.widget = this.viewer.getTable();
            event.data = this.viewer.getTable().getSelection();
            handleJarSelected(new SelectionEvent(event));

            // update page state
            updateState();
        }
    }

    /**
     * Handler for when a table row is selected.
     * 
     * @since 5.5.3
     */
    void handleJarSelected( SelectionEvent event ) {
        IStatus status = null;
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enablement for the use existing jar in configuration button
        boolean enable = false;

        if (!selection.isEmpty()) {
            int numSelected = selection.size();

            if (numSelected != 1) {
                status = MULTIPLE_JARS_SELECTED;

                if (this.helper.canOverwriteWorkspaceConfigJars()) {
                    enable = true;
                    boolean allUsingExistingJar = true;

                    // make sure all jars currently exist in the configuration
                    for (Object jar : selection.toArray()) {
                        String jarName = (String)jar;

                        // once we find one jar that doesn't exist in the configuration we disable button
                        if (!this.helper.jarExistsInConfiguration(jarName)) {
                            enable = false;
                            break;
                        }

                        // no need to enable if all are already set to use the existing jar
                        if (allUsingExistingJar) {
                            allUsingExistingJar = !isExistingJarBeingOverwritten(jarName);
                        }
                    }

                    if (enable) {
                        enable = !allUsingExistingJar;
                    }
                }
            } else {
                // only one jar is selected
                String jarName = (String)selection.getFirstElement();
                status = this.helper.getRequiredExtensionJarStatus(jarName);
                enable = (this.helper.canOverwriteWorkspaceConfigJars() && isExistingJarBeingOverwritten(jarName));
            }
        }

        // update details panel and button enablement
        this.detailsArea.setStatus(status);
        this.btnUseExistingJar.setEnabled(enable);
    }

    /**
     * Handler for when the use existing jar in configuration button is pressed. Pre-condition: only called when all the selected
     * jar names exist in the configuration.
     * 
     * @since 5.5.3
     */
    void handleUseExistingJar() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        for (Object jarFile : selection.toArray()) {
            this.helper.setRequiredJarPath((String)jarFile, ConnectorImportHelper.USE_WSCONFIG_JAR);
            this.viewer.refresh(jarFile);
        }

        // reselect so that details and button state
        Event event = new Event();
        event.widget = this.viewer.getTable();
        event.data = this.viewer.getTable().getSelection();
        handleJarSelected(new SelectionEvent(event));
    }

    /**
     * @param jarName the jar name being checked
     * @return <code>true</code> if the specified jar name is an existing jar and is currently being overwritten by the import
     */
    private boolean isExistingJarBeingOverwritten( String jarName ) {
        if (this.helper.jarExistsInConfiguration(jarName)) {
            if (this.helper.getRequiredJarPath(jarName) == null) {
                return true;
            } else if (!this.helper.getRequiredJarPath(jarName).equals(ConnectorImportHelper.USE_WSCONFIG_JAR)) {
                return true;
            }
        }

        // not being overwritten or not an existing jar
        return false;
    }

    /**
     * Updates UI controls.
     * 
     * @since 5.5.3
     */
    private void refresh() {
        this.viewer.refresh();
        WidgetUtil.pack(this.viewer.getTable(), 10);

        // auto-select first row
        Table table = this.viewer.getTable();

        if (this.viewer.getSelection().isEmpty() && (table.getItemCount() != 0)) {
            table.select(0);
            Event event = new Event();
            event.widget = table;
            table.notifyListeners(SWT.Selection, event);
        }

        // update page state
        updateState();
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

        if (visible) {
            refresh();

            // set browse button enablement
            this.btnBrowse.setEnabled(this.helper.canOverwriteWorkspaceConfigJars());
        }
    }

    /**
     * Update page messages and button state.
     * 
     * @since 5.5.3
     */
    public void updateState() {
        // complete if all jars accounted for
        boolean complete = this.helper.getUnmappedExtensionJarNames().isEmpty();

        setErrorMessage(null);
        if (complete) {
            // no required jars need to be imported
            if (this.helper.getAllRequiredExtensionJarNames().isEmpty()) {
                Object[] bindings = new Object[2];
                bindings[0] = this.helper.getSelectedImportFileConnectorTypes().size();
                bindings[1] = this.helper.getSelectedImportFileConnectors().size();
                setMessage(NLS.bind(I18n.NoJarsRequiredMsg, bindings));
            } else {
                int numNewJars = 0;

                // all required jars have import paths identified,
                // need to see if any are already in configuration and not being overwritten
                for (String jarName : this.helper.getAllRequiredExtensionJarNames()) {
                    String path = this.helper.getRequiredJarPath(jarName);
                    // Normal path - not connector_patch.jar
                    if (!DqpExtensionsHandler.CONNECTOR_PATCH_JAR.equals(jarName)) {
                        if (!ConnectorImportHelper.USE_WSCONFIG_JAR.equals(path)) {
                            ++numNewJars;
                        }
                        // connector_patch.jar - if path is set, increment number
                    } else if (path != null) {
                        ++numNewJars;
                    }
                }

                Object[] bindings = new Object[3];
                bindings[0] = this.helper.getSelectedImportFileConnectorTypes().size();
                bindings[1] = this.helper.getSelectedImportFileConnectors().size();
                bindings[2] = numNewJars;

                setMessage(NLS.bind(I18n.AllJarsAccountedForMsg, bindings));
            }
        } else {
            setMessage(NLS.bind(I18n.MissingJarFilesMsg, this.helper.getUnmappedExtensionJarNames().size()),
                       IMessageProvider.ERROR);
        }

        setPageComplete(complete);
    }

    /**
     * The <code>JarContentProvider</code> provides the required jar names as content.
     * 
     * @since 5.5.3
     */
    class JarContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return accessHelper().getAllRequiredExtensionJarNames().toArray();
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
    class JarLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            if (columnIndex == ColumnIndexes.STATUS) {
                String jarName = (String)element;
                IStatus status = accessHelper().getRequiredExtensionJarStatus(jarName);
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
            String jarName = (String)element;

            if (columnIndex == ColumnIndexes.NAME) {
                return jarName;
            }

            if (columnIndex == ColumnIndexes.PATH) {
                String path = accessHelper().getRequiredJarPath(jarName);

                if (!StringUtil.isEmpty(path)) {
                    if (path.equals(ConnectorImportHelper.USE_WSCONFIG_JAR)) {
                        path = I18n.JarExistsInConfigurationPath;
                    }

                    if (path.equals(ConnectorImportHelper.USE_CAF_JAR)) {
                        path = I18n.JarExistsInCaf;
                    }
                }

                return (StringUtil.isEmpty(path) ? StringUtil.Constants.EMPTY_STRING : path);
            }

            return StringUtil.Constants.EMPTY_STRING;
        }
    }

    /**
     * The <code>JarSelectionDialog</code> is a file system chooser that allows the user to select only jars having names matching
     * those of the required jars.
     * 
     * @since 5.5.3
     */
    class JarSelectionDialog extends ElementTreeSelectionDialog {

        /**
         * @param parent
         * @param labelProvider
         * @param contentProvider
         */
        public JarSelectionDialog( Shell parent,
                                   Collection<String> jarNames ) {
            super(parent, new FileSystemLabelProvider(), new FileSystemContentProvider());
            setTitle(I18n.JarFileBrowseDialogTitle);
            setDefaultImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.IMPORT_JAR_ICON));
            setAllowMultiple(jarNames.size() > 1);
            setValidator(new JarSelectionValidator(jarNames));
            setComparator(new ViewerComparator() {
                @Override
                public int category( Object element ) {
                    if (element instanceof File) {
                        if (((File)element).isFile()) {
                            return 1;
                        }
                    }

                    // sort folders first
                    return 0;
                }
            });
            addFilter(new ViewerFilter() {
                @Override
                public boolean select( Viewer viewer,
                                       Object parentElement,
                                       Object element ) {
                    if (element instanceof File) {
                        File file = (File)element;

                        if (file.isFile()) {
                            return FileUtil.isArchiveFileName(file.getName(), false);
                        }
                        return true;
                    }

                    return false;
                }
            });

            // construct description
            StringBuffer listOfJars = new StringBuffer();
            List<String> jars = new ArrayList<String>(jarNames);

            for (int size = jarNames.size(), i = 0; i < size; ++i) {
                if (i != 0) {
                    listOfJars.append(", "); //$NON-NLS-1$
                }

                listOfJars.append(jars.get(i));
            }

            setMessage(NLS.bind(I18n.JarFileBrowseDialogDescription, listOfJars));
        }
    }

    /**
     * Makes sure only jars with names matching the required jar names are valid.
     * 
     * @since 5.5.3
     */
    static class JarSelectionValidator implements ISelectionStatusValidator {
        private final boolean multiSelect;
        private final Collection<String> jarNames;

        public JarSelectionValidator( Collection<String> jarNames ) {
            this.jarNames = jarNames;
            this.multiSelect = (jarNames.size() > 1);
        }

        /**
         * @param jarName the jar name being checked
         * @return <code>true</code> if the jar name matches a required jar name
         * @since 5.5.3
         */
        private boolean isValidJar( String jarName ) {
            for (String validName : this.jarNames) {
                if (validName.equals(jarName)) {
                    return true;
                }
            }

            return false;
        }

        public IStatus validate( Object[] selection ) {
            int numSelected = selection.length;

            if (numSelected == 0) {
                return NO_JAR_SELECTION;
            }

            if (((numSelected > 1) && !this.multiSelect)) {
                return INVALID_JAR_SELECTION;
            }

            for (int i = 0; i < selection.length; i++) {
                Object obj = selection[i];

                if (obj instanceof File) {
                    File file = (File)obj;

                    if (!file.isFile() || !isValidJar(file.getName())) {
                        return INVALID_JAR_SELECTION;
                    }
                }
            }

            return VALID_JAR_SELECTION;
        }
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.internal.jdbc.ui.ModelerJdbcUiPlugin;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.0
 */
public final class JdbcDriverWizard extends AbstractWizard
    implements InternalModelerJdbcUiPluginConstants, InternalModelerJdbcUiPluginConstants.Widgets, InternalUiConstants.Widgets,
    ListPanel.Constants, StringUtil.Constants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcDriverWizard.class);

    private static final String ADD_EXTERNAL_LIBRARY_DIALOG_TITLE = getString("addExternalLibraryDialogTitle"); //$NON-NLS-1$
    private static final String ADD_LIBRARY_DIALOG_TITLE = getString("addLibraryDialogTitle"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;

    private static final String ADD_EXTERNAL_BUTTON = getString("addExternalButton"); //$NON-NLS-1$
    private static final String DRIVERS_GROUP = getString("driversGroup"); //$NON-NLS-1$
    private static final String LIBRARIES_GROUP = getString("librariesGroup"); //$NON-NLS-1$
    private static final String UPDATE_BUTTON = getString("updateButton"); //$NON-NLS-1$
    private static final String UPDATE_LABEL = getString("updateLabel"); //$NON-NLS-1$

    private static final String ADD_LIBRARY_DIALOG_MESSAGE = getString("addLibraryDialogMessage"); //$NON-NLS-1$
    private static final String DUPLICATE_DRIVER_MESSAGE = getString("duplicateDriverMessage"); //$NON-NLS-1$
    static final String FINDING_CLASSES_MESSAGE = getString("findingClassesMessage"); //$NON-NLS-1$
    static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String REFERENCED_DRIVERS_MESSAGE = getString("referencedDriversMessage"); //$NON-NLS-1$
    private static final String UPDATE_CLASS_NAME_MESSAGE = getString("updateClassNameMessage", UPDATE_LABEL); //$NON-NLS-1$

    private static final String REFERENCED_DRIVERS_MESSAGE_SEPARATOR = "\n\n"; //$NON-NLS-1$
    private static final String REFERENCED_DRIVERS_PREFIX = "\t"; //$NON-NLS-1$

    private static final String JAR_EXTENSION = ".jar"; //$NON-NLS-1$
    private static final String ZIP_EXTENSION = ".zip"; //$NON-NLS-1$

    private static final String LIBRARY_FILTER = '*' + JAR_EXTENSION + "; " + '*' + ZIP_EXTENSION; //$NON-NLS-1$

    private static final String[] LIBRARY_FILTER_EXTENSIONS = new String[] {LIBRARY_FILTER};
    private static final String[] LIBRARY_FILTER_NAMES = new String[] {getString("libraryFilterName") + " (" + LIBRARY_FILTER + ')'}; //$NON-NLS-1$ //$NON-NLS-2$

    private static final boolean DRIVERS_ONLY = true;
    static final Image ERROR_ICON = JFaceResources.getImage(org.eclipse.jface.dialogs.Dialog.DLG_IMG_MESSAGE_ERROR);
    static final Image WARNING_ICON = JFaceResources.getImage(org.eclipse.jface.dialogs.Dialog.DLG_IMG_MESSAGE_WARNING);
    static final Image INFO_ICON = JFaceResources.getImage(org.eclipse.jface.dialogs.Dialog.DLG_IMG_MESSAGE_INFO);

    static final String NEW_DRIVER_NAME = getString("newDriverName"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    private static String getString( final String id,
                                     final Object parameter ) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    JdbcManager mgr;
    JdbcDriver driver;

    private WizardPage pg;
    private ListPanel driverPanel, libraryPanel;
    private Composite editPanel;
    private Text nameText, urlSyntaxText;
    private Combo classNameCombo;
    private Button updateButton;
    private Map enableMap;

    /**
     * @since 4.0
     */
    public JdbcDriverWizard() {
        super(ModelerJdbcUiPlugin.getDefault(), TITLE, null);
        // Save reference to JDBC manager
        this.mgr = JdbcUiUtil.getJdbcManager();
        // Return if manager failed to get created
        if (this.mgr == null) {
            return;
        }
        // Create page
        this.pg = new WizardPage(JdbcDriverWizard.class.getSimpleName(), PAGE_TITLE, null) {

            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };
        // Initialize page
        this.pg.setMessage(INITIAL_MESSAGE);
        this.pg.setPageComplete(false);
        // Add page to wizard
        addPage(this.pg);
    }

    /**
     * @since 4.0
     */
    Object[] addDriver() {
        this.driver = this.mgr.getFactory().createJdbcDriver();
        this.driver.setName(NEW_DRIVER_NAME);
        this.mgr.getJdbcDrivers().add(this.driver);
        this.driverPanel.addItem(this.driver);
        this.nameText.setFocus();
        this.nameText.selectAll();
        validateAllDrivers();
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @since 4.0
     */
    void addExternalLibraries() {
        // Display file dialog for user to choose libraries
        final FileDialog dlg = new FileDialog(getShell(), SWT.MULTI);
        dlg.setText(ADD_EXTERNAL_LIBRARY_DIALOG_TITLE);
        dlg.setFilterNames(LIBRARY_FILTER_NAMES);
        dlg.setFilterExtensions(LIBRARY_FILTER_EXTENSIONS);
        dlg.open();
        // Add selected libraries to driver's list of URL's
        final String[] names = dlg.getFileNames();
        if (names == null) {
            return;
        }
        final List urls = this.driver.getJarFileUris();
        final int count = urls.size();
        final String path = dlg.getFilterPath();
        for (int ndx = 0; ndx < names.length; ++ndx) {
            final String url = new File(path, names[ndx]).toURI().toString();
            if (!urls.contains(url)) {
                urls.add(url);
                this.libraryPanel.getTableViewer().add(url);
            }
        }
        if (urls.size() != count) {
            enableClassNameButton();
        }
    }

    /**
     * @since 4.0
     */
    Object[] addLibraries() {
        final ElementTreeSelectionDialog dlg = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
                                                                              new WorkbenchContentProvider());
        dlg.setTitle(ADD_LIBRARY_DIALOG_TITLE);
        dlg.setMessage(ADD_LIBRARY_DIALOG_MESSAGE);
        dlg.setComparator(new ResourceComparator(ResourceComparator.NAME));
        dlg.setValidator(new ISelectionStatusValidator() {

            public IStatus validate( final Object[] selection ) {
                for (int ndx = selection.length; --ndx >= 0;) {
                    if (!(selection[ndx] instanceof IFile)) {
                        return new Status(IStatus.ERROR, PLUGIN_ID, 0, "Only files may be selected.", null); //$NON-NLS-1$
                    }
                }
                return new Status(IStatus.OK, PLUGIN_ID, 0, EMPTY_STRING, null);
            }
        });
        dlg.addFilter(new ViewerFilter() {

            @Override
            public boolean select( final Viewer viewer,
                                   final Object parent,
                                   final Object element ) {
                if (element instanceof IContainer) {
                    return true;
                }
                if (!(element instanceof IFile)) {
                    return false;
                }
                final String name = ((IFile)element).getName();
                return (name.endsWith(JAR_EXTENSION) || name.endsWith(ZIP_EXTENSION));
            }
        });
        dlg.setInput(ResourcesPlugin.getWorkspace().getRoot());
        if (dlg.open() == Window.OK) {
            final Object[] archives = dlg.getResult();
            final String[] selectedUrls = new String[archives.length];
            final List urls = this.driver.getJarFileUris();
            final int count = urls.size();
            for (int ndx = 0; ndx < archives.length; ++ndx) {
                // final String url = ((IFile)archives[ndx]).getLocation().toString();
                final String url = new File(((IFile)archives[ndx]).getLocation().toString()).toURI().toString();
                selectedUrls[ndx] = url;
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            }
            if (urls.size() != count) {
                enableClassNameButton();
            }
            return selectedUrls;
        }
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @since 4.0
     */
    void classNameModified( final ModifyEvent event ) {
        final Combo combo = (Combo)event.widget;
        if (this.driver != null) {
            this.driver.setPreferredDriverClassName(combo.getText());
            validateDriver();
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    Composite createPageControl( final Composite parent ) {
        // Create page
        final Composite pg = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        IListPanelController ctrlr = new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addDriver();
            }

            @Override
            public void itemsSelected( final IStructuredSelection selection ) {
                driversSelected(selection);
            }

            @Override
            public Object[] removeButtonSelected( final IStructuredSelection selection ) {
                return removeDrivers(selection);
            }
        };
        this.driverPanel = new ListPanel(pg, DRIVERS_GROUP, ctrlr, SWT.MULTI);
        ((GridData)this.driverPanel.getLayoutData()).minimumHeight = 150;
        ((GridData)this.driverPanel.getLayoutData()).widthHint = 580;
        final TableViewer viewer = this.driverPanel.getTableViewer();
        viewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText( final Object driver ) {
                if (driver == null) {
                    return EMPTY_STRING;
                }
                return ((JdbcDriver)driver).getName();
            }

            @Override
            public boolean isLabelProperty( final Object driver,
                                            final String property ) {
                return true;
            }

            @Override
            public Image getImage( Object element ) {
                final IStatus status = mgr.isValid((JdbcDriver)element);
                if (status.isOK()) {
                    return null;
                } // endif

                // info, error, warning status:
                switch (status.getSeverity()) {
                    case IStatus.ERROR:
                        return ERROR_ICON;
                    case IStatus.WARNING:
                        return WARNING_ICON;
                    case IStatus.INFO:
                    default:
                        return INFO_ICON;
                } // endswitch
            }
        });
        viewer.setSorter(new ViewerSorter());
        this.editPanel = WidgetFactory.createPanel(pg, SWT.NO_TRIM, GridData.FILL_BOTH, 1, COLUMN_COUNT);
        WidgetFactory.createLabel(this.editPanel, NAME_LABEL);
        this.nameText = WidgetFactory.createTextField(this.editPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.nameText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                nameModified(event);
            }
        });
        WidgetFactory.createLabel(this.editPanel, URL_SYNTAX_LABEL);
        this.urlSyntaxText = WidgetFactory.createTextField(this.editPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.urlSyntaxText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                urlSyntaxModified(event);
            }
        });
        ctrlr = new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addLibraries();
            }

            @Override
            public void downButtonSelected( final IStructuredSelection selection ) {
                moveLibraries(selection, DOWN);
            }

            @Override
            public Object[] removeButtonSelected( final IStructuredSelection selection ) {
                removeLibraries(selection);
                return super.removeButtonSelected(selection);
            }

            @Override
            public void upButtonSelected( final IStructuredSelection selection ) {
                moveLibraries(selection, UP);
            }
        };
        this.libraryPanel = new ListPanel(this.editPanel, LIBRARIES_GROUP, ctrlr, SWT.MULTI, ITEMS_ORDERED,
                                          GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
        ((GridData)this.libraryPanel.getLayoutData()).horizontalSpan = COLUMN_COUNT;
        ((GridData)this.libraryPanel.getLayoutData()).minimumHeight = 180;
        ((GridData)this.libraryPanel.getLayoutData()).widthHint = 580;
        // Add extra buttons to list edit panel (in reverse order that they should appear in wizard)
        // this.libraryPanel.addButton(ADD_EXTERNAL_FOLDER_BUTTON).addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected( final SelectionEvent event ) {
        // addExternalFolder();
        // }
        // });
        this.libraryPanel.addButton(ADD_EXTERNAL_BUTTON).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                addExternalLibraries();
            }
        });
        // this.libraryPanel.addButton(ADD_FOLDER_BUTTON).addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected( final SelectionEvent event ) {
        // addFolder();
        // }
        // });
        WidgetFactory.createLabel(this.editPanel, CLASS_NAME_LABEL);
        this.classNameCombo = WidgetFactory.createCombo(this.editPanel, SWT.READ_ONLY, GridData.FILL_HORIZONTAL);
        this.classNameCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                classNameModified(event);
            }
        });
        this.updateButton = WidgetFactory.createButton(this.editPanel, UPDATE_BUTTON);
        this.updateButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                updateClassNames();
            }
        });
        // Add drivers to driver panel after construction of driver panel and library panel to avoid initial sizing/selection
        // issues.
        ((GridData)this.driverPanel.getLayoutData()).heightHint = this.driverPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        List drivers = this.mgr.getJdbcDrivers();
        if (drivers != null && !drivers.isEmpty()) {
            this.driverPanel.addItems(drivers.toArray());
        }
        // Initialize widgets
        viewer.setSelection(new StructuredSelection(new Object[] {this.driver}));

        return pg;
    }

    /**
     * @since 4.0
     */
    void driversSelected( final IStructuredSelection selection ) {
        this.driver = null;
        this.libraryPanel.clear();
        if (selection.size() != 1) {
            if (this.enableMap == null) {
                this.enableMap = WidgetUtil.disable(this.editPanel);
            }
            this.nameText.setText(EMPTY_STRING);
            this.urlSyntaxText.setText(EMPTY_STRING);
            this.classNameCombo.removeAll();
        } else {
            final JdbcDriver driver = (JdbcDriver)selection.getFirstElement();
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
            this.nameText.setText(driver.getName());
            final String syntax = driver.getUrlSyntax();
            this.urlSyntaxText.setText(syntax == null ? EMPTY_STRING : syntax);
            this.libraryPanel.getTableViewer().add(driver.getJarFileUris().toArray());
            WidgetUtil.setComboItems(this.classNameCombo, driver.getAvailableDriverClassNames());
            WidgetUtil.setComboText(this.classNameCombo, driver.getPreferredDriverClassName());
            this.driver = driver;
            validateAllDrivers();
        }
    }

    /**
     * @since 4.0
     */
    private void enableClassNameButton() {
        this.classNameCombo.setEnabled(false);
        this.updateButton.setEnabled(true);
        this.pg.setMessage(UPDATE_CLASS_NAME_MESSAGE, IMessageProvider.ERROR);
        this.pg.setPageComplete(false);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        return JdbcUiUtil.saveChanges();
    }

    /**
     * @since 4.0
     */
    JdbcDriver getSelection() {
        return this.driver;
    }

    /**
     * @since 4.0
     */
    private void invalidateDriver( final JdbcDriver driver,
                                   final String message ) {
        this.pg.setMessage(message, IMessageProvider.ERROR);
        this.pg.setPageComplete(false);
    }

    /**
     * @since 4.0
     */
    void moveLibraries( final IStructuredSelection selection,
                        final int direction ) {
        final EList urls = this.driver.getJarFileUris();
        final Object[] items = selection.toArray();
        for (int ndx = 0; ndx < items.length; ++ndx) {
            final Object item = items[direction < 0 ? ndx : items.length - ndx - 1];
            final int itemNdx = urls.indexOf(item);
            urls.move(itemNdx + direction, itemNdx);
        }
        enableClassNameButton();
    }

    /**
     * @since 4.0
     */
    void nameModified( final ModifyEvent event ) {
        final Text text = (Text)event.widget;
        if (this.driver != null) {
            this.driver.setName(text.getText().trim());
            this.driverPanel.getTableViewer().update(this.driver, null);
            validateDriver();
        }
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     * @since 4.0
     */
    @Override
    public boolean performCancel() {
        final List connections = new ArrayList(this.mgr.getJdbcSources());

        // Because of a change in EMF (maybe a bug) the reload() method will turn the jdbcSources in this list
        // to EProxy objects. Need to clear the list BEFORE reloading model. This cleanly breaks these objects
        // away from their container preventing the conversion to EProxy's
        this.mgr.getJdbcSources().clear();

        JdbcUiUtil.reload();

        final List loadedConnections = this.mgr.getJdbcSources();
        loadedConnections.clear();
        loadedConnections.addAll(connections);

        return super.performCancel();
    }

    /**
     * @since 4.0
     */
    Object[] removeDrivers( final IStructuredSelection selection ) {
        final List drivers = new ArrayList(selection.size());
        final List refdDrivers = new ArrayList(0);
        final List dbs = this.mgr.getJdbcSources();
        for (final Iterator driverIter = selection.iterator(); driverIter.hasNext();) {
            final JdbcDriver driver = (JdbcDriver)driverIter.next();
            boolean refd = false;
            for (final Iterator dbIter = dbs.iterator(); dbIter.hasNext();) {
                final JdbcSource db = (JdbcSource)dbIter.next();
                if (db.getJdbcDriver() == driver) {
                    refd = true;
                    break;
                }
            }
            if (refd) {
                refdDrivers.add(driver);
            } else {
                drivers.add(driver);
            }
        }
        this.mgr.getJdbcDrivers().removeAll(drivers);
        if (!refdDrivers.isEmpty()) {
            final StringBuffer msg = new StringBuffer(REFERENCED_DRIVERS_MESSAGE + REFERENCED_DRIVERS_MESSAGE_SEPARATOR);
            for (final Iterator iter = refdDrivers.iterator(); iter.hasNext();) {
                msg.append(REFERENCED_DRIVERS_PREFIX);
                msg.append(((JdbcDriver)iter.next()).getName());
            }
            MessageDialog.openWarning(getShell(), WARNING_MESSAGE_TITLE, msg.toString());
        }
        validateAllDrivers();
        return drivers.toArray();
    }

    /**
     * @since 4.0
     */
    void removeLibraries( final IStructuredSelection selection ) {
        final List urls = this.driver.getJarFileUris();
        urls.removeAll(selection.toList());
        enableClassNameButton();
    }

    /**
     * @since 4.0
     */
    void setSelection( final JdbcDriver driver ) {
        this.driver = driver;
    }

    /**
     * @since 4.0
     */
    void updateClassNames() {
        // Compute available class names
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                monitor.setTaskName(FINDING_CLASSES_MESSAGE);
                try {
                    JdbcDriverWizard.this.mgr.computeAvailableDriverClasses(JdbcDriverWizard.this.driver, DRIVERS_ONLY);
                } catch (final JdbcException err) {
                    throw new InvocationTargetException(err);
                }
            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(false, false, op);
            // Update class name combo, preserving any previous selection
            WidgetUtil.setComboItems(this.classNameCombo, this.driver.getAvailableDriverClassNames());
            this.updateButton.setEnabled(false);
            this.classNameCombo.setEnabled(this.classNameCombo.getItemCount() > 0);
            validateDriver();
        } catch (final Exception err) {
            Util.log(err);
            this.pg.setErrorMessage(err.getMessage());
        }
    }

    /**
     * @since 4.0
     */
    void urlSyntaxModified( final ModifyEvent event ) {
        final Text text = (Text)event.widget;
        if (this.driver != null) {
            this.driver.setUrlSyntax(text.getText().trim());
            validateDriver();
        }
    }

    /**
     * @since 4.0
     */
    private void validateAllDrivers() {
        // See if there are any errors on any of the drivers ...
        IStatus currentDriverStatus = null;
        for (final Iterator iter = this.mgr.getJdbcDrivers().iterator(); iter.hasNext();) {
            final JdbcDriver driver = (JdbcDriver)iter.next();

            // Make sure there is not more than 1 driver with the same name ...
            final JdbcDriver[] drivers = this.mgr.findDrivers(driver.getName());
            if (drivers.length > 1) {
                invalidateDriver(driver, DUPLICATE_DRIVER_MESSAGE);
            }
            // Validate the driver
            final IStatus status = this.mgr.isValid(driver);
            if (driver == this.driver) {
                // This is the current driver, so save the status ...
                currentDriverStatus = status;
            }
            if (!status.isOK()) {
                if (status.getSeverity() == IStatus.ERROR) {
                    // Found an error!
                    invalidateDriver(driver, status.getMessage());
                }
            }
        }

        // analyze the status we have:
        if (currentDriverStatus == null) {
            // There are no drivers ...
            pg.setMessage(VALID_DIALOG_MESSAGE);
            pg.setPageComplete(true);
        } else {
            if (currentDriverStatus.isOK()) {
                // Override the "valid" message ...
                pg.setMessage(VALID_DIALOG_MESSAGE);
                pg.setPageComplete(true);
            } else {
                // Post the message for the current driver ...
                final int wizSev;
                int statusSev = currentDriverStatus.getSeverity();
                switch (statusSev) {
                    case IStatus.WARNING:
                        wizSev = IMessageProvider.WARNING;
                        break;
                    case IStatus.ERROR:
                        wizSev = IMessageProvider.ERROR;
                        break;
                    case IStatus.INFO:
                    default:
                        wizSev = IMessageProvider.INFORMATION;
                        break;
                } // endswitch

                // see if next allowed:
                if (statusSev != IStatus.ERROR) {
                    // not error, allow:
                    pg.setPageComplete(true);
                } // endif

                pg.setMessage(currentDriverStatus.getMessage(), wizSev);
            }
        }
    }

    /**
     * @since 4.0
     */
    private void validateDriver() {
        IStatus status = null;
        if (this.driver != null) {
            // Validate the driver
            status = this.mgr.isValid(driver);
            // This is the current driver, so save the status ...
            if (!status.isOK()) {
                if (status.getSeverity() == IStatus.ERROR) {
                    // Found an error!
                    invalidateDriver(driver, status.getMessage());
                    return;
                }
            }
        }

        // We've found no errors so far
        if (status == null) {
            // There are no drivers ...
            this.pg.setMessage(VALID_DIALOG_MESSAGE);
        } else {
            if (status.isOK()) {
                // Override the "valid" message ...
                this.pg.setMessage(VALID_DIALOG_MESSAGE);
            } else {
                // Post the message for the current driver ...
                final int severity = (status.getSeverity() == IStatus.WARNING ? IMessageProvider.WARNING : IMessageProvider.INFORMATION);
                this.pg.setMessage(status.getMessage(), severity);
            }
        }
        this.pg.setPageComplete(true);
    }

    /**
     * @since 4.0
     */
    ListPanel testGetDriverPanel() {
        return this.driverPanel;
    }

    /**
     * @since 4.0
     */
    JdbcManager testGetJdbcManager() {
        return this.mgr;
    }
}

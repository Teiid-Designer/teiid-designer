/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.internal.jdbc.ui.ModelerJdbcUiPlugin;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.dialog.AbstractPasswordDialog;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * @since 4.0
 */
public class JdbcSourceWizard extends AbstractWizard
    implements ListPanel.Constants, InternalModelerJdbcUiPluginConstants, InternalModelerJdbcUiPluginConstants.Widgets,
    InternalUiConstants.Widgets, StringUtil.Constants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcSourceWizard.class);

    private static final String JDBC_LOAD_CONTAINER_NAME = "JDBC_LOAD_CONTAINER"; //$NON-NLS-1$

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;

    private static final String SOURCES_GROUP = getString("sourcesGroup"); //$NON-NLS-1$
    private static final String DRIVERS_BUTTON = getString("driversButton"); //$NON-NLS-1$
    private static final String DRIVERS_BUTTON_TOOLTIP = getString("driversButtonTooltip"); //$NON-NLS-1$

    private static final String PROPERTIES_BUTTON = getString("propertiesButton"); //$NON-NLS-1$
    private static final String PROPERTIES_TOOLTIP = getString("propertiesButtonTooltip"); //$NON-NLS-1$
    private static final String TEST_BUTTON = getString("testButton"); //$NON-NLS-1$

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String DUPLICATE_SOURCE_MESSAGE = getString("duplicateSourceMessage"); //$NON-NLS-1$
    static final String CONNECTION_SUCCEEDED_MESSAGE = getString("connectionSucceededMessage"); //$NON-NLS-1$

    private static final String NEW_SOURCE_NAME = getString("newSourceName"); //$NON-NLS-1$

    private static final String ADD_BUTTON_TEXT = getString("addSourceButton"); //$NON-NLS-1$
    private static final String ADD_BUTTON_TOOLTIP = getString("addSourceButton.tip"); //$NON-NLS-1$
    private static final String SAVE_BUTTON = getString("saveSourcesButton"); //$NON-NLS-1$
    private static final String LOAD_BUTTON = getString("loadSourcesButton"); //$NON-NLS-1$
    private static final String SAVE_BUTTON_TOOLTIP = getString("saveSourcesTooltip"); //$NON-NLS-1$
    private static final String LOAD_BUTTON_TOOLTIP = getString("loadSourcesTooltip"); //$NON-NLS-1$

    private static final String SAVE_SOURCES_DIALOG_TITLE = getString("saveSourcesDialogTitle"); //$NON-NLS-1$
    private static final String LOAD_SOURCES_DIALOG_TITLE = getString("loadSourcesDialogTitle"); //$NON-NLS-1$
    static final String SAVE_SOURCES_ERROR_MESSAGE = getString("saveSourcesErrorMessage"); //$NON-NLS-1$
    static final String LOAD_SOURCES_ERROR_TITLE = getString("loadSourcesErrorTitle"); //$NON-NLS-1$
    static final String LOAD_SOURCES_ERROR_MESSAGE = getString("loadSourcesErrorMessage"); //$NON-NLS-1$
    static final String LOAD_SOURCES_FILE_ERROR_MESSAGE = getString("loadSourcesFileErrorMessage"); //$NON-NLS-1$
    static final String SAVE_SOURCES_OVERWRITE_TITLE = getString("saveSourcesOverwriteFileTitle"); //$NON-NLS-1$
    static final String SAVE_SOURCES_OVERWRITE_MESSAGE = getString("saveSourcesOverwriteFileMessage"); //$NON-NLS-1$
    static final String SAVE_SOURCES_READONLY_TITLE = getString("saveSourcesReadonlyFileTitle"); //$NON-NLS-1$
    static final String SAVE_SOURCES_READONLY_MESSAGE = getString("saveSourcesReadonlyFileMessage"); //$NON-NLS-1$
    private static final String SOURCE_FILE_EXTENSION = getString("sourceFileExtension"); //$NON-NLS-1$
    private static final String SOURCE_FILE_FILTER = getString("sourceFileFilter"); //$NON-NLS-1$
    static final String SAVE_SOURCES_CONFIRMATION_TITLE = getString("saveSourcesConfirmationTitle"); //$NON-NLS-1$
    static final String SAVE_SOURCES_CONFIRMATION_MESSAGE = getString("saveSourcesConfirmationMessage"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    JdbcManager mgr;
    JdbcSource src;

    private WizardPage pg;
    ListPanel srcPanel;
    private Composite editPanel;
    private Text nameText, userText;
    private LabelProvider driverLabelProvider;
    private Combo driverCombo, urlCombo;
    private CLabel urlSyntaxLabel;
    private Button testButton;
    private Map enableMap;
    private Button propertiesButton;
    String pwd;

    /**
     * @since 4.0
     */
    public JdbcSourceWizard() {
        super(ModelerJdbcUiPlugin.getDefault(), TITLE, null);
        // Save reference to JDBC manager
        this.mgr = JdbcUiUtil.getJdbcManager();
        // Return if manager failed to get created
        if (this.mgr == null) {
            return;
        }
        // Create page
        this.pg = new WizardPage(JdbcSourceWizard.class.getSimpleName(), PAGE_TITLE, null) {

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
    Object[] addSource() {
        this.src = this.mgr.getFactory().createJdbcSource();
        setSourceName(NEW_SOURCE_NAME);
        this.mgr.getJdbcSources().add(this.src);
        this.srcPanel.addItem(this.src);
        // Re-select previously selected driver to update URL & URL syntax fields
        final Event event = new Event();
        event.widget = this.driverCombo;
        driverModified(new ModifyEvent(event));
        // Set focus on name field & select auto-generated name so user can change
        this.nameText.setFocus();
        this.nameText.selectAll();
        validateSource();
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    Composite createPageControl( final Composite parent ) {

        // must prevent nulls from being loaded into the table
        ArrayList sourceList = new ArrayList(this.mgr.getJdbcSources().size());
        for (Iterator iter = this.mgr.getJdbcSources().iterator(); iter.hasNext();) {
            Object src = iter.next();
            if (src != null) {
                sourceList.add(src);
            }
        }

        // Create page
        final Composite pg = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        final IListPanelController ctrlr = new ListPanelAdapter() {

            @Override
            public Object[] addButtonSelected() {
                return addSource();
            }

            @Override
            public void itemsSelected( final IStructuredSelection selection ) {
                sourcesSelected(selection);
            }

            @Override
            public Object[] removeButtonSelected( final IStructuredSelection selection ) {
                return removeSources(selection);
            }
        };

        this.srcPanel = new ListPanel(pg, SOURCES_GROUP, ctrlr, SWT.MULTI, ListPanel.Constants.NONE, sourceList);
        ((GridData)this.srcPanel.getLayoutData()).minimumHeight = 200;
        ((GridData)this.srcPanel.getLayoutData()).widthHint = 580;
        final TableViewer viewer = srcPanel.getTableViewer();
        viewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((JdbcSource)source).getName();
            }

            @Override
            public boolean isLabelProperty( final Object source,
                                            final String property ) {
                return true;
            }
        });
        viewer.setSorter(new ViewerSorter() {});

        // change text of add button
        Button btn = this.srcPanel.getButton(com.metamatrix.ui.internal.InternalUiConstants.Widgets.ADD_BUTTON);
        btn.setText(ADD_BUTTON_TEXT);
        btn.setToolTipText(ADD_BUTTON_TOOLTIP);

        Button saveButton = this.srcPanel.addButton(SAVE_BUTTON);
        saveButton.setToolTipText(SAVE_BUTTON_TOOLTIP);
        saveButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                saveSources();
            }
        });
        saveButton.moveBelow(this.srcPanel.getButton(InternalUiConstants.Widgets.REMOVE_BUTTON));
        Button loadButton = this.srcPanel.addButton(LOAD_BUTTON);
        loadButton.setToolTipText(LOAD_BUTTON_TOOLTIP);
        loadButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                loadSources();
            }
        });
        loadButton.moveBelow(saveButton);

        this.editPanel = new Composite(pg, SWT.NO_TRIM) {

            @Override
            public Point computeSize( int theWidthHint,
                                      int theHeightHint,
                                      boolean theChanged ) {
                return super.computeSize(SWT.DEFAULT, SWT.DEFAULT, theChanged);
            }
        };
        GridLayout layout = new GridLayout(COLUMN_COUNT, false);
        this.editPanel.setLayout(layout);
        this.editPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        WidgetFactory.createLabel(this.editPanel, NAME_LABEL);

        this.nameText = WidgetFactory.createTextField(this.editPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.nameText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                nameModified(event);
            }
        });
        WidgetFactory.createLabel(this.editPanel, DRIVER_LABEL);
        this.driverLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object driver ) {
                if (driver instanceof JdbcDriver) return ((JdbcDriver)driver).getName();
                else if (driver instanceof String) return (String)driver;

                return EMPTY_STRING;
            }
        };
        this.driverCombo = WidgetFactory.createCombo(this.editPanel,
                                                     SWT.READ_ONLY,
                                                     GridData.FILL_HORIZONTAL,
                                                     this.driverLabelProvider);
        this.driverCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                driverModified(event);
            }
        });

        Button driversButton = WidgetFactory.createButton(this.editPanel, DRIVERS_BUTTON);
        driversButton.setToolTipText(DRIVERS_BUTTON_TOOLTIP);
        driversButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                launchDriversWizard();
            }
        });

        WidgetFactory.createLabel(this.editPanel, URL_SYNTAX_LABEL);
        this.urlSyntaxLabel = WidgetFactory.createLabel(this.editPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 2);
        this.propertiesButton = WidgetFactory.createButton(this.editPanel, PROPERTIES_BUTTON);
        this.propertiesButton.setToolTipText(PROPERTIES_TOOLTIP);
        this.propertiesButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                launchPropertiesDialog();
            }
        });

        WidgetFactory.createLabel(this.editPanel, URL_LABEL);
        this.urlCombo = WidgetFactory.createCombo(this.editPanel,
                                                  SWT.NONE,
                                                  GridData.HORIZONTAL_ALIGN_FILL,
                                                  COLUMN_COUNT - 1,
                                                  getDialogSettings().getArray(URL_LABEL));
        this.urlCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                urlModified(event);
            }
        });
        WidgetFactory.createLabel(this.editPanel, USER_NAME_LABEL);
        this.userText = WidgetFactory.createTextField(this.editPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.userText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                userNameModified(event);
            }
        });
        this.testButton = WidgetFactory.createButton(this.editPanel, TEST_BUTTON, GridData.HORIZONTAL_ALIGN_CENTER, COLUMN_COUNT);
        this.testButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                testConnection();
            }
        });

        // Initialize widgets
        viewer.setSelection(new StructuredSelection(new Object[] {this.src}));
        return pg;
    }

    /**
     * @since 4.0
     */
    void driverModified( final ModifyEvent event ) {
        if (this.src == null) {
            return;
        }
        // Wrap in transaction so it doesn't result in Significant Undoable
        boolean started = ModelerCore.startTxn(false, false, "Set Driver Values", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(JdbcSourceWizard.this));
        boolean succeeded = false;
        try {
            final String text = this.driverCombo.getText();
            final JdbcDriver[] drivers = this.mgr.findDrivers(text);
            if (drivers.length > 0) {
                final JdbcDriver driver = drivers[0];
                this.src.setJdbcDriver(driver);
                this.src.setDriverName(driver.getName());
                this.src.setDriverClass(driver.getPreferredDriverClassName());
                // If URL syntax is different, replace
                if (!this.urlSyntaxLabel.getText().equals(driver.getUrlSyntax())) {
                    JdbcUiUtil.setText(this.urlSyntaxLabel, driver.getUrlSyntax());
                    this.urlCombo.setText(driver.getUrlSyntax());
                }
            } else {
                this.src.setJdbcDriver(null);
                this.src.setDriverName(EMPTY_STRING);
                this.src.setDriverClass(EMPTY_STRING);
                this.urlSyntaxLabel.setText(EMPTY_STRING);
                JdbcUiUtil.setEnabled(this.urlSyntaxLabel, false);
            }
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        validateSource();
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        // Wrap in transaction so it doesn't result in Significant Undoable
        boolean started = ModelerCore.startTxn(false, false, "Save JDBC Changes", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(JdbcSourceWizard.this));
        boolean succeeded = false;
        try {
            JdbcUiUtil.saveChanges();
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        if (succeeded) {
            WidgetUtil.saveSettings(getDialogSettings(), URL_LABEL, this.urlCombo);
        }
        return succeeded;
    }

    /**
     * @return The password entered when the user tested the connection to the source, or <code>null</code> if connection was not
     *         tested.
     * @since 5.0
     */
    public String getPassword() {
        return this.pwd;
    }

    /**
     * @since 4.0
     */
    JdbcSource getSelection() {
        return this.src;
    }

    /**
     * @since 4.0
     */
    private void invalidateSource( final JdbcSource source,
                                   final String message ) {
        this.pg.setMessage(message, IMessageProvider.ERROR);
        this.pg.setPageComplete(false);
        this.testButton.setEnabled(false);
        if (source != this.src) {
            // this.srcPanel.getViewer().setSelection(new StructuredSelection(src), true);
        }
    }

    /**
     * @since 4.0
     */
    void launchDriversWizard() {
        final JdbcDriverWizard wizard = new JdbcDriverWizard();
        final WizardDialog dlg = WidgetFactory.createOnePageWizardDialog(getShell(), wizard);
        wizard.setSelection(this.src.getJdbcDriver());
        if (dlg.open() == Window.OK) {
            // Update drivers combo, preserving any previous selection but first unregister all modify listeners because the call
            // to WidgetUtil.setComboItems sends a lot of events (it removes all items first and then adds all back in). THis was
            // causing the URL to be set back to the template version.

            // unregister all listeners
            Listener[] listeners = this.driverCombo.getListeners(SWT.Modify);
            for (Listener listener : listeners) {
                this.driverCombo.removeListener(SWT.Modify, listener);
            }

            // populate driver combo
            WidgetUtil.setComboItems(this.driverCombo, this.mgr.getJdbcDrivers(), this.driverLabelProvider, true);

            // re-register all listeners
            for (Listener listener : listeners) {
                this.driverCombo.addListener(SWT.Modify, listener);
            }

            // now notify all listeners
            Event e = new Event();
            e.type = SWT.Modify;
            e.widget = this.driverCombo;
            this.driverCombo.notifyListeners(SWT.Modify, e);

            final JdbcDriver driver = wizard.getSelection();
            if (driver != null && this.src.getJdbcDriver() != driver) {
                WidgetUtil.setComboText(this.driverCombo, driver, this.driverLabelProvider);
            }
            validateSource();
        }
    }

    void launchPropertiesDialog() {
        if (this.src != null) {
            new JdbcDriverPropertiesDialog(this.getShell(), this.src).open();
        }
    }

    void loadSources() {
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[] {SOURCE_FILE_FILTER});
        dlg.setText(LOAD_SOURCES_DIALOG_TITLE);
        String fileStr = dlg.open();
        // If there is no file extension, add .sql
        if (fileStr != null && fileStr.indexOf('.') == -1) {
            fileStr = fileStr + '.' + SOURCE_FILE_EXTENSION;
        }
        if (fileStr != null) {
            final String fileName = fileStr;
            UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                public void run() {
                    Resource resource = null;
                    try {
                        URI uri = URI.createFileURI(fileName);
                        resource = ModelerCore.createContainer(JDBC_LOAD_CONTAINER_NAME).getOrCreateResource(uri);
                        if (resource != null) {
                            Map options = (resource.getResourceSet() != null ? resource.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
                            resource.load(options);
                            try {
                                List newSources = mgr.loadConnections(resource);
                                for (Iterator iter = newSources.iterator(); iter.hasNext();) {
                                    Object src = iter.next();
                                    if (src != null) {
                                        srcPanel.getTableViewer().add(src);
                                    }
                                }
                                validateSource();
                            } catch (Exception e) {
                                InternalModelerJdbcUiPluginConstants.Util.log(IStatus.ERROR, e, LOAD_SOURCES_ERROR_MESSAGE);
                                MessageDialog.openError(getShell(), LOAD_SOURCES_ERROR_TITLE, LOAD_SOURCES_ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        InternalModelerJdbcUiPluginConstants.Util.log(IStatus.ERROR, e, LOAD_SOURCES_FILE_ERROR_MESSAGE);
                        MessageDialog.openError(getShell(), LOAD_SOURCES_ERROR_TITLE, LOAD_SOURCES_FILE_ERROR_MESSAGE);
                    } finally {
                        if (resource != null) {
                            resource.unload();
                        }
                    }

                }
            });
        }
    }

    /**
     * @since 4.0
     */
    void nameModified( final ModifyEvent event ) {
        if (this.src == null) {
            return;
        }

        final Text text = (Text)event.widget;
        setSourceName(text.getText().trim());
        this.srcPanel.getTableViewer().update(this.src, null);

        validateSource();
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     * @since 4.0
     */
    @Override
    public boolean performCancel() {
        JdbcUiUtil.reload();
        return super.performCancel();
    }

    /**
     * @since 4.0
     */
    Object[] removeSources( final IStructuredSelection selection ) {
        this.mgr.getJdbcSources().removeAll(selection.toList());
        validateSource();
        return selection.toArray();
    }

    void saveSources() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {SOURCE_FILE_FILTER});
        dlg.setText(SAVE_SOURCES_DIALOG_TITLE);
        String fileStr = dlg.open();
        // If there is no file extension, add .sql
        if (fileStr != null && fileStr.indexOf('.') == -1) {
            fileStr = fileStr + '.' + SOURCE_FILE_EXTENSION;
        }
        if (fileStr != null) {
            final String fileName = fileStr;
            UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                public void run() {
                    try {
                        File file = new File(fileName);
                        if (file.exists()) {
                            if (file.canWrite()) {
                                if (!MessageDialog.openConfirm(getShell(),
                                                               SAVE_SOURCES_OVERWRITE_TITLE,
                                                               SAVE_SOURCES_OVERWRITE_MESSAGE)) {
                                    return;
                                }
                            } else {
                                MessageDialog.openError(getShell(), SAVE_SOURCES_READONLY_TITLE, SAVE_SOURCES_READONLY_MESSAGE);
                                return;
                            }
                        } else {
                            file.createNewFile();
                        }
                        FileOutputStream os = new FileOutputStream(file);
                        mgr.saveConnections(os);
                        os.close();

                        MessageDialog.openInformation(getShell(),
                                                      SAVE_SOURCES_CONFIRMATION_TITLE,
                                                      SAVE_SOURCES_CONFIRMATION_MESSAGE);
                    } catch (Exception e) {
                        InternalModelerJdbcUiPluginConstants.Util.log(IStatus.ERROR, e, SAVE_SOURCES_ERROR_MESSAGE);
                    }
                }
            });
        }
    }

    /**
     * @since 4.0
     */
    void setSelection( final JdbcSource source ) {
        this.src = source;
    }

    void setSourceName( String text ) {
        // Wrap in transaction so it doesn't result in Significant Undoable
        boolean started = ModelerCore.startTxn(false, false, "Set JDBC Source Name", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(JdbcSourceWizard.this));
        boolean succeeded = false;
        try {
            this.src.setName(text);
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @since 4.0
     */
    void sourcesSelected( final IStructuredSelection selection ) {
        this.src = null;
        if (selection.size() != 1) {
            if (this.enableMap == null) {
                this.enableMap = WidgetUtil.disable(this.editPanel);
            }
            this.nameText.setText(EMPTY_STRING);
            this.driverCombo.removeAll();
            this.urlSyntaxLabel.setText(EMPTY_STRING);
            this.urlCombo.setText(EMPTY_STRING);
            this.userText.setText(EMPTY_STRING);
        } else {
            final JdbcSource src = (JdbcSource)selection.getFirstElement();
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
            this.nameText.setText(src.getName());

            final JdbcDriver driver = this.mgr.findBestDriver(src);
            List allDrivers = new ArrayList();
            allDrivers.add(SELECT_DRIVER_ITEM);
            allDrivers.addAll(this.mgr.getJdbcDrivers());
            WidgetUtil.setComboItems(this.driverCombo, allDrivers, this.driverLabelProvider, true);
            if (driver == null) driverCombo.select(0);
            JdbcUiUtil.setText(this.urlSyntaxLabel, driver == null ? null : driver.getUrlSyntax());
            final String url = src.getUrl();
            JdbcUiUtil.setText(this.urlCombo, url == null ? EMPTY_STRING : url);
            // Set driver after setting url since combo listener looks at url
            // JdbcUiUtil.setText(this.driverCombo, url == null ? SPACE : driverLabelProvider.getText(driver));
            WidgetUtil.setComboText(this.driverCombo, driver, this.driverLabelProvider);
            final String name = src.getUsername();
            JdbcUiUtil.setText(this.userText, name == null ? EMPTY_STRING : name);
            this.src = src;
            validateSource();
        }
    }

    /**
     * @since 4.0
     */
    void testConnection() {
        new AbstractPasswordDialog(getShell()) {

            @Override
            protected boolean isPasswordValid( final String password ) {
                Connection connection = null;
                boolean connected = false;
                // Open a connection - and be sure to close it! (per defect 11221)
                try {
                    connection = JdbcUiUtil.connect(JdbcSourceWizard.this.src, password);
                    if (connection != null) {
                        // We've connected ...
                        connected = true;
                        JdbcSourceWizard.this.pwd = password;
                    }
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Throwable e1) {
                            InternalModelerJdbcUiPluginConstants.Util.log(e1);
                        } finally {
                            connection = null;
                        }
                    }
                }
                if (connected) {
                    // Present the info in a dialog ...
                    MessageDialog.openInformation(getShell(), NOTIFICATION_MESSAGE_TITLE, CONNECTION_SUCCEEDED_MESSAGE);
                }
                // Return whether we've connected ...
                return connected;
            }
        }.open();
    }

    /**
     * @since 4.0
     */
    void urlModified( final ModifyEvent event ) {
        if (this.src == null) {
            return;
        }
        // Wrap in transaction so it doesn't result in Significant Undoable
        boolean started = ModelerCore.startTxn(false, false, "Set JDBC Source URL", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(JdbcSourceWizard.this));
        boolean succeeded = false;
        try {
            this.src.setUrl(this.urlCombo.getText().trim());
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        validateSource();
    }

    /**
     * @since 4.0
     */
    void userNameModified( final ModifyEvent event ) {
        if (this.src == null) {
            return;
        }
        // Wrap in transaction so it doesn't result in Significant Undoable
        boolean started = ModelerCore.startTxn(false, false, "Set JDBC Source Username", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(JdbcSourceWizard.this));
        boolean succeeded = false;
        try {
            final Text text = (Text)event.widget;
            this.src.setUsername(text.getText().trim());
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        validateSource();
    }

    /**
     * @since 4.0
     */
    void validateSource() {
        JdbcSource nonErrSrc = null;
        IStatus nonErrStatus = null;
        for (final Iterator iter = this.mgr.getJdbcSources().iterator(); iter.hasNext();) {
            final JdbcSource src = (JdbcSource)iter.next();
            final JdbcSource[] srcs = this.mgr.findSources(src.getName());
            if (srcs.length > 1) {
                invalidateSource(src, DUPLICATE_SOURCE_MESSAGE);
                return;
            }
            final IStatus status = this.mgr.isValid(src);
            if (!status.isOK()) {
                if (status.getSeverity() == IStatus.ERROR) {
                    invalidateSource(src, status.getMessage());
                    return;
                }
                if (nonErrSrc == null || (nonErrStatus.getSeverity() == IStatus.INFO && status.getSeverity() == IStatus.WARNING)) {
                    nonErrSrc = src;
                    nonErrStatus = status;
                }
            }
        }
        if (nonErrSrc == null) {
            this.pg.setMessage(VALID_DIALOG_MESSAGE);
        } else {
            final int severity = (nonErrStatus.getSeverity() == IStatus.WARNING ? IMessageProvider.WARNING : IMessageProvider.INFORMATION);
            this.pg.setMessage(nonErrStatus.getMessage(), severity);
            if (nonErrSrc != this.src) {
                // this.srcPanel.getViewer().setSelection(new StructuredSelection(nonErrSrc), true);
            }
        }
        this.testButton.setEnabled(true);
        this.pg.setPageComplete(true);
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.wizards;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.jdbc.ui.util.JdbcUiUtil;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.widget.IListPanelController;
import com.metamatrix.ui.internal.widget.ListPanel;
import com.metamatrix.ui.internal.widget.ListPanelAdapter;
import com.metamatrix.ui.internal.wizard.IPersistentWizardPage;

/**
 * @since 4.0
 */
final class JdbcImportMetadataPage extends WizardPage implements InternalUiConstants.Widgets,
                                                                 IPersistentWizardPage,
                                                                 ListPanel.Constants,
                                                                 UiConstants {
    //============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcImportMetadataPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final String APPROXIMATIONS_BUTTON = getString("approximationsButton"); //$NON-NLS-1$
    private static final String FOREIGN_KEYS_BUTTON   = getString("foreignKeysButton"); //$NON-NLS-1$
    private static final String INDEXES_BUTTON        = getString("indexesButton"); //$NON-NLS-1$
    private static final String PROCEDURES_BUTTON     = getString("proceduresButton"); //$NON-NLS-1$
    private static final String UNIQUE_BUTTON         = getString("uniqueButton"); //$NON-NLS-1$

    private static final String TABLE_TYPES_GROUP = getString("tableTypesGroup"); //$NON-NLS-1$

    private static final String INITIAL_MESSAGE      = getString("initialMessage"); //$NON-NLS-1$
    private static final String INVALID_PAGE_MESSAGE =
        getString("invalidPageMessage", PROCEDURES_BUTTON, TABLE_TYPES_GROUP); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 2;

    private static final int CHECKBOX_TEXT_GAP = 5;

    //============================================================================================================================
    // Static Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    private static String getString(final String id, final String parameter1, final String parameter2) {
        return Util.getString(I18N_PREFIX + id, parameter1, parameter2);
    }

    //============================================================================================================================
	// Variables

    private Includes incls;
    private JdbcImportSettings importSettings;
    private Button foreignKeysCheckBox, indexesCheckBox, uniqueCheckBox, approximationsCheckBox, proceduresCheckBox;
    private ListPanel listPanel;
    private Map enableMap;
    private boolean initd;

    //============================================================================================================================
	// Constructors

	/**<p>
	 * </p>
	 * @param pageName
	 * @since 4.0
	 */
    JdbcImportMetadataPage() {
        super(JdbcImportMetadataPage.class.getSimpleName(), TITLE, null);
    }

    //============================================================================================================================
    // Implemented Methods

	/**<p>
	 * </p>
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 * @since 4.0
	 */
	public void createControl(final Composite parent) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pg);
        // Add widgets to page
        final Composite checkBoxPanel = WidgetFactory.createPanel(pg, SWT.NO_TRIM, GridData.VERTICAL_ALIGN_BEGINNING);
        {
            this.foreignKeysCheckBox = WidgetFactory.createCheckBox(checkBoxPanel, FOREIGN_KEYS_BUTTON);
            this.foreignKeysCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    foreignKeysCheckBoxSelected();
                }
            });
            this.indexesCheckBox = WidgetFactory.createCheckBox(checkBoxPanel);
            this.indexesCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    indexesCheckBoxSelected();
                }
            });
            final int indent = this.indexesCheckBox.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x + CHECKBOX_TEXT_GAP;
            this.indexesCheckBox.setText(INDEXES_BUTTON);
            this.uniqueCheckBox = createNestedCheckbox(checkBoxPanel, UNIQUE_BUTTON, indent);
            this.uniqueCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    uniqueCheckBoxSelected();
                }
            });
            this.approximationsCheckBox = createNestedCheckbox(checkBoxPanel, APPROXIMATIONS_BUTTON, indent);
            this.approximationsCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    approximationsCheckBoxSelected();
                }
            });
            this.proceduresCheckBox = WidgetFactory.createCheckBox(checkBoxPanel, PROCEDURES_BUTTON);
            this.proceduresCheckBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    procuduresButtonSelected();
                }
            });
        }
        final IListPanelController ctrlr = new ListPanelAdapter() {
            @Override
            public Object[] addButtonSelected() {
                return null;
            }
        };
        this.listPanel = new ListPanel(pg, TABLE_TYPES_GROUP, ctrlr, SWT.READ_ONLY | SWT.MULTI, ITEMS_COMMONLY_ALL_SELECTED);
        this.listPanel.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
                tableTypesSelected(event);
			}
		});
	}

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.wizard.IPersistentWizardPage#saveSettings()
     * @since 4.0
     */
    public void saveSettings() {
        final IDialogSettings dlgSettings = getDialogSettings();
        // Information must be obtained from wizard, not local variables, since this method may be called w/o this page every
        // being set visible, via the user having pre-selected a destination folder and clicking on the "Finish" earlier in the
        // wizard.
        final Includes incls = ((JdbcImportWizard)getWizard()).getDatabase().getIncludes();
        dlgSettings.put(APPROXIMATIONS_BUTTON, incls.getApproximateIndexes());
        dlgSettings.put(FOREIGN_KEYS_BUTTON, incls.includeForeignKeys());
        dlgSettings.put(INDEXES_BUTTON, incls.includeIndexes());
        dlgSettings.put(PROCEDURES_BUTTON, incls.includeProcedures());
        dlgSettings.put(UNIQUE_BUTTON, incls.getUniqueIndexesOnly());
        final String[] tableTypes = incls.getIncludedTableTypes();
        if (tableTypes != null) {
            dlgSettings.put(TABLE_TYPES_GROUP, tableTypes);
        }
    }

    //============================================================================================================================
    // Overridden Methods

    /**<p>
	 * </p>
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 * @since 4.0
	 */
	@Override
    public void setVisible(final boolean visible) {
        if (visible) {
            // Wrap in transaction so it doesn't result in Significant Undoable
            boolean started = ModelerCore.startTxn(false, false, "Initializing Import Settings", this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                initializeInTransaction();
                succeeded = true;
            } finally {
                if ( started ) {
                    if ( succeeded ) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        setMessage(INITIAL_MESSAGE);
        super.setVisible(visible);
	}

    void initializeInTransaction() {
        // Connect to database chosen in previous page
        final JdbcDatabase db = ((JdbcImportWizard)getWizard()).getDatabase();
        if (db == null) {
            return;
        }
        // Get database's includes
        this.incls = db.getIncludes();
        final JdbcSource src = ((JdbcImportWizard)getWizard()).getSource();
        this.importSettings = src.getImportSettings();
        // Initialize widgets with inclusion information
        final IDialogSettings dlgSettings = getDialogSettings();
        final boolean updating = ((JdbcImportWizard)getWizard()).isUpdatedModel();
        if (!updating  &&  dlgSettings.get(PROCEDURES_BUTTON) != null  && !this.initd) {
            this.initd = true;
            this.incls.setApproximateIndexes(dlgSettings.getBoolean(APPROXIMATIONS_BUTTON));
            this.incls.setIncludeForeignKeys(dlgSettings.getBoolean(FOREIGN_KEYS_BUTTON));
            this.incls.setIncludeIndexes(dlgSettings.getBoolean(INDEXES_BUTTON));
            this.incls.setIncludeProcedures(dlgSettings.getBoolean(PROCEDURES_BUTTON));
            this.incls.setUniqueIndexesOnly(dlgSettings.getBoolean(UNIQUE_BUTTON));
            this.incls.setIncludedTableTypes(dlgSettings.getArray(TABLE_TYPES_GROUP));
            this.importSettings.setIncludeApproximateIndexes(dlgSettings.getBoolean(APPROXIMATIONS_BUTTON));
            this.importSettings.setIncludeForeignKeys(dlgSettings.getBoolean(FOREIGN_KEYS_BUTTON));
            this.importSettings.setIncludeIndexes(dlgSettings.getBoolean(INDEXES_BUTTON));
            this.importSettings.setIncludeProcedures(dlgSettings.getBoolean(PROCEDURES_BUTTON));
            this.importSettings.setIncludeUniqueIndexes(dlgSettings.getBoolean(UNIQUE_BUTTON));
            final List tableTypes = this.importSettings.getIncludedTableTypes();
            final String[] ttypes = this.incls.getIncludedTableTypes();
            for (int i = 0; i < ttypes.length; ++i) {
                final String tableType = ttypes[i];
                tableTypes.add(tableType);
            }
        } else {
            this.incls.setApproximateIndexes(false);
            this.incls.setIncludeForeignKeys(true);
            this.incls.setIncludeIndexes(false);
            this.incls.setIncludeProcedures(false);
            this.incls.setUniqueIndexesOnly(false);
            this.importSettings.setIncludeApproximateIndexes(true);
            this.importSettings.setIncludeForeignKeys(true);
            this.importSettings.setIncludeIndexes(false);
            this.importSettings.setIncludeProcedures(false);
            this.importSettings.setIncludeUniqueIndexes(false);
        }
        setSelected(this.approximationsCheckBox, this.incls.getApproximateIndexes());
        setSelected(this.foreignKeysCheckBox, this.incls.includeForeignKeys());
        setSelected(this.indexesCheckBox, this.incls.includeIndexes());
        setSelected(this.proceduresCheckBox, this.incls.includeProcedures());
        setSelected(this.uniqueCheckBox, this.incls.getUniqueIndexesOnly());
        final TableViewer viewer = this.listPanel.getTableViewer();
        for (Object obj = viewer.getElementAt(0);  obj != null;  obj = viewer.getElementAt(0)) {
            viewer.remove(obj);
        }
        ResultSet result = null;
        try {
            result = db.getDatabaseMetaData().getTableTypes();
            final List types = new ArrayList();
            while (result.next()) {
                String type = result.getString(1).trim();
                if(!types.contains(type)) {
                    types.add(type);
                }
            }
            // If this is an update, retain only the table types imported last time
            if (!types.isEmpty()) {
                viewer.add(types.toArray());
                final List lastTypes = this.importSettings.getIncludedTableTypes();
                if (lastTypes != null && !lastTypes.isEmpty()) {
                    viewer.setSelection(new StructuredSelection(lastTypes));
                } else {
                    viewer.setSelection(new StructuredSelection(types));
                }
            }
        } catch (final Exception err) {
            JdbcUiUtil.showAccessError(err);
        } finally {
            if ( result != null ) {
                try {
                    result.close();
                } catch (SQLException e) {
                    UiConstants.Util.log(e);
                }
            }
        }
    }

    //============================================================================================================================
    // MVC Controller Methods

    /**<p>
     * </p>
     * @param event
     * @since 4.0
     */
    void approximationsCheckBoxSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set Opproxomations Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.incls.setApproximateIndexes(this.approximationsCheckBox.getSelection());
            this.importSettings.setIncludeApproximateIndexes(this.incls.getApproximateIndexes());
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void foreignKeysCheckBoxSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set Foreign Keys Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.incls.setIncludeForeignKeys(this.foreignKeysCheckBox.getSelection());
            this.importSettings.setIncludeForeignKeys(this.incls.includeForeignKeys());
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
	}

    /**<p>
     * </p>
     * @since 4.0
     */
    void indexesCheckBoxSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set Indexes Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            final boolean enabled = this.indexesCheckBox.getSelection();
            this.incls.setIncludeIndexes(enabled);
            this.uniqueCheckBox.setEnabled(enabled);
            this.approximationsCheckBox.setEnabled(enabled);
            this.importSettings.setIncludeIndexes(this.incls.includeIndexes());
            this.importSettings.setIncludeUniqueIndexes(this.incls.getUniqueIndexesOnly());
            this.importSettings.setIncludeApproximateIndexes(this.incls.getApproximateIndexes());
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    /**<p>
     * </p>
     * @param event
     * @since 4.0
     */
    void procuduresButtonSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set Procedures Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.incls.setIncludeProcedures(this.proceduresCheckBox.getSelection());
            this.importSettings.setIncludeProcedures(this.incls.includeProcedures());
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        validatePage();
    }

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void tableTypesSelected(final SelectionChangedEvent event) {
        final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        final String[] types = new String[selection.size()];

        final List tableTypes = this.importSettings.getIncludedTableTypes();
        tableTypes.clear();
        final Iterator iter = selection.iterator();
        for (int ndx = 0;  ndx < types.length;  ++ndx) {
            types[ndx] = (String)iter.next();
            tableTypes.add(types[ndx]);
        }
        this.incls.setIncludedTableTypes(types);
        final boolean enabled = (types.length > 0);
        this.foreignKeysCheckBox.setEnabled(enabled);
        this.indexesCheckBox.setEnabled(enabled);
        if (enabled) {
            if (this.enableMap != null) {
                WidgetUtil.restore(this.enableMap);
                this.enableMap = null;
            }
        } else {
            this.enableMap = WidgetUtil.disable(new Control[] {this.uniqueCheckBox, this.approximationsCheckBox});
        }
        validatePage();
	}

    /**<p>
     * </p>
     * @param event
     * @since 4.0
     */
    void uniqueCheckBoxSelected() {
        boolean requiredStart = ModelerCore.startTxn(false,false,"Set Unique Indexes Option",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            this.incls.setUniqueIndexesOnly(this.uniqueCheckBox.getSelection());
            this.importSettings.setIncludeUniqueIndexes(this.incls.getUniqueIndexesOnly());
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    //============================================================================================================================
	// Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private Button createNestedCheckbox(final Composite parent, final String text, final int indent) {
        final Button button = WidgetFactory.createCheckBox(parent, text);
        final GridData gridData = (GridData)button.getLayoutData();
        gridData.horizontalIndent = indent;
        return button;
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    private void setSelected(final Button checkBox, final boolean selected) {
        checkBox.setSelection(selected);
        checkBox.notifyListeners(SWT.Selection, new Event());
    }

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	private void validatePage() {
        if (this.proceduresCheckBox.getSelection()  ||  !this.listPanel.getViewer().getSelection().isEmpty()) {
            WizardUtil.setPageComplete(this);
        } else {
            WizardUtil.setPageComplete(this, INVALID_PAGE_MESSAGE, ERROR);
        }
	}
}

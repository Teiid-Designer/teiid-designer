/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.IMessageListener;
import com.metamatrix.rose.internal.IUnit;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.rose.internal.ui.RoseUiPlugin;
import com.metamatrix.rose.internal.ui.util.PathVariableDialog;
import com.metamatrix.rose.internal.ui.util.RoseImporterUiUtils;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.ui.internal.widget.StatusLabel;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public final class SourceUnitsPage extends AbstractWizardPage implements IRoseUiConstants, IRoseUiConstants.Images {

    /** Wizard page identifier. */
    public static final String PAGE_ID = SourceUnitsPage.class.getSimpleName();

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SourceUnitsPage.class);

    /** Key for source models MRU list stored in dialog settings. */
    private static final String SOURCE_MODEL_LIST = "sourceModels"; //$NON-NLS-1$

    /** Column headers for the units table. */
    private static final String[] UNITS_TBL_HDRS;

    /** Index of the error icon column in the units table. */
    static final int ERROR_ICON_COLUMN;

    /** Index of the name column in the units table. */
    static final int NAME_COLUMN;

    /** Index of the path column in the units table. */
    static final int PATH_COLUMN;

    /** Column headers for the variables table. */
    private static final String[] VARIABLES_TBL_HDRS;

    /** Index of the variable column in the variables table. */
    static final int VARIABLE_COLUMN;

    /** Index of the value column in the variables table. */
    static final int VALUE_COLUMN;

    static {
        //
        // Units Table
        //

        // set column indexes
        NAME_COLUMN = 0;
        ERROR_ICON_COLUMN = 1;
        PATH_COLUMN = 2;

        // set column headers
        UNITS_TBL_HDRS = new String[3];
        UNITS_TBL_HDRS[ERROR_ICON_COLUMN] = ""; //$NON-NLS-1$
        UNITS_TBL_HDRS[NAME_COLUMN] = UTIL.getString(PREFIX + "table.units.column.name"); //$NON-NLS-1$
        UNITS_TBL_HDRS[PATH_COLUMN] = UTIL.getString(PREFIX + "table.units.column.path"); //$NON-NLS-1$

        //
        // Variables Table
        //

        // set column indexes
        VARIABLE_COLUMN = 0;
        VALUE_COLUMN = 1;

        // set column headers
        VARIABLES_TBL_HDRS = new String[2];
        VARIABLES_TBL_HDRS[VARIABLE_COLUMN] = UTIL.getString(PREFIX + "table.variables.column.variable"); //$NON-NLS-1$
        VARIABLES_TBL_HDRS[VALUE_COLUMN] = UTIL.getString(PREFIX + "table.variables.column.value"); //$NON-NLS-1$
    }

    /** Business object for widget. */
    private RoseImporter importer;

    private List selectedUnits;

    boolean showPathVariables;

    IUnit sourceUnit;

    /** Checks all rows in the units table. */
    private Button btnCheckAll;

    /** Checks all children of the selected rows in the units table. */
    private Button btnCheckChildren;

    /** Sets the currently selected path map variable to the current directory. */
    private Button btnCurrDir;

    /** Edit's the selected path map variable. */
    private Button btnEditPathVariable;

    /** Unchecks all rows in the units table. */
    private Button btnUncheckAll;

    /** Unchecks all children of the selected rows in the units table. */
    private Button btnUncheckChildren;

    /** MRU list of source models. */
    private Combo cbxSourceUnit;

    private StatusLabel lblUnitStatusMsg;

    /** Viewer for path map table. */
    private TreeViewer pathMapViewer;

    /** Viewer for units table. */
    private TreeViewer unitsViewer;

    /**
     * @param theImporter
     * @since 4.1
     */
    public SourceUnitsPage( RoseImporter theImporter ) {
        super(PAGE_ID, UTIL.getString(PREFIX + "title")); //$NON-NLS-1$

        this.importer = theImporter;
        this.selectedUnits = this.importer.getSelectedUnits();
        this.showPathVariables = false;

        setPageComplete(false);

        // register to receive addUnitLoadListener
        this.importer.addUnitSourceMessageListener(new IMessageListener() {
            public void messageSent( final IMessage theMessage ) {
                // if not on UI thread put it on
                if (Display.getCurrent() == null) {
                    getControl().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            handleImporterMessage(theMessage);
                        }
                    });
                } else {
                    handleImporterMessage(theMessage);
                }
            }
        });
    }

    private void createActions( IToolBarManager theToolBarMgr ) {
        // create show variables action
        Action action = new Action("", IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                SourceUnitsPage.this.showPathVariables = !SourceUnitsPage.this.showPathVariables;
                getUnitsViewer().refresh(true);
            }
        };
        action.setImageDescriptor(RoseUiPlugin.getDefault().getImageDescriptor(Images.SHOW_PATH_VARIABLES_ICON));
        action.setToolTipText(UTIL.getString(PREFIX + "button.showPathVariables.tip")); //$NON-NLS-1$
        theToolBarMgr.add(action);

        theToolBarMgr.update(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.1
     */
    public void createControl( Composite theParent ) {
        final int COLUMN_COUNT = 3;

        //
        // Get saved wizard settings and make sure values are still valid
        //

        IDialogSettings settings = getDialogSettings();
        WidgetUtil.removeMissingResources(settings, SOURCE_MODEL_LIST);

        //
        // Create main container
        //

        Composite pnlMain = WidgetFactory.createPanel(theParent);
        pnlMain.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pnlMain);

        //
        // pnlMain contents
        //

        // label for source folder combo
        WidgetFactory.createLabel(pnlMain, UTIL.getString(PREFIX + "label.sourceModel")); //$NON-NLS-1$

        // import source folder combo
        this.cbxSourceUnit = WidgetFactory.createCombo(pnlMain,
                                                       SWT.READ_ONLY,
                                                       GridData.FILL_HORIZONTAL,
                                                       settings.getArray(SOURCE_MODEL_LIST));
        this.cbxSourceUnit.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleSourceModelModified();
            }
        });

        // browse button to choose import folder
        Button btn = WidgetFactory.createButton(pnlMain, UTIL.getString(PREFIX + "button.browse")); //$NON-NLS-1$
        btn.setToolTipText(UTIL.getString(PREFIX + "button.browse.sourceModel.tip")); //$NON-NLS-1$
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleBrowseSourceSelected();
            }
        });

        // tab folder to switch between units and path map
        int style = GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL;
        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlMain, SWT.BOTTOM, style, COLUMN_COUNT);
        createTabFolderContents(tabFolder);
    }

    private void createPathMapTabContents( CTabItem theTab ) {
        // view form for contained Rose units
        ViewForm viewForm = WidgetFactory.createViewForm(theTab.getParent(), SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, UTIL.getString(PREFIX + "label.pathMapViewForm"))); //$NON-NLS-1$

        // panel for view form content
        Composite pnlPathMapTab = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH, 1, 2);
        viewForm.setContent(pnlPathMapTab);
        theTab.setControl(viewForm);

        // create units table
        createPathMapTableViewer(pnlPathMapTab);

        // create panel to hold all the buttons
        Composite pnlButtons = WidgetFactory.createPanel(pnlPathMapTab, GridData.VERTICAL_ALIGN_CENTER);

        //
        // pnlButtons content
        //

        // edit button
        this.btnEditPathVariable = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.editVariable"), //$NON-NLS-1$
                                                              GridData.FILL_HORIZONTAL);
        this.btnEditPathVariable.setEnabled(false);
        this.btnEditPathVariable.setToolTipText(UTIL.getString(PREFIX + "button.editVariable.tip")); //$NON-NLS-1$
        this.btnEditPathVariable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleEditPathMapVariable();
            }
        });

        // set to current directory button
        this.btnCurrDir = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.currentDirectory"), //$NON-NLS-1$
                                                     GridData.FILL_HORIZONTAL);
        this.btnCurrDir.setEnabled(false);
        this.btnCurrDir.setToolTipText(UTIL.getString(PREFIX + "button.currentDirectory.tip")); //$NON-NLS-1$
        this.btnCurrDir.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSetToCurrentDirectory();
            }
        });

    }

    private void createPathMapTableViewer( Composite theParent ) {
        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION;
        this.pathMapViewer = WidgetFactory.createTreeViewer(theParent, style);
        this.pathMapViewer.setContentProvider(new PathMapTreeContentProvider());
        this.pathMapViewer.setLabelProvider(new PathMapTableLabelProvider());

        Tree tree = this.pathMapViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handlePathVariableSelected();
            }
        });

        // create columns
        for (int i = 0; i < VARIABLES_TBL_HDRS.length; i++) {
            TreeColumn col = new TreeColumn(tree, SWT.LEFT);
            col.setText(VARIABLES_TBL_HDRS[i]);
        }
    }

    private void createTabFolderContents( CTabFolder theTabFolder ) {
        CTabItem tab = WidgetFactory.createTab(theTabFolder, UTIL.getString(PREFIX + "tab.units")); //$NON-NLS-1$
        tab.setToolTipText(UTIL.getString(PREFIX + "tab.units.tip")); //$NON-NLS-1$
        createUnitsTabContents(tab);

        tab = WidgetFactory.createTab(theTabFolder, UTIL.getString(PREFIX + "tab.pathMap")); //$NON-NLS-1$
        tab.setToolTipText(UTIL.getString(PREFIX + "tab.pathMap.tip")); //$NON-NLS-1$
        createPathMapTabContents(tab);

        // size columns to headers initially
        packTableColumns();

        // select first tab
        theTabFolder.setSelection(0);
    }

    private void createUnitsTableViewer( Composite theParent ) {
        // table
        int style = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION;
        Tree tblTree = new Tree(theParent, style);
        tblTree.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Table tbl = tblTree.getTable();
        tblTree.setLinesVisible(true);
        tblTree.setHeaderVisible(true);

        this.unitsViewer = new TreeViewer(tblTree);
        this.unitsViewer.setContentProvider(new UnitsTableContentProvider());
        this.unitsViewer.setLabelProvider(new UnitsTableLabelProvider());
        this.unitsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleUnitSelected(theEvent.getSelection());
            }
        });
        this.unitsViewer.addTreeListener(new ITreeViewerListener() {
            public void treeCollapsed( TreeExpansionEvent theEvent ) {
            }

            public void treeExpanded( TreeExpansionEvent theEvent ) {
                handleTreeExpanded(theEvent.getElement());
            }
        });
        this.unitsViewer.getTree().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCheckStateChanged(theEvent);
            }
        });

        // create columns
        for (int i = 0; i < UNITS_TBL_HDRS.length; i++) {
            TreeColumn col = new TreeColumn(tblTree, SWT.LEFT);
            col.setText(UNITS_TBL_HDRS[i]);

            if (i == ERROR_ICON_COLUMN) {
                col.setResizable(false);
                col.setImage(RoseImporterUiUtils.getProblemViewImage());
            }
        }
    }

    private void createUnitsTabContents( CTabItem theTab ) {
        // view form for contained Rose units
        ViewForm viewForm = WidgetFactory.createViewForm(theTab.getParent(), SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, UTIL.getString(PREFIX + "label.unitsViewForm"))); //$NON-NLS-1$

        // create toolbar and install actions
        createActions(WidgetFactory.createViewFormToolBar(viewForm));

        // panel for view form content
        final int COLUMNS = 2;
        Composite pnlUnitTab = WidgetFactory.createPanel(viewForm, SWT.NONE, GridData.FILL_BOTH, 1, COLUMNS);
        viewForm.setContent(pnlUnitTab);
        theTab.setControl(viewForm);

        // create units table
        createUnitsTableViewer(pnlUnitTab);

        // create panel to hold all the buttons
        Composite pnlButtons = WidgetFactory.createPanel(pnlUnitTab, GridData.VERTICAL_ALIGN_CENTER);

        // label showing selected unit's status message
        this.lblUnitStatusMsg = new StatusLabel(pnlUnitTab);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = COLUMNS;
        this.lblUnitStatusMsg.setLayoutData(gd);

        //
        // pnlButtons content
        //

        // check all button
        this.btnCheckAll = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.checkAll"), //$NON-NLS-1$
                                                      GridData.FILL_HORIZONTAL);
        this.btnCheckAll.setEnabled(false);
        this.btnCheckAll.setToolTipText(UTIL.getString(PREFIX + "button.checkAll.tip")); //$NON-NLS-1$
        this.btnCheckAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCheckAll();
            }
        });

        // uncheck all button
        this.btnUncheckAll = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.uncheckAll"), //$NON-NLS-1$
                                                        GridData.FILL_HORIZONTAL);
        this.btnUncheckAll.setEnabled(false);
        this.btnUncheckAll.setToolTipText(UTIL.getString(PREFIX + "button.uncheckAll.tip")); //$NON-NLS-1$
        this.btnUncheckAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleUncheckAll();
            }
        });

        // check children button
        this.btnCheckChildren = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.checkChildren"), //$NON-NLS-1$
                                                           GridData.FILL_HORIZONTAL);
        this.btnCheckChildren.setEnabled(false);
        this.btnCheckChildren.setToolTipText(UTIL.getString(PREFIX + "button.checkChildren.tip")); //$NON-NLS-1$
        this.btnCheckChildren.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCheckChildren();
            }
        });

        // uncheck children button
        this.btnUncheckChildren = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.uncheckChildren"), //$NON-NLS-1$
                                                             GridData.FILL_HORIZONTAL);
        this.btnUncheckChildren.setEnabled(false);
        this.btnUncheckChildren.setToolTipText(UTIL.getString(PREFIX + "button.uncheckChildren.tip")); //$NON-NLS-1$
        this.btnUncheckChildren.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleUncheckChildren();
            }
        });
    }

    private TreeItem findTableTreeItem( TreeItem theParent,
                                        Object theUnit ) {
        TreeItem result = null;
        TreeItem[] kids = theParent.getItems();

        if (kids.length > 0) {
            for (int i = 0; i < kids.length; i++) {
                Object data = kids[i].getData();

                // sometimes in tree expansion the data has not been set yet so be careful here
                if ((data != null) && data.equals(theUnit)) {
                    result = kids[i];
                    break;
                }
            }
        }

        return result;
    }

    RoseImporter getImporter() {
        return this.importer;
    }

    private TreeViewer getPathMapViewer() {
        return this.pathMapViewer;
    }

    String getSelectedPathMapVariable() {
        String result = null;
        ISelection selection = getPathMapViewer().getSelection();

        if (!selection.isEmpty()) {
            Object variable = ((IStructuredSelection)selection).getFirstElement();
            ITableLabelProvider lblprovider = (ITableLabelProvider)getPathMapViewer().getLabelProvider();
            result = lblprovider.getColumnText(variable, VARIABLE_COLUMN);
        }

        return result;
    }

    IUnit getSourceUnit() {
        return this.sourceUnit;
    }

    TreeViewer getUnitsViewer() {
        return this.unitsViewer;
    }

    /** Handler for when the browse button is selected to choose a source model/unit. */
    void handleBrowseSourceSelected() {
        FileDialog dialog = new FileDialog(getShell());
        dialog.setText(UTIL.getString(PREFIX + "sourceUnitChooser.title")); //$NON-NLS-1$
        dialog.setFilterExtensions(FILE_EXTENSIONS);

        String selectedUnit = dialog.open();

        // modify history if new model selected
        if (selectedUnit != null) {
            ArrayList items = new ArrayList(Arrays.asList(this.cbxSourceUnit.getItems()));

            if (!items.contains(selectedUnit)) {
                items.add(selectedUnit);
                WidgetUtil.setComboItems(this.cbxSourceUnit, items, null, false, selectedUnit);
            } else {
                this.cbxSourceUnit.setText(selectedUnit);
            }
        }
    }

    void handleCheckAll() {
        Tree tableTree = getUnitsViewer().getTree();
        TreeItem root = tableTree.getItems()[0]; // should only be one root

        // need to load all units recursively first before setting them checked
        loadUnit(this.sourceUnit, true);
        setChildrenChecked(root, true, true);

        if (!root.getChecked()) {
            root.setChecked(true);

            // since programmatically checking item does not generate a selection event,
            // call this method which calls the selection handler
            setUnitSelectionStatus(root);
        }

        // validate to set page completeness and message
        validate();

        getUnitsViewer().getTree().getColumn(NAME_COLUMN).pack();
    }

    void handleCheckChildren() {
        Tree tableTree = getUnitsViewer().getTree();
        TreeItem item = tableTree.getSelection()[0]; // should always be one

        // need to load all units recursively first before setting them checked
        loadUnit((IUnit)item.getData(), true);
        setChildrenChecked(item, true, true);

        // validate to set page completeness and message
        validate();
    }

    void handleCheckStateChanged( SelectionEvent theEvent ) {
        if (theEvent.detail == SWT.CHECK) {
            IUnit unit = (IUnit)theEvent.item.getData();
            getImporter().setUnitSelected(unit, ((TreeItem)theEvent.item).getChecked());

            // validate to set page completeness and message
            validate();
        }
    }

    void handleEditPathMapVariable() {
        UiBusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
            public void run() {
                String name = getSelectedPathMapVariable();
                String value = (String)getImporter().getPathMap().get(name);
                PathVariableDialog dlg = new PathVariableDialog(name, value);

                if (dlg.open() == Window.OK) {
                    String newValue = dlg.getValue();

                    // update variable value
                    setPathMapVariable(name, newValue);
                }
            }
        });
    }

    void handleImporterMessage( IMessage theMessage ) {
        setPageMessage(theMessage);
    }

    void handlePathVariableSelected() {
        boolean enable = !getPathMapViewer().getSelection().isEmpty();
        this.btnEditPathVariable.setEnabled(enable);
        this.btnCurrDir.setEnabled(enable);
    }

    void handleSetToCurrentDirectory() {
        setPathMapVariable(getSelectedPathMapVariable(), UTIL.getString(PREFIX + "text.value.currentDirectory")); //$NON-NLS-1$
    }

    /** Handler for changes to the Source Model combo. */
    void handleSourceModelModified() {
        final String unitName = this.cbxSourceUnit.getText();

        if ((unitName != null) && (unitName.length() > 0)) {
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run( IProgressMonitor theMonitor ) throws InvocationTargetException {
                    try {
                        SourceUnitsPage.this.sourceUnit = getImporter().loadUnit(unitName, theMonitor);
                    } catch (Exception theException) {
                        throw new InvocationTargetException(theException);
                    } finally {
                        theMonitor.done();
                    }
                }
            };

            try {
                ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
                dlg.run(true, false, op);

                if (!dlg.getProgressMonitor().isCanceled()) {
                    refreshSourceUnit();
                }
            } catch (InvocationTargetException theException) {
                UTIL.log(theException);
                WidgetUtil.showError(theException);
            } catch (InterruptedException theException) {
            }
        }

        // validate to set page completeness and message
        validate();
    }

    void handleTreeExpanded( Object theObject ) {
        if (!((IUnit)theObject).isLoaded()) {
            loadUnit((IUnit)theObject, false);
            getUnitsViewer().getTree().getColumn(NAME_COLUMN).pack();
        }
    }

    void handleUncheckAll() {
        Tree tableTree = getUnitsViewer().getTree();
        TreeItem root = tableTree.getItems()[0]; // should only be one root
        setChildrenChecked(root, false, true);

        if (root.getChecked()) {
            root.setChecked(false);

            // since programmatically checking item does not generate a selection event,
            // call this method which calls the selection handler
            setUnitSelectionStatus(root);
        }

        // validate to set page completeness and message
        validate();
    }

    void handleUncheckChildren() {
        Tree tableTree = getUnitsViewer().getTree();
        TreeItem item = tableTree.getSelection()[0]; // should always be one
        setChildrenChecked(item, false, true);

        // validate to set page completeness and message
        validate();
    }

    void handleUnitSelected( ISelection theSelection ) {
        boolean enable = true;
        IUnit selectedUnit = null;

        if (theSelection.isEmpty()) {
            enable = false;
        } else if (theSelection instanceof IStructuredSelection) {
            Object selection = ((IStructuredSelection)theSelection).getFirstElement();
            ITreeContentProvider provider = (ITreeContentProvider)getUnitsViewer().getContentProvider();
            enable = provider.hasChildren(selection);
            selectedUnit = (IUnit)selection;
        }

        this.btnCheckChildren.setEnabled(enable);
        this.btnUncheckChildren.setEnabled(enable);

        // update selection status message
        RoseImporterUiUtils.setLabelProperties(selectedUnit, this.lblUnitStatusMsg, true);

        // validate to set page completeness and message
        validate();
    }

    boolean isShowPathVariables() {
        return this.showPathVariables;
    }

    private void loadUnit( final IUnit theUnit,
                           final boolean theRecurseFlag ) {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    loadUnit(theUnit, theMonitor, theRecurseFlag);
                } catch (Exception theException) {
                    throw new InvocationTargetException(theException);
                } finally {
                    theMonitor.done();
                }
            }
        };

        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(true, false, op);

            if (!dlg.getProgressMonitor().isCanceled()) {
                getUnitsViewer().refresh(theUnit);
            }
        } catch (InvocationTargetException theException) {
            UTIL.log(theException);
            WidgetUtil.showError(theException);
        } catch (InterruptedException theException) {
        }
    }

    void loadUnit( IUnit theUnit,
                   IProgressMonitor theMonitor,
                   boolean theRecurseFlag ) {
        if (!theUnit.isLoaded()) {
            getImporter().loadUnit(theUnit, theMonitor);
        }

        if (theRecurseFlag) {
            Iterator itr = theUnit.getContainedUnits().iterator();

            while (itr.hasNext()) {
                IUnit kid = (IUnit)itr.next();
                loadUnit(kid, theMonitor, true);
            }
        }
    }

    private void packTableColumns() {
        // units table
        Tree tree = getUnitsViewer().getTree();
        for (int i = 0; i < UNITS_TBL_HDRS.length; tree.getColumn(i++).pack()) {

        }

        // path map table
        tree = getPathMapViewer().getTree();
        for (int i = 0; i < VARIABLES_TBL_HDRS.length; tree.getColumn(i++).pack()) {

        }
    }

    private void refreshSourceUnit() {
        // refresh path map table
        getPathMapViewer().setInput(getImporter().getPathMap());

        // refresh units table
        getUnitsViewer().setInput(this);

        // enable units table buttons
        this.btnCheckAll.setEnabled(getUnitsViewer().getTree().getItemCount() != 0);
        this.btnUncheckAll.setEnabled(!getImporter().getSelectedUnits().isEmpty());

        // size columns to data
        packTableColumns();
    }

    private void refreshUnit( Object theUnit ) {
        // first need to reset the expanded state of the unit's tree item to unexpanded because
        // the children may have changed
        resetExpandedState((IUnit)theUnit);

        // now refresh
        getUnitsViewer().refresh(theUnit, true);
    }

    private void resetExpandedState( IUnit theUnit ) {
        // create unit path to tree root (in reverse order)
        List unitPath = new ArrayList();
        unitPath.add(theUnit);

        IUnit parent = theUnit.getContainingUnit();

        while (parent != null) {
            unitPath.add(parent);
            parent = parent.getContainingUnit();
        }

        // now find TableTreeItem roots (only should have one)
        TreeItem[] roots = getUnitsViewer().getTree().getItems();

        if (roots.length > 0) {
            TreeItem itemToReset = roots[0];
            TreeItem tempItem = null;

            if (!unitPath.isEmpty()) {
                // walk down the tree items until find the right one
                for (int i = unitPath.size() - 2; i >= 0; i--) {
                    tempItem = findTableTreeItem(itemToReset, unitPath.get(i));

                    if (tempItem == null) {
                        break;
                    }
                    itemToReset = tempItem;
                }
            }

            // should never be null as should always have a root
            itemToReset.setExpanded(false);
        }
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizardPage#saveSettings()
     * @since 4.1
     */
    @Override
    public void saveSettings() {
        IDialogSettings settings = getDialogSettings();
        WidgetUtil.saveSettings(settings, SOURCE_MODEL_LIST, this.cbxSourceUnit);
    }

    private void setChildrenChecked( TreeItem theParent,
                                     boolean theCheckedFlag,
                                     boolean theRecurseFlag ) {
        // need to make sure the unit is loaded before getting children and before checking.
        // loading makes the children available.
        if (theCheckedFlag && !theParent.getExpanded()) {
            theParent.setExpanded(true);

            // send event so that tree expansion handler will kick in to load the unit
            Event event = new Event();
            event.item = theParent;
            getUnitsViewer().getTree().notifyListeners(SWT.Expand, event);
        }

        TreeItem[] kids = theParent.getItems();

        for (int i = 0; i < kids.length; i++) {
            // recurse if necessary
            if (theRecurseFlag) {
                if (theCheckedFlag || (!theCheckedFlag && ((IUnit)theParent.getData()).isLoaded())) {
                    setChildrenChecked(kids[i], theCheckedFlag, theRecurseFlag);
                }
            }

            if (kids[i].getChecked() != theCheckedFlag) {
                kids[i].setChecked(theCheckedFlag);

                // since programmatically checking item does not generate a selection event,
                // call this method which calls the selection handler
                setUnitSelectionStatus(kids[i]);
            }
        }
    }

    private void setPageMessage( final IMessage theMessage ) {
        // the IMessages from the importer, regardless of their severity has nothing to do with the
        // completeness of the page. see validate method.

        if (theMessage == null) {
            // generate own message
            validate();
        } else {
            Object unit = theMessage.getObject();

            if (unit == null) {
                // overall message. update page message.
                int severity = theMessage.getType();

                if (severity != IStatus.ERROR) {
                    setErrorMessage(null); // must clear in order to get other messages to show

                    if (severity == IStatus.WARNING) {
                        setMessage(theMessage.getText(), IMessageProvider.WARNING);
                    } else {
                        // no messages from importer so provide one
                        setPageMessage(null); 
                    }
                } else {
                    setErrorMessage(theMessage.getText());
                }
            } else {
                // unit message. update corresponding row.
                refreshUnit(unit);
            }
        }
    }

    void setPathMapVariable( String theName,
                             String theNewValue ) {
        getImporter().mapPath(theName, theNewValue);
        getPathMapViewer().refresh(((IStructuredSelection)getPathMapViewer().getSelection()).getFirstElement(), true);

        // need to update selected unit status msg since it's message may have changed
        IStructuredSelection selection = (IStructuredSelection)getUnitsViewer().getSelection();

        if (selection.size() == 1) {
            // update selection status message
            RoseImporterUiUtils.setLabelProperties((IUnit)selection.getFirstElement(), this.lblUnitStatusMsg, true);
        }

        // =============================================================
        // Since path has changed, we'll need to make the TreeTable in
        // the Units tab redisplay itself. [see Defect 12947 (jh)]
        // =============================================================

        // 1. capture current expanded state
        Object[] eeExpanded = getUnitsViewer().getExpandedElements();

        // 2. capture current 'checked' state
        Map mapItemCheckedStates = WidgetUtil.getCheckedStates(getUnitsViewer());

        // 3. force the input in again (this is the trick that gets the '+' sign
        // to be displayed in the case in which it was missing before this
        // 'setPath', but should be there after it ).
        Object oInput = getUnitsViewer().getInput();
        getUnitsViewer().setInput(oInput);

        // 4. restore expanded state
        getUnitsViewer().setExpandedElements(eeExpanded);

        // 5. restore 'checked' state
        WidgetUtil.setCheckedStates(getUnitsViewer(), mapItemCheckedStates);

        // 6. refresh the tree
        getUnitsViewer().refresh(true);
        packTableColumns();

        // 7. set page status
        validate();
    }

    /**
     * Should be called after the item is checked or unchecked programmatically to get the unit selected or unselected.
     * 
     * @param theItem the tree item whose unit is being selected
     */
    private void setUnitSelectionStatus( TreeItem theItem ) {
        Event event = new Event();
        event.widget = getUnitsViewer().getTree();
        event.item = theItem;
        event.detail = SWT.CHECK;
        handleCheckStateChanged(new SelectionEvent(event));
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        if (theShowFlag) {
            validate(); // to get initial msg
        }

        super.setVisible(theShowFlag);
    }

    /**
     * Validates the UI to ensure all required information is present. Sets page message and icon based on validation results.
     * Must be called whenever information is changed.
     */
    private void validate() {
        boolean pageComplete = true;

        // must have a source unit
        if (this.sourceUnit == null) {
            pageComplete = false;
            setErrorMessage(UTIL.getString(PREFIX + "msg.noSourceUnit")); //$NON-NLS-1$
        } else if (this.selectedUnits.isEmpty()) {
            // must have at least one unit selected
            pageComplete = false;
            setErrorMessage(UTIL.getString(PREFIX + "msg.noSelectedUnits")); //$NON-NLS-1$
        } else {
            // make sure all selected units have been resolved to a file on the file system
            boolean valid = true;
            Iterator itr = this.importer.getSelectedUnits().iterator();

            while (itr.hasNext()) {
                IUnit unit = (IUnit)itr.next();

                if (!unit.exists()) {
                    pageComplete = false;
                    valid = false;
                    setErrorMessage(UTIL.getString(PREFIX + "msg.selectedUnitNotFound", //$NON-NLS-1$
                                                   new Object[] {unit.getQualifiedName()}));
                    this.unitsViewer.reveal(unit);
                    break;
                }
            }

            if (valid) {
                setErrorMessage(null); // must clear in order to get other messages to show
                setMessage(UTIL.getString(PREFIX + "msg.pageComplete", //$NON-NLS-1$
                                          new Object[] {new Integer(getImporter().getSelectedUnits().size())}));
            }
        }

        setPageComplete(pageComplete);
        this.btnUncheckAll.setEnabled(!getImporter().getSelectedUnits().isEmpty());
    }

    class PathMapTreeContentProvider implements ITreeContentProvider {

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
        public Object[] getChildren( Object theInputElement ) {
            Object[] result = null;

            if (theInputElement instanceof Map) {
                Set entries = ((Map)theInputElement).entrySet();
                result = new Object[entries.size()];
                Iterator itr = entries.iterator();
                int i = 0;

                while (itr.hasNext()) {
                    result[i++] = itr.next();
                }
            } else {
                result = new Object[0];
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent( Object element ) {
            Object result = null;
            if (!(element instanceof Map)) {
                if (element instanceof TreeItem) {
                    TreeItem item = (TreeItem)element;
                    result = item.getParent();
                }
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren( Object parentElement ) {

            if (parentElement instanceof Map) {
                Set entries = ((Map)parentElement).entrySet();
                if (entries.size() > 0) {
                    return true;
                }
            }

            return false;
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

    class PathMapTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;
            Map.Entry row = (Map.Entry)theElement;

            if (theIndex == VARIABLE_COLUMN) {
                result = row.getKey().toString();
            } else if (theIndex == VALUE_COLUMN) {
                Object value = row.getValue();
                result = (value == null) ? "" : value.toString(); //$NON-NLS-1$
            } else {
                // should not happen
                result = ""; //$NON-NLS-1$
            }

            return result;
        }

    }

    class UnitsTableContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        /** Contains the source unit. */
        private Object[] rootArray;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         * @since 4.1
         */
        public Object[] getChildren( Object theParentElement ) {
            return ((IUnit)theParentElement).getContainedUnits().toArray();
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        public Object[] getElements( Object theInputElement ) {
            return this.rootArray;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         * @since 4.1
         */
        public Object getParent( Object theElement ) {
            return (getSourceUnit() == theElement) ? null : ((IUnit)theElement).getContainingUnit();
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         * @since 4.1
         */
        public boolean hasChildren( Object theElement ) {
            // this will put a + sign next to the node. when expanded the children will be accounted for.
            return true;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
            this.rootArray = new Object[] {getSourceUnit()};
        }

    }

    class UnitsTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;

            if (theElement instanceof IUnit) {
                IUnit unit = (IUnit)theElement;

                if (theColumnIndex == ERROR_ICON_COLUMN) {
                    result = RoseImporterUiUtils.getStatusImage(unit, true);
                } else if (theColumnIndex == PATH_COLUMN) {
                    result = RoseImporterUiUtils.getUnitImage(unit);
                }
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = ""; //$NON-NLS-1$

            if (theElement instanceof IUnit) {
                IUnit unit = (IUnit)theElement;

                if (theIndex == NAME_COLUMN) {
                    result = unit.getName();
                } else if (theIndex == PATH_COLUMN) {
                    result = (isShowPathVariables()) ? unit.getUnresolvedPath() : unit.getResolvedPath();
                }
            } else {
                result = (theElement == null) ? "" //$NON-NLS-1$
                : theElement.toString();
            }

            return result;
        }

    }
}

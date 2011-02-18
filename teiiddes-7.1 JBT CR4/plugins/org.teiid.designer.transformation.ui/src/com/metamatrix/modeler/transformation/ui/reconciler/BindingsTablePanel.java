/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.reconciler.datatype.DatatypeReconcilerDialog;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.table.TableSizeAdapter;

/**
 * BindingsTable
 */
public class BindingsTablePanel extends Composite {

    // Style Contants
    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL;
    private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;

    private static final String LOCKED_CHECKBOX_TEXT = UiConstants.Util.getString("BindingsTablePanel.lockedCheckbox.text"); //$NON-NLS-1$
    private static final String UP_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.upAttrButton.text"); //$NON-NLS-1$
    private static final String DOWN_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.downAttrButton.text"); //$NON-NLS-1$
    private static final String SWAP_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.swapAttrButton.text"); //$NON-NLS-1$
    private static final String TOP_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.topAttrButton.text"); //$NON-NLS-1$
    private static final String BOTTOM_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.bottomAttrButton.text"); //$NON-NLS-1$
    private static final String DELETE_ATTR_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.deleteAttrButton.text"); //$NON-NLS-1$
    private static final String RESOLVE_TYPE_BUTTON_TEXT = UiConstants.Util.getString("BindingsTablePanel.resolveTypeButton.text"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_TITLE_TEXT = UiConstants.Util.getString("BindingsTablePanel.table.title"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_DATAYPE_NOTE_TEXT = UiConstants.Util.getString("BindingsTablePanel.datatype.note"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_ATTR_COL_TEXT = UiConstants.Util.getString("BindingsTablePanel.attrCol.title"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_SQL_COL_TEXT = UiConstants.Util.getString("BindingsTablePanel.sqlColumn.title"); //$NON-NLS-1$
    private static final String DATATYPE_RECONCILER_DIALOG_TITLE = UiConstants.Util.getString("DatatypeReconciler.title.text"); //$NON-NLS-1$

    // Set the table column property names
    private final String ATTRIBUTE_COLUMN = "attribute"; //$NON-NLS-1$
    private final String SQL_COLUMN = "sql"; //$NON-NLS-1$
    private ColorManager colorManager = new ColorManager();

    // Create a BindingList and assign it to an instance variable
    BindingList bindingList;
    private Table table;
    TableViewer tableViewer;

    private Button upButton;
    private Button downButton;
    private Button swapButton;
    private Button topButton;
    private Button bottomButton;
    private Button deleteButton;
    private Button resolveTypeButton;
    private Button targetLockedCheckbox;
    // flag can disable modification of sql if not a QueryCommand
    private boolean sqlModifiable = true;
    private boolean isReadOnly = false;

    // Set column names
    String[] columnNames = new String[] {ATTRIBUTE_COLUMN, SQL_COLUMN};

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public BindingsTablePanel( Composite parent ) {
        super(parent, SWT.NONE);
        init();
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public BindingsTablePanel( Composite parent,
                               boolean readOnlyState ) {
        super(parent, SWT.NONE);
        isReadOnly = readOnlyState;
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        // ------------------------------
        // Set layout for the Composite
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        this.setLayoutData(gridData);

        WidgetFactory.createLabel(this, LABEL_GRID_STYLE, 1, BINDINGS_TABLE_DATAYPE_NOTE_TEXT);

        Composite comp = new Composite(this, SWT.NONE);
        GridData gridDataComp = new GridData(GridData.FILL_HORIZONTAL);
        comp.setLayoutData(gridDataComp);
        GridLayout gridLayout2 = new GridLayout();
        comp.setLayout(gridLayout2);
        gridLayout2.numColumns = 2;

        GridData gridDataLeft = new GridData(GridData.FILL_BOTH);
        gridDataLeft.horizontalAlignment = SWT.BEGINNING;

        targetLockedCheckbox = WidgetFactory.createCheckBox(comp, LOCKED_CHECKBOX_TEXT);
        targetLockedCheckbox.setEnabled(!isReadOnly);
        targetLockedCheckbox.setLayoutData(gridDataLeft);
        targetLockedCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                targetLockedChanged();
            }
        });

        GridData gridDataRight = new GridData(GridData.FILL_BOTH);
        gridDataRight.horizontalAlignment = SWT.END;

        CLabel leftLabel = WidgetFactory.createLabel(comp, LABEL_GRID_STYLE, 2, BINDINGS_TABLE_TITLE_TEXT);
        leftLabel.setLayoutData(gridDataRight);

        // ----------------------------------
        // Create the Table Viewer Panel
        // ----------------------------------
        createTableViewerPanel();

        // --------------------------------------
        // Create the Control Button Composite
        // --------------------------------------
        createControlButtonPanel();

        setButtonStates();
    }

    private void createTableViewerPanel() {
        // Create the table
        createTable(this);

        // Create and setup the TableViewer
        createTableViewer();
        tableViewer.setContentProvider(new BindingContentProvider());
        tableViewer.setLabelProvider(new BindingLabelProvider(true));
        // The input for the table viewer is the instance of BindingList
        bindingList = new BindingList();
        tableViewer.setInput(bindingList);
    }

    /**
     * Initialize the panel.
     */
    private void createControlButtonPanel() {
        Composite buttonComposite = new Composite(this, SWT.NONE);
        // ------------------------------
        // Set layout for the Composite
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        buttonComposite.setLayout(gridLayout);
        gridLayout.numColumns = 9;
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        buttonComposite.setLayoutData(gridData);

        topButton = WidgetFactory.createButton(buttonComposite, TOP_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        topButton.setEnabled(!isReadOnly);
        topButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                topButtonPressed();
            }
        });

        upButton = WidgetFactory.createButton(buttonComposite, UP_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        upButton.setEnabled(!isReadOnly);
        upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                upButtonPressed();
            }
        });

        swapButton = WidgetFactory.createButton(buttonComposite, SWAP_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        swapButton.setEnabled(!isReadOnly);
        swapButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                swapButtonPressed();
            }
        });

        downButton = WidgetFactory.createButton(buttonComposite, DOWN_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        downButton.setEnabled(!isReadOnly);
        downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                downButtonPressed();
            }
        });

        bottomButton = WidgetFactory.createButton(buttonComposite, BOTTOM_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        bottomButton.setEnabled(!isReadOnly);
        bottomButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                bottomButtonPressed();
            }
        });

        WidgetFactory.createLabel(buttonComposite, BUTTON_GRID_STYLE, "    "); //$NON-NLS-1$

        deleteButton = WidgetFactory.createButton(buttonComposite, DELETE_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        deleteButton.setEnabled(!isReadOnly);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                deleteButtonPressed();
            }
        });

        WidgetFactory.createLabel(buttonComposite, BUTTON_GRID_STYLE, "    "); //$NON-NLS-1$

        resolveTypeButton = WidgetFactory.createButton(buttonComposite, RESOLVE_TYPE_BUTTON_TEXT, BUTTON_GRID_STYLE);
        resolveTypeButton.setEnabled(!isReadOnly);
        resolveTypeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                resolveTypeButtonPressed();
            }
        });
    }

    /**
     * Create the Table
     */
    private void createTable( Composite parent ) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        table = new Table(parent, style);
        TableLayout layout = new TableLayout();
        table.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column with attribute
        TableColumn column1 = new TableColumn(table, SWT.LEFT, 0);
        column1.setText(BINDINGS_TABLE_ATTR_COL_TEXT);
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 2nd column with sql
        TableColumn column2 = new TableColumn(table, SWT.LEFT, 1);
        column2.setText(BINDINGS_TABLE_SQL_COL_TEXT);
        weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        createTableTooltipListeners(table);

        // add a listener to keep the table sized to it's container
        new TableSizeAdapter(table, 10);
    }

    /**
     * Create the TableViewer
     */
    private void createTableViewer() {
        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);

        tableViewer.setColumnProperties(columnNames);

        // Create the cell editors
        CellEditor[] editors = new CellEditor[columnNames.length];

        // Column 1 : Description (Free text)
        TextCellEditor textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).setTextLimit(60);
        editors[0] = textEditor;

        // Column 2 : SQL not editable
        editors[1] = null;

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);
        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new BindingCellModifier(this));
    }

    /**
     * Setup listeners for table tooltips
     */
    private void createTableTooltipListeners( final Table table ) {
        // Disable native tooltip
        table.setToolTipText(""); //$NON-NLS-1$

        // Implement a "fake" tooltip
        final Listener labelListener = new Listener() {
            public void handleEvent( Event event ) {
                Label label = (Label)event.widget;
                Shell shell = label.getShell();
                switch (event.type) {
                    case SWT.MouseDown:
                        Event e = new Event();
                        e.item = (TableItem)label.getData("_TABLEITEM"); //$NON-NLS-1$
                        // Assuming table is single select, set the selection as if
                        // the mouse down event went through to the table
                        table.setSelection(new TableItem[] {(TableItem)e.item});
                        table.notifyListeners(SWT.Selection, e);
                        shell.dispose();
                        break;
                    case SWT.MouseExit:
                        shell.dispose();
                        break;
                }
            }
        };

        // table listener
        Listener tableListener = new Listener() {
            Shell tip = null;
            Label label = null;

            public void handleEvent( Event event ) {
                switch (event.type) {
                    case SWT.Dispose:
                    case SWT.KeyDown:
                    case SWT.MouseMove: {
                        if (tip == null) break;
                        tip.dispose();
                        tip = null;
                        label = null;
                        break;
                    }
                    case SWT.MouseHover: {
                        Point pt = new Point(event.x, event.y);
                        int index = table.getTopIndex();
                        // Find the cell that the cursor is over. Set the tooltip for the cell.
                        while (index < table.getItemCount()) {
                            // get current row
                            TableItem item = table.getItem(index);
                            // Iterate columns, set tooltip text when found
                            for (int iCol = 0; iCol < columnNames.length; iCol++) {
                                // Current cell boundaries
                                Rectangle rect = item.getBounds(iCol);
                                // If cursor is within the cell, set the tooltip
                                if (rect.contains(pt)) {
                                    if (tip != null && !tip.isDisposed()) tip.dispose();
                                    tip = new Shell(getShell(), SWT.ON_TOP);
                                    tip.setLayout(new FillLayout());
                                    label = new Label(tip, SWT.NONE);
                                    label.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                                    label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                                    label.setData("_TABLEITEM", item); //$NON-NLS-1$
                                    // Get data for the row - (the Binding)
                                    Object data = item.getData();
                                    String tipText = null;
                                    if (data != null && data instanceof Binding) {
                                        // First Column - get the attribute full name
                                        if (iCol == 0) {
                                            tipText = ((Binding)data).getAttributeFullName();
                                            // Second Column - get the symbol name
                                        } else if (iCol == 1) {
                                            SingleElementSymbol symbol = ((Binding)data).getCurrentSymbol();
                                            if (symbol != null) {
                                                tipText = symbol.getName();
                                            }
                                        }
                                    }
                                    // set label text
                                    if (tipText != null) {
                                        label.setText(tipText);
                                    }
                                    // set tip and show it
                                    label.addListener(SWT.MouseExit, labelListener);
                                    label.addListener(SWT.MouseDown, labelListener);
                                    Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                                    Point pt2 = table.toDisplay(rect.x, rect.y);
                                    tip.setBounds(pt2.x, pt2.y, size.x, size.y);
                                    tip.setVisible(true);
                                }
                            }
                            index++;
                        }
                    }
                }
            }
        };

        // add the table listener
        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

    /**
     * Set the TargetLocked checkbox state
     */
    public void setTargetLocked( boolean isLocked ) {
        this.targetLockedCheckbox.setSelection(isLocked);
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingContentProvider implements IStructuredContentProvider, IBindingListViewer {
        public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
            if (newInput != null) ((BindingList)newInput).addChangeListener(this);
            if (oldInput != null) ((BindingList)oldInput).removeChangeListener(this);
        }

        public void dispose() {
            bindingList.removeChangeListener(this);
        }

        // Return the bindings as an array of Objects
        public Object[] getElements( Object parent ) {
            return bindingList.getAll().toArray();
        }

        /**
         * @see IBindingListViewer#addBinding(Binding)
         */
        public void addBinding( Binding binding ) {
            tableViewer.add(binding);
        }

        /**
         * @see IBindingListViewer#addBinding(Binding)
         */
        public void insertBinding( Binding binding,
                                   int index ) {
            tableViewer.insert(binding, index);
        }

        /**
         * @see IBindingListViewer#addBindings(Object[])
         */
        public void addBindings( Object[] bindings ) {
            tableViewer.add(bindings);
        }

        /**
         * @see IBindingListViewer#removeBinding(Binding)
         */
        public void removeBinding( Binding binding ) {
            tableViewer.remove(binding);
        }

        /**
         * @see IBindingListViewer#removeBindings(Binding[])
         */
        public void removeBindings( Object[] bindings ) {
            tableViewer.remove(bindings);
        }

        /**
         * @see IBindingListViewer#updateBindings(Binding)
         */
        public void updateBinding( Binding binding ) {
            tableViewer.update(binding, null);
        }

        /**
         * @see IBindingListViewer#updateBindings(Binding)
         */
        public void refresh( boolean updateLabels ) {
            tableViewer.refresh(updateLabels);
        }

    }

    /**
     * Add Listener for BindingsTable selections
     * 
     * @param listener the listener to add
     */
    public void addTableSelectionListener( ISelectionChangedListener listener ) {
        tableViewer.addSelectionChangedListener(listener);
    }

    /**
     * Remove Listener for BindingsTable selections
     * 
     * @param listener the listener to remove
     */
    public void removeTableSelectionListener( ISelectionChangedListener listener ) {
        tableViewer.removeSelectionChangedListener(listener);
    }

    /**
     * Add Listener for TargetLocked checkbox selections
     * 
     * @param listener the listener to add
     */
    public void addTargetLockedCheckboxListener( SelectionListener listener ) {
        targetLockedCheckbox.addSelectionListener(listener);
    }

    /**
     * Remove Listener for TargetLocked checkbox selections
     * 
     * @param listener the listener to remove
     */
    public void removeTargetLockedCheckboxListener( SelectionListener listener ) {
        targetLockedCheckbox.removeSelectionListener(listener);
    }

    /**
     * Return the column names in a collection
     * 
     * @return List containing column names
     */
    public java.util.List getColumnNames() {
        return Arrays.asList(columnNames);
    }

    /**
     * @return currently selected item
     */
    public ISelection getSelection() {
        return tableViewer.getSelection();
    }

    /**
     * Select the next Binding which is bound
     * 
     * @param binding the supplied binding
     */
    public void selectNextBound( Binding binding ) {
        Binding nextSelection = bindingList.getNextBound(binding);
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        }
    }

    /**
     * Select the next Binding which is unbound
     * 
     * @param binding the supplied binding
     */
    public void selectNextUnbound( Binding binding ) {
        Binding nextSelection = bindingList.getNextUnbound(binding);
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        }
    }

    /**
     * Select the first Binding in the binding list which is bound
     * 
     * @param binding the supplied binding
     */
    public void selectFirstBound() {
        Binding nextSelection = bindingList.getFirstBound();
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        }
    }

    /**
     * Select the first Binding in the binding list which is unbound
     * 
     * @param binding the supplied binding
     */
    public void selectFirstUnbound() {
        Binding nextSelection = bindingList.getFirstUnbound();
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        }
    }

    /**
     * Set the BindingList
     */
    public void setBindingList( BindingList list ) {
        this.bindingList = list;
        tableViewer.setInput(bindingList);

        // Sets columns to fit data - eliminates extra rows at top of table
        table.getColumn(0).pack();
        table.getColumn(1).pack();
        table.pack();

        setButtonStates();
        updateRowColors();
    }

    /**
     * Return the BindingList
     */
    public BindingList getBindingList() {
        return bindingList;
    }

    /**
     * Add a Binding to the table
     */
    public void addBinding( Binding binding ) {
        bindingList.add(binding);
    }

    /**
     * Return the current selected bindings as a list
     */
    public List getSelectedBindings() {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        return selection.toList();
    }

    /**
     * Return the tableViewer used in the composite
     */
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    /**
     * Return whether the target virtual group is locked
     */
    public boolean isTargetLocked() {
        return targetLockedCheckbox.getSelection();
    }

    /**
     * Set the enabled/disabled states of the Table Control Buttons.
     */
    public void setButtonStates() {
        if (isReadOnly) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            swapButton.setEnabled(false);
            topButton.setEnabled(false);
            bottomButton.setEnabled(false);
            deleteButton.setEnabled(false);
            resolveTypeButton.setEnabled(false);
            return;
        }
        // Initialize all buttons to disabled
        boolean upEnabled = false;
        boolean downEnabled = false;
        boolean swapEnabled = false;
        boolean deleteEnabled = false;
        boolean resolveTypeEnabled = false;

        int[] selectedBindings = table.getSelectionIndices();
        int nSelected = selectedBindings.length;

        List allBindings = bindingList.getAll();
        // Enable ResolveType button if any of the bindings is bound and has a type conflict
        Iterator iter = allBindings.iterator();
        while (iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            if (binding.isBound() && binding.hasTypeConflict()) {
                // boolean bindingHasConflict =
                // TransformationMappingHelper.hasTypeConflict(binding.getAttribute(),binding.getCurrentSymbol());
                // if(bindingHasConflict) {
                resolveTypeEnabled = true;
                break;
                // }
            }
        }

        // If the Group isnt locked, update virtual attribute controls
        if (!targetLockedCheckbox.getSelection()) {
            int nBindings = getBindingList().size();
            // DeleteButton enabled for selectedRows of 1 or more
            if (nSelected >= 1) {
                deleteEnabled = true;
            }
            // Move, Swap buttons only enabled if SQL Modifiable
            if (nBindings > 1 && sqlModifiable) {
                // number table row selections = 1
                if (nSelected == 1) {
                    if (selectedBindings[0] != nBindings - 1) {
                        downEnabled = true;
                    }
                    if (selectedBindings[0] != 0) {
                        upEnabled = true;
                    }
                    // number table row selections = 2
                } else if (nSelected == 2) {
                    swapEnabled = true;
                }
            }
        }

        upButton.setEnabled(upEnabled);
        topButton.setEnabled(upEnabled);
        downButton.setEnabled(downEnabled);
        bottomButton.setEnabled(downEnabled);
        swapButton.setEnabled(swapEnabled);
        deleteButton.setEnabled(deleteEnabled);
        resolveTypeButton.setEnabled(resolveTypeEnabled);
    }

    /**
     * update Row background colors, based on binding and type conflict status.
     */
    public void updateRowColors() {
        int rows = table.getItemCount();
        for (int i = 0; i < rows; i++) {
            TableItem item = table.getItem(i);
            Binding binding = getBindingList().get(i);
            if (!binding.isBound() || binding.hasTypeConflict()) {
                item.setBackground(colorManager.getColor(ColorManager.UNBOUND_BACKGROUND));
            } else {
                item.setBackground(colorManager.getColor(ColorManager.BOUND_BACKGROUND));
            }
        }

    }

    /**
     * Handler for Up Button
     */
    void upButtonPressed() {
        List selections = getSelectedBindings();
        if (selections.size() == 1) {
            bindingList.moveUp((Binding)selections.get(0));
        }
        tableViewer.setSelection(new StructuredSelection(selections), true);
    }

    /**
     * Handler for Down Button
     */
    void downButtonPressed() {
        List selections = getSelectedBindings();
        if (selections.size() == 1) {
            bindingList.moveDown((Binding)selections.get(0));
        }
        tableViewer.setSelection(new StructuredSelection(selections), true);
    }

    /**
     * Handler for Swap Button
     */
    void swapButtonPressed() {
        List selections = getSelectedBindings();
        if (selections.size() == 2) {
            bindingList.swap((Binding)selections.get(0), (Binding)selections.get(1));
        }
        tableViewer.setSelection(new StructuredSelection(selections), true);
    }

    /**
     * Handler for Top Button
     */
    void topButtonPressed() {
        List selections = getSelectedBindings();
        if (selections.size() == 1) {
            bindingList.moveTop((Binding)selections.get(0));
        }
        tableViewer.setSelection(new StructuredSelection(selections), true);
    }

    /**
     * Handler for Bottom Button
     */
    void bottomButtonPressed() {
        List selections = getSelectedBindings();
        if (selections.size() == 1) {
            bindingList.moveBottom((Binding)selections.get(0));
        }
        tableViewer.setSelection(new StructuredSelection(selections), true);
    }

    /**
     * Handler for Delete Button
     */
    void deleteButtonPressed() {
        List selections = getSelectedBindings();
        if (!selections.isEmpty()) {
            bindingList.removeAll(selections);
        }
        // tableViewer.setSelection(new StructuredSelection(selections),true);
        // table.showSelection();
        // BindingList refresh - will cause SQL and display to update
        bindingList.refresh(true);
    }

    /**
     * Handler for Resolve Type Button
     */
    void resolveTypeButtonPressed() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        // --------------------------------------------------
        // Create the DatatypeReconciler Dialog and Open it
        // --------------------------------------------------
        // Only pass in bindings that have a type conflict
        BindingList typeConflictBindings = new BindingList();
        for (int i = 0; i < bindingList.size(); i++) {
            Binding currentBinding = bindingList.get(i);
            if (currentBinding.isBound() && currentBinding.hasTypeConflict()) {
                typeConflictBindings.add(currentBinding);
            }
        }
        boolean isLocked = this.targetLockedCheckbox.getSelection();
        DatatypeReconcilerDialog datatypeReconcilerDialog = new DatatypeReconcilerDialog(shell, typeConflictBindings, isLocked,
                                                                                         colorManager,
                                                                                         DATATYPE_RECONCILER_DIALOG_TITLE);
        int returnStatus = datatypeReconcilerDialog.open();

        // ---------------------------------------------------------
        // Handle return - Apply Mods on OK, reject Mods on cancel
        // ---------------------------------------------------------
        if (returnStatus == Window.OK) {
            // Check the datatypeReconciler - were changes made. If so, update the "real" bindings
            if (datatypeReconcilerDialog.hasModifications()) {
                datatypeReconcilerDialog.applyBindingTypeModifications();
            }
        } else {
            datatypeReconcilerDialog.clearBindingTypeModifications();
        }

        // BindingList refresh - will cause SQL and display to update
        bindingList.refresh(true);
        updateRowColors();
        setButtonStates();
    }

    /**
     * Handler for TargetLocked checkbox
     */
    void targetLockedChanged() {
        setButtonStates();
    }
}

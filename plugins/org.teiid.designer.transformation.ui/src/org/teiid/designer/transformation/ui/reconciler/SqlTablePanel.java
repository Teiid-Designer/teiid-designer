/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.util.TransformationSqlHelper;
import org.teiid.designer.ui.common.table.TableSizeAdapter;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;

/**
 * BindingsTable
 *
 * @since 8.0
 */
public class SqlTablePanel extends Composite {

    // Style Contants
    static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_CENTER;

    private static final String SORT_BUTTON_TEXT = UiConstants.Util.getString("SqlTablePanel.sortButton.text"); //$NON-NLS-1$
    private static final String UNSORT_BUTTON_TEXT = UiConstants.Util.getString("SqlTablePanel.unsortButton.text"); //$NON-NLS-1$

    // Create a SqlList and assign it to an instance variable
    SqlList sqlList = new SqlList();
    // Keep list of original symbol names to remember original order.
    private List originalSymbolNames;
    private List availableSymbolNames;
    private Table table;
    TableViewer tableViewer;
    private Button removeButton;
    private Button clearButton;
    private Button addButton;
    private Button sortButton;
    private boolean isReadOnly = false;
    private boolean bUseOriginalOrder = true;
    private ViewerSorter viewerSorter;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public SqlTablePanel( Composite parent ) {
        super(parent, SWT.NONE);
        init();
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public SqlTablePanel( Composite parent,
                          boolean isReadOnly ) {
        super(parent, SWT.NONE);
        this.isReadOnly = isReadOnly;
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

        WidgetFactory.createLabel(this, LABEL_GRID_STYLE, 1, UiConstants.Util.getString("SqlTablePanel.sqlList.title")); //$NON-NLS-1$
        // ----------------------------------
        // Create the Table Viewer Panel
        // ----------------------------------
        createTable(this);

        // --------------------------------------
        // Create the Control Button Composite
        // --------------------------------------
        createControlButtonPanel();

        // Init button enable states
        setButtonStates();
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
        gridLayout.numColumns = 4;
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        buttonComposite.setLayoutData(gridData);
        addButton = WidgetFactory.createButton(buttonComposite,
                                               UiConstants.Util.getString("SqlTablePanel.addButton.text"), BUTTON_GRID_STYLE); //$NON-NLS-1$
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                addButtonPressed();
            }
        });
        removeButton = WidgetFactory.createButton(buttonComposite,
                                                  UiConstants.Util.getString("SqlTablePanel.removeSqlButton.text"), BUTTON_GRID_STYLE); //$NON-NLS-1$
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                removeSqlButtonPressed();
            }
        });
        clearButton = WidgetFactory.createButton(buttonComposite,
                                                 UiConstants.Util.getString("SqlTablePanel.clearButton.text"), BUTTON_GRID_STYLE); //$NON-NLS-1$
        clearButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                clearButtonPressed();
            }
        });

        // jh Defect 21404: Add sort button
        sortButton = WidgetFactory.createButton(buttonComposite, SORT_BUTTON_TEXT, BUTTON_GRID_STYLE);
        sortButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                sortButtonPressed();
            }
        });

        GridData gridData2 = (GridData)sortButton.getLayoutData();

        gridData2.widthHint = 45;
        sortButton.setLayoutData(gridData2);
    }

    /**
     * Create the Table
     */
    private void createTable( final Composite parent ) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        table = new Table(parent, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableLayout layout = new TableLayout();
        table.setLayout(layout);

        // 1st column with attribute
        TableColumn column1 = new TableColumn(table, SWT.LEFT, 0);
        column1.setText(UiConstants.Util.getString("SqlTablePanel.sqlList.column.text")); //$NON-NLS-1$
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new SqlContentProvider());
        tableViewer.setLabelProvider(new SqlLabelProvider());
        tableViewer.setInput(sqlList);
        tableViewer.getTable().setHeaderVisible(false);

        createTableTooltipListeners(table);

        // add a listener to keep the table sized to it's container
        new TableSizeAdapter(table, 10);
    }

    /**
     * Setup listeners for table tooltips
     */
    private void createTableTooltipListeners( final Table table ) {
        // Disable native tooltip
        table.setToolTipText(""); //$NON-NLS-1$

        // Implement a "fake" tooltip
        final Listener labelListener = new Listener() {
            @Override
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

        Listener tableListener = new Listener() {
            Shell tip = null;
            Label label = null;

            @Override
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
                        TableItem item = table.getItem(new Point(event.x, event.y));
                        if (item != null) {
                            if (tip != null && !tip.isDisposed()) tip.dispose();
                            tip = new Shell(getShell(), SWT.ON_TOP);
                            tip.setLayout(new FillLayout());
                            label = new Label(tip, SWT.NONE);
                            label.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                            label.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                            label.setData("_TABLEITEM", item); //$NON-NLS-1$
                            Object data = item.getData();
                            String tipText = null;
                            if (data instanceof IExpression) {
                                IQueryService queryService = ModelerCore.getTeiidQueryService();
                                tipText = queryService.getSymbolName(((IExpression)data));
                            }
                            if (tipText != null) {
                                label.setText(tipText);
                            }
                            label.addListener(SWT.MouseExit, labelListener);
                            label.addListener(SWT.MouseDown, labelListener);
                            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            Rectangle rect = item.getBounds(0);
                            Point pt = table.toDisplay(rect.x, rect.y);
                            tip.setBounds(pt.x + 10, pt.y + 10, size.x, size.y);
                            tip.setVisible(true);
                        }
                    }
                }
            }
        };
        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }

    /**
     * Handler for remove SQL Button
     */
    void removeSqlButtonPressed() {
        // Save the last selection index
        int[] selectedIndices = table.getSelectionIndices();
        int lastIndex = 0;
        if (selectedIndices.length > 0) {
            lastIndex = selectedIndices[selectedIndices.length - 1];
        }
        // Remove Selections from the sqlList
        List selections = getSelectedSymbols();
        if (!selections.isEmpty()) {
            sqlList.removeAll(selections);
        }

        // Reselect last index
        selectIndex(lastIndex);
        setButtonStates();
    }

    /**
     * Handler for remove SQL Button
     */
    void clearButtonPressed() {
        // Remove Selections from the sqlList
        List contents = new ArrayList(sqlList.getAll());
        if (!contents.isEmpty()) {
            sqlList.removeAll(contents);
        }

        // Reselect last index
        selectIndex(0);
        setButtonStates();
    }

    /**
     * Handler for remove SQL Button
     */
    void addButtonPressed() {
        if (availableSymbolNames != null && !availableSymbolNames.isEmpty()) {
            AddSqlSymbolsDialog dialog = new AddSqlSymbolsDialog(getShell(), availableSymbolNames);
            int returnStatus = dialog.open();
            if (returnStatus == Window.CANCEL) {
                return;
            }

            List newSymbols = dialog.getSelectedSymbols();
            if (newSymbols != null && !newSymbols.isEmpty()) {
                // Save the last selection index
                int[] selectedIndices = table.getSelectionIndices();
                int lastIndex = 0;
                if (selectedIndices.length > 0) {
                    lastIndex = selectedIndices[selectedIndices.length - 1];
                }

                // Find all symbols for language object

                sqlList.addAll(newSymbols);
                // Reselect last index
                selectIndex(lastIndex);
                setButtonStates();
            }
        }
    }

    /**
     * Handler for remove SQL Button
     */
    void sortButtonPressed() {

        // 1. set the sort flag
        bUseOriginalOrder = !bUseOriginalOrder;

        // 2. change the sorter
        if (bUseOriginalOrder) {
            tableViewer.setSorter(null);
        } else {
            tableViewer.setSorter(getViewerSorter());
        }

        setButtonStates();
    }

    private ViewerSorter getViewerSorter() {
        if (viewerSorter == null) {
            viewerSorter = new ViewerSorter();
        }
        return viewerSorter;
    }

    public void addTableSelectionListener( ISelectionChangedListener listener ) {
        tableViewer.addSelectionChangedListener(listener);
    }

    public void removeTableSelectionListener( ISelectionChangedListener listener ) {
        tableViewer.removeSelectionChangedListener(listener);
    }

    /**
     * Set Button States
     */
    public void setButtonStates() {
    	
        removeButton.setEnabled(true);
        clearButton.setEnabled(true);
        addButton.setEnabled(true);
        sortButton.setEnabled(true);

        if (isReadOnly) {
            removeButton.setEnabled(false);
            clearButton.setEnabled(false);
            addButton.setEnabled(false);
            return;
        }

        boolean tableIsEmpty = table.getItems() == null || table.getItems().length == 0;
        // -- update the sort button text (before we check the readonly flag)
        if (bUseOriginalOrder) {
            sortButton.setText(SORT_BUTTON_TEXT);
        } else {
            sortButton.setText(UNSORT_BUTTON_TEXT);
        }

        // Button enablements based on table content
        if (tableIsEmpty) {
            removeButton.setEnabled(false);
            clearButton.setEnabled(false);
            sortButton.setEnabled(false);
        } else {
            removeButton.setEnabled(table.getSelectionIndices().length > 0);
        }
        
        // Disable Add button if nothing to add
        if(this.availableSymbolNames==null || this.availableSymbolNames.isEmpty()) {
        	addButton.setEnabled(false);
        }
    }

    /**
     * Return the TableViewer for the Sql Table Viewer
     */
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    /**
     * Set the SqlList
     */
    public void setSqlList( SqlList list ) {
        this.sqlList = list;
        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new SqlContentProvider());
        tableViewer.setLabelProvider(new SqlLabelProvider());
        tableViewer.setInput(sqlList);
        tableViewer.getTable().setHeaderVisible(false);

        // Builder originalSymbols list
        originalSymbolNames = new ArrayList(list.size());
        for (int i = 0; i < list.size(); i++) {
        	IExpression sym = list.getSymbolAt(i);
            String shortName = TransformationSqlHelper.getSingleElementSymbolShortName(sym, false);
            originalSymbolNames.add(shortName);
        }

        setButtonStates();
    }

    /**
     * Return the SqlList
     */
    public SqlList getSqlList() {
        return sqlList;
    }

    /**
     * Return the current selected symbols as a list
     */
    public List getSelectedSymbols() {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        return selection.toList();
    }

    /**
     * add a Symbol to the list
     */
    public void addSymbol( IExpression seSymbol ) {
        if (!sqlList.containsSymbol(seSymbol)) {
            // If symbol is in the original list, add it in the right place
            int index = getInsertIndex(seSymbol);
            sqlList.insert(seSymbol, index);
        }
    }

    /**
     * Get the Index for inserting a SQL element, based on the original SQL order.
     * 
     * @param string the SQL element name
     * @return the index location to insert
     */
    private int getInsertIndex( IExpression seSymbol ) {
        int index = sqlList.size();
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        String suppliedName = queryService.getSymbolShortName(seSymbol);
        int nSymbols = sqlList.size();
        for (int i = 0; i < nSymbols; i++) {
        	IExpression currentSymbol = sqlList.getSymbolAt(i);
            String symbolName = queryService.getSymbolShortName(currentSymbol);
            if (isStringBefore(suppliedName, symbolName, originalSymbolNames)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Checks whether a string is before another string in a list.
     * 
     * @param str1 the first string
     * @param str2 the second string
     * @param list the list
     * @return true if str1 is before str2, false if not.
     */
    private boolean isStringBefore( String str1,
                                    String str2,
                                    List list ) {
        if (containsIgnoreCase(list, str1) && containsIgnoreCase(list, str2)) {
            int str1Index = indexOfIgnoreCase(list, str1);
            int str2Index = indexOfIgnoreCase(list, str2);
            return (str1Index < str2Index) ? true : false;
        }
        return false;
    }

    /**
     * this method checks whether the supplied Collection of Strings contains the supplied String, ignoring the case of the
     * strings.
     * 
     * @param collection the collection of Strings to test.
     * @param string the string to test.
     * @return true if the collection contains the string, false if not. The case of the strings are ignored.
     */
    private boolean containsIgnoreCase( Collection collection,
                                        String string ) {
        if (string == null) return false;
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            String collStr = (String)iter.next();
            if (collStr != null && collStr.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * this method gets the index of a supplied string within a supplied List, ignoring the case of the strings.
     * 
     * @param list the List of Strings to test.
     * @param string the string to test.
     * @return the index of the supplied string in the list, -1 if its not in the list
     */
    private int indexOfIgnoreCase( List list,
                                   String string ) {
        if (string == null || list == null) return -1;
        int nItems = list.size();
        for (int i = 0; i < nItems; i++) {
            String listStr = (String)list.get(i);
            if (listStr != null && listStr.equalsIgnoreCase(string)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Select the Symbol at the supplied index. If the index does not exist, then select the first symbol in the list.
     * 
     * @param index the index to select
     */
    public void selectIndex( int index ) {
    	IExpression nextSelection = sqlList.getSymbolAt(index);
        // If the index returned null, get the first symbol
        if (nextSelection == null) {
            nextSelection = sqlList.getFirstSymbol();
        }
        // Set the selection if not null
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        }
        
        setButtonStates();
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class SqlContentProvider implements IStructuredContentProvider, ISqlListViewer {
        @Override
		public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
            if (newInput != null) ((SqlList)newInput).addChangeListener(this);
            if (oldInput != null) ((SqlList)oldInput).removeChangeListener(this);
        }

        @Override
		public void dispose() {
            sqlList.removeChangeListener(this);
        }

        // Return the bindings as an array of Objects
        @Override
		public Object[] getElements( Object parent ) {
            return sqlList.getAll().toArray();
        }

        /**
         * @see IBindingListViewer#addSymbol(SingleElementSymbol)
         */
        @Override
        public void addSymbol( IExpression symbol ) {
            if (sqlList.containsSymbol(symbol)) tableViewer.add(symbol);
        }

        /**
         * @see IBindingListViewer#addSymbol(SingleElementSymbol)
         */
        @Override
        public void insertSymbol( IExpression symbol,
                                  int index ) {
            if (sqlList.containsSymbol(symbol)) tableViewer.insert(symbol, index);
        }

        /**
         * @see IBindingListViewer#addSymbols(Object[])
         */
        @Override
		public void addSymbols( Object[] symbols ) {
            for (int i = 0; i < symbols.length; i++) {
                addSymbol((IExpression)symbols[i]);
            }
        }

        /**
         * @see IBindingListViewer#removeSymbol(SingleElementSymbol)
         */
        @Override
        public void removeSymbol( IExpression symbol ) {
            tableViewer.remove(symbol);
        }

        /**
         * @see IBindingListViewer#removeSymbols(SingleElementSymbol[])
         */
        @Override
		public void removeSymbols( Object[] symbols ) {
            tableViewer.remove(symbols);
        }

        /**
         * @see IBindingListViewer#updateSymbols(SingleElementSymbol)
         */
        @Override
        public void updateSymbol( IExpression symbol ) {
            tableViewer.update(symbol, null);
        }

        /**
         * @see IBindingListViewer#updateSymbols(SingleElementSymbol)
         */
        @Override
		public void refresh( boolean updateLabels ) {
            tableViewer.refresh(updateLabels);
        }

    }

    public List getAvailableSymbolNames() {
        return this.availableSymbolNames;
    }

    public void setAvailableSymbolNames( List availableSymbolNames ) {
        this.availableSymbolNames = new ArrayList(availableSymbolNames);
        setButtonStates();
    }

    public void preDispose( boolean allowCreateAttributes ) {
        if (!getSqlList().getAll().isEmpty()) {
            MyMessageDialog myDialog = new MyMessageDialog(getShell(), allowCreateAttributes);

            int result = myDialog.open();
            boolean createAttributes = myDialog.getCreateAttributesState();
            TransformationMappingHelper.setCreateTargetAttributes(createAttributes);
            if (result == Window.OK) {
                clearButtonPressed();
            }
        }
    }

    class MyMessageDialog extends MessageDialog {
        public boolean createAttributesState = false;
        public boolean allowCreateAttributes = false;

        /**
         * @param theParentShell
         * @param theDialogTitle
         * @param theDialogTitleImage
         * @param theDialogMessage
         * @param theDialogImageType
         * @param theDialogButtonLabels
         * @param theDefaultIndex
         * @since 5.0
         */
        public MyMessageDialog( Shell theParentShell,
                                boolean allowCreateAttributes ) {
            super(theParentShell, UiConstants.Util.getString("SqlTablePanel.myMessageDialog.title"), //$NON-NLS-1$
                  null, UiConstants.Util.getString("SqlTablePanel.myMessageDialog.message"), //$NON-NLS-1$
                  MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 0);
            this.allowCreateAttributes = allowCreateAttributes;
        }

        @Override
        protected Control createCustomArea( Composite theParent ) {

            Group group = WidgetFactory.createGroup(theParent,
                                                    UiConstants.Util.getString("SqlTablePanel.myMessageDialog.groupTitle"), //$NON-NLS-1$
                                                    GridData.FILL_BOTH);
            group.setLayoutData(new GridData(GridData.FILL_BOTH));
            group.getBorderWidth();

            if (allowCreateAttributes) {
                String theText = UiConstants.Util.getString("SqlTablePanel.myMessageDialog.createAttributesMessage"); //$NON-NLS-1$
                WidgetFactory.createLabel(group, theText);

                final Button createAttributesOnExitButton = WidgetFactory.createButton(group,
                                                                                       UiConstants.Util.getString("SqlTablePanel.createAttributesOnExitButton.text"), //$NON-NLS-1$
                                                                                       LABEL_GRID_STYLE,
                                                                                       1,
                                                                                       SWT.CHECK);
                createAttributesOnExitButton.setSelection(false);
                createAttributesOnExitButton.setToolTipText(UiConstants.Util.getString("SqlTablePanel.createAttributesOnExitButton.toolTip")); //$NON-NLS-1$
                createAttributesOnExitButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected( final SelectionEvent event ) {
                        createAttributesState = createAttributesOnExitButton.getSelection();
                    }
                });
            }

            return group;
        }

        public boolean getCreateAttributesState() {
            return createAttributesState;
        }

    }
}

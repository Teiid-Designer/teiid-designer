/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent;
import org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.table.EObjectPropertiesOrderPreferences;
import org.teiid.designer.ui.table.PropertyOrder;


/**
 * @author SDelap Editor Component For Table Editor Columns
 *
 * @since 8.0
 */
public class TableEditorPreferencesComponent implements IEditorPreferencesComponent {

    private static final String DESCRIPTION = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.description"); //$NON-NLS-1$
    static final String VISIBLE_HEADER = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.visibleHeader"); //$NON-NLS-1$
    static final String YES = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.yes"); //$NON-NLS-1$
    static final String NO = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.no"); //$NON-NLS-1$
    private static final String VALIDATION_ERROR = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.validationErrorAllColumnsHidden"); //$NON-NLS-1$
    private static final String TABLE_LABEL = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.tableLabel"); //$NON-NLS-1$
    private static final String COLUMNS_LABEL = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.columnsLabel"); //$NON-NLS-1$
    private static final String UP = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.upLabel"); //$NON-NLS-1$
    private static final String DOWN = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.downLabel"); //$NON-NLS-1$
    private static final String RESTORE_DEFAULTS = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.restoreDefaults"); //$NON-NLS-1$

    private ListViewer eObjectListViewer;
    TableViewer columnTableViewer;
    private EObjectPropertiesOrderPreferences modelTableColumnUtils;
    private Map columnArrays = new HashMap();
    ArrayList modifiedEObjects = new ArrayList();
    private String columnHeader = UiPlugin.getDefault().getPluginUtil().getString("TableEditorPreferencesComponent.columnHeader"); //$NON-NLS-1$
    private ArrayList validationListeners = new ArrayList();
    private Button upButton;
    private Button downButton;

    public TableEditorPreferencesComponent() {
        this.modelTableColumnUtils = UiPlugin.getDefault().getEObjectPropertiesOrderPreferences();
        initializeColumnArrays();
    }

    private void initializeColumnArrays() {
        Iterator eObjectIterator = this.modelTableColumnUtils.getInitializedEObjects().iterator();
        while (eObjectIterator.hasNext()) {
            String eObject = (String)eObjectIterator.next();
            ArrayList columnArrayList = this.modelTableColumnUtils.getOrderedPropertyList(eObject);
            columnArrayList = cloneList(columnArrayList);
            PropertyOrder[] columnOrders = (PropertyOrder[])columnArrayList.toArray(new PropertyOrder[0]);
            this.columnArrays.put(eObject, columnOrders);
        }
    }

    private ArrayList cloneList( ArrayList list ) {
        ArrayList newList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Object newObject = ((PropertyOrder)list.get(i)).clone();
            newList.add(newObject);
        }

        return newList;
    }

    void populateColumnsList() {
        String eObject = getEObjectSelection();
        if (eObject == null) {
            this.columnTableViewer.setInput(new ArrayList());
        } else {
            this.columnTableViewer.setInput(this.columnArrays.get(eObject));
        }
    }

    /**
     * @see IEditorPreferencesComponent#createEditorPreferencesComponent(Composite)
     */
    @Override
	public Composite createEditorPreferencesComponent( Composite parent ) {
        Composite columnComposite = new Composite(parent, SWT.NONE);
        GridLayout columnLayout = new GridLayout();
        columnLayout.numColumns = 3;
        columnLayout.marginHeight = 8;
        columnLayout.marginWidth = 8;

        columnComposite.setLayout(columnLayout);

        Text text = new Text(columnComposite, SWT.WRAP | SWT.READ_ONLY);
        text.setText(DESCRIPTION);

        GridData textData = new GridData();
        textData.horizontalSpan = 3;
        text.setLayoutData(textData);

        WidgetFactory.createLabel(columnComposite, GridData.HORIZONTAL_ALIGN_BEGINNING, TABLE_LABEL);
        WidgetFactory.createLabel(columnComposite, GridData.HORIZONTAL_ALIGN_BEGINNING, COLUMNS_LABEL);

        Composite buttonComposite = new Composite(columnComposite, SWT.NONE);
        GridData buttonData = new GridData();
        buttonData.verticalSpan = 2;
        buttonData.verticalAlignment = GridData.CENTER;
        buttonData.horizontalAlignment = GridData.BEGINNING;
        buttonComposite.setLayoutData(buttonData);

        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonComposite.setLayout(buttonLayout);

        this.upButton = WidgetFactory.createButton(buttonComposite, UP, GridData.VERTICAL_ALIGN_CENTER + GridData.FILL_HORIZONTAL);
        this.upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                moveColumnUp();
            }
        });
        this.downButton = WidgetFactory.createButton(buttonComposite, DOWN, GridData.VERTICAL_ALIGN_CENTER
                                                                            + GridData.FILL_HORIZONTAL);
        this.downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                moveColumnDown();
            }
        });

        this.eObjectListViewer = new ListViewer(columnComposite, SWT.BORDER);
        this.eObjectListViewer.setLabelProvider(new TableLabelProvider());
        this.eObjectListViewer.setContentProvider(new ArrayContentProvider());
        this.eObjectListViewer.addSelectionChangedListener(new TableSelectionChangedListener());
        Menu popupMenu = new Menu(this.eObjectListViewer.getControl());
        MenuItem item = new MenuItem(popupMenu, SWT.NONE);
        item.setText(RESTORE_DEFAULTS);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                TableEditorPreferencesComponent.this.performDefaults();
            }
        });
        this.eObjectListViewer.getControl().setMenu(popupMenu);

        GridData tableData = new GridData(GridData.FILL_BOTH);
        this.eObjectListViewer.getControl().setLayoutData(tableData);

        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
        this.columnTableViewer = new TableViewer(columnComposite, tableStyle);
        this.columnTableViewer.setContentProvider(new ArrayContentProvider());
        this.columnTableViewer.setLabelProvider(new ColumnOrderLabelProvider());
        Table table = this.columnTableViewer.getTable();
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                setUpDownButtonState();
            }
        });
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText(columnHeader);
        column1.setWidth(100);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText(VISIBLE_HEADER);
        column2.setWidth(50);
        table.pack();

        GridData columnData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(columnData);

        CellEditor[] cellEditors = new CellEditor[2];
        cellEditors[1] = new CheckboxCellEditor(table, SWT.NONE);
        this.columnTableViewer.setCellEditors(cellEditors);
        this.columnTableViewer.setCellModifier(new ColumnCellModifier());
        this.columnTableViewer.setColumnProperties(new String[] {columnHeader, VISIBLE_HEADER});

        refreshLists();
        setUpDownButtonState();
        validate();
        return columnComposite;
    }

    private void refreshLists() {
        ArrayList eObjects = new ArrayList(this.columnArrays.keySet());
        Collections.sort(eObjects);
        this.eObjectListViewer.setInput(eObjects);
        if (eObjects.size() > 0) {
            ((List)this.eObjectListViewer.getControl()).setSelection(0);
            populateColumnsList();
        } else {
            this.columnTableViewer.setInput(new ArrayList());
        }
    }

    void moveColumnUp() {
        int selectionIndex = getColumnOrderSelectionIndex();
        if (selectionIndex > -1) {
            PropertyOrder temp = getCurrentColumnOrderArray()[selectionIndex];
            getCurrentColumnOrderArray()[selectionIndex] = getCurrentColumnOrderArray()[selectionIndex - 1];
            getCurrentColumnOrderArray()[selectionIndex - 1] = temp;
            this.columnTableViewer.refresh();
            setColumnSelection(selectionIndex - 1);
            setUpDownButtonState();
        }
        ((Table)this.columnTableViewer.getControl()).setFocus();
        this.modifiedEObjects.add(getEObjectSelection());
    }

    void moveColumnDown() {
        int selectionIndex = getColumnOrderSelectionIndex();
        if ((selectionIndex < getCurrentColumnOrderArray().length - 1) && (selectionIndex > -1)) {
            PropertyOrder temp = getCurrentColumnOrderArray()[selectionIndex];
            getCurrentColumnOrderArray()[selectionIndex] = getCurrentColumnOrderArray()[selectionIndex + 1];
            getCurrentColumnOrderArray()[selectionIndex + 1] = temp;
            this.columnTableViewer.refresh();
            setColumnSelection(selectionIndex + 1);
            setUpDownButtonState();
        }
        ((Table)this.columnTableViewer.getControl()).setFocus();
        this.modifiedEObjects.add(getEObjectSelection());
    }

    private void setColumnSelection( int index ) {
        ((Table)this.columnTableViewer.getControl()).setSelection(index);
    }

    private PropertyOrder[] getCurrentColumnOrderArray() {
        return (PropertyOrder[])this.columnArrays.get(getEObjectSelection());
    }

    String getEObjectSelection() {
        IStructuredSelection selection = (IStructuredSelection)this.eObjectListViewer.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        return (String)selection.getFirstElement();
    }

    private int getColumnOrderSelectionIndex() {
        Table table = (Table)this.columnTableViewer.getControl();
        return table.getSelectionIndex();
    }

    @Override
	public String getName() {
        return TABLE_LABEL;
    }

    @Override
	public String getTooltip() {
        return TABLE_LABEL;
    }

    /**
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#performDefaults()
     */
    @Override
	public void performDefaults() {
        String eObject = getEObjectSelection();
        if (eObject != null) {
            this.columnArrays.remove(eObject);
        }
        this.modifiedEObjects.add(eObject);
        refreshLists();
        setUpDownButtonState();
        validate();
    }

    /**
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#performOk()
     */
    @Override
	public boolean performOk() {
        for (int i = 0; i < this.modifiedEObjects.size(); i++) {
            String eObject = (String)this.modifiedEObjects.get(i);
            // Test if defaults were restored which removes the eObject
            if (!this.columnArrays.containsKey(eObject)) {
                this.modelTableColumnUtils.removeEObject(eObject);
            } else {
                PropertyOrder[] columns = (PropertyOrder[])this.columnArrays.get(eObject);
                for (int x = 0; x < columns.length; x++) {
                    columns[x].setOrder(x + 1);
                }
                this.modelTableColumnUtils.replaceColumnsList(eObject, getListFromArray(columns));

            }
        }
        
        this.modelTableColumnUtils.firePropertiesChanged(this.modifiedEObjects);
        UiPlugin.getDefault().extractModelTableColumnUtilsToPreferenceStore(); // persist changes
        return true;
    }

    private ArrayList getListFromArray( PropertyOrder[] columns ) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < columns.length; i++) {
            list.add(columns[i]);
        }
        return list;
    }

    class TableLabelProvider implements ILabelProvider {

        @Override
		public Image getImage( Object element ) {
            return null;
        }

        @Override
		public String getText( Object element ) {
            return CoreStringUtil.computePluralForm(CoreStringUtil.computeDisplayableForm(element.toString()));
        }

        @Override
		public void addListener( ILabelProviderListener listener ) {

        }

        @Override
		public void dispose() {
        }

        @Override
		public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        @Override
		public void removeListener( ILabelProviderListener listener ) {

        }
    }

    class ColumnOrderLabelProvider implements ITableLabelProvider {
        public String getText( Object element ) {
            return element.toString();
            // return ((ColumnOrder) element).getName();
        }

        public Image getImage( Object element ) {
            return null;
        }

        @Override
		public void addListener( ILabelProviderListener listener ) {

        }

        @Override
		public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }

        @Override
		public String getColumnText( Object element,
                                     int columnIndex ) {
            PropertyOrder columnOrder = (PropertyOrder)element;
            if (columnIndex == 0) {
                return columnOrder.getName();
            }
            return columnOrder.isVisible() ? YES : NO;
        }

        @Override
		public void dispose() {
        }

        @Override
		public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        @Override
		public void removeListener( ILabelProviderListener listener ) {

        }

    }

    void setUpDownButtonState() {
        this.upButton.setEnabled(getColumnOrderSelectionIndex() > 0);
        this.downButton.setEnabled((getColumnOrderSelectionIndex() > -1)
                                   && (getColumnOrderSelectionIndex() < (getCurrentColumnOrderArray().length - 1)));
    }

    void clearColumnSelection() {
        this.columnTableViewer.getTable().setSelection(-1);
    }

    class TableSelectionChangedListener implements ISelectionChangedListener {
        @Override
		public void selectionChanged( SelectionChangedEvent event ) {
            clearColumnSelection();
            populateColumnsList();
            setUpDownButtonState();
        }
    }

    class ColumnCellModifier implements ICellModifier {
        @Override
		public boolean canModify( Object element,
                                  String property ) {
            return property.equals(VISIBLE_HEADER);
        }

        @Override
		public Object getValue( Object element,
                                String property ) {
            return new Boolean(((PropertyOrder)element).isVisible());
        }

        @Override
		public void modify( Object element,
                            String property,
                            Object value ) {
            PropertyOrder columnOrder = (PropertyOrder)((TableItem)element).getData();
            columnOrder.setVisible(((Boolean)value).booleanValue());
            TableEditorPreferencesComponent.this.columnTableViewer.update(columnOrder, null);
            TableEditorPreferencesComponent.this.modifiedEObjects.add(getEObjectSelection());
            validate();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#addValidationListener(org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener)
     */
    @Override
	public void addValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.preferences.IEditorPreferencesComponent#removeValidationListener(org.teiid.designer.ui.common.preferences.IEditorPreferencesValidationListener)
     */
    @Override
	public void removeValidationListener( IEditorPreferencesValidationListener listener ) {
        this.validationListeners.remove(listener);
    }

    public void fireValidationStatus( boolean validationStatus,
                                      String message ) {
        for (int i = 0; i < this.validationListeners.size(); i++) {
            ((IEditorPreferencesValidationListener)this.validationListeners.get(i)).validationStatus(validationStatus, message);
        }
    }

    @Override
	public void validate() {
        boolean valid = true;
        Iterator eObjectsIterator = this.columnArrays.keySet().iterator();
        while (eObjectsIterator.hasNext()) {
            Object key = eObjectsIterator.next();
            boolean eObjectValid = false;
            PropertyOrder[] columnOrders = (PropertyOrder[])this.columnArrays.get(key);
            for (int i = 0; i < columnOrders.length; i++) {
                if (columnOrders[i].isVisible()) {
                    eObjectValid = true;
                    break;
                }
            }
            if (!eObjectValid) {
                valid = false;
                break;
            }
        }
        if (valid) {
            fireValidationStatus(valid, null);
        } else {
            fireValidationStatus(valid, VALIDATION_ERROR);
        }
    }
}

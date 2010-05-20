/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.table.TableSizeAdapter;

/**
 * Panel used by AddSqlSymbolsDialog Contains a table viewer with two columns. One for Symbol Name and one for Location.
 * 
 * @since 5.0
 */
public class AddSqlSymbolsPanel extends Composite {

    private int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private String TABLE_LABEL = UiConstants.Util.getString("AddSqlSymbolsPanel.tableLabel"); //$NON-NLS-1$
    private String COLUMN_1_LABEL = UiConstants.Util.getString("AddSqlSymbolsPanel.column_1_Label"); //$NON-NLS-1$
    private String COLUMN_2_LABEL = UiConstants.Util.getString("AddSqlSymbolsPanel.column_2_Label"); //$NON-NLS-1$
    private Table table;
    private TableViewer tableViewer;
    List availableSymbols = Collections.EMPTY_LIST;

    /**
     * @since 5.0
     */
    public AddSqlSymbolsPanel( Composite parent,
                               List availableSymbols ) {
        super(parent, SWT.NONE);
        this.availableSymbols = availableSymbols;
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

        WidgetFactory.createLabel(this, LABEL_GRID_STYLE, 1, TABLE_LABEL);
        // ----------------------------------
        // Create the Table Viewer Panel
        // ----------------------------------
        createTable(this);
    }

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
        column1.setText(COLUMN_1_LABEL);
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 2nd column with attribute
        TableColumn column2 = new TableColumn(table, SWT.LEFT, 1);
        column2.setText(COLUMN_2_LABEL);
        weight = new ColumnWeightData(2);
        layout.addColumnData(weight);

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new MySqlContentProvider());
        tableViewer.setLabelProvider(new MySqlLabelProvider());
        tableViewer.setInput(availableSymbols);

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
                            if (data != null && data instanceof SingleElementSymbol) {
                                tipText = ((SingleElementSymbol)data).getName();
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
     * @return
     * @since 5.0
     */
    public List getSelectedSymbols() {
        ISelection theSelection = tableViewer.getSelection();
        if (theSelection == null || theSelection.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return SelectionUtilities.getSelectedObjects(theSelection);
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class MySqlContentProvider implements IStructuredContentProvider {
        public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
        }

        public void dispose() {
        }

        // Return the bindings as an array of Objects
        public Object[] getElements( Object parent ) {
            return availableSymbols.toArray();
        }

    }

    /**
     * @since 5.0
     */
    class MySqlLabelProvider extends SqlLabelProvider {

        @Override
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            String columnText = "EMPTY"; //$NON-NLS-1$

            switch (theColumnIndex) {
                case 0: {
                    columnText = super.getColumnText(theElement, theColumnIndex);
                }
                    break;

                case 1: {
                    if (theElement != null) {
                        // Alias Symbol
                        if (theElement instanceof AliasSymbol) {
                            AliasSymbol aSymbol = (AliasSymbol)theElement;
                            SingleElementSymbol uSymbol = aSymbol.getSymbol();
                            if (uSymbol != null) {
                                EObject singleElementEObject = TransformationSqlHelper.getSingleElementSymbolEObject(uSymbol);
                                if (singleElementEObject != null) {
                                    columnText = getAppendedPath(singleElementEObject);
                                }
                            }
                            // SingleElementSymbol
                        } else if (theElement instanceof SingleElementSymbol) {
                            EObject singleElementEObject = TransformationSqlHelper.getSingleElementSymbolEObject((SingleElementSymbol)theElement);
                            if (singleElementEObject != null) {
                                columnText = getAppendedPath(singleElementEObject);
                            }
                        }
                    }
                }
                    break;
            }
            return columnText;
        }

        private String getAppendedPath( EObject eObj ) {
            if (TransformationHelper.isMappingClass(eObj)) {
                MappingClass mc = (MappingClass)eObj;
                EObject doc = mc.getMappingClassSet().getTarget();
                IPath pathToDoc = ModelerCore.getModelEditor().getFullPathToParent(doc);
                pathToDoc = pathToDoc.append(ModelerCore.getModelEditor().getName(doc));
                return pathToDoc.toString();
            }

            return ModelerCore.getModelEditor().getFullPathToParent(eObj).toString();
        }

    }
}

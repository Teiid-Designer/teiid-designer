/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.common.table;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 *
 */
public class TableViewerBuilder {

    /*
     * Parent single composite for table viewer. Required by the TableColumnLayout
     */
    private final Composite tableComposite;

    /*
     * Table column layout that proportions columns in its parent table allowing
     * the parent composite to be resized.
     */
    private final TableColumnLayout tableColumnLayout;

    /*
     * Table Viewer built by this builder
     */
    private final TableViewer tableViewer;


    /**
     * @param parent
     * @param tableViewerStyles 
     */
    public TableViewerBuilder(Composite parent, int tableViewerStyles) {
        // Required due to the use of TableColumnLayout for the child table's layout.
        tableComposite = new Composite(parent, SWT.NONE);
        if (parent.getLayout() instanceof GridLayout)
            GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);

        // Allows for % weighting of the columns so that Name column is slightly longer
        tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        this.tableViewer = new TableViewer(tableComposite, tableViewerStyles);

        Table table = this.tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
    }

    /**
     * @return the tableComposite
     */
    public Composite getTableComposite() {
        return this.tableComposite;
    }

    /**
     * @return the tableViewer
     */
    public TableViewer getTableViewer() {
        return this.tableViewer;
    }

    /**
     * @return table control
     */
    public Table getTable() {
        return this.tableViewer.getTable();
    }


    /**
     * @return table control
     */
    public Table getControl() {
        return getTable();
    }

    /**
     * @return table viewer's current selection
     */
    public ISelection getSelection() {
        return this.tableViewer.getSelection();
    }

    /**
     * Set the table viewer's selection
     *
     * @param selection
     */
    public void setSelection(ISelection selection) {
        this.tableViewer.setSelection(selection);
    }

    /**
     * Set the table viewer's selection
     *
     * @param selection
     * @param reveal
     */
    public void setSelection(ISelection selection, boolean reveal) {
        this.tableViewer.setSelection(selection, reveal);
    }

    /**
     * @param contentProvider
     */
    public void setContentProvider(IContentProvider contentProvider) {
        tableViewer.setContentProvider(contentProvider);
    }

    /**
     * @param labelProvider
     */
    public void setLabelProvider(IBaseLabelProvider labelProvider) {
        tableViewer.setLabelProvider(labelProvider);
    }

    /**
     * @param comparator
     */
    public void setComparator(ViewerComparator comparator) {
        tableViewer.setComparator(comparator);
    }

    /**
     * @param selectionChangedListener
     */
    public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        this.tableViewer.addSelectionChangedListener(selectionChangedListener);
    }

    /**
     * @param doubleClickListener
     */
    public void addDoubleClickListener(IDoubleClickListener doubleClickListener) {
        this.tableViewer.addDoubleClickListener(doubleClickListener);
    }

    /**
     * @param columnStyle
     * @param weight 
     * @param minSize 
     * @param resizeable
     * @return new column added to the table viewer with the given weight and minimum size
     */
    public TableViewerColumn createColumn(int columnStyle, int weight, int minSize, boolean resizeable) {
        TableViewerColumn column = new TableViewerColumn(this.tableViewer, columnStyle);
        this.tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(weight, minSize, resizeable));
        return column;
    }

    /**
     * @param input
     */
    public void setInput(Object input) {
        this.tableViewer.setInput(input);
    }

    /**
     * Refresh the table viewer
     */
    public void refresh() {
        this.tableViewer.refresh();
    }

    /**
     * Refresh the table viewer's element
     *
     * @param element
     */
    public void refresh(Object element) {
        this.tableViewer.refresh(element);
    }

    /**
     * Add the given element to the table viewer
     *
     * @param element
     */
    public void add(Object element) {
        this.tableViewer.add(element);
    }
}

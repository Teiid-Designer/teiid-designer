/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;

/**
 * TableViewerSorter is a ViewerSorter for TableViewers. It automatically hooks up the TableViewer's TableColumn for selection and
 * decorates the columns with the appropriate icons.
 */
public class TableViewerSorter extends ViewerSorter implements UiConstants.Images {

    /** The sorted column of the TableViewer should be sorted ascending */
    public static final int ASCENDING = 0;
    /** The sorted column of the TableViewer should be sorted ascending */
    public static final int DESCENDING = 1;

    private static final int UNSORT = 2;

    TableViewer viewer;
    private int sortColumn = 0;
    private int sortOrder = UNSORT;

    /**
     * This class handles selections of the column headers. Selection of the column header will cause resorting of the shown tasks
     * using that column's sorter. Repeated selection of the header will toggle sorting order (ascending versus descending).
     */
    private SelectionListener headerListener = new SelectionAdapter() {
        /**
         * Handles the case of user selecting the header area. If the column has not been selected previously, it will set the
         * sorter of that column to be the current tasklist sorter. Repeated presses on the same column header will toggle sorting
         * order (ascending/descending).
         */
        @Override
        public void widgetSelected( SelectionEvent e ) {
            // column selected - need to sort
            setSortColumn(viewer.getTable().indexOf((TableColumn)e.widget));
            viewer.refresh();
        }
    };

    /**
     * Construct an instance of TableViewerSorter. When first displayed the table will not be sorted.
     * 
     * @param tableViewer this sorter's TableViewer
     */
    public TableViewerSorter( TableViewer tableViewer ) {
        this.viewer = tableViewer;
        setSortListener();
    }

    /**
     * Construct an instance of TableViewerSorter and specify it's initial sort characteristics.
     * 
     * @param tableViewer this sorter's TableViewer
     * @param initialColumn the initial column that should be sorted when the table is first displayed.
     * @param initialSortOrder the initial sort order that should be displayed when the table is first displayed. Use either
     *        ASCENDING or DESCENDING
     */
    public TableViewerSorter( TableViewer tableViewer,
                              int initialColumn,
                              int initialSortOrder ) {
        this(tableViewer);
        sortColumn = initialColumn;
        sortOrder = initialSortOrder;
    }

    /**
     * Subclasses may implement this method to adapt between the row-level element that is exposed by Table and the column-level
     * data that is to be sorted. The baseclass has a comparator variable <code>collator</code> which may be used to compare basic
     * types; for example:
     * <p>
     * <code>
     *     collator.compare(element1.getValue(columnIndex), element2.getValue(columnIndex));
     *  </code>
     * </p>
     * 
     * @param viewer
     * @param object1
     * @param object2
     * @param column
     * @return a positive integer if the data for element1 is greater than the data at element2, or a negative integer if the data
     *         for element1 is less than the data at element2, or zero if they are equal.
     */
    protected int compareColumn( final TableViewer viewer,
                                 final Object object1,
                                 final Object object2,
                                 final int column ) {
        return super.compare(this.viewer, object1, object2);
    }

    public void setSortListener() {
        TableColumn[] columns = viewer.getTable().getColumns();
        for (int i = 0; i < columns.length; ++i) {
            columns[i].addSelectionListener(headerListener);
        }
    }

    public boolean isUnsorted() {
        return this.sortOrder == UNSORT;
    }

    void setSortColumn( int columnIndex ) {

        if (this.sortColumn == columnIndex) {
            // if this is another click on the same column, increment the sort order
            if (sortOrder == ASCENDING) {
                sortOrder = DESCENDING;
            } else if (sortOrder == DESCENDING) {
                sortOrder = UNSORT;
            } else {
                sortOrder = ASCENDING;
            }
        } else {
            sortOrder = ASCENDING;
            if (this.sortColumn >= 0) {
                viewer.getTable().getColumn(this.sortColumn).setImage(null);
            }
        }

        setImageForSortOrder(columnIndex, sortOrder);

        this.sortColumn = columnIndex;
    }

    private void setImageForSortOrder( int columnIndex,
                                       int sorOrder ) {

        switch (sortOrder) {
            case ASCENDING:
                viewer.getTable().getColumn(columnIndex).setImage(UiPlugin.getDefault().getImage(ASCENDING_ICON));
                break;
            case DESCENDING:
                viewer.getTable().getColumn(columnIndex).setImage(UiPlugin.getDefault().getImage(DESCENDING_ICON));
                break;
            default:
                viewer.getTable().getColumn(columnIndex).setImage(null);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare( Viewer viewer,
                        Object e1,
                        Object e2 ) {
        int result = 0;

        if (sortColumn >= 0) {

            // only run the collator if we are sorting
            if (this.sortOrder != UNSORT) {
                result = compareColumn((TableViewer)viewer, e1, e2, sortColumn);
            }

            if (this.sortOrder == DESCENDING) {
                // reverse the compare result for descending
                if (result > 0) {
                    result = -1;
                } else if (result < 0) {
                    result = 1;
                }
            }
        }
        setImageForSortOrder(sortColumn, sortOrder);
        return result;
    }
}

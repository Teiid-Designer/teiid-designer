/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * The <code>TableColumnSelectionHelper</code> class can be used to keep track of the selected row and selected column of a table.
 */
public class TableColumnSelectionHelper {

    private int selectedColumn = -1;

    private int selectedRow = -1;

    private Table table;

    public TableColumnSelectionHelper( Table theTable ) {
        construct(theTable);
    }

    public TableColumnSelectionHelper( TableViewer theViewer ) {
        ArgCheck.isNotNull(theViewer);
        construct(theViewer.getTable());
    }

    protected void construct( Table theTable ) {
        ArgCheck.isNotNull(theTable);
        table = theTable;
        table.addMouseListener(new TableMouseListener());
        table.addKeyListener(new TableKeyListener());
        table.addSelectionListener(new TableSelectionListener());
        handleSelectionEvent(null);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int[] getSelectedRowAndColumn() {
        return new int[] {getSelectedRow(), getSelectedColumn()};
    }

    protected void handleKeyEvent( KeyEvent theEvent ) {
        // Note: tab, left arrow, right arrow
        if (theEvent.keyCode == SWT.ARROW_UP) {
            selectedColumn = -1;
        } else if (theEvent.keyCode == SWT.ARROW_DOWN) {
            selectedColumn = -1;
        }
    }

    protected void handleMouseEvent( MouseEvent theEvent ) {
        TableItem[] selection = table.getSelection();

        if (selection.length > 0) {
            for (int numCols = table.getColumnCount(), i = 0; i < numCols; i++) {
                Rectangle bounds = selection[0].getBounds(i);

                if (bounds.contains(theEvent.x, theEvent.y)) {
                    selectedColumn = i;
                    break;
                }
            }

            selectedRow = table.getSelectionIndex();
        }
    }

    protected void handleSelectionEvent( SelectionEvent theEvent ) {
        int[] indexes = table.getSelectionIndices();

        if (indexes.length > 0) {
            selectedRow = indexes[0];
        } else {
            selectedRow = -1;
            selectedColumn = -1;
        }
    }

    class TableKeyListener extends KeyAdapter {

        /**
         * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
         */
        @Override
        public void keyPressed( KeyEvent theEvent ) {
            handleKeyEvent(theEvent);
        }
    }

    class TableMouseListener extends MouseAdapter {

        /**
         * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
         */
        @Override
        public void mouseDown( MouseEvent theEvent ) {
            handleMouseEvent(theEvent);
        }
    }

    class TableSelectionListener extends SelectionAdapter {

        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected( SelectionEvent theEvent ) {
            handleSelectionEvent(theEvent);
        }
    }

}

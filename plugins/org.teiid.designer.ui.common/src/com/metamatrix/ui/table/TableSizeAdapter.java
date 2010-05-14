/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * TableSizeAdapter is a hack around the windows table look & feel behavior that leaves extra
 * space to the right of the table columns, which therefore looks like an extra column.  This
 * class currently supports tables containing EXACTLY 2 columns, and will throw a RuntimeException
 * if the column count is not == 2.
 */
public class TableSizeAdapter extends ControlAdapter {

    private Table table;
    private int margin;
    
    public TableSizeAdapter(Table table, int margin) {
        this.table = table;
        this.margin = margin;
        table.getParent().addControlListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     */
    @Override
    public void controlResized(ControlEvent e) {
        Composite container = table.getParent();
        Rectangle area = container.getClientArea();
        Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        int width = area.width - 2*table.getBorderWidth() - margin; //swjTODO: find where this extra space is coming from.
        if (preferredSize.y > area.height) {
            // Subtract the scrollbar width from the total column width
            // if a vertical scrollbar will be required
            Point vBarSize = table.getVerticalBar().getSize();
            width -= vBarSize.x;
        }
        Point oldSize = table.getSize();
        
        TableColumn[] columns = table.getColumns();
        if ( columns.length == 2 ) {
            if (oldSize.x > area.width) {
                // table is getting smaller so make the columns 
                // smaller first and then resize the table to
                // match the client area width
                columns[0].setWidth(width/2);
                columns[1].setWidth(width - columns[0].getWidth());
                table.setSize(area.width, area.height);
            } else {
                // table is getting bigger so make the table 
                // bigger first and then make the columns wider
                // to match the client area width
                table.setSize(area.width, area.height);
                columns[0].setWidth(width/2);
                columns[1].setWidth(width - columns[0].getWidth());
            }    
        } else if ( columns.length == 1 ) {
            columns[0].setWidth(width);
//        } else if ( columns.length > 2 ) {
//            //modTODO: figure out how to support n columns (by resizing only the last column).
//            throw new RuntimeException("TableSizeAdapter does not support tables containing " + columns.length + " columns"); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

}

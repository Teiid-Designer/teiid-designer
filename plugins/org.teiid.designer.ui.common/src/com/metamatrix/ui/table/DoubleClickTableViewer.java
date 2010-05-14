/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * DoubleClickTableViewer is a hack into TableViewer that changes it's hard-coded single-click to edit behavior into double-click
 * to edit. It works by intercepting the CellModifier and wrapping it with a DoubleClickCellModifier, which basically disables
 * cell editing unless a double-click occurs. Then this class hooks up a double-click mouse listener to the table and directly
 * calls editElement on the TableViewer.
 */
public class DoubleClickTableViewer extends TableViewer {

    boolean editEnabled = false;
    private Table tableControl;

    /**
     * Construct an instance of DoubleClickTableViewer.
     * 
     * @param parent
     * @param style
     */
    public DoubleClickTableViewer( Composite parent,
                                   int style ) {
        super(parent, style);
    }

    /**
     * @see org.eclipse.jface.viewers.ContentViewer#hookControl(org.eclipse.swt.widgets.Control)
     */
    @Override
    protected void hookControl( Control control ) {
        super.hookControl(control);

        // hook up a double-click listener
        tableControl = (Table)control;
        tableControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick( MouseEvent e ) {
                activateEdit(e);
            }
        });
    }

    protected void activateEdit( MouseEvent e ) {

        // calculate the column from the mouse location
        int columnToEdit;
        int columns = tableControl.getColumnCount();
        if (columns == 0) {
            // If no TableColumn, Table acts as if it has a single column
            // which takes the whole width.
            columnToEdit = 0;
        } else {
            columnToEdit = -1;

            Item[] selection = tableControl.getSelection();
            if (selection.length != 1) return;

            TableItem tableItem = (TableItem)selection[0];
            for (int i = 0; i < columns; i++) {
                Rectangle bounds = tableItem.getBounds(i);
                if (bounds.contains(e.x, e.y)) {
                    columnToEdit = i;
                    break;
                }
            }
            if (columnToEdit == -1) {
                return;
            }
        }

        // allow the DoubleClickCellModifier to edit the value
        this.editEnabled = true;

        // call editElement directly on the TableViewer
        renameInline(columnToEdit);

    }

    public void renameInline( int columnToEdit ) {
        // call editElement directly on the TableViewer
        IStructuredSelection selection = (IStructuredSelection)super.getSelection();
        Object element = selection.getFirstElement();
        this.editEnabled = true;
        super.editElement(element, columnToEdit);

        // disable the DoubleClickCellModifier's canModify method once the cell editor is finished
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                editEnabled = false;
            }
        });
    }

    /**
     * @see org.eclipse.jface.viewers.TableViewer#setCellModifier(org.eclipse.jface.viewers.ICellModifier)
     */
    @Override
    public void setCellModifier( ICellModifier modifier ) {
        super.setCellModifier(new DoubleClickCellModifier(modifier));
    }

    /**
     * DoubleClickCellModifier is a wrapper for a real ICellModifier that answers false to canModify when a single-click occurs on
     * the table. This is necessary to disable the single-click-to-edit behavior that is hard-coded into TableViewer.
     */
    class DoubleClickCellModifier implements ICellModifier {
        private ICellModifier delegate;

        public DoubleClickCellModifier( ICellModifier modifier ) {
            delegate = modifier;
        }

        public boolean canModify( Object element,
                                  String property ) {
            // check the outer class
            if (editEnabled) {
                // check the delegate
                return delegate.canModify(element, property);
            }
            return false;
        }

        public Object getValue( Object element,
                                String property ) {
            return delegate.getValue(element, property);
        }

        public void modify( Object element,
                            String property,
                            Object value ) {
            delegate.modify(element, property, value);
        }
    }
}

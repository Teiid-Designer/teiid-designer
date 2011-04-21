/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.metamatrix.ui.internal.widget.ButtonProvider;

/**
 * @param <T>
 */
public final class TableAndToolBar<T> {

    private final Table<T> table;
    private final ToolBarManager toolBarMgr;

    /**
     * @param <V>
     * @param parent the parent panel (never <code>null</code>)
     * @param span the layout data horizontal span
     * @param tableProvider
     * @param columnProviders
     */
    public <V> TableAndToolBar( final Composite parent,
                                     final int span,
                                     final TableProvider<T> tableProvider,
                                     final ColumnProvider<T, V>... columnProviders ) {
        // Create table
        table = new Table<T>(parent, tableProvider, columnProviders);

        // Create button bar
        ToolBar toolBar = new ToolBar(parent, SWT.PUSH | SWT.BORDER);
        this.toolBarMgr = new ToolBarManager(toolBar);

        // add doubleclick listener
        if (tableProvider.isDoubleClickSupported()) {
            getViewer().addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick( final DoubleClickEvent event ) {
                    tableProvider.doubleClicked((T)((IStructuredSelection)event.getSelection()).getFirstElement());
                }
            });
        }
    }

    /**
     * @param buttonProvider the button provider (may not be <code>null</code>)
     */
    public void add( final ButtonProvider buttonProvider ) {
        assert (buttonProvider != null);

        // add a separator between toolbar buttons
        if (this.toolBarMgr.getItems().length != 0) {
            this.toolBarMgr.add(new Separator());
        }

        // create action using information from button provider
        final IAction action = new Action(buttonProvider.getText(), SWT.BORDER) {
            @Override
            public void run() {
                // let provider know button was pushed and refresh tree
                buttonProvider.selected((IStructuredSelection)getViewer().getSelection());
                getViewer().refresh();
            }
        };

        action.setToolTipText(buttonProvider.getToolTip());
        action.setImageDescriptor(buttonProvider.getImageDescriptor());
        this.toolBarMgr.add(action); // add to toolbar

        // initial enablement
        if (!buttonProvider.isEnabled((IStructuredSelection)getViewer().getSelection())) {
            action.setEnabled(false);
        }

        // update toolbar to show new action
        this.toolBarMgr.update(true);

        // add viewer selection listener
        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean enabled = buttonProvider.isEnabled((IStructuredSelection)event.getSelection());

                if (enabled != action.isEnabled()) {
                    action.setEnabled(enabled);
                }
            }
        });
    }

    /**
     * @return table the table (never <code>null</code>)
     */
    public Table<T> getTable() {
        return table;
    }

    /**
     * @return the table viewer (never <code>null</code>)
     */
    public TableViewer getViewer() {
        return table.getViewer();
    }

    /**
     * @param input the viewer input
     */
    public void setInput( final Object input ) {
        table.setInput(input);
    }

}

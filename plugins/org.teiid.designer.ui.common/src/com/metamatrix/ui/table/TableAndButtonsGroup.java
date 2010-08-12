/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.ButtonProvider;

/**
 * @param <T>
 */
public final class TableAndButtonsGroup<T> {

    static final String ADD_BUTTON = UiConstants.Util.getString("addButton"); //$NON-NLS-1$
    static final String EDIT_BUTTON = UiConstants.Util.getString("editButton"); //$NON-NLS-1$
    static final String REMOVE_BUTTON = UiConstants.Util.getString("removeButton"); //$NON-NLS-1$
    static final String NEW_BUTTON = UiConstants.Util.getString("newButton"); //$NON-NLS-1$

    private final Group group;

    final Table<T> table;
    final Composite buttonBar;
    final Map<Button, ButtonProvider> buttonProvidersByButton = new ConcurrentHashMap<Button, ButtonProvider>();

    /**
     * @param <V>
     * @param parent
     * @param title
     * @param tableProvider
     * @param columnProviders
     */
    public <V> TableAndButtonsGroup( final Composite parent,
                                     final String title,
                                     final int span,
                                     final TableProvider<T> tableProvider,
                                     final ColumnProvider<T, V>... columnProviders ) {
        // Create group surrounding table and buttons
        group = new Group(parent, SWT.NONE);
        group.setText(title);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, span, 1));
        // Create table
        table = new Table<T>(group, tableProvider, columnProviders);
        // Create button bar
        buttonBar = WidgetFactory.createPanel(group, SWT.NO_TRIM, GridData.VERTICAL_ALIGN_CENTER);
        if (tableProvider.isDoubleClickSupported()) table.getViewer().addDoubleClickListener(new IDoubleClickListener() {

            @SuppressWarnings( "unchecked" )
            @Override
            public void doubleClick( final DoubleClickEvent event ) {
                tableProvider.doubleClicked((T)((IStructuredSelection)event.getSelection()).getFirstElement());
            }
        });
    }

    /**
     * @param buttonProvider
     */
    public void add( final ButtonProvider buttonProvider ) {
        final Button button = WidgetFactory.createButton(buttonBar, buttonProvider.getText(), GridData.HORIZONTAL_ALIGN_FILL
                                                                                              | GridData.HORIZONTAL_ALIGN_CENTER);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                buttonProvider.selected((IStructuredSelection)table.getViewer().getSelection());
                table.getViewer().refresh();
            }
        });
        if (!buttonProvider.isEnabled((IStructuredSelection)table.getViewer().getSelection())) button.setEnabled(false);
        if (buttonProvidersByButton.isEmpty()) table.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                final IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                for (final Control control : buttonBar.getChildren()) {
                    if (!(control instanceof Button)) continue;
                    final ButtonProvider provider = buttonProvidersByButton.get(control);
                    if (provider == null) continue;
                    final boolean enabled = provider.isEnabled(selection);
                    if (enabled != control.isEnabled()) control.setEnabled(enabled);
                }
            }
        });
        buttonProvidersByButton.put(button, buttonProvider);
    }

    /**
     * @return group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * @return table
     */
    public Table<T> getTable() {
        return table;
    }

    /**
     * @param input
     */
    public void setInput( final Object input ) {
        table.setInput(input);
    }

    /**
     * 
     */
    public abstract class AddButtonProvider implements ButtonProvider {

        /**
         * 
         */
        protected abstract void add();

        /**
         * {@inheritDoc}
         * 
         * @return {@value TableAndButtonsGroup#ADD_BUTTON}
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#getText()
         */
        @Override
        public final String getText() {
            return ADD_BUTTON;
        }

        /**
         * {@inheritDoc}
         * 
         * @return <code>true</code>
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public boolean isEnabled( final IStructuredSelection selection ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Calls {@link #add()}, then {@link Table#packColumns() packs} the table's columns if this is the first row added.
         * </p>
         * 
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#selected(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public final void selected( final IStructuredSelection selection ) {
            add();
            if (table.getViewer().getTable().getItemCount() == 1) table.packColumns();
        }
    }

    /**
     * 
     */
    public abstract class EditButtonProvider implements ButtonProvider {

        /**
         * {@inheritDoc}
         * 
         * @return {@value TableAndButtonsGroup#EDIT_BUTTON}
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#getText()
         */
        @Override
        public final String getText() {
            return EDIT_BUTTON;
        }

        /**
         * {@inheritDoc}
         * 
         * @return <code>true</code> if the supplied selection contains exactly one element
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public boolean isEnabled( final IStructuredSelection selection ) {
            return selection.size() == 1;
        }
    }

    /**
     * 
     */
    public abstract class RemoveButtonProvider implements ButtonProvider {

        /**
         * {@inheritDoc}
         * 
         * @return {@value TableAndButtonsGroup#REMOVE_BUTTON}
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#getText()
         */
        @Override
        public final String getText() {
            return REMOVE_BUTTON;
        }

        /**
         * {@inheritDoc}
         * 
         * @return <code>true</code> if the supplied selection is not empty
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public boolean isEnabled( final IStructuredSelection selection ) {
            return !selection.isEmpty();
        }
    }
    
    /**
     * 
     */
    public abstract class NewButtonProvider implements ButtonProvider {

        /**
         * {@inheritDoc}
         * 
         * @return {@value TableAndButtonsGroup#NEW_BUTTON}
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#getText()
         */
        @Override
        public final String getText() {
            return NEW_BUTTON;
        }

        /**
         * {@inheritDoc}
         * 
         * @return <code>true</code> if the supplied selection is not empty
         * @see com.metamatrix.modeler.internal.vdb.ui.editor.ButtonProvider#isEnabled(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        public boolean isEnabled( final IStructuredSelection selection ) {
            return true;
        }
    }
}

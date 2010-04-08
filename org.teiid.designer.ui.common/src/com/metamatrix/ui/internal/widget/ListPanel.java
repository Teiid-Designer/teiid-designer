/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.ui.internal.InternalUiConstants.Widgets;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * Must be added to a container that has a {@link org.eclipse.swt.layout.GridLayout}.
 * 
 * @since 4.0
 */
public class ListPanel extends AbstractVerticalButtonPanel implements CoreStringUtil.Constants, Widgets {

    public static interface Constants {
        int NONE = 0;
        int ITEMS_ORDERED = 1;
        int ITEMS_EDITABLE = 1 << 1;
        int ITEMS_COMMONLY_ALL_SELECTED = 1 << 2;

        int DOWN = 1;
        int UP = -1;
    }

    private IListPanelController ctrlr;
    Button addButton, editButton, removeButton, upButton, downButton, selectAllButton, deselectAllButton;
    private boolean enabled;
    private ListenerList checkStateListeners;

    /**
     * @param parent
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller ) {
        this(parent, title, controller, SWT.NONE);
    }

    /**
     * @param parent
     * @param style
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller,
                      final int style ) {
        this(parent, title, controller, style, Constants.NONE);
    }

    /**
     * @param parent
     * @param style
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller,
                      final int style,
                      final int itemStyle ) {
        this(parent, title, controller, style, itemStyle, GridData.FILL_BOTH);
    }

    /**
     * @param parent
     * @param style
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller,
                      final int style,
                      final int itemStyle,
                      final int gridStyle ) {
        this(parent, title, controller, style, itemStyle, gridStyle, (java.util.List)null);
    }

    /**
     * @param parent
     * @param style
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller,
                      final int style,
                      final int itemStyle,
                      final java.util.List items ) {
        this(parent, title, controller, style, itemStyle, GridData.FILL_BOTH, items);
    }    

    /**
     * @param parent
     * @param style
     * @since 4.0
     */
    public ListPanel( final Composite parent,
                      final String title,
                      final IListPanelController controller,
                      final int style,
                      final int itemStyle,
                      final int gridStyle,
                      final java.util.List items ) {
        super(title, parent, style);
        this.enabled = super.getEnabled(); // to initialize current enabled state
        constructListEditPanel(controller, itemStyle, gridStyle, items);
    }

    /**
     * @since 4.0
     */
    protected void constructListEditPanel( final IListPanelController controller,
                                           final int itemStyle,
                                           final int gridStyle,
                                           final java.util.List items ) {
        CoreArgCheck.isNotNull(controller);
        this.ctrlr = controller;
        final TableViewer viewer = getTableViewer();

        // Initialize table if items available
        if (items != null && !items.isEmpty()) {
            viewer.add(items.toArray());
        }
        // Add vertical button bar
        final int style = viewer.getControl().getStyle();
        if ((style & SWT.READ_ONLY) == 0) {
            // Add buttons to button bar
            this.addButton = addButton(ADD_BUTTON);
            this.addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    addButtonSelected();
                }
            });
        }
        if ((itemStyle & Constants.ITEMS_EDITABLE) != 0) {
            this.editButton = addButton(EDIT_BUTTON);
            this.editButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    editButtonSelected();
                }
            });
            // Add double-click listener to list that auto-edits if editing enabled
            viewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick( final DoubleClickEvent event ) {
                    if (ListPanel.this.editButton.isEnabled()) {
                        editButtonSelected();
                    }
                }
            });
        }
        if ((style & SWT.READ_ONLY) == 0) {
            this.removeButton = addButton(REMOVE_BUTTON);
            this.removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    removeButtonSelected();
                }
            });
        }
        if ((itemStyle & Constants.ITEMS_ORDERED) != 0) {
            this.upButton = addButton(UP_BUTTON);
            this.upButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    upButtonSelected();
                }
            });
            this.downButton = addButton(DOWN_BUTTON);
            this.downButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    downButtonSelected();
                }
            });
        }
        if ((itemStyle & Constants.ITEMS_COMMONLY_ALL_SELECTED) != 0) {
            this.selectAllButton = addButton(SELECT_ALL_BUTTON);
            this.selectAllButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    selectAllButtonSelected();
                }
            });
            this.deselectAllButton = addButton(DESELECT_ALL_BUTTON);
            this.deselectAllButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    deselectAllButtonSelected();
                }
            });
        }
        // Initialize buttons
        updateButtons();
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public TableViewer getTableViewer() {
        return (TableViewer)getViewer();
    }

    /**
     * Adds the specified listener to those receiving events. Only if the viewer is a checkbox viewer is the listener added.
     * 
     * @param theListener the listener being added
     */
    public void addCheckStateListener( ICheckStateListener theListener ) { // NO_UCD (Indicates this is ignored by unused code detection tool)
        if (getViewer() instanceof CheckboxTableViewer) {
            if (this.checkStateListeners == null) {
                checkStateListeners = new ListenerList(ListenerList.IDENTITY);
            }

            this.checkStateListeners.add(theListener);
        }
    }

    /**
     * Removes the specified listener to those receiving events.
     * 
     * @param theListener the listener being added
     */
    public void removeCheckStateListener( ICheckStateListener theListener ) {
        if (this.checkStateListeners != null) {
            this.checkStateListeners.remove(theListener);
        }
    }

    /**
     * Notifies registered listeners.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void notifyCheckStateListeners( final CheckStateChangedEvent theEvent ) {
        if (this.checkStateListeners != null) {
            Object[] array = checkStateListeners.getListeners();

            for (int i = 0; i < array.length; i++) {
                final ICheckStateListener l = (ICheckStateListener)array[i];

                SafeRunner.run(new SafeRunnable() {
                    public void run() {
                        l.checkStateChanged(theEvent);
                    }

                    @Override
                    public void handleException( Throwable theEvent ) {
                        super.handleException(theEvent);

                        // if an unexpected exception happens remove listener to make sure the workbench keeps running.
                        removeCheckStateListener(l);
                    }
                });
            }
        }
    }

    /**
     * @see com.metamatrix.ui.internal.widget.AbstractVerticalButtonPanel#createViewer(org.eclipse.swt.widgets.Composite, int)
     * @since 4.0
     */
    @Override
    protected Viewer createViewer( final Composite parent,
                                   final int style ) {

        // Add single-column table (i.e, a list w/ icons)
        if (WidgetUtil.hasState(style, SWT.CHECK)) {
            final CheckListPanelViewer viewer = new CheckListPanelViewer(parent, style);
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged( final SelectionChangedEvent event ) {
                    itemsSelected((IStructuredSelection)event.getSelection());
                }
            });

            // when the panel is disabled it's scrollbars also disable. this is bad since the user can't see
            // all the items in the panel then. so when the component does disable just undo the checked state
            // being set.
            viewer.addCheckStateListenerAccess(new ICheckStateListener() {
                public void checkStateChanged( CheckStateChangedEvent theEvent ) {
                    // undo the check
                    if (!getEnabled() && theEvent.getSource() != this) {
                        viewer.setChecked(theEvent.getElement(), !theEvent.getChecked());
                    } else {
                        notifyCheckStateListeners(theEvent);
                    }
                }
            });
            return viewer;
        }

        final ListPanelViewer viewer = new ListPanelViewer(parent, style);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
                itemsSelected((IStructuredSelection)event.getSelection());
            }
        });
        return viewer;
    }

    /**
     * @since 4.0
     */
    public void addItem( final Object item ) {
        addItems(new Object[] {item});
    }

    /**
     * @since 4.0
     */
    public void addItems( final Object[] items ) {
        if (items.length > 0) {
            final TableViewer viewer = getTableViewer();
            viewer.add(items);
            viewer.setSelection(new StructuredSelection(items));
            updateButtons();
        }
    }

    /**
     * @since 4.0
     */
    public void clear() {
        final TableViewer viewer = getTableViewer();
        for (Object obj = viewer.getElementAt(0); obj != null; obj = viewer.getElementAt(0)) {
            viewer.remove(obj);
        }
    }

    /**
     * @since 4.0
     */
    public boolean contains( final Object element ) {
        CoreArgCheck.isNotNull(element);
        final TableItem[] items = getTableViewer().getTable().getItems();
        for (int ndx = items.length; --ndx >= 0;) {
            if (element.equals(items[ndx].getData())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.eclipse.swt.widgets.Control#getEnabled()
     * @since 4.2
     */
    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    /**
     * @see org.eclipse.swt.widgets.Control#isEnabled()
     * @since 4.2
     */
    @Override
    public boolean isEnabled() {
        return this.enabled && getParent().isEnabled();
    }

    /**
     * When disabling the list the list actually remains enabled. This is done because once disabled it's scrollbars are also
     * disabled so the list won't scroll. If a checkbox tree viewer is used the checkbox is still enabled but if the user clicks
     * it to change it's state then the state is toggled back to its original state. Keep this in mind if you add a checkbox state
     * listener. You will always have to check the enabled state.
     * 
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     * @since 4.2
     */
    @Override
    public void setEnabled( boolean theEnableFlag ) {
        // don't call super since disabling will also disable the scrollbars in the list
        this.enabled = theEnableFlag;

        // set button state
        updateButtons();

        // change color to match enabled or disabled color
        int colorCode = (theEnableFlag ? SWT.COLOR_WHITE : SWT.COLOR_WIDGET_BACKGROUND);
        getViewer().getControl().setBackground(UiUtil.getSystemColor(colorCode));

        if (!theEnableFlag) {
            getTableViewer().setCellEditors(null);
        }
    }

    /**
     * Get the specified button from this panel. Works only for the standard button types.
     * 
     * @param buttonNameConstant see InternalUiConstants.Widgets
     * @return the button specified, or null if the button could not be found, or was not created in this instance of the panel.
     * @since 4.2
     */
    public Button getButton( String buttonNameConstant ) {
        if (ADD_BUTTON.equals(buttonNameConstant)) {
            return addButton;
        } else if (DESELECT_ALL_BUTTON.equals(buttonNameConstant)) {
            return deselectAllButton;
        } else if (DOWN_BUTTON.equals(buttonNameConstant)) {
            return downButton;
        } else if (EDIT_BUTTON.equals(buttonNameConstant)) {
            return editButton;
        } else if (REMOVE_BUTTON.equals(buttonNameConstant)) {
            return removeButton;
        } else if (SELECT_ALL_BUTTON.equals(buttonNameConstant)) {
            return selectAllButton;
        } else if (UP_BUTTON.equals(buttonNameConstant)) {
            return upButton;
        }
        return null;
    }

    /**
     * @since 4.0
     */
    @Override
    public Button addButton( final String name ) {
        final Button button = super.addButton(name);
        button.moveBelow(this.addButton);
        return button;
    }

    /**
     * @since 4.0
     */
    public void updateButtons() {
        final TableViewer viewer = getTableViewer();
        final IStructuredSelection selection = getSelection();
        final int count = selection.size();
        final int itemCount = viewer.getTable().getItemCount();
        final boolean itemsExist = (itemCount > 0);
        if (this.editButton != null) {
            this.editButton.setEnabled(getEnabled() && (count == 1));
        }
        final boolean itemsSelected = (count > 0);
        if (this.removeButton != null) {
            this.removeButton.setEnabled(getEnabled() && itemsSelected);
        }
        if (this.upButton != null) {
            this.upButton.setEnabled(getEnabled() && itemsSelected && selection.getFirstElement() != viewer.getElementAt(0));
            if (itemsExist && itemsSelected) {
                final Object lastSelectedObj = selection.toArray()[count - 1];
                final Object lastObj = viewer.getElementAt(itemCount - 1);
                this.downButton.setEnabled(getEnabled() && (lastSelectedObj != lastObj));
            } else {
                this.downButton.setEnabled(false);
            }
        }
        if (this.selectAllButton != null) {
            this.selectAllButton.setEnabled(getEnabled() && itemsExist && count < itemCount);
            this.deselectAllButton.setEnabled(getEnabled() && itemsExist && itemsSelected);
        }
        if (this.addButton != null) {
            this.addButton.setEnabled(getEnabled());
        }
    }

    /**
     * @since 4.0
     */
    void addButtonSelected() {
        final Object[] items = this.ctrlr.addButtonSelected();
        addItems(items);
    }

    /**
     * @since 4.0
     */
    void deselectAllButtonSelected() {
        getViewer().setSelection(new StructuredSelection());
    }

    /**
     * @since 4.0
     */
    void downButtonSelected() {
        this.ctrlr.downButtonSelected(getSelection());
        moveItems(Constants.DOWN);
    }

    /**
     * @since 4.0
     */
    void editButtonSelected() {
        final Object item = this.ctrlr.editButtonSelected(getSelection());
        if (item != null) {
            final TableViewer viewer = getTableViewer();
            viewer.getTable().getSelection()[0].setData(item);
            viewer.update(item, null);
            itemsSelected(getSelection());
        }
    }

    /**
     * @since 4.0
     */
    void itemsSelected( final IStructuredSelection selection ) {
        this.ctrlr.itemsSelected(selection);
        updateButtons();
    }

    /**
     * @since 4.0
     */
    void removeButtonSelected() {
        final Object[] items = this.ctrlr.removeButtonSelected(getSelection());
        if (items.length > 0) {
            getTableViewer().remove(items);
            updateButtons();
        }
    }

    /**
     * @since 4.0
     */
    void selectAllButtonSelected() {
        final ListPanelViewer viewer = (ListPanelViewer)getViewer();
        viewer.setSelection(new StructuredSelection(viewer.getRawChildren(viewer.getInput())));
    }

    /**
     * @since 4.0
     */
    void upButtonSelected() {
        this.ctrlr.upButtonSelected(getSelection());
        moveItems(Constants.UP);
    }

    /**
     * @since 4.0
     */
    private void moveItems( final int direction ) {
        final TableViewer viewer = getTableViewer();
        final Table table = viewer.getTable();
        final int[] rows = table.getSelectionIndices();
        final Object[] items = new Object[rows.length * 2];
        for (int ndx = 0; ndx < rows.length; ++ndx) {
            final int rowsNdx = (direction < 0 ? ndx : rows.length - ndx - 1);
            final int row = rows[rowsNdx];
            final TableItem srcTableItem = table.getItem(row);
            final TableItem destTableItem = table.getItem(row + direction);
            final Object srcItem = srcTableItem.getData();
            final Object destItem = destTableItem.getData();
            destTableItem.setData(srcItem);
            srcTableItem.setData(destItem);
            final int itemsNdx = ndx * 2;
            items[itemsNdx] = srcItem;
            items[itemsNdx + 1] = destItem;
            rows[rowsNdx] += direction;
        }
        viewer.update(items, null);
        table.setSelection(rows);
        updateButtons();
    }

    /**
     * @since 4.0
     */
    private class ListPanelViewer extends TableViewer {

        /**
         * @since 4.0
         */
        ListPanelViewer( final Composite parent,
                         final int style ) {
            super(parent, style);
        }

        /**
         * Overridden to handle lack of content provider
         * 
         * @see org.eclipse.jface.viewers.StructuredViewer#getRawChildren(java.lang.Object)
         * @since 4.0
         */
        @Override
        protected Object[] getRawChildren( final Object parent ) {
            Object[] children = super.getRawChildren(parent);
            final Table table = getTable();
            final int count = table.getItemCount();
            if (children.length == 0 && count > 0) {
                children = new Object[count];
                final TableItem[] items = table.getItems();
                for (int ndx = 0; ndx < items.length; ++ndx) {
                    children[ndx] = items[ndx].getData();
                }
            }
            return children;
        }
    }

    /**
     * @since 4.2
     */
    class CheckListPanelViewer extends CheckboxTableViewer {

        /**
         * @since 4.2
         */
        CheckListPanelViewer( final Composite parent,
                              final int style ) {
            super(new Table(parent, style));
        }

        /**
         * This is an unsupported operation. Use
         * 
         * @see org.eclipse.jface.viewers.CheckboxTableViewer#addCheckStateListener(org.eclipse.jface.viewers.ICheckStateListener)
         * @since 4.2
         */
        @Override
        public void addCheckStateListener( ICheckStateListener theListener ) {
            throw new UnsupportedOperationException("Use " + ListPanel.class + ".addCheckStateListener(ICheckStateListener)"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * Gives access to registering a listener.
         * 
         * @param theListener the listener being registered
         * @since 4.2
         */
        void addCheckStateListenerAccess( ICheckStateListener theListener ) {
            super.addCheckStateListener(theListener);
        }

        /**
         * Overridden to handle lack of content provider
         * 
         * @see org.eclipse.jface.viewers.StructuredViewer#getRawChildren(java.lang.Object)
         * @since 4.0
         */
        @Override
        protected Object[] getRawChildren( final Object parent ) {
            Object[] children = super.getRawChildren(parent);
            final Table table = getTable();
            final int count = table.getItemCount();
            if (children.length == 0 && count > 0) {
                children = new Object[count];
                final TableItem[] items = table.getItems();
                for (int ndx = 0; ndx < items.length; ++ndx) {
                    children[ndx] = items[ndx].getData();
                }
            }
            return children;
        }
    }
}

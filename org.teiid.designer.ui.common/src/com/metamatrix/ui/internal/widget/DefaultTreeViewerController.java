/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.0
 */
public class DefaultTreeViewerController implements
                                        InternalUiConstants.Widgets,
                                        ITreeViewerController {

    // ============================================================================================================================
    // Implemented Methods

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
     * @since 4.0
     */
    public void checkedStateToggled(final TreeItem item) {
    }

    /**
     * @return True
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#isItemCheckable(org.eclipse.swt.widgets.TreeItem)
     * @since 4.0
     */
    public boolean isItemCheckable(final TreeItem item) {
        return true;
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
     * @since 4.0
     */
    public void itemCollapsed(final TreeExpansionEvent event) {
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemDoubleClicked(org.eclipse.jface.viewers.DoubleClickEvent)
     * @since 4.0
     */
    public void itemDoubleClicked(final DoubleClickEvent event) {
    }

    /**
     * Updates checked state of item's children based upon item's checked state.
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
     * @since 4.0
     */
    public void itemExpanded(final TreeExpansionEvent event) {
        if(((TreeViewer)event.getTreeViewer()).getTree().getSelection().length > 0) {
            final TreeItem item = ((TreeViewer)event.getTreeViewer()).getTree().getSelection()[0];
            if (WidgetUtil.isChecked(item)) {
                updateChildren(item, true);
            }
        }
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemSelected(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 4.0
     */
    public void itemSelected(final SelectionChangedEvent event) {
    }

    /**
     * Updates checked state of item and its children (based upon parent item's checked state).
     * 
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
     * @since 4.0
     */
    public void update(final TreeItem item,
                       final boolean selected) {
        final boolean checked = !WidgetUtil.isUnchecked(item);
        item.setChecked(checked);
        item.setGrayed(WidgetUtil.isPartiallyChecked(item));
        if (selected) {
            updateChildren(item, checked);
            for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
                int state = PARTIALLY_CHECKED;
                final TreeItem[] children = parent.getItems();
                for (int ndx = children.length; --ndx >= 0;) {
                    final TreeItem child = children[ndx];
                    if (WidgetUtil.isPartiallyChecked(child)) {
                        state = PARTIALLY_CHECKED;
                        break;
                    }
                    final int childState = WidgetUtil.getCheckedState(child);
                    if (state == PARTIALLY_CHECKED) {
                        state = childState;
                    } else if (state != childState) {
                        state = PARTIALLY_CHECKED;
                        break;
                    }
                }
                if (state != WidgetUtil.getCheckedState(parent)) {
                    WidgetUtil.setCheckedState(parent, state, false, this);
                }
            }
        }
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void updateChildren(final TreeItem item,
                                final boolean checked) {
        final TreeItem[] children = item.getItems();
        for (int ndx = children.length; --ndx >= 0;) {
            final TreeItem child = children[ndx];
            updateChildren(child, checked);
            WidgetUtil.setChecked(child, checked, false, this);
        }
    }
}

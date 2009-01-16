/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.modelgenerator.ui.wizards;

import org.eclipse.emf.mapping.Mapping;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.TreeItem;

import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ui.tree.DifferenceAnalysis;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;

/**
 * RefreshTreeViewerController
 */
public class RefreshTreeViewerController extends DefaultTreeViewerController {
    //============================================================================================================================
    // Implemented Methods
    
    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
     * @since 4.0
     */
    @Override
    public void checkedStateToggled(final TreeItem item) {
        if(item!=null) {
            boolean isChecked = item.getChecked();
            Object obj = item.getData();
            if(obj instanceof DifferenceReport) {
                Mapping mapping = ((DifferenceReport)obj).getMapping();
                DifferenceDescriptor descriptor = DifferenceAnalysis.getDifferenceDescriptor(mapping);
                descriptor.setSkip(!isChecked);
            } else if(obj instanceof Mapping) {
                DifferenceDescriptor descriptor = DifferenceAnalysis.getDifferenceDescriptor((Mapping)obj);
                descriptor.setSkip(!isChecked);
            }
        }
    }

    /**<p>
     * </p>
     * @return True
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#isItemCheckable(org.eclipse.swt.widgets.TreeItem)
     * @since 4.0
     */
    @Override
    public boolean isItemCheckable(final TreeItem item) {
        boolean isCheckable = true;
        if(item!=null) {
            Object obj = item.getData();
            if(obj instanceof DifferenceReport) {
                isCheckable = true;
            }else if(obj instanceof Mapping) {
                Mapping mapping = (Mapping)obj;
                if(DifferenceAnalysis.isUnchanged(mapping)) {
                    isCheckable = false;
                }
            }
        }
        return isCheckable;
    }
    
    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
     * @since 4.0
     */
    @Override
    public void itemCollapsed(final TreeExpansionEvent event) {
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemDoubleClicked(org.eclipse.jface.viewers.DoubleClickEvent)
     * @since 4.0
     */
    @Override
    public void itemDoubleClicked(final DoubleClickEvent event) {
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemSelected(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 4.0
     */
    @Override
    public void itemSelected(final SelectionChangedEvent event) {
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.internal.widget.ITreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
     * @since 4.0
     */
    @Override
    public void update(final TreeItem item, final boolean selected) {
        final boolean checked = !WidgetUtil.isUnchecked(item);
        item.setChecked(checked);
        if(WidgetUtil.isPartiallyChecked(item) || !isItemCheckable(item)) {
            item.setGrayed(true);
        }
        if (selected) {
            updateChildren(item, checked);
            for (TreeItem parent = item.getParentItem();  parent != null;  parent = parent.getParentItem()) {
                int state = PARTIALLY_CHECKED;
                final TreeItem[] children = parent.getItems();
                for (int ndx = children.length;  --ndx >= 0;) {
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
    
    //============================================================================================================================
    // Utility Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    private void updateChildren(final TreeItem item, final boolean checked) {
        final TreeItem[] children = item.getItems();
        for (int ndx = children.length;   --ndx >= 0;) {
            final TreeItem child = children[ndx];
            updateChildren(child, checked);
            WidgetUtil.setChecked(child, checked, false, this);
        }
    }

}

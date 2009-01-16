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

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelectionFilter;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceView;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;

/**
 * CheckboxTreePanel.
 */
public class CheckboxTreePanel extends Composite implements ModelGeneratorUiConstants, StringUtil.Constants {

    private TreeViewer treeViewer;
    ModelWorkspaceSelections selections;
    ModelWorkspaceView view;
    private CheckboxTreeController controller;
    /** List of listeners registered for this panels events */
    private List eventListeners;
    boolean treeExpanded = false;

    /**
     * Constructor
     * 
     * @param parent the parent composite
     * @param title the title text
     * @param selections the model workspace selections
     */
    public CheckboxTreePanel( Composite parent,
                              String title,
                              ModelWorkspaceSelections selections ) {
        super(parent, SWT.NULL);
        this.selections = selections;
        initialize(title);
    }

    /**
     * Set the tree selections using the provided list of objects
     * 
     * @param selectedObj the selected objects
     */
    public void setTreeSelections( final List selectedObjs ) {
        IStructuredSelection sel = new StructuredSelection(selectedObjs);
        this.treeViewer.setSelection(sel, true);
        final TreeItem[] items = this.treeViewer.getTree().getSelection();
        for (int i = 0; i < items.length; i++) {
            TreeItem item = items[i];
            Object data = item.getData();
            if (selectedObjs.contains(data)) {
                WidgetUtil.setChecked(item, true, false, this.controller);
                controller.checkedStateToggled(item);
                // If the item is not checkable, gray it
                if (!controller.isItemCheckable(item)) {
                    item.setGrayed(true);
                    this.controller.update(item, false);
                }
            }
        }
        this.treeViewer.setSelection(null);
    }

    // -------------------------------------------------------------------------
    // Methods to Register, UnRegister, Notify Listeners to this Panels Events
    // -------------------------------------------------------------------------
    /**
     * This method will register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be registered
     */
    public void addEventListener( EventObjectListener listener ) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        eventListeners.add(listener);
    }

    /**
     * This method will un-register the listener for all CheckboxSelectionEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeEventListener( EventObjectListener listener ) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a CheckboxSelectionEvents
     */
    void notifyEventListeners() {
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(new EventObject(this));
                }
            }
        }
    }

    /**
     * Initialize the Panel
     */
    private void initialize( String title ) {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        // --------------------------
        // Group for checkbox tree
        // --------------------------
        Group group = WidgetFactory.createGroup(this, title, GridData.FILL_BOTH);

        // ----------------------------
        // TreeViewer
        // ----------------------------
        controller = new CheckboxTreeController();
        this.treeViewer = WidgetFactory.createTreeViewer(group, SWT.MULTI | SWT.CHECK, GridData.FILL_BOTH, controller);

        final Tree tree = this.treeViewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        view = selections.getModelWorkspaceView();

        this.treeViewer.setContentProvider(new CheckboxTreeContentProvider());
        this.treeViewer.setLabelProvider(new CheckboxTreeLabelProvider());

        this.treeViewer.setInput(ModelerCore.getModelWorkspace());
    }

    boolean isSelectable( final Object node ) {
        boolean isSelectable = false;
        if (node != null) {
            List filters = selections.getModelWorkspaceSelectionFilters();
            Iterator iter = filters.iterator();
            while (iter.hasNext()) {
                final ModelWorkspaceSelectionFilter filter = (ModelWorkspaceSelectionFilter)iter.next();
                if (filter.isSelectable(node)) {
                    isSelectable = true;
                    break;
                }
            }
        }
        return isSelectable;
    }

    class CheckboxTreeLabelProvider extends LabelProvider {
        final WorkbenchLabelProvider workbenchProvider = new WorkbenchLabelProvider();

        @Override
        public Image getImage( final Object node ) {
            if (node instanceof EObject) {
                return ModelUtilities.getEMFLabelProvider().getImage(node);
            }
            return workbenchProvider.getImage(((ModelWorkspaceItem)node).getResource());
        }

        @Override
        public String getText( final Object node ) {
            if (node instanceof EObject) {
                return ModelUtilities.getEMFLabelProvider().getText(node);
            }
            return workbenchProvider.getText(((ModelWorkspaceItem)node).getResource());
        }
    }

    class CheckboxTreeContentProvider implements ITreeContentProvider {
        public void dispose() {
        }

        public Object[] getChildren( final Object node ) {
            try {
                return view.getChildren(node);
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
                return EMPTY_STRING_ARRAY;
            }
        }

        public Object[] getElements( final Object inputElement ) {
            return getChildren(inputElement);
        }

        public Object getParent( final Object node ) {
            return view.getParent(node);
        }

        public boolean hasChildren( final Object node ) {
            try {
                return view.hasChildren(node);
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
                return false;
            }
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }

    class CheckboxTreeController extends DefaultTreeViewerController {

        /**
         * @see com.metamatrix.ui.internal.widget.DefaultTreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public void checkedStateToggled( TreeItem item ) {
            final Object node = item.getData();
            final boolean select = item.getChecked(); // (selections.getSelectionMode(node) != ModelWorkspaceSelections.SELECTED);
            // Select node in both view and model
            try {
                selections.setSelected(node, select);
            } catch (final ModelWorkspaceException err) {
                Util.log(err);
            }
            notifyEventListeners();
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#isItemCheckable(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public boolean isItemCheckable( final TreeItem item ) {
            final Object node = item.getData();
            return isSelectable(node);
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
         * @since 4.0
         */
        @Override
        public void update( final TreeItem item,
                            final boolean selected ) {
            if (item.getData() != null) {
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
                if (!isItemCheckable(item)) {
                    item.setGrayed(true);
                } else {
                    item.setGrayed(false);
                }
            }
        }

        /**
         * @since 4.0
         */
        private void updateChildren( final TreeItem item,
                                     final boolean checked ) {
            final TreeItem[] children = item.getItems();
            for (int ndx = children.length; --ndx >= 0;) {
                final TreeItem child = children[ndx];
                if (child.getData() != null) {
                    updateChildren(child, checked);
                    WidgetUtil.setChecked(child, checked, false, this);
                }
            }
        }

        /**
         * @see com.metamatrix.ui.internal.widget.ITreeViewerController#itemExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
         * @since 4.0
         */
        @Override
        public void itemExpanded( final TreeExpansionEvent event ) {
            if (treeExpanded) {
                super.itemExpanded(event);
            } else {
                final TreeItem[] items = ((TreeViewer)event.getTreeViewer()).getTree().getSelection();

                if (items.length > 0) {
                    final TreeItem item = items[0];
                    if (item.getData() != null) {
                        updateChildren(item, false);
                    }
                }
                treeExpanded = true;
            }
            // BML 8/25/04 overrode the DefaultTreeViewerController method here so that the tree would be updated
            // for selectability whenever it is expanded the first time. Basically, all non-selectable items wouldn't
            // be greyed out until something was selected after the expansion. This was confusing.
            // if (WidgetUtil.isChecked(item)) {
            // updateChildren(item, true);
            // }
        }

    }

}// end CheckboxTreePanel

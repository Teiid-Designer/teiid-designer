/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Tree;
import com.metamatrix.ui.tree.TreeViewerUtil;

/**
 * Extension to CheckboxTreeViewer in which: A checkmark at a node automatically places a checkmark at all descendant nodes.
 * Clearing a checkmark at a node automatically clears a checkmark at all ancestor nodes. The case where a node has no checkmark
 * but at least one descendant node has a checkmark is handled by the 'style' parameter.
 */
public class InheritanceCheckboxTreeViewer extends CheckboxTreeViewer implements ICheckStateListener {

    // Values for uncheckedNodeWithCheckedDescendantsStyle:
    public final static int UNCHECKED_WHITE = 1;
    public final static int UNCHECKED_GRAYED = 2;
    public final static int CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_UNCHECKED_GRAYED = 3;
    public final static int CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_CHECKED_GRAYED_IF_ANY_CHECKED = 4;

    int uncheckedNodeWithCheckedDescendantsStyle;
    private boolean listenerEnabled = true;
    private INodeDescendantsDeselectionHandler deselectionHandler;
    private ICheckableController checkController;

    public InheritanceCheckboxTreeViewer( Tree tree,
                                          int style,
                                          INodeDescendantsDeselectionHandler deselectionHandler ) {
        super(tree);
        this.uncheckedNodeWithCheckedDescendantsStyle = style;
        this.deselectionHandler = deselectionHandler;
        this.addCheckStateListener(this);
    }

    @Override
    public Object getRoot() {
        return super.getRoot();
    }

    public void setListenerEnabled( boolean state ) {
        this.listenerEnabled = state;
    }

    public void setCheckableController( ICheckableController theController ) {
        this.checkController = theController;
    }

    public void checkStateChanged( CheckStateChangedEvent event ) {
        // undo the event if needed
        if ((this.checkController != null) && !this.checkController.isEditable(event.getElement())) {
            setChecked(event.getElement(), !event.getChecked());
        } else if (this.listenerEnabled) {
            // just in case, don't re-enter this while working:
            listenerEnabled = false;
            // Have to take care of changing the checked state and/or shading of ancestor or
            // descendant nodes. Since this can take more than a second, we will use a
            // wait-cursor.
            final InheritanceCheckboxTreeViewer viewer = this;
            final ITreeContentProvider contentProvider = (ITreeContentProvider)viewer.getContentProvider();
            final boolean isChecked = event.getChecked();
            final Object node = event.getElement();
            boolean deselecting = false;
            if (!isChecked) {
                if (contentProvider != null) {
                    boolean hasDescendant = contentProvider.hasChildren(node);
                    if (hasDescendant) {
                        deselecting = deselectionHandler.deselectDescendants(node);
                    }
                }
            }
            final boolean deselectingDescendants = deselecting;
            Runnable runnable = new Runnable() {
                public void run() {

                    if (isChecked) {
                        viewer.setGrayed(node, false);
                        // Set all descendant nodes to checked:
                        List /*<Object>*/descendants = TreeViewerUtil.getDescendantsOfNode(viewer, node, false);
                        Iterator it = descendants.iterator();
                        while (it.hasNext()) {
                            Object curNode = it.next();
                            viewer.setChecked(curNode, true);
                            viewer.setGrayed(curNode, false);
                        }
                        // the following is much faster than the above, but will not create
                        // children for nodes that have not yet been expanded. Other code
                        // would have to be more intelligent in processing the tree checked
                        // status for this to work.
                        if (viewer.uncheckedNodeWithCheckedDescendantsStyle != InheritanceCheckboxTreeViewer.UNCHECKED_WHITE) {
                            // Change ancestor nodes to grayed or checked
                            if (contentProvider != null) {
                                Object parent = contentProvider.getParent(node);
                                switch (viewer.uncheckedNodeWithCheckedDescendantsStyle) {
                                    case InheritanceCheckboxTreeViewer.UNCHECKED_GRAYED:
                                        while (parent != null) {
                                            viewer.setGrayed(parent, true);
                                            parent = contentProvider.getParent(parent);
                                        }
                                        break;

                                    case InheritanceCheckboxTreeViewer.CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_UNCHECKED_GRAYED:
                                        boolean allDescendantsChecked = true;
                                        while (parent != null) {

                                            // if all descendents of last (lower) node were checked
                                            if (allDescendantsChecked) {
                                                // determine if all descendents of the current ('parent') node are checked
                                                allDescendantsChecked = TreeViewerUtil.allDescendantsChecked(viewer, parent);
                                            }
                                            // set the state of the current ('parent') node
                                            if (allDescendantsChecked) {
                                                viewer.setChecked(parent, true);
                                                viewer.setGrayed(parent, false);
                                            } else {
                                                viewer.setGrayed(parent, true);
                                            }
                                            // ascend to my parent and repeat the process
                                            parent = contentProvider.getParent(parent);
                                        }
                                        break;

                                    case InheritanceCheckboxTreeViewer.CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_CHECKED_GRAYED_IF_ANY_CHECKED:
                                        boolean anyDescendantsChecked2 = false;
                                        boolean allDescendantsChecked2 = true;

                                        // loop backwards through ancestors
                                        while (parent != null) {

                                            anyDescendantsChecked2 = TreeViewerUtil.anyDescendantChecked(viewer, parent);

                                            // if all descendents of last (lower) node were checked
                                            if (allDescendantsChecked2) {
                                                // determine if all descendents of the current ('parent') node are checked
                                                allDescendantsChecked2 = TreeViewerUtil.allDescendantsChecked(viewer, parent);
                                            }
                                            // set the state of the current ('parent') node
                                            // All descendents selected
                                            if (allDescendantsChecked2) {
                                                viewer.setChecked(parent, true);
                                                viewer.setGrayed(parent, false);
                                            } else
                                            // Some descendents selected
                                            if (anyDescendantsChecked2) {
                                                viewer.setChecked(parent, true);
                                                viewer.setGrayed(parent, true);
                                            }
                                            // No descendents selected
                                            else {
                                                viewer.setGrayed(parent, true);
                                            }
                                            // ascend to my parent and repeat the process
                                            parent = contentProvider.getParent(parent);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    } else {
                        // Unchecked
                        if (contentProvider != null) {
                            if (deselectingDescendants) {
                                List /*<Object>*/descendants = TreeViewerUtil.getDescendantsOfNode(viewer, node, true);
                                Iterator it = descendants.iterator();
                                while (it.hasNext()) {
                                    Object curNode = it.next();
                                    viewer.setChecked(curNode, false);
                                    viewer.setGrayed(curNode, false);
                                }
                                // the following is much faster than the above, but will not create
                                // children for nodes that have not yet been expanded. Other code
                                // would have to be more intelligent in processing the tree checked
                                // status for this to work.
                            }

                            // Set ancestor nodes to grayed as appropriate
                            boolean anyDescendantChecked = false;
                            // For implementation ease, start parent out as the node itself, will reset
                            // to unchecked but this does not matter
                            Object parent = node;
                            while (parent != null) {
                                viewer.setChecked(parent, false);
                                if (viewer.uncheckedNodeWithCheckedDescendantsStyle != InheritanceCheckboxTreeViewer.UNCHECKED_WHITE) {
                                    if (!anyDescendantChecked) {
                                        anyDescendantChecked = TreeViewerUtil.anyDescendantChecked(viewer, parent);
                                    }
                                    switch (viewer.uncheckedNodeWithCheckedDescendantsStyle) {
                                        case InheritanceCheckboxTreeViewer.UNCHECKED_GRAYED:
                                            viewer.setGrayed(parent, anyDescendantChecked);
                                            viewer.setChecked(parent, false);
                                            break;

                                        case InheritanceCheckboxTreeViewer.CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_UNCHECKED_GRAYED:
                                            viewer.setGrayed(parent, anyDescendantChecked);
                                            viewer.setChecked(parent, false);
                                            break;

                                        case InheritanceCheckboxTreeViewer.CHECKED_WHITE_IF_ALL_DESCENDANTS_CHECKED_ELSE_CHECKED_GRAYED_IF_ANY_CHECKED:
                                            viewer.setGrayed(parent, anyDescendantChecked);
                                            viewer.setChecked(parent, anyDescendantChecked);
                                            break;

                                        default:
                                            break;
                                    }
                                }
                                parent = contentProvider.getParent(parent);
                            }
                        }
                    }
                }
            };
            BusyIndicator.showWhile(null, runnable);
            // restore enabled state:
            listenerEnabled = true;
        }
    }
}

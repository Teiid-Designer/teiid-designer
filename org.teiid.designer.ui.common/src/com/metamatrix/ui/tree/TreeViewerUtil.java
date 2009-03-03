/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

/**
 * TreeViewerUtil is a set of static utility methods for operating on TreeViewer.
 */
abstract public class TreeViewerUtil {

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String VISIBLE = "TreeExpansionMonitor.visible"; //$NON-NLS-1$

    /**
     * Return an ordered list, top to bottom, of all Objects that are visible in the
     * specified TreeViewer.  If the tree is fully collapsed, only the top root nodes
     * will be returned.
     * @param viewer
     * @return
     */
    public static List getVisibleObjects(TreeViewer viewer) {
        
        // get this TreeViewer's content provider so we can walk the content
        ITreeContentProvider contentProvider = (ITreeContentProvider) viewer.getContentProvider();
        
        // get all the expanded visible nodes in the tree
        Object[] expandedNodes = viewer.getVisibleExpandedElements();
        
        LinkedList result = new LinkedList(Arrays.asList(expandedNodes));
        if ( result.isEmpty() ) {
            // if there are no expanded nodes, then return the roots of the tree
            result.add(contentProvider.getElements(viewer.getInput()));
        } else {
            // walk through all the expanded nodes and, if their children are not already in
            //   the result, add them.
            int index = 0;
            for ( int i=0 ; i<expandedNodes.length ; ++i ) {
                Object[] children = contentProvider.getChildren(expandedNodes[i]);
                // set the array index in case we need to add children beneath this node
                index = result.indexOf(expandedNodes[i]);
                for ( int j=0 ; j<children.length ; ++j ) {
                    int childIndex = result.indexOf(children[j]);
                    if ( childIndex > 0 ) {
                        // this node is also expanded, so no need to add it - just increment the array index
                        index = childIndex; 
                    } else {
                        // add this node and increment thearray index
                        result.add(++index, children[j]);
                    }
                   
                }
            }
        }
        
        return result;   
    }

    public static List getVisibleObjectsIncludingUnexpanded( TreeViewer tv ) {
        // update visibleTreeItems
        TreeItem[] items = tv.getTree().getItems();
        ArrayList visibleTreeItems = new ArrayList( items.length );
        for ( int i = 0; i < items.length; i++ ) {
            visibleTreeItems.add( items[ i ] );
        }
        
        // update visibleNodes
        ArrayList nodes = new ArrayList(items.length);
        internalGetVisibleObjects(nodes, items);
        return nodes;
    }

    protected static boolean isVisible(TreeItem item) {
        boolean result = true;
//        boolean result = false;
        Object obj = item.getData(VISIBLE);
        if ( obj != null ) {
            result = ((Boolean) obj).booleanValue();
        }
        return result;
    }

    
    private static void internalGetVisibleObjects(List list, TreeItem[] items) {
        for ( int i=0 ; i<items.length ; ++i ) {
            // items[i].getData() was returning null.  Defect 12204 fix: Add null check.
            if ( isVisible(items[i]) && items[i].getData() != null) {
                list.add(items[i].getData());
                internalGetVisibleObjects(list, items[i].getItems());
            }
        }
    }

	/**
	 * Returns an ordered list of the descendants of a node in a tree.  Assumes that
	 * the tree has a content provider, returns null otherwise.
	 * 
	 * @param viewer    the TreeViewer for the tree
	 * @param node      Object contained as a node in the tree, the root of this query
	 * @param includeNode  true if node itself is to be included at start of the list
	 * @return ordered list of descendants of the given node in the tree
	 */
	public static List getDescendantsOfNode(TreeViewer viewer, Object node,
			boolean includeNode, boolean stopAtSplitters) {
		List result = null;
		ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
		if (provider != null) {
			result = new ArrayList();
			if (includeNode) {
				result.add(node);
			}
			Object[] children;
            if (stopAtSplitters 
             && node instanceof TreeSplitter 
             && !((TreeSplitter) node).isMaterialized()) {
                // not materialized, don't do anything more
                children = EMPTY_OBJECT_ARRAY;

            } else {
                // just process normally:
                children = provider.getChildren(node);
            } //endif

            for (int i = 0; i < children.length; i++) {
				result.add(children[i]);
				List childsChildren = TreeViewerUtil.getDescendantsOfNode(viewer, 
						children[i], false, stopAtSplitters);
				result.addAll(childsChildren);
			}
		}
		return result;
	}

    /** Set the checked and grayed state of an entire tree.  This internally uses
      *  TreeItems, so it is much more efficient than viewer.setChecked.  It will
      *  not automatically expand nodes that have children, and so may miss some
      *  not-yet-existant nodes.
      * 
      * @param treeViewer The tree to change.
      * @param parent the root of the tree of nodes to be updated
      * @param state The new state information.  If newCheckState is not null, 
      *   the item has its checked set to the value.  If newGrayedState is not 
      *   null, the item has its grayed set to the value.
      */
    public static void setAllTreeItemsState(TreeViewer treeViewer, ItemCheckState state) {
        TreeItem[] items = treeViewer.getTree().getItems();
        for ( int i=0 ; i<items.length ; ++i ) {
            TreeItem treeItem = items[i];
            updateState(treeItem, state);
            setAllChildrenState(treeItem, state);
        }
    }

    /** Set the checked and grayed state of a subtree of nodes.
      * 
      * @param parent the root of the tree of nodes to be updated
      * @param state The new state information.  If newCheckState is not null, 
      *   the item has its checked set to the value.  If newGrayedState is not 
      *   null, the item has its grayed set to the value.
      */
    public static void setAllChildrenState(TreeItem parent, ItemCheckState state) {
        TreeItem[] items = parent.getItems();
        for ( int i=0 ; i<items.length ; ++i ) {
            TreeItem treeItem = items[i];
            updateState(treeItem, state);
            setAllChildrenState(treeItem, state);
        } // endfor
    }

    /** Update the checked and grayed state of a TreeItem
      * 
      * @param treeItem The item to update.
      * @param state The new state information.  If newCheckState is not null, 
      *   the item has its checked set to the value.  If newGrayedState is not 
      *   null, the item has its grayed set to the value.
      */
    public static void updateState(TreeItem treeItem, ItemCheckState state) {
        if (state.newCheckState != null) {
            treeItem.setChecked(state.newCheckState.booleanValue());
        } // endif
        if (state.newGrayedState != null) {
            treeItem.setGrayed(state.newGrayedState.booleanValue());
        } // endif
    }

    /** Gets a List of all root nodes present in the specified tree.
      * 
      * @param viewer The Tree we are working with.
      * @return {@link Collections.EMPTY_LIST} if the content provider is null,
      *   the children of viewer.getInput() otherwise.
      */
    public static List getRootNodes(TreeViewer viewer) {
        List result = Collections.EMPTY_LIST;
        ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
        if (provider != null) {
            Object[] children = provider.getChildren(viewer.getInput());
            result = Arrays.asList(children);
        }
        return result;
    }

    /** Set the checked status of an entire tree.  This method uses 
      *  viewer.setChecked(), which has some performance issues in large trees.
      * 
      * @param viewer The viewer we are using.
      * @param checkedState The new checked state of each node.
      * @param stopAtSplitters Whether to stop descending if a TreeSplitter object is
      *   encountered.  This should usually be true.
      */
    public static void setAllChecked(CheckboxTreeViewer viewer, boolean checkedState, boolean stopAtSplitters) {
        setSubtreeChecked(viewer, viewer.getInput(), checkedState, stopAtSplitters);
    }

    /** Set the checked status of all children of the specified node.  This method uses 
      *  viewer.setChecked(), which has some performance issues in large trees.
      * 
      * @param viewer The viewer we are using.
      * @param node The node whose subtree to check.
      * @param checkedState The new checked state of each node.
      * @param stopAtSplitters Whether to stop descending if a TreeSplitter object is
      *   encountered.  This should usually be true.
      */
    public static void setSubtreeChecked(CheckboxTreeViewer viewer, Object node, boolean checkedState, boolean stopAtSplitters) {
        // get children:
        ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
        if (provider == null) {
            // no provider; can't proceed.
            return;
        } // endif

        //fix this to check if root is TS and/or materialized.
        if (stopAtSplitters && node instanceof TreeSplitter) {
            TreeSplitter ts = (TreeSplitter) node;
            if (!ts.isMaterialized()) {
                // not materialized, don't do anything more
                return;
            } // endif
        } //endif
            
        Object[] children = provider.getChildren(node);

        // set checked on each child:
        for (int i = 0; i < children.length; i++) {
            Object child = children[i];
            viewer.setChecked(child, checkedState);
            if (stopAtSplitters) {
                // keep calling this method recursively, so that we can check for TreeSplitters
                setSubtreeChecked(viewer, child, checkedState, true);
            } else {
                // don't stop at splitters, just allow SWT to do the checking:
                viewer.setSubtreeChecked(node, checkedState);
            } // endif
        } // endfor
    }

    /** Scans the tree, starting at the specified node, to see if all children of that
      *  node have a checked status of checkedState.  There are performance implications to
      *  this method.  Specifically, it uses the content provider to obtain children, and 
      *  then the viewer to get the checked state.  This method will get the most accurate
      *  results, at a cost of speed.  Using TreeItems directly would work to a point, but
      *  would only work if the entire tree had been expanded. 
      * 
      * @param viewer The tree we are looking at.
      * @param node The starting node.
      * @param checkedState the state to compare to.
      * @return true if all descendants of node are checked.
      */
    public static boolean allDescendantsHaveState(CheckboxTreeViewer viewer, Object node, boolean checkedState) {
    	ITreeContentProvider contentProvider = (ITreeContentProvider)viewer.getContentProvider();
    	boolean mismatchFound = false;
    	Object[] children;
        // check for splitterness:
        if (node instanceof TreeSplitter
         && !((TreeSplitter)node).isMaterialized()) {
            // not yet materialized (expanded)... don't descend past this.
            children = EMPTY_OBJECT_ARRAY;

        } else {
            // not a TreeSplitter or already materialized, go ahead:
            children = contentProvider.getChildren(node);
        } // endif

        int i = 0;
    	while ((i < children.length) && (!mismatchFound)) {
    		if (viewer.getChecked(children[i]) != checkedState) {
    			mismatchFound = true;
    		} else {
    			if (!allDescendantsHaveState(viewer, children[i], checkedState)) {
    				mismatchFound = true;
    			} else {
    				i++;
    			}
    		}
    	}
    	return (!mismatchFound);
    }

    /** Scans the tree, starting at the specified node, to see if all children of that
      *  node are checked.  There are performance implications to this method; see
      *  allDescendantsHaveState for more information.
      * 
      * @param viewer The tree we are looking at.
      * @param node The starting node.
      * @see #allDescendantsHaveState(CheckboxTreeViewer, Object, boolean)
      * @return true if all descendants of node are checked.
      */
    public static boolean allDescendantsChecked(CheckboxTreeViewer viewer, Object node) {
    	boolean allChecked = allDescendantsHaveState(viewer, node, true);
    	return allChecked;
    }

    /** Scans the tree, starting at the specified node, to see if any children of that
      *  node are checked.  There are performance implications to this method; see
      *  allDescendantsHaveState for more information.
      * 
      * @param viewer The tree we are looking at.
      * @param node The starting node.
      * @see #allDescendantsHaveState(CheckboxTreeViewer, Object, boolean)
      * @return true if any descendants of node are checked.
      */
    public static boolean anyDescendantChecked(CheckboxTreeViewer viewer, Object node) {
    	boolean anyChecked = (!allDescendantsHaveState(viewer, node, false));
    	return anyChecked;
    }

    public static Object[] split(LargeTreeContentProvider largeModel, Object parentElement, Object[] rawNodes, int offset, int length) {
        // split into multiple groups.
        // first, determine scope.  That is, if there are more than 10,000 entries, we
        //  will need to break things up at a higher level, and so on.  We never want
        //  more than LargeTreeContentProvider.MAX_NODES entries at any particular node.
        int scope = LargeTreeContentProvider.MAX_NODES;
        int splitterCount = length / scope;
        while (splitterCount > LargeTreeContentProvider.MAX_NODES) {
            scope *= LargeTreeContentProvider.MAX_NODES;
            splitterCount = length / scope;
        } // endwhile

        int leftoverKids = length % scope;
        if (leftoverKids != 0) {
            splitterCount++; // add one for the remainder
        } // endif

        // less than LargeTreeContentProvider.MAX_NODES end entries, just make one layer:
        return makeSplitters(largeModel, scope, splitterCount, offset, parentElement, rawNodes);
    }

    public static Object[] makeSplitters(LargeTreeContentProvider largeModel, int scope, int splitterCount, int baseOffset, Object parentElement, Object[] rawChildren) {
        Object[] splitters = new Object[splitterCount];
        for (int i = 0; i < splitterCount; i++) {
            int start = baseOffset + i*scope;
            int end = start + scope - 1;
            if (end >= rawChildren.length) {
                end = rawChildren.length - 1;
            } // endif

            int splitLength = end-start+1;

            String labelString;
            if (largeModel != null) {
                labelString = largeModel.getLabelString(rawChildren[start], rawChildren[end], splitLength);
            } else {
                labelString = null;
            } // endif
            splitters[i] = new TreeSplitter(largeModel, parentElement, rawChildren, start, splitLength, labelString);
        } // endfor
        
        return splitters;
    }

    //
    // Inner classes:
    //
    public static final class ItemCheckState {
        public Boolean newCheckState;
        public Boolean newGrayedState;
        public ItemCheckState() {}
        public ItemCheckState(Boolean checkState, Boolean grayedState) {
            newCheckState = checkState;
            newGrayedState = grayedState;
        }
    }
}

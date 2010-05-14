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
import java.util.List;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * TreeViewerUtil is a set of static utility methods for operating on TreeViewer.
 */
abstract public class TreeViewerUtil {

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
			boolean includeNode) {
		List result = null;
		ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
		if (provider != null) {
			result = new ArrayList();
			if (includeNode) {
				result.add(node);
			}
			Object[] children = provider.getChildren(node);

            for (int i = 0; i < children.length; i++) {
				result.add(children[i]);
				List childsChildren = TreeViewerUtil.getDescendantsOfNode(viewer, 
						children[i], false);
				result.addAll(childsChildren);
			}
		}
		return result;
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
      */
    public static void setAllChecked(CheckboxTreeViewer viewer, boolean checkedState) {
        setSubtreeChecked(viewer, viewer.getInput(), checkedState);
    }

    /** Set the checked status of all children of the specified node.  This method uses 
      *  viewer.setChecked(), which has some performance issues in large trees.
      * 
      * @param viewer The viewer we are using.
      * @param node The node whose subtree to check.
      * @param checkedState The new checked state of each node.
      */
    public static void setSubtreeChecked(CheckboxTreeViewer viewer, Object node, boolean checkedState) {
        // get children:
        ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
        if (provider == null) {
            // no provider; can't proceed.
            return;
        } // endif
            
        Object[] children = provider.getChildren(node);

        // set checked on each child:
        for (int i = 0; i < children.length; i++) {
            Object child = children[i];
            viewer.setChecked(child, checkedState);
            viewer.setSubtreeChecked(node, checkedState);
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
    	Object[] children = contentProvider.getChildren(node);

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
}

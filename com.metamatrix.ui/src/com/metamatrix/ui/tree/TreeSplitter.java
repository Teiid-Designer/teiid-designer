/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * Tree node class that will automatically split its contents into chunks of LargeTreeContentProvider.MAX_NODES or less. It works
 * off a backing array, and takes care to minimize memory usage and maximize performance.
 * 
 * @see com.metamatrix.ui.tree.LargeTreeContentProvider
 * @see com.metamatrix.ui.tree.TreeViewerUtil
 */
public class TreeSplitter implements IAdaptable {
    private static final IWorkbenchAdapter WORKBENCH_ADAPTER = new TreeSplitterWorkbenchAdapter();

    private Object parent;
    private Object[] children;
    private Object[] mainArray;
    private int offset;
    private int length;
    private String name;
    private LargeTreeContentProvider largeModel;

    /**
     * Constructs a TreeSplitter.
     * 
     * @param largeModel Used to construct more TreeSplitters and establish names for these splitters
     * @param parent
     * @param array
     * @param offset
     * @param length
     * @param label
     */
    public TreeSplitter( LargeTreeContentProvider largeModel,
                         Object parent,
                         Object[] array,
                         int offset,
                         int length,
                         String label ) {
        this.largeModel = largeModel;
        this.parent = parent;
        mainArray = array;
        this.offset = offset;
        this.length = length;
        name = label;
    }

    public Object getParent() {
        return parent;
    }

    public synchronized Object[] getChildren() {
        if (children == null) {
            if (length > LargeTreeContentProvider.MAX_NODES) {
                // still too many, make another split:
                children = TreeViewerUtil.split(largeModel, this, mainArray, offset, length);

            } else {
                // small enough to fit. Do so:
                children = new Object[length];
                System.arraycopy(mainArray, offset, children, 0, length);
            } // endif

            // now, remove reference to main array and model (to facilitate GC):
            mainArray = null;
            largeModel = null;
        } // endif

        return children;
    }

    public int getChildCount() {
        int rv;
        if (children != null) {
            rv = children.length;
        } else {
            rv = length;
        } // endif

        return rv;
    }

    public String getName() {
        if (name == null) {
            name = InternalUiConstants.Util.getString("TreeSplitter.label", new Integer(offset), new Integer(offset + length - 1)); //$NON-NLS-1$
        } // endif

        return name;
    }

    /**
     * Returns whether this node has been fully created. Essentially, but not exactly, whether getChildren() has been called.
     * 
     * @return
     */
    public boolean isMaterialized() {
        return children != null;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Object getAdapter( Class adapter ) {
        if (IWorkbenchAdapter.class.equals(adapter)) {
            return WORKBENCH_ADAPTER;
        } // endif

        return null;
    }

    static class TreeSplitterWorkbenchAdapter implements IWorkbenchAdapter {
        public Object[] getChildren( Object o ) {
            if (o instanceof TreeSplitter) {
                TreeSplitter ts = (TreeSplitter)o;
                return ts.getChildren();
            } // endif

            return null;
        }

        public ImageDescriptor getImageDescriptor( Object o ) {
            if (o instanceof TreeSplitter) {
                return UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.TREE_SPLITTER_LINE);
            } // endif

            return null;
        }

        public String getLabel( Object o ) {
            if (o instanceof TreeSplitter) {
                TreeSplitter ts = (TreeSplitter)o;
                return ts.getName();
            } // endif

            return null;
        }

        public Object getParent( Object o ) {
            if (o instanceof TreeSplitter) {
                TreeSplitter ts = (TreeSplitter)o;
                return ts.getParent();
            } // endif

            return null;
        }
    } // endclass TreeSplitterWorkbenchAdapter
}

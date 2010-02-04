/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.tree;

import java.util.Collections;
import java.util.List;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * @since 4.0
 */
public class SortedTreeNode extends SimpleTreeNode
implements Comparable {
    //============================================================================================================================
	// Constructors

	/**
	 * @since 4.0
	 */
	public SortedTreeNode() {
		super();
	}

	/**
	 * @param object
	 * @since 4.0
	 */
	public SortedTreeNode(final Object object) {
		super(object);
	}

	/**
	 * @param parent
	 * @since 4.0
	 */
	public SortedTreeNode(final SimpleTreeNode parent) {
		super(parent);
	}

	/**
	 * @param object
	 * @param parent
	 * @since 4.0
	 */
	public SortedTreeNode(final Object object, final SimpleTreeNode parent) {
		super(object, parent);
	}

    //============================================================================================================================
	// SimpleTreeNode Methods
    
    /**
	 * @see com.metamatrix.core.tree.SimpleTreeNode#addChild(com.metamatrix.core.tree.SimpleTreeNode)
	 * @since 4.0
	 */
	@Override
    protected boolean addChild(final SimpleTreeNode child) {
        final List children = getChildList();
        int ndx = Collections.binarySearch(children, child);
        if (ndx < 0) {
            ndx += ndx * -2 - 1;
        }
        children.add(ndx, child);
        return true;
	}
    
    //============================================================================================================================
	// Comparable Methods
    
    /**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @since 4.0
	 */
	public int compareTo(final Object node) {
        ArgCheck.isInstanceOf(SortedTreeNode.class, node);
        final Object nodeObj = ((SimpleTreeNode)node).getObject();
        final Object obj = getObject();
        if (obj == null  &&  nodeObj == null) {
            return 0;
        }
        if (obj == null) {
            return -1;
        }
        if (nodeObj == null) {
            return 1;
        }
        return obj.toString().compareTo(nodeObj.toString());
	}

    //============================================================================================================================
	// Object Methods
    
    /**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @since 4.0
	 */
	@Override
    public boolean equals(final Object node) {
        if (!(node instanceof SortedTreeNode)) {
            return false;
        }
        final Object nodeObj = ((SimpleTreeNode)node).getObject();
        final Object obj = getObject();
        if (obj == null) {
            return (nodeObj == null);
        }
		return obj.equals(nodeObj);
	}
}

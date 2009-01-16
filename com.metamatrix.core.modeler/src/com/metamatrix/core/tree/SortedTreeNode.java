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

package com.metamatrix.core.tree;

import java.util.Collections;
import java.util.List;

import com.metamatrix.core.util.ArgCheck;

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

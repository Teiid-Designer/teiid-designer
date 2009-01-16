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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.metamatrix.core.util.StringUtil;

/**
 * @since 4.0
 */
public class SimpleTreeNode
implements StringUtil.Constants, TreeNode {
    //============================================================================================================================
	// Variables
    
    private Object obj;
    private SimpleTreeNode parent;
    private List children;
    
    //============================================================================================================================
	// Constructors
    
    /**
	 * @since 4.0
	 */
	public SimpleTreeNode() {
        this(null, null);
	}
    
    /**
	 * @since 4.0
	 */
	public SimpleTreeNode(final Object object) {
        this(object, null);
	}
    
    /**
     * @since 4.0
     */
    public SimpleTreeNode(final SimpleTreeNode parent) {
        this(null, parent);
    }
    
    /**
	 * @since 4.0
	 */
	public SimpleTreeNode(final Object object, final SimpleTreeNode parent) {
        setObject(object);
        setParent(parent);
        this.children = createChildList();
	}
    
    //============================================================================================================================
	// TreeNode Methods
    
    /**
	 * @see com.metamatrix.core.tree.TreeNode#getObject()
	 * @since 4.0
	 */
	public final Object getObject() {
		return this.obj;
	}
    
    /**
	 * @see com.metamatrix.core.tree.TreeNode#getParent()
	 * @since 4.0
	 */
	public final TreeNode getParent() {
		return this.parent;
	}
    
    /**
	 * @see com.metamatrix.core.tree.TreeNode#getChildren()
	 * @since 4.0
	 */
	public final List getChildren() {
		return Collections.unmodifiableList(this.children);
	}
    
    /**
	 * @see com.metamatrix.core.tree.TreeNode#hasChildren()
	 * @since 4.0
	 */
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}
    
    //============================================================================================================================
	// Property Methods
    
    /**
	 * @since 4.0
	 */
	public void setObject(final Object object) {
		this.obj = object;
	}
    
    /**
     * If it is possible that a node that is removed cannot be re-added, then it is also possible that if this method returns
     * false, this node will be an orphan.
	 * @since 4.0
	 */
	public boolean setParent(final SimpleTreeNode parent) {
        final SimpleTreeNode oldParent = this.parent;
        if (parent == oldParent) {
            return false;
        }
        if (oldParent != null) {
            if (!oldParent.removeChild(this)) {
                return false;
            }
        }
        this.parent = parent;
        if (parent != null) {
            if (!parent.addChild(this)) {
                // Attempt to add this node back to its old parent.  If this fails, this node will be an orphan.
                oldParent.addChild(this);
                return false;
            }
        }
		return true;
	}
    
    //============================================================================================================================
	// Declared Methods
    
    /**
	 * @since 4.0
	 */
	protected List createChildList() {
		return new ArrayList();
	}
    
    /**
	 * @since 4.0
	 */
	protected List getChildList() {
		return this.children;
	}
    
    /**
	 * @since 4.0
	 */
	protected boolean addChild(final SimpleTreeNode child) {
		return this.children.add(child);
	}
    
    /**
	 * @since 4.0
	 */
	private boolean removeChild(final TreeNode child) {
        return this.children.remove(child);
	}
    
    //============================================================================================================================
	// Object Methods
    
    /**
	 * @see java.lang.Object#toString()
	 * @since 4.0
	 */
	@Override
    public String toString() {
		return (this.obj == null ? EMPTY_STRING : this.obj.toString());
	}
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.tree;

import java.util.List;

/**
 * @since 4.0
 */
public interface TreeNode {
    //============================================================================================================================
	// Methods
    
    /**
     * @return This node's object; may be null.
	 * @since 4.0
	 */
	Object getObject();
    
    /**
     * @return This node's parent; may be null.
	 * @since 4.0
	 */
	TreeNode getParent();
    
    /**
     * @return An unmodifiable list of this node's children; never null.
	 * @since 4.0
	 */
	List getChildren();
    
    /**
	 * @since 4.0
	 */
	boolean hasChildren();
}

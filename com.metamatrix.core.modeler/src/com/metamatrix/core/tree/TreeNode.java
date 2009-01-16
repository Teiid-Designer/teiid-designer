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

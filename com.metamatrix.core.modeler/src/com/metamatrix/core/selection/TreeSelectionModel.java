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

package com.metamatrix.core.selection;

/**
 * TreeSelectionModel
 */
public interface TreeSelectionModel extends TreeSelection {

    /**
     * Set the selection mode on the specified node.  Depending upon the existing value and upon the
     * new value, invoking this method may cause the selection mode to change on other node below 
     * the supplied object.  This method does nothing if the new selection mode matches the current mode.
     * Note that it is not possible to set the node as {@link #PARTIALLY_SELECTED}, since that is done
     * as a by-product of selecting or unselecting children.
     * @param node the node in the tree for which the selection state is to be returned; may not be null
     * @param selected true if the new selection mode of the node is to be {@link #SELECTED}, or false
     * if the selection mode is to be {@link #UNSELECTED}.
     */
    public void setSelected( Object node, boolean selected );

}

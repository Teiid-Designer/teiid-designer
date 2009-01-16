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

package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.TreeItem;

/**<p>
 * </p>
 * @since 4.0
 */
public interface ITreeViewerController {
    //============================================================================================================================
	// MVC Controller Methods

    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void checkedStateToggled(TreeItem item);
    
    /**<p>
     * </p>
     * @since 4.0
     */
    boolean isItemCheckable(TreeItem item);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemCollapsed(TreeExpansionEvent event);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemDoubleClicked(DoubleClickEvent event);

    /**<p>
     * </p>
     * @since 4.0
     */
    void itemExpanded(TreeExpansionEvent event);

    /**<p>
	 * </p>
	 * @since 4.0
	 */
    void itemSelected(SelectionChangedEvent event);
    
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void update(TreeItem item, boolean selected);
}

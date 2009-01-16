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

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @since 4.0
 */
public interface IListPanelController {
    //============================================================================================================================
	// Declared Methods
    
    /**<p>
     * </p>
     * @since 4.0
     */
    Object[] addButtonSelected();
    
    /**<p>
     * </p>
     * @since 4.0
     */
    void downButtonSelected(IStructuredSelection selection);

    /**<p>
     * </p>
     * @since 4.0
     */
    Object editButtonSelected(IStructuredSelection selection);
    
    /**<p>
	 * </p>
	 * @since 4.0
	 */
	void itemsSelected(IStructuredSelection selection);
    
    /**<p>
     * </p>
     * @since 4.0
     */
    Object[] removeButtonSelected(IStructuredSelection selection);
    
    /**<p>
     * </p>
     * @since 4.0
     */
    void upButtonSelected(IStructuredSelection selection);
}

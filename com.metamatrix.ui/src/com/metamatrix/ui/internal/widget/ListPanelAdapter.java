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

import com.metamatrix.core.util.StringUtil;

/**
 * @since 4.0
 */
public class ListPanelAdapter
implements IListPanelController, StringUtil.Constants {
    //============================================================================================================================
	// ListEditPanelController Methods

    /**<p>
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#addButtonSelected()
	 * @since 4.0
	 */
	public Object[] addButtonSelected() {
		return EMPTY_STRING_ARRAY;
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#downButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public void downButtonSelected(final IStructuredSelection selection) {
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#editButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public Object editButtonSelected(IStructuredSelection selection) {
        return selection.getFirstElement();
	}

    /**<p>
     * Does nothing.
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#itemsSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public void itemsSelected(final IStructuredSelection selection) {
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#removeButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public Object[] removeButtonSelected(IStructuredSelection selection) {
		return selection.toArray();
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see com.metamatrix.ui.internal.widget.IListPanelController#upButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	public void upButtonSelected(IStructuredSelection selection) {
	}
}

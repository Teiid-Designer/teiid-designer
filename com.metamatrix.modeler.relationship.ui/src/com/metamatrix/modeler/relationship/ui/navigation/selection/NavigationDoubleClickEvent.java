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

package com.metamatrix.modeler.relationship.ui.navigation.selection;

import java.util.EventObject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;

/**
 * Event object describing a double-click. The source of these
 * events is a viewer.
 *
 * @see IDoubleClickListener
 */
public class NavigationDoubleClickEvent extends EventObject {

	/**
     */
    private static final long serialVersionUID = 1L;
    /**
	 * The selection.
	 */
	protected ISelection selection;
/**
 * Creates a new event for the given source and selection.
 *
 * @param source the viewer
 * @param selection the selection
 */
public NavigationDoubleClickEvent(Object source, ISelection selection) {
	super(source);
	Assert.isNotNull(selection);
	this.selection = selection;
}
/**
 * Returns the selection.
 *
 * @return the selection
 */
public ISelection getSelection() {
	return selection;
}

}

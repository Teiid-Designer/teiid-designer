/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

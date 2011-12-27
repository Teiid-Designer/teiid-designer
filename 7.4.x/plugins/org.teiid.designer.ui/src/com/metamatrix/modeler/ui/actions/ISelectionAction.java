/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import org.eclipse.jface.viewers.ISelection;

/**
 * This interface provides actions away to answer the question "Do I care about the selection"
 * and should I be displayed to the user.
 * @author BLaFond
 *
 */
public interface ISelectionAction {

	boolean isApplicable(ISelection selection);
	
}

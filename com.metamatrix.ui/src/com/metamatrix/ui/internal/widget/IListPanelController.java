/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

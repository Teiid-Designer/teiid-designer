/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;


/**
 * The <code>ICheckableController</code> is a controller for an {@link org.eclipse.jface.viewers.ICheckable}.
 * @since 4.2
 */
public interface ICheckableController {

    /**
     * Indicates if the check state of the specified object can be changed.
     * @param theObject the object being tested
     * @return <code>true</code>if editable; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean isEditable(Object theObject);

}

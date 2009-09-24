/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 *
 */
public interface IPreviewDataContentProvider extends IStructuredContentProvider {
    /**
     * Clears all displayed results.
     *
     * @since 5.5.3
     */
    void clearAllResults();

    /**
     * Removes the specified results.
     *
     * @param results the results to be removed
     * @since 5.5.3
     */
    void removeResults( Object[] results );
}

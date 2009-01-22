/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**<p>
 * Implements all {@link ITableLabelProvider} methods except for
 * {@link ITableLabelProvider#getColumnText(Object, int) getColumnText(Object, int)}.
 * </p>
 * @since 4.0
 */
public abstract class AbstractTableLabelProvider
implements ITableLabelProvider {
    //============================================================================================================================
    // Implemented Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.0
     */
    public void addListener(final ILabelProviderListener listener) {
    }

    /**<p>
     * </p>
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.0
     */
    public void dispose() {
    }

	/**<p>
	 * </p>
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 * @since 4.0
	 */
	public Image getColumnImage(final Object element, final int column) {
		return null;
	}

	/**<p>
	 * </p>
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 * @since 4.0
	 */
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	/**<p>
	 * </p>
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 * @since 4.0
	 */
	public void removeListener(final ILabelProviderListener listener) {
	}
}

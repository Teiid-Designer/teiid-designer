/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.widget;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.teiid.core.designer.util.CoreStringUtil;

/**
 * @since 8.0
 */
public class ListPanelAdapter
implements IListPanelController, CoreStringUtil.Constants {
    //============================================================================================================================
	// ListEditPanelController Methods

    /**<p>
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#addButtonSelected()
	 * @since 4.0
	 */
	@Override
	public Object[] addButtonSelected() {
		return EMPTY_STRING_ARRAY;
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#downButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public void downButtonSelected(final IStructuredSelection selection) {
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#editButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public Object editButtonSelected(IStructuredSelection selection) {
        return selection.getFirstElement();
	}

    /**<p>
     * Does nothing.
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#itemsSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public void itemsSelected(final IStructuredSelection selection) {
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#removeButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public Object[] removeButtonSelected(IStructuredSelection selection) {
		return selection.toArray();
	}

	/**<p>
     * Does nothing.
	 * </p>
	 * @see org.teiid.designer.ui.common.widget.IListPanelController#upButtonSelected(org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public void upButtonSelected(IStructuredSelection selection) {
	}
}

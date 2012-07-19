/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.ui.common.UiConstants;
import org.teiid.designer.ui.common.UiPlugin;
import org.teiid.designer.ui.common.widget.ButtonProvider;

/**
 * @param <T>
 *
 * @since 8.0
 */
public abstract class CheckBoxColumnProvider<T> extends DefaultColumnProvider<T, Boolean> {

    /**
     * {@inheritDoc}
     * 
     * @return {{@link SWT#CENTER}
     * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getAlignment()
     */
    @Override
    public int getAlignment() {
        return SWT.CENTER;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the {@link CheckboxCellEditor} class
     * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getEditorClass()
     */
    @Override
    public Class<? extends CellEditor> getEditorClass() {
        return CheckboxCellEditor.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the image of a {@link ButtonProvider check box} with the appropriate {@link #getValue(Object) checked} state
     * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getImage(java.lang.Object)
     */
    @Override
    public final Image getImage( final T element ) {
    	if (getValue(element)) return UiPlugin.getDefault().getImage(UiConstants.Images.CHECKED_CHECKBOX);
    	return UiPlugin.getDefault().getImage(UiConstants.Images.UNCHECKED_CHECKBOX);
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>null</code>
     * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final T element ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     *@return <code>false</code>
     * @see org.teiid.designer.ui.common.table.DefaultColumnProvider#isResizable()
     */
    @Override
    public boolean isResizable() {
        return false;
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.ui.UiPlugin;

/**
 * @param <T>
 */
public abstract class CheckBoxColumnProvider<T> extends DefaultColumnProvider<T, Boolean> {

    /**
     * {@inheritDoc}
     * 
     * @return {{@link SWT#CENTER}
     * @see com.metamatrix.ui.table.DefaultColumnProvider#getAlignment()
     */
    @Override
    public int getAlignment() {
        return SWT.CENTER;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the {@link CheckboxCellEditor} class
     * @see com.metamatrix.ui.table.DefaultColumnProvider#getEditorClass()
     */
    @Override
    public Class<? extends CellEditor> getEditorClass() {
        return CheckboxCellEditor.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the image of a {@link ButtonProvider check box} with the appropriate {@link #getValue(Object) checked} state
     * @see com.metamatrix.ui.table.DefaultColumnProvider#getImage(java.lang.Object)
     */
    @Override
    public final Image getImage( final T element ) {
        if (getValue(element)) return JFaceResources.getImage(UiPlugin.CHECKED_BOX);
        return JFaceResources.getImage(UiPlugin.UNCHECKED_BOX);
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>null</code>
     * @see com.metamatrix.ui.table.DefaultColumnProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final T element ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     *@return <code>false</code>
     * @see com.metamatrix.ui.table.DefaultColumnProvider#isResizable()
     */
    @Override
    public boolean isResizable() {
        return false;
    }
}

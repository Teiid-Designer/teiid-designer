/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;

/**
 * @param <T>
 */
public abstract class CheckBoxColumnProvider<T> extends DefaultColumnProvider<T, Boolean> {

    /**
     * {@inheritDoc}
     * 
     * @return {{@link SWT#CENTER}
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.DefaultColumnProvider#getAlignment()
     */
    @Override
    public int getAlignment() {
        return SWT.CENTER;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the {@link CheckboxCellEditor} class
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.DefaultColumnProvider#getEditorClass()
     */
    @Override
    public Class<? extends CellEditor> getEditorClass() {
        return CheckboxCellEditor.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the image of a {@link ButtonProvider check box} with the appropriate {@link #getValue(Object) checked} state
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.DefaultColumnProvider#getImage(java.lang.Object)
     */
    @Override
    public final Image getImage( final T element ) {
        if (getValue(element)) return JFaceResources.getImage(VdbUiPlugin.CHECKED_BOX);
        return JFaceResources.getImage(VdbUiPlugin.UNCHECKED_BOX);
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>null</code>
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.DefaultColumnProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final T element ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     *@return <code>false</code>
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.DefaultColumnProvider#isResizable()
     */
    @Override
    public boolean isResizable() {
        return false;
    }
}

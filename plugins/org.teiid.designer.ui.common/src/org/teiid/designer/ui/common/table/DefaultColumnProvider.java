/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @param <T>
 * @param <V>
 */
public abstract class DefaultColumnProvider<T, V> implements ColumnProvider<T, V> {

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings( "unchecked" )
    public int compare( final T element1,
                        final T element2 ) {
        final V value = getValue(element1);
        if (value instanceof Comparable) return ((Comparable)value).compareTo(getValue(element2));
        final String text = getText(element1);
        if (text != null) return text.compareTo(getText(element2));
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {{@link SWT#LEFT}
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getAlignment()
     */
    @Override
    public int getAlignment() {
        return SWT.LEFT;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the {@link TextCellEditor} class
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getEditorClass()
     */
    @Override
    public Class<? extends CellEditor> getEditorClass() {
        return TextCellEditor.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>null</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( final T element ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@link #getValue(Object)}<code>.toString()</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final T element ) {
        final V value = getValue(element);
        return value == null ? null : value.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>null</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getText(java.lang.Object)
     */
    @Override
    public String getToolTip( final T element ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>false</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#isEditable(java.lang.Object)
     */
    @Override
    public boolean isEditable( final T element ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#isResizable()
     */
    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code>
     * @see org.teiid.designer.ui.common.table.ColumnProvider#isSortable()
     */
    @Override
    public boolean isSortable() {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation does nothing.
     * </p>
     * 
     * @see org.teiid.designer.ui.common.table.ColumnProvider#getValue(java.lang.Object)
     */
    @Override
    public void setValue( final T element,
                              final V value ) {
    }
}

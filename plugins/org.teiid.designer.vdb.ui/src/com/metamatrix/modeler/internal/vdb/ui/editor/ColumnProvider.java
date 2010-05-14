/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.util.Comparator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @param <T>
 * @param <V>
 */
public interface ColumnProvider<T, V> extends Comparator<T> {

    /**
     * @return {@link SWT#LEFT}, {@link SWT#CENTER}, or {@link SWT#RIGHT}
     */
    int getAlignment();

    /**
     * @return the cell editor that will be used to edit the {@link TableColumn column's} {@link #isEditable(Object) editable} cells
     */
    Class<? extends CellEditor> getEditorClass();

    /**
     * @param element
     * @return the {@link TableColumn column's} image for the supplied element value
     */
    Image getImage( T element );

    /**
     * @return the name of the {@link TableColumn column}
     */
    String getName();

    /**
     * @param element
     * @return the {@link TableColumn column's} text for the supplied element value (often just the string representation of the
     *         cell's {@link #getValue(Object) value})
     */
    String getText( T element );

    /**
     * @param element
     * @return the {@link TableColumn column's} tooltip for the supplied element value
     */
    String getToolTip( T element );

    /**
     * @param element
     * @return the {@link TableColumn column's} model value for the supplied element value; must not be <code>null</code>
     */
    V getValue( T element );

    /**
     * @param element
     * @return <code>true</code> if the {@link TableColumn column} is editable for the supplied element value
     */
    boolean isEditable( T element );

    /**
     * @return <code>true</code> if the column is resizable
     */
    boolean isResizable();

    /**
     * @return <code>true</code> if the column is sortable
     */
    boolean isSortable();

    /**
     * Must be implemented if the {@link TableColumn column} is {@link #isEditable(Object) editable} for the supplied element value.
     * 
     * @param element
     * @param value
     */
    void setValue( T element,
                   V value );
}

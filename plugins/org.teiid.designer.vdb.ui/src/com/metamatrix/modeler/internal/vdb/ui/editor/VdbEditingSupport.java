/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

/**
 * The <code>VdbEditingSupport</code> class provides table cell editing that provides a combobox editor when there are know values
 * and a text editor when there are no known values.
 */
abstract class VdbEditingSupport extends EditingSupport {

    private CellEditor currentEditor;
    private String[] items;
    private IResource vdb;

    /**
     * @param viewer the table viewer (may not be <code>null</code>)
     * @param vdb the VDB being edited (may not be <code>null</code>)
     */
    public VdbEditingSupport( ColumnViewer viewer,
                              IResource vdb ) {
        super(viewer);
        this.vdb = vdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit( Object element ) {
        return !this.vdb.getResourceAttributes().isReadOnly();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {
        this.items = refreshItems();

        // makes sure items is null if empty
        if ((this.items != null) && (this.items.length == 0)) {
            this.items = null;
        }

        if (this.items == null) {
            // use text editor since there are no known values
            this.currentEditor = new TextCellEditor((Composite)getViewer().getControl());
        } else {
            // use combobox editor since we do have known values
            ComboBoxCellEditor comboEditor = new ComboBoxCellEditor((Composite)getViewer().getControl(), new String[0], SWT.NONE);
            comboEditor.setItems(this.items);
            this.currentEditor = comboEditor;
        }

        return this.currentEditor;
    }

    /**
     * @param element the object whose value is being obtained
     * @return the value
     */
    protected abstract String getElementValue( Object element );

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
     */
    @Override
    protected Object getValue( Object element ) {
        String value = getElementValue(element);

        if (value == null) {
            value = ""; //$NON-NLS-1$
        }

        // when using text editor just return the value
        if (this.items == null) {
            return value;
        }

        // when using combobox editor return index of value
        for (int i = 0; i < this.items.length; ++i) {
            if (value.equals(this.items[i])) {
                return i;
            }
        }

        // current value is not found on server insert it at index zero
        String[] temp = new String[this.items.length + 1];
        temp[0] = value;
        System.arraycopy(this.items, 0, temp, 1, this.items.length);
        this.items = temp;
        ((ComboBoxCellEditor)this.currentEditor).setItems(this.items);
        return 0;
    }

    /**
     * @return the list of known values (can be <code>null</code> or empty)
     */
    protected abstract String[] refreshItems();

    /**
     * @param element the element whose value needs to be set
     * @param newValue the new value
     */
    protected abstract void setElementValue( Object element,
                                             String newValue );

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void setValue( Object element,
                             Object value ) {
        String newValue;

        if (value instanceof Integer) {
            // using the combobox editor
            int index = (Integer)value;

            if (index == -1) {
                // user typed in a new value
                newValue = ((CCombo)((ComboBoxCellEditor)this.currentEditor).getControl()).getText();
            } else {
                // user picked an existing value
                newValue = this.items[index];
            }
        } else {
            // using the text editor
            newValue = (String)value;
        }

        setElementValue(element, newValue);
    }

}

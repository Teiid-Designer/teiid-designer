/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.ui.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Simple abstract class for use in table viewers with columns.
 */
public abstract class CheckBoxEditingSupport  extends EditingSupport {

    /**
     * The current {@link CellEditor}.
     */
    protected CellEditor checkBoxEditor;


    /**
     * @param viewer the table viewer (may not be <code>null</code>)
     * @param resource the resource being edited (may not be <code>null</code>)
     */
    public CheckBoxEditingSupport( ColumnViewer viewer ) {
        super(viewer);
    }

    /**
     * @param element the element being edited
     * @return <code>true</code> if the ComboBox editor should be editable
     */
    protected boolean canAddNewValue( Object element ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit( Object element ) {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {

        // use combobox editor since we do have known values
        int style = (canAddNewValue(element) ? SWT.NONE : SWT.READ_ONLY);
        checkBoxEditor = new CheckboxCellEditor((Composite)getViewer().getControl(), style);

        return this.checkBoxEditor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
     */
    @Override
    protected Object getValue( Object element ) {
        return Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void setValue( Object element,
                             Object value ) {
    	
    	setElementValue(element, value);

        getViewer().refresh(element);
    }
    
    /**
     * @param element the element whose value needs to be set
     * @param newValue the new value
     */
    protected abstract void setElementValue( Object element,
                                             Object newValue );


}

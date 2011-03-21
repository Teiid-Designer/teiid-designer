/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

/**
 * The <code>ResourceEditingSupport</code> class provides cell editing support for a resource. A combobox editor is used when there
 * are know values. Otherwise a text editor is used.
 */
public abstract class ResourceEditingSupport extends EditingSupport {

    /**
     * The current {@link CellEditor}.
     */
    protected CellEditor currentEditor;

    /**
     * The editors allowed values (<code>null</code> or empty if a text cell editor should be used).
     */
    private String[] allowedValues;

    /**
     * An optional validator.
     */
    private ICellEditorValidator validator;

    /**
     * The resource whose attributes are used to determine if the cell editor value is editable.
     */
    private IResource resource;

    /**
     * @param viewer the table viewer (may not be <code>null</code>)
     * @param resource the resource being edited (may not be <code>null</code>)
     */
    public ResourceEditingSupport( ColumnViewer viewer,
                                   IResource resource ) {
        super(viewer);
        this.resource = resource;
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
        return !this.resource.getResourceAttributes().isReadOnly();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {
        this.allowedValues = refreshItems(element);

        // makes sure items is null if empty
        if ((this.allowedValues != null) && (this.allowedValues.length == 0)) {
            this.allowedValues = null;
        }

        if (this.allowedValues == null) {
            // use text editor since there are no known values
            this.currentEditor = new TextCellEditor((Composite)getViewer().getControl());

            // hook validator up
            if (this.validator != null) {
                this.currentEditor.setValidator(this.validator);
            }
        } else {
            // use combobox editor since we do have known values
            int style = (canAddNewValue(element) ? SWT.NONE : SWT.READ_ONLY);
            ComboBoxCellEditor comboEditor = new ComboBoxCellEditor((Composite)getViewer().getControl(), new String[0], style);
            comboEditor.setItems(this.allowedValues);
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
        if (this.allowedValues == null) {
            return value;
        }

        // when using combobox editor return index of value
        for (int i = 0; i < this.allowedValues.length; ++i) {
            if (value.equals(this.allowedValues[i])) {
                return i;
            }
        }

        // current value is not found on server insert it at index zero
        String[] temp = new String[this.allowedValues.length + 1];
        temp[0] = value;
        System.arraycopy(this.allowedValues, 0, temp, 1, this.allowedValues.length);
        this.allowedValues = temp;
        ((ComboBoxCellEditor)this.currentEditor).setItems(this.allowedValues);
        return 0;
    }

    /**
     * @param element the element whose items are being requested (never <code>null</code>)
     * @return the list of known values (can be <code>null</code> or empty)
     */
    protected abstract String[] refreshItems( Object element );

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
                newValue = this.allowedValues[index];
            }
        } else {
            // using the text editor
            newValue = (String)value;
        }

        setElementValue(element, newValue);
        getViewer().refresh(element);
    }

    /**
     * @param validator the validator to use when editing a cell (can be <code>null</code> if no validation should be done)
     */
    public void setValidator( ICellEditorValidator validator ) {
        this.validator = validator;
    }

}

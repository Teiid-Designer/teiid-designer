/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.vdb.TranslatorOverrideProperty;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.table.ResourceEditingSupport;

class TranslatorOverridePropertyEditingSupport extends ResourceEditingSupport {

    /**
     * @param viewer
     * @param vdb
     */
    public TranslatorOverridePropertyEditingSupport( ColumnViewer viewer,
                                   IResource vdb ) {
        super(viewer, vdb);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        return super.canEdit(element) && property.getDefinition().isModifiable();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;

        // no editor if not editable
        if (!property.getDefinition().isModifiable()) {
            return null;
        }

        if (property.getDefinition().isMasked()) {
            this.currentEditor = new TextCellEditor((Composite)getViewer().getControl());
            ((Text)currentEditor.getControl()).setEchoChar('*');
            return this.currentEditor;
        }

        return super.getCellEditor(element);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#getElementValue(java.lang.Object)
     */
    @Override
    protected String getElementValue( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        String overrideValue = property.getOverriddenValue();

        if (StringUtilities.isEmpty(overrideValue)) {
            return property.getDefinition().getDefaultValue();
        }

        return overrideValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#refreshItems(java.lang.Object)
     */
    @Override
    protected String[] refreshItems( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        return property.getDefinition().getAllowedValues();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
     */
    @Override
    protected void setElementValue( Object element,
                                    String newValue ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        String currentValue = property.getOverriddenValue();
        boolean doIt = false;

        if (StringUtilities.isEmpty(newValue)) {
            if (!StringUtilities.isEmpty(currentValue)) {
                doIt = true;
            }
        } else {
            String defaultValue = property.getDefinition().getDefaultValue();

            // new value is not empty
            // current value is empty
            // set value if new value is not the default value
            if (StringUtilities.isEmpty(currentValue)) {
                if (StringUtilities.isEmpty(defaultValue) || !defaultValue.equals(newValue)) {
                    doIt = true;
                }
            } else {
                // new value is not empty
                // current value is not empty
                // set if new value != current value
                // set if new value != default value
                // if new value == default value set to null
                if (!newValue.equals(currentValue)) {
                    doIt = true;

                    if (!StringUtilities.isEmpty(defaultValue) && defaultValue.equals(newValue)) {
                        newValue = null;
                    }
                }
            }
        }

        if (doIt) {
            property.setValue(newValue);
            // cause a selection event to be fired so that actions can set their enablement
            getViewer().setSelection(new StructuredSelection(element));
        }
    }
}
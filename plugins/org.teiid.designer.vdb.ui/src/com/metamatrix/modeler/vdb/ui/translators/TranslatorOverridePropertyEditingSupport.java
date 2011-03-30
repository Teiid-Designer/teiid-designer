/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.vdb.TranslatorOverrideProperty;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.table.PropertyEditingSupport;

class TranslatorOverridePropertyEditingSupport extends PropertyEditingSupport {

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
     * @see com.metamatrix.ui.table.PropertyEditingSupport#getPropertyDefinition(java.lang.Object)
     */
    @Override
    protected PropertyDefinition getPropertyDefinition( Object element ) {
        TranslatorOverrideProperty property = (TranslatorOverrideProperty)element;
        return property.getDefinition();
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
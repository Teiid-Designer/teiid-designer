/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.teiid.core.properties.PropertyDefinition;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.table.PropertyEditingSupport;

class ExtendedPropertyEditingSupport extends PropertyEditingSupport {

    /**
     * @param viewer
     * @param model
     */
    public ExtendedPropertyEditingSupport( ColumnViewer viewer,
                                           IResource iResource ) {
        super(viewer, iResource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.table.ResourceEditingSupport#getElementValue(java.lang.Object)
     */
    @Override
    protected String getElementValue( Object element ) {
        ExtendedProperty property = (ExtendedProperty)element;
        String overrideValue = property.getValue();

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
        ExtendedProperty property = (ExtendedProperty)element;
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
        ExtendedProperty property = (ExtendedProperty)element;
        String currentValue = property.getValue();
        boolean doIt = false;

        if (StringUtilities.isEmpty(newValue)) {
            doIt = true;
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

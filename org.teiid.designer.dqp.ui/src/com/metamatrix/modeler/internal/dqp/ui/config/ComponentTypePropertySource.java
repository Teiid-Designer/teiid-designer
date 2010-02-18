/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import java.util.Collection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.designer.runtime.ConnectorType;

/**
 * @since 4.2
 */
public class ComponentTypePropertySource implements IPropertySource {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private final ConnectorType connectorType;

    private boolean editable;

    /**
     * @since 4.2
     */
    public ComponentTypePropertySource( ConnectorType type ) {
        this.connectorType = type;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    public Object getEditableValue() {
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {

        Collection<PropertyDefinition> propDefns;

        try {
            propDefns = this.connectorType.getPropertyDefinitions();
        } catch (Exception e) {
            // TODO log
            return new IPropertyDescriptor[0];
        }

        IPropertyDescriptor[] result = new IPropertyDescriptor[propDefns.size()];
        int index = 0;
        for (PropertyDefinition propertyDefn : propDefns) {
            if (this.editable) {
                result[index++] = new TextPropertyDescriptor(propertyDefn, propertyDefn.getDisplayName());
            } else {
                result[index++] = new PropertyDescriptor(propertyDefn, propertyDefn.getDisplayName());
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue( Object id ) {
        Object defValue = ((PropertyDefinition)id).getDefaultValue();

        if (defValue == null) {
            defValue = EMPTY_STRING;
        }

        return defValue;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet( Object id ) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue( Object id ) {
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
    }

}

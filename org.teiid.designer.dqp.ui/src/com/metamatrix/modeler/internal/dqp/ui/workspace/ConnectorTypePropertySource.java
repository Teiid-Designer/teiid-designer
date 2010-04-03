/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.Collection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.core.util.StringUtil;

/**
 * @since 4.2
 */
public final class ConnectorTypePropertySource implements IPropertySource {

    private final ConnectorType connectorType;

    /**
     * @since 4.2
     */
    public ConnectorTypePropertySource( ConnectorType type ) {
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
            UTIL.log(e);
            return new IPropertyDescriptor[0];
        }

        IPropertyDescriptor[] result = new IPropertyDescriptor[propDefns.size()];
        int index = 0;
        for (PropertyDefinition propertyDefn : propDefns) {
            // connector type properties can't be edited
            result[index++] = new PropertyDescriptor(propertyDefn, propertyDefn.getDisplayName());
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
            defValue = StringUtil.Constants.EMPTY_STRING;
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
        // nothing to do since properties are not editable
    }

    public void setEditable( boolean editable ) {
        // nothing to do since properties are not editable
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
        // nothing to do since properties are not editable
    }

}

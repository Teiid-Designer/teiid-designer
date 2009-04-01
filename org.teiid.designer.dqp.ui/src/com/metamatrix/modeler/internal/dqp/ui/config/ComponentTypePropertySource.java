/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ConfigurationObjectEditor;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;


/**
 * @since 4.2
 */
public class ComponentTypePropertySource implements
                                        IPropertySource {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private ComponentType componentType;

    private boolean editable;

//    private ConfigurationObjectEditor editor;

    /**
     *
     * @since 4.2
     */
    public ComponentTypePropertySource(ComponentType type, ConfigurationObjectEditor editor) {
        this.componentType = type;
//        this.editor = editor;
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
        Collection properties = componentType.getComponentTypeDefinitions();
        IPropertyDescriptor[] result = new IPropertyDescriptor[properties.size()];
        int index = 0;
        for ( Iterator iter = componentType.getComponentTypeDefinitions().iterator() ; iter.hasNext() ; ) {
            ComponentTypeDefn propertyDefn = (ComponentTypeDefn) iter.next();

            if (this.editable) {
                result[index++] = new TextPropertyDescriptor(propertyDefn, propertyDefn.getName());
            } else {
                result[index++] = new PropertyDescriptor(propertyDefn, propertyDefn.getName());
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue(Object id) {
        String defID = ((ComponentTypeDefn)id).getName();
        String defValue = componentType.getDefaultValue(defID);


        if ( defValue == null ) {
            defValue = EMPTY_STRING;
        } else if (ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH.equals(defID)) {
            defValue = ModelerDqpUtils.getConnectorClasspathDisplayValue(defValue);
        }

        return defValue;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet(Object id) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue(Object id) {
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue(Object id,
                                 Object value) {
    }

}

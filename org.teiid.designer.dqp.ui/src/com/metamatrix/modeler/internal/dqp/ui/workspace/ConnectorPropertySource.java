/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;

/**
 * @since 5.5
 */
public class ConnectorPropertySource implements IPropertySource {

    private Connector connector; // null if creating a new connector
    private final ConnectorType type;
    private final Properties properties;
    private final Properties initialValues;

    private boolean isEditable = false;
    private RuntimePropertySourceProvider provider;

    /**
     * @param type the connector type of the connector (never <code>null</code>)
     * @param properties the properties of the connector (never <code>null</code>)
     */
    public ConnectorPropertySource( ConnectorType type,
                                    Properties properties ) {
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        this.type = type;
        this.properties = properties;

        this.initialValues = new Properties(this.properties);
    }

    /**
     * @param connector the connector whose properties are being edited (never <code>null</code>)
     */
    public ConnectorPropertySource( Connector connector ) {
        this(connector.getType(), connector.getProperties());
        this.connector = connector;
        this.properties.setProperty("rarName", connector.getType().getName());
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    public Object getEditableValue() {
        return this.properties;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] result = new IPropertyDescriptor[0];
        Collection<PropertyDefinition> typeDefs = this.type.getPropertyDefinitions();
        boolean showExpertProps = this.provider.isShowingExpertProperties();
        Collection temp = new ArrayList(typeDefs.size());

        for (PropertyDefinition propDefn : typeDefs) {
            String id = propDefn.getName();
            String displayName = propDefn.getDisplayName();

            // don't add if an expert or readonly property and expert properties are not being shown
            if ((propDefn.isAdvanced() || !propDefn.isModifiable()) && !showExpertProps) {
                continue;
            }

            Object descriptor = null;

            // make sure readonly definitions are not modifiable
            if (this.isEditable && propDefn.isModifiable()) {
                descriptor = new TextPropertyDescriptor(id, displayName);
            } else {
                // read only
                descriptor = new PropertyDescriptor(id, displayName);
            }

            temp.add(descriptor);
        }

        if (!temp.isEmpty()) {
            temp.toArray(result = new IPropertyDescriptor[temp.size()]);
        }

        result = sortPropertyDescriptors(result);

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    public boolean isPropertySet( Object id ) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue( Object id ) {
        String result = this.properties.getProperty((String)id);

        if (result == null) {
            result = CoreStringUtil.Constants.EMPTY_STRING;
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue( Object id ) {
        PropertyDefinition propDefn = ((PropertyDefinition)id);
        setPropertyValue(id, this.initialValues.getProperty(propDefn.getName()));
    }

    public void setEditable( boolean isEditable ) {
        this.isEditable = isEditable;
    }

    public void setProvider( RuntimePropertySourceProvider provider ) {
        this.provider = provider;
    }

    /**
     * Sorts the properties using a custom sorter that puts the URL, user, and password always before other properties.
     * 
     * @param theDescriptors the descriptors being sorted
     * @return the sorted descriptors
     * @since 5.5
     */
    private IPropertyDescriptor[] sortPropertyDescriptors( IPropertyDescriptor[] theDescriptors ) {
        List descriptors = Arrays.asList(theDescriptors);
        Collections.sort(descriptors, new DescriptorSorter());
        return (IPropertyDescriptor[])descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
        PropertyDefinition propDefn = ((PropertyDefinition)id);
        try {
            this.connector.setPropertyValue(propDefn.getName(), value.toString());

            if (this.connector != null) {
                this.provider.propertyChanged(this.connector);
            }
        } catch (Exception e) {
            UTIL.log(e);
        }
    }

    /**
     * Sorts property descriptors using there display names but making sure the URL, user, and password (in that order) are always
     * the first 3 properties.
     * 
     * @since 5.5
     */
    class DescriptorSorter implements Comparator {
        public int compare( Object theDescriptor,
                            Object theOtherDescriptor ) {
            int result = 0;
            Object tempId = ((IPropertyDescriptor)theDescriptor).getId();
            Object otherTempId = ((IPropertyDescriptor)theOtherDescriptor).getId();

            if ((tempId instanceof String) && (otherTempId instanceof String)) {
                String id = (String)tempId;
                String otherId = (String)otherTempId;

                // ensure URL comes first, user next, password next, and then sort by display name.
                if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)) {
                    result = -100;
                } else if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER)) {
                    result = (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL) ? 1 : -50);
                } else if (id.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_PASSWORD)) {
                    result = (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)
                              || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER) ? 1 : -25);
                } else if (otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL)
                           || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER)
                           || otherId.equals(JDBCConnectionPropertyNames.CONNECTOR_JDBC_PASSWORD)) {
                    result = 1;
                } else {
                    result = ((IPropertyDescriptor)theDescriptor).getDisplayName().compareTo(((IPropertyDescriptor)theOtherDescriptor).getDisplayName());
                }
            } else if (tempId instanceof Comparable) {
                result = ((Comparable)tempId).compareTo(otherTempId);
            } else {
                result = ((IPropertyDescriptor)theDescriptor).getDisplayName().compareTo(((IPropertyDescriptor)theOtherDescriptor).getDisplayName());
            }

            return result;
        }
    }

}

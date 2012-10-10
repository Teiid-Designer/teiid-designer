/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.JDBCConnectionPropertyNames;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class ConnectionPropertySource implements IPropertySource {

    private TeiidTranslator translator;
    private final Properties initialValues;

    private boolean isEditable = false;
    private RuntimePropertySourceProvider provider;

    /**
     * @param connector the connector whose properties are being edited (never <code>null</code>)
     */
    public ConnectionPropertySource( TeiidTranslator translator ) {
        this(translator.getProperties());
        this.translator = translator;
    }

    /**
     * @param type the connector type of the connector (never <code>null</code>)
     * @param properties the properties of the connector (never <code>null</code>)
     */
    private ConnectionPropertySource( Properties properties ) {
        CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        this.initialValues = new Properties(properties);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    @Override
	public Object getEditableValue() {
        return this.translator;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    @Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] result = new IPropertyDescriptor[0];
        Collection<? extends PropertyDefinition> typeDefs = this.translator.getPropertyDefinitions();
        boolean showExpertProps = this.provider.isShowingExpertProperties();
        Collection<PropertyDescriptor> temp = new ArrayList<PropertyDescriptor>(typeDefs.size());

        for (final PropertyDefinition propDefn : typeDefs) {
            final String id = propDefn.getName();
            String displayName = propDefn.getDisplayName();
            //
            // // don't add if an expert or readonly property and expert properties are not being shown
            // if ((propDefn.isAdvanced() || !propDefn.isModifiable()) && !showExpertProps) {
            // continue;
            // }

            PropertyDescriptor descriptor = null;

            // make sure readonly definitions are not modifiable
            if (this.isEditable && propDefn.isModifiable()) {
                if (propDefn.isMasked()) {
                    descriptor = new MaskedTextPropertyDescriptor(id, displayName);
                } else {
                    descriptor = new TextPropertyDescriptor(id, displayName);
                }

                // set validator
                final TeiidTranslator validator = this.translator;

                descriptor.setValidator(new ICellEditorValidator() {
                    @Override
                    public String isValid( Object value ) {
                        String newValue = (String)value;

                        // OK not to have a value if not required
                        if (((newValue == null) || (newValue.length() == 0)) && !propDefn.isRequired()) {
                            return null;
                        }

                        return (validator.isValidPropertyValue(id, newValue));
                    }
                });
            } else {
                // read only
                if (propDefn.isMasked()) {
                    descriptor = new MaskedTextPropertyDescriptor(id, displayName);
                    ((MaskedTextPropertyDescriptor)descriptor).setEditable(false);
                } else {
                    descriptor = new PropertyDescriptor(id, displayName);
                }
            }

            // identify as expert property
            if (!showExpertProps && propDefn.isAdvanced()) {
                descriptor.setFilterFlags(new String[] {IPropertySheetEntry.FILTER_ID_EXPERT});
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
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Object getPropertyValue( Object id ) {
        String propName = (String)id;
        String result = this.translator.getPropertyValue(propName);

        // property not found or value is null
        if (result == null) return null;

        // return empty string
        if (result.length() == 0) return result;

        PropertyDefinition propDefn = this.translator.getPropertyDefinition(propName);

        // if masked property don't return actual result
        if (propDefn.isMasked()) {
            StringBuilder sb = new StringBuilder(10);

            for (int i = 0; i < 10; ++i) {
                sb.append(WidgetFactory.PASSWORD_ECHO_CHAR);
            }

            return sb.toString();
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean isPropertySet( Object id ) {
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    @Override
	public void resetPropertyValue( Object id ) {
        PropertyDefinition propDefn = ((PropertyDefinition)id);
        setPropertyValue(id, this.initialValues.getProperty(propDefn.getName()));
    }

    public void setEditable( boolean isEditable ) {
        this.isEditable = isEditable;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    @Override
	public void setPropertyValue( Object id,
                                  Object value ) {
        String propName = (String)id;

        try {
            String oldValue = this.translator.getPropertyValue(propName);
            String newValue = value.toString();
            this.translator.setPropertyValue(propName, newValue);
            this.provider.propertyChanged(new PropertyChangeEvent(translator, propName, oldValue, newValue));
        } catch (Exception e) {
            UTIL.log(e);
        }
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
     * Sorts property descriptors using there display names but making sure the URL, user, and password (in that order) are always
     * the first 3 properties.
     * 
     * @since 5.5
     */
    class DescriptorSorter implements Comparator {
        @Override
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

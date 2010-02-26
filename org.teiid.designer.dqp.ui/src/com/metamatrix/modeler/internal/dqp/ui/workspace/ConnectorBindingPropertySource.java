/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5
 */
public class ConnectorBindingPropertySource implements IPropertySource {

    private Connector binding;
    private ConnectorType type;

    private boolean isEditable = false;
    private ConnectorBindingPropertySourceProvider provider;
    private final ExecutionAdmin admin;

    /**
     * @since 4.2
     */
    public ConnectorBindingPropertySource( Connector connector,
                                           ExecutionAdmin admin ) {
        this.binding = connector;
        this.admin = admin;

        if (connector != null) {
            this.type = this.admin.getConnectorType(connector);

            // we should always find a type
            if (this.type == null) {
                DqpUiConstants.UTIL.log(IStatus.ERROR,
                                        DqpUiConstants.UTIL.getString("ConnectorBindingsPropertySource.bindingTypeNotFound", //$NON-NLS-1$
                                                                      this.binding.getName()));
            }
        }
    }

    public void setProvider( ConnectorBindingPropertySourceProvider provider ) {
        this.provider = provider;
    }

    public void setEditable( boolean isEditable ) {
        this.isEditable = isEditable;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     * @since 4.2
     */
    public Object getEditableValue() {
        return this.binding;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     * @since 4.2
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] result = new IPropertyDescriptor[0];

        // don't return any descriptors if no binding or no type. this prevents the
        // set and get property methods from being called.
        if ((this.binding != null) && (this.type != null)) {
            Collection<PropertyDefinition> typeDefs;
            try {
                typeDefs = this.type.getPropertyDefinitions();
            } catch (Exception e) {
                // TODO log
                return new IPropertyDescriptor[0];
            }

            if ((typeDefs != null) && !typeDefs.isEmpty()) {
                boolean showExpertProps = this.provider.isShowingExpertProperties();
                Collection temp = new ArrayList(typeDefs.size());

                for (PropertyDefinition propDefn : typeDefs) {
                    String id = propDefn.getName();
                    String displayName = propDefn.getDisplayName();

                    // don't add if an expert or readonly property and expert properties are not being shown
                    if ((propDefn.isAdvanced() || !propDefn.isModifiable()) && !showExpertProps) {
                        continue;
                    }

                    // don't add hidden definitions or expert props if not showing
                    Object descriptor = null;

                    // make sure readonly definitions are not modifiable
                    if (this.isEditable && propDefn.isModifiable()) {
                        descriptor = new TextPropertyDescriptor(id, displayName);
                    } else {
                        descriptor = new PropertyDescriptor(id, displayName);
                    }

                    temp.add(descriptor);
                }

                if (!temp.isEmpty()) {
                    temp.toArray(result = new IPropertyDescriptor[temp.size()]);
                }
            }

            result = sortPropertyDescriptors(result);
        }

        return result;
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
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public Object getPropertyValue( Object id ) {
        String result = binding.getPropertyValue((String)id);

        if (result == null) {
            result = StringUtil.Constants.EMPTY_STRING;
        }

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
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     * @since 4.2
     */
    public void resetPropertyValue( Object id ) {
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
        try {
            ModelerDqpUtils.setPropertyValue(this.binding, id, value);
            this.provider.propertyChanged(this.binding);
        } catch (final Exception error) {
            UiUtil.runInSwtThread(new Runnable() {

                public void run() {
                    DqpUiPlugin.showErrorDialog(Display.getCurrent().getActiveShell(), error);
                }
            }, false);
        }
    }

    public Connector getConnector() {
        return this.binding;
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

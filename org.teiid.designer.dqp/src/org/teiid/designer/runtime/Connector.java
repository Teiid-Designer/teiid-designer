/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import org.teiid.adminapi.ConnectionFactory;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.HashCodeUtil;

/**
 */
public class Connector implements Comparable<Connector> {

    private final ConnectionFactory binding;
    private final ConnectorType type;

    Connector( ConnectionFactory binding,
               ConnectorType type ) {
        CoreArgCheck.isNotNull(binding, "binding"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$

        this.binding = binding;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( Connector connector ) {
        CoreArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        return getName().compareTo(connector.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        Connector other = (Connector)obj;
        Server otherServer = other.getType().getAdmin().getServer();

        if (getName().equals(other.getName()) && getType().getAdmin().getServer().equals(otherServer)) return true;

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getName()
     */
    public String getName() {
        return binding.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getProperties()
     */
    public Properties getProperties() {
        return binding.getProperties();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getPropertyValue(java.lang.String)
     */
    public String getPropertyValue( String name ) {
        return binding.getPropertyValue(name);
    }

    /**
     * @return type
     */
    public ConnectorType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, getName());
        return HashCodeUtil.hashCode(result, getType().getAdmin().getServer().hashCode());
    }

    /**
     * @param name the property name
     * @param value the proposed new value
     * @return <code>true</code> if the property exists and the proposed value is valid
     * @since 7.0
     */
    public boolean isValidPropertyValue( String name,
                                         String value ) {
        if (value == null) {
            return false;
        }
        PropertyDefinition definition = this.type.getPropertyDefinition(name);
        if (definition == null) return false;

        String type = definition.getPropertyTypeClassName();

        // Note: "String" does not need any validation here

        if (Boolean.class.getName().equals(type)) {

            if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return false;
            }
        } else if (Character.class.getName().equals(type)) {
            if (value.length() != 1) {
                return false;
            }
        } else if (Byte.class.getName().equals(type)) {
            try {
                Byte.parseByte(value);
            } catch (Exception e) {
                return false;
            }
        } else if (Short.class.getName().equals(type)) {
            try {
                Short.parseShort(value);
            } catch (Exception e) {
                return false;
            }
        } else if (Integer.class.getName().equals(type)) {
            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                return false;
            }
        } else if (Long.class.getName().equals(type)) {
            try {
                Long.parseLong(value);
            } catch (Exception e) {
                return false;
            }
        } else if (Float.class.getName().equals(type)) {
            try {
                Float.parseFloat(value);
            } catch (Exception e) {
                return false;
            }
        } else if (Double.class.getName().equals(type)) {
            try {
                Double.parseDouble(value);
            } catch (Exception e) {
                return false;
            }
        }

        // should only get here if valid so far
        if (definition.isConstrainedToAllowedValues()) {
            Collection values = definition.getAllowedValues();
            assert ((values != null) && !values.isEmpty()); // TODO is this a valid assert??

            for (Object allowedValue : values) {
                if (allowedValue.equals(value)) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    /**
     * Sets a connector property.
     * 
     * @param name the property name (never <code>null</code>)
     * @param value the new property value
     * @throws Exception if there is a problem changing the property
     * @since 5.0
     */
    public void setPropertyValue( String name,
                                  String value ) throws Exception {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        this.type.getAdmin().setPropertyValue(this, name, value);
        getProperties().setProperty(name, value); // TODO does the admin call do this
    }

    /**
     * @param changedProperties the list of properties that are being changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( Properties changedProperties ) throws Exception {
        CoreArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        Set<Entry<Object, Object>> entrySet = changedProperties.entrySet();
        CoreArgCheck.isNotEmpty(entrySet, "changedProperties"); //$NON-NLS-1$
        this.type.getAdmin().setProperties(this, changedProperties);

        // TODO does the admin call do this
        Properties props = getProperties();

        for (Entry<Object, Object> entry : entrySet) {
            // TODO: MAY NOT WORK DUE TO ADMIN API's APPARENT READ-ONLY NATURE??
            props.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Util.getString("connectorDetailedName", getName(), getType().getName(), getType().getAdmin().getServer()); //$NON-NLS-1$
    }

}

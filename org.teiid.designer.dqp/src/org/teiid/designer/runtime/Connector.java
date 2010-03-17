/*
 * JBoss DNA (http://www.jboss.org/dna)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * JBoss DNA is free software. Unless otherwise indicated, all code in JBoss DNA
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * JBoss DNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.teiid.designer.runtime;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 */
public final class Connector {

    private final ConnectorBinding binding;
    private final ConnectorType type;

    Connector( ConnectorBinding binding,
               ConnectorType type ) {
        assert (binding != null);
        assert (type != null);

        this.binding = binding;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        // TODO overwrite to compare name and server
        return super.equals(obj);
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
        // TODO overwrite to look at name and server
        return super.hashCode();
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
        ArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        this.type.getAdmin().setPropertyValue(this, name, value);
        getProperties().setProperty(name, value); // TODO does the admin call do this
    }

    /**
     * @param changedProperties the list of properties that are being changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( Properties changedProperties ) throws Exception {
        ArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        Set<Entry<Object, Object>> entrySet = changedProperties.entrySet();
        ArgCheck.isNotEmpty(entrySet, "changedProperties"); //$NON-NLS-1$
        this.type.getAdmin().setProperties(this, changedProperties);

        // TODO does the admin call do this
        Properties props = getProperties();

        for (Entry<Object, Object> entry : entrySet) {
            // TODO: MAY NOT WORK DUE TO ADMIN API's APPARENT READ-ONLY NATURE??
            props.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }

}

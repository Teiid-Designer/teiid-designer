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
import org.eclipse.core.runtime.IStatus;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.dqp.DqpPlugin;

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
     * Sets a connector property.
     * 
     * @param name the property name
     * @param value the new property value
     * @throws Exception if the property was not set
     * @since 5.0
     */
    public void setPropertyValue( String name,
                                  String value ) throws Exception {
        ArgCheck.isNotNull(name, "name"); //$NON-NLS-1$

        PropertyDefinition propDef = type.getPropertyDefinition(name);
        if (propDef == null) {
            throw new Exception(DqpPlugin.Util.getString("Connector.unableToFindTypeDefinition", name)); //$NON-NLS-1$
        }

        if (isValid(propDef, value)) {
            if (propDef.getDefaultValue() == null || !propDef.getDefaultValue().equals(value)) {
                this.type.getAdmin().setPropertyValue(this, name, value);
            }
        } else {
            throw new Exception(DqpPlugin.Util.getString("Connector.invalidPropertyValue", value, name)); //$NON-NLS-1$
        }
    }

    private boolean isValidValue( PropertyDefinition definition,
                                  String value ) {
        String type = definition.getPropertyTypeClassName();

        if ("String".equals(type)) { //$NON-NLS-1$
            return true;
        }

        if ("Boolean".equals(type)) { //$NON-NLS-1$
            try {
                Boolean.parseBoolean(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Character".equals(type)) { //$NON-NLS-1$
            return ((value == null) || (value.length() <= 1));
        }

        if ("Byte".equals(type)) { //$NON-NLS-1$
            try {
                Byte.parseByte(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Short".equals(type)) { //$NON-NLS-1$
            try {
                Short.parseShort(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Int".equals(type)) { //$NON-NLS-1$
            try {
                Integer.parseInt(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Long".equals(type)) { //$NON-NLS-1$
            try {
                Long.parseLong(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Float".equals(type)) { //$NON-NLS-1$
            try {
                Float.parseFloat(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        if ("Double".equals(type)) { //$NON-NLS-1$
            try {
                Double.parseDouble(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        // should never get here
        assert (false);
        return false;
    }

    private boolean isValid( PropertyDefinition definition,
                             String value ) {
        if (isValidValue(definition, value) && definition.isConstrainedToAllowedValues()) {
            Collection values = definition.getAllowedValues();

            if (values != null && !values.isEmpty()) {
                for (Object allowedValue : values) {
                    if (allowedValue.equals(value)) {
                        return true;
                    }
                }

                // value is not allowable
                return false;
            }

            // if there are no allowed values, this is an illegal state for the property defn
            // but we need to allow the user to continue - just log it.
            Object[] msgArray = new Object[] {definition.getName(), type.getName()};
            DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("Connector.noAllowedValuesWarning", msgArray)); // TODO i18n this
        }

        return true;
    }
}

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
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.common.object.PropertyType;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * @author Dan Florian
 */
public final class Connector {

    private final ConnectorBinding binding;
    private final ConnectorType type;

    Connector( ConnectorBinding binding,
               ConnectorType type ) {
        this.binding = binding;
        this.type = type;
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
    public String getPropertyValue( String arg0 ) {
        return binding.getPropertyValue(arg0);
    }

    /**
     * @return type
     */
    public ConnectorType getType() {
        return type;
    }

    /**
     * Sets a connector property.
     * 
     * @param name the property name
     * @param value the new property value
     * @return a list of errors thrown by property/configuration listeners after the property was successfully set
     * @throws Exception if the property was not set
     * @since 5.0
     */
    public Exception[] setPropertyValue( String name,
                                         String value ) throws Exception {
        ArgCheck.isNotNull(name, "name"); //$NON-NLS-1$

        PropertyDefinition propDef = type.getPropertyDefinition(name);
        if (propDef == null) {
            throw new Exception(DqpPlugin.Util.getString("Connector.unableToFindTypeDefinition", name)); //$NON-NLS-1$
        }

        boolean valid = isValid(propDef, value);
        if (valid) {
            if (propDef.getDefaultValue() == null || !propDef.getDefaultValue().equals(value)) {
                return binding.setConnectorPropertyValue(theBinding, name, value);
            }
        }
        throw new Exception(DqpPlugin.Util.getString("Connector.invalidPropertyValue", value, name)); //$NON-NLS-1$
    }
    
    private boolean isValid(PropertyDefinition definition, String value) {
        // TODO: What is this?
        String propType = definition.getPropertyTypeClassName();
        boolean result = propType.isValidValue(value);

        if (definition.isConstrainedToAllowedValues()) {
            Collection values = definition.getAllowedValues();
            if (values != null && !values.isEmpty()) {
                result = false;
                for (int size = values.size(), i = 0; i < size; ++i) {
                    if (value.equals(values.get(i))) {
                        result = true;
                        break;
                    }
                }
            } else {
                // if there are no allowed values, this is an illegal state for the property defn
                // but we need to allow the user to continue - just log it.
                Object[] msgArray = new Object[] {definition.getName(), type.getName()};
                DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("Connector.noAllowedValuesWarning", msgArray)); //$NON-NLS-1$
            }
        }
        return true;
    }
}

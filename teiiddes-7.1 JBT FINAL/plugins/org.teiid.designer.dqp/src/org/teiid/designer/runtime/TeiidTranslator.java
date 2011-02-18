/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;

import com.metamatrix.core.util.CoreArgCheck;
import org.teiid.core.util.HashCodeUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 */
public class TeiidTranslator implements Comparable<TeiidTranslator> {

    private final Translator translator;
    private final ExecutionAdmin admin;

    private final Collection<PropertyDefinition> propDefs;

    TeiidTranslator( Translator translator, Collection<PropertyDefinition> propDefs, ExecutionAdmin admin) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propDefs, "propDefs"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.translator = translator;
        this.admin = admin;
        this.propDefs = propDefs;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TeiidTranslator translator ) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        return getName().compareTo(translator.getName());
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

        TeiidTranslator other = (TeiidTranslator)obj;
        Server otherServer = other.getAdmin().getServer();

        if (getName().equals(other.getName()) && (getAdmin().getServer() == otherServer || getAdmin().getServer().equals(otherServer)) ) return true;

        return false;
    }

    /**
     * Obtains all the names of the properties whose values are invalid.
     * 
     * @return the names of the properties with invalid values (never <code>null</code> but can be empty)
     */
    public Collection<String> findInvalidProperties() {
        Collection<String> propertyNames = new ArrayList<String>();

        for (PropertyDefinition propDefn : this.propDefs) {
            String name = propDefn.getName();
            String value = getPropertyValue(name);

            if (StringUtilities.isEmpty(value)) {
                if (propDefn.isRequired()) {
                    propertyNames.add(name);
                }
            } else if (isValidPropertyValue(name, value) != null) {
                propertyNames.add(name);
            }
        }

        return propertyNames;
    }

    protected Translator getTranslator() {
        return this.translator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getName()
     */
    public String getName() {
        return translator.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getProperties()
     */
    public Properties getProperties() {
        return translator.getProperties();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getPropertyValue(java.lang.String)
     */
    public String getPropertyValue( String name ) {
        return translator.getPropertyValue(name);
    }

    /**
     * @return type
     */
    public String getType() {
        return this.translator.getType();
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
        return HashCodeUtil.hashCode(result, getAdmin().getServer().hashCode());
    }

    /**
     * @return the execution admin (never <code>null</code>)
     */
    public ExecutionAdmin getAdmin() {
        return this.admin;
    }
    
    /**
     * @param name the name of the <code>PropertyDefinition</code> being requested (never <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    public PropertyDefinition getPropertyDefinition( String name ) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$

        for (PropertyDefinition propDef : getPropertyDefinitions()) {
            if (name.equals(propDef.getName())) return propDef;
        }

        return null;
    }
    
    /**
     * @return the string version of the default value for each property (empty string if no default)
     */
    public Properties getDefaultPropertyValues() {
        Properties defaultValues = new Properties();

        for (PropertyDefinition propDef : getPropertyDefinitions()) {
            String value = (propDef.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                              : propDef.getDefaultValue().toString();
            defaultValues.setProperty(propDef.getName(), value);
        }

        return defaultValues;
    }
    
    /**
     * @param name the property name
     * @param value the proposed new value
     * @return null if the property exists and the proposed value is valid or an error message
     * @since 7.0
     */
    public String isValidPropertyValue( String name,
                                        String value ) {
        // make sure there is a property definition
        PropertyDefinition definition = this.getPropertyDefinition(name);
        if (definition == null) return Util.getString("missingPropertyDefinition", name); //$NON-NLS-1$

        // make sure there is a value
        if (value == null) {
            return Util.getString("invalidNullPropertyValue", name); //$NON-NLS-1$
        }

        String type = definition.getPropertyTypeClassName();

        // Note: "String" does not need any validation here

        if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
            if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return Util.getString("invalidPropertyEditorValue", value, Boolean.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Character.class.getName().equals(type) || Character.TYPE.getName().equals(type)) {
            if (value.length() != 1) {
                return Util.getString("invalidPropertyEditorValue", value, Character.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Byte.class.getName().equals(type) || Byte.TYPE.getName().equals(type)) {
            try {
                Byte.parseByte(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Byte.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Short.class.getName().equals(type) || Short.TYPE.getName().equals(type)) {
            try {
                Short.parseShort(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Short.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Integer.class.getName().equals(type) || Integer.TYPE.getName().equals(type)) {
            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Integer.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Long.class.getName().equals(type) || Long.TYPE.getName().equals(type)) {
            try {
                Long.parseLong(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Long.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Float.class.getName().equals(type) || Float.TYPE.getName().equals(type)) {
            try {
                Float.parseFloat(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Float.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Double.class.getName().equals(type) || Double.TYPE.getName().equals(type)) {
            try {
                Double.parseDouble(value);
            } catch (Exception e) {
                return Util.getString("invalidPropertyEditorValue", value, Double.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (!String.class.getName().equals(type)){
            return Util.getString("unknownPropertyType", name, type); //$NON-NLS-1$
        }

        // should only get here if valid so far
        if (definition.isConstrainedToAllowedValues()) {
            Collection values = definition.getAllowedValues();
            assert ((values != null) && !values.isEmpty());

            for (Object allowedValue : values) {
                if (allowedValue.equals(value)) {
                    return null;
                }
            }

            return Util.getString("invalidPropertyEditorConstrainedValue", value, values.toString()); //$NON-NLS-1$;
        }

        // valid value
        return null;
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

        // TODO does the admin call do this
        Properties props = getProperties();

        for (Entry<Object, Object> entry : entrySet) {
            // TODO: MAY NOT WORK DUE TO ADMIN API's APPARENT READ-ONLY NATURE??
            props.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }
    
    /**
     * @return an immutable collection of property definitions (never <code>null</code>);
     * @since 7.0
     */
    public Collection<PropertyDefinition> getPropertyDefinitions() {
        return this.propDefs;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Util.getString("connectorDetailedName", getName(), getType(), getAdmin().getServer()); //$NON-NLS-1$
    }

}

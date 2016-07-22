/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.teiid.adminapi.AdminObject;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.runtime.client.Messages;

/**
 *
 * @since 8.0
 */
public class TeiidTranslator implements Comparable<TeiidTranslator>, ITeiidTranslator {

    private final Translator translator;
    private final ITeiidServer teiidServer;

    private final Collection<TeiidPropertyDefinition> propDefs = new ArrayList<TeiidPropertyDefinition>();
    private final Collection<TeiidPropertyDefinition> importPropDefs = new ArrayList<TeiidPropertyDefinition>();
    private final Collection<TeiidPropertyDefinition> extPropDefs = new ArrayList<TeiidPropertyDefinition>();

    /**
     * @param translator
     * @param propDefs
     * @param teiidServer
     */
    public TeiidTranslator( Translator translator, Collection<? extends PropertyDefinition> propDefs, ITeiidServer teiidServer) {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(propDefs, "propDefs"); //$NON-NLS-1$
        ArgCheck.isNotNull(teiidServer, "teiidServer"); //$NON-NLS-1$

        this.translator = translator;
        this.teiidServer = teiidServer;
        
        for (PropertyDefinition propDefn : propDefs) {
            TeiidPropertyDefinition teiidPropertyDefn = new TeiidPropertyDefinition();
            
            teiidPropertyDefn.setName(propDefn.getName());
            teiidPropertyDefn.setDisplayName(propDefn.getDisplayName());
            teiidPropertyDefn.setDescription(propDefn.getDescription());
            teiidPropertyDefn.setPropertyTypeClassName(propDefn.getPropertyTypeClassName());
            teiidPropertyDefn.setDefaultValue(propDefn.getDefaultValue());
            teiidPropertyDefn.setAllowedValues(propDefn.getAllowedValues());
            teiidPropertyDefn.setModifiable(propDefn.isModifiable());
            teiidPropertyDefn.setConstrainedToAllowedValues(propDefn.isConstrainedToAllowedValues());
            teiidPropertyDefn.setAdvanced(propDefn.isAdvanced());
            teiidPropertyDefn.setRequired(propDefn.isRequired());
            teiidPropertyDefn.setMasked(propDefn.isMasked());
            
            this.propDefs.add(teiidPropertyDefn);
        }
    }
    
    /**
     * @param translator
     * @param propDefs
     * @param importPropDefs
     * @param extPropDefs
     * @param teiidServer
     */
    public TeiidTranslator( Translator translator, 
    						Collection<? extends PropertyDefinition> propDefs, 
    						Collection<? extends PropertyDefinition> importPropDefs,
    						Collection<? extends PropertyDefinition> extPropDefs,
    						ITeiidServer teiidServer) {
    	this(translator, propDefs, teiidServer);
    	
        for (PropertyDefinition propDefn : importPropDefs) {
            TeiidPropertyDefinition teiidPropertyDefn = new TeiidPropertyDefinition();
            
            teiidPropertyDefn.setName(propDefn.getName());
            teiidPropertyDefn.setDisplayName(propDefn.getDisplayName());
            teiidPropertyDefn.setDescription(propDefn.getDescription());
            teiidPropertyDefn.setPropertyTypeClassName(propDefn.getPropertyTypeClassName());
            teiidPropertyDefn.setDefaultValue(propDefn.getDefaultValue());
            teiidPropertyDefn.setAllowedValues(propDefn.getAllowedValues());
            teiidPropertyDefn.setModifiable(propDefn.isModifiable());
            teiidPropertyDefn.setConstrainedToAllowedValues(propDefn.isConstrainedToAllowedValues());
            teiidPropertyDefn.setAdvanced(propDefn.isAdvanced());
            teiidPropertyDefn.setRequired(propDefn.isRequired());
            teiidPropertyDefn.setMasked(propDefn.isMasked());
            
            this.importPropDefs.add(teiidPropertyDefn);
        }
        
        for (PropertyDefinition propDefn : extPropDefs) {
            TeiidPropertyDefinition teiidPropertyDefn = new TeiidPropertyDefinition();
            
            teiidPropertyDefn.setName(propDefn.getName());
            teiidPropertyDefn.setDisplayName(propDefn.getDisplayName());
            teiidPropertyDefn.setDescription(propDefn.getDescription());
            teiidPropertyDefn.setPropertyTypeClassName(propDefn.getPropertyTypeClassName());
            teiidPropertyDefn.setDefaultValue(propDefn.getDefaultValue());
            teiidPropertyDefn.setAllowedValues(propDefn.getAllowedValues());
            teiidPropertyDefn.setModifiable(propDefn.isModifiable());
            teiidPropertyDefn.setConstrainedToAllowedValues(propDefn.isConstrainedToAllowedValues());
            teiidPropertyDefn.setAdvanced(propDefn.isAdvanced());
            teiidPropertyDefn.setRequired(propDefn.isRequired());
            teiidPropertyDefn.setMasked(propDefn.isMasked());
            teiidPropertyDefn.setOwner(propDefn.getPropertyValue("owner")); //$NON-NLS-1$
            this.extPropDefs.add(teiidPropertyDefn);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( TeiidTranslator translator ) {
        ArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
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

        ITeiidTranslator other = (ITeiidTranslator)obj;
        ITeiidServer otherServer = other.getTeiidServer();

        if (getName().equals(other.getName()) && (getTeiidServer() == otherServer || getTeiidServer().equals(otherServer)) ) return true;

        return false;
    }

    /**
     * Obtains all the names of the properties whose values are invalid.
     * 
     * @return the names of the properties with invalid values (never <code>null</code> but can be empty)
     */
    @Override
    public Collection<String> findInvalidProperties(TranslatorPropertyType propType) {
        Collection<String> propertyNames = new ArrayList<String>();

        for (TeiidPropertyDefinition propDefn : this.propDefs) {
            String name = propDefn.getName();
            String value = getPropertyValue(name, propType);

            if (value == null  ||  value.length() == 0) {
                if (propDefn.isRequired()) {
                    propertyNames.add(name);
                }
            } else if (isValidPropertyValue(name, value, propType) != null) {
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
     * @see AdminObject#getName()
     */
    @Override
    public String getName() {
        return translator.getName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AdminObject#getProperties()
     */
    @Override
    public Properties getProperties() {
        return translator.getProperties();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AdminObject#getPropertyValue(java.lang.String)
     */
    @Override
    public String getPropertyValue( String name, TranslatorPropertyType type) {
        return translator.getPropertyValue(name);
    }

    /**
     * @return type
     */
    @Override
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
        final int prime = 31;
        int result = 0;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result + ((this.getTeiidServer() == null) ? 0 : this.getTeiidServer().hashCode());
        return result;
    }

    /**
     * @return the execution teiidServer (never <code>null</code>)
     */
    @Override
    public ITeiidServer getTeiidServer() {
        return this.teiidServer;
    }
    
    @Override
    public TeiidPropertyDefinition getPropertyDefinition( String name, TranslatorPropertyType type) {
        ArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        if( type == TranslatorPropertyType.OVERRIDE ) {
	        for (TeiidPropertyDefinition propDef : getPropertyDefinitions()) {
	            if (name.equals(propDef.getName())) return propDef;
	        }
        } else if( type == TranslatorPropertyType.IMPORT ) {
	        for (TeiidPropertyDefinition propDef : getImportPropertyDefinitions()) {
	            if (name.equals(propDef.getName())) return propDef;
	        }
        } else if( type == TranslatorPropertyType.EXTENSION_METADATA ) {
	        for (TeiidPropertyDefinition propDef : getExtensionPropertyDefinitions()) {
	            if (name.equals(propDef.getName())) return propDef;
	        }
        }

        return null;
    }
    
    /**
     * @return the string version of the default value for each property (empty string if no default)
     */
    @Override
    public Properties getDefaultPropertyValues() {
        Properties defaultValues = new Properties();

        for (TeiidPropertyDefinition propDef : getPropertyDefinitions()) {
            String value = (propDef.getDefaultValue() == null) ? StringUtil.Constants.EMPTY_STRING
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
    @Override
    public String isValidPropertyValue( String name,
                                        String value,
                                        TranslatorPropertyType propType) {
        // make sure there is a property definition
        TeiidPropertyDefinition definition = this.getPropertyDefinition(name, propType);
        if (definition == null) return Messages.getString(Messages.ExecutionAdmin.missingPropertyDefinition, name);

        // make sure there is a value
        if (value == null) {
            return Messages.getString(Messages.ExecutionAdmin.invalidNullPropertyValue, name);
        }

        String type = definition.getPropertyTypeClassName();

        // Note: "String" does not need any validation here

        if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
            if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Boolean.TYPE.getName());
            }
        } else if (Character.class.getName().equals(type) || Character.TYPE.getName().equals(type)) {
            if (value.length() != 1) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Character.TYPE.getName());
            }
        } else if (Byte.class.getName().equals(type) || Byte.TYPE.getName().equals(type)) {
            try {
                Byte.parseByte(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Byte.TYPE.getName());
            }
        } else if (Short.class.getName().equals(type) || Short.TYPE.getName().equals(type)) {
            try {
                Short.parseShort(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Short.TYPE.getName());
            }
        } else if (Integer.class.getName().equals(type) || Integer.TYPE.getName().equals(type)) {
            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Integer.TYPE.getName());
            }
        } else if (Long.class.getName().equals(type) || Long.TYPE.getName().equals(type)) {
            try {
                Long.parseLong(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Long.TYPE.getName());
            }
        } else if (Float.class.getName().equals(type) || Float.TYPE.getName().equals(type)) {
            try {
                Float.parseFloat(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Float.TYPE.getName());
            }
        } else if (Double.class.getName().equals(type) || Double.TYPE.getName().equals(type)) {
            try {
                Double.parseDouble(value);
            } catch (Exception e) {
                return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorValue, value, Double.TYPE.getName());
            }
        } else if (!String.class.getName().equals(type)){
            return Messages.getString(Messages.ExecutionAdmin.unknownPropertyType, name, type);
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

            return Messages.getString(Messages.ExecutionAdmin.invalidPropertyEditorConstrainedValue, value, values.toString());
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
    @Override
    public void setPropertyValue( String name,
                                  String value,
                                  TranslatorPropertyType type) throws Exception {
        ArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        getProperties().setProperty(name, value); // TODO does the teiidServer call do this
    }

    /**
     * @param changedProperties the list of properties that are being changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    @Override
    public void setProperties( Properties changedProperties ) throws Exception {
        ArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        Set<Entry<Object, Object>> entrySet = changedProperties.entrySet();
        ArgCheck.isNotEmpty(entrySet, "changedProperties"); //$NON-NLS-1$

        // TODO does the teiidServer call do this
        Properties props = getProperties();

        for (Entry<Object, Object> entry : entrySet) {
            // TODO: MAY NOT WORK DUE TO ADMIN API's APPARENT READ-ONLY NATURE??
            props.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }
    
    @Override
    public Collection<TeiidPropertyDefinition> getPropertyDefinitions() {
        return this.propDefs;
    }
    
    @Override
	public Collection<TeiidPropertyDefinition> getImportPropertyDefinitions() {
		return this.importPropDefs;
	}

	@Override
	public Collection<TeiidPropertyDefinition> getExtensionPropertyDefinitions() {
		return this.extPropDefs;
	}

	/**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Messages.getString(Messages.ExecutionAdmin.connectorDetailedName, getName(), getType());
    }

}
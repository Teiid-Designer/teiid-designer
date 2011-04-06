/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.cnd;

import org.eclipse.osgi.util.NLS;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.Messages;

/**
 * {@link PropertyDefinition} structural object designed to hold CND or Compact Node Definition property values as well
 * as Model Extension Property values including "display name", masked, description, etc... 
 * 
 */
public class CndPropertyDefinition implements PropertyDefinition {
	
	public static final String[] TYPE_STRINGS = 
	   {"ZERO_PLACE_HODER", //$NON-NLS-1$
		"STRING",  //$NON-NLS-1$
		"BINARY", //$NON-NLS-1$
		"LONG", //$NON-NLS-1$
		"DOUBLE", //$NON-NLS-1$
		"DATE", //$NON-NLS-1$
		"BOOLEAN", //$NON-NLS-1$
		"NAME", //$NON-NLS-1$
		"PATH", //$NON-NLS-1$
		"REFERENCE", //$NON-NLS-1$
		"WEAKREFERENCE", //$NON-NLS-1$
		"URI", //$NON-NLS-1$
		"DECIMAL"}; //$NON-NLS-1$

    private final String typeName;

    boolean advanced;
	String[] allowedValues;
    String defaultValue;
    String description;
    String displayName;
    String id;
    boolean masked;
    boolean modifiable;
    boolean required;
    
    // CND-related info
    String namespacePrefix;

    public CndPropertyDefinition( String typeName ) {
        this.typeName = typeName;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        return this.id.equals(((CndPropertyDefinition)obj).id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getAllowedValues()
     */
    @Override
    public String[] getAllowedValues() {
        return this.allowedValues;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isAdvanced()
     */
    @Override
    public boolean isAdvanced() {
        return this.advanced;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isMasked()
     */
    @Override
    public boolean isMasked() {
        return this.masked;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isModifiable()
     */
    @Override
    public boolean isModifiable() {
        return this.modifiable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isRequired()
     */
    @Override
    public boolean isRequired() {
        return this.required;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isValidValue(java.lang.String)
     */
    @Override
    public String isValidValue( String newValue ) {
    	/* 
    	 * Possible Types from the CND attributes
    	 * 
    	 property_type ::=  "STRING" | "String" |"string" |
					        "BINARY" | "Binary" | "binary" |CndPropertyDefinition
					        "LONG" | "Long" | "long" |
					        "DOUBLE" | "Double" | "double" |
					        "BOOLEAN" | "Boolean" | "boolean" |
					        "DATE" | "Date" | "date" |
					        "NAME | "Name | "name" |
					        "PATH" | "Path" | "path" |
					        "REFERENCE" | "Reference" |
					           "reference" |
					        "UNDEFINED" | "Undefined" |
					           "undefined" | "*"
        */
    	
        // if empty must have a value or a default value if required
        if (isEmpty(newValue)) {
            // invalid if required and no default value
            if (isRequired() && isEmpty(getDefaultValue())) {
                return NLS.bind(Messages.InvalidNullPropertyValue, getDisplayName());
            }

            // OK to be null/empty
            return null;
        }

        if (Boolean.class.getName().equals(this.typeName) || Boolean.TYPE.getName().equals(this.typeName)) {
            if (!newValue.equalsIgnoreCase(Boolean.TRUE.toString()) && !newValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return NLS.bind(Messages.InvalidPropertyEditorValue, newValue, Boolean.TYPE.getName());
            }
        } else if (Long.class.getName().equals(this.typeName) || Long.TYPE.getName().equals(this.typeName)) {
            try {
                Long.parseLong(newValue);
            } catch (Exception e) {
                return NLS.bind(Messages.InvalidPropertyEditorValue, newValue, Long.TYPE.getName());
            }
        } else if (Double.class.getName().equals(this.typeName) || Double.TYPE.getName().equals(this.typeName)) {
            try {
                Double.parseDouble(newValue);
            } catch (Exception e) {
                return NLS.bind(Messages.InvalidPropertyEditorValue, newValue, Double.TYPE.getName());
            }
        } else if (!String.class.getName().equals(this.typeName)) {
            return NLS.bind(Messages.UnknownPropertyType, this.displayName, this.typeName);
        }

        // valid value
        return null;
    }
    
    /**
     * Simple getter for the value of the Namespace prefix
     * 
     * @return the namespace prefix
     */
    public String getNamespacePrefix() {
    	return this.namespacePrefix;
    }
    
    /**
     * Simple getter for the value of the CND key
     * 
     * @return the CND key string value
     */
    public String getCndKey() {
    	return this.namespacePrefix + ':' + this.id;
    }
    
    /**
     * Simple getter for the value of the property definition type
     * 
     * @return the type name
     */
    public String getTypeName() {
		return this.typeName;
	}

    /**
     * Simple setter for the advanced property value
     * 
     * @param the advanced property value
     */
	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}

	/**
     * Simple setter for the advanced property value
     * 
     * @param the advanced property value
     */
	public void setAllowedValues(String[] allowedValues) {
		this.allowedValues = allowedValues;
	}

	/**
     * Simple setter for the defaultValue property value
     * 
     * @param the defaultValue property value
     */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
     * Simple setter for the description property value
     * 
     * @param the description property value
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * Simple setter for the displayName property value
     * 
     * @param the displayName property value
     */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
     * Simple setter for the id property value
     * 
     * @param the id property value
     */
	public void setId(String id) {
		this.id = id;
	}

	/**
     * Simple setter for the masked property value
     * 
     * @param the masked property value
     */
	public void setMasked(boolean masked) {
		this.masked = masked;
	}

	/**
     * Simple setter for the modifiable property value
     * 
     * @param the modifiable property value
     */
	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}

	/**
     * Simple setter for the required property value
     * 
     * @param the required property value
     */
	public void setRequired(boolean required) {
		this.required = required;
	}
	
    /**
     * Indicates if the specified text is either empty or <code>null</code>.
     * 
     * @param text the text being checked (may be <code>null</code>)
     * @return <code>true</code> if the specified text is either empty or <code>null</code>
     */
    public static boolean isEmpty( final String text ) {
        return ((text == null) || (text.trim().length() == 0));
    }
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName());
		sb.append("\n  Name\t\t\t = ").append(displayName) //$NON-NLS-1$
		.append("\n  id\t\t\t = ").append(id) //$NON-NLS-1$
		.append("\n  description\t = ").append(description) //$NON-NLS-1$
		.append("\n  required\t\t = ").append(required) //$NON-NLS-1$
		.append("\n  type\t\t\t = ").append(typeName) //$NON-NLS-1$
		.append("\n  modifiable\t = ").append(modifiable) //$NON-NLS-1$
		.append("\n  default value\t = ").append(defaultValue); //$NON-NLS-1$
		if( allowedValues != null ) {
			boolean first = true;
			for( String str : allowedValues ) {
				if( first ) {
					sb.append("\n  allowed values = "); //$NON-NLS-1$
				}
				if( !first ) sb.append(", "); //$NON-NLS-1$
				if( first ) first = false;
				sb.append(str); 
			}
		}
		sb.append("\n  masked\t\t = ").append(masked); //$NON-NLS-1$
		sb.append("\n  advanced\t\t = ").append(advanced); //$NON-NLS-1$
		
		return sb.toString();
	}
}
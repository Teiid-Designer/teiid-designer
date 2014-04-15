package org.teiid.designer.core.translators;


import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;

public class TranslatorProperty implements PropertyDefinition {

    private final String className;

    boolean advanced;
    String[] allowedValues;
    String defaultValue;
    String description;
    String displayName;
    String id;
    boolean masked;
    boolean modifiable;
    boolean required;

    public TranslatorProperty( String className ) {
        this.className = className;
    }

    /**
     * {@inheritDoc}
     * 
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

        return this.id.equals(((TranslatorProperty)obj).id);
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#getAllowedValues()
     */
    @Override
    public String[] getAllowedValues() {
        return this.allowedValues;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#getId()
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
     * PropertyDefinition#isAdvanced()
     */
    @Override
    public boolean isAdvanced() {
        return this.advanced;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#isMasked()
     */
    @Override
    public boolean isMasked() {
        return this.masked;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#isModifiable()
     */
    @Override
    public boolean isModifiable() {
        return this.modifiable;
    }

    /**
     * {@inheritDoc}
     * 
     * PropertyDefinition#isRequired()
     */
    @Override
    public boolean isRequired() {
        return this.required;
    }

    public String getClassName() {
		return className;
	}

	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}

	public void setAllowedValues(String[] allowedValues) {
		this.allowedValues = allowedValues;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMasked(boolean masked) {
		this.masked = masked;
	}

	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
     * {@inheritDoc}
     * 
     * PropertyDefinition#isValidValue(java.lang.String)
     */
    @Override
    public String isValidValue( String newValue ) {
        // if empty must have a value or a default value if required
        if (StringUtilities.isEmpty(newValue)) {
            // invalid if required and no default value
            if (isRequired() && StringUtilities.isEmpty(getDefaultValue())) {
                return ModelerCore.Util.getString("invalidNullPropertyValue", getDisplayName()); //$NON-NLS-1$
            }

            // OK to be null/empty
            return null;
        }

        if (Boolean.class.getName().equals(this.className) || Boolean.TYPE.getName().equals(this.className)) {
            if (!newValue.equalsIgnoreCase(Boolean.TRUE.toString()) && !newValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Boolean.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Character.class.getName().equals(this.className) || Character.TYPE.getName().equals(this.className)) {
            if (newValue.length() != 1) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Character.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Byte.class.getName().equals(this.className) || Byte.TYPE.getName().equals(this.className)) {
            try {
                Byte.parseByte(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Byte.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Short.class.getName().equals(this.className) || Short.TYPE.getName().equals(this.className)) {
            try {
                Short.parseShort(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Short.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Integer.class.getName().equals(this.className) || Integer.TYPE.getName().equals(this.className)) {
            try {
                Integer.parseInt(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Integer.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Long.class.getName().equals(this.className) || Long.TYPE.getName().equals(this.className)) {
            try {
                Long.parseLong(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Long.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Float.class.getName().equals(this.className) || Float.TYPE.getName().equals(this.className)) {
            try {
                Float.parseFloat(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Float.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (Double.class.getName().equals(this.className) || Double.TYPE.getName().equals(this.className)) {
            try {
                Double.parseDouble(newValue);
            } catch (Exception e) {
                return ModelerCore.Util.getString("invalidPropertyValueForType", newValue, Double.TYPE.getName()); //$NON-NLS-1$
            }
        } else if (!String.class.getName().equals(this.className)) {
            return ModelerCore.Util.getString("unknownPropertyType", this.displayName, this.className); //$NON-NLS-1$
        }

        // valid value
        return null;
    }

}
package org.teiid.designer.teiidimporter.ui.panels;

import java.io.Serializable;
import org.teiid.core.designer.util.CoreStringUtil;

/**
 * PropertyItem
 * The object for holding property info
 */
public class PropertyItem extends Object implements Serializable {

    @SuppressWarnings( "javadoc" )
    public static final String CONNECTION_URL_DISPLAYNAME = "connection-url";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String USERNAME_PROP_DISPLAYNAME = "user-name";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String PASSWORD_PROP_DISPLAYNAME = "password";  //$NON-NLS-1$

	private static final long serialVersionUID = 1L;
	private String name;
	private String displayName;
	private boolean isRequired = false;
	private boolean isModifiable = false;
	private boolean isMasked = false;
	private String value;
	private String defaultValue;
	private String originalValue;

	/**
	 * Constructor
	 */
	public PropertyItem() {
	}
		
	/**
	 * Return the property name
	 * @return the property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the property name
	 * @param name the property name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the property Display name
	 * @return the property display name
	 */
	public String getDisplayName() {
		return displayName;
	}

    /**
     * Set the Property display name
     * @param displayName the property display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

	/**
	 * Get the property value
	 * @return the property value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the property value
	 * @param value the property value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Get the property default value
	 * @return the property default value
	 */
	public String getDefaultValue() {
	    return defaultValue;
	}
	
	/**
	 * Set the Property default value
	 * @param defaultValue the Property default value
	 */
	public void setDefaultValue(String defaultValue) {
	    this.defaultValue = defaultValue;
	}

	/**
	 * Determine if the property is required
	 * @return 'true' if required, 'false' if not.
	 */
	public boolean isRequired() {
		return isRequired;
	}

	/**
     * Set whether the Property is required
     * @param isRequired 'true' if required, 'false' if not.
	 */
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

    /**
     * Determine if the property is modifiable
     * @return 'true' if modifiable, 'false' if not.
     */
	public boolean isModifiable() {
		return isModifiable;
	}

	/**
     * Set whether the Property is modifiable
     * @param isModifiable the Property
	 */
	public void setModifiable(boolean isModifiable) {
		this.isModifiable = isModifiable;
	}
	
    /**
     * Determine if the property is masked
     * @return 'true' if masked, 'false' if not.
     */
	public boolean isMasked() {
		return isMasked;
	}

	/**
     * Set whether the Property is masked
     * @param isMasked 'true' if masked, 'false' if not
	 */
	public void setMasked(boolean isMasked) {
		this.isMasked = isMasked;
	}
	
	/**
     * Set the Property original value
     * @param value the Property original value
	 */
	public void setOriginalValue(String value) {
	    this.originalValue = value;
	}
	
    /**
     * Determine if the property has a valid value
     * @return 'true' if valid, 'false' if not.
     */
	public boolean hasValidValue() {
	    boolean isValid = true;
	    
	    // If its required, must have a value
	    if(this.isRequired && CoreStringUtil.isEmpty(this.value)) {
	        isValid = false;
	    }
	    
	    return isValid;
	}
	
	/**
	 * Determine if the property has changed from its original value
	 * @return 'true' if property has changed, 'false' if not.
	 */
	public boolean hasChanged() {
	    boolean hasChanged = false;
	    if(CoreStringUtil.isEmpty(this.originalValue)) {
	        hasChanged = CoreStringUtil.isEmpty(this.value) ? false : true;
	    } else {
	        if (CoreStringUtil.isEmpty(this.value)) {
	            hasChanged=true;
	        } else {
	            hasChanged = this.value.equals(this.originalValue) ? false : true;
	        }
	    }
	    return hasChanged;
	}
	
	/**
	 * Reset the property value back to its original value
	 */
	public void reset() {
	    this.value = this.originalValue;
	}
}
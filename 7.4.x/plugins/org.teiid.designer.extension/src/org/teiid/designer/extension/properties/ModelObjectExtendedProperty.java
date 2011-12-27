/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.properties;

import org.teiid.core.properties.PropertyDefinition;


/**
 * <p>Extended Model Object property object. Contains both a {@link PropertyDefinition} and a current string value</p>
 * 
 * <p>Note that the property definition contains the default value</p>
 */
public class ModelObjectExtendedProperty {

	private PropertyDefinition definition;
	
	private String value;
	
	public ModelObjectExtendedProperty(PropertyDefinition definition, String value) {
		super();
		this.definition = definition;
		this.value = value;
	}

	/**
	 * Returns the current property value
	 * 
	 * @return the current property value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Setter for the property value
	 * 
	 * @param value the string value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Getter for the {@link PropertyDefinition}
	 * @return the {@link PropertyDefinition}
	 */
	public PropertyDefinition getDefinition() {
		return this.definition;
	}
	
	/**
	 * Returns true if the current value is equal to the {@link PropertyDefinition}'s default value
	 * @return true if value == default value. false otherwise
	 */
	public boolean isDefaultValue() {
		if( this.value == null || this.value.equals(this.definition.getDefaultValue()) ) {
			return true;
		}
		
		return false;
	}
}

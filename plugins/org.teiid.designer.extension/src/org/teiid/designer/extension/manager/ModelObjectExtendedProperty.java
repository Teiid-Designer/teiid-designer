/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.manager;

import org.teiid.core.properties.PropertyDefinition;



public class ModelObjectExtendedProperty {

	private PropertyDefinition definition;
	
	private String value;
	
	public ModelObjectExtendedProperty(PropertyDefinition definition, String value) {
		super();
		this.definition = definition;
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PropertyDefinition getDefinition() {
		return this.definition;
	}
	
	public boolean isDefaultValue() {
		if( this.value == null || this.value.equals(this.definition.getDefaultValue()) ) {
			return true;
		}
		
		return false;
	}
}

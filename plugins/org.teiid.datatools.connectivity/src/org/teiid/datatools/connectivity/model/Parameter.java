/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.datatools.connectivity.model;

/**
 * 
 */
public class Parameter {
	private char COLON = ':';
	
	
	public static final String PREFIX = "rest_param:"; //$NON-NLS-N$
	public static final String HEADER_PREFIX = "header_param:"; //$NON-NLS-N$
	
	private String name;
	private String defaultValue;
	private Type type;
	
	public enum Type { Query, URI, Header;
		
		Type() {
		}
		
		public static Type fromValue( String value ) {
	        try {
	            return valueOf(value);
	        } catch (IllegalArgumentException e) {
	            return null;
	        }
	    }
		
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 */
	public Parameter(String name, String defaultValue, Type type) {
		super();
		this.name = name;
		this.defaultValue = defaultValue;
		this.type = type;
	}
	
	/**
	 * @param name
	 * @param defaultValue
	 */
	public Parameter(String keyName, String propertyValue) {
		super();
		this.name = extractName(keyName);
		this.defaultValue = extractDefaultValue(propertyValue);
		this.type = extractType(propertyValue);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	public String getPropertyKey() {
		return (this.type.equals(Parameter.Type.Header) ? HEADER_PREFIX : PREFIX) + getName();
	}
	
	public String getPropertyValue() {
		return getType().toString() + ':' + getDefaultValue();
	}
	
	private String extractName(String keyName) {
		if( keyName.indexOf(COLON) > -1 ) {
			return keyName.substring(keyName.indexOf(COLON)+1);
		}
		return keyName;
	}
	
	private Type extractType(String propertyValue) {
		if( propertyValue.indexOf(COLON) > -1 ) {
			return Type.fromValue(propertyValue.substring(0, propertyValue.indexOf(COLON)));
		}
		return Type.Query;
	}
	
	private String extractDefaultValue(String propertyValue) {
		if( propertyValue.indexOf(COLON) > -1 ) {
			return propertyValue.substring(propertyValue.indexOf(COLON)+1);
		}
		return propertyValue;
	}

}

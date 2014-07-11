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
	
	private String name;
	private String defaultValue;
	private Type type;
	
	public enum Type { Query, URI;
		
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

}

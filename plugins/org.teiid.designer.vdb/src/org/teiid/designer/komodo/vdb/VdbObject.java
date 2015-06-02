/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.Properties;

import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;

/**
 * Base vdb object class
 * 
 * @author blafond
 *
 */
public abstract class VdbObject implements StringConstants {
	boolean changed;
	
	String name;
	String description;
	Properties properties;

	
	/**
	 * 
	 */
	public VdbObject() {
		properties = new Properties();
	}

	/**
	 * @return properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param newName
	 */
	public void setName(String newName) {
		setChanged(this.name, newName);
		this.name = newName;
	}
	/**
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		if( properties.get(key) != null ) {
			String valueString = properties.getProperty(key);
			if( StringUtilities.areDifferent(value, valueString)) {
				this.properties.put(key,  value);
				setChanged(true);
			}
		} else {
			this.properties.put(key,  value);
			setChanged(true);
		}

	}
	
	/**
	 * @param key
	 */
	public void removeProperty(String key) {
		setChanged(this.properties.remove(key) != null);
	}

	/**
	 * 
	 * @param newDescription
	 */
	public void setDescription(String newDescription) {
		setChanged(this.description, newDescription);
		setChanged(true);
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}
	
	
	/**
	 * @return changed
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * @param value
	 */
	protected void setChanged(boolean value) {
		changed = value;
	}

	/**
	 * @param value1
	 * @param value2
	 */
	protected void setChanged(boolean value1, boolean value2) {
		setChanged(value1 != value2);
	}
	
	/**
	 * @param value1
	 * @param value2
	 */
	protected void setChanged(String value1, String value2) {
		setChanged(StringUtilities.areDifferent(value1, value2));
	}
	
	/**
	 * @param value1
	 * @param value2
	 */
	protected void setChanged(int value1, int value2) {
		setChanged(value1 != value2);
	}
	

}

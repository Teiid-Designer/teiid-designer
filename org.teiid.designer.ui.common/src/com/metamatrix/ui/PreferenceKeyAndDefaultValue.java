/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * PreferenceKeyAndDefaultValue
 * 
 * Data class to encapsulate the key and default value for a preference
 */
public class PreferenceKeyAndDefaultValue {
	/////////////////////////////////////////////////////////////////////
	// Static methods
	/////////////////////////////////////////////////////////////////////
	/**
	 * Static method to store a default value for a preference in an IPreferenceStore.
	 * 
	 * @param  preferenceStore    the preference store to be stored into
	 * @param  keyAndValue     data object containing the key and default value for the preference
	 */	
	public static void storePreferenceDefault(IPreferenceStore preferenceStore,
			PreferenceKeyAndDefaultValue keyAndValue) {
		String key = keyAndValue.getKey();
		Object value = keyAndValue.getDefaultValue();
		if (value instanceof Boolean) {
			boolean bVal = ((Boolean)value).booleanValue();
			preferenceStore.setDefault(key, bVal);
		} else if (value instanceof Double) {
			double dVal = ((Double)value).doubleValue();
			preferenceStore.setDefault(key, dVal);
		} else if (value instanceof Float) {
			float fVal = ((Float)value).floatValue();
			preferenceStore.setDefault(key, fVal);
		} else if (value instanceof Integer) {
			int iVal = ((Integer)value).intValue();
			preferenceStore.setDefault(key, iVal);
		} else if (value instanceof Long) {
			long lVal = ((Long)value).longValue();
			preferenceStore.setDefault(key, lVal);
		} else if (value instanceof String) {
			String str = (String)value;
			preferenceStore.setDefault(key, str);
		} else if (value instanceof RGB) {
			RGB rgb = (RGB)value;
			PreferenceConverter.setDefault(preferenceStore, key, rgb);
		} else if (value instanceof Point) {
			Point pt = (Point)value;
			PreferenceConverter.setDefault(preferenceStore, key, pt);
		} else if (value instanceof Rectangle) {
			Rectangle rec = (Rectangle)value;
			PreferenceConverter.setDefault(preferenceStore, key, rec);
		} else if (value instanceof FontData) {
			FontData fd = (FontData)value;
			PreferenceConverter.setDefault(preferenceStore, key, fd);
		} else if (value instanceof FontData[]) {
			FontData[] fd = (FontData[])value;
			PreferenceConverter.setDefault(preferenceStore, key, fd);
		}
	}
	 
	/////////////////////////////////////////////////////////////////////
	// Instance variables
	/////////////////////////////////////////////////////////////////////
	private String key;
	private Object defaultValue;
	
	/////////////////////////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////////////////////////
	/**
	 * PreferenceKeyAndDefaultValue constructor.
	 * 
	 * @param key    the key for the preference
	 * @param defaultValue   its default value
	 */
	public PreferenceKeyAndDefaultValue(String key, Object defaultValue) {
		super();
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	/////////////////////////////////////////////////////////////////////
	// Instance methods
	/////////////////////////////////////////////////////////////////////
	/**
	 * Get the key for the preference
	 * 
	 * @return   key for the preference
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Get the default value for the preference
	 * 
	 * @return  default value for the preference
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
}

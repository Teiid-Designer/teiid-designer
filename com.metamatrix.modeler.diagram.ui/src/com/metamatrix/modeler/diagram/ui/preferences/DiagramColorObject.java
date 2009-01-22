/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;

/**
 * DiagramColorObject
 * 
 * Data class to encapsulate a String display name and a String preference key which 
 * corresponds to a preference whose value is an RGB.
 */
public class DiagramColorObject {
	private String displayName;
	private String rgbPreferenceKey;
	
	/**
	 * Constructor.
	 * 
	 * @param displayName   the name, in displayable form
	 * @param rgbPreferenceKey  key to a preference whose value must be an RGB
	 */
	public DiagramColorObject(String displayName, String rgbPreferenceKey) {
		super();
		this.displayName = displayName;
		this.rgbPreferenceKey = rgbPreferenceKey;
	}
	
	/**
	 * Get the name for display
	 * 
	 * @return  name for display
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Get the preference key
	 * 
	 * @return  preference key
	 */
	public String getRGBPreferenceKey() {
		return rgbPreferenceKey;
	}
	
	/**
	 * Get the color corresponding to the preference key
	 * 
	 * @return   Color (RGB) corresponding to the preference key
	 */
	public RGB getPreferenceValue() {
        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
        RGB rgb = PreferenceConverter.getColor(preferenceStore, rgbPreferenceKey);
        return rgb;
	}
}

/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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

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

package com.metamatrix.modeler.internal.ui.settings;


public class ModelerDialogSettingsInitializer {

	public void initialize() {
		//		initializeProblemsViewFilter();
	}


	// /*
	// * Initialize Problem View's Filter Dialog Setting
	// */
	// private void initializeProblemsViewFilter() {
	// boolean enabledValue = Boolean.valueOf(ModelerDialogSettingsI18n.ProblemsViewFilter_Enabled).booleanValue();
	//        
	// int onResourceValue = 0;
	// try {
	// onResourceValue = Integer.parseInt(ModelerDialogSettingsI18n.ProblemsViewFilter_OnResource);
	// } catch (NumberFormatException eNumberFormat) {
	// }
	//        
	// int markerLimitValue = 0;
	// try {
	// markerLimitValue = Integer.parseInt(ModelerDialogSettingsI18n.ProblemsViewFilter_MarkerLimit);
	// } catch (NumberFormatException eNumberFormat) {
	// }
	//        
	// boolean filterOnMarkerLimitValue =
	// Boolean.valueOf(ModelerDialogSettingsI18n.ProblemsViewFilter_FilterOnMarkerLimit).booleanValue();
	//
	// IDialogSettings settings = getProblemsViewMarkerFilterDialogSettings();
	// if( settings != null ) {
	// String setting = null;
	//            
	// // Get settings and set if non-existent
	// setting = settings.get(TAG_FILTER_ENABLED);
	// if (setting == null) {
	// settings.put(TAG_FILTER_ENABLED, enabledValue);
	// }
	//	        
	// // Get settings and set if non-existent
	// setting = settings.get(TAG_FILTER_FILTER_ON_MARKER_LIMIT);
	//
	// if (setting == null) {
	// settings.put(TAG_FILTER_FILTER_ON_MARKER_LIMIT, filterOnMarkerLimitValue);
	// }
	//            
	// setting = settings.get(TAG_FILTER_MARKER_LIMIT);
	//
	// if (setting == null) {
	// settings.put(TAG_FILTER_MARKER_LIMIT, markerLimitValue);
	// }
	//            
	// setting = settings.get(TAG_FILTER_ON_RESOURCE);
	//
	// if (setting == null) {
	// settings.put(TAG_FILTER_ON_RESOURCE, onResourceValue);
	// }
	// }
	// }
	//
	// private IDialogSettings getProblemsViewMarkerFilterDialogSettings() {
	// // NOTE: Code taken from ProblemsView getDialogSettings() method
	// AbstractUIPlugin plugin = (AbstractUIPlugin) Platform
	// .getPlugin(PlatformUI.PLUGIN_ID);
	// IDialogSettings workbenchSettings = plugin.getDialogSettings();
	// IDialogSettings settings = workbenchSettings
	// .getSection(TAG_PROBLEMS_VIEW_DIALOG_SECTION);
	//
	// if (settings == null)
	// settings = workbenchSettings.addNewSection(TAG_PROBLEMS_VIEW_DIALOG_SECTION);
	//        
	// IDialogSettings subSettings = settings.getSection(TAG_FILTER_SECTION);
	// if( subSettings == null ) {
	// subSettings = settings.addNewSection(TAG_FILTER_SECTION);
	// }
	// return subSettings;
	// }
}

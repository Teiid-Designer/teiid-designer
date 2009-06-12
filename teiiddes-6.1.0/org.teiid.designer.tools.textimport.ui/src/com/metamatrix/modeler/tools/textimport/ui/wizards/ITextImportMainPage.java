/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui.wizards;

public interface ITextImportMainPage {

	
	void saveWidgetValues();
	
	boolean finish();
	
	String getType();
	
	String getComboText();
	
	String getDescriptionText();
	
	String getSampleDataText();
}

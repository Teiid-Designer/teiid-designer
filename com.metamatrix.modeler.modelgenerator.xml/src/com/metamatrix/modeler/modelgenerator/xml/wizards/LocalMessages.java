/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalMessages {
	
	private static final String BUNDLE_NAME = "com.metamatrix.modeler.modelgenerator.xml.i18n"; //$NON-NLS-1$

	//CHECKSTYLE:OFF
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	//CHECKSTYLE:ON

	private LocalMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}

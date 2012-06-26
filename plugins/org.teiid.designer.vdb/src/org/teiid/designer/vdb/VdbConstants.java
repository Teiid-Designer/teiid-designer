/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;


/**
 * 
 */
public interface VdbConstants {

	/**
	 * The plugin id
	 */
    String PLUGIN_ID = "org.teiid.designer.vdb"; //$NON-NLS-1$

    /**
     * The package id for this plugin
     */
    String PACKAGE_ID = VdbConstants.class.getPackage().getName();
	
    /** Constants for the SourceHandler extension point */
    interface SourceHandlerExtension {
        String ID = "sourceHandler"; //$NON-NLS-1$
        String CLASS = "class"; //$NON-NLS-1$
        String CLASSNAME = "name"; //$NON-NLS-1$
    }
    
    /** Constants for the SourceHandler extension point */
    interface Translator {
        String NAME_KEY = "name"; //$NON-NLS-1$
    }
}

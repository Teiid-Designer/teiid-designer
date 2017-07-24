/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import javax.print.DocFlavor.BYTE_ARRAY;

import org.teiid.core.designer.util.StringConstants;


/**
 * 
 *
 * @since 8.0
 */
public interface VdbConstants extends StringConstants {

	/**
	 * The plugin id
	 */
    String PLUGIN_ID = VdbPlugin.class.getPackage().getName();

    /**
     * The package id for this plugin
     */
    String PACKAGE_ID = VdbConstants.class.getPackage().getName();
    
    /**
     * The extension property key for vdb-name
     */
    String VDB_NAME_KEY = "core:vdb-name"; //$NON-NLS-1$
    
    /**
     * The extension property key for vdb-name
     */
    String VDB_VERSION_KEY = "core:vdb-version"; //$NON-NLS-1$

    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
    String PREVIEW_PREFIX = "PREVIEW_"; //$NON-NLS-1$

    /**
     * The location of a vdb manifest
     */
    String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$
    
    /**
     *  Dynamic VDB file extension
     */
    String DYNAMIC_VDB_FILE_EXTENSION = XML;

    /**
     * The default query timeout value
     */
    int DEFAULT_TIMEOUT = 0;

    /**
     * The default value indicating if this VDB is a preview VDB. Value is {@value} .
     */
    boolean DEFAULT_PREVIEW = false;

    /**
     * The default version number. Value is {@value} .
     */
    int DEFAULT_VERSION = 1;

    /**
     * Schema for vdb file
     */
    String VDB_DEPLOYER_XSD = "vdb-deployer.xsd"; //$NON-NLS-1$

    /** Constants for the SourceHandler extension point */
    interface SourceHandlerExtension {
        String ID = "sourceHandler"; //$NON-NLS-1$
        String CLASS = "class"; //$NON-NLS-1$
        String CLASSNAME = "name"; //$NON-NLS-1$
    }

    /**
     * Translator name key
     */
    String TRANSLATOR_NAME_KEY = "name"; //$NON-NLS-1$

    /**
     * Preference indicating that a vdb conversion is in progress
     */
    String CONVERSION_IN_PROGRESS = "conversion-in-progress"; //$NON-NLS-1$
    
    /**
     * The prefix used before the workspace identifier when creating a Preview VDB name.
     */
    String DOT_VDB = ".vdb"; //$NON-NLS-1$
    
    /** Constants for the SourceHandler extension point */
    interface ConnectionTypes {
        String NONE = "NONE"; //$NON-NLS-1$
        String BY_VERSION = "BY_VERSION"; //$NON-NLS-1$
        String ANY = "ANY"; //$NON-NLS-1$
    }
    
    /**
     * 
     */
    String[] CONNECTION_TYPES = {ConnectionTypes.NONE, ConnectionTypes.BY_VERSION, ConnectionTypes.ANY};
}

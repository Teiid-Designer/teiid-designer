/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.teiidimporter.ui;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;


/**
 * UiConstants
 * 
 * @since 8.1
 */
public interface UiConstants {

    @SuppressWarnings( "javadoc" )
    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    @SuppressWarnings( "javadoc" )
    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    @SuppressWarnings( "javadoc" )
    interface ImageIds {
        String FOLDER = "icons/"; //$NON-NLS-1$
        
        String IMPORT_TEIID_METADATA = FOLDER + "importTeiidMetadataWiz.gif"; //$NON-NLS-1$
        String RESET_PROPERTY = FOLDER + "restore-default-value.png";  //$NON-NLS-1$
        String ADD_PROPERTY = FOLDER + "add_property.png";  //$NON-NLS-1$
        String REMOVE_PROPERTY = FOLDER + "remove_property.png";  //$NON-NLS-1$
    }
    
    String FILTER_CONSTAINTS = "filterConstraints"; //$NON-NLS-1$
    
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui.util;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.metamodels.builder.execution.MetamodelBuilderExecutionPlugin;

/** 
 * Utility class for the GenericImportUi plugin.
 */
public class GenericImportUtil {

    //Plugin constants
    final static String pluginID = MetamodelBuilderExecutionPlugin.PLUGIN_ID;

	// ==================================================================================
    //                        S T A T I C  M E T H O D S
    // ==================================================================================

    /**
     * Helper to  add a new status with no exception
     */
    public static void addStatus(final MultiStatus parent, final int severity, final String msg) {
        addStatus(parent, severity, msg, null);
    }
    
    /**
     *  Helper to add a new status to the given MultiStatus
     */
    public static void addStatus(final MultiStatus parent, final int severity, final String msg, final Throwable err) {
        final Status sts = new Status(severity, pluginID, 0, msg, err);
        parent.add(sts);
    }
    

}

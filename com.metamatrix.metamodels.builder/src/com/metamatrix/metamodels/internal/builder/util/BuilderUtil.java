package com.metamatrix.metamodels.internal.builder.util;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.metamodels.builder.BuilderConstants;
import com.metamatrix.metamodels.builder.MetamodelBuilderPlugin;

/** 
 * This is a helper class to encapsulate reusable methods developed for
 * the MetamodelsBuilder plugin
 */
public class BuilderUtil implements BuilderConstants {
    final static String pluginID = MetamodelBuilderPlugin.PLUGIN_ID;
    final PluginUtil UTIL = MetamodelBuilderPlugin.Util;
	
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.builder.util;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.PluginUtil;
import org.teiid.designer.metamodels.builder.MetamodelBuilderPlugin;


/**
 * This is a helper class to encapsulate reusable methods developed for the MetamodelsBuilder plugin
 *
 * @since 8.0
 */
public class BuilderUtil {
    final static String pluginID = MetamodelBuilderPlugin.PLUGIN_ID;
    final PluginUtil UTIL = MetamodelBuilderPlugin.Util;

    // ==================================================================================
    // S T A T I C M E T H O D S
    // ==================================================================================

    /**
     * Helper to add a new status with no exception
     */
    public static void addStatus( final MultiStatus parent,
                                  final int severity,
                                  final String msg ) {
        addStatus(parent, severity, msg, null);
    }

    /**
     * Helper to add a new status to the given MultiStatus
     */
    public static void addStatus( final MultiStatus parent,
                                  final int severity,
                                  final String msg,
                                  final Throwable err ) {
        final Status sts = new Status(severity, pluginID, 0, msg, err);
        parent.add(sts);
    }

}

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

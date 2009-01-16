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

package com.metamatrix.modeler.internal.core.util;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.ModelerCore;


/** 
 * This static utility class allows methods to log timing values.
 * @since 4.3
 */
public abstract class StartupLogger {
    private static boolean propLoaded = false;
    private static boolean loggingStartup = false;
    
    /** 
     * @return Returns the loggingStartup value.
     * @since 4.3
     */
    public static boolean isLoggingStartup() {
        if( !propLoaded ) {
            String timer = System.getProperty("startupTimer"); //$NON-NLS-1$
            if( timer != null )
                loggingStartup = true;
            propLoaded = true;
        }
        return loggingStartup;
    }
    
    public static void log(String message) {
        if( isLoggingStartup() )
            ModelerCore.Util.log(IStatus.INFO, " >> STARTUP: " + message); //$NON-NLS-1$
    }
    
    public static void log(String message, long time) {
        if( isLoggingStartup() ) {
            log(message + " : Time = [" + time + "] ms"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    
}

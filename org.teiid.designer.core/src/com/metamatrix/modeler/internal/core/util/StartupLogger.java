/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

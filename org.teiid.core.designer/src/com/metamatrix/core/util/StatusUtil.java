/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.log.LogMessage;

public class StatusUtil {

    public static LogMessage convert( IStatus status ) {
        return new LogMessage(new Object[] {status.getMessage()});
    }

    public static IStatus createIStatus( final int severity,
                                         final String pluginId,
                                         final int errorCode,
                                         final String message,
                                         Throwable throwable ) {
        return new Status(severity, pluginId, errorCode, message, throwable);
    }

}

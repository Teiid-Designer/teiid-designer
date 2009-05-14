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
import com.metamatrix.core.log.MessageLevel;



public class StatusUtil {
    
    public static LogMessage convert(IStatus status) {
        int messageLevel = MessageLevel.INFO;
        
        switch(status.getSeverity()) {
            case IStatus.ERROR:
                messageLevel = MessageLevel.ERROR;
                break;
            case IStatus.WARNING:
                messageLevel = MessageLevel.WARNING;
                break;
            case IStatus.OK:
                messageLevel = MessageLevel.NONE;
                break;
            default:
                messageLevel = MessageLevel.INFO;
        }        
        return new LogMessage(status.getPlugin(), messageLevel, status.getException(), new Object[] {status.getMessage()}, status.getCode());
    }
    
    public static IStatus convert(LogMessage message) {
        int severity = IStatus.ERROR;
        
        switch(message.getLevel()) {
            
            case MessageLevel.CRITICAL:
                severity = IStatus.ERROR;
                break;

            case MessageLevel.ERROR:
                severity = IStatus.ERROR;
                break;

            case MessageLevel.DETAIL:
            case MessageLevel.TRACE:
                severity = IStatus.INFO;
                break;
                
            case MessageLevel.INFO:
                severity = IStatus.INFO;
                break;
                
            case MessageLevel.WARNING:
                severity = IStatus.WARNING;
                break;
                
            case MessageLevel.NONE:
                severity = IStatus.OK;
                break;
        }        
        
        return new Status(severity, message.getContext(), message.getErrorCode(), message.getText(), message.getException());
    }
    
    public static IStatus createIStatus(final int severity, final String pluginId, final int errorCode, final String message, Throwable throwable) {
        return new Status(severity, pluginId, errorCode, message, throwable);
    }
    
}

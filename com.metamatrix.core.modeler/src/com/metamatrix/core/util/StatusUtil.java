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

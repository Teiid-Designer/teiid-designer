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

import java.io.File;


/** 
 * Utility class used to centralize determining File Separator strings independent of OS.
 * 
 * @since 5.0
 */
public final class FileSeparatorUtil {

    private static final String FILE_SEPARATOR_OS = File.separator;
    public static final String FILE_SEPARATOR_WIN32 = "\\";        //$NON-NLS-1$
    public static final String FILE_SEPARATOR_LINUX = "/";         //$NON-NLS-1$

    public static boolean hasFileSeparator(String somePath) {
        if (somePath == null) {
            final String msg = "file path cannot be NULL";  //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        
        if( somePath.indexOf(FILE_SEPARATOR_WIN32) > -1 ||
            somePath.indexOf(FILE_SEPARATOR_LINUX) > -1 ) {
            return true;
        }
        
        return false;            
    }
    
    /**
     * Returns a file separator for a given input string path
     * If either the WIN32 or LINUX separators are present, it is returned.
     * If NONE is detected, then the File.separator (OS specific) is returned. 
     * @param somePath
     * @return
     * @since 5.0
     */
    public static String getFileSeparator(String somePath) {
        if( somePath.indexOf(FILE_SEPARATOR_WIN32) > -1) {
            return FILE_SEPARATOR_WIN32;
        }
        
        if( somePath.indexOf(FILE_SEPARATOR_LINUX) > -1 ) {
            return FILE_SEPARATOR_LINUX;
        }
        
        return FILE_SEPARATOR_OS;
    }
    
    public static String getOSFileSeparator() {
        return FILE_SEPARATOR_OS;
    }
}

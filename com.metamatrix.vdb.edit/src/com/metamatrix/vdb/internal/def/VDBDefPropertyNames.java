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

package com.metamatrix.vdb.internal.def;


/**
 * These property names defines the header information stored in the .DEF file.
 * These settings are used to determine backwards compatibility when importing 
 * the .DEF file.
 * 
 * The #CONFIGURATION_VERSION is used to check .DEF files prior to the 4.2.1 release.
 *  
 * @since 4.2
 */

public interface VDBDefPropertyNames {

    public static final String VDB_EXPORTER_VERSION = "VDBExporterVersion"; //$NON-NLS-1$
    public static final String APPLICATION_CREATED_BY = "ApplicationCreatedBy"; //$NON-NLS-1$
    public static final String APPLICATION_VERSION_CREATED_BY = "ApplicationVersion"; //$NON-NLS-1$
    public static final String USER_CREATED_BY = "UserCreatedBy"; //$NON-NLS-1$
    public static final String METAMATRIX_SYSTEM_VERSION = "MetaMatrixSystemVersion"; //$NON-NLS-1$
    public static final String TIME = "Time"; //$NON-NLS-1$
    
    
    /**
     * VDBEXPORTER_LATEST_VERSION indicates the version of the VDB Definition (.DEF) that
     * is currently supported. 
     */
    public static final String VDBEXPORTER_LATEST_VERSION = "4.1"; //$NON-NLS-1$
    
    
    public static final double VDBEXPORTER_LATEST_VERSION_DBL = 4.1;
    
    
    
}

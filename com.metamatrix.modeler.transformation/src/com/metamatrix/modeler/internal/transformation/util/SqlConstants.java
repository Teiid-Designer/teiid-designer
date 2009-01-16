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

package com.metamatrix.modeler.internal.transformation.util;

/**
 * SqlConstants
 */
public interface SqlConstants {

    public static final String TAB = "\t"; //$NON-NLS-1$
    public static final String CR = "\n"; //$NON-NLS-1$
    public static final String BLANK = ""; //$NON-NLS-1$
    public static final String DBL_SPACE = "  "; //$NON-NLS-1$
    public static final String SPACE = " "; //$NON-NLS-1$
    public static final String COMMA = ","; //$NON-NLS-1$
    public static final String RETURN = "\n"; //$NON-NLS-1$
    public static final String SELECT = "SELECT"; //$NON-NLS-1$
    public static final String FROM = "FROM"; //$NON-NLS-1$
    public static final String WHERE = "WHERE"; //$NON-NLS-1$
    
    public static final String SQL_TYPE_SELECT_STRING = "SELECT"; //$NON-NLS-1$
    public static final String SQL_TYPE_UPDATE_STRING = "UPDATE"; //$NON-NLS-1$
    public static final String SQL_TYPE_INSERT_STRING = "INSERT"; //$NON-NLS-1$
    public static final String SQL_TYPE_DELETE_STRING = "DELETE"; //$NON-NLS-1$
    public static final String SQL_TYPE_UNKNOWN_STRING = "UNKNOWN"; //$NON-NLS-1$
}

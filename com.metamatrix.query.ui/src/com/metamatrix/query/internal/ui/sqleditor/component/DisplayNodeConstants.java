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

package com.metamatrix.query.internal.ui.sqleditor.component;

/**
 * The <code>DisplayNodeConstants</code> interface contains constants used when working with {@link DisplayNode}s.
 * @author Mark Drilling 11/22/02
 * @since 3.1
 * @version 1.0
 */
public interface DisplayNodeConstants {

	// Node String Constants
    public static final String BLANK = ""; //$NON-NLS-1$
    public static final String SPACE = " "; //$NON-NLS-1$
    public static final String DBLSPACE = "  "; //$NON-NLS-1$
    public static final String TAB = "\t"; //$NON-NLS-1$
	public static final String CR = "\n"; //$NON-NLS-1$
	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String COLON = ":"; //$NON-NLS-1$
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	public static final String LTPAREN = "("; //$NON-NLS-1$
	public static final String RTPAREN = ")"; //$NON-NLS-1$
    public static final String EQUALS = "="; //$NON-NLS-1$
    public static final String ERROR = "ERROR"; //$NON-NLS-1$
    public static final String GT = ">"; //$NON-NLS-1$
    public static final String GE = ">="; //$NON-NLS-1$
    public static final String LT = "<"; //$NON-NLS-1$
    public static final String LE = "<="; //$NON-NLS-1$
    public static final String NE = "<>"; //$NON-NLS-1$
    public static final String NULL = "null"; //$NON-NLS-1$
    public static final String UNKNOWN = "Unknown"; //$NON-NLS-1$
    public static final String SELECT_STR = "SELECT"; //$NON-NLS-1$
    public static final String FROM_STR = "FROM"; //$NON-NLS-1$
    public static final String WHERE_STR = "WHERE"; //$NON-NLS-1$
    public static final String OPTIONAL_COMMENTS = "/* optional */"; //$NON-NLS-1$
    public static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    public static final String EXEC = "EXEC"; //$NON-NLS-1$

	// Node Type Identifiers
    public static final int ELEMENT = 0;
    public static final int GROUP = 1;
    public static final int CRITERIA = 2;
    public static final int EXPRESSION = 3;
    public static final int COMMAND = 4;
    public static final int CLAUSE = 5;
    public static final int SELECT = 6;
    public static final int INTO = 7;
    public static final int FROM = 8;
    public static final int WHERE = 9;
    public static final int GROUPBY =10;
    public static final int HAVING = 11;
    public static final int ORDERBY = 12;
    public static final int OPTION = 13;
    public static final int EDITABLE_CRITERIA = 14;
    public static final int SCALAR_SUBQUERY = 15;
    
}


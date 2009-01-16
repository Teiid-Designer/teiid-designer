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

package com.metamatrix.query.internal.ui.sqleditor.sql;

/**
 * SqlFormattingConstants
 */
public interface SqlFormattingConstants {
    
    // =========================================
    // Constants
    // =========================================
    
    /**
     * SQL Keywords that require special handling in formatting
     * @since 4.0  
     */
    public static final String SELECT = "SELECT";                       //$NON-NLS-1$ 
    public static final String FROM   = "FROM";                         //$NON-NLS-1$ 
    public static final String WHERE  = "WHERE";                        //$NON-NLS-1$ 
    public static final String INSERT = "INSERT";                       //$NON-NLS-1$ 
    public static final String VALUES = "VALUES";                       //$NON-NLS-1$ 
    public static final String UPDATE = "UPDATE";                       //$NON-NLS-1$ 
    public static final String SET    = "SET";                          //$NON-NLS-1$ 
    public static final String DELETE = "DELETE FROM";                  //$NON-NLS-1$ 
    public static final String CREATE_PROCEDURE = "CREATE VIRTUAL PROCEDURE"; //$NON-NLS-1$
    public static final String CREATE_LOCAL_TEMP_TABLE = "CREATE LOCAL TEMPORARY TABLE";                       //$NON-NLS-1$ 
    public static final String DROP_TABLE = "DROP TABLE";                       //$NON-NLS-1$ 
     

    
    /**
     * A String array of these keywords
     * @since 4.0
     */
    public static final String[] KEYWORDS = new String[] { SELECT,      
                                                           FROM,        
                                                           WHERE,       
                                                           INSERT,      
                                                           VALUES,      
                                                           UPDATE,      
                                                           SET,         
                                                           DELETE,      
                                                           CREATE_PROCEDURE };    

    /**
     * Special characters used in parsing and/or formatting
     * @since 4.0  
     */
    public static final char SPACE          = ' ';                      
    public static final char PAREN_OPEN     = '(';                      
    public static final char PAREN_CLOSE    = ')';                      

    public static final String NEWLINE      = "\n";                     //$NON-NLS-1$
    public static final String INDENT       = "\t";                     //$NON-NLS-1$
    public static final char chNEWLINE      = '\n';                     
    public static final char chINDENT       = '\t';                     
    public static final String EMPTY_STRING = "";                       //$NON-NLS-1$

}

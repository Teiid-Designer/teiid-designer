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

package com.metamatrix.modeler.jdbc.metadata;

import java.sql.DatabaseMetaData;

/**
 * JdbcProcedure
 */
public interface JdbcProcedure extends JdbcNode {
    
    public static final short RESULT_UNKNOWN    = DatabaseMetaData.procedureResultUnknown;
    public static final short NO_RESULT         = DatabaseMetaData.procedureNoResult;
    public static final short RETURNS_RESULT    = DatabaseMetaData.procedureReturnsResult;

    /**
     * Get the remarks for this table.
     * @return the remarks; may be null or empty
     */
    public String getRemarks();
    
    /**
     * Get the type of this procedure.  One of {@link #RESULT_UNKNOWN}, {@link #NO_RESULT} or
     * {@link #RETURNS_RESULT}.
     * @return the procedure type
     */
    public short getProcedureType();
}

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

/**
 * DatabaseInfo
 */
public interface DatabaseInfo {
    
    /**
     * Return the product name (as reported by the driver).
     * @return the product name
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    String getProductName();

    /**
     * Return the product version (as reported by the driver).
     * @return the product version
     * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
     */
    String getProductVersion();

    /**
     * Return the major version of the driver.
     * @return the driver's major version
     * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
     */
    int getDriverMajorVersion();

    /**
     * Return the minor version of the driver.
     * @return the driver's minor version
     * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
     */
    int getDriverMinorVersion();

    /**
     * Return the name of the driver.
     * @return the driver name
     * @see java.sql.DatabaseMetaData#getDriverName()
     */
    String getDriverName();

    /**
     * Return the version of the driver.
     * @return the driver version
     * @see java.sql.DatabaseMetaData#getDriverVersion()
     */
    String getDriverVersion();

    /**
     * Return the URL for the database (as reported by the driver).
     * @return the URL for the database
     * @see java.sql.DatabaseMetaData#getURL()
     */
    String getDatabaseURL();

    /**
     * Return the username (as reported by the driver).
     * @return the username
     * @see java.sql.DatabaseMetaData#getUserName()
     */
    String getUserName();
    
    /**
     * Return whether the database is in a read-only mode.
     * @return true if the database is read only
     * @see java.sql.DatabaseMetaData#isReadOnly()
     */
    boolean isReadOnly();

}

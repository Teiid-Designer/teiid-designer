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

package com.metamatrix.modeler.dqp;


/** 
 * This has names for properties stored on connectorbindings stored on vdb definition file
 * and also the jdbc import properties stored on physical models. 
 * @since 4.3
 */
public class JDBCConnectionPropertyNames {

    /**
     * This is the name of the property that stores driver class name on a connector binding.
     * This is same as the property {@link com.metamatrix.connector.jdbc.JDBCPropertyNames.DRIVER_CLASS}. 
     */
    public static final String CONNECTOR_JDBC_DRIVER_CLASS = "Driver"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores the connection url name on a connector binding.
     * This is same as the property {@link com.metamatrix.connector.jdbc.JDBCPropertyNames.URL}. 
     */    
    public static final String CONNECTOR_JDBC_URL = "URL"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores user name on a connector binding.
     * This is same as the property {@link com.metamatrix.connector.jdbc.JDBCPropertyNames.USERNAME}. 
     */
    public static final String CONNECTOR_JDBC_USER = "User"; //$NON-NLS-1$
    
    /**
     * The property identifier that stores the JDBC URL password.
     * This is same as the property {@link com.metamatrix.connector.jdbc.JDBCPropertyNames.URL}.
     * @since 5.5 
     */
    public static final String CONNECTOR_JDBC_PASSWORD = "Password"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores driver class name on the jdbc import settings of a physical models.
     * This is same as the property defined in {@link com.metamatrix.modeler.jdbc.relational.aspects.sql.addJdbcSourceProperties()} method. 
     */
    public static final String JDBC_IMPORT_DRIVER_CLASS = "com.metamatrix.modeler.jdbc.JdbcSource.driverClass"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores driver class name on the jdbc import settings of a physical models.
     * This is same as the property defined in {@link com.metamatrix.modeler.jdbc.relational.aspects.sql.addJdbcSourceProperties()} method.
     */    
    public static final String JDBC_IMPORT_URL = "com.metamatrix.modeler.jdbc.JdbcSource.url"; //$NON-NLS-1$

    /**
     * This is the name of the property that stores user name on the jdbc import settings of a physical models.
     * This is same as the property defined in {@link com.metamatrix.modeler.jdbc.relational.aspects.sql.addJdbcSourceProperties()} method.
     */    
    public static final String JDBC_IMPORT_USERNAME = "com.metamatrix.modeler.jdbc.JdbcSource.username"; //$NON-NLS-1$

}

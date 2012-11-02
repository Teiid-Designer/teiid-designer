/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

/**
 * @since 8.0
 */
public interface ITeiidJdbcInfo extends ITeiidConnectionInfo {

    /**
     * The default Teiid JDBC port number. Value is {@value} .
     */
    public static final String DEFAULT_PORT = "31000"; //$NON-NLS-1$
    /**
     * The default Teiid Admin secure protocol flag. Value is {@value} .
     */
    public static final boolean DEFAULT_SECURE = false;
    /**
     * The default username for the teiid server
     */
    public static final String DEFAULT_JDBC_USERNAME = "user"; //$NON-NLS-1$
    /**
     * The default password for the teiid server
     */
    public static final String DEFAULT_JDBC_PASSWORD = "user"; //$NON-NLS-1$

}

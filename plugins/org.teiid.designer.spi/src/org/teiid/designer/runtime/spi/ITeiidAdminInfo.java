/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

/**
 * @since 8.0
 */
public interface ITeiidAdminInfo extends ITeiidConnectionInfo {

    /**
     * The default Teiid Admin persist password flag. Value is {@value} .
     */
    public static final boolean DEFAULT_PERSIST_PASSWORD = true;
    /**
     * The default Teiid Admin port number. Value is {@value} .
     */
    public static final String DEFAULT_PORT = "9999"; //$NON-NLS-1$
    /**
     * The default Teiid Admin port number on teiid 7.7 servers.
     */
    public static final String DEFAULT_LEGACY_PORT = "31443"; //$NON-NLS-1$
    
    /**
     * The default Teiid Admin secure protocol flag. Value is {@value} .
     */
    public static final boolean DEFAULT_SECURE = true;
    
    /**
     * The default username for the teiid server
     */
    public static final String DEFAULT_ADMIN_USERNAME = "admin"; //$NON-NLS-1$
    /**
     * The default password for the teiid server
     */
    public static final String DEFAULT_ADMIN_PASSWORD = "admin"; //$NON-NLS-1$

}

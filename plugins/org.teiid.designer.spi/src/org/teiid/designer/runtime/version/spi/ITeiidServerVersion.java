/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.version.spi;

/**
 * Parent marker interface for teiid server version information
 */
public interface ITeiidServerVersion {
    
    public static final String DOT = ".";  //$NON-NLS-1$
    
    public static final String WILDCARD = "*"; //$NON-NLS-1$
    
    public static final String ZERO = "0"; //$NON-NLS-1$

    public static final String ONE = "1"; //$NON-NLS-1$

    public static final String TWO = "2"; //$NON-NLS-1$

    public static final String THREE = "3"; //$NON-NLS-1$

    public static final String FOUR = "4"; //$NON-NLS-1$

    public static final String FIVE = "5"; //$NON-NLS-1$

    public static final String SIX = "6"; //$NON-NLS-1$

    public static final String SEVEN = "7"; //$NON-NLS-1$

    public static final String EIGHT = "8"; //$NON-NLS-1$

    public static final String NINE = "9"; //$NON-NLS-1$

    /**
     * Default teiid 8 server version
     */
    public static final String DEFAULT_TEIID_8_SERVER_ID = EIGHT + DOT + ZERO + DOT + ZERO;

    /**
     * Default teiid 7 server version
     */
    public static final String DEFAULT_TEIID_7_SERVER_ID = SEVEN + DOT + SEVEN + DOT + ZERO;
    
    
    /**
     * @return the major version segment
     */
    String getMajor();
    
    /**
     * @return the minor version segment
     */
    String getMinor();
    
    /**
     * @return the micro version segment 
     */
    String getMicro();

    /**
     * Test whether the minor or micro segments are wildcards '*'
     * 
     * @return true if there are wildcards. false otherwise
     */
    boolean hasWildCards();

    /**
     * @param otherVersion
     * 
     * @return true if the otherVersion is considered equivalent
     */
    boolean compareTo(ITeiidServerVersion otherVersion);
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.version.spi;

/**
 * Parent marker interface for teiid instance version information
 */
public interface ITeiidServerVersion {
    
    String DOT = ".";  //$NON-NLS-1$
    
    String WILDCARD = "x"; //$NON-NLS-1$
    
    String ZERO = "0"; //$NON-NLS-1$

    String ONE = "1"; //$NON-NLS-1$

    String TWO = "2"; //$NON-NLS-1$

    String THREE = "3"; //$NON-NLS-1$

    String FOUR = "4"; //$NON-NLS-1$

    String FIVE = "5"; //$NON-NLS-1$

    String SIX = "6"; //$NON-NLS-1$

    String SEVEN = "7"; //$NON-NLS-1$

    String EIGHT = "8"; //$NON-NLS-1$

    String NINE = "9"; //$NON-NLS-1$

    /**
     * Default teiid 8 server version
     */
    String DEFAULT_TEIID_8_SERVER_ID = EIGHT + DOT + FOUR + DOT + ZERO;

    /**
     * Default teiid 7 server version
     */
    String DEFAULT_TEIID_7_SERVER_ID = SEVEN + DOT + SEVEN + DOT + ZERO;
    
    
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
    
    /**
     * Is this a 7 server?
     * 
     * @return true is version is 7
     */
    boolean isSevenServer();

    /**
     * @return the minimum version that this version could be,
     *                 eg. 8.x.x will be 8.0.0 while 8.1.x will be 8.1.0 and
     *                       8.2.1 will always be 8.2.1
     */
    ITeiidServerVersion getMinimumVersion();

    /**
     * @return the maximum version that this version could be,
     *                 eg. 8.x.x will be 8.9.9 while 8.1.x will be 8.1.9 and
     *                       8.2.1 will always be 8.2.1
     */
    ITeiidServerVersion getMaximumVersion();

    /**
     * Is this version greater than the given version
     *
     * Wildcards will cause the result to return false since either
     * this or otherVersion could be the greater depending on the
     * value given to the wildcard.
     *
     * @param otherVersion
     *
     * @return true if this version is greater. False otherwise.
     */
    boolean isGreaterThan(ITeiidServerVersion otherVersion);

    /**
     * Is this version less than the given version
     *
     * Wildcards will cause the result to return false since either
     * this or otherVersion could be the lesser depending on the
     * value given to the wildcard.
     *
     * @param otherVersion
     *
     * @return true if this version is less. False otherwise.
     */
    boolean isLessThan(ITeiidServerVersion otherVersion);

    /**
     * Convenience that delegates to {@link #compareTo(ITeiidServerVersion)}
     * and {@link #isGreaterThan(ITeiidServerVersion)}.
     *
     * @param otherVersion
     *
     * @return this is greater than or equal to otherVersion
     */
    boolean isGreaterThanOrEqualTo(ITeiidServerVersion otherVersion);

    /**
     * Convenience that delegates to {@link #compareTo(ITeiidServerVersion)}
     * and {@link #isLessThan(ITeiidServerVersion)}.
     *
     * @param otherVersion
     *
     * @return this is less than or equal to otherVersion
     */
    boolean isLessThanOrEqualTo(ITeiidServerVersion otherVersion);
}

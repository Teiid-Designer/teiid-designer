/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.version.spi;

import org.teiid.core.designer.util.CoreArgCheck;


/**
 * Teiid Server version class 
 * 
 * @since 8.0
 */
public class TeiidServerVersion implements ITeiidServerVersion {

    /**
     * The default teiid 8 server version
     */
    public static final ITeiidServerVersion DEFAULT_TEIID_8_SERVER = new TeiidServerVersion(DEFAULT_TEIID_8_SERVER_ID);

    private String versionString = ZERO + DOT + ZERO + DOT + ZERO;

    private final String majorVersion;

    private String minorVersion = WILDCARD;

    private String microVersion = WILDCARD;

    /**
     * Create a new instance with the given version segments
     * 
     * @param major
     * @param minor
     * @param micro
     */
    public TeiidServerVersion(String major, String minor, String micro) {
        CoreArgCheck.isNotNull(major);
        CoreArgCheck.isNotNull(minor);
        CoreArgCheck.isNotNull(micro);
        
        this.majorVersion = major;
        this.minorVersion = minor;
        this.microVersion = micro;
        this.versionString = major + DOT + minor + DOT + micro;
    }
    
    /**
     * Create a new instance with the given version string
     * 
     * @param versionString
     */
    public TeiidServerVersion(String versionString) {
        this.versionString = versionString;

        String[] tokens = versionString.split("\\."); //$NON-NLS-1$

        switch (tokens.length) {
            case 3:
                majorVersion = tokens[0];
                minorVersion = tokens[1];
                microVersion = tokens[2];
                break;
            case 2:
                majorVersion = tokens[0];
                minorVersion = tokens[1];
                break;
            case 1:
            default:
                majorVersion = tokens[0];
        }
    }

    @Override
    public String toString() {
        return versionString;
    }

    @Override
    public String getMajor() {
        return majorVersion;
    }

    @Override
    public String getMinor() {
        return minorVersion;
    }

    @Override
    public String getMicro() {
        return microVersion;
    }
    
    @Override
    public boolean hasWildCards() {
        return minorVersion.equals(WILDCARD) || microVersion.equals(WILDCARD);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.majorVersion == null) ? 0 : this.majorVersion.hashCode());
        result = prime * result + ((this.microVersion == null) ? 0 : this.microVersion.hashCode());
        result = prime * result + ((this.minorVersion == null) ? 0 : this.minorVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TeiidServerVersion other = (TeiidServerVersion)obj;
        if (this.majorVersion == null) {
            if (other.majorVersion != null) return false;
        } else if (!this.majorVersion.equals(other.majorVersion)) return false;
        if (this.microVersion == null) {
            if (other.microVersion != null) return false;
        } else if (!this.microVersion.equals(other.microVersion)) return false;
        if (this.minorVersion == null) {
            if (other.minorVersion != null) return false;
        } else if (!this.minorVersion.equals(other.minorVersion)) return false;
        return true;
    }

    
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.version.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.DesignerSPIPlugin;
import org.teiid.designer.Messages;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;


/**
 * Teiid Instance version class 
 * 
 * @since 8.0
 */
public class TeiidServerVersion implements ITeiidServerVersion {

    /**
     * Version enumerator
     */
    public static enum Version {

        /**
         * Teiid 7.7
         */
        TEIID_7_7(VersionID.TEIID_7_7),

        /**
         * Teiid 8.0
         */
        TEIID_8_0(VersionID.TEIID_8_0),

        /**
         * Teiid 8.1
         */
        TEIID_8_1(VersionID.TEIID_8_1),

        /**
         * Teiid 8.2
         */
        TEIID_8_2(VersionID.TEIID_8_2),

        /**
         * Teiid 8.3
         */
        TEIID_8_3(VersionID.TEIID_8_3),

        /**
         * Teiid 8.4
         */
        TEIID_8_4(VersionID.TEIID_8_4),

        /**
         * Teiid 8.5
         */
        TEIID_8_5(VersionID.TEIID_8_5),

        /**
         * Teiid 8.6
         */
        TEIID_8_6(VersionID.TEIID_8_6),

        /**
         * Teiid 8.7
         */
        TEIID_8_7(VersionID.TEIID_8_7),

        /**
         * Teiid 8.8
         */
        TEIID_8_8(VersionID.TEIID_8_8),

        /**
         * Teiid 8.9
         */
        TEIID_8_9(VersionID.TEIID_8_9),

        /**
         * Teiid 8.10
         */
        TEIID_8_10(VersionID.TEIID_8_10),

        /**
         * Teiid 8.11
         */
        TEIID_8_11(VersionID.TEIID_8_11),

        /**
         * Teiid 8.12
         */
        TEIID_8_12_4(VersionID.TEIID_8_12_4),

        /**
         * Default Teiid for this Designer
         */
        TEIID_DEFAULT(VersionID.TEIID_8_12_4);

        private final ITeiidServerVersion version;

        Version(VersionID id) {
            version = new TeiidServerVersion(id.toString());
        }

        /**
         * @return version model
         */
        public ITeiidServerVersion get() {
            return version;
        }
    }

    private String versionString = ZERO + DOT + ZERO + DOT + ZERO;

    private final String majorVersion;

    private String minorVersion = WILDCARD;

    private String microVersion = WILDCARD;

    /**
     * Create a new instance with the given version segments
     * 
     * @param major the major version
     * @param minor the minor version
     * @param micro the micro version
     */
    public TeiidServerVersion(String major, String minor, String micro) {
        if (major == null)
            throw new IllegalArgumentException(NLS.bind(Messages.valueCannotBeNull, "major")); //$NON-NLS-1$
        
        if (minor == null)
            throw new IllegalArgumentException(NLS.bind(Messages.valueCannotBeNull, "minor")); //$NON-NLS-1$
        
        if (micro == null)
            throw new IllegalArgumentException(NLS.bind(Messages.valueCannotBeNull, "micro")); //$NON-NLS-1$
        
        this.majorVersion = major;
        this.minorVersion = minor;
        this.microVersion = micro;
        this.versionString = major + DOT + minor + DOT + micro;
    }
    
    /**
     * Create a new instance with the given version string
     * 
     * @param versionString the version string
     */
    public TeiidServerVersion(String versionString) {
        this.versionString = versionString;

        String[] tokens = versionString.split("\\."); //$NON-NLS-1$

        if (tokens.length >= 3) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
            if(tokens[2]!=null) {
            	int dashIndex = tokens[2].indexOf('-');
            	if(dashIndex!=-1 && tokens[2].length()>0) {
            		microVersion = tokens[2].substring(0,dashIndex);
            	} else {
                    microVersion = tokens[2];
            	}
            }
        }
        else if(tokens.length == 2) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
        }
        else {
            majorVersion = tokens[0];
        }
        this.versionString = majorVersion + DOT + minorVersion + DOT + microVersion;
    }

    /**
     * Get the ultimate default teiid instance version. This is the provided
     * as the default teiid instance version IF the user has not configured
     * a server connection nor set the default teiid instance preference.
     *
     * This attempts to derive the latest version of server from
     * the installed client runtimes but if none, returns the
     * hardcoded default value.
     *
     * @return {@link ITeiidServerVersion} default version
     */
    public static ITeiidServerVersion deriveUltimateDefaultServerVersion() {
        ITeiidServerVersion lastTestedDefault = Version.TEIID_DEFAULT.get();

        Collection<ITeiidServerVersion> serverVersions = null;
        try {
            serverVersions = TeiidRuntimeRegistry.getInstance().getSupportedVersions();
        } catch (Exception ex) {
            DesignerSPIPlugin.log(ex);
            return lastTestedDefault;
        }

        if (serverVersions == null || serverVersions.isEmpty())
            return lastTestedDefault;

        if (serverVersions.size() == 1)
            return serverVersions.iterator().next();

        // Find the latest server version by sorting the registered client runtime versions
        List<String> items = new ArrayList<String>(serverVersions.size());
        for (ITeiidServerVersion serverVersion : serverVersions) {
            /*
             * Do not offer unreleased and untested versions by default.
             * Does not stop the user choosing such versions but avoids
             * displaying them up-front.
             */
            if (serverVersion.isGreaterThan(lastTestedDefault))
                continue;

            items.add(serverVersion.toString());
        }
        Collections.sort(items, Collections.reverseOrder());

        return new TeiidServerVersion(items.get(0));
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
        return majorVersion.equals(WILDCARD) || minorVersion.equals(WILDCARD) || microVersion.equals(WILDCARD);
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

    @Override
    public boolean compareTo(ITeiidServerVersion otherVersion) {
        String entryMajor = otherVersion.getMajor();

        if (! getMajor().equals(entryMajor) && ! getMajor().equals(WILDCARD) && ! entryMajor.equals(WILDCARD))
            return false;
        
        String entryMinor = otherVersion.getMinor();
        
        if (! getMinor().equals(entryMinor) && ! getMinor().equals(WILDCARD) && ! entryMinor.equals(WILDCARD))
            return false;
        
        String entryMicro = otherVersion.getMicro();
        
        if (! getMicro().equals(entryMicro) && ! getMicro().equals(WILDCARD) && ! entryMicro.equals(WILDCARD))
            return false;
        
        /*
         *  Either this version or entry version contain sufficient wildcards
         *  to be considered a match
         */
        return true;
    }
    
    @Override
    public boolean isSevenServer() {
        return ITeiidServerVersion.SEVEN.equals(getMajor());
    }

    @Override
    public ITeiidServerVersion getMinimumVersion() {
        if (! this.hasWildCards())
            return this;

        String major = getMajor().equals(WILDCARD) ? SEVEN : getMajor();
        String minor = getMinor().equals(WILDCARD) ? ZERO : getMinor();
        String micro = getMicro().equals(WILDCARD) ? ZERO : getMicro();

        return new TeiidServerVersion(major, minor, micro);
    }

    @Override
    public ITeiidServerVersion getMaximumVersion() {
        if (! this.hasWildCards())
            return this;

        String major = getMajor().equals(WILDCARD) ? NINE : getMajor();
        String minor = getMinor().equals(WILDCARD) ? NINE : getMinor();
        String micro = getMicro().equals(WILDCARD) ? NINE : getMicro();

        return new TeiidServerVersion(major, minor, micro);
    }

    @Override
    public boolean isGreaterThan(ITeiidServerVersion otherVersion) {
        ITeiidServerVersion myMinVersion = getMinimumVersion();
        ITeiidServerVersion otherMaxVersion = otherVersion.getMaximumVersion();

        int majCompResult = isOtherNumberGreaterThan(myMinVersion.getMajor(), otherMaxVersion.getMajor());
        if (majCompResult > 0)
            return true;
        
        int minCompResult = isOtherNumberGreaterThan(myMinVersion.getMinor(), otherMaxVersion.getMinor());
        if (majCompResult == 0 && minCompResult > 0)
            return true;

        int micCompResult = isOtherNumberGreaterThan(myMinVersion.getMicro(), otherMaxVersion.getMicro());
        if (majCompResult == 0 && minCompResult == 0 && micCompResult > 0)
            return true;

        return false;
    }

    @Override
    public boolean isGreaterThan(Version otherVersion) {
        return isGreaterThan(otherVersion.get());
    }

    @Override
    public boolean isLessThan(ITeiidServerVersion otherVersion) {
        ITeiidServerVersion myMaxVersion = getMaximumVersion();
        ITeiidServerVersion otherMinVersion = otherVersion.getMinimumVersion();

        int majCompResult;
        try {
            int myMax = Integer.parseInt(myMaxVersion.getMajor());
            int otherMin = Integer.parseInt(otherMinVersion.getMajor());
            majCompResult = Integer.valueOf(myMax).compareTo(Integer.valueOf(otherMin));

        } catch (NumberFormatException ex) {
            // One or other is a string so compare lexographically
            majCompResult = myMaxVersion.getMajor().compareTo(otherMinVersion.getMajor());
        }

        if (majCompResult < 0)
            return true;

        int minCompResult;
        try {
            int myMax = Integer.parseInt(myMaxVersion.getMinor());
            int otherMin = Integer.parseInt(otherMinVersion.getMinor());
            minCompResult = Integer.valueOf(myMax).compareTo(Integer.valueOf(otherMin));
        } catch (NumberFormatException ex) {
            // One or other is a string so compare lexographically
            minCompResult = myMaxVersion.getMinor().compareTo(otherMinVersion.getMinor());
        }

        if (majCompResult == 0 && minCompResult < 0)
            return true;

        int micCompResult;
        try {
            int myMax = Integer.parseInt(myMaxVersion.getMicro());
            int otherMin = Integer.parseInt(otherMinVersion.getMicro());
            micCompResult = Integer.valueOf(myMax).compareTo(Integer.valueOf(otherMin));
        } catch (NumberFormatException ex) {
            // One or other is a string so compare lexographically
            micCompResult = myMaxVersion.getMicro().compareTo(otherMinVersion.getMicro());
        }

        if (majCompResult == 0 && minCompResult == 0 && micCompResult < 0)
            return true;
            
        return false;
    }

    @Override
    public boolean isLessThan(Version otherVersion) {
        return isLessThan(otherVersion.get());
    }

    @Override
    public boolean isGreaterThanOrEqualTo(ITeiidServerVersion otherVersion) {
        return this.compareTo(otherVersion) || this.isGreaterThan(otherVersion);
    }

    @Override
    public boolean isGreaterThanOrEqualTo(Version otherVersion) {
        return isGreaterThanOrEqualTo(otherVersion.get());
    }

    @Override
    public boolean isLessThanOrEqualTo(ITeiidServerVersion otherVersion) {
        return this.compareTo(otherVersion) || this.isLessThan(otherVersion);
    }

    @Override
    public boolean isLessThanOrEqualTo(Version otherVersion) {
        return isLessThan(otherVersion.get());
    }

    private int isOtherNumberGreaterThan(String myNumber, String otherNumber) {
        int myValue = -1;
        int otherValue = -1;

        try {
            myValue = Integer.parseInt(myNumber);
        } catch (NumberFormatException e) {
            myValue = -1;
        }

        try {
            otherValue = Integer.parseInt(otherNumber);
        } catch (NumberFormatException e) {
            otherValue = -1;
        }

        if (myValue < 0 || otherValue < 0) {
            return myNumber.compareTo(otherNumber);
        } else {
            return myValue - otherValue;
        }
    }
}

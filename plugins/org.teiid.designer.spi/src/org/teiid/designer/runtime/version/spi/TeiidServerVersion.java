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
     * The default teiid 8 server version
     */
    public static final ITeiidServerVersion DEFAULT_TEIID_8_SERVER = new TeiidServerVersion(DEFAULT_TEIID_8_SERVER_ID);

    /**
     * The default teiid 7 server version
     */
    public static final ITeiidServerVersion DEFAULT_TEIID_7_SERVER = new TeiidServerVersion(DEFAULT_TEIID_7_SERVER_ID);
    
    /**
     * The teiid 8.6 server version - this version introduced an Admin API change
     */
    public static final ITeiidServerVersion TEIID_8_6_SERVER = new TeiidServerVersion(TEIID_8_6_SERVER_ID);

    /**
     * The default preferred server
     */
    public static final ITeiidServerVersion DEFAULT_TEIID_SERVER = DEFAULT_TEIID_8_SERVER;

    /**
     * Collection of the default teiid instance version identifiers
     */
    public static Collection<String> DEFAULT_TEIID_SERVER_IDS = null;
    
    static {
        List<String> ids = new ArrayList<String>();
        ids.add(DEFAULT_TEIID_7_SERVER_ID);
        ids.add(DEFAULT_TEIID_8_SERVER_ID);
        DEFAULT_TEIID_SERVER_IDS = Collections.unmodifiableCollection(ids);
    }

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
     * @param versionString
     */
    public TeiidServerVersion(String versionString) {
        this.versionString = versionString;

        String[] tokens = versionString.split("\\."); //$NON-NLS-1$

        if (tokens.length >= 3) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
            microVersion = tokens[2];
        }
        else if(tokens.length == 2) {
            majorVersion = tokens[0];
            minorVersion = tokens[1];
        }
        else {
            majorVersion = tokens[0];
        }
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
        ITeiidServerVersion lastTestedDefault = TeiidServerVersion.DEFAULT_TEIID_SERVER;

        Collection<ITeiidServerVersion> serverVersions = null;
        try {
            serverVersions = TeiidRuntimeRegistry.getInstance().getRegisteredServerVersions();
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

    @Override
    public boolean compareTo(ITeiidServerVersion otherVersion) {
        if (! getMajor().equals(otherVersion.getMajor()))
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
    public boolean isGreaterThan(ITeiidServerVersion otherVersion) {
        try {
            int myMajor = Integer.parseInt(getMajor());
            int otherMajor = Integer.parseInt(otherVersion.getMajor());
            if (myMajor > otherMajor)
                return true;

            if (getMinor().equals(WILDCARD) || otherVersion.getMinor().equals(WILDCARD))
                return false;

            int myMinor = Integer.parseInt(getMinor());
            int otherMinor = Integer.parseInt(otherVersion.getMinor());
            if (((myMajor * 100) + (myMinor * 10)) > ((otherMajor * 100) + (otherMinor * 10)))
                return true;

            if (getMicro().equals(WILDCARD) || otherVersion.getMicro().equals(WILDCARD))
                return false;

            int myMicro = Integer.parseInt(getMicro());
            int otherMicro = Integer.parseInt(otherVersion.getMicro());
            if (((myMajor * 100) + (myMinor * 10) + myMicro) > ((otherMajor * 100) + (otherMinor * 10) + otherMicro))
                return true;
        }
        catch (Exception ex) {
            DesignerSPIPlugin.log(ex);
        }

        return false;
    }

    @Override
    public boolean isLessThan(ITeiidServerVersion otherVersion) {
        try {
            int myMajor = Integer.parseInt(getMajor());
            int otherMajor = Integer.parseInt(otherVersion.getMajor());
            if (myMajor < otherMajor)
                return true;

            if (getMinor().equals(WILDCARD) || otherVersion.getMinor().equals(WILDCARD))
                return false;

            int myMinor = Integer.parseInt(getMinor());
            int otherMinor = Integer.parseInt(otherVersion.getMinor());
            if (((myMajor * 100) + (myMinor * 10)) < ((otherMajor * 100) + (otherMinor * 10)))
                return true;

            if (getMicro().equals(WILDCARD) || otherVersion.getMicro().equals(WILDCARD))
                return false;

            int myMicro = Integer.parseInt(getMicro());
            int otherMicro = Integer.parseInt(otherVersion.getMicro());
            if (((myMajor * 100) + (myMinor * 10) + myMicro) < ((otherMajor * 100) + (otherMinor * 10) + otherMicro))
                return true;
        }
        catch (Exception ex) {
            DesignerSPIPlugin.log(ex);
        }

        return false;
    }
}

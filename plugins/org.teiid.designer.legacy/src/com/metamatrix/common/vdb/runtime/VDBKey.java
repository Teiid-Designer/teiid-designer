/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.vdb.runtime;

import org.teiid.core.util.HashCodeUtil;

public class VDBKey {

    private String name;
    private String version;

    public VDBKey( String name,
                   String version ) {
        this.name = name.toUpperCase();
        if (version != null) {
            this.version = version.toUpperCase();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return HashCodeUtil.hashCode(name.hashCode(), version);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals( Object obj ) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof VDBKey)) {
            return false;
        }

        VDBKey other = (VDBKey)obj;

        if (!other.name.equals(this.name)) {
            return false;
        }

        if (this.version != null) {
            if (!this.version.equals(other.version)) {
                return false;
            }
        } else if (other.version != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return name + " " + version; //$NON-NLS-1$
    }

}

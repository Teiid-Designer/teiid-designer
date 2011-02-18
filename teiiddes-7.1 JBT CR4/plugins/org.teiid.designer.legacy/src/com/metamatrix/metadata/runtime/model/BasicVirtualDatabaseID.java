/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.model;

import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.metadata.runtime.RuntimeMetadataPlugin;
import com.metamatrix.metadata.runtime.api.VirtualDatabaseID;
import com.metamatrix.metadata.util.ErrorMessageKeys;

public class BasicVirtualDatabaseID extends BasicMetadataID implements VirtualDatabaseID {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String version;

/**
 * Call constructor to instantiate a VirtualDatabaseID object for the fully qualified Virtual Database name, version and an internal unique identifier.
 */
    public BasicVirtualDatabaseID(String fullName, String versionName, long internalUniqueID) {
        super(fullName, internalUniqueID);
        this.version = versionName;
        updateHashCode();
    }

/**
 * Call constructor to instantiate a VirtualDatabaseID object for the fully qualified Virtual Database name and version.
 */
    public BasicVirtualDatabaseID(String fullName, String versionName) {
        super(fullName);
        this.version = versionName;
        updateHashCode();
    }
/**
 * returns the version.
 * @return String
 */
    public String getVersion() {
	    return version;
    }

    public void setVersion(String version){
        this.version = version;
        updateHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if ( this == obj ) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        //if ( this.getClass().isInstance(obj) ) {
        if ( obj instanceof BasicVirtualDatabaseID ) {

        	// Do quick hash code check first
        	if( this.hashCode() != obj.hashCode() ) {
        		return false;
		      }

            // If the types aren't the same, then fail
            BasicVirtualDatabaseID that = (BasicVirtualDatabaseID) obj;
            if ( this.getClass() != that.getClass() ) {
                return false;
            }

            return (this.getFullName() + version).equalsIgnoreCase( that.getFullName() + that.getVersion() );
        }

        // Otherwise not comparable ...
        return false;
    }

    @Override
    public int compareTo(Object obj) {
        BasicVirtualDatabaseID that = (BasicVirtualDatabaseID) obj;     // May throw ClassCastException
        if ( obj == null ) {
            throw new TeiidRuntimeException(ErrorMessageKeys.GEN_0005, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.GEN_0005));
        }

        int diff = this.hashCode() - that.hashCode();
        if ( diff != 0 ) {
            return diff;
        }

        if ( this.getClass() != that.getClass() ) {
            diff = this.getClass().hashCode() - that.getClass().hashCode();
            return diff;
        }

        return (this.getFullName() + version).compareToIgnoreCase( that.getFullName() + that.getVersion());
    }

    @Override
    public int compareToByName(Object obj) {
        BasicVirtualDatabaseID that = (BasicVirtualDatabaseID) obj;     // May throw ClassCastException
        if ( obj == null ) {
            throw new TeiidRuntimeException(ErrorMessageKeys.GEN_0005, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.GEN_0005));
        }

        return (this.getFullName() + version).compareToIgnoreCase( that.getFullName() + that.getVersion());
    }

    @Override
    protected int computeHashCode() {
        return (this.getFullName() + version).toLowerCase().hashCode();
    }
}


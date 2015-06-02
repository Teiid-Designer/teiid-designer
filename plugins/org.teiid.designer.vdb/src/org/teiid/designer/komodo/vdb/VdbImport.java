/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

/**
 * Represents a referenced VDB.
 */
public class VdbImport extends VdbObject {
	int version;
	boolean importDataPolicies;
	
    /**
     * The type identifier.
     */
    int TYPE_ID = VdbImport.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_IMPORT;

    /**
     * The default value indicating if the data policies should be imported. Value is {@value} .
     */
    boolean DEFAULT_IMPORT_DATA_POLICIES = true;

    /**
     * An empty array of VDB imports.
     */
    VdbImport[] NO_IMPORTS = new VdbImport[0];

    /**
     * @param vdbName
     * @param importDataPolicies
     * @param version
     */
    public VdbImport(String vdbName, boolean importDataPolicies, int version) {
    	super();
    	setName(vdbName);
    	this.importDataPolicies = importDataPolicies;
    	this.version = version;
    }
    
    /**
     * @return the value of the <code>version</code> property

     * @see Vdb#DEFAULT_VERSION
     */
    public int getVersion() {
    	return this.version;
    }

    /**
     * @return <code>true</code> if data policies should be imported
     * @see #DEFAULT_IMPORT_DATA_POLICIES
     */
    public boolean isImportDataPolicies( ) {
    	return this.importDataPolicies;
    }

    /**
     * @param newImportDataPolicies
     *        the new value for the <code>import data policies</code> property
     * @see #DEFAULT_IMPORT_DATA_POLICIES
     */
    public void setImportDataPolicies( final boolean newImportDataPolicies ) {
    	setChanged(this.importDataPolicies, newImportDataPolicies);
    	this.importDataPolicies = newImportDataPolicies;
    }

    /**
     * @param newVersion
     *        the new value of the <code>version</code> property
     * @see Vdb#DEFAULT_VERSION
     */
    public void setVersion(final int newVersion ) {
    	setChanged(this.version, newVersion);
    	this.version = newVersion;
    }

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.util.concurrent.atomic.AtomicReference;

import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.manifest.ImportVdbElement;

/**
*
*
* @since 8.0
*/
public class VdbImportVdbEntry {
	
	private final Vdb vdb;
	
	private String name;
	
	final AtomicReference<Integer> version = new AtomicReference<Integer>();
	
	final AtomicReference<Boolean> importDataPolicies = new AtomicReference<Boolean>();
	
	/**
	 * @param vdb
	 * @param importVdbName
	 */
	public VdbImportVdbEntry(Vdb vdb, String importVdbName) {
		this.vdb = vdb;
        this.name = importVdbName;
        this.version.set(1);
        this.importDataPolicies.set(false);
	}
	
	/**
	 * @param vdb
	 *	@param element
	 */
	public VdbImportVdbEntry(Vdb vdb, ImportVdbElement element) {
		this(vdb, element.getName());
        this.version.set(element.getVersion());    
        this.importDataPolicies.set(element.isImportDataPolicies());
	}
	
	/**
	 * @return return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return return the version
	 */
	public int getVersion() {
		return version.get();
	}
	
	/**
	 * Modify the version attribute
	 * 
	 * @param version
	 */
	public void setVersion(int version) {
		final int oldVersion = this.version.get();
        if (version == oldVersion) return;
        this.version.set(version);
		vdb.setModified(this, Event.IMPORT_VDB_ENTRY_VERSION, oldVersion, version);
	}

	/**
	 * @return the import data policies flag
	 */
	public boolean isImportDataPolicies() {
		return importDataPolicies.get();
	}
	
	/**
	 * Modify the import data policies flag
	 * 
	 * @param importDataPolicies
	 */
	public void setImportDataPolicies(boolean importDataPolicies) {
		final boolean oldImportDataPolicies = this.importDataPolicies.get();
        if (importDataPolicies == oldImportDataPolicies) return;
        this.importDataPolicies.set(importDataPolicies);
		vdb.setModified(this, Event.IMPORT_VDB_ENTRY_DATA_POLICY, oldImportDataPolicies, importDataPolicies);
	}

	@SuppressWarnings("javadoc")
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.vdb == null) ? 0 : this.vdb.hashCode());
		return result;
	}

	@SuppressWarnings("javadoc")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VdbImportVdbEntry other = (VdbImportVdbEntry) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.vdb == null) {
			if (other.vdb != null)
				return false;
		} else if (!this.vdb.equals(other.vdb))
			return false;
		return true;
	}
}

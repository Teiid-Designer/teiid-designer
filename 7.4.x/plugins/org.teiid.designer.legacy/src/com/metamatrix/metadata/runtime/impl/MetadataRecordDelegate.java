/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.index.IndexConstants;

public class MetadataRecordDelegate {

    private String uuid;
    private String parentUUID;
    private String nameInSource;
    private String fullName;
	private String name;
	
	public String getUUID() {
		return uuid;
	}
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	public String getParentUUID() {
		return parentUUID;
	}
	public void setParentUUID(String parentUUID) {
		this.parentUUID = parentUUID;
	}
	public String getNameInSource() {
		return nameInSource;
	}
	public void setNameInSource(String nameInSource) {
		this.nameInSource = nameInSource;
	}
	public String getFullName() {
        return this.fullName == null ? this.name : this.fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getName() {
    	if(this.name == null || this.name.trim().length() == 0) {
			int nmIdx = this.fullName != null ? this.fullName.lastIndexOf(IndexConstants.NAME_DELIM_CHAR) : -1;
			if (nmIdx == -1) {
				this.name = this.fullName;
			} else {
				this.name = this.fullName != null ? this.fullName.substring(nmIdx+1) : null;
			}
    	}
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
}

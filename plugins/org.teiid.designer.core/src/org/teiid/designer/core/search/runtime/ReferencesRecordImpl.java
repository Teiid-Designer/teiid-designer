/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

import org.teiid.designer.core.index.IndexConstants;


/**
 * ReferencesRecordImpl
 *
 * @since 8.0
 */
public class ReferencesRecordImpl extends AbstractSearchRecord implements ReferencesRecord {
	
	private String referencedUUID;

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.ReferencesRecord#getReferencedUUIDs()
	 */
	@Override
	public String getReferencedUUID() {
		return this.referencedUUID;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.SearchRecord#getRecordType()
	 */
	@Override
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.OBJECT_REF;
	}

	/**
	 * @param collection
	 */
	public void setReferencedUUID(String refUUID) {
	    referencedUUID = refUUID;
	}

}

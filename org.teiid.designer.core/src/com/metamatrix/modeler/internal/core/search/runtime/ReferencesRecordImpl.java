/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.ReferencesRecord;

/**
 * ReferencesRecordImpl
 */
public class ReferencesRecordImpl extends AbstractSearchRecord implements ReferencesRecord {
	
	private String referencedUUID;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ReferencesRecord#getReferencedUUIDs()
	 */
	public String getReferencedUUID() {
		return this.referencedUUID;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
	 */
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

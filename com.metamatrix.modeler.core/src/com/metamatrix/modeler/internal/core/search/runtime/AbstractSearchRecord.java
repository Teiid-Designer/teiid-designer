/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.search.runtime.SearchRecord;

/**
 * AbstractSearchRecord
 */
public abstract class AbstractSearchRecord implements SearchRecord {

	private String uuid;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.SearchRecord#getUUID()
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * @param string
	 */
	public void setUUID(String string) {
		uuid = string;
	}
}

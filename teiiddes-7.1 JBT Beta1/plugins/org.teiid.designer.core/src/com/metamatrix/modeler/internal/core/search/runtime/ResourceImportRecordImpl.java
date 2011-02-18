/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;

/**
 * ResourceImportRecordImpl
 */
public class ResourceImportRecordImpl extends AbstractSearchRecord implements ResourceImportRecord {

	private String path;

	// path to the imported resource
	private String importedPath;	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceImportRecord#getPath()
	 */
	public String getPath() {
		return this.path;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceImportRecord#getImportedPath()
	 */
	public String getImportedPath() {
		return this.importedPath;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.MODEL_IMPORT;
	}

	/**
	 * @param string
	 */
	public void setImportedPath(String string) {
		importedPath = string;
	}

	/**
	 * @param string
	 */
	public void setPath(String string) {
		path = string;
	}

}

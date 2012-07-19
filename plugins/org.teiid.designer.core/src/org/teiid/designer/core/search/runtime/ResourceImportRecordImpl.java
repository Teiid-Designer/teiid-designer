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
 * ResourceImportRecordImpl
 *
 * @since 8.0
 */
public class ResourceImportRecordImpl extends AbstractSearchRecord implements ResourceImportRecord {

	private String path;

	// path to the imported resource
	private String importedPath;	

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.ResourceImportRecord#getPath()
	 */
	@Override
	public String getPath() {
		return this.path;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.ResourceImportRecord#getImportedPath()
	 */
	@Override
	public String getImportedPath() {
		return this.importedPath;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.SearchRecord#getRecordType()
	 */
	@Override
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

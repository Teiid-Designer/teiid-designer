/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.ResourceRecord;

/**
 * ResourceRecordImpl
 */
public class ResourceRecordImpl extends AbstractSearchRecord implements ResourceRecord {

	private String path;
	// uuid of the imported resource	
	private String URI;
	// path to the imported resource
	private String metamodelURI;	

	private String modelType;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceRecord#getPath()
	 */
	public String getPath() {
		return this.path;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceRecord#getURI()
	 */
	public String getURI() {
		return this.URI;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceRecord#getMetamodelURI()
	 */
	public String getMetamodelURI() {
		return this.metamodelURI;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceRecord#getModelType()
	 */
	public String getModelType() {
		return this.modelType;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.RESOURCE;
	}

	/**
	 * @param string
	 */
	public void setMetamodelURI(String string) {
		metamodelURI = string;
	}

	/**
	 * @param string
	 */
	public void setModelType(String string) {
		modelType = string;
	}

	/**
	 * @param string
	 */
	public void setPath(String string) {
		path = string;
	}

	/**
	 * @param string
	 */
	public void setURI(String string) {
		URI = string;
	}

}

/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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

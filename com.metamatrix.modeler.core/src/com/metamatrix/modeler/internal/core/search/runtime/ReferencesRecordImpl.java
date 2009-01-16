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

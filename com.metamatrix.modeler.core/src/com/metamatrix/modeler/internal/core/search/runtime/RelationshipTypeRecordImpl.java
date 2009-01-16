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
import com.metamatrix.modeler.core.search.runtime.RelationshipTypeRecord;

/**
 * RelationshipTypeRecordImpl
 */
public class RelationshipTypeRecordImpl extends AbstractRelationshipRecord implements RelationshipTypeRecord {

	// names
	private String sourceRoleName;
	private String targetRoleName;
	private String superTypeUUID;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.RelationshipTypeRecord#getSourceRoleName()
	 */
	public String getSourceRoleName() {
		return this.sourceRoleName;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.RelationshipTypeRecord#getSuperTypeName()
	 */
	public String getSuperTypeUUID() {
		return this.superTypeUUID;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.RelationshipTypeRecord#getTargetRoleName()
	 */
	public String getTargetRoleName() {
		return this.targetRoleName;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE;
	}

	/**
	 * @param string
	 */
	public void setSourceRoleName(String string) {
		sourceRoleName = string;
	}

	/**
	 * @param string
	 */
	public void setSuperTypeUUID(String string) {
		superTypeUUID = string;
	}

	/**
	 * @param string
	 */
	public void setTargetRoleName(String string) {
		targetRoleName = string;
	}

}

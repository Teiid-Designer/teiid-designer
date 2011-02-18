/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

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
import com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord;

/**
 * RelatedObjectRecordImpl.java
 */
public class RelatedObjectRecordImpl extends AbstractRelationshipRecord implements RelatedObjectRecord {

	// uuids
	private String relationshipUUID;
	private String relatedObjUUID;
	// uris	
	private String relatedObjectUri;
	private String metaClassUri;
	private String relatedMetaClassUri;
	// names
	private String roleName;
	private String relatedRoleName;
	private String relatedObjectName;
	// paths
	private String resourcePath;
	private String relatedResourcePath;
	
	// this record is for the source of the relationship
	private boolean isSourceObject;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getRelationshipUUID()
	 */
	public String getRelationshipUUID() {
		return relationshipUUID;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#isSourceObject()
	 */
	public boolean isSourceObject() {
		return isSourceObject;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT;
	}

	/**
	 * @param b
	 */
	public void setSourceObject(boolean b) {
		isSourceObject = b;
	}

	/**
	 * @param string
	 */
	public void setRelationshipUUID(String string) {
		relationshipUUID = string;
	}



	/**
	 * @param string
	 */
	public void setRelatedObjectUri(String string) {
		relatedObjectUri = string;
	}

	/**
	 * @param string
	 */
	public void setRelatedObjectUUID(String string) {
		relatedObjUUID = string;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getOpossiteObjectUUID()
	 */
	public String getRelatedObjectUUID() {
		return this.relatedObjUUID;
	}



	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getRelatedObjectUri()
	 */
	public String getRelatedObjectUri() {
		return this.relatedObjectUri;
	}

	/**
	 * @return
	 */
	public String getMetaClassUri() {
		return metaClassUri;
	}

	/**
	 * @return
	 */
	public String getRelatedMetaClassUri() {
		return relatedMetaClassUri;
	}

	/**
	 * @param string
	 */
	public void setMetaClassUri(String string) {
		metaClassUri = string;
	}

	/**
	 * @param string
	 */
	public void setRelatedMetaClassUri(String string) {
		relatedMetaClassUri = string;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getRelatedRoleName()
	 */
	public String getRelatedRoleName() {
		return this.relatedRoleName;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord#getRoleName()
	 */
	public String getRoleName() {
		return this.roleName;
	}

	/**
	 * @param string
	 */
	public void setRelatedRoleName(String string) {
		relatedRoleName = string;
	}

	/**
	 * @param string
	 */
	public void setRoleName(String string) {
		roleName = string;
	}



	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.RelatedObjectRecord#getRelatedObjectName()
	 */
	public String getRelatedObjectName() {
		return this.relatedObjectName;
	}



	/**
	 * @param string
	 */
	public void setRelatedObjectName(String string) {
		relatedObjectName = string;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord#getRelatedResourcePath()
	 */
	public String getRelatedResourcePath() {
		return this.relatedResourcePath;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord#getResourcePath()
	 */
	public String getResourcePath() {
		return this.resourcePath;
	}

	/**
	 * @param string
	 */
	public void setRelatedResourcePath(String string) {
		relatedResourcePath = string;
	}

	/**
	 * @param string
	 */
	public void setResourcePath(String string) {
		resourcePath = string;
	}

}

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
 * RelatedObjectRecordImpl.java
 *
 * @since 8.0
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
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#getRelationshipUUID()
	 */
	@Override
	public String getRelationshipUUID() {
		return relationshipUUID;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#isSourceObject()
	 */
	@Override
	public boolean isSourceObject() {
		return isSourceObject;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.SearchRecord#getRecordType()
	 */
	@Override
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
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#getOpossiteObjectUUID()
	 */
	@Override
	public String getRelatedObjectUUID() {
		return this.relatedObjUUID;
	}



	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#getRelatedObjectUri()
	 */
	@Override
	public String getRelatedObjectUri() {
		return this.relatedObjectUri;
	}

	/**
	 * @return
	 */
	@Override
	public String getMetaClassUri() {
		return metaClassUri;
	}

	/**
	 * @return
	 */
	@Override
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
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#getRelatedRoleName()
	 */
	@Override
	public String getRelatedRoleName() {
		return this.relatedRoleName;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.RelatedObjectRecord#getRoleName()
	 */
	@Override
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
	 * @See org.teiid.designer.core.relationship.search.index.RelatedObjectRecord#getRelatedObjectName()
	 */
	@Override
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
	 * @See org.teiid.designer.core.search.runtime.RelatedObjectRecord#getRelatedResourcePath()
	 */
	@Override
	public String getRelatedResourcePath() {
		return this.relatedResourcePath;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.search.runtime.RelatedObjectRecord#getResourcePath()
	 */
	@Override
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

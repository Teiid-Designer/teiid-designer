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
 * RelationshipRecordImpl.java
 */
public class RelationshipRecordImpl extends AbstractRelationshipRecord implements RelationshipRecord {

	private String typeUUID;

	private String typeName;
	private String resourcePath;

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.RelationshipRecord#getTypeUUID()
	 */
	@Override
	public String getTypeUUID() {
		return this.typeUUID;
	}

	/**
	 * @param string
	 */
	public void setTypeUUID(String string) {
		typeUUID = string;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.search.index.SearchRecord#getRecordType()
	 */
	@Override
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.search.index.RelationshipRecord#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * @param string
	 */
	public void setTypeName(String string) {
		typeName = string;
	}
	
    /** 
     * @param resourcePath The resourcePath to set.
     * @since 4.2
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /** 
     * @see org.teiid.designer.core.search.runtime.RelationshipRecord#getResourcePath()
     * @since 4.2
     */
    @Override
	public String getResourcePath() {
        return this.resourcePath;
    }
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;

/**
 * RelationshipRecordImpl.java
 */
public class RelationshipRecordImpl extends AbstractRelationshipRecord implements RelationshipRecord {

	private String typeUUID;

	private String typeName;
	private String resourcePath;

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.search.index.RelationshipRecord#getTypeUUID()
	 */
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
	 * @see com.metamatrix.modeler.relationship.search.index.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.search.index.RelationshipRecord#getTypeName()
	 */
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
     * @see com.metamatrix.modeler.core.search.runtime.RelationshipRecord#getResourcePath()
     * @since 4.2
     */
    public String getResourcePath() {
        return this.resourcePath;
    }
}

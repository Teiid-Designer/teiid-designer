/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

import java.util.Collection;
import java.util.List;

/**
 * <p>Instances of this interface 
 * represent Groups in a Model.  The values of a
 *  Group are analogous to a Table in a database.</p>
 */
public interface Group extends MetadataObject {

	/**
	 * Return the path to the group.
	 * @return String 
	 */
    String getPath();
	/**
	 * Returns the name-in-soure for this group.
	 * @return String is the name in source 
	 */
    String getNameInSource();
    /**
	 * Returns whether the name-in-soure is defined for this element.
	 * @return true if this element has the name in source; false otherwise.
	 */
    boolean hasNameInSource();
	/**
	 * Return the group description.
	 * @return String 
	 */    
    String getDescription();
	/**
	 * Return the alias.
	 *  @return String alias
	 */
    String getAlias();
	/**
	 * Returns an ordered list of ElementID's for this group.
	 * @return List of ElementID's
	 */
    List getElementIDs();
	/**
	 * Returns the KeyID's identified for this group.
	 * @return Collection of KeyID's
	 */
    Collection getKeyIDs();
	/**
	 * Return boolean indicating if this a physical model.
	 * @return boolean true if is is a physical model
	 */
    boolean isPhysical();
	/**
	 * Return the query plan for the group if it is a virtual group.
	 * @return String 
	 */
    String getQueryPlan();
	/**
	 * Return the UPDATE query plan for the group if it is a virtual group.
	 * @return String 
	 */
    String getUpdateQueryPlan();
	/**
	 * Return the INSERT query plan for the group if it is a virtual group.
	 * @return String 
	 */
    String getInsertQueryPlan();
	/**
	 * Return the DELETE query plan for the group if it is a virtual group.
	 * @return String 
	 */
    String getDeleteQueryPlan();
    /**
     * Returns the deleteAllowed.
     * @return boolean
     */
    boolean isDeleteAllowed();
    /**
     * Returns the insertAllowed.
     * @return boolean
     */
    boolean isInsertAllowed();
    /**
     * Returns the updateAllowed.
     * @return boolean
     */
    boolean isUpdateAllowed();
	/**
	 * @return boolean true indicates this is a RuntimeMetadata System table 
	 */
    boolean isSystemTable();
	/**
	 * @return short indicating table type (i.e., TABLE, SYSTEM, VIEW)
	 *
	 * @see com.metamatrix.metadata.runtime.api.MetadataConstants.TABLE_TYPES
	 */
    short getTableType();
	/**
	 * Return boolean indicating if the model supports UPDATE operations in the sql.
	 * @return boolean 
	 */
    boolean supportsUpdate();
    /**
	 * Return the mapping document if this group represents a virtual document.
	 * @return the mapping document string.
	 */
    String getMappingDocument();
    /**
     * Whether this group represents a virtual document.
     * @return true if this group represents a virtual document. False otherwise.
     */
    boolean isVirtualDocument();
    /**
     * If this group represents a virtual document, 
     * return a Collection of string representations of XML Schemas 
     * that are referenced by it.
     * @return Collection of String
     */
    Collection getXMLSchemas();
}


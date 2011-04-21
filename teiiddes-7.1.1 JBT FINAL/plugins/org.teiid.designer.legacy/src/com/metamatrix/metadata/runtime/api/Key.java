/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

import java.util.List;

/**
 * <p>Instances of this interface represent Keys for a Group.  The values of a Key are analogous to a Primary Key, Unique Key or Foreign Key in a database.</p> 
 */
public interface Key extends MetadataObject {
/**
 * Return the description.
 * @return String 
 */
    String getDescription();
/**
 * Return the alias.
 *  @return String alias
 */
    String getAlias();
/**
 * Returns an ordered list of ElementID's this key is made of.
 * @return List of ElementID's that make up the key
 */
    List getElementIDs();
/**
 * Return boolean indicating if the key is the primary key.
 * @return boolean 
 */
    boolean isPrimaryKey();
/**
 * Return boolean indicating if this key is a foreign key.
 * @return boolean 
 */
    boolean isForeignKey();
/**
 * Return boolean indicating if the key is a unique key.
 * @return boolean 
 */
    boolean isUniqueKey();
/**
 * Return boolean indicating if this key is indexed.
 * @return boolean 
 */
    boolean isIndexed();
    
/**
 * Return boolean indicating if this is an access pattern.
 * @return boolean 
 */
    boolean isAccessPattern();
    
/**
 * Return short indicating the type of key.
 * @return short
 *
 * @see com.metamatrix.metadata.runtime.api.MetadataConstants.KEY_TYPES
 */
    short getKeyType();
/**
 * Return short indicating the type of reference key matching.
 * @return short
 * 
 * @see com.metamatrix.metadata.runtime.api.MetadataConstants.MATCH_TYPES
 */
    short getMatchType();
    MetadataID getReferencedKey();
/**
 * Return the path to the key.
 * @return String 
 */
    String getPath();
}


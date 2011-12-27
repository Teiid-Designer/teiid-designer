/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;



/**
 * The KeyID is the unique identifier for a Key. 
 */
public interface KeyID extends MetadataID {
/**
 * Return the group name.
 * @return GroupID is the group the key is contained in
 */
    GroupID getGroupID();
/**
 * Return the modelID this key is a part of.
 * @return ModelID is the model the key is contained in
 */
    ModelID getModelID();
}


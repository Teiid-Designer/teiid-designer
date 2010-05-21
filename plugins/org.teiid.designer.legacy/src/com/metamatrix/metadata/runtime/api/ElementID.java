/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;



/**
 * The ElementID is the uniue identifier for an Element. 
 */
public interface ElementID extends MetadataID {
/**
 * Return the group name.
 * @return GroupID is the group the element is contained in 
 */
    GroupID getGroupID();
/**
 * Return the modelID that this element is part of.
 * @return ModelID is the model the element is contained in 
 */
    ModelID getModelID();
}


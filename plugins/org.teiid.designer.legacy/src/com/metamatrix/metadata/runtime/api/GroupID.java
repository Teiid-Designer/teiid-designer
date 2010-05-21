/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;



/**
 * The GroupID is the unique identifier for a Group. 
 */
public interface GroupID extends MetadataID {
/**
 * Return the modelID that this group is part of.
 * @return ModelID is the model the group is contained in
 */
    ModelID getModelID();
}


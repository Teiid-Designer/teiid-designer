/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;



/**
 * The ProcedureID is the unique identifier for a Procedure. 
 */
public interface ProcedureID extends MetadataID {
/**
 * Return the modelID that this procedure is part of.
 * @return ModelID is the model the procedure is contained in
 */
    ModelID getModelID();
}


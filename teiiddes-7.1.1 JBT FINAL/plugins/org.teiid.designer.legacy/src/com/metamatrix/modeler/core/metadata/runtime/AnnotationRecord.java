/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;


/**
 * ModelRecord
 */
public interface AnnotationRecord extends MetadataRecord {

    /**
     * Return the description
     * @return description
     */
    String getDescription();
}
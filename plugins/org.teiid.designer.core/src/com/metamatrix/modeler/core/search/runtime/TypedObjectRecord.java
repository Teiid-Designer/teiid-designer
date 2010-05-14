/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.runtime;

/**
 * TypedObjectRecord
 * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
 */
public interface TypedObjectRecord extends ResourceObjectRecord {
    
    String getDatatypeName();
    
    String getDatatypeID();
    
    String getRuntimeType();
    
}

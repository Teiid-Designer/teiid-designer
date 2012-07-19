/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

/**
 * TypedObjectRecord
 * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
 *
 * @since 8.0
 */
public interface TypedObjectRecord extends ResourceObjectRecord {
    
    String getDatatypeName();
    
    String getDatatypeID();
    
    String getRuntimeType();
    
}

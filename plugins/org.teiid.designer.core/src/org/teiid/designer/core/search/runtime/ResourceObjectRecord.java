/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

/**
 * ResourceObjectRecord
 *
 * @since 8.0
 */
public interface ResourceObjectRecord extends SearchRecord {
    
    String getName();
    
    String getFullname();
    
    String getObjectURI();
	
	String getMetaclassURI();
	
	String getResourcePath();
}

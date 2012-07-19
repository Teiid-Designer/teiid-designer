/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

/**
 * ResourceRecord
 *
 * @since 8.0
 */
public interface ResourceRecord extends SearchRecord {
	
	String getPath();

	String getURI();

	String getMetamodelURI();

	String getModelType();
}

/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.builder;

import java.util.Map;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.internal.builder.util.MetaClassUriHelper;

/**
 * Record object for creating MetaModelEnties for this builder framework 
 */
public class MetamodelEntityRecord {
	
	private String metaClassUri;
	private String parentPath;
	private String parentMetaClassUri;
	private Map featuresNameValueMap;
	
	// ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

	/**
	 * Default Constructor
	 */
	public MetamodelEntityRecord() {
	}
	
	/**
	 * Constructor
	 * @param uri the MetaClassURI for this record
	 * @param parentPath the path to the parent
	 * @param parentUri the MetaClassURI for the parent object
	 * @param featuresMap the feature name-value map for this object
	 */
	public MetamodelEntityRecord(String uri, String parentPath, String parentUri, Map featuresMap) {
		super();
		ArgCheck.isNotNull(uri);
		ArgCheck.isNotNull(parentPath);
		this.metaClassUri = uri;
		this.parentPath = parentPath;
		this.parentMetaClassUri = parentUri;
		this.featuresNameValueMap = featuresMap;
	}

	// ==================================================================================
    //                        M E T H O D S
    // ==================================================================================

	/**
	 * Get the features name-value Map
	 * @return the Map of name-values for the features
	 */
	public Map getFeaturesNameValueMap() {
		return featuresNameValueMap;
	}
	
	/**
	 * Set the features name-value Map
	 * @param featuresNameValueMap the name-value map for the features
	 */
	public void setFeaturesNameValueMap(Map featuresNameValueMap) {
		this.featuresNameValueMap = featuresNameValueMap;
	}
	
	/**
	 * Get the MetaClass URI
	 * @return the metaClass uri for this record.
	 */
	public String getMetaClassUri() {
		return metaClassUri;
	}
	
	/**
	 * Set the MetaClass URI
	 * @param metaClassUri the metaClass URI for this record
	 */
	public void setMetaClassUri(String metaClassUri) {
		ArgCheck.isNotNull(metaClassUri);
		this.metaClassUri = metaClassUri;
	}
	
	/**
	 * Get the MetaClass URI for the parent
	 * @return the parent's MetaClass URI
	 */
	public String getParentMetaClassUri() {
		return parentMetaClassUri;
	}
	
	/**
	 * Set the MetaClass URI for the parent
	 * @param parentMetaClassUri the parent's MetaClass URI
	 */
	public void setParentMetaClassUri(String parentMetaClassUri) {
		this.parentMetaClassUri = parentMetaClassUri;
	}
	
	/**
	 * Get the parent location path
	 * @return the path to this object's parent
	 */
	public String getParentPath() {
		return parentPath;
	}
	
	/**
	 * Set the parent location path
	 * @param parentPath the path to this object's parent
	 */
	public void setParentPath(String parentPath) {
		ArgCheck.isNotNull(parentPath);
		this.parentPath = parentPath;
	}
	
	/**
	 * Return the package portion of the MetaClass Uri for this record.
	 * @return the package Uri for this record's MetaClass Uri
	 */
	public String getPackageUri() {
		return MetaClassUriHelper.getPackageUri(this.metaClassUri);
	}
	
}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;

/**
 * ResourceObjectRecordImpl
 */
public class ResourceObjectRecordImpl extends AbstractSearchRecord implements ResourceObjectRecord {

	private String name;
    private String fullname;
	private String objectURI;
    private String metaclassURI;
	private String resourcePath;

	/**
	 * @see com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord#getMetaclassURI()
	 */
	public String getMetaclassURI() {
		return this.metaclassURI;
	}

	/**
	 * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
	 */
	public char getRecordType() {
		return IndexConstants.SEARCH_RECORD_TYPE.OBJECT;
	}

	/**
	 * @param string
	 */
	public void setMetaclassURI(String string) {
		metaclassURI = string;
	}

    /** 
     * @see com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord#getName()
     * @since 4.2
     */
    public String getName() {
        return this.name;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord#getFullname()
     * @since 4.2
     */
    public String getFullname() {
        return this.fullname;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord#getObjectURI()
     * @since 4.2
     */
    public String getObjectURI() {
        return this.objectURI;
    }
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord#getResourcePath()
     * @since 4.2
     */
    public String getResourcePath() {
        return this.resourcePath;
    }
    /** 
     * @param name The name to set.
     * @since 4.2
     */
    public void setName(String name) {
        this.name = name;
    }
    /** 
     * @param objectURI The objectURI to set.
     * @since 4.2
     */
    public void setObjectURI(String objectURI) {
        this.objectURI = objectURI;
    }
    /** 
     * @param resourcePath The resourcePath to set.
     * @since 4.2
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    
    /** 
     * @param fullname The fullname to set.
     * @since 4.2
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

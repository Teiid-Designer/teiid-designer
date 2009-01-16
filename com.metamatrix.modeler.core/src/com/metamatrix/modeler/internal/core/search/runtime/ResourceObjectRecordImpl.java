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

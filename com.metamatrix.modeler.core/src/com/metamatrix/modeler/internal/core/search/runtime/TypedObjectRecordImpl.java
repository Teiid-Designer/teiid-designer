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
import com.metamatrix.modeler.core.search.runtime.TypedObjectRecord;

/**
 * TypedObjectRecord
 * recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
 */
public class TypedObjectRecordImpl extends ResourceObjectRecordImpl implements TypedObjectRecord {
    
    private String datatypeName;
    private String datatypeID;
    private String runtimeType;    
    
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
     * @since 4.2
     */
    @Override
    public char getRecordType() {
        return IndexConstants.SEARCH_RECORD_TYPE.TYPED_OBJECT;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.TypedObjectRecord#getDatatypeID()
     * @since 4.2
     */
    public String getDatatypeID() {
        return this.datatypeID;
    }
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.TypedObjectRecord#getDatatypeName()
     * @since 4.2
     */
    public String getDatatypeName() {
        return this.datatypeName;
    }
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.TypedObjectRecord#getRuntimeType()
     * @since 4.2
     */
    public String getRuntimeType() {
        return this.runtimeType;
    }
    
    /** 
     * @param datatypeID The datatypeID to set.
     * @since 4.2
     */
    public void setDatatypeID(String datatypeID) {
        this.datatypeID = datatypeID;
    }
    
    /** 
     * @param datatypeName The datatypeName to set.
     * @since 4.2
     */
    public void setDatatypeName(String datatypeName) {
        this.datatypeName = datatypeName;
    }
    
    /** 
     * @param runtimeType The runtimeType to set.
     * @since 4.2
     */
    public void setRuntimeType(String runtimeType) {
        this.runtimeType = runtimeType;
    }
}

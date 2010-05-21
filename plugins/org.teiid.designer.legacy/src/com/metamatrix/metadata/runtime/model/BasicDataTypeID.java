/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.model;

import com.metamatrix.metadata.runtime.api.DataTypeID;
import com.metamatrix.metadata.runtime.api.ModelID;
public class BasicDataTypeID extends BasicMetadataID implements DataTypeID {
    /**
     */
    private static final long serialVersionUID = 1L;
    private ModelID modelID = null;
    private String dtUUID;

/**
 * Call constructor to instantiate a BasicDataTypeID object for the fully qualified name and an internal unique identifier.
 */
    public BasicDataTypeID(String fullName, long internalUniqueID) {
        super(fullName, internalUniqueID);
    }

/**
 * Call constructor to instantiate a BasicDataTypeID object for the fully qualified name.
 */
    protected BasicDataTypeID(String fullName){
        super(fullName);
    }
/**
 * return the modelID this key is a part of.
 * @return ModelID is the model the key is contained in
 */
    public ModelID getModelID() {
        return modelID;
    }

     public void setModelID(ModelID id){
        this.modelID = id;
    }
	
	/**
	 * Override parent method to compare UID
	 */
	@Override
    public boolean equals(Object obj) {
		boolean result = super.equals(obj);
		if(result){
			BasicMetadataID that = (BasicMetadataID) obj;
			return this.getUID() == that.getUID();	
		}
		return result;
	}
	
	/**
	 * Override parent method to compare UID
	 */
	@Override
    public int compareTo(Object obj) {
		int result = super.compareTo(obj);
		if(result == 0){
			BasicMetadataID that = (BasicMetadataID) obj;
			result = (int)(this.getUID() - that.getUID());	
		}
		return result;
	}
	
	public String getUuid(){
        return this.dtUUID;
    }

    public void setUuid(String uuid){
        this.dtUUID = uuid;
    }
}


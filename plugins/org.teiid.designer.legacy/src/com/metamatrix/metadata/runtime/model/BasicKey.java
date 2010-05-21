/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.model;

import java.util.ArrayList;
import java.util.List;
import com.metamatrix.metadata.runtime.api.ElementID;
import com.metamatrix.metadata.runtime.api.Key;
import com.metamatrix.metadata.runtime.api.MetadataID;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;

public class BasicKey extends BasicMetadataObject implements Key {
        /**
     */
    private static final long serialVersionUID = 1L;
        private String description;
        private boolean isPrimaryKey;
        private boolean isForeignKey;
        private boolean isAccessPattern;
        private boolean isIndexed;
        private short matchType = MetadataConstants.NOT_DEFINED_SHORT;
        private boolean isUniqueKey;
        private short keyType;
        private MetadataID referencedKey;
        private transient List elements;
        private String alias;
        private long referencedKeyUid;
        private String path;

/**
 * Call constructor to instantiate a runtime object by passing the RuntimeID that identifies the entity and the VIrtualDatabaseID that identifes the Virtual Database the object will be contained.
 */
    public BasicKey(BasicKeyID keyID, BasicVirtualDatabaseID virtualDBID) {
        super(keyID, virtualDBID);
    }

    public String getDescription() {
        return this.description;
    }

    /**
    * Override the super method so that when the name
    * is returned, it is the name and not the full path for
    * a key
    */
    @Override
    public String getNameInSource() {
        String alias = getAlias();
        if(alias != null)
	        return alias;
        return getName();
    }
    @Override
    public String getAlias(){
        return alias;
    }
    public void setAlias(String alias){
        this.alias = alias;
    }
    public List getElementIDs() {
	      return elements;
    }
    public MetadataID getReferencedKey() {
        return this.referencedKey;
    }
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public boolean isForeignKey() {
        return isForeignKey;
    }
    public boolean isUniqueKey() {
        return isUniqueKey;
    }
    public boolean isIndexed() {
        return isIndexed;
    }
    public boolean isAccessPattern() {
        return isAccessPattern;
    }
    public short getKeyType() {
        return keyType;
    }
    public short getMatchType() {
        return matchType;
    }
    public long getReferencedKeyUID() {
	      return this.referencedKeyUid;
    }
     @Override
    public String getPath() {
	      return this.path;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsIndexed(boolean isIndexed) {
        this.isIndexed = isIndexed;
    }
    public void setKeyType(short keyType) {
        this.keyType = keyType;
        if(keyType == MetadataConstants.KEY_TYPES.PRIMARY_KEY){
            this.isPrimaryKey = true;
        }else if(keyType == MetadataConstants.KEY_TYPES.FOREIGN_KEY){
            this.isForeignKey = true;
        }else if(keyType == MetadataConstants.KEY_TYPES.UNIQUE_KEY){
            this.isUniqueKey = true;
        }else if(keyType == MetadataConstants.KEY_TYPES.ACCESS_PATTERN){
            this.isAccessPattern = true;
        }else{
            //non-unique key
        }
    }
    public void setMatchType(short matchType) {
       this.matchType = matchType;
    }
    public void setReferencedKey(MetadataID referKey) {
	      this.referencedKey = referKey;
    }
    public void setReferencedKeyUID(long uid) {
	      this.referencedKeyUid = uid;
    }

    public void setElementIDs(List elements) {
	      this.elements = elements;
    }
    public void clearElementIDs() {
        if(this.elements != null) {
            this.elements.clear();
        }
    }
    public void setPath(String path) {
	      this.path = path;
    }
    public void addElementID(ElementID elementID){
        if(elements == null)
            elements = new ArrayList();
        elements.add(elementID);
    }
}


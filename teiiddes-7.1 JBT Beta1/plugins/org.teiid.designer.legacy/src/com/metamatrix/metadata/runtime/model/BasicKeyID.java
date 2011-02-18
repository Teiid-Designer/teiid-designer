/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.model;

import org.teiid.logging.LogManager;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.metadata.runtime.RuntimeMetadataPlugin;
import com.metamatrix.metadata.runtime.api.GroupID;
import com.metamatrix.metadata.runtime.api.KeyID;
import com.metamatrix.metadata.runtime.api.ModelID;
import com.metamatrix.metadata.runtime.util.LogRuntimeMetadataConstants;
import com.metamatrix.metadata.runtime.util.RuntimeIDParser;
import com.metamatrix.metadata.util.ErrorMessageKeys;

public class BasicKeyID extends BasicMetadataID implements KeyID {
    /**
     */
    private static final long serialVersionUID = 1L;
    private GroupID groupID = null;
    private ModelID modelID = null;

/**
 * Call constructor to instantiate a BasicKeyID object for the fully qualified Key name and an internal unique identifier.
 */
    public BasicKeyID(String fullName, long internalUniqueID) {
        super(fullName, internalUniqueID);
        if(this.getNameComponents().size() < 3){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid KeyID \"",fullName,"\". Number of name components must be > 2."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BKID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BKID_0001) );
        }
    }

/**
 * Call constructor to instantiate a BasicKeyID object for the fully qualified Key name.
 */
    public BasicKeyID(String fullName){
        super(fullName);
        if(this.getNameComponents().size() < 3){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid KeyID \"",fullName,"\". Number of name components must be > 2."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BKID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BKID_0001) );
        }
    }

/**
 * return the group name.
 * @return GroupID is the group the key is contained in
 */
    public GroupID getGroupID() {
	      if (groupID != null) {
	            return groupID;
	      }
        String groupName = RuntimeIDParser.getGroupFullName(this);
        groupID = new BasicGroupID(groupName);
        return groupID;
    }
/**
 * return the modelID this key is a part of.
 * @return ModelID is the model the key is contained in
 */
    public ModelID getModelID() {
        if (modelID != null) {
            return modelID;
        }
        String modelName = this.getNameComponent(0);
        modelID = new BasicModelID(modelName);
        return modelID;
    }

    public String getModelName(){
        return this.getNameComponent(0);
    }

    public String getGroupName(){
        return RuntimeIDParser.getGroupName(this);
    }

    public void setGroupID(GroupID groupID){
        this.groupID = groupID;
    }

    public void setModelID(ModelID modelID){
        this.modelID = modelID;
    }
}


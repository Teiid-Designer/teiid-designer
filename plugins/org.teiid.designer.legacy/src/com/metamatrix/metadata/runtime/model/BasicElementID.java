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
import com.metamatrix.metadata.runtime.api.ElementID;
import com.metamatrix.metadata.runtime.api.GroupID;
import com.metamatrix.metadata.runtime.api.ModelID;
import com.metamatrix.metadata.runtime.util.LogRuntimeMetadataConstants;
import com.metamatrix.metadata.runtime.util.RuntimeIDParser;
import com.metamatrix.metadata.util.ErrorMessageKeys;

public class BasicElementID extends BasicMetadataID implements ElementID {
    /**
     */
    private static final long serialVersionUID = 1L;
    private GroupID groupID = null;
    private ModelID modelID = null;

/**
 * Call constructor to instantiate a BasicElementID object for the fully qualified name and an internal unique identifier.
 */
    public BasicElementID(String fullName, long internalUniqueID) {
        super(fullName, internalUniqueID);
        if(this.getNameComponents().size() < 3){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid ElementID \"",fullName,"\". Number of name components must be > 2."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BEID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BEID_0001) );
        }
    }

/**
 * Call constructor to instantiate a BasicElementID object for the fully qualified name.
 */
    public BasicElementID(String fullName){
        super(fullName);
        if(this.getNameComponents().size() < 3){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid ElementID \"",fullName,"\". Number of name components must be > 2."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BEID_0001);
        }
    }

    public BasicElementID(String parentName, String name, long internalUniqueID) {
        super(parentName, name, internalUniqueID);
        if(this.getNameComponents().size() < 3){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid ElementID \"",this.getFullName(),"\". Number of name components must be > 2."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BEID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BEID_0001) );
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
        modelID = new BasicModelID(this.getNameComponent(0));
        return modelID;
    }

    public String getModelName(){
        return this.getNameComponent(0);
    }

    public String getGroupName(){
        return RuntimeIDParser.getGroupName(this);
    }

    public void setGroupID(GroupID id){
        this.groupID = id;
    }

    public void setModelID(ModelID id){
        this.modelID = id;
    }

}


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
import com.metamatrix.metadata.runtime.api.ModelID;
import com.metamatrix.metadata.runtime.util.LogRuntimeMetadataConstants;
import com.metamatrix.metadata.util.ErrorMessageKeys;

public class BasicGroupID extends BasicMetadataID implements GroupID {
    /**
     */
    private static final long serialVersionUID = 1L;
    private ModelID modelID = null;
    private String alias = null;

/**
 * Call constructor to instantiate a BasicGroupID object for the fully qualified Group name and an internal unique identifier.
 */
    public BasicGroupID(String fullName, long internalUniqueID) {
        super(fullName, internalUniqueID);
        if(this.getNameComponents().size() < 2){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid GroupID \"",fullName,"\". Number of name components must be > 1."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BGID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BGID_0001) );
        }
    }

/**
 * Call constructor to instantiate a BasicGroupID object for the fully qualified Group name and uid.
 */
    public BasicGroupID(String fullName){
        super(fullName);
        if(this.getNameComponents().size() < 2){
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA,new Object[]{"Invalid GroupID \"",fullName,"\". Number of name components must be > 1."});
            throw new TeiidRuntimeException (ErrorMessageKeys.BGID_0001, RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BGID_0001) );
        }
    }
/*
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

    public void setModelID(ModelID id){
        this.modelID = id;
    }

    public String getAlias(){
        return this.alias;
    }
    void setAlias(String alias){
        this.alias = alias;
    }

}


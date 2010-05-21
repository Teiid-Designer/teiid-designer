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
import com.metamatrix.metadata.runtime.api.ModelID;
import com.metamatrix.metadata.runtime.api.ProcedureID;
import com.metamatrix.metadata.runtime.util.LogRuntimeMetadataConstants;
import com.metamatrix.metadata.util.ErrorMessageKeys;

public class BasicProcedureID extends BasicMetadataID implements ProcedureID {
    /**
     */
    private static final long serialVersionUID = 1L;
    private ModelID modelID = null;

    /**
     * Call constructor to instantiate a BasicProcedureID object for the fully qualified name and an internal unique identifier.
     */
    public BasicProcedureID( String fullName,
                             long internalUniqueID ) {
        super(fullName, internalUniqueID);
        if (this.getNameComponents().size() < 2) {
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA, new Object[] {"Invalid ProcedureID \"",
                fullName, "\". Number of name components must be > 1."});
            throw new TeiidRuntimeException(ErrorMessageKeys.BPID_0001,
                                                 RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BPID_0001));
        }
    }

    /**
     * Call constructor to instantiate a BasicProcedureID object for the fully qualified name.
     */
    public BasicProcedureID( String fullName ) {
        super(fullName);
        if (this.getNameComponents().size() < 2) {
            LogManager.logDetail(LogRuntimeMetadataConstants.CTX_RUNTIME_METADATA, new Object[] {"Invalid ProcedureID \"",
                fullName, "\". Number of name components must be > 1."});
            throw new TeiidRuntimeException(ErrorMessageKeys.BPID_0001,
                                                 RuntimeMetadataPlugin.Util.getString(ErrorMessageKeys.BPID_0001));
        }
    }

    public ModelID getModelID() {
        if (modelID != null) {
            return modelID;
        }
        modelID = new BasicModelID(this.getNameComponent(0));
        return modelID;
    }

    public String getModelName() {
        return this.getNameComponent(0);
    }

    public void setModelID( ModelID modelID ) {
        this.modelID = modelID;
    }
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql.impl;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.sql.lang.SPParameter;

/**
 *
 */
public class StoredProcedureInfoImpl implements IStoredProcedureInfo {

    private final StoredProcedureInfo storedProcedureInfo;
    
    private final SyntaxFactory factory = new SyntaxFactory();
    
    /**
     * 
     */
    public StoredProcedureInfoImpl(StoredProcedureInfo storedProcedureInfo) {
        this.storedProcedureInfo = storedProcedureInfo;
    }

    public StoredProcedureInfo getDelegate() {
        return storedProcedureInfo;
    }
    
    @Override
    public String getProcedureCallableName() {
        return getDelegate().getProcedureCallableName();
    }

    @Override
    public void setProcedureCallableName(String callableName) {
        getDelegate().setProcedureCallableName(callableName);
    }

    @Override
    public Object getModelID() {
        return getDelegate().getModelID();
    }

    @Override
    public void setModelID(Object modelID) {
        getDelegate().setModelID(modelID);
    }

    @Override
    public Object getProcedureID() {
        return getDelegate().getProcedureID();
    }

    @Override
    public void setProcedureID(Object procedureID) {
        getDelegate().setProcedureID(procedureID);
    }

    @Override
    public List<ISPParameter> getParameters() {
        List<ISPParameter> wrapList = new ArrayList<ISPParameter>();
        
        for (SPParameter parameter : getDelegate().getParameters()) {
            wrapList.add(new SPParameterImpl(parameter));
        }
        
        return wrapList;
    }

    @Override
    public void setParameters(List<ISPParameter> parameters) {
        List<SPParameter> unwrapList = new ArrayList<SPParameter>();
        
        for (ISPParameter parameter : parameters) {
            SPParameter delegate = ((SPParameterImpl) parameter).getDelegate();
            unwrapList.add(delegate);
        }
        
        getDelegate().setParameters(unwrapList);
    }

    @Override
    public void addParameter(ISPParameter parameter) {
    }

    @Override
    public boolean returnsResultSet() {
        return getDelegate().returnsResultSet();
    }

    @Override
    public boolean returnsResultParameter() {
        return getDelegate().returnsResultParameter();
    }

    @Override
    public int getUpdateCount() {
        return getDelegate().getUpdateCount();
    }

    @Override
    public void setUpdateCount(int updateCount) {
        getDelegate().setUpdateCount(updateCount);
    }

}

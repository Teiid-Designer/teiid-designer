/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.sql.lang.SPParameter;

/**
 *
 */
public class StoredProcedureInfoImpl implements IStoredProcedureInfo {

    private final StoredProcedureInfo storedProcedureInfo;
    
    /**
     * @param storedProcedureInfo 
     */
    public StoredProcedureInfoImpl(StoredProcedureInfo storedProcedureInfo) {
        this.storedProcedureInfo = storedProcedureInfo;
    }

    /**
     * @return the internal delegate
     */
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
        SPParameterImpl dParameter = (SPParameterImpl) parameter;
        getDelegate().addParameter(dParameter.getDelegate());
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
    
    @Override
    public void setQueryPlan(IQueryNode queryNode) {
        storedProcedureInfo.setQueryPlan(((QueryNodeImpl) queryNode).getDelegate());
    }

    @Override
    public String toString() {
        return storedProcedureInfo.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.storedProcedureInfo == null) ? 0 : this.storedProcedureInfo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StoredProcedureInfoImpl other = (StoredProcedureInfoImpl)obj;
        if (this.storedProcedureInfo == null) {
            if (other.storedProcedureInfo != null) return false;
        } else if (!this.storedProcedureInfo.equals(other.storedProcedureInfo)) return false;
        return true;
    }
    
}

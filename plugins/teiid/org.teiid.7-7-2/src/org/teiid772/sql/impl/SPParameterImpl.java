/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.sql.lang.SPParameter;

/**
 *
 */
public class SPParameterImpl implements ISPParameter {

    private final SPParameter parameter;
    
    /**
     * @param parameter
     */
    public SPParameterImpl(SPParameter parameter) {
        this.parameter = parameter;
    }

    SPParameter getDelegate() {
        return parameter;
    }

    @Override
    public SPParameterImpl clone() {
        return new SPParameterImpl((SPParameter) getDelegate().clone());
    }

    @Override
    public void addResultSetColumn(String colName, Class<?> type, Object id) {
        getDelegate().addResultSetColumn(colName, type, id);
    }

    @Override
    public IElementSymbol getParameterSymbol() {
        return new ElementSymbolImpl(getDelegate().getParameterSymbol());
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public void setName(String name) {
        getDelegate().setName(name);
    }

    @Override
    public ParameterInfo getParameterType() {
        int parameterType = getDelegate().getParameterType();
        for (ISPParameter.ParameterInfo pInfo : ISPParameter.ParameterInfo.values()) {
            if (pInfo.index() == parameterType)
                return pInfo;
        }
        
        throw new RuntimeException();
    }

    @Override
    public void setParameterType(ParameterInfo parameterType) {
        getDelegate().setParameterType(parameterType.index());
    }

    @Override
    public Class<?> getClassType() {
        return getDelegate().getClassType();
    }

    @Override
    public void setClassType(Class<?> klazz) {
        getDelegate().setClassType(klazz);
    }

    @Override
    public Object getMetadataID() {
        return getDelegate().getMetadataID();
    }

    @Override
    public void setMetadataID(Object object) {
        getDelegate().setMetadataID(object);
    }
    
    @Override
    public String toString() {
        return parameter.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.parameter == null) ? 0 : this.parameter.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SPParameterImpl other = (SPParameterImpl)obj;
        if (this.parameter == null) {
            if (other.parameter != null) return false;
        } else if (!this.parameter.equals(other.parameter)) return false;
        return true;
    }
}
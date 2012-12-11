/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class StoredProcedureImpl extends LanguageObjectImpl implements IStoredProcedure {

    /**
     * @param storedProcedure
     */
    public StoredProcedureImpl(StoredProcedure storedProcedure) {
        super(storedProcedure);
    }

    @Override
    public StoredProcedure getDelegate() {
        return (StoredProcedure) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public StoredProcedureImpl clone() {
        return new StoredProcedureImpl((StoredProcedure) getDelegate().clone());
    }

    @Override
    public int getType() {
        return getDelegate().getType();
    }

    @Override
    public IOption getOption() {
        return getFactory().convert(getDelegate().getOption());
    }

    @Override
    public List<IExpression> getProjectedSymbols() {
        return getFactory().wrap(getDelegate().getProjectedSymbols());
    }

    @Override
    public boolean isResolved() {
        return getDelegate().isResolved();
    }

    @Override
    public void setProcedureID(Object procedureID) {
        getDelegate().setProcedureID(procedureID);
    }

    @Override
    public Object getProcedureID() {
        return getDelegate().getProcedureID();
    }

    @Override
    public List<ISPParameter> getInputParameters() {
        List<ISPParameter> inputParameters = new ArrayList<ISPParameter>();
        for (SPParameter inputParameter : getDelegate().getInputParameters()) {
            inputParameters.add(new SPParameterImpl(inputParameter));
        }
        
        return inputParameters;
    }

    @Override
    public void setParameter(ISPParameter parameter) {
        SPParameterImpl parameterImpl = (SPParameterImpl) parameter;
        getDelegate().setParameter(parameterImpl.getDelegate());
    }

    @Override
    public String getProcedureCallableName() {
        return getDelegate().getProcedureCallableName();
    }

    @Override
    public void setProcedureName(String procFullName) {
        getDelegate().setProcedureName(procFullName);
    }

    @Override
    public void setDisplayNamedParameters(boolean b) {
        getDelegate().setDisplayNamedParameters(b);
    }

    @Override
    public String getGroupName() {
        GroupSymbol group = getDelegate().getGroup();
        if (group == null)
            return null;
        
        return group.getName();
    }
}
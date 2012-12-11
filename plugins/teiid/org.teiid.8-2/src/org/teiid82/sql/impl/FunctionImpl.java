/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import java.util.Arrays;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.udf.IFunctionDescriptor;
import org.teiid.query.sql.symbol.Function;

/**
 *
 */
public class FunctionImpl extends ExpressionImpl implements IFunction {

    /**
     * @param function
     */
    public FunctionImpl(Function function) {
        super(function);
    }

    @Override
    public Function getDelegate() {
        return (Function) delegate;
    }
    
    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public FunctionImpl clone() {
        return new FunctionImpl((Function) getDelegate().clone());
    }

    @Override
    public void setType(Class<?> type) {
        getDelegate().setType(type);
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public IExpression[] getArgs() {
        List<IExpression> expressionList = getFactory().wrap(Arrays.asList(getDelegate().getArgs()));
        return expressionList.toArray(new IExpression[0]);
    }

    @Override
    public IExpression getArg(int index) {
        return getFactory().convert(getDelegate().getArg(index));
    }

    @Override
    public boolean isImplicit() {
        return getDelegate().isImplicit();
    }

    @Override
    public IFunctionDescriptor getFunctionDescriptor() {
        return getFactory().createFunctionDescriptor(getDelegate().getFunctionDescriptor());
    }

    @Override
    public void setFunctionDescriptor(IFunctionDescriptor fd) {
        FunctionDescriptorImpl functionDescriptorImpl = (FunctionDescriptorImpl) fd;
        getDelegate().setFunctionDescriptor(functionDescriptorImpl.getDelegate());
    }
}
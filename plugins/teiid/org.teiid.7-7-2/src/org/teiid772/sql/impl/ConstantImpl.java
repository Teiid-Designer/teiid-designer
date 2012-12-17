/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.query.sql.symbol.Constant;

/**
 *
 */
public class ConstantImpl extends ExpressionImpl implements IConstant {

    /**
     * @param constant
     */
    public ConstantImpl(Constant constant) {
        super(constant);
    }

    @Override
    public Constant getDelegate() {
        return (Constant) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ConstantImpl clone() {
        return new ConstantImpl((Constant) getDelegate().clone());
    }

    @Override
    public Object getValue() {
        return getDelegate().getValue();
    }

    @Override
    public boolean isMultiValued() {
        return getDelegate().isMultiValued();
    }
}
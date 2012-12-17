/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.query.sql.symbol.Reference;

/**
 *
 */
public class ReferenceImpl extends ExpressionImpl implements IReference {

    /**
     * @param reference
     */
    public ReferenceImpl(Reference reference) {
        super(reference);
    }

    @Override
    public Reference getDelegate() {
        return (Reference) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ReferenceImpl clone() {
        return new ReferenceImpl((Reference) getDelegate().clone());
    }

    @Override
    public boolean isPositional() {
        return getDelegate().isPositional();
    }

    @Override
    public IElementSymbol getExpression() {
        return getFactory().convert(getDelegate().getExpression());
    }
}
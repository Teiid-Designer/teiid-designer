/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.lang.Into;

/**
 *
 */
public class IntoImpl extends LanguageObjectImpl implements IInto {

    /**
     * @param into
     */
    public IntoImpl(Into into) {
        super(into);
    }

    @Override
    public Into getDelegate() {
        return (Into) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IntoImpl clone() {
        return new IntoImpl((Into) getDelegate().clone());
    }

    @Override
    public IGroupSymbol getGroup() {
        return getFactory().convert(getDelegate().getGroup());
    }
}
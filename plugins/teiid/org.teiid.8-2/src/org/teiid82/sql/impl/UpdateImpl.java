/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.query.sql.lang.Update;

/**
 *
 */
public class UpdateImpl extends CommandImpl implements IUpdate {

    /**
     * @param update
     */
    public UpdateImpl(Update update) {
        super(update);
    }

    @Override
    public Update getDelegate() {
        return (Update) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public UpdateImpl clone() {
        return new UpdateImpl((Update) getDelegate().clone());
    }
}
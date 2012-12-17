/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.IStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.Statement;

/**
 *
 */
public class BlockImpl extends StatementImpl implements IBlock {

    /**
     * @param block
     */
    public BlockImpl(Block block) {
        super(block);
    }

    @Override
    public Block getDelegate() {
        return (Block) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public BlockImpl clone() {
        return new BlockImpl(getDelegate().clone());
    }

    @Override
    public List<IStatement> getStatements() {
        return getFactory().wrap(getDelegate().getStatements());
    }

    @Override
    public void addStatement(IStatement statement) {
        Statement statementImpl = getFactory().convert(statement);
        getDelegate().addStatement(statementImpl);
    }
}
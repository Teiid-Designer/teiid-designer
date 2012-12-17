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
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;

/**
 *
 */
public class CreateUpdateProcedureCommandImpl extends CommandImpl implements ICreateProcedureCommand {

    /**
     * @param createUpdateProcedureCommand
     */
    public CreateUpdateProcedureCommandImpl(CreateUpdateProcedureCommand createUpdateProcedureCommand) {
        super(createUpdateProcedureCommand);
    }

    @Override
    public CreateUpdateProcedureCommand getDelegate() {
        return (CreateUpdateProcedureCommand) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public CreateUpdateProcedureCommandImpl clone() {
        return new CreateUpdateProcedureCommandImpl((CreateUpdateProcedureCommand) getDelegate().clone());
    }

    @Override
    public IBlock getBlock() {
        return getFactory().convert(getDelegate().getBlock());
    }

    @Override
    public void setBlock(IBlock block) {
        Block blockImpl = getFactory().convert(block);
        getDelegate().setBlock(blockImpl);
    }

    @Override
    public void setProjectedSymbols(List<IElementSymbol> projectedSymbols) {
        List<ElementSymbol> symbols = getFactory().unwrap(projectedSymbols);
        getDelegate().setProjectedSymbols(symbols);
    }
}
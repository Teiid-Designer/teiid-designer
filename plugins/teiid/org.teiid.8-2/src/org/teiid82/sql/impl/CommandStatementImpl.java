/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.proc.CommandStatement;

/**
 *
 */
public class CommandStatementImpl extends StatementImpl implements ICommandStatement {

    /**
     * @param commandStatement
     */
    public CommandStatementImpl(CommandStatement commandStatement) {
        super(commandStatement);
    }

    @Override
    public CommandStatement getDelegate() {
        return (CommandStatement) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public CommandStatementImpl clone() {
        return new CommandStatementImpl((CommandStatement) getDelegate().clone());
    }

    @Override
    public ICommand getCommand() {
        return getFactory().convert(getDelegate().getCommand());
    }

    @Override
    public void setCommand(ICommand command) {
        Command commandImpl = getFactory().convert(command);
        getDelegate().setCommand(commandImpl);
    }
}
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.query.sql.lang.Command;

/**
 *
 */
public abstract class CommandImpl extends LanguageObjectImpl implements ICommand {

    /**
     * @param command
     */
    public CommandImpl(Command command) {
        super(command);
    }
    
    @Override
    public Command getDelegate() {
        return (Command) delegate;
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
}

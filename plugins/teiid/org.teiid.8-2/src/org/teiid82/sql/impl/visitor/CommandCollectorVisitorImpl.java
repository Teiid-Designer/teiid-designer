/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.visitor;

import java.util.List;
import org.teiid.designer.query.sql.ICommandCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.visitor.CommandCollectorVisitor;
import org.teiid82.sql.impl.SyntaxFactory;

/**
 *
 */
public class CommandCollectorVisitorImpl implements ICommandCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public List<ICommand> getCommands(ICommand command) {
        Command commandImpl = factory.convert(command);
        List<Command> commands = CommandCollectorVisitor.getCommands(commandImpl);
        return factory.wrap(commands);
    }

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;

/**
 *
 */
public interface ICommandCollectorVisitor {

    /**
     * Retrieve the commands from the given command
     * 
     * @param command
     * 
     * @return list of all sub commands
     */
    List<ICommand> getCommands(ICommand command);
}

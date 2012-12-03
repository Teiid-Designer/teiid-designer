/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;

import java.util.List;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.symbol.IElementSymbol;

/**
 *
 */
public interface ICreateProcedureCommand extends ICommand {

    /**
     * Get the block on this command.
     * 
     * @return The <code>Block</code> on this command
     */
    IBlock getBlock();
    
    /**
     * Set the block on this command.
     * 
     * @param block The <code>Block</code> on this command
     */
    void setBlock(IBlock block);
    
    /**
     * Set the projected symbols
     * 
     * @param projectedSymbols
     */
    void setProjectedSymbols(List<IElementSymbol> projectedSymbols);
}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;

/**
 *
 */
public interface ICreateProcedureCommand<B extends IBlock, E extends IExpression, LV extends ILanguageVisitor>
    extends ICommand<E, LV> {

    /**
     * Get the block on this command.
     * 
     * @return The <code>Block</code> on this command
     */
    B getBlock();
    
    /**
     * Set the block on this command.
     * 
     * @param block The <code>Block</code> on this command
     */
    void setBlock(B block);
}

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
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 * @param <B> 
 * @param <GS> 
 * @param <E> 
 * @param <LV> 
 *
 */
public interface ICreateProcedureCommand<B extends IBlock, GS extends IGroupSymbol, E extends IExpression, LV extends ILanguageVisitor>
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

    /**
     * @return virtual group on this command
     */
    GS getVirtualGroup();

    /**
     * Set the virtual group on this command
     *
     * @param view
     */
    void setVirtualGroup(GS view);

}

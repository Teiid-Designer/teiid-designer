/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface IBlock<S extends IStatement, LV extends ILanguageVisitor> extends IStatement<LV> {

    /**
     * Get all the statements contained on this block.
     * 
     * @return A list of <code>Statement</code>s contained in this block
     */
    List<S> getStatements();

    /**
     * Add a <code>Statement</code> to this block.
     * 
     * @param statement The <code>Statement</code> to be added to the block
     */
    void addStatement(S statement);

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IQueryResolver<C extends ICommand, GS extends IGroupSymbol> {

    /**
     * @param command
     * @param gSymbol
     * @param teiidCommandType
     * @param metadata
     * @throws Exception 
     */
    void resolveCommand(C command, GS gSymbol, int teiidCommandType, IQueryMetadataInterface metadata) throws Exception;

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import java.util.List;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IQueryResolver<C extends ICommand, GS extends IGroupSymbol, E extends IExpression> {

    /**
     * @param command
     * @param gSymbol
     * @param teiidCommandType
     * @param metadata
     * @throws Exception 
     */
    void resolveCommand(C command, GS gSymbol, int teiidCommandType, IQueryMetadataInterface metadata) throws Exception;

    /**
     * @param command
     * @param gSymbol
     * @param commandType
     * @param metadata
     * @param projectedSymbols
     */
    void postResolveCommand(C command, GS gSymbol, int teiidCommandType, IQueryMetadataInterface metadata,
                                               List<E> projectedSymbols);

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.validator;

import org.teiid.designer.query.IQueryResolver;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid82.sql.impl.CrossQueryMetadata;
import org.teiid82.sql.impl.SyntaxFactory;

/**
 *
 */
public class QueryResolverImpl implements IQueryResolver {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public void resolveCommand(ICommand command,
                               IGroupSymbol gSymbol,
                               int commandType,
                               IQueryMetadataInterface metadata) throws Exception {
        
        Command dCommand = factory.convert(command);
        GroupSymbol symbol = factory.convert(gSymbol);
        CrossQueryMetadata cqMetadata = new CrossQueryMetadata(metadata);
        
        QueryResolver.resolveCommand(dCommand, symbol, commandType, cqMetadata);
    }

}

/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.resolver.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.language.SQLConstants;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.ProcedureContainerResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.VariableResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;


/**
 * This class knows how to expand and resolve UDPATE commands.
 */
public class UpdateResolver extends ProcedureContainerResolver implements VariableResolver {

    /**
     * @param queryResolver
     */
    public UpdateResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    /** 
     * @see org.teiid.query.resolver.ProcedureContainerResolver#resolveProceduralCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter)
     */
    @Override
    public void resolveProceduralCommand(Command command, TempMetadataAdapter metadata) 
        throws Exception {

        //Cast to known type
        Update update = (Update) command;

        // Resolve elements and functions
        Set<GroupSymbol> groups = new HashSet<GroupSymbol>();
        groups.add(update.getGroup());
        ResolverVisitor visitor = new ResolverVisitor(command.getTeiidVersion());
        for (SetClause clause : update.getChangeList().getClauses()) {
        	visitor.resolveLanguageObject(clause.getSymbol(), groups, null, metadata);
		}
        getQueryResolver().resolveSubqueries(command, metadata, groups);
        visitor.resolveLanguageObject(update, groups, update.getExternalGroupContexts(), metadata);
    }
    
    /** 
     * @param metadata
     * @param group
     * @return
     * @throws Exception
     * @throws Exception
     */
    @Override
    protected String getPlan(IQueryMetadataInterface metadata,
                           GroupSymbol group) throws Exception {
        return metadata.getUpdatePlan(group.getMetadataID());
    }

    /** 
     * @see org.teiid.query.resolver.VariableResolver#getVariableValues(Command, boolean, IQueryMetadataInterface)
     */
    @Override
    public Map<ElementSymbol, Expression> getVariableValues(Command command, boolean changingOnly,
                                 IQueryMetadataInterface metadata) throws Exception {
        Map<ElementSymbol, Expression> result = new HashMap<ElementSymbol, Expression>();
        
        Update update = (Update) command;
        
        Map<ElementSymbol, Expression> changing = update.getChangeList().getClauseMap();
        
        for (Entry<ElementSymbol, Expression> entry : changing.entrySet()) {
        	ElementSymbol leftSymbol = entry.getKey().clone();
            leftSymbol.getGroupSymbol().setName(ProcedureReservedWords.CHANGING);
            leftSymbol.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            
            Constant constant = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.CONSTANT);
            constant.setValue(Boolean.TRUE);
            result.put(leftSymbol, constant);
            if (!changingOnly) {
            	leftSymbol = entry.getKey().clone();
            	leftSymbol.getGroupSymbol().setName(SQLConstants.Reserved.NEW);
            	result.put(leftSymbol, entry.getValue());
            }
        }
        
        Collection<ElementSymbol> insertElmnts = ResolverUtil.resolveElementsInGroup(update.getGroup(), metadata);

        insertElmnts.removeAll(changing.keySet());

        Iterator<ElementSymbol> defaultIter = insertElmnts.iterator();
        while(defaultIter.hasNext()) {
            ElementSymbol varSymbol = defaultIter.next().clone();
            varSymbol.getGroupSymbol().setName(ProcedureReservedWords.CHANGING);
            varSymbol.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            
            Constant constant = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.CONSTANT);
            constant.setValue(Boolean.FALSE);
            result.put(varSymbol, constant);
        }
        
        return result;
    }

}

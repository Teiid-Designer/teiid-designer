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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.ArgCheck;
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
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.util.SymbolMap;
import org.teiid.runtime.client.Messages;


/**
 * This class knows how to expand and resolve INSERT commands.
 */
public class InsertResolver extends ProcedureContainerResolver implements VariableResolver {

    /**
     * @param queryResolver
     */
    public InsertResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    /**
     * Resolve an INSERT.  Need to resolve elements, constants, types, etc.
     * @see org.teiid.query.resolver.ProcedureContainerResolver#resolveProceduralCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter)
     */
    @Override
    public void resolveProceduralCommand(Command command, TempMetadataAdapter metadata) 
        throws Exception {

        // Cast to known type
        Insert insert = (Insert) command;
        
        if (insert.getValues() != null) {
        	getQueryResolver().resolveSubqueries(command, metadata, null);
	        //variables and values must be resolved separately to account for implicitly defined temp groups
	        resolveList(insert.getValues(), metadata, insert.getExternalGroupContexts(), null);
    	}
        //resolve subquery if there
        if(insert.getQueryExpression() != null) {
        	getQueryResolver().setChildMetadata(insert.getQueryExpression(), command);
            
            getQueryResolver().resolveCommand(insert.getQueryExpression(), metadata.getMetadata(), false);
        }

        Set<GroupSymbol> groups = new HashSet<GroupSymbol>();
        groups.add(insert.getGroup());
        
     // resolve any functions in the values
        List values = insert.getValues();
        boolean usingQuery = insert.getQueryExpression() != null;
        
        if (usingQuery) {
            values = insert.getQueryExpression().getProjectedSymbols();
        }
        
        if (insert.getVariables().isEmpty()) {
            if (insert.getGroup().isResolved()) {
                List<ElementSymbol> variables = ResolverUtil.resolveElementsInGroup(insert.getGroup(), metadata);
                for (Iterator<ElementSymbol> i = variables.iterator(); i.hasNext();) {
                    insert.addVariable(i.next().clone());
                }
            } else {
                for (int i = 0; i < values.size(); i++) {
                    ElementSymbol es = createASTNode(ASTNodes.ELEMENT_SYMBOL);
                	if (usingQuery) {
                		Expression ses = (Expression)values.get(i);
                    	es.setName(Symbol.getShortName(ses));
                    	es.setType(ses.getType());
                    	insert.addVariable(es);
                    } else {
                        es.setName("expr" + i); //$NON-NLS-1$
                    	insert.addVariable(es);
                    }
                }
            }
        } else if (insert.getGroup().isResolved()) {
            resolveVariables(metadata, insert, groups);
        }

        resolveTypes(insert, metadata, values, usingQuery);
        
        if (!insert.getGroup().isResolved()) { //define the implicit temp group
            ResolverUtil.resolveImplicitTempGroup(metadata, insert.getGroup(), insert.getVariables());
            resolveVariables(metadata, insert, groups);
            
            //ensure that the types match
            resolveTypes(insert, metadata, values, usingQuery);
        }
        
        if (insert.getQueryExpression() != null && metadata.isVirtualGroup(insert.getGroup().getMetadataID())) {
        	List<Reference> references = new ArrayList<Reference>(insert.getVariables().size());
        	for (int i = 0; i < insert.getVariables().size(); i++) {
        	    Reference ref = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.REFERENCE);
        	    ref.setIndex(i);
        	    ref.setPositional(true);
        		ref.setType(insert.getVariables().get(i).getType());
				references.add(ref);
			}
        	insert.setValues(references);
        }
    }

    private void resolveVariables(TempMetadataAdapter metadata,
                                  Insert insert,
                                  Set<GroupSymbol> groups) throws Exception {
        try {
            resolveList(insert.getVariables(), metadata, null, groups);
        } catch (QueryResolverException e) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30126, insert.getGroup(), e.getUnresolvedSymbols()));
        }
    }

    private void resolveList(Collection<? extends Expression> elements, TempMetadataAdapter metadata,
                                  GroupContext externalGroups, Set<GroupSymbol> groups) throws QueryResolverException, Exception {
        if (elements == null || elements.isEmpty())
            return;

        ResolverVisitor visitor = new ResolverVisitor(elements.iterator().next().getTeiidVersion());
        for (Iterator<? extends Expression> i = elements.iterator(); i.hasNext();) {
            Expression expr = i.next();
            visitor.resolveLanguageObject(expr, groups, externalGroups, metadata);
        }
    }
    
    /** 
     * @param insert
     * @param metadata
     * @param values 
     * @param usingQuery 
     * @throws Exception
     */
    public void resolveTypes(Insert insert, TempMetadataAdapter metadata, List values, boolean usingQuery) throws Exception {
        
        List newValues = new ArrayList(values.size());
        
        // check that # of variables == # of values
        if(values.size() != insert.getVariables().size()) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30127, insert.getVariables().size(), values.size()));
        }
        
        Iterator valueIter = values.iterator();
        Iterator<ElementSymbol> varIter = insert.getVariables().iterator();
        while(valueIter.hasNext()) {
            // Walk through both elements and expressions, which should match up
			Expression expression = (Expression) valueIter.next();
			ElementSymbol element = varIter.next();
			
			if (expression.getType() == null) {
				ResolverUtil.setDesiredType(SymbolMap.getExpression(expression), element.getType(), insert);
			}

            if(element.getType() != null && expression.getType() != null) {
                String elementTypeName = getDataTypeManager().getDataTypeName(element.getType());
                if (!usingQuery) {
                    newValues.add(ResolverUtil.convertExpression(expression, elementTypeName, metadata));
                } else if (element.getType() != expression.getType()
                           && !getDataTypeManager().isImplicitConversion(getDataTypeManager().getDataTypeName(expression.getType()),
                                                                         getDataTypeManager().getDataTypeName(element.getType()))) {
                    //TODO: a special case here is a projected literal
                     throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30128, new Object[] {expression, expression.getType().getName(), element, element.getType().getName()}));
                }
            } else if (element.getType() == null && expression.getType() != null)  {
                element.setType(expression.getType());
                newValues.add(expression);
            } else {
                ArgCheck.isTrue(false,"Cannot determine element or expression type"); //$NON-NLS-1$
            }
        }

        if (!usingQuery) {
            insert.setValues(newValues);
        }
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
        return metadata.getInsertPlan(group.getMetadataID());
    }
    
    /** 
     * @see org.teiid.query.resolver.ProcedureContainerResolver#resolveGroup(org.teiid.query.metadata.TempMetadataAdapter, org.teiid.query.sql.lang.ProcedureContainer)
     */
    @Override
    protected void resolveGroup(TempMetadataAdapter metadata,
                                ProcedureContainer procCommand) throws Exception {
        if (!procCommand.getGroup().isImplicitTempGroupSymbol() || metadata.getMetadataStore().getTempGroupID(procCommand.getGroup().getName()) != null) {
            super.resolveGroup(metadata, procCommand);
        }
    }

    /** 
     * @throws Exception
     */
    @Override
    public Map<ElementSymbol, Expression> getVariableValues(Command command, boolean changingOnly,
                                 IQueryMetadataInterface metadata) throws Exception {
        
        Insert insert = (Insert) command;
        
        Map<ElementSymbol, Expression> result = new HashMap<ElementSymbol, Expression>();
        
        // iterate over the variables and values they should be the same number
        Iterator<ElementSymbol> varIter = insert.getVariables().iterator();
        Iterator valIter = null;
        if (insert.getQueryExpression() != null) {
        	valIter = insert.getQueryExpression().getProjectedSymbols().iterator();
        } else {
            valIter = insert.getValues().iterator();
        }
        while (varIter.hasNext()) {
            ElementSymbol next = varIter.next();
			ElementSymbol varSymbol = next.clone();
            varSymbol.getGroupSymbol().setName(ProcedureReservedWords.CHANGING);
            varSymbol.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            
            Constant constant = createASTNode(ASTNodes.CONSTANT);
            constant.setValue(Boolean.TRUE);
            if (!changingOnly) {
            	varSymbol = next.clone();
            	varSymbol.getGroupSymbol().setName(SQLConstants.Reserved.NEW);
            	result.put(varSymbol, (Expression)valIter.next());
            }
        }
        
        Collection<ElementSymbol> insertElmnts = ResolverUtil.resolveElementsInGroup(insert.getGroup(), metadata);

        insertElmnts.removeAll(insert.getVariables());

        Iterator<ElementSymbol> defaultIter = insertElmnts.iterator();
        while(defaultIter.hasNext()) {
        	ElementSymbol next = defaultIter.next();
 			ElementSymbol varSymbol = next.clone();
            varSymbol.getGroupSymbol().setName(ProcedureReservedWords.CHANGING);
            varSymbol.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            
            Constant constant = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.CONSTANT);
            constant.setValue(Boolean.FALSE);
            result.put(varSymbol, constant);
            if (!changingOnly) {
            	varSymbol = next.clone();
            	Expression value = ResolverUtil.getDefault(varSymbol, metadata);
            	varSymbol.getGroupSymbol().setName(SQLConstants.Reserved.NEW);
            	result.put(varSymbol, value);
            }
        }
        
        return result;
    }
    
}

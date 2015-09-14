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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.api.exception.query.UnresolvedSymbolDescription;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryMetadataInterface.SupportConstants;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IJoinType.Types;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.CommandResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TableFunctionReference;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.navigator.PostOrderNavigator;
import org.teiid.query.sql.navigator.PreOrPostOrderNavigator;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.ExpressionMappingVisitor;
import org.teiid.runtime.client.Messages;

/**
 *
 */
public class SimpleQueryResolver extends CommandResolver {

    /**
     * @param queryResolver
     */
    public SimpleQueryResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    /** 
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter, boolean)
     */
    @Override
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals)
        throws Exception {

    	Query query = (Query) command;
    	
    	resolveWith(metadata, query);
        
        try {
            QueryResolverVisitor qrv = new QueryResolverVisitor(query, metadata);
            qrv.visit(query);
            ResolverVisitor visitor = (ResolverVisitor)qrv.getVisitor();
			visitor.throwException(true);
			if (visitor.hasUserDefinedAggregate() && getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_6.get())) {
				ExpressionMappingVisitor emv = new ExpressionMappingVisitor(getTeiidVersion(), null) {
					@Override
                    public Expression replaceExpression(Expression element) {
						if (element instanceof Function && !(element instanceof AggregateSymbol) && ((Function) element).isAggregate()) {
							Function f = (Function)element;
							AggregateSymbol as = create(ASTNodes.AGGREGATE_SYMBOL);
							as.setName(f.getName());
							as.setDistinct(false);
							as.setArgs(f.getArgs());
							as.setType(f.getType());
							as.setFunctionDescriptor(f.getFunctionDescriptor());
							return as;
						}
						return element;
					}
				};
				PreOrPostOrderNavigator.doVisit(query, emv, PreOrPostOrderNavigator.POST_ORDER);
			}
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception)e.getCause();
            }
            throw e;
        }
                                       
        if (query.getLimit() != null) {
            ResolverUtil.resolveLimit(query.getLimit());
        }
        
        if (query.getOrderBy() != null) {
        	ResolverUtil.resolveOrderBy(query.getOrderBy(), query, metadata);
        }
        
        List<Expression> symbols = query.getSelect().getProjectedSymbols();
        
        if (query.getInto() != null) {
            GroupSymbol symbol = query.getInto().getGroup();
            ResolverUtil.resolveImplicitTempGroup(metadata, symbol, symbols);
        } else if (resolveNullLiterals) {
            ResolverUtil.resolveNullLiterals(symbols);
        }
    }

	/**
	 * @param metadata
	 * @param query
	 * @throws Exception
	 */
	public void resolveWith(TempMetadataAdapter metadata,
			QueryCommand query) throws Exception {
		if (query.getWith() == null) {
			return;
		}
		LinkedHashSet<GroupSymbol> discoveredGroups = new LinkedHashSet<GroupSymbol>();
		for (WithQueryCommand obj : query.getWith()) {
            QueryCommand queryExpression = obj.getCommand();
            
            getQueryResolver().setChildMetadata(queryExpression, query);
            
            
            QueryCommand recursive = null;
            if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_10.get())) {
                try {
                    getQueryResolver().resolveCommand(queryExpression, metadata.getMetadata(), false);
                } catch (QueryResolverException e) {
                    if (!(queryExpression instanceof SetQuery)) {
                        throw e;
                    }
                    SetQuery setQuery = (SetQuery)queryExpression;
                    //valid form must be a union with nothing above
                    if (setQuery.getOperation() != Operation.UNION
                            || setQuery.getLimit() != null 
                            || setQuery.getOrderBy() != null 
                            || setQuery.getOption() != null) {
                        throw e;
                    }
                    getQueryResolver().resolveCommand(queryExpression, metadata.getMetadata(), false);
                    recursive = setQuery.getRightQuery();
                }
            } else {
                getQueryResolver().resolveCommand(queryExpression, metadata.getMetadata(), false);
            }

            if (!discoveredGroups.add(obj.getGroupSymbol())) {
            	 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30101, obj.getGroupSymbol()));
            }
            List<? extends Expression> projectedSymbols = obj.getCommand().getProjectedSymbols();
            if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
            	if (obj.getColumns().size() != projectedSymbols.size()) {
            		 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30102, obj.getGroupSymbol()));
            	}
            	Iterator<ElementSymbol> iter = obj.getColumns().iterator();
            	for (Expression singleElementSymbol : projectedSymbols) {
            		ElementSymbol es = iter.next();
            		es.setType(singleElementSymbol.getType());
				}
            	projectedSymbols = obj.getColumns();
            } 
            TempMetadataID id = ResolverUtil.addTempGroup(metadata, obj.getGroupSymbol(), projectedSymbols, true);
            obj.getGroupSymbol().setMetadataID(metadata.getMetadataStore().getTempGroupID(obj.getGroupSymbol().getName()));
            obj.getGroupSymbol().setIsTempTable(true);
            List<GroupSymbol> groups = Collections.singletonList(obj.getGroupSymbol());
            ResolverVisitor visitor = new ResolverVisitor(obj.getTeiidVersion());
            if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
	            for (Expression singleElementSymbol : projectedSymbols) {
	                visitor.resolveLanguageObject(singleElementSymbol, groups, metadata);
				}
            }
            if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
            	Iterator<ElementSymbol> iter = obj.getColumns().iterator();
                for (TempMetadataID colid : id.getElements()) {
            		ElementSymbol es = iter.next();
            		es.setMetadataID(colid);
            		es.setGroupSymbol(obj.getGroupSymbol());
				}
            }

            if (recursive != null) {
                // Only be not null is version > 10
                getQueryResolver().setChildMetadata(recursive, query);
                getQueryResolver().resolveCommand(recursive, metadata.getMetadata(), false);
                new SetQueryResolver(getQueryResolver()).resolveSetQuery(metadata, false, (SetQuery)queryExpression, ((SetQuery)queryExpression).getLeftQuery(), recursive);
                obj.setRecursive(true);
            }
        }
	}

    private GroupSymbol resolveAllInGroup(MultipleElementSymbol allInGroupSymbol, Set<GroupSymbol> groups, IQueryMetadataInterface metadata) throws Exception {       
        String groupAlias = allInGroupSymbol.getGroup().getName();
        List<GroupSymbol> groupSymbols = ResolverUtil.findMatchingGroups(groupAlias, groups, metadata);
        if(groupSymbols.isEmpty() || groupSymbols.size() > 1) {
            String msg = Messages.getString(groupSymbols.isEmpty() ? Messages.ERR.ERR_015_008_0047 : Messages.QueryResolver.ambiguous_all_in_group, allInGroupSymbol);
            QueryResolverException qre = new QueryResolverException(msg);
            qre.addUnresolvedSymbol(new UnresolvedSymbolDescription(allInGroupSymbol.toString(), msg));
            throw qre;
        }
        GroupSymbol gs = allInGroupSymbol.getGroup();
        allInGroupSymbol.setGroup(groupSymbols.get(0).clone());
        return groupSymbols.get(0);
    }
    
    /**
     *
     */
    public class QueryResolverVisitor extends PostOrderNavigator {

        private LinkedHashSet<GroupSymbol> currentGroups = new LinkedHashSet<GroupSymbol>();
        private LinkedList<GroupSymbol> discoveredGroups = new LinkedList<GroupSymbol>();
        private List<GroupSymbol> implicitGroups = new LinkedList<GroupSymbol>();
        private TempMetadataAdapter metadata;
        private Query query;
        private boolean allowImplicit = true;
        
        /**
         * @param query
         * @param metadata
         */
        public QueryResolverVisitor(Query query, TempMetadataAdapter metadata) {
            super(new ResolverVisitor(query.getTeiidVersion(), metadata, null, query.getExternalGroupContexts()));
            ResolverVisitor visitor = (ResolverVisitor)getVisitor();
            visitor.setGroups(currentGroups);
            this.query = query;
            this.metadata = metadata;
        }
        
        @Override
        protected void postVisitVisitor(LanguageObject obj) {
            super.postVisitVisitor(obj);
            ResolverVisitor visitor = (ResolverVisitor)getVisitor();
            try {
				visitor.throwException(false);
			} catch (Exception e) {
				 throw new RuntimeException(e);
			}
        }
                
        /**
         * Resolving a Query requires a special ordering
         */
        @Override
        public void visit(Query obj) {
            visitNode(obj.getInto());
            visitNode(obj.getFrom());
            visitNode(obj.getCriteria());
            visitNode(obj.getGroupBy());
            visitNode(obj.getHaving());
            visitNode(obj.getSelect());        
            visitNode(obj.getLimit());
        }
        
        @Override
        public void visit(GroupSymbol obj) {
            try {
                ResolverUtil.resolveGroup(obj, metadata);
            } catch (Exception err) {
                 throw new RuntimeException(err);
            }
        }
                        
        private void resolveSubQuery(SubqueryContainer<?> obj, Collection<GroupSymbol> externalGroups) {
            Command command = obj.getCommand();
            
            getQueryResolver().setChildMetadata(command, query);
            command.pushNewResolvingContext(externalGroups);
            
            try {
                getQueryResolver().resolveCommand(command, metadata.getMetadata(), false);
            } catch (Exception err) {
                 throw new RuntimeException(err);
            }
        }
        
        @Override
        public void visit(MultipleElementSymbol obj) {
        	// Determine group that this symbol is for
            try {
                List<ElementSymbol> elementSymbols = new ArrayList<ElementSymbol>();
                Collection<GroupSymbol> groups = currentGroups;
                if (obj.getGroup() != null) {
                	groups = Arrays.asList(resolveAllInGroup(obj, currentGroups, metadata));
                }
                for (GroupSymbol group : groups) {
                    elementSymbols.addAll(resolveSelectableElements(group));
                }
                obj.setElementSymbols(elementSymbols);
            } catch (Exception err) {
                 throw new RuntimeException(err);
            } 
        }

        private List<ElementSymbol> resolveSelectableElements(GroupSymbol group) throws Exception {
            List<ElementSymbol> elements = ResolverUtil.resolveElementsInGroup(group, metadata);
            
            List<ElementSymbol> result = new ArrayList<ElementSymbol>(elements.size());
   
            // Look for elements that are not selectable and remove them
            for (ElementSymbol element : elements) {
                if(metadata.elementSupports(element.getMetadataID(), SupportConstants.Element.SELECT) && !metadata.isPseudo(element.getMetadataID())) {
                    element = element.clone();
                    element.setGroupSymbol(group);
                	result.add(element);
                }
            }
            return result;
        }
        
        @Override
        public void visit(ScalarSubquery obj) {
            resolveSubQuery(obj, this.currentGroups);
        }
        
        @Override
        public void visit(ExistsCriteria obj) {
            resolveSubQuery(obj, this.currentGroups);
        }
        
        @Override
        public void visit(SubqueryCompareCriteria obj) {
            visitNode(obj.getLeftExpression());
            resolveSubQuery(obj, this.currentGroups);
            postVisitVisitor(obj);
        }
        
        @Override
        public void visit(SubquerySetCriteria obj) {
            visitNode(obj.getExpression());
            resolveSubQuery(obj, this.currentGroups);
            postVisitVisitor(obj);
        }
        
        @Override
        public void visit(TextTable obj) {
        	LinkedHashSet<GroupSymbol> saved = preTableFunctionReference(obj);
        	this.visitNode(obj.getFile());
        	try {
				obj.setFile(ResolverUtil.convertExpression(obj.getFile(), DataTypeManagerService.DefaultDataTypes.CLOB.getId(), metadata));
			} catch (Exception e) {
				 throw new RuntimeException(e);
			}
			postTableFunctionReference(obj, saved);
            //set to fixed width if any column has width specified
            for (TextColumn col : obj.getColumns()) {
				if (col.getWidth() != null) {
					obj.setFixedWidth(true);
					break;
				}
			}
        }
        
        @Override
        public void visit(ArrayTable obj) {
        	LinkedHashSet<GroupSymbol> saved = preTableFunctionReference(obj);
        	visitNode(obj.getArrayValue());
			postTableFunctionReference(obj, saved);
        }
        
        @Override
        public void visit(XMLTable obj) {
        	LinkedHashSet<GroupSymbol> saved = preTableFunctionReference(obj);
        	visitNodes(obj.getPassing());
			postTableFunctionReference(obj, saved);
			try {
	    		ResolverUtil.setDesiredType(obj.getPassing(), obj);
				obj.compileXqueryExpression();
				for (XMLColumn column : obj.getColumns()) {
					if (column.getDefaultExpression() == null) {
						continue;
					}
					visitNode(column.getDefaultExpression());
					Expression ex = ResolverUtil.convertExpression(column.getDefaultExpression(), getDataTypeManager().getDataTypeName(column.getSymbol().getType()), metadata);
					column.setDefaultExpression(ex);
				}
			} catch (Exception e) {
				 throw new RuntimeException(e);
			}
        }
        
        @Override
        public void visit(ObjectTable obj) {
        	LinkedHashSet<GroupSymbol> saved = preTableFunctionReference(obj);
        	visitNodes(obj.getPassing());
			postTableFunctionReference(obj, saved);
			try {
	    		ResolverUtil.setDesiredType(obj.getPassing(), obj, DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass());
				for (ObjectColumn column : obj.getColumns()) {
					if (column.getDefaultExpression() == null) {
						continue;
					}
					visitNode(column.getDefaultExpression());
					Expression ex = ResolverUtil.convertExpression(column.getDefaultExpression(), getDataTypeManager().getDataTypeName(column.getSymbol().getType()), metadata);
					column.setDefaultExpression(ex);
				}
			} catch (Exception e) {
				 throw new RuntimeException(e);
			}
        }
        
        /**
		 * @param tfr  
         * @return set of group symbols
		 */
        public LinkedHashSet<GroupSymbol> preTableFunctionReference(TableFunctionReference tfr) {
        	LinkedHashSet<GroupSymbol> saved = new LinkedHashSet<GroupSymbol>(this.currentGroups);
        	if (allowImplicit) {
        		currentGroups.addAll(this.implicitGroups);
        	}
        	return saved;
        }

        /**
         * @param obj
         * @param saved
         */
        public void postTableFunctionReference(TableFunctionReference obj, LinkedHashSet<GroupSymbol> saved) {
			//we didn't create a true external context, so we manually mark external
			for (ElementSymbol symbol : ElementCollectorVisitor.getElements(obj, false)) {
				if (symbol.isExternalReference()) {
					continue;
				}
				if (implicitGroups.contains(symbol.getGroupSymbol())) {
					symbol.setIsExternalReference(true);
				}
			}
			if (allowImplicit) {
	        	this.currentGroups.clear();
	        	this.currentGroups.addAll(saved);
			}
            discoveredGroup(obj.getGroupSymbol());
            try {
                ResolverUtil.addTempGroup(metadata, obj.getGroupSymbol(), obj.getProjectedSymbols(), false);
            } catch (Exception err) {
                 throw new RuntimeException(err);
            }
            obj.getGroupSymbol().setMetadataID(metadata.getMetadataStore().getTempGroupID(obj.getGroupSymbol().getName()));
            //now resolve the projected symbols
            Set<GroupSymbol> groups = new HashSet<GroupSymbol>();
            groups.add(obj.getGroupSymbol());
            ResolverVisitor visitor = new ResolverVisitor(obj.getTeiidVersion());
            for (ElementSymbol symbol : obj.getProjectedSymbols()) {
                try {
					visitor.resolveLanguageObject(symbol, groups, null, metadata);
				} catch (Exception e) {
					 throw new RuntimeException(e);
				}				
			}
        }
        
        @Override
        public void visit(SubqueryFromClause obj) {
        	Collection<GroupSymbol> externalGroups = this.currentGroups;
        	if (obj.isTable() && allowImplicit) {
        		externalGroups = new ArrayList<GroupSymbol>(externalGroups);
        		externalGroups.addAll(this.implicitGroups);
        	}
            resolveSubQuery(obj, externalGroups);
            discoveredGroup(obj.getGroupSymbol());
            try {
                ResolverUtil.addTempGroup(metadata, obj.getGroupSymbol(), obj.getCommand().getProjectedSymbols(), false);
            } catch (Exception err) {
                 throw new RuntimeException(err);
            }
            obj.getGroupSymbol().setMetadataID(metadata.getMetadataStore().getTempGroupID(obj.getGroupSymbol().getName())); 
        }
                        
        @Override
        public void visit(UnaryFromClause obj) {
            GroupSymbol group = obj.getGroup();
            visitNode(group);
            try {
	            if (!group.isProcedure() && metadata.isXMLGroup(group.getMetadataID())) {
	                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30112));
	            }
	            discoveredGroup(group);
	            if (group.isProcedure()) {
	                createProcRelational(obj);
	            }
            } catch(Exception e) {
                 throw new RuntimeException(e);
			}
        }
        
        private void discoveredGroup(GroupSymbol group) {
        	discoveredGroups.add(group);
        	if (allowImplicit) {
        		implicitGroups.add(group);
        	}
        }

		private void createProcRelational(UnaryFromClause obj) throws Exception {
			GroupSymbol group = obj.getGroup();
			String fullName = metadata.getFullName(group.getMetadataID());
			String queryName = group.getName();
			
			IStoredProcedureInfo storedProcedureInfo = metadata.getStoredProcedureInfoForProcedure(fullName);

			StoredProcedure storedProcedureCommand = getTeiidParser().createASTNode(ASTNodes.STORED_PROCEDURE);
			storedProcedureCommand.setProcedureRelational(true);
			storedProcedureCommand.setProcedureName(fullName);
			
			List<SPParameter> metadataParams = storedProcedureInfo.getParameters();
			
			Query procQuery = getTeiidParser().createASTNode(ASTNodes.QUERY);
			From from = getTeiidParser().createASTNode(ASTNodes.FROM);
			SubqueryFromClause subqueryFromClause = getTeiidParser().createASTNode(ASTNodes.SUBQUERY_FROM_CLAUSE);
			subqueryFromClause.setName("X"); //$NON-NLS-1$
			subqueryFromClause.setCommand(storedProcedureCommand);
			from.addClause(subqueryFromClause);
			procQuery.setFrom(from);
			Select select = getTeiidParser().createASTNode(ASTNodes.SELECT);
			MultipleElementSymbol mes = getTeiidParser().createASTNode(ASTNodes.MULTIPLE_ELEMENT_SYMBOL);
			mes.setName("X"); //$NON-NLS-1$
			select.addSymbol(mes);
			procQuery.setSelect(select);
			
			List<String> accessPatternElementNames = new LinkedList<String>();
			
			int paramIndex = 1;
			
			for (SPParameter metadataParameter : metadataParams) {
			    SPParameter clonedParam = metadataParameter.clone();
			    if (clonedParam.getParameterType()==ISPParameter.ParameterInfo.IN.index() || metadataParameter.getParameterType()==ISPParameter.ParameterInfo.INOUT.index()) {
			        ElementSymbol paramSymbol = clonedParam.getParameterSymbol();
			        Reference ref = getTeiidParser().createASTNode(ASTNodes.REFERENCE);
			        ref.setExpression(paramSymbol);
			        clonedParam.setExpression(ref);
			        clonedParam.setIndex(paramIndex++);
			        storedProcedureCommand.setParameter(clonedParam);
			        
			        String aliasName = paramSymbol.getShortName();
			        
			        if (metadataParameter.getParameterType()==ISPParameter.ParameterInfo.INOUT.index()) {
			            aliasName += "_IN"; //$NON-NLS-1$
			        }

			        ExpressionSymbol es = getTeiidParser().createASTNode(ASTNodes.EXPRESSION_SYMBOL);
			        es.setName(paramSymbol.getShortName());
			        es.setExpression(ref);
			        AliasSymbol newSymbol = getTeiidParser().createASTNode(ASTNodes.ALIAS_SYMBOL);
			        newSymbol.setName(aliasName);
			        newSymbol.setSymbol(es);

			        select.addSymbol(newSymbol);
			        accessPatternElementNames.add(queryName + Symbol.SEPARATOR + aliasName);
			    }
			}
			
			getQueryResolver().resolveCommand(procQuery, metadata.getMetadata());
			
			List<Expression> projectedSymbols = procQuery.getProjectedSymbols();
			
			Set<String> foundNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			
			for (Expression ses : projectedSymbols) {
			    if (!foundNames.add(Symbol.getShortName(ses))) {
			         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30114, fullName));
			    }
			}
			
			TempMetadataID id = metadata.getMetadataStore().getTempGroupID(queryName);

			if (id == null) {
			    metadata.getMetadataStore().addTempGroup(queryName, projectedSymbols, true);
			    
			    id = metadata.getMetadataStore().getTempGroupID(queryName);
			    id.setOriginalMetadataID(storedProcedureCommand.getProcedureID());
			    if (!accessPatternElementNames.isEmpty()) {
				    List<TempMetadataID> accessPatternIds = new LinkedList<TempMetadataID>();
				    
				    for (String name : accessPatternElementNames) {
				        accessPatternIds.add(metadata.getMetadataStore().getTempElementID(name));
				    }
				    
				    id.setAccessPatterns(Arrays.asList(new TempMetadataID("procedure access pattern", accessPatternIds))); //$NON-NLS-1$
			    }
			}
			
			group.setMetadataID(id);
			
		    obj.setExpandedCommand(procQuery);
		}
        
        /** 
         * @see org.teiid.query.sql.navigator.PreOrPostOrderNavigator#visit(org.teiid.query.sql.lang.Into)
         */
        @Override
        public void visit(Into obj) {
            if (!obj.getGroup().isImplicitTempGroupSymbol()) {
                super.visit(obj);
            }
        }

        @Override
        public void visit(JoinPredicate obj) {
            assert currentGroups.isEmpty();
        	List<GroupSymbol> tempImplicitGroups = new ArrayList<GroupSymbol>(discoveredGroups);
        	discoveredGroups.clear();
            visitNode(obj.getLeftClause());
            List<GroupSymbol> leftGroups = new ArrayList<GroupSymbol>(discoveredGroups);
        	discoveredGroups.clear();
            visitNode(obj.getRightClause());
            discoveredGroups.addAll(leftGroups);
            addDiscoveredGroups();
            visitNodes(obj.getJoinCriteria());
            discoveredGroups.addAll(currentGroups);
            currentGroups.clear();
            discoveredGroups.addAll(tempImplicitGroups);
        }

		private void addDiscoveredGroups() {
			for (GroupSymbol group : discoveredGroups) {
				if (!this.currentGroups.add(group)) {
	                String msg = Messages.getString(Messages.ERR.ERR_015_008_0046, group.getName());
	                QueryResolverException qre = new QueryResolverException(msg);
                    qre.addUnresolvedSymbol(new UnresolvedSymbolDescription(group.toString(), msg));
	                 throw new RuntimeException(qre);
	            }
			}
            discoveredGroups.clear();
		}
                
        @Override
        public void visit(From obj) {
            assert currentGroups.isEmpty();
            for (FromClause clause : obj.getClauses()) {
				checkImplicit(clause);
			}
            super.visit(obj);
            addDiscoveredGroups();
        }

		private void checkImplicit(FromClause clause) {
			if (clause instanceof JoinPredicate) {
				JoinPredicate jp = (JoinPredicate)clause;
				if (Types.JOIN_FULL_OUTER.equals(jp.getJoinType().getKind()) || Types.JOIN_RIGHT_OUTER.equals(jp.getJoinType().getKind())) {
					allowImplicit = false;
					return;
				}
				checkImplicit(jp.getLeftClause());
				if (allowImplicit) {
					checkImplicit(jp.getRightClause());
				}
			}
		}
		
		@Override
		public void visit(Limit obj) {
			super.visit(obj);
			if (obj.getOffset() != null) {
				ResolverUtil.setTypeIfNull(obj.getOffset(), DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
				try {
					obj.setOffset(ResolverUtil.convertExpression(obj.getOffset(), DataTypeManagerService.DefaultDataTypes.INTEGER.getId(), metadata));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (obj.getRowLimit() != null) {
                ResolverUtil.setTypeIfNull(obj.getRowLimit(), DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
                try {
                    obj.setRowLimit(ResolverUtil.convertExpression(obj.getRowLimit(), DataTypeManagerService.DefaultDataTypes.INTEGER.getId(), metadata));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
		}
    }
}

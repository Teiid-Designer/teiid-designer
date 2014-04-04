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

package org.teiid.query.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.api.exception.query.QueryValidatorException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.IQueryResolver;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.resolver.command.AlterResolver;
import org.teiid.query.resolver.command.DeleteResolver;
import org.teiid.query.resolver.command.DynamicCommandResolver;
import org.teiid.query.resolver.command.ExecResolver;
import org.teiid.query.resolver.command.InsertResolver;
import org.teiid.query.resolver.command.SetQueryResolver;
import org.teiid.query.resolver.command.SimpleQueryResolver;
import org.teiid.query.resolver.command.TempTableResolver;
import org.teiid.query.resolver.command.UpdateProcedureResolver;
import org.teiid.query.resolver.command.UpdateResolver;
import org.teiid.query.resolver.command.XMLQueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.navigator.DeepPostOrderNavigator;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.visitor.ExpressionMappingVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.query.validator.AbstractValidationVisitor;
import org.teiid.query.validator.UpdateValidator;
import org.teiid.query.validator.UpdateValidator.UpdateInfo;
import org.teiid.query.validator.UpdateValidator.UpdateType;
import org.teiid.query.validator.ValidationVisitor;
import org.teiid.query.validator.Validator;
import org.teiid.query.validator.ValidatorFailure;
import org.teiid.query.validator.ValidatorReport;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


/**
 * <P>The QueryResolver is used between Parsing and QueryValidation. The SQL queries,
 * inserts, updates and deletes are parsed and converted into objects. The language
 * objects have variable names which resolved to fully qualified names using metadata
 * information. The resolver is also used in transforming the values in language
 * objects to their variable types defined in metadata.
 */
public class QueryResolver implements IQueryResolver<Command, GroupSymbol, Expression> {

    private final String BINDING_GROUP = "INPUTS"; //$NON-NLS-1$
	private final CommandResolver simpleQueryResolver;
    private final CommandResolver setQueryResolver;
    private final CommandResolver xmlQueryResolver;
    private final ProcedureContainerResolver execResolver;
    private final ProcedureContainerResolver insertResolver;
    private final ProcedureContainerResolver updateResolver;
    private final ProcedureContainerResolver deleteResolver;
    private final CommandResolver updateProcedureResolver;
    private final CommandResolver dynamicCommandResolver;
    private final CommandResolver tempTableResolver;
    private final CommandResolver alterResolver;

    /*
     * The parser that preceded the resolution
     */
    private final QueryParser parser;

    /**
     * @param parser
     */
    public QueryResolver(QueryParser parser) {
        this.parser = parser;

        simpleQueryResolver = new SimpleQueryResolver(this);
        setQueryResolver = new SetQueryResolver(this);
        xmlQueryResolver = new XMLQueryResolver(this);
        execResolver = new ExecResolver(this);
        insertResolver = new InsertResolver(this);
        updateResolver = new UpdateResolver(this);
        deleteResolver = new DeleteResolver(this);
        updateProcedureResolver = new UpdateProcedureResolver(this);
        dynamicCommandResolver = new DynamicCommandResolver(this);
        tempTableResolver = new TempTableResolver(this);
        alterResolver = new AlterResolver(this);
    }

    /**
     * @param teiidVersion
     */
    public QueryResolver(ITeiidServerVersion teiidVersion) {
        this(new QueryParser(teiidVersion));
    }

    /**
     * @return the query parser
     */
    public QueryParser getQueryParser() {
        return parser;
    }

    /**
     * @return teiid parser
     */
    public TeiidParser getTeiidParser() {
        return parser.getTeiidParser();
    }

    /**
     * @return parser teiid version
     */
    public ITeiidServerVersion getTeiidVersion() {
        return getTeiidParser().getVersion();
    }

    protected boolean isTeiidVersionOrGreater(Version teiidVersion) {
        ITeiidServerVersion minVersion = getTeiidVersion().getMinimumVersion();
        return minVersion.equals(teiidVersion.get()) || minVersion.isGreaterThan(teiidVersion.get());
    }

    protected boolean isTeiid8OrGreater() {
        return isTeiidVersionOrGreater(Version.TEIID_8_0);
    }

    protected boolean isTeiid87OrGreater() {
        return isTeiidVersionOrGreater(Version.TEIID_8_7);
    }

    public Command expandCommand(ProcedureContainer proc, IQueryMetadataInterface metadata) throws Exception {
        ProcedureContainerResolver cr = (ProcedureContainerResolver)chooseResolver(proc, metadata);
        Command command = cr.expandCommand(proc, metadata);
        if (command == null) {
            return null;
        }

        if (command instanceof CreateUpdateProcedureCommand) {
            CreateUpdateProcedureCommand cupCommand = (CreateUpdateProcedureCommand)command;
            cupCommand.setUserCommand(proc);
            //if the subcommand is virtual stored procedure, it must have the same
            //projected symbol as its parent.
            if(!cupCommand.isUpdateProcedure()){
                cupCommand.setProjectedSymbols(proc.getProjectedSymbols());
            } 
        }

        resolveCommand(command, proc.getGroup(), proc.getType(), metadata.getDesignTimeMetadata(), false);
        return command;
    }

	/**
	 * This implements an algorithm to resolve all the symbols created by the
	 * parser into real metadata IDs
	 * 
	 * @param command
	 *            Command the SQL command we are running (Select, Update,
	 *            Insert, Delete)
	 * @param metadata
	 *            IQueryMetadataInterface the metadata
	 * @return store of metadata ids representing the resolution of all symbols
	 * @throws Exception
	 */
	public TempMetadataStore resolveCommand(Command command, IQueryMetadataInterface metadata) throws Exception {
		return resolveCommand(command, metadata, true);
	}

	/**
	 * Resolve a command in a given type container and type context.
	 * @param currentCommand
	 * @param container 
	 * @param type The {@link Command} type
	 * @param metadata 
	 * @param inferProcedureResultSetColumns if true and the currentCommand is a procedure definition, then resolving will set the getResultSetColumns on the command to what is discoverable in the procedure body.
	 * @return metadata object store
	 * @throws Exception 
	 */
    public TempMetadataStore resolveCommand(Command currentCommand, GroupSymbol container, int type, IQueryMetadataInterface metadata, boolean inferProcedureResultSetColumns) throws Exception {
    	ResolverUtil.resolveGroup(container, metadata);
    	switch (type) {
	    case ICommand.TYPE_QUERY:
	    	ResolverUtil.resolveGroup(container, metadata);
	        IQueryNode queryNode = metadata.getVirtualPlan(container.getMetadataID());
            
	        return resolveWithBindingMetadata(currentCommand, metadata, queryNode, false);
    	case ICommand.TYPE_INSERT:
    	case ICommand.TYPE_UPDATE:
    	case ICommand.TYPE_DELETE:
    	case ICommand.TYPE_STORED_PROCEDURE:
    		ProcedureContainerResolver.findChildCommandMetadata(this, currentCommand, container, type, metadata, inferProcedureResultSetColumns);
    	}
    	return resolveCommand(currentCommand, metadata, false);
    }

    @Override
    public void resolveCommand(Command command, GroupSymbol gSymbol, int teiidCommandType, IQueryMetadataInterface metadata)
        throws Exception {
        resolveCommand(command, gSymbol, teiidCommandType, metadata, true);
    }

    @Override
    public void postResolveCommand(Command command, GroupSymbol gSymbol, int commandType,
                                   IQueryMetadataInterface metadata, List<Expression> projectedSymbols) {

        if (command instanceof CreateUpdateProcedureCommand) {

            /**
             * This was added to designer to avoid a validation failure, see TEIIDDES-624
             */
            CreateUpdateProcedureCommand updateCommand = (CreateUpdateProcedureCommand) command;

            if (updateCommand.getResultsCommand() instanceof DynamicCommand) {
                DynamicCommand dynamicCommand = (DynamicCommand) updateCommand.getResultsCommand();

                if (dynamicCommand.isAsClauseSet()) {
                    updateCommand.setProjectedSymbols(projectedSymbols);
                }
            }
        }
    }

	/**
	 * Bindings are a poor mans input parameters.  They are represented in legacy metadata
	 * by ElementSymbols and placed positionally into the command or by alias symbols
	 * and matched by names.  After resolving bindings will be replaced with their
	 * referenced symbols (input names will not be used) and those symbols will
	 * be marked as external references.
	 * @param currentCommand 
	 * @param metadata 
	 * @param queryNode 
	 * @param replaceBindings 
	 * @return metadata object store
	 * @throws Exception 
	 */
	public TempMetadataStore resolveWithBindingMetadata(Command currentCommand,
			IQueryMetadataInterface metadata, IQueryNode queryNode, boolean replaceBindings)
			throws Exception {
		Map<ElementSymbol, ElementSymbol> symbolMap = null;
		TeiidParser teiidParser = parser.getTeiidParser();

		if (queryNode.getBindings() != null && queryNode.getBindings().size() > 0) {
			symbolMap = new HashMap<ElementSymbol, ElementSymbol>();

		    // Create ElementSymbols for each InputParameter
		    final List<ElementSymbol> elements = new ArrayList<ElementSymbol>(queryNode.getBindings().size());
		    boolean positional = true;
		    for (Expression ses : parseBindings(queryNode)) {
		    	String name = Symbol.getShortName(ses);
		    	if (ses instanceof AliasSymbol) {
		    		ses = ((AliasSymbol)ses).getSymbol();
		    		positional = false;
		    	}
		    	ElementSymbol elementSymbol = (ElementSymbol)ses;
		    	ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
		    	visitor.resolveLanguageObject(elementSymbol, metadata);
		    	elementSymbol.setIsExternalReference(true);
		    	if (!positional) {
		    	    if (isTeiid87OrGreater()) {
		    	        ElementSymbol inputSymbol = teiidParser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
		    	        inputSymbol.setName("INPUT" + Symbol.SEPARATOR + name); //$NON-NLS-1$
		    	        inputSymbol.setType(elementSymbol.getType());
		    	        symbolMap.put(inputSymbol, elementSymbol.clone());
		    	    }
		    	    
		    	    ElementSymbol keySymbol = teiidParser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
		    	    keySymbol.setName(BINDING_GROUP + Symbol.SEPARATOR + name);
		    		symbolMap.put(keySymbol, elementSymbol.clone());
		    		elementSymbol.setShortName(name);
		    	}
		        elements.add(elementSymbol);
		    }
		    if (positional) {
		    	ExpressionMappingVisitor emv = new ExpressionMappingVisitor(getTeiidVersion(), null) {
		    		@Override
		    		public Expression replaceExpression(Expression element) {
			    		if (!(element instanceof Reference)) {
			    			return element;
			    		}
			    		Reference ref = (Reference)element;
			    		if (!ref.isPositional()) {
			    			return ref;
			    		}
			    		return elements.get(ref.getIndex()).clone();
		    		}
		    	};
		    	DeepPostOrderNavigator.doVisit(currentCommand, emv);
		    } else {
		        TempMetadataStore rootExternalStore = new TempMetadataStore();
		        
		        GroupContext externalGroups = new GroupContext();
		        
		        ProcedureContainerResolver.addScalarGroup(parser.getTeiidParser(), "INPUT", rootExternalStore, externalGroups, elements); //$NON-NLS-1$
		        ProcedureContainerResolver.addScalarGroup(parser.getTeiidParser(), BINDING_GROUP, rootExternalStore, externalGroups, elements);
		        setChildMetadata(currentCommand, rootExternalStore, externalGroups);
		    }
		}
		TempMetadataStore result = resolveCommand(currentCommand, metadata, false);
		if (replaceBindings && symbolMap != null && !symbolMap.isEmpty()) {
			ExpressionMappingVisitor emv = new ExpressionMappingVisitor(getTeiidVersion(), symbolMap);
			DeepPostOrderNavigator.doVisit(currentCommand, emv);
		}
		return result;
	}

	/**
	 * Bindings are a poor mans input parameters.  They are represented in legacy metadata
	 * by ElementSymbols and placed positionally into the command or by alias symbols
	 * and matched by names.
	 * @param planNode
	 * @return
	 * @throws Exception
	 */
    public List<Expression> parseBindings(IQueryNode planNode) throws Exception {
        Collection<String> bindingsCol = planNode.getBindings();
        if (bindingsCol == null) {
            return Collections.emptyList();
        }
        
        List<Expression> parsedBindings = new ArrayList<Expression>(bindingsCol.size());
        for (Iterator<String> bindings=bindingsCol.iterator(); bindings.hasNext();) {
            try {
                Expression binding = parser.parseSelectExpression(bindings.next());
                parsedBindings.add(binding);
            } catch (Exception err) {
                 throw new TeiidClientException(err, Messages.getString(Messages.TEIID.TEIID30063));
            }
        }
        return parsedBindings;
    }

    public TempMetadataStore resolveCommand(Command currentCommand, IQueryMetadataInterface metadata, boolean resolveNullLiterals)
        throws Exception {

//        TODO
//		LogManager.logTrace(org.teiid.logging.LogConstants.CTX_QUERY_RESOLVER, new Object[]{"Resolving command", currentCommand}); //$NON-NLS-1$
        
        TempMetadataAdapter resolverMetadata = null;
        try {
        	TempMetadataStore discoveredMetadata = currentCommand.getTemporaryMetadata();
            if(discoveredMetadata == null) {
            	discoveredMetadata = new TempMetadataStore();
                currentCommand.setTemporaryMetadata(discoveredMetadata);
            }
            
            resolverMetadata = new TempMetadataAdapter(metadata, discoveredMetadata);
            
            // Resolve external groups for command
            Collection<GroupSymbol> externalGroups = currentCommand.getAllExternalGroups();
            for (GroupSymbol extGroup : externalGroups) {
                Object metadataID = extGroup.getMetadataID();
                //make sure that the group is resolved and that it is pointing to the appropriate temp group
                //TODO: this is mainly for XML resolving since it sends external groups in unresolved
                if (metadataID == null || (!(extGroup.getMetadataID() instanceof TempMetadataID) && discoveredMetadata.getTempGroupID(extGroup.getName()) != null)) {
                    metadataID = resolverMetadata.getGroupID(extGroup.getName());
                    extGroup.setMetadataID(metadataID);
                }
            }

            CommandResolver resolver = chooseResolver(currentCommand, resolverMetadata);

            // Resolve this command
            resolver.resolveCommand(currentCommand, resolverMetadata, resolveNullLiterals);            
        } catch(Exception e) {
             throw new QueryResolverException(e);
        }

        // Flag that this command has been resolved.
        currentCommand.setIsResolved(true);
        
        return resolverMetadata.getMetadataStore();
    }

    /**
     * Method chooseResolver.
     * @param command
     * @param metadata
     * @return CommandResolver
     */
    private CommandResolver chooseResolver(Command command, IQueryMetadataInterface metadata)
        throws Exception {

        switch(command.getType()) {
            case ICommand.TYPE_QUERY:
                if(command instanceof Query) {
                    if(isXMLQuery((Query)command, metadata)) {
                        return xmlQueryResolver;
                    }
                    return simpleQueryResolver;
                }
                return setQueryResolver;
            case ICommand.TYPE_INSERT:               return insertResolver;
            case ICommand.TYPE_UPDATE:               return updateResolver;
            case ICommand.TYPE_DELETE:               return deleteResolver;
            case ICommand.TYPE_STORED_PROCEDURE:     return execResolver;
            case ICommand.TYPE_TRIGGER_ACTION:		return updateProcedureResolver;
            case ICommand.TYPE_UPDATE_PROCEDURE:     return updateProcedureResolver;
//            case ICommand.TYPE_BATCHED_UPDATE:       return batchedUpdateResolver;
            case ICommand.TYPE_DYNAMIC:              return dynamicCommandResolver;
            case ICommand.TYPE_CREATE:               return tempTableResolver;
            case ICommand.TYPE_DROP:                 return tempTableResolver;
            case ICommand.TYPE_ALTER_PROC:           
            case ICommand.TYPE_ALTER_TRIGGER:        
            case ICommand.TYPE_ALTER_VIEW:           return alterResolver;
            default:
                throw new AssertionError("Unknown command type"); //$NON-NLS-1$
        }
    }

    /**
     * Check to verify if the query would return XML results.
     * @param query the query to check
     * @param metadata IQueryMetadataInterface the metadata
     * @return true if query is xml query, false otherwise
     * @throws Exception 
     */
    public boolean isXMLQuery(Query query, IQueryMetadataInterface metadata)
     throws Exception {

        if (query.getWith() != null) {
        	return false;
        }

        // Check first group
        From from = query.getFrom();
        if(from == null){
            //select with no from
            return false;
        }
                
        if (from.getClauses().size() != 1) {
            return false;
        }
        
        FromClause clause = from.getClauses().get(0);
        
        if (!(clause instanceof UnaryFromClause)) {
            return false;
        }
        
        GroupSymbol symbol = ((UnaryFromClause)clause).getGroup();
        
        ResolverUtil.resolveGroup(symbol, metadata);
                
        if (symbol.isProcedure()) {
            return false;
        }
        
        Object groupID = ((UnaryFromClause)clause).getGroup().getMetadataID();

        return metadata.isXMLGroup(groupID);
    }
    
    /**
     * Resolve just a criteria.  The criteria will be modified so nothing is returned.
     * @param criteria Criteria to resolve
     * @param metadata Metadata implementation
     * @throws Exception
     */
    public void resolveCriteria(Criteria criteria, IQueryMetadataInterface metadata)
        throws Exception {
        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());
        visitor.resolveLanguageObject(criteria, metadata);
    }

    public void setChildMetadata(Command subCommand, Command parent) {
    	TempMetadataStore childMetadata = parent.getTemporaryMetadata();
        GroupContext parentContext = parent.getExternalGroupContexts();
        
        setChildMetadata(subCommand, childMetadata, parentContext);
    }
    
    public void setChildMetadata(Command subCommand, TempMetadataStore parentTempMetadata, GroupContext parentContext) {
    	TempMetadataStore tempMetadata = subCommand.getTemporaryMetadata();
        if(tempMetadata == null) {
            subCommand.setTemporaryMetadata(parentTempMetadata.clone());
        } else {
            tempMetadata.getData().putAll(parentTempMetadata.getData());
        }
    
        subCommand.setExternalGroupContexts(parentContext);
    }
    
    public Map<ElementSymbol, Expression> getVariableValues(Command command, boolean changingOnly, IQueryMetadataInterface metadata) throws Exception {
        
        CommandResolver resolver = chooseResolver(command, metadata);
        
        if (resolver instanceof VariableResolver) {
            return ((VariableResolver)resolver).getVariableValues(command, changingOnly, metadata);
        }
        
        return Collections.emptyMap();
    }
    
	public void resolveSubqueries(Command command,
			TempMetadataAdapter metadata, Collection<GroupSymbol> externalGroups)
			throws Exception {
		for (SubqueryContainer container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(command)) {
            setChildMetadata(container.getCommand(), command);
            if (externalGroups != null) {
            	container.getCommand().pushNewResolvingContext(externalGroups);
            }
            resolveCommand(container.getCommand(), metadata.getMetadata(), false);
        }
	}

    public static void validateWithVisitor(AbstractValidationVisitor visitor, IQueryMetadataInterface metadata, Command command)
        throws Exception {

        // Validate with visitor
        ValidatorReport report = Validator.validate(command, metadata, visitor);
        if (report.hasItems()) {
            ValidatorFailure firstFailure = report.getItems().iterator().next();
            throw new TeiidClientException(firstFailure.getMessage());
        }
    }

	public QueryNode resolveView(GroupSymbol virtualGroup, IQueryNode qnode,
			String cacheString, IQueryMetadataInterface qmi) throws Exception {
		qmi = qmi.getDesignTimeMetadata();
		cacheString = "transformation/" + cacheString; //$NON-NLS-1$
		QueryNode cachedNode = (QueryNode)qmi.getFromMetadataCache(virtualGroup.getMetadataID(), cacheString);
        if (cachedNode == null) {
        	Command result = (Command) qnode.getCommand();
        	List<String> bindings = null;
            if (result == null) {
                try {
                	result = getQueryParser().parseCommand(qnode.getQuery());
                } catch(Exception e) {
                     throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30065, virtualGroup));
                }
                
                bindings = qnode.getBindings();
            } else {
            	result = result.clone();
            }
            if (bindings != null && !bindings.isEmpty()) {
            	resolveWithBindingMetadata(result, qmi, qnode, true);
            } else {
            	resolveCommand(result, qmi, false);
            }
	        validateWithVisitor(new ValidationVisitor(getTeiidVersion()), qmi, result);

	        validateProjectedSymbols(virtualGroup, qmi, result);
            cachedNode = new QueryNode(qnode.getQuery());
            cachedNode.setCommand(result);
	        
			if(isView(virtualGroup, qmi)) {
		        String updatePlan = qmi.getUpdatePlan(virtualGroup.getMetadataID());
				String deletePlan = qmi.getDeletePlan(virtualGroup.getMetadataID());
				String insertPlan = qmi.getInsertPlan(virtualGroup.getMetadataID());
				//the elements must be against the view and not the alias
				if (virtualGroup.getDefinition() != null) {
					GroupSymbol group = getTeiidParser().createASTNode(ASTNodes.GROUP_SYMBOL);
					group.setName(virtualGroup.getNonCorrelationName());
					group.setMetadataID(virtualGroup.getMetadataID());
					virtualGroup = group;
				}
	            List<ElementSymbol> elements = ResolverUtil.resolveElementsInGroup(virtualGroup, qmi);
	    		UpdateValidator validator = new UpdateValidator(qmi, determineType(insertPlan), determineType(updatePlan), determineType(deletePlan));
				validator.validate(result, elements);
	    		UpdateInfo info = validator.getUpdateInfo();
	    		cachedNode.setUpdateInfo(info);
			}
	        qmi.addToMetadataCache(virtualGroup.getMetadataID(), cacheString, cachedNode);
        }
		return cachedNode;
	}

	public void validateProjectedSymbols(GroupSymbol virtualGroup,
			IQueryMetadataInterface qmi, Command result)
			throws QueryValidatorException, Exception {
		//ensure that null types match the view
		List<ElementSymbol> symbols = ResolverUtil.resolveElementsInGroup(virtualGroup, qmi);
		List<Expression> projectedSymbols = result.getProjectedSymbols();
		validateProjectedSymbols(virtualGroup, symbols, projectedSymbols);
	}

	public void validateProjectedSymbols(GroupSymbol virtualGroup,
			List<? extends Expression> symbols,
			List<? extends Expression> projectedSymbols)
			throws QueryValidatorException {
		if (symbols.size() != projectedSymbols.size()) {
			 throw new QueryValidatorException(Messages.gs(Messages.TEIID.TEIID30066, virtualGroup, symbols.size(), projectedSymbols.size()));
		}
		DataTypeManagerService dataTypeManager = DataTypeManagerService.getInstance(getTeiidVersion());
		for (int i = 0; i < projectedSymbols.size(); i++) {
			Expression projectedSymbol = projectedSymbols.get(i);
			
			ResolverUtil.setTypeIfNull(projectedSymbol, symbols.get(i).getType());
			
			if (projectedSymbol.getType() != symbols.get(i).getType()) {
                String symbolTypeName = dataTypeManager.getDataTypeName(symbols.get(i).getType());
			    String projSymbolTypeName = dataTypeManager.getDataTypeName(projectedSymbol.getType());
			    
				throw new QueryValidatorException(Messages.getString(Messages.QueryResolver.wrong_view_symbol_type, virtualGroup, i+1, symbolTypeName, projSymbolTypeName));
			}
		}
	}

	public boolean isView(GroupSymbol virtualGroup,
			IQueryMetadataInterface qmi) throws Exception {
		return !(virtualGroup.getMetadataID() instanceof TempMetadataID) && qmi.isVirtualGroup(virtualGroup.getMetadataID());// && qmi.isVirtualModel(qmi.getModelID(virtualGroup.getMetadataID()));
	}
	
	private UpdateType determineType(String plan) {
		UpdateType type = UpdateType.INHERENT;
		if (plan != null) {
		    type = UpdateType.INSTEAD_OF;
		}
		return type;
	}
	
}

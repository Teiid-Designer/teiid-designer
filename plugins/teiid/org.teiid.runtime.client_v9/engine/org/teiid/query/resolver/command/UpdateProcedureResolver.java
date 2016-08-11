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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.api.exception.query.UnresolvedSymbolDescription;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryMetadataInterface.SupportConstants;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.CommandResolver;
import org.teiid.query.resolver.ProcedureContainerResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.ExpressionStatement;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.ReturnStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.proc.Statement.StatementType;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.util.SymbolMap;
import org.teiid.query.sql.visitor.ResolveVirtualGroupCriteriaVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.runtime.client.Messages;

/**
 */
public class UpdateProcedureResolver extends CommandResolver {

    private final List<ElementSymbol> exceptionGroup;

    private DataTypeManagerService dataTypeManager;

    /**
     * @param queryResolver
     */
    public UpdateProcedureResolver(QueryResolver queryResolver) {
        super(queryResolver);

        ElementSymbol es1 = create(ASTNodes.ELEMENT_SYMBOL);
        es1.setName("STATE"); //$NON-NLS-1$
        es1.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());

        ElementSymbol es2 = create(ASTNodes.ELEMENT_SYMBOL);
        es2.setName("ERRORCODE"); //$NON-NLS-1$
        es2.setType(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());

        ElementSymbol es3 = create(ASTNodes.ELEMENT_SYMBOL);
        es3.setName("TEIIDCODE"); //$NON-NLS-1$
        es3.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());

        ElementSymbol es4 = create(ASTNodes.ELEMENT_SYMBOL);
        es4.setName(NonReserved.EXCEPTION);
        es4.setType(Exception.class);

        ElementSymbol es5 = create(ASTNodes.ELEMENT_SYMBOL);
        es5.setName(NonReserved.CHAIN);
        es5.setType(Exception.class);

        exceptionGroup = Arrays.asList(es1, es2, es3, es4, es5);
    }

    /**
     * @return the dataTypeManager
     */
    @Override
    public DataTypeManagerService getDataTypeManager() {
        if (dataTypeManager == null)
            dataTypeManager = DataTypeManagerService.getInstance(getTeiidVersion());

        return this.dataTypeManager;
    }

    /**
     * @param command
     * @param metadata
     */
    private void addRowCountToContext(Command command, TempMetadataAdapter metadata) throws Exception {
        //by creating a new group context here it means that variables will resolve with a higher precedence than input/changing
        GroupContext externalGroups = command.getExternalGroupContexts();

        List<ElementSymbol> symbols = new LinkedList<ElementSymbol>();

        //
        // Only applicable for teiid 7
        //
        if (command instanceof CreateUpdateProcedureCommand && ((CreateUpdateProcedureCommand)command).isUpdateProcedure()) {
        	throw new UnsupportedOperationException();
        }
        //
        // End of Only applicable to teiid 7
        //

        String countVar = ProcedureReservedWords.VARIABLES + Symbol.SEPARATOR + ProcedureReservedWords.ROWCOUNT;
        ElementSymbol updateCount = create(ASTNodes.ELEMENT_SYMBOL);
        updateCount.setName(countVar);
        updateCount.setType(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
        symbols.add(updateCount);

        ProcedureContainerResolver.addScalarGroup(getTeiidParser(),
                                                  ProcedureReservedWords.VARIABLES,
                                                  metadata.getMetadataStore(),
                                                  externalGroups,
                                                  symbols);
    }

    private void resolveCommand(TriggerAction ta, TempMetadataAdapter metadata, boolean resolveNullLiterals) throws Exception {

        ICreateProcedureCommand<Block, GroupSymbol, Expression, LanguageVisitor> cmd;
        cmd = create(ASTNodes.CREATE_PROCEDURE_COMMAND);
        //TODO: this is not generally correct - we should update the api to set the appropriate type
        ((CreateProcedureCommand)cmd).setUpdateType(ICommand.TYPE_INSERT);

        cmd.setBlock(ta.getBlock());
        cmd.setVirtualGroup(ta.getView());

        resolveBlock(cmd, ta.getBlock(), ta.getExternalGroupContexts(), metadata);
    }

    // removed in 8.0
    @Deprecated
    private void resolveVirtualGroupElements(CreateUpdateProcedureCommand procCommand, IQueryMetadataInterface metadata)
        throws Exception {
    		throw new UnsupportedOperationException();
    }

    /**
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, TempMetadataAdapter, boolean)
     */
    @Override
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals) throws Exception {

        addRowCountToContext(command, metadata);

        if (command instanceof CreateProcedureCommand)
            resolveBlock((CreateProcedureCommand) command, ((CreateProcedureCommand) command).getBlock(), command.getExternalGroupContexts(), metadata);
        else if (command instanceof TriggerAction)
            resolveCommand((TriggerAction)command, metadata, resolveNullLiterals);
        else
            throw new IllegalArgumentException();
    }

    /**
     * @param command
     * @param block
     * @param originalExternalGroups
     * @param metadata
     * @throws Exception
     */
    public void resolveBlock(ICreateProcedureCommand<Block, GroupSymbol, Expression, LanguageVisitor> command, Block block, GroupContext originalExternalGroups, TempMetadataAdapter metadata)
        throws Exception {

        //create a new variable and metadata context for this block so that discovered metadata is not visible else where
        TempMetadataStore store = metadata.getMetadataStore().clone();
        metadata = new TempMetadataAdapter(metadata.getMetadata(), store);
        GroupContext externalGroups = new GroupContext(originalExternalGroups, null);

        //create a new variables group for this block
        GroupSymbol variables = ProcedureContainerResolver.addScalarGroup(getTeiidParser(),
                                                                          ProcedureReservedWords.VARIABLES,
                                                                          store,
                                                                          externalGroups,
                                                                          new LinkedList<Expression>());

        for (Statement statement : block.getStatements()) {
            resolveStatement(command, statement, externalGroups, variables, metadata);
        }

        if (block.getExceptionGroup() != null) {
            //create a new variable and metadata context for this block so that discovered metadata is not visible else where
            store = metadata.getMetadataStore().clone();
            metadata = new TempMetadataAdapter(metadata.getMetadata(), store);
            externalGroups = new GroupContext(originalExternalGroups, null);

            //create a new variables group for this block
            variables = ProcedureContainerResolver.addScalarGroup(getTeiidParser(),
                                                                  ProcedureReservedWords.VARIABLES,
                                                                  store,
                                                                  externalGroups,
                                                                  new LinkedList<Expression>());
            isValidGroup(metadata, block.getExceptionGroup());

            if (block.getExceptionStatements() != null) {
                ProcedureContainerResolver.addScalarGroup(getTeiidParser(),
                                                          block.getExceptionGroup(),
                                                          store,
                                                          externalGroups,
                                                          exceptionGroup,
                                                          false);
                for (Statement statement : block.getExceptionStatements()) {
                    resolveStatement(command, statement, externalGroups, variables, metadata);
                }
            }
        }
    }

    // Removed in Teiid 8.0
    @Deprecated
    private void resolveStatement(CreateUpdateProcedureCommand command, Statement statement, GroupContext externalGroups, GroupSymbol variables, TempMetadataAdapter metadata)
        throws Exception {
    	throw new UnsupportedOperationException();
    }

    @SuppressWarnings( "incomplete-switch" )
    private void resolveStatement(CreateProcedureCommand command, Statement statement, GroupContext externalGroups, GroupSymbol variables, TempMetadataAdapter metadata)
        throws Exception {
        ResolverVisitor visitor = new ResolverVisitor(getTeiidVersion());

        switch (statement.getType()) {
            case TYPE_IF:
                IfStatement ifStmt = (IfStatement)statement;
                Criteria ifCrit = ifStmt.getCondition();
                for (SubqueryContainer container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(ifCrit)) {
                    resolveEmbeddedCommand(metadata, externalGroups, container.getCommand());
                }
                visitor.resolveLanguageObject(ifCrit, null, externalGroups, metadata);
                resolveBlock(command, ifStmt.getIfBlock(), externalGroups, metadata);
                if (ifStmt.hasElseBlock()) {
                    resolveBlock(command, ifStmt.getElseBlock(), externalGroups, metadata);
                }
                break;
            case TYPE_COMMAND:
                CommandStatement cmdStmt = (CommandStatement)statement;
                Command subCommand = cmdStmt.getCommand();

                TempMetadataStore discoveredMetadata = resolveEmbeddedCommand(metadata, externalGroups, subCommand);

                if (subCommand instanceof StoredProcedure) {
                    StoredProcedure sp = (StoredProcedure)subCommand;
                    for (SPParameter param : sp.getParameters()) {
                        ISPParameter.ParameterInfo paramType = ISPParameter.ParameterInfo.valueOf(param.getParameterType());
                        switch (paramType) {
                            case OUT:
                            case RETURN_VALUE:
            	            	if (param.getExpression() != null) {
            	            		if (!isAssignable(metadata, param)) {
	                                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30121, param.getExpression()));
	                                }
	                                sp.setCallableStatement(true);
            	            	}
                                break;
                            case INOUT:
                                if (!isAssignable(metadata, param)) {
                                    continue;
                                }
                                sp.setCallableStatement(true);
                                break;
                        }
                    }
                }

                if (discoveredMetadata != null) {
                    metadata.getMetadataStore().getData().putAll(discoveredMetadata.getData());
                }

                //dynamic commands need to be updated as to their implicitly expected projected symbols 
                if (subCommand instanceof DynamicCommand) {
                    DynamicCommand dynCommand = (DynamicCommand)subCommand;

                    if (dynCommand.getIntoGroup() == null && !dynCommand.isAsClauseSet()) {
                        if ((command.getResultSetColumns() != null && command.getResultSetColumns().isEmpty())
                            || !cmdStmt.isReturnable() || command.getResultSetColumns() == null) {
                            //we're not interested in the resultset
                            dynCommand.setAsColumns(Collections.EMPTY_LIST);
                        } else {
                            //should match the procedure
                            dynCommand.setAsColumns(command.getResultSetColumns());
                        }
                    }
                }

                if (command.getResultSetColumns() == null && cmdStmt.isReturnable() && subCommand.returnsResultSet()
                    && subCommand.getResultSetColumns() != null && !subCommand.getResultSetColumns().isEmpty()) {
                    command.setResultSetColumns(subCommand.getResultSetColumns());
                	if (command.getProjectedSymbols().isEmpty()) {
                		command.setProjectedSymbols(subCommand.getResultSetColumns());
                	}
                }

                break;
            case TYPE_ERROR:
            case TYPE_ASSIGNMENT:
            case TYPE_DECLARE:
            case TYPE_RETURN:
                ExpressionStatement exprStmt = (ExpressionStatement)statement;
                //first resolve the value.  this ensures the value cannot use the variable being defined
                if (exprStmt.getExpression() != null) {
                    Expression expr = exprStmt.getExpression();
                    for (SubqueryContainer container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(expr)) {
                        resolveEmbeddedCommand(metadata, externalGroups, container.getCommand());
                    }
                    visitor.resolveLanguageObject(expr, null, externalGroups, metadata);
                }

                //second resolve the variable
                switch (statement.getType()) {
                    case TYPE_DECLARE:
                        collectDeclareVariable((DeclareStatement)statement, variables, metadata, externalGroups);
                        break;
                    case TYPE_ASSIGNMENT:
                        AssignmentStatement assStmt = (AssignmentStatement)statement;
                        visitor.resolveLanguageObject(assStmt.getVariable(), null, externalGroups, metadata);
                        if (!metadata.elementSupports(assStmt.getVariable().getMetadataID(), SupportConstants.Element.UPDATE)) {
                            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30121, assStmt.getVariable()));
                        }
                        //don't allow variable assignments to be external
                        assStmt.getVariable().setIsExternalReference(false);
                        break;
                    case TYPE_RETURN:
                        ReturnStatement rs = (ReturnStatement)statement;
                        if (rs.getExpression() != null) {
                            if (command.getReturnVariable() == null) {
                                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31125, rs));
                            }
                            rs.setVariable(command.getReturnVariable().clone());
                        }
                        //else - we don't currently require the use of return for backwards compatibility
                        break;
                }

                //third ensure the type matches
                if (exprStmt.getExpression() != null) {
                    Class<?> varType = exprStmt.getExpectedType();
                    Class<?> exprType = exprStmt.getExpression().getType();
                    if (exprType == null) {
                        throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30123));
                    }
                    String varTypeName = getDataTypeManager().getDataTypeName(varType);
                    exprStmt.setExpression(ResolverUtil.convertExpression(exprStmt.getExpression(), varTypeName, metadata));
                    if (statement.getType() == StatementType.TYPE_ERROR) {
                        ResolverVisitor.checkException(exprStmt.getExpression());
                    }
                }
                break;
            case TYPE_WHILE:
                WhileStatement whileStmt = (WhileStatement)statement;
                Criteria whileCrit = whileStmt.getCondition();
                for (SubqueryContainer container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(whileCrit)) {
                    resolveEmbeddedCommand(metadata, externalGroups, container.getCommand());
                }
                visitor.resolveLanguageObject(whileCrit, null, externalGroups, metadata);
                resolveBlock(command, whileStmt.getBlock(), externalGroups, metadata);
                break;
            case TYPE_LOOP:
                LoopStatement loopStmt = (LoopStatement)statement;
                String groupName = loopStmt.getCursorName();

                isValidGroup(metadata, groupName);
                Command cmd = loopStmt.getCommand();
                resolveEmbeddedCommand(metadata, externalGroups, cmd);
                List<Expression> symbols = cmd.getProjectedSymbols();

                //add the loop cursor group into its own context
                TempMetadataStore store = metadata.getMetadataStore().clone();
                metadata = new TempMetadataAdapter(metadata.getMetadata(), store);
                externalGroups = new GroupContext(externalGroups, null);

                ProcedureContainerResolver.addScalarGroup(getTeiidParser(), groupName, store, externalGroups, symbols, false);

                resolveBlock(command, loopStmt.getBlock(), externalGroups, metadata);
                break;
            case TYPE_COMPOUND:
                resolveBlock(command, (Block)statement, externalGroups, metadata);
                break;
        }
    }

    private void resolveStatement(ICreateProcedureCommand command, Statement statement, GroupContext externalGroups, GroupSymbol variables, TempMetadataAdapter metadata)
        throws Exception {

        if (command instanceof CreateProcedureCommand)
            resolveStatement((CreateProcedureCommand)command, statement, externalGroups, variables, metadata);
        else
            throw new IllegalArgumentException();
    }

    private void isValidGroup(TempMetadataAdapter metadata, String groupName) throws Exception {
        if (metadata.getMetadataStore().getTempGroupID(groupName) != null) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30124, groupName));
        }

        //check - cursor name should not start with #
        if (GroupSymbol.isTempGroupName(groupName)) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30125, groupName));
        }
    }

    private boolean isAssignable(TempMetadataAdapter metadata, SPParameter param) throws Exception {
        if (!(param.getExpression() instanceof ElementSymbol)) {
            return false;
        }
        ElementSymbol symbol = (ElementSymbol)param.getExpression();

        return metadata.elementSupports(symbol.getMetadataID(), SupportConstants.Element.UPDATE);
    }

    private TempMetadataStore resolveEmbeddedCommand(TempMetadataAdapter metadata, GroupContext groupContext, Command cmd)
        throws Exception {
        getQueryResolver().setChildMetadata(cmd, metadata.getMetadataStore(), groupContext);

        return getQueryResolver().resolveCommand(cmd, metadata.getMetadata());
    }

    private void collectDeclareVariable(DeclareStatement obj, GroupSymbol variables, TempMetadataAdapter metadata, GroupContext externalGroups)
        throws Exception {
        ElementSymbol variable = obj.getVariable();
        String typeName = obj.getVariableType();
        GroupSymbol gs = variable.getGroupSymbol();
        if (gs == null) {
            String outputName = variable.getShortName();
            gs = create(ASTNodes.GROUP_SYMBOL);
            gs.setName(ProcedureReservedWords.VARIABLES);
            variable.setGroupSymbol(gs);
            variable.setOutputName(outputName);
        } else {
            if (gs.getSchema() != null || !gs.getShortName().equalsIgnoreCase(ProcedureReservedWords.VARIABLES)) {
                handleUnresolvableDeclaration(variable,
                                              Messages.getString(Messages.ERR.ERR_015_010_0031, new Object[] {
                                                  ProcedureReservedWords.VARIABLES, variable}));
            }
        }
        boolean exists = false;
        try {
            ResolverVisitor visitor = new ResolverVisitor(variable.getTeiidVersion());
            visitor.resolveLanguageObject(variable, null, externalGroups, metadata);
            exists = true;
        } catch (Exception e) {
            //ignore, not already defined
        }
        if (exists) {
            handleUnresolvableDeclaration(variable, Messages.getString(Messages.ERR.ERR_015_010_0032, variable.getOutputName()));
        }
        variable.setType(getDataTypeManager().getDataTypeClass(typeName));
        variable.setGroupSymbol(variables);
        TempMetadataID id = new TempMetadataID(
                                               variable.getName(),
                                               typeName.equalsIgnoreCase(SQLConstants.NonReserved.EXCEPTION) ? Exception.class : variable.getType());
        id.setUpdatable(true);
        variable.setMetadataID(id);
        //TODO: this will cause the variables group to loose it's cache of resolved symbols
        metadata.getMetadataStore().addElementToTempGroup(ProcedureReservedWords.VARIABLES, variable.clone());
    }

    private void handleUnresolvableDeclaration(ElementSymbol variable, String description) throws QueryResolverException {
        UnresolvedSymbolDescription symbol = new UnresolvedSymbolDescription(variable.toString(), description);
        QueryResolverException e = new QueryResolverException(symbol.getDescription());
        e.setUnresolvedSymbols(Arrays.asList(new Object[] {symbol}));
        throw e;
    }

}

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.query;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IJoinType;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISPParameter.ParameterInfo;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IElementSymbol.DisplayMode;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryCompareCriteria.PredicateQuantifier;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;

/**
 *
 */
public class SyntaxFactory implements IQueryFactory <Expression, 
                                                                                                 Expression,
                                                                                                 FromClause,
                                                                                                 ElementSymbol,
                                                                                                 Command,
                                                                                                 QueryCommand,
                                                                                                 Criteria,
                                                                                                 Constant,
                                                                                                 Block,
                                                                                                 Expression,
                                                                                                 GroupSymbol,
                                                                                                 JoinType> {

    private TeiidParser teiidParser;

    private final TeiidNodeFactory nodeFactory = TeiidNodeFactory.getInstance();

    /**
     * @param teiidParser
     */
    public SyntaxFactory(TeiidParser teiidParser) {
        this.teiidParser = teiidParser;
    }

    private boolean isGreaterThanOrEqualTo(Version teiidVersion) {
        ITeiidServerVersion minVersion = teiidParser.getVersion().getMinimumVersion();
        return minVersion.equals(teiidVersion.get()) || minVersion.isGreaterThan(teiidVersion.get());
    }

    private <T extends LanguageObject> T create(ASTNodes nodeType) {
        return nodeFactory.create(teiidParser, nodeType);
    }

    @Override
    public IFunction createFunction(String name, List<? extends Expression> arguments) {
        if (arguments == null) {
            arguments = new ArrayList<Expression>();
        }

        Function function = create(ASTNodes.FUNCTION);
        function.setName(name);
        function.setArgs(arguments.toArray(new Expression[0]));
        return function;
    }

    @Override
    public IAggregateSymbol createAggregateSymbol(String functionName, AggregateSymbol.Type functionType, boolean isDistinct, Expression expression) {
        AggregateSymbol aggregateSymbol = create(ASTNodes.AGGREGATE_SYMBOL);
        aggregateSymbol.setName(functionName);
        aggregateSymbol.setAggregateFunction(functionType);
        aggregateSymbol.setDistinct(isDistinct);

        if (expression != null) {
            if (isGreaterThanOrEqualTo(Version.TEIID_8_0))
                aggregateSymbol.setArgs(new Expression[] { expression });
            else
                aggregateSymbol.setExpression(expression);
        }

        return aggregateSymbol;
    }

    @Override
    public IElementSymbol createElementSymbol(String name) {
        ElementSymbol elementSymbol = create(ASTNodes.ELEMENT_SYMBOL);
        elementSymbol.setName(name);
        return elementSymbol;
    }

    @Override
    public IElementSymbol createElementSymbol(String name, boolean displayFullyQualified) {
        ElementSymbol elementSymbol = create(ASTNodes.ELEMENT_SYMBOL);
        elementSymbol.setName(name);
        if (displayFullyQualified)
            elementSymbol.setDisplayMode(DisplayMode.FULLY_QUALIFIED);
        else
            elementSymbol.setDisplayMode(DisplayMode.SHORT_OUTPUT_NAME);

        return elementSymbol;
    }

    @Override
    public IAliasSymbol createAliasSymbol(String name, Expression symbol) {
        AliasSymbol aliasSymbol = create(ASTNodes.ALIAS_SYMBOL);
        aliasSymbol.setName(name);
        aliasSymbol.setSymbol(symbol);
        return aliasSymbol;
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name) {
        GroupSymbol groupSymbol = create(ASTNodes.GROUP_SYMBOL);
        groupSymbol.setName(name);
        return groupSymbol;
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name, String definition) {
        GroupSymbol groupSymbol = create(ASTNodes.GROUP_SYMBOL);
        groupSymbol.setName(name);
        groupSymbol.setDefinition(definition);
        return groupSymbol;
    }

    @Override
    public IExpressionSymbol createExpressionSymbol(String name, Expression expression) {
        ExpressionSymbol expressionSymbol = create(ASTNodes.EXPRESSION_SYMBOL);
        expressionSymbol.setName(name);
        expressionSymbol.setExpression(expression);
        return expressionSymbol;
    }

    @Override
    public IMultipleElementSymbol createMultipleElementSymbol() {
        MultipleElementSymbol multipleElementSymbol = create(ASTNodes.MULTIPLE_ELEMENT_SYMBOL);
        return multipleElementSymbol;
    }

    @Override
    public IConstant createConstant(Object value) {
        Constant constant = create(ASTNodes.CONSTANT);
        constant.setValue(value);
        return constant;
    }

    @Override
    public IDeclareStatement createDeclareStatement(ElementSymbol variable, String valueType) {
        DeclareStatement declareStatement = create(ASTNodes.DECLARE_STATEMENT);
        declareStatement.setVariable(variable);
        declareStatement.setVariableType(valueType);
        return declareStatement;
    }

    @Override
    public ICommandStatement createCommandStatement(Command command) {
        CommandStatement commandStatement = create(ASTNodes.COMMAND_STATEMENT);
        commandStatement.setCommand(command);
        return commandStatement;
    }

    @Override
    public IRaiseStatement createRaiseStatement(Expression expression) {
        if (isGreaterThanOrEqualTo(Version.TEIID_8_0)) {
            RaiseStatement raiseStatement = create(ASTNodes.RAISE_STATEMENT);
            raiseStatement.setExpression(expression);
            return raiseStatement;
        } else {
            RaiseErrorStatement raiseErrorStatement = create(ASTNodes.RAISE_ERROR_STATEMENT);
            raiseErrorStatement.setExpression(expression);
            return raiseErrorStatement;
        }
    }

    @Override
    public IQuery createQuery() {
        Query query = create(ASTNodes.QUERY);
        return query;
    }

    @Override
    public ISetQuery createSetQuery(Operation operation, boolean all, QueryCommand leftQuery, QueryCommand rightQuery) {
        SetQuery setQuery = create(ASTNodes.SET_QUERY);
        setQuery.setLeftQuery(leftQuery);
        setQuery.setAll(all);
        setQuery.setOperation(operation);
        setQuery.setRightQuery(rightQuery);
        return setQuery;
    }

    @Override
    public ISetQuery createSetQuery(Operation operation) {
        SetQuery setQuery = create(ASTNodes.SET_QUERY);
        setQuery.setOperation(operation);
        return setQuery;
    }

    @Override
    public ICompareCriteria createCompareCriteria() {
        CompareCriteria compareCriteria = create(ASTNodes.COMPARE_CRITERIA);
        return compareCriteria;
    }

    @Override
    public ICompareCriteria createCompareCriteria(Expression expression1, int operator, Expression expression2) {
        CompareCriteria compareCriteria = create(ASTNodes.COMPARE_CRITERIA);
        compareCriteria.setLeftExpression(expression1);
        compareCriteria.setOperator(Operator.findOperator(operator));
        compareCriteria.setRightExpression(expression2);
        return compareCriteria;
    }

    @Override
    public IIsNullCriteria createIsNullCriteria() {
        IsNullCriteria isNullCriteria = create(ASTNodes.IS_NULL_CRITERIA);
        return isNullCriteria;
    }

    @Override
    public IIsNullCriteria createIsNullCriteria(Expression expression) {
        IsNullCriteria isNullCriteria = create(ASTNodes.IS_NULL_CRITERIA);
        isNullCriteria.setExpression(expression);
        return isNullCriteria;
    }

    @Override
    public INotCriteria createNotCriteria() {
        NotCriteria notCriteria = create(ASTNodes.NOT_CRITERIA);
        return notCriteria;
    }

    @Override
    public INotCriteria createNotCriteria(Criteria criteria) {
        NotCriteria notCriteria = create(ASTNodes.NOT_CRITERIA);
        notCriteria.setCriteria(criteria);
        return notCriteria;
    }

    @Override
    public IMatchCriteria createMatchCriteria() {
        MatchCriteria matchCriteria = create(ASTNodes.MATCH_CRITERIA);
        return matchCriteria;
    }

    @Override
    public ISetCriteria createSetCriteria() {
        SetCriteria setCriteria = create(ASTNodes.SET_CRITERIA);
        return setCriteria;
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria() {
        SubquerySetCriteria subquerySetCriteria = create(ASTNodes.SUBQUERY_SET_CRITERIA);
        return subquerySetCriteria;
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria(Expression expression, QueryCommand command) {
        SubquerySetCriteria subquerySetCriteria = create(ASTNodes.SUBQUERY_SET_CRITERIA);
        subquerySetCriteria.setExpression(expression);
        subquerySetCriteria.setCommand(command);
        return subquerySetCriteria;
    }

    @Override
    public ISubqueryCompareCriteria createSubqueryCompareCriteria(Expression leftExpression, QueryCommand command, int operator, int predicateQuantifier) {
        SubqueryCompareCriteria subqueryCompareCriteria = create(ASTNodes.SUBQUERY_COMPARE_CRITERIA);
        subqueryCompareCriteria.setLeftExpression(leftExpression);
        subqueryCompareCriteria.setCommand(command);
        subqueryCompareCriteria.setOperator(Operator.findOperator(operator));
        subqueryCompareCriteria.setPredicateQuantifier(PredicateQuantifier.findQuantifier(predicateQuantifier));
        return subqueryCompareCriteria;
    }

    @Override
    public IScalarSubquery createScalarSubquery(QueryCommand queryCommand) {
        ScalarSubquery scalarSubquery = create(ASTNodes.SCALAR_SUBQUERY);
        scalarSubquery.setCommand(queryCommand);
        return scalarSubquery;
    }

    @Override
    public IBetweenCriteria createBetweenCriteria(ElementSymbol elementSymbol, Constant constant1, Constant constant2) {
        BetweenCriteria betweenCriteria = create(ASTNodes.BETWEEN_CRITERIA);
        betweenCriteria.setExpression(elementSymbol);
        betweenCriteria.setLowerExpression(constant1);
        betweenCriteria.setUpperExpression(constant2);
        return betweenCriteria;
    }

    @Override
    public ICompoundCriteria createCompoundCriteria(int operator, List<? extends Criteria> criteria) {
        CompoundCriteria compoundCriteria = create(ASTNodes.COMPOUND_CRITERIA);
        compoundCriteria.setOperator(operator);
        compoundCriteria.setCriteria(criteria);
        return compoundCriteria;
    }

    @Override
    public IExistsCriteria createExistsCriteria(QueryCommand queryCommand) {
        ExistsCriteria existsCriteria = create(ASTNodes.EXISTS_CRITERIA);
        existsCriteria.setCommand(queryCommand);
        return existsCriteria;
    }

    @Override
    public IBlock createBlock() {
        Block block = create(ASTNodes.BLOCK);
        return block;
    }

    @Override
    public ICreateProcedureCommand createCreateProcedureCommand(Block block) {
        if (isGreaterThanOrEqualTo(Version.TEIID_8_0)) {
            CreateProcedureCommand command = create(ASTNodes.CREATE_PROCEDURE_COMMAND);
            command.setBlock(block);
            return command;
        } else {
            CreateUpdateProcedureCommand command = create(ASTNodes.CREATE_UPDATE_PROCEDURE_COMMAND);
            command.setBlock(block);
            return command;
        }
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol, Expression expression) {
        AssignmentStatement assignmentStatement = create(ASTNodes.ASSIGNMENT_STATEMENT);
        assignmentStatement.setVariable(elementSymbol);
        assignmentStatement.setExpression(expression);
        return assignmentStatement;
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol, QueryCommand queryCommand) {
        AssignmentStatement assignmentStatement = create(ASTNodes.ASSIGNMENT_STATEMENT);
        assignmentStatement.setVariable(elementSymbol);
        assignmentStatement.setCommand(queryCommand);
        return assignmentStatement;
    }

    @Override
    public ISelect createSelect() {
        Select select = create(ASTNodes.SELECT);
        return select;
    }

    @Override
    public ISelect createSelect(List<? extends Expression> symbols) {
        Select select = create(ASTNodes.SELECT);
        select.setSymbols(symbols);
        return select;
    }

    @Override
    public IFrom createFrom() {
        From from = create(ASTNodes.FROM);
        return from;
    }

    @Override
    public IFrom createFrom(List<? extends FromClause> fromClauses) {
        From from = create(ASTNodes.FROM);
        from.setClauses(fromClauses);
        return from;
    }

    @Override
    public IUnaryFromClause createUnaryFromClause(GroupSymbol symbol) {
        UnaryFromClause unaryFromClause = create(ASTNodes.UNARY_FROM_CLAUSE);
        unaryFromClause.setGroup(symbol);
        return unaryFromClause;
    }

    @Override
    public ISubqueryFromClause createSubqueryFromClause(String name, QueryCommand command) {
        SubqueryFromClause subqueryFromClause = create(ASTNodes.SUBQUERY_FROM_CLAUSE);
        subqueryFromClause.setName(name);
        subqueryFromClause.setCommand(command);
        return subqueryFromClause;
    }

    @Override
    public IJoinType getJoinType(IJoinType.Types joinType) {
        JoinType join = create(ASTNodes.JOIN_TYPE);
        join.setKind(joinType);
        return join;
    }

    @Override
    public IJoinPredicate createJoinPredicate(FromClause leftClause, FromClause rightClause, JoinType joinType) {
        JoinPredicate joinPredicate = create(ASTNodes.JOIN_PREDICATE);
        joinPredicate.setJoinType(joinType);
        joinPredicate.setLeftClause(leftClause);
        joinPredicate.setRightClause(rightClause);
        return joinPredicate;
    }

    @Override
    public IJoinPredicate createJoinPredicate(FromClause leftClause, FromClause rightClause, JoinType joinType, List<Criteria> criteria) {
        JoinPredicate joinPredicate = create(ASTNodes.JOIN_PREDICATE);
        joinPredicate.setJoinType(joinType);
        joinPredicate.setLeftClause(leftClause);
        joinPredicate.setRightClause(rightClause);
        joinPredicate.setJoinCriteria(criteria);
        return joinPredicate;
    }

    @Override
    public IGroupBy createGroupBy() {
        GroupBy groupBy = create(ASTNodes.GROUP_BY);
        return groupBy;
    }

    @Override
    public IOrderBy createOrderBy() {
        OrderBy orderBy = create(ASTNodes.ORDER_BY);
        return orderBy;
    }

    @Override
    public IOption createOption() {
        Option option = create(ASTNodes.OPTION);
        return option;
    }

    @Override
    public IUpdate createUpdate() {
        Update update = create(ASTNodes.UPDATE);
        return update;
    }

    @Override
    public IDelete createDelete() {
        Delete delete = create(ASTNodes.DELETE);
        return delete;
    }

    @Override
    public IInsert createInsert() {
        Insert insert = create(ASTNodes.INSERT);
        return insert;
    }

    @Override
    public IStoredProcedure createStoredProcedure() {
        StoredProcedure storedProcedure = create(ASTNodes.STORED_PROCEDURE);
        return storedProcedure;
    }

    @Override
    public ISPParameter createSPParameter(int index, Expression expression) {
        return new SPParameter(teiidParser, index, expression);
    }

    @Override
    public ISPParameter createSPParameter(int index, ParameterInfo parameterType, String name) {
        return new SPParameter(teiidParser, index, parameterType.index(), name);
    }

    @Override
    public IReference createReference(int index) {
        Reference reference = create(ASTNodes.REFERENCE);
        reference.setIndex(index);
        return reference;
    }

    @Override
    public IMetadataID createMetadataID(String id, Class clazz) {
        return new TempMetadataID(id, clazz);
    }

    @Override
    public IStoredProcedureInfo createStoredProcedureInfo() {
        return new StoredProcedureInfo();
    }

    @Override
    public IQueryNode createQueryNode(String queryPlan) {
        return new QueryNode(queryPlan);
    }
}

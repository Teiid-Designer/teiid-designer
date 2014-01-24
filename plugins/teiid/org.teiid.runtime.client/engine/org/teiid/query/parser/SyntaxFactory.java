/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.parser;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.symbol.Expression;
import org.teiid.query.sql.lang.symbol.Function;

/**
 *
 */
public class SyntaxFactory {

    private TeiidParser teiidParser;

    private final TeiidNodeFactory nodeFactory = TeiidNodeFactory.getInstance();

    /**
     * @param queryParser
     */
    public SyntaxFactory(TeiidParser teiidParser) {
        this.teiidParser = teiidParser;
    }

    private boolean isGreaterThanOrEqualTo(ITeiidServerVersion teiidVersion) {
        return teiidParser.getVersion().equals(teiidVersion) || teiidParser.getVersion().isGreaterThan(teiidVersion);
    }

    private <T extends LanguageObject> T createASTNode(ASTNodes nodeType) {
        return nodeFactory.create(teiidParser, nodeType);
    }

    public IFunction createFunction(String name, List<? extends Expression> arguments) {
        if (arguments == null) {
            arguments = new ArrayList<Expression>();
        }

        Function function = createASTNode(ASTNodes.FUNCTION);
        function.setName(name);
        function.setArgs(arguments.toArray(new Expression[0]));
        return function;
    }

//    public IAggregateSymbol createAggregateSymbol(String functionName, AggregateSymbol.Type functionType, boolean isDistinct, Expression expression) {
//        AggregateSymbol aggregateSymbol = createASTNode(ASTNodes.AGGREGATE_SYMBOL);
//        aggregateSymbol.setName(functionName);
//        aggregateSymbol.setAggregateFunction(functionType);
//        aggregateSymbol.setDistinct(isDistinct);
//
//        if (isGreaterThanOrEqualTo(TeiidServerVersion.TEIID_8_SERVER))
//            aggregateSymbol.setArgs(new Expression[] { expression });
//        else
//            aggregateSymbol.setExpression(expression);
//
//        return aggregateSymbol;
//    }
//
//    public IElementSymbol createElementSymbol(String name) {
//        ElementSymbol elementSymbol = createASTNode(ASTNodes.ELEMENT_SYMBOL);
//        elementSymbol.setName(name);
//        return elementSymbol;
//    }
//
//    public IElementSymbol createElementSymbol(String name, boolean displayFullyQualified) {
//        ElementSymbol elementSymbol = createElementSymbol(name);
//        if (displayFullyQualified)
//            elementSymbol.setDisplayMode(DisplayMode.FULLY_QUALIFIED);
//        else
//            elementSymbol.setDisplayMode(DisplayMode.SHORT_OUTPUT_NAME);
//
//        return elementSymbol;
//    }
//
//    public IAliasSymbol createAliasSymbol(String name, Expression symbol) {
//        AliasSymbol aliasSymbol = createASTNode(ASTNodes.ALIAS_SYMBOL);
//        aliasSymbol.setName(name);
//        aliasSymbol.setSymbol(symbol);
//        return aliasSymbol;
//    }
//
//    public IGroupSymbol createGroupSymbol(String name) {
//        GroupSymbol groupSymbol = createASTNode(ASTNodes.GROUP_SYMBOL);
//        groupSymbol.setName(name);
//        return groupSymbol;
//    }
//
//    public IGroupSymbol createGroupSymbol(String name, String definition) {
//        GroupSymbol groupSymbol = createGroupSymbol(name);
//        groupSymbol.setDefinition(definition);
//        return groupSymbol;
//    }
//
//    public IExpressionSymbol createExpressionSymbol(String name, Expression expression) {
//        ExpressionSymbol expressionSymbol = createASTNode(ASTNodes.EXPRESSION_SYMBOL);
//        expressionSymbol.setName(name);
//        expressionSymbol.setExpression(expression);
//        return expressionSymbol;
//    }
//
//    public IMultipleElementSymbol createMultipleElementSymbol() {
//        return new MultipleElementSymbol();
//    }
//
//    public IConstant createConstant(Object value) {
//        return new Constant(value);
//    }
//
//    public IDeclareStatement createDeclareStatement(ElementSymbol variable, String valueType) {
//        return new DeclareStatement(variable, valueType);
//    }
//
//    public ICommandStatement createCommandStatement(Command command) {
//        return new CommandStatement(command);
//    }
//
//    public IRaiseStatement createRaiseStatement(Expression expression) {
//        return new RaiseStatement(expression);
//    }
//
//    public IQuery createQuery() {
//        return new Query();
//    }
//
//    public ISetQuery createSetQuery(Operation operation, boolean all, QueryCommand leftQuery, QueryCommand rightQuery) {
//        return new SetQuery(operation, all, leftQuery, rightQuery);
//    }
//
//    public ISetQuery createSetQuery(Operation operation) {
//        return new SetQuery(operation);
//    }
//
//    public ICompareCriteria createCompareCriteria() {
//        return new CompareCriteria();
//    }
//
//    public ICompareCriteria createCompareCriteria(Expression expression1, int operator, Expression expression2) {
//        return new CompareCriteria(expression1, operator, expression2);
//    }
//
//    public IIsNullCriteria createIsNullCriteria() {
//        return new IsNullCriteria();
//    }
//
//    public IIsNullCriteria createIsNullCriteria(Expression expression) {
//        return new IsNullCriteria(expression);
//    }
//
//    public INotCriteria createNotCriteria() {
//        return new NotCriteria();
//    }
//
//    public INotCriteria createNotCriteria(Criteria criteria) {
//        return new NotCriteria(criteria);
//    }
//
//    public IMatchCriteria createMatchCriteria() {
//        return new MatchCriteria();
//    }
//
//    public ISetCriteria createSetCriteria() {
//        return new SetCriteria();
//    }
//
//    public ISubquerySetCriteria createSubquerySetCriteria() {
//        return new SubquerySetCriteria();
//    }
//
//    public ISubquerySetCriteria createSubquerySetCriteria(Expression expression, QueryCommand command) {
//        return new SubquerySetCriteria(expression, command);
//    }
//
//    public ISubqueryCompareCriteria createSubqueryCompareCriteria(Expression leftExpression, QueryCommand command, int operator, int predicateQuantifier) {
//        return new SubqueryCompareCriteria(leftExpression, command, operator, predicateQuantifier);
//    }
//
//    public IScalarSubquery createScalarSubquery(QueryCommand queryCommand) {
//        return new ScalarSubquery(queryCommand);
//    }
//
//    public IBetweenCriteria createBetweenCriteria(ElementSymbol elementSymbol, Constant constant1, Constant constant2) {
//        return new BetweenCriteria(elementSymbol, constant1, constant2);
//    }
//
//    public ICompoundCriteria createCompoundCriteria(int operator, List<? extends Criteria> criteria) {
//        return new CompoundCriteria(operator, criteria);
//    }
//
//    public IExistsCriteria createExistsCriteria(QueryCommand queryCommand) {
//        return new ExistsCriteria(queryCommand);
//    }
//
//    public IBlock createBlock() {
//        return new Block();
//    }
//
//    public ICreateProcedureCommand createCreateProcedureCommand(Block block) {
//        return new CreateProcedureCommand(block);
//    }
//
//    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol, Expression expression) {
//        return new AssignmentStatement(elementSymbol, expression);
//    }
//
//    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol, QueryCommand queryCommand) {
//        return new AssignmentStatement(elementSymbol, queryCommand);
//    }
//
//    public ISelect createSelect() {
//        return new Select();
//    }
//
//    public ISelect createSelect(List<? extends Expression> symbols) {
//        return new Select(symbols);
//    }
//
//    public IFrom createFrom() {
//        return new From();
//    }
//
//    public IFrom createFrom(List<? extends FromClause> fromClauses) {
//        return new From(fromClauses);
//    }
//
//    public IUnaryFromClause createUnaryFromClause(GroupSymbol symbol) {
//        return new UnaryFromClause(symbol);
//    }
//
//    public ISubqueryFromClause createSubqueryFromClause(String name, QueryCommand command) {
//        return new SubqueryFromClause(name, command);
//    }
//
//    public IJoinType getJoinType(IJoinType.Types joinType) {
//        switch (joinType) {
//            case JOIN_INNER:
//                return JoinType.JOIN_INNER;
//            case JOIN_RIGHT_OUTER:
//                return JoinType.JOIN_RIGHT_OUTER;
//            case JOIN_LEFT_OUTER:
//                return JoinType.JOIN_LEFT_OUTER;
//            case JOIN_FULL_OUTER:
//                return JoinType.JOIN_FULL_OUTER;
//            case JOIN_CROSS:
//                return JoinType.JOIN_CROSS;
//            case JOIN_UNION:
//                return JoinType.JOIN_UNION;
//            case JOIN_SEMI:
//                return JoinType.JOIN_SEMI;
//            case JOIN_ANTI_SEMI:
//                return JoinType.JOIN_ANTI_SEMI;
//            default:
//                throw new IllegalArgumentException();
//        }
//    }
//
//    public IJoinPredicate createJoinPredicate(FromClause leftClause, FromClause rightClause, JoinType joinType) {
//        return new JoinPredicate(leftClause, rightClause, joinType);
//    }
//
//    public IJoinPredicate createJoinPredicate(FromClause leftClause, FromClause rightClause, JoinType joinType, List<Criteria> criteria) {
//        return new JoinPredicate(leftClause, rightClause, joinType, criteria);
//    }
//
//    public IGroupBy createGroupBy() {
//        return new GroupBy();
//    }
//
//    public IOrderBy createOrderBy() {
//        return new OrderBy();
//    }
//
//    public IOption createOption() {
//        return new Option();
//    }
//
//    public IUpdate createUpdate() {
//        return new Update();
//    }
//
//    public IDelete createDelete() {
//        return new Delete();
//    }
//
//    public IInsert createInsert() {
//        return new Insert();
//    }
//
//    public IStoredProcedure createStoredProcedure() {
//        return new StoredProcedure();
//    }
//
//    public ISPParameter createSPParameter(int index, Expression expression) {
//        return new SPParameter(index, expression);
//    }
//
//    public ISPParameter createSPParameter(int index, ParameterInfo parameterType, String name) {
//        return new SPParameter(index, parameterType.index(), name);
//    }
//
//    public IReference createReference(int index) {
//        return new Reference(index);
//    }
//
//    public IMetadataID createMetadataID(String id, Class clazz) {
//        return new TempMetadataID(id, clazz);
//    }
//
//    public IStoredProcedureInfo createStoredProcedureInfo() {
//        return new StoredProcedureInfo();
//    }
//
//    public IQueryNode createQueryNode(String queryPlan) {
//        return new QueryNode(queryPlan);
//    }
}

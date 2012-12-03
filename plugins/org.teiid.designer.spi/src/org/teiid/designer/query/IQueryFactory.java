/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import java.util.List;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria.LogicalOperator;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.IQueryCommand;
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
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;

/**
 *
 */
public interface IQueryFactory {
    
    /**
     * Create a new function
     * 
     * @param name
     * @param arguments
     * 
     * @return instance of {@link IFunction}
     */
    IFunction createFunction(String name, IExpression[] arguments);
    
    /**
     * Create a new aggregate symbol
     * 
     * @param functionName 
     * @param isDistinct 
     * @param expression
     * 
     * @return instance of {@link IAggregateSymbol}
     */
    IAggregateSymbol createAggregateSymbol(String functionName, boolean isDistinct, IExpression expression);
    
    /**
     * Create a new element symbol
     * 
     * @param name
     * 
     * @return instance of {@link IElementSymbol}
     */
    IElementSymbol createElementSymbol(String name);

    /**
     * Create a new element symbol
     * 
     * @param name
     * @param displayFullyQualified True if should display fully qualified
     * 
     * @return instance of {@link IElementSymbol}
     */
    IElementSymbol createElementSymbol(String name, boolean displayFullyQualified);
    
    /**
     * Create an alias symbol
     * 
     * @param name
     * @param expression
     * 
     * @return instance of {@link IAliasSymbol}
     */
    IAliasSymbol createAliasSymbol(String name, IExpression expression);
    
    /**
     * Create a new group symbol
     * 
     * @param name
     * 
     * @return instance of {@link IGroupSymbol}
     */
    IGroupSymbol createGroupSymbol(String name);
    
    /**
     * Create a new group symbol
     * 
     * @param name
     * @param definition
     * 
     * @return instance of {@link IGroupSymbol}
     */
    IGroupSymbol createGroupSymbol(String name, String definition);
    
    /**
     * Create an expression symbol
     * 
     * @param name
     * @param expression
     * 
     * @return instance of {@link IExpressionSymbol}
     */
    IExpressionSymbol createExpressionSymbol(String name, IExpression expression);
    
    /**
     * Create an multiple element symbol
     * 
     * @return instance of {@link IMultipleElementSymbol}
     */
    IMultipleElementSymbol createMultipleElementSymbol();
    
    /**
     * Create a new constant
     * 
     * @param value
     * 
     * @return instance of {@link IConstant}
     */
    IConstant createConstant(Object value);

    /**
     * Create a new declare statement.
     * 
     * @param variable The <code>ElementSymbol</code> object that is the variable
     * @param valueType The type of this variable
     * 
     * @return instance of  {@link IDeclareStatement}
     */
    IDeclareStatement createDeclareStatement(IElementSymbol variable, String valueType);
    
    /**
     * Create a command statement
     * 
     * @param command
     * 
     * @return instance of {@link ICommandStatement}
     */
    ICommandStatement createCommandStatement(ICommand command);
    
    /**
     * Create a raise statement
     * 
     * @param expression
     * 
     * @return instance of {@link IRaiseStatement}
     */
    IRaiseStatement createRaiseStatement(IExpression expression);
    
    /**
     * Create a query
     * 
     * @return instance of {@link IQuery}
     */
    IQuery createQuery();

    /**
     * Create a set query
     * 
     * @param operation
     * @param all
     * @param leftQuery 
     * @param rightQuery 
     * 
     * @return instance of {@link ISetQuery}
     */
    ISetQuery createSetQuery(Operation operation, 
                                              boolean all, 
                                              IQueryCommand leftQuery, 
                                              IQueryCommand rightQuery);

    /**
     * Create a set query
     * 
     * @param operation
     * 
     * @return instance of {@link ISetQuery}
     */
    ISetQuery createSetQuery(Operation operation);
    
    /**
     * Create a compare criteria
     * 
     * @return instance of {@link ICompareCriteria}
     */
    ICompareCriteria createCompareCriteria();

    /**
     * Create a compare criteria
     * 
     * @param expression1
     * @param operator
     * @param expression2
     * 
     * @return instance of {@link ICompareCriteria}
     */
    ICompareCriteria createCompareCriteria(IExpression expression1, 
                                                                   int operator, 
                                                                   IExpression expression2);
    
    /**
     * Create an is null criteria
     * 
     * @return instance of {@link IIsNullCriteria}
     */
    IIsNullCriteria createIsNullCriteria();

    /**
     * Create an is null criteria
     * 
     * @param expression 
     * 
     * @return instance of {@link IIsNullCriteria}
     */
    IIsNullCriteria createIsNullCriteria(IExpression expression);
    
    /**
     * Create a not criteria
     * 
     * @return instance of {@link INotCriteria}
     */
    INotCriteria createNotCriteria();
    
    /**
     * Create a not criteria
     * 
     * @param criteria 
     * 
     * @return instance of {@link INotCriteria}
     */
    INotCriteria createNotCriteria(ICriteria criteria);
    
    /**
     * Create a match criteria
     * 
     * @return instance of {@link IMatchCriteria}
     */
    IMatchCriteria createMatchCriteria();

    /**
     * Create a set criteria
     * 
     * @return instance of {@link ISetCriteria}
     */
    ISetCriteria createSetCriteria();

    /**
     * Create a subquery set criteria
     * 
     * @return instance of {@link ISubquerySetCriteria}
     */
    ISubquerySetCriteria createSubquerySetCriteria();

    /**
     * Create a subquery set criteria
     * 
     * @param expression 
     * @param command 
     * 
     * @return instance of {@link ISubquerySetCriteria}
     */
    ISubquerySetCriteria createSubquerySetCriteria(IExpression expression, IQueryCommand command);
    
    /**
     * Create a subquery compare criteria
     * 
     * @param leftExpression
     * @param command
     * @param operator
     * @param predicateQuantifier
     * 
     * @return instance of {@link ISubqueryCompareCriteria}
     */
    ISubqueryCompareCriteria createSubqueryCompareCriteria(IExpression leftExpression, IQueryCommand command, int operator, int predicateQuantifier);
    
    /**
     * Create a scalar sub query
     * 
     * @param queryCommand
     * 
     * @return instance of {@link IScalarSubquery}
     */
    IScalarSubquery createScalarSubquery(IQueryCommand queryCommand);
    
    /**
     * Create an in-between criteria
     * 
     * @param elementSymbol
     * @param constant1
     * @param constant2
     * 
     * @return instance of {@link IBetweenCriteria}
     */
    IBetweenCriteria createBetweenCriteria(IElementSymbol elementSymbol,
                                                                   IConstant constant1,
                                                                   IConstant constant2);

    /**
     * Create a compound criteria
     * 
     * @param operator
     * @param criteria
     * 
     * @return instance of {@link ICompoundCriteria}
     */
    ICompoundCriteria createCompoundCriteria(LogicalOperator operator, ICriteria... criteria);

    /**
     * Create an exists criteria
     * 
     * @param queryCommand
     * 
     * @return instance of {@link IExistsCriteria}
     */
    IExistsCriteria createExistsCriteria(IQueryCommand queryCommand);
    
    /**
     * Create a block
     * 
     * @return instance of {@link IBlock}
     */
    IBlock createBlock();

    /**
     * Create a create-procedure statement
     * 
     * @param block
     * 
     * @return instance of {@link ICreateProcedureCommand}
     */
    ICreateProcedureCommand createCreateProcedureCommand(IBlock block);

    /**
     * Create an assignment statement
     * 
     * @param elementSymbol 
     * @param expression
     * 
     * @return instance of {@link IAssignmentStatement}
     */
    IAssignmentStatement createAssignmentStatement(IElementSymbol elementSymbol, IExpression expression);

    /**
     * Create an assignment statement
     * 
     * @param elementSymbol 
     * @param queryCommand
     * 
     * @return instance of {@link IAssignmentStatement}
     */
    IAssignmentStatement createAssignmentStatement(IElementSymbol elementSymbol, IQueryCommand queryCommand);

    /**
     * Create a select
     * 
     * @return instance of {@link ISelect}
     */
    ISelect createSelect();
    
    /**
     * Create a select
     * 
     * @param symbols
     * 
     * @return instance of {@link ISelect}
     */
    ISelect createSelect(List<? extends IExpression> symbols);

    /**
     * Create a from
     * 
     * @return instance of {@link IFrom}
     */
    IFrom createFrom();

    /**
     * Create a from
     * 
     * @param fromClauses
     * 
     * @return instance of {@link IFrom}
     */
    IFrom createFrom(List<? extends IFromClause> fromClauses);
    
    /**
     * Create a unary from clause
     * 
     * @param symbol
     * 
     * @return instance of {@link IUnaryFromClause}
     */
    IUnaryFromClause createUnaryFromClause(IGroupSymbol symbol);

    /**
     * Create a subquery from clause
     * 
     * @param name
     * @param command
     * 
     * @return instance of {@link ISubqueryFromClause}
     */
    ISubqueryFromClause createSubqueryFromClause(String name, ICommand command);

    /**
     * Create a join predicate
     * 
     * @param leftClause 
     * @param rightClause 
     * @param joinType
     * 
     * @return instance of {@link IJoinPredicate}
     */
    IJoinPredicate createJoinPredicate(IFromClause leftClause,
                                                         IFromClause rightClause, 
                                                         IJoinPredicate.JoinType joinType);
    /**
     * Create a join predicate
     * 
     * @param leftClause 
     * @param rightClause 
     * @param joinType
     * @param criteria 
     * 
     * @return instance of {@link IJoinPredicate}
     */    
    IJoinPredicate createJoinPredicate(IFromClause leftClause, 
                                                         IFromClause rightClause,
                                                         IJoinPredicate.JoinType joinType,
                                                         List<ICriteria> criteria);
    
    /**
     * Create a group by
     * 
     * @return instance of {@link IGroupBy}
     */
    IGroupBy createGroupBy();

    /**
     * Create an order by
     * 
     * @return instance of {@link IOrderBy}
     */
    IOrderBy createOrderBy();
    
    /**
     * Create an option
     * 
     * @return instance of {@link IOption}
     */
    IOption createOption();
    
    /**
     * Create an update
     * 
     * @return instance of {@link IUpdate}
     */
    IUpdate createUpdate();
    
    /**
     * Create a delete     
     * 
     * @return instance of {@link IDelete}
     */
    IDelete createDelete();
    
    /**
     * Create an insert     
     * 
     * @return instance of {@link IInsert}
     */
    IInsert createInsert();
    
    /**
     * Create a stored procedure
     * 
     * @return instance of {@link IStoredProcedure}
     */
    IStoredProcedure createStoredProcedure();

    /**
     * Create a stored procedure parameter
     * 
     * @param index
     * @param expression
     * 
     * @return instance of {@link ISPParameter}
     */
    ISPParameter createSPParameter(int index, IExpression expression);

    /**
     * Create a stored procedure parameter
     * 
     * @param index 
     * @param parameterType 
     * @param name
     * 
     * @return instance of {@link ISPParameter}
     */
    ISPParameter createSPParameter(int index, ParameterInfo parameterType, String name);
    
    /**
     * Create a reference
     * 
     * @param index
     * 
     * @return instance of {@link IReference}
     */
    IReference createReference(int index);

    /**
     * Create a metadata id
     * 
     * @param upperCase
     * @param clazz
     * 
     * @return instance of {@link IMetadataID}
     */
    IMetadataID createMetadataID(String upperCase, Class<?> clazz);

    /**
     * Create a stored procedure info
     * 
     * @return instance of {@link IStoredProcedureInfo}
     */
    IStoredProcedureInfo createStoredProcedureInfo();

}

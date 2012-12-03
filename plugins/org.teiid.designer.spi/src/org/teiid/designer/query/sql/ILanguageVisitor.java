/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
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
import org.teiid.designer.query.sql.proc.IRaiseStatement;
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
public interface ILanguageVisitor {
    
    /**
     * @param symbol
     */
    void visit(IGroupSymbol symbol);
    
    /**
     * @param symbol
     */
    void visit(IElementSymbol symbol);

    /**
     * @param option
     */
    void visit(IOption option);

    /**
     * @param function
     */
    void visit(IFunction function);

    /**
     * @param aliasSymbol
     */
    void visit(IAliasSymbol aliasSymbol);

    /**
     * @param assignmentStatement
     */
    void visit(IAssignmentStatement assignmentStatement);

    /**
     * @param betweenCriteria
     */
    void visit(IBetweenCriteria betweenCriteria);

    /**
     * @param block
     */
    void visit(IBlock block);

    /**
     * @param commandStatement
     */
    void visit(ICommandStatement commandStatement);

    /**
     * @param compareCriteria
     */
    void visit(ICompareCriteria compareCriteria);

    /**
     * @param compoundCriteria
     */
    void visit(ICompoundCriteria compoundCriteria);

    /**
     * @param constant
     */
    void visit(IConstant constant);

    /**
     * @param createProcedureCommand
     */
    void visit(ICreateProcedureCommand createProcedureCommand);

    /**
     * @param delete
     */
    void visit(IDelete delete);

    /**
     * @param existsCriteria
     */
    void visit(IExistsCriteria existsCriteria);

    /**
     * @param expressionSymbol
     */
    void visit(IExpressionSymbol expressionSymbol);

    /**
     * @param from
     */
    void visit(IFrom from);

    /**
     * @param groupBy
     */
    void visit(IGroupBy groupBy);

    /**
     * @param insert
     */
    void visit(IInsert insert);

    /**
     * @param into
     */
    void visit(IInto into);

    /**
     * @param isNullCriteria
     */
    void visit(IIsNullCriteria isNullCriteria);

    /**
     * @param joinPredicate
     */
    void visit(IJoinPredicate joinPredicate);

    /**
     * @param matchCriteria
     */
    void visit(IMatchCriteria matchCriteria);

    /**
     * @param multipleElementSymbol
     */
    void visit(IMultipleElementSymbol multipleElementSymbol);

    /**
     * @param notCriteria
     */
    void visit(INotCriteria notCriteria);

    /**
     * @param orderBy
     */
    void visit(IOrderBy orderBy);

    /**
     * @param orderByItem
     */
    void visit(IOrderByItem orderByItem);

    /**
     * @param query
     */
    void visit(IQuery query);

    /**
     * @param raiseStatement
     */
    void visit(IRaiseStatement raiseStatement);

    /**
     * @param reference
     */
    void visit(IReference reference);

    /**
     * @param scalarSubquery
     */
    void visit(IScalarSubquery scalarSubquery);

    /**
     * @param select
     */
    void visit(ISelect select);

    /**
     * @param setCriteria
     */
    void visit(ISetCriteria setCriteria);

    /**
     * @param setQuery
     */
    void visit(ISetQuery setQuery);

    /**
     * @param storedProcedure
     */
    void visit(IStoredProcedure storedProcedure);

    /**
     * @param subqueryCompareCriteria
     */
    void visit(ISubqueryCompareCriteria subqueryCompareCriteria);

    /**
     * @param subqueryFromClause
     */
    void visit(ISubqueryFromClause subqueryFromClause);

    /**
     * @param subquerySetCriteria
     */
    void visit(ISubquerySetCriteria subquerySetCriteria);

    /**
     * @param unaryFromClause
     */
    void visit(IUnaryFromClause unaryFromClause);

    /**
     * @param update
     */
    void visit(IUpdate update);

}

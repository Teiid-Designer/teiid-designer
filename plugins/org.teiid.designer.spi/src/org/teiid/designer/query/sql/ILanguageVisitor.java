/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import org.teiid.designer.query.sql.lang.IAlterProcedure;
import org.teiid.designer.query.sql.lang.IAlterTrigger;
import org.teiid.designer.query.sql.lang.IAlterView;
import org.teiid.designer.query.sql.lang.IArrayTable;
import org.teiid.designer.query.sql.lang.IBatchedUpdateCommand;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICreate;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IDependentSetCriteria;
import org.teiid.designer.query.sql.lang.IDrop;
import org.teiid.designer.query.sql.lang.IDynamicCommand;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IExpressionCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IJoinType;
import org.teiid.designer.query.sql.lang.ILimit;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IObjectTable;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.designer.query.sql.lang.IProcedureContainer;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetClause;
import org.teiid.designer.query.sql.lang.ISetClauseList;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.ITextTable;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.lang.IWithQueryCommand;
import org.teiid.designer.query.sql.lang.IXMLTable;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.IBranchingStatement;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.ICriteriaSelector;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IExceptionExpression;
import org.teiid.designer.query.sql.proc.IHasCriteria;
import org.teiid.designer.query.sql.proc.IIfStatement;
import org.teiid.designer.query.sql.proc.ILoopStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.proc.IReturnStatement;
import org.teiid.designer.query.sql.proc.ITranslateCriteria;
import org.teiid.designer.query.sql.proc.ITriggerAction;
import org.teiid.designer.query.sql.proc.IWhileStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IArray;
import org.teiid.designer.query.sql.symbol.ICaseExpression;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IDerivedColumn;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IQueryString;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.designer.query.sql.symbol.ISearchedCaseExpression;
import org.teiid.designer.query.sql.symbol.ITextLine;
import org.teiid.designer.query.sql.symbol.IWindowFunction;
import org.teiid.designer.query.sql.symbol.IXMLAttributes;
import org.teiid.designer.query.sql.symbol.IXMLElement;
import org.teiid.designer.query.sql.symbol.IXMLForest;
import org.teiid.designer.query.sql.symbol.IXMLNamespaces;
import org.teiid.designer.query.sql.symbol.IXMLParse;
import org.teiid.designer.query.sql.symbol.IXMLQuery;
import org.teiid.designer.query.sql.symbol.IXMLSerialize;



/**
 *
 */
public interface ILanguageVisitor {
     
    // Visitor methods for language objects
    public void visit(IBatchedUpdateCommand obj);
    public void visit(IBetweenCriteria obj);
    public void visit(ICaseExpression obj);
    public void visit(ICompareCriteria obj);
    public void visit(ICompoundCriteria obj);
    public void visit(IDelete obj);
    public void visit(IExistsCriteria obj);
    public void visit(IFrom obj);
    public void visit(IGroupBy obj);
    public void visit(IInsert obj);
    public void visit(IIsNullCriteria obj);
    public void visit(IJoinPredicate obj);
    public void visit(IJoinType obj);
    public void visit(ILimit obj);
    public void visit(IMatchCriteria obj);
    public void visit(INotCriteria obj);
    public void visit(IOption obj);
    public void visit(IOrderBy obj);
    public void visit(IQuery obj);
    public void visit(ISearchedCaseExpression obj);
    public void visit(ISelect obj);
    public void visit(ISetCriteria obj);
    public void visit(ISetQuery obj);
    public void visit(IStoredProcedure obj);
    public void visit(ISubqueryCompareCriteria obj);
    public void visit(ISubqueryFromClause obj);
    public void visit(ISubquerySetCriteria obj);
    public void visit(IUnaryFromClause obj);
    public void visit(IUpdate obj);
    public void visit(IInto obj);
    public void visit(IDependentSetCriteria obj);
    public void visit(ICreate obj);
    public void visit(IDrop obj);

    // Visitor methods for symbol objects
    public void visit(IAggregateSymbol obj);
    public void visit(IAliasSymbol obj);
    public void visit(IMultipleElementSymbol obj);
    public void visit(IConstant obj);
    public void visit(IElementSymbol obj);
    public void visit(IExpressionSymbol obj);
    public void visit(IFunction obj);
    public void visit(IGroupSymbol obj);
    public void visit(IReference obj);
    public void visit(IScalarSubquery obj);
    
    // Visitor methods for procedure language objects    
    public void visit(IAssignmentStatement obj);
    public void visit(IBlock obj);
    public void visit(ICommandStatement obj);
    public void visit(ICreateProcedureCommand obj);
    public void visit(ICriteriaSelector obj);
    public void visit(IDeclareStatement obj);
    public void visit(IHasCriteria obj);
    public void visit(IIfStatement obj);
    public void visit(IRaiseStatement obj);
    public void visit(ITranslateCriteria obj);
    public void visit(IBranchingStatement obj);
    public void visit(IWhileStatement obj);
    public void visit(ILoopStatement obj);
    public void visit(IDynamicCommand obj);
    public void visit(IProcedureContainer obj);
    public void visit(ISetClauseList obj);
    public void visit(ISetClause obj);
    public void visit(IOrderByItem obj);
    public void visit(IXMLElement obj);
    public void visit(IXMLAttributes obj);
    public void visit(IXMLForest obj);
    public void visit(IXMLNamespaces obj);
    public void visit(ITextTable obj);
    public void visit(ITextLine obj);
    public void visit(IXMLTable obj);
    public void visit(IDerivedColumn obj);
    public void visit(IXMLSerialize obj);
    public void visit(IXMLQuery obj);
    public void visit(IQueryString obj);
    public void visit(IXMLParse obj);
    public void visit(IExpressionCriteria obj);
    public void visit(IWithQueryCommand obj);
    public void visit(ITriggerAction obj);
    public void visit(IArrayTable obj);

    public void visit(IAlterView obj);
    public void visit(IAlterProcedure obj);
    public void visit(IAlterTrigger obj);

    public void visit(IWindowFunction windowFunction);
    
    public void visit(IArray array);
    public void visit(IObjectTable objectTable);

    public void visit(IExceptionExpression obj);

    public void visit(IReturnStatement obj);

}

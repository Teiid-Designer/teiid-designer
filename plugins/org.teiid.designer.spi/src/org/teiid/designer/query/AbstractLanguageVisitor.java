/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IAlterProcedure;
import org.teiid.designer.query.sql.lang.IAlterTrigger;
import org.teiid.designer.query.sql.lang.IAlterView;
import org.teiid.designer.query.sql.lang.IArrayTable;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICreate;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IDrop;
import org.teiid.designer.query.sql.lang.IDynamicCommand;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IExpressionCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IIsDistinctCriteria;
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
import org.teiid.designer.query.sql.symbol.IWindowSpecification;
import org.teiid.designer.query.sql.symbol.IXMLAttributes;
import org.teiid.designer.query.sql.symbol.IXMLCast;
import org.teiid.designer.query.sql.symbol.IXMLElement;
import org.teiid.designer.query.sql.symbol.IXMLExists;
import org.teiid.designer.query.sql.symbol.IXMLForest;
import org.teiid.designer.query.sql.symbol.IXMLNamespaces;
import org.teiid.designer.query.sql.symbol.IXMLParse;
import org.teiid.designer.query.sql.symbol.IXMLQuery;
import org.teiid.designer.query.sql.symbol.IXMLSerialize;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 *
 */
public abstract class AbstractLanguageVisitor implements ILanguageVisitor {

    @Override
    public void visit(IBetweenCriteria obj) {
    }

    @Override
    public void visit(ICaseExpression obj) {
    }

    @Override
    public void visit(ICompareCriteria obj) {
    }

    @Override
    public void visit(ICompoundCriteria obj) {
    }

    @Override
    public void visit(ICreate obj) {
    }

    @Override
    public void visit(IDelete obj) {
    }

    @Override
    public void visit(IExistsCriteria obj) {
    }

    @Override
    public void visit(IFrom obj) {
    }

    @Override
    public void visit(IGroupBy obj) {
    }

    @Override
    public void visit(IInsert obj) {
    }

    @Override
    public void visit(IIsNullCriteria obj) {
    }

    @Override
    public void visit(IJoinPredicate obj) {
    }

    @Override
    public void visit(IJoinType obj) {
    }

    @Override
    public void visit(ILimit obj) {
    }

    @Override
    public void visit(IMatchCriteria obj) {
    }

    @Override
    public void visit(INotCriteria obj) {
    }

    @Override
    public void visit(IOption obj) {
    }

    @Override
    public void visit(IOrderBy obj) {
    }

    @Override
    public void visit(IQuery obj) {
    }

    @Override
    public void visit(ISearchedCaseExpression obj) {
    }

    @Override
    public void visit(ISelect obj) {
    }

    @Override
    public void visit(ISetCriteria obj) {
    }

    @Override
    public void visit(ISetQuery obj) {
    }

    @Override
    public void visit(IStoredProcedure obj) {
    }

    @Override
    public void visit(ISubqueryCompareCriteria obj) {
    }

    @Override
    public void visit(ISubqueryFromClause obj) {
    }

    @Override
    public void visit(ISubquerySetCriteria obj) {
    }

    @Override
    public void visit(IUnaryFromClause obj) {
    }

    @Override
    public void visit(IUpdate obj) {
    }

    @Override
    public void visit(IInto obj) {
    }

    @Override
    public void visit(IDrop obj) {
    }

    @Override
    public void visit(IAggregateSymbol obj) {
    }

    @Override
    public void visit(IAliasSymbol obj) {
    }

    @Override
    public void visit(IMultipleElementSymbol obj) {
    }

    @Override
    public void visit(IConstant obj) {
    }

    @Override
    public void visit(IElementSymbol obj) {
    }

    @Override
    public void visit(IExpressionSymbol obj) {
    }

    @Override
    public void visit(IIsDistinctCriteria obj) {
    }

    @Override
    public void visit(IFunction obj) {
    }

    @Override
    public void visit(IGroupSymbol obj) {
    }

    @Override
    public void visit(IReference obj) {
    }

    @Override
    public void visit(IScalarSubquery obj) {
    }

    @Override
    public void visit(IAssignmentStatement obj) {
    }

    @Override
    public void visit(IBlock obj) {
    }

    @Override
    public void visit(ICommandStatement obj) {
    }

    @Override
    public void visit(ICreateProcedureCommand obj) {
    }

    @Override
    public void visit(ICriteriaSelector obj) {
    }

    @Override
    public void visit(IDeclareStatement obj) {
    }

    @Override
    public void visit(IHasCriteria obj) {
    }

    @Override
    public void visit(IIfStatement obj) {
    }

    @Override
    public void visit(IRaiseStatement obj) {
    }

    @Override
    public void visit(ITranslateCriteria obj) {
    }

    @Override
    public void visit(IBranchingStatement obj) {
    }

    @Override
    public void visit(IWhileStatement obj) {
    }

    @Override
    public void visit(ILoopStatement obj) {
    }

    @Override
    public void visit(IDynamicCommand obj) {
    }

    @Override
    public void visit(IProcedureContainer obj) {
    }

    @Override
    public void visit(ISetClauseList obj) {
    }

    @Override
    public void visit(ISetClause obj) {
    }

    @Override
    public void visit(IOrderByItem obj) {
    }

    @Override
    public void visit(IXMLElement obj) {
    }

    @Override
    public void visit(IXMLAttributes obj) {
    }

    @Override
    public void visit(IXMLForest obj) {
    }

    @Override
    public void visit(IXMLNamespaces obj) {
    }

    @Override
    public void visit(ITextTable obj) {
    }

    @Override
    public void visit(ITextLine obj) {
    }

    @Override
    public void visit(IXMLTable obj) {
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(IXMLExists obj) {
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(IXMLCast obj) {
    }

    @Override
    public void visit(IDerivedColumn obj) {
    }

    @Override
    public void visit(IXMLSerialize obj) {
    }

    @Override
    public void visit(IXMLQuery obj) {
    }

    @Override
    public void visit(IQueryString obj) {
    }

    @Override
    public void visit(IXMLParse obj) {
    }

    @Override
    public void visit(IExpressionCriteria obj) {
    }

    @Override
    public void visit(IWithQueryCommand obj) {
    }

    @Override
    public void visit(ITriggerAction obj) {
    }

    @Override
    public void visit(IArrayTable obj) {
    }

    @Override
    public void visit(IObjectTable objectTable) {
    }

    @Override
    public void visit(IAlterView obj) {
    }

    @Override
    public void visit(IAlterProcedure obj) {
    }

    @Override
    public void visit(IAlterTrigger obj) {
    }

    @Override
    public void visit(IWindowFunction windowFunction) {
    }

    @Override
    public void visit(IArray array) {
    }

    @Override
    public void visit(IExceptionExpression exceptionExpression) {
    }

    @Override
    public void visit(IReturnStatement returnStatement) {
    }

    @Override
    public void visit(IWindowSpecification windowSpecification) {
    }
}

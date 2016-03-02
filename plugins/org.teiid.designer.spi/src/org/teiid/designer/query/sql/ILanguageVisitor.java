/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import org.teiid.designer.annotation.Since;
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
public interface ILanguageVisitor {
     
    // Visitor methods for language objects
    void visit(IBetweenCriteria obj);
    void visit(ICaseExpression obj);
    void visit(ICompareCriteria obj);
    void visit(ICompoundCriteria obj);
    void visit(ICreate obj);
    void visit(IDelete obj);
    void visit(IExistsCriteria obj);
    void visit(IFrom obj);
    void visit(IGroupBy obj);
    void visit(IInsert obj);
    void visit(IIsNullCriteria obj);
    void visit(IJoinPredicate obj);
    void visit(IJoinType obj);
    void visit(ILimit obj);
    void visit(IMatchCriteria obj);
    void visit(INotCriteria obj);
    void visit(IOption obj);
    void visit(IOrderBy obj);
    void visit(IQuery obj);
    void visit(ISearchedCaseExpression obj);
    void visit(ISelect obj);
    void visit(ISetCriteria obj);
    void visit(ISetQuery obj);
    void visit(IStoredProcedure obj);
    void visit(ISubqueryCompareCriteria obj);
    void visit(ISubqueryFromClause obj);
    void visit(ISubquerySetCriteria obj);
    void visit(IUnaryFromClause obj);
    void visit(IUpdate obj);
    void visit(IInto obj);
    void visit(IDrop obj);

    // Visitor methods for symbol objects
    void visit(IAggregateSymbol obj);
    void visit(IAliasSymbol obj);
    void visit(IArray obj);
    void visit(IMultipleElementSymbol obj);
    void visit(IConstant obj);
    void visit(IElementSymbol obj);
    void visit(IExpressionSymbol obj);

    @Since(Version.TEIID_8_12_4)
    void visit(IIsDistinctCriteria obj);

    void visit(IFunction obj);
    void visit(IGroupSymbol obj);
    void visit(IReference obj);
    void visit(IScalarSubquery obj);
    
    // Visitor methods for procedure language objects    
    void visit(IAssignmentStatement obj);
    void visit(IBlock obj);
    void visit(ICommandStatement obj);
    void visit(ICreateProcedureCommand obj);
    void visit(ICriteriaSelector obj);
    void visit(IDeclareStatement obj);
    void visit(IHasCriteria obj);
    void visit(IIfStatement obj);
    void visit(IRaiseStatement obj);
    void visit(ITranslateCriteria obj);
    void visit(IBranchingStatement obj);
    void visit(IWhileStatement obj);
    void visit(ILoopStatement obj);
    void visit(IDynamicCommand obj);
    void visit(ISetClauseList obj);
    void visit(ISetClause obj);
    void visit(IOrderByItem obj);
    void visit(IXMLElement obj);
    void visit(IXMLAttributes obj);
    void visit(IXMLForest obj);
    void visit(IXMLNamespaces obj);
    void visit(ITextTable obj);
    void visit(ITextLine obj);
    void visit(IXMLTable obj);
    
    @Since(Version.TEIID_8_10)
    void visit(IXMLExists obj);
    
    @Since(Version.TEIID_8_10)
    void visit(IXMLCast obj);

    void visit(IDerivedColumn obj);
    void visit(IXMLSerialize obj);
    void visit(IXMLQuery obj);
    void visit(IQueryString obj);
    void visit(IXMLParse obj);
    void visit(IExpressionCriteria obj);
    void visit(IWithQueryCommand obj);
    void visit(ITriggerAction obj);
    void visit(IArrayTable obj);
    void visit(IProcedureContainer obj);

    void visit(IAlterView obj);
    void visit(IAlterProcedure obj);
    void visit(IAlterTrigger obj);

    void visit(IWindowFunction windowFunction);

    void visit(IObjectTable objectTable);

    void visit(IExceptionExpression obj);

    void visit(IReturnStatement obj);

    void visit(IWindowSpecification obj);

}

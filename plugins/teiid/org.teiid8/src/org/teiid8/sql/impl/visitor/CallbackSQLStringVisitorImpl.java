/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql.impl.visitor;

import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitorCallback;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.ILanguageObject;
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
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid8.sql.impl.AliasSymbolImpl;
import org.teiid8.sql.impl.AssignmentStatementImpl;
import org.teiid8.sql.impl.BetweenCriteriaImpl;
import org.teiid8.sql.impl.BlockImpl;
import org.teiid8.sql.impl.CommandStatementImpl;
import org.teiid8.sql.impl.CompareCriteriaImpl;
import org.teiid8.sql.impl.CompoundCriteriaImpl;
import org.teiid8.sql.impl.ConstantImpl;
import org.teiid8.sql.impl.CreateProcedureCommandImpl;
import org.teiid8.sql.impl.DeleteImpl;
import org.teiid8.sql.impl.ElementSymbolImpl;
import org.teiid8.sql.impl.ExistsCriteriaImpl;
import org.teiid8.sql.impl.ExpressionSymbolImpl;
import org.teiid8.sql.impl.FromImpl;
import org.teiid8.sql.impl.FunctionImpl;
import org.teiid8.sql.impl.GroupByImpl;
import org.teiid8.sql.impl.GroupSymbolImpl;
import org.teiid8.sql.impl.InsertImpl;
import org.teiid8.sql.impl.IntoImpl;
import org.teiid8.sql.impl.IsNullCriteriaImpl;
import org.teiid8.sql.impl.JoinPredicateImpl;
import org.teiid8.sql.impl.LanguageObjectImpl;
import org.teiid8.sql.impl.MatchCriteriaImpl;
import org.teiid8.sql.impl.MultipleElementSymbolImpl;
import org.teiid8.sql.impl.NotCriteriaImpl;
import org.teiid8.sql.impl.OptionImpl;
import org.teiid8.sql.impl.OrderByImpl;
import org.teiid8.sql.impl.OrderByItemImpl;
import org.teiid8.sql.impl.QueryImpl;
import org.teiid8.sql.impl.RaiseStatementImpl;
import org.teiid8.sql.impl.ReferenceImpl;
import org.teiid8.sql.impl.ScalarSubqueryImpl;
import org.teiid8.sql.impl.SelectImpl;
import org.teiid8.sql.impl.SetCriteriaImpl;
import org.teiid8.sql.impl.SetQueryImpl;
import org.teiid8.sql.impl.StoredProcedureImpl;
import org.teiid8.sql.impl.SubqueryCompareCriteriaImpl;
import org.teiid8.sql.impl.SubqueryFromClauseImpl;
import org.teiid8.sql.impl.SubquerySetCriteriaImpl;
import org.teiid8.sql.impl.SyntaxFactory;
import org.teiid8.sql.impl.UnaryFromClauseImpl;
import org.teiid8.sql.impl.UpdateImpl;

/**
 *
 */
public class CallbackSQLStringVisitorImpl extends SQLStringVisitor implements ISQLStringVisitor {

    private final ISQLStringVisitorCallback callback;
    
    private final SyntaxFactory factory = new SyntaxFactory();
    
    /**
     * @param callback 
     */
    public CallbackSQLStringVisitorImpl(ISQLStringVisitorCallback callback) {
        this.callback = callback;
    }
    
    @Override
    protected void visitNode(LanguageObject obj) {
        ILanguageObject languageObject = factory.createLanguageObject(obj);
        callback.visitNode(languageObject);
    }
    
    @Override
    protected void addTabs(int level) {
        callback.addTabs(level);
    }
    
    @Override
    protected void visitCriteria(String keyWord, Criteria crit) {
        ICriteria criteria = (ICriteria) factory.createLanguageObject(crit);
        callback.visitCriteria(keyWord, criteria);
    }
    
    @Override
    protected void append(Object value) {
        callback.append(value);
    }
    
    @Override
    protected void beginClause(int level) {
        callback.beginClause(level);
    }
    
    @Override
    public String getSQLString(ILanguageObject languageObject) {
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl) languageObject;
        return getSQLString(languageObjectImpl.getDelegate());
    }

    @Override
    public void visit(IGroupSymbol symbol) {
        GroupSymbolImpl groupSymbolImpl = (GroupSymbolImpl) symbol;
        visit(groupSymbolImpl.getDelegate());
    }

    @Override
    public void visit(IElementSymbol symbol) {
        ElementSymbolImpl elementSymbolImpl = (ElementSymbolImpl) symbol;
        visit(elementSymbolImpl.getDelegate());
    }

    @Override
    public void visit(IOption option) {
        OptionImpl optionImpl = (OptionImpl) option;
        visit(optionImpl.getDelegate());
    }

    @Override
    public void visit(IFunction function) {
        FunctionImpl functionImpl = (FunctionImpl) function;
        visit(functionImpl.getDelegate());
    }

    @Override
    public void visit(IAliasSymbol aliasSymbol) {
        AliasSymbolImpl aliasSymbolImpl = (AliasSymbolImpl) aliasSymbol;
        visit(aliasSymbolImpl.getDelegate());
    }

    @Override
    public void visit(IAssignmentStatement assignmentStatement) {
        AssignmentStatementImpl assignmentStatementImpl = (AssignmentStatementImpl) assignmentStatement;
        visit(assignmentStatementImpl.getDelegate());
    }

    @Override
    public void visit(IBetweenCriteria betweenCriteria) {
        BetweenCriteriaImpl betweenCriteriaImpl = (BetweenCriteriaImpl) betweenCriteria;
        visit(betweenCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IBlock block) {
        BlockImpl blockImpl = (BlockImpl) block;
        visit(blockImpl.getDelegate());
    }

    @Override
    public void visit(ICommandStatement commandStatement) {
        CommandStatementImpl commandStatementImpl = (CommandStatementImpl) commandStatement;
        visit(commandStatementImpl.getDelegate());
    }

    @Override
    public void visit(ICompareCriteria compareCriteria) {
        CompareCriteriaImpl compareCriteriaImpl = (CompareCriteriaImpl) compareCriteria;
        visit(compareCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(ICompoundCriteria compoundCriteria) {
        CompoundCriteriaImpl compoundCriteriaImpl = (CompoundCriteriaImpl) compoundCriteria;
        visit(compoundCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IConstant constant) {
        ConstantImpl constantImpl = (ConstantImpl) constant;
        visit(constantImpl.getDelegate());
    }

    @Override
    public void visit(ICreateProcedureCommand createProcedureCommand) {
        CreateProcedureCommandImpl createProcedureCommandImpl = (CreateProcedureCommandImpl) createProcedureCommand;
        visit(createProcedureCommandImpl.getDelegate());
    }

    @Override
    public void visit(IDelete delete) {
        DeleteImpl deleteImpl = (DeleteImpl) delete;
        visit(deleteImpl.getDelegate());
    }

    @Override
    public void visit(IExistsCriteria existsCriteria) {
        ExistsCriteriaImpl existsCriteriaImpl = (ExistsCriteriaImpl) existsCriteria;
        visit(existsCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IExpressionSymbol expressionSymbol) {
        ExpressionSymbolImpl expressionSymbolImpl = (ExpressionSymbolImpl) expressionSymbol;
        visit(expressionSymbolImpl.getDelegate());
    }

    @Override
    public void visit(IFrom from) {
        FromImpl fromImpl = (FromImpl) from;
        visit(fromImpl.getDelegate());
    }

    @Override
    public void visit(IGroupBy groupBy) {
        GroupByImpl groupByImpl = (GroupByImpl) groupBy;
        visit(groupByImpl.getDelegate());
    }

    @Override
    public void visit(IInsert insert) {
        InsertImpl insertImpl = (InsertImpl) insert;
        visit(insertImpl.getDelegate());
    }

    @Override
    public void visit(IInto into) {
        IntoImpl intoImpl = (IntoImpl) into;
        visit(intoImpl.getDelegate());
    }

    @Override
    public void visit(IIsNullCriteria isNullCriteria) {
        IsNullCriteriaImpl isNullCriteriaImpl = (IsNullCriteriaImpl) isNullCriteria;
        visit(isNullCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IJoinPredicate joinPredicate) {
        JoinPredicateImpl joinPredicateImpl = (JoinPredicateImpl) joinPredicate;
        visit(joinPredicateImpl.getDelegate());
    }

    @Override
    public void visit(IMatchCriteria matchCriteria) {
        MatchCriteriaImpl matchCriteriaImpl = (MatchCriteriaImpl) matchCriteria;
        visit(matchCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IMultipleElementSymbol multipleElementSymbol) {
        MultipleElementSymbolImpl multipleElementSymbolImpl = (MultipleElementSymbolImpl) multipleElementSymbol;
        visit(multipleElementSymbolImpl.getDelegate());
    }

    @Override
    public void visit(INotCriteria notCriteria) {
        NotCriteriaImpl notCriteriaImpl = (NotCriteriaImpl) notCriteria;
        visit(notCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IOrderBy orderBy) {
        OrderByImpl orderByImpl = (OrderByImpl) orderBy;
        visit(orderByImpl.getDelegate());
    }

    @Override
    public void visit(IOrderByItem orderByItem) {
        OrderByItemImpl orderByItemImpl = (OrderByItemImpl) orderByItem;
        visit(orderByItemImpl.getDelegate());
    }

    @Override
    public void visit(IQuery query) {
        QueryImpl queryImpl = (QueryImpl) query;
        visit(queryImpl.getDelegate());
    }

    @Override
    public void visit(IRaiseStatement raiseStatement) {
        RaiseStatementImpl raiseStatementImpl = (RaiseStatementImpl) raiseStatement;
        visit(raiseStatementImpl.getDelegate());
    }

    @Override
    public void visit(IReference reference) {
        ReferenceImpl referenceImpl = (ReferenceImpl) reference;
        visit(referenceImpl.getDelegate());
    }

    @Override
    public void visit(IScalarSubquery scalarSubquery) {
        ScalarSubqueryImpl scalarSubqueryImpl = (ScalarSubqueryImpl) scalarSubquery;
        visit(scalarSubqueryImpl.getDelegate());
    }

    @Override
    public void visit(ISelect select) {
        SelectImpl selectImpl = (SelectImpl) select;
        visit(selectImpl.getDelegate());
    }

    @Override
    public void visit(ISetCriteria setCriteria) {
        SetCriteriaImpl setCriteriaImpl = (SetCriteriaImpl) setCriteria;
        visit(setCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(ISetQuery setQuery) {
        SetQueryImpl setQueryImpl = (SetQueryImpl) setQuery;
        visit(setQueryImpl.getDelegate());
    }

    @Override
    public void visit(IStoredProcedure storedProcedure) {
        StoredProcedureImpl storedProcedureImpl = (StoredProcedureImpl) storedProcedure;
        visit(storedProcedureImpl.getDelegate());
    }

    @Override
    public void visit(ISubqueryCompareCriteria subqueryCompareCriteria) {
        SubqueryCompareCriteriaImpl subqueryCompareCriteriaImpl = (SubqueryCompareCriteriaImpl) subqueryCompareCriteria;
        visit(subqueryCompareCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(ISubqueryFromClause subqueryFromClause) {
        SubqueryFromClauseImpl subqueryFromClauseImpl = (SubqueryFromClauseImpl) subqueryFromClause;
        visit(subqueryFromClauseImpl.getDelegate());
    }

    @Override
    public void visit(ISubquerySetCriteria subquerySetCriteria) {
        SubquerySetCriteriaImpl subquerySetCriteriaImpl = (SubquerySetCriteriaImpl) subquerySetCriteria;
        visit(subquerySetCriteriaImpl.getDelegate());
    }

    @Override
    public void visit(IUnaryFromClause unaryFromClause) {
        UnaryFromClauseImpl unaryFromClauseImpl = (UnaryFromClauseImpl) unaryFromClause;
        visit(unaryFromClauseImpl.getDelegate());
    }

    @Override
    public void visit(IUpdate update) {
        UpdateImpl updateImpl = (UpdateImpl) update;
        visit(updateImpl.getDelegate());
    }
}

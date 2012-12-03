/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql.impl.visitor;

import org.teiid.designer.query.sql.ISQLStringVisitor;
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
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid8.sql.impl.LanguageObjectImpl;

/**
 *
 */
public class SQLStringVisitorImpl implements ISQLStringVisitor {

    @Override
    public String getSQLString(ILanguageObject languageObject) {
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl) languageObject;
        return SQLStringVisitor.getSQLString(languageObjectImpl.getDelegate());
    }

    @Override
    public void visit(IGroupSymbol symbol) {
    }

    @Override
    public void visit(IElementSymbol symbol) {
    }

    @Override
    public void visit(IOption option) {
    }

    @Override
    public void visit(IFunction function) {
    }

    @Override
    public void visit(IAliasSymbol aliasSymbol) {
    }

    @Override
    public void visit(IAssignmentStatement assignmentStatement) {
    }

    @Override
    public void visit(IBetweenCriteria betweenCriteria) {
    }

    @Override
    public void visit(IBlock block) {
    }

    @Override
    public void visit(ICommandStatement commandStatement) {
    }

    @Override
    public void visit(ICompareCriteria compareCriteria) {
    }

    @Override
    public void visit(ICompoundCriteria compoundCriteria) {
    }

    @Override
    public void visit(IConstant constant) {
    }

    @Override
    public void visit(ICreateProcedureCommand createProcedureCommand) {
    }

    @Override
    public void visit(IDelete delete) {
    }

    @Override
    public void visit(IExistsCriteria existsCriteria) {
    }

    @Override
    public void visit(IExpressionSymbol expressionSymbol) {
    }

    @Override
    public void visit(IFrom from) {
    }

    @Override
    public void visit(IGroupBy groupBy) {
    }

    @Override
    public void visit(IInsert insert) {
    }

    @Override
    public void visit(IInto into) {
    }

    @Override
    public void visit(IIsNullCriteria isNullCriteria) {
    }

    @Override
    public void visit(IJoinPredicate joinPredicate) {
    }

    @Override
    public void visit(IMatchCriteria matchCriteria) {
    }

    @Override
    public void visit(IMultipleElementSymbol multipleElementSymbol) {
    }

    @Override
    public void visit(INotCriteria notCriteria) {
    }

    @Override
    public void visit(IOrderBy orderBy) {
    }

    @Override
    public void visit(IOrderByItem orderByItem) {
    }

    @Override
    public void visit(IQuery query) {
    }

    @Override
    public void visit(IRaiseStatement raiseStatement) {
    }

    @Override
    public void visit(IReference reference) {
    }

    @Override
    public void visit(IScalarSubquery scalarSubquery) {
    }

    @Override
    public void visit(ISelect select) {
    }

    @Override
    public void visit(ISetCriteria setCriteria) {
    }

    @Override
    public void visit(ISetQuery setQuery) {
    }

    @Override
    public void visit(IStoredProcedure storedProcedure) {
    }

    @Override
    public void visit(ISubqueryCompareCriteria subqueryCompareCriteria) {
    }

    @Override
    public void visit(ISubqueryFromClause subqueryFromClause) {
    }

    @Override
    public void visit(ISubquerySetCriteria subquerySetCriteria) {
    }

    @Override
    public void visit(IUnaryFromClause unaryFromClause) {
    }

    @Override
    public void visit(IUpdate update) {
    }
}

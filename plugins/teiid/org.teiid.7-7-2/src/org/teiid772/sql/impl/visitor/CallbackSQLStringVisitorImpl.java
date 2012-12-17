/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitorCallback;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BatchedUpdateCommand;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.DependentSetCriteria;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid772.sql.impl.LanguageObjectImpl;
import org.teiid772.sql.impl.SyntaxFactory;

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
    protected void visitCriteria(String keyWord,
                                 Criteria crit) {
        ICriteria criteria = (ICriteria)factory.createLanguageObject(crit);
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
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl)languageObject;
        return getSQLString(languageObjectImpl.getDelegate());
    }

    @Override
    public void visit(ILanguageObject languageObject) {
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl)languageObject;
        LanguageObject delegate = languageObjectImpl.getDelegate();

        if (delegate instanceof AlterView) {
            visit((AlterView)delegate);
            return;
        }

        if (delegate instanceof AlterProcedure) {
            visit((AlterProcedure)delegate);
            return;
        }

        if (delegate instanceof AlterTrigger) {
            visit((AlterTrigger)delegate);
            return;
        }
        
        if (delegate instanceof BatchedUpdateCommand) {
            visit((BatchedUpdateCommand)delegate);
            return;
        }
        
        if (delegate instanceof Create) {
            visit((Create)delegate);
            return;
        }
        
        if (delegate instanceof CreateUpdateProcedureCommand) {
            visit((CreateUpdateProcedureCommand)delegate);
            return;
        }
        
        if (delegate instanceof Drop) {
            visit((Drop)delegate);
            return;
        }
        
        if (delegate instanceof DynamicCommand) {
            visit((DynamicCommand)delegate);
            return;
        }
        
        if (delegate instanceof Insert) {
            visit((Insert)delegate);
            return;
        }
        
        if (delegate instanceof Delete) {
            visit((Delete)delegate);
            return;
        }
        
        if (delegate instanceof Update) {
            visit((Update)delegate);
            return;
        }
        
        if (delegate instanceof StoredProcedure) {
            visit((StoredProcedure)delegate);
            return;
        }
        
        if (delegate instanceof SetQuery) {
            visit((SetQuery)delegate);
            return;
        }
        
        if (delegate instanceof Query) {
            visit((Query)delegate);
            return;
        }
        
        if (delegate instanceof TriggerAction) {
            visit((TriggerAction)delegate);
            return;
        }
        
        if (delegate instanceof DerivedColumn) {
            visit((DerivedColumn)delegate);
            return;
        }
        
        if (delegate instanceof From) {
            visit((From)delegate);
            return;
        }
        
        if (delegate instanceof UnaryFromClause) {
            visit((UnaryFromClause)delegate);
            return;
        }
        
        if (delegate instanceof XMLTable) {
            visit((XMLTable)delegate);
            return;
        }
        
        if (delegate instanceof TextTable) {
            visit((TextTable)delegate);
            return;
        }
        
        if (delegate instanceof ArrayTable) {
            visit((ArrayTable)delegate);
            return;
        }
        
        if (delegate instanceof SubqueryFromClause) {
            visit((SubqueryFromClause)delegate);
            return;
        }
        
        if (delegate instanceof JoinPredicate) {
            visit((JoinPredicate)delegate);
            return;
        }
        
        if (delegate instanceof GroupBy) {
            visit((GroupBy)delegate);
            return;
        }
        
        if (delegate instanceof Into) {
            visit((Into)delegate);
            return;
        }
        
        if (delegate instanceof JoinType) {
            visit((JoinType)delegate);
            return;
        }
        
        if (delegate instanceof Limit) {
            visit((Limit)delegate);
            return;
        }
        
        if (delegate instanceof Option) {
            visit((Option)delegate);
            return;
        }
        
        if (delegate instanceof OrderBy) {
            visit((OrderBy)delegate);
            return;
        }
        
        if (delegate instanceof OrderByItem) {
            visit((OrderByItem)delegate);
            return;
        }
        
        if (delegate instanceof Select) {
            visit((Select)delegate);
            return;
        }
        
        if (delegate instanceof SetClause) {
            visit((SetClause)delegate);
            return;
        }

        if (delegate instanceof SetClauseList) {
            visit((SetClauseList)delegate);
            return;
        }        
        
        if (delegate instanceof DeclareStatement) {
            visit((DeclareStatement)delegate);
            return;
        }      
        
        if (delegate instanceof AssignmentStatement) {
            visit((AssignmentStatement)delegate);
            return;
        }
        
        if (delegate instanceof Block) {
            visit((Block)delegate);
            return;
        }
        
        if (delegate instanceof BranchingStatement) {
            visit((BranchingStatement)delegate);
            return;
        }
        
        if (delegate instanceof CommandStatement) {
            visit((CommandStatement)delegate);
            return;
        }
        
        if (delegate instanceof IfStatement) {
            visit((IfStatement)delegate);
            return;
        }
        
        if (delegate instanceof LoopStatement) {
            visit((LoopStatement)delegate);
            return;
        }
        
        if (delegate instanceof RaiseErrorStatement) {
            visit((RaiseErrorStatement)delegate);
            return;
        }
        
        if (delegate instanceof WhileStatement) {
            visit((WhileStatement)delegate);
            return;
        }        
        
        if (delegate instanceof AliasSymbol) {
            visit((AliasSymbol)delegate);
            return;
        }

        if (delegate instanceof ElementSymbol) {
            visit((ElementSymbol)delegate);
            return;
        }
        
        if (delegate instanceof AggregateSymbol) {
            visit((AggregateSymbol) delegate);
            return;
        }
        
        if (delegate instanceof ExpressionSymbol) {
            visit((ExpressionSymbol)delegate);
            return;
        }

        if (delegate instanceof GroupSymbol) {
            visit((GroupSymbol)delegate);
            return;
        }
        
        if (delegate instanceof WindowFunction) {
            visit((WindowFunction)delegate);
            return;
        }

        if (delegate instanceof WindowSpecification) {
            visit((WindowSpecification)delegate);
            return;
        }

        if (delegate instanceof XMLAttributes) {
            visit((XMLAttributes)delegate);
            return;
        }

        if (delegate instanceof XMLNamespaces) {
            visit((XMLNamespaces)delegate);
            return;
        }
        
        if (delegate instanceof CaseExpression) {
            visit((CaseExpression)delegate);
            return;
        }

        if (delegate instanceof SearchedCaseExpression) {
            visit((SearchedCaseExpression)delegate);
            return;
        }

        if (delegate instanceof Constant) {
            visit((Constant)delegate);
            return;
        }
        
        if (delegate instanceof ExpressionCriteria) {
            visit((ExpressionCriteria)delegate);
            return;
        }
        
        if (delegate instanceof NotCriteria) {
            visit((NotCriteria)delegate);
            return;
        }
        
        if (delegate instanceof CompoundCriteria) {
            visit((CompoundCriteria)delegate);
            return;
        }
        
        if (delegate instanceof CompareCriteria) {
            visit((CompareCriteria)delegate);
            return;
        }
        
        if (delegate instanceof SubqueryCompareCriteria) {
            visit((SubqueryCompareCriteria)delegate);
            return;
        }
        
        if (delegate instanceof DependentSetCriteria) {
            visit((DependentSetCriteria)delegate);
            return;
        }        
        
        if (delegate instanceof SetCriteria) {
            visit((SetCriteria)delegate);
            return;
        }
        
        if (delegate instanceof SubquerySetCriteria) {
            visit((SubquerySetCriteria)delegate);
            return;
        }        
        
        if (delegate instanceof BetweenCriteria) {
            visit((BetweenCriteria)delegate);
            return;
        }
        
        if (delegate instanceof ExistsCriteria) {
            visit((ExistsCriteria)delegate);
            return;
        }
        
        if (delegate instanceof IsNullCriteria) {
            visit((IsNullCriteria)delegate);
            return;
        }

        if (delegate instanceof MatchCriteria) {
            visit((MatchCriteria)delegate);
            return;
        }
        
        if (delegate instanceof Function) {
            visit((Function)delegate);
            return;
        }
        
        if (delegate instanceof MultipleElementSymbol) {
            visit((MultipleElementSymbol)delegate);
            return;
        }

        if (delegate instanceof QueryString) {
            visit((QueryString)delegate);
            return;
        }
        
        if (delegate instanceof Reference) {
            visit((Reference)delegate);
            return;
        }
        
        if (delegate instanceof ScalarSubquery) {
            visit((ScalarSubquery)delegate);
            return;
        }
        
        if (delegate instanceof TextLine) {
            visit((TextLine)delegate);
            return;
        }
        
        if (delegate instanceof XMLElement) {
            visit((XMLElement)delegate);
            return;
        }

        if (delegate instanceof XMLForest) {
            visit((XMLForest)delegate);
            return;
        }

        if (delegate instanceof XMLParse) {
            visit((XMLParse)delegate);
            return;
        }

        if (delegate instanceof XMLQuery) {
            visit((XMLQuery)delegate);
            return;
        }

        if (delegate instanceof XMLSerialize) {
            visit((XMLSerialize)delegate);
            return;
        }

        if (delegate instanceof WithQueryCommand) {
            visit((WithQueryCommand)delegate);
            return;
        }
        
        throw new RuntimeException("Failed to visit delegate of type " + delegate.getClass().getSimpleName()); //$NON-NLS-1$
    }

}

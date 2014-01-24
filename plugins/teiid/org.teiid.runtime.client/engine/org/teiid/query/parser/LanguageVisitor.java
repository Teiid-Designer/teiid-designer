/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.parser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.AbstractLanguageVisitor;
import org.teiid.designer.query.sql.lang.IAlterProcedure;
import org.teiid.designer.query.sql.lang.IAlterTrigger;
import org.teiid.designer.query.sql.lang.IAlterView;
import org.teiid.designer.query.sql.lang.IArrayTable;
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
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.ILimit;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IObjectTable;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
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
import org.teiid.designer.query.sql.symbol.IXMLElement;
import org.teiid.designer.query.sql.symbol.IXMLForest;
import org.teiid.designer.query.sql.symbol.IXMLNamespaces;
import org.teiid.designer.query.sql.symbol.IXMLParse;
import org.teiid.designer.query.sql.symbol.IXMLQuery;
import org.teiid.designer.query.sql.symbol.IXMLSerialize;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ElementSymbol;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.HasCriteria;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.ProjectedColumn;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.lang.proc.AssignmentStatement;
import org.teiid.query.sql.lang.proc.Block;
import org.teiid.query.sql.lang.proc.BranchingStatement;
import org.teiid.query.sql.lang.proc.CommandStatement;
import org.teiid.query.sql.lang.proc.CreateProcedureCommand;
import org.teiid.query.sql.lang.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.lang.proc.DeclareStatement;
import org.teiid.query.sql.lang.proc.ExceptionExpression;
import org.teiid.query.sql.lang.proc.IfStatement;
import org.teiid.query.sql.lang.proc.LoopStatement;
import org.teiid.query.sql.lang.proc.RaiseErrorStatement;
import org.teiid.query.sql.lang.proc.RaiseStatement;
import org.teiid.query.sql.lang.proc.ReturnStatement;
import org.teiid.query.sql.lang.proc.Statement;
import org.teiid.query.sql.lang.proc.TriggerAction;
import org.teiid.query.sql.lang.proc.WhileStatement;
import org.teiid.query.sql.lang.symbol.AggregateSymbol;
import org.teiid.query.sql.lang.symbol.AliasSymbol;
import org.teiid.query.sql.lang.symbol.CaseExpression;
import org.teiid.query.sql.lang.symbol.Constant;
import org.teiid.query.sql.lang.symbol.DerivedColumn;
import org.teiid.query.sql.lang.symbol.ExpressionSymbol;
import org.teiid.query.sql.lang.symbol.Function;
import org.teiid.query.sql.lang.symbol.GroupSymbol;
import org.teiid.query.sql.lang.symbol.JSONObject;
import org.teiid.query.sql.lang.symbol.MultipleElementSymbol;
import org.teiid.query.sql.lang.symbol.QueryString;
import org.teiid.query.sql.lang.symbol.Reference;
import org.teiid.query.sql.lang.symbol.ScalarSubquery;
import org.teiid.query.sql.lang.symbol.SearchedCaseExpression;
import org.teiid.query.sql.lang.symbol.TextLine;
import org.teiid.query.sql.lang.symbol.WindowFunction;
import org.teiid.query.sql.lang.symbol.WindowSpecification;
import org.teiid.query.sql.lang.symbol.XMLAttributes;
import org.teiid.query.sql.lang.symbol.XMLColumn;
import org.teiid.query.sql.lang.symbol.XMLElement;
import org.teiid.query.sql.lang.symbol.XMLForest;
import org.teiid.query.sql.lang.symbol.XMLNamespaces;
import org.teiid.query.sql.lang.symbol.XMLParse;
import org.teiid.query.sql.lang.symbol.XMLQuery;
import org.teiid.query.sql.lang.symbol.XMLSerialize;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public abstract class LanguageVisitor extends AbstractLanguageVisitor {

    protected final ITeiidServerVersion teiidVersion;

    /*
     * Required if nodes are to be created by the visitor
     */
    private final QueryParser parser;

    private static final Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();

    static {
        // cache all the methods on this visitor
        Method[] methods = LanguageVisitor.class.getMethods();
        for (Method method : methods) {
            if (!method.getName().equals("visit")) //$NON-NLS-1$
                continue;

            Class<?>[] params = method.getParameterTypes();
            if (params == null || params.length == 0)
                continue;

            for (Class<?> param : params) {
                if (LanguageObject.class.isAssignableFrom(param)) {
                    methodCache.put(param, method);
                }
            }
        }
    }

    /**
     * Construct new instance of visitor dependent on
     * teiid version.
     *
     * @param teiidVersion used to check visitor methods are
     *                                      applicable
     */
    public LanguageVisitor(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        this.parser = new QueryParser(teiidVersion);
    }

    protected TeiidParser getTeiidParser() {
        return parser.getTeiidParser();
    }

    protected <T extends LanguageObject> T createNode(ASTNodes nodeType) {
        return getTeiidParser().createASTNode(nodeType);
    }

    private Method searchMethodCache(Class<?> methodClass) {
        Method method = methodCache.get(methodClass);
        if (method != null)
            return method;

        // Cannot find any method for this class but it could be a version
        // specific class such as Aggregate8Symbol so the class' interface
        Class<?>[] interfaces = methodClass.getInterfaces();
        if (interfaces == null)
            return null;

        for (Class<?> iface : interfaces) {
            method = methodCache.get(iface);
            if (method != null)
                return method;
        }

        return null;
    }

    protected void isApplicable(ILanguageObject node) {
        Method method = searchMethodCache(node.getClass());
        if (method == null) {
            throw new RuntimeException("No visit method for " + node.getClass()); //$NON-NLS-1$
        }

        String message = "The visit method " + method.toGenericString() + " is not applicable for teiid version " + teiidVersion; //$NON-NLS-1$ //$NON-NLS-2$
        if (AnnotationUtils.hasAnnotation(method, Removed.class)) {
            Removed removed = AnnotationUtils.getAnnotation(method, Removed.class);
            if (AnnotationUtils.isGreaterThanOrEqualTo(removed, teiidVersion)) {
                throw new RuntimeException(message);
            }
        }

        if (AnnotationUtils.hasAnnotation(method, Since.class)) {
            Since since = AnnotationUtils.getAnnotation(method, Since.class);
            if (!AnnotationUtils.isGreaterThanOrEqualTo(since, teiidVersion)) {
                throw new RuntimeException(message);
            }
        }
    }

    public void visit(LanguageObject node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Command node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AlterView node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AlterTrigger node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AlterProcedure node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TriggerAction node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Drop node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(RaiseErrorStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(RaiseStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ExceptionExpression node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Statement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(BranchingStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ReturnStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WhileStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(LoopStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(IfStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DeclareStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CommandStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(CreateProcedureCommand node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(CreateUpdateProcedureCommand node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DynamicCommand node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetClauseList node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetClause node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ProjectedColumn node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(StoredProcedure node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Insert node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Update node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Delete node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(QueryCommand node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WithQueryCommand node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetQuery node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Query node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Into node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Select node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExpressionSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DerivedColumn node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(MultipleElementSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(From node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(FromClause node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(JoinPredicate node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(JoinType node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLSerialize node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ArrayTable node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextTable node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextColumn node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLQuery node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ObjectTable node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ObjectColumn node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLTable node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLColumn node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubqueryFromClause node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(UnaryFromClause node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Criteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CompoundCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(NotCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CompareCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubqueryCompareCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(MatchCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(BetweenCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(IsNullCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubquerySetCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExistsCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(GroupBy node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(OrderBy node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(OrderByItem node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Limit node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Option node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Reference node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CaseExpression node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SearchedCaseExpression node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Function node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLParse node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(QueryString node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLElement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLAttributes node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(JSONObject node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLForest node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLNamespaces node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AssignmentStatement node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ScalarSubquery node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(GroupSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Constant node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ElementSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Block node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExpressionCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AliasSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AggregateSymbol node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WindowFunction node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WindowSpecification node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextLine node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(CriteriaSelector node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(HasCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(TranslateCriteria node) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }


    // Visitor methods for language objects
    @Override
    public void visit(IBetweenCriteria obj) {
        visit((BetweenCriteria) obj);
    }

    @Override
    public void visit(ICaseExpression obj) {
        visit((CaseExpression) obj);
    }

    @Override
    public void visit(ICompareCriteria obj) {
        visit((CompareCriteria) obj);
    }

    @Override
    public void visit(ICompoundCriteria obj) {
        visit((CompoundCriteria) obj);
    }

    @Override
    public void visit(IDelete obj) {
        visit((Delete) obj);
    }

    @Override
    public void visit(IExistsCriteria obj) {
        visit((ExistsCriteria) obj);
    }

    @Override
    public void visit(IFrom obj) {
        visit((From) obj);
    }

    @Override
    public void visit(IGroupBy obj) {
        visit((GroupBy) obj);
    }

    @Override
    public void visit(IInsert obj) {
        visit((Insert) obj);
    }

    @Override
    public void visit(IIsNullCriteria obj) {
        visit((IsNullCriteria) obj);
    }

    @Override
    public void visit(IJoinPredicate obj) {
        visit((JoinPredicate) obj);
    }

    @Override
    public void visit(IJoinType obj) {
        visit((JoinType) obj);
    }

    @Override
    public void visit(ILimit obj) {
        visit((Limit) obj);
    }

    @Override
    public void visit(IMatchCriteria obj) {
        visit((MatchCriteria) obj);
    }

    @Override
    public void visit(INotCriteria obj) {
        visit((NotCriteria) obj);
    }

    @Override
    public void visit(IOption obj) {
        visit((Option) obj);
    }

    @Override
    public void visit(IOrderBy obj) {
        visit((OrderBy) obj);
    }

    @Override
    public void visit(IQuery obj) {
        visit((Query) obj);
    }

    @Override
    public void visit(ISearchedCaseExpression obj) {
        visit((SearchedCaseExpression) obj);
    }

    @Override
    public void visit(ISelect obj) {
        visit((Select) obj);
    }

    @Override
    public void visit(ISetCriteria obj) {
        visit((SetCriteria) obj);
    }

    @Override
    public void visit(ISetQuery obj) {
        visit((SetQuery) obj);
    }

    @Override
    public void visit(IStoredProcedure obj) {
        visit((StoredProcedure) obj);
    }

    @Override
    public void visit(ISubqueryCompareCriteria obj) {
        visit((SubqueryCompareCriteria) obj);
    }

    @Override
    public void visit(ISubqueryFromClause obj) {
        visit((SubqueryFromClause) obj);
    }

    @Override
    public void visit(ISubquerySetCriteria obj) {
        visit((SubquerySetCriteria) obj);
    }

    @Override
    public void visit(IUnaryFromClause obj) {
        visit((UnaryFromClause) obj);
    }

    @Override
    public void visit(IUpdate obj) {
        visit((Update) obj);
    }

    @Override
    public void visit(IInto obj) {
        visit((Into) obj);
    }

    public void visit(IDependentSetCriteria obj) {
        throw new UnsupportedOperationException();
    }

    public void visit(ICreate obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(IDrop obj) {
        visit((Drop) obj);
    }

    // Visitor methods for symbol objects
    @Override
    public void visit(IAggregateSymbol obj) {
        visit((AggregateSymbol) obj);
    }

    @Override
    public void visit(IAliasSymbol obj) {
        visit((AliasSymbol) obj);
    }

    @Override
    public void visit(IMultipleElementSymbol obj) {
        visit((MultipleElementSymbol) obj);
    }

    @Override
    public void visit(IConstant obj) {
        visit((Constant) obj);
    }

    @Override
    public void visit(IElementSymbol obj) {
        visit((ElementSymbol) obj);
    }

    @Override
    public void visit(IExpressionSymbol obj) {
        visit((ExpressionSymbol) obj);
    }

    @Override
    public void visit(IFunction obj) {
        visit((Function) obj);
    }

    @Override
    public void visit(IGroupSymbol obj) {
        visit((GroupSymbol) obj);
    }

    @Override
    public void visit(IReference obj) {
        visit((Reference) obj);
    }

    @Override
    public void visit(IScalarSubquery obj) {
        visit((ScalarSubquery) obj);
    }

    // Visitor methods for procedure language objects    
    @Override
    public void visit(IAssignmentStatement obj) {
        visit((AssignmentStatement) obj);
    }

    @Override
    public void visit(IBlock obj) {
        visit((Block) obj);
    }

    @Override
    public void visit(ICommandStatement obj) {
        visit((CommandStatement) obj);
    }

    @Override
    public void visit(ICreateProcedureCommand obj) {
        if (obj instanceof CreateProcedureCommand)
            visit((CreateProcedureCommand) obj);
        else
            visit((CreateUpdateProcedureCommand) obj);
    }

    @Override
    public void visit(ICriteriaSelector obj) {
        visit((CriteriaSelector) obj);
    }

    @Override
    public void visit(IDeclareStatement obj) {
        visit((IAssignmentStatement) obj);
    }

    @Override
    public void visit(IHasCriteria obj) {
        visit((HasCriteria) obj);
    }

    @Override
    public void visit(IIfStatement obj) {
        visit((IfStatement) obj);
    }

    @Override
    public void visit(IRaiseStatement obj) {
        if (obj instanceof RaiseStatement)
            visit((RaiseStatement) obj);
        else
            visit((RaiseErrorStatement) obj);
    }

    @Override
    public void visit(IBranchingStatement obj) {
        visit((BranchingStatement) obj);
    }

    @Override
    public void visit(ITranslateCriteria obj) {
        visit((TranslateCriteria) obj);
    }

    @Override
    public void visit(IWhileStatement obj) {
        visit((WhileStatement) obj);
    }

    @Override
    public void visit(ILoopStatement obj) {
        visit((LoopStatement) obj);
    }

    @Override
    public void visit(IDynamicCommand obj) {
        visit((DynamicCommand) obj);
    }

    @Override
    public void visit(ISetClauseList obj) {
        visit((SetClauseList) obj);
    }

    @Override
    public void visit(ISetClause obj) {
        visit((SetClause) obj);
    }

    @Override
    public void visit(IOrderByItem obj) {
        visit((OrderByItem) obj);
    }

    @Override
    public void visit(IXMLElement obj) {
        visit((XMLElement) obj);
    }

    @Override
    public void visit(IXMLAttributes obj) {
        visit((XMLAttributes) obj);
    }

    @Override
    public void visit(IXMLForest obj) {
        visit((XMLForest) obj);
    }

    @Override
    public void visit(IXMLNamespaces obj) {
        visit((XMLNamespaces) obj);
    }

    @Override
    public void visit(ITextTable obj) {
        visit((TextTable) obj);
    }

    @Override
    public void visit(ITextLine obj) {
        visit((TextLine) obj);
    }

    @Override
    public void visit(IXMLTable obj) {
        visit((XMLTable) obj);
    }

    @Override
    public void visit(IDerivedColumn obj) {
        visit((DerivedColumn) obj);
    }

    @Override
    public void visit(IXMLSerialize obj) {
        visit((XMLSerialize) obj);
    }

    @Override
    public void visit(IXMLQuery obj) {
        visit((XMLQuery) obj);
    }

    @Override
    public void visit(IQueryString obj) {
        visit((QueryString) obj);
    }

    @Override
    public void visit(IXMLParse obj) {
        visit((XMLParse) obj);
    }

    @Override
    public void visit(IExpressionCriteria obj) {
        visit((ExpressionCriteria) obj);
    }

    @Override
    public void visit(IWithQueryCommand obj) {
        visit((WithQueryCommand) obj);
    }

    @Override
    public void visit(ITriggerAction obj) {
        visit((TriggerAction) obj);
    }

    @Override
    public void visit(IObjectTable obj) {
        visit((ObjectTable) obj);
    }

    @Override
    public void visit(IArrayTable obj) {
        visit((ArrayTable) obj);
    }

    @Override
    public void visit(IAlterView obj) {
        visit((AlterView) obj);
    }

    @Override
    public void visit(IAlterProcedure obj) {
        visit((AlterProcedure) obj);
    }

    @Override
    public void visit(IAlterTrigger obj) {
        visit((AlterTrigger) obj);
    }

    @Override
    public void visit(IWindowFunction obj) {
        visit((WindowFunction) obj);
    }

    @Override
    public void visit(IWindowSpecification obj) {
        visit((WindowSpecification) obj);
    }

    @Override
    public void visit(IExceptionExpression obj) {
        visit((ExceptionExpression) obj);
    }

    @Override
    public void visit(IReturnStatement obj) {
        visit((ReturnStatement) obj);
    }
}

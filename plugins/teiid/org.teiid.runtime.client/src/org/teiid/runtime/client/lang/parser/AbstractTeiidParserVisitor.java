/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.parser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.AggregateSymbol;
import org.teiid.runtime.client.lang.ast.AliasSymbol;
import org.teiid.runtime.client.lang.ast.Alter;
import org.teiid.runtime.client.lang.ast.ArrayTable;
import org.teiid.runtime.client.lang.ast.AssignmentStatement;
import org.teiid.runtime.client.lang.ast.BetweenCriteria;
import org.teiid.runtime.client.lang.ast.Block;
import org.teiid.runtime.client.lang.ast.BranchingStatement;
import org.teiid.runtime.client.lang.ast.CaseExpression;
import org.teiid.runtime.client.lang.ast.Command;
import org.teiid.runtime.client.lang.ast.CommandStatement;
import org.teiid.runtime.client.lang.ast.CompareCriteria;
import org.teiid.runtime.client.lang.ast.CompoundCriteria;
import org.teiid.runtime.client.lang.ast.Constant;
import org.teiid.runtime.client.lang.ast.CreateProcedureCommand;
import org.teiid.runtime.client.lang.ast.CreateUpdateProcedureCommand;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.CriteriaSelector;
import org.teiid.runtime.client.lang.ast.DeclareStatement;
import org.teiid.runtime.client.lang.ast.Delete;
import org.teiid.runtime.client.lang.ast.DerivedColumn;
import org.teiid.runtime.client.lang.ast.Drop;
import org.teiid.runtime.client.lang.ast.DynamicCommand;
import org.teiid.runtime.client.lang.ast.ElementSymbol;
import org.teiid.runtime.client.lang.ast.ExceptionExpression;
import org.teiid.runtime.client.lang.ast.ExistsCriteria;
import org.teiid.runtime.client.lang.ast.ExpressionCriteria;
import org.teiid.runtime.client.lang.ast.ExpressionSymbol;
import org.teiid.runtime.client.lang.ast.From;
import org.teiid.runtime.client.lang.ast.FromClause;
import org.teiid.runtime.client.lang.ast.Function;
import org.teiid.runtime.client.lang.ast.GroupBy;
import org.teiid.runtime.client.lang.ast.GroupSymbol;
import org.teiid.runtime.client.lang.ast.HasCriteria;
import org.teiid.runtime.client.lang.ast.IfStatement;
import org.teiid.runtime.client.lang.ast.Insert;
import org.teiid.runtime.client.lang.ast.Into;
import org.teiid.runtime.client.lang.ast.IsNullCriteria;
import org.teiid.runtime.client.lang.ast.JSONObject;
import org.teiid.runtime.client.lang.ast.JoinPredicate;
import org.teiid.runtime.client.lang.ast.JoinType;
import org.teiid.runtime.client.lang.ast.LanguageObject;
import org.teiid.runtime.client.lang.ast.Limit;
import org.teiid.runtime.client.lang.ast.LoopStatement;
import org.teiid.runtime.client.lang.ast.MatchCriteria;
import org.teiid.runtime.client.lang.ast.MultipleElementSymbol;
import org.teiid.runtime.client.lang.ast.NotCriteria;
import org.teiid.runtime.client.lang.ast.ObjectColumn;
import org.teiid.runtime.client.lang.ast.ObjectTable;
import org.teiid.runtime.client.lang.ast.Option;
import org.teiid.runtime.client.lang.ast.OrderBy;
import org.teiid.runtime.client.lang.ast.OrderByItem;
import org.teiid.runtime.client.lang.ast.ProjectedColumn;
import org.teiid.runtime.client.lang.ast.Query;
import org.teiid.runtime.client.lang.ast.QueryCommand;
import org.teiid.runtime.client.lang.ast.QueryString;
import org.teiid.runtime.client.lang.ast.RaiseErrorStatement;
import org.teiid.runtime.client.lang.ast.RaiseStatement;
import org.teiid.runtime.client.lang.ast.Reference;
import org.teiid.runtime.client.lang.ast.ReturnStatement;
import org.teiid.runtime.client.lang.ast.ScalarSubquery;
import org.teiid.runtime.client.lang.ast.SearchedCaseExpression;
import org.teiid.runtime.client.lang.ast.Select;
import org.teiid.runtime.client.lang.ast.SetClause;
import org.teiid.runtime.client.lang.ast.SetClauseList;
import org.teiid.runtime.client.lang.ast.SetCriteria;
import org.teiid.runtime.client.lang.ast.SetQuery;
import org.teiid.runtime.client.lang.ast.SimpleNode;
import org.teiid.runtime.client.lang.ast.Statement;
import org.teiid.runtime.client.lang.ast.StoredProcedure;
import org.teiid.runtime.client.lang.ast.SubqueryCompareCriteria;
import org.teiid.runtime.client.lang.ast.SubqueryFromClause;
import org.teiid.runtime.client.lang.ast.SubquerySetCriteria;
import org.teiid.runtime.client.lang.ast.TextColumn;
import org.teiid.runtime.client.lang.ast.TextLine;
import org.teiid.runtime.client.lang.ast.TextTable;
import org.teiid.runtime.client.lang.ast.TranslateCriteria;
import org.teiid.runtime.client.lang.ast.TriggerAction;
import org.teiid.runtime.client.lang.ast.UnaryFromClause;
import org.teiid.runtime.client.lang.ast.Update;
import org.teiid.runtime.client.lang.ast.WhileStatement;
import org.teiid.runtime.client.lang.ast.WindowFunction;
import org.teiid.runtime.client.lang.ast.WindowSpecification;
import org.teiid.runtime.client.lang.ast.WithQueryCommand;
import org.teiid.runtime.client.lang.ast.XMLAttributes;
import org.teiid.runtime.client.lang.ast.XMLColumn;
import org.teiid.runtime.client.lang.ast.XMLElement;
import org.teiid.runtime.client.lang.ast.XMLForest;
import org.teiid.runtime.client.lang.ast.XMLNamespaces;
import org.teiid.runtime.client.lang.ast.XMLParse;
import org.teiid.runtime.client.lang.ast.XMLQuery;
import org.teiid.runtime.client.lang.ast.XMLSerialize;
import org.teiid.runtime.client.lang.ast.XMLTable;
import org.teiid.runtime.client.sql.QueryParser;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public abstract class AbstractTeiidParserVisitor {

    protected final ITeiidServerVersion teiidVersion;

    /*
     * Required if nodes are to be created by the visitor
     */
    private final QueryParser parser;

    private static final Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();

    static {
        // cache all the methods on this visitor
        Method[] methods = AbstractTeiidParserVisitor.class.getMethods();
        for (Method method : methods) {
            if (!method.getName().equals("visit")) //$NON-NLS-1$
            continue;

            Class<?>[] params = method.getParameterTypes();
            if (params == null || params.length == 0) continue;

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
    public AbstractTeiidParserVisitor(ITeiidServerVersion teiidVersion) {
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

    protected void isApplicable(LanguageObject node) {
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

    public void visit(SimpleNode node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Command node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Alter<? extends Command> node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TriggerAction node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Drop node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(RaiseStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ExceptionExpression node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Statement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(BranchingStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ReturnStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WhileStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(LoopStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(IfStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DeclareStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CommandStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(CreateProcedureCommand node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DynamicCommand node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetClauseList node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetClause node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ProjectedColumn node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(StoredProcedure node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Insert node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Update node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Delete node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(QueryCommand node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WithQueryCommand node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetQuery node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Query node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Into node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Select node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExpressionSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(DerivedColumn node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(MultipleElementSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(From node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(FromClause node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(JoinPredicate node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(JoinType node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLSerialize node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ArrayTable node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextTable node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextColumn node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLQuery node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(ObjectTable node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ObjectColumn node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLTable node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLColumn node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubqueryFromClause node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(UnaryFromClause node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Criteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CompoundCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(NotCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CompareCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubqueryCompareCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(MatchCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(BetweenCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(IsNullCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SubquerySetCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SetCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExistsCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(GroupBy node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(OrderBy node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(OrderByItem node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Limit node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Option node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Reference node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(CaseExpression node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(SearchedCaseExpression node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Function node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLParse node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(QueryString node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLElement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLAttributes node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Since( "8.0.0" )
    public void visit(JSONObject node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLForest node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(XMLNamespaces node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AssignmentStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ScalarSubquery node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(GroupSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Constant node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ElementSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(Block node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(ExpressionCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AliasSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(AggregateSymbol node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WindowFunction node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(WindowSpecification node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    public void visit(TextLine node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(RaiseErrorStatement node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(CriteriaSelector node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(HasCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(TranslateCriteria node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }

    @Removed( "8.0.0" )
    public void visit(CreateUpdateProcedureCommand node, Object data) {
        isApplicable(node);
        throw new UnsupportedOperationException();
    }
}

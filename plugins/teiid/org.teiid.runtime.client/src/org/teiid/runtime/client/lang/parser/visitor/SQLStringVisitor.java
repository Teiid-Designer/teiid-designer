/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, wr`ite to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.runtime.client.lang.parser.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.runtime.client.function.SourceSystemFunctions;
import org.teiid.runtime.client.lang.SPParameter;
import org.teiid.runtime.client.lang.SQLConstants;
import org.teiid.runtime.client.lang.SQLConstants.NonReserved;
import org.teiid.runtime.client.lang.SQLConstants.Tokens;
import org.teiid.runtime.client.lang.SourceHint;
import org.teiid.runtime.client.lang.SourceHint.SpecificHint;
import org.teiid.runtime.client.lang.SubqueryHint;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.AggregateSymbol;
import org.teiid.runtime.client.lang.ast.AggregateSymbol.Type;
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
import org.teiid.runtime.client.lang.ast.Expression;
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
import org.teiid.runtime.client.lang.ast.Labeled;
import org.teiid.runtime.client.lang.ast.LanguageObject;
import org.teiid.runtime.client.lang.ast.Limit;
import org.teiid.runtime.client.lang.ast.LoopStatement;
import org.teiid.runtime.client.lang.ast.MatchCriteria;
import org.teiid.runtime.client.lang.ast.MultipleElementSymbol;
import org.teiid.runtime.client.lang.ast.NamespaceItem;
import org.teiid.runtime.client.lang.ast.NotCriteria;
import org.teiid.runtime.client.lang.ast.ObjectColumn;
import org.teiid.runtime.client.lang.ast.ObjectTable;
import org.teiid.runtime.client.lang.ast.Option;
import org.teiid.runtime.client.lang.ast.OrderBy;
import org.teiid.runtime.client.lang.ast.OrderByItem;
import org.teiid.runtime.client.lang.ast.PredicateCriteria;
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
import org.teiid.runtime.client.lang.ast.Statement;
import org.teiid.runtime.client.lang.ast.StoredProcedure;
import org.teiid.runtime.client.lang.ast.SubqueryCompareCriteria;
import org.teiid.runtime.client.lang.ast.SubqueryFromClause;
import org.teiid.runtime.client.lang.ast.SubquerySetCriteria;
import org.teiid.runtime.client.lang.ast.Symbol;
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
import org.teiid.runtime.client.lang.parser.AbstractTeiidParserVisitor;
import org.teiid.runtime.client.types.DataTypeManagerService;
import org.teiid.runtime.client.util.StringUtil;

/**
 * <p>
 * The SQLStringVisitor will visit a set of ast nodes and return the corresponding SQL string representation.
 * </p>
 */
public class SQLStringVisitor extends AbstractTeiidParserVisitor implements SQLConstants.Reserved, SQLConstants.Tokens {

    /**
     * Undefined
     */
    public static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private static final TeiidServerVersion TEIID_VERSION_8 = new TeiidServerVersion("8.0.0"); //$NON-NLS-1$

    private static final String BEGIN_HINT = "/*+"; //$NON-NLS-1$

    private static final String END_HINT = "*/"; //$NON-NLS-1$

    private static final char ID_ESCAPE_CHAR = '\"';

    protected StringBuilder parts = new StringBuilder();

    private boolean shortNameOnly = false;

    /**
     * @param teiidVersion
     */
    public SQLStringVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion.getMaximumVersion());
    }

    /**
     * Helper to quickly get the parser string for an object using the visitor.
     *
     * @param teiidVersion
     * @param obj Language object
     *
     * @return String SQL String for obj
     */
    public static final String getSQLString(ITeiidServerVersion teiidVersion, LanguageObject obj) {
        SQLStringVisitor visitor = new SQLStringVisitor(teiidVersion);
        return visitor.returnSQLString(obj);
    }

    /**
     * @param languageObject
     * @return sql representation of {@link LanguageObject}
     */
    public String returnSQLString(LanguageObject languageObject) {
        if (languageObject == null) {
            return UNDEFINED;
        }

        isApplicable(languageObject);
        languageObject.accept(this, null);
        return getSQLString();
    }

    /**
     * Retrieve completed string from the visitor.
     *
     * @return Complete SQL string for the visited nodes
     */
    public String getSQLString() {
        return this.parts.toString();
    }

    protected boolean isTeiid8OrGreater() {
        return teiidVersion.equals(TEIID_VERSION_8) || teiidVersion.isGreaterThan(TEIID_VERSION_8);
    }

    protected void visitNode(LanguageObject obj, Object data) {
        if (obj == null) {
            append(UNDEFINED);
            return;
        }
        isApplicable(obj);
        obj.accept(this, data);
    }

    protected void append(Object value) {
        this.parts.append(value);
    }

    protected void beginClause(int level) {
        append(SPACE);
    }

    private Constant newConstant(Object value) {
        Constant constant = createNode(ASTNodes.CONSTANT);
        constant.setValue(value);
        return constant;
    }

    // ############ Visitor methods for language objects ####################

    @Override
    public void visit(BetweenCriteria obj, Object data) {
        visitNode(obj.getExpression(), data);
        append(SPACE);

        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(BETWEEN);
        append(SPACE);
        visitNode(obj.getLowerExpression(), data);

        append(SPACE);
        append(AND);
        append(SPACE);
        visitNode(obj.getUpperExpression(), data);
    }

    @Override
    public void visit(CaseExpression obj, Object data) {
        append(CASE);
        append(SPACE);
        visitNode(obj.getExpression(), data);
        append(SPACE);

        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenExpression(i), data);
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i), data);
            append(SPACE);
        }

        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression(), data);
            append(SPACE);
        }
        append(END);
    }

    @Override
    public void visit(CompareCriteria obj, Object data) {
        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression, data);
        append(SPACE);
        append(obj.getOperator().toString());
        append(SPACE);
        Expression rightExpression = obj.getRightExpression();
        visitNode(rightExpression, data);
    }

    @Override
    public void visit(CompoundCriteria obj, Object data) {
        // Get operator string
        int operator = obj.getOperator();
        String operatorStr = ""; //$NON-NLS-1$
        if (operator == CompoundCriteria.AND) {
            operatorStr = AND;
        } else if (operator == CompoundCriteria.OR) {
            operatorStr = OR;
        }

        // Get criteria
        List<Criteria> subCriteria = obj.getCriteria();

        // Build parts
        if (subCriteria.size() == 1) {
            // Special case - should really never happen, but we are tolerant
            Criteria firstChild = subCriteria.get(0);
            visitNode(firstChild, data);
        } else {
            // Add first criteria
            Iterator<Criteria> iter = subCriteria.iterator();

            while (iter.hasNext()) {
                // Add criteria
                Criteria crit = iter.next();
                append(Tokens.LPAREN);
                visitNode(crit, data);
                append(Tokens.RPAREN);

                if (iter.hasNext()) {
                    // Add connector
                    append(SPACE);
                    append(operatorStr);
                    append(SPACE);
                }
            }
        }
    }

    @Override
    public void visit(Delete obj, Object data) {
        // add delete clause
        append(DELETE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        // add from clause
        append(FROM);
        append(SPACE);
        visitNode(obj.getGroup(), data);

        // add where clause
        if (obj.getCriteria() != null) {
            beginClause(0);
            visitCriteria(WHERE, obj.getCriteria(), data);
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption(), data);
        }
    }

    @Override
    public void visit(From obj, Object data) {
        append(FROM);
        beginClause(1);
        registerNodes(obj.getClauses(), 0, data);
    }

    @Override
    public void visit(GroupBy obj, Object data) {
        append(GROUP);
        append(SPACE);
        append(BY);
        append(SPACE);
        registerNodes(obj.getSymbols(), 0, data);
    }

    @Override
    public void visit(Insert obj, Object data) {
        if (isTeiid8OrGreater() && obj.isMerge()) {
            append(MERGE);
        } else {
            append(INSERT);
        }
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup(), data);

        if (!obj.getVariables().isEmpty()) {
            beginClause(2);

            // Columns clause
            List<ElementSymbol> vars = obj.getVariables();
            if (vars != null) {
                append("("); //$NON-NLS-1$
                this.shortNameOnly = true;
                registerNodes(vars, 0, data);
                this.shortNameOnly = false;
                append(")"); //$NON-NLS-1$
            }
        }
        beginClause(1);
        if (obj.getQueryExpression() != null) {
            visitNode(obj.getQueryExpression(), data);
            //         } else if (obj.getTupleSource() != null) {
            //             append(VALUES);
            //             append(" (...)"); //$NON-NLS-1$
        } else if (obj.getValues() != null) {
            append(VALUES);
            beginClause(2);
            append("("); //$NON-NLS-1$
            registerNodes(obj.getValues(), 0, data);
            append(")"); //$NON-NLS-1$
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption(), data);
        }
    }

    @Override
    public void visit(Drop obj, Object data) {
        append(DROP);
        append(SPACE);
        append(TABLE);
        append(SPACE);
        visitNode(obj.getTable(), data);
    }

    @Override
    public void visit(IsNullCriteria obj, Object data) {
        Expression expr = obj.getExpression();
        if (isTeiid8OrGreater())
            appendNested(expr, data);
        else
            visitNode(expr, data);

        append(SPACE);
        append(IS);
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(NULL);
    }

    @Override
    public void visit(JoinPredicate obj, Object data) {
        addHintComment(obj, data);

        if (obj.hasHint()) {
            append("(");//$NON-NLS-1$
        }

        // left clause
        FromClause leftClause = obj.getLeftClause();
        if (leftClause instanceof JoinPredicate && !((JoinPredicate)leftClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(leftClause, data);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(leftClause, data);
        }

        // join type
        append(SPACE);
        visitNode(obj.getJoinType(), data);
        append(SPACE);

        // right clause
        FromClause rightClause = obj.getRightClause();
        if (rightClause instanceof JoinPredicate && !((JoinPredicate)rightClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(rightClause, data);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(rightClause, data);
        }

        // join criteria
        List joinCriteria = obj.getJoinCriteria();
        if (joinCriteria != null && joinCriteria.size() > 0) {
            append(SPACE);
            append(ON);
            append(SPACE);
            Iterator critIter = joinCriteria.iterator();
            while (critIter.hasNext()) {
                Criteria crit = (Criteria)critIter.next();
                if (crit instanceof PredicateCriteria || crit instanceof NotCriteria) {
                    visitNode(crit, data);
                } else {
                    append("("); //$NON-NLS-1$
                    visitNode(crit, data);
                    append(")"); //$NON-NLS-1$
                }

                if (critIter.hasNext()) {
                    append(SPACE);
                    append(AND);
                    append(SPACE);
                }
            }
        }

        if (obj.hasHint()) {
            append(")"); //$NON-NLS-1$
        }
    }

    private void addHintComment(FromClause obj, Object data) {
        if (obj.hasHint()) {
            append(BEGIN_HINT);
            append(SPACE);
            if (obj.isOptional()) {
                append(Option.OPTIONAL);
                append(SPACE);
            }
            if (obj.isMakeDep()) {
                append(Option.MAKEDEP);
                append(SPACE);
            }
            if (obj.isMakeNotDep()) {
                append(Option.MAKENOTDEP);
                append(SPACE);
            }
            if (obj.isMakeInd()) {
                append(FromClause.MAKEIND);
                append(SPACE);
            }
            if (obj.isNoUnnest()) {
                append(SubqueryHint.NOUNNEST);
                append(SPACE);
            }

            if (isTeiid8OrGreater() && obj.isPreserve()) {
                append(FromClause.PRESERVE);
                append(SPACE);
            }
            append(END_HINT);
            append(SPACE);
        }
    }

    @Override
    public void visit(JoinType obj, Object data) {
        String[] output = null;
        switch (obj.getKind()) {
            case JOIN_ANTI_SEMI:
                output = new String[] {"ANTI SEMI", SPACE, JOIN}; //$NON-NLS-1$
                break;
            case JOIN_CROSS:
                output = new String[] {CROSS, SPACE, JOIN};
                break;
            case JOIN_FULL_OUTER:
                output = new String[] {FULL, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_INNER:
                output = new String[] {INNER, SPACE, JOIN};
                break;
            case JOIN_LEFT_OUTER:
                output = new String[] {LEFT, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_RIGHT_OUTER:
                output = new String[] {RIGHT, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_SEMI:
                output = new String[] {"SEMI", SPACE, JOIN}; //$NON-NLS-1$
                break;
            case JOIN_UNION:
                output = new String[] {UNION, SPACE, JOIN};
                break;
            default:
                throw new AssertionError();
        }

        for (String part : output) {
            append(part);
        }
    }

    @Override
    public void visit(MatchCriteria obj, Object data) {
        visitNode(obj.getLeftExpression(), data);

        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        switch (obj.getMode()) {
            case SIMILAR:
                append(SIMILAR);
                append(SPACE);
                append(TO);
                break;
            case LIKE:
                append(LIKE);
                break;
            case REGEX:
                append(LIKE_REGEX);
                break;
        }
        append(SPACE);

        visitNode(obj.getRightExpression(), data);

        if (obj.getEscapeChar() != MatchCriteria.NULL_ESCAPE_CHAR) {
            append(SPACE);
            append(ESCAPE);
            if (isTeiid8OrGreater()) {
                append(SPACE);
                outputLiteral(String.class, false, obj.getEscapeChar());
            } else {
                append(" '"); //$NON-NLS-1$
                append(String.valueOf(obj.getEscapeChar()));
                append("'"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(NotCriteria obj, Object data) {
        append(NOT);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCriteria(), data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(Option obj, Object data) {
        append(OPTION);

        Collection<String> groups = obj.getDependentGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(MAKEDEP);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        }

        groups = obj.getNotDependentGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(MAKENOTDEP);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        }

        groups = obj.getNoCacheGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(NOCACHE);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        } else if (obj.isNoCache()) {
            append(" "); //$NON-NLS-1$
            append(NOCACHE);
        }

    }

    @Override
    public void visit(OrderBy obj, Object data) {
        append(ORDER);
        append(SPACE);
        append(BY);
        append(SPACE);
        registerNodes(obj.getOrderByItems(), 0, data);
    }

    @Override
    public void visit(OrderByItem obj, Object data) {
        Expression ses = obj.getSymbol();
        if (ses instanceof AliasSymbol) {
            AliasSymbol as = (AliasSymbol)ses;
            outputDisplayName(as.getOutputName());
        } else {
            visitNode(ses, data);
        }
        if (!obj.isAscending()) {
            append(SPACE);
            append(DESC);
        } // Don't print default "ASC"
        if (obj.getNullOrdering() != null) {
            append(SPACE);
            append(NonReserved.NULLS);
            append(SPACE);
            append(obj.getNullOrdering().name());
        }
    }

    @Override
    public void visit(DynamicCommand obj, Object data) {
        append(EXECUTE);
        append(SPACE);
        append(IMMEDIATE);
        append(SPACE);
        visitNode(obj.getSql(), data);

        if (obj.isAsClauseSet()) {
            beginClause(1);
            append(AS);
            append(SPACE);
            for (int i = 0; i < obj.getAsColumns().size(); i++) {
                ElementSymbol symbol = (ElementSymbol)obj.getAsColumns().get(i);
                outputShortName(symbol, data);
                append(SPACE);
                append(DataTypeManagerService.getInstance().getDataTypeName(symbol.getType()));
                if (i < obj.getAsColumns().size() - 1) {
                    append(", "); //$NON-NLS-1$
                }
            }
        }

        if (obj.getIntoGroup() != null) {
            beginClause(1);
            append(INTO);
            append(SPACE);
            visitNode(obj.getIntoGroup(), data);
        }

        if (obj.getUsing() != null && !obj.getUsing().isEmpty()) {
            beginClause(1);
            append(USING);
            append(SPACE);
            visitNode(obj.getUsing(), data);
        }

        if (obj.getUpdatingModelCount() > 0) {
            beginClause(1);
            append(UPDATE);
            append(SPACE);
            if (obj.getUpdatingModelCount() > 1) {
                append("*"); //$NON-NLS-1$
            } else {
                append("1"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(SetClauseList obj, Object data) {
        for (Iterator<SetClause> iterator = obj.getClauses().iterator(); iterator.hasNext();) {
            SetClause clause = iterator.next();
            visitNode(clause, data);
            if (iterator.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(SetClause obj, Object data) {
        ElementSymbol symbol = obj.getSymbol();
        outputShortName(symbol, data);
        append(" = "); //$NON-NLS-1$
        visitNode(obj.getValue(), data);
    }

    @Override
    public void visit(WithQueryCommand obj, Object data) {
        visitNode(obj.getGroupSymbol(), data);
        append(SPACE);
        if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
            append(Tokens.LPAREN);
            shortNameOnly = true;
            registerNodes(obj.getColumns(), 0, data);
            shortNameOnly = false;
            append(Tokens.RPAREN);
            append(SPACE);
        }
        append(AS);
        append(SPACE);
        append(Tokens.LPAREN);
        visitNode(obj.getQueryExpression(), data);
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(Query obj, Object data) {
        addWithClause(obj, data);
        append(SELECT);

        SourceHint sh = obj.getSourceHint();
        addSourceHint(sh);
        if (obj.getSelect() != null) {
            visitNode(obj.getSelect(), data);
        }

        if (obj.getInto() != null) {
            beginClause(1);
            visitNode(obj.getInto(), data);
        }

        if (obj.getFrom() != null) {
            beginClause(1);
            visitNode(obj.getFrom(), data);
        }

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria(), data);
        }

        // Group by clause
        if (obj.getGroupBy() != null) {
            beginClause(1);
            visitNode(obj.getGroupBy(), data);
        }

        // Having clause
        if (obj.getHaving() != null) {
            beginClause(1);
            visitCriteria(HAVING, obj.getHaving(), data);
        }

        // Order by clause
        if (obj.getOrderBy() != null) {
            beginClause(1);
            visitNode(obj.getOrderBy(), data);
        }

        if (obj.getLimit() != null) {
            beginClause(1);
            visitNode(obj.getLimit(), data);
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption(), data);
        }
    }

    private void addSourceHint(SourceHint sh) {
        if (sh != null) {
            append(SPACE);
            append(BEGIN_HINT);
            append("sh"); //$NON-NLS-1$

            if (isTeiid8OrGreater() && sh.isUseAliases()) {
                append(SPACE);
                append("KEEP ALIASES"); //$NON-NLS-1$
            }

            if (sh.getGeneralHint() != null) {
                appendSourceHintValue(sh.getGeneralHint());
            }
            if (sh.getSpecificHints() != null) {
                for (Map.Entry<String, SpecificHint> entry : sh.getSpecificHints().entrySet()) {
                    append(entry.getKey());
                    if (isTeiid8OrGreater() && entry.getValue().isUseAliases()) {
                        append(SPACE);
                        append("KEEP ALIASES"); //$NON-NLS-1$
                    }

                    appendSourceHintValue(entry.getValue().getHint());
                }
            }
            append(END_HINT);
        }
    }

    private void addWithClause(QueryCommand obj, Object data) {
        if (obj.getWith() != null) {
            append(WITH);
            append(SPACE);
            registerNodes(obj.getWith(), 0, data);
            beginClause(0);
        }
    }

    protected void visitCriteria(String keyWord, Criteria crit, Object data) {
        append(keyWord);
        append(SPACE);
        visitNode(crit, data);
    }

    @Override
    public void visit(SearchedCaseExpression obj, Object data) {
        append(CASE);
        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(SPACE);
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenCriteria(i), data);
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i), data);
        }
        append(SPACE);
        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression(), data);
            append(SPACE);
        }
        append(END);
    }

    @Override
    public void visit(Select obj, Object data) {
        if (obj.isDistinct()) {
            append(SPACE);
            append(DISTINCT);
        }
        beginClause(2);

        Iterator<Expression> iter = obj.getSymbols().iterator();
        while (iter.hasNext()) {
            Expression symbol = iter.next();
            visitNode(symbol, data);
            if (iter.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
    }

    private void appendSourceHintValue(String sh) {
        append(Tokens.COLON);
        append('\'');
        append(escapeStringValue(sh, "'")); //$NON-NLS-1$
        append('\'');
        append(SPACE);
    }

    @Override
    public void visit(SetCriteria obj, Object data) {
        // variable

        if (isTeiid8OrGreater())
            appendNested(obj.getExpression(), data);
        else
            visitNode(obj.getExpression(), data);

        // operator and beginning of list
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(IN);
        append(" ("); //$NON-NLS-1$

        // value list
        Collection vals = obj.getValues();
        int size = vals.size();
        if (size == 1) {
            Iterator iter = vals.iterator();
            Expression expr = (Expression)iter.next();
            visitNode(expr, data);
        } else if (size > 1) {
            Iterator iter = vals.iterator();
            Expression expr = (Expression)iter.next();
            visitNode(expr, data);
            while (iter.hasNext()) {
                expr = (Expression)iter.next();
                append(", "); //$NON-NLS-1$
                visitNode(expr, data);
            }
        }
        append(")"); //$NON-NLS-1$
    }

    /**
     * Condition operators have lower precedence than LIKE/SIMILAR/IS
     * @param ex
     */
    @Since( "8.0.0" )
    private void appendNested(Expression ex, Object data) {
        boolean useParens = ex instanceof Criteria;
        if (useParens) {
            append(Tokens.LPAREN);
        }
        visitNode(ex, data);
        if (useParens) {
            append(Tokens.RPAREN);
        }
    }

    @Override
    public void visit(SetQuery obj, Object data) {
        addWithClause(obj, data);
        QueryCommand query = obj.getLeftQuery();
        appendSetQuery(obj, query, false, data);

        beginClause(0);
        append(obj.getOperation());

        if (obj.isAll()) {
            append(SPACE);
            append(ALL);
        }
        beginClause(0);
        query = obj.getRightQuery();
        appendSetQuery(obj, query, true, data);

        if (obj.getOrderBy() != null) {
            beginClause(0);
            visitNode(obj.getOrderBy(), data);
        }

        if (obj.getLimit() != null) {
            beginClause(0);
            visitNode(obj.getLimit(), data);
        }

        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption(), data);
        }
    }

    protected void appendSetQuery(SetQuery parent, QueryCommand obj, boolean right, Object data) {
        if (obj.getLimit() != null
            || obj.getOrderBy() != null
            || (right && ((obj instanceof SetQuery && ((parent.isAll() && !((SetQuery)obj).isAll()) || parent.getOperation() != ((SetQuery)obj).getOperation()))))) {
            append(Tokens.LPAREN);
            visitNode(obj, data);
            append(Tokens.RPAREN);
        } else {
            visitNode(obj, data);
        }
    }

    @Override
    public void visit(StoredProcedure obj, Object data) {
        if (obj.isCalledWithReturn()) {
            for (SPParameter param : obj.getParameters()) {
                if (param.getParameterType() == SPParameter.RETURN_VALUE) {
                    if (param.getExpression() == null) {
                        append("?"); //$NON-NLS-1$
                    } else {
                        visitNode(param.getExpression(), data);
                    }
                }
            }
            append(SPACE);
            append(Tokens.EQ);
            append(SPACE);
        }
        // exec clause
        append(EXEC);
        append(SPACE);
        append(obj.getProcedureName());
        append("("); //$NON-NLS-1$
        boolean first = true;
        for (SPParameter param : obj.getParameters()) {
            if (param.isUsingDefault() || param.getParameterType() == SPParameter.RETURN_VALUE
                || param.getParameterType() == SPParameter.RESULT_SET || param.getExpression() == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                append(", "); //$NON-NLS-1$
            }
            if (obj.isDisplayNamedParameters()) {
                append(escapeSinglePart(Symbol.getShortName(param.getParameterSymbol().getOutputName())));
                append(" => "); //$NON-NLS-1$
            }

            boolean addParens = !obj.isDisplayNamedParameters() && param.getExpression() instanceof CompareCriteria;
            if (addParens) {
                append(Tokens.LPAREN);
            }
            visitNode(param.getExpression(), data);
            if (addParens) {
                append(Tokens.RPAREN);
            }
        }
        append(")"); //$NON-NLS-1$

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption(), data);
        }
    }

    @Override
    public void visit(SubqueryFromClause obj, Object data) {
        addHintComment(obj, data);
        if (obj.isTable()) {
            append(TABLE);
        }
        append("(");//$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(")");//$NON-NLS-1$
        append(" AS ");//$NON-NLS-1$

        GroupSymbol groupSymbol = obj.getGroupSymbol();
        if (isTeiid8OrGreater())
            append(escapeSinglePart(groupSymbol.getOutputName()));
        else
            append(groupSymbol.getOutputName());
    }

    @Override
    public void visit(SubquerySetCriteria obj, Object data) {
        // variable
        visitNode(obj.getExpression(), data);

        // operator and beginning of list
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(IN);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(UnaryFromClause obj, Object data) {
        addHintComment(obj, data);
        visitNode(obj.getGroupSymbol(), data);
    }

    @Override
    public void visit(Update obj, Object data) {
        // Update clause
        append(UPDATE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        visitNode(obj.getGroup(), data);
        beginClause(1);
        // Set clause
        append(SET);
        beginClause(2);
        visitNode(obj.getChangeList(), data);

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria(), data);
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption(), data);
        }
    }

    @Override
    public void visit(Into obj, Object data) {
        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup(), data);
    }

    // ############ Visitor methods for symbol objects ####################

    @Override
    public void visit(AggregateSymbol obj, Object data) {
        if (isTeiid8OrGreater())
            append(obj.getName());
        else
            append(obj.getAggregateFunction().name());
        append("("); //$NON-NLS-1$

        if (obj.isDistinct()) {
            append(DISTINCT);
            append(" "); //$NON-NLS-1$
        } else if (isTeiid8OrGreater() && obj.getAggregateFunction() == Type.USER_DEFINED) {
            append(ALL);
            append(" "); //$NON-NLS-1$
        }

        if ((!isTeiid8OrGreater() && obj.getExpression() == null) ||
             (isTeiid8OrGreater() && (obj.getArgs() == null ||  obj.getArgs().length == 0))) {
            if (obj.getAggregateFunction() == Type.COUNT) {
                append(Tokens.ALL_COLS);
            }
        } else if (isTeiid8OrGreater()) {
            registerNodes(obj.getArgs(), 0, data);
        } else {
            visitNode(obj.getExpression(), data);
        }

        if (obj.getOrderBy() != null) {
            append(SPACE);
            visitNode(obj.getOrderBy(), data);
        }
        append(")"); //$NON-NLS-1$

        if (obj.getCondition() != null) {
            append(SPACE);
            append(FILTER);
            append(Tokens.LPAREN);
            append(WHERE);
            append(SPACE);
            append(obj.getCondition());
            append(Tokens.RPAREN);
        }
    }

    @Override
    public void visit(AliasSymbol obj, Object data) {
        visitNode(obj.getSymbol(), data);
        append(SPACE);
        append(AS);
        append(SPACE);
        append(escapeSinglePart(obj.getOutputName()));
    }

    @Override
    public void visit(MultipleElementSymbol obj, Object data) {
        if (obj.getGroup() == null) {
            append(Tokens.ALL_COLS);
        } else {
            visitNode(obj.getGroup(), data);
            append(Tokens.DOT);
            append(Tokens.ALL_COLS);
        }
    }

    private void visit7(Constant obj, Object data) {
        Class<?> type = obj.getType();
        String[] constantParts = null;
        if (obj.isMultiValued()) {
            constantParts = new String[] {"?"}; //$NON-NLS-1$
        } else if (obj.getValue() == null) {
            if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {UNKNOWN};
            } else {
                constantParts = new String[] {"null"}; //$NON-NLS-1$
            }
        } else {
            if (Number.class.isAssignableFrom(type)) {
                constantParts = new String[] {obj.getValue().toString()};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {obj.getValue().equals(Boolean.TRUE) ? TRUE : FALSE};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass())) {
                constantParts = new String[] {"{ts'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIME.getTypeClass())) {
                constantParts = new String[] {"{t'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.DATE.getTypeClass())) {
                constantParts = new String[] {"{d'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (constantParts == null) {
                String strValue = obj.getValue().toString();
                strValue = escapeStringValue(strValue, "'"); //$NON-NLS-1$
                constantParts = new String[] {"'", strValue, "'"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        for (String string : constantParts) {
            append(string);
        }
    }

    private void outputLiteral(Class<?> type, boolean multiValued, Object value) throws AssertionError {
        String[] constantParts = null;
        if (multiValued) {
            constantParts = new String[] {"?"}; //$NON-NLS-1$
        } else if (value == null) {
            if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {UNKNOWN};
            } else {
                constantParts = new String[] {"null"}; //$NON-NLS-1$
            }
        } else {
            if (Number.class.isAssignableFrom(type)) {
                constantParts = new String[] {value.toString()};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {value.equals(Boolean.TRUE) ? TRUE : FALSE};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass())) {
                constantParts = new String[] {"{ts'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIME.getTypeClass())) {
                constantParts = new String[] {"{t'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.DATE.getTypeClass())) {
                constantParts = new String[] {"{d'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass())) {
                constantParts = new String[] {"X'", value.toString(), "'"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (constantParts == null) {
                if (isTeiid8OrGreater() && DataTypeManagerService.DefaultDataTypes.isLOB(type)) {
                    constantParts = new String[] {"?"}; //$NON-NLS-1$
                } else {
                    String strValue = value.toString();
                    strValue = escapeStringValue(strValue, "'"); //$NON-NLS-1$
                    constantParts = new String[] {"'", strValue, "'"}; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        for (String string : constantParts) {
            append(string);
        }
    }

    private void visit8(Constant obj, Object data) {
        Class<?> type = obj.getType();
        boolean multiValued = obj.isMultiValued();
        Object value = obj.getValue();
        outputLiteral(type, multiValued, value);
    }

    @Override
    public void visit(Constant obj, Object data) {
        if (isTeiid8OrGreater())
            visit8(obj, data);
        else
            visit7(obj, data);
    }

    /**
     * Take a string literal and escape it as necessary. By default, this converts ' to ''.
     * 
     * @param str String literal value (unquoted), never null
     * @return Escaped string literal value
     */
    static String escapeStringValue(String str, String tick) {
        return StringUtil.replaceAll(str, tick, tick + tick);
    }

    @Override
    public void visit(ElementSymbol obj, Object data) {
        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.SHORT_OUTPUT_NAME) || shortNameOnly) {
            outputShortName(obj, data);
            return;
        }
        String name = obj.getOutputName();
        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.FULLY_QUALIFIED)) {
            name = obj.getName();
        }
        outputDisplayName(name);
    }

    private void outputShortName(ElementSymbol obj, Object data) {
        outputDisplayName(Symbol.getShortName(obj.getOutputName()));
    }

    private void outputDisplayName(String name) {
        String[] pathParts = name.split("\\."); //$NON-NLS-1$
        for (int i = 0; i < pathParts.length; i++) {
            if (i > 0) {
                append(Symbol.SEPARATOR);
            }
            append(escapeSinglePart(pathParts[i]));
        }
    }

    @Override
    public void visit(ExpressionSymbol obj, Object data) {
        visitNode(obj.getExpression(), data);
    }

    @Override
    public void visit(Function obj, Object data) {
        String name = obj.getName();
        Expression[] args = obj.getArgs();
        if (obj.isImplicit()) {
            // Hide this function, which is implicit
            visitNode(args[0], data);

        } else if (name.equalsIgnoreCase(CONVERT) || name.equalsIgnoreCase(CAST)) {
            append(name);
            append("("); //$NON-NLS-1$

            if (args != null && args.length > 0) {
                visitNode(args[0], data);

                if (name.equalsIgnoreCase(CONVERT)) {
                    append(", "); //$NON-NLS-1$
                } else {
                    append(" "); //$NON-NLS-1$
                    append(AS);
                    append(" "); //$NON-NLS-1$
                }

                if (args.length < 2 || args[1] == null || !(args[1] instanceof Constant)) {
                    append(UNDEFINED);
                } else {
                    append(((Constant)args[1]).getValue());
                }
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equals("+") || name.equals("-") || name.equals("*") || name.equals("/") || name.equals("||")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            append("("); //$NON-NLS-1$

            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    visitNode(args[i], data);
                    if (i < (args.length - 1)) {
                        append(SPACE);
                        append(name);
                        append(SPACE);
                    }
                }
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equalsIgnoreCase(NonReserved.TIMESTAMPADD) || name.equalsIgnoreCase(NonReserved.TIMESTAMPDIFF)) {
            append(name);
            append("("); //$NON-NLS-1$

            if (args != null && args.length > 0) {
                append(((Constant)args[0]).getValue());
                registerNodes(args, 1, data);
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equalsIgnoreCase(SourceSystemFunctions.XMLPI)) {
            append(name);
            append("(NAME "); //$NON-NLS-1$
            outputDisplayName((String)((Constant)args[0]).getValue());
            registerNodes(args, 1, data);
            append(")"); //$NON-NLS-1$
        } else if (name.equalsIgnoreCase(SourceSystemFunctions.TRIM)) {
            append(name);
            append(SQLConstants.Tokens.LPAREN);
            String value = (String)((Constant)args[0]).getValue();
            if (!value.equalsIgnoreCase(BOTH)) {
                append(((Constant)args[0]).getValue());
                append(" "); //$NON-NLS-1$
            }
            append(args[1]);
            append(" "); //$NON-NLS-1$
            append(FROM);
            append(" "); //$NON-NLS-1$
            append(args[2]);
            append(")"); //$NON-NLS-1$
        } else {
            append(name);
            append("("); //$NON-NLS-1$
            registerNodes(args, 0, data);
            append(")"); //$NON-NLS-1$
        }
    }

    private void registerNodes(LanguageObject[] objects, int begin, Object data) {
        registerNodes(Arrays.asList(objects), begin, data);
    }

    private void registerNodes(List<? extends LanguageObject> objects, int begin, Object data) {
        for (int i = begin; i < objects.size(); i++) {
            if (i > 0) {
                append(", "); //$NON-NLS-1$
            }
            visitNode(objects.get(i), data);
        }
    }

    @Override
    public void visit(GroupSymbol obj, Object data) {
        String alias = null;
        String fullGroup = obj.getOutputName();
        if (obj.getOutputDefinition() != null) {
            alias = obj.getOutputName();
            fullGroup = obj.getOutputDefinition();
        }

        outputDisplayName(fullGroup);

        if (alias != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            append(escapeSinglePart(alias));
        }
    }

    @Override
    public void visit(Reference obj, Object data) {
        if (!obj.isPositional() && obj.getExpression() != null) {
            visitNode(obj.getExpression(), data);
        } else {
            append("?"); //$NON-NLS-1$
        }
    }

    // ############ Visitor methods for storedprocedure language objects ####################

    private void visit7(Block obj, Object data) {
        addLabel(obj);
        List<Statement> statements = obj.getStatements();
        // Add first clause
        append(BEGIN);
        if (obj.isAtomic()) {
            append(SPACE);
            append(ATOMIC);
        }
        append("\n"); //$NON-NLS-1$
        Iterator<Statement> stmtIter = statements.iterator();
        while (stmtIter.hasNext()) {
            // Add each statement
            addTabs(1);
            visitNode(stmtIter.next(), data);
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
        append(END);
    }

    private void addStatements(List<Statement> statements, Object data) {
        Iterator<Statement> stmtIter = statements.iterator();
        while (stmtIter.hasNext()) {
            // Add each statement
            addTabs(1);
            visitNode(stmtIter.next(), data);
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
    }

    private void visit8(Block obj, Object data) {
        addLabel(obj);
        List<Statement> statements = obj.getStatements();
        // Add first clause
        append(BEGIN);
        if (obj.isAtomic()) {
            append(SPACE);
            append(ATOMIC);
        }
        append("\n"); //$NON-NLS-1$
        addStatements(statements, data);
        if (obj.getExceptionGroup() != null) {
            append(NonReserved.EXCEPTION);
            append(SPACE);
            outputDisplayName(obj.getExceptionGroup());
            append("\n"); //$NON-NLS-1$
            if (obj.getExceptionStatements() != null) {
                addStatements(obj.getExceptionStatements(), data);
            }
        }
        append(END);
    }

    @Override
    public void visit(Block block, Object data) {
        if (isTeiid8OrGreater())
            visit8(block, data);
        else
            visit7(block, data);
    }

    private void addLabel(Labeled obj) {
        if (obj.getLabel() != null) {
            outputDisplayName(obj.getLabel());
            append(SPACE);
            append(Tokens.COLON);
            append(SPACE);
        }
    }

    /**
    * @param level  
    */
    protected void addTabs(int level) {
    }

    @Override
    public void visit(CommandStatement obj, Object data) {
        visitNode(obj.getCommand(), data);
        if (isTeiid8OrGreater() && !obj.isReturnable()) {
            append(SPACE);
            append(WITHOUT);
            append(SPACE);
            append(RETURN);
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    @Removed( "8.0.0" )
    public void visit(CreateUpdateProcedureCommand obj, Object data) {
        append(CREATE);
        append(SPACE);
        if (!obj.isUpdateProcedure()) {
            append(VIRTUAL);
            append(SPACE);
        }
        append(PROCEDURE);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock(), data);
    }

    @Override
    public void visit(CreateProcedureCommand obj, Object data) {
        append(CREATE);
        append(SPACE);
        append(VIRTUAL);
        append(SPACE);
        append(PROCEDURE);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock(), data);
    }

    @Override
    public void visit(DeclareStatement obj, Object data) {
        append(DECLARE);
        append(SPACE);
        append(obj.getVariableType());
        append(SPACE);
        createAssignment(obj, data);
    }

    /**
     * @param obj
     * @param parts
     */
    private void createAssignment(AssignmentStatement obj, Object data) {
        visitNode(obj.getVariable(), data);
        if (obj.getExpression() != null) {
            append(" = "); //$NON-NLS-1$
            visitNode(obj.getExpression(), data);
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(IfStatement obj, Object data) {
        append(IF);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition(), data);
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getIfBlock(), data);
        if (obj.hasElseBlock()) {
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(ELSE);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            visitNode(obj.getElseBlock(), data);
        }
    }

    @Override
    public void visit(AssignmentStatement obj, Object data) {
        createAssignment(obj, data);
    }

    @Override
    public void visit(RaiseStatement obj, Object data) {
        append(NonReserved.RAISE);
        append(SPACE);
        if (obj.isWarning()) {
            append(SQLWARNING);
            append(SPACE);
        }
        visitNode(obj.getExpression(), data);
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(HasCriteria obj, Object data) {
        append(HAS);
        append(SPACE);
        visitNode(obj.getSelector(), data);
    }

    @Override
    public void visit(TranslateCriteria obj, Object data) {
        append(TRANSLATE);
        append(SPACE);
        visitNode(obj.getSelector(), data);

        if (obj.hasTranslations()) {
            append(SPACE);
            append(WITH);
            append(SPACE);
            append("("); //$NON-NLS-1$
            Iterator critIter = obj.getTranslations().iterator();

            while (critIter.hasNext()) {
                visitNode((Criteria)critIter.next(), data);
                if (critIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
                if (!critIter.hasNext()) {
                    append(")"); //$NON-NLS-1$
                }
            }
        }
    }

    @Override
    public void visit(CriteriaSelector obj, Object data) {
        switch (obj.getSelectorType()) {
            case EQ:
                append("= "); //$NON-NLS-1$
                break;
            case GE:
                append(">= "); //$NON-NLS-1$
                break;
            case GT:
                append("> "); //$NON-NLS-1$
                break;
            case LE:
                append("<= "); //$NON-NLS-1$
                break;
            case LT:
                append("< "); //$NON-NLS-1$
                break;
            case NE:
                append("<> "); //$NON-NLS-1$
                break;
            case IN:
                append(IN);
                append(SPACE);
                break;
            case IS_NULL:
                append(IS);
                append(SPACE);
                append(NULL);
                append(SPACE);
                break;
            case LIKE:
                append(LIKE);
                append(SPACE);
                break;
            case BETWEEN:
                append(BETWEEN);
                append(SPACE);
                break;
            case NO_TYPE:
            default:
                // Append nothing
                break;
        }

        append(CRITERIA);
        if (obj.hasElements()) {
            append(SPACE);
            append(ON);
            append(SPACE);
            append("("); //$NON-NLS-1$

            Iterator elmtIter = obj.getElements().iterator();
            while (elmtIter.hasNext()) {
                visitNode((ElementSymbol)elmtIter.next(), data);
                if (elmtIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
            }
            append(")"); //$NON-NLS-1$
        }
    }

    @Override
    public void visit(RaiseErrorStatement obj, Object data) {
        append(ERROR);
        append(SPACE);
        visitNode(obj.getExpression(), data);
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(ExceptionExpression exceptionExpression, Object data) {
        append(SQLEXCEPTION);
        append(SPACE);
        visitNode(exceptionExpression.getMessage(), data);
        if (exceptionExpression.getSqlState() != null) {
            append(SPACE);
            append(SQLSTATE);
            append(SPACE);
            append(exceptionExpression.getSqlState());
            if (exceptionExpression.getErrorCode() != null) {
                append(Tokens.COMMA);
                append(SPACE);
                append(exceptionExpression.getErrorCode());
            }
        }
        if (exceptionExpression.getParent() != null) {
            append(SPACE);
            append(NonReserved.CHAIN);
            append(SPACE);
            append(exceptionExpression.getParent());
        }
    }

    @Override
    public void visit(ReturnStatement obj, Object data) {
        append(RETURN);
        if (obj.getExpression() != null) {
            append(SPACE);
            visitNode(obj.getExpression(), data);
        }
        append(Tokens.SEMICOLON);
    }

    @Override
    public void visit(BranchingStatement obj, Object data) {
        switch (obj.getMode()) {
            case CONTINUE:
                append(CONTINUE);
                break;
            case BREAK:
                append(BREAK);
                break;
            case LEAVE:
                append(LEAVE);
                break;
        }
        if (obj.getLabel() != null) {
            append(SPACE);
            outputDisplayName(obj.getLabel());
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(LoopStatement obj, Object data) {
        addLabel(obj);
        append(LOOP);
        append(" "); //$NON-NLS-1$
        append(ON);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(") "); //$NON-NLS-1$
        append(AS);
        append(" "); //$NON-NLS-1$
        if (isTeiid8OrGreater())
            outputDisplayName(obj.getCursorName());
        else
            append(obj.getCursorName());

        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock(), data);
    }

    @Override
    public void visit(WhileStatement obj, Object data) {
        addLabel(obj);
        append(WHILE);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition(), data);
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock(), data);
    }

    @Override
    public void visit(ExistsCriteria obj, Object data) {
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(EXISTS);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(")"); //$NON-NLS-1$
    }

    private void addSubqueryHint(SubqueryHint hint) {
        if (hint.isNoUnnest()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.NOUNNEST);
            append(SPACE);
            append(END_HINT);
        } else if (hint.isDepJoin()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.DJ);
            append(SPACE);
            append(END_HINT);
        } else if (hint.isMergeJoin()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.MJ);
            append(SPACE);
            append(END_HINT);
        }
    }

    @Override
    public void visit(SubqueryCompareCriteria obj, Object data) {
        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression, data);

        String operator = obj.getOperator().toString();
        String quantifier = obj.getPredicateQuantifierAsString();

        // operator and beginning of list
        append(SPACE);
        append(operator);
        append(SPACE);
        append(quantifier);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(ScalarSubquery obj, Object data) {
        // operator and beginning of list
        append("("); //$NON-NLS-1$
        visitNode(obj.getCommand(), data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLAttributes obj, Object data) {
        append(XMLATTRIBUTES);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0, data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLElement obj, Object data) {
        append(XMLELEMENT);
        append("(NAME "); //$NON-NLS-1$
        outputDisplayName(obj.getName());
        if (obj.getNamespaces() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getNamespaces(), data);
        }
        if (obj.getAttributes() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getAttributes(), data);
        }
        if (!obj.getContent().isEmpty()) {
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getContent(), 0, data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLForest obj, Object data) {
        append(XMLFOREST);
        append("("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces(), data);
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getArgs(), 0, data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(JSONObject obj, Object data) {
        append(NonReserved.JSONOBJECT);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0, data);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(TextLine obj, Object data) {
        append(FOR);
        append(SPACE);
        registerNodes(obj.getExpressions(), 0, data);

        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()), data);
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            append(NonReserved.QUOTE);
            append(SPACE);
            visitNode(newConstant(obj.getQuote()), data);
        }
        if (obj.isIncludeHeader()) {
            append(SPACE);
            append(NonReserved.HEADER);
        }
        if (obj.getEncoding() != null) {
            append(SPACE);
            append(NonReserved.ENCODING);
            append(SPACE);
            outputDisplayName(obj.getEncoding());
        }
    }

    @Override
    public void visit(XMLNamespaces obj, Object data) {
        append(XMLNAMESPACES);
        append("("); //$NON-NLS-1$
        for (Iterator<NamespaceItem> items = obj.getNamespaceItems().iterator(); items.hasNext();) {
            NamespaceItem item = items.next();
            if (item.getPrefix() == null) {
                if (item.getUri() == null) {
                    append("NO DEFAULT"); //$NON-NLS-1$
                } else {
                    append("DEFAULT "); //$NON-NLS-1$
                    visitNode(newConstant(item.getUri()), data);
                }
            } else {
                visitNode(newConstant(item.getUri()), data);
                append(" AS "); //$NON-NLS-1$
                outputDisplayName(item.getPrefix());
            }
            if (items.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(Limit obj, Object data) {
        if (!obj.isStrict()) {
            append(BEGIN_HINT);
            append(SPACE);
            append(Limit.NON_STRICT);
            append(SPACE);
            append(END_HINT);
            append(SPACE);
        }
        if (obj.getRowLimit() == null) {
            append(OFFSET);
            append(SPACE);
            visitNode(obj.getOffset(), data);
            append(SPACE);
            append(ROWS);
            return;
        }
        append(LIMIT);
        if (obj.getOffset() != null) {
            append(SPACE);
            visitNode(obj.getOffset(), data);
            append(","); //$NON-NLS-1$
        }
        append(SPACE);
        visitNode(obj.getRowLimit(), data);
    }

    @Override
    public void visit(TextTable obj, Object data) {
        addHintComment(obj, data);
        append("TEXTTABLE("); //$NON-NLS-1$
        visitNode(obj.getFile(), data);
        if (isTeiid8OrGreater() && obj.getSelector() != null) {
            append(SPACE);
            append(NonReserved.SELECTOR);
            append(SPACE);
            append(escapeSinglePart(obj.getSelector()));
        }
        append(SPACE);
        append(NonReserved.COLUMNS);

        for (Iterator<TextColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            TextColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            if (col.getWidth() != null) {
                append(SPACE);
                append(NonReserved.WIDTH);
                append(SPACE);
                append(col.getWidth());
            }
            if (col.isNoTrim()) {
                append(SPACE);
                append(NO);
                append(SPACE);
                append(NonReserved.TRIM);
            }
            if (isTeiid8OrGreater() && col.getSelector() != null) {
                append(SPACE);
                append(NonReserved.SELECTOR);
                append(SPACE);
                append(escapeSinglePart(col.getSelector()));
                append(SPACE);
                append(col.getPosition());
            }
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }
        if (!obj.isUsingRowDelimiter()) {
            append(SPACE);
            append(NO);
            append(SPACE);
            append(ROW);
            append(SPACE);
            append(NonReserved.DELIMITER);
        }
        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()), data);
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            if (obj.isEscape()) {
                append(ESCAPE);
            } else {
                append(NonReserved.QUOTE);
            }
            append(SPACE);
            visitNode(newConstant(obj.getQuote()), data);
        }
        if (obj.getHeader() != null) {
            append(SPACE);
            append(NonReserved.HEADER);
            if (1 != obj.getHeader()) {
                append(SPACE);
                append(obj.getHeader());
            }
        }
        if (obj.getSkip() != null) {
            append(SPACE);
            append(NonReserved.SKIP);
            append(SPACE);
            append(obj.getSkip());
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(XMLTable obj, Object data) {
        addHintComment(obj, data);
        append("XMLTABLE("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces(), data);
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()), data);
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0, data);
        }

        if ((isTeiid8OrGreater() && !obj.getColumns().isEmpty() && !obj.isUsingDefaultColumn())
            || (!isTeiid8OrGreater() && !obj.getColumns().isEmpty())) {
            append(SPACE);
            append(NonReserved.COLUMNS);
            for (Iterator<XMLColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
                XMLColumn col = cols.next();
                append(SPACE);
                outputDisplayName(col.getName());
                append(SPACE);
                if (col.isOrdinal()) {
                    append(FOR);
                    append(SPACE);
                    append(NonReserved.ORDINALITY);
                } else {
                    append(col.getType());
                    if (col.getDefaultExpression() != null) {
                        append(SPACE);
                        append(DEFAULT);
                        append(SPACE);
                        visitNode(col.getDefaultExpression(), data);
                    }
                    if (col.getPath() != null) {
                        append(SPACE);
                        append(NonReserved.PATH);
                        append(SPACE);
                        visitNode(newConstant(col.getPath()), data);
                    }
                }
                if (cols.hasNext()) {
                    append(","); //$NON-NLS-1$
                }
            }
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(ObjectTable obj, Object data) {
        addHintComment(obj, data);
        append("OBJECTTABLE("); //$NON-NLS-1$
        if (obj.getScriptingLanguage() != null) {
            append(LANGUAGE);
            append(SPACE);
            visitNode(newConstant(obj.getScriptingLanguage()), data);
            append(SPACE);
        }
        visitNode(newConstant(obj.getRowScript()), data);
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0, data);
        }
        append(SPACE);
        append(NonReserved.COLUMNS);
        for (Iterator<ObjectColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            ObjectColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            append(SPACE);
            visitNode(newConstant(col.getPath()), data);
            if (col.getDefaultExpression() != null) {
                append(SPACE);
                append(DEFAULT);
                append(SPACE);
                visitNode(col.getDefaultExpression(), data);
            }
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(XMLQuery obj, Object data) {
        append("XMLQUERY("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces(), data);
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()), data);
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0, data);
        }
        if (obj.getEmptyOnEmpty() != null) {
            append(SPACE);
            if (obj.getEmptyOnEmpty()) {
                append(NonReserved.EMPTY);
            } else {
                append(NULL);
            }
            append(SPACE);
            append(ON);
            append(SPACE);
            append(NonReserved.EMPTY);
        }
        append(")");//$NON-NLS-1$
    }

    @Override
    public void visit(DerivedColumn obj, Object data) {
        visitNode(obj.getExpression(), data);
        if (obj.getAlias() != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            outputDisplayName(obj.getAlias());
        }
    }

    @Override
    public void visit(XMLSerialize obj, Object data) {
        append(XMLSERIALIZE);
        append(Tokens.LPAREN);
        if (obj.getDocument() != null) {
            if (obj.getDocument()) {
                append(NonReserved.DOCUMENT);
            } else {
                append(NonReserved.CONTENT);
            }
            append(SPACE);
        }
        visitNode(obj.getExpression(), data);
        if (obj.getTypeString() != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            append(obj.getTypeString());
        }
        if (isTeiid8OrGreater()) {
            if (obj.getEncoding() != null) {
                append(SPACE);
                append(NonReserved.ENCODING);
                append(SPACE);
                append(escapeSinglePart(obj.getEncoding()));
            }
            if (obj.getVersion() != null) {
                append(SPACE);
                append(NonReserved.VERSION);
                append(SPACE);
                append(newConstant(obj.getVersion()));
            }
            if (obj.getDeclaration() != null) {
                append(SPACE);
                if (obj.getDeclaration()) {
                    append(NonReserved.INCLUDING);
                } else {
                    append(NonReserved.EXCLUDING);
                }
                append(SPACE);
                append(NonReserved.XMLDECLARATION);
            }
        }
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(QueryString obj, Object data) {
        append(NonReserved.QUERYSTRING);
        append("("); //$NON-NLS-1$
        visitNode(obj.getPath(), data);
        if (!obj.getArgs().isEmpty()) {
            append(","); //$NON-NLS-1$
            append(SPACE);
            registerNodes(obj.getArgs(), 0, data);
        }
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLParse obj, Object data) {
        append(XMLPARSE);
        append(Tokens.LPAREN);
        if (obj.isDocument()) {
            append(NonReserved.DOCUMENT);
        } else {
            append(NonReserved.CONTENT);
        }
        append(SPACE);
        visitNode(obj.getExpression(), data);
        if (obj.isWellFormed()) {
            append(SPACE);
            append(NonReserved.WELLFORMED);
        }
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(ExpressionCriteria obj, Object data) {
        visitNode(obj.getExpression(), data);
    }

    @Override
    public void visit(TriggerAction obj, Object data) {
        append(FOR);
        append(SPACE);
        append(EACH);
        append(SPACE);
        append(ROW);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock(), data);
    }

    @Override
    public void visit(ArrayTable obj, Object data) {
        addHintComment(obj, data);
        append("ARRAYTABLE("); //$NON-NLS-1$
        visitNode(obj.getArrayValue(), data);
        append(SPACE);
        append(NonReserved.COLUMNS);

        for (Iterator<ProjectedColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            ProjectedColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }

        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    private void visitAlterProcedure(Alter<? extends CreateProcedureCommand> alterProcedure, Object data) {
        append(ALTER);
        append(SPACE);
        append(PROCEDURE);
        append(SPACE);
        append(alterProcedure.getTarget());
        beginClause(1);
        append(AS);
        append(alterProcedure.getDefinition().getBlock());
    }

    private void visitAlterTrigger(Alter<TriggerAction> alterTrigger, Object data) {
        if (alterTrigger.isCreate()) {
            append(CREATE);
        } else {
            append(ALTER);
        }
        append(SPACE);
        append(TRIGGER);
        append(SPACE);
        append(ON);
        append(SPACE);
        append(alterTrigger.getTarget());
        beginClause(0);
        append(NonReserved.INSTEAD);
        append(SPACE);
        append(OF);
        append(SPACE);
        append(alterTrigger.getEvent());
        if (alterTrigger.getDefinition() != null) {
            beginClause(0);
            append(AS);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(alterTrigger.getDefinition());
        } else {
            append(SPACE);
            append(alterTrigger.getEnabled() ? NonReserved.ENABLED : NonReserved.DISABLED);
        }
    }

    private void visitAlterView(Alter<QueryCommand> alterView, Object data) {
        append(ALTER);
        append(SPACE);
        append(NonReserved.VIEW);
        append(SPACE);
        append(alterView.getTarget());
        beginClause(0);
        append(AS);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        append(alterView.getDefinition());
    }

    @Override
    public void visit(Alter<? extends Command> alter, Object data) {
        switch (alter.getAlterType()) {
            case VIEW:
                visitAlterView((Alter<QueryCommand>)alter, data);
                return;
            case PROCEDURE:
                visitAlterProcedure((Alter<? extends CreateProcedureCommand>)alter, data);
                return;
            case TRIGGER:
                visitAlterTrigger((Alter<TriggerAction>)alter, data);
                return;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void visit(WindowFunction windowFunction, Object data) {
        append(windowFunction.getFunction());
        append(SPACE);
        append(OVER);
        append(SPACE);
        append(windowFunction.getWindowSpecification());
    }

    @Override
    public void visit(WindowSpecification windowSpecification, Object data) {
        append(Tokens.LPAREN);
        boolean needsSpace = false;
        if (windowSpecification.getPartition() != null) {
            append(PARTITION);
            append(SPACE);
            append(BY);
            append(SPACE);
            registerNodes(windowSpecification.getPartition(), 0, data);
            needsSpace = true;
        }
        if (windowSpecification.getOrderBy() != null) {
            if (needsSpace) {
                append(SPACE);
            }
            append(windowSpecification.getOrderBy());
        }
        append(Tokens.RPAREN);
    }

    private String escapeSinglePart(String part) {
        if (isReservedWord(part)) {
            return ID_ESCAPE_CHAR + part + ID_ESCAPE_CHAR;
        }
        boolean escape = true;
        char start = part.charAt(0);
        if (start == '#' || start == '@' || StringUtil.isLetter(start)) {
            escape = false;
            for (int i = 1; !escape && i < part.length(); i++) {
                char c = part.charAt(i);
                escape = !StringUtil.isLetterOrDigit(c) && c != '_';
            }
        }
        if (escape) {
            return ID_ESCAPE_CHAR + escapeStringValue(part, "\"") + ID_ESCAPE_CHAR; //$NON-NLS-1$
        }
        return part;
    }

    /**
     * Check whether a string is considered a reserved word or not. Subclasses may override to change definition of reserved word.
     *
     * @param string String to check
     * @return True if reserved word
     */
    private boolean isReservedWord(String string) {
        if (string == null) {
            return false;
        }
        return SQLConstants.isReservedWord(teiidVersion, string);
    }

}

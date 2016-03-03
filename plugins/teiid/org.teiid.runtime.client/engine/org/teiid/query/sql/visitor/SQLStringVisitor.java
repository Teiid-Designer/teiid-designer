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
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teiid.core.types.ArrayImpl;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.IToken;
import org.teiid.designer.query.sql.lang.IComment;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol.Type;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.language.SQLConstants.Reserved;
import org.teiid.language.SQLConstants.Tokens;
import org.teiid.metadata.AbstractMetadataRecord;
import org.teiid.metadata.BaseColumn;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.metadata.Column;
import org.teiid.metadata.ForeignKey;
import org.teiid.metadata.KeyRecord;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.Table;
import org.teiid.query.metadata.DDLConstants;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CacheHint;
import org.teiid.query.sql.lang.Comment;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Create.CommitAction;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.HasCriteria;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsDistinctCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.Labeled;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NamespaceItem;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.Option.MakeDep;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.PredicateCriteria;
import org.teiid.query.sql.lang.ProjectedColumn;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SourceHint;
import org.teiid.query.sql.lang.SourceHint.SpecificHint;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubqueryHint;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.ExceptionExpression;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.proc.ReturnStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.JSONObject;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLCast;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLExists;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.translator.SourceSystemFunctions;

/**
 * <p>
 * The SQLStringVisitor will visit a set of ast nodes and return the corresponding SQL string representation.
 * </p>
 */
public class SQLStringVisitor extends LanguageVisitor
    implements SQLConstants.Reserved, SQLConstants.NonReserved, SQLConstants.Tokens, DDLConstants, ISQLStringVisitor<LanguageObject> {

    /*
     * Converted from static field to function to allow version to be checked
     */
    @Since(Version.TEIID_8_10)
    private static Map<String, String> builtinPrefixes(ITeiidServerVersion version) {
        Map<String, String> builtinPrefixes = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : MetadataFactory.builtinNamespaces(version).entrySet()) {
            builtinPrefixes.put(entry.getValue(), entry.getKey());
        }

        return builtinPrefixes;
    }

    @Since(Version.TEIID_8_0)
    private static final HashSet<String> LENGTH_DATATYPES = new HashSet<String>(
        Arrays.asList(
            DataTypeManagerService.DefaultDataTypes.CHAR.getId(),
            DataTypeManagerService.DefaultDataTypes.CLOB.getId(),
            DataTypeManagerService.DefaultDataTypes.BLOB.getId(),
            DataTypeManagerService.DefaultDataTypes.OBJECT.getId(),
            DataTypeManagerService.DefaultDataTypes.XML.getId(),
            DataTypeManagerService.DefaultDataTypes.STRING.getId(),
            DataTypeManagerService.DefaultDataTypes.VARBINARY.getId(),
            DataTypeManagerService.DefaultDataTypes.BIG_INTEGER.getId()));

    @Since(Version.TEIID_8_0)
    private static final HashSet<String> PRECISION_DATATYPES = new HashSet<String>(
        Arrays.asList(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getId()));

    /**
     * Undefined
     */
    public static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private static final String BEGIN_HINT = "/*+"; //$NON-NLS-1$

    private static final String END_HINT = "*/"; //$NON-NLS-1$

    private static final char ID_ESCAPE_CHAR = '\"';

    /**
     * Indicator for disabling comments across multiple instances of this class.
     * Possibilities of calling new instances of this class from this class when
     * calling append(LanguageObject). This will convert the LanguageObject to
     * a String using a new instance of this class. Since we only want comments
     * added by the single 'top-most' instance then this set must be empty for
     * comments to be added.
     */
    private static Set<String> ALLOW_COMMENTS = new HashSet<String>();

    protected StringBuilder parts = new StringBuilder();

    private boolean shortNameOnly = false;

    /**
     * @param teiidVersion
     */
    public SQLStringVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    /**
     * Helper to quickly get the parser string for an object using the visitor.
     *
     * @param obj Language object
     *
     * @return String SQL String for obj
     */
    public static final String getSQLString(LanguageObject obj) {
        if (obj == null) {
            return UNDEFINED;
        }

        SQLStringVisitor visitor = new SQLStringVisitor(obj.getTeiidVersion());
        return visitor.returnSQLString(obj);
    }

    /**
     * @param languageObject
     * @return sql representation of {@link LanguageObject}
     */
    @Override
    public String returnSQLString(LanguageObject languageObject) {
        if (languageObject == null) {
            return UNDEFINED;
        }

        isApplicable(languageObject);
        languageObject.acceptVisitor(this);
        return getSQLString();
    }

    /**
     * Retrieve completed string from the visitor.
     *
     * @return Complete SQL string for the visited nodes
     */
    public String getSQLString() {
        return this.parts.toString().trim();
    }

    /**
     * @return the shortNameOnly
     */
    public boolean isShortNameOnly() {
        if (!isTeiid8OrGreater())
            return false; // Not applicable for teiid 7

        return this.shortNameOnly;
    }

    /**
     * @param shortNameOnly the shortNameOnly to set
     */
    private void setShortNameOnly(boolean shortNameOnly) {
        if (!isTeiid8OrGreater())
            return; // Not applicable for teiid 7

        this.shortNameOnly = shortNameOnly;
    }

    protected void visitNode(LanguageObject obj) {
        if (obj == null) {
            append(UNDEFINED);
            return;
        }
        isApplicable(obj);
        obj.acceptVisitor(this);
    }

    protected void append(Object value) {
        if (value instanceof LanguageObject) {
            disableComments((LanguageObject) value);
        }

        this.parts.append(value);

        if (value instanceof LanguageObject) {
            enableComments((LanguageObject) value);
        }
    }

    protected void beginClause(int level) {
        append(SPACE);
    }

    private Constant newConstant(Object value) {
        Constant constant = createNode(ASTNodes.CONSTANT);
        constant.setValue(value);
        return constant;
    }

    private int nextAvailableSpace(String sql, int index) {
        for (int i = index - 1; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if (Character.isWhitespace(c))
                return i + 1;
        }

        return sql.length();
    }

    /**
     * based on {@link #outputDisplayName(String)}
     *
     * @param name
     * @return escaped version of name, eg. x.from.y becomes x."from".y
     */
    public String displayName(IToken token) {
        if (! token.isId())
            return token.getText();

        StringBuffer buf = new StringBuffer();
        String[] pathParts = token.getText().split("\\."); //$NON-NLS-1$
        for (int i = 0; i < pathParts.length; i++) {
            if (i > 0) {
                buf.append(Symbol.SEPARATOR);
            }
            buf.append(escapeSinglePart(pathParts[i]));
        }

        return buf.toString();
    }

    private String escape(String text, String character, boolean optional) {
        String target = StringUtil.Constants.ESCAPE + character;
        String replacement = StringUtil.Constants.REGEX_ESCAPE + character + (optional ? StringUtil.Constants.QMARK : StringUtil.Constants.EMPTY_STRING);
        text = text.replaceAll(target, replacement);
        return text;
    }

    /**
     * @param sql
     * @param comment
     * @return
     */
    private int calculateLocation(String sql, Comment comment) {
        int cmtIdx = comment.getOffset();
        LinkedList<IToken> preTokens = new LinkedList(comment.getPreTokens());

        if (preTokens.isEmpty())
            return cmtIdx; // can happen if offset is 0 and comment is at the start

        // i : case-insensitive mode
        // s: include line terminators in . matchings
        StringBuffer regex = new StringBuffer("(?is)"); //$NON-NLS-1$
        Iterator<IToken> iterator = preTokens.iterator();
        while(iterator.hasNext()) {
            IToken token = iterator.next();
            String text = displayName(token);

            // Must escape question marks first since optional is represented by question marks in regex
            text = escape(text, SQLConstants.Tokens.QMARK, false);

            text = escape(text, StringUtil.Constants.SPEECH_MARK, true);
            text = escape(text, StringUtil.Constants.QUOTE, true);
            text = escape(text, SQLConstants.Tokens.LPAREN, true);
            text = escape(text, SQLConstants.Tokens.RPAREN, true);

            // The zero at the end of a time is optional, eg. 19:00:02.50, so
            // gets dropped by the production. Thus, need to make it
            // optional. Have to do it prior to escaping DOT, otherwise the
            // replace regex becomes consumed by backslashes!
            String target = "([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9])0"; //$NON-NLS-1$
            String replacement = "$1[0-9]?'"; //$NON-NLS-1$
            text = text.replaceAll(target, replacement);

            // The source hint is parseable as /*+ sh ... */ but all spaces are removed
            // between the + and sh upon conversion.
            target = "\\/\\*\\+\\s*sh";
            replacement = "/*+sh";
            text = text.replaceAll(target, replacement);

            text = escape(text, SQLConstants.Tokens.DOT, false);
            text = escape(text, StringUtil.Constants.LBRACE, false);

            // Makes right brace optional since fn{ is dropped as the prefix of functions
            // and its closing brace will remain as a pre token
            text = escape(text, StringUtil.Constants.RBRACE, true);
            text = escape(text, StringUtil.Constants.FORWARD_SLASH, false);
            text = escape(text, StringUtil.Constants.STAR, false);
            text = escape(text, StringUtil.Constants.PLUS, false);
            text = escape(text, StringUtil.Constants.PIPE, false);

            regex.append(text);

            if (iterator.hasNext())
                regex.append(DOT).append(StringUtil.Constants.STAR).append(QMARK);
        }

        Pattern pattern = Pattern.compile(regex.toString());
        Matcher matcher = pattern.matcher(sql);

        //
        // Find ALL the possible sub sequences that the matcher could match.
        // Most of the time it should be only one but sometimes a SELECT subquery
        // could have the same WHERE condition as the outer master query.
        // WITH x AS (SELECT a FROM db.g WHERE b = aString /* Comment 1 */)
        //                     SELECT a FROM db.g WHERE b = aString /* Comment 2 */
        List<Integer> results = new ArrayList<Integer>();
        boolean result = true;
        while (result) {
            result = matcher.find();
            if (result) {
                // We know that the matcher has succeeded once but is possible that
                // since its a reluctant matcher it is in fact finding a result far earlier
                // than required.
                results.add(matcher.end());
            }
        }

        if (results.isEmpty())
            return -1;

        //
        // Loop through the candidates and choose the pair {start, end} which has an end
        // value closest to the offset of the comment
        //
        Integer pref = results.get(0);
        int diff = Math.abs(pref - cmtIdx);
        for (Integer poss : results) {
            int pdiff = Math.abs(poss - cmtIdx);
            if (pdiff < diff)
                pref = poss;
        }

        int offset = pref + 1;
        if (offset != cmtIdx)
            cmtIdx = offset;

        //
        // At this point, we have a location index for the comment.
        // However, its still possible, despite best efforts that that index
        // is within a term. Thus, check the index finally...
        //
        return nextAvailableSpace(sql, cmtIdx);
    }

    @Override
    public void enableComments(Object obj) {
        ALLOW_COMMENTS.remove(obj.getClass().getSimpleName() + System.identityHashCode(obj));
    }

    @Override
    public void disableComments(Object obj) {
        ALLOW_COMMENTS.add(obj.getClass().getSimpleName() + System.identityHashCode(obj));
    }

    private Set<IComment> seenComments = new HashSet<IComment>();

    protected void addComments(LanguageObject obj) {
        // Comments disabled if this set contains identity hashcodes
        if (!ALLOW_COMMENTS.isEmpty())
            return;

        Set<Comment> comments = obj.getComments();

        for (Comment comment : comments) {
            if (seenComments.contains(comment))
                continue;

            String sql = parts.toString();
            int insertIdx = calculateLocation(sql, comment);
            if (insertIdx == -1)
                continue;

            // Handling trailing comments
            if (insertIdx >= sql.length()) {
                // insert index is the end of the sql string
                if (! sql.endsWith(SPACE))
                    parts.append(SPACE);

                // avoid appending a space since the sql doesn't end with one
                // so will most likely be added prior to the next token
                parts.append(comment.getText());

                if (! comment.isMultiLine())
                    parts.append(NEWLINE);

            } else {
                // Most of the comments with be dealt with here
                String text = comment.getText();
                if (comment.isMultiLine())
                    text = text + SPACE;
                else
                    text = text + NEWLINE;

                parts.insert(insertIdx, text);
            }

            seenComments.add(comment);
        }
    }

    // ############ Visitor methods for language objects ####################

    @Override
    public void visit(BetweenCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getExpression());
        append(SPACE);

        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(BETWEEN);
        append(SPACE);
        visitNode(obj.getLowerExpression());

        append(SPACE);
        append(AND);
        append(SPACE);
        visitNode(obj.getUpperExpression());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(CaseExpression obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(CASE);
        append(SPACE);
        visitNode(obj.getExpression());
        append(SPACE);

        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenExpression(i));
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i));
            append(SPACE);
        }

        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression());
            append(SPACE);
        }
        append(END);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(CompareCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression);
        append(SPACE);
        append(obj.getOperatorAsString());
        append(SPACE);
        Expression rightExpression = obj.getRightExpression();
        visitNode(rightExpression);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(CompoundCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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
            visitNode(firstChild);
        } else {
            // Add first criteria
            Iterator<Criteria> iter = subCriteria.iterator();

            while (iter.hasNext()) {
                // Add criteria
                Criteria crit = iter.next();
                append(Tokens.LPAREN);
                visitNode(crit);
                append(Tokens.RPAREN);

                if (iter.hasNext()) {
                    // Add connector
                    append(SPACE);
                    append(operatorStr);
                    append(SPACE);
                }
            }
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Delete obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        // add delete clause
        append(DELETE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        // add from clause
        append(FROM);
        append(SPACE);
        visitNode(obj.getGroup());

        // add where clause
        if (obj.getCriteria() != null) {
            beginClause(0);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(From obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(FROM);
        beginClause(1);
        registerNodes(obj.getClauses(), 0);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(GroupBy obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(GROUP);
        append(SPACE);
        append(BY);
        append(SPACE);
        if (isTeiidVersionOrGreater(Version.TEIID_8_5) && obj.isRollup()) {
        	append(ROLLUP);
        	append(Tokens.LPAREN);
        }
        registerNodes(obj.getSymbols(), 0);
        if (isTeiidVersionOrGreater(Version.TEIID_8_5) && obj.isRollup()) {
        	append(Tokens.RPAREN);
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Insert obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isTeiid8OrGreater() && obj.isMerge()) {
            append(MERGE);
        } else {
            append(INSERT);
        }
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup());

        if (!obj.getVariables().isEmpty()) {
            beginClause(2);

            // Columns clause
            List<ElementSymbol> vars = obj.getVariables();
            if (vars != null) {
                append("("); //$NON-NLS-1$
                setShortNameOnly(true);
                registerNodes(vars, 0);
                setShortNameOnly(false);
                append(")"); //$NON-NLS-1$
            }
        }
        beginClause(1);
        if (obj.getQueryExpression() != null) {
            visitNode(obj.getQueryExpression());
            //         } else if (obj.getTupleSource() != null) {
            //             append(VALUES);
            //             append(" (...)"); //$NON-NLS-1$
        } else if (obj.getValues() != null) {
            append(VALUES);
            beginClause(2);
            append("("); //$NON-NLS-1$
            registerNodes(obj.getValues(), 0);
            append(")"); //$NON-NLS-1$
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }

    private void visit7( Create obj ) {
        append(CREATE);
        append(SPACE);
        append(LOCAL);
        append(SPACE);
        append(TEMPORARY);
        append(SPACE);
        append(TABLE);
        append(SPACE);
        visitNode(obj.getTable());
        append(SPACE);

        // Columns clause
        List<Column> columns = obj.getColumns();
        append("("); //$NON-NLS-1$
        Iterator<Column> iter = columns.iterator();
        while (iter.hasNext()) {
            Column element = iter.next();
            outputDisplayName(element.getName());
            append(SPACE);
            if (element.isAutoIncremented()) {
                append(NonReserved.SERIAL);
            } else {
                append(element.getRuntimeType());
                if (element.getNullType() == NullType.No_Nulls) {
                    append(NOT);
                    append(SPACE);
                    append(NULL);
                }
            }
            if (iter.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
        if (!obj.getPrimaryKey().isEmpty()) {
            append(", "); //$NON-NLS-1$
            append(PRIMARY);
            append(" "); //$NON-NLS-1$
            append(NonReserved.KEY);
            append(Tokens.LPAREN);
            Iterator<ElementSymbol> pkiter = obj.getPrimaryKey().iterator();
            while (pkiter.hasNext()) {
                outputShortName(pkiter.next());
                if (pkiter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
            }
            append(Tokens.RPAREN);
        }
        append(")"); //$NON-NLS-1$
    }

    private String buildTableOptions(Table table) {
        StringBuilder options = new StringBuilder();
        addCommonOptions(options, table);
        
        if (table.isMaterialized()) {
            addOption(options, MATERIALIZED, table.isMaterialized());
            if (table.getMaterializedTable() != null) {
                addOption(options, MATERIALIZED_TABLE, table.getMaterializedTable().getName());
            }
        }
        if (table.supportsUpdate()) {
            addOption(options, UPDATABLE, table.supportsUpdate());
        }
        if (table.getCardinality() != -1) {
            addOption(options, CARDINALITY, table.getCardinality());
        }
        if (!table.getProperties().isEmpty()) {
            for (String key:table.getProperties().keySet()) {
                addOption(options, key, table.getProperty(key, false));
            }
        }
        return options.toString();
    }

    private void addCommonOptions(StringBuilder sb, AbstractMetadataRecord record) {
        if (record.getUUID() != null && !record.getUUID().startsWith("tid:")) { //$NON-NLS-1$
            addOption(sb, UUID, record.getUUID());
        }
        if (record.getAnnotation() != null) {
            addOption(sb, ANNOTATION, record.getAnnotation());
        }
        if (record.getNameInSource() != null && !record.getNameInSource().equals(record.getName())) {
            addOption(sb, NAMEINSOURCE, record.getNameInSource());
        }
    }
    
    private void buildContraints(Table table) {
        addConstraints(table.getAccessPatterns(), "AP", ACCESSPATTERN); //$NON-NLS-1$
        
        KeyRecord pk = table.getPrimaryKey();
        if (pk != null) {
            addConstraint("PK", PRIMARY_KEY, pk, true); //$NON-NLS-1$
        }

        addConstraints(table.getUniqueKeys(), UNIQUE, UNIQUE);
        addConstraints(table.getIndexes(), INDEX, INDEX);
        addConstraints(table.getFunctionBasedIndexes(), INDEX, INDEX);

        for (int i = 0; i < table.getForeignKeys().size(); i++) {
            ForeignKey key = table.getForeignKeys().get(i);
            addConstraint("FK" + i, FOREIGN_KEY, key, false); //$NON-NLS-1$
            append(SPACE);
            append(REFERENCES);
            if (key.getReferenceTableName() != null) {
                append(SPACE);
                GroupSymbol gs = getTeiidParser().createASTNode(ASTNodes.GROUP_SYMBOL);
                gs.setName(key.getReferenceTableName());
                append(gs.getName());
            }
            append(SPACE);
            addNames(key.getReferenceColumns());
            appendOptions(key);
        }
    }

    private void addConstraints(List<KeyRecord> constraints, String defaultName, String type) {
        for (int i = 0; i < constraints.size(); i++) {
            KeyRecord constraint = constraints.get(i);
            addConstraint(defaultName + i, type, constraint, true);
        }
    }

    private void addConstraint(String defaultName, String type,
            KeyRecord constraint, boolean addOptions) {
        append(COMMA);
        append(NEWLINE);
        append(TAB);
        boolean nameMatches = defaultName.equals(constraint.getName());
        if (!nameMatches) {
            append(CONSTRAINT);
            append(SPACE);
            append(escapeSinglePart(constraint.getName()));
            append(SPACE); 
        }
        append(type);
        addColumns(constraint.getColumns(), false);
        if (addOptions) {
            appendOptions(constraint);
        }
    }

    private void addColumns(List<Column> columns, boolean includeType) {
        append(LPAREN);
        boolean first = true;
        for (Column c:columns) {
            if (first) {
                first = false;
            }
            else {
                append(COMMA);
                append(SPACE);
            }
            if (includeType) {
                appendColumn(c, true, includeType);
                appendColumnOptions(c);
            } else if (c.getParent() instanceof KeyRecord) {
                //function based column
                append(c.getNameInSource());
            } else {
                append(escapeSinglePart(c.getName()));
            }
        }
        append(RPAREN);
    }

    private void addNames(List<String> columns) {
        if (columns != null) {
            append(LPAREN);
            boolean first = true;
            for (String c:columns) {
                if (first) {
                    first = false;
                }
                else {
                    append(COMMA);
                    append(SPACE);
                }
                append(escapeSinglePart(c));
            }
            append(RPAREN);
        }
    }   
    
    private void visit(Column column) {
        append(NEWLINE);
        append(TAB);
        appendColumn(column, true, true);
        
        if (column.isAutoIncremented()) {
            append(SPACE);
            append(DDLConstants.AUTO_INCREMENT);
        }
        
        appendDefault(column);
        
        // options
        appendColumnOptions(column);
    }

    private void appendDefault(BaseColumn column) {
        if (column.getDefaultValue() != null) {
            append(SPACE);
            append(DEFAULT);
            append(SPACE);
            append(TICK);
            append(StringUtil.replaceAll(column.getDefaultValue(), TICK, TICK + TICK));
            append(TICK);
        }
    }

    private void appendColumn(BaseColumn column, boolean includeName, boolean includeType) {
        if (includeName) {
            append(escapeSinglePart(column.getName()));
        }
        if (includeType) {
            String runtimeTypeName = column.getDatatype().getRuntimeTypeName();
            if (includeName) {
                append(SPACE);
            }
            append(runtimeTypeName);
            if (LENGTH_DATATYPES.contains(runtimeTypeName)) {
                if (column.getLength() != 0 && column.getLength() != column.getDatatype().getLength()) {
                    append(LPAREN);
                    append(column.getLength());
                    append(RPAREN);
                }
            } else if (PRECISION_DATATYPES.contains(runtimeTypeName) 
                    && (column.getPrecision() != column.getDatatype().getPrecision() || column.getScale() != column.getDatatype().getScale())) {
                append(LPAREN);
                append(column.getPrecision());
                if (column.getScale() != 0) {
                    append(COMMA);
                    append(column.getScale());
                }
                append(RPAREN);
            }
            if (column.getNullType() == NullType.No_Nulls) {
                append(SPACE);
                append(NOT_NULL);
            }
        }
    }   
    
    private void appendColumnOptions(BaseColumn column) {
        StringBuilder options = new StringBuilder();
        addCommonOptions(options, column);
        
        if (!column.getDatatype().isBuiltin()) {
            addOption(options, UDT, column.getDatatype().getName() + "("+column.getLength()+ ", " +column.getPrecision()+", " + column.getScale()+ ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        
        if (column.getDatatype().getRadix() != 0 && column.getRadix() != column.getDatatype().getRadix()) {
            addOption(options, RADIX, column.getRadix());
        }
        
        if (column instanceof Column) {
            buildColumnOptions((Column)column, options);
        }
        if (options.length() != 0) {
            append(SPACE);
            append(OPTIONS);
            append(SPACE);
            append(LPAREN);
            append(options);
            append(RPAREN);
        }
    }

    private void buildColumnOptions(Column column, 
            StringBuilder options) {
        if (!column.isSelectable()) {
            addOption(options, SELECTABLE, column.isSelectable());
        }       

        // if table is already updatable, then columns are implicitly updatable.
        if (!column.isUpdatable() && column.getParent() instanceof Table && ((Table)column.getParent()).supportsUpdate()) {
            addOption(options, UPDATABLE, column.isUpdatable());
        }
        
        if (column.isCurrency()) {
            addOption(options, CURRENCY, column.isCurrency());
        }
            
        // only record if not default
        if (!column.isCaseSensitive() && column.getDatatype().isCaseSensitive()) {
            addOption(options, CASE_SENSITIVE, column.isCaseSensitive());
        }
        
        if (!column.isSigned() && column.getDatatype().isSigned()) {
            addOption(options, SIGNED, column.isSigned());
        }         
        if (column.isFixedLength()) {
            addOption(options, FIXED_LENGTH, column.isFixedLength());
        }
        // length and octet length should be same. so this should be never be true.
        //TODO - this is not quite valid since we are dealing with length representing chars in UTF-16, then there should be twice the bytes
        if (column.getCharOctetLength() != 0 && column.getLength() != column.getCharOctetLength()) {
            addOption(options, CHAR_OCTET_LENGTH, column.getCharOctetLength());
        }   
        
        // by default the search type is default data type search, so avoid it.
        if (column.getSearchType() != null && (!column.getSearchType().equals(column.getDatatype().getSearchType()) || column.isSearchTypeSet())) {
            addOption(options, SEARCHABLE, column.getSearchType().name());
        }
        
        if (column.getMinimumValue() != null) {
            addOption(options, MIN_VALUE, column.getMinimumValue());
        }
        
        if (column.getMaximumValue() != null) {
            addOption(options, MAX_VALUE, column.getMaximumValue());
        }
        
        if (column.getNativeType() != null) {
            addOption(options, NATIVE_TYPE, column.getNativeType());
        }
        
        if (column.getNullValues() != -1) {
            addOption(options, NULL_VALUE_COUNT, column.getNullValues());
        }
        
        if (column.getDistinctValues() != -1) {
            addOption(options, DISTINCT_VALUES, column.getDistinctValues());
        }       
        
        buildOptions(column, options);
    }
    
    private void appendOptions(AbstractMetadataRecord record) {
        StringBuilder options = new StringBuilder();
        buildOptions(record, options);
        if (options.length() != 0) {
            append(SPACE);
            append(OPTIONS);
            append(SPACE);
            append(LPAREN);
            append(options);
            append(RPAREN);
        }
    }

    private void buildOptions(AbstractMetadataRecord record, StringBuilder options) {
        if (!record.getProperties().isEmpty()) {
            for (Map.Entry<String, String> entry:record.getProperties().entrySet()) {
                addOption(options, entry.getKey(), entry.getValue());
            }
        }
    }   
    
    private void addOption(StringBuilder sb, String key, Object value) {
        if (sb.length() != 0) {
            sb.append(COMMA).append(SPACE);
        }

        Constant c = getTeiidParser().createASTNode(ASTNodes.CONSTANT);
        c.setValue(value);
        value = c;

        if (key != null && key.length() > 2 && key.charAt(0) == '{') { 
            String origKey = key;
            int index = key.indexOf('}');
            if (index > 1) {
                String uri = key.substring(1, index);
                key = key.substring(index + 1, key.length());
                String prefix = builtinPrefixes(getTeiidVersion()).get(uri);
                if (prefix != null) {
                    key = prefix + ":" + key; //$NON-NLS-1$
                } else {
                    key = origKey;
                }
            }
        }
        sb.append(escapeSinglePart(key));
        append(SPACE);
        append(value);
    }

    private String addTableBody(Table table) {
        String name = escapeSinglePart(table.getName());
        append(name);
        
        if (table.getColumns() != null) {
            append(SPACE);
            append(LPAREN);
            boolean first = true; 
            for (Column c:table.getColumns()) {
                if (first) {
                    first = false;
                }
                else {
                    append(COMMA);
                }
                visit(c);
            }
            buildContraints(table);
            append(NEWLINE);
            append(RPAREN);         
        }
        
        // options
        String options = buildTableOptions(table);      
        if (!options.isEmpty()) {
            append(SPACE);
            append(OPTIONS);
            append(SPACE);
            append(LPAREN);
            append(options);
            append(RPAREN);
        }
        return name;
    }

    private void visit8( Create obj ) {
        append(CREATE);
        append(SPACE);
        if (obj.getTableMetadata() != null) {
            append(FOREIGN);
            append(SPACE);
            append(TEMPORARY);
            append(SPACE);
            append(TABLE);
            append(SPACE);
            
            addTableBody(obj.getTableMetadata());

            append(SPACE);
            append(ON);
            append(SPACE);
            outputLiteral(String.class, false, obj.getOn());
            return;
        }
        append(LOCAL);
        append(SPACE);
        append(TEMPORARY);
        append(SPACE);
        append(TABLE);
        append(SPACE);
        visitNode(obj.getTable());
        append(SPACE);

        // Columns clause
        List<Column> columns = obj.getColumns();
        append("("); //$NON-NLS-1$
        Iterator<Column> iter = columns.iterator();
        while (iter.hasNext()) {
            Column element = iter.next();
            outputDisplayName(element.getName());
            append(SPACE);
            if (element.isAutoIncremented()) {
                append(NonReserved.SERIAL);
            } else {
                append(element.getRuntimeType());
                if (element.getNullType() == NullType.No_Nulls) {
                    append(SPACE);
                    append(NOT);
                    append(SPACE);
                    append(NULL);
                }
            }
            if (iter.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
        if (!obj.getPrimaryKey().isEmpty()) {
            append(", "); //$NON-NLS-1$
            append(PRIMARY);
            append(" "); //$NON-NLS-1$
            append(NonReserved.KEY);
            append(Tokens.LPAREN);
            Iterator<ElementSymbol> pkiter = obj.getPrimaryKey().iterator();
            while (pkiter.hasNext()) {
                outputShortName(pkiter.next());
                if (pkiter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
            }
            append(Tokens.RPAREN);
        }
        
        append(Tokens.RPAREN);

        if (isTeiid810OrGreater()) {
            CommitAction commitAction = obj.getCommitAction();
            if (commitAction != null) {
                append(Tokens.SPACE);
                append(Reserved.ON);
                append(Tokens.SPACE);
                append(Reserved.COMMIT);
                append(Tokens.SPACE);
                switch (commitAction) {
                    case PRESERVE_ROWS:
                        append(NonReserved.PRESERVE);
                        append(Tokens.SPACE);
                        append(Reserved.ROWS);
                        break;
                }
            }
        }
    }

    @Override
    public void visit(Create obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isTeiid8OrGreater())
            visit8(obj);
        else
            visit7(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Drop obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(DROP);
        append(SPACE);
        append(TABLE);
        append(SPACE);
        visitNode(obj.getTable());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(IsNullCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        Expression expr = obj.getExpression();
        if (isTeiid8OrGreater())
            appendNested(expr);
        else
            visitNode(expr);

        append(SPACE);
        append(IS);
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(NULL);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(JoinPredicate obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        addHintComment(obj);

        if (obj.hasHint()) {
            append("(");//$NON-NLS-1$
        }

        // left clause
        FromClause leftClause = obj.getLeftClause();
        if (leftClause instanceof JoinPredicate && !((JoinPredicate)leftClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(leftClause);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(leftClause);
        }

        // join type
        append(SPACE);
        visitNode(obj.getJoinType());
        append(SPACE);

        // right clause
        FromClause rightClause = obj.getRightClause();
        if (rightClause instanceof JoinPredicate && !((JoinPredicate)rightClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(rightClause);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(rightClause);
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
                    visitNode(crit);
                } else {
                    append("("); //$NON-NLS-1$
                    visitNode(crit);
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

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    private void addHintComment(FromClause obj) {
        if (! obj.hasHint())
            return;

        append(BEGIN_HINT);
        append(SPACE);
        if (obj.isOptional()) {
            append(Option.OPTIONAL);
            append(SPACE);
        }

        if (obj.getMakeDep() != null) {
            if (obj.getMakeDep().isSimple() && isLessThanTeiid8124()) {
                append(Option.MAKEDEP);
                append(SPACE);
            } else {
                append(Option.MAKEDEP);
                appendMakeDepOptions(obj.getMakeDep());
                append(SPACE);
            }
        }
        
        if (obj.isMakeNotDep()) {
            append(Option.MAKENOTDEP);
            append(SPACE);
        }
        
        // Will return false if NOT less than Teiid 8.12.4        
        if (obj.isMakeInd()) {
            append(FromClause.MAKEIND);
            append(SPACE);
        }

        // Will return null if less than Teiid 8.12.4
        if (obj.getMakeInd() != null) {
            append(MAKEIND);
            appendMakeDepOptions(obj.getMakeInd());
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

    @Override
    public void visit(JoinType obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(MatchCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getLeftExpression());

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

        visitNode(obj.getRightExpression());

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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(NotCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(NOT);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCriteria());
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Option obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(OPTION);

        Collection<String> groups = obj.getDependentGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(MAKEDEP);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();
            Iterator<MakeDep> iter1 = obj.getMakeDepOptions().iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());
                
                appendMakeDepOptions(iter1.next());

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

        enableComments(obj);
        addComments(obj);
    }

    /**
     * @param makedep
     * @return this visitor
     */
    public SQLStringVisitor appendMakeDepOptions(MakeDep makedep) {
        if (isLessThanTeiidVersion(Version.TEIID_8_5))
            return this;

        boolean parens = false;
        if (makedep.getMax() != null || makedep.isJoin()) {
            append(Tokens.LPAREN);
            parens = true;
        }
        boolean space = false;
        if (makedep.getMax() != null) {
            if (space) {
                append(SPACE);
            } else {
                space = true;
            }
            append(NonReserved.MAX);
            append(Tokens.COLON);
            append(makedep.getMax());
        }
        if (makedep.getJoin() != null) {
            if (space) {
                append(SPACE);
            } else {
                space = true;
            }
            if (!makedep.getJoin()) {
                append(NO);
                append(SPACE);
            }
            append(JOIN);
        }
        if (parens) {
            append(Tokens.RPAREN);
        }

        return this;
    }

    @Override
    public void visit(OrderBy obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(ORDER);
        append(SPACE);
        append(BY);
        append(SPACE);
        registerNodes(obj.getOrderByItems(), 0);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(OrderByItem obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        Expression ses = obj.getSymbol();
        if (ses instanceof AliasSymbol) {
            AliasSymbol as = (AliasSymbol)ses;
            outputDisplayName(as.getOutputName());
        } else {
            visitNode(ses);
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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(DynamicCommand obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(EXECUTE);
        append(SPACE);
        append(IMMEDIATE);
        append(SPACE);
        visitNode(obj.getSql());

        if (obj.isAsClauseSet()) {
            beginClause(1);
            append(AS);
            append(SPACE);
            for (int i = 0; i < obj.getAsColumns().size(); i++) {
                ElementSymbol symbol = (ElementSymbol)obj.getAsColumns().get(i);
                outputShortName(symbol);
                append(SPACE);
                append(getDataTypeManager().getDataTypeName(symbol.getType()));
                if (i < obj.getAsColumns().size() - 1) {
                    append(", "); //$NON-NLS-1$
                }
            }
        }

        if (obj.getIntoGroup() != null) {
            beginClause(1);
            append(INTO);
            append(SPACE);
            visitNode(obj.getIntoGroup());
        }

        if (obj.getUsing() != null && !obj.getUsing().isEmpty()) {
            beginClause(1);
            append(USING);
            append(SPACE);
            visitNode(obj.getUsing());
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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(SetClauseList obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        for (Iterator<SetClause> iterator = obj.getClauses().iterator(); iterator.hasNext();) {
            SetClause clause = iterator.next();
            visitNode(clause);
            if (iterator.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(SetClause obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        ElementSymbol symbol = obj.getSymbol();
        outputShortName(symbol);
        append(" = "); //$NON-NLS-1$
        visitNode(obj.getValue());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(WithQueryCommand obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getGroupSymbol());
        append(SPACE);
        if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
            append(Tokens.LPAREN);
            setShortNameOnly(true);
            registerNodes(obj.getColumns(), 0);
            setShortNameOnly(false);
            append(Tokens.RPAREN);
            append(SPACE);
        }
        append(AS);
        append(SPACE);
        append(Tokens.LPAREN);
        if (isTeiidVersionOrGreater(Version.TEIID_8_5) && obj.getCommand() == null) {
            append("<dependent values>"); //$NON-NLS-1$
        } else {
            visitNode(obj.getCommand());
        }
        append(Tokens.RPAREN);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Query obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

    	addCacheHint(obj.getCacheHint());
        addWithClause(obj);
        append(SELECT);

        SourceHint sh = obj.getSourceHint();
        addSourceHint(sh);
        if (obj.getSelect() != null) {
            visitNode(obj.getSelect());
        }

        if (obj.getInto() != null) {
            beginClause(1);
            visitNode(obj.getInto());
        }

        if (obj.getFrom() != null) {
            beginClause(1);
            visitNode(obj.getFrom());
        }

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Group by clause
        if (obj.getGroupBy() != null) {
            beginClause(1);
            visitNode(obj.getGroupBy());
        }

        // Having clause
        if (obj.getHaving() != null) {
            beginClause(1);
            visitCriteria(HAVING, obj.getHaving());
        }

        // Order by clause
        if (obj.getOrderBy() != null) {
            beginClause(1);
            visitNode(obj.getOrderBy());
        }

        if (obj.getLimit() != null) {
            beginClause(1);
            visitNode(obj.getLimit());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }

    private void addSourceHint(SourceHint sh) {
        if (sh == null)
            return;

        append(SPACE);
        append(BEGIN_HINT);
        append("sh"); //$NON-NLS-1$

        if (isTeiid8OrGreater() && sh.isUseAliases()) {
            append(SPACE);
            append("KEEP ALIASES"); //$NON-NLS-1$
        }

        if (sh.getGeneralHint() != null) {
            appendSourceHintValue(sh.getGeneralHint());
        } else {
            append(SPACE);
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

    private void addWithClause(QueryCommand obj) {
        if (obj.getWith() != null) {
            append(WITH);
            append(SPACE);
            registerNodes(obj.getWith(), 0);
            beginClause(0);
        }
    }

    protected void visitCriteria(String keyWord, Criteria crit) {
        append(keyWord);
        append(SPACE);
        visitNode(crit);
    }

    @Override
    public void visit(SearchedCaseExpression obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(CASE);
        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(SPACE);
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenCriteria(i));
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i));
        }
        append(SPACE);
        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression());
            append(SPACE);
        }
        append(END);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Select obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (obj.isDistinct()) {
            append(SPACE);
            append(DISTINCT);
        }
        beginClause(2);

        Iterator<Expression> iter = obj.getSymbols().iterator();
        while (iter.hasNext()) {
            Expression symbol = iter.next();
            visitNode(symbol);
            if (iter.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }

        enableComments(obj);
        addComments(obj);
    }

    private void appendSourceHintValue(String sh) {
        append(Tokens.COLON);
        append('\'');
        append(escapeStringValue(sh, "'")); //$NON-NLS-1$
        append('\'');
        append(SPACE);
    }

    @Override
    public void visit(SetCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        // variable

        if (isTeiid8OrGreater())
            appendNested(obj.getExpression());
        else
            visitNode(obj.getExpression());

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
            visitNode(expr);
        } else if (size > 1) {
            Iterator iter = vals.iterator();
            Expression expr = (Expression)iter.next();
            visitNode(expr);
            while (iter.hasNext()) {
                expr = (Expression)iter.next();
                append(", "); //$NON-NLS-1$
                visitNode(expr);
            }
        }
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    /**
     * Condition operators have lower precedence than LIKE/SIMILAR/IS
     * @param ex
     */
    @Since(Version.TEIID_8_0)
    private void appendNested(Expression ex) {
        boolean useParens = ex instanceof Criteria;
        if (useParens) {
            append(Tokens.LPAREN);
        }
        visitNode(ex);
        if (useParens) {
            append(Tokens.RPAREN);
        }
    }

    @Override
    public void visit(SetQuery obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

    	addCacheHint(obj.getCacheHint());
        addWithClause(obj);
        QueryCommand query = obj.getLeftQuery();
        appendSetQuery(obj, query, false);

        beginClause(0);
        append(obj.getOperation());

        if (obj.isAll()) {
            append(SPACE);
            append(ALL);
        }
        beginClause(0);
        query = obj.getRightQuery();
        appendSetQuery(obj, query, true);

        if (obj.getOrderBy() != null) {
            beginClause(0);
            visitNode(obj.getOrderBy());
        }

        if (obj.getLimit() != null) {
            beginClause(0);
            visitNode(obj.getLimit());
        }

        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }

    protected void appendSetQuery(SetQuery parent, QueryCommand obj, boolean right) {
        if (obj.getLimit() != null
            || obj.getOrderBy() != null
            || (right && ((obj instanceof SetQuery && ((parent.isAll() && !((SetQuery)obj).isAll()) || parent.getOperation() != ((SetQuery)obj).getOperation()))))) {
            append(Tokens.LPAREN);
            visitNode(obj);
            append(Tokens.RPAREN);
        } else {
            visitNode(obj);
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(StoredProcedure obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

    	addCacheHint(obj.getCacheHint());
        if (obj.isCalledWithReturn()) {
            for (SPParameter param : obj.getParameters()) {
                if (param.getParameterType() == SPParameter.RETURN_VALUE) {
                    if (param.getExpression() == null) {
                        append("?"); //$NON-NLS-1$
                    } else {
                        visitNode(param.getExpression());
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
            visitNode(param.getExpression());
            if (addParens) {
                append(Tokens.RPAREN);
            }
        }
        append(")"); //$NON-NLS-1$

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }
    
    /**
     * @param obj
     */
    public void addCacheHint( CacheHint obj ) {
        if (obj == null) {
            return;
        }

        append(BEGIN_HINT);
        append(SPACE);
        append(CacheHint.CACHE);
        boolean addParens = false;
        if (obj.isPrefersMemory()) {
            append(Tokens.LPAREN);
            addParens = true;
            append(CacheHint.PREF_MEM);
        }
        if (obj.getTtl() != null) {
            if (!addParens) {
                append(Tokens.LPAREN);
                addParens = true;
            } else {
                append(SPACE);
            }
            append(CacheHint.TTL);
            append(obj.getTtl());
        }
        if (obj.getUpdatable() != null) {
            if (!addParens) {
                append(Tokens.LPAREN);
                addParens = true;
            } else {
                append(SPACE);
            }
            append(CacheHint.UPDATABLE);
        }
        if (obj.getScope() != null) {
            if (!addParens) {
                append(Tokens.LPAREN);
                addParens = true;
            } else {
                append(SPACE);
            }     
            append(CacheHint.SCOPE);
            append(obj.getScope());            
        }
        if (obj.getMinRows() != null && isTeiid811OrGreater()) {
            if (!addParens) {
                append(Tokens.LPAREN);
                addParens = true;
            } else {
                append(SPACE);
            }
            append(CacheHint.MIN);
            append(obj.getMinRows());
        }
        if (addParens) {
            append(Tokens.RPAREN);
        }
        append(SPACE);
        append(END_HINT);
        beginClause(0);
    }

    @Override
    public void visit(SubqueryFromClause obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        addHintComment(obj);
        if (obj.isTable()) {
            append(TABLE);
        }
        append("(");//$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")");//$NON-NLS-1$
        append(" AS ");//$NON-NLS-1$

        GroupSymbol groupSymbol = obj.getGroupSymbol();
        if (isTeiid8OrGreater())
            append(escapeSinglePart(groupSymbol.getOutputName()));
        else
            append(groupSymbol.getOutputName());

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(SubquerySetCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        // variable
        visitNode(obj.getExpression());

        // operator and beginning of list
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(IN);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(UnaryFromClause obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        addHintComment(obj);
        visitNode(obj.getGroup());

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Update obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        // Update clause
        append(UPDATE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        visitNode(obj.getGroup());
        beginClause(1);
        // Set clause
        append(SET);
        beginClause(2);
        visitNode(obj.getChangeList());

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Into obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup());

        enableComments(obj);
        addComments(obj);
    }

    // ############ Visitor methods for symbol objects ####################

    @Override
    public void visit(AggregateSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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
            registerNodes(obj.getArgs(), 0);
        } else {
            visitNode(obj.getExpression());
        }

        if (obj.getOrderBy() != null) {
            append(SPACE);
            visitNode(obj.getOrderBy());
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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(AliasSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getSymbol());
        append(SPACE);
        append(AS);
        append(SPACE);
        append(escapeSinglePart(obj.getOutputName()));

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(MultipleElementSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (obj.getGroup() == null) {
            append(Tokens.ALL_COLS);
        } else {
            visitNode(obj.getGroup());
            append(Tokens.DOT);
            append(Tokens.ALL_COLS);
        }

        enableComments(obj);
        addComments(obj);
    }

    private void visit7(Constant obj) {
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
			if (isTeiid87OrGreater() && value.getClass() == ArrayImpl.class) {
				ArrayImpl av = (ArrayImpl)value;
				append(Tokens.LPAREN);
				for (int i = 0; i < av.getValues().length; i++) {
					if (i > 0) {
						append(Tokens.COMMA);
						append(SPACE);
					}
					Object value2 = av.getValues()[i];
					outputLiteral(value2!=null?value2.getClass():av.getValues().getClass().getComponentType(), multiValued, value2);
				}

                // Added to support Teiid 8.9
				if (av.getValues().length == 1 && isTeiid89OrGreater()) {
				    append(Tokens.COMMA);
				}

				append(Tokens.RPAREN);
                return;

            } else if (type.isArray() && isTeiid89OrGreater()) { // Added to support Teiid 8.9
                append(Tokens.LPAREN);
                int length = java.lang.reflect.Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        append(Tokens.COMMA);
                        append(SPACE);
                    }
                    Object value2 = java.lang.reflect.Array.get(value, i);
                    outputLiteral(type.getComponentType(), multiValued, value2);
                }
                if (length == 1) {
                    append(Tokens.COMMA);
                }

                append(Tokens.RPAREN);
                return;
            }

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
                } else if (isTeiid8124OrGreater()) {
                    append('\'');
                    String strValue = value.toString();
                    for (int i = 0; i < strValue.length(); i++) {
                        char c = strValue.charAt(i);
                        if (c == '\'') {
                            parts.append('\'');
                        } else if (Character.isISOControl(c)) {
                            parts.append("\\u" + PropertiesUtils.toHex((c >> 12) & 0xF) + PropertiesUtils.toHex((c >>  8) & 0xF) //$NON-NLS-1$ 
                                    + PropertiesUtils.toHex((c >>  4) & 0xF) + PropertiesUtils.toHex(c & 0xF));
                            continue;
                        }
                        parts.append(c);
                    }
                    parts.append('\'');
                    return;
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

    private void visit8(Constant obj) {
        Class<?> type = obj.getType();
        boolean multiValued = obj.isMultiValued();
        Object value = obj.getValue();
        outputLiteral(type, multiValued, value);
    }

    @Override
    public void visit(Constant obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isTeiid8OrGreater())
            visit8(obj);
        else
            visit7(obj);

        enableComments(obj);
        addComments(obj);
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
    public void visit(ElementSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.SHORT_OUTPUT_NAME) ||isShortNameOnly()) {
            outputShortName(obj);

            enableComments(obj);
            addComments(obj);
            return;
        }
        String name = obj.getOutputName();
        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.FULLY_QUALIFIED)) {
            name = obj.getName();
        }
        outputDisplayName(name);

        enableComments(obj);
        addComments(obj);
    }

    private void outputShortName(ElementSymbol obj) {
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
    public void visit(ExpressionSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getExpression());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Function obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        String name = obj.getName();
        Expression[] args = obj.getArgs();
        if (obj.isImplicit()) {
            // Hide this function, which is implicit
            visitNode(args[0]);

        } else if (name.equalsIgnoreCase(CONVERT) || name.equalsIgnoreCase(CAST) || name.equalsIgnoreCase(XMLCAST)) {
            append(name);
            append("("); //$NON-NLS-1$

            if (args != null && args.length > 0) {
                visitNode(args[0]);

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
                    visitNode(args[i]);
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
                registerNodes(args, 1);
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equalsIgnoreCase(SourceSystemFunctions.XMLPI)) {
            append(name);
            append("(NAME "); //$NON-NLS-1$
            outputDisplayName((String)((Constant)args[0]).getValue());
            registerNodes(args, 1);
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
            registerNodes(args, 0);
            append(")"); //$NON-NLS-1$
        }

        enableComments(obj);
        addComments(obj);
    }

    private void registerNodes(LanguageObject[] objects, int begin) {
        registerNodes(Arrays.asList(objects), begin);
    }

    private void registerNodes(List<? extends LanguageObject> objects, int begin) {
        if (objects == null)
            return;

        for (int i = begin; i < objects.size(); i++) {
            if (i > 0) {
                append(", "); //$NON-NLS-1$
            }
            visitNode(objects.get(i));
        }
    }

    @Override
    public void visit(GroupSymbol obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Reference obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (!obj.isPositional() && obj.getExpression() != null) {
            visitNode(obj.getExpression());
        } else {
            append("?"); //$NON-NLS-1$
        }

        enableComments(obj);
        addComments(obj);
    }

    // ############ Visitor methods for storedprocedure language objects ####################

    private void visit7(Block obj) {
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
            visitNode(stmtIter.next());
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
        append(END);
    }

    private void addStatements(List<Statement> statements) {
        Iterator<Statement> stmtIter = statements.iterator();
        while (stmtIter.hasNext()) {
            // Add each statement
            addTabs(1);
            visitNode(stmtIter.next());
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
    }

    private void visit8(Block obj) {
        addLabel(obj);
        List<Statement> statements = obj.getStatements();
        // Add first clause
        append(BEGIN);
        if (obj.isAtomic()) {
            append(SPACE);
            append(ATOMIC);
        }
        append("\n"); //$NON-NLS-1$
        addStatements(statements);
        if (obj.getExceptionGroup() != null) {
            append(NonReserved.EXCEPTION);
            append(SPACE);
            outputDisplayName(obj.getExceptionGroup());
            append("\n"); //$NON-NLS-1$
            if (obj.getExceptionStatements() != null) {
                addStatements(obj.getExceptionStatements());
            }
        }
        append(END);
    }

    @Override
    public void visit(Block block) {
        // Turn off adding comments until the query is completely formed
        disableComments(block);

        if (isTeiid8OrGreater())
            visit8(block);
        else
            visit7(block);

        enableComments(block);
        addComments(block);
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
        // Do nothing
    }

    @Override
    public void visit(CommandStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getCommand());
        if (isTeiid8OrGreater() && !obj.isReturnable()) {
            append(SPACE);
            append(WITHOUT);
            append(SPACE);
            append(RETURN);
        }
        append(";"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    @Removed(Version.TEIID_8_0)
    public void visit(CreateUpdateProcedureCommand obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(CREATE);
        append(SPACE);
        if (!obj.isUpdateProcedure()) {
            append(VIRTUAL);
            append(SPACE);
        }
        append(PROCEDURE);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(CreateProcedureCommand obj) {
    	addCacheHint(obj.getCacheHint());
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isLessThanTeiidVersion(Version.TEIID_8_4)) {
            append(CREATE);
            append(SPACE);
            append(VIRTUAL);
            append(SPACE);
            append(PROCEDURE);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
        }
        visitNode(obj.getBlock());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(DeclareStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(DECLARE);
        append(SPACE);
        append(obj.getVariableType());
        append(SPACE);
        createAssignment(obj);

        enableComments(obj);
        addComments(obj);
    }

    /**
     * @param obj
     * @param parts
     */
    private void createAssignment(AssignmentStatement obj) {
        visitNode(obj.getVariable());
        if (obj.getExpression() != null) {
            append(" = "); //$NON-NLS-1$
            visitNode(obj.getExpression());
        }
        append(";"); //$NON-NLS-1$
        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(IfStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(IF);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition());
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getIfBlock());
        if (obj.hasElseBlock()) {
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(ELSE);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            visitNode(obj.getElseBlock());
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(AssignmentStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        createAssignment(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(RaiseStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(NonReserved.RAISE);
        append(SPACE);
        if (obj.isWarning()) {
            append(SQLWARNING);
            append(SPACE);
        }
        visitNode(obj.getExpression());
        append(";"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(HasCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(HAS);
        append(SPACE);
        visitNode(obj.getSelector());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(TranslateCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(TRANSLATE);
        append(SPACE);
        visitNode(obj.getSelector());

        if (obj.hasTranslations()) {
            append(SPACE);
            append(WITH);
            append(SPACE);
            append("("); //$NON-NLS-1$
            Iterator critIter = obj.getTranslations().iterator();

            while (critIter.hasNext()) {
                visitNode((Criteria)critIter.next());
                if (critIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
                if (!critIter.hasNext()) {
                    append(")"); //$NON-NLS-1$
                }
            }
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(CriteriaSelector obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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
                visitNode((ElementSymbol)elmtIter.next());
                if (elmtIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
            }
            append(")"); //$NON-NLS-1$
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(RaiseErrorStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(ERROR);
        append(SPACE);
        visitNode(obj.getExpression());
        append(";"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ExceptionExpression exceptionExpression) {
        // Turn off adding comments until the query is completely formed
        disableComments(exceptionExpression);

        append(SQLEXCEPTION);
        append(SPACE);
        visitNode(exceptionExpression.getMessage());
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

        enableComments(exceptionExpression);
        addComments(exceptionExpression);
    }

    @Override
    public void visit(ReturnStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(RETURN);
        if (obj.getExpression() != null) {
            append(SPACE);
            visitNode(obj.getExpression());
        }
        append(Tokens.SEMICOLON);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(BranchingStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(LoopStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        addLabel(obj);
        append(LOOP);
        append(" "); //$NON-NLS-1$
        append(ON);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(") "); //$NON-NLS-1$
        append(AS);
        append(" "); //$NON-NLS-1$
        if (isTeiid8OrGreater())
            outputDisplayName(obj.getCursorName());
        else
            append(obj.getCursorName());

        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(WhileStatement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        addLabel(obj);
        append(WHILE);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition());
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ExistsCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(EXISTS);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
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
    public void visit(SubqueryCompareCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression);

        String operator = obj.getOperatorAsString();
        String quantifier = obj.getPredicateQuantifierAsString();

        // operator and beginning of list
        append(SPACE);
        append(operator);
        append(SPACE);
        append(quantifier);

        if (isTeiid810OrGreater()) {
            addSubqueryHint(obj.getSubqueryHint());
            append(SPACE);
        }

        append("("); //$NON-NLS-1$

        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ScalarSubquery obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isTeiid810OrGreater()) {
            if (obj.getSubqueryHint().isDepJoin() || obj.getSubqueryHint().isMergeJoin() || obj.getSubqueryHint().isNoUnnest()) {
                if (this.parts.charAt(this.parts.length() - 1) == ' ') {
                    this.parts.setLength(this.parts.length() - 1);
                }
                addSubqueryHint(obj.getSubqueryHint());
                append(SPACE);
            }
        }

        // operator and beginning of list
        append("("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLAttributes obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(XMLATTRIBUTES);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLElement obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(XMLELEMENT);
        append("(NAME "); //$NON-NLS-1$
        outputDisplayName(obj.getName());
        if (obj.getNamespaces() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getNamespaces());
        }
        if (obj.getAttributes() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getAttributes());
        }
        if (!obj.getContent().isEmpty()) {
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getContent(), 0);
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLForest obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(XMLFOREST);
        append("("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(JSONObject obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(NonReserved.JSONOBJECT);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(TextLine obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(FOR);
        append(SPACE);
        registerNodes(obj.getExpressions(), 0);

        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()));
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            append(NonReserved.QUOTE);
            append(SPACE);
            visitNode(newConstant(obj.getQuote()));
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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLNamespaces obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(XMLNAMESPACES);
        append("("); //$NON-NLS-1$
        for (Iterator<NamespaceItem> items = obj.getNamespaceItems().iterator(); items.hasNext();) {
            NamespaceItem item = items.next();
            if (item.getPrefix() == null) {
                if (item.getUri() == null) {
                    append("NO DEFAULT"); //$NON-NLS-1$
                } else {
                    append("DEFAULT "); //$NON-NLS-1$
                    visitNode(newConstant(item.getUri()));
                }
            } else {
                visitNode(newConstant(item.getUri()));
                append(" AS "); //$NON-NLS-1$
                outputDisplayName(item.getPrefix());
            }
            if (items.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(Limit obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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
            visitNode(obj.getOffset());
            append(SPACE);
            append(ROWS);

            enableComments(obj);
            addComments(obj);
            return;
        }
        append(LIMIT);
        if (obj.getOffset() != null) {
            append(SPACE);
            visitNode(obj.getOffset());
            append(","); //$NON-NLS-1$
        }
        append(SPACE);
        visitNode(obj.getRowLimit());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(TextTable obj) {
        addHintComment(obj);
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append("TEXTTABLE("); //$NON-NLS-1$
        visitNode(obj.getFile());
        if (isTeiid8OrGreater() && obj.getSelector() != null) {
            append(SPACE);
            append(NonReserved.SELECTOR);
            append(SPACE);
            append(escapeSinglePart(obj.getSelector()));
        }
        append(SPACE);
        append(NonReserved.COLUMNS);
        boolean noTrim = obj.isNoTrim();
        for (Iterator<TextColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            TextColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            if (col.isOrdinal()) {
                // Will only ever come in here is Teiid 8.7 or greater
                append(FOR);
                append(SPACE);
                append(NonReserved.ORDINALITY);
            } else {
                if (col.getHeader() != null && isTeiid811OrGreater()) {
                    outputLiteral(String.class, false, col.getHeader());
                    append(SPACE);
                }
                append(col.getType());
                if (col.getWidth() != null) {
                    append(SPACE);
                    append(NonReserved.WIDTH);
                    append(SPACE);
                    append(col.getWidth());
                }
                if (!noTrim && col.isNoTrim()) {
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
        } else if (obj.getRowDelimiter() != null) {
            append(SPACE);
            append(ROW);
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);

            TeiidNodeFactory factory = new TeiidNodeFactory();
            Constant constant = factory.create(getTeiidParser(), ASTNodes.CONSTANT);
            constant.setValue(obj.getRowDelimiter());
            visitNode(constant);
        }

        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()));
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            if (obj.isEscape()) {
                append(ESCAPE);
            } else {
                append(NonReserved.QUOTE);
            }
            append(SPACE);
            visitNode(newConstant(obj.getQuote()));
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
        if (noTrim) {
            append(SPACE);
            append(NO);
            append(SPACE);
            append(NonReserved.TRIM);
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLTable obj) {
        addHintComment(obj);
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append("XMLTABLE("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
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
                        visitNode(col.getDefaultExpression());
                    }
                    if (col.getPath() != null) {
                        append(SPACE);
                        append(NonReserved.PATH);
                        append(SPACE);
                        visitNode(newConstant(col.getPath()));
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

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ObjectTable obj) {
        addHintComment(obj);
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append("OBJECTTABLE("); //$NON-NLS-1$
        if (obj.getScriptingLanguage() != null) {
            append(LANGUAGE);
            append(SPACE);
            visitNode(newConstant(obj.getScriptingLanguage()));
            append(SPACE);
        }
        visitNode(newConstant(obj.getRowScript()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
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
            visitNode(newConstant(col.getPath()));
            if (col.getDefaultExpression() != null) {
                append(SPACE);
                append(DEFAULT);
                append(SPACE);
                visitNode(col.getDefaultExpression());
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

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLQuery obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append("XMLQUERY("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
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

        enableComments(obj);
        addComments(obj);
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(XMLExists exists) {
        // Turn off adding comments until the query is completely formed
        disableComments(exists);

        append("XMLEXISTS("); //$NON-NLS-1$
        XMLQuery obj = exists.getXmlQuery();
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(","); //$NON-NLS-1$
            append(SPACE);
        }

        TeiidNodeFactory factory = new TeiidNodeFactory();
        Constant constant = factory.create(getTeiidParser(), ASTNodes.CONSTANT);
        constant.setValue(obj.getXquery());
        visitNode(constant);

        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
        }
        append(")");//$NON-NLS-1$

        enableComments(exists);
        addComments(exists);
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(XMLCast exists) {
        // Turn off adding comments until the query is completely formed
        disableComments(exists);

        append("XMLCAST("); //$NON-NLS-1$
        append(exists.getExpression());
        append(Tokens.SPACE);
        append(AS);
        append(Tokens.SPACE);
        append(getDataTypeManager().getDataTypeName(exists.getType()));
        append(")");//$NON-NLS-1$

        enableComments(exists);
        addComments(exists);
    }

    @Override
    public void visit(DerivedColumn obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getExpression());
        if (obj.getAlias() != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            outputDisplayName(obj.getAlias());
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLSerialize obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

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
        visitNode(obj.getExpression());
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

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(QueryString obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(NonReserved.QUERYSTRING);
        append("("); //$NON-NLS-1$
        visitNode(obj.getPath());
        if (!obj.getArgs().isEmpty()) {
            append(","); //$NON-NLS-1$
            append(SPACE);
            registerNodes(obj.getArgs(), 0);
        }
        append(")"); //$NON-NLS-1$

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(XMLParse obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(XMLPARSE);
        append(Tokens.LPAREN);
        if (obj.isDocument()) {
            append(NonReserved.DOCUMENT);
        } else {
            append(NonReserved.CONTENT);
        }
        append(SPACE);
        visitNode(obj.getExpression());
        if (obj.isWellFormed()) {
            append(SPACE);
            append(NonReserved.WELLFORMED);
        }
        append(Tokens.RPAREN);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ExpressionCriteria obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        visitNode(obj.getExpression());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(TriggerAction obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(FOR);
        append(SPACE);
        append(EACH);
        append(SPACE);
        append(ROW);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(ArrayTable obj) {
        addHintComment(obj);
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append("ARRAYTABLE("); //$NON-NLS-1$
        visitNode(obj.getArrayValue());
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

        if (isLessThanTeiid8124())
            addMakeDep(obj);

        enableComments(obj);
        addComments(obj);
    }

    private void addMakeDep(FromClause obj) {
        if (isLessThanTeiidVersion(Version.TEIID_8_5))
            return;

        MakeDep makeDep = obj.getMakeDep();
        if (makeDep != null && !makeDep.isSimple()) {
            append(SPACE);
            append(MAKEDEP);
            appendMakeDepOptions(makeDep);
        }

        if (isLessThanTeiid8124())
            return;

        makeDep = obj.getMakeInd();
        if (makeDep != null && !makeDep.isSimple()) {
            append(SPACE);
            append(MAKEIND);
            appendMakeDepOptions(makeDep);
        }
    }

    private void visit7(AlterProcedure<CreateUpdateProcedureCommand> obj) {
        append(ALTER);
        append(SPACE);
        append(PROCEDURE);
        append(SPACE);
        append(obj.getTarget());
        beginClause(1);
        append(AS);
        append(obj.getDefinition().getBlock());
    }

    private void visit8(AlterProcedure<CreateProcedureCommand> obj) {
        append(ALTER);
        append(SPACE);
        append(PROCEDURE);
        append(SPACE);
        append(obj.getTarget());
        beginClause(1);
        append(AS);
        append(obj.getDefinition().getBlock());
    }

    @Override
    public void visit(AlterProcedure obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (isTeiid8OrGreater())
            visit8(obj);
        else
            visit7(obj);

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(AlterTrigger obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        if (obj.isCreate()) {
            append(CREATE);
        } else {
            append(ALTER);
        }
        append(SPACE);
        append(TRIGGER);
        append(SPACE);
        append(ON);
        append(SPACE);
        append(obj.getTarget());
        beginClause(0);
        append(NonReserved.INSTEAD);
        append(SPACE);
        append(OF);
        append(SPACE);
        append(obj.getEvent());
        if (obj.getDefinition() != null) {
            beginClause(0);
            append(AS);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(obj.getDefinition());
        } else {
            append(SPACE);
            append(obj.getEnabled() ? NonReserved.ENABLED : NonReserved.DISABLED);
        }

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(AlterView obj) {
        // Turn off adding comments until the query is completely formed
        disableComments(obj);

        append(ALTER);
        append(SPACE);
        append(NonReserved.VIEW);
        append(SPACE);
        append(obj.getTarget());
        beginClause(0);
        append(AS);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        append(obj.getDefinition());

        enableComments(obj);
        addComments(obj);
    }

    @Override
    public void visit(WindowFunction windowFunction) {
        // Turn off adding comments until the query is completely formed
        disableComments(windowFunction);

        append(windowFunction.getFunction());
        append(SPACE);
        append(OVER);
        append(SPACE);
        append(windowFunction.getWindowSpecification());

        enableComments(windowFunction);
        addComments(windowFunction);
    }

    @Override
    public void visit(WindowSpecification windowSpecification) {
        // Turn off adding comments until the query is completely formed
        disableComments(windowSpecification);

        append(Tokens.LPAREN);
        boolean needsSpace = false;
        if (windowSpecification.getPartition() != null) {
            append(PARTITION);
            append(SPACE);
            append(BY);
            append(SPACE);
            registerNodes(windowSpecification.getPartition(), 0);
            needsSpace = true;
        }
        if (windowSpecification.getOrderBy() != null) {
            if (needsSpace) {
                append(SPACE);
            }
            append(windowSpecification.getOrderBy());
        }
        append(Tokens.RPAREN);

        enableComments(windowSpecification);
        addComments(windowSpecification);
    }

    @Override
    public void visit(Array array) {
        // Turn off adding comments until the query is completely formed
        disableComments(array);

        if (!array.isImplicit()) {
            append(Tokens.LPAREN);
        }
        registerNodes(array.getExpressions(), 0);
        if (!array.isImplicit()) {
    		if (array.getExpressions().size() == 1) {
    			append(Tokens.COMMA);
    		}
            append(Tokens.RPAREN);
        }

        enableComments(array);
        addComments(array);
    }

    @Override
    @Since(Version.TEIID_8_12_4)
    public void visit(IsDistinctCriteria isDistinctCriteria) {
        append(isDistinctCriteria.getLeftRowValue());
        append(SPACE);
        append(IS);
        append(SPACE);
        if (isDistinctCriteria.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(DISTINCT);
        append(SPACE);
        append(FROM);
        append(SPACE);
        append(isDistinctCriteria.getRightRowValue());
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
        return SQLConstants.isReservedWord(getTeiidVersion(), string);
    }

}

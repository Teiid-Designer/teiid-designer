/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.lang.IComment;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IWindowFunction;
import org.teiid.designer.query.sql.symbol.IWindowSpecification;

/**
 * The <code>DisplayNodeFactory</code> class is the Factory used to create all different types of DisplayNodes.
 *
 * @since 8.0
 */
public class DisplayNodeFactory {

    private static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private static final String[] SEPARATORS = new String[11];

    static {
        SEPARATORS[0] = " "; //$NON-NLS-1$
        SEPARATORS[1] = " \n"; //$NON-NLS-1$
        SEPARATORS[2] = "\n"; //$NON-NLS-1$
        SEPARATORS[3] = " \t"; //$NON-NLS-1$
        SEPARATORS[4] = "\t"; //$NON-NLS-1$
        SEPARATORS[5] = ", "; //$NON-NLS-1$
        SEPARATORS[6] = ","; //$NON-NLS-1$
        SEPARATORS[7] = "("; //$NON-NLS-1$
        SEPARATORS[8] = ")"; //$NON-NLS-1$
        SEPARATORS[9] = " ("; //$NON-NLS-1$
        SEPARATORS[10] = ") "; //$NON-NLS-1$
    }

    private static final List SEPARATOR_WORDS = Arrays.asList(SEPARATORS);

    // ************************************************
    // Public Methods
    // ************************************************
    static <T extends DisplayNode> T createDisplayNode( DisplayNode parentNode, Object obj, int indentLevel ) {
        final T node = constructDisplayNode(parentNode, obj);
        final boolean dontAppend = obj instanceof IGroupSymbol || obj instanceof IMultipleElementSymbol
                                   || obj instanceof IElementSymbol;
        if (obj instanceof ILanguageObject) {
            DisplayNodeVisitor ssv = new DisplayNodeVisitor(node, dontAppend, indentLevel);
            IQueryService queryService = ModelerCore.getTeiidQueryService();
            ISQLStringVisitor callbackSQLStringVisitor = queryService.getCallbackSQLStringVisitor(ssv);
            ((ILanguageObject)obj).acceptVisitor(callbackSQLStringVisitor);

            /*
             * Fetch and stash any comments from the parser.
             * Only places them in the root node of the Display tree
             */
            if (parentNode == null) {
                Set<IComment> comments = ((ILanguageObject)obj).getComments();
                for (IComment comment : comments) {
                    CommentDisplayNode commentNode = createDisplayNode(node, comment);
                    node.addCommentNode(commentNode);
                }
            }
        }
        return node;
    }

    /**
     * @param parentNode
     * @param obj
     * @return new display node
     */
    public static <T extends DisplayNode> T createDisplayNode( DisplayNode parentNode, Object obj ) {
        return createDisplayNode(parentNode, obj, 0);
    }

    static <T extends DisplayNode> T constructDisplayNode( DisplayNode parentNode, Object obj) {
        // ---------------------------------------------------------------------
        // Commands
        // ---------------------------------------------------------------------
        if (obj instanceof IQuery) {
            QueryDisplayNode node = new QueryDisplayNode(parentNode, (IQuery)obj);
            return (T) node;
        } else if (obj instanceof ISetQuery) {
            SetQueryDisplayNode node = new SetQueryDisplayNode(parentNode, (ISetQuery)obj);
            return (T) node;
        } else if (obj instanceof IInsert) {
            InsertDisplayNode node = new InsertDisplayNode(parentNode, (IInsert)obj);
            return (T) node;
        } else if (obj instanceof IUpdate) {
            UpdateDisplayNode node = new UpdateDisplayNode(parentNode, (IUpdate)obj);
            return (T) node;
        } else if (obj instanceof IDelete) {
            DeleteDisplayNode node = new DeleteDisplayNode(parentNode, (IDelete)obj);
            return (T) node;
        } else if (obj instanceof ISelect) {
            SelectDisplayNode node = new SelectDisplayNode(parentNode, (ISelect)obj);
            return (T) node;
        } else if (obj instanceof IFrom) {
            FromDisplayNode node = new FromDisplayNode(parentNode, (IFrom)obj);
            return (T) node;
        } else if (obj instanceof IGroupBy) {
            GroupByDisplayNode node = new GroupByDisplayNode(parentNode, (IGroupBy)obj);
            return (T) node;
        } else if (obj instanceof IOrderBy) {
            OrderByDisplayNode node = new OrderByDisplayNode(parentNode, (IOrderBy)obj);
            return (T) node;
        } else if( parentNode instanceof FunctionDisplayNode && obj instanceof IConstant && ((IConstant)obj).getValue() != null ) {
        	return (T)  new TextDisplayNode(parentNode, obj.toString());
        } else if (obj instanceof String) {
            // ---------------------------------------------------------------------
            // Keywords, Separators, Unknown Strings
            // ---------------------------------------------------------------------
            String text = (String)obj;
            if (SEPARATOR_WORDS.contains(text)) {
                return (T)  new SeparatorDisplayNode(parentNode, text);
            }
            return (T) new TextDisplayNode(parentNode, text);
            // ---------------------------------------------------------------------
            // FromClause Parts
            // ---------------------------------------------------------------------
        } else if (obj instanceof ICompareCriteria) {
            CompareCriteriaDisplayNode node = new CompareCriteriaDisplayNode(parentNode, (ICompareCriteria)obj);
            return (T) node;
        } else if (obj instanceof IMatchCriteria) {
            MatchCriteriaDisplayNode node = new MatchCriteriaDisplayNode(parentNode, (IMatchCriteria)obj);
            return (T) node;
        } else if (obj instanceof ISetCriteria) {
            SetCriteriaDisplayNode node = new SetCriteriaDisplayNode(parentNode, (ISetCriteria)obj);
            return (T) node;
        } else if (obj instanceof ICompoundCriteria) {
            CompoundCriteriaDisplayNode node = new CompoundCriteriaDisplayNode(parentNode, (ICompoundCriteria)obj);
            return (T) node;
        } else if (obj instanceof IFunction) {
             // ---------------------------------------------------------------------
            // Constant, Function, Expression Nodes
            // ---------------------------------------------------------------------
            FunctionDisplayNode node = new FunctionDisplayNode(parentNode, (IFunction)obj);
            return (T) node;
        } else if (obj instanceof IWindowFunction || obj instanceof IWindowSpecification ) {
            TextDisplayNode node = new TextDisplayNode(parentNode, obj.toString());
            return (T) node;
        } else if (obj instanceof IAliasSymbol) {
            AliasSymbolDisplayNode node = new AliasSymbolDisplayNode(parentNode, (IAliasSymbol)obj);
            return (T) node;
        } else if (obj instanceof IElementSymbol) {
            ElementSymbolDisplayNode node = new ElementSymbolDisplayNode(parentNode, (IElementSymbol)obj);
            return (T) node;
        } else if (obj instanceof IGroupSymbol) {
            GroupSymbolDisplayNode node = new GroupSymbolDisplayNode(parentNode, (IGroupSymbol)obj);
            return (T) node;
        } else if (obj instanceof IMultipleElementSymbol) {
            SymbolDisplayNode node = new SymbolDisplayNode(parentNode, (IMultipleElementSymbol)obj);
            return (T) node;
        } else if (obj instanceof ILanguageObject) {
            DisplayNode node = new DisplayNode();
            node.parentNode = parentNode;
            node.languageObject = (ILanguageObject)obj;
            return (T) node;
        } else if (obj instanceof IComment) {
            CommentDisplayNode node = new CommentDisplayNode(parentNode, (IComment) obj);
            return (T) node;
        } else {
            String unknownText = UNDEFINED;
            if (obj != null) {
                String objText = obj.toString();
                if (objText.trim().length() > 0) {
                    unknownText = objText;
                }
            }
            UnknownDisplayNode node = new UnknownDisplayNode(parentNode, unknownText);
            return (T) node;
        }
    }

    /**
     * @param parentNode
     * @param name
     * @return unknown display node
     */
    public static DisplayNode createUnknownQueryDisplayNode( DisplayNode parentNode,
                                                             String name ) {
        return new UnknownQueryDisplayNode(parentNode, name);
    }

}

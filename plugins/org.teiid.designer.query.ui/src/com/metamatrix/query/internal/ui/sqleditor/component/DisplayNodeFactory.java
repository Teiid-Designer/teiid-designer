/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.Arrays;
import java.util.List;
import org.teiid.language.SQLConstants;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.ExpressionStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.AllInGroupSymbol;
import org.teiid.query.sql.symbol.AllSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;

/**
 * The <code>DisplayNodeFactory</code> class is the Factory used to create all different types of DisplayNodes.
 */
public class DisplayNodeFactory {

    /**
     * This visitor works intimately with the SQLStringVisitor to construct a display node tree.
     */
    private static final class DisplayNodeVisitor extends SQLStringVisitor {
        private final DisplayNode node;
        private final boolean dontAppend;
        private int indentLevel;
        private int originalLevel;

        DisplayNodeVisitor( DisplayNode node,
                            boolean dontAppend,
                            int indentLevel ) {
            this.node = node;
            this.dontAppend = dontAppend;
            this.indentLevel = indentLevel;
            this.originalLevel = indentLevel;
        }

        @Override
        protected void visitNode( LanguageObject obj ) {
            if (obj == null || (obj instanceof ExpressionSymbol && !(obj instanceof AggregateSymbol))) {
                super.visitNode(obj);
                return;
            }
            // turn off indenting for nested commands
            int childIndent = indentLevel;
            if ((node.languageObject instanceof SubqueryContainer || node.languageObject instanceof ExpressionStatement)
                && obj instanceof Command) {
                childIndent = -1;
            }
            DisplayNode child = createDisplayNode(node, obj, childIndent);
            node.addChildNode(child);
        }

        @Override
        protected void addTabs( int level ) {
            setIndentLevel(this.originalLevel + level);
            if (this.indentLevel > 0) {
                node.displayNodeList.addAll(DisplayNodeUtils.getIndentNodes(node, this.indentLevel));
            }
        }

        /*
         * Allows for the creation of having/where nodes
         * even though there isn't a direct language representation
         */
        @Override
        protected void visitCriteria( String keyWord,
                                      Criteria crit ) {
            if (SQLConstants.Reserved.WHERE.equals(keyWord)) {
                DisplayNode child = new WhereDisplayNode(node, crit);
                createCriteriaNode(keyWord, crit, child);
            } else if (SQLConstants.Reserved.HAVING.equals(keyWord)) {
                DisplayNode child = new HavingDisplayNode(node, crit);
                createCriteriaNode(keyWord, crit, child);
            } else {
                super.visitCriteria(keyWord, crit);
            }
        }

        private void createCriteriaNode( String keyWord,
                                         Criteria crit,
                                         DisplayNode child ) {
            node.addChildNode(child);
            child.displayNodeList.add(DisplayNodeFactory.createDisplayNode(child, keyWord));
            setIndentLevel(this.indentLevel + 1);
            beginClause(child, this.indentLevel);
            child.addChildNode(createDisplayNode(child, crit, indentLevel));
        }

        @Override
        protected void append( Object value ) {
            if (dontAppend) {
                return; // for compatibility, this keeps symbols from having children
            }
            // otherwise, the value is a string/enum/primitive
            node.displayNodeList.add(constructDisplayNode(node, value));
        }

        @Override
        protected void beginClause( int level ) {
            setIndentLevel(this.originalLevel + level);
            beginClause(node, this.indentLevel);
        }

        static void beginClause( DisplayNode node,
                                 int level ) {
            if (level >= 0 && DisplayNodeUtils.isClauseCROn()) {
                node.displayNodeList.add(DisplayNodeFactory.createDisplayNode(node, DisplayNodeConstants.CR));
                if (DisplayNodeUtils.isClauseIndentOn()) {
                    node.displayNodeList.addAll(DisplayNodeUtils.getIndentNodes(node, level));
                }
            } else {
                node.displayNodeList.add(DisplayNodeFactory.createDisplayNode(node, DisplayNodeConstants.SPACE));
            }
        }

        private void setIndentLevel( int indentLevel ) {
            // preserve -1, which means no indenting
            if (this.indentLevel != -1) {
                this.indentLevel = indentLevel;
            }
        }

    }

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
    static DisplayNode createDisplayNode( DisplayNode parentNode,
                                          Object obj,
                                          int indentLevel ) {
        final DisplayNode node = constructDisplayNode(parentNode, obj);
        final boolean dontAppend = obj instanceof GroupSymbol || obj instanceof MultipleElementSymbol
                                   || obj instanceof ElementSymbol;
        if (obj instanceof LanguageObject) {
            DisplayNodeVisitor ssv = new DisplayNodeVisitor(node, dontAppend, indentLevel);
            ((LanguageObject)obj).acceptVisitor(ssv);
        }
        return node;
    }

    public static DisplayNode createDisplayNode( DisplayNode parentNode,
                                                 Object obj ) {
        return createDisplayNode(parentNode, obj, 0);
    }

    static DisplayNode constructDisplayNode( DisplayNode parentNode,
                                             Object obj ) {
        // ---------------------------------------------------------------------
        // Commands
        // ---------------------------------------------------------------------
        if (obj instanceof Query) {
            QueryDisplayNode node = new QueryDisplayNode(parentNode, (Query)obj);
            return node;
        } else if (obj instanceof SetQuery) {
            SetQueryDisplayNode node = new SetQueryDisplayNode(parentNode, (SetQuery)obj);
            return node;
        } else if (obj instanceof Insert) {
            InsertDisplayNode node = new InsertDisplayNode(parentNode, (Insert)obj);
            return node;
        } else if (obj instanceof Update) {
            UpdateDisplayNode node = new UpdateDisplayNode(parentNode, (Update)obj);
            return node;
        } else if (obj instanceof Delete) {
            DeleteDisplayNode node = new DeleteDisplayNode(parentNode, (Delete)obj);
            return node;
        } else if (obj instanceof Select) {
            SelectDisplayNode node = new SelectDisplayNode(parentNode, (Select)obj);
            return node;
        } else if (obj instanceof From) {
            FromDisplayNode node = new FromDisplayNode(parentNode, (From)obj);
            return node;
        } else if (obj instanceof GroupBy) {
            GroupByDisplayNode node = new GroupByDisplayNode(parentNode, (GroupBy)obj);
            return node;
        } else if (obj instanceof OrderBy) {
            OrderByDisplayNode node = new OrderByDisplayNode(parentNode, (OrderBy)obj);
            return node;
        } else if (obj instanceof String) {
            // ---------------------------------------------------------------------
            // Keywords, Separators, Unknown Strings
            // ---------------------------------------------------------------------
            String text = (String)obj;
            if (SEPARATOR_WORDS.contains(text)) {
                return new SeparatorDisplayNode(parentNode, text);
            }
            return new TextDisplayNode(parentNode, text);
            // ---------------------------------------------------------------------
            // FromClause Parts
            // ---------------------------------------------------------------------
        } else if (obj instanceof CompareCriteria) {
            CompareCriteriaDisplayNode node = new CompareCriteriaDisplayNode(parentNode, (CompareCriteria)obj);
            return node;
        } else if (obj instanceof MatchCriteria) {
            MatchCriteriaDisplayNode node = new MatchCriteriaDisplayNode(parentNode, (MatchCriteria)obj);
            return node;
        } else if (obj instanceof SetCriteria) {
            SetCriteriaDisplayNode node = new SetCriteriaDisplayNode(parentNode, (SetCriteria)obj);
            return node;
        } else if (obj instanceof CompoundCriteria) {
            CompoundCriteriaDisplayNode node = new CompoundCriteriaDisplayNode(parentNode, (CompoundCriteria)obj);
            return node;
        } else if (obj instanceof Function) {
             // ---------------------------------------------------------------------
            // Constant, Function, Expression Nodes
            // ---------------------------------------------------------------------
            FunctionDisplayNode node = new FunctionDisplayNode(parentNode, (Function)obj);
            return node;
        } else if (obj instanceof AliasSymbol) {
            AliasSymbolDisplayNode node = new AliasSymbolDisplayNode(parentNode, (AliasSymbol)obj);
            return node;
        } else if (obj instanceof ElementSymbol) {
            ElementSymbolDisplayNode node = new ElementSymbolDisplayNode(parentNode, (ElementSymbol)obj);
            return node;
        } else if (obj instanceof GroupSymbol) {
            GroupSymbolDisplayNode node = new GroupSymbolDisplayNode(parentNode, (GroupSymbol)obj);
            return node;
        } else if (obj instanceof AllSymbol) {
            SymbolDisplayNode node = new SymbolDisplayNode(parentNode, (AllSymbol)obj);
            return node;
        } else if (obj instanceof AllInGroupSymbol) {
            SymbolDisplayNode node = new SymbolDisplayNode(parentNode, (AllInGroupSymbol)obj);
            return node;
        } else if (obj instanceof LanguageObject) {
            DisplayNode node = new DisplayNode();
            node.parentNode = parentNode;
            node.languageObject = (LanguageObject)obj;
            return node;
        } else {
            String unknownText = UNDEFINED;
            if (obj != null) {
                String objText = obj.toString();
                if (objText.trim().length() > 0) {
                    unknownText = objText;
                }
            }
            UnknownDisplayNode node = new UnknownDisplayNode(parentNode, unknownText);
            return node;
        }
    }

    public static DisplayNode createUnknownQueryDisplayNode( DisplayNode parentNode,
                                                             String name ) {
        return new UnknownQueryDisplayNode(parentNode, name);
    }

}

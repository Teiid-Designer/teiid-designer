/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.Symbol;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;

/**
 * The <code>DisplayNodeUtils</code> class contains static methods that are useful in working with DisplayNodes.
 */
public final class DisplayNodeUtils implements DisplayNodeConstants {

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Method to determine whether the specified index immediately follows a comma, ignoring spaces.
     * 
     * @param displayNodes the list of display nodes to check
     * @param index the index to test
     * @return true if the index immediately follows a comma, false if not.
     */
    public static boolean isIndexRightAfterComma( List displayNodes,
                                                  int index ) {
        // Get DisplayNodes before the specified index
        List leadingNodes = getDisplayNodesBeforeIndex(displayNodes, index);

        int nLeading = leadingNodes.size();
        for (int i = nLeading - 1; i >= 0; i--) {
            DisplayNode node = (DisplayNode)leadingNodes.get(i);
            if (node instanceof SeparatorDisplayNode) {
                String nodeStr = node.toString();
                if (nodeStr.indexOf(COMMA) != -1) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Method to determine whether the specified index immediately preceeds a comma, ignoring spaces.
     * 
     * @param displayNodes the list of display nodes to check
     * @param index the index to test
     * @return true if the index is immediately before a comma, false if not.
     */
    public static boolean isIndexRightBeforeComma( List displayNodes,
                                                   int index ) {
        // Get DisplayNodes after the specified index
        List trailingNodes = getDisplayNodesAfterIndex(displayNodes, index);

        // If any of the trailing nodes are not a Separator, not at end
        Iterator iter = trailingNodes.iterator();
        while (iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if (node instanceof SeparatorDisplayNode) {
                String nodeStr = node.toString();
                if (nodeStr.indexOf(COMMA) != -1) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns a list of DisplayNodes (potentially 2) at a given index. Returns an empty list if the index is out of range.
     * 
     * @param displayNodes the list of display nodes to check
     * @param index the index to test
     * @return the List of nodes that the Index is within
     */
    public static List getDisplayNodesAtIndex( List displayNodes,
                                               int index ) {
        ArrayList validNodes = new ArrayList(0);

        Iterator iter = displayNodes.iterator();
        while (iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if (node.isAnywhereWithin(index)) {
                validNodes.add(node);
            }
        }

        return validNodes;
    }

    /**
     * Returns a list of DisplayNodes before the display Node that the arg index is within.
     * 
     * @param displayNodes the list of displayNodes
     * @param the index
     * @return the list of displayNodes before the index
     */
    public static List getDisplayNodesBeforeIndex( List displayNodes,
                                                   int index ) {
        List validNodes = new ArrayList(0);
        Iterator dnIter = displayNodes.iterator();
        while (dnIter.hasNext()) {
            DisplayNode node = (DisplayNode)dnIter.next();
            int nodeEndIndex = node.getEndIndex();
            if (nodeEndIndex < index) {
                validNodes.add(node);
            } else {
                break;
            }
        }
        return validNodes;
    }

    /**
     * Returns a list of DisplayNodes after the display Node that the arg index is within.
     * 
     * @param displayNodes the list of displayNodes
     * @param the index
     * @return the list of displayNodes after the index
     */
    public static List getDisplayNodesAfterIndex( List displayNodes,
                                                  int index ) {
        List validNodes = new ArrayList(0);
        Iterator dnIter = displayNodes.iterator();
        while (dnIter.hasNext()) {
            DisplayNode node = (DisplayNode)dnIter.next();
            int nodeStartIndex = node.getStartIndex();
            if (nodeStartIndex >= index) {
                validNodes.add(node);
            }
        }
        return validNodes;
    }

    /**
     * Method to check whether the supplied node has any SymbolDisplayNodes in it.
     * 
     * @param node the query display node to check
     * @return true if the node has at least one symbol, false if not.
     */
    public static boolean hasSymbol( DisplayNode node ) {
        if (node == null) {
            return false;
        }

        if (isSymbolNode(node)) {
            return true;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (isSymbolNode(displayNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check whether the supplied node has any ExpressionDisplayNodes in it.
     * 
     * @param node the query display node to check
     * @return true if the node has at least one expression, false if not.
     */
    public static boolean hasExpression( DisplayNode node ) {
        if (node.languageObject instanceof Expression) {
            return true;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (getExpressionForNode(displayNode) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the starting index of the next SymbolDisplayNode following the supplied index. Returns -1 if there is no
     * SymbolDisplayNode following the index.
     * 
     * @param node the query display node to check
     * @param index the index to test
     * @return the starting index of the next display node, -1 if there isnt any.
     */
    public static int getStartIndexOfNextSymbol( DisplayNode node,
                                                 int index ) {
        if (isSymbolNode(node)) {
            if (node.isAnywhereWithin(index)) {
                return node.getStartIndex();
            }
            return -1;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (isSymbolNode(displayNode)) {
                if (displayNode.getStartIndex() >= index) {
                    return displayNode.getStartIndex();
                }
            }
        }
        return -1;
    }

    /**
     * Method to get the ending index of the SymbolDisplayNode preceding the supplied index. Returns -1 if there is no
     * SymbolDisplayNode preceding the index.
     * 
     * @param node the query display node to check
     * @param index the index to test
     * @return the ending index of the preceding display node, -1 if there isnt any.
     */
    public static int getEndIndexOfPreviousSymbol( DisplayNode node,
                                                   int index ) {
        int prevEnd = -1;
        if (isSymbolNode(node)) {
            if (node.isAnywhereWithin(index)) {
                return node.getEndIndex() + 1;
            }
            return prevEnd;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (displayNode.getEndIndex() >= index) {
                break;
            }
            if (isSymbolNode(displayNode)) {
                prevEnd = displayNode.getEndIndex() + 1;
            }
        }
        return prevEnd;
    }

    /**
     * Method to get the starting index of the next ExpressionDisplayNode following the supplied index. Returns -1 if there is no
     * ExpressionDisplayNode following the index.
     * 
     * @param node the query display node to check
     * @param index the index to test
     * @return the starting index of the next expression display node, -1 if there isnt any.
     */
    public static int getStartIndexOfNextExpression( DisplayNode node,
                                                     int index ) {
        if (node.languageObject instanceof Expression) {
            if (node.isAnywhereWithin(index)) {
                return node.getStartIndex();
            }
            return -1;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (displayNode.isInExpression()) {
                if (displayNode.getStartIndex() >= index) {
                    return displayNode.getStartIndex();
                }
            }
        }
        return -1;
    }

    /**
     * Method to get the ending index of the ExpressionDisplayNode preceding the supplied index. Returns -1 if there is no
     * ExpressionDisplayNode preceding the index.
     * 
     * @param node the query display node to check
     * @param index the index to test
     * @return the ending index of the preceding expression display node, -1 if there isnt any.
     */
    public static int getEndIndexOfPreviousExpression( DisplayNode node,
                                                       int index ) {
        int prevEnd = -1;
        if (node.languageObject instanceof Expression) {
            if (node.isAnywhereWithin(index)) {
                return node.getEndIndex() + 1;
            }
            return prevEnd;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if (displayNode.getEndIndex() >= index) {
                break;
            }
            if (displayNode.isInExpression()) {
                prevEnd = displayNode.getEndIndex() + 1;
            }
        }
        return prevEnd;
    }

    /**
     * Method to determine whether the index is at the "start" of a clause. By start, meaning that the index can only be preceeded
     * by Keywords and non-comma separators.
     * 
     * @param the clause to test
     * @param index the index to test
     * @return true if the index is at the beginning of a clause, false if not.
     */
    public static boolean isIndexAtClauseStart( DisplayNode clauseNode,
                                                int index ) {
        if (clauseNode != null && isClauseNode(clauseNode)) {
            // Get Clause DisplayNodes before the specified index
            List displayNodes = clauseNode.getDisplayNodeList();
            List leadingNodes = getDisplayNodesBeforeIndex(displayNodes, index);

            Iterator iter = leadingNodes.iterator();
            while (iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if (node.languageObject != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method to determine whether the index is at the end of a clause.
     * 
     * @param clauseNode the clause display Node to test
     * @param index the index to test
     * @return true if the index is at the end of a clause, false if not.
     */
    public static boolean isIndexAtClauseEnd( DisplayNode clauseNode,
                                              int index ) {
        if (clauseNode != null && isClauseNode(clauseNode)) {
            // Get Clause DisplayNodes after the specified index
            List displayNodes = clauseNode.getDisplayNodeList();
            List trailingNodes = getDisplayNodesAfterIndex(displayNodes, index);

            // If any of the trailing nodes are not a Separator, not at end
            Iterator iter = trailingNodes.iterator();
            while (iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if (!(node instanceof SeparatorDisplayNode)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is a Clause.
     * 
     * @param clauseNode the clause display Node to test
     * @return true if the node is a clause, false if not.
     */
    public static boolean isClauseNode( DisplayNode clauseNode ) {
        if (clauseNode instanceof SelectDisplayNode) {
            return true;
        } else if (clauseNode.languageObject instanceof From) {
            return true;
        } else if (clauseNode instanceof WhereDisplayNode) {
            return true;
        } else if (clauseNode.languageObject instanceof GroupBy) {
            return true;
        } else if (clauseNode instanceof HavingDisplayNode) {
            return true;
        } else if (clauseNode.languageObject instanceof OrderBy) {
            return true;
        } else if (clauseNode.languageObject instanceof Option) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to determine whether a DisplayNode is a Command.
     * 
     * @param node the display Node to test
     * @return true if the node is a command, false if not.
     */
    public static boolean isCommandNode( DisplayNode node ) {
        if (node.getLanguageObject() instanceof Query) {
            return true;
        } else if (node.languageObject instanceof SetQuery) {
            return true;
        } else if (node.languageObject instanceof Update) {
            return true;
        } else if (node.languageObject instanceof Insert) {
            return true;
        } else if (node.languageObject instanceof Delete) {
            return true;
        } else if (node.languageObject instanceof StoredProcedure) {
            return true;
        } else if (node.languageObject instanceof CreateUpdateProcedureCommand) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to determine whether a specified type can be inserted before the specified DisplayNode.
     * 
     * @param node the display Node to test
     * @param type the type
     * @return true if the type can be inserted before the specified displayNode, false if not.
     */
    public static boolean canInsertBefore( DisplayNode node,
                                           int type ) {
        return canInsertNextTo(node, type);
    }

    /**
     * Method to determine whether a specified type can be inserted after the specified DisplayNode.
     * 
     * @param node the display Node to test
     * @param type the type
     * @return true if the type can be inserted after the specified displayNode, false if not.
     */
    public static boolean canInsertAfter( DisplayNode node,
                                          int type ) {
        return canInsertNextTo(node, type);
    }

    /**
     * Method to determine whether a specified type can be inserted next to the specified DisplayNode.
     * 
     * @param node the display Node to test
     * @param type the type
     * @return true if the type can be inserted next to the specified displayNode, false if not.
     */
    private static boolean canInsertNextTo( DisplayNode node,
                                            int type ) {
        boolean result = false;
        if (type == EXPRESSION) {
            // If node is an expression or within an expression, cant insert before it
            // Must use the builder
//            if (!node.isInExpression()) {
                // Get the clause that this node is in
                DisplayNode clauseNode = getNodeTypeForNode(node, CLAUSE);
                if (clauseNode != null) {
                    if (clauseNode.supportsExpression()) {
                        result = true;
                    }
                }
//            }
        } else if (type == CRITERIA) {
            // If node is a criteria or within a criteria, cant insert before it
            // Must use the builder
            if (!node.isInCriteria()) {
                // Get the command that this node is in
                DisplayNode commandNode = getNodeTypeForNode(node, COMMAND);
                if (commandNode instanceof QueryDisplayNode) {
                    QueryDisplayNode queryNode = (QueryDisplayNode)commandNode;
                    DisplayNode whereClause = queryNode.getClauseDisplayNode(WHERE);
                    if (whereClause == null) {
                        result = true;
                    }
                } else if (commandNode instanceof DeleteDisplayNode) {
                    DeleteDisplayNode deleteNode = (DeleteDisplayNode)commandNode;
                    DisplayNode whereClause = deleteNode.getClauseDisplayNode(WHERE);
                    if (whereClause == null) {
                        result = true;
                    }
                } else if (commandNode instanceof UpdateDisplayNode) {
                    UpdateDisplayNode updateNode = (UpdateDisplayNode)commandNode;
                    DisplayNode whereClause = updateNode.getClauseDisplayNode(WHERE);
                    if (whereClause == null) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Determines whether the index is anywhere within a DisplayNode of the specified type, given a List of DisplayNodes
     * 
     * @param nodeList the display Node list to insert into
     * @param index the cursor index
     * @param nodeType the type of DisplayNode
     * @return true if the cursor index is within the specified type, false if not
     */
    public static boolean isIndexWithin( List nodeList,
                                         int index,
                                         int nodeType ) {
        boolean result = false;
        List nodes = getDisplayNodesAtIndex(nodeList, index);
        // --------------------------------------------------
        // Index is between nodes, look at both
        // --------------------------------------------------
        if (nodes.size() == 2) {
            // Get the index nodes
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            // If second node is within the type, true
            if (isNodeWithin(node2, nodeType)) {
                result = true;
                // If first node is within the type, true
            } else if (isNodeWithin(node1, nodeType)) {
                result = true;
                // Otherwise, false
            } else {
                result = false;
            }
            // --------------------------------------------------
            // Index is within a node
            // --------------------------------------------------
        } else if (nodes.size() == 1) {
            // return command for the node
            if (isNodeWithin((DisplayNode)nodes.get(0), nodeType)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine whether a DisplayNode is within a DisplayNode of a specified type.
     * 
     * @param node the display Node to test
     * @param nodeType the type of node
     * @return true if the node is within a displayNode of specified type, false if not.
     */
    public static boolean isNodeWithin( DisplayNode node,
                                        int nodeType ) {
        if (nodeType == EXPRESSION) {
            return isWithinExpression(node);
        } else if (nodeType == CRITERIA) {
            return isWithinCriteria(node);
        } else if (nodeType == EDITABLE_CRITERIA) {
            return isWithinEditableCriteria(node);
        } else if (nodeType == SELECT) {
            return isWithinSelect(node);
        } else if (nodeType == FROM) {
            return isWithinFrom(node);
        } else if (nodeType == COMMAND) {
            return isWithinCommand(node);
        } else {
            return false;
        }
    }

    /**
     * Method to determine whether a DisplayNode is within a Criteria.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a criteria displayNode, false if not.
     */
    public static boolean isWithinCriteria( DisplayNode displayNode ) {
        if (getCriteriaForNode(displayNode) != null) {
            return true;
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within an 'editable' Criteria, which is a Criteria that the CriteriaBuilder
     * can handle.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a criteria displayNode, false if not.
     */
    public static boolean isWithinEditableCriteria( DisplayNode displayNode ) {
        DisplayNode criteriaDN = getCriteriaForNode(displayNode);
        return isEditableCriteria(criteriaDN);
    }

    public static boolean isEditableCriteria( DisplayNode criteriaDN ) {
        boolean result = false;
        if (criteriaDN != null) {
            // Must be one of the editable Criteria
            if (criteriaDN.languageObject instanceof CompoundCriteria || criteriaDN.languageObject instanceof CompareCriteria
                || criteriaDN.languageObject instanceof IsNullCriteria || criteriaDN.languageObject instanceof MatchCriteria
                || criteriaDN.languageObject instanceof SetCriteria) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine whether a DisplayNode is within an Expression.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within an expression displayNode, false if not.
     */
    public static boolean isWithinExpression( DisplayNode displayNode ) {
        if (getExpressionForNode(displayNode) != null) {
            return true;
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within a Select clause.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a Select displayNode, false if not.
     */
    public static boolean isWithinSelect( DisplayNode displayNode ) {
        if (displayNode.languageObject instanceof Select) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while (parentNode != null) {
            if (parentNode instanceof SelectDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within a From clause.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a From displayNode, false if not.
     */
    public static boolean isWithinFrom( DisplayNode displayNode ) {
        if (displayNode.languageObject instanceof From) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while (parentNode != null) {
            if (parentNode.languageObject instanceof From) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within a Command.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a Command displayNode, false if not.
     */
    public static boolean isWithinCommand( DisplayNode displayNode ) {
        if (isCommandNode(displayNode)) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while (parentNode != null) {
            if (isCommandNode(parentNode)) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within a Subquery.
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a subquery displayNode, false if not.
     */
    public static boolean isWithinSubQueryNode( DisplayNode displayNode ) {
        DisplayNode parentNode = displayNode;
        while (parentNode != null) {
            if (parentNode.languageObject instanceof SubqueryFromClause || parentNode.languageObject instanceof ScalarSubquery
                || parentNode.languageObject instanceof SubqueryCompareCriteria
                || parentNode.languageObject instanceof SubquerySetCriteria) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Method to determine whether a DisplayNode is within a Setquery .
     * 
     * @param displayNode the display Node to test
     * @return true if the node is within a subquery displayNode, false if not.
     */
    public static boolean isWithinSetQueryNodeBeforeSubQueryNode( DisplayNode displayNode ) {
        boolean hitSubQueryFirst = false;
        if (displayNode instanceof SetQueryDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while (parentNode != null) {
            if (parentNode.languageObject instanceof SubqueryFromClause || parentNode.languageObject instanceof ScalarSubquery
                || parentNode.languageObject instanceof SubqueryCompareCriteria
                || parentNode.languageObject instanceof SubquerySetCriteria) {
                hitSubQueryFirst = true;
            } else if (!hitSubQueryFirst && parentNode instanceof SetQueryDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * Get the SubQuery Command DisplayNode that this DisplayNode is within.
     * 
     * @param displayNode the display Node to test
     * @return the subQuery DisplayNode if available, null if not.
     */
    public static DisplayNode getSubQueryCommandDisplayNode( DisplayNode displayNode ) {
        if (!isWithinSubQueryNode(displayNode)) {
            return null;
        }
        DisplayNode result = null;
        if (displayNode.languageObject instanceof SubqueryFromClause) {
            result = displayNode.getChildren().get(0);
        } else if (displayNode.languageObject instanceof SubquerySetCriteria) {
            result = displayNode.getChildren().get(1);
        } else if (displayNode.languageObject instanceof ScalarSubquery) {
            result = displayNode.getChildren().get(0);
        } else if (displayNode.languageObject instanceof SubqueryCompareCriteria) {
            result = displayNode.getChildren().get(1);
        } else {
            DisplayNode parentNode = displayNode.getParent();
            while (parentNode != null) {
                if (parentNode.languageObject instanceof SubqueryFromClause) {
                    result = parentNode.getChildren().get(0);
                    break;
                } else if (parentNode.languageObject instanceof SubquerySetCriteria) {
                    result = parentNode.getChildren().get(1);
                    break;
                } else if (parentNode.languageObject instanceof ScalarSubquery) {
                    result = parentNode.getChildren().get(0);
                    break;
                } else if (parentNode.languageObject instanceof SubqueryCompareCriteria) {
                    result = parentNode.getChildren().get(1);
                    break;
                }
                parentNode = parentNode.getParent();
            }
        }
        return result;
    }

    /**
     * Method to determine whether a DisplayNode is a Symbol Node. This includes SymbolDisplayNodes, ElementSymbolDisplayNodes and
     * UnaryFromClauseDisplayNodes.
     * 
     * @param node the display Node to test
     * @return true if the node is a symbol node, false if not.
     */
    public static boolean isSymbolNode( DisplayNode node ) {
        if (node.languageObject instanceof Symbol || node.languageObject instanceof ElementSymbol
            || node.languageObject instanceof UnaryFromClause) {
            return true;
        }
        return false;
    }

    /**
     * Method to determine if a given type can be inserted into a DisplayNode List at a given index.
     * 
     * @param nodeList the display Node list to insert into
     * @param index the desired insertion index
     * @param nodeType the desired type to insert.
     * @return 'true' if the type can be inserted, 'false' if not.
     */
    public static boolean isInsertAllowed( List nodeList,
                                           int index,
                                           int nodeType ) {
        boolean result = false;
        // ------------------------------------------------------------------
        // Find the nodes that the index is between (0,1,or 2)
        // ------------------------------------------------------------------
        List nodes = getDisplayNodesAtIndex(nodeList, index);
        // --------------------------------------------------
        // Index is between nodes, look at both
        // --------------------------------------------------
        if (nodes.size() == 2) {
            // Get the index nodes
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            // Can type be inserted before the second node.
            if (DisplayNodeUtils.canInsertBefore(node2, nodeType)) {
                result = true;
                // Can type be inserted after the first node
            } else if (DisplayNodeUtils.canInsertAfter(node1, nodeType)) {
                result = true;
                // Otherwise, false
            } else {
                result = false;
            }
            // --------------------------------------------------
            // Index is within a node
            // --------------------------------------------------
        } else if (nodes.size() == 1) {
            // can type be inserted after node
        	DisplayNode nodeAtIndex = getNodeAtIndex((DisplayNode)nodes.get(0), index);
            if (nodeAtIndex != null && DisplayNodeUtils.canInsertAfter(nodeAtIndex, nodeType)) {
                result = true;
            }
//            if (DisplayNodeUtils.canInsertAfter((DisplayNode)nodes.get(0), nodeType)) {
//                result = true;
//            }
        }
        return result;
    }

    /**
     * Method to get the specified DisplayNode type, given a DisplayNode.
     * 
     * @param node the display Node to test
     * @param nodeType the type of node
     * @return the node of the specified type (if applicable), null if not.
     */
    public static DisplayNode getNodeTypeAtIndex( List nodeList,
                                                  int index,
                                                  int nodeType ) {
        DisplayNode result = null;
        // ------------------------------------------------------------------
        // Find the listNodes that the index is between (0,1,or 2)
        // ------------------------------------------------------------------
        List nodes = getDisplayNodesAtIndex(nodeList, index);
        int nNodes = nodes.size();
        // index is within a Node, return type for it
        if (nNodes == 1) {
            DisplayNode node = (DisplayNode)nodes.get(0);
            return getNodeTypeAtIndex(node, index, nodeType);
            // index is between two Nodes, first check the second node, then the first
        } else if (nNodes == 2) {
            DisplayNode node = (DisplayNode)nodes.get(1);
            result = getNodeTypeForNode(node, nodeType);
            if (result != null) {
                return result;
            }
            node = (DisplayNode)nodes.get(0);
            result = getNodeTypeForNode(node, nodeType);
        }
        return result;
    }
    
    public static DisplayNode getNodeTypeAtIndex(DisplayNode parentNode, int index, int nodeType) {
    	if( parentNode != null && parentNode.isAnywhereWithin(index) ) {
    		DisplayNode theNode = getNodeTypeForNode(parentNode, nodeType);
    		if( theNode != null ) {
    			return theNode;
    		}
    	}
    	
    	for( DisplayNode node : parentNode.getChildren()) {
    		DisplayNode theNode = getNodeTypeAtIndex(node, index, nodeType);
    		if( theNode != null ) {
    			return theNode;
    		}
    	}
    	
    	return null;
    }
    
    public static DisplayNode getNodeAtIndex(DisplayNode parentNode, int index) {
    	if( parentNode != null && parentNode.isAnywhereWithin(index) && parentNode.getChildren().isEmpty() ) {
    		return parentNode;
    	}
    	
    	for( DisplayNode node : parentNode.getChildren()) {
    		DisplayNode theNode = getNodeAtIndex(node, index);
    		if( theNode != null ) {
    			return theNode;
    		}
    	}
    	
    	return null;
    }

    /**
     * Method to get a node of the specified DisplayNode type, given a DisplayNode. The nodes ancestry is walked up until a node
     * of the right type is found, else null is returned.
     * 
     * @param node the display Node to test
     * @param nodeType the type of node
     * @return the node of the specified type (if applicable), null if not.
     */
    public static DisplayNode getNodeTypeForNode( DisplayNode node,
                                                  int nodeType ) {
        if (nodeType == EXPRESSION) {
            return getExpressionForNode(node);
        } else if (nodeType == CRITERIA) {
            return getCriteriaForNode(node);
        } else if (nodeType == COMMAND) {
            return getCommandForNode(node);
        } else if (nodeType == CLAUSE) {
            return getClauseForNode(node);
        } else {
            return null;
        }
    }

    /**
     * Get the CriteriaDisplayNode for a given DisplayNode. If the DisplayNode is itself a CriteriaDisplayNode, return it. If not,
     * return the first parent that is a CriteriaDisplayNode. Otherwise, return null.
     * 
     * @param node the Display node to check.
     * @return the criteria display Node for this node.
     */
    public static DisplayNode getCriteriaForNode( DisplayNode node ) {
        return node.getCriteria();
    }

    /**
     * Get the CriteriaDisplayNode for a given DisplayNode. If the DisplayNode is itself a CriteriaDisplayNode, return it. If not,
     * return the first parent that is a CriteriaDisplayNode. Otherwise, return null.
     * 
     * @param node the Display node to check.
     * @return the criteria display Node for this node.
     */
    public static DisplayNode getCriteriaInNode( DisplayNode node ) {
        if (node.languageObject instanceof Criteria) {
            return node;
        }

        DisplayNode nextNode = null;
        for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
            nextNode = (DisplayNode)iter.next();
            return getCriteriaInNode(nextNode);
        }

        return null;
    }

    /**
     * Get the ExpressionDisplayNode for a given DisplayNode. If the DisplayNode is itself an ExpressionDisplayNode, return it. If
     * not, return the first parent that is an ExpressionDisplayNode. Otherwise, return null.
     * 
     * @param node the Display node to check.
     * @return the Expression display Node for this node.
     */
    public static DisplayNode getExpressionForNode( DisplayNode node ) {
        if (node == null) return null;
        if (node.languageObject instanceof ElementSymbol) {
            if (node.isInExpression()) {
                return node.getExpression();
            }
            return null;
        } else if (node.languageObject instanceof Expression && !(node.languageObject instanceof ScalarSubquery)) {
            return node;
        } else if (node.isInExpression()) {
            return node.getExpression();
        } else {
            return null;
        }
    }

    /**
     * Get the command DisplayNode for a given DisplayNode. If the DisplayNode is within a subquery, the subquery command is
     * returned. If not, return the first parent that is a commnad DisplayNode is returned. Otherwise, return null.
     * 
     * @param node the Display node to check.
     * @return the command display Node for this node.
     */
    public static DisplayNode getCommandForNode( DisplayNode node ) {
        if (node == null) return null;
        DisplayNode result = null;
        if (!isWithinSetQueryNodeBeforeSubQueryNode(node) && isWithinSubQueryNode(node)) {
            result = getSubQueryCommandDisplayNode(node);
        } else {
            DisplayNode parentNode = node.getParent();
            while (parentNode != null) {
                if (isCommandNode(parentNode)) {
                    result = parentNode;
                    break;
                } else if (parentNode.languageObject instanceof CommandStatement) {
                    List childList = ((DisplayNode)parentNode).getChildren();
                    result = (DisplayNode)childList.get(0);
                    break;
                } else {
                    parentNode = parentNode.getParent();
                }
            }
        }
        return result;
    }

    /**
     * Get the clause DisplayNode for a given DisplayNode. If not with a clause, return null.
     * 
     * @param node the Display node to check.
     * @return the clause display Node for this node.
     */
    public static DisplayNode getClauseForNode( DisplayNode node ) {
        DisplayNode result = null;
        if (node != null) {
            if (isClauseNode(node)) {
                result = node;
            } else {
                DisplayNode parentNode = node.getParent();
                while (parentNode != null) {
                    if (isClauseNode(parentNode)) {
                        result = parentNode;
                        break;
                    }
                    parentNode = parentNode.getParent();
                }
            }
        }
        return result;
    }

    /**
     * Returns a boolean representing the state of this preference.
     * 
     * @return the 'start clauses on newline' preference truth value
     */
    public static boolean isClauseCROn() {
        UiPlugin uiPlugin = UiPlugin.getDefault();
        if (uiPlugin != null) {
            return uiPlugin.getPreferenceStore().getBoolean(UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE);
        }
        return true;
    }

    /**
     * Returns a boolean representing the state of this preference.
     * 
     * @return the 'indent clause content' preference truth value
     */
    public static boolean isClauseIndentOn() {
        UiPlugin uiPlugin = UiPlugin.getDefault();
        if (uiPlugin != null) {
            return uiPlugin.getPreferenceStore().getBoolean(UiConstants.Prefs.INDENT_CLAUSE_CONTENT);
        }
        return true;
    }

    public static List getIndentNodes( DisplayNode dnNode,
                                       int iIndentLevel ) {
        ArrayList arylIndentNodes = new ArrayList(iIndentLevel);

        for (int i = 0; i < iIndentLevel; i++) {
            arylIndentNodes.add(DisplayNodeFactory.createDisplayNode(dnNode, TAB));
        }

        return arylIndentNodes;
    }
}

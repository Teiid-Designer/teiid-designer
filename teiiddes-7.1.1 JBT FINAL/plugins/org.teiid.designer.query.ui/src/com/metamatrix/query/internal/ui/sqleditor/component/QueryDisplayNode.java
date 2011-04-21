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
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;

/**
 * The <code>QueryDisplayNode</code> class is used to represent a SELECT Query.
 */
public class QueryDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * QueryDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param query the query language object used to construct this display node.
     */
    public QueryDisplayNode( DisplayNode parentNode,
                             Query query ) {
        this.parentNode = parentNode;
        this.languageObject = query;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the DisplayNode for the Clause if there is one, null if not
     */
    public DisplayNode getClauseDisplayNode( int clauseType ) {
        DisplayNode resultNode = null;
        if (clauseType < SELECT || clauseType > OPTION) {
            return resultNode;
        }
        Iterator iter = childNodeList.iterator();
        while (iter.hasNext()) {
            resultNode = (DisplayNode)iter.next();
            switch (clauseType) {
                case SELECT:
                    if (resultNode.languageObject instanceof Select) {
                        return resultNode;
                    }
                    break;
                case INTO:
                    if (resultNode.languageObject instanceof Into) {
                        return resultNode;
                    }
                    break;
                case FROM:
                    if (resultNode.languageObject instanceof From) {
                        return resultNode;
                    }
                    break;
                case WHERE:
                    if (resultNode instanceof WhereDisplayNode) {
                        return resultNode;
                    }
                    break;
                case GROUPBY:
                    if (resultNode.languageObject instanceof GroupBy) {
                        return resultNode;
                    }
                    break;
                case HAVING:
                    if (resultNode instanceof HavingDisplayNode) {
                        return resultNode;
                    }
                    break;
                case ORDERBY:
                    if (resultNode.languageObject instanceof OrderBy) {
                        return resultNode;
                    }
                    break;
                case OPTION:
                    if (resultNode.languageObject instanceof Option) {
                        return resultNode;
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Returns the DisplayNode clause at a given index. The entire clause is returned - SELECT, FROM, WHERE, GROUPBY, HAVING,
     * ORDERBY, or OPTION
     */
    public DisplayNode getClauseAtIndex( int index ) {
        int nChildren = childNodeList.size();
        List validClauses = new ArrayList(0);
        for (int i = 0; i < nChildren; i++) {
            DisplayNode node = childNodeList.get(i);
            if (node.isAnywhereWithin(index)) {
                validClauses.add(node);
            }
        }
        // if the index is between two clauses, return the second one
        int nClauses = validClauses.size();
        if (nClauses == 0) {
            // Do one last check to see if between nodes
            for (int i = 0; i < (nChildren - 1); i++) {
                DisplayNode node1 = childNodeList.get(i);
                DisplayNode node2 = childNodeList.get(i + 1);
                int endNode1 = node1.getEndIndex();
                int startNode2 = node2.getStartIndex();
                if (index > (endNode1 + 1) && index < startNode2) {
                    return node1;
                }
                if (i == (nChildren - 2)) {
                    return node2;
                }
            }
            return null;
        } else if (nClauses == 1) {
            return (DisplayNode)validClauses.get(0);
        } else if (nClauses == 2) {
            return (DisplayNode)validClauses.get(1);
        } else {
            return null;
        }
    }

}

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.Iterator;
import java.util.List;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Option;

/**
 * The <code>DeleteDisplayNode</code> class is used to represent a DELETE command.
 */
public class DeleteDisplayNode extends DisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * DeleteDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param update The delete language object used to construct this display node.
     */
    public DeleteDisplayNode( DisplayNode parentNode,
                              Delete delete ) {
        this.parentNode = parentNode;
        this.languageObject = delete;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Delete Clause supports Groups
     */
    @Override
    public boolean supportsGroup() {
        return true;
    }

    /**
     * Delete Clause supports criteria
     */
    @Override
    public boolean supportsCriteria() {
        return true;
    }

    /**
     * Returns the DisplayNode for the Clause if there is one, null if not
     */
    public DisplayNode getClauseDisplayNode( int clauseType ) {
        if (clauseType < SELECT || clauseType > OPTION) {
            return null;
        }
        Iterator iter = childNodeList.iterator();
        while (iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            switch (clauseType) {
                case WHERE:
                    if (node instanceof WhereDisplayNode) {
                        return node;
                    }
                    break;
                case OPTION:
                    if (node.languageObject instanceof Option) {
                        return node;
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Returns the DisplayNode clause at a given index. The entire clause is returned - WHERE or OPTION
     */
    public DisplayNode getClauseAtIndex( int index ) {
        List nodes = DisplayNodeUtils.getDisplayNodesAtIndex(displayNodeList, index);
        // if the index is between two clauses, return the second one
        int nNodes = nodes.size();
        if (nNodes == 0) {
            return null;
        } else if (nNodes == 1) {
            return DisplayNodeUtils.getClauseForNode((DisplayNode)nodes.get(0));
        } else if (nNodes == 2) {
            DisplayNode clause1 = DisplayNodeUtils.getClauseForNode((DisplayNode)nodes.get(0));
            DisplayNode clause2 = DisplayNodeUtils.getClauseForNode((DisplayNode)nodes.get(1));
            if (clause2 != null) {
                return clause2;
            }
            return clause1;
        } else {
            return null;
        }
    }

}

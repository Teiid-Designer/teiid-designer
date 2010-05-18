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

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.FromClause;
import com.metamatrix.query.sql.lang.JoinPredicate;
import com.metamatrix.query.sql.lang.JoinType;
import com.metamatrix.query.sql.lang.PredicateCriteria;

/**
 * The <code>JoinPredicateDisplayNode</code> class is used to represent a JoinPredicate.
 */
public class JoinPredicateDisplayNode extends FromClauseDisplayNode {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    /**
     * JoinPredicateDisplayNode constructor
     * 
     * @param parentNode the parent DisplayNode of this.
     * @param predicate the query language object used to construct this display node.
     */
    public JoinPredicateDisplayNode( DisplayNode parentNode,
                                     JoinPredicate predicate ) {
        this.parentNode = parentNode;
        this.languageObject = predicate;
        createChildNodes();
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        // int indent = this.getIndentLevel();

        JoinPredicate joinPredicate = (JoinPredicate)this.getLanguageObject();
        FromClause clause = joinPredicate.getLeftClause();
        if (clause == null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, ERROR));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, clause));
        }

        clause = joinPredicate.getRightClause();
        if (clause == null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, ERROR));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, clause));
        }

        Iterator iter = joinPredicate.getJoinCriteria().iterator();
        while (iter.hasNext()) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, clause));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     * Create the DisplayNode list for this type of DisplayNode. This is a list of all the lowest level nodes for this
     * DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        // int indent = this.getIndentLevel();

        JoinPredicate joinPredicate = (JoinPredicate)this.getLanguageObject();
        if (joinPredicate.isOptional()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, OPTIONAL_COMMENTS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
        }

        if (joinPredicate.hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, LTPAREN));
        }

        // Left FromClause
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        // indent = child.getIndentLevel();
        if (child instanceof JoinPredicateDisplayNode && !joinPredicate.getLeftClause().hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, LTPAREN));
            if (child.hasDisplayNodes()) {
                displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, RTPAREN));
        } else {
            if (child.hasDisplayNodes()) {
                displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                displayNodeList.add(child);
            }
        }

        JoinType joinType = joinPredicate.getJoinType();
        if (joinType == null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, ERROR));
        } else {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, joinType.toString()));
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, SPACE));

        // Right FromClause
        child = (DisplayNode)childNodeList.get(1);
        // indent = child.getIndentLevel();
        if (child instanceof JoinPredicateDisplayNode && !joinPredicate.getRightClause().hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, LTPAREN));
            if (child.hasDisplayNodes()) {
                displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, RTPAREN));
        } else {
            if (child.hasDisplayNodes()) {
                displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                displayNodeList.add(child);
            }
        }

     // join criteria
        List joinCriteria = joinPredicate.getJoinCriteria();
    	if(joinCriteria != null && joinCriteria.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ON));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
    		Iterator critIter = joinCriteria.iterator();
    		while(critIter.hasNext()) {
    			Criteria crit = (Criteria) critIter.next();
                if(crit instanceof PredicateCriteria) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,crit));
                } else {
                	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,crit));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
                }

    			if(critIter.hasNext()) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.AND));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
    			}
    		}
    	}

        if(joinPredicate.hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        }

        addFromClauseDepOptions(joinPredicate);

    }

}

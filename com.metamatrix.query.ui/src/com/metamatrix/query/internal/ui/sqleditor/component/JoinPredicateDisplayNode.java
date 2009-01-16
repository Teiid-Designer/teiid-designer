/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.FromClause;
import com.metamatrix.query.sql.lang.JoinPredicate;
import com.metamatrix.query.sql.lang.JoinType;

/**
 * The <code>JoinPredicateDisplayNode</code> class is used to represent a JoinPredicate.
 */
public class JoinPredicateDisplayNode extends FromClauseDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   JoinPredicateDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param predicate the query language object used to construct this display node.
     */
    public JoinPredicateDisplayNode(DisplayNode parentNode, JoinPredicate predicate) {
        this.parentNode = parentNode;
        this.languageObject = predicate;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        JoinPredicate joinPredicate = (JoinPredicate)this.getLanguageObject();
        FromClause clause = joinPredicate.getLeftClause();
        if(clause==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,clause));
        }

        clause = joinPredicate.getRightClause();
        if(clause==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,clause));
        }

        Iterator iter = joinPredicate.getJoinCriteria().iterator();
        while( iter.hasNext() ) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,iter.next()));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        //int indent = this.getIndentLevel();
        
        JoinPredicate joinPredicate = (JoinPredicate)this.getLanguageObject();
        if(joinPredicate.isOptional()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,OPTIONAL_COMMENTS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        
        if (joinPredicate.hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        }

        // Left FromClause
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        //indent = child.getIndentLevel();
        if( child instanceof JoinPredicateDisplayNode && !joinPredicate.getLeftClause().hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        } else {
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
        }

        JoinType joinType = joinPredicate.getJoinType();
		if(joinType == null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
		} else {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,joinType.toString()));
		}

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // Right FromClause
        child = (DisplayNode)childNodeList.get(1);
        //indent = child.getIndentLevel();
        if( child instanceof JoinPredicateDisplayNode && !joinPredicate.getRightClause().hasHint()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, LTPAREN));
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        } else {
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
        }

        if(childNodeList.size()>2) {
            Iterator iter = childNodeList.iterator();
            if(iter.hasNext()) iter.next();
            if(iter.hasNext()) iter.next();

            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ON));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            while(iter.hasNext()) {
                child = (DisplayNode)iter.next();
                //indent = child.getIndentLevel();
                if( child.hasDisplayNodes() ) {
                        displayNodeList.addAll(child.getDisplayNodeList());
                } else {
                        displayNodeList.add(child);
                }
                if(iter.hasNext()) {
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


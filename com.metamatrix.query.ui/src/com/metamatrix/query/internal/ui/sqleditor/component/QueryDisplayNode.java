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
import java.util.List;

import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.GroupBy;
import com.metamatrix.query.sql.lang.Limit;
import com.metamatrix.query.sql.lang.Option;
import com.metamatrix.query.sql.lang.OrderBy;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.Select;

/**
 * The <code>QueryDisplayNode</code> class is used to represent a SELECT Query.
 */
public class QueryDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   QueryDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param query the query language object used to construct this display node.
     */
    public QueryDisplayNode(DisplayNode parentNode, Query query) {
        this.parentNode = parentNode;
        this.languageObject = query;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the DisplayNode for the Clause if there is one, null if not
     */
    public DisplayNode getClauseDisplayNode(int clauseType) {
        DisplayNode resultNode = null;
        if(clauseType<SELECT || clauseType>OPTION) {
            return resultNode;
        }
        Iterator iter = childNodeList.iterator();
        while (iter.hasNext()) {
            resultNode = (DisplayNode)iter.next();
            switch(clauseType) {
                case SELECT:
                    if(resultNode instanceof SelectDisplayNode) {
                        return resultNode;
                    }
                    break;
                case INTO:
                    if(resultNode instanceof IntoDisplayNode) {
                        return resultNode;
                    }
                    break;
                case FROM:
                    if(resultNode instanceof FromDisplayNode) {
                        return resultNode;
                    }
                    break;
                case WHERE:
                    if(resultNode instanceof WhereDisplayNode) {
                        return resultNode;
                    }
                    break;
                case GROUPBY:
                    if(resultNode instanceof GroupByDisplayNode) {
                        return resultNode;
                    }
                    break;
                case HAVING:
                    if(resultNode instanceof HavingDisplayNode) {
                        return resultNode;
                    }
                    break;
                case ORDERBY:
                    if(resultNode instanceof OrderByDisplayNode) {
                        return resultNode;
                    }
                    break;
                case OPTION:
                    if(resultNode instanceof OptionDisplayNode) {
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
     * Returns the DisplayNode clause at a given index.  The entire clause is returned -
     *   SELECT, FROM, WHERE, GROUPBY, HAVING, ORDERBY, or OPTION 
     */
    public DisplayNode getClauseAtIndex(int index) {
        int nChildren = childNodeList.size();
        List validClauses = new ArrayList(0);
        for(int i=0; i<nChildren; i++) {
            DisplayNode node = (DisplayNode)childNodeList.get(i);
            if( node.isAnywhereWithin(index) ) {
                validClauses.add(node);
            }
        }
        // if the index is between two clauses, return the second one
        int nClauses = validClauses.size();
        if(nClauses==0) {
            // Do one last check to see if between nodes
            for(int i=0; i<(nChildren-1); i++) {
                DisplayNode node1 = (DisplayNode)childNodeList.get(i);
                DisplayNode node2 = (DisplayNode)childNodeList.get(i+1);
                int endNode1 = node1.getEndIndex();
                int startNode2 = node2.getStartIndex();
                if( index>(endNode1+1) && index<startNode2 ) {
                    return node1;
                }
                if(i==(nChildren-2)) {
                    return node2;
                }
            }
            return null;
        } else if(nClauses==1) {
            return (DisplayNode)validClauses.get(0);
        } else if(nClauses==2) {
            return (DisplayNode)validClauses.get(1);
        } else {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a QueryDisplayNode,
     *  the children are the clauses of the Query.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        Query query = (Query)(this.getLanguageObject());
        //int indent = this.getIndentLevel();

        //----------------------------------------------------
        // Add clauses to childNodeList
        //----------------------------------------------------
        Select select = query.getSelect();
        if(select!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,select));
        }
        if(query.getInto() != null){
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,query.getInto()));
        }
        From from = query.getFrom();
        if(from!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,from));
        }
        Criteria criteria = query.getCriteria();
        if(criteria!=null) {
            childNodeList.add(DisplayNodeFactory.createWhereDisplayNode(this,criteria));
        }
        GroupBy groupBy = query.getGroupBy();
        if(groupBy!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,groupBy));
        }
        Criteria having = query.getHaving();
        if(having!=null) {
            childNodeList.add(DisplayNodeFactory.createHavingDisplayNode(this,having));
        }
        OrderBy orderBy = query.getOrderBy();
        if(orderBy!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,orderBy));
        }
        Limit limit = query.getLimit();
        if(limit!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,limit));
        }
        Option option = query.getOption();
        if(option!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,option));
        }


        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        Iterator iter = this.getChildren().iterator();
        while(iter.hasNext()) {
            DisplayNode childNode = (DisplayNode) iter.next();
            //-----------------------------------------
            // Add the clause
            //-----------------------------------------
            if(childNode.hasDisplayNodes()) {
            	displayNodeList.addAll(childNode.getDisplayNodeList());
            } else {
            	displayNodeList.add(childNode);
            }
            boolean hasNext = iter.hasNext();
            //-----------------------------------------
            // Add post-clause formatting if necessary
            //-----------------------------------------
            addPostClauseFormatting(childNode,hasNext);
        }
	}
    
    private void addPostClauseFormatting(DisplayNode owner,boolean hasNext) {
        if(hasNext) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(owner,SPACE));
            if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(owner,CR));
            }
        }
    }

}

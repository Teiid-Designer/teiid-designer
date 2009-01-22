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
import com.metamatrix.query.sql.lang.QueryCommand;
import com.metamatrix.query.sql.lang.SetQuery;

/**
 * The <code>SetQueryDisplayNode</code> class is used to represent a Set Query, for example
 * Union Queries.
 */
public class SetQueryDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SetQueryDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param seQuery the query language object used to construct this display node.
     */
    public SetQueryDisplayNode(DisplayNode parentNode, SetQuery setQuery) {
        this.parentNode = parentNode;
        this.languageObject = setQuery;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the DisplayNode clause at a given index.  If the index is not within a
     * QueryDisplayNode, null is returned.
     * @param index the cursor index
     * @return the Query Clause that the index is within, null if not.
     */
    public DisplayNode getClauseAtIndex(int index) {
        // Get the QueryDisplayNode at this index
        QueryDisplayNode queryNode = getQueryAtIndex(index);
        // Get the query clause
        if(queryNode!=null) {
            return queryNode.getClauseAtIndex(index);
        }
        return null;
    }

    /**
     * Returns the QueryDisplayNode at a given cursor index.  If the cursor is not
     * within a query displayNode, null is returned.
     * @param index the cursor index
     * @return the QueryDisplayNode that the index is within, null if not.
     */
    public QueryDisplayNode getQueryAtIndex(int index) {
        // Get the QueryDisplayNodes
        List queryNodeList = getQueryDisplayNodes();
        // Iterate through the QueryDisplayNodes
        Iterator iter = queryNodeList.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if(node.isAnywhereWithin(index)) {
                return (QueryDisplayNode)node;
            }
        }
        return null;
    }

    /**
     * Returns the QueryIndex for the supplied index.  The queryIndex is the position
     * of the QueryDisplayNode in the SetQuery.  For example, query 2 of 8.  Query index
     * starts at 0.
     * @param index the cursor index
     * @return the Query index for the query that the cursor index is within, -1 if not.
     */
    public int getQueryIndex(int index) {
        // Get the QueryDisplayNodes
        List queryNodeList = getQueryDisplayNodes();
        // Iterate through the QueryDisplayNodes
        for(int i=0; i<queryNodeList.size(); i++) {
            DisplayNode queryNode = (DisplayNode)queryNodeList.get(i);
            if(queryNode.isAnywhereWithin(index)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the list of QueryDisplayNodes for this SetQuery.
     * @param the list of QueryDisplayNodes
     */
    public List getQueryDisplayNodes( ) {
        List queryNodeList = new ArrayList();
        // Iterate through the child Nodes (QueryDisplayNode or SetQueryDisplayNode);
        Iterator iter = childNodeList.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if(node instanceof QueryDisplayNode) {
                queryNodeList.add(node);
            } else if(node instanceof SetQueryDisplayNode) {
                queryNodeList.addAll(((SetQueryDisplayNode)node).getQueryDisplayNodes());
           	}
        }
        return queryNodeList;
    }

    /**
     * Returns the QueryDisplayNode at the specified query index
     * @param queryIndex the query index
     * @return the QueryDisplayNode for the specified queryIndex, null if queryIndex
     * is invalid.
     */
    public QueryDisplayNode getQueryDisplayNode(int queryIndex) {
        List queryNodes = getQueryDisplayNodes();
        int nNodes = queryNodes.size();
        if(nNodes!=0) {
            if(queryIndex>=0 && queryIndex<nNodes) {
                return (QueryDisplayNode)queryNodes.get(queryIndex);
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a SetQueryDisplayNode,
     *  the children are the Sub-Queries (QueryDisplayNodes).
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        SetQuery setQuery = (SetQuery)(this.getLanguageObject());
        //int indent = this.getIndentLevel();

        //----------------------------------------------------
        // the Children are the SubQueries
        //----------------------------------------------------
        List queries = setQuery.getQueryCommands();
        Iterator iter = queries.iterator();
        while( iter.hasNext() ) {
            QueryCommand command = (QueryCommand)iter.next();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,command));
            // Increment Indent for each subsequent Query
            //indent++;
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
        SetQuery setQuery = (SetQuery)(this.getLanguageObject());
        displayNodeList = new ArrayList();
        //int baseIndent = this.getIndentLevel();

        Iterator childIter = this.getChildren().iterator();

        // Add first query to string and ignore first use all flag
        DisplayNode childNode = (DisplayNode) childIter.next();
        //int indent = childNode.getIndentLevel();
        if(childNode instanceof QueryDisplayNode) {
            displayNodeList.addAll(childNode.getDisplayNodeList());
        } else {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
            displayNodeList.addAll(childNode.getDisplayNodeList());
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        }

        // Add rest of queries
        while(childIter.hasNext()) {
            childNode = (DisplayNode) childIter.next();
//            indent = childNode.getIndentLevel();
//
//            int shiftedIndent = indent-1;
//            if(shiftedIndent<0) shiftedIndent=0;

            // present the operationString
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,setQuery.getOperation()));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

            if(setQuery.isAll()) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ALL));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));

            if(childNode instanceof QueryDisplayNode) {
                displayNodeList.addAll(childNode.getDisplayNodeList());
            } else {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
                displayNodeList.addAll(childNode.getDisplayNodeList());
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
            }
        }

        if(setQuery.getOrderBy() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            DisplayNode orderByNode = DisplayNodeFactory.createDisplayNode(this,setQuery.getOrderBy());
            if(orderByNode.hasDisplayNodes()) {
                displayNodeList.addAll(orderByNode.getDisplayNodeList());
            } else {
                displayNodeList.add(orderByNode);
            }
        }
        if(setQuery.getLimit() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            DisplayNode limitNode = DisplayNodeFactory.createDisplayNode(this,setQuery.getLimit());
            if(limitNode.hasDisplayNodes()) {
                displayNodeList.addAll(limitNode.getDisplayNodeList());
            } else {
                displayNodeList.add(limitNode);
            }
        }

        if(setQuery.getOption() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            DisplayNode optionNode = DisplayNodeFactory.createDisplayNode(this,setQuery.getOption());
            if(optionNode.hasDisplayNodes()) {
            	displayNodeList.addAll(optionNode.getDisplayNodeList());
            } else {
            	displayNodeList.add(optionNode);
            }
        }
	}

}


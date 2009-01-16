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

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.GroupBy;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * The <code>GroupByDisplayNode</code> class is used to represent a Query's GROUPBY clause.
 */
public class GroupByDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   GroupByDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param groupBy the query language object used to construct this display node.
     */
    public GroupByDisplayNode(DisplayNode parentNode, GroupBy groupBy) {
        this.parentNode = parentNode;
        this.languageObject = groupBy;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * GroupBy Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        GroupBy groupBy = (GroupBy)(this.getLanguageObject());
        List symbols = groupBy.getSymbols();

//        int indent = this.getIndentLevel();
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	indent++;
//        }
	    Iterator iter = symbols.iterator();
        while( iter.hasNext() ) {
			Expression symbol = (Expression) iter.next();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,symbol));
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
//        int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.GROUP));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.BY));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }

        Iterator iter = childNodeList.iterator();
        while( iter.hasNext() ) {
            DisplayNode childNode = (DisplayNode) iter.next();
            //indent = childNode.getIndentLevel();
            if(childNode.hasDisplayNodes()) {
                displayNodeList.addAll(childNode.getDisplayNodeList());
            } else {
                displayNodeList.add(childNode);
            }
            if( iter.hasNext() ) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
            }
        }
//        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }

	}

}


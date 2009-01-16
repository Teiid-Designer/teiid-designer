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
import com.metamatrix.query.sql.lang.SetCriteria;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * The <code>SetCriteriaDisplayNode</code> class is used to represent a Set Criteria.
 */
public class SetCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SetCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public SetCriteriaDisplayNode(DisplayNode parentNode, SetCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean supportsCriteria() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  The Child nodes are
     *   the Left Expression and the Values.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        SetCriteria setCriteria = (SetCriteria)this.getLanguageObject();
        Expression expression = setCriteria.getExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

        Iterator iter = setCriteria.getValues().iterator();
        while( iter.hasNext() ) {
            expression = (Expression)iter.next();
            if(expression==null) {
                childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
            } else {
                childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
            }
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

        // If Left Expression has display nodes, add them to the list
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }
        //indent = child.getIndentLevel();

		// Keyword IN
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        // Check if the SetCriteria is negated
        if( ((SetCriteria)this.languageObject).isNegated() ) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IN));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE+LTPAREN));

        // value list
		int size = childNodeList.size()-1;
		if(size == 1) {
            child = (DisplayNode)childNodeList.get(1);
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
		} else if(size > 1) {
			Iterator iter = childNodeList.iterator();
            iter.next(); // Skip the first child
            child = (DisplayNode)iter.next();
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
			while(iter.hasNext()) {
                child = (DisplayNode)iter.next();
                //indent = child.getIndentLevel();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                if( child.hasDisplayNodes() ) {
                        displayNodeList.addAll(child.getDisplayNodeList());
                } else {
                        displayNodeList.add(child);
                }
			}
		}
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	}

}


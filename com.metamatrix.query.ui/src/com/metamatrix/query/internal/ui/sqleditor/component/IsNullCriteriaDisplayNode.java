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

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.IsNullCriteria;
import com.metamatrix.query.sql.symbol.Expression;

/**
 * The <code>IsNullCriteriaDisplayNode</code> class is used to represent an IsNull Criteria.
 */
public class IsNullCriteriaDisplayNode extends PredicateCriteriaDisplayNode {

    private static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   IsNullCriteriaDisplayNode constructors
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public IsNullCriteriaDisplayNode(DisplayNode parentNode, IsNullCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
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

        IsNullCriteria isNullCriteria = (IsNullCriteria)this.getLanguageObject();
        Expression expression = isNullCriteria.getExpression();
        if(expression==null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,UNDEFINED));
        } else {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,expression));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        IsNullCriteria isNullCriteria = (IsNullCriteria)this.getLanguageObject();
        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        // If Expression has display nodes, add them to the list
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        if (isNullCriteria.isNegated()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOT));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NULL));

	}

}


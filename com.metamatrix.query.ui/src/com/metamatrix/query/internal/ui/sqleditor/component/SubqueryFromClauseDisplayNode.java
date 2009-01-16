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
import com.metamatrix.query.sql.lang.SubqueryFromClause;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.ReservedWords;

/**
 * The <code>SubqueryFromClauseDisplayNode</code> class is used to represent a Subquery From Clause.
 */
public class SubqueryFromClauseDisplayNode extends FromClauseDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SubqueryFromClauseDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param clause the language object used to construct this display node.
     */
    public SubqueryFromClauseDisplayNode(DisplayNode parentNode, SubqueryFromClause clause) {
        this.parentNode = parentNode;
        this.languageObject = clause;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        //int indent = this.getIndentLevel();

		SubqueryFromClause subQueryFromClause = (SubqueryFromClause)(this.getLanguageObject());
        Command queryCommand = subQueryFromClause.getCommand();

		childNodeList.add(DisplayNodeFactory.createDisplayNode(this,queryCommand));

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
		
        SubqueryFromClause subQueryFromClause = (SubqueryFromClause)(this.getLanguageObject());
        if(subQueryFromClause.isOptional()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,OPTIONAL_COMMENTS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        // ChildNode is the QueryCommand DisplayNode
        DisplayNode childNode = (DisplayNode)childNodeList.get(0);
        if(childNode.hasDisplayNodes()) {
            displayNodeList.addAll(childNode.getDisplayNodeList());
        } else {
            displayNodeList.add(childNode);
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        // Keyword AS
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.AS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,subQueryFromClause.getName()));
        
        addFromClauseDepOptions(subQueryFromClause);
	}

}


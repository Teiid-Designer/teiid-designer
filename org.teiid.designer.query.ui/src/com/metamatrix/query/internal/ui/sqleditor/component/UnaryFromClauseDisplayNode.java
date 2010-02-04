/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import com.metamatrix.query.sql.lang.UnaryFromClause;

/**
 * The <code>UnaryFromClauseDisplayNode</code> class is used to represent a Unary From Clause.
 */
public class UnaryFromClauseDisplayNode extends FromClauseDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   UnaryFromClauseDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param clause the query language object used to construct this display node.
     */
    public UnaryFromClauseDisplayNode(DisplayNode parentNode, UnaryFromClause clause) {
        this.parentNode = parentNode;
        this.languageObject = clause;
        createDisplayNodeList(clause);
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList(UnaryFromClause fromClause) {
        displayNodeList = new ArrayList();
        
        if(fromClause.isOptional()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,OPTIONAL_COMMENTS));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }  

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,fromClause.getGroup()));
        
        addFromClauseDepOptions(fromClause);
    }    

}


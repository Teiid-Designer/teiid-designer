/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Into;
import com.metamatrix.query.sql.symbol.GroupSymbol;

/**
 * IntoDisplayNode is used to represent an Into LanguageObject
 */
public class IntoDisplayNode extends DisplayNode {

    /**
     *  IntoDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param into The Into language object used to construct this display node.
     */
    public IntoDisplayNode(DisplayNode parentNode, Into into) {
        this.parentNode = parentNode;
        this.languageObject = into;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For an AssignmentStatementDisplayNode,
     *  there are two children - (1) ElementSymbol variable, (2) either a Command or an Expression.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        Into into = (Into)(this.getLanguageObject());

        //----------------------------------------------------
        // GroupSymbol child
        //----------------------------------------------------
        GroupSymbol gSymbol = into.getGroup();
        if(gSymbol!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,gSymbol));
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

        //displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INTO));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        // Add the Group Symbol Child
        DisplayNode node = (DisplayNode)childNodeList.get(0);
        displayNodeList.add(node);
        
//        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
    }

}

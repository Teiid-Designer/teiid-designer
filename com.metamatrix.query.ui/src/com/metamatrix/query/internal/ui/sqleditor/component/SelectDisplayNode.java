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
import com.metamatrix.query.sql.lang.Select;
import com.metamatrix.query.sql.symbol.SelectSymbol;

/**
 * The <code>SelectDisplayNode</code> class is used to represent a Query's SELECT clause.
 */
public class SelectDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   SelectDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param select the query language object used to construct this display node.
     */
    public SelectDisplayNode(DisplayNode parentNode, Select select) {
        this.parentNode = parentNode;
        this.languageObject = select;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Select Clause supports Expressions
     */
    @Override
    public boolean supportsExpression() {
        return true;
    }

    /**
     * Select Clause supports Elements
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
        Select select = (Select)(this.getLanguageObject());
        List symbols = select.getSymbols();
        int indent = this.getIndentLevel();
        if( DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	indent++;  // Children are indented +1 from the keyword
        }
        
	    Iterator iter = symbols.iterator();
        while( iter.hasNext() ) {
			SelectSymbol symbol = (SelectSymbol) iter.next();
            DisplayNode dnNode = DisplayNodeFactory.createDisplayNode(this,symbol);
            dnNode.setIndentLevel( indent );
            childNodeList.add( dnNode );
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

        Select select = (Select)(this.getLanguageObject());
                     
        // indent this SELECT command itself, if it is other than the outermost SELECT
//        if(DisplayNodeUtils.isClauseIndentOn() && isDescendentOfSetQuery() ) {
//            displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, getIndentLevel() + 1 ) );
//        }

        int childIndent = this.getIndentLevel();
        childIndent++;
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.SELECT));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        if(select.isDistinct()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DISTINCT));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }
        
        // process children
        List children = this.getChildren();
        int nChildren = children.size();

        for(int i=0; i<nChildren; i++) {
            DisplayNode childNode = (DisplayNode) children.get(i);            
            
            if(childNode.hasDisplayNodes()) {
                // Only tab the first symbol
                if(i==0 && DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                List lstChildren = childNode.getDisplayNodeList();                
                DisplayNodeUtils.setIndentLevel( lstChildren, childIndent );                
                displayNodeList.addAll(lstChildren);
            } else {
                childNode.setIndentLevel( childIndent );
                // Only tab the first symbol
                if(i==0 && DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, childIndent ) );
                }
                displayNodeList.add(childNode);
            }
            
            // Add comma if not the last child
            if(i!=(nChildren-1)) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            }
        }

//        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        
//        if(DisplayNodeUtils.isClauseCROn() 
//        && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)  
//        && getIndentLevel() == 0 ) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }
	}

    public boolean isDescendentOfSetQuery() {  
        DisplayNode displayNode = this;

        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
//            System.out.println("[SelectDisplayNode.isOutermostSelect] parentNode: " + parentNode.getClass().getName() ); //$NON-NLS-1$ 
            if(parentNode instanceof SetQueryDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        
        return false;
    }

}


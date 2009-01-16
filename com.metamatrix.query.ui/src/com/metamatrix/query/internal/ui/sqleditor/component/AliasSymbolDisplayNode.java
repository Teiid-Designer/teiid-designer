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
import com.metamatrix.query.sql.symbol.AliasSymbol;

/**
 * The <code>AliasSymbolDisplayNode</code> class is used to represent AliasSymbols.
 */
public class AliasSymbolDisplayNode extends ExpressionDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   AliasSymbolDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param symbol the query language object used to construct this display node.
     */
    public AliasSymbolDisplayNode(DisplayNode parentNode, AliasSymbol symbol) {
        this.parentNode = parentNode;
        this.languageObject = symbol;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode. The Child node is
     *   the ElementSymbol.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        AliasSymbol aliasSymbol = (AliasSymbol)this.getLanguageObject();
        childNodeList.add(DisplayNodeFactory.createDisplayNode(this,aliasSymbol.getSymbol()));

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

        AliasSymbol aliasSymbol = (AliasSymbol)this.getLanguageObject();
        // Get the childNode
        DisplayNode child = (DisplayNode)childNodeList.get(0);
//        int indent = child.getIndentLevel();

        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }
        // Keyword AS
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.AS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

		// If alias is reserved word, it needs to be in quotes to escape it
		String alias = aliasSymbol.getName();
		if(ReservedWords.isReservedWord(alias)) {
			alias = "\"" + alias + "\"";    //$NON-NLS-1$ //$NON-NLS-2$ 
		}

        displayNodeList.add(new AliasNameDisplayNode(this, alias));

    }

    private class AliasNameDisplayNode extends DisplayNode {
    	private String name;
    	
        public AliasNameDisplayNode(AliasSymbolDisplayNode theParent,
                                    String theName) {
            parentNode = theParent;
            name = theName;
            childNodeList = new ArrayList();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this, name));
            displayNodeList = new ArrayList();
        	displayNodeList.add(childNodeList.get(0));
        }

        @Override
        public boolean isInExpression() {
        	return true;
        }
        
    }
    
}


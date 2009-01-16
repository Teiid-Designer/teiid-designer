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
import com.metamatrix.query.sql.lang.Drop;

/**
 * The <code>DropCommandDisplayNode</code> class is used to represent a Drop command.
 */
public class DropCommandDisplayNode extends DisplayNode {
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  CreateCommandDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param create The Create language object used to construct this display node.
     */
    public DropCommandDisplayNode(DisplayNode parentNode, Drop drop) {
        this.parentNode = parentNode;
        this.languageObject = drop;
        createDisplayNodeList();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        Drop drop = (Drop)(this.getLanguageObject());
        
		// position of the child in childNodeList
		//int childIndex = 0;
		
        //int indent = this.getIndentLevel();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DROP));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.TABLE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,drop.getTable().getName()));
	}

    /**
     * Check whether a string is considered a reserved word or not.  Subclasses
     * may override to change definition of reserved word.
     * @param string String to check
     * @return True if reserved word
     */
    protected boolean isReservedWord(String string) {
    	if(string == null) {
    	    return false;
    	}
   		return ReservedWords.isReservedWord(string);
    }
	   
}

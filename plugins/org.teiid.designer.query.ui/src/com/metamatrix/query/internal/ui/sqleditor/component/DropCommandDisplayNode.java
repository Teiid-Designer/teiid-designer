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
import org.teiid.query.sql.lang.Drop;

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

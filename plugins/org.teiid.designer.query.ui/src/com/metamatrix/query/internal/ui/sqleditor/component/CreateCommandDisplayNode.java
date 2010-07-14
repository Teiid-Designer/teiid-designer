/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.teiid.core.types.DataTypeManager;
import org.teiid.language.SQLConstants;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.symbol.ElementSymbol;

/**
 * The <code>CreateCommandDisplayNode</code> class is used to represent a Create command.
 */
public class CreateCommandDisplayNode extends DisplayNode {
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  CreateCommandDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param create The Create language object used to construct this display node.
     */
    public CreateCommandDisplayNode(DisplayNode parentNode, Create create) {
        this.parentNode = parentNode;
        this.languageObject = create;
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
        Create create = (Create)(this.getLanguageObject());
        
		// position of the child in childNodeList
		//int childIndex = 0;
		
        //int indent = this.getIndentLevel();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.CREATE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.LOCAL));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.TEMPORARY));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SQLConstants.Reserved.TABLE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,create.getTable().getName()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        
        List columns = create.getColumns();
        if(columns != null) {
            Iterator iter = columns.iterator();
            while(iter.hasNext()) {
                ElementSymbol column = (ElementSymbol)iter.next();
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,column.getShortName()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,DataTypeManager.getDataTypeName(column.getType())));
                if(iter.hasNext()) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                }
            }
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
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
   		return SQLConstants.isReservedWord(string);
    }
	   
}

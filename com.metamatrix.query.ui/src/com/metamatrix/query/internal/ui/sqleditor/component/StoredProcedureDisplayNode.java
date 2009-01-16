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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.StoredProcedure;
import com.metamatrix.query.sql.lang.SPParameter;

/**
 * The <code>StoredProcedureDisplayNode</code> class is used to represent a StoredProcedure command.
 */
public class StoredProcedureDisplayNode extends DisplayNode {
	
    private static final char ID_ESCAPE_CHAR = '\"';
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  StoredProcedureDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param insert The StoredProcedure language object used to construct this display node.
     */
    public StoredProcedureDisplayNode(DisplayNode parentNode, StoredProcedure storedProc) {
        this.parentNode = parentNode;
        this.languageObject = storedProc;
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
        StoredProcedure storedProc = (StoredProcedure)(this.getLanguageObject());
        
		// position of the child in childNodeList
		//int childIndex = 0;
		
        //int indent = this.getIndentLevel();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.EXEC));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,storedProc.getProcedureName()));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        
        List params = storedProc.getInputParameters();
        if(params!=null) {
            Iterator iter = params.iterator();
            
        	while(iter.hasNext()) {
        		SPParameter param = (SPParameter)iter.next();
        		
        		if (storedProc.displayNamedParameters()) {
        			String part = escapeSinglePart(param.getParameterSymbol().getShortName());
                	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,part));
                	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE)); 
                	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,"=")); //$NON-NLS-1$
                	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE)); 
        		}	        		
            	if(param.getExpression() == null) {
        			if(param.getName()!=null) {
        				String part = escapeSinglePart(storedProc.getParamFullName(param));
         				displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,part));
        			} else {
         				displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,"?")); //$NON-NLS-1$
       				}
        		} else {
        			String pVal = param.getExpression().toString();
        			displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,pVal));
        		}
        		if(iter.hasNext()) {
        			displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
        			displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		}
        	}
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
        
		if(storedProc.getOption()!=null) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,storedProc.getOption()));
		} 
	}
    
    private String escapeSinglePart(String part) {
    	if(isReservedWord(part)) {
    	    return ID_ESCAPE_CHAR + part + ID_ESCAPE_CHAR;
    	}
  	 	return part;
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

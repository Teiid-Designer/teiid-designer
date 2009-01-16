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

import java.util.*;

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.Option;

/**
 * The <code>OptionDisplayNode</code> class is used to represent a Query's OPTION clause.
 */
public class OptionDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   OptionDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param option the query language object used to construct this display node.
     */
    public OptionDisplayNode(DisplayNode parentNode, Option option) {
        this.parentNode = parentNode;
        this.languageObject = option;
        createChildNodes();
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.
     */
    private void createChildNodes() {
        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        //if(childNodeList.size()==0) return;

        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.OPTION));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        	indent++;
//        }

        Option option = (Option)(this.getLanguageObject());
        if(option.getShowPlan()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.SHOWPLAN));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        if(option.getPlanOnly()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.PLANONLY));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }
        if(option.getDebug()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DEBUG));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }        
        
        List makeDepGroups = option.getDependentGroups();
        if(makeDepGroups != null && makeDepGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.MAKEDEP));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
            Iterator iter = makeDepGroups.iterator();
            String group = (String) iter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
            while(iter.hasNext()) {
                group = (String) iter.next();   
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));                
            }    
        }

        List makeNotDepGroups = option.getNotDependentGroups();
        if(makeNotDepGroups != null && makeNotDepGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.MAKENOTDEP));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
            Iterator iter = makeNotDepGroups.iterator();
            String group = (String) iter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
            while(iter.hasNext()) {
                group = (String) iter.next();   
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));                
            }    
        }

        Collection noCacheGroups = option.getNoCacheGroups();
        if(noCacheGroups != null && noCacheGroups.size() > 0) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOCACHE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

            Iterator gIter = noCacheGroups.iterator();
            String group = (String) gIter.next();
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
            while(gIter.hasNext()) {
                group = (String) gIter.next();   
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));                
            }    
        }else if(option.isNoCache()){
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NOCACHE));
        }
        
        // No options were set, omit option
        if(displayNodeList.size()==2) {
        	displayNodeList.clear();
        }

//        if(isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
//        }
	}

}


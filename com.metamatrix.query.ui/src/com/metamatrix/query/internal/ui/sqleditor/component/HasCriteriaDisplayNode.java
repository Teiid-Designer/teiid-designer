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
import com.metamatrix.query.sql.proc.HasCriteria;

/**
 * The <code>HasCriteriaDisplayNode</code> class is used to represent 
 * a HasCriteria LanguageObject.
 */
public class HasCriteriaDisplayNode extends DisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  HasCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param hasCriteria The HasCriteria language object used to construct this display node.
     */
    public HasCriteriaDisplayNode(DisplayNode parentNode, HasCriteria hasCriteria) {
        this.parentNode = parentNode;
        this.languageObject = hasCriteria;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a HasCriteriaDisplayNode,
     *  there is a single child, the CriteriaSelector
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        HasCriteria hasCrit = (HasCriteria)this.getLanguageObject();
	    childNodeList.add(DisplayNodeFactory.createDisplayNode(this,hasCrit.getSelector()));
 
        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList( );
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();

//        int indent = this.getIndentLevel();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.HAS));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        // Add the CriteriaSelector DisplayNodes
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }
	}    

}

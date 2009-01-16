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

import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.CompoundCriteria;
import com.metamatrix.query.sql.lang.Criteria;

/**
 * The <code>CompoundCriteriaDisplayNode</code> class is used to represent CompoundCriteria.
 */
public class CompoundCriteriaDisplayNode extends LogicalCriteriaDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   CompoundCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param criteria the query language object used to construct this display node.
     */
    public CompoundCriteriaDisplayNode(DisplayNode parentNode, CompoundCriteria criteria) {
        this.parentNode = parentNode;
        this.languageObject = criteria;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean supportsCriteria() {
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
//        int indent = this.getIndentLevel();

        CompoundCriteria compoundCriteria = (CompoundCriteria)this.getLanguageObject();
        Iterator iter = compoundCriteria.getCriteria().iterator();
        while( iter.hasNext() ) {
            Criteria criteria = (Criteria)iter.next();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,criteria));
        }

        // Build the Display Node List
        createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        if(childNodeList.size()==0) return;

        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        CompoundCriteria compoundCriteria = (CompoundCriteria)this.getLanguageObject();

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));

        // Criteria 1
        DisplayNode child = (DisplayNode)childNodeList.get(0);
        if( child.hasDisplayNodes() ) {
                displayNodeList.addAll(child.getDisplayNodeList());
        } else {
                displayNodeList.add(child);
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));

	    // Criterias 2->n
	    String opStr = (compoundCriteria.getOperator() == CompoundCriteria.AND) ? ReservedWords.AND : ReservedWords.OR;
	    for ( int i = 1; i < compoundCriteria.getCriteriaCount(); i++ ) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,opStr));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE+LTPAREN));
            child = (DisplayNode)childNodeList.get(i);
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	    }

    }

}


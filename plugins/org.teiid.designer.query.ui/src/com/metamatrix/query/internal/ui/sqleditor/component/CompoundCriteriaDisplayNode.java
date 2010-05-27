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
import com.metamatrix.query.sql.ReservedWords;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;

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
        DisplayNode child = childNodeList.get(0);
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
            child = childNodeList.get(i);
            if( child.hasDisplayNodes() ) {
                    displayNodeList.addAll(child.getDisplayNodeList());
            } else {
                    displayNodeList.add(child);
            }
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	    }

    }

}


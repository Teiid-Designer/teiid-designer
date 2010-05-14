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
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.proc.CriteriaSelector;

/**
 * The <code>CriteriaSelectorDisplayNode</code> class is used to represent 
 * a CriteriaSelector LanguageObject.
 */
public class CriteriaSelectorDisplayNode extends DisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  CriteriaSelectorDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param critSelector The CriteriaSelector language object used to construct this display node.
     */
    public CriteriaSelectorDisplayNode(DisplayNode parentNode, CriteriaSelector critSelector) {
        this.parentNode = parentNode;
        this.languageObject = critSelector;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a CriteriaSelectorDisplayNode,
     *  the children are ElementSymbols.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

		CriteriaSelector selector = (CriteriaSelector)this.getLanguageObject();
     	List elements = selector.getElements();
     	if(elements!=null) {
	        Iterator elemIter = elements.iterator();
	        while(elemIter.hasNext()) {
	            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,elemIter.next()));
	        }
     	}
 
        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList(selector);
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList(CriteriaSelector criteriaSelector) {
        displayNodeList = new ArrayList();

        //int indent = this.getIndentLevel();

        int selectorType = criteriaSelector.getSelectorType();

        switch(selectorType) {
        	case CriteriaSelector.COMPARE_EQ:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,EQUALS));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;
        	case CriteriaSelector.COMPARE_GE:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,GE));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.COMPARE_GT:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,GT));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.COMPARE_LE:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LE));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.COMPARE_LT:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LT));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.COMPARE_NE:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,NE));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.IN:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IN));
           		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
           	        		break;        		
        	case CriteriaSelector.IS_NULL:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.IS));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.NULL));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.LIKE:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.LIKE));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        	case CriteriaSelector.BETWEEN:
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.BETWEEN));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        		break;        		
        }

	    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.CRITERIA));
		if(criteriaSelector.hasElements()) {
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.ON));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
	        
	        Iterator childIter = getChildren().iterator();
	        while(childIter.hasNext()) {
	        	DisplayNode node = (DisplayNode)childIter.next();
	        	if(node.hasDisplayNodes()) {
	        		displayNodeList.addAll(node.getDisplayNodeList());
	        	} else {
	        		displayNodeList.add(node);
	        	}
				if(childIter.hasNext()) {
	        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
	        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
				}
	        }
	        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
		}
	} 

}

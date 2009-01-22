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
import com.metamatrix.query.sql.proc.TranslateCriteria;

/**
 * The <code>TranslateCriteriaDisplayNode</code> class is used to represent a TranslateCriteria.
 */
public class TranslateCriteriaDisplayNode extends CriteriaDisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  TranslateCriteriaDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param transCriteria The TranslateCriteria language object used to construct this display node.
     */
    public TranslateCriteriaDisplayNode(DisplayNode parentNode, TranslateCriteria transCriteria) {
        this.parentNode = parentNode;
        this.languageObject = transCriteria;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   Create the child nodes for this type of DisplayNode.  For a TranslateCriteriaDisplayNode,
     *  the children are the translation criteria.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        //int indent = this.getIndentLevel();

    	TranslateCriteria transCriteria = (TranslateCriteria)this.getLanguageObject();
    	if(transCriteria.hasTranslations()) {
	        Iterator critIter = transCriteria.getTranslations().iterator();
	    	while(critIter.hasNext()) {
	            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,critIter.next()));
	    	}
    	}

        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList(transCriteria);
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList(TranslateCriteria transCriteria) {
        displayNodeList = new ArrayList();
        //int indent = this.getIndentLevel();
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.TRANSLATE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

    	DisplayNode node = DisplayNodeFactory.createDisplayNode(this,transCriteria.getSelector());
    	if(node.hasDisplayNodes()) {
    		displayNodeList.addAll(node.getDisplayNodeList());
    	} else {
    		displayNodeList.add(node);
    	}
        
        // Iterate thru the child Criteria nodes
        Iterator iter = getChildren().iterator();
        if(iter.hasNext()) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.WITH));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
        }
        while(iter.hasNext()) {
        	node = (DisplayNode)iter.next();
    		if(node.hasDisplayNodes()) {
    			displayNodeList.addAll(node.getDisplayNodeList());
    		} else {
    			displayNodeList.add(node);
    		}
			if(iter.hasNext()) {
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
			}
			if(!iter.hasNext()) {
        		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
			}
        }
	}    
}

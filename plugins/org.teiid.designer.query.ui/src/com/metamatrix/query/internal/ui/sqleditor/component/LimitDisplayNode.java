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
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.symbol.Constant;

/**
 * The <code>LimitDisplayNode</code> class is used to represent a Query's LIMIT clause.
 */
public class LimitDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   LimitDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param limit the LIMIT clause used to construct this display node.
     */
    public LimitDisplayNode(DisplayNode parentNode, Limit limit) {
        this.parentNode = parentNode;
        this.languageObject = limit;
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

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.LIMIT));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        Limit limit = (Limit)getLanguageObject();
        if (limit.getOffset() != null) {
            if (limit.getOffset() instanceof Constant) {
                Integer offset = (Integer)((Constant)limit.getOffset()).getValue();
                if (offset.intValue() > 0) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,limit.getOffset()));
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                }
            } else {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,limit.getOffset()));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
            }
        }
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,limit.getRowLimit()));
	}

}


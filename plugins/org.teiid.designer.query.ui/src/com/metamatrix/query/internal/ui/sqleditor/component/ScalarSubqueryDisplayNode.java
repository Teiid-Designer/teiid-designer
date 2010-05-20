/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.ScalarSubquery;

/**
 * The <code>ScalarSubqueryDisplayNode</code> class is used to represent a ScalarSubquery.
 */
public class ScalarSubqueryDisplayNode extends ExpressionDisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   ScalarSubqueryDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param function the query language object used to construct this display node.
     */
    public ScalarSubqueryDisplayNode(DisplayNode parentNode, ScalarSubquery scalarSubquery) {
        this.parentNode = parentNode;
        this.languageObject = scalarSubquery;
        createChildNodes();
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

		ScalarSubquery scalarSubquery = (ScalarSubquery)this.getLanguageObject();
		Command command = scalarSubquery.getCommand();
		if(command==null) {
			childNodeList.add(DisplayNodeFactory.createDisplayNode(this,"ERROR")); //$NON-NLS-1$
		} else {
			childNodeList.add(DisplayNodeFactory.createDisplayNode(this,command));
		}

		// Build the Display Node List
		createDisplayNodeList();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
		displayNodeList = new ArrayList();
//		int indent = this.getIndentLevel();

		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));

		// Add Command DisplayNodes to the list
		DisplayNode child = (DisplayNode)childNodeList.get(0);
		if( child.hasDisplayNodes() ) {
				displayNodeList.addAll(child.getDisplayNodeList());
		} else {
				displayNodeList.add(child);
		}

		displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
	}

}

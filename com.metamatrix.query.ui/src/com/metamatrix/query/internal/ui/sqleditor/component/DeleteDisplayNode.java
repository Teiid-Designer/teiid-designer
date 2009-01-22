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
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.Delete;
import com.metamatrix.query.sql.lang.Option;
import com.metamatrix.query.sql.symbol.GroupSymbol;

/**
 * The <code>DeleteDisplayNode</code> class is used to represent a DELETE command.
 */
public class DeleteDisplayNode extends DisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  DeleteDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param update The delete language object used to construct this display node.
     */
    public DeleteDisplayNode(DisplayNode parentNode, Delete delete) {
        this.parentNode = parentNode;
        this.languageObject = delete;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Delete Clause supports Groups
     */
    @Override
    public boolean supportsGroup() {
        return true;
    }
    
    /**
     * Delete Clause supports criteria
     */    
    @Override
    public boolean supportsCriteria() {
        return true;
    }    
    
    /**
     * Returns the DisplayNode for the Clause if there is one, null if not
     */
    public DisplayNode getClauseDisplayNode(int clauseType) {
        if(clauseType<SELECT || clauseType>OPTION) {
            return null;
        }
        Iterator iter = childNodeList.iterator();
        while (iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            switch(clauseType) {
                case WHERE:
                    if(node instanceof WhereDisplayNode) {
                        return node;
                    }
                    break;
                case OPTION:
                    if(node instanceof OptionDisplayNode) {
                        return node;
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }
    
    /**
     * Returns the DisplayNode clause at a given index.  The entire clause is returned -
     *   WHERE or OPTION 
     */
    public DisplayNode getClauseAtIndex(int index) {
        List nodes = DisplayNodeUtils.getDisplayNodesAtIndex(displayNodeList,index);
        // if the index is between two clauses, return the second one
        int nNodes = nodes.size();
        if(nNodes==0) {
            return null;
        } else if(nNodes==1) {
        	return DisplayNodeUtils.getClauseForNode( (DisplayNode)nodes.get(0) );
        } else if(nNodes==2) {
        	DisplayNode clause1 = DisplayNodeUtils.getClauseForNode( (DisplayNode)nodes.get(0) );
        	DisplayNode clause2 = DisplayNodeUtils.getClauseForNode( (DisplayNode)nodes.get(1) );
        	if(clause2!=null) {
        		return clause2;
        	}
      		return clause1;
        } else {
            return null;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////    
    
    /**
     *  Create the child nodes for this type of DisplayNode.  For a QueryDisplayNode,
     *  the children are the clauses of the Query.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        Delete delete = (Delete)(this.getLanguageObject());
//        int indent = this.getIndentLevel();

        //---------------------------------------------------------------
        // Add clauses to childNodeList - will have at most two children
        //---------------------------------------------------------------
        GroupSymbol group = delete.getGroup();
        // Group Symbol
        if(group!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
        }

/*
 * jhTODO: this version treats the WHERE as just a child of the DELETE FROM.
 *         I think it needs to be a keyword with its own display node,
 *         and the criteria should go under it, indented.
 */
		// Criteria
        Criteria criteria = delete.getCriteria();
		if(criteria != null) {
            childNodeList.add(DisplayNodeFactory.createWhereDisplayNode(this,criteria));			
		}

		// Option
        Option option = delete.getOption();
		if(option != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,option));
		}

        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
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
        
		// DELETE keywords
        int indent = this.getIndentLevel();
        indent++;
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.DELETE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.FROM));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        // Get child DisplayNodes
        Iterator iter = this.getChildren().iterator();
                
        // Insert Group Child DisplayNode
        if(iter.hasNext()) {
	        DisplayNode groupChild = (DisplayNode)iter.next();
	        //indent = groupChild.getIndentLevel();
	        if( groupChild.hasDisplayNodes() ) {
	            displayNodeList.addAll(groupChild.getDisplayNodeList());
	        } else {
	            displayNodeList.add(groupChild);
	        }
	        if(iter.hasNext()) {
		        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
		        if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
		        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
	                if( DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
	                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
	                }
		        }
	        }
        }
        
        //-------------------------------------------------------------------
        // If there are additional children, it's the Criteria and/or Option
        //-------------------------------------------------------------------
		while(iter.hasNext()) {
	        DisplayNode childNode = (DisplayNode) iter.next();
	        if( childNode.hasDisplayNodes() ) {
	            displayNodeList.addAll(childNode.getDisplayNodeList());
	        } else {
	            displayNodeList.add(childNode);
	        }
		}
    }
}

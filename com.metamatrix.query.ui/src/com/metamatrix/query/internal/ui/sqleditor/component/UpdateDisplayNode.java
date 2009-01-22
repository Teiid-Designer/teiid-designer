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
import com.metamatrix.query.sql.lang.AbstractCompareCriteria;
import com.metamatrix.query.sql.lang.CompareCriteria;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.Option;
import com.metamatrix.query.sql.lang.SetClause;
import com.metamatrix.query.sql.lang.Update;
import com.metamatrix.query.sql.symbol.GroupSymbol;
/**
 * The <code>UpdateDisplayNode</code> class is used to represent a UPDATE command.
 */
public class UpdateDisplayNode extends DisplayNode {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  UpdateDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param update The update language object used to construct this display node.
     */
    public UpdateDisplayNode(DisplayNode parentNode, Update update) {
        this.parentNode = parentNode;
        this.languageObject = update;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Update Clause supports Groups
     */
    @Override
    public boolean supportsGroup() {
        return true;
    }
    
    /**
     * Update Clause supports Criteria
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
        Update update = (Update)(this.getLanguageObject());

        //----------------------------------------------------
        // Add clauses to childNodeList
        //----------------------------------------------------
        GroupSymbol group = update.getGroup();
		
        
        if(group!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
        }

        for (SetClause clause : update.getChangeList().getClauses()) {	
            Criteria setCriteria = new CompareCriteria(clause.getSymbol(), AbstractCompareCriteria.EQ, clause.getValue()); 
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,setCriteria));
        }

        Criteria criteria = update.getCriteria();
		if(criteria != null) {
            childNodeList.add(DisplayNodeFactory.createWhereDisplayNode(this,criteria));
		}

        Option option = update.getOption();
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
		// position of the child in childNodeList
		int childIndex = 0;
        int indent = this.getIndentLevel();
     
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
            // where will this be used?
            indent++;
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.UPDATE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }

        Iterator iter = this.getChildren().iterator();
        
        //---------------------------------------------------------        
        // get the display node for the group in the Insert
        //---------------------------------------------------------        
        DisplayNode groupChild = (DisplayNode) iter.next();
        indent = groupChild.getIndentLevel();
        childIndex++;
        indent++;

        if( groupChild.hasDisplayNodes() ) {

            if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
            }

            List lstChildren = groupChild.getDisplayNodeList();                
            DisplayNodeUtils.setIndentLevel( lstChildren, indent );                
            displayNodeList.addAll(lstChildren);
        } else {

            groupChild.setIndentLevel( indent );
            if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
            }

            displayNodeList.add(groupChild);
        }

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
		if(DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
		}

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.SET));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        
        if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }
        
        
        //---------------------------------------------------------        
        // Iterate thru the changeList
        //---------------------------------------------------------        
        Update update = (Update)(this.getLanguageObject());

    	int numCrit = update.getChangeList().getClauses().size();

    	while(iter.hasNext() && childIndex <= numCrit) {
	        // get the display node for the group in the Insert
	        DisplayNode criteriaChild = (DisplayNode) iter.next();

            if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
            }
            
	        if( criteriaChild.hasDisplayNodes() ) {

                List lstChildren = criteriaChild.getDisplayNodeList();                
                DisplayNodeUtils.setIndentLevel( lstChildren, indent );                
                displayNodeList.addAll(lstChildren);
	        } else {
                criteriaChild.setIndentLevel( indent );
                
	            displayNodeList.add(criteriaChild);
	        }
	        if(childIndex!=numCrit) {
	        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA));
	        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
	        }
	        childIndex++;
    	}

        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
		
        if( DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this) ) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
		}

        //-------------------------------------------------------------------
        // If there are additional children, it's the Criteria and/or Option
        //-------------------------------------------------------------------
		while(iter.hasNext()) {
	        // get the display node for the group in the Insert
	        DisplayNode childNode = (DisplayNode) iter.next();
	        if( childNode.hasDisplayNodes() ) {
	            displayNodeList.addAll(childNode.getDisplayNodeList());
	        } else {
	            displayNodeList.add(childNode);
	        }
		}
    }
}

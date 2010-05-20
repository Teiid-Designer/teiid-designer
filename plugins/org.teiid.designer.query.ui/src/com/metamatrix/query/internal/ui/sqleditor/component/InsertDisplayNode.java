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
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 * The <code>InsertDisplayNode</code> class is used to represent a INSERT command.
 */
public class InsertDisplayNode extends DisplayNode {
	
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *  InsertDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param insert The insert language object used to construct this display node.
     */
    public InsertDisplayNode(DisplayNode parentNode, Insert insert) {
        this.parentNode = parentNode;
        this.languageObject = insert;
        createChildNodes();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Insert Clause supports Groups
     */
    @Override
    public boolean supportsGroup() {
        return true;
    }
    
    /**
     * Insert Clause supports Elements
     */
    @Override
    public boolean supportsElement() {
        return true;
    }

    /**
     * Insert Clause supports Criteria
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
     *   OPTION 
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
     *   Create the child nodes for this type of DisplayNode.  For a InsertDisplayNode,
     *  the children are parts of the Insert.
     */
    private void createChildNodes() {
        childNodeList = new ArrayList();
        Insert insert = (Insert)(this.getLanguageObject());
//        int indent = this.getIndentLevel();
        
        if ( insert.getQueryExpression() != null) {
            this.createChildNodesForInsertIntoSubquery();
            return;
        }

            
        //----------------------------------------------------
        // First childNode is the Group being Inserted on
        //----------------------------------------------------
        GroupSymbol group = insert.getGroup();
        if(group!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
        }
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//        	indent++;
//        }
		
        //----------------------------------------------------
        // Next nVars childNodes are variables
        //----------------------------------------------------
        Iterator varIter = insert.getVariables().iterator();
        while( varIter.hasNext() ) {
			Expression element = (Expression) varIter.next();
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,element));
        }
        
        //----------------------------------------------------
        // Next nVals childNodes are values
        //----------------------------------------------------
        Iterator valIter = insert.getValues().iterator();
        while( valIter.hasNext() ) {
            Expression value = (Expression)valIter.next();
            if(value==null) {
                childNodeList.add(DisplayNodeFactory.createDisplayNode(this,ERROR));
            } else {
                childNodeList.add(DisplayNodeFactory.createDisplayNode(this,value));
            }
        }

        //----------------------------------------------------
        // Final childNode is Option node
        //----------------------------------------------------
		//indent = this.getIndentLevel();
        Option option = insert.getOption();
		if(option != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,option));
		}

        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeList();
    }	

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
        // position of the child in childNodeList
        int childIndex = 0;     
        
        int indent = this.getIndentLevel();
        indent++;
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INSERT));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INTO));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        Insert insert = (Insert)(this.getLanguageObject());

        List childNodes = this.getChildren();
        int totalChildNodes = childNodes.size();
                
        GroupSymbol group = insert.getGroup();
        if(group!=null) {
            // get the display node for the group in the Insert
            DisplayNode groupChild = (DisplayNode)childNodes.get(childIndex);
            childIndex++;
            if( groupChild.hasDisplayNodes() ) {
                displayNodeList.addAll(groupChild.getDisplayNodeList());
            } else {
                displayNodeList.add(groupChild);
            }        
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        }

        if( DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }
        
        if(!insert.getVariables().isEmpty()) {
            
            if(DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
            }

            
            int varNum = insert.getVariables().size();
            for(int i=0; i<varNum; i++) {
                DisplayNode varChild = (DisplayNode)childNodes.get(i+childIndex);
                //indent = varChild.getIndentLevel();
                // Add left paren in front of first
                if(i==0) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
                }
                // add the displayNodes for current variable                
                if(varChild.hasDisplayNodes()) {
                    displayNodeList.addAll(varChild.getDisplayNodeList());
                } else {
                    displayNodeList.add(varChild);
                }
                // if not the last, add a comma separator
                if( i != (varNum-1) ) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                }               
            }
            if(varNum>0) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                childIndex+=varNum;
            }
        }

        
        if( DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        }
               
        if(insert.getValues() != null) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.VALUES));        
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            
            if( DisplayNodeUtils.isClauseCROn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
                if( DisplayNodeUtils.isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
                    displayNodeList.addAll( DisplayNodeUtils.getIndentNodes( this, indent ) );
                }
            }
    
            int valNum = insert.getValues().size();
            for(int i=0; i<valNum; i++) {
                DisplayNode valChild = (DisplayNode)childNodes.get(i+childIndex);
                //indent = valChild.getIndentLevel();
                // Add left paren in front of first
                if(i==0) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,LTPAREN));
                }
                // add the displayNodes for current variable                
                if(valChild.hasDisplayNodes()) {
                    displayNodeList.addAll(valChild.getDisplayNodeList());
                } else {
                    displayNodeList.add(valChild);
                }
                // if not the last, add a comma separator
                if( i != (valNum-1) ) {
                    displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,COMMA+SPACE));
                }               
            }
            if(valNum>0) {
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,RTPAREN));
                displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
                childIndex+=valNum;
            }
        }
        
        //----------------------------------------------
        // If there is another child, it's the Option
        //----------------------------------------------
        if(childIndex<totalChildNodes) {
            // get the display node for the group in the Insert
            DisplayNode childNode = (DisplayNode) childNodes.get(childIndex);
            if( childNode.hasDisplayNodes() ) {
                displayNodeList.addAll(childNode.getDisplayNodeList());
            } else {
                displayNodeList.add(childNode);
            }
        }
        
    }    
    
    /**
     *   Create the child nodes for this type of DisplayNode.  For a InsertDisplayNode,
     *  the children are parts of the Insert.
     */
    private void createChildNodesForInsertIntoSubquery() {
        childNodeList = new ArrayList();
        Insert insert = (Insert)(this.getLanguageObject());
//        int indent = this.getIndentLevel();

        //----------------------------------------------------
        // First childNode is the Group being Inserted on
        //----------------------------------------------------
        GroupSymbol group = insert.getGroup();
        if(group!=null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,group));
        }
//        if(isClauseIndentOn() && !DisplayNodeUtils.isWithinNoClauseIndentNode(this)) {
//          indent++;
//        }
        
        //----------------------------------------------------
        // Next childNode is the subquery
        //----------------------------------------------------
        Command command = insert.getQueryExpression();
        
        
        childNodeList.add( DisplayNodeFactory.createDisplayNode( this, command ) );

        //----------------------------------------------------
        // Final childNode is Option node
        //----------------------------------------------------
        //indent = this.getIndentLevel();
        Option option = insert.getOption();
        if(option != null) {
            childNodeList.add(DisplayNodeFactory.createDisplayNode(this,option));
        }

        //----------------------------------------------------
        // Create the Display Node List
        //----------------------------------------------------
        createDisplayNodeListForInsertIntoSubquery();
    }   

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeListForInsertIntoSubquery() {
        displayNodeList = new ArrayList();
        
        int indent = this.getIndentLevel();
        indent++;
        
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INSERT));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,ReservedWords.INTO));
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        Insert insert = (Insert)(this.getLanguageObject());
        
        GroupSymbol group = insert.getGroup();        
        displayNodeList.add( DisplayNodeFactory.createDisplayNode( this, group ) );
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));

        Command query = insert.getQueryExpression();
        displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,CR));
        displayNodeList.add( DisplayNodeFactory.createDisplayNode( this, query ) );
                       
	}    


}

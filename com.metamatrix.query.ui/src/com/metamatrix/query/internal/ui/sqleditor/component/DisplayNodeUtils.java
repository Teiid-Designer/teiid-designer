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
import java.util.List;
import java.util.StringTokenizer;

import com.metamatrix.api.exception.query.QueryParserException;
import com.metamatrix.query.sql.ProcedureReservedWords;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.visitor.CommandCollectorVisitor;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;


/**
 * The <code>DisplayNodeUtils</code> class contains static methods that are useful
 * in working with DisplayNodes.
 */
public final class DisplayNodeUtils implements DisplayNodeConstants {

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    /**
    * Method to determine whether the specified index immediately follows a comma,
    * ignoring spaces.
    * @param displayNodes the list of display nodes to check
    * @param index the index to test
    * @return true if the index immediately follows a comma, false if not.
    */
    public static boolean isIndexRightAfterComma(List displayNodes, int index) {
        // Get DisplayNodes before the specified index
        List leadingNodes = getDisplayNodesBeforeIndex(displayNodes,index);

        int nLeading = leadingNodes.size();
        for(int i=nLeading-1; i>=0; i--) {
            DisplayNode node = (DisplayNode)leadingNodes.get(i);
            if( node instanceof SeparatorDisplayNode ) {
                String nodeStr = node.toString();
                if(nodeStr.indexOf(COMMA)!=-1) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
	}

    /**
    * Method to determine whether the specified index immediately preceeds a comma,
    * ignoring spaces.
    * @param displayNodes the list of display nodes to check
    * @param index the index to test
    * @return true if the index is immediately before a comma, false if not.
    */
    public static boolean isIndexRightBeforeComma(List displayNodes, int index) {
        // Get DisplayNodes after the specified index
        List trailingNodes = getDisplayNodesAfterIndex(displayNodes,index);

        // If any of the trailing nodes are not a Separator, not at end
        Iterator iter = trailingNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if( node instanceof SeparatorDisplayNode ) {
                String nodeStr = node.toString();
                if(nodeStr.indexOf(COMMA)!=-1) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
	}

    /**
     * Returns a list of DisplayNodes (potentially 2) at a given index.  Returns an
     * empty list if the index is out of range.
     * @param displayNodes the list of display nodes to check
     * @param index the index to test
     * @return the List of nodes that the Index is within
	 */
    public static List getDisplayNodesAtIndex(List displayNodes,int index) {
        ArrayList validNodes = new ArrayList(0);

        Iterator iter = displayNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if( node.isAnywhereWithin(index) ) {
                validNodes.add(node);
            }
        }

        return validNodes;
    }

    /**
     * Returns a list of DisplayNodes before the display Node that the arg index
     * is within.
     * @param displayNodes the list of displayNodes
     * @param the index
     * @return the list of displayNodes before the index
     */
    public static List getDisplayNodesBeforeIndex(List displayNodes, int index) {
        List validNodes = new ArrayList(0);
        Iterator dnIter = displayNodes.iterator();
        while(dnIter.hasNext()) {
            DisplayNode node = (DisplayNode)dnIter.next();
            int nodeEndIndex = node.getEndIndex();
            if(nodeEndIndex<index) {
                validNodes.add(node);
            } else {
                break;
            }
        }
        return validNodes;
    }

    /**
     * Returns a list of DisplayNodes after the display Node that the arg index
     * is within.
     * @param displayNodes the list of displayNodes
     * @param the index
     * @return the list of displayNodes after the index
     */
    public static List getDisplayNodesAfterIndex(List displayNodes, int index) {
        List validNodes = new ArrayList(0);
        Iterator dnIter = displayNodes.iterator();
        while(dnIter.hasNext()) {
            DisplayNode node = (DisplayNode)dnIter.next();
            int nodeStartIndex = node.getStartIndex();
            if(nodeStartIndex>=index) {
                validNodes.add(node);
            }
        }
        return validNodes;
    }

	/**
	 * Method to check whether the supplied node has any SymbolDisplayNodes in it.
     * @param node the query display node to check
     * @return true if the node has at least one symbol, false if not.
	 */
    public static boolean hasSymbol(DisplayNode node) {
        if( node == null ) {
            return false;
        }
        
        if( isSymbolNode(node) ) {
            return true;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( isSymbolNode(displayNode) ) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Method to check whether the supplied node has any ExpressionDisplayNodes in it.
     * @param node the query display node to check
     * @return true if the node has at least one expression, false if not.
	 */
    public static boolean hasExpression(DisplayNode node) {
        if( node instanceof ExpressionDisplayNode ) {
            return true;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( getExpressionForNode(displayNode)!=null ) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Method to get the starting index of the next SymbolDisplayNode following
     * the supplied index.  Returns -1 if there is no SymbolDisplayNode following
     * the index.
     * @param node the query display node to check
     * @param index the index to test
     * @return the starting index of the next display node, -1 if there isnt any.
	 */
    public static int getStartIndexOfNextSymbol(DisplayNode node,int index) {
        if( isSymbolNode(node) ) {
            if(node.isAnywhereWithin(index)) {
                return node.getStartIndex();
            }
            return -1;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( isSymbolNode(displayNode) ) {
                if(displayNode.getStartIndex()>=index) {
                    return displayNode.getStartIndex();
                }
            }
        }
        return -1;
    }

	/**
	 * Method to get the ending index of the SymbolDisplayNode preceding the
     * supplied index.  Returns -1 if there is no SymbolDisplayNode preceding
     * the index.
     * @param node the query display node to check
     * @param index the index to test
     * @return the ending index of the preceding display node, -1 if there isnt any.
	 */
    public static int getEndIndexOfPreviousSymbol(DisplayNode node,int index) {
        int prevEnd = -1;
        if( isSymbolNode(node) ) {
            if(node.isAnywhereWithin(index)) {
                return node.getEndIndex()+1;
            }
            return prevEnd;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( displayNode.getEndIndex()>=index ) {
                break;
            }
            if( isSymbolNode(displayNode) ) {
                prevEnd = displayNode.getEndIndex()+1;
            }
        }
        return prevEnd;
    }

	/**
	 * Method to get the starting index of the next ExpressionDisplayNode following
     * the supplied index.  Returns -1 if there is no ExpressionDisplayNode following
     * the index.
     * @param node the query display node to check
     * @param index the index to test
     * @return the starting index of the next expression display node, -1 if there isnt any.
	 */
    public static int getStartIndexOfNextExpression(DisplayNode node,int index) {
        if( node instanceof ExpressionDisplayNode ) {
            if(node.isAnywhereWithin(index)) {
                return node.getStartIndex();
            }
            return -1;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( displayNode.isInExpression() ) {
                if(displayNode.getStartIndex()>=index) {
                    return displayNode.getStartIndex();
                }
            }
        }
        return -1;
    }

	/**
	 * Method to get the ending index of the ExpressionDisplayNode preceding the
     * supplied index.  Returns -1 if there is no ExpressionDisplayNode preceding
     * the index.
     * @param node the query display node to check
     * @param index the index to test
     * @return the ending index of the preceding expression display node, -1 if there isnt any.
	 */
    public static int getEndIndexOfPreviousExpression(DisplayNode node,int index) {
        int prevEnd = -1;
        if( node instanceof ExpressionDisplayNode ) {
            if(node.isAnywhereWithin(index)) {
                return node.getEndIndex()+1;
            }
            return prevEnd;
        }
        List nodes = node.getDisplayNodeList();
        Iterator iter = nodes.iterator();
        while(iter.hasNext()) {
            DisplayNode displayNode = (DisplayNode)iter.next();
            if( displayNode.getEndIndex()>=index ) {
                break;
            }
            if( displayNode.isInExpression() ) {
                prevEnd = displayNode.getEndIndex()+1;
            }
        }
        return prevEnd;
    }

    /**
    * Method to determine whether the index is at the "start" of a clause.  By start,
    * meaning that the index can only be preceeded by Keywords and non-comma separators.
    * @param the clause to test
    * @param index the index to test
    * @return true if the index is at the beginning of a clause, false if not.
    */
    public static boolean isIndexAtClauseStart(DisplayNode clauseNode, int index) {
        if( clauseNode!=null && isClauseNode(clauseNode) ) {
            // Get Clause DisplayNodes before the specified index
            List displayNodes = clauseNode.getDisplayNodeList();
            List leadingNodes = getDisplayNodesBeforeIndex(displayNodes,index);

            // If any of the leading nodes are not Keyword or Separator, not at beginning
            Iterator iter = leadingNodes.iterator();
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if( !(node instanceof KeywordDisplayNode) && !(node instanceof SeparatorDisplayNode) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
	}

    /**
    * Method to determine whether the index is at the end of a clause.
    * @param clauseNode the clause display Node to test
    * @param index the index to test
    * @return true if the index is at the end of a clause, false if not.
    */
    public static boolean isIndexAtClauseEnd(DisplayNode clauseNode, int index) {
        if( clauseNode!=null && isClauseNode(clauseNode) ) {
            // Get Clause DisplayNodes after the specified index
            List displayNodes = clauseNode.getDisplayNodeList();
            List trailingNodes = getDisplayNodesAfterIndex(displayNodes,index);

            // If any of the trailing nodes are not a Separator, not at end
            Iterator iter = trailingNodes.iterator();
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if( !(node instanceof SeparatorDisplayNode) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
	}

    /**
    * Method to determine whether a DisplayNode is a Clause.
    * @param clauseNode the clause display Node to test
    * @return true if the node is a clause, false if not.
    */
    public static boolean isClauseNode(DisplayNode clauseNode) {
        if(clauseNode instanceof SelectDisplayNode) {
            return true;
        } else if(clauseNode instanceof FromDisplayNode) {
            return true;
        } else if(clauseNode instanceof WhereDisplayNode) {
            return true;
        } else if(clauseNode instanceof GroupByDisplayNode) {
            return true;
        } else if(clauseNode instanceof HavingDisplayNode) {
            return true;
        } else if(clauseNode instanceof OrderByDisplayNode) {
            return true;
        } else if(clauseNode instanceof OptionDisplayNode) {
            return true;
        } else {
            return false;
        }
    }

    /**
    * Method to determine whether a DisplayNode is a Command.
    * @param node the display Node to test
    * @return true if the node is a command, false if not.
    */
    public static boolean isCommandNode(DisplayNode node) {
        if(node instanceof QueryDisplayNode) {
            return true;
        } else if(node instanceof SetQueryDisplayNode) {
            return true;
        } else if(node instanceof UpdateDisplayNode) {
            return true;
        } else if(node instanceof InsertDisplayNode) {
            return true;
        } else if(node instanceof DeleteDisplayNode) {
            return true;
        } else if(node instanceof StoredProcedureDisplayNode) {
            return true;
        } else if(node instanceof CreateUpdateProcedureDisplayNode) {
            return true;
        } else {
            return false;
        }
    }

    /**
    * Method to determine whether a specified type can be inserted before the specified DisplayNode.
    * @param node the display Node to test
    * @param type the type
    * @return true if the type can be inserted before the specified displayNode, false if not.
    */
    public static boolean canInsertBefore(DisplayNode node, int type) {
    	return canInsertNextTo(node,type);
    }
    
    /**
    * Method to determine whether a specified type can be inserted after the specified DisplayNode.
    * @param node the display Node to test
    * @param type the type
    * @return true if the type can be inserted after the specified displayNode, false if not.
    */
    public static boolean canInsertAfter(DisplayNode node, int type) {
    	return canInsertNextTo(node,type);
    }

    /**
    * Method to determine whether a specified type can be inserted next to the specified DisplayNode.
    * @param node the display Node to test
    * @param type the type
    * @return true if the type can be inserted next to the specified displayNode, false if not.
    */
    private static boolean canInsertNextTo(DisplayNode node, int type) {
    	boolean result = false;
        if(type==EXPRESSION) {
        	// If node is an expression or within an expression, cant insert before it
        	// Must use the builder
        	if(!node.isInExpression()) {
	        	// Get the clause that this node is in
	        	DisplayNode clauseNode = getNodeTypeForNode(node,CLAUSE);
	        	if(clauseNode!=null) {
	        		if( clauseNode.supportsExpression() ) {
	        			result=true;
	        		}
	        	}
        	}
        } else if(type==CRITERIA) {
        	// If node is a criteria or within a criteria, cant insert before it
        	// Must use the builder
        	if(!node.isInCriteria()) {
	        	// Get the command that this node is in
	        	DisplayNode commandNode = getNodeTypeForNode(node,COMMAND);
	        	if(commandNode instanceof QueryDisplayNode) {
	        		QueryDisplayNode queryNode = (QueryDisplayNode)commandNode;
	        		DisplayNode whereClause = queryNode.getClauseDisplayNode(WHERE);
	        		if(whereClause==null) {
        				result=true;
	        		}
	        	} else if (commandNode instanceof DeleteDisplayNode) {
	        		DeleteDisplayNode deleteNode = (DeleteDisplayNode)commandNode;
	        		DisplayNode whereClause = deleteNode.getClauseDisplayNode(WHERE);
	        		if(whereClause==null) {
        				result=true;
	        		}
	        	} else if (commandNode instanceof UpdateDisplayNode) {
	        		UpdateDisplayNode updateNode = (UpdateDisplayNode)commandNode;
	        		DisplayNode whereClause = updateNode.getClauseDisplayNode(WHERE);
	        		if(whereClause==null) {
        				result=true;
	        		}
	        	}
        	}
        } 
        return result;
    }

    /**
     * Determines whether the index is anywhere within a DisplayNode of the specified 
     * type, given a List of DisplayNodes
     * @param nodeList the display Node list to insert into
     * @param index the cursor index
     * @param nodeType the type of DisplayNode
     * @return true if the cursor index is within the specified type, false if not
     */
    public static boolean isIndexWithin(List nodeList, int index, int nodeType) {
    	boolean result = false;
        List nodes = getDisplayNodesAtIndex(nodeList,index);
        //--------------------------------------------------
        // Index is between nodes, look at both
        //--------------------------------------------------
        if(nodes.size()==2) {
            // Get the index nodes
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            // If second node is within the type, true
            if(isNodeWithin(node2,nodeType)) {
            	result=true;
            // If first node is within the type, true
            } else if(isNodeWithin(node1,nodeType)) {
            	result=true;
            // Otherwise, false
            } else {
            	result=false;
            }
        //--------------------------------------------------
        // Index is within a node
        //--------------------------------------------------
        } else if(nodes.size()==1) {
        	// return command for the node
        	if(isNodeWithin((DisplayNode)nodes.get(0),nodeType)) {
        		result=true;
        	}
        } 
        return result;
    }

    /**
    * Method to determine whether a DisplayNode is within ancestor DisplayNode.
    * @param displayNode the display Node to test
    * @param ancestorNode the ancestor display Node
    * @return true if the node is within a ancestor displayNode, false if not.
    */
    public static boolean isWithinDisplayNode(DisplayNode displayNode,DisplayNode ancestorNode) {
    	boolean result = false;
    	if(displayNode!=null && ancestorNode!=null) {
    		// If provided nodes are same, its within
	        if(displayNode.equals(ancestorNode)) {
	            result = true;
	        } else {
		        DisplayNode parentNode = displayNode.getParent();
		        while(parentNode!=null) {
		            if(parentNode.equals(ancestorNode)) {
		                result=true;
		                break;
		            }
		            parentNode = parentNode.getParent();
		        }
	        }
    	}
        return result;
    }
    
    /**
    * Method to determine whether a DisplayNode is within a List of ancestor DisplayNodes.
    * @param displayNode the display Node to test
    * @param ancestorNodes the ancestor display Node List
    * @return true if the node is within any of the supplied ancestor displayNode, false if not.
    */
    public static boolean isWithinDisplayNodes(DisplayNode displayNode,List ancestorNodes) {
    	boolean result = false;
    	if(displayNode!=null && ancestorNodes!=null) {
    		Iterator iter = ancestorNodes.iterator();
    		while(iter.hasNext()) {
    			DisplayNode thisAncestor = (DisplayNode)iter.next();
    			if( isWithinDisplayNode(displayNode,thisAncestor) ) {
    				result = true;
    				break;
    			}
    		}
    	}
        return result;
    }

    /**
    * Method to determine whether a DisplayNode is within a DisplayNode of a specified type.
    * @param node the display Node to test
    * @param nodeType the type of node
    * @return true if the node is within a displayNode of specified type, false if not.
    */
    public static boolean isNodeWithin(DisplayNode node, int nodeType) {
        if(nodeType==EXPRESSION) {
            return isWithinExpression(node);
        } else if(nodeType==CRITERIA) {
            return isWithinCriteria(node);
        } else if(nodeType==EDITABLE_CRITERIA) {
            return isWithinEditableCriteria(node);
        } else if(nodeType==SELECT) {
            return isWithinSelect(node);
        } else if(nodeType==FROM) {
            return isWithinFrom(node);
        } else if(nodeType==COMMAND) {
            return isWithinCommand(node);
        } else {
            return false;
        }
    }

    /**
    * Method to determine whether a DisplayNode is within a Criteria.
    * @param displayNode the display Node to test
    * @return true if the node is within a criteria displayNode, false if not.
    */
    public static boolean isWithinCriteria(DisplayNode displayNode) {
    	if( getCriteriaForNode(displayNode)!=null ) {
    		return true;
    	}
  		return false;
    }
    
    /**
    * Method to determine whether a DisplayNode is within an 'editable' Criteria, which is
    * a Criteria that the CriteriaBuilder can handle.
    * @param displayNode the display Node to test
    * @return true if the node is within a criteria displayNode, false if not.
    */
    public static boolean isWithinEditableCriteria(DisplayNode displayNode) {
        CriteriaDisplayNode criteriaDN = getCriteriaForNode(displayNode);
        return isEditableCriteria(criteriaDN);
    }
    
    public static boolean isEditableCriteria(CriteriaDisplayNode criteriaDN) {
        boolean result = false;
        if(criteriaDN!=null) {
            // Must be one of the editable Criteria
            if(criteriaDN instanceof CompoundCriteriaDisplayNode ||
               criteriaDN instanceof CompareCriteriaDisplayNode || 
               criteriaDN instanceof IsNullCriteriaDisplayNode || 
               criteriaDN instanceof MatchCriteriaDisplayNode ||
               criteriaDN instanceof SetCriteriaDisplayNode) {
                result=true;
            }
        }
        return result;
    }

    /**
    * Method to determine whether a DisplayNode is within an Expression.
    * @param displayNode the display Node to test
    * @return true if the node is within an expression displayNode, false if not.
    */
    public static boolean isWithinExpression(DisplayNode displayNode) {
    	if( getExpressionForNode(displayNode)!=null ) {
    		return true;
    	}
   		return false;
    }

    /**
    * Method to determine whether a DisplayNode is within a Select clause.
    * @param displayNode the display Node to test
    * @return true if the node is within a Select displayNode, false if not.
    */
    public static boolean isWithinSelect(DisplayNode displayNode) {
        if(displayNode instanceof SelectDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof SelectDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }
    
    /**
    * Method to determine whether a DisplayNode is within a From clause.
    * @param displayNode the display Node to test
    * @return true if the node is within a From displayNode, false if not.
    */
    public static boolean isWithinFrom(DisplayNode displayNode) {
        if(displayNode instanceof FromDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof FromDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
    * Method to determine whether a DisplayNode is within a Command.
    * @param displayNode the display Node to test
    * @return true if the node is within a Command displayNode, false if not.
    */
    public static boolean isWithinCommand(DisplayNode displayNode) {
        if(isCommandNode(displayNode)) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(isCommandNode(parentNode)) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }
    
    /**
    * Method to determine whether a DisplayNode is within a Statement.
    * @param displayNode the display Node to test
    * @return true if the node is within a Statement displayNode, false if not.
    */
    public static boolean isWithinStatement(DisplayNode displayNode) {
        if(displayNode instanceof StatementDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof StatementDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
    * Method to determine whether a DisplayNode is within a Procedure.
    * @param displayNode the display Node to test
    * @return true if the node is within a CreateUpdateProcedureDisplayNode, false if not.
    */
    public static boolean isWithinProcedure(DisplayNode displayNode) {
        if(displayNode instanceof CreateUpdateProcedureDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof CreateUpdateProcedureDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
    * Method to determine whether a DisplayNode is within a Subquery.
    * @param displayNode the display Node to test
    * @return true if the node is within a subquery displayNode, false if not.
    */
    public static boolean isWithinSubQueryNode(DisplayNode displayNode) {
        if(displayNode instanceof SubqueryFromClauseDisplayNode || 
           displayNode instanceof ScalarSubqueryDisplayNode ||
           displayNode instanceof SubqueryCompareCriteriaDisplayNode ||
           displayNode instanceof SubquerySetCriteriaDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof SubqueryFromClauseDisplayNode ||
               parentNode instanceof ScalarSubqueryDisplayNode ||
               parentNode instanceof SubqueryCompareCriteriaDisplayNode ||
               parentNode instanceof SubquerySetCriteriaDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }
    
    /**
     * Method to determine whether a DisplayNode is within a Setquery .
     * @param displayNode the display Node to test
     * @return true if the node is within a subquery displayNode, false if not.
     */
     public static boolean isWithinSetQueryNodeBeforeSubQueryNode(DisplayNode displayNode) {
         boolean hitSubQueryFirst = false;
         if(displayNode instanceof SetQueryDisplayNode) {
             return true;
         }
         DisplayNode parentNode = displayNode.getParent();
         while(parentNode!=null) {
             if(parentNode instanceof SubqueryFromClauseDisplayNode ||
                parentNode instanceof ScalarSubqueryDisplayNode ||
                parentNode instanceof SubqueryCompareCriteriaDisplayNode ||
                parentNode instanceof SubquerySetCriteriaDisplayNode) {
                 hitSubQueryFirst = true;
             } else if( !hitSubQueryFirst && parentNode instanceof SetQueryDisplayNode) {
                 return true;
             }
             parentNode = parentNode.getParent();
         }
         return false;
     }
    
    /**
    * Method to determine whether a DisplayNode is within another DisplayNode
    * which should have clause indenting turned off.
    * @param displayNode the display Node to test
    * @return true if the node is within a non-indentable node, false if not.
    */
    public static boolean isWithinNoClauseIndentNode(DisplayNode displayNode) {
        if(displayNode instanceof SubqueryFromClauseDisplayNode || 
           displayNode instanceof ScalarSubqueryDisplayNode ||
           displayNode instanceof SubqueryCompareCriteriaDisplayNode ||
           displayNode instanceof SubquerySetCriteriaDisplayNode ||
           displayNode instanceof CommandStatementDisplayNode ||
           displayNode instanceof AssignmentStatementDisplayNode) {
            return true;
        }
        if(displayNode instanceof QueryDisplayNode && displayNode.getParent() instanceof LoopStatementDisplayNode) {
            return true;
        }
        DisplayNode parentNode = displayNode.getParent();
        while(parentNode!=null) {
            if(parentNode instanceof SubqueryFromClauseDisplayNode ||
               parentNode instanceof ScalarSubqueryDisplayNode ||
               parentNode instanceof SubqueryCompareCriteriaDisplayNode ||
               parentNode instanceof SubquerySetCriteriaDisplayNode ||
               parentNode instanceof CommandStatementDisplayNode ||
               parentNode instanceof AssignmentStatementDisplayNode) {
                return true;
            }
            if(parentNode instanceof QueryDisplayNode && parentNode.getParent() instanceof LoopStatementDisplayNode) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
    * Get the SubQuery Command DisplayNode that this DisplayNode is within.
    * @param displayNode the display Node to test
    * @return the subQuery DisplayNode if available, null if not.
    */
    public static DisplayNode getSubQueryCommandDisplayNode(DisplayNode displayNode) {
    	if( !isWithinSubQueryNode(displayNode) ) {
    		return null;
    	}
    	DisplayNode result = null;
        if(displayNode instanceof SubqueryFromClauseDisplayNode) {
            result = (DisplayNode)displayNode.getChildren().get(0);
        } else if(displayNode instanceof SubquerySetCriteriaDisplayNode) {
            result = (DisplayNode)displayNode.getChildren().get(1);
        } else if(displayNode instanceof ScalarSubqueryDisplayNode) {
            result = (DisplayNode)displayNode.getChildren().get(0);
        } else if(displayNode instanceof SubqueryCompareCriteriaDisplayNode) {
            result = (DisplayNode)displayNode.getChildren().get(1);
        } else {
	        DisplayNode parentNode = displayNode.getParent();
	        while(parentNode!=null) {
	            if(parentNode instanceof SubqueryFromClauseDisplayNode) {
            		result = (DisplayNode)parentNode.getChildren().get(0);
            		break;
	            } else if(parentNode instanceof SubquerySetCriteriaDisplayNode) {
            		result = (DisplayNode)parentNode.getChildren().get(1);
            		break;
                } else if(parentNode instanceof ScalarSubqueryDisplayNode) {
                    result = (DisplayNode)parentNode.getChildren().get(0);
                    break;
                } else if(parentNode instanceof SubqueryCompareCriteriaDisplayNode) {
                    result = (DisplayNode)parentNode.getChildren().get(1);
                    break;
	            }
	            parentNode = parentNode.getParent();
	        }
        }
        return result;
    }

    /**
     * Get all of the CommandDisplayNodes associated with the provided command displayNode node.
     * @param displayNode the command display Node for which to get associated command nodes
     * @return the list of Command DisplayNodes
     */
    public static List getAllCommandDisplayNodes(DisplayNode commandDisplayNode) {
        List inputCommandNodes = new ArrayList();
        
        if(commandDisplayNode instanceof SetQueryDisplayNode) {
            List setQueryNodes = ((SetQueryDisplayNode)commandDisplayNode).getQueryDisplayNodes();
            inputCommandNodes.addAll(setQueryNodes);
        } else {
            inputCommandNodes.add(commandDisplayNode);
        }
        
        List allCommandNodes = new ArrayList();
        Iterator iter = inputCommandNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode currentCommandNode = (DisplayNode)iter.next();
            // passed displayNode must be CommandDisplayNode
            if(isCommandNode(currentCommandNode)) {
                // add the supplied commandNode
                allCommandNodes.add(currentCommandNode);
                
                // Get language Object and count the total commands
                Command command = (Command)currentCommandNode.getLanguageObject();
                List allCommandObjs = CommandCollectorVisitor.getCommands(command);
    
                // count up the target number of commands
                int nTarget = allCommandObjs.size();
                
                // If there are no embedded commands, stop looking
                if(allCommandNodes.size()<nTarget) {
                    // Get all of the lowest level display nodes
                    List allDisplayNodes = currentCommandNode.getDisplayNodeList();
                    Iterator dnIter = allDisplayNodes.iterator();
                    while(dnIter.hasNext()) {
                        DisplayNode dn = (DisplayNode)dnIter.next();
                        List parentCommandNodes = getParentCommandNodes(dn);
                        Iterator pnIter = parentCommandNodes.iterator();
                        while(pnIter.hasNext()) {
                            DisplayNode pNode = (DisplayNode)pnIter.next();
                            if(allCommandNodes.contains(pNode)) {
                                allCommandNodes.add(pNode);
                            }
                        }
                        if(allCommandNodes.size()==nTarget) {
                            break;
                        }
                    }
                }
            }
        }
        return allCommandNodes;
    }

    /**
     * get the entire parent command node heirarchy for the supplied displayNode.
     * @param displayNode the display Node 
     * @return the list of all parent Command DisplayNodes
     */
    public static List getParentCommandNodes(DisplayNode displayNode) {
         List parentCommandNodes = new ArrayList();
         if(isWithinCommand(displayNode)) {
             DisplayNode parentNode = displayNode.getParent();
             while(parentNode!=null) {
                 if(isCommandNode(parentNode)) {
                     parentCommandNodes.add(parentNode);
                 }
                 parentNode = parentNode.getParent();
             }
             
         }
         return parentCommandNodes;
    }

    /**
    * Method to determine whether a DisplayNode is a Symbol Node.  This includes
    * SymbolDisplayNodes, ElementSymbolDisplayNodes and UnaryFromClauseDisplayNodes.
    * @param node the display Node to test
    * @return true if the node is a symbol node, false if not.
    */
    public static boolean isSymbolNode(DisplayNode node) {
        if(node instanceof SymbolDisplayNode || node instanceof ElementSymbolDisplayNode ||
           node instanceof UnaryFromClauseDisplayNode) {
            return true;
        }
        return false;
    }

    /**
    * Test whether the supplied DisplayNode is a keyword
    * @param displayNode the DisplayNode to test.
    * @return 'true' if the supplied DisplayNode is a keyword, 'false' if not.
    */
    public static boolean isKeyword(DisplayNode displayNode) {
    	boolean isKeyword = false;
    	if( displayNode instanceof KeywordDisplayNode || 
    	    displayNode instanceof FunctionNameDisplayNode ) {
        	isKeyword=true;
    	} 
    	return isKeyword;
    }

    /**
    * Test whether the supplied DisplayNode is a special Variable
    * @param displayNode the DisplayNode to test.
    * @return 'true' if the supplied DisplayNode is a special variable, 'false' if not.
    */
    public static boolean isSpecialVar(DisplayNode displayNode) {
    	boolean isSPVar = false;
        if(displayNode instanceof ElementSymbolDisplayNode) {
        	// Special Variables are within Procedures
        	if( isWithinProcedure(displayNode) ) {
	        	ElementSymbol eSymbol = (ElementSymbol)((ElementSymbolDisplayNode)displayNode).getLanguageObject();
	        	String eName = eSymbol.getName();
	        	int dotIndex = eName.indexOf('.');
	        	// Test whether the Element name starts with "INPUT", "CHANGING" or "VARIABLES"
	        	if(dotIndex!=-1) {
		        	String startText = eName.substring(0,dotIndex);
		        	if(startText.equalsIgnoreCase(ProcedureReservedWords.INPUT) || 
		        	   startText.equalsIgnoreCase(ProcedureReservedWords.CHANGING) ||
		        	   startText.equalsIgnoreCase(ProcedureReservedWords.VARIABLES) ) {
		        		isSPVar=true;
		        	}
	        	} 
        	}
        } 
    	return isSPVar;
    }

    /**
    * Method to determine if a given type can be inserted into a DisplayNode List
    * at a given index.
    * @param nodeList the display Node list to insert into
    * @param index the desired insertion index
    * @param nodeType the desired type to insert.
    * @return 'true' if the type can be inserted, 'false' if not.
    */
    public static boolean isInsertAllowed(List nodeList, int index, int nodeType) {
    	boolean result = false;
        //------------------------------------------------------------------
        // Find the nodes that the index is between (0,1,or 2)
        //------------------------------------------------------------------
        List nodes = getDisplayNodesAtIndex(nodeList,index);
        //--------------------------------------------------
        // Index is between nodes, look at both
        //--------------------------------------------------
        if(nodes.size()==2) {
            // Get the index nodes
            DisplayNode node1 = (DisplayNode)nodes.get(0);
            DisplayNode node2 = (DisplayNode)nodes.get(1);
            // Can type be inserted before the second node.
            if(DisplayNodeUtils.canInsertBefore(node2,nodeType)) {
            	result=true;
            // Can type be inserted after the first node
            } else if(DisplayNodeUtils.canInsertAfter(node1,nodeType)) {
            	result=true;
            // Otherwise, false
            } else {
            	result=false;
            }
        //--------------------------------------------------
        // Index is within a node
        //--------------------------------------------------
        } else if(nodes.size()==1) {
        	// can type be inserted after node
        	if(DisplayNodeUtils.canInsertAfter((DisplayNode)nodes.get(0),nodeType)) {
        		result=true;
        	}
        } 
    	return result;
    }
    
    /**
    * Method to get the specified DisplayNode type, given a DisplayNode.
    * @param node the display Node to test
    * @param nodeType the type of node
    * @return the node of the specified type (if applicable), null if not.
    */
    public static DisplayNode getNodeTypeAtIndex(List nodeList, int index, int nodeType) {
    	DisplayNode result = null;
        //------------------------------------------------------------------
        // Find the listNodes that the index is between (0,1,or 2)
        //------------------------------------------------------------------
        List nodes = getDisplayNodesAtIndex(nodeList,index);
        int nNodes = nodes.size();
        // index is within a Node, return type for it
        if (nNodes==1) {
            DisplayNode node = (DisplayNode)nodes.get(0);
            result = getNodeTypeForNode(node,nodeType);
        // index is between two Nodes, first check the second node, then the first
        } else if (nNodes==2) {
            DisplayNode node = (DisplayNode)nodes.get(1);
            result = getNodeTypeForNode(node,nodeType);
            if(result!=null) {
                return result;
            }
            node = (DisplayNode)nodes.get(0);
            result = getNodeTypeForNode(node,nodeType);
        }
        return result;
    }

    /**
    * Method to get a node of the specified DisplayNode type, given a DisplayNode.
    * The nodes ancestry is walked up until a node of the right type is found, else
    * null is returned.
    * @param node the display Node to test
    * @param nodeType the type of node
    * @return the node of the specified type (if applicable), null if not.
    */
    public static DisplayNode getNodeTypeForNode(DisplayNode node, int nodeType) {
        if(nodeType==EXPRESSION) {
            return getExpressionForNode(node);
        } else if(nodeType==CRITERIA) {
            return getCriteriaForNode(node);
        } else if(nodeType==COMMAND) {
            return getCommandForNode(node);
        } else if(nodeType==CLAUSE) {
            return getClauseForNode(node);
        } else {
            return null;
        }
    }

    /**
     * Get the CriteriaDisplayNode for a given DisplayNode.  If the DisplayNode
     * is itself a CriteriaDisplayNode, return it.  If not, return the first parent
     * that is a CriteriaDisplayNode.  Otherwise, return null.
     * @param node the Display node to check.
     * @return the criteria display Node for this node.
     */
    public static CriteriaDisplayNode getCriteriaForNode(DisplayNode node) {
        if(node instanceof CriteriaDisplayNode) {
            return (CriteriaDisplayNode)node;
        } else if(node.isInCriteria()) {
            return node.getCriteria();
        } else {
            return null;
        }
    }
    
    /**
     * Get the CriteriaDisplayNode for a given DisplayNode.  If the DisplayNode
     * is itself a CriteriaDisplayNode, return it.  If not, return the first parent
     * that is a CriteriaDisplayNode.  Otherwise, return null.
     * @param node the Display node to check.
     * @return the criteria display Node for this node.
     */
    public static CriteriaDisplayNode getCriteriaInNode(DisplayNode node) {
        if(node instanceof CriteriaDisplayNode) {
            return (CriteriaDisplayNode)node;
        }
        
        DisplayNode nextNode = null;
        for( Iterator iter = node.getChildren().iterator(); iter.hasNext(); ) {
            nextNode = (DisplayNode)iter.next();
            return getCriteriaInNode(nextNode);
        }

        return null;
    }

    /**
     * Get the ExpressionDisplayNode for a given DisplayNode.  If the DisplayNode
     * is itself an ExpressionDisplayNode, return it.  If not, return the first parent
     * that is an ExpressionDisplayNode.  Otherwise, return null.
     * @param node the Display node to check.
     * @return the Expression display Node for this node.
     */
    public static ExpressionDisplayNode getExpressionForNode(DisplayNode node) {
        if(node==null) return null;
        if(node instanceof ElementSymbolDisplayNode) {
            if(node.isInExpression()) {
            	return node.getExpression();
            }
            return null;
        } else if(node instanceof ExpressionDisplayNode && !(node instanceof ScalarSubqueryDisplayNode)) {
            return (ExpressionDisplayNode)node;
        } else if(node.isInExpression()) {
            return node.getExpression();
        } else {
            return null;
        }
    }

    /**
     * Get the command DisplayNode for a given DisplayNode.  If the DisplayNode
     * is within a subquery, the subquery command is returned.  If not, return the first parent
     * that is a commnad DisplayNode is returned.  Otherwise, return null.
     * @param node the Display node to check.
     * @return the command display Node for this node.
     */
    public static DisplayNode getCommandForNode(DisplayNode node) {
        if(node==null) return null;
        DisplayNode result = null;
        if( !isWithinSetQueryNodeBeforeSubQueryNode(node) && isWithinSubQueryNode(node) ) {
        	result = getSubQueryCommandDisplayNode(node);
        } else {
	        DisplayNode parentNode = node.getParent();
	        while(parentNode!=null) {
	            if( isCommandNode(parentNode) ) {
            		result = parentNode;
            		break;
	            } else if( parentNode instanceof CommandStatementDisplayNode ) {
                    List childList = ((CommandStatementDisplayNode)parentNode).getChildren();
                    result = (DisplayNode)childList.get(0);
                    break;
                } else {
	            	parentNode = parentNode.getParent();
	            }
	        }
        }
        return result;
    }

    /**
     * Get the clause DisplayNode for a given DisplayNode.  If not with
     * a clause, return null.
     * @param node the Display node to check.
     * @return the clause display Node for this node.
     */
    public static DisplayNode getClauseForNode(DisplayNode node) {
        DisplayNode result = null;
        if(node!=null) {
	        if( isClauseNode(node) ) {
	        	result = node;
	        } else {
		        DisplayNode parentNode = node.getParent();
		        while(parentNode!=null) {
		            if( isClauseNode(parentNode) ) {
	            		result = parentNode;
	            		break;
		            }
		            parentNode = parentNode.getParent();
		        }
	        }
        }
        return result;
    }

    /**
    * Get the Error indices, given a List of DisplayNodes and a QueryParserException.
    * The DisplayNode list is scanned, and if the QueryParserException can be determined
    * to be within them, the start and end index of the displayNode range is returned.
    * A two item List is always returned, the startIndex and the endIndex.  -1 values
    * indicate that the index could not be matched to a display node.
    * @param displayNodes the List of DisplayNodes to scan
    * @param qpe the parser exception
    * @return the List containing the errorStart and errorEnd indices
    */
    public static List getErrorIndices(List displayNodes, QueryParserException qpe) {
		//-----------------------------------
		// Reset the start and end Indices
		//-----------------------------------
        int errorStart = -1;
        int errorEnd = -1;
        List indexList = new ArrayList(2);
        indexList.add(0,new Integer(errorStart));
        indexList.add(1,new Integer(errorEnd));
        if(qpe.isLocationKnown()) {
            // Use the Line and Column to find the DisplayNode, then set start and end
            int errorLine = qpe.getLine();
            int errorColumn = qpe.getColumn();
            int index = getIndexForLineColumn(getString(displayNodes),errorLine,errorColumn);
            Iterator iter = displayNodes.iterator();
            while( iter.hasNext() ) {
                DisplayNode node = (DisplayNode)iter.next();
                if(node.isAnywhereWithin(index)) {
                    errorStart = node.getStartIndex();
                    errorEnd = node.getEndIndex()+1;
                    // Check the next node and set to its end (index may be on a border)
                    if(iter.hasNext()) {
                        node = (DisplayNode)iter.next();
                        if(node.isAnywhereWithin(index)) {
                            errorEnd = node.getEndIndex()+1;
                        }
                    }
        			indexList.set(0,new Integer(errorStart));
        			indexList.set(1,new Integer(errorEnd));
                    return indexList;
                }
            }
        }
        return indexList;
    }

    /**
    * Get the Error indices, given a List of DisplayNodes and a match string.
    * The DisplayNode list is scanned, and if the match string can be determined
    * to be within them, the start and end index of the displayNode range is returned.
    * A two item List is always returned, the startIndex and the endIndex.  -1 values
    * indicate that the index could not be matched to a display node.
    * @param displayNodes the List of DisplayNodes to scan
    * @param str the match string
    * @return the List containing the errorStart and errorEnd indices
    */
    public static List getErrorIndices(List displayNodes, String str) {
		//-----------------------------------
		// Reset the start and end Indices
		//-----------------------------------
        int errorStart = -1;
        int errorEnd = -1;
        List indexList = new ArrayList(2);
        indexList.add(0,new Integer(errorStart));
        indexList.add(1,new Integer(errorEnd));
        Iterator iter = displayNodes.iterator();
        while( iter.hasNext() ) {
            DisplayNode node = (DisplayNode)iter.next();
            if(node.hasDisplayNodes()) {
                Iterator dnIter = node.getDisplayNodeList().iterator();
                while(dnIter.hasNext()) {
                    DisplayNode dn = (DisplayNode)dnIter.next();
                    if(dn.toString().equals(str)) {
                        errorStart = dn.getStartIndex();
                        errorEnd = dn.getEndIndex()+1;
                        indexList.set(0,new Integer(errorStart));
                        indexList.set(1,new Integer(errorEnd));
                        return indexList;
                    }
                }
            } else {
                if(node.toString().equals(str)) {
                    errorStart = node.getStartIndex();
                    errorEnd = node.getEndIndex()+1;
                    indexList.set(0,new Integer(errorStart));
                    indexList.set(1,new Integer(errorEnd));
                    return indexList;
                }
            }
        }
        return indexList;
    }

	/**
	* Get the overall string index, given the line and column indices from a parser exception.
	* @param sqlString the sqlString to search
	* @param line the line number
	* @param column the column number within the provided line
	* @return the index corresponding to the line and column number
	*/
	private static int getIndexForLineColumn(String sqlString,int line,int column) {
		StringTokenizer st = new StringTokenizer(sqlString,CR);
		int nLines = st.countTokens();
		if(nLines<line) return -1;
		int count = 0;
		for(int i=0; i<line; i++) {
			if(i+1==line) count+=column;
			else count+=st.nextToken().length()+1;
		}
		return count-1;
	}
	
	/**
	* Get the line index for the given overall string index.
	* @param sqlString the sqlString to search
	* @param index the overall index withing the string
	* @return the line number that the index is within
	*/
	public static int getLineForIndex(String sqlString,int index) {
		StringTokenizer st = new StringTokenizer(sqlString,CR);
		int nLines = st.countTokens();
		int nStart = 0;
		int nChars = 0;
		int theLine = -1;
		for(int i=0; i<nLines; i++) {
			nChars = st.nextToken().length();
			int nEnd = nStart + nChars;
			if(index>=nStart && index<nEnd) {
				theLine = i+1;
				break;
			}
			nStart += nChars;
		}
		return theLine;
	}

	/**
	* Get the column index for the given overall string index.
	* @param sqlString the sqlString to search
	* @param index the overall index withing the string
	* @return the column number that the index is at
	*/
	public static int getColumnForIndex(String sqlString,int index) {
		StringTokenizer st = new StringTokenizer(sqlString,CR);
		int nLines = st.countTokens();
		int nStart = 0;
		int nChars = 0;
		int theLine = -1;
		for(int i=0; i<nLines; i++) {
			nChars = st.nextToken().length();
			int nEnd = nStart + nChars;
			if(index>=nStart && index<nEnd) {
				theLine = i+1;
				break;
			}
			nStart += nChars;
		}
		if(theLine!=-1) {
			return (index-nStart);
		}
		return -1;
	}

    /**
     * Returns the String representation for a List of DisplayNodes
     * @param displayNodes a List of DisplayNodes
     * @return the string representation of the DisplayNode List
     */
    public static String getString(List displayNodes) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = displayNodes.iterator();
        while( iter.hasNext() ) {
            sb.append(iter.next().toString());
        }
        return sb.toString();
    }

    /**
     * Returns a boolean representing the state of this preference.
     * @return the 'start clauses on newline' preference truth value
     */
    public static boolean isClauseCROn() {
    	UiPlugin uiPlugin = UiPlugin.getDefault();
    	if(uiPlugin!=null) {
    		return uiPlugin.getPreferenceStore().getBoolean( UiConstants.Prefs.START_CLAUSES_ON_NEW_LINE );
    	} 
   		return true;
    }   
    
    /**
     * Returns a boolean representing the state of this preference.
     * @return the 'indent clause content' preference truth value
     */
    public static boolean isClauseIndentOn() {
    	UiPlugin uiPlugin = UiPlugin.getDefault();
    	if(uiPlugin!=null) {
    		return uiPlugin.getPreferenceStore().getBoolean( UiConstants.Prefs.INDENT_CLAUSE_CONTENT );
    	} 
   		return true;
    }
    
    public static void setIndentLevel( List lstChildren, int indent ) {
        Iterator iter = lstChildren.iterator();
        
        while(iter.hasNext()) {
            DisplayNode childNode = (DisplayNode) iter.next();
            childNode.setIndentLevel( indent );            
        }    
    }
    
    public static List getIndentNodes( DisplayNode dnNode, int iIndentLevel ) {
        ArrayList arylIndentNodes = new ArrayList( iIndentLevel );
        
        for( int i = 0; i < iIndentLevel; i++ ) {
            arylIndentNodes.add(DisplayNodeFactory.createDisplayNode(dnNode,TAB));
        }

        return arylIndentNodes;
    }
}

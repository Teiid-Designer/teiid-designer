/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.SetQuery;


/** This class provides the GroupSymbolFinder the knowledge to know where the cursor is at
 *  so it can make decisions about what group symbols to include in the builders.
 * @since 4.2
 */
public class SqlIndexLocator {
    public static final int UNKNOWN              = -1;
    
    public static final int QUERY                = 10;
//    private static final int SUBQUERY             = 11;
    public static final int SET_QUERY            = 12;
    
    public static final int SELECT                   = 20;
    public static final int FROM                     = 21;
    public static final int WHERE                    = 22;
    public static final int SUBQUERY_FROM_CLAUSE     = 23;
    public static final int SCALAR_SUBQUERY          = 24;
//    private static final int SELECT_IN_EXISTS_CRITERIA = 25;
//    private static final int SELECT_IN_UNION_SELECT    = 26;

    
    public static final int CRITERIA             = 30;
    public static final int EXISTS_CRITERIA      = 31;
    public static final int HAS_CRITERIA         = 32;
    public static final int SUBQUERY_COMPARE_CRITERIA = 33;
    public static final int SUBQUERY_SET_CRITERIA = 34;

    public static final int GROUP_BY             = 40;
    public static final int ORDER_BY             = 41;
    public static final int HAVING               = 42;
    public static final int OPTION               = 43;
    
    public static final int UPDATE               = 50;
    public static final int INSERT               = 51;
    public static final int DELETE               = 52;
    public static final int STORED_PROCEDURE     = 53;
    public static final int CREATE_UPDATE_PROCEDURE = 54;
    
    public static final int COMMAND_STATEMENT    = 60;
    
    private static final int TYPE_UNKNOWN             = -1;
    private static final int TYPE_QUERY               = 0;
//    private static final int TYPE_SUBQUERY            = 1;
    private static final int TYPE_UNION               = 2;
    private static final int TYPE_QUERY_IN_CRITERIA   = 3;
    private static final int TYPE_QUERY_IN_UNION      = 4;
    private static final int TYPE_QUERY_IN_COMMAND      = 5;
    
//    private static final String UNKNOWN_STR             = "Unknown Node"; //$NON-NLS-1$
//    
//    private static final String QUERY_STR                = "Query Node"; //$NON-NLS-1$
////    private static final String SUBQUERY_STR             = "Subquery Node"; //$NON-NLS-1$
//    private static final String SET_QUERY_STR            = "Set Query Node"; //$NON-NLS-1$
//    
//    private static final String SELECT_STR                   = "Select Node"; //$NON-NLS-1$
//    private static final String FROM_STR                     = "From Node"; //$NON-NLS-1$
//    private static final String WHERE_STR                    = "Where Node"; //$NON-NLS-1$
//    private static final String SUBQUERY_FROM_CLAUSE_STR     = "Subquery From Clause Node"; //$NON-NLS-1$
//    private static final String SCALAR_SUBQUERY_STR          = "Scalar Subquery Node"; //$NON-NLS-1$
////    private static final String SELECT_IN_EXISTS_CRITERIA_STR = "Select Node in Exists Criteria Node"; //$NON-NLS-1$
////    private static final String SELECT_IN_UNION_SELECT_STR    = "Select Node in Union Node"; //$NON-NLS-1$
//
//    
//    private static final String CRITERIA_STR                  = "Critiera Node"; //$NON-NLS-1$
//    private static final String EXISTS_CRITERIA_STR           = "Exists Criteria Node"; //$NON-NLS-1$
//    private static final String HAS_CRITERIA_STR              = "Has Criteria Node"; //$NON-NLS-1$
//    private static final String SUBQUERY_COMPARE_CRITERIA_STR = "Subquery Compare Criteria Node"; //$NON-NLS-1$
//    private static final String SUBQUERY_SET_CRITERIA_STR     = "Subquery Set Criteria Node"; //$NON-NLS-1$
//
//    private static final String GROUP_BY_STR                  = "Group By Node"; //$NON-NLS-1$
//    private static final String ORDER_BY_STR                  = "Order By Node"; //$NON-NLS-1$
//    private static final String HAVING_STR                    = "Having Node"; //$NON-NLS-1$
//    private static final String OPTION_STR                    = "Option Node"; //$NON-NLS-1$
//    
//    private static final String UPDATE_STR                    = "Update Node"; //$NON-NLS-1$
//    private static final String INSERT_STR                    = "Insert Node"; //$NON-NLS-1$
//    private static final String DELETE_STR                    = "Delete Node"; //$NON-NLS-1$
//    private static final String STORED_PROCEDURE_STR          = "Stored Procedure Node"; //$NON-NLS-1$
//    private static final String CREATE_UPDATE_PROCEDURE_STR   = "Create Update Procedure Node"; //$NON-NLS-1$
//    
//    private static final String COMMAND_STATEMENT_STR            = "Command Statement Node"; //$NON-NLS-1$
//    private static final String IN = " In "; //$NON-NLS-1$

    private int currentIndex = 0;
    private int primaryNodeType = UNKNOWN;
    private int primaryLanguageObjectType = TYPE_UNKNOWN;
    private QueryDisplayComponent displayComponent;
    private DisplayNode commandDisplayNode;
    private DisplayNode primaryIndexDisplayNode;
    private LanguageObject primaryLanguageObject;
//    private String primaryLOTypeString = null;
    private boolean subQuerySelected = false;
    private boolean selectScopeSelected = false;
    private boolean whereSelected = false;
    
    /** 
     * Constructor
     * 
     * @since 4.2
     */
    public SqlIndexLocator(QueryDisplayComponent displayComponent, int index ) {
        super();
        this.displayComponent = displayComponent;

        init(index);
    }
    
    public boolean isSubQuerySelected() {
        return subQuerySelected;
    }
    
    public boolean isUnionSegmentSelected() {
        return primaryLanguageObjectType == TYPE_QUERY_IN_UNION;
    }
    
    public boolean isCriteriaQuerySelected() {
        return primaryLanguageObjectType == TYPE_QUERY_IN_CRITERIA ||
               getDisplayNodeType(primaryIndexDisplayNode) == WHERE;
    }
    
    public boolean isCommandQuerySelected() {
        return primaryLanguageObjectType == TYPE_QUERY_IN_COMMAND;
    }
    
    public boolean hasSubQueries() {
        if( primaryLanguageObject != null && primaryLanguageObject instanceof Query ) {
            return !((Query)primaryLanguageObject).getSubCommands().isEmpty();
        }
        
        return false;
    }
    

    private void init(int index) {

        if(index<0 )
            currentIndex=0;
        else
            this.currentIndex = index;
        
        commandDisplayNode = displayComponent.getCommandDisplayNodeAtIndex(currentIndex);
        
        if( commandDisplayNode == null ) // carretOffset too large??
            commandDisplayNode = displayComponent.getDisplayNode();
        
//        correctIndexForCommandDisplayNode();
        primaryLanguageObject = commandDisplayNode.getLanguageObject();
        setPrimaryIndexDisplayNode();
        setPrimaryLanguageObjectType();
//        System.out.println("\n " + getScopeDescription()); //$NON-NLS-1$
//        System.out.println(" FULL      = " + getFullScopeDescription(getScopeDescription()));
        selectScopeSelected = isIndexInSelectScope();
        subQuerySelected = isSubQueryNode(commandDisplayNode);
        whereSelected = isIndexInWhere();
    }
    
    private void setPrimaryLanguageObjectType() {
        if( primaryLanguageObject != null ) {
            if( primaryLanguageObject instanceof Query ) {
                // Check to see if it's a subquery???
                DisplayNode parentNode = commandDisplayNode.getParent();
                if( parentNode != null && getDisplayNodeType(parentNode) == EXISTS_CRITERIA ) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_CRITERIA;
//                    primaryLOTypeString = "QUERY in CRITERIA"; //$NON-NLS-1$
                } else if( parentNode != null && getDisplayNodeType(parentNode) == SET_QUERY ) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_UNION;
//                    primaryLOTypeString = "QUERY in UNION"; //$NON-NLS-1$
                } else if( parentNode != null && getDisplayNodeType(parentNode) == COMMAND_STATEMENT) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_COMMAND;
//                    primaryLOTypeString = "QUERY in COMMAND"; //$NON-NLS-1$
                } else  {
                    primaryLanguageObjectType = TYPE_QUERY;
//                    primaryLOTypeString = "QUERY"; //$NON-NLS-1$
                }
            } else if( primaryLanguageObject instanceof SetQuery ) {
                DisplayNode parentNode = commandDisplayNode.getParent();
                if( parentNode != null && getDisplayNodeType(parentNode) == EXISTS_CRITERIA ) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_CRITERIA;
//                    primaryLOTypeString = "UNION QUERY IN CRITERIA"; //$NON-NLS-1$
                } else if( parentNode != null && getDisplayNodeType(parentNode) == SET_QUERY ) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_UNION;
//                    primaryLOTypeString = "UNION QUERY IN UNION"; //$NON-NLS-1$
                } else if( parentNode != null && getDisplayNodeType(parentNode) == SUBQUERY_FROM_CLAUSE ) {
                    primaryLanguageObjectType = TYPE_QUERY_IN_UNION;
//                    primaryLOTypeString = "UNION QUERY IN SUBQUERY FROM"; //$NON-NLS-1$
                } else {
                    primaryLanguageObjectType = TYPE_UNION;
//                    primaryLOTypeString = "UNION"; //$NON-NLS-1$
                }
                
            } else {
                primaryLanguageObjectType = TYPE_UNKNOWN;
            }
        }
    }
    
    private void setPrimaryIndexDisplayNode() {
        // Need take the display node and check the index to see if it's between the first SELECT
        // first FROM\
        int lastEndIndex = -1;
        DisplayNode lastNode = null;
        
        if( commandDisplayNode != null ) {
            List displayNodes = commandDisplayNode.getChildren();
            lastNode = commandDisplayNode;
            
            Iterator iter = displayNodes.iterator();
            
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                lastEndIndex = node.endIndex;
                
                if( currentIndex >= node.startIndex && currentIndex <= node.endIndex ) {
                    primaryIndexDisplayNode = node;
                    setPrimaryIndexNodeType();
                    break;
                } else if( currentIndex < lastEndIndex ) {
                    primaryIndexDisplayNode = lastNode;
                    setPrimaryIndexNodeType();
                }
                lastNode = node;
            }
            
            if( primaryIndexDisplayNode == null ) {
                primaryIndexDisplayNode = lastNode;
                setPrimaryIndexNodeType();
            }
        }
    }
    
    private void setPrimaryIndexNodeType() {
        primaryNodeType = getDisplayNodeType(primaryIndexDisplayNode);
    }
    
    public int getDisplayNodeType(DisplayNode node) {
        
         if(node instanceof SelectDisplayNode) {
             return SELECT;
         } else if(node instanceof FromDisplayNode) {
             return FROM;
         } else if(node instanceof WhereDisplayNode) {
             return WHERE;
         } else if(node instanceof GroupByDisplayNode) {
             return GROUP_BY;
         } else if(node instanceof HavingDisplayNode) {
             return HAVING;
         } else if(node instanceof OrderByDisplayNode) {
             return ORDER_BY;
         } else if(node instanceof OptionDisplayNode) {
             return OPTION;
         } else if(node instanceof QueryDisplayNode) {
             return QUERY;
         } else if(node instanceof SetQueryDisplayNode) {
             return SET_QUERY;
         } else if(node instanceof UpdateDisplayNode) {
             return UPDATE;
         } else if(node instanceof InsertDisplayNode) {
             return INSERT;
         } else if(node instanceof DeleteDisplayNode) {
             return DELETE;
         } else if(node instanceof StoredProcedureDisplayNode) {
             return STORED_PROCEDURE;
         } else if(node instanceof CreateUpdateProcedureDisplayNode) {
             return CREATE_UPDATE_PROCEDURE;
         } else if(node instanceof HasCriteriaDisplayNode) {
             return HAS_CRITERIA;
         } else if(node instanceof ExistsCriteriaDisplayNode) {
             return EXISTS_CRITERIA;
         } else if(node instanceof SubqueryFromClauseDisplayNode) {
             return SUBQUERY_FROM_CLAUSE;
         } else if(node instanceof ScalarSubqueryDisplayNode) {
             return SCALAR_SUBQUERY;
         } else if(node instanceof SubqueryCompareCriteriaDisplayNode) {
             return SUBQUERY_COMPARE_CRITERIA;
         } else if(node instanceof SubquerySetCriteriaDisplayNode) {
             return SUBQUERY_SET_CRITERIA;
         } else if(node instanceof CriteriaDisplayNode) {
             return CRITERIA;
         } else if(node instanceof CommandStatementDisplayNode) {
             return COMMAND_STATEMENT;
         } else {
             return UNKNOWN;
         }
     }
    
//    public String getDisplayNodeTypeString(int type) {
//        String result = UNKNOWN_STR;
//        switch(type) {
//             case SELECT:                   { result = SELECT_STR; } break;
//             case FROM:                     { result = FROM_STR; } break;
//             case WHERE:                    { result = WHERE_STR; } break;
//             case GROUP_BY:                 { result = GROUP_BY_STR; } break;
//             case HAVING:                   { result = HAVING_STR; } break;
//             case ORDER_BY:                 { result = ORDER_BY_STR; } break;
//             case OPTION:                   { result = OPTION_STR;} break;
//             case QUERY:                    { result = QUERY_STR; } break;
//             case SET_QUERY:                { result = SET_QUERY_STR; } break;
//             case UPDATE:                   { result = UPDATE_STR;} break;
//             case INSERT:                   { result = INSERT_STR;} break;
//             case DELETE:                   { result = DELETE_STR;} break;
//             case STORED_PROCEDURE:         { result = STORED_PROCEDURE_STR;} break;
//             case CREATE_UPDATE_PROCEDURE:  { result = CREATE_UPDATE_PROCEDURE_STR;} break;
//             case HAS_CRITERIA:             { result = HAS_CRITERIA_STR;} break;
//             case EXISTS_CRITERIA:          { result = EXISTS_CRITERIA_STR;} break;
//             case SUBQUERY_FROM_CLAUSE:     { result = SUBQUERY_FROM_CLAUSE_STR;} break;
//             case SCALAR_SUBQUERY:          { result = SCALAR_SUBQUERY_STR;} break;
//             case SUBQUERY_COMPARE_CRITERIA: { result = SUBQUERY_COMPARE_CRITERIA_STR;} break;
//             case SUBQUERY_SET_CRITERIA:    { result = SUBQUERY_SET_CRITERIA_STR;} break;
//             case CRITERIA:                 { result = CRITERIA_STR;} break;
//             case COMMAND_STATEMENT:        { result = COMMAND_STATEMENT_STR;} break;
//             case UNKNOWN:
//             default: {} break;
//         }
//        
//        return result;
//     }
    
    public LanguageObject getPrimaryLanguageObject() {
        return this.primaryLanguageObject;
    }
    public int getPrimaryNodeType() {
        return this.primaryNodeType;
    }
    
//    private LanguageObject getParentLanguageObject(DisplayNode childNode) {
//        LanguageObject parentLO = null;
//        DisplayNode parentNode = childNode.getParent();
//        if( parentNode != null ) {
//             parentLO = parentNode.getLanguageObject();
//        }
//        
//        return parentLO;
//    }
    
    
//    public String getScopeDescription() {
//        String scope = 
//            "Selected [" //$NON-NLS-1$
//            + getDisplayNodeTypeString(getPrimaryNodeType())
//            + "] In [" + primaryLOTypeString + "]"; //$NON-NLS-1$ //$NON-NLS-2$
//        
//        return scope;
//    }
//    
//    public String getParentScopeDescription(DisplayNode parentNode) {
//        String scope = 
//            getDisplayNodeTypeString(getDisplayNodeType(parentNode))
//            + IN + primaryLOTypeString;
//        
//        return scope;
//    }
    
    public boolean isSubQueryNode(DisplayNode node) {
        int nodeType = getDisplayNodeType(node);
        if( nodeType == QUERY || nodeType == SET_QUERY) {
            // Check it's parent's parent. if FROM, then subquery
            DisplayNode parentNode = node.getParent();
            if( parentNode != null ) {
                int parentType = getDisplayNodeType(parentNode);
                if( parentType == SUBQUERY_FROM_CLAUSE)
                    return true;
                
                if( parentType == SET_QUERY && primaryLanguageObjectType == TYPE_QUERY_IN_UNION ) {   
                    parentNode = parentNode.getParent();
                    if( parentNode != null ) {
                        if( getDisplayNodeType(parentNode) == SUBQUERY_FROM_CLAUSE)
                            return true;
                    }
                }
            }
        }
        
        return false;
    }
    
//    public String getFullScopeDescription(String primaryDescription) {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(primaryDescription);
//        
//        // Start with the grandparent.
//        DisplayNode parentNode = commandDisplayNode.getParent();
//        if( parentNode != null )
//            parentNode = parentNode.getParent();
//        
//        // Now we walk the nodes until parent == null
//        while( parentNode != null ) {
//            String segmentString = getParentScopeDescription(parentNode);
//            buffer.append(IN + segmentString);
//            parentNode = parentNode.getParent();
//        }
//        return buffer.toString();
//    }
    
//    private void correctIndexForCommandDisplayNode() {
//        currentIndex = currentIndex - getIndexForDisplayNode(commandDisplayNode, getTopDisplayNode());
//    }
    
    public int getIndexForDisplayNode(DisplayNode parentNode, DisplayNode targetNode) {
        if( targetNode.getParent() != null && !parentNode.getChildren().isEmpty() ) {
            // Walk through the children
            Iterator iter = parentNode.getChildren().iterator();
            DisplayNode nextNode = null;
            while( iter.hasNext() ) {
                nextNode = (DisplayNode)iter.next();
                if( nextNode.equals(targetNode) ) {
                    return nextNode.startIndex;
                } 
                int tmpIndex = getIndexForDisplayNode(nextNode, targetNode);
                if( tmpIndex > -1 )
                    return tmpIndex;
            }
        }
        return -1;
    }
    
    public DisplayNode getTopDisplayNode() {
        DisplayNode parentNode = commandDisplayNode;
        while( parentNode.getParent() != null ) {
            parentNode = parentNode.getParent();
        }
        return parentNode;
    }
    
    public DisplayNode getCommandDisplayNode() {
        return this.commandDisplayNode;
    }
    
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    public boolean isIndexInSelectScope() {
        // Need take the display node and check the index to see if it's between the first SELECT
        // first FROM\

        if(primaryIndexDisplayNode instanceof SelectDisplayNode || primaryIndexDisplayNode instanceof FromDisplayNode )
            return true;
        
        int startSelectIndex = -1;
        int endFromIndex = -1;
        List displayNodes = primaryIndexDisplayNode.getChildren();
        
        Iterator iter = displayNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if( node instanceof SelectDisplayNode ) {
                startSelectIndex = node.startIndex;
            }
            if( node instanceof FromDisplayNode ) {
                endFromIndex = node.endIndex + 1;
            }
            int correctedIndex = getCurrentIndex() - getCommandDisplayNode().getStartIndex();
            if( node instanceof WhereDisplayNode ) {
                boolean inNode = isIndexInSelectScope(node, correctedIndex);
                if( inNode )
                    return true;
            }
            if( node instanceof ExistsCriteriaDisplayNode ) {
                boolean inNode = false;
                for(Iterator iter2 = node.getChildren().iterator(); iter2.hasNext(); ) {
                    DisplayNode nextNode = (DisplayNode)iter2.next();
                    inNode = isIndexInSelectScope(nextNode, currentIndex);
                    if( inNode )
                        return true;
                }
            }
            
            if( startSelectIndex > -1 && endFromIndex > -1 ) {
                if( currentIndex >= startSelectIndex && currentIndex <= endFromIndex )
                    return true;
                
                return false;
            }
        }
        
        return false;
    }
    
    private boolean isIndexInSelectScope(DisplayNode displayNode, int index) {
        // Need take the display node and check the index to see if it's between the first SELECT
        // first FROM\

        if( displayNode != null ) {
            if(displayNode instanceof SelectDisplayNode || displayNode instanceof FromDisplayNode )
                return true;
            int startSelectIndex = -1;
            int endFromIndex = -1;
            List displayNodes = displayNode.getChildren();
            
            Iterator iter = displayNodes.iterator();
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if( node instanceof SelectDisplayNode ) {
                    startSelectIndex = node.startIndex;
                }
                if( node instanceof FromDisplayNode ) {
                    endFromIndex = node.endIndex + 1;
                }
                
                if( node instanceof WhereDisplayNode ||
                    node instanceof ExistsCriteriaDisplayNode || 
                    node instanceof QueryDisplayNode ) {
                    boolean inNode = isIndexInSelectScope(node, index);
                    if( inNode )
                        return true;
                }
                
                if( startSelectIndex > -1 && endFromIndex > -1 ) {
                    if( index >= startSelectIndex && index <= endFromIndex )
                        return true;
                    
                }
            }
        }
        
        return false;
    }
    
    public boolean isIndexInWhere() {
        // Need take the display node and check the index to see if it's within a WHERE clause

        if(primaryIndexDisplayNode instanceof WhereDisplayNode )
            return true;
        
        List displayNodes = primaryIndexDisplayNode.getChildren();
        
        Iterator iter = displayNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            int correctedIndex = getCurrentIndex() - getCommandDisplayNode().getStartIndex();
            if( node instanceof WhereDisplayNode ) {
                if( correctedIndex >= node.startIndex && correctedIndex <= node.endIndex)
                    return true;
            }
        }
        
        return false;
    }
    
    public boolean isWhereSelected() {
        return this.whereSelected;
    }

    public boolean isSelectScopeSelected() {
        return this.selectScopeSelected;
    }
    
    public DisplayNode getSelectedSelectQuery() {
        if(primaryIndexDisplayNode instanceof SelectDisplayNode || primaryIndexDisplayNode instanceof FromDisplayNode )
            return primaryIndexDisplayNode.getParent();
        
        int startSelectIndex = -1;
        int endFromIndex = -1;
        List displayNodes = primaryIndexDisplayNode.getChildren();
        
        Iterator iter = displayNodes.iterator();
        while(iter.hasNext()) {
            DisplayNode node = (DisplayNode)iter.next();
            if( node instanceof SelectDisplayNode ) {
                startSelectIndex = node.startIndex;
            }
            if( node instanceof FromDisplayNode ) {
                endFromIndex = node.endIndex + 1;
            }
            int correctedIndex = getCurrentIndex() - getCommandDisplayNode().getStartIndex();
            if( node instanceof WhereDisplayNode ) {
                DisplayNode selectNode = getSelectedSelectQuery(node, correctedIndex);
                if( selectNode != null)
                    return selectNode;
            }
            if( node instanceof ExistsCriteriaDisplayNode ) {
                for(Iterator iter2 = node.getChildren().iterator(); iter2.hasNext(); ) {
                    DisplayNode nextNode = (DisplayNode)iter2.next();
                    DisplayNode selectNode = getSelectedSelectQuery(nextNode, currentIndex);
                    if( selectNode != null)
                        return selectNode;
                }
            }
            
            if( startSelectIndex > -1 && endFromIndex > -1 ) {
                if( currentIndex >= startSelectIndex && currentIndex <= endFromIndex )
                    return node.getParent();
                
                return null;
            }
        }
        
        return null;
    }
    
    private DisplayNode getSelectedSelectQuery(DisplayNode displayNode, int index) {
        // Need take the display node and check the index to see if it's between the first SELECT
        // last FROM indexes

        if( displayNode != null ) {
            if(displayNode instanceof SelectDisplayNode || displayNode instanceof FromDisplayNode )
                return displayNode.getParent();
            
            int startSelectIndex = -1;
            int endFromIndex = -1;
            List displayNodes = displayNode.getChildren();
            
            Iterator iter = displayNodes.iterator();
            while(iter.hasNext()) {
                DisplayNode node = (DisplayNode)iter.next();
                if( node instanceof SelectDisplayNode ) {
                    startSelectIndex = node.startIndex;
                }
                if( node instanceof FromDisplayNode ) {
                    endFromIndex = node.endIndex + 1;
                }
                
                if( node instanceof WhereDisplayNode ||
                    node instanceof ExistsCriteriaDisplayNode || 
                    node instanceof QueryDisplayNode) {
                    DisplayNode selectNode = getSelectedSelectQuery(node, index);
                    if( selectNode != null)
                        return selectNode;
                }
                
                if( startSelectIndex > -1 && endFromIndex > -1 ) {
                    if( currentIndex >= startSelectIndex && currentIndex <= endFromIndex )
                        return node.getParent();
                }
            }
        }
        
        return null;
    }
    
    public List collectCriteriaParentQueries(boolean useSelectQuery) {
        if( isCriteriaQuerySelected() ) {
            // Let's get selection starting point
            
            DisplayNode startingNode = null;
            
            if( useSelectQuery )
                startingNode = getSelectedSelectQuery();
            else
                startingNode = primaryIndexDisplayNode;
            
            // From the current primaryIndexDisplayNode, walk up and find all nested Criteria parents
            List parentQueries = new ArrayList();
            
            parentQueries.add(startingNode);
            
            boolean foundUnexpectedParent = false;
            boolean shouldFindCriteria = false;
            boolean foundExpectedCriteria = true;
            DisplayNode parentNode = startingNode.getParent();
            
            while( !foundUnexpectedParent && foundExpectedCriteria ) {
                if( parentNode == null )
                    foundUnexpectedParent = true;
                else {
                    if( shouldFindCriteria && !(parentNode instanceof ExistsCriteriaDisplayNode) )
                        foundExpectedCriteria = false;
                    
                    if( foundExpectedCriteria ) {
                        shouldFindCriteria = false;
                        
                        if( parentNode instanceof QueryDisplayNode ) {
                            parentQueries.add(parentNode);
                            // Expect to find another exists criteria next
                            if( parentNode.getParent() != null &&
                                !(parentNode.getParent() instanceof SubqueryFromClauseDisplayNode)) {
                                shouldFindCriteria = true;
                            }
                        } else if( parentNode instanceof FromDisplayNode ||
                                   parentNode instanceof SelectDisplayNode ||
                                   parentNode instanceof ExistsCriteriaDisplayNode ||
                                   parentNode instanceof WhereDisplayNode || 
                                   parentNode instanceof SubqueryFromClauseDisplayNode ) {
                            // ALL OK
                        } else {
                            foundUnexpectedParent = true;
                        }

                    }
                    parentNode = parentNode.getParent();
                }
                
            }
            return parentQueries;
        }
        return Collections.EMPTY_LIST;
    }
    
    public boolean isSelectedNodeContainedWithinType(int type) {
        // Need to walk the display node and check parents until done
        
        DisplayNode parentNode = primaryIndexDisplayNode;
        
        while( parentNode != null ) {
            if( getDisplayNodeType(parentNode) == type ) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        
        return false;
    }
    
    public DisplayNode getExistsCriteriaWhereNode(DisplayNode node ) {
        DisplayNode whereNode = node.getParent();
        if( whereNode != null && getDisplayNodeType(whereNode) == SqlIndexLocator.WHERE ) {
            DisplayNode queryNode = whereNode.getParent();
            if( queryNode != null && queryNode.getLanguageObject() instanceof Query ) {
                return queryNode;
            }
        }
        
        return null;
    }
}

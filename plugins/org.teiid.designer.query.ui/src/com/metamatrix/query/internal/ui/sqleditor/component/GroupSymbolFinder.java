/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.visitor.GroupCollectorVisitor;


/** This class, along with SqlIndexLocator provides actions and panels a way to narrow down the
 * scope of the query that is currently selected or the cursor is currently in.
 * @since 4.2
 */
public class GroupSymbolFinder {
    SqlIndexLocator locator;
    Collection externalGroups;
    
    /** 
     * Constructor
     * @since 4.2
     * @param locator = SqlIndexLocator instance 
     * @param exGroups - Any external groups which may be added to the returned list if appropriate
     */
    public GroupSymbolFinder(SqlIndexLocator locator, Collection exGroups) {
        super();
        this.locator = locator;
        if( exGroups.isEmpty() )
            externalGroups = Collections.EMPTY_LIST;
        else
            this.externalGroups = new ArrayList(exGroups);
    }
    
    /**
     * Method returns any group symbols in scope for populating the attribute tree in builders 
     * 
     * @return
     * @since 4.2
     */
    public List find() {
        Set groups = new HashSet();
        if(locator.getCommandDisplayNode() !=null) {
            // If the Command is within a SubQuery, handle as a special case.
            // Need to walk all the way up the chain, gathering groups
            
            if(locator.isCriteriaQuerySelected()) {
                if( !externalGroups.isEmpty() )
                    groups.addAll(externalGroups);
                groups.addAll(getGroupsForCriteria());
            } else if(locator.isSubQuerySelected() ) {
                return getGroupsForSubQuery(); 
            } else if( locator.isUnionSegmentSelected() ) {
            	// Defect 17714 - Needed to add external groups here (i.e. InputSet) when selecting
            	// a Union Segement (From clause?)
                if( !externalGroups.isEmpty() )
                    groups.addAll(externalGroups);
                groups.addAll(getGroupsForUnionSegment());
            } else {
                return getGroups();
            }
        }
        
        return new ArrayList(groups);
    }
    
    private List getGroupsForCriteria() {
        Set groups = new HashSet();
        if( locator.isSelectScopeSelected() || locator.isWhereSelected() ) {
            groups.addAll(GroupCollectorVisitor.getGroups(locator.getPrimaryLanguageObject(), true));
            List groupsInScope = locator.collectCriteriaParentQueries(locator.isSelectScopeSelected());
            DisplayNode dNode = null;
            for( Iterator iter = groupsInScope.iterator(); iter.hasNext(); ) {
                dNode = (DisplayNode)iter.next();
                groups.addAll(GroupCollectorVisitor.getGroups(dNode.getLanguageObject(), true));
            }
        }
        
        return new ArrayList(groups);
    }
    
    private List getGroupsForSubQuery() {
        Set allGroups = new HashSet();
        // Get this commands groupSymbols
        allGroups.addAll(GroupCollectorVisitor.getGroups(locator.getPrimaryLanguageObject(), true));
        // Get parent node, keep going up until parent is null
        if( !locator.isUnionSegmentSelected() ) {
            DisplayNode parentNode = locator.getCommandDisplayNode().getParent();
            while(parentNode!=null) {
                LanguageObject parentLangObj = parentNode.getLanguageObject();
                parentNode = parentNode.getParent();
                // Check if Parent Node is UNION (then Stop)
                if( parentNode instanceof SetQueryDisplayNode) 
                    parentNode = null;
                
                if(parentNode != null && parentLangObj instanceof Command) {
                    allGroups.addAll(GroupCollectorVisitor.getGroups(parentLangObj,true));
                }
            }
            // Now let's add for the real selected Select query
            if( locator.isSelectScopeSelected() ) {
                DisplayNode selectedSelectQueryNode = locator.getSelectedSelectQuery();
                if( selectedSelectQueryNode != null )
                    allGroups.addAll(GroupCollectorVisitor.getGroups(selectedSelectQueryNode.getLanguageObject(), true));
            }
            if( locator.isCriteriaQuerySelected() ) {
                allGroups.addAll(getGroupsForCriteria());
            }
            if( !externalGroups.isEmpty() )
                allGroups.addAll(externalGroups);
        }
        return new ArrayList(allGroups);
    }
    
    private List getGroupsForUnionSegment() {
        Set allGroups = new HashSet();
        allGroups.addAll(GroupCollectorVisitor.getGroups(locator.getPrimaryLanguageObject(), true));
        return new ArrayList(allGroups);
    }
    
    private List getGroups() {
        Set allGroups = new HashSet();
         // Not within subquery, or Union, get the treeView using the command
        LanguageObject langObj = locator.getPrimaryLanguageObject();
        // Language Object should be a Command
        if (langObj!=null && langObj instanceof Command) {
        
            // make sure no duplicates before adding in external groups
            
            Collection groups = GroupCollectorVisitor.getGroupsIgnoreInlineViews(langObj, true);
            
            Collection clonedGrps = new ArrayList(groups.size());
            Iterator grpIter = groups.iterator();
            while(grpIter.hasNext()) {
                GroupSymbol gSymbol = (GroupSymbol)grpIter.next();
                clonedGrps.add(gSymbol.clone());
            }
            
            allGroups.addAll(externalGroups);
            allGroups.addAll(clonedGrps);
        }
        
        return new ArrayList(allGroups);
    }

}

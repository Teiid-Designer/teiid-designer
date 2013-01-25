/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/** This class, along with SqlIndexLocator provides actions and panels a way to narrow down the
 * scope of the query that is currently selected or the cursor is currently in.
 * @since 8.0
 */
public class GroupSymbolFinder {
    SqlIndexLocator locator;
    Collection externalGroups;
    private final IGroupCollectorVisitor visitor;
    
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
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        visitor = queryService.getGroupCollectorVisitor(true);
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
            groups.addAll(visitor.findGroups(locator.getPrimaryLanguageObject()));
            List groupsInScope = locator.collectCriteriaParentQueries(locator.isSelectScopeSelected());
            DisplayNode dNode = null;
            for( Iterator iter = groupsInScope.iterator(); iter.hasNext(); ) {
                dNode = (DisplayNode)iter.next();
                groups.addAll(visitor.findGroups(dNode.getLanguageObject()));
            }
        }
        
        return new ArrayList(groups);
    }
    
    private List getGroupsForSubQuery() {
        Set allGroups = new HashSet();
        // Get this commands groupSymbols
        allGroups.addAll(visitor.findGroups(locator.getPrimaryLanguageObject()));
        // Get parent node, keep going up until parent is null
        if( !locator.isUnionSegmentSelected() ) {
            DisplayNode parentNode = locator.getCommandDisplayNode().getParent();
            while(parentNode!=null) {
                ILanguageObject parentLangObj = parentNode.getLanguageObject();
                parentNode = parentNode.getParent();
                // Check if Parent Node is UNION (then Stop)
                if( parentNode instanceof SetQueryDisplayNode) 
                    parentNode = null;
                
                if(parentNode != null && parentLangObj instanceof ICommand) {
                    allGroups.addAll(visitor.findGroups(parentLangObj));
                }
            }
            // Now let's add for the real selected Select query
            if( locator.isSelectScopeSelected() ) {
                DisplayNode selectedSelectQueryNode = locator.getSelectedSelectQuery();
                if( selectedSelectQueryNode != null )
                    allGroups.addAll(visitor.findGroups(selectedSelectQueryNode.getLanguageObject()));
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
        allGroups.addAll(visitor.findGroups(locator.getPrimaryLanguageObject()));
        return new ArrayList(allGroups);
    }
    
    private List getGroups() {
        Set allGroups = new HashSet();
         // Not within subquery, or Union, get the treeView using the command
        ILanguageObject langObj = locator.getPrimaryLanguageObject();
        // Language Object should be a Command
        if (langObj!=null && langObj instanceof ICommand) {
        
            // make sure no duplicates before adding in external groups
            
            Collection groups = visitor.findGroupsIgnoreInlineViews(langObj);
            
            Collection clonedGrps = new ArrayList(groups.size());
            Iterator grpIter = groups.iterator();
            while(grpIter.hasNext()) {
                IGroupSymbol gSymbol = (IGroupSymbol)grpIter.next();
                clonedGrps.add(gSymbol.clone());
            }
            
            allGroups.addAll(externalGroups);
            allGroups.addAll(clonedGrps);
        }
        
        return new ArrayList(allGroups);
    }

}

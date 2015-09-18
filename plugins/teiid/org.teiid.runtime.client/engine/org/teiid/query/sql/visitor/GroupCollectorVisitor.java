/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;
import org.teiid.query.sql.navigator.PreOrPostOrderNavigator;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.runtime.client.Messages;


/**
 * <p>This visitor class will traverse a language object tree and collect all group
 * symbol references it finds.  It uses a collection to collect the groups in so
 * different collections will give you different collection properties - for instance,
 * using a Set will remove duplicates.</p>
 *
 * <p>The easiest way to use this visitor is to call the static methods which create
 * the visitor (and possibly the collection), run the visitor, and get the collection.
 * The public visit() methods should NOT be called directly.</p>
 */
public class GroupCollectorVisitor extends LanguageVisitor
    implements IGroupCollectorVisitor<LanguageObject, GroupSymbol> {

    private Collection<GroupSymbol> groups;

    private boolean isIntoClauseGroup;
       
    // In some cases, set a flag to ignore groups created by a subquery from clause
    private boolean ignoreInlineViewGroups = false;
    private Collection<GroupSymbol> inlineViewGroups;    // groups defined by a SubqueryFromClause

    /**
     * Construct a new visitor with a default returning collection
     * @param teiidVersion 
     * 
     * @param removeDuplicates 
     */
    public GroupCollectorVisitor(ITeiidServerVersion teiidVersion, boolean removeDuplicates) {
        this(teiidVersion, removeDuplicates ? new HashSet<GroupSymbol>() : new ArrayList<GroupSymbol>());
    }
    
    /**
     * Construct a new visitor with the specified collection, which should
     * be non-null.
     * @param teiidVersion 
     * @param groups Collection to use for groups
     * @throws IllegalArgumentException If groups is null
     */
	public GroupCollectorVisitor(ITeiidServerVersion teiidVersion, Collection<GroupSymbol> groups) {
	    super(teiidVersion);
        if(groups == null) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_015_010_0023));
        }
        this.groups = groups;
    }

    /**
     * Get the groups collected by the visitor.  This should best be called
     * after the visitor has been run on the language object tree.
     * @return Collection of {@link org.teiid.query.sql.symbol.GroupSymbol}
     */
    public Collection<GroupSymbol> getGroups() {
        return this.groups;
    }
    
    public Collection<GroupSymbol> getInlineViewGroups() {
        return this.inlineViewGroups;
    }
    
    public void setIgnoreInlineViewGroups(boolean ignoreInlineViewGroups) {
        this.ignoreInlineViewGroups = ignoreInlineViewGroups;
    }
    
    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    public void visit(GroupSymbol obj) {
        if(this.isIntoClauseGroup){
            if (!obj.isTempGroupSymbol()) {
                // This is a physical group. Collect it.
                this.groups.add(obj);
            }
            this.isIntoClauseGroup = false;
        }else{
            this.groups.add(obj);
        }
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    public void visit(StoredProcedure obj) {
        this.groups.add(obj.getGroup());
    }

    public void visit(Into obj) {
        this.isIntoClauseGroup = true;
    }
    
    
    public void visit(SubqueryFromClause obj) {
        if(this.ignoreInlineViewGroups) {
            if(this.inlineViewGroups == null) { 
                this.inlineViewGroups = new ArrayList<GroupSymbol>();
            }
            this.inlineViewGroups.add(obj.getGroupSymbol());
        }
    }
    
    /**
     * Helper to quickly get the groups from obj in the groups collection
     * @param obj Language object
     * @param elements Collection to collect groups in
     */
    public static void getGroups(LanguageObject obj, Collection<GroupSymbol> groups) {
        GroupCollectorVisitor visitor = new GroupCollectorVisitor(obj.getTeiidVersion(), groups);
        PreOrderNavigator.doVisit(obj, visitor);
    }

    /**
     * Helper to quickly get the groups from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate groups will be
     * filtered out.
     * @param obj Language object
     * @param removeDuplicates True to remove duplicates
     * @return Collection of {@link org.teiid.query.sql.symbol.GroupSymbol}
     */
    public static Collection<GroupSymbol> getGroups(LanguageObject obj, boolean removeDuplicates) {
        Collection<GroupSymbol> groups = null;
        if(removeDuplicates) {
            groups = new HashSet<GroupSymbol>();
        } else {
            groups = new ArrayList<GroupSymbol>();
        }
        GroupCollectorVisitor visitor = new GroupCollectorVisitor(obj.getTeiidVersion(), groups);
        PreOrderNavigator.doVisit(obj, visitor);
        return groups;
    }
    
    /**
     * Helper to quickly get the groups from obj in the groups collection
     * @param obj Language object
     * @param elements Collection to collect groups in
     */
    public static void getGroupsIgnoreInlineViews(LanguageObject obj, Collection<GroupSymbol> groups) {
        GroupCollectorVisitor visitor = new GroupCollectorVisitor(obj.getTeiidVersion(), groups);
        visitor.setIgnoreInlineViewGroups(true);
        DeepPreOrderNavigator.doVisit(obj, visitor);  
        
        if(visitor.getInlineViewGroups() != null) {
            groups.removeAll(visitor.getInlineViewGroups());
        }
    }

    /**
     * Helper to quickly get the groups from obj in a collection.  The 
     * removeDuplicates flag affects whether duplicate groups will be 
     * filtered out.
     * @param obj Language object
     * @param removeDuplicates True to remove duplicates
     * @return Collection of {@link org.teiid.query.sql.symbol.GroupSymbol}
     */
    public static Collection<GroupSymbol> getGroupsIgnoreInlineViewsAndEvaluatableSubqueries(LanguageObject obj, boolean removeDuplicates) {
        Collection<GroupSymbol> groups = null;
        if(removeDuplicates) { 
            groups = new LinkedHashSet<GroupSymbol>();
        } else {
            groups = new ArrayList<GroupSymbol>();
        }    
        GroupCollectorVisitor visitor = new GroupCollectorVisitor(obj.getTeiidVersion(), groups);
        visitor.setIgnoreInlineViewGroups(true);

        if (visitor.isTeiid811OrGreater()) {
            PreOrPostOrderNavigator nav = new PreOrPostOrderNavigator(visitor, PreOrPostOrderNavigator.PRE_ORDER, true);
            nav.setSkipEvaluatable(true);
            obj.acceptVisitor(nav);
        } else {
            DeepPreOrderNavigator.doVisit(obj, visitor);
        }

        if(visitor.getInlineViewGroups() != null) {
            groups.removeAll(visitor.getInlineViewGroups());
        }

        return groups;
    }
    
    @Override
    public Collection<GroupSymbol> findGroups(LanguageObject obj) {
        PreOrderNavigator.doVisit(obj, this);
        return groups;
    }
    
    @Override
    public Collection<GroupSymbol> findGroupsIgnoreInlineViews(LanguageObject obj) {
        setIgnoreInlineViewGroups(true);
        DeepPreOrderNavigator.doVisit(obj, this);  
        
        if(getInlineViewGroups() != null) {
            groups.removeAll(getInlineViewGroups());
        }
        
        return groups;
    }

}

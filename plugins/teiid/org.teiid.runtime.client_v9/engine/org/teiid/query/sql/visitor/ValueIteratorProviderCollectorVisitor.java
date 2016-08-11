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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.teiid.designer.query.sql.IValueIteratorProviderCollectorVisitor;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.symbol.ScalarSubquery;


/**
 * <p>This visitor class will traverse a language object tree and collect all language
 * objects that implement {@link SubqueryContainer}.  
 * By default it uses a java.util.ArrayList to collect the objects in the order 
 * they're found.</p>
 * 
 * <p>The easiest way to use this visitor is to call one of the static methods which create 
 * the visitor, run the visitor, and get the collection. 
 * The public visit() methods should NOT be called directly.</p>
 */
public class ValueIteratorProviderCollectorVisitor extends LanguageVisitor
    implements IValueIteratorProviderCollectorVisitor<LanguageObject, SubqueryContainer> {

    private List<SubqueryContainer> valueIteratorProviders;
	private boolean collectLateral;
    
    /**
     * Construct a new visitor with the default collection type, which is a 
     * {@link java.util.ArrayList}.  
     * @param teiidVersion
     */
    public ValueIteratorProviderCollectorVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
        this.valueIteratorProviders = new ArrayList<SubqueryContainer>();
    }   

	/**
	 * Construct a new visitor with the given Collection to accumulate
     * ValueIteratorProvider instances
     * @param teiidVersion
	 * @param valueIteratorProviders Collection to accumulate found 
	 */
	ValueIteratorProviderCollectorVisitor(ITeiidServerVersion teiidVersion, List<SubqueryContainer> valueIteratorProviders) {
	    super(teiidVersion);
		this.valueIteratorProviders = valueIteratorProviders;
	}   
    
    /**
     * Get the value iterator providers collected by the visitor.  This should best be called 
     * after the visitor has been run on the language object tree.
     * @return Collection of {@link SubqueryContainer}
     * (by default, this is a java.util.ArrayList)
     */
    public List<SubqueryContainer> getValueIteratorProviders() { 
        return this.valueIteratorProviders;
    }
    
    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be 
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(SubquerySetCriteria obj) {
        this.valueIteratorProviders.add(obj);
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be 
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(SubqueryCompareCriteria obj) {
    	if (obj.getCommand() != null) {
    		this.valueIteratorProviders.add(obj);
    	}
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be 
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(ExistsCriteria obj) {
        this.valueIteratorProviders.add(obj);
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be 
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(ScalarSubquery obj) {
        this.valueIteratorProviders.add(obj);
    }
    
    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be 
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(SubqueryFromClause obj) {
    	if (collectLateral && obj.isLateral()) {
    		this.valueIteratorProviders.add(obj);
    	}
    }
    


    @Override
    public List<SubqueryContainer> findValueIteratorProviders(LanguageObject obj) {
        PreOrderNavigator.doVisit(obj, this);
        return getValueIteratorProviders();
    }

    /**
     * Helper to quickly get the ValueIteratorProvider instances from obj
     * @param obj Language object
     * @return java.util.ArrayList of found ValueIteratorProvider
     */
    public static final List<SubqueryContainer> getValueIteratorProviders(LanguageObject obj) {
        ValueIteratorProviderCollectorVisitor visitor = new ValueIteratorProviderCollectorVisitor(obj.getTeiidVersion());
        return visitor.findValueIteratorProviders(obj);
    }

	/**
	 * @param obj
	 * @param valueIteratorProviders
	 */
	public static final void getValueIteratorProviders(LanguageObject obj, List<SubqueryContainer> valueIteratorProviders) {
		ValueIteratorProviderCollectorVisitor visitor = new ValueIteratorProviderCollectorVisitor(obj.getTeiidVersion(), valueIteratorProviders);
		visitor.findValueIteratorProviders(obj);
	}
          	
    /**
     * @param languageObjects
     * @return list of {@link SubqueryContainer}s
     */
    public static final List<SubqueryContainer> getValueIteratorProviders(Collection<? extends LanguageObject> languageObjects) {
    	if (languageObjects == null || languageObjects.isEmpty()) {
    		return Collections.emptyList();
    	}
    	LanguageObject languageObject = languageObjects.iterator().next();
    	List<SubqueryContainer> result = new LinkedList<SubqueryContainer>();
        ValueIteratorProviderCollectorVisitor visitor = new ValueIteratorProviderCollectorVisitor(languageObject.getTeiidVersion(), result);
        for (LanguageObject obj : languageObjects) {
            visitor.findValueIteratorProviders(obj);
        }
        return result;
    }           
    
	public void setCollectLateral(boolean b) {
		this.collectLateral = true;
	}     
}

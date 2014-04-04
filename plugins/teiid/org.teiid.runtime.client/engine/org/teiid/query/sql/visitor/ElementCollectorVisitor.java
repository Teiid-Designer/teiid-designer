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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.runtime.client.Messages;


/**
 * <p>This visitor class will traverse a language object tree and collect all element
 * symbol references it finds.  It uses a collection to collect the elements in so
 * different collections will give you different collection properties - for instance,
 * using a Set will remove duplicates.</p>
 *
 * <p>The easiest way to use this visitor is to call the static methods which create
 * the visitor (and possibly the collection), run the visitor, and return the collection.
 * The public visit() methods should NOT be called directly.</p>
 */
public class ElementCollectorVisitor extends LanguageVisitor
    implements IElementCollectorVisitor<LanguageObject, ElementSymbol> {

    private Collection<? super ElementSymbol> elements;
    private boolean aggsOnly;

    /**
     * Construct a new visitor with a default returning collection
     * @param teiidVersion
     * @param removeDuplicates 
     */
    public ElementCollectorVisitor(ITeiidServerVersion teiidVersion, boolean removeDuplicates) {
        this(teiidVersion, removeDuplicates ? new HashSet<ElementSymbol>() : new ArrayList<ElementSymbol>());
    }

    /**
     * Construct a new visitor with the specified collection, which should
     * be non-null.
     * @param teiidVersion
     * @param elements Collection to use for elements
     * @throws IllegalArgumentException If elements is null
     */
	public ElementCollectorVisitor(ITeiidServerVersion teiidVersion, Collection<? super ElementSymbol> elements) {
	    super(teiidVersion);
        if(elements == null) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_015_010_0021));
        }
        this.elements = elements;
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(ElementSymbol obj) {
    	if (!aggsOnly || obj.isAggregate()) {
            this.elements.add(obj);
    	}
    }

    /**
     * Visit a language object and collect symbols.  This method should <b>NOT</b> be
     * called directly.
     * @param obj Language object
     */
    @Override
    public void visit(MultipleElementSymbol obj) {
        List<ElementSymbol> elementSymbols = obj.getElementSymbols();
		if(elementSymbols != null) {
        	for (int i = 0; i < elementSymbols.size(); i++) {
				visit(elementSymbols.get(i));
			}
        }
    }

    /**
     * Helper to quickly get the elements from obj in the elements collection
     * @param obj Language object
     * @param elements Collection to collect elements in
     */
    public static final void getElements(LanguageObject obj, Collection<? super ElementSymbol> elements) {
    	if(obj == null) {
    		return;
    	}
        ElementCollectorVisitor visitor = new ElementCollectorVisitor(obj.getTeiidVersion(), elements);
        PreOrderNavigator.doVisit(obj, visitor);
    }
    
    /**
     * @param objs
     * @param elements
     */
    public static final void getElements(Collection<? extends LanguageObject> objs, Collection<ElementSymbol> elements) {
    	if(objs == null) {
    		return;
    	}

    	LanguageObject obj = objs.iterator().next();
        ElementCollectorVisitor visitor = new ElementCollectorVisitor(obj.getTeiidVersion(), elements);
        for (LanguageObject object : objs) {
            PreOrderNavigator.doVisit(object, visitor);
		}
    }

    /**
     * Helper to quickly get the elements from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate elements will be
     * filtered out.
     * @param obj Language object
     * @param removeDuplicates True to remove duplicates
     * @return Collection of {@link ElementSymbol}
     */
    public static final Collection<ElementSymbol> getElements(LanguageObject obj, boolean removeDuplicates) {
        return ElementCollectorVisitor.getElements(obj, removeDuplicates, false);
    }

    /**
     * Helper to quickly get the elements from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate elements will be
     * filtered out.
     * @param obj Language object
     * @param removeDuplicates True to remove duplicates
     * @param useDeepIteration indicates whether or not to iterate into nested
     * subqueries of the query 
     * @return Collection of {@link ElementSymbol}
     */
    public static final Collection<ElementSymbol> getElements(LanguageObject obj, boolean removeDuplicates, boolean useDeepIteration) {
    	return getElements(obj, removeDuplicates, useDeepIteration, false);
    }
    
    /**
     * @param obj
     * @param removeDuplicates
     * @param useDeepIteration
     * @param aggsOnly
     * @return Collection of {@link ElementSymbol}
     */
    public static final Collection<ElementSymbol> getElements(LanguageObject obj, boolean removeDuplicates, boolean useDeepIteration, boolean aggsOnly) {
        if(obj == null) {
            return Collections.emptyList();
        }
        Collection<ElementSymbol> elements = null;
        if(removeDuplicates) {
            elements = new LinkedHashSet<ElementSymbol>();
        } else {
            elements = new ArrayList<ElementSymbol>();
        }
        ElementCollectorVisitor visitor = new ElementCollectorVisitor(obj.getTeiidVersion(), elements);
        visitor.aggsOnly = aggsOnly;
        if (useDeepIteration){
            DeepPreOrderNavigator.doVisit(obj, visitor);
        } else {
            PreOrderNavigator.doVisit(obj, visitor);
        }
        
        return elements;
    }
    
    /**
     * @param obj
     * @param removeDuplicates
     * @return aggregates from given object
     */
    public static final Collection<ElementSymbol> getAggregates(LanguageObject obj, boolean removeDuplicates) {
    	return getElements(obj, removeDuplicates, false, true);
    }
    
    @Override
    public Collection<? super ElementSymbol> findElements(LanguageObject obj) {
        return findElements(obj, false);
    }

    @Override
    public Collection<? super ElementSymbol> findElements(LanguageObject obj, boolean useDeepIteration) {
        return findElements(obj, useDeepIteration, false);
    }
    
    @Override
    public Collection<? super ElementSymbol>  findElements(LanguageObject obj, boolean useDeepIteration, boolean aggsOnly) {
        this.aggsOnly = aggsOnly;
        if (useDeepIteration){
            DeepPreOrderNavigator.doVisit(obj, this);
        } else {
            PreOrderNavigator.doVisit(obj, this);
        }
        
        return elements;
    }


}

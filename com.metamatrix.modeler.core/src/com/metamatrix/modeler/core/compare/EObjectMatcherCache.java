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

package com.metamatrix.modeler.core.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EReference;

/**
 * A cache of {@link com.metamatrix.modeler.core.compare.EObjectMatcher instances}.  This class
 * is not thread-safe.
 */
public class EObjectMatcherCache {

    private final List matcherFactories;
    private final Map matchersByFeature;
    private List rootMatchers;

    /**
     * Construct an instance of EObjectMatcherCache.
     * 
     */
    public EObjectMatcherCache() {
        super();
        this.matcherFactories = new ArrayList();
        this.matchersByFeature = new HashMap();
    }
    
    public List getEObjectMatcherFactories() {
        return this.matcherFactories;
    }
    
    public synchronized void addEObjectMatcherFactories( final List factories ) {
        final Iterator iter = factories.iterator();
        while (iter.hasNext()) {
            final Object factory = iter.next();
            if ( !this.matcherFactories.contains(factory) ) {
                this.matcherFactories.add(factory);
            }
        }
    }

    public List getEObjectMatchersForRoots() {
        if ( this.rootMatchers == null ) {
            this.rootMatchers = new ArrayList();
            final Iterator iter = this.matcherFactories.iterator();
            while (iter.hasNext()) {
                final EObjectMatcherFactory factory = (EObjectMatcherFactory)iter.next();
                final List newRootMatchers = factory.createEObjectMatchersForRoots();
                addAllWithNoDuplicates(newRootMatchers,this.rootMatchers);
            }
        }
        return this.rootMatchers;
    }

    public List getEObjectMatchers( final EReference reference) {
        List results = (List) this.matchersByFeature.get(reference);
        if ( results == null ) {
            results = new LinkedList();
            final Iterator iter = this.matcherFactories.iterator();
            while (iter.hasNext()) {
                final EObjectMatcherFactory factory = (EObjectMatcherFactory)iter.next();
                final List newRootMatchers = factory.createEObjectMatchers(reference);
                addAllWithNoDuplicates(newRootMatchers,results);
            }
            this.matchersByFeature.put(reference,results);
        }
        return results;
    }
    
    protected void addAllWithNoDuplicates( final List thingsToAdd, final List listToAddTo ) {
        final Iterator iter = thingsToAdd.iterator();
        while (iter.hasNext()) {
            final Object thingToAdd = iter.next();
            if ( !listToAddTo.contains(thingToAdd) ) {
                listToAddTo.add(thingToAdd);
            }
                    
        }
    }
    

}

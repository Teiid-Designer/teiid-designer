/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.emf.ecore.EObject;

import org.teiid.core.util.LRUCache;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextBuilder;
import com.metamatrix.modeler.relationship.NavigationContextBuilderException;
import com.metamatrix.modeler.relationship.NavigationContextException;
import com.metamatrix.modeler.relationship.NavigationContextInfo;

/**
 * NavigationContextCache
 */
public class NavigationContextCache {
    
    public static final int DEFAULT_MAX_SIZE = 10;
    
    private final LRUCache cache;
    private NavigationResolver resolver = new NavigationResolver();
    private NavigationContextBuilder navContextBuilder = new NavigationContextBuilderImpl();

    /**
     * Construct an instance of NavigationContextCache.
     */
    public NavigationContextCache(NavigationContextBuilder navContextBuilder) {
        this(DEFAULT_MAX_SIZE, navContextBuilder);
    }
    
    /**
     * Construct an instance of NavigationContextCache.
     */
    public NavigationContextCache( final int maxSize, NavigationContextBuilder navContextBuilder ) {
        this.cache = new LRUCache(maxSize);
        this.navContextBuilder = navContextBuilder;
    }
    
    public synchronized void clearCache() {
        this.cache.clear();
    }
    
    public synchronized void removeFromCache( final NavigationContextInfo info ) {
        this.cache.remove(info);
    }
    
    /**
     * Find an existing NavigationContext given the supplied key, or if one is not currently in the cache,
     * create a new NavigationContext and put it in the cache.
     * <p>
     * This method is thread safe.
     * </p>
     * @param info the key for the context; may not be null
     * @return the requested NavigationContext; never null
     * @throws NavigationContextException if there is a problem
     */
    public synchronized NavigationContext getNavigationContext( final NavigationContextInfo info ) throws NavigationContextException {
        final NavigationContext existing = (NavigationContext)this.cache.get(info);
        if ( existing != null ) {
            return existing;
        }
        // It was not found, so create it ...
        final NavigationContext newContext = doCreateContext(info);
        //Set the focus node label on the info object.
        info.setLabel(newContext.getFocusNode().getLabel());
        newContext.getInfo().setLabel(info.getLabel());
        //Add to cache
        this.cache.put(info,newContext);
        return newContext;
    }
    
    /**
     * Method that is used to reconstruct an NavigationContext when an existing context is not found
     * in the cache for the given key. The NavigationContextBuilder is used to create and source a
     * NavigationContext object.
     * @param info the key for the context; may not be null
     * @return the new NavigationContext; may not be null
     */
    protected NavigationContext doCreateContext( final NavigationContextInfo info ) throws NavigationContextException{
           
        try
		{
			return getNavContextBuilder().buildNavigationContext(info);
		} catch (NavigationContextBuilderException e)
		{
           throw new NavigationContextException(e);
		}
    }
    
    /**
     * @return
     */
    public NavigationResolver getResolver() {
        return resolver;
    }

    /**
     * @param resolver
     */
    public void setResolver(final NavigationResolver resolver) {
        this.resolver = resolver;
    }
    
    /**
    * @return navContextBuilder
    */
    public NavigationContextBuilder getNavContextBuilder() {
       return navContextBuilder;
    }

}
